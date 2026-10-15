package java.lang;
public final class Integer {
    public static final int MAX_VALUE = 2147483647;
    public static final int MIN_VALUE = -2147483648;
    private int v;
    public Integer(int v) { this.v = v; }
    public int intValue() { return v; }
    public String toString() { return null; }
    public static int parseInt(String s) throws NumberFormatException { return 0; }
    public static int parseInt(String s, int radix) throws NumberFormatException { return 0; }
    public static String toString(int i) { return null; }
    public static String toString(int i, int radix) { return null; }
    public static Integer valueOf(String s) { return null; }
    public static String toHexString(int i) { return null; }
    public static int hashCode(int i) { return 0; }
}
