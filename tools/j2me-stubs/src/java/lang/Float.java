package java.lang;
public final class Float {
    public static final float NaN = 0.0f / 0.0f;
    public static final float MAX_VALUE = 3.4028235e38f;
    public static final float MIN_VALUE = 1.4e-45f;
    private float v;
    public Float(float v) { this.v = v; }
    public float floatValue() { return v; }
    public static float parseFloat(String s) throws NumberFormatException { return 0; }
    public static boolean isNaN(float f) { return false; }
    public static String toString(float f) { return null; }
    public static int floatToIntBits(float f) { return 0; }
    public static int floatToRawIntBits(float f) { return 0; }
    public static float intBitsToFloat(int i) { return 0; }
}
