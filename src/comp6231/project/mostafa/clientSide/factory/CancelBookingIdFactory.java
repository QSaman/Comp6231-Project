package comp6231.project.mostafa.clientSide.factory;

import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import comp6231.project.common.corba.users.StudentOperations;
import comp6231.project.common.corba.users.StudentOperationsHelper;
import comp6231.project.mostafa.core.Program;
import comp6231.project.mostafa.clientSide.StudentClient;

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
			StudentOperations serverInterface = StudentOperationsHelper.narrow(Program.getNcRef().resolve_str("FES"));
			String result = serverInterface.cancelBooking(id, bookingId);
			
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
