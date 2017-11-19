package comp6231.project.mostafa.core;

public class Constants {
	public static final int KKL_PORT = 2962;
	public static final int DVL_PORT = 2963;
	public static final int WST_PORT = 2961;
	public static final int FE_PORT = 2960;
			
	public static final int KKL_PORT_LISTEN = 2900;
	public static final int DVL_PORT_LISTEN = 2901;
	public static final int WST_PORT_LISTEN = 2902;
	public static final int FE_PORT_LISTEN = 2903;
	
	public static final int INITIAL_TIME = 7*24*60*3600;
//	public static final int INITIAL_TIME = 60;
	
	public static final String ERR_NOT_BOOKED_NOROOM = "notbooked(noroom)";
	public static final String ERR_NOT_BOOKED_NODATE = "notbooked(nodate)";
	public static final String ERR_NOT_BOOKED_NOTIME = "notbooked(notime)";
	public static final String ERR_NOT_BOOKED_NOTAV = "notbooked(notAvailable)";
	
	public static final String REDUCE_BOOK_COUNT = "reducebookcount";
	public static final String BOOK_ROOM = "bookRoom";
	public static final String REQ_GETAVTIME = "getAvailableTimeSlot";
	public static final String REQ_CANCEL_BOOK = "cancelBooking";
	public static final String REQ_REMOVE_BOOK = "removeBooking";
	
	public static final String RESULT_UDP_FAILD = "faild";
	
	public static final String COMMIT = "commit";
	public static final String ROLLBACK = "rollBack";
}
