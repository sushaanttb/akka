import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FileUtils {

    public static List<File> getFiles(String directoryName){

        File d = new File(directoryName);

        System.out.println("Returning files from directory: "+directoryName);
        if(d.exists() && d.isDirectory()){
            return Arrays.asList(d.listFiles());
        }
        return null;
    }
}
