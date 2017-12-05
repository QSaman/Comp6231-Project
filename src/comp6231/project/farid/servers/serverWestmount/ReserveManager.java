package comp6231.project.farid.servers.serverWestmount;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import comp6231.project.farid.sharedPackage.UdpSender;
import comp6231.shared.Constants;

/**
 * Created by Farid on 9/24/2017.
 */
public class ReserveManager implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2287146796418085925L;
	static Map<String, ReserveManager> reserveMap = Collections.synchronizedMap(new HashMap<>());
    static Map<String, CountController> counterDB = Collections.synchronizedMap(new HashMap<>());

    private int roomNumber;
    private String studentID;
    private String bookingID;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    ReserveManager(int roomNumber, String studentID, String bookingID, LocalDate date, LocalTime startTime, LocalTime endTime, LocalDate currentDate) throws Exception {
        this.roomNumber = roomNumber;
        this.studentID = studentID;
        this.bookingID = bookingID;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        synchronized (Locker.reserveLock) {
            reserveMap.put(bookingID, this);
        }

        if (counterDB.containsKey(studentID)
                && getBestExpireDateFromServers(studentID).isBefore(currentDate)){
            counterDB.get(studentID).reset(currentDate);
            resetCounterInOtherServers();
        }

        if (!counterDB.containsKey(studentID)) {
            counterDB.put(studentID, new CountController(currentDate));
        }

        if (counterDB.containsKey(studentID)) {
            counterDB.get(studentID).incrementCounter();
        }
    }

    private void resetCounterInOtherServers() throws Exception {
        String stringToSend3 = "reset-" + studentID;
        byte [] sendData3 = ServerWestmount.sendMessageServerToserver(stringToSend3,studentID);
        UdpSender sender = new UdpSender(sendData3, Constants.kklPortListenFaridActive, "");
        sender.start();
        
        sender.join();
        String result3 = sender.getResult();
        ServerWestmount.westmountServerLogger.log(result3);

        String stringToSend4 = "reset-" + studentID;
        byte[] sendData4 = ServerWestmount.sendMessageServerToserver(stringToSend4,studentID);
        UdpSender sender2 = new UdpSender(sendData4, Constants.dvlPortListenFaridActive, "");
        sender2.start();
        
        sender.join();
        String result4 = sender.getResult();
        ServerWestmount.westmountServerLogger.log(result4);

    }
    static boolean removeFromReserveList(String studentID, String bookingID) throws Exception {
        synchronized (Locker.reserveLock) {
            if (reserveMap.containsKey(bookingID) && reserveMap.get(bookingID).getStudentID().equals(studentID)) {
                LinkedHashMap<LocalTime, LocalTime> tempMap = new LinkedHashMap<>();
                tempMap.put(reserveMap.get(bookingID).getStartTime(), reserveMap.get(bookingID).getEndTime());
                new Admin().createRoom(reserveMap.get(bookingID).getRoomNumber(),
                        reserveMap.get(bookingID).getDate(), tempMap);

                reserveMap.remove(bookingID);
                synchronized (Locker.counterLock) {
                    ReserveManager.counterDB.get(studentID).decrementCounter();
                }
                return true;
            }
            return false;
        }
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getStudentID() {
        return studentID;
    }

    public LocalDate getDate() {
        return date;
    }

    LocalTime getStartTime() {
        return startTime;
    }

    LocalTime getEndTime() {
        return endTime;
    }

    static void printCurrentReserveList() {

        StringBuilder log = new StringBuilder();
        log.append("\n$$$ CURRENT RESERVE LIST:");
        synchronized (Locker.reserveLock) {
            for (HashMap.Entry<String, ReserveManager> entry : reserveMap.entrySet()) {

                log.append("\n").append(entry.getValue());

            }
        }
        ServerWestmount.westmountServerLogger.log(log.toString());
    }

    public static LocalDate getBestExpireDateFromServers(String id) throws Exception{
        String stringToSend3 = "getExpire-" + id;
        byte [] sendData3 = ServerWestmount.sendMessageServerToserver(stringToSend3,"");
        UdpSender sender = new UdpSender(sendData3, Constants.kklPortListenFaridActive, "");
        UdpSender sender2 = new UdpSender(sendData3, Constants.dvlPortListenFaridActive, "");
        sender.start();
        sender2.start();
        
        sender.join();
        sender2.join();
        
        String result3 = sender.getResult();
        String result4 = sender2.getResult();

        String expireDateString1 = result3.trim();
        LocalDate expireDate1 = (expireDateString1.equals("0")?LocalDate.now():LocalDate.of(Integer.parseInt(expireDateString1.substring(0,4))
                ,Integer.parseInt(expireDateString1.substring(5,7))
                ,Integer.parseInt(expireDateString1.substring(8,expireDateString1.length()))));
        String expireDateString2 = result4.trim();
        LocalDate expireDate2 = (expireDateString2.equals("0")?LocalDate.now():LocalDate.of(Integer.parseInt(expireDateString2.substring(0,4))
                ,Integer.parseInt(expireDateString2.substring(5,7))
                ,Integer.parseInt(expireDateString2.substring(8,expireDateString2.length()))));
        LocalDate expireDate3 = (ReserveManager.counterDB.containsKey(id) ?
                ReserveManager.counterDB.get(id).getExpireDate() : LocalDate.now());

        LocalDate finalDate = (expireDate1.isBefore(expireDate2)
                ?
                (expireDate1.isBefore(expireDate3)?expireDate1:(expireDate3.isBefore(expireDate2)?expireDate3:expireDate2))
                :
                (expireDate2.isBefore(expireDate3)?expireDate2:(expireDate3.isBefore(expireDate1)?expireDate3:expireDate1)));
        return finalDate;
    }
    @Override
    public String toString() {
        return "ReserveManager{" +
                "studentID='" + studentID + '\'' +
                ", bookingID='" + bookingID + '\'' +
                ", date=" + date +
                ", room number=" + roomNumber +
                ", timeSlot= " + startTime + " / " + endTime +
                '}';
    }
}
