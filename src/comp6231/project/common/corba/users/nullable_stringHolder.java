package comp6231.project.common.corba.users;

/**
* comp6231/project/common/corba/users/nullable_stringHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from /Users/wmg/Documents/workspace/Comp6231-Project/Operations.idl
* Thursday, November 9, 2017 10:26:03 PM EST
*/

public final class nullable_stringHolder implements org.omg.CORBA.portable.Streamable
{
  public String value;

  public nullable_stringHolder ()
  {
  }

  public nullable_stringHolder (String initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = i.read_string ();
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    o.write_string (value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return comp6231.project.common.corba.users.nullable_stringHelper.type ();
  }

}