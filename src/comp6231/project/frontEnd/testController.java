package comp6231.project.frontEnd;

import comp6231.project.frontEnd.udp.ErrorHandler;
import comp6231.project.replicaManager.messages.RMKillMessage;

public class testController {

	public static void main(String[] args) {

		while (true) {

//			System.out.println("1- ** KILL SERVER **");
			System.out.println("1- ** FAKE GENERATOR **");

			int choice = InputManager.getNumber("number of choice");
			switch (choice) {
//			case (1):
//				int campusKill;
//				while (true) {
//					System.out.println("\nWhich server do you want to kill?\n");
//					System.out.println("1- Dorval-Farid");
//					System.out.println("2- Kirkland-Farid");
//					System.out.println("3- Westmount-Farid");
//					System.out.println("4- Dorval-Saman");
//					System.out.println("5- Kirkland-Saman");
//					System.out.println("6- Westmount-Saman");
//					System.out.println("7- Dorval-Mostafa");
//					System.out.println("8- Kirkland-Mostafa");
//					System.out.println("9- Westmount-Mostafa");
//					campusKill = InputManager.getNumber("number of choice");
//					if (campusKill == 1 || campusKill == 2 || campusKill == 3 || campusKill == 4 || campusKill == 5
//							|| campusKill == 6 || campusKill == 7 || campusKill == 8 || campusKill == 9)
//						break;
//					else
//						System.out.println("Wrong number. try again!");
//				}
//				String killServerString = "==> Killed and switched.";
//				killServer(campusKill == 1 ? "FDVL"
//						: campusKill == 2 ? "FKKL"
//								: campusKill == 3 ? "FWST"
//										: campusKill == 4 ? "SDVL"
//												: campusKill == 5 ? "SKKL"
//														: campusKill == 6 ? "SWST"
//																: campusKill == 7 ? "MDVL"
//																		: campusKill == 8 ? "MKKL" : "MWST");
//				System.out.println(killServerString);
//				break;
			case (1):
				int fakeGen;
				while (true) {
					System.out.println("\nWhich server do you want to generate fake?\n");
					System.out.println("1- Dorval-Farid");
					System.out.println("2- Kirkland-Farid");
					System.out.println("3- Westmount-Farid");
					System.out.println("4- Dorval-Saman");
					System.out.println("5- Kirkland-Saman");
					System.out.println("6- Westmount-Saman");
					System.out.println("7- Dorval-Mostafa");
					System.out.println("8- Kirkland-Mostafa");
					System.out.println("9- Westmount-Mostafa");
					fakeGen = InputManager.getNumber("number of choice");
					if (fakeGen == 1 || fakeGen == 2 || fakeGen == 3 || fakeGen == 4 || fakeGen == 5 || fakeGen == 6
							|| fakeGen == 7 || fakeGen == 8 || fakeGen == 9)
						break;
					else
						System.out.println("Wrong number. try again!");
				}
				String fakeGenString = "==> fake generated.";
				fakeGenerate(
						fakeGen == 1 ? "Farid"
								: fakeGen == 2 ? "Farid"
										: fakeGen == 3 ? "Farid"
												: fakeGen == 4 ? "Saman"
														: fakeGen == 5 ? "Saman"
																: fakeGen == 6 ? "Saman"
																		: fakeGen == 7 ? "Mostafa"
																				: fakeGen == 8 ? "Mostafa" : "Mostafa",
						fakeGen == 1 ? "DVL"
								: fakeGen == 2 ? "KKL"
										: fakeGen == 3 ? "WST"
												: fakeGen == 4 ? "DVL"
														: fakeGen == 5 ? "KKL"
																: fakeGen == 6 ? "WST"
																		: fakeGen == 7 ? "DVL"
																				: fakeGen == 8 ? "KKL" : "WST");
				System.out.println(fakeGenString);
				break;
			default:
				System.out.println("Wrong choice. Go back to menu and try again!");
			}
		}
	}

	public static void killServer(String campusName) {
		PortSwitcher.switchServer(campusName);
		RMKillMessage message = new RMKillMessage(-1, campusName);
		new Thread((new ErrorHandler(FEUtility.getInstance().findRMPort(campusName), message))).start();
	}

	public static void fakeGenerate(String replicaName, String campusName) {
		new Thread(new ErrorHandler(replicaName, false)).start();
	}

}
