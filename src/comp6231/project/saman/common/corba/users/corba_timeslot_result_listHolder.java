package comp6231.project.saman.common.corba.users;


/**
* comp6231/project/saman/common/corba/users/corba_timeslot_result_listHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from SamanOperations.idl
* Saturday, November 11, 2017 9:10:31 PM EST
*/

public final class corba_timeslot_result_listHolder implements org.omg.CORBA.portable.Streamable
{
  public comp6231.project.saman.common.corba.data_structure.CorbaTimeSlotResult value[] = null;

  public corba_timeslot_result_listHolder ()
  {
  }

  public corba_timeslot_result_listHolder (comp6231.project.saman.common.corba.data_structure.CorbaTimeSlotResult[] initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = comp6231.project.saman.common.corba.users.corba_timeslot_result_listHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    comp6231.project.saman.common.corba.users.corba_timeslot_result_listHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return comp6231.project.saman.common.corba.users.corba_timeslot_result_listHelper.type ();
  }

}
