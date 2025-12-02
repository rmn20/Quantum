package code.HUD;

import code.HUD.Base.Selectable;
import code.utils.ImageResize;
import code.utils.IniFile;
import code.utils.Main;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class Menu extends Selectable {

    private Main main;
    public Image backgroundMain;
    public Image background;
    public static int w = 240;
    public static int h = 320;
    public static boolean hasSave;
    
    private int newGameIndex, continueIndex, levelSelectIndex;
    private int helpIndex, optionsIndex, exitIndex;

    public Menu(Main main) {
        hasSave = Main.hasSave();
        this.main = main;
        w = getWidth();
        h = getHeight();

        background = ImageResize.createImage(Main.background, w, h);
        backgroundMain = background;

        if (Main.background_logo!=null && !Main.background_logo.equals(Main.background)) {
            backgroundMain = ImageResize.createImage(Main.background_logo, w, h);
        }
        
        if (Main.isMusic && Main.menuMusic!=null) {
            try {
                Main.musicPlayer.loadFile(Main.menuMusic);
                Main.musicPlayer.setVolume(Main.music);
                Main.musicPlayer.start();
            } catch (Exception e) {}
        }

        reloadText();
    }

    public final void reloadText() {
		newGameIndex = continueIndex = levelSelectIndex = 
				helpIndex = optionsIndex = exitIndex = -1;
		
        IniFile lang = Main.getGameText();
        Vector newList = new Vector();
        
        int levels = main.getAvailableLevelCount();
        
        if(!hasSave) {
            if(levels > 1 && Main.canSelectLevel) {
                newList.addElement(lang.get("SELECT_LEVEL"));
                levelSelectIndex = 0;
            } else {
                newList.addElement(lang.get("NEW_GAME"));
                newGameIndex = 0;
            }
        } else {
            newList.addElement(lang.get("CONTINUE")); 
            continueIndex = 0;
            newGameIndex = 1;
            
            if(levels>1 && Main.canSelectLevel) {
                newList.addElement(lang.get("SELECT_LEVEL"));
                levelSelectIndex = 1;
                newGameIndex = 2;
            }
            newList.addElement(lang.get("NEW_GAME"));
        }
        
        newList.addElement(lang.get("HELP"));
        helpIndex = newList.size()-1;
        
        newList.addElement(lang.get("OPTIONS"));
        optionsIndex = newList.size()-1;
        
        newList.addElement(lang.get("EXIT"));
        exitIndex = newList.size()-1;
        
        
        if(DeveloperMenu.debugMode) {
            newList.addElement("Benchmark");
            newList.addElement("AE-Mods");
            newList.addElement("Developer menu");
        }
        
        this.set(Main.getFont(), newList, lang.get("SELECT"), (String) null);

    }

    public final void destroy() {
        super.destroy();
        Main.musicPlayer.destroy();
        background = backgroundMain = null;
    }

    protected final void paint(Graphics g) {
        drawBackgroundLogo(g);
        super.paint(g);
    }

    public final void drawBackgroundLogo(Graphics g) {
        g.drawImage(backgroundMain, 0, 0, 0);
    }

    public final void drawBackground(Graphics g) {
        g.drawImage(background, 0, 0, 0);
    }

    protected final void onLeftSoftKey() {
        this.onKey5();
    }

    protected final void onKey5() {
        int index = itemIndex();
        
        if(index == newGameIndex) {
            newGame();
        } else if(index == continueIndex) {
            continueGame();
        } else if(index == levelSelectIndex) {
            selectLevel();
        } else if(index == helpIndex) {
            Help help = new Help(main, this);
            Main.setCurrent(help);
        } else if(index == optionsIndex) {
            Main.setCurrent(new Setting(main, this, background));
        } else if(index == exitIndex) {
            main.notifyDestroyed();
        } else if(index == list.getItems().length-3 && DeveloperMenu.debugMode) {
            Main.setCurrent(new Benchmark(main, this));
        } else if(index == list.getItems().length-2 && DeveloperMenu.debugMode) {
            try {
                main.platformRequest("http://ae-mods.ru");
            } catch (Exception exc) {}
        } else if(index == list.getItems().length-1 && DeveloperMenu.debugMode) {
            Main.setCurrent(new DeveloperMenu(main, this, background));
        }
    
        repaint();

    }

    private void selectLevel() {
        Main.setCurrent(new LevelSelection(main, this));
    }

    private void continueGame() {
        int levelNumber = Main.getContinueLevel();
        Main.loadLevel(true, true, levelNumber, null, main, this, 1, true);
    }

    private void newGame() {
        Main.removeSave();
        Main.loadLevel(false, false, 1, null, main, this, 1, true);
    }

}
