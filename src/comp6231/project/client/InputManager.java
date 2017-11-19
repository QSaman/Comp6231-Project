package comp6231.project.client;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * Created by Farid on 9/24/2017.
 */
public class InputManager {

	@SuppressWarnings("resource")
	static int getNumber(String fieldName) {
		System.out.println("\nPlease enter " + fieldName + " :");
		return new Scanner(System.in).nextInt();
	}

	@SuppressWarnings("resource")
	static String getString(String fieldName) {
		System.out.println("\nPlease enter " + fieldName + " :");
		return new Scanner(System.in).nextLine();
	}

	public static String getDate() {
		System.out.println("Please enter a date: YYYY/MM/DD");

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine();

		int year = Integer.parseInt(input.substring(0, 4));
		int month = Integer.parseInt(input.substring(5, input.substring(5).indexOf("/") + 5));
		int day = Integer.parseInt(input.substring(input.substring(5).indexOf("/") + 6));

		return LocalDate.of(year, month, day).toString();
	}

	@SuppressWarnings("resource")
	static String getTime(String timeType) {
		System.out.println("Please enter " + timeType + " time: HH:MM");

		// String time = new Scanner(System.in).nextLine();

		// int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
		// int minute = Integer.parseInt(time.substring(time.indexOf(":") + 1));

		return new Scanner(System.in).nextLine();
	}

	static String[] getListOfTimesFromAdmin() {
		LinkedHashMap<LocalTime, LocalTime> listOfTimesFromAdmin = new LinkedHashMap<>();
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		boolean isFinishedListInput = false;

		System.out.println("\n\n! You have to enter each time like this: " + "\n(Start time: 10:30)"
				+ "\n(End time: 13:00)" + "\n\n===> Please enter a list of times below: ");

		while (!isFinishedListInput) {

			System.out.println("Start time: ");
			String startTime = scanner.nextLine();
			System.out.println("End Time: ");
			String endTime = scanner.nextLine();

			int startTimeHour = Integer.parseInt(startTime.substring(0, startTime.indexOf(":")));
			int startTimeMinute = Integer.parseInt(startTime.substring(startTime.indexOf(":") + 1));
			int endTimeHour = Integer.parseInt(endTime.substring(0, endTime.indexOf(":")));
			int endTimeMinute = Integer.parseInt(endTime.substring(endTime.indexOf(":") + 1));

			LocalTime startTimeLocalTime = LocalTime.of(startTimeHour, startTimeMinute);
			LocalTime endTimeLocalTime = LocalTime.of(endTimeHour, endTimeMinute);

			if (!listOfTimesFromAdmin.containsKey(startTimeLocalTime))
				listOfTimesFromAdmin.put(startTimeLocalTime, endTimeLocalTime);

			System.out.println("Add next? (y) or (n)");
			char respond = scanner.nextLine().toLowerCase().charAt(0);

			if (respond == 'n') {
				isFinishedListInput = true;
				System.out.println("You have finished adding list.");
			} else
				System.out.println("==> Add next time: ");
		}

		String[] reslut = new String[listOfTimesFromAdmin.size()];
		int counter = 0;
		for (Entry<LocalTime, LocalTime> entry : listOfTimesFromAdmin.entrySet()) {
			reslut[counter] = toString(entry.getKey()) + "-" + toString(entry.getValue());
			counter++;
		}
		return reslut;
	}
	
	private static String toString(LocalTime time){
		return time.getHour()+":"+time.getMinute();		
	}
}
