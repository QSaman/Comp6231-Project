package comp6231.project.common.corba.users;


/**
* comp6231/project/common/corba/users/StudentOperationsOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Operations.idl
* Friday, December 1, 2017 4:13:59 PM EST
*/

public interface StudentOperationsOperations 
{
  String bookRoom (String user_id, String campus_name, int room_number, String date, String time_slot);
  String getAvailableTimeSlot (String user_id, String date);
  String cancelBooking (String user_id, String bookingID);
  String changeReservation (String user_id, String booking_id, String new_campus_name, int new_room_number, String new_date, String new_time_slot);
  boolean studentLogin (String studentID);
  void signOut (String ID);
  String killServer (String campusName);
} // interface StudentOperationsOperations
