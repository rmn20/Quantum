package code.HUD;

import code.HUD.Base.TextView;
import code.utils.Main;
import javax.microedition.lcdui.Graphics;
final class Help extends GUIScreen {

   private int y0; // y точки из которой перетскивается текст
   private Main main;
   private Menu menu;
   private TextView text;
   private static short toMove=0;
   int x, y;

   public Help(Main main, Menu menu) {
      this.main = main;
      this.menu = menu;
      this.setFont(Main.getFont());
      this.setSoftKeysNames((String)null, Main.getGameText().get("BACK"));
      x = getWidth() / 15;
      y = getHeight() / 13;
      int width = getWidth() - x * 2;
      int height = getHeight() - y * 2;
      text = new TextView(Main.getGameText().get("HELP_TEXT"), width, height, Main.getFont());
      text.setCenter(true);
      toMove=0;
      pointerPressed=false;
   }

    protected final void paint(Graphics g) {
        if(pressUp && toMove == 0 && !pointerPressed) toMove += Main.getFont().height();
        if(pressDown && toMove == 0 && !pointerPressed) toMove -= Main.getFont().height();
        if(toMove != 0 && !pointerPressed) {
            this.text.move(Math.max(Math.min(3, toMove), -3));
            toMove -= Math.max(Math.min(3, toMove), -3);
        }
        this.menu.drawBackground(g);
        this.text.paint(g,x,y);
        this.drawSoftKeys(g);

        if(!pointerPressed) try {
            Thread.sleep(22L);
            repaint();
        } catch (Exception exc) {/*System.out.println("Helpscreen render error: "+exc.getMessage());*/

        }
    }

   protected final void onRightSoftKey() {
      Main.setCurrent(this.menu);
   }


   protected final void pointerPressed(int x, int y) {
      super.pointerPressed(x, y);
      this.y0 = y;
   }
   
   protected final void pointerReleased(int x, int y) {
      super.pointerReleased(x, y);
      this.repaint();
   }

   protected final void pointerDragged(int x, int y) {
      x = y - this.y0;
      this.y0 = y;
      toMove=0;
      this.serviceRepaints();
      this.text.move(x);
      this.repaint();
   }
}
