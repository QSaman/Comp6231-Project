package comp6231.project.frontEnd.messages;

import comp6231.project.messageProtocol.MessageHeader;

public class FEChangeReservationMessage extends MessageHeader{
	public String user_id;
	public String booking_id;
	public String new_campus_name;
	public int new_room_number;
	public String new_date;
	public String new_time_slot;
	
	public FEChangeReservationMessage(int sequenceNumber,String user_id, String booking_id, String new_campus_name, int new_room_number, String new_date, String new_time_slot) {
		super(sequenceNumber, CommandType.Change_Reservation, MessageType.Request, ProtocolType.Frontend_To_Replica);
		this.user_id = user_id;
		this.new_room_number = new_room_number;
		this.new_date = new_date;
		this.new_time_slot = new_time_slot;
		this.booking_id = booking_id;
		this.new_campus_name = new_campus_name;
	}
}
