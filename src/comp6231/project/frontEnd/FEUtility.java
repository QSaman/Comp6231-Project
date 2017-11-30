package comp6231.project.frontEnd;

import comp6231.project.farid.sharedPackage.DrrsConstants;
import comp6231.shared.Constants;

public class FEUtility {
	private static FEUtility instance = null;
		
	private FEUtility(){
	}

	/**
	 * @return the instance
	 */
	public static FEUtility getInstance() {
		if(instance == null){
			instance = new FEUtility();
		}
		return instance;
	}
	
	public int findMostafaUDPListenerPort(String id){
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
	
	public int findFaridUDPListenerPort(String id){
		if(id.contains("DVL") || id.contains("dvl")){
			return DrrsConstants.DVL_PORT;
		}else if (id.contains("KKL") || id.contains("kkl")){
			return DrrsConstants.KKL_PORT;
		}else if(id.contains("WST") || id.contains("wst")) {
			return DrrsConstants.WST_PORT;
		}else{
			return -1;	
		}
	}
}
