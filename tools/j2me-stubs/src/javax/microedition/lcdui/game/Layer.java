package javax.microedition.lcdui.game;
import javax.microedition.lcdui.Graphics;
public abstract class Layer {
    public void setPosition(int x, int y) {}
    public void move(int dx, int dy) {}
    public int getX() { return 0; }
    public int getY() { return 0; }
    public int getWidth() { return 0; }
    public int getHeight() { return 0; }
    public void setVisible(boolean v) {}
    public boolean isVisible() { return false; }
    public abstract void paint(Graphics g);
}
