package comp6231.project.farid.sharedPackage;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.SocketException;

import net.rudp.ReliableSocket;
import comp6231.shared.Constants;

public class UdpSender extends Thread{		
	private String result;
	private byte[] m;
	private int serverPort;
	private String udpId;
	
	public UdpSender(byte[] m, int serverPort, String udpId){
		this.m = m;
		this.serverPort = serverPort;
		this.result = null;
		this.udpId = udpId;
	}

	@Override
	public void run() {
		ReliableSocket aSocket = null;
		try {
			aSocket = new ReliableSocket();
			aSocket.connect(new InetSocketAddress("127.0.0.1", serverPort));

			OutputStreamWriter out = new OutputStreamWriter(aSocket.getOutputStream()) ;
			out.write(new String(m,0,m.length));
			out.write('\u0004');
			out.flush();
			out.close();

			
			System.out.println("UDP SOCKET REQUESTED: " + new String(m,0,m.length) + " ServerPort: "+ serverPort);
			InputStreamReader in = new InputStreamReader(aSocket.getInputStream());

			CharArrayWriter writer = new CharArrayWriter(Constants.BUFFER_SIZE);
			
			while(true) {
			  int n = in.read();
			  if( n < 0  || n == '\u0004') break;
			  writer.write(n);
			}

			result = writer.toString();
			System.out.println("result of udpSender is : " + result);
		}catch (SocketException e){
			e.getMessage();
		}catch (IOException e){
			e.getMessage();
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
