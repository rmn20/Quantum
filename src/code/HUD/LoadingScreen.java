package code.HUD;

import code.Gameplay.GameScreen;
import code.HUD.Base.Font;
import code.utils.Main;
import javax.microedition.lcdui.Graphics;

public final class LoadingScreen extends GUIScreen {

   private final Main main;
   private int frames = 0;
   
   private int lvl;
   private boolean loadSave;
   private boolean loadPos;

   public LoadingScreen(Main main, int lvl, boolean loadSave, boolean loadPos) {
      this.main = main;
      
      this.lvl = lvl;
      this.loadSave = loadSave;
      this.loadPos = loadPos;
      
      setFont(Main.getFont());
   } 

   public final void destroy() {
      super.destroy();
   }

   protected final void paint(Graphics g) {
      if(frames == 0) {
          Font font = getFont();
          int y = getHeight() / 2 - font.height() / 2;
          Main.drawBck(g, y - font.height() / 2, y + font.height() / 2);
          font.drawString(g, Main.getGameText().getDef("LOADING_SCREEN", "LOADING_SCREEN"), getWidth() / 2, y, 1 | 2);
      }
      
      frames++;
      
      if(frames < 2) {
          repaint();
      } else if(frames == 2) {
          
            GameScreen gs = new GameScreen(this.main, lvl, null);
            
            if(loadSave) {
                Main.loadGame(gs.player, getWidth(), getHeight(), gs.scene);
                gs.scene.getG3D().updateFov((int) gs.player.fov);
                Main.loadObjects(gs.player, gs.getWidth(), gs.getHeight(), gs.scene, gs.levelNumber);
                if(loadPos) Main.loadPosition(gs.player);
            }
            
            gs.player.copyNewToUsed();
            gs.start();
            gs.scene.deleteUsedObjects(gs.player);
            gs.scene.removeKilledBots();
            
            Main.setCurrent(gs);
      }
   }

}
