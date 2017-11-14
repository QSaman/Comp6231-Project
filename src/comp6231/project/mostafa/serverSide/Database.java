package comp6231.project.mostafa.serverSide;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.mostafa.core.Constants;

public class Database {
	private static Database instance = null;

	public enum deleteStatus{
		DELETE_SUCC, DELETE_FAILD, DELETE_TIME_NOTEXIST, DELETE_ROOMNU_NOTEXIST, DELETE_DATE_NOSTEXIST, DELETE_BOOKED
	}

	private HashMap<String, HashMap<Integer, HashMap<Integer, RoomRecord>>> roomRecords;
	private Map<String, HashMap<Integer, HashMap<Integer, RoomRecord>>> syncronizedMap;

	private HashMap<String, Integer> bookingCount;
	private HashMap<String, RoomRecord> bookingIds;
	private CopyDatabase copyDatabase;

	private Database(){
		roomRecords    = new HashMap<String, HashMap<Integer,HashMap<Integer, RoomRecord>>>();
		syncronizedMap = Collections.synchronizedMap(roomRecords);
		bookingCount   = new HashMap<String, Integer>();
		bookingIds     = new HashMap<String, RoomRecord>();
		copyDatabase   = new CopyDatabase();
	}

	public static Database getInstance(){
		if(instance == null){
			instance = new Database();
		}
		return instance;
	}

	public Boolean removeBookingId(String bookingId){
		synchronized(bookingIds){
			 RoomRecord result = bookingIds.remove(bookingId);
			 if(result != null){
				 Server.log("BookingID: "+bookingId+" Removed.");
				 return true;
			 }else{
				 Server.log("BookingID: "+bookingId+" Was not Exist.");
				 return false;
			 }
		}
	}
	
