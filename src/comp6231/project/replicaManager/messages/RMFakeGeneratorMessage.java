package comp6231.project.replicaManager.messages;

import comp6231.project.messageProtocol.MessageHeader;

public class RMFakeGeneratorMessage extends MessageHeader{
	public boolean turnOn;
	
	public RMFakeGeneratorMessage(int sequenceNumber , boolean turnOn) {
		super(sequenceNumber, CommandType.Fake_Generator, MessageType.Request, ProtocolType.ReplicaManager_Message);
		this.turnOn = turnOn;
	}
}
