package comp6231.project.frontEnd.udp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import net.rudp.ReliableSocket;
import comp6231.project.frontEnd.FE;
import comp6231.project.frontEnd.FEUtility;
import comp6231.project.frontEnd.Info;
import comp6231.project.frontEnd.PortSwitcher;
import comp6231.project.frontEnd.ReturnStatus;
import comp6231.project.frontEnd.messages.FECancelBookingMessage;
import comp6231.project.frontEnd.messages.FEChangeReservationMessage;
import comp6231.project.frontEnd.messages.FEReplyMessage;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.messageProtocol.MessageHeader.CommandType;
import comp6231.project.messageProtocol.MessageHeader.MessageType;
import comp6231.project.replicaManager.messages.RMKillMessage;
import comp6231.shared.Constants;

public class Sequencer extends Thread{

	// key : seqNumber
	// value : FEPair
	public static ConcurrentHashMap<Integer, FEPair> holdBack = new ConcurrentHashMap<Integer, FEPair>();
	private static ConcurrentHashMap<String, ArrayList<String>> b_id = new ConcurrentHashMap<String, ArrayList<String>>();
	private static final Object lock_b_id = new Object();
	public FEPair pair;

	// key: replicaId 
	private static ConcurrentHashMap<Integer, ConcurrentLinkedQueue<Info>> timeOutCach = new ConcurrentHashMap<>();
	private static final Object timeOutLock = new Object();

