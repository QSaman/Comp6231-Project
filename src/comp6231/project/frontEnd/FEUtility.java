package comp6231.project.frontEnd;

import comp6231.shared.Constants;

public class FEUtility {
	private static FEUtility instance = null;
	private static final Object singeltoneLock = new Object();
	
	private FEUtility(){
	}

	/**
	 * @return the instance
	 */
	public static FEUtility getInstance() {
		if(instance == null) {
			synchronized (singeltoneLock) {
				if(instance == null){
					instance = new FEUtility();

				}
			}
		}
		return instance;
	}
	
	public String findUDPListenerPort(String id){
		if(id.contains("DVL") || id.contains("dvl")){
			return Constants.DVL_GROUP;
		}else if (id.contains("KKL") || id.contains("kkl")){
			return Constants.KKL_GROUP;
		}else if(id.contains("WST") || id.contains("wst")) {
			return Constants.WST_GROUP;
		}else{
			return Constants.NULL_STRING;
		}
	}
	
	public int findRMPort(String campusName) {
		if(campusName.startsWith("F")) {
			return Constants.RM_PORT_LISTEN_Farid;
		}else if(campusName.startsWith("M")){
			return Constants.RM_PORT_LISTEN_RE1;
		}else if(campusName.startsWith("S")) {
			return Constants.RM_PORT_LISTEN_RE2;
		}else {
			return -1;
		}
	}
	
	public String findRMCode(String campusName) {
		if(campusName.startsWith("F")) {
			return "Farid";
		}else if(campusName.startsWith("M")){
			return "Mostafa";
		}else if(campusName.startsWith("S")) {
			return "Saman";
		}else {
			return Constants.NULL_STRING;
		}
	}
}
