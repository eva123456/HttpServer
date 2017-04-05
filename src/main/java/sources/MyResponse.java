package sources;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by eva on 02.04.17.
 */
public class MyResponse {

    private FileStorage fileStorage;
    private String path;
    private String method;
    private static HashMap<String, String > contentTypes;
    private boolean badRequest;
    private static final String success = "200 OK";
    private String request;


    public MyResponse(MyRequest request){
        this.request = request.allReq;
        method = request.getMethod();
        if(!request.methodIsValid(method)){
            badRequest = true;
            return;
        }
        path = request.getFilePath();
        if(path == null){
            badRequest = true;
            return;
        }
        fileStorage = new FileStorage(path);//initFile
        contentTypes = new HashMap<>();
        fillContentTypes();
    }

    private void fillContentTypes(){
        contentTypes.put(".html", "text/html");
        contentTypes.put(".txt", "text/html");
        contentTypes.put(".css", "text/css");
        contentTypes.put(".js", "text/javascript");
        contentTypes.put(".jpg", "image/jpeg");
        contentTypes.put(".jpeg", "image/jpeg");
        contentTypes.put(".png", "image/png");
        contentTypes.put(".gif", "image/gif");
        contentTypes.put(".swf", "application/x-shockwave-flash");
    }

    public void write(OutputStream output) throws IOException {
        String status = getStatus(path);
        String responseHeaders = buildHeaders(status);
        //System.out.println(responseHeaders);
        output.write(responseHeaders.getBytes());
        if ((method != null)&&method.equals("GET")&&status.equals(success)) {
            try {
                fileStorage.showFile(output);
            }catch (SocketException e){
                System.out.println("Response :: write : request = " + request);
            }

        }
        output.flush();
    }

    private String buildHeaders(String status){
        String headers = "HTTP/1.1 " + status + "\r\n" +
                "Date: " + (new Date()).toString() + "\r\n"+
                "Server: HttpServer\r\n";
        if(!status.equals(success)){
            headers += "Connection: close\r\n\r\n";
        }else {
            headers += "Content-Type: " + getContentType(path)+"\r\n" +
                    "Content-Length: " + fileStorage.getContentLength() + "\r\n" +
                    "Connection: close\r\n\r\n";
        }
        return headers;
    }

    private String getContentType(String path){
        if (fileStorage.isDirectory()){
            return contentTypes.get(".html");
        }
        int pos = path.lastIndexOf('.');
        String extension = path.substring(pos);
        return contentTypes.get(extension);
    }

    private String getStatus(String path){
        if(badRequest){
            return "400 Bad Request";
        }
        if(fileStorage.notfound){
            return "404 Not Found";
        }
        if(fileStorage.forbidden){
            return "403 Forbidden";
        }
        return success;
    }

}
