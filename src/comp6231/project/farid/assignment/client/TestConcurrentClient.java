package comp6231.project.farid.assignment.client;

import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import ServerImplementation.ServerInterface;
import ServerImplementation.ServerInterfaceHelper;

public class TestConcurrentClient {
	
	static ServerInterface serverInterface = null;

    public static void main(String[] args) throws Exception {

        boolean exitProgram = false;
        while (!exitProgram) {

        	ORB orb = ORB.init(args, null);
			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
			
            String ID = null;
            String subIDCampus = null;
            MyLogger logger = null;

            boolean isTrueID = false;
            while (!isTrueID) {

                System.out.println("(TEST CONCURRENT) Welcome to the ROOM RESERVATION SYSTEM: ");
                ID = InputManager.getString("ID").toUpperCase();
                subIDCampus = ID.substring(0, 3);
                if (isCorrectID(ID.substring(4)) && (ID.startsWith("DVLS") || ID.startsWith("DVLA")
                        || ID.startsWith("KKLA") || ID.startsWith("KKLS")
                        || ID.startsWith("WSTA") || ID.startsWith("WSTS"))) {
                    isTrueID = true;
                    logger = new MyLogger(ID);
                } else
                    System.out.println("You have entered wrong ID. Please try again!");

            }

            switch (subIDCampus) {
                case ("DVL"):
    				serverInterface = (ServerInterface) ServerInterfaceHelper.narrow(ncRef.resolve_str("DVL"));

                    System.out.println("@@@ " + ID + " Connected to Dorval Campus. (TEST CONCURRENT)");
                    logger.log("@@@ " + ID + " Connected to Dorval Campus. (TEST CONCURRENT)");
                    break;

                case ("KKL"):
    				serverInterface = (ServerInterface) ServerInterfaceHelper.narrow(ncRef.resolve_str("KKL"));

                    System.out.println("@@@ " + ID + " Connected to Kirkland Campus. (TEST CONCURRENT)");
                    logger.log("@@@ " + ID + " Connected to Kirkland Campus. (TEST CONCURRENT)");
                    break;

                case ("WST"):
    				serverInterface = (ServerInterface) ServerInterfaceHelper.narrow(ncRef.resolve_str("WST"));

                    System.out.println("@@@ " + ID + " Connected to Westmount Campus. (TEST CONCURRENT)");
                    logger.log("@@@ " + ID + " Connected to Westmount Campus. (TEST CONCURRENT)");
                    break;
                default:
                    System.out.println("Wrong ID");
                    break;
            }

            String role = (ID.charAt(3) == 'S' ? "student" : "admin");
            boolean signedIn;
            if (role.equals("admin")) {
                
                signedIn = serverInterface.setAdminID(ID);
                if (!signedIn) {
                    System.out.println("!!!!!!!!!!! User has already signed in with other system.");
                }
            } else {
                
                signedIn = serverInterface.setStudentID(ID);
                if (!signedIn) {
                    System.out.println("!!!!!!!!!!! User has already signed in with other system.");
                }
            }

            while (signedIn) {
                System.out.println("\n\nWelcome " + role + " " + ID + " to the menu. Please choose an operation: (TEST CONCURRENT)");
                try {
                    if (role.equals("admin")) {
                        System.out.println("1- (TEST CONCURRENT) Create room");
                        System.out.println("2- (TEST CONCURRENT) Delete room");
                        int choice = InputManager.getNumber("number of choice");
                        switch (choice) {
                            case (1):
                                String startTaskCreat = InputManager.getTime("(TEST CONCURRENT) START TASK");
                            	LocalTime startTaskCreate = getLocalTimeOfString(startTaskCreat);
                                String idCre = ID;
                                int roomNumberCre = InputManager.getNumber("room number");
                                String dateCre = InputManager.getDate();
                                String timesCre = InputManager.getListOfTimesFromAdmin();


                                System.out.println("==== W A I T  T O  S T A R T  T H E  T A S K ===");

                                while (true) {
                                    if (LocalTime.now().isAfter(startTaskCreate)) {
                                        Runnable runnable = () -> {
                                            try {
                                                String createResult = serverInterface.createRoom(idCre, roomNumberCre, dateCre, timesCre);
                                                System.out.println(createResult);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        };
                                        new Thread(runnable).start();
                                        new Thread(runnable).start();
                                        new Thread(runnable).start();
                                        new Thread(runnable).start();

                                        break;
                                    }
                                }
                                break;
                            case (2):
                                String startTaskDelet = InputManager.getTime("(TEST CONCURRENT) START TASK");
                                LocalTime startTaskDelete = getLocalTimeOfString(startTaskDelet);

                                String idDel = ID;
                                int roomNumberDelete = InputManager.getNumber("room number");
                                String dateDelete = InputManager.getDate();
                                String timesDelete = InputManager.getListOfTimesFromAdmin();
                                System.out.println("==== W A I T  T O  S T A R T  T H E  T A S K ===");

                                while (true) {
                                    if (LocalTime.now().isAfter(startTaskDelete)) {
                                        Runnable runnable = () -> {

                                            try {
                                                String deleteResult = serverInterface.deleteRoom(idDel, roomNumberDelete, dateDelete, timesDelete);
                                                System.out.println(deleteResult);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        };
                                        new Thread(runnable).start();
                                        new Thread(runnable).start();
                                        new Thread(runnable).start();
                                        new Thread(runnable).start();

                                        break;
                                    }
                                }
                                break;

                            default:
                                System.out.println("Wrong choice. Go back to menu and try again!");
                        }
                    } else {
                        System.out.println("1- (TEST CONCURRENT) Book room");
                        System.out.println("2- (TEST CONCURRENT) Get all available times number");
                        System.out.println("3- (TEST CONCURRENT) Cancel reservation");
                        System.out.println("4- (TEST CONCURRENT) Change reservation");
                        int choice = InputManager.getNumber("number of choice");
                        switch (choice) {
                            case (1):
                                int campus;
                                while (true) {
                                    System.out.println("\nIn which campus?\n");
                                    System.out.println("1- Dorval");
                                    System.out.println("2- Kirkland");
                                    System.out.println("3- Westmount");
                                    campus = InputManager.getNumber("number of choice");
                                    if (campus == 1 || campus == 2 || campus == 3)
                                        break;
                                    else
                                        System.out.println("Wrong campus number. try again!");
                                }

                                String startTaskBoo = InputManager.getTime("(TEST CONCURRENT) START TASK");
                                LocalTime startTaskBook = getLocalTimeOfString(startTaskBoo);
                                String idBook = ID;
                                int campusBook = campus;
                                int roomBook = InputManager.getNumber("room number");
                                String dateBook = InputManager.getDate();
                                String startBookTime = InputManager.getTime("start");
                                String endBookTime = InputManager.getTime("end");

                                System.out.println("==== W A I T  T O  S T A R T  T H E  T A S K ===");

                                while (true) {
                                    if (LocalTime.now().isAfter(startTaskBook)) {

                                        Runnable runnable = () -> {
                                            String bookResult;
                                            try {
                                                bookResult = serverInterface.bookRoom(idBook, campusBook, roomBook, dateBook
                                                        , startBookTime, endBookTime);
                                                System.out.println(bookResult);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        };
                                        new Thread(runnable).start();
                                        new Thread(runnable).start();
                                        new Thread(runnable).start();
                                        new Thread(runnable).start();

                                        break;
                                    }
                                }
                                break;
                            case (2):

                                String startTaskGetTim = InputManager.getTime("(TEST CONCURRENT) START TASK");
                            	LocalTime startTaskGetTime = getLocalTimeOfString(startTaskGetTim);

                                String dateGetTime = InputManager.getDate();
                                String idGetTime = ID;

                                System.out.println("==== W A I T  T O  S T A R T  T H E  T A S K ===");

                                while (true) {
                                    if (LocalTime.now().isAfter(startTaskGetTime)) {

                                        Runnable runnable = () -> {
                                            String getTimeResult;
                                            try {
                                                getTimeResult = serverInterface.getAvailableTimeSlot(idGetTime, dateGetTime);
                                                System.out.println(getTimeResult);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        };
                                        new Thread(runnable).start();
                                        new Thread(runnable).start();
                                        new Thread(runnable).start();
                                        new Thread(runnable).start();

                                        break;
                                    }
                                }
                                break;

                            case (3):

                                String startTaskCance = InputManager.getTime("(TEST CONCURRENT) START TASK");
                            	LocalTime startTaskCancel = getLocalTimeOfString(startTaskCance);
                                String bookingIdCancel = InputManager.getString("bookingID");
                                String idCancel = ID;

                                System.out.println("==== W A I T  T O  S T A R T  T H E  T A S K ===");
                                while (true) {
                                    if (LocalTime.now().isAfter(startTaskCancel)) {
                                        Runnable runnable = () -> {
                                            String cancelResult;
                                            try {
                                                cancelResult = serverInterface.cancelBooking(idCancel, bookingIdCancel);
                                                System.out.println(cancelResult);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        };
                                        new Thread(runnable).start();
                                        new Thread(runnable).start();
                                        new Thread(runnable).start();
                                        new Thread(runnable).start();

                                        break;
                                    }
                                }
                                break;
                            case (4):
                                String startTaskChang = InputManager.getTime("(TEST CONCURRENT) START TASK");
                            	LocalTime startTaskChange = getLocalTimeOfString(startTaskChang);
                                int campusChange;
                                while(true) {
                                    System.out.println("\nIn which campus is new TimeSlot?\n");
                                    System.out.println("1- Dorval");
                                    System.out.println("2- Kirkland");
                                    System.out.println("3- Westmount");
                                    campusChange = InputManager.getNumber("number of choice");
                                    if (campusChange == 1 || campusChange == 2 || campusChange == 3) {
                                        break;
                                    }
                                    else {
                                        System.out.println("Wrong campus number. try again!");
                                    }
                                }
                                String bookingIdChange = InputManager.getString("bookingID");
                                int roomChange = InputManager.getNumber("room number");
                                String idChange = ID;
                                int campusChangeNum = campusChange;
                                String startTimeChange = InputManager.getTime("start");
                                String endTimeChange = InputManager.getTime("end");
                                System.out.println("==== W A I T  T O  S T A R T  T H E  T A S K ===");

                                while (true) {
                                    if (LocalTime.now().isAfter(startTaskChange)) {
                                        Runnable runnable = ()-> {
                                            String changeTimeSlotResult;
                                            try {
                                                changeTimeSlotResult = serverInterface.changeReservation(idChange, bookingIdChange,
                                                        campusChangeNum, roomChange, startTimeChange, endTimeChange);
                                                System.out.println(changeTimeSlotResult);
                                            } catch (Exception ignored){ }
                                        };

                                        new Thread(runnable).start();
                                        new Thread(runnable).start();
                                        new Thread(runnable).start();
                                        new Thread(runnable).start();

                                        break;
                                    }
                                }
                                break;
                            default:
                                System.out.println("Wrong choice. Go back to menu and try again!");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("!!! Wrong input. try again !!!");
                }
                boolean isTrueAnswer = false;
                while (!isTrueAnswer) {
                    System.out.println("\n\nPlease choose one: \n1- Go back to menu \n2- Sign out");
                    int answer = InputManager.getNumber("number of choice");
                    switch (answer) {

                        case (1):
                            isTrueAnswer = true;
                            break;
                        case (2):
                            signedIn = false;
                            if (role.equals("admin"))
                                serverInterface.signOut(ID);
                            else
                                serverInterface.signOut(ID);
                            isTrueAnswer = true;
                            System.out.println(ID + " signed out.");
                            logger.log(ID + " signed out.");
                            logger.close();
                            System.out.println("\nThank you for using this reservation system.");
                            break;
                        default:
                            System.out.println("Wrong choice.");
                    }
                }
            }
            System.out.println("\n1- Exit\n2- Sign in");
            String endChoice = InputManager.getString("number of choice");
            exitProgram = endChoice.equals("1");
        }

    }

    private static LocalTime getLocalTimeOfString(String time) {
    	int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
        int minute = Integer.parseInt(time.substring(time.indexOf(":") + 1));
		return LocalTime.of(hour, minute);
	}

	private static boolean isCorrectID(String string) {
        Pattern pattern = Pattern.compile("^[0-9]{4}$");
        Matcher matcher = pattern.matcher(string);
        return matcher.find() && matcher.group().equals(string);
    }
}