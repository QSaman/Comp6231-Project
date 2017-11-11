package comp6231.project.saman.campus.message_protocol;

import com.google.gson.Gson;

/**
 * @author saman
 *
 */
public class MessageHeader {
	
	public enum CommandType
	{
		Book_Room,
		Cancel_Book_Room,
		Get_Available_TimeSlots,
		Remove_Student_Record,
		Start_Week,
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
