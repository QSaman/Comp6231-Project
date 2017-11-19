package comp6231.project.frontEnd.messages;

import comp6231.project.messageProtocol.MessageHeader;

public class FELoginStudentMessage extends MessageHeader {
	public String userId;
	
	public FELoginStudentMessage(int sequence_number, String userId) {
		super(sequence_number, CommandType.LoginStudent, MessageType.Request, ProtocolType.Frontend_To_Replica);
		this.userId = userId;
	}

}
