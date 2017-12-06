/**
 * 
 */
package comp6231.project.saman.campus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson;

import comp6231.project.frontEnd.messages.FEBookRoomRequestMessage;
import comp6231.project.frontEnd.messages.FECancelBookingMessage;
import comp6231.project.frontEnd.messages.FEChangeReservationMessage;
import comp6231.project.frontEnd.messages.FECreateRoomRequestMessage;
import comp6231.project.frontEnd.messages.FEDeleteRoomRequestMessage;
import comp6231.project.frontEnd.messages.FEGetAvailableTimeSlotMessage;
import comp6231.project.frontEnd.messages.FEReplyMessage;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.messageProtocol.MessageHeader.CommandType;
import comp6231.project.messageProtocol.MessageHeader.MessageType;
import comp6231.project.messageProtocol.MessageHeader.ProtocolType;
import comp6231.project.saman.campus.message_protocol.saman_replica.JsonMessage;
import comp6231.project.saman.campus.message_protocol.saman_replica.ReplicaRequestMessageHeader;
import comp6231.project.saman.campus.message_protocol.saman_replica.ReplyMessageHeader;
import comp6231.project.saman.common.DateReservation;
import comp6231.project.saman.common.TimeSlot;
import comp6231.project.saman.common.TimeSlotResult;


/**
 * @author saman
 *
 */
public class UdpServer extends Thread {
	private Campus campus;
	private DatagramSocket socket;	//https://stackoverflow.com/questions/6265731/do-java-sockets-support-full-duplex
	private final Object write_socket_lock = new Object();
	public final static int datagram_send_size = 256;
	JsonMessage json_message;
	Gson gson;
	
	public UdpServer(Campus campus, Gson gson) throws SocketException, RemoteException
	{
		this.gson = gson;
		this.campus = campus;
		socket = new DatagramSocket(this.campus.getPort());		
	}
	
	public void setJsonMessage(JsonMessage json_message)
	{
		this.json_message = json_message;
	}
	
