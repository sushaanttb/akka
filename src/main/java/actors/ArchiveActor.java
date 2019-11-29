package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import utils.ESTConstants;
import utils.FileUtils;

//dump file to archiveFolder once it's completed
public class ArchiveActor extends AbstractActor {

    private int totalNumOfFilesToBeArchived;
    private int numOfFilesArchived;

    public ArchiveActor(int totalNumOfFilesToBeArchived) {
        this.totalNumOfFilesToBeArchived = totalNumOfFilesToBeArchived;
    }

    static public Props props(int numOfFilesToBeArchived){
        return Props.create(ArchiveActor.class, ()-> new ArchiveActor(numOfFilesToBeArchived));
    }

    @Override
    public Receive createReceive(){
        return receiveBuilder().match(ArchiveMessage.class, archiveMessage -> {
            System.out.println("Archive Message received for :: "+ archiveMessage.fileName + " by " + this.self() + "from ::" + this.sender());
            FileUtils.move(ESTConstants.SOURCE_DIRECTORY+"\\"+ archiveMessage.fileName,ESTConstants.ARCHIVE_DIRECTORY+"\\"+ archiveMessage.fileName);

            numOfFilesArchived++;

            //notify to it's parent i.e. directory actor that batch is completed
            if(numOfFilesArchived==totalNumOfFilesToBeArchived) {
                this.context().parent().tell(new DirectoryActor.BatchCompletionMessage(),getSelf());

                // Finally, stop this actor as batch processing is completed and same is no longer required
                this.context().stop(this.self());
            }

        }).build();
    }

    static public class ArchiveMessage {
        private final String fileName;
        public ArchiveMessage(String fileName) {this.fileName = fileName;}
    }
}
