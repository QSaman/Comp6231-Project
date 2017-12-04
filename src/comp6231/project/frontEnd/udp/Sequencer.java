package comp6231.project.frontEnd.udp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import net.rudp.ReliableSocket;
import comp6231.project.frontEnd.FE;
import comp6231.project.frontEnd.Info;
import comp6231.project.frontEnd.ReturnStatus;
import comp6231.project.frontEnd.messages.FEReplyMessage;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.messageProtocol.MessageHeader.CommandType;
import comp6231.project.messageProtocol.MessageHeader.MessageType;
import comp6231.shared.Constants;

public class Sequencer extends Thread{

	// key : seqNumber
	// value : FEPair
	public static ConcurrentHashMap<Integer, FEPair> holdBack = new ConcurrentHashMap<Integer, FEPair>();
	public FEPair pair;
	
	private static int sequenceNumber = 0;
	private static final Object lock = new Object();

	private MessageHeader args;
	private String group;
	private String result;
	public ReturnStatus returnStatus;
	
	public Sequencer(MessageHeader args, String group){
		this.args = args;
		this.group = group;
		result = "";
	}

	@Override
	public void run() {
		adjustSeqNumber();
		byte [] m = FE.gson.toJson(args).getBytes();			
		sendToReplica(m);
		setTimeOut();
	}

	private void adjustSeqNumber(){
		synchronized (lock) {
			args.sequence_number = sequenceNumber;
			pair = new FEPair(sequenceNumber, group);
			holdBack.put(sequenceNumber, pair);
			sequenceNumber ++;
		}
	}

	public void sendToReplica(byte[] sendBuffer) {

		int[] replicaPorts =  new int[3];


		if(group.equals(Constants.DVL_GROUP)){
			replicaPorts[0] = Constants.dvlPortListenFaridActive;
			replicaPorts[1] = Constants.dvlPortListenRe1Active;
			replicaPorts[2] = Constants.dvlPortListenRe2Active;
		}else if(group.equals(Constants.KKL_GROUP)){
			replicaPorts[0] = Constants.kklPortListenFaridActive;
			replicaPorts[1] = Constants.kklPortListenRe1Active;
			replicaPorts[2] = Constants.kklPortListenRe2Active;
		}else if(group.equals(Constants.WST_GROUP)){
			replicaPorts[0] = Constants.wstPortListenFaridActive;
			replicaPorts[1] = Constants.wstPortListenRe1Active;
			replicaPorts[2] = Constants.wstPortListenRe2Active;
		}else{
			FE.log("group is not valid in sequencer class: " + group);
			return;
		}

		for(int i=0;i< Constants.ACTIVE_SERVERS; ++i) {

			final int idxPort = i;

			new Thread(new Runnable() {
				@Override
				public void run() {

					try {
						ReliableSocket sendToReplica = new ReliableSocket();

						sendToReplica.connect(new InetSocketAddress("127.0.0.1", replicaPorts[idxPort]));
						
						OutputStream out = sendToReplica.getOutputStream();
						out.write(sendBuffer);

						out.flush();
						out.close();
						sendToReplica.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}).start();
		}
	}
	
	public void setTimeOut(){
		try {
			if(pair.semaphore.tryAcquire(2,TimeUnit.MINUTES)){
					for(int i = 0 ; i< Constants.ACTIVE_SERVERS; ++i){
						handlePair(i);
					}
					if(returnStatus != ReturnStatus.ErrorInMessageType && returnStatus != ReturnStatus.FakeGenertor && returnStatus != ReturnStatus.CantLogin){
						returnStatus = ReturnStatus.Ok;
					}
			}else{
				generateResult("Time out for sequence Number: "+ pair.id+ " for the group of : "+ pair.group, ReturnStatus.Timeout);
				// TODO handle rm that killed here if needed
			}
		} catch (InterruptedException e) {
			generateResult(ReturnStatus.ErrorSetTimeOut.toString(), ReturnStatus.ErrorSetTimeOut);
			e.printStackTrace();
		}
		
	}
	
	private void handlePair(int index){
		Info info = pair.infos.get(index);
		String json = info.json;
		MessageHeader message = FE.gson.fromJson(json, MessageHeader.class);
		
		if(message.message_type == MessageType.Reply){
			FEReplyMessage replyMessage = (FEReplyMessage) message;
			if(replyMessage.status){
				FE.log("message from port: " + info.port +" with index of: "+index+" is: "+ replyMessage.replyMessage);
				if(replyMessage.command_type == CommandType.LoginAdmin || replyMessage.command_type == CommandType.LoginStudent){
					if(replyMessage.command_type.equals("False")){
						returnStatus = ReturnStatus.CantLogin;
						result = "login faild";
					}
					result = "login done";
				}else{
					result += replyMessage.replyMessage + " ";
				}

			}else{
				FE.log("Fake Generator is on for packet with sequence Number: " +pair.id + "in Group of : " + pair.group + "with port: "+ info.port);
				returnStatus = ReturnStatus.FakeGenertor;
				// TODO rm needs handle this 
			}
		}else{
			generateResult("Message type is not replay something is wrong!", ReturnStatus.ErrorInMessageType);
		}
	}

	private void generateResult(String msg, ReturnStatus status){
		result = msg;
		returnStatus = status;
		FE.log(result);
	}
	
	public String getResult(){
		return result;
	}
}
