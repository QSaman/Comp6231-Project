package comp6231.project.common.corba.users;


/**
* comp6231/project/common/corba/users/StudentOperationsHelper.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Operations.idl
* Friday, December 1, 2017 4:13:59 PM EST
*/

abstract public class StudentOperationsHelper
{
  private static String  _id = "IDL:comp6231/project/common/corba/users/StudentOperations:1.0";

  public static void insert (org.omg.CORBA.Any a, comp6231.project.common.corba.users.StudentOperations that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static comp6231.project.common.corba.users.StudentOperations extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      __typeCode = org.omg.CORBA.ORB.init ().create_interface_tc (comp6231.project.common.corba.users.StudentOperationsHelper.id (), "StudentOperations");
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static comp6231.project.common.corba.users.StudentOperations read (org.omg.CORBA.portable.InputStream istream)
  {
    return narrow (istream.read_Object (_StudentOperationsStub.class));
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, comp6231.project.common.corba.users.StudentOperations value)
  {
    ostream.write_Object ((org.omg.CORBA.Object) value);
  }

  public static comp6231.project.common.corba.users.StudentOperations narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof comp6231.project.common.corba.users.StudentOperations)
      return (comp6231.project.common.corba.users.StudentOperations)obj;
    else if (!obj._is_a (id ()))
      throw new org.omg.CORBA.BAD_PARAM ();
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      comp6231.project.common.corba.users._StudentOperationsStub stub = new comp6231.project.common.corba.users._StudentOperationsStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

  public static comp6231.project.common.corba.users.StudentOperations unchecked_narrow (org.omg.CORBA.Object obj)
  {
    if (obj == null)
      return null;
    else if (obj instanceof comp6231.project.common.corba.users.StudentOperations)
      return (comp6231.project.common.corba.users.StudentOperations)obj;
    else
    {
      org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl)obj)._get_delegate ();
      comp6231.project.common.corba.users._StudentOperationsStub stub = new comp6231.project.common.corba.users._StudentOperationsStub ();
      stub._set_delegate(delegate);
      return stub;
    }
  }

}
