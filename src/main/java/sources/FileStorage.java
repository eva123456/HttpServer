package sources;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by eva on 02.04.17.
 */
public class FileStorage {

    private static String DOCUMENT_ROOT = "/home/eva/highload/DOCUMENT_ROOT";
    private static final String INDEX = "/index.html";
    private File currentFile;
    private boolean isDirectory;

    FileStorage(String path){
        DOCUMENT_ROOT = "/home/eva/highload/DOCUMENT_ROOT";
        isDirectory = false;
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

    public long getContentLength(String path){
        String fullPath = (new StringBuilder()).append(DOCUMENT_ROOT)
                .append(path).toString();
        File file = new File(fullPath);
        return file.length();
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
