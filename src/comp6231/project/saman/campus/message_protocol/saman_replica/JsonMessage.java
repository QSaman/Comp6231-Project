/**
 * 
 */
package comp6231.project.saman.campus.message_protocol.saman_replica;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import comp6231.project.saman.campus.UdpServer;
import comp6231.project.saman.campus.message_protocol.InitializeSerializer;
import comp6231.project.saman.campus.message_protocol.MessageHeader;
import comp6231.project.saman.campus.message_protocol.MessageHeaderDeserializer;
import comp6231.project.saman.campus.message_protocol.ReplyMessageHeader;

/**
 * @author saman
 *
 */
public class JsonMessage {
	HashMap<Integer, ReplyMessageHeader> replied_msg_list;
	HashMap<Integer, Object> wait_object_list;
	public Gson gson;
	UdpServer udp_server;
	
	public ReplyMessageHeader sendJson(ReplicaRequestMessageHeader msg, InetAddress address, int port) throws InterruptedException, IOException
	{
		Object obj = new Object();
		synchronized (wait_object_list) {
			wait_object_list.put(msg.sequence_number, obj);
		}
		String json_msg = gson.toJson(msg);
		udp_server.sendDatagram(json_msg.getBytes(), address, port);
		synchronized (obj) {
			obj.wait();
		}		
		ReplyMessageHeader ret = null;
		synchronized (wait_object_list) {
			 ret = replied_msg_list.get(msg.sequence_number);
			replied_msg_list.remove(msg.sequence_number);
		}
		return ret;
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
