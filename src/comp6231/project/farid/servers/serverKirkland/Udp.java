package comp6231.project.farid.servers.serverKirkland;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import comp6231.project.farid.servers.serverKirkland.Student;
import comp6231.project.farid.sharedPackage.DrrsConstants;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.messageProtocol.MessageHeader.ProtocolType;
import comp6231.project.messageProtocol.sharedMessage.ServerToServerMessage;

public class Udp  implements Runnable {
	private DatagramSocket serverSocket;
	private final Object sendLock = new Object();
	private byte[] sendData;
	@Override
	public void run() {
		serverSocket = null;
		try {
			serverSocket = new DatagramSocket(DrrsConstants.KKL_PORT);
			byte[] receiveData = new byte[1024];
			sendData = new byte[1024];
			
			while(true){
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);

				Thread thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							Udp.this.handlePacket(receivePacket);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				thread.start();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(serverSocket != null) serverSocket.close();
		}
	}
	
	private void handlePacket(DatagramPacket receivePacket) throws Exception {
			String json = new String(receivePacket.getData(),0,receivePacket.getLength()).trim();

			String packetToSend = processData(json);
			sendData = packetToSend.getBytes();

			send(receivePacket.getAddress(), receivePacket.getPort(), sendData);
	}
	
	private void send(InetAddress address, int port, byte[] data){
		DatagramPacket reply = new DatagramPacket(
				data, 
				data.length, 
				address, 
				port
		);

		try {
			synchronized (sendLock) {
				serverSocket.send(reply);
			}
		} catch (IOException e) {
			e.getMessage();
		}
	}
	
