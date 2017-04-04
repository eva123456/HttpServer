package sources;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * Created by eva on 02.04.17.
 */
public class HttpServer {
    private static int PORT = 8080;
    private static HashMap<String, String> params;

    public static void main(String[] args) throws IOException {
        //command-parser needed
        for(int i = 0; i < args.length; ++i){
            System.out.println(args[i]);
        }

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Start at port 8080!");
        while (true){
            Socket socket = serverSocket.accept();
            new Thread(new SocketTask(socket)).start();
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
            System.out.println("Process finished!\n");
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
}
