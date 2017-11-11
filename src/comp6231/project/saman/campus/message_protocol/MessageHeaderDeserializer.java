package comp6231.project.saman.campus.message_protocol;

import java.lang.reflect.Type;
import java.util.EnumMap;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;


class MessageHeaderDeserializer implements JsonDeserializer<MessageHeader>
{
	private EnumMap<MessageHeader.CommandType, EnumMap<MessageHeader.MessageType, EnumMap<MessageHeader.ProtocolType, Class<? extends MessageHeader>>>> mp;
	private Gson gson; 
	
	public MessageHeaderDeserializer() {
		mp = new EnumMap<>(MessageHeader.CommandType.class);
		gson = new Gson();
	}
	
	public MessageHeaderDeserializer addClass(MessageHeader.CommandType cmd, MessageHeader.MessageType type, MessageHeader.ProtocolType protocol, Class<? extends MessageHeader> class_name)
	{
		EnumMap<MessageHeader.ProtocolType, Class<? extends MessageHeader>> prt_type = find(cmd, type, true);
		prt_type.put(protocol, class_name);
		return this;
	}
	
	public void clearClassList()
	{
		mp.clear();
	}
	
	protected EnumMap<MessageHeader.ProtocolType, Class<? extends MessageHeader>> find(MessageHeader.CommandType cmd, MessageHeader.MessageType type, boolean create)
	{
		EnumMap<MessageHeader.MessageType, EnumMap<MessageHeader.ProtocolType, Class<? extends MessageHeader>>> msg_type_map;
		msg_type_map = mp.get(cmd);
		if (msg_type_map == null)
		{
			if (create)
			{
				msg_type_map = new EnumMap<>(MessageHeader.MessageType.class);
				mp.put(cmd, msg_type_map);
			}
			else
				return null;
		}
		EnumMap<MessageHeader.ProtocolType, Class<? extends MessageHeader>> prt_type;
		prt_type = msg_type_map.get(type);
		if (prt_type == null)
		{
			if (create)
			{
				prt_type = new EnumMap<>(MessageHeader.ProtocolType.class);
				msg_type_map.put(type, prt_type);
			}
			else
				return null;
		}
		return prt_type;
	}		
	
	@Override
	public MessageHeader deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
			throws JsonParseException {
		JsonObject obj = arg0.getAsJsonObject();
		MessageHeader header = gson.fromJson(arg0, MessageHeader.class);
//		MessageHeader.CommandType cmd = MessageHeader.CommandType.valueOf(obj.get("command_type").getAsString());
//		MessageHeader.MessageType msg_type = MessageHeader.MessageType.valueOf(obj.get("message_type").getAsString());
//		MessageHeader.ProtocolType prt_type = MessageHeader.ProtocolType.valueOf(obj.get("protocol_type").getAsString());			
		EnumMap<MessageHeader.ProtocolType, Class<? extends MessageHeader>> my_map = find(header.command_type, header.message_type, false);
		Class<? extends MessageHeader> class_name = null;
		if (my_map != null)
		 class_name = my_map.get(header.protocol_type);
		if (class_name != null)
			return gson.fromJson(arg0, class_name);
		return null;
	}
	
}
