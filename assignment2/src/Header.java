public class Header {

    private String length;
    private String type;
    private String httpResponse;

    public void setType(String t){
        type = "Content-Type: " + t + " \r\n";
    }

    public void setLength(int l){
        length = "Content-Length: " + l + " \r\n\r\n";
    }

    public void setHttpResponse(String httpCode){
        if(httpCode.equals("403")){
            httpResponse = "HTTP/1.1 403 Forbidden Resource "+"\r\n";
        }else if(httpCode.equals("500")){
            httpResponse = "HTTP/1.1 500 Internal Server Error "+"\r\n";
        }else if(httpCode.equals("404")){
            httpResponse = "HTTP/1.1 404 NOT FOUND "+"\r\n";
        }else if(httpCode.equals("200")){
            httpResponse = "HTTP/1.1 200 OK /r/n"+"\r\n";
        }
    }

    public String getHeader(){
        return httpResponse+type+length;
    }

    public String getHttpResponse(){
        return httpResponse;
    }
}
