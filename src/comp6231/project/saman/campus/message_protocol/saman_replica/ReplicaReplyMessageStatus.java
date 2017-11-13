/**
 * 
 */
package comp6231.project.saman.campus.message_protocol.saman_replica;


/**
 * @author saman
 *
 */
public class ReplicaReplyMessageStatus extends ReplyMessageHeader {
	
	public boolean status;

	/**
	 * @param sequence_number
	 * @param command_type
	 * @param protocol_type
	 * @param reply_message
	 */
	public ReplicaReplyMessageStatus(int sequence_number, CommandType command_type, ProtocolType protocol_type,
			String reply_message, boolean status) {
		super(sequence_number, command_type, protocol_type, reply_message);
		this.status = status;
	}

	/**
	 * 
	 */
	public ReplicaReplyMessageStatus() {
		// TODO Auto-generated constructor stub
	}

}
