package java.lang;
public class Thread implements Runnable {
    public static final int MIN_PRIORITY = 1, NORM_PRIORITY = 5, MAX_PRIORITY = 10;
    public Thread() {}
    public Thread(Runnable r) {}
    public Thread(Runnable r, String name) {}
    public void start() {}
    public void run() {}
    public void setPriority(int p) {}
    public int getPriority() { return 0; }
    public void setName(String n) {}
    public String getName() { return null; }
    public boolean isAlive() { return false; }
    public final void join() throws InterruptedException {}
    public final void join(long ms) throws InterruptedException {}
    public static void sleep(long ms) throws InterruptedException {}
    public static Thread currentThread() { return null; }
    public static void yield() {}
}
