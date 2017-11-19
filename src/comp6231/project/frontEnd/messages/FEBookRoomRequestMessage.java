package comp6231.project.frontEnd.messages;

import comp6231.project.messageProtocol.MessageHeader;

public class FEBookRoomRequestMessage extends MessageHeader{
	public String userId;
	public String campusName;
	public int roomNumber;
	public String date;
	public String timeSlot;
	
	public FEBookRoomRequestMessage(int sequenceNumber,String userId, String campusName, int roomNumber,
			String date, String timeSlot) {
		super(sequenceNumber, CommandType.Book_Room, MessageType.Request, ProtocolType.Frontend_To_Replica);
		this.userId = userId;
		this.campusName = campusName;
		this.roomNumber = roomNumber;
		this.date = date;
		this.timeSlot = timeSlot;
	}
}
