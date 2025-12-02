package code.HUD;

import code.HUD.Base.Selectable;
import code.utils.IniFile;
import code.utils.Main;
import code.utils.canvas.MyCanvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class DeveloperMenu extends Selectable {

   public static boolean debugMode = false;
   public static boolean drawLevelPoints = true;
   public static boolean drawPortals = false;
   public static boolean renderPolygonsOverwrite = false;
   public static boolean godMode = true;
   public static boolean showShootCollision = false;
   public static boolean showFps = true;
   public static boolean showRam = true;
   public static boolean showRoomID = false;
   public static boolean showPlayerPos = false;
   public static int fly = 0;
   
   private final Main main;
   private final MyCanvas menu;
   private final Object background;
   private final int backgroundH;
   private final int h;

   public DeveloperMenu(Main main, MyCanvas menu, Object background) {
      this.main = main;
      this.menu = menu;
      this.background=background;
      IniFile var4 = Main.getGameText();
      String[] var3 = new String[14];
      set(Main.getFont(), var3, (String)null, var4.get("BACK"));
      setItems();

      backgroundH=this.getHeight() / 2 - this.getHeight()*Main.getDisplaySize() / 200;
      h=this.getHeight()*Main.getDisplaySize() / 100;
   }

   private void setItems() {
      String[] items=getItems();
      int i=0;
      
      items[i] = "Debug: "+debugMode;i++;
      items[i] = "Open all levels";i++;
      items[i] = "Can select level: " + Main.canSelectLevel;i++;
      items[i] = "Show QFPS: "+showFps;i++;
      items[i] = "Show RAM: "+showRam;i++;
      items[i] = "Show room ID: "+showRoomID;i++;
      items[i] = "Draw portals: "+drawPortals;i++;
      items[i] = "Show player pos: "+showPlayerPos;i++;
      items[i] = "God mode: "+godMode;i++;
      items[i] = "Show level objects: "+drawLevelPoints;i++;
      items[i] = "Show shoot collision: "+showShootCollision;i++;
      items[i] = "Render Pixels Overwrite: "+renderPolygonsOverwrite;i++;
      items[i] = "Fly: "+(fly==0?"Off":fly==1?"Fly":"Noclip");i++;
      items[i] = "Play video!!! :D";

   }

    protected final void paint(Graphics g) {
        g.setColor(0);
        g.fillRect(0, 0, getWidth(), getHeight());

        if(background instanceof Image) {
            g.drawImage((Image) background, 0, 0, 0);
        } else if(this.background instanceof int[]) {
            g.drawRGB((int[]) background, 0, getWidth(), 0, backgroundH, getWidth(), h, false);
        }

        if(this.menu instanceof PauseScreen) {
            list.drawBck(g, getWidth() / 8, Main.getFont().height(), getWidth() * 6 / 8, getHeight() - Main.getFont().height()*2);
            Main.drawBckDialog(g, getHeight() - Main.getFont().height(), getHeight());
        }
        list.draw(g, getWidth() / 8, Main.getFont().height(), getWidth() * 6 / 8, getHeight() - Main.getFont().height() * 2);
        drawSoftKeys(g);
    }

   protected final void onRightSoftKey() {
      Main.setCurrent(this.menu);
   }

   protected final void onLeftSoftKey() {
      this.onKey6();
   }
   

   protected final void onKey4() {
      int var1 = this.itemIndex();
      int id = 0;
      id++; if(var1 == id-1) {
          debugMode=false;
          if(menu instanceof Menu) ((Menu)menu).reloadText();
          if(menu instanceof PauseScreen) ((PauseScreen)menu).reloadText();
      }
      id++; 
      id++; if(var1 == id-1) {
		  Main.canSelectLevel = false;
          if(menu instanceof Menu) ((Menu)menu).reloadText();
	  }
	  id++; if(var1 == id-1) showFps=false;
      id++; if(var1 == id-1) showRam=false;
      id++; if(var1 == id-1) showRoomID=false;
      id++; if(var1 == id-1) drawPortals=false;
      id++; if(var1 == id-1) showPlayerPos=false;
      id++; if(var1 == id-1) godMode=false;
      id++; if(var1 == id-1) drawLevelPoints=false;
      id++; if(var1 == id-1) showShootCollision=false;
      id++; if(var1 == id-1) renderPolygonsOverwrite=false;
      id++; if(var1 == id-1) setFly(fly==0?2:fly==1?0:1);
      id++; 
      
      this.setItems();
      this.repaint();
   }


   protected final void onKey6() {
      int var1 = this.itemIndex();
      int id = 0;
      id++; if(var1 == id-1) {
          debugMode=true;
          if(menu instanceof Menu) ((Menu)menu).reloadText();
          if(menu instanceof PauseScreen) ((PauseScreen)menu).reloadText();
      }
      id++; 
	  id++; if(var1 == id-1) {
		  Main.canSelectLevel = true;
          if(menu instanceof Menu) ((Menu)menu).reloadText();
	  }
      id++; if(var1 == id-1) showFps=true;
      id++; if(var1 == id-1) showRam=true;
      id++; if(var1 == id-1) showRoomID=true;
      id++; if(var1 == id-1) drawPortals=true;
      id++; if(var1 == id-1) showPlayerPos=true;
      id++; if(var1 == id-1) godMode=true;
      id++; if(var1 == id-1) drawLevelPoints=true;
      id++; if(var1 == id-1) showShootCollision=true;
      id++; if(var1 == id-1) renderPolygonsOverwrite=true;
      id++; if(var1 == id-1) setFly(fly==0?1:fly==1?2:0);
      id++; 
      
      this.setItems();
      this.repaint();
   }
   
 
   protected final void onKey5() {
      int var1 = this.itemIndex();
      int id = 0;
      boolean repaint = true;
      id++; if(var1 == id-1) {
		  debugMode^=true;
		  if(menu instanceof Menu) ((Menu) menu).reloadText();
		  if(menu instanceof PauseScreen) ((PauseScreen) menu).reloadText();
	  }
      id++; if(var1 == id-1) {
		  main.setAvailableLevelCount(Main.lastLevel);
		  if(menu instanceof Menu) ((Menu) menu).reloadText();
	  }
	  id++; if(var1 == id-1) {
		  Main.canSelectLevel ^= true;
          if(menu instanceof Menu) ((Menu)menu).reloadText();
	  }
      id++; if(var1 == id-1) showFps^=true;
      id++; if(var1 == id-1) showRam^=true;
      id++; if(var1 == id-1) showRoomID^=true;
      id++; if(var1 == id-1) drawPortals^=true;
      id++; if(var1 == id-1) showPlayerPos^=true;
      id++; if(var1 == id-1) godMode^=true;
      id++; if(var1 == id-1) drawLevelPoints^=true;
      id++; if(var1 == id-1) showShootCollision^=true;
      id++; if(var1 == id-1) renderPolygonsOverwrite^=true;
      id++; if(var1 == id-1) setFly(fly==0?1:fly==1?2:0);
      id++; if(var1 == id-1) {repaint=false;VideoPlayer vp=new VideoPlayer(main,"/video.3gp",this);}
      
      this.setItems();
      if(repaint)this.repaint();
    }
   
    public void setFly(int fl) {
        fly = fl;
        if(menu instanceof PauseScreen) {
            PauseScreen ps = (PauseScreen)menu;
            ps.gameScreen.player.character.fly = (fl>0);
        }
    }
}
