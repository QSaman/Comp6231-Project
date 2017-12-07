package comp6231.project.mostafa.serverSide;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocket;
import comp6231.project.frontEnd.PortSwitcher;
import comp6231.project.frontEnd.messages.FEBookRoomRequestMessage;
import comp6231.project.frontEnd.messages.FECancelBookingMessage;
import comp6231.project.frontEnd.messages.FEChangeReservationMessage;
import comp6231.project.frontEnd.messages.FECreateRoomRequestMessage;
import comp6231.project.frontEnd.messages.FEDeleteRoomRequestMessage;
import comp6231.project.frontEnd.messages.FEGetAvailableTimeSlotMessage;
import comp6231.project.frontEnd.messages.FEReplyMessage;
import comp6231.project.mostafa.serverSide.Database;
import comp6231.project.mostafa.serverSide.Information;
import comp6231.project.replicaManager.messages.RMFakeGeneratorMessage;
import comp6231.project.replicaManager.messages.RMKillMessage;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.messageProtocol.MessageHeader.CommandType;
import comp6231.project.messageProtocol.MessageHeader.MessageType;
import comp6231.project.messageProtocol.MessageHeader.ProtocolType;
import comp6231.project.messageProtocol.sharedMessage.ServerToServerMessage;
import comp6231.shared.Constants;

public class UDPlistener  implements Runnable {
	private ReliableServerSocket socket;

	@Override
	public void run() {
		socket = null;
		try {
			socket = new ReliableServerSocket(Information.getInstance().getUDPListenPort());

			while(true){
				ReliableSocket aSocket = (ReliableSocket) socket.accept();
				new Handler(aSocket).start();
			}

		}catch (IOException e){
			Server.log("Socket: " + e.getMessage());
		}
	}

	class Handler extends Thread{

		ReliableSocket socket;
		private ProtocolType protocolType;
		
		public Handler(ReliableSocket socket) {
			this.socket = socket;
		}

		@Override
		public void run(){
			try {
				InputStreamReader in = new InputStreamReader(socket.getInputStream());

				CharArrayWriter writer = new CharArrayWriter(Constants.BUFFER_SIZE);
				
				while(true) {
				  int n = in.read();
				  if( n < 0  || n == '\u0004') break;
				  writer.write(n);
				}

				handlePacket(writer.toString());

			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(socket !=null){try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}}
			}
		}

