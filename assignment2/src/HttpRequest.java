public class HttpRequest {

    private String filePath;
    private String req;
    private String version;

    public HttpRequest(String path){

        String[] tmp = path.split("\n");
        setReq(tmp[0].split(" ")[0]);
        setFilePath(tmp[0].split(" ")[1]);
        setVersion(tmp[0].split(" ")[2]);

    }

    private void setReq(String req){
        this.req = req;
    }

    public String getReq(){
        return req;
    }

    private void setFilePath(String file){
        this.filePath = file;
    }

    public String getFilePath(){
        return filePath;
    }

    private void setVersion(String ver){
        this.version = ver;
    }

    public String getVersion(){
        return version;
    }
}
