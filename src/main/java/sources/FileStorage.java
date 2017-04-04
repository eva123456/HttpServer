package sources;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by eva on 02.04.17.
 */
public class FileStorage {

    public static String DOCUMENT_ROOT = "/home/eva/highload/DOCUMENT_ROOT";
    private static final String INDEX = "/index.html";
    private File currentFile;
    private boolean isDirectory;
    public static boolean forbidden;
    public static boolean notfound;

    FileStorage(String path){
        isDirectory = false;
        forbidden = (path.indexOf("../") != -1);
        notfound = false;
        initFile(path);
    }

    private void initFile(String path){
        StringBuilder sb = new StringBuilder()
                .append(DOCUMENT_ROOT)
                .append(path);
        currentFile = new File(sb.toString());
        if(currentFile.isDirectory()){
            sb.append(INDEX);
            currentFile = new File(sb.toString());
            isDirectory = true;
        }
        forbidden = forbidden || (isDirectory() && !fileExist()) || !accessAllowed();
        notfound = (!isDirectory() && !fileExist());
    }

    //get file and send it out
    public void showFile(OutputStream output) throws IOException {
        //initFile(path);
        if(fileExist()){
            java.nio.file.Path fullPath = Paths.get(currentFile.getAbsolutePath());
            byte[] content = Files.readAllBytes(fullPath);
            output.write(content);
        }
    }

    public long getContentLength(){
        return currentFile.length();
    }

    public boolean isDirectory(){
        return this.isDirectory;
    }


    public boolean fileExist(){
        return currentFile.exists();
    }

    public boolean accessAllowed(){
        return currentFile.canRead();
    }

}
