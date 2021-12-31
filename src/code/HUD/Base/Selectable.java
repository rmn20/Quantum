package code.HUD.Base;

import code.HUD.GUIScreen;
import javax.microedition.lcdui.Graphics;
import code.utils.Main;
import java.util.Vector;

public class Selectable extends GUIScreen {

   public ItemList list;


   public Selectable() {
   }

   public void destroy() {
      super.destroy();
      this.list = null;
   }

   protected final void set(Font font, Vector list, String leftSoft, String rightSoft) {
       String[] items = new String[list.size()];
       
       for(int i=0;i<items.length;i++) {
           items[i] = (String)list.elementAt(i);
       }
       
       set(font, items, leftSoft, rightSoft);
   }

   protected final void set(Font font, String[] items, String leftSoft, String rightSoft) {
      this.list = new ItemList(items);
      this.list.setFont(font);
      this.setFont(font);
      this.setSoftKeysNames(leftSoft, rightSoft);
   }
   
   protected final void set(Font font, String[] items, String leftSoft, String rightSoft,boolean[] ms) {
      this.list = new ItemList(items,font,ms);
      this.list.setFont(font);
      this.setFont(font);
      this.setSoftKeysNames(leftSoft, rightSoft);
   }

   protected void paint(Graphics g) {
      this.list.draw(g, 0, Main.getFont().height(), getWidth(), getHeight()-Main.getFont().height()*2);
      this.drawSoftKeys(g);
   }

   protected final void onKey2() {
      this.list.scrollUp();
      if(this.list.midSel!=null) if(this.list.midSel[this.list.getIndex()]==true) if(this.list.getIndex()!=0) {onKey2();return;}
      if(this.list.midSel!=null) if(this.list.midSel[this.list.getIndex()]==true) if(this.list.getIndex()==0) {onKey2();return;}
      repaint();
   }

   protected final void onKey8() {
      this.list.scrollDown();
      if(this.list.midSel!=null) if(this.list.midSel[this.list.getIndex()]==true) if(this.list.getIndex()!=this.list.getItems().length-1) {onKey8();return;}
      if(this.list.midSel!=null) if(this.list.midSel[this.list.getIndex()]==true) if(this.list.getIndex()==this.list.getItems().length-1) {onKey8();return;}
      repaint();
   }
   
   protected final void onKeyRepeated2() {
       onKey2();
   }

   protected final void onKeyRepeated8() {
       onKey8();
   }

   // Номер выбранного пункта меню
   public final int itemIndex() {
      return this.list.getIndex();
   }
   public final void setItemIndex(int i) {
      this.list.setIndex(i);
   }


   public final String[] getItems() {
      return this.list.getItems();
   }
}
