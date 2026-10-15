package java.lang;
public final class Long {
    public static final long MAX_VALUE = 9223372036854775807L;
    public static final long MIN_VALUE = -9223372036854775808L;
    private long v;
    public Long(long v) { this.v = v; }
    public long longValue() { return v; }
    public static long parseLong(String s) throws NumberFormatException { return 0; }
    public static String toString(long l) { return null; }
    public static String toString(long l, int radix) { return null; }
    public static String toHexString(long l) { return null; }
    public static int hashCode(long l) { return 0; }
}
