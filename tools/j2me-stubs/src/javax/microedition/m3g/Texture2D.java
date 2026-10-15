package javax.microedition.m3g;
public class Texture2D extends Transformable {
    public static final int BLEND_MODULATE = 48, BLEND_REPLACE = 49;
    public static final int FILTER_BASE_LEVEL = 32, FILTER_LINEAR = 33, FILTER_NEAREST = 34;
    public static final int FUNC_ADD = 64, FUNC_BLEND = 65, FUNC_DECAL = 66, FUNC_MODULATE = 67, FUNC_REPLACE = 68;
    public static final int WRAP_CLAMP = 80, WRAP_REPEAT = 81;
    public static final int LEVELS = 2;
    public Texture2D(Image2D image) {}
    public void setImage(Image2D image) {}
    public void setImage(Image2D image, int level) {}
    public Image2D getImage(int level) { return null; }
    public void setBlending(int func) {}
    public void setBlending(int func, int color) {}
    public int getBlendMode() { return 0; }
    public int getBlendColor() { return 0; }
    public void setFiltering(int levelFilter, int imageFilter) {}
    public int getLevelFilter() { return 0; }
    public int getImageFilter() { return 0; }
    public void setWrapping(int modeS, int modeT) {}
    public int getWrappingS() { return 0; }
    public int getWrappingT() { return 0; }
    public void setLevelCount(int levels) {}
    public int getLevelCount() { return 0; }
}
