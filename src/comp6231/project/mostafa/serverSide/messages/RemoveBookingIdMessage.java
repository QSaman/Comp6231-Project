package comp6231.project.mostafa.serverSide.messages;

import comp6231.project.messageProtocol.IRequest;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.mostafa.core.Constants;
import comp6231.project.mostafa.serverSide.Database;

public class RemoveBookingIdMessage extends MessageHeader implements IRequest{

	public String userId;
	public String bookingId;
	
	public RemoveBookingIdMessage(int sequence_number, CommandType command_type, MessageType message_type, ProtocolType protocol_type, String userId, String bookingId){
		super(sequence_number, command_type, message_type, protocol_type);
		this.userId = userId;
		this.bookingId = bookingId;
	}
	
	@Override
	public String handleRequest() {
		boolean removeResult = Database.getInstance().removeBookingId(bookingId);
		if (removeResult){
			return "bookingId: "+bookingId+" removed";
		}else{
			return Constants.RESULT_UDP_FAILD;
		}
		
	}

}
