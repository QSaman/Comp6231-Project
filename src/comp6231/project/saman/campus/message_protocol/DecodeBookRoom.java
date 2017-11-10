package comp6231.project.saman.campus.message_protocol;

public class DecodeBookRoom extends DecodeMessageBase {
	
	public String booking_id;
	
	public DecodeBookRoom(int message_id, String reply_message, String booking_id)
	{
		super(message_id, reply_message);
		this.booking_id = booking_id;
	}

	public DecodeBookRoom() {
		// TODO Auto-generated constructor stub
	}

}
