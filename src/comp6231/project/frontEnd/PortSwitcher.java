package comp6231.project.frontEnd;

import comp6231.shared.Constants;

public class PortSwitcher {
	private static final Object lock = new Object();
	
	public static void switchServer(String campusCode) {
		synchronized (lock) {
			StringBuilder builder = new StringBuilder();
			if (campusCode.equals("DVL")) {
				Constants.dvlPortListenFaridActive = changePort(Constants.dvlPortListenFaridActive, Constants.DVL_PORT_LISTEN_FARID_ORIGINAL, Constants.DVL_PORT_LISTEN_FARID_BACKUP);
				Constants.dvlPortListenRe1Active = changePort(Constants.dvlPortListenRe1Active, Constants.DVL_PORT_LISTEN_RE1_ORIGINAL, Constants.DVL_PORT_LISTEN_RE1_BACKUP);
				Constants.dvlPortListenRe2Active = changePort(Constants.dvlPortListenRe2Active, Constants.DVL_PORT_LISTEN_RE2_ORIGINAL, Constants.DVL_PORT_LISTEN_RE2_BACKUP);
			} else if (campusCode.equals("KKL")) {
				Constants.kklPortListenFaridActive = changePort(Constants.kklPortListenFaridActive, Constants.KKL_PORT_LISTEN_FARID_ORIGINAL, Constants.KKL_PORT_LISTEN_FARID_BACKUP);
				Constants.kklPortListenRe1Active = changePort(Constants.kklPortListenRe1Active, Constants.KKL_PORT_LISTEN_RE1_ORIGINAL, Constants.KKL_PORT_LISTEN_RE1_BACKUP);
				Constants.kklPortListenRe2Active = changePort(Constants.kklPortListenRe2Active, Constants.KKL_PORT_LISTEN_RE2_ORIGINAL, Constants.KKL_PORT_LISTEN_RE2_BACKUP);
			} else if (campusCode.equals("WST")) {
				Constants.wstPortListenFaridActive = changePort(Constants.wstPortListenFaridActive, Constants.WST_PORT_LISTEN_FARID_ORIGINAL, Constants.WST_PORT_LISTEN_FARID_BACKUP);
				Constants.wstPortListenRe1Active = changePort(Constants.wstPortListenRe1Active, Constants.WST_PORT_LISTEN_RE1_ORIGINAL, Constants.WST_PORT_LISTEN_RE1_BACKUP);
				Constants.wstPortListenRe2Active = changePort(Constants.wstPortListenRe2Active, Constants.WST_PORT_LISTEN_RE2_ORIGINAL, Constants.WST_PORT_LISTEN_RE2_BACKUP);
			}	
		}
		
	}

	private static int changePort(int active,int original, int backup) {
		if (active == original){
			return backup;
		} else {
			return original;
		}
	}
}
