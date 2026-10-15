package javax.microedition.lcdui;
public class TextField extends Item {
    public static final int ANY = 0, NUMERIC = 2, PASSWORD = 0x10000;
    public TextField(String label, String text, int maxSize, int constraints) {}
    public void setString(String s) {}
    public String getString() { return null; }
    public int size() { return 0; }
    public int getMaxSize() { return 0; }
}
