package comp6231.project.replicaManager.messages;

import comp6231.project.messageProtocol.MessageHeader;

public class RMFakeGeneratorMessage extends MessageHeader{
	public boolean turnOff;
	public int port;
	
	public RMFakeGeneratorMessage(int sequenceNumber , boolean turnOff, int port) {
		super(sequenceNumber, CommandType.Fake_Generator, MessageType.Request, ProtocolType.ReplicaManager_Message);
		this.turnOff = turnOff;
		this.port = port;
	}
}
