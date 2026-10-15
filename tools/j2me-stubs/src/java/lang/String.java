package java.lang;
public final class String {
    public String() {}
    public String(String s) {}
    public String(char[] c) {}
    public String(char[] c, int off, int len) {}
    public String(byte[] b) {}
    public String(byte[] b, int off, int len) {}
    public String(byte[] b, String enc) throws java.io.UnsupportedEncodingException {}
    public String(byte[] b, int off, int len, String enc) throws java.io.UnsupportedEncodingException {}
    public int length() { return 0; }
    public char charAt(int i) { return 0; }
    public void getChars(int a, int b, char[] dst, int off) {}
    public byte[] getBytes() { return null; }
    public byte[] getBytes(String enc) throws java.io.UnsupportedEncodingException { return null; }
    public char[] toCharArray() { return null; }
    public boolean equals(Object o) { return false; }
    public boolean equalsIgnoreCase(String s) { return false; }
    public int compareTo(String s) { return 0; }
    public String substring(int a) { return null; }
    public String substring(int a, int b) { return null; }
    public String concat(String s) { return null; }
    public String replace(char a, char b) { return null; }
    public String toLowerCase() { return null; }
    public String toUpperCase() { return null; }
    public String trim() { return null; }
    public int indexOf(int c) { return 0; }
    public int indexOf(int c, int from) { return 0; }
    public int indexOf(String s) { return 0; }
    public int indexOf(String s, int from) { return 0; }
    public int lastIndexOf(int c) { return 0; }
    public int lastIndexOf(String s) { return 0; }
    public boolean startsWith(String s) { return false; }
    public boolean startsWith(String s, int off) { return false; }
    public boolean endsWith(String s) { return false; }
    public String intern() { return null; }
    public static String valueOf(int i) { return null; }
    public static String valueOf(long l) { return null; }
    public static String valueOf(char c) { return null; }
    public static String valueOf(boolean b) { return null; }
    public static String valueOf(float f) { return null; }
    public static String valueOf(double d) { return null; }
    public static String valueOf(char[] c) { return null; }
    public static String valueOf(Object o) { return null; }
    public String toString() { return null; }
    public int hashCode() { return 0; }
}
