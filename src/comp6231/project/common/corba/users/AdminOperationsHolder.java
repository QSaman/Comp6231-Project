package comp6231.project.common.corba.users;

/**
* comp6231/project/common/corba/users/AdminOperationsHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from /Users/wmg/Documents/workspace/Comp6231-Project/Operations.idl
* Thursday, November 9, 2017 10:26:03 PM EST
*/

public final class AdminOperationsHolder implements org.omg.CORBA.portable.Streamable
{
  public comp6231.project.common.corba.users.AdminOperations value = null;

  public AdminOperationsHolder ()
  {
  }

  public AdminOperationsHolder (comp6231.project.common.corba.users.AdminOperations initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = comp6231.project.common.corba.users.AdminOperationsHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    comp6231.project.common.corba.users.AdminOperationsHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return comp6231.project.common.corba.users.AdminOperationsHelper.type ();
  }

}