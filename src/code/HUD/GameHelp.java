package code.HUD;

import code.AI.Player;
import code.Gameplay.Map.RoomObject;
import code.HUD.Base.TextView;
import code.utils.ImageResize;
import code.utils.Main;
import code.utils.canvas.MyCanvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class GameHelp extends GUIScreen {

    private int y0; // y точки из которой перетаскивается текст
    private Main main;
    private Menu menu;
    private TextView text;
    int x, y;
    private int levelNumber;
    private Object hudInfo;
    private int stateCode = 1;
    private boolean preview = false;
    private boolean levelEnd;
    private Image background;
            
    public static boolean loadSave = false;
    public static boolean loadpos = true;
    private static short toMove = 0;

    public GameHelp(Main main, Menu menu, int levelNumberz, boolean levelEnd, Object hudInfo) {
        init(main, menu, levelNumberz, levelEnd, hudInfo, 1);
    }

    public GameHelp(Main main, Menu menu, int levelNumberz, boolean levelEnd, Object hudInfo, int helpState) {
        init(main, menu, levelNumberz, levelEnd, hudInfo, helpState);
    }

    public void init(Main main, Menu menu, int levelNumberz, boolean levelEnd, Object hudInfo, int code) {
        this.main = main;
        this.menu = menu;
        loadSave = false;
        loadpos = true;
        this.levelNumber = levelNumberz;
        this.hudInfo = hudInfo;
        this.stateCode = code;
        this.levelEnd = levelEnd;
        this.setFont(Main.getFont());
        this.setLeftSoft(Main.getGameText().get("NEXT"));
        
        String prefix = levelEnd?"LEVEL_END_":"GAME_HELP_";
        
        String add = "";
        if(stateCode > 1) add = "_" + Integer.toString(stateCode);
        Player.toAddOnStart.addElement(prefix+"SCREEN_" + Integer.toString(levelNumber) + add);
                
        x = this.getWidth() / 15;
        y = Main.getFont().height();
        toMove = 0;
        
        String txt;
        if(levelNumberz != 1) {
            txt = Main.getGameText().get(prefix+"TEXT_" + Integer.toString(levelNumberz) + add);
        } else {
            txt = Main.getGameText().get(prefix+"TEXT");
            if(txt == null) txt = Main.getGameText().get(prefix+"TEXT_1" + add);
        }
        if(txt == null) txt = "";

        String back1 = Main.getGameText().get(prefix+"PREVIEW_" + Integer.toString(levelNumberz) + add);
        String back2 = Main.getGameText().get(prefix+"BACKGROUND_" + Integer.toString(levelNumberz) + add);

        int textHeight = getHeight() - y * 2;
        text = new TextView(txt, getWidth() - x * 2, textHeight, Main.getFont());
        text.setCenter(true);

        try {
            if(back1 != null) {
                int min = Math.min(getWidth(), getHeight());
                background = ImageResize.createImage(back1, min / 240f, min / 240f);
                int offset = background.getHeight() + Main.getFont().height() * 2;
                y = offset + Main.getFont().height();
                text.setHeight(getHeight() - offset - Main.getFont().height() * 2);
                preview = true;
            } else if(back2 != null) {
                background = ImageResize.createImage(back2, getWidth(), getHeight());
            } else {
                if(menu != null && menu.background != null) background = menu.background;
                if(menu == null && background == null) background = ImageResize.createImage(Main.background, getWidth(), getHeight());
            }
        } catch (Exception err) {
            err.printStackTrace();
        }
        
        if(text.getTextHeight() < text.getHeight() && !preview) {
            text.setY(Math.max(0, (text.getHeight() - text.getTextHeight()) / 2));
        }
    }

    protected final void paint(Graphics g) {
        g.setColor(0);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        if(pressUp && toMove == 0 && !pointerPressed) toMove += Main.getFont().height();
        if(pressDown && toMove == 0 && !pointerPressed) toMove -= Main.getFont().height();
        if(toMove != 0 && !pointerPressed && text.getTextHeight() > text.getHeight()) {
            this.text.move(Math.max(Math.min(3, toMove), -3));
            toMove -= Math.max(Math.min(3, toMove), -3);
        }
        
        if(background != null && preview) {
            g.setColor(0xffffff);
            int posX = (getWidth() - background.getWidth()) / 2;
            g.drawImage(background, posX, (y - background.getHeight()) / 2, 0);
        } else if(background != null) {
            g.setColor(0xffffff);
            g.drawImage(background, (getWidth() - background.getWidth()) / 2, (getHeight() - background.getHeight()) / 2, 0);
        }

        text.paint(g, x, y);
        g.translate(0, 0);
        g.setClip(0, 0, getWidth(), getHeight());
        drawSoftKeys(g);

        if(!pointerPressed) try {
            Thread.sleep(22L);
            repaint();
        } catch (Exception exc) {
        }
    }

    protected final void onLeftSoftKey() {
        this.onKey5();
    }

    protected final void onKey5() {
        if(levelEnd) {
            
            if(needToShow(levelNumber, stateCode+1, true)) {
                init(main, menu, levelNumber, true, hudInfo, stateCode+1);
            } else {
                this.background = null;
                this.destroy();
                
                if(Main.isLastLevel(levelNumber)) {
                    MyCanvas scr;

                    if(TitleScreen.hasTitleScreen()) scr = new TitleScreen(main, null);
                    else scr = new Menu(main);

                    Main.setCurrent(scr);
                } else {
                    Main.loadLevel(loadSave, loadpos, levelNumber + 1, hudInfo, main, menu, 1);
                }
            }
            
        } else {
            this.background = null;
            this.destroy();
            Main.loadLevel(loadSave, loadpos, levelNumber, hudInfo, main, menu, stateCode + 1);
        }
    }

    protected final void pointerPressed(int x, int y) {
        super.pointerPressed(x, y);
        this.y0 = y;
    }

    protected final void pointerDragged(int x, int y) {
        x = y - this.y0;
        this.y0 = y;
        toMove = 0;
        this.serviceRepaints();
        this.text.move(x);
        this.repaint();

    }

    protected final void pointerReleased(int x, int y) {
        super.pointerReleased(x, y);
        this.repaint();
    }
    
    public static boolean needToShow(int levelNumber, int helpState, boolean levelEnd) {
        String add = "";
        if(helpState > 1) add = "_" + Integer.toString(helpState);
        String prefix = levelEnd?"LEVEL_END_":"GAME_HELP_";
            
        boolean dontNeedToShow = (
                Main.getGameText().get(prefix+"TEXT_" + Integer.toString(levelNumber) + add) == null
                    && (levelNumber != 1 || add.length() != 0 || Main.getGameText().get(prefix+"TEXT") == null)
                    && Main.getGameText().get(prefix+"PREVIEW_" + Integer.toString(levelNumber) + add) == null
                    && Main.getGameText().get(prefix+"BACKGROUND_" + Integer.toString(levelNumber) + add) == null
                )
                ||
                (
                RoomObject.containsSimple(Player.usedPoints, prefix+"SCREEN_" + Integer.toString(levelNumber) + add)
                    && Main.getGameText().getInt(prefix+"VIEW_ONCLE_" + Integer.toString(levelNumber) + add, 0) == 1
                );
        
        return !dontNeedToShow;
    }

}
