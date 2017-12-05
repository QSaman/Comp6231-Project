package comp6231.project.farid.sharedPackage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

			OutputStream out = aSocket.getOutputStream();
			out.write(m);
			out.flush();
			out.close();
			
			byte[] buffer = new byte[Constants.BUFFER_SIZE];
			InputStream in = aSocket.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while(true) {
			  int n = in.read(buffer);
			  if( n < 0 ) break;
			  baos.write(buffer,0,n);
			}

			byte data[] = baos.toByteArray();
			
			result = new String(data, 0 ,data.length);
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
