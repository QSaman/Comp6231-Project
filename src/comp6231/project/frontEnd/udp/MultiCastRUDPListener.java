package comp6231.project.frontEnd.udp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocket;
import comp6231.project.frontEnd.FE;
import comp6231.project.frontEnd.Info;
import comp6231.project.frontEnd.messages.FEReplyMessage;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.messageProtocol.MessageHeader.MessageType;
import comp6231.project.messageProtocol.MessageHeader.ProtocolType;
import comp6231.shared.Constants;

public class MultiCastRUDPListener implements Runnable{
	private ReliableServerSocket socket;
	private static final Object lock = new Object();
	
	@Override
	public void run() {
		socket = null;
		try {
			socket = new ReliableServerSocket(Constants.FE_PORT_LISTEN);

			while(true){
				ReliableSocket clientSocket = (ReliableSocket) socket.accept();

				new Handler(clientSocket).start();
			}

		}catch (IOException e){
			FE.log("Socket: " + e.getMessage());
		}
	}

	class Handler extends Thread{
		private ReliableSocket socket;

		public Handler(ReliableSocket socket) {
			this.socket = socket;
		}

		@Override
		public void run(){
			try {
				InputStream in = socket.getInputStream();
				byte[] buffer = new byte[Constants.BUFFER_SIZE];

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				while(true) {
					int n = in.read(buffer);
					if( n < 0 ) break;
					baos.write(buffer,0,n);
				}

				byte data[] = baos.toByteArray();			    
				handlePacket(data);
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(socket !=null){try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}}
			}
		}

		private void handlePacket(byte[] buffer) {
			String json_msg_str = new String(buffer,0,buffer.length);

			FE.log("MULTI CAST UDP Socket Received JSON: "+json_msg_str);
			process(json_msg_str);
		}
		private void process(String json_msg_str){
			FE.log("message of json_msg_str: "+ json_msg_str);
			MessageHeader json_msg = FE.fromJson(json_msg_str);

			if(json_msg.protocol_type == ProtocolType.Frontend_To_Replica){
				if(json_msg.message_type == MessageType.Reply){
					FEReplyMessage replyMessage = (FEReplyMessage) json_msg;
					processFrontEndToserver(replyMessage, json_msg_str);
				}else{
					FE.log("MulitCast udp error in message type");
				}
			}else{
				FE.log("Multi cast udp error in protocol type");
			}
		}

		private void processFrontEndToserver(FEReplyMessage replyMessage, String json){

			FEPair pair = Sequencer.holdBack.get(replyMessage.sequence_number);

			synchronized (lock) {
				if(replyMessage.replicaId.contains("Mostafa")) {
					pair.infos.put(1, new Info(json, socket.getPort()));
				}else if (replyMessage.replicaId.contains("Farid")) {
					pair.infos.put(0, new Info(json, socket.getPort()));
				}else {
					pair.infos.put(2, new Info(json, socket.getPort()));
				}
			}

			pair.semaphore.release();
			FE.log("semaphore released for seqnum: "+ replyMessage.sequence_number + " with message: " + replyMessage.replyMessage + " with booking id: " +replyMessage.bookingId);
		}
	}


}