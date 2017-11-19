package comp6231.project.frontEnd.messages;

import comp6231.project.messageProtocol.MessageHeader;

public class FELoginAdminMessage extends MessageHeader {
public String userId;
	
	public FELoginAdminMessage(int sequence_number, String userId) {
		super(sequence_number, CommandType.LoginAdmin, MessageType.Request, ProtocolType.Frontend_To_Replica);
		this.userId = userId;
	}
}
