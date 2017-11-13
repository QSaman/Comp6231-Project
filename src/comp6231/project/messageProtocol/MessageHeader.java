package comp6231.project.messageProtocol;


/**
 * @author saman
 *
 */
public class MessageHeader {
	
	// some of the Command Types are shared between saman(S),mostafa(M),farid(F).
	public enum CommandType
	{
		Book_Room,
		Cancel_Book_Room,
		Get_Available_TimeSlots,
		S_Remove_Student_Record,
		S_Start_Week,
		M_Reduce_Book_Count,
		M_Remove_BookingId,
		M_Commit,
		M_Rollback
	}
	
	public enum MessageType
	{
		Request,
		Reply
	}
	
	public enum ProtocolType
	{
		Frontend_To_Replica,
		Server_To_Server
	}
	
	public int sequence_number;
	public CommandType command_type;
	public MessageType message_type;
	public ProtocolType protocol_type;
	
	public MessageHeader(int sequence_number, CommandType command_type, MessageType message_type, ProtocolType protocol_type)
	{
		this.sequence_number = sequence_number;
		this.command_type = command_type;
		this.message_type = message_type;
		this.protocol_type = protocol_type;
	}

	public MessageHeader() {
		// TODO Auto-generated constructor stub
	}
	

}
