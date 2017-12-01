package comp6231.project.frontEnd.udp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

import comp6231.project.frontEnd.FE;
import comp6231.project.frontEnd.messages.FEReplyMessage;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.messageProtocol.MessageHeader.MessageType;
import comp6231.project.messageProtocol.MessageHeader.ProtocolType;
import comp6231.shared.Constants;
import net.rudp.ReliableSocket;

public class FEUDPConnection extends Thread{
	private ReliableSocket socket;
	private InputStream in;
	private OutputStream out;
	private final Object sendLock = new Object();
	
	public FEUDPConnection(ReliableSocket socket) {
		this.socket = socket;
	}
	
	@Override
	public void run() {	
		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
			
			byte[] buffer = new byte[Constants.BUFFER_SIZE];
			int size = in.read();
			
			String json_msg_str = new String(buffer,0,size);

			FE.log("MULTI CAST UDP Socket Received JSON: "+json_msg_str);
			String result = process(json_msg_str);
			byte[] data = result.getBytes();

//			send(request.getAddress(), request.getPort(), data);
			
			
		}catch (SocketException e){
			FE.log("Socket: " + e.getMessage());
		}catch (IOException e){
			FE.log("IO: " + e.getMessage());
		}finally {
			if(socket != null)
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	
//	private void send(InetAddress address, int port, byte[] data){
//		DatagramPacket reply = new DatagramPacket(
//				data, 
//				data.length, 
//				address, 
//				port
//		);
//
//		try {
//			synchronized (sendLock) {
//				socket.send(reply);
//			}
//		} catch (IOException e) {
//			FE.log(e.getMessage());
//		}
//	}
	
	private String process(String json_msg_str){
		String result = null;
		MessageHeader json_msg = FE.gson.fromJson(json_msg_str, MessageHeader.class);
		
		if(json_msg.protocol_type == ProtocolType.Frontend_To_Replica){
			if(json_msg.message_type == MessageType.Reply){
				FEReplyMessage replyMessage = (FEReplyMessage) json_msg;
				result = processFrontEndToserver(replyMessage);
			}
		}
		return result;
	}
	
	private String processFrontEndToserver(FEReplyMessage replyMessage){
		String result = "";
		if(replyMessage.status){
			result = replyMessage.replyMessage;
		}else{
			result = "Fake Generator is on";
		}
		
		FE.log(result);
		return result;	
	}
}
