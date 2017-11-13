package comp6231.project.mostafa.serverSide;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import comp6231.project.mostafa.core.Constants;

public class UDPlistener  implements Runnable {
	private DatagramSocket socket;
	
	@Override
	public void run() {
		socket = null;
		try {
			socket = new DatagramSocket(Information.getInstance().getUDPListenPort());
			byte[] buffer = new byte[1000];
			
			while(true){
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				socket.receive(request);
				handlePacket(request);
			}

		}catch (SocketException e){
			Server.log("Socket: " + e.getMessage());
		}catch (IOException e){
			Server.log("IO: " + e.getMessage());
		}finally {
			if(socket != null) socket.close();
		}
	}
	
	private void handlePacket(DatagramPacket request) {
			String stringBuffer = new String(request.getData(),0,request.getLength());
			String splited [] = stringBuffer.split(" ");
			
			Server.log("UDP Socket Received: "+stringBuffer);
			String result = process(splited);
			byte[] data = result.getBytes();

			send(request.getAddress(), request.getPort(), data);
	}
	
	private void send(InetAddress address, int port, byte[] data){
		DatagramPacket reply = new DatagramPacket(
				data, 
				data.length, 
				address, 
				port
		);

		try {
			socket.send(reply);
		} catch (IOException e) {
			Server.log(e.getMessage());
		}
	}
	
	private String process(String[] splited){
		String result = null;
		if(splited[0].equalsIgnoreCase(Constants.BOOK_ROOM)){
			int roomNumber =Integer.parseInt(splited[1]);
			String date = splited[2];
			String time = splited[3];
			String id = splited[4];
			String[] timeSplit = time.split("-");
 			result = Database.getInstance().tryToBookRoom(roomNumber, date, Information.getInstance().convertTimeToSec(timeSplit[0]), Information.getInstance().convertTimeToSec(timeSplit[1]), id);
		}else if (splited[0].equalsIgnoreCase(Constants.REDUCE_BOOK_COUNT)){
			String id = splited[1];
			result = Database.getInstance().reduceBookingCount(id);
		}else if (splited[0].equalsIgnoreCase(Constants.REQ_GETAVTIME)){
			String date = splited[1];
			result = Database.getInstance().findAvailableTimeSlot(date)+"";
		}else if (splited[0].equalsIgnoreCase(Constants.REQ_CANCEL_BOOK)){
			String bookingId = splited[1];
			String id = splited[2];
			result = Database.getInstance().cancelBookingId(bookingId, id);
		}else if(splited[0].equalsIgnoreCase(Constants.REQ_REMOVE_BOOK)){
			String bookingId = splited[1];
			boolean removeResult = Database.getInstance().removeBookingId(bookingId);
			if (removeResult){
				result = "bookingId: "+bookingId+" removed";
			}else{
				result = Constants.RESULT_UDP_FAILD;
			}
			
		}else if(splited[0].equals(Constants.COMMIT)){
			Database.getInstance().commit();
			result = "UDP-Database commited";
		}else if (splited[0].equals(Constants.ROLLBACK)){
			Database.getInstance().rollBack();
			result = "UDP-Database rollBacked";
		}
		return result;
	}
}
