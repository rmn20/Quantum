package javax.microedition.media.control;
import javax.microedition.media.Control;
public interface VideoControl extends Control {
    int USE_NATIVE_VIDEO = 0, USE_DIRECT_VIDEO = 1;
    Object initDisplayMode(int mode, Object canvas);
    void setDisplayLocation(int x, int y);
    void setDisplaySize(int w, int h);
    void setVisible(boolean visible);
    int getSourceWidth();
    int getSourceHeight();
    int getDisplayWidth();
    int getDisplayHeight();
    byte[] getSnapshot(String imageType);
}
