package comp6231.project.frontEnd.udp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.google.gson.Gson;

import comp6231.project.frontEnd.FEUtility;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.messageProtocol.StartGson;
import comp6231.project.replicaManager.messages.RMFakeGeneratorMessage;
import comp6231.project.replicaManager.messages.RMKillMessage;
import comp6231.shared.Constants;
import net.rudp.ReliableSocket;

public class ErrorHandler implements Runnable{
	public enum messageStatus{
		E_Kill, E_Fake_Generator
	}
	
	private String replicaId;
	
	private int portTosend;
	private MessageHeader messageHeader;
	private messageStatus status;
	private boolean turnOff;
	
	private static Gson gson;
	private static final Object lock = new Object();
	
	public ErrorHandler(String replicaId, boolean turnOff) {
		this.replicaId = replicaId;;
		this.turnOff = turnOff;
		status = messageStatus.E_Fake_Generator;
		gson = StartGson.initReplicaManager();
	}
	
	public ErrorHandler(int portTosend, MessageHeader messageHeader) {
		RMKillMessage m = (RMKillMessage)messageHeader;
		replicaId = FEUtility.getInstance().findRMCode(m.portSwitcherArg);
		this.portTosend = portTosend;
		this.messageHeader = messageHeader;
		status = messageStatus.E_Kill;
		gson = StartGson.initReplicaManager();
	}

	@Override
	public void run() {
		if(status == messageStatus.E_Fake_Generator) {
			RMFakeGeneratorMessage message = new RMFakeGeneratorMessage(-1, turnOff);
			String json = toJson(message);
			send(json, findRMPort(replicaId));
		}else if(status == messageStatus.E_Kill) {
			String json = toJson(messageHeader);
			send(json, portTosend);
		}	
	}
	
	private void send(String json, int port) {
		try {
			ReliableSocket socket = new ReliableSocket();
			socket.connect(new InetSocketAddress(findRMAdrr(replicaId), port));
			OutputStream out = socket.getOutputStream();
			out.write(json.getBytes());
			out.flush();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	private int findServerPort(String replicaId,String group) {
//		if(group.equals(Constants.DVL_GROUP)) {
//			if(replicaId.contains("Farid")) {
//				return Constants.dvlPortListenFaridActive;
//			}else if(replicaId.contains("Mostafa")){
//				return Constants.dvlPortListenRe1Active;
//			}else if(replicaId.contains("Saman")){
//				return Constants.dvlPortListenRe2Active;
//			}
//		}else if (group.equals(Constants.KKL_GROUP)) {
//			if(replicaId.contains("Farid")) {
//				return Constants.kklPortListenFaridActive;
//			}else if(replicaId.contains("Mostafa")){
//				return Constants.kklPortListenRe1Active;
//			}else if(replicaId.contains("Saman")){
//				return Constants.kklPortListenRe2Active;
//			}
//		}else if (group.equals(Constants.WST_GROUP)) {
//			if(replicaId.contains("Farid")) {
//				return Constants.wstPortListenFaridActive;
//			}else if(replicaId.contains("Mostafa")){
//				return Constants.wstPortListenRe1Active;
//			}else if(replicaId.contains("Saman")){
//				return Constants.wstPortListenRe2Active;
//			}
//		}else {
//			FE.log(" Wrong value for group in error handleing");
//			return -1;
//		}
//		return -1;
//	}
	
	private int findRMPort(String replicaId) {
		if(replicaId.contains("Farid")) {
			return Constants.RM_PORT_LISTEN_Farid;
		}else if(replicaId.contains("Mostafa")) {
			return Constants.RM_PORT_LISTEN_RE1;
		}else if(replicaId.contains("Saman")) {
			return Constants.RM_PORT_LISTEN_RE2;
		}else {
			return -1;
		}
	}
	
	private String findRMAdrr(String replicaId) {
		if(replicaId.contains("Farid")) {
			return Constants.FARID_IP;
		}else if(replicaId.contains("Mostafa")) {
			return Constants.MOSTAFA_IP;
		}else if(replicaId.contains("Saman")) {
			return Constants.SAMAN_IP;
		}else {
			return Constants.NULL_STRING;
		}
	}
	
	public static MessageHeader fromJson(String json){
		synchronized (lock) {
			return gson.fromJson(json, MessageHeader.class);
		}
	}
	
	public static String toJson(MessageHeader args){
		synchronized (lock) {
			return gson.toJson(args);
		}
	}
}