package comp6231.project.frontEnd.udp;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import net.rudp.ReliableSocket;
import comp6231.project.frontEnd.FE;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.shared.Constants;

public class Sequencer extends Thread{

	// key : seqNumber
	// value : FEPair
	public static ConcurrentHashMap<Integer, FEPair> holdBack = new ConcurrentHashMap<Integer, FEPair>();
	public FEPair pair;
	
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
		setTimeOut();
	}

	private void adjustSeqNumber(){
		synchronized (lock) {
			args.sequence_number = sequenceNumber;
			sequenceNumber ++;
			pair = new FEPair();
			holdBack.put(sequenceNumber, pair);
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
	
	public void setTimeOut(){
        ErrorHandler res = new ErrorHandler(pair, id, city);
        res.start();
	}
	
	class ErrorHandler extends Thread {

        private FEPair pair;
        private int id;
        private String city;
        private String result;

        public ErrorHandler(FEPair pair, int id, String city) {
            this.pair = pair;
            this.id = id;
            this.city = city;
        }

        public String readResult() {
            return result;
        }

        @Override
        public void run() {

            try {

                pair.semaphore.tryAcquire(2,TimeUnit.MINUTES);

                ArrayList<String> results = pair.entry.get(id);



                String p0 = results.get(0).split(",")[0];

                System.out.println("Replica 1 has produced " + p0);

                String p1 = results.get(1).split(",")[0];

                System.out.println("Replica 2 has produced " + p1);

                String p2 = "";

                if (results.size() <= 2) {
                    result = p0;
                    reportError(2, city);
                    return;
                }
                else {
                    p2 =  results.get(2).split(",")[0];
                    System.out.println("Replica 3 has produced " + p2);

                }

                if (pair.method != Protocol.GET_BOOKED_FLIGHT_COUNT) {

                    if (p0.equals(p1)) {

                        if (!p0.equals(p2)) {

                            int id = Integer.valueOf(results.get(2).split(",")[1]);

                            reportError(id, city);

                        }

                        result = p0;


                    } else if (p0.equals(p2)) {

                        if (!p0.equals(p1)) {

                            int id = Integer.valueOf(results.get(1).split(",")[1]);

                            reportError(id, city);

                        }

                        result = p0;

                    } else if (p1.equals(p2)) {

                        if (!p1.equals(p0)) {

                            int id = Integer.valueOf(results.get(0).split(",")[1]);

                            reportError(id, city);

                        }

                        result = p1;

                    } else {

                        System.out.println("no bugs");
                        result = p1;
                    }
                }
                else {
                    result = p0;
                }



            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
