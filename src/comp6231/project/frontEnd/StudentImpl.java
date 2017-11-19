package comp6231.project.frontEnd;

import comp6231.project.common.corba.users.StudentOperationsPOA;
import comp6231.project.frontEnd.messages.FEBookRoomRequestMessage;
import comp6231.project.frontEnd.udp.MultiCastRUDPSender;
import comp6231.project.mostafa.core.Constants;

public class StudentImpl extends StudentOperationsPOA {

	@Override
	public String bookRoom(String user_id, String campus_name, int room_number,
			String date, String time_slot) {
		 FEBookRoomRequestMessage message = new FEBookRoomRequestMessage(1, user_id, campus_name, room_number, date, time_slot);
		 MultiCastRUDPSender thread = new MultiCastRUDPSender (message, Constants.DVL_PORT_LISTEN, "DVL");
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String cancelBooking(String user_id, String bookingID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String changeReservation(String user_id, String booking_id,
			String new_campus_name, int new_room_number, String new_date,
			String new_time_slot) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean studentLogin(String studentID) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void signOut(String ID) {
		// TODO Auto-generated method stub
		
	}

}
