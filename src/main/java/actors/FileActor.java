package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class FileActor extends AbstractActor {

    private ActorRef lineActor;

    static public Props props(){
        return Props.create(FileActor.class, FileActor::new);
    }
    
    @Override
    public Receive createReceive(){
        return receiveBuilder().match(FileMessage.class, fileMessage -> {
            System.out.println("File Processing STARTED for :: "+ fileMessage.fileName + " by "+ this.self() + "from ::" + this.sender());

//            System.out.println(Paths.get(fileMessage.fileName).toAbsolutePath()); //this was incorrect
//            System.out.println(Paths.get("tmp\\"+ fileMessage.fileName).toAbsolutePath());

            //fetch all file lines in Memory, so even if file gets deleted somehow it wont impact
            List<String> lines = Files.readAllLines(Paths.get("tmp\\"+fileMessage.fileName), Charset.defaultCharset());

            //ToDo: terminate the line actor??
            //create new child lineActor with total number of lines
            lineActor = getContext().actorOf(LineActor.props(lines.size()));

            for(String line: lines) lineActor.tell( new LineActor.LineMessage(fileMessage.fileName, line), self());

        }).match(FileCompletionMessage.class, fileCompletionMessage -> {
            System.out.println("In File Actor File Processing COMPLETED for :: "+ fileCompletionMessage.fileName + " by "+ this.self() + "from ::" + this.sender());
            // i.e. tell directory actor
            this.getContext().getParent().tell( fileCompletionMessage,getSelf());

            //Finally, stop this actor as file processing is completed and same is no longer required
            this.context().stop(this.self());
        })
        .build();
    }

    static public class FileMessage {
        private final String fileName;
        public FileMessage(String fileName) { this.fileName = fileName;}
    }

    static  public class FileCompletionMessage{
        private final String fileName;
        public FileCompletionMessage(String fileName) { this.fileName = fileName;}

        public String getFileName() {
            return fileName;
        }
    }
}

