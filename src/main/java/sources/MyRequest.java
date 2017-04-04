package sources;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by eva on 02.04.17.
 */
public class MyRequest {

    private String requestMethod;
    private String filePath;

    public MyRequest(String request) throws UnsupportedEncodingException {
        this.requestMethod = findMethod(request);
        this.filePath = findPath(request);
    }

    public String findMethod(String request){
        int pos = request.indexOf(' ');
        if(pos == -1)
            return null;
        String method = request.substring(0, pos);
        if(methodIsValid(method)){
            return method;
        }else {
            return null;
        }
    }

    public String findPath(String request) throws UnsupportedEncodingException {
        String path;
        int firstPos = request.indexOf(' ');
        int secondPos = request.indexOf(' ', ++firstPos);
        if((firstPos == -1) || (secondPos == -1))
            return null;
        path = request.substring(firstPos, secondPos);
        path = URLDecoder.decode(path, "UTF-8");//for backspaces
        return path;
    }

    public boolean methodIsValid(String method){
        if(method == null){
            return false;
        }
        return method.equals("GET") || method.equals("HEAD");
    }

    public String getMethod(){
        return requestMethod;
    }


    public String getFilePath(){
        return filePath;
    }

}
