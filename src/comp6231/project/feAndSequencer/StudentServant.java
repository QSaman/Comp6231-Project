package comp6231.project.feAndSequencer;

import org.omg.CORBA.ORB;

import comp6231.project.common.corba.users.StudentOperationsPOA;

public class StudentServant extends StudentOperationsPOA {

	private ORB orb = null;

	public void setORB(ORB orb) {
		this.orb = orb;
	}

	@Override
	public String bookRoom(String user_id, String campus_name, int room_number, String date, String time_slot) {
		System.out.println("bookRoom invoked.");
		return "bookRoom invoked.";
	}

	@Override
	public String getAvailableTimeSlot(String user_id, String date) {
		System.out.println("getAvailableTimeSlot invoked.");
		return "getAvailableTimeSlot invoked.";
	}

	@Override
	public String cancelBooking(String user_id, String bookingID) {
		System.out.println("cancelBooking invoked.");
		return "cancelBooking invoked.";
	}

	@Override
	public String changeReservation(String user_id, String booking_id, String new_campus_name, int new_room_number,
			String new_date, String new_time_slot) {
		System.out.println("changeReservation invoked.");
		return "changeReservation invoked.";
	}

	@Override
	public boolean studentLogin(String studentID) {
		System.out.println("studentLogin invoked.");
		return true;
	}

	@Override
	public void signOut(String ID) {
		System.out.println("signOut invoked.");
	}
}
