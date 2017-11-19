package comp6231.project.mostafa.clientSide.factory;

import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import comp6231.project.common.corba.users.StudentOperations;
import comp6231.project.common.corba.users.StudentOperationsHelper;
import comp6231.project.mostafa.core.Program;
import comp6231.project.mostafa.clientSide.StudentClient;

public class ChangeReservationFactory implements Runnable {

	private int roomNumber;
	private String date;
	private String time;
	private String id;
	private String newCampusName;
	private String bookingId;
	private StudentClient studentClient;
	private int port;

	public ChangeReservationFactory( String newCampusName, int roomNumber, String date, String time, String id, String bookingId, StudentClient studentClient, int port){
		this.roomNumber = roomNumber;
		this.date = date;
		this.time = time;
		this.id = id;
		this.newCampusName = newCampusName;
		this.bookingId = bookingId;
		this.studentClient = studentClient;
		this.port = port;
	}

	@Override
	public void run(){
		try {
			StudentOperations serverInterface = StudentOperationsHelper.narrow(Program.getNcRef().resolve_str("FES"));
			String result = serverInterface.changeReservation(id, bookingId, newCampusName, roomNumber, date, time);
			
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
