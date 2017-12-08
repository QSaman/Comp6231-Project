package comp6231.project.replicaManager.messages;

import comp6231.project.messageProtocol.MessageHeader;

public class RMFakeGeneratorMessage extends MessageHeader{
	public boolean turnOff;
	
	public RMFakeGeneratorMessage(int sequenceNumber , boolean turnOff) {
		super(sequenceNumber, CommandType.Fake_Generator, MessageType.Request, ProtocolType.ReplicaManager_Message);
		this.turnOff = turnOff;
	}
}
