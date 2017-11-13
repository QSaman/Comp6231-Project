package comp6231.project.mostafa.serverSide.messages;

import comp6231.project.messageProtocol.IRequest;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.mostafa.serverSide.Database;

public class RollBackMessage extends MessageHeader implements IRequest{
	public String userId;
	
	public RollBackMessage(int sequence_number, CommandType command_type, MessageType message_type, ProtocolType protocol_type, String userId) {
		super(sequence_number, command_type, message_type, protocol_type);
		this.userId = userId;
	}
	
	@Override
	public String handleRequest() {
		Database.getInstance().rollBack();
		return "UDP-Database RolledBacked";
	}
}
