/**
 * 
 */
package comp6231.project.saman.campus.communication;

import java.util.HashMap;

import comp6231.project.saman.campus.CampusCommunication;

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
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see comp6231.project.saman.campus.CampusCommunication#getRemoteInfo(java.lang.String)
	 */
	@Override
	public RemoteInfo getRemoteInfo(String campus_name) {
		return campus_remote_info.get(campus_name);
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

}
