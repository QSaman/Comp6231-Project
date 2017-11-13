package comp6231.project.farid.servers.serverKirkland;

public class Locker {
    static final Object databaseLock = new Object();
    static final Object studentsLock = new Object();
    static final Object adminsLock = new Object();
    static final Object counterLock = new Object();
    static final Object reserveLock = new Object();
}
