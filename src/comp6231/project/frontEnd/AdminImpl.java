package comp6231.project.frontEnd;

import comp6231.project.common.corba.users.AdminOperationsPOA;
import comp6231.project.frontEnd.messages.FECreateRoomRequestMessage;
import comp6231.project.frontEnd.udp.MultiCastRUDPSender;
import comp6231.project.mostafa.core.Constants;

public class AdminImpl extends AdminOperationsPOA {

	@Override
	public String createRoom(String user_id, int room_number, String date,
			String[] time_slots) {
		 FECreateRoomRequestMessage message = new FECreateRoomRequestMessage(1, user_id, room_number, date, time_slots);
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
	public String deleteRoom(String user_id, int room_number, String date,
			String[] time_slots) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean adminLogin(String adminID) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void signOut(String ID) {
		// TODO Auto-generated method stub
		
	}

}
