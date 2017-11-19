/**
 * 
 */
package comp6231.project.messageProtocol;

import comp6231.project.frontEnd.messages.FEBookRoomRequestMessage;
import comp6231.project.frontEnd.messages.FECancelBookingMessage;
import comp6231.project.frontEnd.messages.FEChangeReservationMessage;
import comp6231.project.frontEnd.messages.FECreateRoomRequestMessage;
import comp6231.project.frontEnd.messages.FEDeleteRoomRequestMessage;
import comp6231.project.frontEnd.messages.FEGetAvailableTimeSlotMessage;
import comp6231.project.frontEnd.messages.FEReplyMessage;
import comp6231.project.messageProtocol.MessageHeader.CommandType;
import comp6231.project.messageProtocol.MessageHeader.MessageType;
import comp6231.project.messageProtocol.MessageHeader.ProtocolType;
import comp6231.project.messageProtocol.sharedMessage.ServerToServerMessage;
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
		ds.addClass(CommandType.Quantity, MessageType.Quantity, ProtocolType.Server_To_Server, ServerToServerMessage.class);
	}
	
	public static void initializeMostafaReplica(MessageHeaderDeserializer ds)
	{
		ds.addClass(CommandType.Quantity, MessageType.Quantity, ProtocolType.Server_To_Server, ServerToServerMessage.class);
	}
	
	public static void initializeFrontendTOReplica(MessageHeaderDeserializer ds)
	{
		ds.addClass(CommandType.Book_Room, MessageType.Request, ProtocolType.Frontend_To_Replica, FEBookRoomRequestMessage.class);
		ds.addClass(CommandType.Book_Room, MessageType.Reply, ProtocolType.Frontend_To_Replica, FEReplyMessage.class);
		
		ds.addClass(CommandType.Cancel_Book_Room, MessageType.Request, ProtocolType.Frontend_To_Replica, FECancelBookingMessage.class);
		ds.addClass(CommandType.Cancel_Book_Room, MessageType.Reply, ProtocolType.Frontend_To_Replica, FEReplyMessage.class);
		
		ds.addClass(CommandType.Get_Available_TimeSlots, MessageType.Request, ProtocolType.Frontend_To_Replica, FEGetAvailableTimeSlotMessage.class);
		ds.addClass(CommandType.Get_Available_TimeSlots, MessageType.Reply, ProtocolType.Frontend_To_Replica, FEReplyMessage.class);
		
		ds.addClass(CommandType.Change_Reservation, MessageType.Request, ProtocolType.Frontend_To_Replica, FEChangeReservationMessage.class);
		ds.addClass(CommandType.Change_Reservation, MessageType.Reply, ProtocolType.Frontend_To_Replica, FEReplyMessage.class);
		
		ds.addClass(CommandType.Create_Room, MessageType.Request, ProtocolType.Frontend_To_Replica, FECreateRoomRequestMessage.class);
		ds.addClass(CommandType.Create_Room, MessageType.Reply, ProtocolType.Frontend_To_Replica, FEReplyMessage.class);
		
		ds.addClass(CommandType.Delete_Room, MessageType.Request, ProtocolType.Frontend_To_Replica, FEDeleteRoomRequestMessage.class);
		ds.addClass(CommandType.Delete_Room, MessageType.Reply, ProtocolType.Frontend_To_Replica, FEReplyMessage.class);
		
		ds.addClass(CommandType.Login, MessageType.Request, ProtocolType.Frontend_To_Replica, ServerToServerMessage.class);
		ds.addClass(CommandType.Login, MessageType.Reply, ProtocolType.Frontend_To_Replica, FEReplyMessage.class);
		
		ds.addClass(CommandType.SignOut, MessageType.Request, ProtocolType.Frontend_To_Replica, ServerToServerMessage.class);
		ds.addClass(CommandType.SignOut, MessageType.Reply, ProtocolType.Frontend_To_Replica, FEReplyMessage.class);
	}
	
	public static void initializeAll(MessageHeaderDeserializer ds)
	{
		initializeSamanReplica(ds);
		initializeMostafaReplica(ds);
		initializeFaridReplica(ds);
		initializeFrontendTOReplica(ds);
	}
	/**
	 * 
	 */
	public InitializeSerializer() {
		// TODO Auto-generated constructor stub
	}

}
