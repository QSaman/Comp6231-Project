package comp6231.project.frontEnd.messages;

import comp6231.project.messageProtocol.MessageHeader;
import comp6231.shared.Constants;

public class FEReplyMessage extends MessageHeader{
	public String replyMessage;
	// this is for fault generator
	public boolean isFakeGeneratorOff;
	public String bookingId;
	public String replicaId;
	
	public FEReplyMessage(int sequenceNumber, CommandType commandType, String replyMessage, boolean isFakeGeneratorOff, String replicaId) {
		super(sequenceNumber, commandType, MessageType.Reply, ProtocolType.Frontend_To_Replica);
		this.replyMessage = replyMessage;
		this.isFakeGeneratorOff = isFakeGeneratorOff;
		this.bookingId = Constants.NULL_STRING;
		this.replicaId = replicaId;
	}
	
	public FEReplyMessage(int sequenceNumber, CommandType commandType, String replyMessage, boolean status, String bookingId , String replicaId) {
		super(sequenceNumber, commandType, MessageType.Reply, ProtocolType.Frontend_To_Replica);
		this.replyMessage = replyMessage;
		this.isFakeGeneratorOff = status;
		this.bookingId = bookingId; 
		this.replicaId = replicaId;
	}
}
