package comp6231.project.mostafa.serverSide;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import ServerImpl.ServerInterfacePOA;
import core.Constants;

public class ServerImpl extends ServerInterfacePOA {

	private static final Object lock = new Object();

	protected ServerImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String bookRoom(String campusName, int roomNumber, String date,
			String time, String id){
		if(!Information.getInstance().isUserClient(id)){
			return "access denied the id is not in a right format for user";
		}
		
		String result = "";
		String[] timeSplit = time.split("-");
		String bookingId;
		synchronized(lock){
			if(Database.getInstance().isBookCountReachedToLimit(id)){
				return "the book count for student< "+id+ " >is reached to limit.";
			}else{
				if(campusName.equalsIgnoreCase(Information.getInstance().getServerCode())){
					bookingId = Database.getInstance().tryToBookRoom(roomNumber, date, Information.getInstance().convertTimeToSec(timeSplit[0]), Information.getInstance().convertTimeToSec(timeSplit[1]), id);
					if(Information.getInstance().isBooked(bookingId)){
						Database.getInstance().increaseBookingCount(id);
					}
					result = result +" studnet: "+id+" roomNumber: "+roomNumber+" on date: "+date+" in time: "+time+" with bookingId "+bookingId+"\n";
				}else{
					int serverPort = Information.getInstance().findUDPPort(campusName);
					String data = Constants.BOOK_ROOM+" "+roomNumber+" "+date+" "+time+" "+id;
					if(serverPort != -1){
						UDP thread;
						thread = new UDP(data, serverPort, "");
						thread.start();
						try {
							thread.join();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						bookingId = thread.getResult();
						if(Information.getInstance().isBooked(bookingId)){
							Database.getInstance().increaseBookingCount(id);
						}
						result = result +"UDP RESPONES: "+  "studnet: "+id+" roomNumber: "+roomNumber+" on date: "+date+" in time: "+time+" with bookingId "+bookingId+"\n";
					}else{
						result = "the book operation for student< "+id+ " > was faild.(PORT)";
					}
				}
			}
		}
		Server.log(result);
		return result;
	}

	@Override
	public String create(int roomNumber, String date, String[] time,
			String id) {
		if(!Information.getInstance().isUserAdmin(id)){
			return "access denied the id is not in a right format for admin";
		}
		
		String result = "";
		for(int j = 0 ; j < time.length; ++j){
			String splited [] = time[j].split("-");
			boolean added = Database.getInstance().addToDatabase(date, roomNumber,Information.getInstance().convertTimeToSec(splited[0]), Information.getInstance().convertTimeToSec(splited[1]));
			if(added){
				result = result + "id: "+id+" roomNumber: "+roomNumber+" date: "+date+" time: "+time[j]+" created\n";
			}else{
				result = result + "id: "+id+" roomNumber: "+roomNumber+" date: "+date+" time: "+time[j]+" HasTimeConfilict\n";
			}

		}
		Server.log(result);
		return result;
	}

	@Override
	public String delete(int roomNumber, String date, String[] time,
			String id){
		if(!Information.getInstance().isUserAdmin(id)){
			return "access denied the id is not in a right format for admin";
		}
		
		String result = "";
		for(int j = 0 ; j < time.length; ++j){
			String splited [] = time[j].split("-");
			Database.deleteStatus removed = Database.getInstance().removeFromDatabase(date, roomNumber,Information.getInstance().convertTimeToSec(splited[0]), Information.getInstance().convertTimeToSec(splited[1]));
			if(removed == Database.deleteStatus.DELETE_DATE_NOSTEXIST){
				result = result + "id: "+id+" roomNumber: "+roomNumber+" date: "+date+" time: "+time[j]+" DateDoesNotExist\n";
			}else if (removed == Database.deleteStatus.DELETE_ROOMNU_NOTEXIST){
				result = result + "id: "+id+" roomNumber: "+roomNumber+" date: "+date+" time: "+time[j]+" RoomNumberDoesNotExist\n";
			}else if(removed == Database.deleteStatus.DELETE_TIME_NOTEXIST){
				result = result + "id: "+id+" roomNumber: "+roomNumber+" date: "+date+" time: "+time[j]+" TimeSlotDoesNotExist\n";
			}else if(removed == Database.deleteStatus.DELETE_FAILD){
				result = result + "id: "+id+" roomNumber: "+roomNumber+" date: "+date+" time: "+time[j]+" DeleteOperationFaild\n";
			}else if(removed == Database.deleteStatus.DELETE_SUCC){
				result = result + "id: "+id+" roomNumber: "+roomNumber+" date: "+date+" time: "+time[j]+" Deleted\n";
			}else if(removed == Database.deleteStatus.DELETE_BOOKED){
				result = result + "id: "+id+" roomNumber: "+roomNumber+" date: "+date+" time: "+time[j]+" Deleted-roomHasbeenBookedBefore\n";
			}
		}
		Server.log(result);
		return result;
	}

	@Override
	public String getAvailableTimeSlot(String date, String id){
		if(!Information.getInstance().isUserClient(id)){
			return "access denied the id is not in a right format for user";
		}
		
		String result = "";
		if(Information.getInstance().getServerCode().equalsIgnoreCase("DVL")){
			List<UDP> threads = new ArrayList<UDP>();
			int dvlCount = Database.getInstance().findAvailableTimeSlot(date);
			
			threads.add(new UDP(Constants.REQ_GETAVTIME+" "+date, Constants.KKL_PORT_LISTEN, "KKL"));
			threads.add(new UDP(Constants.REQ_GETAVTIME+" "+date, Constants.WST_PORT_LISTEN, "WST"));
			for (UDP udp : threads) {
				udp.start();
			}
			
			for (UDP udp : threads) {
				try {
					udp.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			for (UDP udp : threads) {
				result = result+ udp.getUdpId()+" "+Integer.parseInt(udp.getResult())+","+" ";
			}
			result = result+ "DVL"+" "+dvlCount;

		}else if (Information.getInstance().getServerCode().equalsIgnoreCase("KKL")){
			List<UDP> threads = new ArrayList<UDP>();
			int kklCount = Database.getInstance().findAvailableTimeSlot(date);

			threads.add(new UDP(Constants.REQ_GETAVTIME+" "+date, Constants.DVL_PORT_LISTEN, "DVL"));
			threads.add(new UDP(Constants.REQ_GETAVTIME+" "+date, Constants.WST_PORT_LISTEN, "WST"));
			for (UDP udp : threads) {
				udp.start();
			}
			
			for (UDP udp : threads) {
				try {
					udp.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			for (UDP udp : threads) {
				result = result+ udp.getUdpId()+" "+Integer.parseInt(udp.getResult())+","+" ";
			}
			result = result+ "KKL"+" "+kklCount;
		}else if (Information.getInstance().getServerCode().equalsIgnoreCase("WST")){
			List<UDP> threads = new ArrayList<UDP>();
			int wstCount = Database.getInstance().findAvailableTimeSlot(date);

			threads.add(new UDP(Constants.REQ_GETAVTIME+" "+date, Constants.KKL_PORT_LISTEN, "KKL"));
			threads.add(new UDP(Constants.REQ_GETAVTIME+" "+date, Constants.DVL_PORT_LISTEN, "DVL"));
			for (UDP udp : threads) {
				udp.start();
			}
			
			for (UDP udp : threads) {
				try {
					udp.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			for (UDP udp : threads) {
				result = result+ udp.getUdpId()+" "+Integer.parseInt(udp.getResult())+","+" ";
			}
			result = result+ "WST"+" "+wstCount;
		}
		
		Server.log("getAvailableTimeSlot: "+result);
		return result;
	}

	@Override
	public String CancelBookingId(String bookingId, String id){
		if(!Information.getInstance().isUserClient(id)){
			return "access denied the id is not in a right format for user";
		}
		
		String result = "";
		UDP thread = null;
		if(Information.getInstance().getServerCode().equalsIgnoreCase("DVL")){
			if(bookingId.contains("DVL")){
				result = Database.getInstance().cancelBookingId(bookingId, id);
			}else if(bookingId.contains("WST")){
				thread = new UDP(Constants.REQ_CANCEL_BOOK+" "+bookingId+" "+id, Constants.WST_PORT_LISTEN, "WST");
			}else{
				thread = new UDP(Constants.REQ_CANCEL_BOOK+" "+bookingId+" "+id, Constants.KKL_PORT_LISTEN, "KKL");
			}
		}else if(Information.getInstance().getServerCode().equalsIgnoreCase("KKL")){
			if(bookingId.contains("KKL")){
				result = Database.getInstance().cancelBookingId(bookingId, id);
			}else if(bookingId.contains("WST")){
				thread = new UDP(Constants.REQ_CANCEL_BOOK+" "+bookingId+" "+id, Constants.WST_PORT_LISTEN, "WST");
			}else{
				thread = new UDP(Constants.REQ_CANCEL_BOOK+" "+bookingId+" "+id, Constants.DVL_PORT_LISTEN, "DVL");
			}
		}else{
			if(bookingId.contains("WST")){
				result = Database.getInstance().cancelBookingId(bookingId, id);
			}else if(bookingId.contains("KKL")){
				thread = new UDP(Constants.REQ_CANCEL_BOOK+" "+bookingId+" "+id, Constants.KKL_PORT_LISTEN, "KKL");
			}else{
				thread = new UDP(Constants.REQ_CANCEL_BOOK+" "+bookingId+" "+id, Constants.DVL_PORT_LISTEN, "DVL");
			}
		}
		if(thread != null){
			thread.start();
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			result = thread.getResult();
		}

		Server.log(result);
		return result;
	}

	@Override
	public String changeReservation(String bookingId, String newCampusName, String date,
			int newRoomNo, String newTimeSlot, String id) {
		if(!Information.getInstance().isUserClient(id)){
			return "access denied the id is not in a right format for user";
		}
		
		String result = "";
		
		//commit
		Information.getInstance().transaction(true);
		
		String cancelResult = CancelBookingId(bookingId, id);
		if(cancelResult.contains("canceled")){
			String bookRoomResult;
			bookRoomResult = bookRoom(newCampusName, newRoomNo, date, newTimeSlot, id);
			if(bookRoomResult.contains("booked") && !bookRoomResult.contains("notbooked")){
				result = "changeReservation successfully done" + bookRoomResult;
			}else{
				result = "changeReservation Faild becuase(bookingFailure): "+ bookRoomResult;
				//rollBack
				Information.getInstance().transaction(false);
				Server.log("rollBack happened");
			}
		}else{
			result = "changeReservation Faild becuase(cancelingFailure): "+ cancelResult;
		}
		Server.log(result);
		return result;
	}

}
