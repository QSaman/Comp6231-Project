package comp6231.project.saman.common.corba.users;


/**
* comp6231/project/saman/common/corba/users/CorbaTimeSlotResultHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from SamanOperations.idl
* Saturday, November 11, 2017 9:10:31 PM EST
*/

abstract public class CorbaTimeSlotResultHelper
{
  private static String  _id = "IDL:comp6231/project/saman/common/corba/users/CorbaTimeSlotResult:1.0";

  public static void insert (org.omg.CORBA.Any a, comp6231.project.saman.common.corba.data_structure.CorbaTimeSlotResult that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static comp6231.project.saman.common.corba.data_structure.CorbaTimeSlotResult extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = comp6231.project.saman.common.corba.data_structure.CorbaTimeSlotResultHelper.type ();
      __typeCode = org.omg.CORBA.ORB.init ().create_alias_tc (comp6231.project.saman.common.corba.users.CorbaTimeSlotResultHelper.id (), "CorbaTimeSlotResult", __typeCode);
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static comp6231.project.saman.common.corba.data_structure.CorbaTimeSlotResult read (org.omg.CORBA.portable.InputStream istream)
  {
    comp6231.project.saman.common.corba.data_structure.CorbaTimeSlotResult value = null;
    value = comp6231.project.saman.common.corba.data_structure.CorbaTimeSlotResultHelper.read (istream);
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, comp6231.project.saman.common.corba.data_structure.CorbaTimeSlotResult value)
  {
    comp6231.project.saman.common.corba.data_structure.CorbaTimeSlotResultHelper.write (ostream, value);
  }

}
