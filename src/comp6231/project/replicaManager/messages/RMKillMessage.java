package comp6231.project.replicaManager.messages;

import comp6231.project.messageProtocol.MessageHeader;

public class RMKillMessage extends MessageHeader {
	public String portSwitcherArg;
	
	public RMKillMessage(int sequenceNumber , String portSwitcherArg) {
		super(sequenceNumber, CommandType.Kill, MessageType.Request, ProtocolType.ReplicaManager_Message);
		this.portSwitcherArg = portSwitcherArg;
	}
}
