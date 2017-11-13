package comp6231.project.messageProtocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StartGson {
	public static Gson initGsonMostafa()
	{
		MessageHeaderDeserializer ds = new MessageHeaderDeserializer();		
		InitializeSerializer.initializeMostafaReplica(ds);
		return new GsonBuilder().registerTypeAdapter(MessageHeader.class, ds).create();
	}
	
	public static Gson initGsonFarid()
	{
		MessageHeaderDeserializer ds = new MessageHeaderDeserializer();		
		InitializeSerializer.initializeFaridReplica(ds);
		return new GsonBuilder().registerTypeAdapter(MessageHeader.class, ds).create();
	}
}
