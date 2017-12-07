package comp6231.project.farid.servers.serverWestmount;

import java.io.IOException;
import java.net.DatagramSocket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import comp6231.project.messageProtocol.StartGson;
import comp6231.project.messageProtocol.sharedMessage.ServerToServerMessage;

public class ServerWestmount {

	public static Gson gson;
	private static boolean isFakeGeneratorOff = true;
	private static final Object fakeGeneratorLock = new Object();
	static Map<LocalDate, HashMap<Integer, HashMap<LocalTime, LocalTime>>> dataBase = Collections
			.synchronizedMap(new HashMap<>());
	static MyLogger westmountServerLogger;

	static Map<String, Student> students = Collections.synchronizedMap(new HashMap<>());
	static Map<String, Admin> admins = Collections.synchronizedMap(new HashMap<>());
	private static DatagramSocket serverSocket;

	public static void save() throws Exception {
//		setStudentID("DVLS1234");
//		bookRoom("DVLS1234", 1, 1, LocalDate.of(2017, 1, 1), LocalTime.of(8, 0), LocalTime.of(9, 0));
		SaverLoader saverLoader = new SaverLoader();
		saverLoader.copyServerToObject();
		saverLoader.printCurrentDatabase();
		try {
			saverLoader.serializeDataOut();
			System.out.println("Saved");
		} catch (IOException e) {
			System.out.println("ERROR IN SERIALIZING");
		}
	}
	
	public static void load() throws Exception{
		SaverLoader saverLoader = null;
		try {
			saverLoader = SaverLoader.serializeDataIn();
			saverLoader.printCurrentDatabase();
			saverLoader.copyObjectToServer();
			System.out.println("Loaded");
		} catch (ClassNotFoundException|IOException e) {
			System.out.println("ERROR IN LOADING");
		}
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
		
		gson = StartGson.initGsonFarid();
		Thread udpThread = new Thread(new Udp(args));
		udpThread.start();
	}
	
	public static byte[] sendMessageServerToserver(String stringToSend, String studentID){
		ServerToServerMessage message = new ServerToServerMessage(stringToSend, studentID);
		return gson.toJson(message).getBytes();
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

	/**
	 * @return the isFakeGeneratorOff
	 */
	public static boolean isFakeGeneratorOff() {
		synchronized (fakeGeneratorLock) {
			return isFakeGeneratorOff;	
		}
	}

	/**
	 * @param isFakeGeneratorOff the isFakeGeneratorOff to set
	 */
	public static void setFakeGeneratorOff(boolean isFakeGeneratorOff) {
		synchronized (fakeGeneratorLock) {
			ServerWestmount.isFakeGeneratorOff = isFakeGeneratorOff;
		}
	}
}