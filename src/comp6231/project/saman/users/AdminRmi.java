package comp6231.project.saman.users;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;

import comp6231.project.saman.common.DateReservation;
import comp6231.project.saman.common.TimeSlot;
import comp6231.project.saman.common.users.AdminOperations;

public class AdminRmi implements AdminInterface {
	AdminOperations remote_stub;

	public AdminRmi(AdminOperations remote_stub) {
		this.remote_stub = remote_stub;
	}

	@Override
	public boolean createRoom(String user_id, int room_number, DateReservation date, ArrayList<TimeSlot> time_slots) {
		try {
			return remote_stub.createRoom(user_id, room_number, date, time_slots);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deleteRoom(String user_id, int room_number, DateReservation date, ArrayList<TimeSlot> time_slots) {
		try {
			return remote_stub.deleteRoom(user_id, room_number, date, time_slots);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean startWeek(String user_id) {
		try {
			return remote_stub.startWeek(user_id);
		} catch (NotBoundException | IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void testMethod() {
		try {
			remote_stub.testMethod();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
