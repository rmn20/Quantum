package javax.microedition.lcdui;
public class Alert extends Screen {
    public static final int FOREVER = -2;
    public Alert(String title) {}
    public Alert(String title, String text, Image img, AlertType t) {}
    public void setString(String s) {}
    public String getString() { return null; }
    public void setTimeout(int t) {}
    public int getTimeout() { return 0; }
    public void setType(AlertType t) {}
    public void setImage(Image i) {}
}
