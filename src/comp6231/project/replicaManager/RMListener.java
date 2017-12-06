package comp6231.project.replicaManager;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;

import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.messageProtocol.MessageHeader.CommandType;
import comp6231.project.messageProtocol.MessageHeader.ProtocolType;
import comp6231.project.replicaManager.messages.RMKillMessage;
import comp6231.shared.Constants;
import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocket;

public class RMListener implements Runnable {

	private ReliableServerSocket socket;

	@Override
	public void run() {
		socket = null;
		try {
			socket = new ReliableServerSocket(RMInformation.getInstance().getUDPListenPort());

			while(true){
				ReliableSocket aSocket = (ReliableSocket) socket.accept();
				new Thread(new Handler(aSocket)).start();
			}

		}catch (IOException e){
			ReplicaManager.log("Socket: " + e.getMessage());
		}

	}


	private class Handler implements Runnable{

		private ReliableSocket socket;

		public Handler(ReliableSocket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {
				InputStreamReader in = new InputStreamReader(socket.getInputStream());

				CharArrayWriter writer = new CharArrayWriter(Constants.BUFFER_SIZE);

				while(true) {
					int n = in.read();
					if( n < 0 ) break;
					writer.write(n);
				}

				handlePacket(writer.toString());

			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				if(socket !=null){try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}}
			}

		}

		private void handlePacket(String json_msg_str) {

			ReplicaManager.log("UDP Socket Received JSON: "+json_msg_str);
			process(json_msg_str);
		}
		
		private void process(String json_msg_str){
			MessageHeader json_msg = ReplicaManager.fromJson(json_msg_str);

			if(json_msg.protocol_type == ProtocolType.ReplicaManager_Message){
				if(json_msg.command_type == CommandType.Kill) {
					RMKillMessage message  = (RMKillMessage) json_msg;
				}else if (json_msg.command_type == CommandType.Fake_Generator) {
					
				}else {
					ReplicaManager.log("CommandType is incorrect");
				}
			}else{
				ReplicaManager.log("Protocol Type is incorrect");
			}

		}

		private void send(String data){
			try {
				ReliableSocket aSocket = new ReliableSocket();
				aSocket.connect(new InetSocketAddress("127.0.0.1", Constants.FE_PORT_LISTEN));
				OutputStreamWriter out = new OutputStreamWriter(aSocket.getOutputStream());
				out.write(data);
				out.flush();
				out.close();
				aSocket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
