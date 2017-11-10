package comp6231.project.mostafa.clientSide;

import java.util.HashMap;

import core.CommandEnum;

public class Utility {

	private static Utility instance = null;
	
	public HashMap<String, Client> clients; 
	
	private Utility(){
		clients = new HashMap<String, Client>();
	}

	/**
	 * @return the instance
	 */
	public static Utility getInstance() {
		if(instance == null){
			instance = new Utility();
		}
		return instance;
	}
		
	public CommandEnum findCommand(String id){
		if(id.contains("create")){
			return CommandEnum.CREATE;
		}else if (id.contains("delete")){
			return CommandEnum.DELETE;
		}else if (id.contains("bookroom")){
			return CommandEnum.BOOKROOM;
		}else if(id.contains("getavltimeSlot")){
			return CommandEnum.GETAVTIMESLOT;
		}else if(id.contains("cancelbooking")){
			return CommandEnum.CANCELBOOKING;
		}else if(id.contains("changereservation")){
			return CommandEnum.CHANGERESERVATION;
		}else{
			return CommandEnum.UNDEFINED;
		}
	}
	
	public boolean isUserClient(String id){
		if(id.contains("dvls")){
			return true;
		}else if (id.contains("kkls")){
			return true;
		}else if (id.contains("wsts")){
			return true;
		}else {
			return false;
		}
	}
	
	public boolean isUserAdmin(String id){
		if(id.contains("dvla")){
			return true;
		}else if (id.contains("kkla")){
			return true;
		}else if (id.contains("wsta")){
			return true;
		}else {
			return false;
		}
	}
	
	public boolean isClientExist(String id){
		Client client = clients.get(id);
		
		if(client !=null){
			return true;
		}
		return false;
	}
}
