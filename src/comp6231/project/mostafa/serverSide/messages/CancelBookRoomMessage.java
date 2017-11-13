package comp6231.project.mostafa.serverSide.messages;

import comp6231.project.messageProtocol.IRequest;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.mostafa.serverSide.Database;

public class CancelBookRoomMessage extends MessageHeader implements IRequest{
	public String bookingId;
	public String userId;
	
	public CancelBookRoomMessage(int sequenceNumber, CommandType commandType, MessageType messageType, ProtocolType protocolType, String userId, String bookingId) {
		super(sequenceNumber,commandType, messageType, protocolType);
		this.bookingId = bookingId;
		this.userId = userId;
	}
	
	@Override
	public String handleRequest() {
		return Database.getInstance().cancelBookingId(bookingId, userId);
	}
}
