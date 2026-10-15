package java.io;
public class DataInputStream extends InputStream implements DataInput {
    protected InputStream in;
    public DataInputStream(InputStream in) {}
    public int read() throws IOException { return -1; }
    public int read(byte[] b, int off, int len) throws IOException { return -1; }
    public void readFully(byte[] b) throws IOException {}
    public void readFully(byte[] b, int off, int len) throws IOException {}
    public int skipBytes(int n) throws IOException { return 0; }
    public boolean readBoolean() throws IOException { return false; }
    public byte readByte() throws IOException { return 0; }
    public int readUnsignedByte() throws IOException { return 0; }
    public short readShort() throws IOException { return 0; }
    public int readUnsignedShort() throws IOException { return 0; }
    public char readChar() throws IOException { return 0; }
    public int readInt() throws IOException { return 0; }
    public long readLong() throws IOException { return 0; }
    public float readFloat() throws IOException { return 0; }
    public double readDouble() throws IOException { return 0; }
    public String readUTF() throws IOException { return null; }
    public int available() throws IOException { return 0; }
    public void close() throws IOException {}
}
