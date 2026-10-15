package java.lang;
public final class Short {
    public static final short MAX_VALUE = 32767;
    public static final short MIN_VALUE = -32768;
    private short v;
    public Short(short v) { this.v = v; }
    public short shortValue() { return v; }
    public static short parseShort(String s) throws NumberFormatException { return 0; }
}
