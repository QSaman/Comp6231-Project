/**
 * 
 */
package comp6231.project.saman.campus.communication;

import java.util.HashMap;

import comp6231.project.saman.campus.CampusCommunication;
import comp6231.shared.Constants;

/**
 * @author saman
 *
 */
public class ProjectCampusCommunication extends CampusCommunication {
	
	public String[] campus_names;
	public HashMap<String, RemoteInfo> campus_remote_info;

	/**
	 * 
	 */
	public ProjectCampusCommunication() {
	}

	/* (non-Javadoc)
	 * @see comp6231.project.saman.campus.CampusCommunication#getRemoteInfo(java.lang.String)
	 */
	@Override
	public RemoteInfo getRemoteInfo(String campus_name) {
		RemoteInfo ri = new RemoteInfo();
		ri.address = Constants.SAMAN_IP;
		if (campus_name.equals("DVL"))
			ri.port = Constants.dvlPortListenRe2Active;
		else if (campus_name.equals("KKL"))
			ri.port = Constants.kklPortListenRe2Active;
		else if (campus_name.equals("WST"))
			ri.port = Constants.wstPortListenRe2Active;
		else
		{
			System.err.println("Invalid campus name: " + campus_name);
			ri.port = 0;
		}
		//return campus_remote_info.get(campus_name);
		return ri;
	}

	/* (non-Javadoc)
	 * @see comp6231.project.saman.campus.CampusCommunication#getAllCampusNames()
	 */
	@Override
	public String[] getAllCampusNames() {
		return campus_names;
	}

	/* (non-Javadoc)
	 * @see comp6231.project.saman.campus.CampusCommunication#startServer()
	 */
	@Override
	public void startServer() {
		RemoteInfo ri = new RemoteInfo();
		ri.address = campus.getAddress();
		ri.port = campus.getPort();
		campus_remote_info.put(campus.getCampusName(), ri);
	}

	@Override
	public void updateCampusRemoteInfo(String campus_name, RemoteInfo remote_info) {
//		campus_remote_info.put(campus_name, remote_info);		
	}
}
