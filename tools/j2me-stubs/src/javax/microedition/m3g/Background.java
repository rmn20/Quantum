package javax.microedition.m3g;
public class Background extends Node {
    public static final int MODE_BORDER = 64, MODE_REPEAT = 65, MODE_STRETCH = 66;
    public Background() {}
    public void setColor(int argb) {}
    public int getColor() { return 0; }
    public void setImage(Image2D img) {}
    public Image2D getImage() { return null; }
    public void setImageMode(int modeX, int modeY) {}
    public int getImageModeX() { return 0; }
    public int getImageModeY() { return 0; }
    public void setCrop(int x, int y, int w, int h) {}
    public int getCropX() { return 0; }
    public void setColorClearEnable(boolean e) {}
    public boolean isColorClearEnabled() { return false; }
    public void setImageClearEnable(boolean e) {}
    public boolean isImageClearEnabled() { return false; }
}
