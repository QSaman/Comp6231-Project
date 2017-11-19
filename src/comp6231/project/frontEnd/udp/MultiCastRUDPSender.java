package comp6231.project.frontEnd.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import comp6231.project.frontEnd.FE;
import comp6231.project.messageProtocol.MessageHeader;

public class MultiCastRUDPSender extends Thread{

	private String result;
	private MessageHeader args;
	private int serverPort;
	private String udpId;
	
	public MultiCastRUDPSender(MessageHeader args, int serverPort, String udpId){
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
			byte [] m = FE.gson.toJson(args).getBytes();
			InetAddress aHost = InetAddress.getByName("localhost"); 
			DatagramPacket request =
					new DatagramPacket(m, m.length, aHost, serverPort); 
			aSocket.send(request);
			FE.log("UDP Socket Requested: "+ args);
			byte[] buffer = new byte[1000];
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			aSocket.receive(reply);
			
			result = new String(reply.getData()).trim();
		}catch (SocketException e){
			FE.log("Socket: " + e.getMessage());
		}catch (IOException e){
			FE.log("IO: " + e.getMessage());
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
