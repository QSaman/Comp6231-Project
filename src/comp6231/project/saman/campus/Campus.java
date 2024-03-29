/**
 * 
 */
package comp6231.project.saman.campus;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.google.gson.Gson;

import comp6231.project.messageProtocol.StartGson;
import comp6231.project.saman.campus.message_protocol.saman_replica.*;
import comp6231.project.saman.common.DateReservation;
import comp6231.project.saman.common.LoggerHelper;
import comp6231.project.saman.common.TimeSlot;
import comp6231.project.saman.common.TimeSlotResult;
import comp6231.project.saman.common.users.CampusUser;

/**
 * @author saman
 *
 */
public class Campus implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4266457842199746243L;

	public enum CampusType
	{
		Main,
		Backup
	}
	
	private CampusType campus_type;
	private String name;

	public HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>> db;
	private final Object date_db_lock = new Object();
	private final Object room_db_lock = new Object();
	public HashMap<String, StudentRecord> student_db;
	private final Object write_student_db_lock = new Object();		
	private String address;	//The address of this server
	private int port;	//The UDP listening port of this server	
	private UdpServer udp_server;
	private Logger logger;
	private CampusCommunication campus_comm;
	private JsonMessage json_message;
	private Gson gson;
	private boolean fake_result;
	private CampusContainer campus_container;
		
	public Campus(String name, String address, int port, Logger logger, CampusCommunication campus_comm, CampusType campus_type) throws IOException
	{
		this.name = name;
		this.campus_type = campus_type;
		this.fake_result = false;
		//db = new HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>>();
		//student_db = new HashMap<String, StudentRecord>();
		this.address = address;
		this.port = port;
		this.logger = logger;
		initGson();
		udp_server = new UdpServer(this, gson, logger);
		json_message = new JsonMessage(udp_server, gson);
		udp_server.setJsonMessage(json_message);		
		this.campus_comm = campus_comm;
		udp_server.start();
		this.campus_comm.setCampus(this);
	}
	
	public void setDB(HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>> db)
	{
		this.db = db;
	}
	
	public void setCampusContainer(CampusContainer campus_container)
	{
		this.campus_container = campus_container;
	}
	
	public void setStudentDB(HashMap<String, StudentRecord> student_db)
	{
		this.student_db = student_db;
	}
	
	private void initGson()
	{
//		MessageHeaderDeserializer ds = new MessageHeaderDeserializer();		
//		InitializeSerializer.initializeSamanReplica(ds);
//		gson = new GsonBuilder().registerTypeAdapter(MessageHeader.class, ds).create();
		gson = StartGson.initGsonSaman();
	}
	
	public void starServer()
	{		
		campus_comm.startServer();
		logger.info(LoggerHelper.format(getName() + " - " + campus_type + " bound"));
	}
	
	public String getCampusName() {
		return getName();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param args
	 * @throws RemoteException 
	 */
	public static void main(String[] args) throws RemoteException {

	}
	
	private abstract class DbOperations	//(key, vale), value = (sub_key, sub_value)
	{
		@SuppressWarnings("unused")
		private HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>> _db;
		protected boolean _operation_status;
		protected TimeSlot _time_slot;
		protected ArrayList<TimeSlot> _time_slots;
		
		public DbOperations(HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>> db)
		{
			this._db = db;
			_operation_status = true;
		}
		//All the following methods are thread-safe for campus time slots database
		public abstract HashMap<Integer, ArrayList<TimeSlot>> onNullValue();
		public abstract ArrayList<TimeSlot> onNullSubValue(HashMap<Integer, ArrayList<TimeSlot>> val);
		public abstract void onSubValue(ArrayList<TimeSlot> sub_val);
	}
	
	private void traverseDb(int room_number, DateReservation date, DbOperations db_ops)
	{
		HashMap<Integer, ArrayList<TimeSlot>> val = null;
		//https://stackoverflow.com/questions/11050539/using-hashmap-in-multithreaded-environment:
		//https://stackoverflow.com/questions/2688629/is-a-hashmap-thread-safe-for-different-keys:
		//The get() goes to an infinite loop because one of the threads has only a partially updated 
		//view of the HashMap in memory and there must be some sort of pointer loop
		synchronized (date_db_lock) {
			val = db.get(date);
			if (val == null)
			{
				val = db_ops.onNullValue();
				if (val == null)
					return;
				db.put(date, val);
			}
		}		

		ArrayList<TimeSlot> sub_val = null;
		synchronized (room_db_lock) {
			 sub_val = val.get(room_number);
			 if (sub_val == null)
			 {
				 sub_val = db_ops.onNullSubValue(val);
				 if (sub_val == null)
					 return;
				 val.put(room_number, sub_val);
				 return;
			 }
		}
		synchronized (sub_val) {
			db_ops.onSubValue(sub_val);
		}				
	}

	public boolean createRoom(String user_id, int room_number, DateReservation date, ArrayList<TimeSlot> time_slots) {
		CampusUser user = new CampusUser(user_id);
		String log_msg = String.format("received createRoom(user_id: %s, room_number: %d, date: %s, time slots: %s",
				user_id, room_number, date, time_slots);
		logger.info(LoggerHelper.format(log_msg));
		if (!user.isAdmin() || !user.getCampus().equals(getName()))
		{
			logger.warning(LoggerHelper.format(String.format("%s is not authorized to createRoom in %s", user_id, getName())));
			return false;
		}
		DbOperations db_ops = new DbOperations(db) {
			
			@Override
			public void onSubValue(ArrayList<TimeSlot> sub_val) {
				for (TimeSlot time_slot : time_slots)
				{
					boolean conflict = false;
					for (TimeSlot cur : sub_val)
						if (cur.conflict(time_slot))
						{
							logger.warning(LoggerHelper.format(String.format("There is a conflict between %s and %s", cur, time_slot)));
							conflict = true;
							_operation_status = false;
							break;
						}
					if (!conflict)
					{
						sub_val.add(time_slot);
						logger.info("Create time slot: " + time_slot.toString());
					}
				}
			}
			
			@Override
			public HashMap<Integer, ArrayList<TimeSlot>> onNullValue() {
				HashMap<Integer, ArrayList<TimeSlot>> val = new HashMap<Integer, ArrayList<TimeSlot>>();
				logger.finest(LoggerHelper.format(String.format("There aren't any record for room number %d. I create one.", room_number)));
				return val;
			}
			
			@Override
			public ArrayList<TimeSlot> onNullSubValue(HashMap<Integer, ArrayList<TimeSlot>> val) {				
				return time_slots;
			}
		};
		traverseDb(room_number, date, db_ops);
		if (fake_result)
			return !db_ops._operation_status;
		return db_ops._operation_status;
	}

	//TODO reduce student count if the reservation is deleted
	public boolean deleteRoom(String user_id, int room_number, DateReservation date, ArrayList<TimeSlot> time_slots) {
		String log_msg = String.format("received deleteRoom(user id: %s, room number: %d, date: %s, time slots: %s", 
				user_id, room_number, date, time_slots);
		logger.info(LoggerHelper.format(log_msg));
		CampusUser user = new CampusUser(user_id);
		if (!user.isAdmin() || !user.getCampus().equals(getName()))
		{
			logger.warning(LoggerHelper.format(String.format("%s is not authorized to deleteRoom in %s", user_id, getName())));
			return false;
		}
		DbOperations ops = new DbOperations(db) {
			
			@Override
			public void onSubValue(ArrayList<TimeSlot> sub_val) {
				ArrayList<Thread> threads = new ArrayList<>();
				//HashMap<Integer, WaitObject> res = new HashMap<>();	//(message_id, wait ojbect)
				HashMap<Integer, ReplicaReplyMessageStatus> res = new HashMap<>();
				HashMap<Integer, TimeSlot> removed_list = new HashMap<>();	//(message_id, time slot)
				
				ArrayList<TimeSlot> new_time_slots = new ArrayList<TimeSlot>();
				HashMap<TimeSlot, Boolean> del_list = new HashMap<>();
				for (TimeSlot val1 : sub_val)
				{
					boolean found = false;
					for (TimeSlot val2 : time_slots)
						if (val1.equals(val2))
						{
							found = true;
							del_list.put(val2, true);
							break;
						}					
					if (found)	//We need to delete val1 from time slot list
					{
						logger.info("I'm going to delete time slot " + val1.toString());
						if (val1.isBooked())
						{
							CampusUser user = new CampusUser(val1.getUsername());
							if (userBelongHere(user))
							{
								logger.info(String.format("%s who is in this campus, booked time slot %s." +
										"I'm going to remove booking id %s from his/her record while deleting time slot", 
										val1.getUsername(), val1, val1.getBookingId()));
								boolean status = removeStudentRecord(val1.getUsername(), val1.getBookingId());								
								if (!status)
								{
									logger.severe(String.format("I cannot delete booking id %s from %s records ", 
											val1.getBookingId(), val1.getUsername()));
									_operation_status = false;
								}
							}
							else
							{
								CampusUser a_user = new CampusUser(val1.getUsername());
								logger.info(String.format("%s who is in another campus (%s), booked time slot %s." +
										"I'm going to send a request to that campus to remove booking id %s from" + 
										" his/her record while I'm deleting this time slot", val1.getUsername(), 
										a_user.getCampus(), val1, val1.getBookingId()));
								
								int message_id = MessageProtocol.generateMessageId();								
								Thread thread = new Thread(new Runnable() {
									
									@Override
									public void run() {
										ReplicaRequestRemoveStudentRecord send_msg = 
												new ReplicaRequestRemoveStudentRecord(message_id, val1.getUsername(), val1.getBookingId()); 
										ReplicaReplyMessageStatus reply = null;
										try {
											reply = (ReplicaReplyMessageStatus)sendMessage(send_msg, user.getCampus());
										} catch (IOException | InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										synchronized (res) {
											res.put(message_id, reply);												
										}											
									}
								});
								removed_list.put(message_id, val1);
								thread.start();									
								threads.add(thread);
							}
						}
					}
					else	//We shouldn't delete val1
					{
						//logger.warning(String.format("I couldn't find %s in time slot database", val1));
						new_time_slots.add(val1);
						//_operation_status = false;
					}
				}
				//Since we use sub_val as a lock object (see create room method), we couldn't simply use val.put(room_number, new_time_slots)
				sub_val.clear();
				for (TimeSlot value : new_time_slots)
					sub_val.add(value);
				
				try {
					for (Thread thread : threads)
						thread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				for (int message_id : res.keySet())
				{
					TimeSlot ts = removed_list.get(message_id);
					boolean status = res.get(message_id).status;
					if (!status)
					{
						CampusUser a_user = new CampusUser(ts.getUsername());
						logger.severe(String.format("%s campus cannot delete booking id %s from student %s records",
								a_user.getCampus(), ts.getBookingId(), ts.getUsername()));
						_operation_status = false;
					}
				}
				for (TimeSlot ts : time_slots)
				{
					if (del_list.get(ts) == null)
					{
						logger.warning("Time slot " + ts.toString() + " is not in database");
						_operation_status = false;
					}
				}
			}
			
			@Override
			public HashMap<Integer, ArrayList<TimeSlot>> onNullValue() {
				_operation_status = false;
				logger.severe(String.format("%s doesn't have any room numbers!", date));
				return null;
				}
			
			@Override
			public ArrayList<TimeSlot> onNullSubValue(HashMap<Integer, ArrayList<TimeSlot>> val) {
				_operation_status = false;
				logger.severe(String.format("We don't have any time slots for date %s and room number %d",
						date, room_number));
				return null;
				}
		};
		
		traverseDb(room_number, date, ops);
		if (fake_result)
			return !ops._operation_status;
		return ops._operation_status;
	}
	
	/**
	 * This method is not thread-safe!!!
	 */
	private void clearAllDatabases()
	{
//		db = new HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>>();
//		student_db = new HashMap<String, StudentRecord>();
		db.clear();
		student_db.clear();
		logger.info("################# Campus " + getName() + " starts a new week! #################");
	}
	public void startWeek()
	{
		//Be careful don't use the reverse order in other places. It can be a deadlock!
		synchronized (date_db_lock) {			
			synchronized (write_student_db_lock) {
				clearAllDatabases();
			}
		}
	}
	public boolean startWeek(String user_id) throws IOException, InterruptedException {
		logger.info(LoggerHelper.format(String.format("recieved a request for starting a new week from user %s", user_id)));
		CampusUser user = new CampusUser(user_id);
		if (!user.isAdmin() && !user.getCampus().equals(getName()))
		{
			logger.warning(String.format("user %s is not authorized to start week in this campus (%s)", user_id, getName()));
			return false;
		}
		//Be careful don't use the reverse order in other places. It can be a deadlock!
		synchronized (date_db_lock) {			
			synchronized (write_student_db_lock) {
				clearAllDatabases();
				ArrayList<Thread> threads = new ArrayList<Thread>();
				String[] campus_names = campus_comm.getAllCampusNames();
				for (String campus_str : campus_names)
				{
					if (campus_str.equals(getName()))
						continue;
					int message_id = MessageProtocol.generateMessageId();		
					Thread thread = new Thread(new Runnable() {				
						@Override
						public void run() {
							ReplicaRequestStartWeek send_msg = new ReplicaRequestStartWeek(message_id, user_id);
							try {
								logger.info("Sending request to " + campus_str + " to start a new week");
								sendMessage(send_msg, campus_str);
							} catch (IOException | InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					thread.start();
					threads.add(thread);
				}
				for (Thread thread : threads)
					thread.join();
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param room_number
	 * @param date
	 * @param time_slot
	 * @return TimeSlot or null if it's not available
	 */
	private TimeSlot findTimeSlot(int room_number, DateReservation date, TimeSlot time_slot)
	{
		DbOperations ops = new DbOperations(db) {
			
			@Override
			public HashMap<Integer, ArrayList<TimeSlot>> onNullValue() {return null;}
			
			@Override
			public ArrayList<TimeSlot> onNullSubValue(HashMap<Integer, ArrayList<TimeSlot>> val) {return null;}

			@Override
			public void onSubValue(ArrayList<TimeSlot> sub_val) {
				for (TimeSlot ts : sub_val)
					if (ts.equals(time_slot))
					{
						_time_slot = ts;
						break;
					}		
			}
		};
		traverseDb(room_number, date, ops);
		return ops._time_slot;
	}
	
	private ArrayList<TimeSlot> findTimeSlots(int room_number, DateReservation date)
	{
		DbOperations ops = new DbOperations(db) {			
			
			@Override
			public void onSubValue(ArrayList<TimeSlot> sub_val) {_time_slots = sub_val;}
			
			@Override
			public HashMap<Integer, ArrayList<TimeSlot>> onNullValue() {return null;}
			
			@Override
			public ArrayList<TimeSlot> onNullSubValue(HashMap<Integer, ArrayList<TimeSlot>> val) { return null;}
		};
		traverseDb(room_number, date, ops);
		return ops._time_slots;
	}
	
	private ReplyMessageHeader sendMessage(ReplicaRequestMessageHeader message, String campus_name) throws IOException, InterruptedException
	{
		CampusCommunication.RemoteInfo remote_info = campus_comm.getRemoteInfo(campus_name);
		InetAddress address = InetAddress.getByName(remote_info.address);
		int port = remote_info.port;
		ReplyMessageHeader res = json_message.sendJson(message, address, port);
		return res;
	}
	
	private boolean userBelongHere(CampusUser user)
	{
		return user.getCampus().equals(getName());
	}
	
	private abstract class UserDbOperations
	{
		protected CampusUser _user;
		public String _booking_id;
		@SuppressWarnings("unused")
		protected HashMap<String, StudentRecord> _student_db;
		protected boolean _status;
		
		public UserDbOperations(HashMap<String, StudentRecord> student_db, CampusUser user) 
		{
			this._user = user;
			this._student_db = student_db;
			_booking_id = "";
			_status = true;
		}
		
		//All the following methods are tread-safe for user database
		public abstract boolean onUserBelongHere(StudentRecord record);
		public abstract StudentRecord onNullUserRecord(CampusUser user);
		public abstract boolean onOperationOnThisCampus(ArrayList<TimeSlot> time_slots);
		public abstract boolean onOperationOnOtherCampus(int message_id) throws NotBoundException, IOException, InterruptedException;
		public abstract void onPostUserBelongHere(StudentRecord record);
		public abstract ArrayList<TimeSlot> findTimeSlots();
	}

	private void traverseStudentDb(UserDbOperations user_db_ops, CampusUser user, String campus_name) throws NotBoundException, IOException, InterruptedException
	{
		
		//String booking_id = null;
		StudentRecord record = null;
		
		if (userBelongHere(user))
		{
			synchronized (write_student_db_lock) {
				record = student_db.get(user.getUserId());
				if (record == null)
				{
					record = user_db_ops.onNullUserRecord(user);
					if (record == null)
						return;
					student_db.put(user.getUserId(), record);
				}				
			}
			synchronized (record) {			
				if (!user_db_ops.onUserBelongHere(record))
					return;
				
				if (campus_name.equals(getName()))
				{
					ArrayList<TimeSlot> time_slots = user_db_ops.findTimeSlots();
					if (time_slots == null)
						return;
					synchronized (time_slots) {
						if (!user_db_ops.onOperationOnThisCampus(time_slots))
							return;
					}
				}
				else
				{
					int msg_id = MessageProtocol.generateMessageId();
					if (!user_db_ops.onOperationOnOtherCampus(msg_id))
						return;
				}
				user_db_ops.onPostUserBelongHere(record);
			}
		}
		else if (campus_name.equals(getName()))	//We received booking request from another campus
		{
			ArrayList<TimeSlot> time_slots = user_db_ops.findTimeSlots();
			if (time_slots == null)
				return;
			synchronized (time_slots) {
				user_db_ops.onOperationOnThisCampus(time_slots);
			}			
		}
		else
			throw new IllegalArgumentException("The sender campus send the message to the wrong campus: (" + campus_name + ", " + getName() + ")");
	}
	
	public String changeReservation(String user_id, String booking_id, String new_campus_name, int new_room_number, DateReservation new_date, TimeSlot new_time_slot) throws NotBoundException, IOException, InterruptedException
	{
		CampusUser user = new CampusUser(user_id);
		String log_msg = String.format("received changeReservation(user id: %s, booking_id: %s, new_campus name: %s, " + 
		"new_room number: %d, new_date: %s, new_time slot: %s)", user_id, booking_id, new_campus_name, new_room_number, 
		new_date, new_time_slot);
		logger.info(LoggerHelper.format(log_msg));
		if (!user.isStudent())
		{
			logger.warning(String.format("user %s is not allowed to changeReservation in %s campus", user_id, getName()));
			return "";
		}
		String new_booking_id = bookRoom(user_id, new_campus_name, new_room_number, new_date, new_time_slot, 4);
		if (!new_booking_id.isEmpty())
		{
			boolean status = cancelBooking(user_id, booking_id);
			if (!status)
			{
				cancelBooking(user_id, new_booking_id);
				return "";
			}
		}
		if (fake_result)
		{
			if (new_booking_id.isEmpty())
				new_booking_id = "fake_booking_id";
			else
				new_booking_id = "";
		}
		return new_booking_id;
	}
	
	public String bookRoom(String user_id, String campus_name, int room_number, DateReservation date, TimeSlot time_slot) throws NotBoundException, IOException, InterruptedException {
		return bookRoom(user_id, campus_name, room_number, date, time_slot, 3);
	}
	public String bookRoom(String user_id, String campus_name, int room_number, DateReservation date, TimeSlot time_slot, int max_booking_num) throws NotBoundException, IOException, InterruptedException {
		
		CampusUser user = new CampusUser(user_id);
		String log_msg = String.format("received bookRoom(user id: %s, campus name: %s, " + 
		"room number: %d, date: %s, time slot: %s)", user_id, campus_name, room_number, date, time_slot);
		logger.info(LoggerHelper.format(log_msg));
		if (!user.isStudent())
		{
			logger.warning(String.format("user %s is not allowed to bookRoom in %s campus", user_id, getName()));
			return null;
		}
		
		UserDbOperations ops = new UserDbOperations(student_db, user) {
			
			@Override
			public boolean onUserBelongHere(StudentRecord record) {
				if (!record.canBook(max_booking_num))
					return false;
				return true;
			}
			
			@Override
			public void onPostUserBelongHere(StudentRecord record) {
				record.saveBookRoomRequest(_booking_id, date, time_slot);				
			}
			
			@Override
			public StudentRecord onNullUserRecord(CampusUser user)
			{
				StudentRecord record = new StudentRecord(user);
				return record;
			}
			
			@Override
			public boolean onOperationOnThisCampus(ArrayList<TimeSlot> time_slots) {
				logger.fine(String.format("bookRoom for user %s is going to be done here", user_id));
				if (time_slots == null)
					return false;
				TimeSlot ts = null;
				boolean booked = false;
				String prev_booking_id = "";
				for (TimeSlot tmp : time_slots)
					if (tmp.equals(time_slot))
					{
						if (!tmp.isBooked())
						{
							ts = tmp;							
						}
						else
						{
							booked = true;
							prev_booking_id = tmp.getBookingId();
						}
						break;
					}
				if (ts == null)
				{
					if (booked)
						logger.warning(String.format("Time slot %s is booked before in database (bookRoom): " + prev_booking_id, time_slot));
					else
						logger.warning(String.format("I cannot find time slot %s in database (bookRoom)", time_slot));
					return false;
				}
				_booking_id = BookingIdGenerator.generate(getName(), date, room_number);
				ts.bookTimeSlot(user_id, _booking_id);
				return true;
			}
			
			@Override
			public boolean onOperationOnOtherCampus(int message_id) throws NotBoundException, IOException, InterruptedException {
				logger.fine(String.format("I'm going to send a request to campus %s for booking a room", campus_name));
				int msg_id = MessageProtocol.generateMessageId();
				ReplicaRequestBookRoom send_msg = new ReplicaRequestBookRoom(msg_id, user_id, room_number, date, time_slot);   
				ReplicaReplyBookRoom reply = (ReplicaReplyBookRoom) sendMessage(send_msg, campus_name);
				_booking_id = reply.booking_id;
				return reply.status;
			}
			
			@Override
			public ArrayList<TimeSlot> findTimeSlots()
			{
				return Campus.this.findTimeSlots(room_number, date);
			}
		};
		
		traverseStudentDb(ops, user, campus_name);
		if (fake_result)
		{
			if (ops._booking_id.isEmpty())
				ops._booking_id = "fake_booking_id";
			else
				ops._booking_id = "";
		}
		return ops._booking_id;
	}
	
	public int getThisCampusAvailableTimeSlots(DateReservation date)
	{
		HashMap<Integer, ArrayList<TimeSlot>> val = null;
		synchronized (date_db_lock) {
			val = db.get(date);
		}
		int ret = 0;
		if (val == null)
			return ret;
		synchronized (room_db_lock) {
			for (int room_number : val.keySet())
			{
				ArrayList<TimeSlot> time_slots = val.get(room_number);
				for (TimeSlot time_slot : time_slots)
					if (!time_slot.isBooked())
						++ret;
			}
		}
		if (fake_result)
			++ret;
		return ret;
	}	

	public ArrayList<TimeSlotResult> getAvailableTimeSlot(String user_id, DateReservation date) throws IOException, InterruptedException {
		String log_msg = String.format("received getAvailableTimeSlot(date: %s)", date);
		logger.info(LoggerHelper.format(log_msg));
		
		ArrayList<TimeSlotResult> ret = new ArrayList<TimeSlotResult>();
		ret.add(new TimeSlotResult(getName(), getThisCampusAvailableTimeSlots(date)));
		HashMap<Integer, String> hm = new HashMap<>();	//(message_id, campus_name)
		//HashMap<Integer, UdpServer.WaitObject> hm_wo = new HashMap<>();	//(message_id, WaitObject)
		HashMap<Integer, ReplicaReplyAvailableTimeSlots> reply_list = new HashMap<>();
		ArrayList<Thread> threads = new ArrayList<Thread>();
		String[] campus_names = campus_comm.getAllCampusNames();
		for (String campus_str : campus_names)
		{
			if (campus_str.equals(getName()))
				continue;
			int message_id = MessageProtocol.generateMessageId();
			byte[] send_msg = MessageProtocol.encodeGetAvailableTimeSlotsMessage(message_id, date);
			hm.put(message_id, campus_str);						
			Thread thread = new Thread(new Runnable() {				
				@Override
				public void run() {
					ReplicaRequestAvailableTimeSlots send_msg = new ReplicaRequestAvailableTimeSlots(message_id, user_id, date.toString());
					ReplicaReplyAvailableTimeSlots reply = null;
					try {
						reply = (ReplicaReplyAvailableTimeSlots)sendMessage(send_msg, campus_str);
					} catch (IOException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					synchronized (reply_list) {
						reply_list.put(message_id, reply);
					}					
				}
			});
			thread.start();
			threads.add(thread);
		}
		System.out.println("threads.length(): " + threads.size()); 
		for (Thread thread : threads)
			thread.join();
		for (int msg_id : hm.keySet())
			ret.add(new TimeSlotResult(hm.get(msg_id), reply_list.get(msg_id).available_timeslot_number));
		return ret;
	}
	
	public boolean removeStudentRecord(String user_id, String booking_id)
	{
		StudentRecord record = null;
		synchronized (write_student_db_lock) {
			record = student_db.get(user_id);
		}
		if (record == null)
			return false;
		synchronized (record) {
			return record.removeBookRoomRequest(booking_id);
		}
	}

	public boolean cancelBooking(String user_id, String bookingID) throws NotBoundException, IOException, InterruptedException {
		CampusUser user = new CampusUser(user_id);
		String log_msg = String.format("received cancelBooking(user id: %s, booking id: %s)", user_id, bookingID);
		logger.info(LoggerHelper.format(log_msg));
		if (!user.isStudent())
		{
			logger.warning(String.format("user %s is not authorized to cancelBooking in this campus (%s)", 
					user_id, getName()));
			return false;
		}
		try
		{
			BookingIdGenerator big = new BookingIdGenerator(bookingID);
			boolean found = false;
			for (String campus_str : campus_comm.getAllCampusNames())
			{
				if (campus_str.equals(big.getCampusName()))
				{
					found = true;
					break;
				}
			}
			if (!found)
			{
				logger.warning("Invalid booking id: " + bookingID);
				return false;
			}
			UserDbOperations ops = new UserDbOperations(student_db, user) {
				
				@Override
				public boolean onUserBelongHere(StudentRecord record) {
					return true;
				}
				
				@Override
				public void onPostUserBelongHere(StudentRecord record) {
					_status = record.removeBookRoomRequest(bookingID);
				}
				
				@Override
				public boolean onOperationOnThisCampus(ArrayList<TimeSlot> time_slots) {
					for (TimeSlot time_slot : time_slots)
						if (time_slot.getBookingId().equals(bookingID) && time_slot.getUsername().equals(user_id))
						{
							time_slot.cancelTimeSlot();
							return true;
						}
					_status = false;
					return _status;
				}
				
				@Override
				public boolean onOperationOnOtherCampus(int message_id) throws NotBoundException, IOException, InterruptedException {
					int msg_id = MessageProtocol.generateMessageId();
					ReplicaRequestCancelBookRoom send_msg = new ReplicaRequestCancelBookRoom(msg_id, user_id, bookingID);				
					ReplicaReplyMessageStatus reply = (ReplicaReplyMessageStatus)sendMessage(send_msg, big.getCampusName());
					_status = reply.status;
					return _status;				
				}
				
				@Override
				public StudentRecord onNullUserRecord(CampusUser user) {_status = false; return null;}
				
				@Override
				public ArrayList<TimeSlot> findTimeSlots() {				
					ArrayList<TimeSlot> time_slots = Campus.this.findTimeSlots(big.getRoomNumber(), big.getDate());
					if (time_slots == null)
						_status = false;
					return time_slots;
				}
			};
			
			traverseStudentDb(ops, user, big.getCampusName());
			if (fake_result)
				return !ops._status;		
			return ops._status;		
		}
		catch(IllegalArgumentException ex)
		{
			logger.warning("Invalid booking id " + bookingID);
			return false;
		}
	}

	public void testMethod() {
		System.out.println("I am test!");
		
	}

	public int getPort() {
		return port;
	}

	public String getAddress() {
		return address;
	}

	/**
	 * @return the campus_type
	 */
	public CampusType getCampusType() {
		return campus_type;
	}

	/**
	 * @return the fake_result
	 */
	public boolean isFakeResult() {
		return fake_result;
	}

	/**
	 * @param fake_result the fake_result to set
	 */
	public void setFakeResult(boolean fake_result) {
		this.fake_result = fake_result;
	}

	/**
	 * @return the campus_container
	 */
	public CampusContainer getCampusContainer() {
		return campus_container;
	}
	
}
