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


import comp6231.project.saman.common.DateReservation;
import comp6231.project.saman.common.TimeSlot;

public class SaverLoader implements Serializable {
	//
	/**
	 * 
	 */
	private static final long serialVersionUID = 8285664676826383466L;

	HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>> dataBase = new HashMap<>();
	HashMap<String, StudentRecord> students = new HashMap<>();
	
    void copyObjectToServer(){
    	for (int i = 0; i < 6; ++i) {
		copyMap(dataBase, Bootstrap.campuses.get(0).db);
		copyMap(students, Bootstrap.campuses.get(0).student_db);
    	}
    	
    }
    
    void copyServerToObject() {
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
	    String fileName= "saman db.txt";
	    FileOutputStream fos = new FileOutputStream(fileName);
	    ObjectOutputStream oos = new ObjectOutputStream(fos);
	    oos.writeObject(this);
	    oos.close();
	}
	
	public static SaverLoader serializeDataIn() throws IOException, ClassNotFoundException{
	   String fileName= "saman db.txt";
	   FileInputStream fin = new FileInputStream(fileName);
	   ObjectInputStream ois = new ObjectInputStream(fin);
	   SaverLoader saverLoader= (SaverLoader) ois.readObject();
	   ois.close();
	   return saverLoader;
	}

}
