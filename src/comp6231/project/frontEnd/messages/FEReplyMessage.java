package comp6231.project.frontEnd.messages;

import comp6231.project.messageProtocol.MessageHeader;
import comp6231.shared.Constants;

public class FEReplyMessage extends MessageHeader{
	public String replyMessage;
	// this is for fault generator
	public boolean status;
	public String bookingId;
	public String replicaId;
	
	public FEReplyMessage(int sequenceNumber, CommandType commandType, String replyMessage, boolean status) {
		super(sequenceNumber, commandType, MessageType.Reply, ProtocolType.Frontend_To_Replica);
		this.replyMessage = replyMessage;
		this.status = status;
		this.bookingId = Constants.NULL_STRING;
		this.replicaId = Constants.NULL_STRING;
	}
	
	public FEReplyMessage(int sequenceNumber, CommandType commandType, String replyMessage, boolean status, String bookingId , String campusId) {
		super(sequenceNumber, commandType, MessageType.Reply, ProtocolType.Frontend_To_Replica);
		this.replyMessage = replyMessage;
		this.status = status;
		this.bookingId = bookingId; 
		this.replicaId = campusId;
	}
}
