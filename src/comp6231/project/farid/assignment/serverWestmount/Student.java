package assignment.serverWestmount;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Student {

	private String studentID;

	Student() throws RemoteException {
		super();
	}

	boolean setStudentID(String studentID) {

		this.studentID = studentID;
		ServerWestmount.westmountServerLogger.log("\nStudent " + studentID + " signed in.\n");
		return true;
	}

	public void signOut() {
		ServerWestmount.westmountServerLogger.log("\nStudent " + studentID + " signed out.\n");
	}

	String bookRoom(int roomNumber, LocalDate date, LocalTime startTime, LocalTime endTime) throws Exception {
		StringBuilder resultToSendToStudent = new StringBuilder();
		resultToSendToStudent.append("\n%%% REQUEST STARTED - Result of booking room ").append(roomNumber)
				.append(" for the date ").append(date).append(" and time slot: ").append(startTime).append(" / ")
				.append(endTime).append(", by student ").append(studentID);
		synchronized (Locker.counterLock) {
			if (!ReserveManager.counterDB.containsKey(studentID)
					|| ((ReserveManager.getBestExpireDateFromServers(studentID).isBefore(LocalDate.now())))
					|| (ReserveManager.counterDB.containsKey(studentID)
							&& ReserveManager.counterDB.get(studentID).getCounter() <= 2)) {

				String newBookingID = getNewBookingID();
				try {
					synchronized (Locker.databaseLock) {
						if (ServerWestmount.dataBase.get(date).get(roomNumber).containsKey(startTime)
								&& ServerWestmount.dataBase.get(date).get(roomNumber).get(startTime).equals(endTime)) {

							ServerWestmount.dataBase.get(date).get(roomNumber).remove(startTime);
							new ReserveManager(roomNumber, studentID, newBookingID, date, startTime, endTime,
									LocalDate.now());
							resultToSendToStudent.append("\nThe room ").append(roomNumber).append(" for date ")
									.append(date).append(" for time slot ").append(startTime).append(" / ")
									.append(endTime).append(" successfully booked.").append("\nBooking ID is: ")
									.append(newBookingID);// .append("\n Number of booking
															// ").append(ReserveManager.counterDB.get(studentID).getCounter()).append("
															// booking. Can book ").append(3 -
															// ReserveManager.counterDB.get(studentID).getCounter()).append("
															// more rooms until the date
															// ").append(ReserveManager.counterDB.get(studentID).getExpireDate()).append(".");
						} else {
							resultToSendToStudent.append("\nThe request for booking room ").append(roomNumber)
									.append(" for date ").append(date).append(" for time slot ").append(startTime)
									.append(" / ").append(endTime).append(" failed.");
						}
					}
				} catch (Exception e) {
					// e.printStackTrace();
					return "!!! No data found with this information !!!";
				}

				resultToSendToStudent.append("\n=== FINISHED - Result of booking room ").append(roomNumber)
						.append(" and time slot: ").append(startTime).append(" / ").append(endTime)
						.append(", by student ").append(studentID);

				ServerWestmount.westmountServerLogger.log(resultToSendToStudent.toString());
				ReserveManager.printCurrentReserveList();
				ServerWestmount.printCurrentDatabase();

				return resultToSendToStudent.toString();
			} else {
				resultToSendToStudent.append("\nThe request for booking room ").append(roomNumber).append(" for date ")
						.append(date).append(" for time slot ").append(startTime).append(" / ").append(endTime)
						.append(" failed. \nNo permission to book more rooms.");
				resultToSendToStudent.append("\n=== FINISHED - Result of booking room ").append(roomNumber)
						.append(" and time slot: ").append(startTime).append(" / ").append(endTime)
						.append(", by student ").append(studentID);
			}
		}

		ServerWestmount.westmountServerLogger.log(resultToSendToStudent.toString());
		ReserveManager.printCurrentReserveList();
		ServerWestmount.printCurrentDatabase();

		return resultToSendToStudent.toString();
	}

	private boolean isStudentBookingCounterWithAllServersOK() throws Exception {

		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("localhost");
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];

		String stringToSend = "getCounter-" + studentID;
		sendData = stringToSend.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9867);
		clientSocket.send(sendPacket);

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		String result = new String(receivePacket.getData());

		clientSocket.close();

		DatagramSocket clientSocket2 = new DatagramSocket();
		InetAddress IPAddress2 = InetAddress.getByName("localhost");
		byte[] sendData2 = new byte[1024];
		byte[] receiveData2 = new byte[1024];

		String stringToSend2 = "getCounter-" + studentID;
		sendData2 = stringToSend2.getBytes();
		DatagramPacket sendPacket2 = new DatagramPacket(sendData2, sendData2.length, IPAddress2, 9763);
		clientSocket2.send(sendPacket2);

		DatagramPacket receivePacket2 = new DatagramPacket(receiveData2, receiveData2.length);
		clientSocket2.receive(receivePacket2);
		String result2 = new String(receivePacket2.getData());

		clientSocket2.close();
		int total;
		synchronized (Locker.counterLock) {
			int counterOnWST = (ReserveManager.counterDB.containsKey(studentID)
					? ReserveManager.counterDB.get(studentID).getCounter()
					: 0);
			int counterOnKKL = Integer.parseInt(result.trim());
			int counterOnDVL = Integer.parseInt(result2.trim());
			total = counterOnDVL + counterOnKKL + counterOnWST;
		}

		return total <= 2;
	}

	public String bookRoom(int campus, int roomNumber, LocalDate date, LocalTime startTime, LocalTime endTime)
			throws Exception {

		if (isStudentBookingCounterWithAllServersOK()) {
			if (campus != 3) {
				int port = 0;
				if (campus == 2)
					port = 9867;
				else if (campus == 1)
					port = 9763;

				DatagramSocket clientSocket = new DatagramSocket();
				InetAddress IPAddress = InetAddress.getByName("localhost");
				byte[] sendData = new byte[1024];
				byte[] receiveData = new byte[1024];

				String stringToSend = "book-" + studentID + "@" + roomNumber + "%" + date + "#" + startTime + "*"
						+ endTime;
				sendData = stringToSend.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
				clientSocket.send(sendPacket);

				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);
				String result = new String(receivePacket.getData());

				clientSocket.close();

				return result;
			}

			return bookRoom(roomNumber, date, startTime, endTime);
		}
		return "\n!!! No permission to book more rooms.\n";
	}

	int kklNumber = 0;
	int wstNumber = 0;
	int dvlNumber = 0;

	String getAvailableTimeSlot(LocalDate date) throws Exception {
		StringBuilder resultToSendToStudent = new StringBuilder();
		resultToSendToStudent.append("\n%%% STARTED - Result of getting number of available times for the date ")
				.append(date).append(", by student ").append(studentID);

		int result;
		
		Thread threadWST = new Thread(() -> {
			wstNumber = getAvailableTimeFromServerForDate(date);
		});
		Thread threadKKL = new Thread(() -> {
			try {
				kklNumber = Integer.parseInt(getNumberOfAvailableTimesFromOtherServer(9867, date).trim());
			} catch (Exception e) {
				// e.printStackTrace();
			}
		});
		Thread threadDVL = new Thread(() -> {
			try {
				dvlNumber = Integer.parseInt(getNumberOfAvailableTimesFromOtherServer(9763, date).trim());
			} catch (Exception e) {
				// e.printStackTrace();
			}
		});
		
		threadDVL.start();
		threadKKL.start();
		threadWST.start();
		threadDVL.join();
		threadKKL.join();
		threadWST.join();
		
		result = wstNumber + kklNumber + dvlNumber;
		resultToSendToStudent.append("\nThe number of available times is: ").append(result).append("\nWestmount: ")
				.append(wstNumber).append(", Kirkland: ").append(kklNumber).append(", Dorval: ").append(dvlNumber);

		resultToSendToStudent.append("\n=== FINISHED - Result of getting number of available times for the date ")
				.append(date).append(".").append(", by student ").append(studentID);
		ServerWestmount.westmountServerLogger.log(resultToSendToStudent.toString());

		return resultToSendToStudent.toString();
	}

	static int getAvailableTimeFromServerForDate(LocalDate date) {
		int result = 0;
		synchronized (Locker.databaseLock) {
			try {
				for (HashMap.Entry<Integer, HashMap<LocalTime, LocalTime>> entry : ServerWestmount.dataBase.get(date)
						.entrySet()) {
					result += entry.getValue().size();
				}
			} catch (Exception ignored) {
			}
		}
		return result;
	}

	private String getNumberOfAvailableTimesFromOtherServer(int port, LocalDate date) throws Exception {
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName("localhost");
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		String sentence = date.toString();
		sendData = sentence.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
		clientSocket.send(sendPacket);

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		clientSocket.receive(receivePacket);
		String result = new String(receivePacket.getData());

		clientSocket.close();

		return result;
	}

	public String cancelBooking(String bookingID) throws Exception {
		if (bookingID.startsWith("WST")) {
			StringBuilder resultToSendToStudent = new StringBuilder();
			resultToSendToStudent.append("\n%%% STARTED - Result of canceling booking with ID ").append(bookingID)
					.append(".").append(", by student ").append(studentID);

			try {
				resultToSendToStudent.append(ReserveManager.removeFromReserveList(studentID, bookingID)
						? "\nThe booking number " + bookingID + " canceled successfully."
						: "\n The booking number " + bookingID + " failed canceling.");
				resultToSendToStudent.append("\n=== FINISHED - Result of canceling booking with ID ").append(bookingID)
						.append(".").append(", by student ").append(studentID);
			} catch (Exception e) {
				return "!!! No data found with this information !!!";
			}
			ServerWestmount.westmountServerLogger.log(resultToSendToStudent.toString());

			ReserveManager.printCurrentReserveList();
			ServerWestmount.printCurrentDatabase();
			return resultToSendToStudent.toString();
		} else {
			int port = 0;
			if (bookingID.startsWith("DVL")) {
				port = 9763;
			} else if (bookingID.startsWith("KKL")) {
				port = 9867;
			}

			DatagramSocket clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName("localhost");
			byte[] sendData = new byte[1024];
			byte[] receiveData = new byte[1024];

			String stringToSend = "cancel-" + studentID + "#" + bookingID;
			sendData = stringToSend.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			clientSocket.send(sendPacket);

			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			String result = new String(receivePacket.getData());

			clientSocket.close();

			return result;
		}
	}

	public String changeReservation(String studentID, String bookingID, int campus, int roomNumber, LocalTime startTime,
			LocalTime endTime) throws Exception {
		if (bookingID.startsWith("WST")) {
			StringBuilder resultToSendToStudent = new StringBuilder();
			resultToSendToStudent.append("\n%%% STARTED - Result of changing time slot with booking ID ")
					.append(bookingID).append(".").append(", by student ").append(studentID);

			try {
				synchronized (Locker.reserveLock) {
					if (ReserveManager.reserveMap.containsKey(bookingID)
							&& ReserveManager.reserveMap.get(bookingID).getStudentID().equals(studentID)) {
						ReserveManager.counterDB.get(studentID).decrementCounter();
						String bookResult = bookRoom(campus, roomNumber,
								ReserveManager.reserveMap.get(bookingID).getDate(), startTime, endTime);
						Scanner scanner = new Scanner(bookResult);
						scanner.nextLine();
						scanner.nextLine();
						String tempResultOfBook = scanner.nextLine();
						ReserveManager.counterDB.get(studentID).incrementCounter();
						if (tempResultOfBook.substring(tempResultOfBook.indexOf("/")).charAt(8) == 's') {
							resultToSendToStudent.append(ReserveManager.removeFromReserveList(studentID, bookingID)
									? "\nThe booking number " + bookingID + " canceled successfully."
									: "\n The booking number " + bookingID + " failed canceling.");

						}
						resultToSendToStudent.append(bookResult);
					}
				}
				resultToSendToStudent.append("\n=== FINISHED - Result of changing time slot with booking ID ")
						.append(bookingID).append(".").append(", by student ").append(studentID);
			} catch (Exception e) {
				e.printStackTrace();
				return "!!! No data found with this information !!!";
			}
			ServerWestmount.westmountServerLogger.log(resultToSendToStudent.toString());

			ReserveManager.printCurrentReserveList();
			ServerWestmount.printCurrentDatabase();
			return resultToSendToStudent.toString();
		} else {
			int port = 0;
			if (bookingID.startsWith("DVL")) {
				port = 9763;
			} else if (bookingID.startsWith("KKL")) {
				port = 9867;
			}

			DatagramSocket clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName("localhost");
			byte[] sendData = new byte[1024];
			byte[] receiveData = new byte[1024];

			String stringToSend = "chan-" + studentID + "@" + roomNumber + "%" + campus + "#" + startTime + "*"
					+ endTime + "&" + bookingID;
			sendData = stringToSend.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
			clientSocket.send(sendPacket);

			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			String result = new String(receivePacket.getData());

			clientSocket.close();

			return result;
		}

		// return "NOT IMPLEMENTED YET.";
	}

	private String getNewBookingID() {
		String stringRange = "012345ABCDEFGHIjklmnopqrstuvwxyz";
		int length = stringRange.length();
		Random random = new Random();
		String result;
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < length; i++) {

			stringBuilder.append(stringRange.charAt(random.nextInt(length)));

		}
		result = "WST-" + stringBuilder.toString();
		synchronized (Locker.reserveLock) {
			return ReserveManager.reserveMap.containsKey(result) ? getNewBookingID() : result;
		}
	}
}
