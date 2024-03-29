/**
 * 
 */
package comp6231.project.saman.campus.message_protocol.saman_replica;

import comp6231.project.saman.campus.Campus;

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
	public ReplicaRequestMessageHeader(int message_id, CommandType command_type, String user_id) {
		super(message_id, command_type, ProtocolType.Server_To_Server, user_id);
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