	private FEReplyMessage handleFERequests(MessageHeader json_msg)
	{
		FEReplyMessage reply = null;
		String reply_msg = new String();
		String booking_id = new String();
		boolean boolean_res = false;
		switch (json_msg.command_type) {
		case Book_Room:
			FEBookRoomRequestMessage book_room_req = (FEBookRoomRequestMessage)json_msg;
			try {						
				booking_id = campus.bookRoom(book_room_req.userId, book_room_req.campusName, book_room_req.roomNumber, new DateReservation(book_room_req.date), 
						new TimeSlot(book_room_req.timeSlot));
				if (booking_id.isEmpty())
					reply_msg = "Booking operation wasn't successful";
				else
					reply_msg = "Book room completed successfully: " + booking_id; 
			} catch (NotBoundException | IOException | InterruptedException e) {
				e.printStackTrace();
				reply_msg = "Booking operation wasn't successful: " + e.getMessage();
			}
//			reply = new FEReplyMessage(book_room_req.sequence_number, book_room_req.command_type, 
//					reply_msg, true);
			reply = new FEReplyMessage(book_room_req.sequence_number, book_room_req.command_type, 
					reply_msg, true, booking_id, "Saman");
			break;					
		case Cancel_Book_Room:
			FECancelBookingMessage cancel_booking_req = (FECancelBookingMessage)json_msg;
			try {						
				 boolean_res = campus.cancelBooking(cancel_booking_req.user_id, cancel_booking_req.booking_id);
				if (boolean_res)
					reply_msg = "Booked time slot cancelled successfully!";
				else
					reply_msg = "Cannot cancel booked time slot!";
			} catch (NotBoundException | IOException | InterruptedException e) {
				e.printStackTrace();
				reply_msg = "Cannot cancel booked time slot!" + e.getMessage();
			}
			reply = new FEReplyMessage(cancel_booking_req.sequence_number, cancel_booking_req.command_type, reply_msg, true);
			break;
		case Change_Reservation:
			FEChangeReservationMessage change_res_req = (FEChangeReservationMessage)json_msg;
			try {
				booking_id = campus.changeReservation(change_res_req.user_id, change_res_req.booking_id, change_res_req.new_campus_name, 
						change_res_req.new_room_number, new DateReservation(change_res_req.new_date), new TimeSlot(change_res_req.new_time_slot));
				if (booking_id.isEmpty())
					reply_msg = "Cannot change reservation";
				else
					reply_msg = "new booking id: " + booking_id;
				
			} catch (NotBoundException | IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				reply_msg = "Cannot change reservation: " + e.getMessage();
			}
			reply = new FEReplyMessage(change_res_req.sequence_number, change_res_req.command_type, reply_msg, true, 
					booking_id, "Saman");
			break;
		case Create_Room:
			FECreateRoomRequestMessage create_room_req = (FECreateRoomRequestMessage)json_msg;
			//campus.createRoom(user_id, room_number, date, time_slots)
			boolean_res = campus.createRoom(create_room_req.userId, create_room_req.roomNumber, new DateReservation(create_room_req.date), 
					TimeSlot.toTimeSlot(create_room_req.timeSlots));
			if (boolean_res)
				reply_msg = "Create room is done successfully";
			else
				reply_msg = "Error in creating room";
			reply = new FEReplyMessage(create_room_req.sequence_number, create_room_req.command_type, reply_msg, true);							
			break;
		case Delete_Room:
			FEDeleteRoomRequestMessage del_room_req = (FEDeleteRoomRequestMessage)json_msg;
			//campus.deleteRoom(user_id, room_number, date, time_slots)
			boolean_res = campus.deleteRoom(del_room_req.userId, del_room_req.roomNumber, new DateReservation(del_room_req.date), 
					TimeSlot.toTimeSlot(del_room_req.timeSlots));
			if (boolean_res)
				reply_msg = "Deleting room is done successfully!";
			else
				reply_msg = "There is a problem in deleting room";
			reply = new FEReplyMessage(del_room_req.sequence_number, del_room_req.command_type, reply_msg, true);
			break;
		case Get_Available_TimeSlots:
			FEGetAvailableTimeSlotMessage avail_req = (FEGetAvailableTimeSlotMessage)json_msg;
			try {
				ArrayList<TimeSlotResult> all = campus.getAvailableTimeSlot(avail_req.user_id, new DateReservation(avail_req.date));
				reply_msg = TimeSlotResult.toString(all);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				reply_msg = "There is a problem in getting available time slots: " + e.getMessage();
			}
			//reply = new FEReplyMessage(sequenceNumber, commandType, replyMessage, status);
			reply = new FEReplyMessage(avail_req.sequence_number, avail_req.command_type, reply_msg, true);
			break;
		case LoginAdmin:
			break;
		case LoginStudent:
			break;
		case Quantity:
			break;
		case S_Remove_Student_Record:
			break;
		case S_Start_Week:
			break;
		case SignOut:
			break;
		default:
			break;
		}
		return reply;
	}
		
	private void processRequest(byte[] message, InetAddress address, int port)
	{
		String json_msg_str = new String(message);
		System.out.println(json_msg_str);
		MessageHeader json_msg = gson.fromJson(json_msg_str, MessageHeader.class);
		if (json_msg.protocol_type == ProtocolType.Server_To_Server)
		{
			if (json_msg.message_type == MessageType.Request)
			{
				ReplicaRequestMessageHeader tmp = (ReplicaRequestMessageHeader)json_msg;
				ReplyMessageHeader reply = tmp.handleRequest(campus);
				String reply_msg = gson.toJson(reply);
				try {
					sendDatagram(reply_msg.getBytes(), address, port);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else if (json_msg.message_type == MessageType.Reply)
				json_message.onReceivedReplyMessage((ReplyMessageHeader)json_msg);
		}
		else if (json_msg.protocol_type == ProtocolType.Frontend_To_Replica)
		{
			if (json_msg.message_type == MessageType.Request)
			{
				FEReplyMessage reply = handleFERequests(json_msg);
			}
			else if (json_msg.message_type == MessageType.Reply)
			{
				System.out.println("Reply messages are not supported for frontend to server");
			}
		}
	}
	
	public void sendDatagram(byte[] message, InetAddress address, int port) throws IOException
	{
		//System.out.println("Send message to " + address + ":" + port);
		DatagramPacket packet = new DatagramPacket(message, message.length, address, port);
		synchronized (write_socket_lock) {
			socket.send(packet);
		}		
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			byte[] buffer = new byte[datagram_send_size];
			DatagramPacket packet = new DatagramPacket(buffer, datagram_send_size);
			try {
				socket.receive(packet);
				Thread thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						UdpServer.this.processRequest(Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getLength()), packet.getAddress(), packet.getPort());
					}
				});
				thread.start();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
