package java.io;
public class ByteArrayOutputStream extends OutputStream {
    protected byte[] buf;
    protected int count;
    public ByteArrayOutputStream() {}
    public ByteArrayOutputStream(int size) {}
    public void write(int b) {}
    public void write(byte[] b, int off, int len) {}
    public void reset() {}
    public byte[] toByteArray() { return null; }
    public int size() { return 0; }
    public String toString() { return null; }
    public void close() {}
}
