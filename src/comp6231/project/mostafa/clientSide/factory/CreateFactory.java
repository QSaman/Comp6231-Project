package comp6231.project.mostafa.clientSide.factory;

import java.util.List;

import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import comp6231.project.common.corba.users.AdminOperations;
import comp6231.project.common.corba.users.AdminOperationsHelper;
import comp6231.project.mostafa.core.Program;
import comp6231.project.mostafa.clientSide.AdminClient;

public class CreateFactory implements Runnable {

	private int roomNumber;
	private String date;
	private List<String> time;
	private String id;
	private AdminClient adminClient;
	private int port;

	public CreateFactory(int roomNumber, String date, List<String> time, String id, AdminClient adminClient, int port){
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
			AdminOperations serverInterface = AdminOperationsHelper.narrow(Program.getNcRef().resolve_str("FEA"));
			String result = serverInterface.createRoom(id,roomNumber, date, time.toArray(new String[0]));
			
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
