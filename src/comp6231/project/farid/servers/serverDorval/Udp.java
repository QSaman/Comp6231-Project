package comp6231.project.farid.servers.serverDorval;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocket;
import comp6231.project.frontEnd.messages.FEBookRoomRequestMessage;
import comp6231.project.frontEnd.messages.FECancelBookingMessage;
import comp6231.project.frontEnd.messages.FEChangeReservationMessage;
import comp6231.project.frontEnd.messages.FECreateRoomRequestMessage;
import comp6231.project.frontEnd.messages.FEDeleteRoomRequestMessage;
import comp6231.project.frontEnd.messages.FEGetAvailableTimeSlotMessage;
import comp6231.project.frontEnd.messages.FELoginAdminMessage;
import comp6231.project.frontEnd.messages.FELoginStudentMessage;
import comp6231.project.frontEnd.messages.FEReplyMessage;
import comp6231.project.frontEnd.messages.FESignOutMessage;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.messageProtocol.MessageHeader.CommandType;
import comp6231.project.messageProtocol.MessageHeader.ProtocolType;
import comp6231.project.messageProtocol.sharedMessage.ServerToServerMessage;
import comp6231.shared.Constants;

public class Udp implements Runnable {
	private ReliableServerSocket serverSocket;
	String udp_name;
	Udp(String args[]){
		udp_name = args[0];
	}
	@Override
	public void run() {
		serverSocket = null;
		try {
			serverSocket = new ReliableServerSocket(udp_name.equals("dvl_org")
					?Constants.DVL_PORT_LISTEN_FARID_ORIGINAL
							:
								Constants.DVL_PORT_LISTEN_FARID_BACKUP);
			while (true) {
				ReliableSocket aSocket = (ReliableSocket) serverSocket.accept();
				new Handler(aSocket).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class Handler extends Thread{

		private ReliableSocket socket;

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
			} catch (Exception e) {
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

		private void handlePacket(byte[] buffer, int size) throws Exception {
			String json = new String(buffer,0,size).trim();

			String packetToSend = processData(json);
			byte[] data = packetToSend.getBytes();

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

		private String processData(String json) throws Exception {
			String packetToSend = null;
			MessageHeader json_msg = ServerDorval.gson.fromJson(json, MessageHeader.class);
			boolean isFeToServer = json_msg.protocol_type == ProtocolType.Frontend_To_Replica ? true : false;
			FEReplyMessage replyMessage = null;

			if (isFeToServer) {

				//json = json.substring(3);
				int seqNumber = json_msg.sequence_number;
				if (json_msg.command_type == CommandType.LoginStudent) {

					FELoginStudentMessage message = (FELoginStudentMessage) json_msg;

					String tempStudentID = message.userId.toUpperCase();
					packetToSend = ServerDorval.setStudentID(tempStudentID) ? "True" : "False";
					replyMessage = new FEReplyMessage(seqNumber, CommandType.LoginStudent, packetToSend, true);

				} else if (json_msg.command_type == CommandType.LoginAdmin) {

					FELoginAdminMessage message = (FELoginAdminMessage) json_msg;

					String tempAdminID = message.userId.toUpperCase();
					packetToSend = ServerDorval.setAdminID(tempAdminID) ? "True" : "False";
					replyMessage = new FEReplyMessage(seqNumber, CommandType.LoginAdmin, packetToSend, true);

				} else if (json_msg.command_type == CommandType.SignOut) {

					FESignOutMessage message = (FESignOutMessage) json_msg;

					String tempID = message.userId.toUpperCase();
					ServerDorval.signOut(tempID);
					replyMessage = new FEReplyMessage(seqNumber, CommandType.SignOut, "Sign Out Called", true);

				} else if (json_msg.command_type == CommandType.Book_Room) {

					FEBookRoomRequestMessage message = (FEBookRoomRequestMessage) json_msg;

					String tempStudentID = message.userId.toUpperCase();
					int tempRoomNumber = message.roomNumber;
					int campus = message.campusName.toUpperCase().equals("DVL")?1:
						message.campusName.toUpperCase().equals("KKL")?2:3;
					LocalDate date = getLocalDate(message.date);
					LocalTime startTime = getLocalTime(message.timeSlot.substring(0, message.timeSlot.indexOf("-")));
					LocalTime endTime = getLocalTime(message.timeSlot.substring(message.timeSlot.indexOf("-")+1));
					packetToSend = ServerDorval.bookRoom(tempStudentID, campus, tempRoomNumber,
							date, startTime,endTime);
					replyMessage = new FEReplyMessage(seqNumber, CommandType.Book_Room, packetToSend, true);

				} else if (json_msg.command_type == CommandType.Cancel_Book_Room) {

					FECancelBookingMessage message = (FECancelBookingMessage) json_msg;

					String tempStudentID = message.user_id.toUpperCase();
					String tempBookingID = message.booking_id;

					packetToSend = ServerDorval.cancelBooking(tempStudentID, tempBookingID);
					replyMessage = new FEReplyMessage(seqNumber, CommandType.Cancel_Book_Room, packetToSend, true);


				} else if (json_msg.command_type == CommandType.Get_Available_TimeSlots) {

					FEGetAvailableTimeSlotMessage message = (FEGetAvailableTimeSlotMessage) json_msg;

					String tempStudentID = message.user_id.toUpperCase();
					LocalDate date = getLocalDate(message.date);

					packetToSend = ServerDorval.getAvailableTimeSlot(tempStudentID, date);
					replyMessage = new FEReplyMessage(seqNumber, CommandType.Get_Available_TimeSlots, packetToSend, true);

				} else if (json_msg.command_type == CommandType.Change_Reservation) {

					FEChangeReservationMessage message = (FEChangeReservationMessage) json_msg;

					String tempStudentID = message.user_id.toUpperCase();
					String bookingID = message.booking_id;
					int tempRoomNumber = message.new_room_number;
					int campus = message.new_campus_name.toUpperCase().equals("DVL")?1:
						message.new_campus_name.toUpperCase().equals("KKL")?2:3;

					LocalTime startTime = getLocalTime(message.new_time_slot.substring(0, message.new_time_slot.indexOf("-")));
					LocalTime endTime = getLocalTime(message.new_time_slot.substring(message.new_time_slot.indexOf("-")+1));

					packetToSend = ServerDorval.changeReservation(tempStudentID, bookingID, campus, tempRoomNumber,
							startTime, endTime);
					replyMessage = new FEReplyMessage(seqNumber, CommandType.Change_Reservation, packetToSend, true);

				} else if (json_msg.command_type == CommandType.Create_Room) {

					FECreateRoomRequestMessage message = (FECreateRoomRequestMessage) json_msg;

					String adminID = message.userId.toUpperCase();
					int roomNumber = message.roomNumber;
					LocalDate date = getLocalDate(message.date);
					LinkedHashMap<LocalTime, LocalTime> listOfTimeSlots = new LinkedHashMap<>();
					for (String timeSlot : message.timeSlots) {
						listOfTimeSlots.put(getLocalTime(timeSlot.substring(0, timeSlot.indexOf("-"))),
								getLocalTime(timeSlot.substring(timeSlot.indexOf("-")+1)));
					}

					packetToSend = ServerDorval.createRoom(adminID, roomNumber, date, listOfTimeSlots);
					replyMessage = new FEReplyMessage(seqNumber, CommandType.Create_Room, packetToSend, true);

				} else if (json_msg.command_type == CommandType.Delete_Room) {

					FEDeleteRoomRequestMessage message = (FEDeleteRoomRequestMessage) json_msg;

					String adminID = message.userId.toUpperCase();
					int roomNumber = message.roomNumber;
					LocalDate date = getLocalDate(message.date);
					LinkedHashMap<LocalTime, LocalTime> listOfTimeSlots = new LinkedHashMap<>();
					for (String timeSlot : message.timeSlots) {
						listOfTimeSlots.put(getLocalTime(timeSlot.substring(0, timeSlot.indexOf("-"))),
								getLocalTime(timeSlot.substring(timeSlot.indexOf("-")+1)));
					}

					packetToSend = ServerDorval.deleteRoom(adminID, roomNumber, date, listOfTimeSlots);
					replyMessage = new FEReplyMessage(seqNumber, CommandType.Delete_Room, packetToSend, true);
				}
			} else {
				// legacy code
				ServerToServerMessage message = (ServerToServerMessage) json_msg;
				String request = message.legacy;

				Pattern datePattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}$");
				Matcher dateMatcher = datePattern.matcher(request);
				// If the receiving package is started with a date
				if (dateMatcher.find() && dateMatcher.group().equals(request)) {
					int year = Integer.parseInt(request.substring(0, 4));
					int month = Integer.parseInt(request.substring(5, 7));
					int day = Integer.parseInt(request.substring(8, 10));
					packetToSend = Integer
							.toString(Student.getAvailableTimeFromServerForDate(LocalDate.of(year, month, day)));
				} else if (request.startsWith("book")) { // If the receiving package is started with "book"
					String tempStudentID = request.substring(5, 13);
					int tempRoomNumber = Integer
							.parseInt(request.substring(request.indexOf("@") + 1, request.indexOf("%")));
					int year = Integer.parseInt(request.substring(request.indexOf("%") + 1, request.indexOf("%") + 5));
					int month = Integer.parseInt(request.substring(request.indexOf("%") + 6, request.indexOf("%") + 8));
					int day = Integer.parseInt(request.substring(request.indexOf("%") + 9, request.indexOf("%") + 11));
					int startHour = Integer.parseInt(request.substring(request.indexOf("#") + 1, request.indexOf("#") + 3));
					int startMinute = Integer
							.parseInt(request.substring(request.indexOf("#") + 4, request.indexOf("#") + 6));
					int endHour = Integer.parseInt(request.substring(request.indexOf("*") + 1, request.indexOf("*") + 3));
					int endMinute = Integer.parseInt(request.substring(request.indexOf("*") + 4, request.indexOf("*") + 6));

					Student tempStudent = new Student();
					tempStudent.setStudentID(tempStudentID);

					packetToSend = tempStudent.bookRoom(tempRoomNumber, LocalDate.of(year, month, day),
							LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute));

					tempStudent.signOut();
				} else if (request.startsWith("cancel")) { // If the receiving package is started with "cancel"
					String tempStudentID = request.substring(7, 15);
					String tempBookingID = request.substring(request.indexOf("#") + 1).trim();

					Student tempStudent = new Student();
					tempStudent.setStudentID(tempStudentID);

					packetToSend = tempStudent.cancelBooking(tempBookingID);
					tempStudent.signOut();
				} else if (request.startsWith("getCounter")) { // If the receiving package is started with
					// "getCounter"
					String tempStudentID = request.substring(11, 19);
					synchronized (Locker.counterLock) {
						if (ReserveManager.counterDB.containsKey(tempStudentID))
							packetToSend = String.valueOf(ReserveManager.counterDB.get(tempStudentID).getCounter());
						else
							packetToSend = "0";
					}
				} else if (request.startsWith("getExpire")) { // If the receiving package is started with
					// "getExpre"
					String tempStudentID = request.substring(10, 18);
					synchronized (Locker.counterLock) {
						if (ReserveManager.counterDB.containsKey(tempStudentID))
							packetToSend = String.valueOf(ReserveManager.counterDB.get(tempStudentID).getExpireDate());
						else
							packetToSend = "0";
					}
				} else if (request.startsWith("reset")) {
					String tempStudentID = request.substring(6, 14);
					if (ReserveManager.counterDB.containsKey(tempStudentID))
						ReserveManager.counterDB.get(tempStudentID).reset(LocalDate.now());
					packetToSend = "RESET";

				} else if (request.startsWith("chan")) {
					// "chan-" + studentID + "@" + roomNumber + "%" + campus + "#" + startTime + "*"
					// + endTime + "&" + bookingID;
					String tempStudentID = request.substring(5, 13);
					int tempRoomNumber = Integer
							.parseInt(request.substring(request.indexOf("@") + 1, request.indexOf("%")));
					int campus = Integer.parseInt(request.substring(request.indexOf("%") + 1, request.indexOf("%") + 2));
					int startHour = Integer.parseInt(request.substring(request.indexOf("#") + 1, request.indexOf("#") + 3));
					int startMinute = Integer
							.parseInt(request.substring(request.indexOf("#") + 4, request.indexOf("#") + 6));
					int endHour = Integer.parseInt(request.substring(request.indexOf("*") + 1, request.indexOf("*") + 3));
					int endMinute = Integer.parseInt(request.substring(request.indexOf("*") + 4, request.indexOf("*") + 6));
					String bookingID = request.substring(request.indexOf("&") + 1).trim();

					Student tempStudent = new Student();
					tempStudent.setStudentID(tempStudentID);

					packetToSend = tempStudent.changeReservation(tempStudentID, bookingID, campus, tempRoomNumber,
							LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute));

					tempStudent.signOut();
				}
			}
			if (isFeToServer) {
				return ServerDorval.gson.toJson(replyMessage);
			}
			return packetToSend;
		}

		LocalTime getLocalTime(String string) {
			int hour = Integer.parseInt(string.substring(0, string.indexOf(":")));
			int minute = Integer.parseInt(string.substring(string.indexOf(":")+1));
			return LocalTime.of(hour, minute);
		}

		LocalDate getLocalDate(String string) {
			int year = Integer.parseInt(string.substring(0, 4));
			int month = Integer.parseInt(string.substring(5, 7));
			int day = Integer.parseInt(string.substring(8));
			return LocalDate.of(year, month, day);
		}

	}

}
