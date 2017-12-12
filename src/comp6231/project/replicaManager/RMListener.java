package comp6231.project.replicaManager;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import comp6231.project.frontEnd.PortSwitcher;
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
			String adress ="";
			
			if(RMInformation.getInstance().getRmCode().equals("FARID")) {
				adress = Constants.FARID_IP;
			}else if (RMInformation.getInstance().getRmCode().equals("RE1")){
				adress = Constants.MOSTAFA_IP;
			}else if(RMInformation.getInstance().getRmCode().equals("RE2")) {
				adress = Constants.SAMAN_IP;
			}
			socket = new ReliableServerSocket(RMInformation.getInstance().getUDPListenPort(), 0 , InetAddress.getByName(adress));

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
					PortSwitcher.switchServer(message.portSwitcherArg);
					sendKillToAll(json_msg_str);
					send(json_msg_str);
				}else if (json_msg.command_type == CommandType.Fake_Generator) {
					sendToAll(json_msg_str);
				}else {
					ReplicaManager.log("CommandType is incorrect");
				}
			}else{
				ReplicaManager.log("Protocol Type is incorrect");
			}

		}

		private void send(String data) {
			try {
				Thread.sleep(5000);
				ReliableSocket aSocket = new ReliableSocket();
				
				aSocket.connect(new InetSocketAddress(Constants.FE_CLIENT_IP, Constants.FE_PORT_LISTEN));
				OutputStream out = aSocket.getOutputStream();
				out.write(data.getBytes());
				out.flush();
				out.close();
				aSocket.close();

			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}

		private void sendToAll(String data){

			int[] replicaPorts =  new int[3];
			if(RMInformation.getInstance().getRmName().equals(Constants.RM_FARID)) {
				replicaPorts[0] = Constants.kklPortListenFaridActive;
				replicaPorts[1] = Constants.dvlPortListenFaridActive;
				replicaPorts [2] = Constants.wstPortListenFaridActive;
			}else if (RMInformation.getInstance().getRmName().equals(Constants.RM_RE1)) {
				replicaPorts[0] = Constants.kklPortListenRe1Active;
				replicaPorts[1] = Constants.dvlPortListenRe1Active;
				replicaPorts [2] = Constants.wstPortListenRe1Active;
			}else if (RMInformation.getInstance().getRmName().equals(Constants.RM_RE2)) {
				replicaPorts[0] = Constants.kklPortListenRe2Active;
				replicaPorts[1] = Constants.dvlPortListenRe2Active;
				replicaPorts [2] = Constants.wstPortListenRe2Active;
			}else {
				ReplicaManager.log("Wrong Replica name ");
			}

			for(int i=0;i < 3; ++i) {

				final int idxPort = i;

				new Thread(new Runnable() {
					@Override
					public void run() {

						try {
							ReliableSocket sendToReplica = new ReliableSocket();

							String adress ="";
							
							if(RMInformation.getInstance().getRmCode().equals("FARID")) {
								adress = Constants.FARID_IP;
							}else if (RMInformation.getInstance().getRmCode().equals("RE1")){
								adress = Constants.MOSTAFA_IP;
							}else if(RMInformation.getInstance().getRmCode().equals("RE2")) {
								adress = Constants.SAMAN_IP;
							}
							
							sendToReplica.connect(new InetSocketAddress(adress, replicaPorts[idxPort]));

							OutputStream out = sendToReplica.getOutputStream();
							out.write(data.getBytes());

							out.flush();
							out.close();
							sendToReplica.close();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}).start();
			}
		}
		
		private void sendKillToAll(String data){

			int[] replicaPorts =  new int[6];
			if(RMInformation.getInstance().getRmName().equals(Constants.RM_FARID)) {
				replicaPorts[0] = Constants.DVL_PORT_LISTEN_FARID_ORIGINAL;
				replicaPorts[1] = Constants.DVL_PORT_LISTEN_FARID_BACKUP;
				replicaPorts [2] = Constants.KKL_PORT_LISTEN_FARID_ORIGINAL;
				replicaPorts[3] = Constants.KKL_PORT_LISTEN_FARID_BACKUP;
				replicaPorts[4] = Constants.WST_PORT_LISTEN_FARID_ORIGINAL;
				replicaPorts [5] = Constants.WST_PORT_LISTEN_FARID_BACKUP;
			}else if (RMInformation.getInstance().getRmName().equals(Constants.RM_RE1)) {
				replicaPorts[0] = Constants.DVL_PORT_LISTEN_RE1_ORIGINAL;
				replicaPorts[1] = Constants.DVL_PORT_LISTEN_RE1_BACKUP;
				replicaPorts [2] = Constants.KKL_PORT_LISTEN_RE1_ORIGINAL;
				replicaPorts[3] = Constants.KKL_PORT_LISTEN_RE1_BACKUP;
				replicaPorts[4] = Constants.WST_PORT_LISTEN_RE1_ORIGINAL;
				replicaPorts [5] = Constants.WST_PORT_LISTEN_RE1_BACKUP;
			}else if (RMInformation.getInstance().getRmName().equals(Constants.RM_RE2)) {
				replicaPorts[0] = Constants.DVL_PORT_LISTEN_RE2_ORIGINAL;
				replicaPorts[1] = Constants.DVL_PORT_LISTEN_RE2_BACKUP;
				replicaPorts [2] = Constants.KKL_PORT_LISTEN_RE2_ORIGINAL;
				replicaPorts[3] = Constants.KKL_PORT_LISTEN_RE2_BACKUP;
				replicaPorts[4] = Constants.WST_PORT_LISTEN_RE2_ORIGINAL;
				replicaPorts [5] = Constants.WST_PORT_LISTEN_RE2_BACKUP;
			}else {
				ReplicaManager.log("Wrong Replica name ");
			}

			for(int i=0;i < 6; ++i) {

				final int idxPort = i;

				new Thread(new Runnable() {
					@Override
					public void run() {

						try {
							ReliableSocket sendToReplica = new ReliableSocket();

							String adress ="";
							
							if(RMInformation.getInstance().getRmCode().equals("FARID")) {
								adress = Constants.FARID_IP;
							}else if (RMInformation.getInstance().getRmCode().equals("RE1")){
								adress = Constants.MOSTAFA_IP;
							}else if(RMInformation.getInstance().getRmCode().equals("RE2")) {
								adress = Constants.SAMAN_IP;
							}
							
							sendToReplica.connect(new InetSocketAddress(adress, replicaPorts[idxPort]));

							OutputStream out = sendToReplica.getOutputStream();
							out.write(data.getBytes());

							out.flush();
							out.close();
							sendToReplica.close();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}).start();
			}
		}
	}
}
