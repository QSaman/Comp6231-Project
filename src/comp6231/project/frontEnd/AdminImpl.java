package comp6231.project.frontEnd;

import comp6231.project.common.corba.users.AdminOperationsPOA;
import comp6231.project.frontEnd.messages.FECreateRoomRequestMessage;
import comp6231.project.frontEnd.messages.FEDeleteRoomRequestMessage;
import comp6231.project.frontEnd.messages.FELoginAdminMessage;
import comp6231.project.frontEnd.messages.FESignOutMessage;
import comp6231.project.frontEnd.udp.ErrorHandler;
import comp6231.project.frontEnd.udp.Sequencer;
import comp6231.project.replicaManager.messages.RMKillMessage;

public class AdminImpl extends AdminOperationsPOA {

	@Override
	public String createRoom(String user_id, int room_number, String date,
			String[] time_slots) {
		FE.log("CREATE ROOM ");
		 FECreateRoomRequestMessage message = new FECreateRoomRequestMessage(1, user_id, room_number, date, time_slots);
		 Sequencer thread = new Sequencer (message, FEUtility.getInstance().findUDPListenerPort(user_id));
		 thread.start();
		 try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return thread.getResult();
	}

	@Override
	public String deleteRoom(String user_id, int room_number, String date,
			String[] time_slots) {
		FE.log("Delete ROOM");
		FEDeleteRoomRequestMessage message = new FEDeleteRoomRequestMessage(1, user_id, room_number, date, time_slots);
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
	public boolean adminLogin(String adminID) {
		FE.log("Admin login");
		FELoginAdminMessage message = new FELoginAdminMessage(1, adminID);
		Sequencer thread = new Sequencer (message, FEUtility.getInstance().findUDPListenerPort(adminID));
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
		FE.log("Admin signout");
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
