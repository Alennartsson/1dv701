public class Header {

    private String length;
    private String type;
    private String httpResponse;
    private String location;

    public void setType(String t){
        type = "Content-Type: " + t + " \r\n";
    }

    public void setLength(int l){
        length = "Content-Length: " + l + " \r\n\r\n";
    }

    public void setLocation(String loc){
        location = "Location: "+loc +" \r\n";
    }

    public void setHttpResponse(String httpCode){
        switch (httpCode) {
            case "403":
                httpResponse = "HTTP/1.1 403 Forbidden " + "\r\n";
                break;
            case "500":
                httpResponse = "HTTP/1.1 500 Internal Server Error " + "\r\n";
                break;
            case "404":
                httpResponse = "HTTP/1.1 404 NOT FOUND " + "\r\n";
                break;
            case "200":
                httpResponse = "HTTP/1.1 200 OK " + "\r\n";
                break;
            case "302":
                httpResponse = "HTTP/1.1 302 Found " + "\r\n";
                break;
        }
    }

    public String getHeader(){
        return httpResponse+type+length;
    }

    public String getHttpResponse(){
        return httpResponse;
    }

    public String getLocation(){
        return location;
    }
}
