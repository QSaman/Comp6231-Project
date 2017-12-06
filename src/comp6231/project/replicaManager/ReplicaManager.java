package comp6231.project.replicaManager;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.google.gson.Gson;

import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.messageProtocol.StartGson;

public class ReplicaManager {
	// logger is thread safe 
	public static Logger log;
	private static Gson gson;
	private static final Object lock = new Object();
	
	public static void main(String[] args) {
		 
		RMInformation.getInstance().initializeServerInformation(args[0]);
		initializeLog();
		
		gson = StartGson.initReplicaManager();
		
		Thread listener = new Thread(new RMListener());
		listener.start();
	}
	
	private static void initializeLog(){
		String id = RMInformation.getInstance().getRmName();
		log = Logger.getLogger(id);
		FileHandler fh;  
		try {  

			// This block configure the logger with handler and formatter  
			fh = new FileHandler("log/mostafa/"+id+".log");  
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
	}

	public static void log(String text){
		log.info("ServerSide->"+"id: "+RMInformation.getInstance().getRmName()+" Message: "+text);
	}
	
	public static MessageHeader fromJson(String json){
		synchronized (lock) {
			return gson.fromJson(json, MessageHeader.class);
		}
	}
	
	public static String toJson(MessageHeader args){
		synchronized (lock) {
			return gson.toJson(args);
		}
	}
}
