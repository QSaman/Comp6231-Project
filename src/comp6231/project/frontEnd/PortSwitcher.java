package comp6231.project.frontEnd;

import comp6231.shared.Constants;

public class PortSwitcher {
	private static final Object lock = new Object();

	public static void switchServer(String campusCode) {
		synchronized (lock) {
			if (campusCode.equals("FDVL")) {
				Constants.dvlPortListenFaridActive = changePort(Constants.dvlPortListenFaridActive,
						Constants.DVL_PORT_LISTEN_FARID_ORIGINAL, Constants.DVL_PORT_LISTEN_FARID_BACKUP);
			} else if (campusCode.equals("SDVL")){
				Constants.dvlPortListenRe2Active = changePort(Constants.dvlPortListenRe2Active,
						Constants.DVL_PORT_LISTEN_RE2_ORIGINAL, Constants.DVL_PORT_LISTEN_RE2_BACKUP);
			} else if (campusCode.equals("MDVL")) {
				Constants.dvlPortListenRe1Active = changePort(Constants.dvlPortListenRe1Active,
						Constants.DVL_PORT_LISTEN_RE1_ORIGINAL, Constants.DVL_PORT_LISTEN_RE1_BACKUP);
			} else if (campusCode.equals("FKKL")) {
				Constants.kklPortListenFaridActive = changePort(Constants.kklPortListenFaridActive,
						Constants.KKL_PORT_LISTEN_FARID_ORIGINAL, Constants.KKL_PORT_LISTEN_FARID_BACKUP);
			} else if (campusCode.equals("SKKL")) {
				Constants.kklPortListenRe2Active = changePort(Constants.kklPortListenRe2Active,
						Constants.KKL_PORT_LISTEN_RE2_ORIGINAL, Constants.KKL_PORT_LISTEN_RE2_BACKUP);
			} else if (campusCode.equals("MKKL")) {
				Constants.kklPortListenRe1Active = changePort(Constants.kklPortListenRe1Active,
						Constants.KKL_PORT_LISTEN_RE1_ORIGINAL, Constants.KKL_PORT_LISTEN_RE1_BACKUP);
			} else if (campusCode.equals("FWST")) {
				Constants.wstPortListenFaridActive = changePort(Constants.wstPortListenFaridActive,
						Constants.WST_PORT_LISTEN_FARID_ORIGINAL, Constants.WST_PORT_LISTEN_FARID_BACKUP);
			} else if (campusCode.equals("SWST")) {
				Constants.wstPortListenRe2Active = changePort(Constants.wstPortListenRe2Active,
						Constants.WST_PORT_LISTEN_RE2_ORIGINAL, Constants.WST_PORT_LISTEN_RE2_BACKUP);
			} else if (campusCode.equals("MWST")) {
				Constants.wstPortListenRe1Active = changePort(Constants.wstPortListenRe1Active,
						Constants.WST_PORT_LISTEN_RE1_ORIGINAL, Constants.WST_PORT_LISTEN_RE1_BACKUP);
			}
		}
	}

	private static int changePort(int active, int original, int backup) {
		if (active == original) {
			return backup;
		} else {
			return original;
		}
	}
}
