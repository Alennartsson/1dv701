public class HttpRequest {

    private String file;

    public HttpRequest(String path){

        String[] tmp = path.split("\n");
        setFile(tmp[0].split(" ")[1]);
    }
    private void setFile(String file){
        this.file = file;
    }

    public String getFile(){
        return file;
    }
}
