/**
 * 
 */
package comp6231.project.saman.campus.message_protocol.saman_replica;

import comp6231.project.messageProtocol.MessageHeader;

/**
 * @author saman
 *
 */
public  class RequestMessageHeader extends MessageHeader {
	public String user_id;
	
	public RequestMessageHeader(int message_id, CommandType command_type, ProtocolType protocol_type, String user_id)
	{
		super(message_id, command_type, MessageType.Request, protocol_type);
		this.user_id = user_id;
	}
		

	/**
	 * 
	 */
	public RequestMessageHeader() {
		// TODO Auto-generated constructor stub
	}

}
