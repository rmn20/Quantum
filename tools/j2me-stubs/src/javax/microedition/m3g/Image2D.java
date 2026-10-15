package javax.microedition.m3g;
public class Image2D extends Object3D {
    public static final int ALPHA = 96, LUMINANCE = 97, LUMINANCE_ALPHA = 98, RGB = 99, RGBA = 100;
    public Image2D(int format, Object image) {}
    public Image2D(int format, int width, int height) {}
    public Image2D(int format, int width, int height, byte[] palette, byte[] pixels) {}
    public Image2D(int format, int width, int height, byte[] image) {}
    public void set(int x, int y, int width, int height, byte[] image) {}
    public void set(int x, int y, int width, int height, int[] image) {}
    public boolean isMutable() { return false; }
    public int getFormat() { return 0; }
    public int getWidth() { return 0; }
    public int getHeight() { return 0; }
}
