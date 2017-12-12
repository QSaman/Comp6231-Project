package comp6231.project.frontEnd.udp;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import comp6231.project.frontEnd.Info;
import comp6231.shared.Constants;

public class FEPair {
	public ConcurrentHashMap<Integer, Info> infos; 
	public Semaphore semaphore;
	private int index ;
	public int id;
	public String group;
	private static boolean isWait = false;
	public static final Object wait = new Object();
	public static final Object monitor = new Object();

	public FEPair(int id, String group) {
		this.group = group;
		this.id = id;
		index = 0;
		semaphore = new Semaphore(-Constants.ACTIVE_SERVERS + 1);
		infos = new ConcurrentHashMap<Integer, Info>();   
	}

	public synchronized void setIndex(int index){
		this.index = index;
	}

	public synchronized int getIndex(){
		return index;
	}

	public synchronized int adjustIndex(){
		int localIndex = index;
		index ++;
		return localIndex;
	}
	
	public static boolean isWait() {
		synchronized (wait) {
			return isWait;
		}
	}
	
	public static void setWait(boolean isWait) {
		synchronized (wait) {
			FEPair.isWait = isWait;
		}
	}
}
