package comp6231.project.mostafa.serverSide;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Server {
	public static Logger log;

	public static void main(String[] args) throws Exception{
		if(args[4] == null){
			return;
		}
		
		Information.getInstance().initializeServerInformation(Integer.parseInt(args[4]));
		initializeLog();

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
