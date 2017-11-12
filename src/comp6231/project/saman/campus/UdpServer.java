/**
 * 
 */
package comp6231.project.saman.campus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import comp6231.project.saman.campus.message_protocol.InitializeSerializer;
import comp6231.project.saman.campus.message_protocol.MessageHeader;
import comp6231.project.saman.campus.message_protocol.MessageHeader.MessageType;
import comp6231.project.saman.campus.message_protocol.MessageHeader.ProtocolType;
import comp6231.project.saman.campus.message_protocol.MessageHeaderDeserializer;
import comp6231.project.saman.campus.message_protocol.ReplyMessageHeader;
import comp6231.project.saman.campus.message_protocol.saman_replica.JsonMessage;
import comp6231.project.saman.campus.message_protocol.saman_replica.ReplicaRequestMessageHeader;


/**
 * @author saman
 *
 */
public class UdpServer extends Thread {
	private Campus campus;
	private DatagramSocket socket;	//https://stackoverflow.com/questions/6265731/do-java-sockets-support-full-duplex
	private final Object write_socket_lock = new Object();
	public final static int datagram_send_size = 256;
	JsonMessage json_message;
	Gson gson;
	
	public UdpServer(Campus campus, Gson gson) throws SocketException, RemoteException
	{
		this.gson = gson;
		this.campus = campus;
		socket = new DatagramSocket(this.campus.getPort());		
	}
	
	public void setJsonMessage(JsonMessage json_message)
	{
		this.json_message = json_message;
	}
		
	private void processRequest(byte[] message, InetAddress address, int port)
	{
		String json_msg_str = new String(message);
		System.out.println(json_msg_str);
		MessageHeader json_msg = gson.fromJson(json_msg_str, MessageHeader.class);
		if (json_msg.protocol_type == ProtocolType.Server_To_Server)
		{
			if (json_msg.message_type == MessageType.Request)
			{
				ReplicaRequestMessageHeader tmp = (ReplicaRequestMessageHeader)json_msg;
				ReplyMessageHeader reply = tmp.handleRequest(campus);
				String reply_msg = gson.toJson(reply);
				try {
					sendDatagram(reply_msg.getBytes(), address, port);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (json_msg.message_type == MessageType.Reply)
				json_message.onReceivedReplyMessage((ReplyMessageHeader)json_msg);
		}
		else if (json_msg.protocol_type == ProtocolType.Frontend_To_Replica)
		{
			//TODO
		}
	}
	
	public void sendDatagram(byte[] message, InetAddress address, int port) throws IOException
	{
		//System.out.println("Send message to " + address + ":" + port);
		DatagramPacket packet = new DatagramPacket(message, message.length, address, port);
		synchronized (write_socket_lock) {
			socket.send(packet);
		}		
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			byte[] buffer = new byte[datagram_send_size];
			DatagramPacket packet = new DatagramPacket(buffer, datagram_send_size);
			try {
				socket.receive(packet);
				Thread thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						UdpServer.this.processRequest(Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getLength()), packet.getAddress(), packet.getPort());
					}
				});
				thread.start();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
