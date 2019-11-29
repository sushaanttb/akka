
import actors.DirectoryActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import cassandra.CassandraUtils;
import utils.FileUtils;

import static utils.ESTConstants.*;

public class MainApplication {

    public static void main(String[] args){
        System.out.println("Starting Batch Processing..");

        CassandraUtils.dropKeyspace(EST_KEYSPACE);
        CassandraUtils.createTable(EST_KEYSPACE,CREATE_EST_PURCHASES_TABLE);
        CassandraUtils.createTable(EST_KEYSPACE,CREATE_EST_LOG_TABLE);

        System.out.println(FileUtils.getFiles(SOURCE_DIRECTORY));
        // approach: FileActor, LineActor
        // A new fileactor per file name :"abc.txt" so if we have 100 files, 100 file actors
        // FileActor : Every fileactor will parse the file and create a new child lineActor for all lines to be parsed
        // (creating a new lineactor for each line doesn't seems to be a good idea still can be changed afterwards if reqd)
        // LineActor The line actor will dump to DB

        final ActorSystem actorSystem = ActorSystem.create("file-reader");

        final ActorRef directoryActor = actorSystem.actorOf(DirectoryActor.props());
        directoryActor.tell(new DirectoryActor.DirectoryMessage(SOURCE_DIRECTORY),ActorRef.noSender());

    }

}
