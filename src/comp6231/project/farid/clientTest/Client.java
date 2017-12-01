package comp6231.project.farid.clientTest;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

import comp6231.shared.Constants;

public class Client {

	private static Scanner scanner;

	public static void main(String[] args) throws Exception {

		while (true) {
			DatagramSocket clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName("localhost");
			byte[] sendData = new byte[1024];
			byte[] receiveData = new byte[1024];
			
			scanner = new Scanner(System.in);
			String stringToSend = scanner.nextLine();

			sendData = stringToSend.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,
					Constants.DVL_PORT_LISTEN_FARID_ACTIVE);
			clientSocket.send(sendPacket);

			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			String result = new String(receivePacket.getData());
			System.out.println(result);

			clientSocket.close();
		}
	}

}
