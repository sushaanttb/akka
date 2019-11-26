import actors.FileActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainApplication {

    public static void main(String[] args){
        System.out.println("Hello world!");

//        CassandraUtils.drop("test");
//        CassandraUtils.createESTSchema("test");

        System.out.println(FileUtils.getFiles("tmp"));

        // approach: FileActor, LineActor
        // A new fileactor per file name :"abc.txt" so if we have 100 files, 100 file actors
        // FileActor : Every fileactor will parse the file and create a new child lineActor for all lines to be parsed
        // (creating a new lineactor for each line doesn't seems to be a good idea still can be changed afterwards if reqd)
        // LineActor The line actor will dump to DB

        //ToDo: knowing when file is processed, dumping into Archive folder   :Done
        //scenarios: failover scenarios what if there was an error in processing the file itself, and error in line apart from the known error fields scenario?
        //suggestion : to use est_log table for file processing check with status success,error, total batch time, last run
        final ActorSystem actorSystem = ActorSystem.create("file-reader");

        //ToDo: Scenario: what if new files get added during the run process?
        List<File> file = FileUtils.getFiles("tmp");

        //ToDo: Scenario: what if we get same file name added during the run process
        file.forEach(f->
                //New FileActor for every file
                actorSystem.actorOf(FileActor.props())
                        .tell(new FileActor.FileMessage(f.getName()),ActorRef.noSender())
        );

        try{
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        }catch(IOException ex){
        }finally {
            System.out.println("Actor Terminated.");
            actorSystem.terminate();
        }

    }




}
