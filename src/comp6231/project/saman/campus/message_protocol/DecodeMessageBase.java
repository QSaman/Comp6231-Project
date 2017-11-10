package comp6231.project.saman.campus.message_protocol;

public class DecodeMessageBase {
	
	public int message_id;
	public String reply_message;
	
	public DecodeMessageBase(int message_id, String reply_message)
	{
		this.message_id = message_id;
		this.reply_message = reply_message;
	}

	public DecodeMessageBase() {
		// TODO Auto-generated constructor stub
	}

}
