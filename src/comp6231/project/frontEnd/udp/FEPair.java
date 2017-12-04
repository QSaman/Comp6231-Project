package comp6231.project.frontEnd.udp;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import comp6231.shared.Constants;

public class FEPair {
	public ConcurrentHashMap<Integer, Info> infos; 
    public Semaphore semaphore;
    public int index ;
    public int id;
    public String group;
    
    public FEPair(int id, String group) {
    	this.group = group;
    	this.id = id;
    	index = 0;
        semaphore = new Semaphore(-Constants.ACTIVE_SERVERS + 1);
        infos = new ConcurrentHashMap<Integer, Info>();   
    }

    class Info {
    	public String json;
    	public int port;
    	
    	public Info(String json , int port) {
    		this.json = json;
    		this.port = port;
		}
    }
}
