package javax.microedition.lcdui;
public abstract class Canvas extends Displayable {
    public static final int KEY_NUM0 = 48, KEY_NUM1 = 49, KEY_NUM2 = 50, KEY_NUM3 = 51,
        KEY_NUM4 = 52, KEY_NUM5 = 53, KEY_NUM6 = 54, KEY_NUM7 = 55, KEY_NUM8 = 56,
        KEY_NUM9 = 57, KEY_STAR = 42, KEY_POUND = 35;
    public static final int KEY_UP = -1, KEY_DOWN = -2, KEY_LEFT = -3, KEY_RIGHT = -4,
        KEY_FIRE = -5, KEY_SOFTKEY1 = -6, KEY_SOFTKEY2 = -7, KEY_CLEAR = -8;
    public static final int GAME_UP = 1, GAME_DOWN = 6, GAME_LEFT = 2, GAME_RIGHT = 5,
        GAME_FIRE = 25, GAME_A = 9, GAME_B = 10, GAME_C = 11, GAME_D = 12;
    public int getWidth() { return 0; }
    public int getHeight() { return 0; }
    public boolean isDoubleBuffered() { return false; }
    public boolean hasPointerEvents() { return false; }
    public boolean hasPointerMotionEvents() { return false; }
    public boolean hasRepeatEvents() { return false; }
    public int getKeyCode(int action) { return 0; }
    public String getKeyName(int code) { return null; }
    public int getGameAction(int code) { return 0; }
    public void setFullScreenMode(boolean m) {}
    public void repaint() {}
    public void repaint(int x, int y, int w, int h) {}
    public void serviceRepaints() {}
    protected abstract void paint(Graphics g);
    protected void keyPressed(int code) {}
    protected void keyReleased(int code) {}
    protected void keyRepeated(int code) {}
    protected void pointerPressed(int x, int y) {}
    protected void pointerReleased(int x, int y) {}
    protected void pointerDragged(int x, int y) {}
    protected void showNotify() {}
    protected void hideNotify() {}
    protected void sizeChanged(int w, int h) {}
}