	private static String processData(String json) throws Exception{
		String packetToSend = null;
		MessageHeader json_msg = ServerKirkland.gson.fromJson(json, MessageHeader.class);
		boolean isFeToServer = json_msg.protocol_type == ProtocolType.Frontend_To_Replica ? true : false;
		if (isFeToServer) {
			// TODO 
			json = json.substring(3);
			if (json.startsWith("signInStudent")) {
				
				String tempStudentID = "DVLS1234"; 
				packetToSend = ServerKirkland.setStudentID(tempStudentID)?"True":"False";

			} else if (json.startsWith("signInAdmin")) {
				
				String tempAdminID = "DVLA1234"; 
				packetToSend = ServerKirkland.setAdminID(tempAdminID)?"True":"False";
				
			} else if(json.startsWith("signOut")){
				
				String tempID = "DVLS1234";
				ServerKirkland.signOut(tempID);
				
			} else if(json.startsWith("bookRoom")){
				
				String tempStudentID = json.substring(5, 13);
				int tempRoomNumber = 1;
				int campus = 1;
				int year = 2017;
				int month = 1;
				int day = 1;
				int startHour = 8;
				int startMinute = 0;
				int endHour = 9;
				int endMinute = 0;

				packetToSend = ServerKirkland.bookRoom(tempStudentID,campus,tempRoomNumber, LocalDate.of(year, month, day),
						LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute));
			
			} else if(json.startsWith("cancelBooking")) {
				
				String tempStudentID = json.substring(5, 13);
				String tempBookingID = "DVL-128913289sdhjkqjh19";

				packetToSend = ServerKirkland.cancelBooking(tempStudentID,tempBookingID);
				
			} else if(json.startsWith("getAvailableTime")) {
				
				String tempStudentID = "DVLS1234";
				int year = 2017;
				int month = 1;
				int day = 1;
				
				packetToSend = ServerKirkland.getAvailableTimeSlot(tempStudentID, LocalDate.of(year, month, day));
				
			} else if(json.startsWith("changeReservation")) {
				
			String tempStudentID = "DVLS1234";
			String bookingID = "DVLS-12313984sddfiuh19dsadoifh";
			int tempRoomNumber = 1;
			int campus = 1;
			
			int startHour = 8;
			int startMinute = 0;
			int endHour = 9;
			int endMinute = 0;

			packetToSend = ServerKirkland.changeReservation(tempStudentID, bookingID, campus, tempRoomNumber,
					LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute));
			} else if(json.startsWith("create")) {
											
				String adminID = "DVLA1234";
				int roomNumber = 1;
				int year = 2017;
				int month = 1;
				int day = 1;
				LinkedHashMap<LocalTime, LocalTime> listOfTimeSlots = new LinkedHashMap<>();
				listOfTimeSlots.put(LocalTime.of(20, 0), LocalTime.of(21, 0));
				
				packetToSend = ServerKirkland.createRoom(adminID, roomNumber, LocalDate.of(year, month, day), listOfTimeSlots);
				
			} else if(json.startsWith("delete")){
				
				String adminID = "DVLA1234";
				int roomNumber = 1;
				int year = 2017;
				int month = 1;
				int day = 1;
				LinkedHashMap<LocalTime, LocalTime> listOfTimeSlots = new LinkedHashMap<>();
				
				packetToSend = ServerKirkland.deleteRoom(adminID, roomNumber, LocalDate.of(year, month, day), listOfTimeSlots);
			}
		}
		else 
		{
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
				packetToSend = Integer.toString(
						Student.getAvailableTimeFromServerForDate(LocalDate.of(year, month, day)));
			} else if (request.startsWith("book")) { // If the receiving package is started with "book"
				String tempStudentID = request.substring(5, 13);
				int tempRoomNumber = Integer
						.parseInt(request.substring(request.indexOf("@") + 1, request.indexOf("%")));
				int year = Integer
						.parseInt(request.substring(request.indexOf("%") + 1, request.indexOf("%") + 5));
				int month = Integer
						.parseInt(request.substring(request.indexOf("%") + 6, request.indexOf("%") + 8));
				int day = Integer
						.parseInt(request.substring(request.indexOf("%") + 9, request.indexOf("%") + 11));
				int startHour = Integer
						.parseInt(request.substring(request.indexOf("#") + 1, request.indexOf("#") + 3));
				int startMinute = Integer
						.parseInt(request.substring(request.indexOf("#") + 4, request.indexOf("#") + 6));
				int endHour = Integer
						.parseInt(request.substring(request.indexOf("*") + 1, request.indexOf("*") + 3));
				int endMinute = Integer
						.parseInt(request.substring(request.indexOf("*") + 4, request.indexOf("*") + 6));

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
						packetToSend = String
								.valueOf(ReserveManager.counterDB.get(tempStudentID).getCounter());
					else
						packetToSend = "0";
				}
			} else if (request.startsWith("getExpire")) { // If the receiving package is started with
															// "getExpre"
				String tempStudentID = request.substring(10, 18);
				synchronized (Locker.counterLock) {
					if (ReserveManager.counterDB.containsKey(tempStudentID))
						packetToSend = String
								.valueOf(ReserveManager.counterDB.get(tempStudentID).getExpireDate());
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
				int campus = Integer
						.parseInt(request.substring(request.indexOf("%") + 1, request.indexOf("%") + 2));
				int startHour = Integer
						.parseInt(request.substring(request.indexOf("#") + 1, request.indexOf("#") + 3));
				int startMinute = Integer
						.parseInt(request.substring(request.indexOf("#") + 4, request.indexOf("#") + 6));
				int endHour = Integer
						.parseInt(request.substring(request.indexOf("*") + 1, request.indexOf("*") + 3));
				int endMinute = Integer
						.parseInt(request.substring(request.indexOf("*") + 4, request.indexOf("*") + 6));
				String bookingID = request.substring(request.indexOf("&") + 1).trim();

				Student tempStudent = new Student();
				tempStudent.setStudentID(tempStudentID);

				packetToSend = tempStudent.changeReservation(tempStudentID, bookingID, campus,
						tempRoomNumber, LocalTime.of(startHour, startMinute),
						LocalTime.of(endHour, endMinute));

				tempStudent.signOut();
			}
		}
		return packetToSend;
	}

}
