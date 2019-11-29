package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
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


    //We don't want to retrieve all the files, we simply want to check if directory is empty or not
    public static boolean isDirEmpty(final String directoryPath) {
        System.out.println("Checking directory in ::"+ Paths.get(directoryPath).toAbsolutePath());
        try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(Paths.get(directoryPath))) {
//              ToDo: This doesnt works?? Why iterator is null for valid directory?
//                return !dirStream.iterator().hasNext();
//            https://stackoverflow.com/questions/5930087/how-to-check-if-a-directory-is-empty-in-java
//              This works
//              dirStream.forEach(path -> System.out.println(path));
                return dirStream==null ;
        }catch (NoSuchFileException ex){
            //even this also works if it is not a valid directory
            System.out.println(directoryPath+ " is not a directory!");
        }catch(Exception ex){
            System.out.println("Some exception occured while checking for directory : "+ directoryPath);
            ex.printStackTrace();
        }
        return false;
    }

    //ToDo: This fails if same file exists already in archive folder , Q: although this case wont be possible but still hw to handle, should we rename the archive file with timestamp?
    public static void move(String sourcePath, String destinationPath){
        try{
            System.out.println("Moving File from "+ Paths.get(sourcePath).toAbsolutePath() +" to "+  Paths.get(destinationPath).toAbsolutePath());

            Path temp = Files.move(Paths.get(sourcePath), Paths.get(destinationPath));

            if(temp != null) System.out.println("File :"+ sourcePath +" moved to : "+  destinationPath +" successfully");
            else System.out.println("Failed to move the file : "+  sourcePath + " to : "+destinationPath);

        }catch(Exception ex){
            System.out.println("Some exception occurred while moving file from sourcePath : "+ sourcePath + " to destinationPath: "+ destinationPath);
            ex.printStackTrace();
        }
    }
}
