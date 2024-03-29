package comp6231.project.saman.common.corba.users;


/**
* comp6231/project/saman/common/corba/users/StudentOperationsPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from SamanOperations.idl
* Saturday, November 11, 2017 9:10:31 PM EST
*/

public abstract class StudentOperationsPOA extends org.omg.PortableServer.Servant
 implements comp6231.project.saman.common.corba.users.StudentOperationsOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("bookRoom", new java.lang.Integer (0));
    _methods.put ("getAvailableTimeSlot", new java.lang.Integer (1));
    _methods.put ("cancelBooking", new java.lang.Integer (2));
    _methods.put ("changeReservation", new java.lang.Integer (3));
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
       case 0:  // comp6231/project/saman/common/corba/users/StudentOperations/bookRoom
       {
         String user_id = in.read_string ();
         String campus_name = in.read_string ();
         int room_number = in.read_long ();
         comp6231.project.saman.common.corba.data_structure.CorbaDateReservation date = comp6231.project.saman.common.corba.users.CorbaDateReservationHelper.read (in);
         comp6231.project.saman.common.corba.data_structure.CorbaTimeSlot time_slot = comp6231.project.saman.common.corba.users.CorbaTimeSlotHelper.read (in);
         String $result = null;
         $result = this.bookRoom (user_id, campus_name, room_number, date, time_slot);
         out = $rh.createReply();
         comp6231.project.saman.common.corba.users.nullable_stringHelper.write (out, $result);
         break;
       }

       case 1:  // comp6231/project/saman/common/corba/users/StudentOperations/getAvailableTimeSlot
       {
         String user_id = in.read_string ();
         comp6231.project.saman.common.corba.data_structure.CorbaDateReservation date = comp6231.project.saman.common.corba.users.CorbaDateReservationHelper.read (in);
         comp6231.project.saman.common.corba.data_structure.CorbaTimeSlotResult $result[] = null;
         $result = this.getAvailableTimeSlot (user_id, date);
         out = $rh.createReply();
         comp6231.project.saman.common.corba.users.corba_timeslot_result_listHelper.write (out, $result);
         break;
       }

       case 2:  // comp6231/project/saman/common/corba/users/StudentOperations/cancelBooking
       {
         String user_id = in.read_string ();
         String bookingID = in.read_string ();
         boolean $result = false;
         $result = this.cancelBooking (user_id, bookingID);
         out = $rh.createReply();
         out.write_boolean ($result);
         break;
       }

       case 3:  // comp6231/project/saman/common/corba/users/StudentOperations/changeReservation
       {
         String user_id = in.read_string ();
         String booking_id = in.read_string ();
         String new_campus_name = in.read_string ();
         int new_room_number = in.read_long ();
         comp6231.project.saman.common.corba.data_structure.CorbaDateReservation new_date = comp6231.project.saman.common.corba.users.CorbaDateReservationHelper.read (in);
         comp6231.project.saman.common.corba.data_structure.CorbaTimeSlot new_time_slot = comp6231.project.saman.common.corba.users.CorbaTimeSlotHelper.read (in);
         String $result = null;
         $result = this.changeReservation (user_id, booking_id, new_campus_name, new_room_number, new_date, new_time_slot);
         out = $rh.createReply();
         comp6231.project.saman.common.corba.users.nullable_stringHelper.write (out, $result);
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:comp6231/project/saman/common/corba/users/StudentOperations:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public StudentOperations _this() 
  {
    return StudentOperationsHelper.narrow(
    super._this_object());
  }

  public StudentOperations _this(org.omg.CORBA.ORB orb) 
  {
    return StudentOperationsHelper.narrow(
    super._this_object(orb));
  }


} // class StudentOperationsPOA
