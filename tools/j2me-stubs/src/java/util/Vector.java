package java.util;
public class Vector {
    public Vector() {}
    public Vector(int cap) {}
    public Vector(int cap, int inc) {}
    public synchronized void addElement(Object o) {}
    public synchronized void insertElementAt(Object o, int i) {}
    public synchronized void setElementAt(Object o, int i) {}
    public synchronized void removeElementAt(int i) {}
    public synchronized boolean removeElement(Object o) { return false; }
    public synchronized void removeAllElements() {}
    public synchronized Object elementAt(int i) { return null; }
    public synchronized Object firstElement() { return null; }
    public synchronized Object lastElement() { return null; }
    public synchronized int size() { return 0; }
    public synchronized boolean isEmpty() { return true; }
    public boolean contains(Object o) { return false; }
    public int indexOf(Object o) { return -1; }
    public int indexOf(Object o, int from) { return -1; }
    public synchronized void copyInto(Object[] a) {}
    public synchronized Enumeration elements() { return null; }
    public synchronized void setSize(int n) {}
    public synchronized int capacity() { return 0; }
    public synchronized void trimToSize() {}
    public String toString() { return null; }
}
