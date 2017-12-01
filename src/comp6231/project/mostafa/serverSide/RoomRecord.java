package comp6231.project.mostafa.serverSide;

import java.io.Serializable;

public class RoomRecord implements Serializable {
	
	private static int idGenerator = 00000;
	private static final Object idGeneratorLock = new Object();
	private static final Object bookedStudentIdLock = new Object();
	
	private String bookingId;
	private String bookedStudentId;
	private	String id;
	private int endTime;
	
	public RoomRecord(int endTime){
		setEndTime(endTime);
		setBookedStudentId(null);
		setId();
	}
	
	public RoomRecord(RoomRecord roomRecordCopy){
		setEndTime(roomRecordCopy.endTime);
		setBookedStudentId(roomRecordCopy.getBookedStudentId());
		id = roomRecordCopy.getId();
		bookingId = roomRecordCopy.getBookingId();
	}
	/**
	 * @return the endTime
	 */
	public int getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return if booking is available
	 */
	public boolean isAvailable() {
		return bookedStudentId == null;
	}
	
	private void setId()
	{
		synchronized (idGeneratorLock) {
			id ="RR"+idGenerator;
			bookingId = Information.getInstance().getServerCode()+id;
			++idGenerator;
		}
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the idGenerator
	 */
	public static int getIdGenerator() {
		return idGenerator;
	}
	
	public static void setIdGenerator(int idGenerator) {
		RoomRecord.idGenerator = idGenerator;
	}

	/**
	 * @return the bookedStudenId
	 */
	public String getBookedStudentId() {
		return bookedStudentId;
	}

	/**
	 * @param bookedStudenId the bookedStudenId to set
	 */
	public void setBookedStudentId(String bookedStudentId) {
		synchronized (bookedStudentIdLock) {
			this.bookedStudentId = bookedStudentId;
		}
	}

	/**
	 * @return the bookingId
	 */
	public String getBookingId() {
		return bookingId;
	}
	
	public String showInfo(){
		return "bookedStundetId: "+ bookedStudentId+" bookingId: "+bookingId+" endTime: "+endTime;
	}
}
