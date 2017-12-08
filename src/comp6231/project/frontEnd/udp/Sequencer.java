package comp6231.project.frontEnd.udp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import net.rudp.ReliableSocket;
import comp6231.project.frontEnd.FE;
import comp6231.project.frontEnd.Info;
import comp6231.project.frontEnd.ReturnStatus;
import comp6231.project.frontEnd.messages.FECancelBookingMessage;
import comp6231.project.frontEnd.messages.FEChangeReservationMessage;
import comp6231.project.frontEnd.messages.FEReplyMessage;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.messageProtocol.MessageHeader.CommandType;
import comp6231.project.messageProtocol.MessageHeader.MessageType;
import comp6231.shared.Constants;

public class Sequencer extends Thread{

	// key : seqNumber
	// value : FEPair
	public static ConcurrentHashMap<Integer, FEPair> holdBack = new ConcurrentHashMap<Integer, FEPair>();
	private static ConcurrentHashMap<String, ArrayList<String>> b_id = new ConcurrentHashMap<String, ArrayList<String>>();
	public FEPair pair;
	
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
			pair = new FEPair(sequenceNumber, group);
			holdBack.put(sequenceNumber, pair);
			sequenceNumber ++;
		}
	}

	@SuppressWarnings("null")
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
			FECancelBookingMessage message2 = new FECancelBookingMessage(message.sequence_number, message.user_id, tokens[1]);
			msgs.add(FE.toJson(message2).getBytes());
			FECancelBookingMessage message3 = new FECancelBookingMessage(message.sequence_number, message.user_id, tokens[2]);
			msgs.add(FE.toJson(message3).getBytes());

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
			FEChangeReservationMessage message2 = new FEChangeReservationMessage(message.sequence_number, message.user_id, tokens[1], message.new_campus_name, message.new_room_number, message.new_date, message.new_time_slot);
			msgs.add(FE.toJson(message2).getBytes());
			FEChangeReservationMessage message3 = new FEChangeReservationMessage(message.sequence_number, message.user_id, tokens[2], message.new_campus_name, message.new_room_number, message.new_date, message.new_time_slot);
			msgs.add(FE.toJson(message3).getBytes());
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
		MessageHeader message = FE.fromJson(json);
		
		if(message.message_type == MessageType.Reply){
			FEReplyMessage replyMessage = (FEReplyMessage) message;
			FE.log("message from port: " + info.port +" with index of: "+index+" is: "+ replyMessage.replyMessage);
			
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
