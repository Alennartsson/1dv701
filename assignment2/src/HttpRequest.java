public class HttpRequest {

    private String file;
    private String req;
    private String version;

    public HttpRequest(String path){

        String[] tmp = path.split("\n");
        setReq(tmp[0].split(" ")[0]);
        setFile(tmp[0].split(" ")[1]);
        setVersion(tmp[0].split(" ")[2]);

    }

    private void setReq(String req){
        this.req = req;
    }

    private String getReq(){
        return req;
    }

    private void setFile(String file){
        this.file = file;
    }

    public String getFile(){
        return file;
    }

    public void setVersion(String ver){
        this.version = ver;
    }

    public String getVersion(){
        return version;
    }
}
