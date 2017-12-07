package comp6231.project.frontEnd.udp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import comp6231.project.frontEnd.FE;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.replicaManager.messages.RMFakeGeneratorMessage;
import comp6231.shared.Constants;
import net.rudp.ReliableSocket;

public class ErrorHandler implements Runnable{
	public enum messageStatus{
		E_Kill, E_Fake_Generator
	}
	
	private String replicaId;
	private String group;
	
	private int portTosend;
	private MessageHeader messageHeader;
	private messageStatus status;
	private boolean turnOff;
	
	public ErrorHandler(String replicaId, String group, boolean turnOff) {
		this.replicaId = replicaId;
		this.group = group;
		this.turnOff = turnOff;
		status = messageStatus.E_Fake_Generator;
	}
	
	public ErrorHandler(int portTosend, MessageHeader messageHeader) {
		this.portTosend = portTosend;
		this.messageHeader = messageHeader;
		status = messageStatus.E_Kill;
	}

	@Override
	public void run() {
		if(status == messageStatus.E_Fake_Generator) {
			RMFakeGeneratorMessage message = new RMFakeGeneratorMessage(-1, turnOff, findServerPort(replicaId, group));
			String json = FE.toJson(message);
			send(json, findRMPort(replicaId));
		}else if(status == messageStatus.E_Kill) {
			String json = FE.toJson(messageHeader);
			send(json, portTosend);
		}	
	}
	
	private void send(String json, int port) {
		try {
			ReliableSocket socket = new ReliableSocket();
			socket.connect(new InetSocketAddress("127.0.0.1", port));
			OutputStream out = socket.getOutputStream();
			out.write(json.getBytes());
			out.flush();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int findServerPort(String replicaId,String group) {
		if(group.equals(Constants.DVL_GROUP)) {
			if(replicaId.equals("Farid")) {
				return Constants.dvlPortListenFaridActive;
			}else if(replicaId.equals("Mostafa")){
				return Constants.dvlPortListenRe1Active;
			}else if(replicaId.equals("Saman")){
				return Constants.dvlPortListenRe2Active;
			}
		}else if (group.equals(Constants.KKL_GROUP)) {
			if(replicaId.equals("Farid")) {
				return Constants.kklPortListenFaridActive;
			}else if(replicaId.equals("Mostafa")){
				return Constants.kklPortListenRe1Active;
			}else if(replicaId.equals("Saman")){
				return Constants.kklPortListenRe2Active;
			}
		}else if (group.equals(Constants.WST_GROUP)) {
			if(replicaId.equals("Farid")) {
				return Constants.wstPortListenFaridActive;
			}else if(replicaId.equals("Mostafa")){
				return Constants.wstPortListenRe1Active;
			}else if(replicaId.equals("Saman")){
				return Constants.wstPortListenRe2Active;
			}
		}else {
			FE.log(" Wrong value for group in error handleing");
			return -1;
		}
		return -1;
	}
	
	private int findRMPort(String replicaId) {
		if(replicaId.equals("Farid")) {
			return Constants.RM_PORT_LISTEN_Farid;
		}else if(replicaId.equals("Mostafa")) {
			return Constants.RM_PORT_LISTEN_RE1;
		}else if(replicaId.contains("Saman")) {
			return Constants.RM_PORT_LISTEN_RE2;
		}else {
			return -1;
		}
	}
}