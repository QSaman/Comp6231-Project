package comp6231.project.saman.campus.message_protocol.saman_replica;

import java.io.IOException;
import java.rmi.NotBoundException;

import comp6231.project.saman.campus.Campus;
import comp6231.project.saman.campus.message_protocol.ReplyMessageHeader;
import comp6231.project.saman.common.DateReservation;
import comp6231.project.saman.common.TimeSlot;

public class ReplicaRequestBookRoom extends ReplicaRequestMessageHeader {
	public int room_number;
	public String date;
	public String time_slot;
	
	public ReplicaRequestBookRoom(int message_id, String user_id, int room_number, DateReservation date, TimeSlot time_slot)
	{
		super(message_id, CommandType.Book_Room, user_id);
		this.room_number = room_number;
		this.date = date.toString();
		this.time_slot = time_slot.toString();
	}	

	public ReplicaRequestBookRoom() {
	}

	@Override
	public ReplyMessageHeader handleRequest(Campus campus) {
		ReplicaReplyBookRoom ret = new ReplicaReplyBookRoom(sequence_number, "", protocol_type, "", false);
		try {
			String res = campus.bookRoom(user_id, campus.getName(), room_number, new DateReservation(date), new TimeSlot(time_slot));
			if (res.isEmpty())
			{
				ret.status = false;
				ret.reply_message = "Failed to book";
			}
			else
			{
				ret.status = true;
				ret.booking_id = res;
				ret.reply_message = "Generated booking id: " + ret.booking_id;
			}			
		} catch (NotBoundException | IOException | InterruptedException e) {
			ret.reply_message = e.getMessage();
		}
		return ret;
	}

}
