package comp6231.project.frontEnd;

import comp6231.project.common.corba.users.AdminOperationsPOA;
import comp6231.project.frontEnd.messages.FECreateRoomRequestMessage;
import comp6231.project.frontEnd.messages.FEDeleteRoomRequestMessage;
import comp6231.project.frontEnd.messages.FELoginAdminMessage;
import comp6231.project.frontEnd.messages.FELoginStudentMessage;
import comp6231.project.frontEnd.messages.FESignOutMessage;
import comp6231.project.frontEnd.udp.MultiCastRUDPSender;

public class AdminImpl extends AdminOperationsPOA {

	@Override
	public String createRoom(String user_id, int room_number, String date,
			String[] time_slots) {
		FE.log("CREATE ROOM ");
		 FECreateRoomRequestMessage message = new FECreateRoomRequestMessage(1, user_id, room_number, date, time_slots);
		 MultiCastRUDPSender thread = new MultiCastRUDPSender (message, FEUtility.getInstance().findFaridUDPListenerPort(user_id), "");
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
	public String deleteRoom(String user_id, int room_number, String date,
			String[] time_slots) {
		FE.log("Delete ROOM");
		FEDeleteRoomRequestMessage message = new FEDeleteRoomRequestMessage(1, user_id, room_number, date, time_slots);
		MultiCastRUDPSender thread = new MultiCastRUDPSender (message, FEUtility.getInstance().findFaridUDPListenerPort(user_id), "");
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
	public boolean adminLogin(String adminID) {
		FE.log("Admin login");
		FELoginAdminMessage message = new FELoginAdminMessage(1, adminID);
		MultiCastRUDPSender thread = new MultiCastRUDPSender (message, FEUtility.getInstance().findFaridUDPListenerPort(adminID), "");
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return thread.getResult().equals("True")?true:false;
	}

	@Override
	public void signOut(String ID) {
		FE.log("Admin signout");
		FESignOutMessage message = new FESignOutMessage(1, ID);
		MultiCastRUDPSender thread = new MultiCastRUDPSender (message, FEUtility.getInstance().findFaridUDPListenerPort(ID), "");
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}

}
