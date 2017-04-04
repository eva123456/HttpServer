package sources;

import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static javafx.application.Platform.exit;

/**
 * Created by eva on 02.04.17.
 */
public class HttpServer {
    private static int PORT;
    private static int NCPU;
    private static String ROOTDIR;
    private static HashMap<String, Object> params = new HashMap<>(3);
    private static ArrayList<MyThread> threads = new ArrayList<MyThread>();
    private static ArrayList<Boolean> freeThreads = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        fillParams(args);

        PORT = (Integer) params.get("-p");
        NCPU = (Integer) params.get("-c");
        ROOTDIR = (String) params.get("-r");
        FileStorage.DOCUMENT_ROOT = ROOTDIR;

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        for(int i = 0; i < NCPU; ++i){
            threads.add(i, new MyThread(i));
            freeThreads.add(i, true);
        }

        int threadID = 0;
        while (true){
            Socket socket = serverSocket.accept();
            if(freeThreads.get(threadID)){
                //get free thread and do the task
                threads.get(threadID).putTaskIntoQueue(socket);
                setBusy(threadID);
            } else {
                threads.get(threadID).putTaskIntoQueue(socket);
                ++threadID;
            }
            if(threadID % NCPU == 0){
                threadID = 0;
            }
        }
    }

    public static void setFree(int id){
        freeThreads.set(id, true);
    }

    public static void setBusy(int id){
        freeThreads.set(id, false);
    }

    private static void fillParams(String[] args){
        params.put("-r", FileStorage.DOCUMENT_ROOT);
        params.put("-c", 4);
        params.put("-p", 8080);

        for(int i = 0; i < args.length; i+=2){
            if (params.containsKey(args[i])){
                if(args[i].equals("-r")){
                    params.put(args[i], args[i+1]);//regexp?
                } else {
                    try {
                        params.put(args[i], Integer.parseInt(args[i+1]));
                    } catch (Exception exception){
                        System.out.println("Unknown parameter value " + args[i+1] + "for" + args[i]);
                        System.exit(0);
                    }
                }
            }
            else{
                System.out.println("Unknown option " + args[i]);
                System.out.println("Usage -r ROOTDIR, -c NCPU, [-p PORT]");
                System.exit(0);
            }
        }
    }
}
