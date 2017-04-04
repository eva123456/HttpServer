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
    private static HashMap<Integer, Thread> threads = new HashMap<>();
    private static ArrayList<Boolean> freeThreads = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        fillParams(args);

        PORT = (Integer) params.get("-p");
        NCPU = (Integer) params.get("-c");
        ROOTDIR = (String) params.get("-r");
        FileStorage.DOCUMENT_ROOT = ROOTDIR;

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server started on port " + PORT);

        for(int i = 0; i < 2*NCPU + 1; ++i){
            threads.put(i, new Thread());
            freeThreads.add(i, true);
        }

        int j = 0;
        while (true){
            Socket socket = serverSocket.accept();
            if(freeThreads.get(j)){
                //get free thread and do the task
                freeThreads.set(j, false);
            }
            ++j;
            if(j % NCPU == 0){
                j = 0;
            }
        }
    }

    private static class SocketTask implements Runnable{

        private Socket socket;
        private InputStream input;
        private OutputStream output;

        SocketTask(Socket socket) throws IOException {
            this.socket = socket;
            this.input = socket.getInputStream();
            this.output = socket.getOutputStream();
        }

        public void run() {
            try {
                readRequests();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Process finished\n");
        }

        private void readRequests() throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            StringBuilder sb = new StringBuilder();
            while (true){
                String str = reader.readLine();
                if(str == null || str.trim().length() == 0)
                    break;
                sb.append(str);
            }
            MyRequest request = new MyRequest(sb.toString());
            MyResponse response = new MyResponse(request);
            response.write(output);
        }
    }

    private static void fillParams(String[] args){
        params.put("-r", FileStorage.DOCUMENT_ROOT);
        params.put("-c", null);
        params.put("-p", 80);

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
