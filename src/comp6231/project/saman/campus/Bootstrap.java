/**
 * 
 */
package comp6231.project.saman.campus;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import comp6231.project.saman.campus.Campus.CampusType;
import comp6231.project.saman.campus.CampusCommunication.RemoteInfo;
import comp6231.project.saman.campus.communication.ProjectCampusCommunication;
import comp6231.project.saman.campus.communication.RmiCampusCommunication;
import comp6231.project.saman.campus.communication.corba.CorbaCampusCommunication;
import comp6231.project.saman.common.DateReservation;
import comp6231.project.saman.common.LoggerHelper;
import comp6231.project.saman.common.TimeSlot;

/**
 * @author saman
 *
 */


public class Bootstrap {
	
	public enum CommunicationType
	{
		RMI,
		CORBA,
		PROJECT
	}
			
	public final static CommunicationType com_type = CommunicationType.PROJECT;
	public final static String corba_port = "1050";
	private static String[] campus_names = {"DVL", "KKL", "WST"};
	private static int[] ports = {7777, 7778, 7779};
	private static int[] backup_ports = {6667, 6668, 6669};
	//If you set the following variable to true, run rmiregistry command from your bin directory
	public final static boolean different_processes = false;

	public static ArrayList<Campus> campuses = new ArrayList<Campus>();
	public static boolean init = false;
		
	public static synchronized void initRmiServers() throws SecurityException, IOException
	{
		if (init || (com_type != CommunicationType.RMI && com_type != CommunicationType.PROJECT))
			return;
		init = true;
		if (!different_processes)
		{
			if (com_type == CommunicationType.RMI)
			{
				try {
					LocateRegistry.createRegistry(1099);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Java RMI registry created.");
			}
			initServers(campus_names, ports);
		}
	}
	
	public static synchronized void initServers(String[] campus_names, int[] ports) throws SecurityException, IOException {	
		for (int i = 0; i < campus_names.length; ++i)
		{
			String campus_name = campus_names[i];
			int port = ports[i];
			int backup_port = backup_ports[i];
			Logger logger = LoggerHelper.getCampusServerLogger(campus_name);
			CampusCommunication comm = null;
			CampusCommunication backup_comm = null;
			switch (com_type)
			{
			case RMI:
				Registry registry = LocateRegistry.getRegistry();
				comm = new RmiCampusCommunication(registry);
				backup_comm = new RmiCampusCommunication(registry);
				break;
			case CORBA:
				comm = new CorbaCampusCommunication();
				backup_comm = new CorbaCampusCommunication();
				break;
			case PROJECT:
				ProjectCampusCommunication tmp = new ProjectCampusCommunication();
				ProjectCampusCommunication backup_tmp = new ProjectCampusCommunication();
				
				tmp.campus_names = Bootstrap.campus_names;
				backup_tmp.campus_names = Bootstrap.campus_names;
				
				HashMap<String, RemoteInfo> hm = new HashMap<>();
				HashMap<String, RemoteInfo> backup_hm = new HashMap<>();
				for (int ii = 0; ii < campus_names.length; ++ii)
				{
					RemoteInfo ri = tmp.new RemoteInfo();
					RemoteInfo backup_ri = tmp.new RemoteInfo();
					ri.address = "127.0.0.1";
					backup_ri.address = "127.0.0.1";
					ri.port = ports[ii];
					backup_ri.port = backup_ports[ii];
					hm.put(campus_names[ii], ri);
					backup_hm.put(campus_names[ii], backup_ri);
				}
				tmp.campus_remote_info = hm;
				backup_tmp.campus_remote_info = backup_hm;
				comm = tmp;
				backup_comm = backup_tmp;				
				break;
			default:
				return;
			}
			Campus campus = new Campus(campus_name, "127.0.0.1", port, logger, comm, CampusType.Main);
			Campus backup_campus = new Campus(campus_name, "127.0.0.1", backup_port, logger, backup_comm, CampusType.Backup);
			HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>> db = new HashMap<>();
			HashMap<String, StudentRecord> student_db = new HashMap<>();
			campus.setDB(db);
			backup_campus.setDB(db);
			campus.setStudentDB(student_db);
			backup_campus.setStudentDB(student_db);
			campus.starServer();
//			if ((i % 2) == 0)
//				campus.starServer();
//			else
//				backup_campus.starServer();
			campuses.add(campus);
			campuses.add(backup_campus);
			
			// test case
			addTestValueToDataBase(campus);
			addTestValueToDataBase(backup_campus);
		}		
	}
	
	
	public static void save() throws Exception {
		SaverLoader saverLoader = new SaverLoader();
		saverLoader.copyServerToObject();
		try {
			saverLoader.serializeDataOut();
			System.out.println("Saved");
		} catch (IOException e) {
//			e.printStackTrace();
			System.out.println("ERROR IN SERIALIZING");
		}
	}
	
	public static void load() throws Exception{
		SaverLoader saverLoader = null;
		try {
			saverLoader = SaverLoader.serializeDataIn();
			saverLoader.copyObjectToServer();
			System.out.println("Loaded");
		} catch (ClassNotFoundException|IOException e) {
			System.out.println("ERROR IN LOADING");
		}
	}
	private static void addTestValueToDataBase(Campus campus) 
	{
		ArrayList<TimeSlot> time_slots = new ArrayList<>();
		time_slots.add(new TimeSlot("8:00 - 9:00"));
		time_slots.add(new TimeSlot("9:00 - 10:00"));
		time_slots.add(new TimeSlot("11:00 - 12:00"));
		time_slots.add(new TimeSlot("10:00 - 11:00"));
		time_slots.add(new TimeSlot("12:00 - 13:00"));
		campus.createRoom("DVLA1234", 1, new DateReservation("01-01-2017"), time_slots);
		campus.createRoom("KKLA1234", 1, new DateReservation("01-01-2017"), time_slots);
		campus.createRoom("WSTA1234", 1, new DateReservation("01-01-2017"), time_slots);
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws SecurityException 
	 * @throws NotBoundException 
	 */
	public static void main(String[] args) throws SecurityException, IOException, NotBoundException {
		Properties props = null;
		if (com_type == CommunicationType.CORBA)
		{
			props = new Properties();
			props.put("org.omg.CORBA.ORBInitialPort", corba_port);
			props.put("org.omg.CORBA.ORBInitialHost", "localhost");
		}
		if (args.length == 0)
		{
			switch (com_type)
			{
			case RMI:
				initRmiServers();
				break;
			case CORBA:
				initServers(campus_names, ports);
				CorbaCampusCommunication.run();
				break;
			case PROJECT:
				initServers(campus_names, ports);
				break;
			}
					
		}
		else if (args.length == 2)
		{
			String[] campus_names = new String[1];
			campus_names[0] = args[0].trim();
			int[] ports = new int[1];
			ports[0] = Integer.parseInt(args[1].trim());
			String[] new_args = null;
			initServers(campus_names, ports);
		}			
	}
	
	private static void testCase() {
		
	}
}
