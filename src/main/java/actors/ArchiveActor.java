package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;

//dump file to archiveFolder once it's completed
public class ArchiveActor extends AbstractActor {

    static public Props props(){
        return Props.create(ArchiveActor.class, ArchiveActor::new);
    }

    @Override
    public Receive createReceive(){
        return receiveBuilder().match(ArchiveMessage.class, archiveMessage -> {
            System.out.println("Archive Message received for :: "+ archiveMessage.fileName + " by " + this.self() + "from ::" + this.sender());
        }).build();
    }

    static public class ArchiveMessage {
        private final String fileName;
        public ArchiveMessage(String fileName) {this.fileName = fileName;}
    }
}
