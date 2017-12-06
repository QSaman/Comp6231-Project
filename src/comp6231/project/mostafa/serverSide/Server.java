package comp6231.project.mostafa.serverSide;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.google.gson.Gson;

import comp6231.project.messageProtocol.StartGson;

public class Server {
	public static Logger log;
	public static Gson gson;

	public static void main(String[] args) throws Exception{
		if(args[0] == null){
			return;
		}

		// save(); //This is for saving the getInstance of Database to the file
		// load(); //This is for loading and setting the instance of Database from the file

		Information.getInstance().initializeServerInformation(Integer.parseInt(args[0]));
		initializeLog();

		gson = Information.getInstance().isReOne == true ? StartGson.initGsonMostafa() : StartGson.initGsonRE2();
		log(Information.getInstance().getServerName()+" Started");

		Thread t = new Thread(new UDPlistener());
		t.start();
		Thread time = new Thread(new Timer());
		time.start();
		Database.getInstance().addTestCase();
	}

	private static void save() throws Exception {
		try {
			Database.getInstance().serializeDataOut();
			System.out.println("Saved");
		} catch (IOException e) {
			System.out.println("ERROR IN SERIALIZING");
		}
	}

	private static void load() throws Exception{
		try {
			Database.serializeDataIn();
			System.out.println("Loaded");
		} catch (ClassNotFoundException|IOException e) {
			System.out.println("ERROR IN LOADING");
		}
	}
	private static void initializeLog(){
		String id = Information.getInstance().getServerName();
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

		//		log.setUseParentHandlers(false);
	}

	public static void log(String text){
		log.info("ServerSide->"+"id: "+Information.getInstance().getServerName()+" Message: "+text);
	}
}
