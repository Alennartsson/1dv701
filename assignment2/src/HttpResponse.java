import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HttpResponse {

    private String response;

    public HttpResponse(HttpRequest request, byte[] buf) throws IOException {
        FileInputStream file = null;
        try {
            file = new FileInputStream(request.getFilePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found");
        }
        int byteReader = 0;
        while ((byteReader = file.read(buf)) != -1){
               setResponse(new String(buf, 0 , byteReader));
        }
        System.out.println("RESPONSE: " +getResponse());
    }

    public void setResponse(String in){
        response = in;
    }

    public String getResponse(){
        return response;
    }
}
