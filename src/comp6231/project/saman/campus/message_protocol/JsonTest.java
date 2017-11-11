package comp6231.project.saman.campus.message_protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import comp6231.project.saman.campus.message_protocol.MessageHeader.CommandType;
import comp6231.project.saman.campus.message_protocol.MessageHeader.MessageType;
import comp6231.project.saman.campus.message_protocol.MessageHeader.ProtocolType;
import comp6231.project.saman.campus.message_protocol.saman_replica.ReplicaReplyBookRoom;
import comp6231.project.saman.campus.message_protocol.saman_replica.ReplicaRequestBookRoom;
import comp6231.project.saman.common.DateReservation;
import comp6231.project.saman.common.TimeSlot;

public class JsonTest {

	public JsonTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		MessageHeaderDeserializer ds = new MessageHeaderDeserializer();
		
		//You need to use addClass for all your custom classes derived from MessageHeader named for example Foo to map them like:
		//(command_type, message_type, protocol_type) ---> Foo
		//In this example I only define ReplicaEncodeBookRoom:
		ds.addClass(CommandType.Book_Room, MessageType.Request, ProtocolType.InterReplica, ReplicaRequestBookRoom.class);
		ds.addClass(CommandType.Book_Room, MessageType.Reply, ProtocolType.InterReplica, ReplicaReplyBookRoom.class);
		
		Gson gson = new GsonBuilder().registerTypeAdapter(MessageHeader.class, ds).create();
		
		
		MessageHeader msg_obj = new ReplicaRequestBookRoom(1, "DVLS1111", 777, new DateReservation("17-09-2017"), new TimeSlot("09:15 - 10:15"));
		
		String json_str = gson.toJson(msg_obj);
		System.out.println(json_str);
		
		MessageHeader msg_obj2 = gson.fromJson(json_str, MessageHeader.class);
		String json_str2 = gson.toJson(msg_obj2);
		System.out.println(json_str2.equals(json_str));				

	}

}
