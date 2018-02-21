import java.io.*;
import java.net.*;


public class TCPEchoServer {
    public static final int MYPORT = 8888;

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket conectionSocket = new ServerSocket(MYPORT);  //Create a socket with the used ip
        int id = 1; //Id that is used to give the clients id.
        while (true) {      //While loop that waits for connection and starts a new thread if conneceted
            Socket connectionSocket = conectionSocket.accept(); //Waits for a connection and creates a new socket for each connection
            System.out.println("Client ["+id+"] connected!");
            tcpClient client = new tcpClient(connectionSocket, id++);
            client.start();
        } 
    }
}

class tcpClient extends Thread{
   private Socket tcpSocket;
   private int clientId;
   private static final int BUFSIZE = 1024;
   private boolean test500 = false;

   tcpClient(Socket socket, int id){        //Setting the socket and client id
        tcpSocket = socket;
        clientId = id;
    }
    @Override
    public void run() {
        String finalMessage;        //String for saving the message tha was received
        byte[] buf= new byte[BUFSIZE];
        try{
            InputStream in = tcpSocket.getInputStream();      //InputStream that loads the message from the client
            OutputStream out = tcpSocket.getOutputStream();      //OutputStrea that sends message to the client
            Header header = new Header();
            //while(true){    //This loop should keep looping untill the client is disconnected.
                try {
                    try{
                        if(test500) { throw new IOException();}
                        int byteReader = in.read(buf);      //We read the received package from the client
                        if (byteReader != -1) {     //If that package isnt lower than 0, lower than 0 means that it dont contain any message.Then we should load a new package.
                            finalMessage = new String(buf, 0, byteReader);      //Add that package to the string
                            HttpRequest request = new HttpRequest(finalMessage);
                            System.out.println("Filepath: "+request.getFilePath() +" requestType: " +request.getReq() +" HttpVersion: " +request.getVersion());
                            HttpResponse response = new HttpResponse(request,buf,clientId, header);;
                            //out.write("HTTP/1.1 200 OK /r/n".getBytes());
                            if(response.isImage()){
                                //  String test = "HTTP/1.1 200 OK \r\nContent-Type: image/png \r\nContent-Length: "+response.getData().length+" \r\n\r\n";
                                System.out.println("Där");
                                System.out.println("header: "+header.getHeader());
                                out.write(header.getHeader().getBytes());
                                out.write(response.getData());
                            }else{
                                System.out.println("Här");
                                System.out.println("Response: "+header.getHttpResponse());
                                out.write(header.getHttpResponse().getBytes());
                                if(response.isIs302()){out.write(header.getLocation().getBytes());}
                                out.write(response.getResponse().getBytes("UTF-8"));             //Send that message to the client, this is done untill the hole message is sent.
                            }
                        }
                    }catch (IOException e){
                        header.setHttpResponse("500");
                        out.write(header.getHttpResponse().getBytes());
                    }

                }catch (Exception e){
                    System.out.printf("Client ["+clientId+"] disconnected ");       //If we disconnect in the middel of a transfer we need to break
                   // break;
                }

        //    }

        } catch (IOException e) {
        e.printStackTrace();
    }

        try{
            tcpSocket.close();          //Closing the socket
        } catch (IOException e) {
            System.out.printf("Cant close socket for connection id: "+clientId);
        }
        Thread.currentThread().interrupt();     //Shutting down the thread

    }
}

