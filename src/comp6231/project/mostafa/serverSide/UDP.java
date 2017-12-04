package comp6231.project.mostafa.serverSide;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.SocketException;

import net.rudp.ReliableSocket;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.shared.Constants;

public class UDP extends Thread{		
	private String result;
	private MessageHeader args;
	private int serverPort;
	private String udpId;
	
	public UDP(MessageHeader args, int serverPort, String udpId){
		this.args = args;
		this.serverPort = serverPort;
		this.result = null;
		this.udpId = udpId;
	}

	@Override
	public void run() {
		ReliableSocket aSocket = null;
		try {
			aSocket = new ReliableSocket();
			aSocket.connect(new InetSocketAddress("127.0.0.1", serverPort));
			byte [] m = Server.gson.toJson(args).getBytes();

			OutputStream out = aSocket.getOutputStream();
			out.write(m);
			out.flush();
			out.close();

			Server.log("UDP Socket Requested: "+ args);
			
			byte[] buffer = new byte[Constants.BUFFER_SIZE];
			InputStream in = aSocket.getInputStream();
			int size = in.read(buffer);
			
			result = new String(buffer, 0 ,size);
		}catch (SocketException e){
			Server.log("Socket: " + e.getMessage());
		}catch (IOException e){
			Server.log("IO: " + e.getMessage());
		}finally {
			if(aSocket != null)
				try {
					aSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} 	
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @return the udpId
	 */
	public String getUdpId() {
		return udpId;
	}
}
