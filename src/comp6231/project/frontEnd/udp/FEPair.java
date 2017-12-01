package comp6231.project.frontEnd.udp;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import comp6231.shared.Constants;

public class FEPair {
	public ConcurrentHashMap<Integer, String> msgs; 
    public Semaphore semaphore;
    public int index ;

    public FEPair() {
    	index = 0;
        semaphore = new Semaphore(-Constants.ACTIVE_SERVERS + 1);
        msgs = new ConcurrentHashMap<Integer, String>();   
    }

}
