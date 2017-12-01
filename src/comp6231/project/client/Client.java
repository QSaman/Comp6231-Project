package comp6231.project.client;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import comp6231.project.common.corba.users.AdminOperations;
import comp6231.project.common.corba.users.AdminOperationsHelper;
import comp6231.project.common.corba.users.StudentOperations;
import comp6231.project.common.corba.users.StudentOperationsHelper;

public class Client {

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
				System.out.println("Welcome to the ROOM RESERVATION SYSTEM: ");
				ID = InputManager.getString("ID").toUpperCase();
				subIDCampus = ID.substring(0, 3);

				// Checking if the entered ID is a correct ID
				if (isCorrectID(ID.substring(4))
						&& (ID.startsWith("DVLS") || ID.startsWith("DVLA") || ID.startsWith("KKLA")
								|| ID.startsWith("KKLS") || ID.startsWith("WSTA") || ID.startsWith("WSTS"))) {
					isTrueID = true;
					logger = new MyLogger(ID); // Creating a Log file for the User who has entered a true ID
				} else
					System.out.println("You have entered wrong ID. Please try again!");
			}
			StudentOperations studentOperations = null;
			AdminOperations adminOperations = null;
			// Connect to the server that is associated with the user ID
			switch (subIDCampus) {

			case ("DVL"):
				System.out.println("@@@ " + ID + " Connecting to Dorval Campus.");
				logger.log("@@@ " + ID + " Connecting to Dorval Campus.");
				break;

			case ("KKL"):
				System.out.println("@@@ " + ID + " Connecting to Kirkland Campus.");
				logger.log("@@@ " + ID + " Connecting to Kirkland Campus.");
				break;

			case ("WST"):
				System.out.println("@@@ " + ID + " Connecting to Westmount Campus.");
				logger.log("@@@ " + ID + " Connecting to Westmount Campus.");
				break;
			default:
				System.out.println("Wrong ID");
				break;
			}

			String role = (ID.charAt(3) == 'S' ? "student" : "admin");
			boolean signedIn;
			if (role.equals("admin")) {
				adminOperations = (AdminOperations) AdminOperationsHelper.narrow(ncRef.resolve_str("FEA"));
				signedIn = adminOperations.adminLogin(ID);
				if (!signedIn) {
					System.out.println("!!!!!!!!!!! User has already signed in with other system.");
				} else {
					System.out.println("@@@ " + ID + " Connected to server");
					logger.log("\"@@@ \"+ID+\" Connected to server\"");
				}
			} else {
				studentOperations = (StudentOperations) StudentOperationsHelper.narrow(ncRef.resolve_str("FES"));
				signedIn = studentOperations.studentLogin(ID);
				if (!signedIn) {
					System.out.println("!!!!!!!!!!! User has already signed in with other system.");
				} else {
					System.out.println("@@@ " + ID + " Connected to server");
					logger.log("\"@@@ \"+ID+\" Connected to server\"");
				}
			}

			// After signing in and regarding user`s role, the operations will come up.
			while (signedIn) {
				System.out.println("\n\nWelcome " + role + " " + ID + " to the menu. Please choose an operation:");
				try {
					// If user is an admin, the Create and Delete methods can be invoked only
					if (role.equals("admin")) {
						System.out.println("1- Create room");
						System.out.println("2- Delete room");
						System.out.println("3- ** KILL SERVER **");
						int choice = InputManager.getNumber("number of choice");
						switch (choice) {
						case (1):
							String createResult = adminOperations.createRoom(ID, InputManager.getNumber("room Number"),
									InputManager.getDate(), InputManager.getListOfTimesFromAdmin());
							System.out.println(createResult);
							logger.log(createResult);
							break;
						case (2):
							String deleteResult = adminOperations.deleteRoom(ID, InputManager.getNumber("room Number"),
									InputManager.getDate(), InputManager.getListOfTimesFromAdmin());
							System.out.println(deleteResult);
							logger.log(deleteResult);
							break;
						case (3):
							int campusKill;
							while (true) {
								System.out.println("\nWhich server do you want to kill?\n");
								System.out.println("1- Dorval");
								System.out.println("2- Kirkland");
								System.out.println("3- Westmount");
								campusKill = InputManager.getNumber("number of choice");
								if (campusKill == 1 || campusKill == 2 || campusKill == 3)
									break;
								else
									System.out.println("Wrong campus number. try again!");
							}
							String killServerString = studentOperations.killServer(campusKill==1?"DVL":campusKill==2?"KKL":"WST");
							System.out.println(killServerString);
							logger.log(killServerString);
							break;
						default:
							System.out.println("Wrong choice. Go back to menu and try again!");
						}

						// If a user is student, then he/she can only invoke book, get times and cancel
						// methods.
					} else {
						System.out.println("1- Book room");
						System.out.println("2- Get all available times number");
						System.out.println("3- Cancel reservation");
						System.out.println("4- Change time slot");
						System.out.println("5- ** KILL SERVER **");
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

							String bookResult = studentOperations.bookRoom(ID,
									campus == 1 ? "DVL" : campus == 2 ? "KKL" : "WST",
									InputManager.getNumber("Room Number"), InputManager.getDate(),
									InputManager.getTime("Start and End"));
							System.out.println(bookResult);
							logger.log(bookResult);
							break;
						case (2):
							String getTimeResult = studentOperations.getAvailableTimeSlot(ID, InputManager.getDate());
							System.out.println(getTimeResult);
							logger.log(getTimeResult);
							break;
						case (3):
							String cancelResult = studentOperations.cancelBooking(ID,
									InputManager.getString("booking ID"));
							System.out.println(cancelResult);
							logger.log(cancelResult);
							break;
						case (4):
							int campusChange;
							while (true) {
								System.out.println("\nIn which campus is new TimeSlot?\n");
								System.out.println("1- Dorval");
								System.out.println("2- Kirkland");
								System.out.println("3- Westmount");
								campusChange = InputManager.getNumber("number of choice");
								if (campusChange == 1 || campusChange == 2 || campusChange == 3)
									break;
								else
									System.out.println("Wrong campus number. try again!");
							}
							// DATE JUST FOR MOSTAFA !!!!!!!!!!!!!!!!!
							String changeTimeSlotResult = studentOperations.changeReservation(ID,
									InputManager.getString("booking ID"),
									campusChange == 1 ? "DVL" : campusChange == 2 ? "KKL" : "WST",
									InputManager.getNumber("room number"), InputManager.getDate(),
									InputManager.getTime("start and end"));
							System.out.println(changeTimeSlotResult);
							logger.log(changeTimeSlotResult);
							break;
						case (5):
							int campusKill;
							while (true) {
								System.out.println("\nWhich server do you want to kill?\n");
								System.out.println("1- Dorval");
								System.out.println("2- Kirkland");
								System.out.println("3- Westmount");
								campusKill = InputManager.getNumber("number of choice");
								if (campusKill == 1 || campusKill == 2 || campusKill == 3)
									break;
								else
									System.out.println("Wrong campus number. try again!");
							}
							String killServerString = studentOperations.killServer(campusKill==1?"DVL":campusKill==2?"KKL":"WST");
							System.out.println(killServerString);
							logger.log(killServerString);
							break;
						default:
							System.out.println("Wrong choice. Go back to menu and try again!");
							break;
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
							adminOperations.signOut(ID);
						else
							studentOperations.signOut(ID);
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

	private static boolean isCorrectID(String string) {
		Pattern pattern = Pattern.compile("^[0-9]{4}$");
		Matcher matcher = pattern.matcher(string);
		return matcher.find() && matcher.group().equals(string);
	}
}
