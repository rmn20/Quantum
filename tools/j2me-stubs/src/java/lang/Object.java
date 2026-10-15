package java.lang;
public class Object {
    public Object() {}
    public boolean equals(Object o) { return this == o; }
    public int hashCode() { return 0; }
    public String toString() { return ""; }
    public final Class getClass() { return null; }
    public final void notify() {}
    public final void notifyAll() {}
    public final void wait() throws InterruptedException {}
    public final void wait(long t) throws InterruptedException {}
    protected void finalize() throws Throwable {}
    protected Object clone() throws CloneNotSupportedException { return null; }
}
