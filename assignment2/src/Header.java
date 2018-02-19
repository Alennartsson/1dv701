public class Header {

    public String getType(String type, String str){
        return "Content-Type: " + type + " " + str;
    }

    public String getLength(long length, String str){
        return "ContentLength: " + length + " " + str;
    }
}
