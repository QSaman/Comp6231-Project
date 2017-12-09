/**
 * 
 */
package comp6231.project.saman.campus.message_protocol.saman_replica;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import comp6231.project.saman.campus.UdpServer;
import comp6231.shared.Constants;
import net.rudp.ReliableSocket;
import comp6231.project.messageProtocol.InitializeSerializer;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.messageProtocol.MessageHeaderDeserializer;

/**
 * @author saman
 *
 */
public class JsonMessage{

	HashMap<Integer, ReplyMessageHeader> replied_msg_list;
	HashMap<Integer, Object> wait_object_list;
	public Gson gson;
	UdpServer udp_server;
	
	public ReplyMessageHeader sendJson(ReplicaRequestMessageHeader msg, InetAddress address, int port) throws InterruptedException, IOException
	{
//		Object obj = new Object();
//		synchronized (wait_object_list) {
//			wait_object_list.put(msg.sequence_number, obj);
//		}
		String json_msg = gson.toJson(msg);
		//udp_server.sendDatagramToFE(json_msg, address.getHostAddress(), port);
		ReliableSocket aSocket = null;
		try {
			System.out.println("debug2: " + address);
			OutputStreamWriter out = null;
			aSocket = new ReliableSocket();
			aSocket.connect(new InetSocketAddress(address, port));
			out = new OutputStreamWriter(aSocket.getOutputStream());
			out.write(json_msg);
			out.write('\u0004');
			out.flush();
			out.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(aSocket.getInputStream()));
			
			CharArrayWriter writer = new CharArrayWriter(Constants.BUFFER_SIZE);
			
			while(true) {
				  int n = in.read();
				  if( n < 0  || n == '\u0004') break;
				  writer.write(n);
				}
			
			  String json_msg_str = writer.toString();
			  System.out.println(json_msg_str);
			  
			  MessageHeader reply = gson.fromJson(json_msg_str, MessageHeader.class);
				return (ReplyMessageHeader) reply;
		} catch (IOException e) {
			throw e;
		}
		finally
		{
			if (aSocket != null)
				aSocket.close();
		}
//		synchronized (obj) {
//			obj.wait();
//		}		
//		ReplyMessageHeader ret = null;
//		synchronized (wait_object_list) {
//			 ret = replied_msg_list.get(msg.sequence_number);
//			replied_msg_list.remove(msg.sequence_number);
//		}
//		return ret;
	}
	
	public void onReceivedReplyMessage(ReplyMessageHeader msg)
	{
		synchronized (replied_msg_list) {
			replied_msg_list.put(msg.sequence_number, msg);
		}
		synchronized (wait_object_list) {
			Object obj = wait_object_list.get(msg.sequence_number);
			synchronized (obj) {
				obj.notify();
			}			
			wait_object_list.remove(msg.sequence_number);
		}
	}

	/**
	 * 
	 */
	public JsonMessage(UdpServer udp_server, Gson gson) {
		replied_msg_list = new HashMap<>();
		wait_object_list = new HashMap<>();
		this.udp_server = udp_server;
		this.gson = gson;
	}

}
