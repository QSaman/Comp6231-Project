package comp6231.project.mostafa.serverSide.messages;

import comp6231.project.messageProtocol.IRequest;
import comp6231.project.messageProtocol.MessageHeader;
import comp6231.project.mostafa.serverSide.Database;
import comp6231.project.mostafa.serverSide.Information;

public class BookRoomMessage extends MessageHeader implements IRequest{
	public int roomNumber;
	public String date;
	public String time;
	public String userId;
	
	public BookRoomMessage(int sequenceNumber, CommandType commandType, MessageType messageType, ProtocolType protocolType, String userId, int roomNumber, String date, String time) {
		super(sequenceNumber,commandType, messageType, protocolType);
		this.roomNumber = roomNumber;
		this.date = date;
		this.time = time;
		this.userId = userId;
	}
	
	@Override
	public String handleRequest() {
		String[] timeSplit = time.split("-");
		return Database.getInstance().tryToBookRoom(roomNumber, date, Information.getInstance().convertTimeToSec(timeSplit[0]), Information.getInstance().convertTimeToSec(timeSplit[1]), userId);
	}

}
