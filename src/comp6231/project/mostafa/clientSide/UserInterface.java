package comp6231.project.mostafa.clientSide;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import clientSide.factory.BookRoomFactory;
import clientSide.factory.CancelBookingIdFactory;
import clientSide.factory.ChangeReservationFactory;
import clientSide.factory.CreateFactory;
import clientSide.factory.DeleteFactory;
import clientSide.factory.GetAvailableTimeSlotFactory;
import core.CommandEnum;
import core.Constants;

public class UserInterface {
	public UserInterface() {
		read();
	}
	
	private void read(){
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String input;
		
		while ( scanner.hasNextLine()) {
			input = scanner.nextLine();
			runCommnad(input);
		}
		
	}
	
	private void runCommnad(String input)
	{
		String splited[] =  input.split(" ");
		String id = splited[0];

		if(splited.length < 2)
		{
			return;
		}
		
		if(Utility.getInstance().isUserClient(id)){
			StudentClient userClient;
			if(Utility.getInstance().isClientExist(id)){
				userClient = (StudentClient)Utility.getInstance().clients.get(id);
			}else {
				userClient = new StudentClient(id);
				Utility.getInstance().clients.put(id, userClient); 
			}
			
			if(splited[1] != null){
				CommandEnum command = Utility.getInstance().findCommand(splited[1]);
				if(command != CommandEnum.UNDEFINED){
					runCommandUser(command, id, splited, userClient);
				}else {
					wrongInput(4);
					return;
				}
			}else{
				wrongInput(2);
				return;
			}
			
		}else if (Utility.getInstance().isUserAdmin(id)){
			AdminClient adminClient;
			if(Utility.getInstance().isClientExist(id)){
				adminClient = (AdminClient)Utility.getInstance().clients.get(id);
			}else {
				adminClient = new AdminClient(id);
				Utility.getInstance().clients.put(id, adminClient); 
			}
			
			if(splited[1] != null){
				CommandEnum command = Utility.getInstance().findCommand(splited[1]);
				if(command != CommandEnum.UNDEFINED){
					runCommandAdmin(command, id, splited, adminClient);
				}else {
					wrongInput(4);
					return;
				}
			}else{
				wrongInput(2);
				return;
			}
		}else {
			wrongInput(3);
			return;
		}
	}
	
	private void wrongInput(int id){
		System.out.println("Wrong Input! "+"errorid: "+ id);
	}
	
	private void runCommandUser(CommandEnum command, String id, String[] splited, StudentClient userClient){
		if(CommandEnum.BOOKROOM == command){
			if(splited.length >= 6)
			{
				String campusName = splited[2];
				int roomNumber = Integer.parseInt(splited[3]);
				String date = splited[4];
				String time = splited[5];
				int port = findServer(id);
				
				Thread t = new Thread(new BookRoomFactory(campusName,roomNumber, date, time, id, userClient, port));
				t.start();
				
			}else {
				userClient.log("Invalid number of paramets for user");
			}
		}else if (CommandEnum.GETAVTIMESLOT == command){
			if(splited.length == 3)
			{
				String date = splited[2];
				int port = findServer(id);
				
				Thread t = new Thread(new GetAvailableTimeSlotFactory(date, id, userClient, port));
				t.start();
				
			}else {
				userClient.log("Invalid number of paramets for user");
			}
		}else if (CommandEnum.CANCELBOOKING == command){
			if(splited.length == 3)
			{
				String bookingId = splited[2];
				int port = findServer(id);
				
				Thread t = new Thread(new CancelBookingIdFactory(bookingId, id, userClient, port));
				t.start();
				
			}else {
				userClient.log("Invalid number of paramets for user");
			}
		}else if (CommandEnum.CHANGERESERVATION == command){
			if(splited.length >= 7)
			{
				String campusName = splited[2];
				int roomNumber = Integer.parseInt(splited[3]);
				String date = splited[4];
				String time = splited[5];
				String bookingId = splited[6];
				int port = findServer(id);
				
				Thread t = new Thread(new ChangeReservationFactory(campusName,roomNumber, date, time, id, bookingId, userClient, port));
				t.start();
				
			}else {
				userClient.log("Invalid number of paramets for user");
			}
		}else{
			userClient.log("Invalid Command For user");
		}
	}
	
	private void runCommandAdmin(CommandEnum command, String id, String[] splited, AdminClient adminClient){
		if(CommandEnum.CREATE == command){
			if(splited.length >= 5)
			{
				int roomNumber = Integer.parseInt(splited[2]);
				String date = splited[3];
				
				List<String> time = new ArrayList<String>();
				for(int i = 4; i < splited.length; ++i){
					time.add(splited[i]);
				}
				int port = findServer(id);
				
				Thread t = new Thread(new CreateFactory(roomNumber, date, time, id, adminClient, port));
				t.start();
				
			}else {
				adminClient.log("Invalid number of paramets for admin");
			}
		}else if (CommandEnum.DELETE == command){
			if(splited.length >= 5)
			{
				int roomNumber = Integer.parseInt(splited[2]);
				String date = splited[3];
				
				List<String> time = new ArrayList<String>();
				for(int i = 4; i < splited.length; ++i){
					time.add(splited[i]);
				}
				int port = findServer(id);
				
				Thread t = new Thread(new DeleteFactory(roomNumber, date, time, id, adminClient, port));
				t.start();
				
			}else {
				adminClient.log("Invalid number of paramets for admin");
			}
		}else{
			adminClient.log("Invalid Command For admin");
		}
	}
	
	public int findServer(String id){
		if(id.contains("dvl")){
			return Constants.DVL_PORT;
		}else if (id.contains("kkl")){
			return Constants.KKL_PORT;
		}else if (id.contains("wst")){
			return Constants.WST_PORT;
		}else {
			return 0;
		}
	}
}
