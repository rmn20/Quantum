package code.HUD;

import code.HUD.Base.Selectable;
import code.HUD.Base.Font;
import code.Gameplay.GameScreen;
import code.utils.IniFile;
import code.utils.Main;
import javax.microedition.lcdui.Graphics;

public final class PauseScreen extends Selectable {

    private Main main;
    GameScreen gameScreen;
    private Object background;
    private boolean mus;
    private long mt = 0L;
    private int height3D;
    private int backgroundH;
    private int state = 0;
    private int lastIndex = 0;
    private boolean saveGame = true;

    public PauseScreen(Main main, GameScreen gameScreen, int[] background2, long time) {
        this.main = main;
        this.gameScreen = gameScreen;
        this.background = background2;
        mus = Main.isMusic;
        lastIndex = 0;
        mt = time;
        state = 0;
        IniFile text = Main.getGameText();
        String[] items = new String[Main.pauseScreenSave ? 6 : 5 + (DeveloperMenu.debugMode ? 1 : 0)];
        set(Main.getFont(), items, text.get("SELECT"), text.get("BACK"));
        reloadText();
        height3D = getHeight() / 2 - getHeight() * Main.getDisplaySize() / 200;
        backgroundH = getHeight() * Main.getDisplaySize() / 100;
        repaint(0, 0, getWidth(), getHeight());
    }

    public void reloadText() {
        IniFile text = Main.getGameText();
        String[] items = new String[(Main.pauseScreenSave ? 6 : 5) + (DeveloperMenu.debugMode ? 1 : 0)];
        set(Main.getFont(), items, text.get("SELECT"), text.get("BACK"));
        int i = 0;
        items[i++] = text.get("CONTINUE");
        items[i++] = text.get("RELOAD");
        if(Main.pauseScreenSave) {
            items[i++] = text.get("SAVE_GAME");
        }
        items[i++] = text.get("OPTIONS");
        items[i++] = text.get("MENU");
        items[i++] = text.get("EXIT");
        if(DeveloperMenu.debugMode) {
            items[i++] = "Developer Menu";
        }
    }

    protected final void paint(Graphics g) {
        g.setColor(0);
        if(state == 0)  lastIndex = list.getIndex();
        
        IniFile text = Main.getGameText();
        g.fillRect(0, 0, getWidth(), getHeight());

        if(background instanceof int[]) {
            g.drawRGB((int[]) background, 0, getWidth(), 0, height3D, getWidth(), backgroundH, false);
        }
        
        g.setColor(0xffffff);
        Font font = Main.getFont();
        Main.drawBckDialog(g, getHeight() - Main.getFont().height(), getHeight());

        if(state == 0) {
            setSoftKeysNames(text.get("SELECT"), text.get("BACK"));
            list.drawBck(g, 0, Main.getFont().height(), getWidth(), getHeight() - Main.getFont().height()*2);
            super.paint(g);

            int y = (getHeight() - Main.getFont().height() * 2 - list.getHeight()) / 2 + Main.getFont().height();
            y /= 3;

            Main.drawBckDialog(g, y - font.height() / 2, y + font.height() / 2);
            font.drawString(g, Main.getGameText().get("PAUSE"), getWidth() / 2, y, 3);
        } else if(state == 1 || state == 2) {
            int y = getHeight() / 2;
            Main.drawBckDialog(g, y - font.height() / 2, y + font.height() / 2);

            font.drawString(g, Main.getGameText().get(state==1?"CONFIRM":"SAVE_GAME?"), getWidth() / 2, y, 3);
            setSoftKeysNames(text.get("YES"), text.get("NO"));
            drawSoftKeys(g);
        }

    }

    protected final void onRightSoftKey() {
        if(state == 0) {
            setItemIndex(0);
            checkVariant();
        } else if(state == 1) {
            list.setIndex(lastIndex);
            state = 0;
            repaint();
        } else if(state == 2) {
            list.setIndex(lastIndex);
            saveGame = false;
            closeGS();
        }

    }

