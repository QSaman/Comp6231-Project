package comp6231.project.common.corba.users;


/**
* comp6231/project/common/corba/users/AdminOperationsOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Operations.idl
* Thursday, December 7, 2017 12:30:29 AM EST
*/

public interface AdminOperationsOperations 
{
  String createRoom (String user_id, int room_number, String date, String[] time_slots);
  String deleteRoom (String user_id, int room_number, String date, String[] time_slots);
  boolean adminLogin (String adminID);
  void signOut (String ID);
  void killServer (String campusName);
  void fakeGenerate (String replicaName, String campusName);
} // interface AdminOperationsOperations
