package code.HUD;

import code.HUD.Base.Font;
import code.HUD.Base.GameKeyboard;
import code.utils.canvas.MainCanvas;
import code.utils.canvas.MyCanvas;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

public abstract class GUIScreen extends MyCanvas {

    private String leftSoft; // название (подпись) левой софт-клавиши
    private String rightSoft; // название правой софт-клавиши
    public boolean pressUp = false;
    public boolean pressDown = false;
    private Font font;
    public final GameKeyboard keys = new GameKeyboard();
    private int x = 0; // x точки нажатия на экран
    private int y = 0; // y точки нажатия на экран
    public boolean inverseHScroll = false;
    public boolean inverseVScroll = false;
    public boolean dragIgnore = false;

    public GUIScreen() {
    }

    public void destroy() {
        this.leftSoft = this.rightSoft = null;
        this.font = null;
    }

    protected final void set(Font font, String leftSoft, String rightSoft) {
        this.font = font;
        this.setSoftKeysNames(leftSoft, rightSoft);
    }

    protected final void setFont(Font font) {
        this.font = font;
    }

    protected final Font getFont() {
        return this.font;
    }

    // задает названия левой и правой софт клавиш
    protected final void setSoftKeysNames(String leftSoft, String rightSoft) {
        this.leftSoft = leftSoft;
        this.rightSoft = rightSoft;
    }

    protected final void setLeftSoft(String name) {
        this.leftSoft = name;
    }

    protected final void drawSoftKeys(Graphics g) {
        int var2 = this.getWidth();
        int var3 = this.getHeight();
        if(this.font == null) {
            System.out.println("GUIScreen: ERROR: font == null");
        } else {
            this.font.setY(0);
            if(this.leftSoft != null) {
                this.font.drawString(g, this.leftSoft, 2, var3, 36);
            }

            if(this.rightSoft != null) {
                this.font.drawString(g, this.rightSoft, var2 - 2, var3, 40);
            }

        }
    }

    protected void keyPressed(int key) {

        if(key == keys.SOFT_LEFT) onLeftSoftKey();
        else if(key == keys.SOFT_RIGHT) onRightSoftKey();

        else if(key == Canvas.KEY_NUM7) onKey7();
        else if(key == Canvas.KEY_NUM9) onKey9();
        else if(key == 53 || key == keys.FIRE) onKey5();
        else if(key == 52 || key == keys.LEFT) onKey4();
        else if(key == 54 || key == keys.RIGHT) onKey6();

        else if(key == 50 || key == keys.UP) {
            onKey2();
            pressUp = true;
        } else if(key == 56 || key == keys.DOWN) {
            onKey8();
            pressDown = true;
        }

    }

    protected void keyRepeated(int key) {

        if(key == 52 || key == keys.LEFT) onKeyRepeated4();
        else if(key == 54 || key == keys.RIGHT) onKeyRepeated6();
        else if(key == 50 || key == keys.UP) onKeyRepeated2();
        else if(key == 56 || key == keys.DOWN) onKeyRepeated8();

    }

    protected void keyReleased(int key) {

        if(key == 50 || key == keys.UP) {
            pressUp = false;
        } else if(key == 56 || key == keys.DOWN) {
            pressDown = false;
        }
    }

    // Действие при нажатии левой софт-клавиши
    protected void onLeftSoftKey() {
    }

    // Действие при нажатии правой софт-клавиши
    protected void onRightSoftKey() {
    }

    // Действие при нажатии 5
    protected void onKey5() {
    }

    // при нажатии 4
    protected void onKey4() {
    }

    // при нажатии 6
    protected void onKey6() {
    }

    // при нажатии 2
    protected void onKey2() {
    }

    // при нажатии 8
    protected void onKey8() {
    }

    // при нажатии 7
    protected void onKey7() {
    }

    // при нажатии 9
    protected void onKey9() {
    }

    // при удержании 4
    protected void onKeyRepeated4() {
    }

    // при удержании 6
    protected void onKeyRepeated6() {
    }

    // при удержании 2
    protected void onKeyRepeated2() {
    }

    // при удержании 8
    protected void onKeyRepeated8() {
    }

    protected void pointerPressed(int x, int y) {
        if(MainCanvas.pstros) {
            this.onLeftSoftKey();
            return;
        }
        super.pointerPressed(x, y);
        this.x = x;
        this.y = y;
        if(isLeftSoft(x, y, this.getWidth(), this.getHeight())) {
            this.keyPressed(this.keys.SOFT_LEFT);
        }

        if(isRightSoft(x, y, this.getWidth(), this.getHeight())) {
            this.keyPressed(this.keys.SOFT_RIGHT);
        }

    }

    protected void pointerDragged(int x, int y) {
        if(dragIgnore) return;
        int var3 = getWidth();
        int var4 = getHeight();
        if(Math.abs(this.x - x) > var3 / 7) {
            if(this.x < x) {
                if(!inverseHScroll) onKey6();
                if(inverseHScroll) onKey4();
            }

            if(this.x > x) {
                if(!inverseHScroll) onKey4();
                if(inverseHScroll) onKey6();
            }

            this.x = x;
        }

        if(Math.abs(this.y - y) > var4 / 7) {
            if(this.y > y) {
                if(!inverseVScroll) onKey2();
                else onKey8();
            }

            if(this.y < y) {
                if(!inverseVScroll) onKey8();
                else onKey2();
            }

            this.y = y;
        }

    }

    protected void pointerReleased(int x, int y) {
        super.pointerReleased(x, y);
    }

    public static boolean isLeftSoft(int x, int y, int w, int h) {
        int var4 = h / 10;
        return inArea(x, y, 0, h - var4, w / 2 - w / 6, h);
    }

    public static boolean isRightSoft(int x, int y, int w, int h) {
        int var4 = h / 10;
        return inArea(x, y, w / 2 + w / 6, h - var4, w, h);
    }

    private static boolean inArea(int x, int y, int x1, int y1, int x2, int y2) {
        return x >= x1 && x <= x2 && y >= y1 && y <= y2;
    }
}
