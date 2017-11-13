package comp6231.project.mostafa.serverSide.messages;

import comp6231.project.messageProtocol.IRequest;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.mostafa.serverSide.Database;

public class GetAvailableTimeSlotsMessage extends MessageHeader implements IRequest {
	public String date;
	public String userId;
	
	public GetAvailableTimeSlotsMessage(int sequence_number, CommandType command_type, MessageType message_type, ProtocolType protocol_type, String userId ,String date){
		super(sequence_number, command_type, message_type, protocol_type);
		this.date = date;
		this.userId = userId;
	}
	
	@Override
	public String handleRequest() {
		return (Database.getInstance().findAvailableTimeSlot(date)+"");
	}

}
