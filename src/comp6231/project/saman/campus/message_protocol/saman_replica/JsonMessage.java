/**
 * 
 */
package comp6231.project.saman.campus.message_protocol.saman_replica;

import java.util.HashMap;

import comp6231.project.saman.campus.message_protocol.ReplyMessageHeader;

/**
 * @author saman
 *
 */
public class JsonMessage {
	HashMap<Integer, ReplyMessageHeader> replied_msg_list;
	HashMap<Integer, Object> wait_object_list;
	
	public ReplyMessageHeader sendJson(ReplicaRequestMessageHeader msg) throws InterruptedException
	{
		Object obj = new Object();
		synchronized (wait_object_list) {
			wait_object_list.put(msg.sequence_number, obj);
		}		
		obj.wait();
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
			obj.notify();
			wait_object_list.remove(msg.sequence_number);
		}
	}

	/**
	 * 
	 */
	public JsonMessage() {
		replied_msg_list = new HashMap<>();
		wait_object_list = new HashMap<>();
	}

}
