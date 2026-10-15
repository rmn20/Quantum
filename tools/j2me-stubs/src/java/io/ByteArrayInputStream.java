package java.io;
public class ByteArrayInputStream extends InputStream {
    protected byte[] buf;
    protected int pos, count;
    public ByteArrayInputStream(byte[] b) {}
    public ByteArrayInputStream(byte[] b, int off, int len) {}
    public int read() throws IOException { return -1; }
    public int read(byte[] b, int off, int len) { return -1; }
    public long skip(long n) { return 0; }
    public int available() { return 0; }
    public void close() {}
}
