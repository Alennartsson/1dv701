import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class HttpResponse {

    public String response;
    private byte[] data;
    private boolean isImage = false;
    private FileInputStream file = null;


    public HttpResponse(HttpRequest request, byte[] buf , int id) throws IOException {
        try {
            file = new FileInputStream(request.getFilePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found for: "+id);
        }
        int byteReader = 0;
        if(request.getFilePath().matches(".*.png") || request.getFilePath().matches(".*.ico") ){
            data = new byte[file.available()];
            file.read(data);
            isImage = true;
        }
        else{
            while ((byteReader = file.read(buf)) != -1){
                setResponse(new String(buf, 0 , byteReader));
            }
            isImage = false;
        }

       file.close();
    }

    public void setResponse(String in){
        response = in;
    }

    public String getResponse(){
        return response;
    }

    public boolean isImage(){return  isImage;}

    public byte[] getData() {
        return data;
    }
}
