package comp6231.project.mostafa.serverSide;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.SocketException;

import net.rudp.ReliableSocket;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.shared.Constants;

public class UDP extends Thread{		
	private String result;
	private MessageHeader args;
	private int serverPort;
	private String udpId;

	public UDP(MessageHeader args, int serverPort, String udpId){
		this.args = args;
		this.serverPort = serverPort;
		this.result = null;
		this.udpId = udpId;
	}

	@Override
	public void run() {
		ReliableSocket aSocket = null;
		try {
			aSocket = new ReliableSocket();
			aSocket.connect(new InetSocketAddress(Constants.MOSTAFA_IP, serverPort));
			String json = Server.gson.toJson(args);

			OutputStreamWriter out = new OutputStreamWriter(aSocket.getOutputStream()) ;
			out.write(json);
			out.write('\u0004');
			out.flush();
			out.close();

			Server.log("UDP Socket Requested: "+ json + " toServerPort of : " + serverPort);

	

			InputStreamReader in = new InputStreamReader(aSocket.getInputStream());

			CharArrayWriter writer = new CharArrayWriter(Constants.BUFFER_SIZE);
			
			while(true) {
			  int n = in.read();
			  if( n < 0  || n == '\u0004') break;
			  writer.write(n);
			}

			result = writer.toString();
			Server.log("result of udpSender is : " + result);
		}catch (SocketException e){
			Server.log("Socket: " + e.getMessage());
		}catch (IOException e){
			Server.log("IO: " + e.getMessage());
		}finally {
			if(aSocket != null)
				try {
					aSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} 	
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @return the udpId
	 */
	public String getUdpId() {
		return udpId;
	}
}
