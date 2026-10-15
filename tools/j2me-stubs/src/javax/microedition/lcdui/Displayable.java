package javax.microedition.lcdui;
public abstract class Displayable {
    public int getWidth() { return 0; }
    public int getHeight() { return 0; }
    public String getTitle() { return null; }
    public void setTitle(String t) {}
    public boolean isShown() { return false; }
    public void addCommand(Command c) {}
    public void removeCommand(Command c) {}
    public void setCommandListener(CommandListener l) {}
    public Ticker getTicker() { return null; }
    public void setTicker(Ticker t) {}
}