	private String b_id_main_key;
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
		b_id_main_key ="";
	}

	@Override
	public void run() {
		adjustSeqNumber();
		byte [] m = FE.toJson(args).getBytes();			
		sendToReplica(m);
		setTimeOut();
	}

	private void adjustSeqNumber(){
		synchronized (lock) {
			args.sequence_number = sequenceNumber;
			String json = FE.toJson(args);
			Info [] data = {new Info(json),new Info(json),new Info(json)};
			pair = new FEPair(sequenceNumber, group, data);
			holdBack.put(sequenceNumber, pair);
			sequenceNumber ++;
		}
	}

	public void sendToReplica(byte[] sendBuffer) {

		int[] replicaPorts =  new int[3];
		String [] adresses = {Constants.FARID_IP,Constants.MOSTAFA_IP,Constants.SAMAN_IP};
		boolean customSend = false;
		String [] tokens = new String[3];
		ArrayList<byte[]> msgs = new ArrayList<>(3); 

		if(args.command_type == CommandType.Cancel_Book_Room) {
			customSend = true;
			FECancelBookingMessage message = (FECancelBookingMessage) args;

			if(b_id.containsKey(message.booking_id)) {
				tokens[0] = message.booking_id;
				tokens[1] = b_id.get(message.booking_id).get(0);
				tokens[2] = b_id.get(message.booking_id).get(1);
				FE.log(tokens[0] +"," + tokens[1]+","+tokens[2]);
			}else {
				for(int i = 0; i< Constants.ACTIVE_SERVERS; ++i) {
					tokens[i] = message.booking_id;
					FE.log(tokens[0] +"," + tokens[1]+","+tokens[2]);
				}
			}

			message.booking_id = tokens[0];
			msgs.add(FE.toJson(message).getBytes());
			pair.data[0] = new Info(FE.toJson(message));
			FECancelBookingMessage message2 = new FECancelBookingMessage(message.sequence_number, message.user_id, tokens[1]);
			msgs.add(FE.toJson(message2).getBytes());
			pair.data[1] = new Info(FE.toJson(message2));
			FECancelBookingMessage message3 = new FECancelBookingMessage(message.sequence_number, message.user_id, tokens[2]);
			msgs.add(FE.toJson(message3).getBytes());
			pair.data[2] = new Info(FE.toJson(message3));

		}else if(args.command_type == CommandType.Change_Reservation) {
			customSend = true;
			FEChangeReservationMessage message = (FEChangeReservationMessage) args;

			if(b_id.containsKey(message.booking_id)) {
				tokens[0] = message.booking_id;
				tokens[1] = b_id.get(message.booking_id).get(0);
				tokens[2] = b_id.get(message.booking_id).get(1);
				FE.log(tokens[0] +"," + tokens[1]+","+tokens[2]);
			}else {
				for(int i = 0; i< Constants.ACTIVE_SERVERS; ++i) {
					tokens[i] = message.booking_id;
					FE.log(tokens[0] +"," + tokens[1]+","+tokens[2]);
				}
			}

			message.booking_id = tokens[0];
			msgs.add(FE.toJson(message).getBytes());
			pair.data[0] = new Info(FE.toJson(message));
			FEChangeReservationMessage message2 = new FEChangeReservationMessage(message.sequence_number, message.user_id, tokens[1], message.new_campus_name, message.new_room_number, message.new_date, message.new_time_slot);
			msgs.add(FE.toJson(message2).getBytes());
			pair.data[1] = new Info(FE.toJson(message2));
			FEChangeReservationMessage message3 = new FEChangeReservationMessage(message.sequence_number, message.user_id, tokens[2], message.new_campus_name, message.new_room_number, message.new_date, message.new_time_slot);
			msgs.add(FE.toJson(message3).getBytes());
			pair.data[2] = new Info(FE.toJson(message3));
		}

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

		if(customSend) {
			for(int i=0;i< Constants.ACTIVE_SERVERS; ++i) {

				final byte[] data = msgs.get(i);
				final int idxPort = i;
				final String adress = adresses[i];

				new Thread(new Runnable() {
					@Override
					public void run() {

						try {
							ReliableSocket sendToReplica = new ReliableSocket();

							sendToReplica.connect(new InetSocketAddress(adress, replicaPorts[idxPort]));

							OutputStream out = sendToReplica.getOutputStream();
							out.write(data);

							out.flush();
							out.close();
							sendToReplica.close();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}).start();
			}
		}else {
			for(int i=0;i< Constants.ACTIVE_SERVERS; ++i) {

				final int idxPort = i;
				final String adress = adresses[i];

				new Thread(new Runnable() {
					@Override
					public void run() {

						try {
							ReliableSocket sendToReplica = new ReliableSocket();

							sendToReplica.connect(new InetSocketAddress(adress, replicaPorts[idxPort]));

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

	}

	public void setTimeOut(){
		try {
			FE.log("................................................Wating For The Results................................................");
			if(pair.semaphore.tryAcquire(30,TimeUnit.SECONDS)){
				for(int i = 0 ; i< Constants.ACTIVE_SERVERS; ++i){
					handlePair(i);
				}
				if(returnStatus != ReturnStatus.ErrorInMessageType && returnStatus != ReturnStatus.FakeGenertor && returnStatus != ReturnStatus.CantLogin){
					returnStatus = ReturnStatus.Ok;
				}
			}else{
				// this is for catching the crash and inform replica manager to replace the replica
				generateResult("Time out for sequence Number: "+ pair.id+ " for the group of : "+ pair.group, ReturnStatus.Timeout);
				for(int i = 0 ; i < Constants.ACTIVE_SERVERS; ++i) {
					if(!pair.infos.containsKey(i)) {
						synchronized (timeOutLock) {
							if(timeOutCach.containsKey(i)) {
								Info info =  pair.data[i];
								info.timeOutGroup = pair.group;
								timeOutCach.get(i).offer(info);
							}else {
								ConcurrentLinkedQueue<Info> queue = new ConcurrentLinkedQueue<>();
								Info info =  pair.data[i];
								info.timeOutGroup = pair.group;
								queue.offer(info);
								timeOutCach.put(i, queue);
							}
						}

						String portSwitcherArgs;
						RMKillMessage killMessage;
						switch (i) {
						case 0:
							if(!FEPair.isOneLock()){
								FEPair.setLockOne(true);
								FE.log("wait started for one");
								portSwitcherArgs = "F"+pair.group;
								killMessage = new RMKillMessage(pair.id, portSwitcherArgs);
								killMessage.replicaId = portSwitcherArgs.substring(0,1);
								FE.log("kill message sent: " + killMessage.toString());
								new Thread((new ErrorHandler(FEUtility.getInstance().findRMPort(portSwitcherArgs), killMessage))).start();
								synchronized (FEPair.lockOne) {
									FEPair.lockOne.wait();
									FE.log("wait finished for one");
									PortSwitcher.switchServer(portSwitcherArgs);
									resend(i);
									FEPair.isOneLock = false;
								}
							}
							break;
						case 1:
							if(!FEPair.isTwoLock()){
								FEPair.setLockTwo(true);
								FE.log("wait started for two");
								portSwitcherArgs = "M"+pair.group;
								killMessage = new RMKillMessage(pair.id, portSwitcherArgs);
								killMessage.replicaId = portSwitcherArgs.substring(0,1);
								FE.log("kill message sent: " + killMessage.toString());
								new Thread((new ErrorHandler(FEUtility.getInstance().findRMPort(portSwitcherArgs), killMessage))).start();
								synchronized (FEPair.lockTwo) {
									FEPair.lockTwo.wait();
									FE.log("wait finished for two");
									PortSwitcher.switchServer(portSwitcherArgs);
									resend(i);
									FEPair.isTwoLock = false;
								}
							}
							break;
						case 2:
							if(!FEPair.isThreeLock()){
								FEPair.setLockThree(true);
								FE.log("wait started for three");
								portSwitcherArgs = "S"+pair.group;
								killMessage = new RMKillMessage(pair.id, portSwitcherArgs);
								killMessage.replicaId  = portSwitcherArgs.substring(0,1);
								FE.log("kill message sent: " + killMessage.toString());
								new Thread((new ErrorHandler(FEUtility.getInstance().findRMPort(portSwitcherArgs), killMessage))).start();
								synchronized (FEPair.lockThree) {
									FEPair.lockThree.wait();
									FE.log("wait finished for three");
									PortSwitcher.switchServer(portSwitcherArgs);
									resend(i);
									FEPair.isThreeLock = false;
								}
							}
							break;
						default:
							break;
						}


					}
				}
			}
		} catch (InterruptedException e) {
			generateResult(ReturnStatus.ErrorSetTimeOut.toString(), ReturnStatus.ErrorSetTimeOut);
			e.printStackTrace();
		}

	}

	private void resend(int replicaId) {
		synchronized (timeOutLock) {
			while(!timeOutCach.get(replicaId).isEmpty()) {
				 Info info = timeOutCach.get(replicaId).poll();
				 if(info != null) {
					 String addr = "";
					 int port = -1;
					 if(replicaId == 0) {
						 addr = Constants.FARID_IP;
						 if(info.timeOutGroup.contains("DVL")) {
							 port = Constants.dvlPortListenFaridActive;
						 }else if (info.timeOutGroup.contains("KKL")) {
							 port = Constants.kklPortListenFaridActive;
						 }else if (info.timeOutGroup.contains("WST")) {
							 port = Constants.wstPortListenFaridActive;
						 }else {
							FE.log(" port in resend for farid is wrong!!");
						}
					 }else if (replicaId == 1) {
						 addr = Constants.MOSTAFA_IP;
						 if(info.timeOutGroup.contains("DVL")) {
							 port = Constants.dvlPortListenRe1Active;
						 }else if (info.timeOutGroup.contains("KKL")) {
							 port = Constants.kklPortListenRe1Active;
						 }else if (info.timeOutGroup.contains("WST")) {
							 port = Constants.wstPortListenRe1Active;
						 }else {
							FE.log(" port in resend for Mostafa is wrong!!");
						}
					}else if (replicaId == 2) {
						 addr = Constants.SAMAN_IP;
						 if(info.timeOutGroup.contains("DVL")) {
							 port = Constants.dvlPortListenRe2Active;
						 }else if (info.timeOutGroup.contains("KKL")) {
							 port = Constants.kklPortListenRe2Active;
						 }else if (info.timeOutGroup.contains("WST")) {
							 port = Constants.wstPortListenRe2Active;
						 }else {
							FE.log(" port in resend for Saman is wrong!!");
						}
					}else {
						FE.log(" replica id in resend is Wrong");
					}
					 FE.log("resend json for port: " +info.json + " "+ port);
					 resendToReplica(info.json, addr, port);
				 }
			}
		}
	}
	
	private void resendToReplica(String data, String addr, int port) {
		try {
			ReliableSocket aSocket = new ReliableSocket();
			aSocket.connect(new InetSocketAddress(addr, port));
			OutputStreamWriter out = new OutputStreamWriter(aSocket.getOutputStream());
			out.write(data);
			out.flush();
			out.close();
			aSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void handlePair(int index){
		Info info = pair.infos.get(index);
		String json = info.json;
		MessageHeader message = FE.fromJson(json);

		if(message.message_type == MessageType.Reply){
			FEReplyMessage replyMessage = (FEReplyMessage) message;
			FE.log(">>HANDLE PAIR: message with sequence number of : " + replyMessage.sequence_number +" with index of: "+index+" is: "+ replyMessage.replyMessage);

			// handle wrong results
			if(!replyMessage.isFakeGeneratorOff){
				FE.log("\n"+">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+"Fake Generator is detected for packet with sequence Number: " +pair.id + " from Campus of : " + pair.group + " from replica of : "+replyMessage.replicaId+">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+"\n");
				returnStatus = ReturnStatus.FakeGenertor;
				new Thread(new ErrorHandler(replyMessage.replicaId, true)).start();;
			}

			// handle return message 
			if(replyMessage.command_type == CommandType.LoginAdmin || replyMessage.command_type == CommandType.LoginStudent){
				if(replyMessage.replyMessage.equals("False")){
					returnStatus = ReturnStatus.CantLogin;
					result = "login faild";
				}
				result = "login done";
			}else{
				// return only Farid results
				if(index == 0) {
					result = replyMessage.replyMessage;
				}
			}

			// handle global booking id
			if(replyMessage.command_type == CommandType.Book_Room || replyMessage.command_type == CommandType.Change_Reservation){
				if(!replyMessage.bookingId.equals(Constants.NULL_STRING)) {
					synchronized (lock_b_id) {
						switch (index) {
						case 0:
							b_id_main_key = replyMessage.bookingId;
							b_id.put(b_id_main_key, new ArrayList<String>());
							break;
						case 1:
							if(b_id.containsKey(b_id_main_key)) {
								b_id.get(b_id_main_key).add(replyMessage.bookingId);
							}else {
								FE.log("global booking key is not exist");
							}
							break;
						case 2:
							if(b_id.containsKey(b_id_main_key)) {
								b_id.get(b_id_main_key).add(replyMessage.bookingId);
							}else {
								FE.log("global booking key is not exist");
							}
						default:
							break;
						}	
					}
				}
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

//	private static int getSeqNumber() {
//		synchronized (lock) {
//			return sequenceNumber;
//		}
//	}
}
