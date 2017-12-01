package comp6231.project.farid.servers.serverKirkland;

import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import comp6231.shared.Constants;

/**
 * Created by Farid on 9/24/2017.
 */
public class ReserveManager implements Serializable {

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

        if(!counterDB.containsKey(studentID)) {
            counterDB.put(studentID, new CountController(currentDate));
        }

        if (counterDB.containsKey(studentID)) {
            counterDB.get(studentID).incrementCounter();
        }
    }

    private void resetCounterInOtherServers() throws Exception {
        DatagramSocket clientSocket3 = new DatagramSocket();
        InetAddress IPAddress3 = InetAddress.getByName("localhost");
        byte[] sendData3 = new byte[1024];
        byte[] receiveData3 = new byte[1024];

        String stringToSend3 = "reset-" + studentID;
        sendData3 = ServerKirkland.sendMessageServerToserver(stringToSend3,studentID); 
        DatagramPacket sendPacket3 = new DatagramPacket(sendData3, sendData3.length, IPAddress3, Constants.wstPortListenFaridActive);
        clientSocket3.send(sendPacket3);

        DatagramPacket receivePacket3 = new DatagramPacket(receiveData3, receiveData3.length);
        clientSocket3.receive(receivePacket3);
        String result3 = new String(receivePacket3.getData());

        clientSocket3.close();

        DatagramSocket clientSocket4 = new DatagramSocket();
        InetAddress IPAddress4 = InetAddress.getByName("localhost");
        byte[] sendData4 = new byte[1024];
        byte[] receiveData4 = new byte[1024];

        String stringToSend4 = "reset-" + studentID;
        sendData4 = ServerKirkland.sendMessageServerToserver(stringToSend4,studentID); 
        DatagramPacket sendPacket4 = new DatagramPacket(sendData4, sendData4.length, IPAddress4, Constants.dvlPortListenFaridActive);
        clientSocket4.send(sendPacket4);

        DatagramPacket receivePacket4 = new DatagramPacket(receiveData4, receiveData4.length);
        clientSocket4.receive(receivePacket4);
        String result4 = new String(receivePacket4.getData());

        clientSocket4.close();

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
        ServerKirkland.kirklandServerLogger.log(log.toString());
    }

     public static LocalDate getBestExpireDateFromServers(String id) throws Exception{
        DatagramSocket clientSocket3 = new DatagramSocket();
        InetAddress IPAddress3 = InetAddress.getByName("localhost");
        byte[] sendData3 = new byte[1024];
        byte[] receiveData3 = new byte[1024];
        String stringToSend3 = "getExpire-" + id;
        sendData3 = ServerKirkland.sendMessageServerToserver(stringToSend3,""); 
        DatagramPacket sendPacket3 = new DatagramPacket(sendData3, sendData3.length, IPAddress3, Constants.wstPortListenFaridActive);
        clientSocket3.send(sendPacket3);

        DatagramPacket receivePacket3 = new DatagramPacket(receiveData3, receiveData3.length);
        clientSocket3.receive(receivePacket3);
        String result3 = new String(receivePacket3.getData());

        clientSocket3.close();

        DatagramSocket clientSocket4 = new DatagramSocket();
        InetAddress IPAddress4 = InetAddress.getByName("localhost");
        byte[] sendData4 = new byte[1024];
        byte[] receiveData4 = new byte[1024];

        String stringToSend4 = "getExpire-" + id;
        sendData4 = ServerKirkland.sendMessageServerToserver(stringToSend4,""); 
        DatagramPacket sendPacket4 = new DatagramPacket(sendData4, sendData4.length, IPAddress4, Constants.dvlPortListenFaridActive);
        clientSocket4.send(sendPacket4);

        DatagramPacket receivePacket4 = new DatagramPacket(receiveData4, receiveData4.length);
        clientSocket4.receive(receivePacket4);
        String result4 = new String(receivePacket4.getData());

        clientSocket4.close();

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
