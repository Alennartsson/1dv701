import java.io.*;

public class HttpResponse {

    public String response;
    private byte[] data;
    private boolean isImage = false;
    private FileInputStream file = null;

    public HttpResponse(HttpRequest request, byte[] buf , int id, Header header) throws IOException {
        if(check403(request.getFilePath())){
            header.setHttpResponse("403");
        }
        int byteReader = 0;
        if(request.getFilePath().matches(".*.png") || request.getFilePath().matches(".*.ico") ){
            if(request.getFilePath().matches(".*.png")){header.setType("image/png"); }else{
                header.setType("image/ico");
            }
            try {
                file = new FileInputStream(request.getFilePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("File not found for: "+id);
            }
            header.setHttpResponse("200");
            data = new byte[file.available()];
            file.read(data);
            isImage = true;
            header.setLength(getData().length);
        }
        else if(request.getFilePath().matches(".*.html") || request.getFilePath().matches(".*.htm")){
            try {
                file = new FileInputStream(request.getFilePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("File not found for: "+id);
            }
            header.setHttpResponse("200");
            while ((byteReader = file.read(buf)) != -1){
                setResponse(new String(buf, 0 , byteReader));
            }
            isImage = false;
        } else{
            String path = request.getFilePath();
            if(path.isEmpty()){
                path +=System.getProperty("user.dir");
            }
            File f = new File(path);
            File[] matchingFiles = f.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    String name = pathname.getName().toLowerCase();
                    return name.startsWith("index") && name.endsWith(".html") || name.endsWith(".htm");
                }
            });

            if(matchingFiles.length == 0){
                System.out.println("cant find index file!");
            }else {
                header.setHttpResponse("200");
                file = new FileInputStream(matchingFiles[0].getAbsolutePath());
                while ((byteReader = file.read(buf)) != -1) {
                    setResponse(new String(buf, 0, byteReader));
                }
                isImage = false;
            }
        }

       file.close();
    }

    public boolean check403(String filePath){
        if(filePath.startsWith("Root") || filePath.startsWith(System.getProperty("user.dir")+"Root")){
            return false;
        }
        return  true;
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
