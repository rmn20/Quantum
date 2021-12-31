package code.HUD.Base;

import javax.microedition.lcdui.Graphics;
import code.utils.Main;

public final class ItemList {

   private Font font;
   private String[] items; // Пункты меню
   public boolean[] midSel; // Пункты меню
   public boolean left=false;
   public boolean redact=false;
   private int index = 0; // Номер выбранного пункта меню


   public ItemList(String[] items, Font font) {
      this.items = items;
      this.font = font;
   }
   
   public ItemList(String[] items, Font font,boolean[] ms) {
      this.items = items;
      this.font = font;
      this.midSel=ms;
   }

   public ItemList(String[] items) {
      this.items = items;
   }

   public final void setFont(Font font) {
      this.font = font;
   }
   
   public int getHeight() {
       return items.length*(font.height()+3);
   }

   public final void draw(Graphics g, int x, int y, int w, int h) {
        final int fontHeight = font.height();
        final int stepY = fontHeight+3;
        final int max = h/stepY;
        final boolean less = items.length <= max;
        int posy = y;
        
        
        int i = 0;
        if(less) posy += h/2 - items.length*stepY/2; //список по середине
        else {
            i = index - max/2;
            if(i < 0) i = 0;
            if(i >= items.length-max) i = items.length-max;
        }
        

        for(; i<items.length; i++) {
            if(posy < y) continue;
            if(posy+fontHeight > y+h) break;
            
            String str = items[i];
            int x2=w/2 - font.widthOf(str)/2 + x;
            boolean ms=false;
            if(midSel!=null) if(midSel[i]==true) ms=true;
            if(left && !ms) {
            x2=x;
            }
            font.drawString(g, str, x2, posy, 0, (redact&&i == index)? 2:((i == index || ms)?1:0) );
            posy += stepY;
        }
   }

    public final void drawBck(Graphics g, int x, int y, int w, int h) {
        final int fontHeight = font.height();
        final int stepY = fontHeight+3;
        final int max = h/stepY;
        final boolean less = items.length <= max;
        int posy = y;
        
        int i = 0;
        if(less) posy += h/2 - items.length*stepY/2; //список по середине
        else {
            i = index - max/2;
            if(i < 0) i = 0;
            if(i >= items.length-max) i = items.length-max;
        }

        int posy2 = posy;
        for(; i<items.length; i++) {
            if(posy2 < y) continue;
            if(posy2+fontHeight > y+h) break;
            posy2 += stepY;
        }

        Main.drawBckDialog(g, posy, posy2 - 3);
    }
    
    public final int getPosY(int h) {
        final int stepY = font.height() + 3;
        final int max = h / stepY;
        final boolean less = items.length <= max;
        int posy = 0;
        
        int i = 0;
        if(less) posy += h / 2 - items.length * stepY / 2; //список по середине
        else {
            i = index - max / 2;
            if(i < 0) i = 0;
            if(i >= items.length - max) i = items.length - max;
        }
        return posy-i*stepY;
    }

    public final void scrollDown() {
        index++;
        index %= items.length;
    }

    public final void scrollUp() {
        index--;
        if(index < 0) index = items.length - 1;
    }

    public final int getIndex() {
        return index;
    }

    public final void setIndex(int i) {
        index = i;
    }

    public final String getCurrentItem() {
        return this.items[this.index];
    }

    public final String[] getItems() {
        return this.items;
    }
}
