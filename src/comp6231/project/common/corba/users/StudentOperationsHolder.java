package comp6231.project.common.corba.users;

/**
* comp6231/project/common/corba/users/StudentOperationsHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Operations.idl
* Friday, December 1, 2017 4:13:59 PM EST
*/

public final class StudentOperationsHolder implements org.omg.CORBA.portable.Streamable
{
  public comp6231.project.common.corba.users.StudentOperations value = null;

  public StudentOperationsHolder ()
  {
  }

  public StudentOperationsHolder (comp6231.project.common.corba.users.StudentOperations initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = comp6231.project.common.corba.users.StudentOperationsHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    comp6231.project.common.corba.users.StudentOperationsHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return comp6231.project.common.corba.users.StudentOperationsHelper.type ();
  }

}
