package comp6231.project.frontEnd;

import comp6231.shared.Constants;

public class FEUtility {
	private static FEUtility instance = null;
	private static final Object lock = new Object();
	
	private FEUtility(){
	}

	/**
	 * @return the instance
	 */
	public static FEUtility getInstance() {
		synchronized (lock) {
			if(instance == null){
				instance = new FEUtility();
			}
			return instance;
		}
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
}
