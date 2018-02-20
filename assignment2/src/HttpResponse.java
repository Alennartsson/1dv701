import java.io.*;

public class HttpResponse {

    public String response;
    private byte[] data;
    private boolean isImage = false;
    private boolean is302 = false;
    private FileInputStream file = null;

    public HttpResponse(HttpRequest request, byte[] buf , int id, Header header) throws IOException {
        if(check403(request.getFilePath())){
            header.setHttpResponse("403");
        }else if(check302(request.getFilePath())){
            header.setHttpResponse("302");
            header.setLocation("https://www.google.se/");
            is302 = true;
        }else {
            int byteReader = 0;
            if (request.getFilePath().matches(".*.png") || request.getFilePath().matches(".*.ico")) {
                if (request.getFilePath().matches(".*.png")) {
                    header.setType("image/png");
                } else {
                    header.setType("image/ico");
                }
                try {
                    file = new FileInputStream(request.getFilePath());

                    header.setHttpResponse("200");
                    data = new byte[file.available()];
                    file.read(data);
                    isImage = true;
                    header.setLength(getData().length);
                } catch (FileNotFoundException e) {
                    header.setHttpResponse("404");
                }
            } else if (request.getFilePath().matches(".*.html") || request.getFilePath().matches(".*.htm")) {
                try {
                    file = new FileInputStream(request.getFilePath());
                    header.setHttpResponse("200");
                    while ((byteReader = file.read(buf)) != -1) {
                        setResponse(new String(buf, 0, byteReader));
                    }
                    isImage = false;

                } catch (FileNotFoundException e) {
                    header.setHttpResponse("404");
                }

            } else {
                String path = request.getFilePath();
                if (path.isEmpty()) {
                    path += System.getProperty("user.dir");
                }
                File f = new File(path);
                File[] matchingFiles = f.listFiles(new FileFilter() {
                    public boolean accept(File pathname) {
                        String name = pathname.getName().toLowerCase();
                        return name.startsWith("index") && name.endsWith(".html") || name.endsWith(".htm");
                    }
                });

                try{
                    if (matchingFiles.length == 0) {
                        System.out.println("sadsaa");
                        header.setHttpResponse("404");
                        isImage = false;
                    } else {
                        header.setHttpResponse("200");
                        file = new FileInputStream(matchingFiles[0].getAbsolutePath());
                        while ((byteReader = file.read(buf)) != -1) {
                            setResponse(new String(buf, 0, byteReader));
                        }
                        isImage = false;
                    }
                }catch (Exception e){
                    header.setHttpResponse("404");
                }

            }
            try {
                file.close();
            } catch (Exception e) {
            }
        }
    }

    public boolean check403(String filePath){
        if(filePath.toLowerCase().startsWith("root") || filePath.toLowerCase().startsWith(System.getProperty("user.dir").toLowerCase()+"root")){
            return true;
        }
        return  false;
    }

    public boolean check302(String filePath){
        if(filePath.toLowerCase().startsWith("google") || filePath.toLowerCase().startsWith(System.getProperty("user.dir").toLowerCase()+"google")){
            return true;
        }
        return  false;
    }


    public void setResponse(String in){
        response = in;
    }

    public String getResponse(){
        return response;
    }

    public boolean isImage(){return  isImage;}

    public boolean isIs302(){return  is302;}

    public byte[] getData() {
        return data;
    }

}
