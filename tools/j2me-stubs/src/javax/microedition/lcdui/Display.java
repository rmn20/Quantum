package javax.microedition.lcdui;
import javax.microedition.midlet.MIDlet;
public final class Display {
    public static Display getDisplay(MIDlet m) { return null; }
    public Displayable getCurrent() { return null; }
    public void setCurrent(Displayable d) {}
    public void setCurrent(Alert a, Displayable d) {}
    public void setCurrentItem(Item i) {}
    public boolean isColor() { return false; }
    public int numColors() { return 0; }
    public int colorLevels() { return 0; }
    public void callSerially(Runnable r) {}
    public boolean flashBacklight(int d) { return false; }
    public boolean vibrate(int d) { return false; }
}
