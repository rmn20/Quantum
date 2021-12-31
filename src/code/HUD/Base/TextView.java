package code.HUD.Base;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author DDDENISSS
 */
public class TextView {
    private static final int INDENT = 3;
    private Vector lines = new Vector();
    private Font font;
    private int w, h;
    private boolean center = false;
    private int yOffset = 0;
    
    public TextView(String str, int w, int h, Font font) {
        this.font = font;
        this.w = w;
        this.h = h;
        if(str!=null) createLines(str, lines, font, w);
    }

    public static void createLines(String txt, Vector lines, Font font, int w) {
        int wordWidth = 0; //длина строки
        int wordStart = 0; //начало строки
        int lastSpace = -1; //предыдущий пробел

        for(int i=0; i<txt.length();) {
            final char ch = txt.charAt(i);
            if(ch == ' ') lastSpace = i;
            int wordEnd = -1;
            
            if (ch == '*') { //символ переноса строки
                wordEnd = i;
                i++; //пропускаем символ переноса
            } else  if (wordWidth + font.charWidth(ch) > w) { //следующий символ не умещается
                if (lastSpace != -1) { //обрезаем по последнему пробелу
                    i = lastSpace+1; //+1 - пропускаем последний пробел
                    wordEnd = lastSpace;
                } else {
                    wordEnd = i;
                }
            }
            
            if(wordEnd != -1) {
                String line = txt.substring(wordStart, wordEnd);
                lines.addElement(line);

                wordWidth = 0;
                wordStart = i;
            } else {
                wordWidth += font.charWidth(ch);
                i++;
            }
        }
        
        if(wordStart<txt.length()) {
            lines.addElement( txt.substring(wordStart, txt.length()) );
        }
        
    }

    public void addString(String str) {
        createLines(str, lines, font, w);
    }

    public void setString(String str) {
        lines.removeAllElements();
        createLines(str, lines, font, w);
    }


    public void paint(Graphics g, int x, int y) {
        int clipX = g.getClipX();
        int clipY = g.getClipY();
        int clipWidth = g.getClipWidth();
        int clipHeight = g.getClipHeight();
        g.setClip(Math.max(clipX,x), Math.max(clipY,y), Math.min(clipWidth,w), Math.min(clipHeight,h));
        
        final int stepY = getLineHeight();
        int posY = yOffset;
        for(int i=0; i<lines.size(); i++) {
            if(posY+stepY >= 0) {
                if(posY > h) break;
                String str = (String) lines.elementAt(i);
                int posX = center ? posX = (w-font.widthOf(str)) >> 1 : 0;
                font.drawString(g, str,  posX+x, posY+y, 0);
            }
            posY += stepY;
        }
        
        g.setClip(clipX, clipY, clipWidth, clipHeight);
    }

    public void move(int dy) {
        yOffset += dy;

        final int textHeight = getTextHeight();
        if(textHeight > h) { //весь текст не умещается на экране
            if(yOffset > 0) yOffset = 0;
            if(yOffset + textHeight < h) yOffset = h - textHeight;
        } else {
            if(yOffset < 0) yOffset = 0;
            if(yOffset + textHeight > h) yOffset = h - textHeight;
        }
        if(textHeight + yOffset < h && yOffset > 0) yOffset = 0;
    }

    public Font getFont() {
        return font;
    }
    public int getCountString() {
        return lines.size();
    }
    public int getLineHeight() {
        return font.height()+INDENT;
    }
    public int getTextHeight() {
        return getLineHeight()*lines.size()-INDENT;
    }

    public int getY() {
        return yOffset;
    }
    public void setY(int y) {
        yOffset = y;
    }
    
    public boolean getCenter() {
        return center;
    }
    public void setCenter(boolean cen) {
        center = cen;
    }

    public int getWidth() {
        return w;
    }
    public int getHeight() {
        return h;
    }
    
    public void setHeight(int h) {
        this.h = h;
    }

    
}