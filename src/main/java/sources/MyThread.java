package sources;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by eva on 04.04.17.
 */
public class MyThread extends Thread {
    private int id;
    private ConcurrentLinkedQueue<Socket> taskQueue = new ConcurrentLinkedQueue<>();
    private Socket socket;
    private InputStream input;
    private OutputStream output;

    MyThread(int id) throws IOException {
        this.id = id;
        start();
    }

    public void init(Socket socket) throws IOException {
        this.socket = socket;
        this.input = socket.getInputStream();
        this.output = socket.getOutputStream();
    }

    public void putTaskIntoQueue(Socket newTask){
        taskQueue.add(newTask);
    }

    @Override
    public void run() {
        while (true){
            while (!taskQueue.isEmpty()){
                socket = taskQueue.poll();
                try {
                    init(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            }
            HttpServer.setFree(this.id);
        }
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