	public String cancelBookingId(String bookingId, String id){
		Map<String, RoomRecord> syncBookingId = Collections.synchronizedMap(bookingIds);
		synchronized (syncBookingId) {
			if(syncBookingId.containsKey(bookingId)){
				String studentId = syncBookingId.get(bookingId).getBookedStudentId();
				if(studentId == null){
					return "bookingId: "+bookingId+" not booked";
				}
				if(studentId.equals(id)){
					boolean isMyServer = Information.getInstance().isMine(studentId);
					String result = "";
					if(isMyServer){
						result = reduceBookingCount(studentId);
					}else{
						int port = Information.getInstance().tryToFindUDPPort(studentId);
						UDP thread;
						MessageHeader message = Information.getInstance().sendMessageServerToServer(Constants.REDUCE_BOOK_COUNT+" "+studentId, "");
						thread = new UDP(message, port, "");
						thread.start();
						try {
							thread.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						result = thread.getResult();
					}
					Server.log(result);
					syncBookingId.get(bookingId).setBookedStudentId(null);
					return "bookingId: "+bookingId+" canceled byId :"+id;
				}else{
					return "the studnet with id: "+id+" is not allowed to cancel the booking id :"+bookingId;
				}

			}else{
				return "bookingId: "+bookingId+" is not exist";
			}
		}
	}

	public int findAvailableTimeSlot(String date){
		if(syncronizedMap.containsKey(date)){
			Set<Entry<Integer, HashMap<Integer, RoomRecord>>> set = syncronizedMap.get(date).entrySet();
			synchronized (syncronizedMap) {
				Iterator<Entry<Integer, HashMap<Integer, RoomRecord>>> i = set.iterator();
				int counter = 0;
				while(i.hasNext()){
					Entry<Integer, HashMap<Integer, RoomRecord>> e = (Entry<Integer, HashMap<Integer, RoomRecord>>) i.next();
					Set<Entry<Integer, RoomRecord>> inerSet = syncronizedMap.get(date).get(e.getKey()).entrySet();
					Iterator<Entry<Integer, RoomRecord>> j = inerSet.iterator();
					while(j.hasNext()){
						Entry<Integer, RoomRecord> entry = (Entry<Integer, RoomRecord>) j.next();
						if(entry.getValue().isAvailable()){
							++counter;	
						}
					}
				}
				Server.log("AvailableTimeSlot server: "+Information.getInstance().getServerCode()+" iis: "+counter);
				return counter;
			}
		}else{
			Server.log("AvailableTimeSlot server: "+Information.getInstance().getServerCode()+" is: "+0);
			return 0;
		}
	}

	public void reset(){
			Set<Entry<String, HashMap<Integer, HashMap<Integer, RoomRecord>>>> set = syncronizedMap.entrySet();
			synchronized (syncronizedMap) {
				Iterator<Entry<String, HashMap<Integer, HashMap<Integer, RoomRecord>>>> i = set.iterator();
				while(i.hasNext()){
					Entry<String, HashMap<Integer, HashMap<Integer, RoomRecord>>> e = (Entry<String, HashMap<Integer, HashMap<Integer, RoomRecord>>>) i.next();
					Set<Entry<Integer, HashMap<Integer, RoomRecord>>> inerSet = syncronizedMap.get(e.getKey()).entrySet();
					Iterator<Entry<Integer, HashMap<Integer, RoomRecord>>> j = inerSet.iterator();
					while(j.hasNext()){
						Entry<Integer, HashMap<Integer, RoomRecord>> e2 =  j.next();
						Set<Entry<Integer, RoomRecord>> inerSet2 = syncronizedMap.get(e.getKey()).get(e2.getKey()).entrySet();
						Iterator<Entry<Integer, RoomRecord>> j2 = inerSet2.iterator();
						while(j2.hasNext()){
							Entry<Integer, RoomRecord> e3 = j2.next();
							e3.getValue().setBookedStudentId(null);
						}
					}
				}
			}
	}
	
	
	public boolean isBookCountReachedToLimit(String id){
		Map<String, Integer>  syncBookingCount = Collections.synchronizedMap(bookingCount);
		synchronized (syncBookingCount) {
			if(syncBookingCount.containsKey(id)){
				if(syncBookingCount.get(id) < 3){
					Server.log("We can add,Booking Count for Student Id: "+id+" is: "+syncBookingCount.get(id));
					return false;
				}else {
					return true;
				}
			}else{
				syncBookingCount.put(id, 0);
				Server.log("Booking Count for Student Id: "+id+" reseted to "+syncBookingCount.get(id));
				return false;
			}
		}
	}

	public void increaseBookingCount(String id){
		Map<String, Integer>  syncBookingCount = Collections.synchronizedMap(bookingCount);
		synchronized (syncBookingCount) {
			syncBookingCount.put(id, syncBookingCount.get(id)+1);
			Server.log("Booking Count for Student Id: "+id+" increased to "+syncBookingCount.get(id));
		}
	}

	public String reduceBookingCount(String id){
		String result = "";
		Map<String, Integer> syncronizedBookingCount = Collections.synchronizedMap(bookingCount);
		synchronized (syncronizedBookingCount) {
			if(syncronizedBookingCount.containsKey(id)){
				int bookingCount = syncronizedBookingCount.get(id);
				syncronizedBookingCount.put(id, bookingCount-1);
				result = "Booking Count for Student Id: "+id+" reduced form "+bookingCount+" to: "+syncronizedBookingCount.get(id);
				Server.log(result);
				return result;
			}else{
				result = "There is no such student with student id: "+id+" to reduce bookCount";
				Server.log(result);
				return result;
			}
		}
	}

	public String tryToBookRoom(int roomNumber, String date, int startTime, int endTime, String id){
		String time = Information.getInstance().convertTimeToString(startTime)+"-"+Information.getInstance().convertTimeToString(endTime);
		if(syncronizedMap.containsKey(date)){
			Map<Integer, HashMap<Integer, RoomRecord>> syncronizedVal = Collections.synchronizedMap(syncronizedMap.get(date));
			if(syncronizedVal.containsKey(roomNumber)){
				Map<Integer, RoomRecord> syncronizedRr = Collections.synchronizedMap(syncronizedVal.get(roomNumber));
				synchronized (syncronizedMap) {
					if(syncronizedRr.containsKey(startTime)){
						if(syncronizedRr.get(startTime).getEndTime() == endTime && syncronizedRr.get(startTime).isAvailable()){
							Server.log("studnet: "+id+" roomNumber: "+roomNumber+" on date: "+date+" in time: "+time+" with bookingId "+syncronizedRr.get(startTime).getBookingId()+" booked");
							syncronizedRr.get(startTime).setBookedStudentId(id);
							Map<String, RoomRecord> syncronizedBookingid = Collections.synchronizedMap(bookingIds);
							syncronizedBookingid.put(syncronizedRr.get(startTime).getBookingId(), syncronizedRr.get(startTime));
							return syncronizedRr.get(startTime).getBookingId()+" booked";	
						}else{
							Server.log("studnet: "+id+" roomNumber: "+roomNumber+" on date: "+date+" in time: "+time+" "+Constants.ERR_NOT_BOOKED_NOTAV);
							return Constants.ERR_NOT_BOOKED_NOTAV;
						}
					}else{
						Server.log("studnet: "+id+" roomNumber: "+roomNumber+" on date: "+date+" in time: "+time+" "+Constants.ERR_NOT_BOOKED_NOTIME);
						return Constants.ERR_NOT_BOOKED_NOTIME;
					}
				}
			}else {
				Server.log("studnet: "+id+" roomNumber: "+roomNumber+" on date: "+date+" in time: "+time+" "+Constants.ERR_NOT_BOOKED_NOROOM);
				return Constants.ERR_NOT_BOOKED_NOROOM;
			}

		}else {
			Server.log("studnet: "+id+" roomNumber: "+roomNumber+" on date: "+date+" in time: "+time+" "+Constants.ERR_NOT_BOOKED_NODATE);
			return Constants.ERR_NOT_BOOKED_NODATE;
		}
	}

	public deleteStatus removeFromDatabase(String date, int roomNumber, int startTime, int endTime){
		synchronized (syncronizedMap) {
			if(syncronizedMap.containsKey(date)){
				Map<Integer, HashMap<Integer, RoomRecord>> syncronizedVal = Collections.synchronizedMap(syncronizedMap.get(date));
				synchronized (syncronizedVal) {
					if(syncronizedVal.containsKey(roomNumber)){
						return removeRoomRecord(date, roomNumber, startTime, endTime);
					}else {
						return deleteStatus.DELETE_ROOMNU_NOTEXIST;
					}
				}
			}else {
				return deleteStatus.DELETE_DATE_NOSTEXIST;
			}
		}
	}

	public boolean addToDatabase(String date, int roomNumber, int startTime, int endTime){
		synchronized (syncronizedMap) {
			if(syncronizedMap.containsKey(date)){
				Map<Integer, HashMap<Integer, RoomRecord>> syncronizedVal = Collections.synchronizedMap(roomRecords.get(date));
				synchronized (syncronizedVal) {
					if(syncronizedVal.containsKey(roomNumber)){
						return addRoomRecord(date, roomNumber, startTime, endTime);
					}else {
						HashMap<Integer, RoomRecord> rrHashMap = new HashMap<Integer, RoomRecord>();
						Map<Integer, RoomRecord> syncronizedRr = Collections.synchronizedMap(rrHashMap);
						RoomRecord rr = new RoomRecord(endTime);
						syncronizedRr.put(startTime, rr);
						syncronizedVal.put(roomNumber, rrHashMap);
						bookingIds.put(rr.getBookingId(), rr);
						return true;
					}
				}
			}else {
				HashMap<Integer, RoomRecord> rrHashMap = new HashMap<Integer, RoomRecord>();
				Map<Integer, RoomRecord> syncronizedRr = Collections.synchronizedMap(rrHashMap);
				RoomRecord rr = new RoomRecord(endTime);
				syncronizedRr.put(startTime, rr);

				HashMap<Integer, HashMap<Integer, RoomRecord>> val = new HashMap<Integer, HashMap<Integer,RoomRecord>>();
				Map<Integer, HashMap<Integer, RoomRecord>> syncronizedVal = Collections.synchronizedMap(val);
				syncronizedVal.put(roomNumber, rrHashMap);
				syncronizedMap.put(date, val);
				bookingIds.put(rr.getBookingId(), rr);
				return true;
			}
		}
	}

	public deleteStatus removeRoomRecord(String date, int roomNumber,int startTime, int endTime){
		HashMap<Integer, RoomRecord> rrHashMap = roomRecords.get(date).get(roomNumber);
		Map<Integer, RoomRecord> syncronizedRr = Collections.synchronizedMap(rrHashMap);

		Set<Entry<Integer,RoomRecord>> set = syncronizedRr.entrySet();
		synchronized (syncronizedRr) {
			Iterator<Entry<Integer, RoomRecord>> i = set.iterator();
			while (i.hasNext()) {
				Entry<Integer, RoomRecord> e = (Entry<Integer, RoomRecord>) i.next();
				if(e.getKey() == startTime && e.getValue().getEndTime() == endTime) {
					boolean result = syncronizedRr.remove(e.getKey(), e.getValue());
					if(e.getValue().isAvailable()){
						if(result)
						{
							return deleteStatus.DELETE_SUCC;
						}else{
							return deleteStatus.DELETE_FAILD;
						}

					}else{
						// reduce the booked count by 1
						if(result){
							boolean isMyServer = Information.getInstance().isMine(e.getValue().getBookedStudentId());
							String Deleteresult = "";
							if(isMyServer){
								Deleteresult = reduceBookingCount(e.getValue().getBookedStudentId());
							}else{
								int port =Information.getInstance().tryToFindUDPPort(e.getValue().getBookedStudentId());
								if(port == -1){
									return deleteStatus.DELETE_FAILD;
								}else{
									UDP thread;
									
									MessageHeader message = Information.getInstance().sendMessageServerToServer(Constants.REDUCE_BOOK_COUNT+" "+e.getValue().getBookedStudentId(), "");
									thread = new UDP(message, port, "");
									thread.start();
									try {
										thread.join();
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									Deleteresult = thread.getResult();
								}
							}
							
							// cancel bookingId
							String bookingId = e.getValue().getBookingId();
							boolean isMybookingId = Information.getInstance().isMine(bookingId);
							if(isMybookingId){
									boolean removeResult = removeBookingId(bookingId);
									if(!removeResult){
										return deleteStatus.DELETE_FAILD;
									}else{
										Deleteresult = Deleteresult + "bookingId: "+bookingId+" removed";
									}
							}else{
								int port =Information.getInstance().tryToFindUDPPort(bookingId);
								if(port == -1){
									return deleteStatus.DELETE_FAILD;
								}else{
									UDP thread;
									
									MessageHeader message = Information.getInstance().sendMessageServerToServer(Constants.REQ_REMOVE_BOOK+" "+bookingId, "");
									thread = new UDP(message, port, "");
									thread.start();
									try {
										thread.join();
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									String UDPResut = thread.getResult();
									if(UDPResut.equals(Constants.RESULT_UDP_FAILD)){
										return deleteStatus.DELETE_FAILD;
									}else{
										Deleteresult = Deleteresult + UDPResut;
									}
								}
							}
							Server.log(Deleteresult);
							return deleteStatus.DELETE_BOOKED;
						}else {
							return deleteStatus.DELETE_FAILD;
						}
					}
				}
			}
			return deleteStatus.DELETE_TIME_NOTEXIST;
		}
	}

	public boolean addRoomRecord(String date, int roomNumber,int startTime, int endTime){
		HashMap<Integer, RoomRecord> rrHashMap = roomRecords.get(date).get(roomNumber);
		Map<Integer, RoomRecord> syncronizedRr = Collections.synchronizedMap(rrHashMap);

		Set<Entry<Integer,RoomRecord>> set = syncronizedRr.entrySet();
		synchronized (syncronizedRr) {
			Iterator<Entry<Integer, RoomRecord>> i = set.iterator();
			while (i.hasNext()) {
				Entry<Integer, RoomRecord> e = (Entry<Integer, RoomRecord>) i.next();
				if(e.getKey() <= startTime && e.getValue().getEndTime() > startTime || e.getKey() > startTime && e.getKey() < endTime) {
					return false;
				}
			}
			RoomRecord rr = new RoomRecord(endTime);
			syncronizedRr.put(startTime, rr);
			bookingIds.put(rr.getBookingId(), rr);
			return true;
		}
	}
	
	public void commit(){
		synchronized (roomRecords) {
			synchronized (bookingIds) {
				copyDatabase.copyRoomRecords(roomRecords, copyDatabase.getRoomRecords(), copyDatabase.getBookingIds());	
			}
		}
		synchronized (bookingCount) {
			copyDatabase.copyBookingCount(bookingCount, copyDatabase.getBookingCount());
		}
		Server.log("Database commited");
	}
	
	public void rollBack(){
		synchronized (roomRecords) {
			roomRecords.clear();
			synchronized (bookingIds) {
				copyDatabase.copyRoomRecords(copyDatabase.getRoomRecords(), roomRecords, bookingIds);
			}
			syncronizedMap = Collections.synchronizedMap(roomRecords);
		}
		synchronized (bookingCount) {
			copyDatabase.copyBookingCount(copyDatabase.getBookingCount(), bookingCount);
		}
		Server.log("DataBase rollBacked");
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
}
