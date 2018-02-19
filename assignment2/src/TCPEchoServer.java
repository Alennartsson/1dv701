import java.io.*;
import java.net.*;


public class TCPEchoServer {
    public static final int MYPORT = 4950;

    public static void main(String[] args) throws IOException {
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
            while(true){    //This loop should keep looping untill the client is disconnected.
                try {
                    int byteReader = in.read(buf);      //We read the received package from the client
                    if (byteReader != -1) {     //If that package isnt lower than 0, lower than 0 means that it dont contain any message.Then we should load a new package.
                        finalMessage = new String(buf, 0, byteReader);      //Add that package to the string
                        out.write(finalMessage.getBytes());             //Send that message to the client, this is done untill the hole message is sent.
                        System.out.printf("TCP echo request from %s", tcpSocket.getInetAddress());
                        System.out.printf(" using port %d\n", tcpSocket.getPort());
                        System.out.println("Received message: " + finalMessage.length() + " bytes" +" "+finalMessage);

                    }
                }catch (Exception e){
                    System.out.printf("Client ["+clientId+"] disconnected ");       //If we disconnect in the middel of a transfer we need to break
                    break;
                }

            }

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

