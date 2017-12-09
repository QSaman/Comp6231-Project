package comp6231.shared;

public class Constants {
	
	public static final String NULL_STRING = "Null String";
	public static final String DILIMITER_STRING = "*%#$@#!";
	public static final String ONE_WAY = "ONE WAY MESSAGE";
	
	//IP CONFIG
	public static final String LOCAL_IP = "127.0.0.1";
//	public static final String FE_CLIENT_IP = "172.31.80.10";
//	public static final String FARID_IP =  "172.31.80.10";
//	public static final String SAMAN_IP = "172.20.10.9";
//	public static final String MOSTAFA_IP = "172.20.10.9";
	
	public static final String FE_CLIENT_IP = LOCAL_IP;
	public static final String FARID_IP =  LOCAL_IP;
	public static final String SAMAN_IP = LOCAL_IP;
	public static final String MOSTAFA_IP = LOCAL_IP;
	
	// RE1 = mostafa RE2 = saman RE3 = farid
	// Replica managers
	public static final String RM_FARID = "rm_farid";
	public static final String RM_RE1 = "rm_re1";
	public static final String RM_RE2 = "rm_re2";
	
	public static final int RM_PORT_LISTEN_Farid = 2973;
	public static final int RM_PORT_LISTEN_RE1 = 2974;
	public static final int RM_PORT_LISTEN_RE2 = 2975;
	
	//RE1
	public static final int KKL_PORT_LISTEN_RE1_ORIGINAL = 2900;
	public static final int DVL_PORT_LISTEN_RE1_ORIGINAL = 2901;
	public static final int WST_PORT_LISTEN_RE1_ORIGINAL = 2902;
	
	public static final int KKL_PORT_LISTEN_RE1_BACKUP = 2803;
	public static final int DVL_PORT_LISTEN_RE1_BACKUP = 2904;
	public static final int WST_PORT_LISTEN_RE1_BACKUP = 2905;
	
	public static final int KKL_PORT_RE1_ORIGINAL = 2962;
	public static final int DVL_PORT_RE1_ORIGINAL = 2963;
	public static final int WST_PORT_RE1_ORGINAL = 2961;

	public static final int KKL_PORT_RE1_BACKUP = 2964;
	public static final int DVL_PORT_RE1_BACKUP = 2965;
	public static final int WST_PORT_RE1_BACKUP = 2966;
			
	public static int kklPortListenRe1Active = KKL_PORT_LISTEN_RE1_ORIGINAL;
	public static int dvlPortListenRe1Active = DVL_PORT_LISTEN_RE1_ORIGINAL;
	public static int wstPortListenRe1Active = WST_PORT_LISTEN_RE1_ORIGINAL;
	
	//RE2
	//2906
	//2907
	//2908
//	public static final int KKL_PORT_LISTEN_RE2_ORIGINAL = 2906;
//	public static final int DVL_PORT_LISTEN_RE2_ORIGINAL = 2907;
//	public static final int WST_PORT_LISTEN_RE2_ORIGINAL = 2908;
	public static final int KKL_PORT_LISTEN_RE2_ORIGINAL = 7778;
	public static final int DVL_PORT_LISTEN_RE2_ORIGINAL = 7777;
	public static final int WST_PORT_LISTEN_RE2_ORIGINAL = 7779;
	
	//2909
	//2910
	//2911
//	public static final int KKL_PORT_LISTEN_RE2_BACKUP = 2909;
//	public static final int DVL_PORT_LISTEN_RE2_BACKUP = 2910;
//	public static final int WST_PORT_LISTEN_RE2_BACKUP = 2911;
	public static final int KKL_PORT_LISTEN_RE2_BACKUP = 6668;
	public static final int DVL_PORT_LISTEN_RE2_BACKUP = 6667;
	public static final int WST_PORT_LISTEN_RE2_BACKUP = 6669;
	
	public static final int KKL_PORT_RE2_ORIGINAL = 2967;
	public static final int DVL_PORT_RE2_ORIGINAL = 2968;
	public static final int WST_PORT_RE2_ORGINAL = 2969;

	public static final int KKL_PORT_RE2_BACKUP = 2970;
	public static final int DVL_PORT_RE2_BACKUP = 2971;
	public static final int WST_PORT_RE2_BACKUP = 2972;
			
	public static int kklPortListenRe2Active = KKL_PORT_LISTEN_RE2_ORIGINAL;
	public static int dvlPortListenRe2Active = DVL_PORT_LISTEN_RE2_ORIGINAL;
	public static int wstPortListenRe2Active = WST_PORT_LISTEN_RE2_ORIGINAL;
	
	//FE
	public static final int FE_PORT = 2960;
	public static final int FE_PORT_LISTEN = 2903;
	
	//Time RE1 & RE2 
	public static final int INITIAL_TIME = 7*24*60*3600;
//	public static final int INITIAL_TIME = 60;
	
	// Server to server  RE1 & RE2
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
	
	public static final int BUFFER_SIZE = 1024;
	
	
	//Saman
	public static final int DVL_PORT_LISTEN_SAMAN_ORIGINAL = 7777;
	public static final int KKL_PORT_LISTEN_SAMAN_ORIGINAL = 7778;
	public static final int WST_PORT_LISTEN_SAMAN_ORIGINAL = 7779;
	
	public static final int DVL_PORT_LISTEN_SAMAN_BACKUP = 6667;
	public static final int KKL_PORT_LISTEN_SAMAN_BACKUP = 6668;
	public static final int WST_PORT_LISTEN_SAMAN_BACKUP = 6669;
	
	// RE3
	public static final int DVL_PORT_LISTEN_FARID_ORIGINAL = 9763;
	public static final int KKL_PORT_LISTEN_FARID_ORIGINAL = 9867;
	public static final int WST_PORT_LISTEN_FARID_ORIGINAL = 9635;
	
	public static final int DVL_PORT_LISTEN_FARID_BACKUP= 9764;
	public static final int KKL_PORT_LISTEN_FARID_BACKUP = 9868;
	public static final int WST_PORT_LISTEN_FARID_BACKUP = 9636;
	
	
	public static int dvlPortListenFaridActive = DVL_PORT_LISTEN_FARID_ORIGINAL;
	public static int kklPortListenFaridActive = KKL_PORT_LISTEN_FARID_ORIGINAL;
	public static int wstPortListenFaridActive = WST_PORT_LISTEN_FARID_ORIGINAL;

	// multi cast group
	
	public static final String DVL_GROUP  = "DVL";
	public static final String KKL_GROUP  = "KKL";
	public static final String WST_GROUP  = "WST";
	public static final int ACTIVE_SERVERS = 3;
}
