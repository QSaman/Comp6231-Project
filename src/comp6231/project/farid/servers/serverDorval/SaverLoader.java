package comp6231.project.farid.servers.serverDorval;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SaverLoader implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9139837675305655077L;

	private Map<LocalDate, HashMap<Integer, HashMap<LocalTime, LocalTime>>> dataBase = Collections
			.synchronizedMap(new HashMap<>());
	
	private Map<String, Admin> admins = Collections.synchronizedMap(new HashMap<>());
	private Map<String, Student> students = Collections.synchronizedMap(new HashMap<>());
	
    private Map<String, ReserveManager> reserveMap = Collections.synchronizedMap(new HashMap<>());
    private Map<String, CountController> counterDB = Collections.synchronizedMap(new HashMap<>());

    void copyObjectToServer(){
    	ServerDorval.dataBase.clear();
		ServerDorval.students.clear();
		ServerDorval.admins.clear();
		ReserveManager.reserveMap.clear();
		ReserveManager.counterDB.clear();

		copyDataBase(dataBase, ServerDorval.dataBase);
		copyMap(students, ServerDorval.students);
		copyMap(admins, ServerDorval.admins);
		copyMap(reserveMap, ReserveManager.reserveMap);
		copyMap(counterDB, ReserveManager.counterDB);
    }
    
    void copyServerToObject() {
		copyDataBase(ServerDorval.dataBase, dataBase);
		copyMap(ServerDorval.students, students);
		copyMap(ServerDorval.admins, admins);
		copyMap(ReserveManager.reserveMap, reserveMap);
		copyMap(ReserveManager.counterDB, counterDB);
    }
    
    private <K, V, M extends Map<K, V>> void copyMap(M baseMap, M copyMap) {	
    	baseMap.forEach((k, v)->{
    		copyMap.put(k, v);
    	});
    }

	private void copyDataBase(Map<LocalDate, HashMap<Integer, HashMap<LocalTime, LocalTime>>> baseMapDb,
			Map<LocalDate, HashMap<Integer, HashMap<LocalTime, LocalTime>>> copyMapDb) {
		baseMapDb.forEach((date, record) -> {
			record.forEach((roomNumber, times) -> {
				times.forEach((start, end) -> {
					if (copyMapDb.containsKey(date)) {
						if (copyMapDb.get(date).containsKey(roomNumber))
							copyMapDb.get(date).get(roomNumber).put(start, end);
						else {
							HashMap<LocalTime, LocalTime> tempTimes = new HashMap<>();
							tempTimes.put(start, end);
							HashMap<Integer, HashMap<LocalTime,LocalTime>> tempRecord = new HashMap<>();
							tempRecord.put(roomNumber, tempTimes);
							copyMapDb.put(date, tempRecord);
						}
					} else {
						HashMap<LocalTime, LocalTime> tempTimes = new HashMap<>();
						tempTimes.put(start, end);
						HashMap<Integer, HashMap<LocalTime,LocalTime>> tempRecord = new HashMap<>();
						tempRecord.put(roomNumber, tempTimes);
						copyMapDb.put(date, tempRecord);
					}
				});
			});
		});
	}
	
	// Save on file
	public void serializeDataOut()throws IOException{
	    String fileName= "ServerDorvalSavedData.txt";
	    FileOutputStream fos = new FileOutputStream(fileName);
	    ObjectOutputStream oos = new ObjectOutputStream(fos);
	    oos.writeObject(this);
	    oos.close();
	}

	void printCurrentDatabase() {
		StringBuilder log = new StringBuilder();
		log.append("\n$$$ CURRENT DATABASE:");
		dataBase.forEach((key, value) -> value.forEach((key1, value1) -> log.append("\nDate:").append(key)
				.append("   Room number: ").append(key1).append("   Time slots:").append(sortByValue(value1))));
		log.append("\n");
		System.out.println(log.toString());
	}
	
	private <K, V extends Comparable<? super V>> HashMap<K, V> sortByValue(HashMap<K, V> hashMap) {
		return hashMap.entrySet().stream().sorted(HashMap.Entry.comparingByValue()).collect(
				Collectors.toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}
	
	public static SaverLoader serializeDataIn() throws IOException, ClassNotFoundException{
	   String fileName= "ServerDorvalSavedData.txt";
	   FileInputStream fin = new FileInputStream(fileName);
	   ObjectInputStream ois = new ObjectInputStream(fin);
	   SaverLoader saverLoader= (SaverLoader) ois.readObject();
	   ois.close();
	   return saverLoader;
	}

}
