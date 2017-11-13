package comp6231.project.saman.campus.message_protocol.saman_replica;

import comp6231.project.messageProtocol.MessageHeader;
/**
 * @author saman
 *
 */
public class ReplyMessageHeader extends MessageHeader {
	
	public String reply_message;
	
	public ReplyMessageHeader(int sequence_number, CommandType command_type, ProtocolType protocol_type, String reply_message)
	{
		super(sequence_number, command_type, MessageType.Reply, protocol_type);
		this.reply_message = reply_message;
	}

	public ReplyMessageHeader() {
		// TODO Auto-generated constructor stub
	}

}
