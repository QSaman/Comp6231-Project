package comp6231.project.frontEnd.messages;

import comp6231.project.messageProtocol.MessageHeader;

public class FEReplyMessage extends MessageHeader{
	public String replyMessage;
	// this is for fault generator
	public boolean status;
	
	public FEReplyMessage(int sequenceNumber, CommandType commandType, String replyMessage, boolean status) {
		super(sequenceNumber, commandType, MessageType.Reply, ProtocolType.Frontend_To_Replica);
		this.replyMessage = replyMessage;
		this.status = status;
	}
}
