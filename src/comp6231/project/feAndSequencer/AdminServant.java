package comp6231.project.feAndSequencer;

import org.omg.CORBA.ORB;

import comp6231.project.common.corba.users.AdminOperationsPOA;

public class AdminServant extends AdminOperationsPOA {
	
	private ORB orb = null;

	public void setORB(ORB orb) {
		this.orb = orb;
	}

	@Override
	public String createRoom(String user_id, int room_number, String date, String[] time_slots) {
		System.out.println("createRoom invoked.");
		return "createRoom invoked.";
	}

	@Override
	public String deleteRoom(String user_id, int room_number, String date, String[] time_slots) {
		System.out.println("deleteRoom invoked.");
		return "deleteRoom invoked.";
	}

	@Override
	public boolean adminLogin(String adminID) {
		System.out.println("adminLogin invoked.");
		return true;
	}

	@Override
	public void signOut(String ID) {
		System.out.println("signOut invoked.");
		
	}

}
