/**
 * 
 */
package comp6231.project.saman.campus.message_protocol.saman_replica;


/**
 * @author saman
 *
 */
public class ReplicaReplyAvailableTimeSlots extends ReplyMessageHeader {
	
	public int available_timeslot_number;

	/**
	 * @param sequence_number
	 * @param command_type
	 * @param protocol_type
	 * @param reply_message
	 */
	public ReplicaReplyAvailableTimeSlots(int sequence_number, ProtocolType protocol_type,
			String reply_message, int available_timeslot_number) {
		super(sequence_number, CommandType.Get_Available_TimeSlots, protocol_type, reply_message);
		this.available_timeslot_number = available_timeslot_number;
	}

	/**
	 * 
	 */
	public ReplicaReplyAvailableTimeSlots() {
		// TODO Auto-generated constructor stub
	}

}
