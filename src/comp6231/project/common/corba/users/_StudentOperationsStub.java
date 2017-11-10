package comp6231.project.common.corba.users;


/**
* comp6231/project/common/corba/users/_StudentOperationsStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from /Users/wmg/Documents/workspace/Comp6231-Project/Operations.idl
* Thursday, November 9, 2017 10:26:03 PM EST
*/

public class _StudentOperationsStub extends org.omg.CORBA.portable.ObjectImpl implements comp6231.project.common.corba.users.StudentOperations
{

  public String bookRoom (String user_id, String campus_name, int room_number, String date, String time_slot)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("bookRoom", true);
                $out.write_string (user_id);
                $out.write_string (campus_name);
                $out.write_long (room_number);
                $out.write_string (date);
                $out.write_string (time_slot);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return bookRoom (user_id, campus_name, room_number, date, time_slot        );
            } finally {
                _releaseReply ($in);
            }
  } // bookRoom

  public String getAvailableTimeSlot (String user_id, String date)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getAvailableTimeSlot", true);
                $out.write_string (user_id);
                $out.write_string (date);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getAvailableTimeSlot (user_id, date        );
            } finally {
                _releaseReply ($in);
            }
  } // getAvailableTimeSlot

  public String cancelBooking (String user_id, String bookingID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("cancelBooking", true);
                $out.write_string (user_id);
                $out.write_string (bookingID);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return cancelBooking (user_id, bookingID        );
            } finally {
                _releaseReply ($in);
            }
  } // cancelBooking

  public String changeReservation (String user_id, String booking_id, String new_campus_name, int new_room_number, String new_date, String new_time_slot)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("changeReservation", true);
                $out.write_string (user_id);
                $out.write_string (booking_id);
                $out.write_string (new_campus_name);
                $out.write_long (new_room_number);
                $out.write_string (new_date);
                $out.write_string (new_time_slot);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return changeReservation (user_id, booking_id, new_campus_name, new_room_number, new_date, new_time_slot        );
            } finally {
                _releaseReply ($in);
            }
  } // changeReservation

  public boolean studentLogin (String studentID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("studentLogin", true);
                $out.write_string (studentID);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return studentLogin (studentID        );
            } finally {
                _releaseReply ($in);
            }
  } // studentLogin

  public void signOut (String ID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("signOut", false);
                $out.write_string (ID);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                signOut (ID        );
            } finally {
                _releaseReply ($in);
            }
  } // signOut

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:comp6231/project/common/corba/users/StudentOperations:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _StudentOperationsStub
