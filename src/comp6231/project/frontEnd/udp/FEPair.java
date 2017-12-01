package comp6231.project.frontEnd.udp;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import comp6231.shared.Constants;

public class FEPair {
	public ConcurrentHashMap<Integer, ArrayList<String>> entry;
    public Semaphore semaphore;
    public int method;

    public FEPair(int seqNumber, int method) {
        semaphore = new Semaphore(-Constants.ACTIVE_SERVERS + 1);
        this.method = method;
        entry = new ConcurrentHashMap<>();
        ArrayList<String> msg = new ArrayList<>();
        entry.put(seqNumber , msg);
    }

}
