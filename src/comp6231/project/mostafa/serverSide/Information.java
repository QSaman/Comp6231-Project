package comp6231.project.mostafa.serverSide;

import java.util.ArrayList;
import java.util.List;

import comp6231.project.mostafa.core.Constants;

public class Information {
	private static Information instance = null;
	
	private String serverCode;
	private String serverName;
	private int serverPort;
	private int UDPListenPort;
	
	private Information(){
		setServerName(null);
		setServerPort(-1);
	}
	
	public static Information getInstance(){
		if(instance == null){
			instance = new Information();
		}
		return instance;
	}

	public void initializeServerInformation(int port){
		serverPort = port;
		setServer(port);
	}
	
	public boolean isUserClient(String id){
		if(id.contains("dvls")){
			return true;
		}else if (id.contains("kkls")){
			return true;
		}else if (id.contains("wsts")){
			return true;
		}else {
			return false;
		}
	}
	
	public boolean isUserAdmin(String id){
		if(id.contains("dvla")){
			return true;
		}else if (id.contains("kkla")){
			return true;
		}else if (id.contains("wsta")){
			return true;
		}else {
			return false;
		}
	}
	
	public void transaction(boolean isCommit){
		String args = "";
		if(isCommit){
			Database.getInstance().commit();
			args = Constants.COMMIT;
		}else{
			Database.getInstance().rollBack();
			args = Constants.ROLLBACK;
		}
		List<UDP> threads = new ArrayList<UDP>();
		
		if(getServerCode().equals("DVL")){
			threads.add(new UDP(args, Constants.WST_PORT_LISTEN, "WST"));
			threads.add(new UDP(args, Constants.KKL_PORT_LISTEN, "KKL"));	
		}else if(getServerCode().equals("WST")){
			threads.add(new UDP(args, Constants.DVL_PORT_LISTEN, "DVL"));
			threads.add(new UDP(args, Constants.KKL_PORT_LISTEN, "KKL"));
		}else if(getServerCode().equals("KKL")){
			threads.add(new UDP(args, Constants.WST_PORT_LISTEN, "WST"));
			threads.add(new UDP(args, Constants.DVL_PORT_LISTEN, "DVL"));
		}
		
		for (UDP udp : threads) {
			udp.start();
		}
		for (UDP udp : threads) {
			try {
				udp.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public boolean isBooked(String bookingId){
		String str = bookingId.substring(0,3);
		if(str.equalsIgnoreCase("DVL") || str.equalsIgnoreCase("KKL") || str.equalsIgnoreCase("WST")){
			return true;
		}else{
			return false;
		}
	}
	
	public int convertTimeToSec(String time){
		String parser[] = time.split(":");
		return(Integer.parseInt(parser[0])*3600 + Integer.parseInt(parser[1])*60);
	}
	
	public String convertTimeToString(int time){
		int hour = time / 3600;
		int minToSec = time % 3600;
		int min = minToSec / 60;
		return hour+":"+min+"0";
	}
	
	public int tryToFindUDPPort(String id){
		if(id.contains("DVL") || id.contains("dvl")){
			return Constants.DVL_PORT_LISTEN;
		}else if (id.contains("KKL") || id.contains("kkl")){
			return Constants.KKL_PORT_LISTEN;
		}else if(id.contains("WST") || id.contains("wst")) {
			return Constants.WST_PORT_LISTEN;
		}else{
			return -1;	
		}
	}
	
	public boolean isMine(String id){
		if(id.contains("DVL") || id.contains("dvl") && getServerCode().equalsIgnoreCase("DVL")){
			return true;
		}else if (id.contains("KKL") || id.contains("kkl") && getServerCode().equalsIgnoreCase("KKL")){
			return true;
		}else if(id.contains("WST") || id.contains("wst") && getServerCode().equalsIgnoreCase("WST")) {
			return true;
		}else{
			return false;	
		}
	}
	/**
	 * @return the serverName
	 */
	public String getServerName() {
		return serverName;
	}

	private void setServer(int port){
		switch (port) {
		case Constants.DVL_PORT:
			serverName = "DVLServer";
			serverCode = "DVL";
			UDPListenPort = Constants.DVL_PORT_LISTEN;
			break;
		case Constants.KKL_PORT:
			serverName = "KKLServer";
			serverCode = "KKL";
			UDPListenPort = Constants.KKL_PORT_LISTEN;
			break;
		case Constants.WST_PORT:
			serverName = "WSTServer";
			serverCode = "WST";
			UDPListenPort = Constants.WST_PORT_LISTEN;
			break;
		default:
			break;
		}
	}
	
	public int findUDPPort(String campusName){
		if(campusName.equalsIgnoreCase("KKL")){
			return Constants.KKL_PORT_LISTEN;
		}else if(campusName.equalsIgnoreCase("DVL")){
			return Constants.DVL_PORT_LISTEN;
		}else if(campusName.equalsIgnoreCase("WST")){
			return Constants.WST_PORT_LISTEN;
		}else {
			return -1;
		}
	}
	
	/**
	 * @param serverName the serverName to set
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	/**
	 * @return the serverPort
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * @param serverPort the serverPort to set
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * @return the serverCode
	 */
	public String getServerCode() {
		return serverCode;
	}

	/**
	 * @return the udpListenPort
	 */
	public int getUDPListenPort() {
		return UDPListenPort;
	}

}
