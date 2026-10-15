package java.io;
public abstract class InputStream {
    public abstract int read() throws IOException;
    public int read(byte[] b) throws IOException { return -1; }
    public int read(byte[] b, int off, int len) throws IOException { return -1; }
    public long skip(long n) throws IOException { return 0; }
    public int available() throws IOException { return 0; }
    public void close() throws IOException {}
    public void mark(int l) {}
    public void reset() throws IOException {}
    public boolean markSupported() { return false; }
}
