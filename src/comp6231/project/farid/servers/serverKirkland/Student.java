package comp6231.project.farid.servers.serverKirkland;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Random;

import comp6231.project.farid.sharedPackage.UdpSender;
import comp6231.shared.Constants;

public class Student implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8707770281988510290L;
	private String studentID;

	public boolean setStudentID(String studentID) {

		this.studentID = studentID;
		ServerKirkland.kirklandServerLogger.log("\nStudent " + studentID + " signed in.\n");
		return true;
	}

	public void signOut() {
		ServerKirkland.kirklandServerLogger.log("\nStudent " + studentID + " signed out.\n");
	}

	public String bookRoom(int roomNumber, LocalDate date, LocalTime startTime, LocalTime endTime) throws Exception {
		StringBuilder resultToSendToStudent = new StringBuilder();
		resultToSendToStudent.append("\n%%% REQUEST STARTED - Result of booking room ").append(roomNumber)
				.append(" for the date ").append(date).append(" and time slot: ").append(startTime).append(" / ")
				.append(endTime).append(", by student ").append(studentID);

		synchronized (Locker.counterLock) {
			if (!ReserveManager.counterDB.containsKey(studentID)
					|| (ReserveManager.getBestExpireDateFromServers(studentID).isBefore(LocalDate.now()))
					|| (ReserveManager.counterDB.containsKey(studentID)
							&& ReserveManager.counterDB.get(studentID).getCounter() <= 2)) {

				String newBookingID = getNewBookingID();
				try {
					synchronized (Locker.databaseLock) {
						if (ServerKirkland.dataBase.get(date).get(roomNumber).containsKey(startTime)
								&& ServerKirkland.dataBase.get(date).get(roomNumber).get(startTime).equals(endTime)) {

							ServerKirkland.dataBase.get(date).get(roomNumber).remove(startTime);
							new ReserveManager(roomNumber, studentID, newBookingID, date, startTime, endTime,
									LocalDate.now());
							resultToSendToStudent.append("\nThe room ").append(roomNumber).append(" for date ")
									.append(date).append(" for time slot ").append(startTime).append(" / ")
									.append(endTime).append(" successfully booked.").append("\nBooking ID is: ")
									.append(newBookingID);
							resultToSendToStudent.insert(0, newBookingID+Constants.DILIMITER_STRING);

															// .append("\n Number of booking
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
					e.printStackTrace();
					return "!!! No data found with this information !!!";
				}

				resultToSendToStudent.append("\n=== FINISHED - Result of booking room ").append(roomNumber)
						.append(" and time slot: ").append(startTime).append(" / ").append(endTime)
						.append(", by student ").append(studentID);

				ServerKirkland.kirklandServerLogger.log(resultToSendToStudent.toString().substring(
						resultToSendToStudent.toString().indexOf(Constants.DILIMITER_STRING+1)));
				ReserveManager.printCurrentReserveList();
				ServerKirkland.printCurrentDatabase();

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

		ServerKirkland.kirklandServerLogger.log(resultToSendToStudent.toString());
		ReserveManager.printCurrentReserveList();
		ServerKirkland.printCurrentDatabase();

		return resultToSendToStudent.toString();
	}

	private boolean isStudentBookingCounterWithAllServersOK() throws Exception {

        String stringToSend = "getCounter-" + studentID;
        byte [] sendData = ServerKirkland.sendMessageServerToserver(stringToSend,studentID);
        UdpSender sender = new UdpSender(sendData, Constants.dvlPortListenFaridActive, "");
        UdpSender sender2 = new UdpSender(sendData, Constants.wstPortListenFaridActive, "");
        sender.start();
        sender2.start();
        
        sender.join();
        sender2.join();
        
        String result = sender.getResult();
        String result2 = sender2.getResult();
        
		int total;
		synchronized (Locker.counterLock) {
			int counterOnKKL = (ReserveManager.counterDB.containsKey(studentID)
					? ReserveManager.counterDB.get(studentID).getCounter()
					: 0);
			int counterOnDVL = Integer.parseInt(result.trim());
			int counterOnWST = Integer.parseInt(result2.trim());
			total = counterOnDVL + counterOnKKL + counterOnWST;
		}

		return total <= 2;
	}

	public String bookRoom(int campus, int roomNumber, LocalDate date, LocalTime startTime, LocalTime endTime)
			throws Exception {

		if (isStudentBookingCounterWithAllServersOK()) {
			if (campus != 2) {
				int port = 0;
				if (campus == 1)
					port = Constants.dvlPortListenFaridActive;
				else if (campus == 3)
					port = Constants.wstPortListenFaridActive;

		        String stringToSend = "book-" + studentID + "@" + roomNumber + "%" + date + "#" + startTime + "*"
 						+ endTime;
		        byte [] sendData = ServerKirkland.sendMessageServerToserver(stringToSend,studentID);
		        UdpSender sender = new UdpSender(sendData, port, "");
		        sender.start();
		        
		        sender.join();
		        
		        String result = sender.getResult();

				return result;
			}

			return bookRoom(roomNumber, date, startTime, endTime);
		}
		return "\n!!! No permission to book more rooms.\n";
	}

	int kklNumber = 0;
	int dvlNumber = 0;
	int wstNumber = 0;

	String getAvailableTimeSlot(LocalDate date) throws Exception {
		StringBuilder resultToSendToStudent = new StringBuilder();
		resultToSendToStudent.append("\n%%% STARTED - Result of getting number of available times for the date ")
				.append(date).append(", by student ").append(studentID);

		int result;

		Thread threadWST = new Thread(() -> {
			try {
				wstNumber = Integer.parseInt(getNumberOfAvailableTimesFromOtherServer(Constants.wstPortListenFaridActive, date).trim());
			} catch (Exception e) {
				// e.printStackTrace();
			}
		});
		Thread threadKKL = new Thread(() -> {
			try {
				kklNumber = getAvailableTimeFromServerForDate(date);
			} catch (Exception e) {
				// e.printStackTrace();
			}
		});
		Thread threadDVL = new Thread(() -> {
			try {
				dvlNumber = Integer.parseInt(getNumberOfAvailableTimesFromOtherServer(Constants.dvlPortListenFaridActive, date).trim());
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
		ServerKirkland.kirklandServerLogger.log(resultToSendToStudent.toString());

		return resultToSendToStudent.toString();
	}

	public static int getAvailableTimeFromServerForDate(LocalDate date) {
		int result = 0;
		synchronized (Locker.databaseLock) {
			try {
				for (HashMap.Entry<Integer, HashMap<LocalTime, LocalTime>> entry : ServerKirkland.dataBase.get(date)
						.entrySet()) {
					result += entry.getValue().size();
				}
			} catch (Exception ignored) {
			}
		}
		return result;
	}

	private String getNumberOfAvailableTimesFromOtherServer(int port, LocalDate date) throws Exception {
		String sentence = date.toString();
		byte [] sendData = ServerKirkland.sendMessageServerToserver(sentence,studentID);

        UdpSender sender = new UdpSender(sendData, port, "");

        sender.start();
        
        sender.join();

        String result = sender.getResult();

		return result;
	}

	public String cancelBooking(String bookingID) throws Exception {
		if (bookingID.startsWith("KKL")) {
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
			ServerKirkland.kirklandServerLogger.log(resultToSendToStudent.toString());

			ReserveManager.printCurrentReserveList();
			ServerKirkland.printCurrentDatabase();
			return resultToSendToStudent.toString();
		} else {
			int port = 0;
			if (bookingID.startsWith("WST")) {
				port = Constants.wstPortListenFaridActive;
			} else if (bookingID.startsWith("DVL")) {
				port = Constants.dvlPortListenFaridActive;
			}

			String stringToSend = "cancel-" + studentID + "#" + bookingID;

			byte [] sendData = ServerKirkland.sendMessageServerToserver(stringToSend,studentID);

	        UdpSender sender = new UdpSender(sendData, port, "");

	        sender.start();
	        
	        sender.join();

	        String result = sender.getResult();

			return result;
		}
	}

	public String changeReservation(String studentID, String bookingID, int campus, int roomNumber, LocalTime startTime,
			LocalTime endTime) throws Exception {
		if (bookingID.startsWith("KKL")) {
			StringBuilder resultToSendToStudent = new StringBuilder();
			resultToSendToStudent.append("\n%%% STARTED - Result of changing time slot with booking ID ")
					.append(bookingID).append(".").append(", by student ").append(studentID);

			try {
				synchronized (Locker.reserveLock) {
					if (ReserveManager.reserveMap.containsKey(bookingID)
							&& ReserveManager.reserveMap.get(bookingID).getStudentID()
									.equals(studentID)) {
						ReserveManager.counterDB.get(studentID).decrementCounter();
						String bookResult = bookRoom(campus, roomNumber,
								ReserveManager.reserveMap.get(bookingID).getDate(), startTime,
								endTime);
						@SuppressWarnings("resource") 
						int index = bookResult.indexOf(Constants.DILIMITER_STRING);
						String bookingId = "";
						ReserveManager.counterDB.get(studentID).incrementCounter();
						if (index != -1) {
							bookingId = bookResult.substring(0, index);
							resultToSendToStudent.append(ReserveManager.removeFromReserveList(studentID, bookingID)
									? "\nThe booking number " + bookingID + " canceled successfully."
									: "\n The booking number " + bookingID + " failed canceling.");
							resultToSendToStudent.insert(0, bookingId+Constants.DILIMITER_STRING);
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
			ServerKirkland.kirklandServerLogger.log(resultToSendToStudent.toString());

			ReserveManager.printCurrentReserveList();
			ServerKirkland.printCurrentDatabase();
			return resultToSendToStudent.toString();
		} else {
			int port = 0;
			if (bookingID.startsWith("WST")) {
				port = Constants.wstPortListenFaridActive;
			} else if (bookingID.startsWith("DVL")) {
				port = Constants.dvlPortListenFaridActive;
			}

			String stringToSend = "chan-" + studentID + "@" + roomNumber + "%" + campus + "#" + startTime + "*"
					+ endTime + "&" + bookingID;
			
			byte [] sendData = ServerKirkland.sendMessageServerToserver(stringToSend,studentID);

	        UdpSender sender = new UdpSender(sendData, port, "");

	        sender.start();
	        
	        sender.join();

	        String result = sender.getResult();

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
		result = "KKL-" + stringBuilder.toString();
		synchronized (Locker.reserveLock) {
			return ReserveManager.reserveMap.containsKey(result) ? getNewBookingID() : result;
		}
	}
}
