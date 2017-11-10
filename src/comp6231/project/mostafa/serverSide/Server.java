package comp6231.project.mostafa.serverSide;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;

import ServerImpl.ServerInterface;
import ServerImpl.ServerInterfaceHelper;

public class Server {
	public static Logger log;

	public static void main(String[] args) throws Exception{
		if(args[4] == null){
			return;
		}
		
		Information.getInstance().initializeServerInformation(Integer.parseInt(args[4]));
		initializeLog();

        // create and initialize the ORB
        ORB orb = ORB.init(args, null);

        // get reference to rootpoa & activate the POAManager
        POA rootpoa =
                (POA)orb.resolve_initial_references("RootPOA");
        rootpoa.the_POAManager().activate();
		ServerImpl serverImpl = new ServerImpl();
		
		 // get object reference from the servant
        org.omg.CORBA.Object ref =
                rootpoa.servant_to_reference(serverImpl);
        // and cast the reference to a CORBA reference
        ServerInterface href = ServerInterfaceHelper.narrow(ref);

        // get the root naming context
        // NameService invokes the transient name service
        org.omg.CORBA.Object objRef =
                orb.resolve_initial_references("NameService");
        // Use NamingContextExt, which is part of the
        // Interoperable Naming Service (INS) specification.
        NamingContextExt ncRef =
                NamingContextExtHelper.narrow(objRef);

        // bind the Object Reference in Naming
        NameComponent path[] = ncRef.to_name( args[4] );
        ncRef.rebind(path, href);

		log(Information.getInstance().getServerName()+" Started");
		
		Thread t = new Thread(new UDPlistener());
		t.start();
		Thread time = new Thread(new Timer());
		time.start();
	}

	private static void initializeLog(){
		String id = Information.getInstance().getServerName();
		log = Logger.getLogger(id);
		FileHandler fh;  
		try {  

			// This block configure the logger with handler and formatter  
			fh = new FileHandler("/Users/wmg/Documents/workspace/Corba/"+id+".log");  
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
		log.info("ServerSide->"+"id: "+Information.getInstance().getServerName()+" Message: "+text);
	}
}
