package comp6231.project.replicaManager;

import comp6231.shared.Constants;

public class RMInformation {
	private static RMInformation instance = null;
	private static Object singeltoneLock = new Object();
	
	private String rmCode;
	private String rmName;
	private int UDPListenPort;
	
	private RMInformation(){
		setRmName(null);
	}
	
	public static RMInformation getInstance(){
		synchronized (singeltoneLock) {
			if(instance == null){
				instance = new RMInformation();
			}
			return instance;
		}
	}

	public void initializeServerInformation(String name){
		setRM(name);
	}
	
	private void setRM(String name) {
		if(name.equals(Constants.RM_RE1)) {
			rmName = Constants.RM_RE1;
			rmCode = "DVL";
			UDPListenPort = Constants.RM_PORT_LISTEN_RE1;
		}else if (name.equals(Constants.RM_RE2)) {
			rmName = Constants.RM_RE2;
			rmCode = "KKL";
			UDPListenPort = Constants.RM_PORT_LISTEN_RE2;
		}else if(name.equals(Constants.RM_RE3)) {
			rmName = Constants.RM_RE3;
			rmCode = "WST";
			UDPListenPort = Constants.RM_PORT_LISTEN_RE3;
		}else {
			System.out.println("THIS RM CONFIG IS WRONG !!!! ");
			System.exit(1);
		}
		
	}
	
	
	/**
	 * @return the rmCode
	 */
	public String getRmCode() {
		return rmCode;
	}

	/**
	 * @param rmCode the rmCode to set
	 */
	public void setRmCode(String rmCode) {
		this.rmCode = rmCode;
	}

	/**
	 * @return the rmName
	 */
	public String getRmName() {
		return rmName;
	}

	/**
	 * @param rmName the rmName to set
	 */
	public void setRmName(String rmName) {
		this.rmName = rmName;
	}

	/**
	 * @return the uDPListenPort
	 */
	public int getUDPListenPort() {
		return UDPListenPort;
	}

	/**
	 * @param uDPListenPort the uDPListenPort to set
	 */
	public void setUDPListenPort(int uDPListenPort) {
		UDPListenPort = uDPListenPort;
	}
}
