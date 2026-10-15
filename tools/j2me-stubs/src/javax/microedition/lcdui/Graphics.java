package javax.microedition.lcdui;
public final class Graphics {
    public static final int HCENTER = 1, VCENTER = 2, LEFT = 4, RIGHT = 8, TOP = 16, BOTTOM = 32;
    public static final int BASELINE = 64, SOLID = 0, DOTTED = 1;
    public int getRedComponent() { return 0; }
    public int getGreenComponent() { return 0; }
    public int getBlueComponent() { return 0; }
    public int getColor() { return 0; }
    public void setColor(int rgb) {}
    public void setColor(int r, int g, int b) {}
    public void setGrayScale(int v) {}
    public int getGrayScale() { return 0; }
    public Font getFont() { return null; }
    public void setFont(Font f) {}
    public int getStrokeStyle() { return 0; }
    public void setStrokeStyle(int s) {}
    public int getTranslateX() { return 0; }
    public int getTranslateY() { return 0; }
    public void translate(int x, int y) {}
    public void setClip(int x, int y, int w, int h) {}
    public int getClipX() { return 0; }
    public int getClipY() { return 0; }
    public int getClipWidth() { return 0; }
    public int getClipHeight() { return 0; }
    public void clipRect(int x, int y, int w, int h) {}
    public void drawLine(int x1, int y1, int x2, int y2) {}
    public void fillRect(int x, int y, int w, int h) {}
    public void drawRect(int x, int y, int w, int h) {}
    public void fillRoundRect(int x, int y, int w, int h, int aw, int ah) {}
    public void drawRoundRect(int x, int y, int w, int h, int aw, int ah) {}
    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {}
    public void fillArc(int x, int y, int w, int h, int sa, int aa) {}
    public void drawArc(int x, int y, int w, int h, int sa, int aa) {}
    public void drawImage(Image img, int x, int y, int anchor) {}
    public void drawRegion(Image src, int sx, int sy, int sw, int sh, int transform, int dx, int dy, int anchor) {}
    public void copyArea(Image src, int sx, int sy, int sw, int sh, int dx, int dy, int anchor) {}
    public void drawRGB(int[] rgb, int off, int scanlen, int x, int y, int w, int h, boolean pa) {}
    public void drawString(String s, int x, int y, int anchor) {}
    public void drawSubstring(String s, int off, int len, int x, int y, int anchor) {}
    public void drawChar(char c, int x, int y, int anchor) {}
    public void drawChars(char[] c, int off, int len, int x, int y, int anchor) {}
}
