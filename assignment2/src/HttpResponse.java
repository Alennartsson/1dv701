import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HttpResponse {

    public String response;

    public HttpResponse(HttpRequest request, byte[] buf , int id) throws IOException {
        FileInputStream file = null;
        System.out.println(System.getProperty("user.dir"));
        try {
            file = new FileInputStream(request.getFilePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found for: "+id);
        }
        int byteReader = 0;
        while ((byteReader = file.read(buf)) != -1){
               setResponse(new String(buf, 0 , byteReader));
        }
       file.close();
    }

    public void setResponse(String in){
        response = in;
    }

    public String getResponse(){
        return response;
    }
}
