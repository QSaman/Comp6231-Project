package comp6231.project.farid.servers.messages;

import java.time.LocalDate;
import java.time.LocalTime;

import comp6231.project.farid.servers.serverDorval.Student;
import comp6231.project.messageProtocol.IRequest;
import comp6231.project.messageProtocol.MessageHeader;

public class BookRoomMessageFarid extends MessageHeader implements IRequest {
	public String userId;
	public int roomNumber;
	public LocalDate date;
	public LocalTime startTime;
	public LocalTime endTime;

	public BookRoomMessageFarid(int sequence_number, CommandType command_type, MessageType message_type,
			ProtocolType protocol_type, String userId, int roomNumber, LocalDate date, LocalTime startTime,
			LocalTime endTime) {
		super(sequence_number, command_type, message_type, protocol_type);
		this.userId = userId;
		this.roomNumber = roomNumber;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	@Override
	public String handleRequest() {
		Student student = null;
		student = new Student();

		student.setStudentID(userId);
		try {
			String resString = student.bookRoom(roomNumber, date, startTime, endTime);
			student.signOut();
			return resString;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

}
