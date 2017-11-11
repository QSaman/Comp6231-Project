package comp6231.project.saman.campus.message_protocol.saman_replica;



public class ReplicaReplyBookRoom extends ReplicaReplyMessageStatus {
	
	public String booking_id;
	
	public ReplicaReplyBookRoom(int sequence_number, String reply_message, ProtocolType protocol_type, String booking_id, boolean status)
	{
		super(sequence_number, CommandType.Book_Room, protocol_type, reply_message, status);
		this.booking_id = booking_id;
	}

	public ReplicaReplyBookRoom() {
		// TODO Auto-generated constructor stub
	}

}
