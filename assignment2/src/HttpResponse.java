import java.io.*;

public class HttpResponse {

    private String response;
    private byte[] data;
    private boolean isImage = false;
    private boolean is302 = false;
    private FileInputStream file = null;

    public HttpResponse(HttpRequest request, byte[] buf , int id, Header header) throws IOException {
        if(check403(request.getFilePath())){        //Checks to see if the requested file is forbidden
            header.setHttpResponse("403");
        }else if(check302(request.getFilePath())){  //Redirects the user to another page
            header.setHttpResponse("302");
            header.setLocation("https://www.google.se/");
            is302 = true;
        }else {
            int byteReader = 0;
            if (request.getFilePath().matches(".*.png") || request.getFilePath().matches(".*.ico")) {       //if statement to see if the file is a png or ico
                if (request.getFilePath().matches(".*.png")) {
                    header.setType("image/png");                        //sets the type to ico or png if it is
                } else {
                    header.setType("image/ico");
                }
                try {
                    file = new FileInputStream(request.getFilePath());

                    header.setHttpResponse("200");                      //if the file is available the header is set to response 200
                    data = new byte[file.available()];
                    file.read(data);
                    isImage = true;
                    header.setLength(getData().length);
                } catch (FileNotFoundException e) {                     //sends 404 if file is not found
                    header.setHttpResponse("404");
                }
            } else if (request.getFilePath().matches(".*.html") || request.getFilePath().matches(".*.htm")) {
                try {
                    file = new FileInputStream(request.getFilePath());          //else if the file is html or htm
                    header.setHttpResponse("200");
                    while ((byteReader = file.read(buf)) != -1) {
                        setResponse(new String(buf, 0, byteReader));
                    }
                    isImage = false;

                } catch (FileNotFoundException e) {                 //sends 404 if file is not found
                    header.setHttpResponse("404");
                }

            } else {
                String path = request.getFilePath();                //if the requested file does not have a path check the directory
                if (path.isEmpty()) {
                    path += System.getProperty("user.dir");
                }
                File f = new File(path);
                File[] matchingFiles = f.listFiles(new FileFilter() {   //if the directory contains an index file, reads it
                    public boolean accept(File pathname) {
                        String name = pathname.getName().toLowerCase();
                        return name.startsWith("index") && name.endsWith(".html") || name.endsWith(".htm");
                    }
                });

                try{
                    if (matchingFiles.length == 0) {        //If the file is empty, sends 404
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

    private boolean check403(String filePath){
        return filePath.toLowerCase().startsWith("root") || filePath.toLowerCase().startsWith(System.getProperty("user.dir").toLowerCase() + "root");
    }

    private boolean check302(String filePath){
        return filePath.toLowerCase().startsWith("google") || filePath.toLowerCase().startsWith(System.getProperty("user.dir").toLowerCase() + "google");
    }


    private void setResponse(String in){
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
