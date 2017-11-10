package comp6231.project.mostafa.clientSide.factory;

import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import ServerImpl.ServerInterfaceHelper;
import core.Program;
import clientSide.StudentClient;

public class CancelBookingIdFactory implements Runnable {

	private String bookingId;
	private String id;
	private StudentClient studentClient;
	private int port;

	public CancelBookingIdFactory(String bookingId, String id, StudentClient studentClient, int port){
		this.bookingId = bookingId;
		this.id = id;
		this.studentClient = studentClient;
		this.port = port;
	}

	@Override
	public void run(){
		try {
			ServerImpl.ServerInterface serverInterface = ServerInterfaceHelper.narrow(Program.getNcRef().resolve_str(port+""));
			String result = serverInterface.CancelBookingId(bookingId, id);
			
			studentClient.log(result);
		} catch (CannotProceed cannotProceed) {
			cannotProceed.printStackTrace();
		} catch (org.omg.CosNaming.NamingContextPackage.InvalidName invalidName) {
			invalidName.printStackTrace();
		} catch (NotFound notFound) {
			notFound.printStackTrace();
		}
	}

}
