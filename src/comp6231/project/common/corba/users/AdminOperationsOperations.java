package comp6231.project.common.corba.users;


/**
* comp6231/project/common/corba/users/AdminOperationsOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from /Users/wmg/Documents/workspace/Comp6231-Project/Operations.idl
* Thursday, November 9, 2017 10:26:03 PM EST
*/

public interface AdminOperationsOperations 
{
  String createRoom (String user_id, int room_number, String date, String[] time_slots);
  String deleteRoom (String user_id, int room_number, String date, String[] time_slots);
  boolean adminLogin (String adminID);
  void signOut (String ID);
} // interface AdminOperationsOperations