    protected final void onLeftSoftKey() {
        if(state == 0 && (itemIndex()<(Main.pauseScreenSave ? 4 : 3) 
                || (itemIndex() == getItems().length-1 && DeveloperMenu.debugMode)) && itemIndex()!=1 && (itemIndex()!=2 || !Main.pauseScreenSave)) {
            checkVariant();
        } else {
            if(state == 2) {
                list.setIndex(lastIndex);
                closeGS();
            } else if(state == 1) {
                list.setIndex(lastIndex);
                
                if(!Main.saveOnQuit || itemIndex()==1 || (itemIndex()==2 && Main.pauseScreenSave)) closeGS();
                else {
                    state = 2;
                    repaint();
                }
            } else {
                state = 1;
                repaint();
            }
        }
    }

    protected final void onKey5() {
        onLeftSoftKey();
    }

    private final void closeGS() {
        int index = itemIndex();

        if(index == (Main.pauseScreenSave ? 4 : 3)) {
            if(Main.saveOnQuit && saveGame && gameScreen.player.getHp()>0) {
                Main.saveGame(gameScreen.levelNumber, gameScreen.player, gameScreen.scene);
                Main.saveObjects(gameScreen.levelNumber, gameScreen.player, gameScreen.scene);
            }
            
            gameScreen.destroy();
            gameScreen = null;
            background = null;
            System.gc();
            Main.setCurrent(new Menu(main));
        } else if(index == (Main.pauseScreenSave ? 5 : 4)) {
            
            if(Main.saveOnQuit && saveGame && gameScreen.player.getHp() > 0) {
                Main.saveGame(gameScreen.levelNumber, gameScreen.player, gameScreen.scene);
                Main.saveObjects(gameScreen.levelNumber, gameScreen.player, gameScreen.scene);
            }
            Main.saveOnQuit = false;//Сохранение уже прошло, нет смысла повторять его снова по закрытию игры
            gameScreen.destroy();
            gameScreen = null;
            background = null;
            System.gc();
            main.notifyDestroyed();
        } else if(index == 1) {

            background = null;
            System.gc();
            boolean hasSave = Main.hasSave();
            int lvl = hasSave?Main.getContinueLevel():gameScreen.levelNumber;
            gameScreen.destroy();
            gameScreen = null;
            
            LoadingScreen ls = new LoadingScreen(main, lvl, hasSave, true);
            Main.setCurrent(ls);

        } else if(index == 2 && Main.pauseScreenSave) {
            if(gameScreen.player.getHp() > 0) {
                Main.saveGame(gameScreen.levelNumber, gameScreen.player, gameScreen.scene);
                Main.saveObjects(gameScreen.levelNumber, gameScreen.player, gameScreen.scene);
            }
            state = 0;
            repaint();
        }
    }

    private final void checkVariant() {
        int item = itemIndex();

        if(item == 0) {
            
            if(Main.isMusic != mus) {
                if(Main.isMusic) {
                    gameScreen.musTime = 0L;
                    mt = 0L;
                    gameScreen.init();
                } else gameScreen.destroyMusic();

            }
            
            if(height3D != getHeight()/2 - getHeight()*Main.getDisplaySize()/200) {
                gameScreen.resize();
                gameScreen.scene.getG3D().resize(getWidth(), getHeight()*Main.getDisplaySize()/100);
            }
            gameScreen.paused = false;
            Main.setCurrent(gameScreen);
            gameScreen.start();
            if(Main.isMusic) gameScreen.startMus(mt);
            background = null;
            System.gc();
            gameScreen = null;
        }

        if(item == (Main.pauseScreenSave?3:2)) main.setCurrent(new Setting(main, this, background));

        if(item == getItems().length-1 && DeveloperMenu.debugMode) main.setCurrent(new DeveloperMenu(main, this, background));
    }

}
