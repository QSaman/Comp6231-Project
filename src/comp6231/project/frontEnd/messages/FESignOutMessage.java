package comp6231.project.frontEnd.messages;

import comp6231.project.messageProtocol.MessageHeader;

public class FESignOutMessage extends MessageHeader {
	public String userId;

	public FESignOutMessage(int sequence_number, String userId) {
		super(sequence_number, CommandType.SignOut, MessageType.Request, ProtocolType.Frontend_To_Replica);
		this.userId = userId;
	}
}
