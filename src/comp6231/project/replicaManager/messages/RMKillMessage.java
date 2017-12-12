package comp6231.project.replicaManager.messages;

import comp6231.project.messageProtocol.MessageHeader;
import comp6231.shared.Constants;

public class RMKillMessage extends MessageHeader {
	public String portSwitcherArg;
	public String replicaId;
	
	public RMKillMessage(int sequenceNumber , String portSwitcherArg) {
		super(sequenceNumber, CommandType.Kill, MessageType.Request, ProtocolType.ReplicaManager_Message);
		this.portSwitcherArg = portSwitcherArg;
		replicaId = Constants.NULL_STRING;
	}
}
