
import javax.xml.crypto.Data;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;


public class TFTPServer 
{
	public static final int TFTPPORT = 4970;
	public static final int BUFSIZE = 516;
	public static final String READDIR = "C:/read/"; //custom address at your PC
	public static final String WRITEDIR = "C:/read/"; //custom address at your PC
	// OP codes
	public static final int OP_RRQ = 1;
	public static final int OP_WRQ = 2;
	public static final int OP_DAT = 3;
	public static final int OP_ACK = 4;
	public static final int OP_ERR = 5;

	public static void main(String[] args) {
		if (args.length > 0) 
		{
			System.err.printf("usage: java %s\n", TFTPServer.class.getCanonicalName());
			System.exit(1);
		}
		//Starting the server
		try 
		{
			TFTPServer server= new TFTPServer();
			server.start();
		}
		catch (SocketException e) 
			{e.printStackTrace();}
	}
	
	private void start() throws SocketException 
	{
		byte[] buf= new byte[BUFSIZE];
		
		// Create socket
		DatagramSocket socket= new DatagramSocket(null);
		
		// Create local bind point 
		SocketAddress localBindPoint= new InetSocketAddress(TFTPPORT);
		socket.bind(localBindPoint);

		System.out.printf("Listening at port %d for new requests\n", TFTPPORT);

		// Loop to handle client requests 
		while (true) 
		{        
			
			final InetSocketAddress clientAddress = receiveFrom(socket, buf);
			
			// If clientAddress is null, an error occurred in receiveFrom()
			if (clientAddress == null) 
				continue;

			final StringBuffer requestedFile= new StringBuffer();
			final int reqtype = ParseRQ(buf, requestedFile);

			new Thread() 
			{
				public void run() 
				{
					try 
					{
						DatagramSocket sendSocket= new DatagramSocket(0);

						// Connect to client
						sendSocket.connect(clientAddress);						
						
						System.out.printf("%s request for %s from %s using port\n",
								(reqtype == OP_RRQ)?"Read":"Write",
								clientAddress.getHostName(), clientAddress.getPort());
								
						// Read request
						if (reqtype == OP_RRQ) 
						{      
							requestedFile.insert(0, READDIR);
							HandleRQ(sendSocket, requestedFile.toString(), OP_RRQ);
						}
						// Write request
						else 
						{                       
							requestedFile.insert(0, WRITEDIR);
							HandleRQ(sendSocket,requestedFile.toString(),OP_WRQ);  
						}
						sendSocket.close();
					} 
					catch (SocketException e) 
						{e.printStackTrace();}
				}
			}.start();
		}
	}
	
