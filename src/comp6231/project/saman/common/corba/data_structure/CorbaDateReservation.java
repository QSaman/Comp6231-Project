package comp6231.project.saman.common.corba.data_structure;


/**
* comp6231/project/saman/common/corba/data_structure/CorbaDateReservation.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from SamanOperations.idl
* Saturday, November 11, 2017 9:10:31 PM EST
*/

public final class CorbaDateReservation implements org.omg.CORBA.portable.IDLEntity
{
  public int year = (int)0;
  public int month = (int)0;
  public int day = (int)0;

  public CorbaDateReservation ()
  {
  } // ctor

  public CorbaDateReservation (int _year, int _month, int _day)
  {
    year = _year;
    month = _month;
    day = _day;
  } // ctor

} // class CorbaDateReservation
