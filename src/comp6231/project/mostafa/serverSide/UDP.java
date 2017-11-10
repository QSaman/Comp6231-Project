package comp6231.project.mostafa.serverSide;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDP extends Thread{		
	private String result;
	private String args;
	private int serverPort;
	private String udpId;
	
	public UDP(String args, int serverPort, String udpId){
		this.args = args;
		this.serverPort = serverPort;
		this.result = null;
		this.udpId = udpId;
	}

	@Override
	public void run() {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket();
			byte [] m = args.getBytes();
			InetAddress aHost = InetAddress.getByName("localhost"); 
			DatagramPacket request =
					new DatagramPacket(m, m.length, aHost, serverPort); 
			aSocket.send(request);
			Server.log("UDP Socket Requested: "+ args);
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			
			result = new String(reply.getData()).trim();
		}catch (SocketException e){
			Server.log("Socket: " + e.getMessage());
		}catch (IOException e){
			Server.log("IO: " + e.getMessage());
		}finally {
			if(aSocket != null) aSocket.close();
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