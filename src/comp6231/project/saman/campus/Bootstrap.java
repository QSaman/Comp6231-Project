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
			Logger logger = LoggerHelper.getCampusServerLogger(campus_name);
			CampusCommunication comm = null;
			switch (com_type)
			{
			case RMI:
				Registry registry = LocateRegistry.getRegistry();
				comm = new RmiCampusCommunication(registry);
				break;
			case CORBA:
				comm = new CorbaCampusCommunication();
				break;
			case PROJECT:
				ProjectCampusCommunication tmp = new ProjectCampusCommunication();
				tmp.campus_names = Bootstrap.campus_names;
				HashMap<String, RemoteInfo> hm = new HashMap<>();
				for (int ii = 0; ii < campus_names.length; ++ii)
				{
					RemoteInfo ri = tmp.new RemoteInfo();
					ri.address = "127.0.0.1";
					ri.port = ports[ii];
					hm.put(campus_names[ii], ri);
				}
				tmp.campus_remote_info = hm;
				comm = tmp;
				break;
			default:
				return;
			}
			Campus campus = new Campus(campus_name, "127.0.0.1", port, logger, comm);
			HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>> db = new HashMap<>();
			HashMap<String, StudentRecord> student_db = new HashMap<>();
			campus.setDB(db);
			campus.setStudentDB(student_db);
			campus.starServer();
			campuses.add(campus);
		}		
		
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

}
