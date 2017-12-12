package comp6231.project.saman.campus;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import comp6231.project.farid.servers.serverDorval.ServerDorval;
import comp6231.project.saman.common.DateReservation;
import comp6231.project.saman.common.TimeSlot;

public class SaverLoader implements Serializable {
	//
	/**
	 * 
	 */
	private static final long serialVersionUID = 8285664676826383466L;
	private int savedCurrentSequenceNumber;
	HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>> dataBase = new HashMap<>();
	HashMap<String, StudentRecord> students = new HashMap<>();
	
	
    public void setCurrentSequenceNumberByLoading() {
		UdpServer.setCurrentSequenceNumber(savedCurrentSequenceNumber);
	}

	public void setSavedCurrentSequenceNumber() {
		this.savedCurrentSequenceNumber = UdpServer.getCurrentSequenceNumber() + 1;
	}
	
    void copyObjectToServer(){
//    	setCurrentSequenceNumberByLoading();
    	
    	for (int i = 0; i < 6; ++i) {
		Bootstrap.campuses.get(0).db.clear();
		Bootstrap.campuses.get(0).student_db.clear();
    	}
    	
    	for (int i = 0; i < 6; ++i) {
		copyMap(dataBase, Bootstrap.campuses.get(0).db);
		copyMap(students, Bootstrap.campuses.get(0).student_db);
    	}
    	
    }
    
    void copyServerToObject() {
//    	setSavedCurrentSequenceNumber();
    	for (int i = 0; i < 6; ++i) {
		copyMap(Bootstrap.campuses.get(0).db, dataBase);
		copyMap(Bootstrap.campuses.get(0).student_db , students);
    	}
    }
    
    private <K, V, M extends Map<K, V>> void copyMap(M baseMap, M copyMap) {	
    	baseMap.forEach((k, v)->{
    		copyMap.put(k, v);
    	});
    }

	// Saving
	public void serializeDataOut()throws IOException{
		setSavedCurrentSequenceNumber();
	    String fileName= "samandb.txt";
	    FileOutputStream fos = new FileOutputStream(fileName);
	    ObjectOutputStream oos = new ObjectOutputStream(fos);
	    oos.writeObject(this);
	    oos.close();
	}
	
	public static SaverLoader serializeDataIn() throws IOException, ClassNotFoundException{
	   String fileName= "samandb.txt";
	   FileInputStream fin = new FileInputStream(fileName);
	   ObjectInputStream ois = new ObjectInputStream(fin);
	   SaverLoader saverLoader= (SaverLoader) ois.readObject();
	   saverLoader.setCurrentSequenceNumberByLoading();
	   ois.close();
	   return saverLoader;
	}

}
