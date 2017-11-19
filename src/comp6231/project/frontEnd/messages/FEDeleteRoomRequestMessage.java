package comp6231.project.frontEnd.messages;

import comp6231.project.messageProtocol.MessageHeader;

public class FEDeleteRoomRequestMessage extends MessageHeader{
	public String userId;
	public int roomNumber;
	public String date;
	public String [] timeSlots;
	
	public FEDeleteRoomRequestMessage(int sequenceNumber,String userId, int roomNumber, String date, String[] timeSlots) {
		super(sequenceNumber, CommandType.Delete_Room, MessageType.Request, ProtocolType.Frontend_To_Replica);
		this.userId = userId;
		this.roomNumber = roomNumber;
		this.date = date;
		this.timeSlots = timeSlots;
	}

}
