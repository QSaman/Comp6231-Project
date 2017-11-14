package comp6231.project.messageProtocol.sharedMessage;

import comp6231.project.messageProtocol.MessageHeader;

public class ServerToServerMessage extends MessageHeader{
	public String userId;
	public String legacy;
	
	public ServerToServerMessage(String legacy, String userId) {
		super(-1, CommandType.Quantity, MessageType.Quantity, ProtocolType.Server_To_Server);
		this.userId = userId;
		this.legacy = legacy;
	}
}
