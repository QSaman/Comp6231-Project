package comp6231.project.saman.campus.message_protocol;

/**
 * @author saman
 *
 */
public class ReplyMessageHeader extends MessageHeader {
	
	public String reply_message;
	
	public ReplyMessageHeader(int message_id, CommandType command_type, ProtocolType protocol_type, String reply_message)
	{
		super(message_id, command_type, MessageType.Reply, protocol_type);
		this.reply_message = reply_message;
	}

	public ReplyMessageHeader() {
		// TODO Auto-generated constructor stub
	}

}
