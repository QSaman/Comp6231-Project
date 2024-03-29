package comp6231.project.saman.common.corba.users;


/**
* comp6231/project/saman/common/corba/users/_StudentOperationsStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from SamanOperations.idl
* Saturday, November 11, 2017 9:10:31 PM EST
*/

public class _StudentOperationsStub extends org.omg.CORBA.portable.ObjectImpl implements comp6231.project.saman.common.corba.users.StudentOperations
{

  public String bookRoom (String user_id, String campus_name, int room_number, comp6231.project.saman.common.corba.data_structure.CorbaDateReservation date, comp6231.project.saman.common.corba.data_structure.CorbaTimeSlot time_slot)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("bookRoom", true);
                $out.write_string (user_id);
                $out.write_string (campus_name);
                $out.write_long (room_number);
                comp6231.project.saman.common.corba.users.CorbaDateReservationHelper.write ($out, date);
                comp6231.project.saman.common.corba.users.CorbaTimeSlotHelper.write ($out, time_slot);
                $in = _invoke ($out);
                String $result = comp6231.project.saman.common.corba.users.nullable_stringHelper.read ($in);
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

  public comp6231.project.saman.common.corba.data_structure.CorbaTimeSlotResult[] getAvailableTimeSlot (String user_id, comp6231.project.saman.common.corba.data_structure.CorbaDateReservation date)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getAvailableTimeSlot", true);
                $out.write_string (user_id);
                comp6231.project.saman.common.corba.users.CorbaDateReservationHelper.write ($out, date);
                $in = _invoke ($out);
                comp6231.project.saman.common.corba.data_structure.CorbaTimeSlotResult $result[] = comp6231.project.saman.common.corba.users.corba_timeslot_result_listHelper.read ($in);
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

  public boolean cancelBooking (String user_id, String bookingID)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("cancelBooking", true);
                $out.write_string (user_id);
                $out.write_string (bookingID);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
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

  public String changeReservation (String user_id, String booking_id, String new_campus_name, int new_room_number, comp6231.project.saman.common.corba.data_structure.CorbaDateReservation new_date, comp6231.project.saman.common.corba.data_structure.CorbaTimeSlot new_time_slot)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("changeReservation", true);
                $out.write_string (user_id);
                $out.write_string (booking_id);
                $out.write_string (new_campus_name);
                $out.write_long (new_room_number);
                comp6231.project.saman.common.corba.users.CorbaDateReservationHelper.write ($out, new_date);
                comp6231.project.saman.common.corba.users.CorbaTimeSlotHelper.write ($out, new_time_slot);
                $in = _invoke ($out);
                String $result = comp6231.project.saman.common.corba.users.nullable_stringHelper.read ($in);
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

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:comp6231/project/saman/common/corba/users/StudentOperations:1.0"};

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
