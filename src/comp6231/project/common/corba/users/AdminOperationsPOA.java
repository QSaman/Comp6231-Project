package comp6231.project.common.corba.users;


/**
* comp6231/project/common/corba/users/AdminOperationsPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Operations.idl
* Wednesday, December 6, 2017 5:02:12 PM EST
*/

public abstract class AdminOperationsPOA extends org.omg.PortableServer.Servant
 implements comp6231.project.common.corba.users.AdminOperationsOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("createRoom", new java.lang.Integer (0));
    _methods.put ("deleteRoom", new java.lang.Integer (1));
    _methods.put ("adminLogin", new java.lang.Integer (2));
    _methods.put ("signOut", new java.lang.Integer (3));
    _methods.put ("killServer", new java.lang.Integer (4));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // comp6231/project/common/corba/users/AdminOperations/createRoom
       {
         String user_id = in.read_string ();
         int room_number = in.read_long ();
         String date = in.read_string ();
         String time_slots[] = comp6231.project.common.corba.users.corba_timeslot_listHelper.read (in);
         String $result = null;
         $result = this.createRoom (user_id, room_number, date, time_slots);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 1:  // comp6231/project/common/corba/users/AdminOperations/deleteRoom
       {
         String user_id = in.read_string ();
         int room_number = in.read_long ();
         String date = in.read_string ();
         String time_slots[] = comp6231.project.common.corba.users.corba_timeslot_listHelper.read (in);
         String $result = null;
         $result = this.deleteRoom (user_id, room_number, date, time_slots);
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 2:  // comp6231/project/common/corba/users/AdminOperations/adminLogin
       {
         String adminID = in.read_string ();
         boolean $result = false;
         $result = this.adminLogin (adminID);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 3:  // comp6231/project/common/corba/users/AdminOperations/signOut
       {
         String ID = in.read_string ();
         this.signOut (ID);
         out = $rh.createReply();
         break;
       }

       case 4:  // comp6231/project/common/corba/users/AdminOperations/killServer
       {
         String campusName = in.read_string ();
         this.killServer (campusName);
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:comp6231/project/common/corba/users/AdminOperations:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public AdminOperations _this() 
  {
    return AdminOperationsHelper.narrow(
    super._this_object());
  }

  public AdminOperations _this(org.omg.CORBA.ORB orb) 
  {
    return AdminOperationsHelper.narrow(
    super._this_object(orb));
  }


} // class AdminOperationsPOA
