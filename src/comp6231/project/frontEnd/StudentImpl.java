package comp6231.project.frontEnd;

import comp6231.project.common.corba.users.StudentOperationsPOA;
import comp6231.project.frontEnd.messages.FEBookRoomRequestMessage;
import comp6231.project.frontEnd.messages.FECancelBookingMessage;
import comp6231.project.frontEnd.messages.FEChangeReservationMessage;
import comp6231.project.frontEnd.messages.FEGetAvailableTimeSlotMessage;
import comp6231.project.frontEnd.udp.MultiCastRUDPSender;

public class StudentImpl extends StudentOperationsPOA {

	@Override
	public String bookRoom(String user_id, String campus_name, int room_number,
			String date, String time_slot) {
		FE.log("Book ROOM"+ Thread.currentThread().getId()+ " Time: " + time_slot);
		FEBookRoomRequestMessage message = new FEBookRoomRequestMessage(1, user_id, campus_name, room_number, date, time_slot);
		MultiCastRUDPSender thread = new MultiCastRUDPSender (message, FEUtility.getInstance().findMostafaUDPListenerPort(user_id), "");
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return thread.getResult();
	}
 
	@Override
	public String getAvailableTimeSlot(String user_id, String date) {
		FE.log("getAvailableTimeSlot");
		FEGetAvailableTimeSlotMessage message = new FEGetAvailableTimeSlotMessage(1, user_id, date);
		MultiCastRUDPSender thread = new MultiCastRUDPSender (message, FEUtility.getInstance().findMostafaUDPListenerPort(user_id), "");
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return thread.getResult();
	}

	@Override
	public String cancelBooking(String user_id, String bookingID) {
		FE.log("cancelBooking");
		FECancelBookingMessage message = new FECancelBookingMessage(1, user_id, bookingID);
		MultiCastRUDPSender thread = new MultiCastRUDPSender (message, FEUtility.getInstance().findMostafaUDPListenerPort(user_id), "");
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return thread.getResult();
	}

	@Override
	public String changeReservation(String user_id, String booking_id,
			String new_campus_name, int new_room_number, String new_date,
			String new_time_slot) {
		FE.log("changeReservation");
		FEChangeReservationMessage message = new FEChangeReservationMessage(1, user_id, booking_id, new_campus_name, new_room_number, new_date, new_time_slot);
		MultiCastRUDPSender thread = new MultiCastRUDPSender (message, FEUtility.getInstance().findMostafaUDPListenerPort(user_id), "");
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return thread.getResult();
	}

	@Override
	public boolean studentLogin(String studentID) {
		FE.log("Studnet login");
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void signOut(String ID) {
		FE.log("Student signout");
		// TODO Auto-generated method stub

	}

}
