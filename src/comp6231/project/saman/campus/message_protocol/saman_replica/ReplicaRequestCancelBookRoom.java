/**
 * 
 */
package comp6231.project.saman.campus.message_protocol.saman_replica;

import java.io.IOException;
import java.rmi.NotBoundException;

import comp6231.project.saman.campus.Campus;

/**
 * @author saman
 *
 */
public class ReplicaRequestCancelBookRoom extends ReplicaRequestMessageHeader {
	
	public String booking_id;

	/**
	 * @param message_id
	 * @param command_type
	 * @param user_id
	 */
	public ReplicaRequestCancelBookRoom(int message_id, String user_id, String booking_id) {
		super(message_id, CommandType.Cancel_Book_Room, user_id);
		this.booking_id = booking_id;
	}

	/**
	 * 
	 */
	public ReplicaRequestCancelBookRoom() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see comp6231.project.saman.campus.message_protocol.saman_replica.ReplicaRequestMessageHeader#handleRequest(comp6231.project.saman.campus.Campus)
	 */
	@Override
	public ReplyMessageHeader handleRequest(Campus campus) {
		ReplicaReplyMessageStatus ret = new ReplicaReplyMessageStatus(sequence_number, command_type, protocol_type, "", false);
		try {
			ret.status = campus.cancelBooking(user_id, booking_id);
			if (ret.status)
				ret.reply_message = "booking id " + booking_id + "cancelled successfully!";
			else
				ret.reply_message = "booking id " + booking_id + "cancellation failed!";
		} catch (NotBoundException | IOException | InterruptedException e) {
			ret.reply_message = e.getMessage();
		}
		return ret;
	}
}
