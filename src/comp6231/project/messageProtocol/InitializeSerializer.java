/**
 * 
 */
package comp6231.project.messageProtocol;

import comp6231.project.messageProtocol.MessageHeader.CommandType;
import comp6231.project.messageProtocol.MessageHeader.MessageType;
import comp6231.project.messageProtocol.MessageHeader.ProtocolType;
import comp6231.project.saman.campus.message_protocol.saman_replica.ReplicaReplyAvailableTimeSlots;
import comp6231.project.saman.campus.message_protocol.saman_replica.ReplicaReplyBookRoom;
import comp6231.project.saman.campus.message_protocol.saman_replica.ReplicaReplyMessageStatus;
import comp6231.project.saman.campus.message_protocol.saman_replica.ReplicaRequestAvailableTimeSlots;
import comp6231.project.saman.campus.message_protocol.saman_replica.ReplicaRequestBookRoom;
import comp6231.project.saman.campus.message_protocol.saman_replica.ReplicaRequestCancelBookRoom;
import comp6231.project.saman.campus.message_protocol.saman_replica.ReplicaRequestRemoveStudentRecord;
import comp6231.project.saman.campus.message_protocol.saman_replica.ReplicaRequestStartWeek;

/**
 * @author saman
 *
 */
public class InitializeSerializer {
	
	public static void initializeSamanReplica(MessageHeaderDeserializer ds)
	{
		ds.addClass(CommandType.Book_Room, MessageType.Request, ProtocolType.Server_To_Server, ReplicaRequestBookRoom.class);
		ds.addClass(CommandType.Book_Room, MessageType.Reply, ProtocolType.Server_To_Server, ReplicaReplyBookRoom.class);
		
		ds.addClass(CommandType.Cancel_Book_Room, MessageType.Request, ProtocolType.Server_To_Server, ReplicaRequestCancelBookRoom.class);
		ds.addClass(CommandType.Cancel_Book_Room, MessageType.Reply, ProtocolType.Server_To_Server, ReplicaReplyMessageStatus.class);
		
		ds.addClass(CommandType.Get_Available_TimeSlots, MessageType.Request, ProtocolType.Server_To_Server, ReplicaRequestAvailableTimeSlots.class);
		ds.addClass(CommandType.Get_Available_TimeSlots, MessageType.Reply, ProtocolType.Server_To_Server, ReplicaReplyAvailableTimeSlots.class);
		
		ds.addClass(CommandType.S_Remove_Student_Record, MessageType.Request, ProtocolType.Server_To_Server, ReplicaRequestRemoveStudentRecord.class);
		ds.addClass(CommandType.S_Remove_Student_Record, MessageType.Reply, ProtocolType.Server_To_Server, ReplicaReplyMessageStatus.class);
		
		ds.addClass(CommandType.S_Start_Week, MessageType.Request, ProtocolType.Server_To_Server, ReplicaRequestStartWeek.class);
		ds.addClass(CommandType.S_Start_Week, MessageType.Reply, ProtocolType.Server_To_Server, ReplicaReplyMessageStatus.class);
	}
	
	public static void initializeFaridReplica(MessageHeaderDeserializer ds)
	{
		//TODO
	}
	
	public static void initializeMostafaReplica(MessageHeaderDeserializer ds)
	{
		ds.addClass(CommandType.Book_Room, MessageType.Request, ProtocolType.Server_To_Server, ReplicaRequestBookRoom.class);
		ds.addClass(CommandType.Book_Room, MessageType.Reply, ProtocolType.Server_To_Server, ReplicaReplyBookRoom.class);
		
		ds.addClass(CommandType.Cancel_Book_Room, MessageType.Request, ProtocolType.Server_To_Server, ReplicaRequestCancelBookRoom.class);
		ds.addClass(CommandType.Cancel_Book_Room, MessageType.Reply, ProtocolType.Server_To_Server, ReplicaReplyMessageStatus.class);
		
		ds.addClass(CommandType.Get_Available_TimeSlots, MessageType.Request, ProtocolType.Server_To_Server, ReplicaRequestAvailableTimeSlots.class);
		ds.addClass(CommandType.Get_Available_TimeSlots, MessageType.Reply, ProtocolType.Server_To_Server, ReplicaReplyAvailableTimeSlots.class);
				
		ds.addClass(CommandType.M_Commit, MessageType.Request, ProtocolType.Server_To_Server, ReplicaRequestBookRoom.class);
		ds.addClass(CommandType.M_Commit, MessageType.Reply, ProtocolType.Server_To_Server, ReplicaReplyBookRoom.class);
		
		ds.addClass(CommandType.M_Reduce_Book_Count, MessageType.Request, ProtocolType.Server_To_Server, ReplicaRequestBookRoom.class);
		ds.addClass(CommandType.M_Reduce_Book_Count, MessageType.Reply, ProtocolType.Server_To_Server, ReplicaReplyBookRoom.class);
		
		ds.addClass(CommandType.M_Remove_BookingId, MessageType.Request, ProtocolType.Server_To_Server, ReplicaRequestBookRoom.class);
		ds.addClass(CommandType.M_Remove_BookingId, MessageType.Reply, ProtocolType.Server_To_Server, ReplicaReplyBookRoom.class);
		
		ds.addClass(CommandType.M_Rollback, MessageType.Request, ProtocolType.Server_To_Server, ReplicaRequestBookRoom.class);
		ds.addClass(CommandType.M_Rollback, MessageType.Reply, ProtocolType.Server_To_Server, ReplicaReplyBookRoom.class);
	}

	/**
	 * 
	 */
	public InitializeSerializer() {
		// TODO Auto-generated constructor stub
	}

}
