package comp6231.project.saman.common.corba.users;

/**
* comp6231/project/saman/common/corba/users/StudentOperationsHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from SamanOperations.idl
* Saturday, November 11, 2017 9:10:31 PM EST
*/

public final class StudentOperationsHolder implements org.omg.CORBA.portable.Streamable
{
  public comp6231.project.saman.common.corba.users.StudentOperations value = null;

  public StudentOperationsHolder ()
  {
  }

  public StudentOperationsHolder (comp6231.project.saman.common.corba.users.StudentOperations initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = comp6231.project.saman.common.corba.users.StudentOperationsHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    comp6231.project.saman.common.corba.users.StudentOperationsHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return comp6231.project.saman.common.corba.users.StudentOperationsHelper.type ();
  }

}