	/**
	 * Reads the first block of data, i.e., the request for an action (read or write).
	 * @param socket (socket to read from)
	 * @param buf (where to store the read data)
	 * @return socketAddress (the socket address of the client)
	 */
	private InetSocketAddress receiveFrom(DatagramSocket socket, byte[] buf)
	{
		// Create datagram packet
		DatagramPacket packet = new DatagramPacket(buf, buf.length);
		
		// Receive packet
		try {
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Get client address and port from the packet
		return new InetSocketAddress(packet.getAddress(), packet.getPort());
	}

	/**
	 * Parses the request in buf to retrieve the type of request and requestedFile
	 * 
	 * @param buf (received request)
	 * @param requestedFile (name of file to read/write)
	 * @return opcode (request type: RRQ or WRQ)
	 */
	private int ParseRQ(byte[] buf, StringBuffer requestedFile) 
	{
		// See "TFTP Formats" in TFTP specification for the RRQ/WRQ request contents
		ByteBuffer wrap = ByteBuffer.wrap(buf);
		short opcode = wrap.getShort();
		String fileName = new String(buf, 2, buf.length-2);
		requestedFile.append(fileName);

		return opcode;
	}

	/**
	 * Handles RRQ and WRQ requests 
	 * 
	 * @param sendSocket (socket used to send/receive packets)
	 * @param requestedFile (name of file to read/write)
	 * @param opcode (RRQ or WRQ)
	 */
	private void HandleRQ(DatagramSocket sendSocket, String requestedFile, int opcode)
	{
		String[] reSplit = requestedFile.split("\0");
		String file = reSplit[0];
		String mode = reSplit[1];
		byte[] buf;

		if (mode.equals("octet")){
			if(opcode == OP_RRQ)
			{
				int block = 0;
				// See "TFTP Formats" in TFTP specification for the DATA and ACK packet contents
				boolean result = send_DATA_receive_ACK(sendSocket, requestedFile, ++block);
			}
			else if (opcode == OP_WRQ)
			{
				int block = 0;
				boolean result = receive_DATA_send_ACK(sendSocket, requestedFile, block);
			}
			else
			{
				System.err.println("Invalid request. Sending an error packet.");
				// See "TFTP Formats" in TFTP specification for the ERROR packet contents
				try {
					send_ERR(sendSocket,"Error packet sent", 0);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return;
			}
		}

	}

	/**
	To be implemented
	*/
	private boolean send_DATA_receive_ACK(DatagramSocket socket, String file, int block) {

		boolean checkSize = false;
		try {
			String[] reSplit = file.split("\0");
			File fileName = new File(reSplit[0]);

			if (!fileName.exists()) {
				send_ERR(socket, "File not found!", 1);

			} else {
			FileInputStream in = new FileInputStream(fileName);


			while (true) {

				//buf equals 512 to leave space for the op code and block #
				byte[] buf = new byte[BUFSIZE - 4];
				int byteReader = in.read(buf);
				if (byteReader < 512) {
					checkSize = true;
				}

				//DATA packet contains Opcode, Block # and data
				ByteBuffer data = ByteBuffer.allocate(BUFSIZE);
				data.putShort((short) OP_DAT);
				data.putShort((short) block);
				data.put(buf);

				try {
					//send packet
					socket.setSoTimeout(3000);
					DatagramPacket packet = new DatagramPacket(data.array(), byteReader + 4);
					socket.send(packet);

					//receive acknowledgement
					ByteBuffer ack = ByteBuffer.allocate(OP_ACK);
					DatagramPacket ackReceive = new DatagramPacket(ack.array(), ack.array().length);
					socket.receive(ackReceive);

					ByteBuffer buffer = ByteBuffer.wrap(ackReceive.getData());
					short opcode = buffer.getShort();
					short ackBlock = buffer.getShort();
					if(opcode == OP_ERR){
						send_ERR(socket, "Error packet received",0);
						break;
					}

					if (ackBlock == block) { //checks that the ack and the data has the same block#
						block++;
					}

					if (checkSize) {
						break;
					}
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					return false;
				}catch (IOException e){
					send_ERR(socket,"Access violation", 2);
				}
			}
		}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	private boolean receive_DATA_send_ACK(DatagramSocket socket, String file, int block) {

		boolean checkSize = false;

		String[] reSplit = file.split("\0");
		File fileName = new File(reSplit[0]);

		if(fileName.exists()){
			try {
				send_ERR(socket,  "File already exist!" ,6);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else {
			try {
				FileOutputStream out = new FileOutputStream(fileName);

				ByteBuffer ack = ByteBuffer.allocate(OP_ACK);
				ack.putShort((short) OP_ACK);
				ack.putShort((short) block);

				DatagramPacket packet = new DatagramPacket(ack.array(), ack.array().length);  //Sending ack
				socket.send(packet);


				while (true) {
					byte[] data = new byte[BUFSIZE];
					DatagramPacket dataReceive = new DatagramPacket(data, data.length);
					socket.receive(dataReceive);

					if (dataReceive.getData().length < 512) {
						checkSize = true;
					}

					ByteBuffer buffer = ByteBuffer.wrap(data);
					short opcode = buffer.getShort();

					if (opcode == OP_DAT) {
						out.write(Arrays.copyOfRange(dataReceive.getData(), 4, dataReceive.getLength()));
						ByteBuffer sendAck = ByteBuffer.allocate(OP_ACK);
						sendAck.putShort((short) OP_ACK);
						sendAck.putShort(buffer.getShort());
						DatagramPacket acknowledgment = new DatagramPacket(sendAck.array(), sendAck.array().length);
						socket.send(acknowledgment);
					}

					if (checkSize) {
						break;
					}
				}

				out.flush();
				out.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return true;
	}

	private void send_ERR(DatagramSocket socket, String error, int errorNumber) throws IOException {
	ByteBuffer errBuf = ByteBuffer.allocate(error.length() + OP_ERR);
	errBuf.putShort((short) OP_ERR);
	errBuf.putShort((short) errorNumber);
	errBuf.put(error.getBytes());

	DatagramPacket sendError = new DatagramPacket(errBuf.array(), errBuf.array().length);

	socket.send(sendError);

	}

	
}



