package comp6231.project.frontEnd.udp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import net.rudp.ReliableSocket;
import comp6231.project.frontEnd.FE;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.shared.Constants;

public class Sequencer extends Thread{

	private static int sequenceNumber = 0;
	private static final Object lock = new Object();

	private MessageHeader args;
	private int group;

	public Sequencer(MessageHeader args, int group){
		this.args = args;
		this.group = group;
	}

	@Override
	public void run() {
		adjustSeqNumber();
		byte [] m = FE.gson.toJson(args).getBytes();			
		sendToReplica(m);
	}

	private void adjustSeqNumber(){
		synchronized (lock) {
			args.sequence_number = sequenceNumber;
			sequenceNumber ++;
		}
	}

	public void sendToReplica(byte[] sendBuffer) {

		int[] replicaPorts =  new int[3];


		switch (group) {
		case Constants.DVL_GROUP:
			replicaPorts[0] = Constants.DVL_PORT_LISTEN_FARID_ACTIVE;
			replicaPorts[1] = Constants.DVL_PORT_LISTEN_RE1_ACTIVE;
			replicaPorts[2] = Constants.DVL_PORT_LISTEN_RE2_ACTIVE;
			break;
		case Constants.KKL_GROUP:
			replicaPorts[0] = Constants.KKL_PORT_LISTEN_FARID_ACTIVE;
			replicaPorts[1] = Constants.KKL_PORT_LISTEN_RE1_ACTIVE;
			replicaPorts[2] = Constants.KKL_PORT_LISTEN_RE2_ACTIVE;
			break;
		case Constants.WST_GROUP:
			replicaPorts[0] = Constants.WST_PORT_LISTEN_FARID_ACTIVE;
			replicaPorts[1] = Constants.WST_PORT_LISTEN_RE1_ACTIVE;
			replicaPorts[2] = Constants.WST_PORT_LISTEN_RE2_ACTIVE;
			break;
		}

		for(int i=0;i< Constants.ACTIVE_SERVERS; ++i) {

			final int idxPort = i;

			new Thread(new Runnable() {
				@Override
				public void run() {

					try {
						ReliableSocket sendToReplica = new ReliableSocket();

						sendToReplica.connect(new InetSocketAddress("127.0.0.1", replicaPorts[idxPort]));

						OutputStream out = sendToReplica.getOutputStream();
						out.write(sendBuffer);

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
