package java.lang;
public final class Byte {
    public static final byte MAX_VALUE = 127;
    public static final byte MIN_VALUE = -128;
    private byte v;
    public Byte(byte v) { this.v = v; }
    public byte byteValue() { return v; }
    public static byte parseByte(String s) throws NumberFormatException { return 0; }
    public static String toString(byte b) { return null; }
}
