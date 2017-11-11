/**
 * 
 */
package comp6231.project.saman.campus.message_protocol.saman_replica;

import comp6231.project.saman.campus.Campus;
import comp6231.project.saman.campus.message_protocol.ReplyMessageHeader;
import comp6231.project.saman.common.DateReservation;

/**
 * @author saman
 *
 */
public class ReplicaRequestAvailableTimeSlots extends ReplicaRequestMessageHeader {
	String date;

	/**
	 * @param message_id
	 * @param command_type
	 * @param user_id
	 */
	public ReplicaRequestAvailableTimeSlots(int message_id, String user_id, String date) {
		super(message_id, CommandType.Get_Available_TimeSlots, user_id);
		this.date = date;
	}

	/**
	 * 
	 */
	public ReplicaRequestAvailableTimeSlots() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see comp6231.project.saman.campus.message_protocol.saman_replica.ReplicaRequestMessageHeader#handleRequest(comp6231.project.saman.campus.Campus)
	 */
	@Override
	public ReplyMessageHeader handleRequest(Campus campus) {
		ReplicaReplyAvailableTimeSlots ret = new ReplicaReplyAvailableTimeSlots(sequence_number, protocol_type, "", 0);
		ret.available_timeslot_number = campus.getThisCampusAvailableTimeSlots(new DateReservation(date));
		ret.reply_message = "Available timeslots in " + campus.getCampusName() + " is " + ret.available_timeslot_number;
		return ret;
	}
}
