/**
 * 
 */
package comp6231.project.saman.campus.message_protocol.saman_replica;

import java.io.IOException;

import comp6231.project.saman.campus.Campus;
import comp6231.project.saman.campus.message_protocol.ReplyMessageHeader;

/**
 * @author saman
 *
 */
public class ReplicaRequestStartWeek extends ReplicaRequestMessageHeader {

	/**
	 * @param message_id
	 * @param command_type
	 * @param user_id
	 */
	public ReplicaRequestStartWeek(int message_id, String user_id) {
		super(message_id, CommandType.Start_Week, user_id);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public ReplicaRequestStartWeek() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see comp6231.project.saman.campus.message_protocol.saman_replica.ReplicaRequestMessageHeader#handleRequest(comp6231.project.saman.campus.Campus)
	 */
	@Override
	public ReplyMessageHeader handleRequest(Campus campus) {
		ReplicaReplyMessageStatus ret = new ReplicaReplyMessageStatus(sequence_number, command_type, protocol_type, "", true);
		campus.startWeek();
		ret.reply_message = "databases reset successfully";
		return ret;
	}

}
