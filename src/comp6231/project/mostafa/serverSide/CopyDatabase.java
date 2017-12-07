package comp6231.project.mostafa.serverSide;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class CopyDatabase implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1270262232854735326L;

	private HashMap<String, HashMap<Integer, HashMap<Integer, RoomRecord>>> roomRecords;

	private HashMap<String, Integer> bookingCount;
	private HashMap<String, RoomRecord> bookingIds;

	public CopyDatabase(){
		roomRecords    = new HashMap<String, HashMap<Integer,HashMap<Integer, RoomRecord>>>();
		bookingCount   = new HashMap<String, Integer>();
		bookingIds     = new HashMap<String, RoomRecord>();
	}

	/**
	 * @return the roomRecords
	 */
	public HashMap<String, HashMap<Integer, HashMap<Integer, RoomRecord>>> getRoomRecords() {
		return roomRecords;
	}

	/**
	 * @param roomRecords the roomRecords to set
	 */
	public void setRoomRecords(HashMap<String, HashMap<Integer, HashMap<Integer, RoomRecord>>> roomRecords) {
		this.roomRecords = roomRecords;
	}

	/**
	 * @return the bookingCount
	 */
	public HashMap<String, Integer> getBookingCount() {
		return bookingCount;
	}

	/**
	 * @param bookingCount the bookingCount to set
	 */
	public void setBookingCount(HashMap<String, Integer> bookingCount) {
		this.bookingCount = bookingCount;
	}

	/**
	 * @return the bookingIds
	 */
	public HashMap<String, RoomRecord> getBookingIds() {
		return bookingIds;
	}

	/**
	 * @param bookingIds the bookingIds to set
	 */
	public void setBookingIds(HashMap<String, RoomRecord> bookingIds) {
		this.bookingIds = bookingIds;
	}
	
	public void copyRoomRecords(HashMap<String, HashMap<Integer,HashMap<Integer, RoomRecord>>> source, HashMap<String, HashMap<Integer,HashMap<Integer, RoomRecord>>> destination, HashMap<String, RoomRecord> destinationBookingIds){
		destination.clear();
		destinationBookingIds.clear();
		HashMap<Integer, RoomRecord> firstMapCopy = new HashMap<Integer, RoomRecord>();
		HashMap<Integer, HashMap<Integer, RoomRecord>> secondMapCopy = new HashMap<Integer, HashMap<Integer,RoomRecord>>();
		for (Map.Entry<String, HashMap<Integer, HashMap<Integer, RoomRecord>>> i: source.entrySet()) {
			for(Map.Entry<Integer, HashMap<Integer, RoomRecord>> j: i.getValue().entrySet()){
				for(Map.Entry<Integer, RoomRecord> k: j.getValue().entrySet()){
					RoomRecord rrCopy = new RoomRecord(k.getValue());
					firstMapCopy.put(k.getKey(), rrCopy);
					secondMapCopy.put(j.getKey(), firstMapCopy);
					destination.put(i.getKey(), secondMapCopy);
					if(!rrCopy.isAvailable()){
						destinationBookingIds.put(rrCopy.getBookingId(), rrCopy);
					}
				}
			}
		}
	}
	
	public void copyBookingCount(HashMap<String, Integer> source, HashMap<String, Integer> destination){
		destination.clear();
		for(Map.Entry<String, Integer> i: source.entrySet()){
			destination.put(i.getKey(), i.getValue());
		}
	}
}
