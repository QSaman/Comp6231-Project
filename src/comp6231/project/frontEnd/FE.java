package comp6231.project.frontEnd;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import com.google.gson.Gson;

import comp6231.project.common.corba.users.AdminOperations;
import comp6231.project.common.corba.users.AdminOperationsHelper;
import comp6231.project.common.corba.users.StudentOperations;
import comp6231.project.common.corba.users.StudentOperationsHelper;
import comp6231.project.frontEnd.udp.MultiCastRUDPListener;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.messageProtocol.StartGson;

public class FE {
	public static Logger log;
	private static Gson gson;
	private static final Object lock = new Object();
	
	public static void main(String[] args) throws ServantNotActive, WrongPolicy, InvalidName, AdapterInactive, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound, CannotProceed {
		confing(args);
		
		gson = StartGson.initFE();
		
		initializeLog();
		log("FE"+" Started");
		
		Thread thread = new Thread(new MultiCastRUDPListener());
		thread.start();
	}
	
	private static void initializeLog(){
		String id = "FE";
		log = Logger.getLogger(id);
		FileHandler fh;  
		try {  

			// This block configure the logger with handler and formatter  
			fh = new FileHandler("log/fe/"+id+".log");  
			log.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();  
			fh.setFormatter(formatter);  

			// the following statement is used to log any messages  
			log(id+" created");

		} catch (SecurityException e) {  
			e.printStackTrace();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}
		
//		log.setUseParentHandlers(false);
	}
		
	public static void log(String text){
		log.info("FE-> Message: "+text);
	}
	
	private static void confing(String[] args) throws ServantNotActive, WrongPolicy, InvalidName, AdapterInactive, org.omg.CosNaming.NamingContextPackage.InvalidName, NotFound, CannotProceed{
        // create and initialize the ORB
        ORB orb = ORB.init(args, null);

        // get reference to rootpoa & activate the POAManager
        POA rootpoa =
                (POA)orb.resolve_initial_references("RootPOA");
        rootpoa.the_POAManager().activate();
		AdminImpl adminImpl = new AdminImpl();
		StudentImpl studentImpl = new StudentImpl();
		
		 // get object reference from the servant
        org.omg.CORBA.Object sRef =
                rootpoa.servant_to_reference(studentImpl);
        
		 // get object reference from the servant
        org.omg.CORBA.Object aRef =
                rootpoa.servant_to_reference(adminImpl);
        // and cast the reference to a CORBA reference
        AdminOperations aHref = AdminOperationsHelper.narrow(aRef);
        StudentOperations sHref = StudentOperationsHelper.narrow(sRef);
        
        // get the root naming context
        // NameService invokes the transient name service
        org.omg.CORBA.Object objRef =
                orb.resolve_initial_references("NameService");
        // Use NamingContextExt, which is part of the
        // Interoperable Naming Service (INS) specification.
        NamingContextExt ncRef =
                NamingContextExtHelper.narrow(objRef);

        // bind the Object Reference in Naming
        NameComponent aPath[] = ncRef.to_name("FEA");
        NameComponent sPath[] = ncRef.to_name("FES");
        ncRef.rebind(aPath, aHref);
        ncRef.rebind(sPath, sHref);
	}
	
	public static MessageHeader fromJson(String json){
		synchronized (lock) {
			return FE.gson.fromJson(json, MessageHeader.class);
		}
	}
	
	public static String toJson(MessageHeader args){
		synchronized (lock) {
			return FE.gson.toJson(args);
		}
	}
}
