package comp6231.project.mostafa.clientSide.factory;

import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import ServerImpl.ServerInterfaceHelper;
import core.Program;
import clientSide.StudentClient;

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
			ServerImpl.ServerInterface serverInterface = ServerInterfaceHelper.narrow(Program.getNcRef().resolve_str(port+""));
			String result = serverInterface.changeReservation(bookingId, newCampusName, date, roomNumber, time, id);
			
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
