package code.HUD;

import code.HUD.Base.TextView;
import code.utils.Main;
import javax.microedition.lcdui.Graphics;

public class TitleScreen extends GUIScreen {

   Main main;
   Menu menu;
   TextView text;
   
   int pixelsPerSec;
   int w, h;
   long start;

   public TitleScreen(Main main, Menu menu) {
      this.main = main;
      this.menu = menu;
      w = getWidth(); h = getHeight();
      
      setFont(Main.getFont());
      setSoftKeysNames(null, null);
      
      
      text = new TextView(Main.getGameText().get("TITLE_SCREEN_TEXT"), w, h, Main.getFont());
      text.setCenter(true);
      
      pixelsPerSec = Main.settings.getInt("TITLE_SCREEN_SPEED", 10);
      
      String music = Main.settings.get("TITLE_SCREEN_MUSIC");
      if(Main.isMusic && music != null) {
          Main.musicPlayer.loadFile(music);
          Main.musicPlayer.setVolume(Main.music);
          Main.musicPlayer.start();
      }
      
      start = System.currentTimeMillis();
   }

    protected final void paint(Graphics g) {
        g.setColor(0);
        g.fillRect(0,0,w,h);
        g.setColor(0xffffff);
        
        int y = h - (int) ((System.currentTimeMillis() - start) * pixelsPerSec / 1000);
        text.setY(y);
        text.paint(g, 0, 0);
        
        if(y == -text.getTextHeight()) {
            onRightSoftKey();
            return;
        }

        try {
            Thread.sleep(5l);
        } catch (Exception e) {}
        
        repaint();
    }

    protected final void onRightSoftKey() {
        if(Main.musicPlayer.hasPlayer()) Main.musicPlayer.stop();
        
        if(menu == null) menu = new Menu(main);
        Main.setCurrent(menu);
    }
    
    public static boolean hasTitleScreen() {
        return Main.getGameText().get("TITLE_SCREEN_TEXT") != null;
    }
    
}
