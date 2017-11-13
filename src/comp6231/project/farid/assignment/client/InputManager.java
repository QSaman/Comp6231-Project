package assignment.client;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
 * Created by Farid on 9/24/2017.
 */
public class InputManager {

    static int getNumber(String fieldName) {
        System.out.println("\nPlease enter " + fieldName + " :");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextInt();
    }

    static String getString(String fieldName) {
        System.out.println("\nPlease enter " + fieldName + " :");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public static String getDate() {
        System.out.println("Please enter a date: YYYY/MM/DD");

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();

        int year = Integer.parseInt(input.substring(0, 4));
        int month = Integer.parseInt(input.substring(5, input.substring(5).indexOf("/") + 5));
        int day = Integer.parseInt(input.substring(input.substring(5).indexOf("/") + 6));

        return LocalDate.of(year, month, day).toString();
    }

    static String getTime(String timeType) {
        System.out.println("Please enter " + timeType + " time: HH:MM");

        Scanner scanner = new Scanner(System.in);
        String time = scanner.nextLine();

        int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
        int minute = Integer.parseInt(time.substring(time.indexOf(":") + 1));

        return LocalTime.of(hour, minute).toString();
    }

    static String getListOfTimesFromAdmin() {
        LinkedHashMap<LocalTime, LocalTime> listOfTimesFromAdmin = new LinkedHashMap<>();
        Scanner scanner = new Scanner(System.in);
        //boolean isFinishedListInput = false;

        System.out.println("\n\n! You have to enter each time like this: " +
                "\n(Start time: 10:30)" +
                "\n(End time: 13:00)" +
                "\n\n===> Please enter a list of times below: ");

        //while (!isFinishedListInput) {

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

            //if (!listOfTimesFromAdmin.containsKey(startTimeLocalTime))
            //    listOfTimesFromAdmin.put(startTimeLocalTime, endTimeLocalTime);

            //System.out.println("Add next? (y) or (n)");
            //char respond = scanner.nextLine().toLowerCase().charAt(0);

            //if (respond == 'n') {
            //    isFinishedListInput = true;
            //    System.out.println("You have finished adding list.");
            //} else
            //    System.out.println("==> Add next time: ");
        //}

        return startTimeLocalTime.toString()+endTimeLocalTime.toString();
    }
}
