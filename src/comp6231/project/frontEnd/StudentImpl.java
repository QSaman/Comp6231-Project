package comp6231.project.frontEnd;

import comp6231.project.common.corba.users.StudentOperationsPOA;
import comp6231.project.frontEnd.messages.FEBookRoomRequestMessage;
import comp6231.project.frontEnd.messages.FECancelBookingMessage;
import comp6231.project.frontEnd.messages.FEChangeReservationMessage;
import comp6231.project.frontEnd.messages.FEGetAvailableTimeSlotMessage;
import comp6231.project.frontEnd.messages.FELoginStudentMessage;
import comp6231.project.frontEnd.messages.FESignOutMessage;
import comp6231.project.frontEnd.udp.ErrorHandler;
import comp6231.project.frontEnd.udp.Sequencer;
import comp6231.project.replicaManager.messages.RMKillMessage;

public class StudentImpl extends StudentOperationsPOA {

	@Override
	public String bookRoom(String user_id, String campus_name, int room_number,
			String date, String time_slot) {
		FE.log("Book ROOM"+ Thread.currentThread().getId()+ " Time: " + time_slot);
		FEBookRoomRequestMessage message = new FEBookRoomRequestMessage(1, user_id, campus_name, room_number, date, time_slot);
		Sequencer thread = new Sequencer (message, FEUtility.getInstance().findUDPListenerPort(user_id));
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
		Sequencer thread = new Sequencer (message, FEUtility.getInstance().findUDPListenerPort(user_id));
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
		Sequencer thread = new Sequencer (message, FEUtility.getInstance().findUDPListenerPort(user_id));
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
		Sequencer thread = new Sequencer (message, FEUtility.getInstance().findUDPListenerPort(user_id));
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
		FELoginStudentMessage message = new FELoginStudentMessage(1, studentID);
		Sequencer thread = new Sequencer (message, FEUtility.getInstance().findUDPListenerPort(studentID));
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(thread.returnStatus == ReturnStatus.CantLogin){
			return false;
		}else{
			return true;
		}
	}

	@Override
	public void signOut(String ID) {
		FE.log("Student signout");
		FESignOutMessage message = new FESignOutMessage(1, ID);
		Sequencer thread = new Sequencer (message, FEUtility.getInstance().findUDPListenerPort(ID));
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void killServer(String campusName) {
		PortSwitcher.switchServer(campusName);
		RMKillMessage message = new RMKillMessage(-1, campusName);
		new Thread((new ErrorHandler(FEUtility.getInstance().findRMPort(campusName), message))).start();
	}

	@Override
	public void fakeGenerate(String replicaName, String campusName) {
		new Thread(new ErrorHandler(replicaName, false)).start();
		
	}

}
