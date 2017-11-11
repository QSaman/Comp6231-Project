package comp6231.project.saman.campus.message_protocol.saman_replica;

import comp6231.project.saman.campus.message_protocol.ReplyMessageHeader;


public class ReplicaReplyBookRoom extends ReplyMessageHeader {
	
	public String booking_id;
	
	public ReplicaReplyBookRoom(int message_id, String reply_message, String booking_id)
	{
		super(message_id, CommandType.Book_Room, ProtocolType.Server_To_Server, reply_message);
		this.booking_id = booking_id;
	}

	public ReplicaReplyBookRoom() {
		// TODO Auto-generated constructor stub
	}

}
