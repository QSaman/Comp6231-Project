/**
 * 
 */
package comp6231.project.saman.common.users;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import comp6231.project.saman.common.DateReservation;
import comp6231.project.saman.common.TimeSlot;
import comp6231.project.saman.common.TimeSlotResult;

/**
 * @author saman
 *
 */
public interface StudentOperations extends Remote {
	String bookRoom(String user_id, String campus_name, int room_number, DateReservation date, TimeSlot time_slot) throws RemoteException, NotBoundException, IOException, InterruptedException;
	ArrayList<TimeSlotResult> getAvailableTimeSlot(DateReservation date) throws RemoteException, NotBoundException, IOException, InterruptedException;
	boolean cancelBooking(String user_id, String bookingID) throws RemoteException, NotBoundException, IOException, InterruptedException;
	public String changeReservation(String user_id, String booking_id, String new_campus_name, int new_room_number, DateReservation new_date, TimeSlot new_time_slot) throws NotBoundException, IOException, InterruptedException, RemoteException;
}