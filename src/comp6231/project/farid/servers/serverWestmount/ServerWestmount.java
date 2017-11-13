package comp6231.project.farid.servers.serverWestmount;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import comp6231.project.farid.sharedPackage.DrrsConstants;

public class ServerWestmount {

	static Map<LocalDate, HashMap<Integer, HashMap<LocalTime, LocalTime>>> dataBase = Collections
			.synchronizedMap(new HashMap<>());
	static MyLogger westmountServerLogger;

	private static Map<String, Student> students = Collections.synchronizedMap(new HashMap<>());
	private static Map<String, Admin> admins = Collections.synchronizedMap(new HashMap<>());
	private static DatagramSocket serverSocket;

	private ServerWestmount() throws RemoteException {
		super();
	}

	private static void addTestCase() {
		HashMap<LocalTime, LocalTime> times = new HashMap<>();
		times.put(LocalTime.of(8, 0), LocalTime.of(9, 0));
		times.put(LocalTime.of(9, 0), LocalTime.of(10, 0));
		times.put(LocalTime.of(10, 0), LocalTime.of(11, 0));
		times.put(LocalTime.of(11, 0), LocalTime.of(12, 0));
		times.put(LocalTime.of(12, 0), LocalTime.of(13, 0));
		HashMap<Integer, HashMap<LocalTime, LocalTime>> rooms = new HashMap<>();
		rooms.put(1, times);
		dataBase.put(LocalDate.of(2017, 1, 1), rooms);
		System.out.println("Test cases have been added");
	}

	public static void main(String[] args) throws Exception {
		addTestCase();
		westmountServerLogger = new MyLogger("WestmountServer");

		Thread udpThread = new Thread(() -> {
			try {
				serverSocket = new DatagramSocket(DrrsConstants.WST_PORT);
				byte[] receiveData = new byte[1024];
				byte[] sendData = new byte[1024];

				while (true) {
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					serverSocket.receive(receivePacket);
					String request = new String(receivePacket.getData(), 0, receivePacket.getLength()).trim();
					int port = receivePacket.getPort();
					String packetToSend = null;

					Pattern datePattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}$");
					Matcher dateMatcher = datePattern.matcher(request);

					if (dateMatcher.find() && dateMatcher.group().equals(request)) {
						int year = Integer.parseInt(request.substring(0, 4));
						int month = Integer.parseInt(request.substring(5, 7));
						int day = Integer.parseInt(request.substring(8, 10));
						packetToSend = Integer
								.toString(Student.getAvailableTimeFromServerForDate(LocalDate.of(year, month, day)));
					} else if (request.startsWith("book")) {
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

					} else if (request.startsWith("cancel")) {
						String tempStudentID = request.substring(7, 15);
						String tempBookingID = request.substring(request.indexOf("#") + 1).trim();

						Student tempStudent = new Student();
						tempStudent.setStudentID(tempStudentID);

						packetToSend = tempStudent.cancelBooking(tempBookingID);
						tempStudent.signOut();
					} else if (request.startsWith("getCounter")) {
						String tempStudentID = request.substring(11, 19);
						synchronized (Locker.counterLock) {
							if (ReserveManager.counterDB.containsKey(tempStudentID))
								packetToSend = String.valueOf(ReserveManager.counterDB.get(tempStudentID).getCounter());
							else
								packetToSend = "0";
						}
					} else if (request.startsWith("getExpire")) {
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

						packetToSend = tempStudent.changeReservation(tempStudentID, bookingID, campus, tempRoomNumber,
								LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute));

						tempStudent.signOut();
					}

					InetAddress IPAddress = receivePacket.getAddress();
					// assert packetToSend != null;
					sendData = packetToSend.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
					serverSocket.send(sendPacket);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		udpThread.start();
	}

	static void printCurrentDatabase() {
		StringBuilder log = new StringBuilder();
		log.append("\n$$$ CURRENT DATABASE:");
		synchronized (Locker.databaseLock) {
			dataBase.forEach((key, value) -> value.forEach((key1, value1) -> log.append("\nDate:").append(key)
					.append("   Room number: ").append(key1).append("   Time slots:").append(sortByValue(value1))));
		}
		log.append("\n");
		westmountServerLogger.log(log.toString());
	}

	private static <K, V extends Comparable<? super V>> HashMap<K, V> sortByValue(HashMap<K, V> hashMap) {
		return hashMap.entrySet().stream().sorted(HashMap.Entry.comparingByValue()).collect(
				Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}

	public static String createRoom(String adminID, int roomNumber, LocalDate date,
			LinkedHashMap<LocalTime, LocalTime> listOfTimeSlots) throws Exception {
		return admins.get(adminID).createRoom(roomNumber, date, listOfTimeSlots);
	}

	public static String deleteRoom(String adminID, int roomNumber, LocalDate date,
			LinkedHashMap<LocalTime, LocalTime> listOfTimeSlots) throws Exception {
		return admins.get(adminID).deleteRoom(roomNumber, date, listOfTimeSlots);
	}

	public static String bookRoom(String studentID, int campus, int roomNumber, LocalDate date, LocalTime startTime,
			LocalTime endTime) throws Exception {
		return students.get(studentID).bookRoom(campus, roomNumber, date, startTime, endTime);
	}

	public static String getAvailableTimeSlot(String studentID, LocalDate date) throws Exception {
		return students.get(studentID).getAvailableTimeSlot(date);
	}

	public static String cancelBooking(String studentID, String bookingID) throws Exception {
		return students.get(studentID).cancelBooking(bookingID);
	}

	public static String changeReservation(String studentID, String bookingID, int campus, int roomNumber,
			LocalTime startTime, LocalTime endTime) throws Exception {
		return students.get(studentID).changeReservation(studentID, bookingID, campus, roomNumber, startTime, endTime);
	}

	public static boolean setAdminID(String adminID) throws Exception {
		synchronized (Locker.adminsLock) {
			if (!admins.containsKey(adminID)) {

				Admin admin = new Admin();
				admins.put(adminID, admin);
				return admin.setAdminID(adminID);
			}
		}
		ServerWestmount.westmountServerLogger
				.log("!!! Admin " + adminID + " tried to connect from other system. BLOCKED !!!");
		return false;
	}

	public static boolean setStudentID(String studentID) throws Exception {
		synchronized (Locker.studentsLock) {
			if (!students.containsKey(studentID)) {
				Student student = new Student();
				students.put(studentID, student);
				return student.setStudentID(studentID);
			}
		}
		ServerWestmount.westmountServerLogger
				.log("!!! Student " + studentID + " tried to connect from other system. BLOCKED !!!");
		return false;
	}

	public static void signOut(String ID) throws Exception {
		if (ID.charAt(3) == 'S') {
			synchronized (Locker.studentsLock) {
				students.remove(ID);
			}
			ServerWestmount.westmountServerLogger.log("\nStudent " + ID + " signed out\n");
		} else if (ID.charAt(3) == 'A') {
			synchronized (Locker.adminsLock) {
				admins.remove(ID);
			}
			ServerWestmount.westmountServerLogger.log("\nAdmin " + ID + " signed out\n");
		}
	}
}