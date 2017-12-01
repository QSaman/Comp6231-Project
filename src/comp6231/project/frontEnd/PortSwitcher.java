package comp6231.project.frontEnd;

import comp6231.shared.Constants;

public class PortSwitcher {
	
	public static void switchServer(String campusCode) {
		if (campusCode.equals("DVL")) {
			changePort(Constants.dvlPortListenFaridActive, Constants.DVL_PORT_LISTEN_FARID_ORIGINAL, Constants.DVL_PORT_LISTEN_FARID_BACKUP);
			changePort(Constants.dvlPortListenRe1Active, Constants.DVL_PORT_LISTEN_RE1_ORIGINAL, Constants.DVL_PORT_LISTEN_RE1_BACKUP);
			changePort(Constants.dvlPortListenRe2Active, Constants.DVL_PORT_LISTEN_RE2_ORIGINAL, Constants.DVL_PORT_LISTEN_RE2_BACKUP);
		} else if (campusCode.equals("KKL")) {
			changePort(Constants.kklPortListenFaridActive, Constants.KKL_PORT_LISTEN_FARID_ORIGINAL, Constants.KKL_PORT_LISTEN_FARID_BACKUP);
			changePort(Constants.kklPortListenRe1Active, Constants.KKL_PORT_LISTEN_RE1_ORIGINAL, Constants.KKL_PORT_LISTEN_RE1_BACKUP);
			changePort(Constants.kklPortListenRe2Active, Constants.KKL_PORT_LISTEN_RE2_ORIGINAL, Constants.KKL_PORT_LISTEN_RE2_BACKUP);
		} else if (campusCode.equals("WST")) {
			changePort(Constants.wstPortListenFaridActive, Constants.WST_PORT_LISTEN_FARID_ORIGINAL, Constants.WST_PORT_LISTEN_FARID_BACKUP);
			changePort(Constants.wstPortListenRe1Active, Constants.WST_PORT_LISTEN_RE1_ORIGINAL, Constants.WST_PORT_LISTEN_RE1_BACKUP);
			changePort(Constants.wstPortListenRe2Active, Constants.WST_PORT_LISTEN_RE2_ORIGINAL, Constants.WST_PORT_LISTEN_RE2_BACKUP);
		}	
	}
	
	private static void changePort(int active,int original, int backup) {
		if (active == original){
			active = backup;
		} else {
			active = original;
		}
	}
}
