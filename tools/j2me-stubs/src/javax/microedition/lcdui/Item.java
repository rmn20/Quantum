package javax.microedition.lcdui;
public abstract class Item {
    public static final int LAYOUT_DEFAULT = 0, LAYOUT_LEFT = 1, LAYOUT_RIGHT = 2, LAYOUT_CENTER = 3;
    public String getLabel() { return null; }
    public void setLabel(String l) {}
    public void setLayout(int l) {}
    public int getLayout() { return 0; }
    public void setPreferredWidth(int w) {}
    public void setPreferredHeight(int h) {}
}
