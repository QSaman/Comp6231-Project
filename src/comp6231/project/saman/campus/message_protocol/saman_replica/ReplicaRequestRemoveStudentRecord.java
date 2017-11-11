/**
 * 
 */
package comp6231.project.saman.campus.message_protocol.saman_replica;

import comp6231.project.saman.campus.Campus;
import comp6231.project.saman.campus.message_protocol.ReplyMessageHeader;

/**
 * @author saman
 *
 */
public class ReplicaRequestRemoveStudentRecord extends ReplicaRequestMessageHeader {
	public String booking_id;

	/**
	 * @param message_id
	 * @param command_type
	 * @param user_id
	 */
	public ReplicaRequestRemoveStudentRecord(int message_id, CommandType command_type, String user_id, String booking_id) {
		super(message_id, CommandType.Remove_Student_Record, user_id);
		this.booking_id = booking_id;
	}

	/**
	 * 
	 */
	public ReplicaRequestRemoveStudentRecord() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see comp6231.project.saman.campus.message_protocol.saman_replica.ReplicaRequestMessageHeader#handleRequest(comp6231.project.saman.campus.Campus)
	 */
	@Override
	public ReplyMessageHeader handleRequest(Campus campus) {
		ReplicaReplyMessageStatus ret = new ReplicaReplyMessageStatus(sequence_number, command_type, protocol_type, "", false);
		ret.status = campus.removeStudentRecord(user_id, booking_id);
		ret.reply_message = "removing " + user_id + " from student db ";
		ret.reply_message += (ret.status ? "was successfull" : "wasn't successfull");
		return ret;
	}

}
