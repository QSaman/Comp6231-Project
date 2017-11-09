/**
 * 
 */
package comp6231.project.saman.users;

import java.util.ArrayList;

import comp6231.project.saman.common.DateReservation;
import comp6231.project.saman.common.TimeSlot;

/**
 * @author saman
 *
 */
public interface AdminInterface {
	
	public boolean createRoom(String user_id, int room_number, DateReservation date, ArrayList<TimeSlot> time_slots);
	public boolean deleteRoom(String user_id, int room_number, DateReservation date, ArrayList<TimeSlot> time_slots);
	public boolean startWeek(String user_id);
	public void testMethod();

}
