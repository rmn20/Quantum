package code.HUD;
import code.HUD.Base.Selectable;
import code.utils.IniFile;
import code.utils.Main;
import javax.microedition.lcdui.Graphics;

public final class LevelSelection extends Selectable {

   private Main main;
   private Menu menu;


   public LevelSelection(Main main, Menu menu) {
      this.main = main;
      this.menu = menu;
      IniFile var6;
      String var3 = (var6 = Main.getGameText()).get("LEVEL");
int op=0;

      String[] var4 = new String[main.getAvailableLevelCount()];

      for(int var5 = 0; var5 < var4.length; ++var5) {
         var4[var5] = var3 + " " + (var5 + 1);
         String tmp="";
         if(Main.levelCounter) tmp=(var5+1)+".";
if(var6.get("LEVEL_"+(var5+1))!=null) var4[var5]=tmp+var6.get("LEVEL_"+(var5+1));
      }

      this.set(Main.getFont(), var4, var6.get("SELECT"), var6.get("BACK"));
      if(Menu.hasSave==true) {
    
    this.setItemIndex(Main.getContinueLevel()-1);
}
   }

   public LevelSelection(Main main, Menu menu, Object hudInfo) {
      this(main, menu);
   }

   protected final void paint(Graphics g) {
      this.menu.drawBackground(g);
      super.paint(g);
   }

   protected final void onRightSoftKey() {
      Main.setCurrent(this.menu);
   }

   
   protected final void onLeftSoftKey() {
      this.onKey5();
   }

   protected final void onKey5() {
int levelNumber=this.itemIndex() + 1;
this.destroy();
Main.loadLevel(Main.levelSelectorLoadData,false,levelNumber,null,main,menu, 1, true);
   }
}
