package comp6231.project.frontEnd.messages;

import comp6231.project.messageProtocol.MessageHeader;

public class FECancelBookingMessage extends MessageHeader{
	public String user_id;
	public String booking_id;

	
	public FECancelBookingMessage(int sequenceNumber,String user_id, String booking_id) {
		super(sequenceNumber, CommandType.Cancel_Book_Room, MessageType.Request, ProtocolType.Frontend_To_Replica);
		this.user_id = user_id;
		this.booking_id = booking_id;
	}
}
