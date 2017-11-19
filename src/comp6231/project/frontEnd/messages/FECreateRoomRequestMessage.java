package comp6231.project.frontEnd.messages;

import comp6231.project.messageProtocol.MessageHeader;

public class FECreateRoomRequestMessage extends MessageHeader{
	public String userId;
	public int roomNumber;
	public String date;
	public String [] timeSlots;
	
	public FECreateRoomRequestMessage(int sequenceNumber,String userId, int roomNumber, String date, String[] timeSlots) {
		super(sequenceNumber, CommandType.Create_Room, MessageType.Request, ProtocolType.Frontend_To_Replica);
		this.userId = userId;
		this.roomNumber = roomNumber;
		this.date = date;
		this.timeSlots = timeSlots;
	}
}
