package comp6231.project.farid.servers.serverWestmount;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Admin implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1645272403502706175L;
	private String adminID;

    public String createRoom(int roomNumber, LocalDate date, LinkedHashMap<LocalTime, LocalTime> listOfTimeSlots) {

        StringBuilder resultToSendToAdmin = new StringBuilder();

        resultToSendToAdmin.append("\n%%% STARTED - Result of creating room and time slots: for date ").append(date).append(", room number ").append(roomNumber).append(". by admin ").append(adminID);
        synchronized (Locker.databaseLock) {
            try {
                if (!ServerWestmount.dataBase.containsKey(date)) {
                    ServerWestmount.dataBase.put(date, new HashMap<>());
                    ServerWestmount.dataBase.get(date).put(roomNumber, new HashMap<>());
                }

                if (ServerWestmount.dataBase.containsKey(date) && !ServerWestmount.dataBase.get(date).containsKey(roomNumber)) {
                    ServerWestmount.dataBase.get(date).put(roomNumber, new HashMap<>());
                }

                if (ServerWestmount.dataBase.containsKey(date)) {
                    listOfTimeSlots.forEach((key, value) -> {
                        if (!isTimeConflict(date, roomNumber, key, value)) {
                            ServerWestmount.dataBase.get(date).get(roomNumber).put(key, value);
                            resultToSendToAdmin.append("\n Time slot: ").append(key).append(" / ").append(value).append(" for room number ").append(roomNumber).append(" date ").append(date).append(" successfully added.");
                        } else
                            resultToSendToAdmin.append("\n Time slot: ").append(key).append(" / ").append(value).append(" for room number ").append(roomNumber).append(" date ").append(date).append(" adding failed due to time conflict.");
                    });
                }
            } catch (Exception e) {
                return "!!! No data can be added with this information !!!";
            }
        }
        resultToSendToAdmin.append("\n=== FINISHED - Result of creating room and time slots. for date ").append(date).append(", room number ").append(roomNumber).append(".").append(" By admin ").append(adminID);

        ServerWestmount.westmountServerLogger.log(resultToSendToAdmin.toString());

        ServerWestmount.printCurrentDatabase();
        return resultToSendToAdmin.toString();
    }

    private boolean isTimeConflict(LocalDate date, int roomNumber, LocalTime startTime, LocalTime endTime) {
        synchronized (Locker.databaseLock) {
            for (Map.Entry<LocalTime, LocalTime> entry : ServerWestmount.dataBase.get(date).get(roomNumber).entrySet()) {

                if (entry.getKey().equals(startTime)
                        || startTime.isAfter(endTime)
                        || startTime.equals(endTime)
                        || endTime.equals(startTime)
                        || (entry.getKey().isBefore(startTime)
                        && startTime.isBefore(entry.getValue()))
                        || endTime.equals(entry.getValue())
                        || (entry.getKey().isBefore(endTime)
                        && endTime.isBefore(entry.getValue()))
                        || (startTime.isBefore(entry.getKey())
                        && endTime.isAfter(entry.getValue())))
                    return true;
            }
            return false;
        }
    }

    String deleteRoom(int roomNumber, LocalDate date, LinkedHashMap<LocalTime, LocalTime> listOfTimeSlots) {

        StringBuilder resultToSendToAdmin = new StringBuilder();
        resultToSendToAdmin.append("\n%%% STARTED - Result of deleting room and time slots: for the date ").append(date).append(", room number ").append(roomNumber).append(".").append("by admin ").append(adminID);

        try {
            listOfTimeSlots.forEach((key, value) -> {
                synchronized (Locker.databaseLock) {
                    if (ServerWestmount.dataBase.get(date).get(roomNumber).containsKey(key)
                            && ServerWestmount.dataBase.get(date).get(roomNumber).get(key).equals(value)) {
                        ServerWestmount.dataBase.get(date).get(roomNumber).remove(key, value);
                        resultToSendToAdmin.append("\n Time slot ").append(key).append(" / ").append(value).append(" for room number ").append(roomNumber).append(" date ").append(date).append(" successfully deleted.");
                    } else {
                        synchronized (Locker.reserveLock) {
                            List<ReserveManager> tempReserves = new ArrayList<>(ReserveManager.reserveMap.values());
                            for (ReserveManager reserveManager : tempReserves) {
                                if (reserveManager.getDate().equals(date)
                                        && reserveManager.getRoomNumber() == roomNumber
                                        && reserveManager.getStartTime().equals(key)
                                        && reserveManager.getEndTime().equals(value)) {
                                    String tempStudentID = reserveManager.getStudentID();

                                    //ReserveManager.reserveMap.forEach((key1, value1) -> {
                                    for (HashMap.Entry<String, ReserveManager> entry : ReserveManager.reserveMap.entrySet()) {
                                        if (entry.getValue().equals(reserveManager)) {
                                            ReserveManager.reserveMap.remove(entry.getKey(), entry.getValue());
                                            ReserveManager.counterDB.get(tempStudentID).decrementCounter();
                                            System.out.println(ReserveManager.counterDB.get(tempStudentID).getCounter());
                                            resultToSendToAdmin.append("\n Time slot ").append(key).append(" / ").append(value).append(" for room number ").append(roomNumber).append(" date ").append(date).append(" successfully deleted.");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
        } catch (Exception e){
            return "!!! No data found with this information !!!";
        }
        resultToSendToAdmin.append("\n=== FINISHED - Result of deleting room and time slots. for date ").append(date).append(", room number ").append(roomNumber).append(".").append("by admin ").append(adminID);
        ServerWestmount.westmountServerLogger.log(resultToSendToAdmin.toString());
        ServerWestmount.printCurrentDatabase();
        ReserveManager.printCurrentReserveList();
        return resultToSendToAdmin.toString();
    }

    boolean setAdminID(String adminID) {

            this.adminID = adminID;
            ServerWestmount.westmountServerLogger.log("\nAdmin " + adminID + " signed in.\n");
            return true;
    }

    public void signOut(){
        ServerWestmount.westmountServerLogger.log("\nAdmin " + adminID + " signed out.\n");

    }
}
