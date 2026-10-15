package javax.microedition.lcdui;
public final class Font {
    public static final int STYLE_PLAIN = 0, STYLE_BOLD = 1, STYLE_ITALIC = 2, STYLE_UNDERLINED = 4;
    public static final int SIZE_SMALL = 8, SIZE_MEDIUM = 0, SIZE_LARGE = 16;
    public static final int FACE_SYSTEM = 0, FACE_MONOSPACE = 32, FACE_PROPORTIONAL = 64;
    public static Font getFont(int face, int style, int size) { return null; }
    public static Font getDefaultFont() { return null; }
    public int getHeight() { return 0; }
    public int stringWidth(String s) { return 0; }
    public int charWidth(char c) { return 0; }
    public int charsWidth(char[] c, int off, int len) { return 0; }
    public int substringWidth(String s, int off, int len) { return 0; }
    public boolean isBold() { return false; }
    public int getStyle() { return 0; }
    public int getFace() { return 0; }
    public int getSize() { return 0; }
    public int getBaselinePosition() { return 0; }
}
