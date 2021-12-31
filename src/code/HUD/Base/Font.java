package code.HUD.Base;

import code.Rendering.Meshes.Mesh;
import code.utils.IniFile;
import code.utils.StringTools;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import java.util.Vector;
import javax.microedition.lcdui.Image;

public final class Font {

   private int space; // Ширина пробела в пикселях
   private Image img,selimg,actimg;
   private int[] coords; // иксы левого верхнего угла каждого символа  
   private char[] chars; // Символы языка
   private int y_src = 0; // у левого верхнего угла символа (нужна при использовании drawRegion)


   public Font(String file) {
      try {
         IniFile str = IniFile.createFromResource(file);
         String var2 = str.get("IMG");
         String sel_font = str.get("SELECTED_IMG");
         String act_font = str.get("ACTIVE_IMG");
         if(act_font==null) act_font = var2;
         if(sel_font==null) sel_font = var2;
         selimg = Image.createImage(sel_font);
         actimg = Image.createImage(act_font);

         chars = str.get("CHARS").toCharArray();

            if(str.get("COORDS") != null) {
                img = Image.createImage(var2);
                coords = StringTools.cutOnInts(str.get("COORDS"), ',');
                space = str.getInt("SPACE");

            } else {

                Image tImg = Image.createImage(var2);
                img = Image.createImage(tImg, 0, 1, tImg.getWidth(), tImg.getHeight() - 1, 0);
                selimg = Image.createImage(selimg, 0, 1, selimg.getWidth(), selimg.getHeight() - 1, 0);
                actimg = Image.createImage(actimg, 0, 1, actimg.getWidth(), actimg.getHeight() - 1, 0);
                int[] line = new int[tImg.getWidth()];

                tImg.getRGB(line, 0, tImg.getWidth(), 0, 0, tImg.getWidth(), 1);

                Vector vCoords = new Vector();
                vCoords.addElement(new Integer(0));
                
                for(int i=0; i<line.length; i++) {
                    int col = line[i];
                    if(col == -16777216) vCoords.addElement(new Integer(i));
                }
                vCoords.addElement(new Integer(img.getWidth()));

                coords = new int[vCoords.size()];
                for(int i=0; i<vCoords.size(); i++) {
                    coords[i] = ((Integer) vCoords.elementAt(i)).intValue();
                }

                this.space = 0;
                for(int i=0; i<chars.length; i++) {
                    space += charWidth(chars[i]);
                }
                space /= chars.length;

            }
        } catch (IOException var3) {
            var3.printStackTrace();
        }
    }

    public int getCode(char ch) {
        for(int i=0; i<chars.length; i++) {
            if(chars[i] == ch) return i;
        }
        return -1;
    }

    public int charWidth(char ch) {
        if(ch == ' ') return space;
        int code = getCode(ch);
        if(code == -1) return space;
        int b = coords[code];
        int e = coords[code+1];
        return e-b;
    }

   public final void setY(int val) {
         this.y_src = 0;
      

   }

   private int indexOf(char ch) {
      for(int var2 = 0; var2 < this.chars.length; ++var2) {
         if(this.chars[var2] == ch) {
            return var2;
         }
      }

      return -1;
   }

   // Рисует строку на экране, аналогично методу из javax.microedition.lcdui.Graphics 
   
   public final void drawString(Graphics g, String str, int x, int y, int anchor) {
       drawString(g, str, x, y, anchor, 0);
   }
   public final void drawString(Graphics g, String str, int x, int y, int anchor, int col) {
      int var10004 = str.length();
      int var6 = y;
      y = var10004;
      boolean var12 = false;
      int var8 = x;
      int var9 = var6;
      Image im = img;
      if(col==1) im = selimg;
      if(col==2) im = actimg;
      // 8=RIGHT
      if((anchor & 8) != 0) {
         var8 = x - this.widthOf(str,g);
      }

      // 32=BOTTOM
      if((anchor & 32) != 0) {
         var9 = var6 - this.height();
      }

      // 1=HCENTER
      if((anchor & 1) != 0) {
         var8 -= this.widthOf(str,g) >> 1;
      }

      // 2=VCENTER
      if((anchor & 2) != 0) {
         var9 -= this.height() >> 1;
      }

      // 64=BASELINE
      if((anchor & 64) != 0) {
         var9 -= this.height() + 1;
      }

      int var7 = var9;
      var6 = var8;
      anchor = y;
      byte var16 = 0;
      String var15 = str;
      Graphics var14 = g;
      Font font = this;
      var8 = this.img.getHeight();
      int defhei=var14.getFont().getHeight();
      var9 = this.y_src * var8;

      for(y = var16; y < anchor; ++y) {
         char var10;
         if((var10 = var15.charAt(y)) == 32 && font.indexOf(var10)==-1)  {
            var6 += font.space;
         } else {
            int var17;
            if((var17 = font.indexOf(var10)) == -1) {
               var14.setColor(0xffffff);

               var14.drawChar(var10,var6, var7+var8,Graphics.BOTTOM|Graphics.LEFT );
               var6 += var14.getFont().charWidth(var10);
            } else {
               int var11 = font.coords[var17];
               var17 = font.coords[var17 + 1] - var11;
               var14.drawRegion(im, var11, var9, var17, var8, 0, var6, var7, 0);
               var6 += var17;
            }
         }
      }

   }


   // Ширина символа в пикселях
   public final int widthOf(char ch,Graphics g) {
      int var3 = this.indexOf(ch);
      if(ch == 32 && var3==-1) {
         return this.space;
      } else {
         if(var3 == -1) {
            return g.getFont().charWidth(ch);
         } else {
            int var2 = this.coords[var3];
            return this.coords[var3 + 1] - var2;
         }
      }
   }

   // Высота шрифта в пикселях
public final int widthOf(char ch) {
      int var3 = this.indexOf(ch);
      if(ch == 32 && var3==-1) {
         return this.space;
      } else {
         if(var3 == -1) {
            return this.space;
         } else {
            int var2 = this.coords[var3];
            return this.coords[var3 + 1] - var2;
         }
      }
   }

   // Высота шрифта в пикселях
   public final int height() {
      return this.img.getHeight();
   }

   public final int height2(String str,Graphics g) {
      int stdh=this.img.getHeight();

for(int var5 = 0; var5 < str.length(); ++var5) {
         if(this.indexOf(str.charAt(var5)) == -1 && str.charAt(var5)!=32) {
            int h= g.getFont().getHeight();
if(h>stdh) return h;
         }       
      }
return stdh;


   }

   // Ширина строки в пикселях
   public final int widthOf(String str,Graphics g) {
      int var3 = str.length();
      char var2[] = str.toCharArray();
      Font var7 = this;
      int var4 = 0;

      for(int var5 = 0; var5 < var3; ++var5) {
         var4 += var7.widthOf(var2[var5],g);
      }

      return var4;
   }


public final int widthOf(String str) {
      int var3 = str.length();
      char var2[] = str.toCharArray();
      Font var7 = this;
      int var4 = 0;

      for(int var5 = 0; var5 < var3; ++var5) {
         var4 += var7.widthOf(var2[var5]);
      }

      return var4;
   }

}
