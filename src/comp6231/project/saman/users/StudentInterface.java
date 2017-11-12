package comp6231.project.saman.users;

import java.util.ArrayList;

import comp6231.project.saman.common.DateReservation;
import comp6231.project.saman.common.LoggerHelper;
import comp6231.project.saman.common.TimeSlot;
import comp6231.project.saman.common.TimeSlotResult;

public interface StudentInterface {
	public String bookRoom(String user_id, String campus_name, int room_number, DateReservation date, TimeSlot time_slot);
	public ArrayList<TimeSlotResult> getAvailableTimeSlot(String user_id, DateReservation date);
	public boolean cancelBooking(String user_id, String bookingID);
	public String changeReservation(String user_id, String booking_id, String new_campus_name, int new_room_number, DateReservation new_date, TimeSlot new_time_slot);

}
