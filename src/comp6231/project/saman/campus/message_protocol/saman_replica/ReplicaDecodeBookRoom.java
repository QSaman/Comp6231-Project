package comp6231.project.saman.campus.message_protocol.saman_replica;

import comp6231.project.saman.campus.message_protocol.ReplyMessageHeader;


public class ReplicaDecodeBookRoom extends ReplyMessageHeader {
	
	public String booking_id;
	
	public ReplicaDecodeBookRoom(int message_id, String reply_message, String booking_id)
	{
		super(message_id, CommandType.Book_Room, ProtocolType.InterReplica, reply_message);
		this.booking_id = booking_id;
	}

	public ReplicaDecodeBookRoom() {
		// TODO Auto-generated constructor stub
	}

}
