package comp6231.project.farid.servers.serverWestmount;

public class Locker {
    static final Object databaseLock = new Object();
    static final Object studentsLock = new Object();
    static final Object adminsLock = new Object();
    static final Object counterLock = new Object();
    static final Object reserveLock = new Object();
}
