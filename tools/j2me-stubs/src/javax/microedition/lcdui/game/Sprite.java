package javax.microedition.lcdui.game;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
public class Sprite extends Layer {
    public static final int TRANS_NONE = 0, TRANS_MIRROR_ROT180 = 1, TRANS_MIRROR = 2,
        TRANS_ROT180 = 3, TRANS_MIRROR_ROT270 = 4, TRANS_ROT90 = 5, TRANS_ROT270 = 6,
        TRANS_MIRROR_ROT90 = 7;
    public Sprite(Image img) {}
    public Sprite(Image img, int frameW, int frameH) {}
    public Sprite(Sprite s) {}
    public void setFrameSequence(int[] seq) {}
    public void nextFrame() {}
    public void prevFrame() {}
    public int getFrame() { return 0; }
    public int getRawFrameCount() { return 0; }
    public void setFrameImage(Image img) {}
    public void defineReferencePixel(int x, int y) {}
    public void setRefPixelPosition(int x, int y) {}
    public int getRefPixelX() { return 0; }
    public int getRefPixelY() { return 0; }
    public void setTransform(int t) {}
    public void paint(Graphics g) {}
    public boolean collidesWith(Sprite s, boolean pixelLevel) { return false; }
    public boolean collidesWith(Image img, int x, int y, boolean pixelLevel) { return false; }
    public Image getImage() { return null; }
}
