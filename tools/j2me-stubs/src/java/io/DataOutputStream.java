package java.io;
public class DataOutputStream extends OutputStream implements DataOutput {
    protected OutputStream out;
    protected int written;
    public DataOutputStream(OutputStream out) {}
    public void write(int b) throws IOException {}
    public void write(byte[] b, int off, int len) throws IOException {}
    public void writeBoolean(boolean v) throws IOException {}
    public void writeByte(int v) throws IOException {}
    public void writeShort(int v) throws IOException {}
    public void writeChar(int v) throws IOException {}
    public void writeInt(int v) throws IOException {}
    public void writeLong(long v) throws IOException {}
    public void writeFloat(float v) throws IOException {}
    public void writeDouble(double v) throws IOException {}
    public void writeUTF(String s) throws IOException {}
    public void flush() throws IOException {}
    public int size() { return 0; }
    public void close() throws IOException {}
}
