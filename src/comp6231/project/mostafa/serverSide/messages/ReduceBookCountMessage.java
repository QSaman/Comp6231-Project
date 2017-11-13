package comp6231.project.mostafa.serverSide.messages;

import comp6231.project.messageProtocol.IRequest;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.mostafa.serverSide.Database;

public class ReduceBookCountMessage extends MessageHeader implements IRequest{
	public String userId;
	public String bookedStundetId;
	
	public ReduceBookCountMessage(int sequence_number, CommandType command_type, MessageType message_type, ProtocolType protocol_type, String userId, String bookedStundetId){
		super(sequence_number, command_type, message_type, protocol_type);
		this.userId = userId;
		this.bookedStundetId = bookedStundetId;
	}
	
	@Override
	public String handleRequest() {
		return Database.getInstance().reduceBookingCount(bookedStundetId);
	}

}
