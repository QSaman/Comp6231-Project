package comp6231.project.mostafa.serverSide;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;

import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocket;
import comp6231.project.frontEnd.messages.FEBookRoomRequestMessage;
import comp6231.project.frontEnd.messages.FECancelBookingMessage;
import comp6231.project.frontEnd.messages.FEChangeReservationMessage;
import comp6231.project.frontEnd.messages.FECreateRoomRequestMessage;
import comp6231.project.frontEnd.messages.FEDeleteRoomRequestMessage;
import comp6231.project.frontEnd.messages.FEGetAvailableTimeSlotMessage;
import comp6231.project.frontEnd.messages.FEReplyMessage;
import comp6231.project.mostafa.serverSide.Database;
import comp6231.project.mostafa.serverSide.Information;
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

		public Handler(ReliableSocket socket) {
			this.socket = socket;
		}

		@Override
		public void run(){
			try {
				InputStream in = socket.getInputStream();
				byte[] buffer = new byte[Constants.BUFFER_SIZE];

				int size = in.read(buffer);
				handlePacket(buffer, size);
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

		private void handlePacket(byte[] buffer, int size) {
			String json_msg_str = new String(buffer,0,size);

			Server.log("UDP Socket Received JSON: "+json_msg_str);
			String result = process(json_msg_str);
			Server.log("UDP Socket Listener Result: "+result);
			byte[] data = result.getBytes();

			send(socket.getInetAddress(), socket.getPort(), data);
		}

		private void send(InetAddress address, int port, byte[] data){
			OutputStream out;
			try {
				out = socket.getOutputStream();
				out.write(data);
				out.flush();
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private String process(String json_msg_str){
			String result = null;
			MessageHeader json_msg = Server.gson.fromJson(json_msg_str, MessageHeader.class);

			if(json_msg.protocol_type == ProtocolType.Server_To_Server){
				ServerToServerMessage message = (ServerToServerMessage) json_msg;
				result  = processServerToServer(message.legacy.split(" "));
			}else{
				if(json_msg.message_type == MessageType.Request){
					result = processFrontEndToServer(json_msg);
				}else{
					Server.log("message type is reply");
				}
			}

			return result;
		}

		private String processFrontEndToServer(MessageHeader json){

			FEReplyMessage replyMessage = null;
			if(json.command_type == CommandType.Create_Room){
				FECreateRoomRequestMessage message = (FECreateRoomRequestMessage) json;
				String result = ServerImpl.GetInstance().create(message.roomNumber, message.date, message.timeSlots, message.userId);
				replyMessage = new FEReplyMessage(message.sequence_number, CommandType.Create_Room, result, true);
			}else if(json.command_type == CommandType.Book_Room){
				FEBookRoomRequestMessage message = (FEBookRoomRequestMessage) json;
				String result = ServerImpl.GetInstance().bookRoom(message.campusName, message.roomNumber, message.date, message.timeSlot, message.userId);
				replyMessage = new FEReplyMessage(message.sequence_number, CommandType.Book_Room, result, true);
			}else if (json.command_type == CommandType.Delete_Room){
				FEDeleteRoomRequestMessage message = (FEDeleteRoomRequestMessage) json;
				String result = ServerImpl.GetInstance().delete(message.roomNumber, message.date, message.timeSlots, message.userId);
				replyMessage = new FEReplyMessage(message.sequence_number, CommandType.Delete_Room, result, true);
			}else if (json.command_type == CommandType.Cancel_Book_Room){
				FECancelBookingMessage message = (FECancelBookingMessage) json;
				String result = ServerImpl.GetInstance().CancelBookingId(message.booking_id, message.user_id);
				replyMessage = new FEReplyMessage(message.sequence_number, CommandType.Cancel_Book_Room, result, true);
			}else if (json.command_type == CommandType.Change_Reservation){
				FEChangeReservationMessage message = (FEChangeReservationMessage) json;
				String result = ServerImpl.GetInstance().changeReservation(message.booking_id, message.new_campus_name, message.new_date, message.new_room_number, message.new_time_slot, message.user_id);
				replyMessage = new FEReplyMessage(message.sequence_number, CommandType.Change_Reservation, result, true);
			}else if (json.command_type == CommandType.Get_Available_TimeSlots){
				FEGetAvailableTimeSlotMessage message = (FEGetAvailableTimeSlotMessage) json;
				String result = ServerImpl.GetInstance().getAvailableTimeSlot(message.date, message.user_id);
				replyMessage = new FEReplyMessage(message.sequence_number, CommandType.Get_Available_TimeSlots, result, true);
			}else if (json.command_type == CommandType.LoginStudent){
				replyMessage = new FEReplyMessage(json.sequence_number, CommandType.LoginStudent, "logined-mostafa", true);
			}else if (json.command_type == CommandType.LoginAdmin){
				replyMessage = new FEReplyMessage(json.sequence_number, CommandType.LoginAdmin, "logined-mostafa", true);
			}else if (json.command_type == CommandType.SignOut){
				replyMessage = new FEReplyMessage(json.sequence_number, CommandType.SignOut, "signOut-mostafa", true);
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
