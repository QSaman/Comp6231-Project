/**
 * 
 */
package comp6231.project.saman.campus.message_protocol.saman_replica;

import comp6231.project.saman.campus.Campus;
import comp6231.project.saman.campus.message_protocol.ReplyMessageHeader;
import comp6231.project.saman.campus.message_protocol.RequestMessageHeader;

/**
 * @author saman
 *
 */
public abstract class  ReplicaRequestMessageHeader extends RequestMessageHeader {

	/**
	 * @param message_id
	 * @param command_type
	 * @param protocol_type
	 * @param user_id
	 */
	public ReplicaRequestMessageHeader(int message_id, CommandType command_type, ProtocolType protocol_type,
			String user_id) {
		super(message_id, command_type, protocol_type, user_id);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public ReplicaRequestMessageHeader() {
		// TODO Auto-generated constructor stub
	}

	
	public abstract ReplyMessageHeader handleRequest(Campus campus);

}
