package comp6231.project.mostafa.serverSide;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.messageProtocol.MessageHeader.CommandType;
import comp6231.project.messageProtocol.MessageHeader.ProtocolType;
import comp6231.project.mostafa.serverSide.messages.BookRoomMessage;
import comp6231.project.mostafa.serverSide.messages.CancelBookRoomMessage;
import comp6231.project.mostafa.serverSide.messages.CommitMessage;
import comp6231.project.mostafa.serverSide.messages.GetAvailableTimeSlotsMessage;
import comp6231.project.mostafa.serverSide.messages.ReduceBookCountMessage;
import comp6231.project.mostafa.serverSide.messages.RemoveBookingIdMessage;
import comp6231.project.mostafa.serverSide.messages.RollBackMessage;

public class UDPlistener  implements Runnable {
	private DatagramSocket socket;
	private final Object sendLock = new Object();
	
	@Override
	public void run() {
		socket = null;
		try {
			socket = new DatagramSocket(Information.getInstance().getUDPListenPort());
			byte[] buffer = new byte[1000];
			
			while(true){
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				socket.receive(request);

				Thread thread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						UDPlistener.this.handlePacket(request);
					}
				});
				thread.start();
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
			String json_msg_str = new String(request.getData(),0,request.getLength());

			Server.log("UDP Socket Received JSON: "+json_msg_str);
			String result = process(json_msg_str);
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
			synchronized (sendLock) {
				socket.send(reply);
			}
		} catch (IOException e) {
			Server.log(e.getMessage());
		}
	}
	
	private String process(String json_msg_str){
		String result = null;
		MessageHeader json_msg = Server.gson.fromJson(json_msg_str, MessageHeader.class);
		
		if(json_msg.protocol_type == ProtocolType.Server_To_Server){
			if(json_msg.command_type == CommandType.Book_Room){
				BookRoomMessage message = (BookRoomMessage)json_msg;
				result = message.handleRequest();
			}else if (json_msg.command_type == CommandType.M_Reduce_Book_Count){
				ReduceBookCountMessage message = (ReduceBookCountMessage)json_msg;
				result = message.handleRequest();
			}else if (json_msg.command_type == CommandType.Get_Available_TimeSlots){
				GetAvailableTimeSlotsMessage message = (GetAvailableTimeSlotsMessage)json_msg;
				result = message.handleRequest();
			}else if (json_msg.command_type == CommandType.Cancel_Book_Room){
				CancelBookRoomMessage message = (CancelBookRoomMessage)json_msg;
				result = message.handleRequest();
			}else if(json_msg.command_type == CommandType.M_Remove_BookingId){
				RemoveBookingIdMessage message = (RemoveBookingIdMessage)json_msg;
				result = message.handleRequest();
			}else if(json_msg.command_type == CommandType.M_Commit ){
				CommitMessage message = (CommitMessage)json_msg;
				result = message.handleRequest();
			}else if (json_msg.command_type == CommandType.M_Rollback){
				RollBackMessage message = (RollBackMessage)json_msg;
				result = message.handleRequest();
			}
		}else{
			// TODO  front end to server
		}

		return result;
	}

}