		private void handlePacket(String json_msg_str) {
			
			Server.log("UDP Socket Received JSON: "+json_msg_str);
			String result = process(json_msg_str);
			Server.log("UDP Socket Listener Result: "+result);
			
			if(protocolType != ProtocolType.ReplicaManager_Message) {
				try {
					Server.save();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				send(socket.getInetAddress(), socket.getPort(), result);
			}
		}

		private void send(InetAddress address, int port, String data){
			OutputStreamWriter out;
			if(protocolType == ProtocolType.Server_To_Server){
				try {
					out = new OutputStreamWriter(socket.getOutputStream());
					out.write(data);
					out.write('\u0004');
					out.flush();
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				try {
					ReliableSocket aSocket = new ReliableSocket();
					aSocket.connect(new InetSocketAddress(Information.getInstance().isReOne == true ?Constants.MOSTAFA_IP : Constants.SAMAN_IP, Constants.FE_PORT_LISTEN));
					out = new OutputStreamWriter(aSocket.getOutputStream());
					out.write(data);
					out.flush();
					out.close();
					aSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		private String process(String json_msg_str){
			String result = null;
			MessageHeader json_msg = Server.gson.fromJson(json_msg_str, MessageHeader.class);

			if(json_msg.protocol_type == ProtocolType.Server_To_Server){
				protocolType = ProtocolType.Server_To_Server;
				ServerToServerMessage message = (ServerToServerMessage) json_msg;
				result  = processServerToServer(message.legacy.split(" "));
			}else if(json_msg.protocol_type == ProtocolType.Frontend_To_Replica){
				protocolType = ProtocolType.Frontend_To_Replica;
				if(json_msg.message_type == MessageType.Request){
					result = processFrontEndToServer(json_msg);
				}else{
					Server.log("message type is reply");
				}
			}else if(json_msg.protocol_type == ProtocolType.ReplicaManager_Message) {
				protocolType = ProtocolType.ReplicaManager_Message;
				if(json_msg.message_type == MessageType.Request){
					processReplicaManager(json_msg);
					result = Constants.ONE_WAY;
				}else{
					Server.log("message type is reply");
				}
			}

			return result;
		}

		
		private void processReplicaManager(MessageHeader json) {
			if(json.command_type == CommandType.Kill) {
				RMKillMessage message = (RMKillMessage) json;
				PortSwitcher.switchServer(message.portSwitcherArg);
				try {
					Server.load();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Server.log("Server Switched");
			}else if (json.command_type == CommandType.Fake_Generator) {
				RMFakeGeneratorMessage message  = (RMFakeGeneratorMessage) json;
				Server.setFakeGeneratorOff(message.turnOff);
				Server.log(" isFakeGeneratorOn : " + !Server.isFakeGeneratorOff());
			}else {
				Server.log(" process replica has wrong Command Type");
			}
		}
		
		private String processFrontEndToServer(MessageHeader json){

			FEReplyMessage replyMessage = null;
			if(json.command_type == CommandType.Create_Room){
				FECreateRoomRequestMessage message = (FECreateRoomRequestMessage) json;
				String result = ServerImpl.GetInstance().create(message.roomNumber, message.date, message.timeSlots, message.userId);
				replyMessage = new FEReplyMessage(message.sequence_number, CommandType.Create_Room, Information.getInstance().replicaId+": "+result, Server.isFakeGeneratorOff(), Information.getInstance().replicaId);
			}else if(json.command_type == CommandType.Book_Room){
				FEBookRoomRequestMessage message = (FEBookRoomRequestMessage) json;
				String result = ServerImpl.GetInstance().bookRoom(message.campusName, message.roomNumber, message.date, message.timeSlot, message.userId);
				
				String bookingId = Constants.NULL_STRING;
				if(result.contains("booked") && !result.contains("notbooked")){
					int startIndex = result.indexOf("bookingId")+ "bookingId".length() +1;
					int endIndex = result.indexOf("booked") -1;
					bookingId = result.substring(startIndex,endIndex);
					Server.log("booking id: " + bookingId + " size: " + bookingId.length() + " trimed bookingID: " + bookingId.trim() + " size trimed: " + bookingId.trim().length());
				}else {
					Server.log(" not booked");
				}
				
				replyMessage = new FEReplyMessage(message.sequence_number, CommandType.Book_Room, Information.getInstance().isReOne == true ? "Mostafa: "+result : "Saman: "+result, true, bookingId,  Information.getInstance().replicaId);
			}else if (json.command_type == CommandType.Delete_Room){
				FEDeleteRoomRequestMessage message = (FEDeleteRoomRequestMessage) json;
				String result = ServerImpl.GetInstance().delete(message.roomNumber, message.date, message.timeSlots, message.userId);
				replyMessage = new FEReplyMessage(message.sequence_number, CommandType.Delete_Room, Information.getInstance().replicaId+": "+result, Server.isFakeGeneratorOff(), Information.getInstance().replicaId);
			}else if (json.command_type == CommandType.Cancel_Book_Room){
				FECancelBookingMessage message = (FECancelBookingMessage) json;
				String result = ServerImpl.GetInstance().CancelBookingId(message.booking_id, message.user_id);
				replyMessage = new FEReplyMessage(message.sequence_number, CommandType.Cancel_Book_Room, Information.getInstance().replicaId+": "+result, Server.isFakeGeneratorOff(), Information.getInstance().replicaId);
			}else if (json.command_type == CommandType.Change_Reservation){
				FEChangeReservationMessage message = (FEChangeReservationMessage) json;
				String result = ServerImpl.GetInstance().changeReservation(message.booking_id, message.new_campus_name, message.new_date, message.new_room_number, message.new_time_slot, message.user_id);
				
				String bookingId = Constants.NULL_STRING;
				if(result.contains("booked") && !result.contains("notbooked")){
					int startIndex = result.indexOf("bookingId")+ "bookingId".length() +1;
					int endIndex = result.indexOf("booked") -1;
					bookingId = result.substring(startIndex,endIndex);
					Server.log("booking id: " + bookingId + " size: " + bookingId.length() + " trimed bookingID: " + bookingId.trim() + " size trimed: " + bookingId.trim().length());
				}else {
					Server.log(" not booked");
				}
				
				replyMessage = new FEReplyMessage(message.sequence_number, CommandType.Change_Reservation, Information.getInstance().isReOne == true ? "Mostafa: "+result : "Saman: "+result, true, bookingId,  Information.getInstance().replicaId);
			}else if (json.command_type == CommandType.Get_Available_TimeSlots){
				FEGetAvailableTimeSlotMessage message = (FEGetAvailableTimeSlotMessage) json;
				String result = ServerImpl.GetInstance().getAvailableTimeSlot(message.date, message.user_id);
				replyMessage = new FEReplyMessage(message.sequence_number, CommandType.Get_Available_TimeSlots, Information.getInstance().replicaId+": "+result, Server.isFakeGeneratorOff(), Information.getInstance().replicaId);
			}else if (json.command_type == CommandType.LoginStudent){
				replyMessage = new FEReplyMessage(json.sequence_number, CommandType.LoginStudent, Information.getInstance().isReOne == true ? "logined-mostafa" : "logined-saman", Server.isFakeGeneratorOff(), Information.getInstance().replicaId);
			}else if (json.command_type == CommandType.LoginAdmin){
				replyMessage = new FEReplyMessage(json.sequence_number, CommandType.LoginAdmin, Information.getInstance().isReOne == true ? "logined-mostafa" : "logined-saman", Server.isFakeGeneratorOff(), Information.getInstance().replicaId);
			}else if (json.command_type == CommandType.SignOut){
				replyMessage = new FEReplyMessage(json.sequence_number, CommandType.SignOut, Information.getInstance().isReOne == true ? "signOut-mostafa" : "signOut-saman", Server.isFakeGeneratorOff(), Information.getInstance().replicaId);
			}else{
				Server.log(" Bad CommandType in udp listener");
			}

			return Server.gson.toJson(replyMessage);
		}

		private String processServerToServer(String splited[]){
			String result = null;
			if(splited[0].equalsIgnoreCase(Constants.BOOK_ROOM)){
				int roomNumber =Integer.parseInt(splited[1]);
				String date = splited[2];
				String time = splited[3];
				String id = splited[4];
				String[] timeSplit = time.split("-");
				result = Database.getInstance().tryToBookRoom(roomNumber, date, Information.getInstance().convertTimeToSec(timeSplit[0]), Information.getInstance().convertTimeToSec(timeSplit[1]), id);
			}else if (splited[0].equalsIgnoreCase(Constants.REDUCE_BOOK_COUNT)){
				String id = splited[1];
				result = Database.getInstance().reduceBookingCount(id);
			}else if (splited[0].equalsIgnoreCase(Constants.REQ_GETAVTIME)){
				String date = splited[1];
				result = Database.getInstance().findAvailableTimeSlot(date)+"";
			}else if (splited[0].equalsIgnoreCase(Constants.REQ_CANCEL_BOOK)){
				String bookingId = splited[1];
				String id = splited[2];
				result = Database.getInstance().cancelBookingId(bookingId, id);
			}else if(splited[0].equalsIgnoreCase(Constants.REQ_REMOVE_BOOK)){
				String bookingId = splited[1];
				boolean removeResult = Database.getInstance().removeBookingId(bookingId);
				if (removeResult){
					result = "bookingId: "+bookingId+" removed";
				}else{
					result = Constants.RESULT_UDP_FAILD;
				}

			}else if(splited[0].equals(Constants.COMMIT)){
				Database.getInstance().commit();
				result = "UDP-Database commited";
			}else if (splited[0].equals(Constants.ROLLBACK)){
				Database.getInstance().rollBack();
				result = "UDP-Database rollBacked";
			}
			return result;
		}
	}


}
