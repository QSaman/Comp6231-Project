package comp6231.project.mostafa.serverSide;

import java.util.ArrayList;
import java.util.List;

import comp6231.project.mostafa.serverSide.Database;
import comp6231.project.mostafa.serverSide.UDP;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.messageProtocol.sharedMessage.ServerToServerMessage;
import comp6231.shared.Constants;

public class Information {
	private static Information instance = null;
	private static Object singeltoneLock = new Object();
	
	private String serverCode;
	private String serverName;
	private int serverPort;
	private int UDPListenPort;
	public boolean isReOne;
	
	private Information(){
		setServerName(null);
		setServerPort(-1);
	}
	
	public static Information getInstance(){
		synchronized (singeltoneLock) {
			if(instance == null){
				instance = new Information();
			}
			return instance;
		}
	}

	public void initializeServerInformation(int port){
		serverPort = port;
		setServer(port);
	}
	
	public boolean isUserClient(String id){
		if(id.contains("dvls") || id.contains("DVLS")){
			return true;
		}else if (id.contains("kkls")|| id.contains("KKLS")){
			return true;
		}else if (id.contains("wsts")|| id.contains("WSTS")){
			return true;
		}else {
			return false;
		}
	}
	
	public boolean isUserAdmin(String id){
		if(id.contains("dvla")|| id.contains("DVLA")){
			return true;
		}else if (id.contains("kkla")|| id.contains("KKLA")){
			return true;
		}else if (id.contains("wsta")|| id.contains("WSTA")){
			return true;
		}else {
			return false;
		}
	}
	
	public MessageHeader sendMessageServerToServer(String data, String studentID){
		return new ServerToServerMessage(data, studentID);
	}
	
	public void transaction(boolean isCommit){
		MessageHeader args;
		if(isCommit){
			Database.getInstance().commit();
			args = sendMessageServerToServer(Constants.COMMIT, "");
		}else{
			Database.getInstance().rollBack();
			args = sendMessageServerToServer(Constants.ROLLBACK, "");
		}
		List<UDP> threads = new ArrayList<UDP>();
		
		if(getServerCode().equals("DVL")){
			threads.add(new UDP(args, isReOne == true ? Constants.wstPortListenRe1Active : Constants.wstPortListenRe2Active, "WST"));
			threads.add(new UDP(args, isReOne == true ? Constants.kklPortListenRe1Active : Constants.kklPortListenRe2Active, "KKL"));	
		}else if(getServerCode().equals("WST")){
			threads.add(new UDP(args, isReOne == true ? Constants.dvlPortListenRe1Active : Constants.dvlPortListenRe2Active, "DVL"));
			threads.add(new UDP(args, isReOne == true ? Constants.kklPortListenRe1Active : Constants.kklPortListenRe2Active, "KKL"));
		}else if(getServerCode().equals("KKL")){
			threads.add(new UDP(args, isReOne == true ? Constants.wstPortListenRe1Active : Constants.wstPortListenRe2Active, "WST"));
			threads.add(new UDP(args, isReOne == true ? Constants.dvlPortListenRe1Active : Constants.dvlPortListenRe2Active, "DVL"));
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
			return isReOne == true ? Constants.dvlPortListenRe1Active : Constants.dvlPortListenRe2Active;
		}else if (id.contains("KKL") || id.contains("kkl")){
			return isReOne == true ? Constants.kklPortListenRe1Active : Constants.kklPortListenRe2Active;
		}else if(id.contains("WST") || id.contains("wst")) {
			return isReOne == true ? Constants.wstPortListenRe1Active : Constants.wstPortListenRe2Active;
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
		case Constants.DVL_PORT_RE1_ORIGINAL:
			serverName = "DVLServer_RE1_Original";
			serverCode = "DVL";
			UDPListenPort = Constants.DVL_PORT_LISTEN_RE1_ORIGINAL;
			isReOne = true;
			break;
		case Constants.KKL_PORT_RE1_ORIGINAL:
			serverName = "KKLServer_RE1_Original";
			serverCode = "KKL";
			UDPListenPort = Constants.KKL_PORT_LISTEN_RE1_ORIGINAL;
			isReOne = true;
			break;
		case Constants.WST_PORT_RE1_ORGINAL:
			serverName = "WSTServer_RE1_Original";
			serverCode = "WST";
			UDPListenPort = Constants.WST_PORT_LISTEN_RE1_ORIGINAL;
			isReOne = true;
			break;
		case Constants.DVL_PORT_RE1_BACKUP:
			serverName = "DVLServer_RE1_Backup";
			serverCode = "DVL";
			UDPListenPort = Constants.DVL_PORT_LISTEN_RE1_BACKUP;
			isReOne = true;
			break;
		case Constants.KKL_PORT_RE1_BACKUP:
			serverName = "KKLServer_RE1_Backup";
			serverCode = "KKL";
			UDPListenPort = Constants.KKL_PORT_LISTEN_RE1_BACKUP;
			isReOne = true;
			break;
		case Constants.WST_PORT_RE1_BACKUP:
			serverName = "WSTServer_RE1_Backup";
			serverCode = "WST";
			UDPListenPort = Constants.WST_PORT_LISTEN_RE1_BACKUP;
			isReOne = true;
			break;
		case Constants.DVL_PORT_RE2_ORIGINAL:
			serverName = "DVLServer_RE2_Original";
			serverCode = "DVL";
			UDPListenPort = Constants.DVL_PORT_LISTEN_RE2_ORIGINAL;
			isReOne = false;
			break;
		case Constants.KKL_PORT_RE2_ORIGINAL:
			serverName = "KKLServer_RE2_Original";
			serverCode = "KKL";
			UDPListenPort = Constants.KKL_PORT_LISTEN_RE2_ORIGINAL;
			isReOne = false;
			break;
		case Constants.WST_PORT_RE2_ORGINAL:
			serverName = "WSTServer_RE2_Original";
			serverCode = "WST";
			UDPListenPort = Constants.WST_PORT_LISTEN_RE2_ORIGINAL;
			isReOne = false;
			break;
		case Constants.DVL_PORT_RE2_BACKUP:
			serverName = "DVLServer_RE2_Backup";
			serverCode = "DVL";
			UDPListenPort = Constants.DVL_PORT_LISTEN_RE2_BACKUP;
			isReOne = false;
			break;
		case Constants.KKL_PORT_RE2_BACKUP:
			serverName = "KKLServer_RE2_Backup";
			serverCode = "KKL";
			UDPListenPort = Constants.KKL_PORT_LISTEN_RE2_BACKUP;
			isReOne = false;
			break;
		case Constants.WST_PORT_RE2_BACKUP:
			serverName = "WSTServer_RE2_Backup";
			serverCode = "WST";
			UDPListenPort = Constants.WST_PORT_LISTEN_RE2_BACKUP;
			isReOne = false;
			break;
		default:
			break;
		}
	}
	
	public int findUDPPort(String campusName){
		if(campusName.equalsIgnoreCase("KKL")){
			return isReOne == true ? Constants.kklPortListenRe1Active : Constants.kklPortListenRe2Active;
		}else if(campusName.equalsIgnoreCase("DVL")){
			return isReOne == true ? Constants.dvlPortListenRe1Active : Constants.dvlPortListenRe2Active;
		}else if(campusName.equalsIgnoreCase("WST")){
			return isReOne == true ? Constants.wstPortListenRe1Active : Constants.wstPortListenRe2Active;
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
