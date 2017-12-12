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
	private int savedCurrentSequenceNumber;
	ArrayList<HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>>> dataBase;
	ArrayList<HashMap<String, StudentRecord>> students;
	
	public SaverLoader() {
		dataBase = new ArrayList<>();
		students = new ArrayList<>();
		for(int i=0 ; i < 6; ++i) {
			dataBase.add(new HashMap<>());
			students.add(new HashMap<>());
		}
	}
	
	public void setCurrentSequenceNumberByLoading() {
		UdpServer.setCurrentSequenceNumber(savedCurrentSequenceNumber);
	}

	public void setSavedCurrentSequenceNumber() {
		this.savedCurrentSequenceNumber = UdpServer.getCurrentSequenceNumber() + 1;
	}

	void copyObjectToServer() {
		// setCurrentSequenceNumberByLoading();

		for (int i = 0; i < 6; ++i) {
			Bootstrap.campuses.get(i).db.clear();
			Bootstrap.campuses.get(i).student_db.clear();
		}

		for (int i = 0; i < 6; ++i) {
			copyDataBase(dataBase.get(i), Bootstrap.campuses.get(i).db);
			copyMap(students.get(i), Bootstrap.campuses.get(i).student_db);
		}

	}

	void copyServerToObject() {
		// setSavedCurrentSequenceNumber();
		for (int i = 0; i < 6; ++i) {
			copyDataBase(Bootstrap.campuses.get(i).db, dataBase.get(i));
			copyMap(Bootstrap.campuses.get(i).student_db, students.get(i));
		}
	}

	private <K, V, M extends Map<K, V>> void copyMap(M baseMap, M copyMap) {
		baseMap.forEach((k, v) -> {
			copyMap.put(k, v);
		});
	}
	
	private void copyDataBase(Map<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>> baseMapDb,
			Map<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>> copyMapDb) {
		baseMapDb.forEach((date, record) -> {
			record.forEach((roomNumber, times) -> {
				times.forEach((time) -> {
					if (copyMapDb.containsKey(date)) {
						if (copyMapDb.get(date).containsKey(roomNumber))
							copyMapDb.get(date).get(roomNumber).add(time);
						else {
							ArrayList<TimeSlot> tempTimes = new ArrayList<>();
							tempTimes.add(time);
							HashMap<Integer, ArrayList<TimeSlot>> tempRecord = new HashMap<>();
							tempRecord.put(roomNumber, tempTimes);
							copyMapDb.put(date, tempRecord);
						}
					} else {
						ArrayList<TimeSlot> tempTimes = new ArrayList<>();
						tempTimes.add(time);
						HashMap<Integer, ArrayList<TimeSlot>> tempRecord = new HashMap<>();
						tempRecord.put(roomNumber, tempTimes);
						copyMapDb.put(date, tempRecord);
					}
				});
			});
		});
	}
	
	private void copyArray(ArrayList<TimeSlot> baseList, ArrayList<TimeSlot> copyList) {
		baseList.forEach(timeSlot->{
			copyList.add(timeSlot);
		});
	}
	
	// Saving
	public void serializeDataOut() throws IOException {
		setSavedCurrentSequenceNumber();
		String fileName = "samandb.txt";
		FileOutputStream fos = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this);
		oos.close();
	}

	public static SaverLoader serializeDataIn() throws IOException, ClassNotFoundException {
		String fileName = "samandb.txt";
		FileInputStream fin = new FileInputStream(fileName);
		ObjectInputStream ois = new ObjectInputStream(fin);
		SaverLoader saverLoader = (SaverLoader) ois.readObject();
		saverLoader.setCurrentSequenceNumberByLoading();
		ois.close();
		return saverLoader;
	}

}
