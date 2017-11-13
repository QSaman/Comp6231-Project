package comp6231.project.farid.sharedPackage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;

public class Create extends MessageHeader {
	
	String adminId;
	int roomNumber;
	LocalDate date;
	LinkedHashMap<LocalTime, LocalTime> listOfTimeSlots;
	
	public Create(String header, String adminId, int roomNumber, LocalDate date,
			LinkedHashMap<LocalTime, LocalTime> listOfTimeSlots) {
		super(header);
		this.adminId = adminId;
		this.roomNumber = roomNumber;
		this.date = date;
		this.listOfTimeSlots = listOfTimeSlots;
	}
	

}
