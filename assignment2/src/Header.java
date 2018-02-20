public class Header {

    private long length;
    private String type;

    public Header(String type, long length){
        this.type = type;
        this.length = length;
    }

    public String getType(){
        return "Content-Type: " + type + " \r\n";
    }

    public String getLength(){
        return "ContentLength: " + length + " \r\n";
    }
}
