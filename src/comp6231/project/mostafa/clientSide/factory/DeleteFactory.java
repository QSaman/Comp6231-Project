package comp6231.project.mostafa.clientSide.factory;

import java.util.List;

import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import ServerImpl.ServerInterfaceHelper;
import core.Program;
import clientSide.AdminClient;

public class DeleteFactory implements Runnable {

	private int roomNumber;
	private String date;
	private List<String> time;
	private String id;
	private AdminClient adminClient;
	private int port;

	public DeleteFactory(int roomNumber, String date, List<String> time, String id, AdminClient adminClient, int port){
		this.roomNumber = roomNumber;
		this.date = date;
		this.time = time;
		this.id = id;
		this.adminClient = adminClient;
		this.port = port;
	}

	@Override
	public void run(){
		try {
			ServerImpl.ServerInterface serverInterface = ServerInterfaceHelper.narrow(Program.getNcRef().resolve_str(port+""));
			String result = serverInterface.delete(roomNumber, date, time.toArray(new String[0]), id);
			
			adminClient.log(result);
		} catch (CannotProceed cannotProceed) {
			cannotProceed.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName invalidName) {
			invalidName.printStackTrace();
		} catch (NotFound notFound) {
			notFound.printStackTrace();
		}
	}

}
