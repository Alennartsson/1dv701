public class HttpResponses {
    public String HttpResponseOK(){
        String OK = "HTTP/1.1 200 OK /r/n";
        return OK;
    }

    public String HttpResponseNotOK(){
        String str = "HTTP/1.1 404 NOT FOUND";
        return str;
    }

    public String HttpInternalServerError(){
        String str = "HTTP/1.1 500 Internal Server Error";
        return str;
    }

    public String HttpForbidden(){
        String str = "HTTP/1.1 403 Forbidden Resource";
        return str;
    }
}
