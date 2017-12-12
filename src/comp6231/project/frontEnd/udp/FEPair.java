package comp6231.project.frontEnd.udp;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

import comp6231.project.frontEnd.Info;
import comp6231.shared.Constants;

public class FEPair {
	public ConcurrentHashMap<Integer, Info> infos; 
	public Semaphore semaphore;
	public int id;
	public String group;
	public Info [] data;
	public static boolean isOneLock = false;
	public static boolean isTwoLock = false;
	public static boolean isThreeLock = false;
	
	public static final Object lockOne = new Object();
	public static final Object lockTwo = new Object();
	public static final Object lockThree = new Object();

	public FEPair(int id, String group, Info [] data) {
		this.group = group;
		this.id = id;
		this.data = data;
		semaphore = new Semaphore(-Constants.ACTIVE_SERVERS + 1);
		infos = new ConcurrentHashMap<Integer, Info>();   
	}
	
	public static boolean isOneLock() {
		synchronized (lockOne) {
			return isOneLock;
		}
	}
	
	public static void setLockOne(boolean isLock) {
		synchronized (lockOne) {
			FEPair.isOneLock = isLock;
		}
	}
	
	public static boolean isTwoLock() {
		synchronized (lockTwo) {
			return isTwoLock;
		}
	}
	
	public static void setLockTwo(boolean isLock) {
		synchronized (lockTwo) {
			FEPair.isTwoLock = isLock;
		}
	}
	
	public static boolean isThreeLock() {
		synchronized (lockThree) {
			return isThreeLock;
		}
	}
	
	public static void setLockThree(boolean isLock) {
		synchronized (lockThree) {
			FEPair.isThreeLock = isLock;
		}
	}
}
