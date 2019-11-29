package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import cassandra.CassandraUtils;
import utils.ESTConstants;

import java.io.File;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

public class DirectoryActor extends AbstractActor {

    private static final int BATCH_SIZE = 2;
    private ActorRef archiveActor;
    private ActorRef cassandraActor = getContext().actorOf(CassandraActor.props());
    private static final long WAIT_TIME_BEFORE_FINAL_STOP = 5000L;   //5s


    static public Props props(){
        return Props.create(DirectoryActor.class, DirectoryActor::new);
    }

    @Override
    public Receive createReceive(){
        return receiveBuilder().match(DirectoryMessage.class, directoryMessage -> {

            //ToDo: this check is not working correctly
//            if(!FileUtils.isDirEmpty("tmp")){

                List<File> batchFiles =  Files.list(Paths.get(ESTConstants.SOURCE_DIRECTORY))
                        .limit(BATCH_SIZE)
                        .map(Path::toFile)
                        .collect(Collectors.toList());

                if(batchFiles.size()==0) {
                    System.out.println("Directory Processing COMPLETED for :: " + directoryMessage.directoryPath + " by " + this.self() + "from ::" + this.sender());
//                  ToDo: need to check as not all EST messages are sent/processed till this is detected
//                  todo: alternate for below
                    Thread.sleep(WAIT_TIME_BEFORE_FINAL_STOP);

                    CassandraUtils.closeCluster();

                    this.context().system().terminate();
                    System.out.println("Actor System terminated after "+WAIT_TIME_BEFORE_FINAL_STOP/1000 + "s as no more files to process");

                    return;
                }

            System.out.println("Directory Processing STARTED for :: "+ directoryMessage.directoryPath + " by "+ this.self() + "from ::" + this.sender() );

            // 1 child archive actor per batch   //ToDo: terminate the previous archive actor once batch is done??   : done
            archiveActor = getContext().actorOf(ArchiveActor.props(batchFiles.size()));

            batchFiles.forEach(f-> {
                //New child FileActor for every file
                getContext().actorOf(FileActor.props())
                        .tell(new FileActor.FileMessage(f.getName()), getSelf());

                cassandraActor.tell(new CassandraActor.ESTLogMessage(f.getName(), CassandraActor.ESTLogStatus.CREATED),getSelf());
            });

        })
        .match(FileActor.FileCompletionMessage.class, fileCompletionMessage -> {
            System.out.println("In Directory Actor, File Processing COMPLETED for :: "+ fileCompletionMessage.getFileName() + " by "+ this.self() + "from ::" + this.sender());
            archiveActor.tell(new ArchiveActor.ArchiveMessage(fileCompletionMessage.getFileName()),self());

            cassandraActor.tell(new CassandraActor.ESTLogMessage(fileCompletionMessage.getFileName(), CassandraActor.ESTLogStatus.ARCHIVED),getSelf());
        } )
        .match(BatchCompletionMessage.class, batchCompletionMessage -> {
            System.out.println("In Directory Actor, Batch Processing COMPLETED  from ::" + this.sender());
            this.self().tell(new DirectoryMessage(ESTConstants.SOURCE_DIRECTORY),this.getSelf());
        } )
        .build();
    }
    
    public static class DirectoryMessage {
        private final String directoryPath;
        public DirectoryMessage(String directoryPath) { this.directoryPath = directoryPath;}
    }

    public static class BatchCompletionMessage{}


}
