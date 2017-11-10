package comp6231.project.saman.campus.message_protocol;

import java.io.IOException;
import java.rmi.NotBoundException;

import comp6231.project.saman.campus.Campus;
import comp6231.project.saman.common.DateReservation;
import comp6231.project.saman.common.TimeSlot;

public class EncodeBookRoom extends EncodeMessageBase {
	public int room_number;
	public DateReservation date;
	public TimeSlot time_slot;
	
	public EncodeBookRoom(int message_id, String user_id, int room_number, DateReservation date, TimeSlot time_slot)
	{
		super(message_id, user_id);
		this.room_number = room_number;
		this.date = date;
		this.time_slot = time_slot;
	}	

	public EncodeBookRoom() {
	}

	@Override
	public DecodeMessageBase handleRequest(Campus campus) {
		DecodeBookRoom ret = new DecodeBookRoom(room_number, "", "");
		try {
			ret.booking_id = campus.bookRoom(user_id, campus.getName(), room_number, date, time_slot);
			ret.reply_message = "Generated booking id: " + ret.booking_id;
		} catch (NotBoundException | IOException | InterruptedException e) {
			ret.reply_message = e.getMessage();
		}
		return ret;
	}

}
