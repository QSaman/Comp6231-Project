package assignment.serverDorval;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;

import org.omg.CORBA.ORB;

public class Servant extends ServerImplementation.ServerInterfacePOA {
	private ORB orb = null;

	public void setORB(ORB orb) {
		this.orb = orb;
	}

	@Override
	public String createRoom(String adminID, int roomNumber, String date, String listOfTimeSlots) {
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7));
		int day = Integer.parseInt(date.substring(8, 10));
		LocalDate dateToCreate = LocalDate.of(year, month, day);
		LinkedHashMap<LocalTime, LocalTime> time = new LinkedHashMap<>();
		LocalTime startTime = LocalTime.of(Integer.parseInt(listOfTimeSlots.substring(0, 2)),
				Integer.parseInt(listOfTimeSlots.substring(3, 5)));
		LocalTime endTime = LocalTime.of(Integer.parseInt(listOfTimeSlots.substring(5, 7)),
				Integer.parseInt(listOfTimeSlots.substring(8).trim()));
		time.put(startTime, endTime);
		String result = null;

		try {
			result = ServerDorval.createRoom(adminID, roomNumber, dateToCreate, time);
		} catch (Exception ignored) {
		}

		return result;
	}

	@Override
	public String deleteRoom(String adminID, int roomNumber, String date, String listOfTimeSlots) {
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7));
		int day = Integer.parseInt(date.substring(8, 10));
		LocalDate dateToCreate = LocalDate.of(year, month, day);
		LinkedHashMap<LocalTime, LocalTime> time = new LinkedHashMap<>();
		LocalTime startTime = LocalTime.of(Integer.parseInt(listOfTimeSlots.substring(0, 2)),
				Integer.parseInt(listOfTimeSlots.substring(3, 5)));
		LocalTime endTime = LocalTime.of(Integer.parseInt(listOfTimeSlots.substring(5, 7)),
				Integer.parseInt(listOfTimeSlots.substring(8).trim()));
		time.put(startTime, endTime);
		String result = null;

		try {
			result = ServerDorval.deleteRoom(adminID, roomNumber, dateToCreate, time);
		} catch (Exception ignored) {
		}

		return result;
	}

	@Override
	public String bookRoom(String studentID, int campus, int roomNumber, String date, String startTime,
			String endTime) {
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7));
		int day = Integer.parseInt(date.substring(8, 10));
		LocalDate dateTo = LocalDate.of(year, month, day);
		LocalTime startTimeToBook = LocalTime.of(Integer.parseInt(startTime.substring(0, 2)),
				Integer.parseInt(startTime.substring(3).trim()));
		LocalTime endTimeToBook = LocalTime.of(Integer.parseInt(endTime.substring(0, 2)),
				Integer.parseInt(endTime.substring(3).trim()));
		String result = null;

		try {
			result = ServerDorval.bookRoom(studentID, campus, roomNumber, dateTo, startTimeToBook, endTimeToBook);
		} catch (Exception ignored) {
		}

		return result;
	}

	@Override
	public String getAvailableTimeSlot(String studentID, String date) {
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7));
		int day = Integer.parseInt(date.substring(8, 10));
		LocalDate dateTo = LocalDate.of(year, month, day);
		String result = null;

		try {
			result = ServerDorval.getAvailableTimeSlot(studentID, dateTo);
		} catch (Exception ignored) {
		}

		return result;
	}

	@Override
	public String cancelBooking(String studentID, String bookingID) {
		String result = null;

		try {
			result = ServerDorval.cancelBooking(studentID, bookingID);
		} catch (Exception ignored) {
		}

		return result;
	}

	@Override
	public String changeReservation(String studentID, String bookingID, int campus, int roomNumber, String startTime,
			String endTime) {
		String result = null;
		LocalTime startTimeTo = LocalTime.of(Integer.parseInt(startTime.substring(0, 2)),
				Integer.parseInt(startTime.substring(3).trim()));
		LocalTime endTimeTo = LocalTime.of(Integer.parseInt(endTime.substring(0, 2)),
				Integer.parseInt(endTime.substring(3).trim()));

		try {
			result = ServerDorval.changeReservation(studentID, bookingID, campus, roomNumber, startTimeTo, endTimeTo);
		} catch (Exception ignored) {
		}

		return result;
	}

	@Override
	public boolean setAdminID(String adminID) {
		try {
			return ServerDorval.setAdminID(adminID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean setStudentID(String studentID) {
		try {
			return ServerDorval.setStudentID(studentID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void signOut(String ID) {
		try {
			ServerDorval.signOut(ID);
		} catch (Exception e) {
		}
	}

}
