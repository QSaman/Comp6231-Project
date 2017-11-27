# Fall 2017 Project

# UDP MESSAGE STANDARD
** REQUIRED KEY : "Method","SequenceNumber"

# Student:

```
JSON bookRoom
{
"MessageType"    : Constants.FRONTEND_TO_SERVER/Constants.SERVER_TO_SERVER,
"Legacy"         : "legacy code",
"Method"         : Constants.BOOK_ROOM,
"UserId"         : "dvls1000",
"CampusName"     : "dvl" ,
"RoomNumber"     : "100",
"Date"           : "2017(year)-05(month)-01(day)",
"TimeSlot"       : "10:15-11:20",
"SequenceNumber" : "1"
}

JSON getAvailableTimeSlot
{
"MessageType"    : Constants.FRONTEND_TO_SERVER/Constants.SERVER_TO_SERVER,
"Legacy"         : "legacy code",
"Method"         : Constants.GET_AVAILABLE_TIME_SLOT,
"UserId"         : "wsts1000",
"Date"           : "2017(year)-05(month)-01(day)",
"SequenceNumber" : "1"
}

JSON CancelBooking
{
"MessageType"    : Constants.FRONTEND_TO_SERVER/Constants.SERVER_TO_SERVER,
"Legacy"         : "legacy code",
"Method"         : Constants.CANCEL_BOOKING,
"UserId"         : "wsts1000",
"Date"           : "2017(year)-05(month)-01(day)",
"BookingIdRm1"   : "saman",
"BookingIdRm2"   : "farid",
"BookingIdRm3"   : "mostafa",
"SequenceNumber" : "1"
}
JSON ChangeReservation
{
"MessageType"    : Constants.FRONTEND_TO_SERVER/Constants.SERVER_TO_SERVER,
"Legacy"         : "legacy code",
"Method"         : Constants.CHANGE_RESERVATION,
"UserId"         : "wsts1000",
"NewCampus"      : "dvl",
"NewRoomNumber"  : "100",
"NewDate"        : "2017(year)-05(month)-01(day)",
"NewTimeSlot"    : "10:15-11:20",
"SequenceNumber" : "1"
}

JSON LoginStudent
{
"MessageType"    : Constants.FRONTEND_TO_SERVER/Constants.SERVER_TO_SERVER,
"Legacy"         : "legacy code",
"Method"         : Constants.LOGINSTUDENT,
"UserId"         : "studentId",
"SequenceNumber" : "1"
}

# Admin:

JSON Create
{
"MessageType"    : Constants.FRONTEND_TO_SERVER/Constants.SERVER_TO_SERVER,
"Legacy"         : "legacy code",
"Method"         : Constants.CREATE,
"UserId"         : "dvls1000",
"RoomNumber"     : "100",
"Date"           : "2017(year)-05(month)-01(day)",
"TimeSlotList"   : ["10:15-11:20","12:00-13:00"],
"SequenceNumber" : "1"
}

JSON Delete
{
"MessageType"    : Constants.FRONTEND_TO_SERVER/Constants.SERVER_TO_SERVER,
"Legacy"         : "legacy code",
"Method"         : Constants.DELETE,
"UserId"         : "dvls1000",
"RoomNumber"     : "100",
"Date"           : "2017(year)-05(month)-01(day)",
"TimeSlotList"   : ["10:15-11:20","12:00-13:00"],
"SequenceNumber" : "1"
}

JSON LoginAdmin
{
"MessageType"    : Constants.FRONTEND_TO_SERVER/Constants.SERVER_TO_SERVER,
"Legacy"         : "legacy code",
"Method"         : Constants.LOGINADMIN,
"UserId"         : "studentId",
"SequenceNumber" : "1"
}

# Shared:

JSON Logout
{
"MessageType"    : Constants.FRONTEND_TO_SERVER/Constants.SERVER_TO_SERVER,
"Legacy"         : "legacy code",
"Method"         : Constants.LOGOUT,
"UserId"         : "studentId",
"SequenceNumber" : "1"
}
```
