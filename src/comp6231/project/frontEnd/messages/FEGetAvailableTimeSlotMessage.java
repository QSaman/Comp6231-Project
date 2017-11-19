package comp6231.project.frontEnd.messages;

import comp6231.project.messageProtocol.MessageHeader;

public class FEGetAvailableTimeSlotMessage extends MessageHeader{
	public String user_id;
	public String date;

	
	public FEGetAvailableTimeSlotMessage(int sequenceNumber, String user_id, String date) {
		super(sequenceNumber, CommandType.Get_Available_TimeSlots, MessageType.Request, ProtocolType.Frontend_To_Replica);
		this.user_id = user_id;
		this.date = date;
	}
}
