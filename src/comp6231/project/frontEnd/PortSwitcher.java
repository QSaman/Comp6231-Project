package comp6231.project.frontEnd;

import comp6231.shared.Constants;

public class PortSwitcher {
	private static final Object lock = new Object();

	public static String switchServer(String campusCode) {
		synchronized (lock) {
			StringBuilder result = new StringBuilder();
			if (campusCode.equals("DVL")) {
				result.append("DVL Servers killed: " + "\n" + Constants.dvlPortListenFaridActive + "\n"
						+ Constants.dvlPortListenRe1Active + "\n" + Constants.dvlPortListenRe2Active);
				Constants.dvlPortListenFaridActive = changePort(Constants.dvlPortListenFaridActive,
						Constants.DVL_PORT_LISTEN_FARID_ORIGINAL, Constants.DVL_PORT_LISTEN_FARID_BACKUP);
				Constants.dvlPortListenRe1Active = changePort(Constants.dvlPortListenRe1Active,
						Constants.DVL_PORT_LISTEN_RE1_ORIGINAL, Constants.DVL_PORT_LISTEN_RE1_BACKUP);
				Constants.dvlPortListenRe2Active = changePort(Constants.dvlPortListenRe2Active,
						Constants.DVL_PORT_LISTEN_RE2_ORIGINAL, Constants.DVL_PORT_LISTEN_RE2_BACKUP);
				result.append("DVL Servers changed to: " + "\n" + Constants.dvlPortListenFaridActive + "\n"
						+ Constants.dvlPortListenRe1Active + "\n" + Constants.dvlPortListenRe2Active);
			} else if (campusCode.equals("KKL")) {
				result.append("KKL Servers killed: " + "\n" + Constants.kklPortListenFaridActive + "\n"
						+ Constants.kklPortListenRe1Active + "\n" + Constants.kklPortListenRe2Active);
				Constants.kklPortListenFaridActive = changePort(Constants.kklPortListenFaridActive,
						Constants.KKL_PORT_LISTEN_FARID_ORIGINAL, Constants.KKL_PORT_LISTEN_FARID_BACKUP);
				Constants.kklPortListenRe1Active = changePort(Constants.kklPortListenRe1Active,
						Constants.KKL_PORT_LISTEN_RE1_ORIGINAL, Constants.KKL_PORT_LISTEN_RE1_BACKUP);
				Constants.kklPortListenRe2Active = changePort(Constants.kklPortListenRe2Active,
						Constants.KKL_PORT_LISTEN_RE2_ORIGINAL, Constants.KKL_PORT_LISTEN_RE2_BACKUP);
				result.append("KKL Servers changed to: " + "\n" + Constants.kklPortListenFaridActive + "\n"
						+ Constants.kklPortListenRe1Active + "\n" + Constants.kklPortListenRe2Active);
			} else if (campusCode.equals("WST")) {
				result.append("WST Servers killed: " + "\n" + Constants.wstPortListenFaridActive + "\n"
						+ Constants.wstPortListenRe1Active + "\n" + Constants.wstPortListenRe2Active);
				Constants.wstPortListenFaridActive = changePort(Constants.wstPortListenFaridActive,
						Constants.WST_PORT_LISTEN_FARID_ORIGINAL, Constants.WST_PORT_LISTEN_FARID_BACKUP);
				Constants.wstPortListenRe1Active = changePort(Constants.wstPortListenRe1Active,
						Constants.WST_PORT_LISTEN_RE1_ORIGINAL, Constants.WST_PORT_LISTEN_RE1_BACKUP);
				Constants.wstPortListenRe2Active = changePort(Constants.wstPortListenRe2Active,
						Constants.WST_PORT_LISTEN_RE2_ORIGINAL, Constants.WST_PORT_LISTEN_RE2_BACKUP);
				result.append("WST Servers changed to: " + "\n" + Constants.wstPortListenFaridActive + "\n"
						+ Constants.wstPortListenRe1Active + "\n" + Constants.wstPortListenRe2Active);
			}
			return result.toString();
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
