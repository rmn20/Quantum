package code.Gameplay;

import code.AI.Player;
import code.Gameplay.Inventory.ItemList;
import code.Gameplay.Inventory.ItemsEngine;
import code.HUD.Base.Font;
import code.HUD.GUIScreen;
import code.utils.ImageResize;
import code.utils.IniFile;
import code.utils.Main;
import code.utils.StringTools;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class InventoryScreen extends GUIScreen {
    
    public static boolean proportionalInventory = true;
    private static boolean side = false; // 0 - from left; 1 - from right
    private static long sideAnimBegin = 0L;
    private static int sideAnimOld = 0;
    private static long sideAnimTime = 130L;
    private static boolean paint = false;//Происходит ли отрисовка
    
    private GameScreen gameScreen;
    private Player player;
    private ItemList items;
    
    private int index = 0;
    private String itemName;
    private String itemScript;
    private String itemPath;
    private boolean itemCanThrow;
    private int itemCount;
    private Image iconItem;

    public InventoryScreen(Main main, GameScreen gameScreen, Player player) {
        this.gameScreen = gameScreen;
        this.items = player.items;
        this.player = player;
        IniFile lang = Main.getGameText();
        loadItem();
        
        sideAnimBegin=0L;
        inverseHScroll=true;
        inverseVScroll=true;
    }

    public final void destroy() {
        super.destroy();
        iconItem = null; itemName = itemScript = itemPath = null;
        items = null;
        player = null;
    }

    private void loadItem() {
        IniFile lang = Main.getGameText();
        
        if(items.size()<=0) {
            itemName = null;
            itemScript = null;
            itemPath = null;
            iconItem = null;
            set(Main.getFont(), null, lang.get("BACK"));
            
            return;
        }
        
        IniFile item = ItemsEngine.items[items.itemAt(index)];
        String path = item.get("ICON");
        
        itemCount = items.itemAtCount(index);
        itemName = item.get("NAME");
        if(itemName!=null) {
            String tmp = Main.getGameText().get(itemName+"_ITEM");
            if(tmp!=null) itemName = tmp;
        }
        itemScript = item.get("ON_ACTIVATE");
        itemCanThrow = item.getInt("CAN_THROW", 1) == 1;
        
        set(Main.getFont(), itemCanThrow?lang.get("THROW_ITEM"):null, lang.get("BACK"));
        
        if(path.equals(itemPath)) return;
        
        itemPath = path;

        if(proportionalInventory) iconItem = ImageResize.createImageProportional(path,getWidth()/240F,getHeight()/320F);
        else iconItem = ImageResize.createImage(path,(int)getWidth(),(int)getHeight());

    }

    protected final void paint(Graphics g) {
        paint=true;
        int w = getWidth();
        int h = getHeight();
        int coof=(Main.verticalShopScroll?h:w);
        if(System.currentTimeMillis()-sideAnimBegin<sideAnimTime) 
            if(sideAnimTime!=0) 
                coof=(int)((System.currentTimeMillis()-sideAnimBegin)*coof/sideAnimTime);
        
        
        
        if(coof<w && !Main.verticalShopScroll) {
            if(!side) g.setClip(sideAnimOld,0,coof-sideAnimOld,h);
            if(side) g.setClip(w-coof,0,coof-sideAnimOld,h);
        } else if(coof<h && Main.verticalShopScroll) {
            if(!side) g.setClip(0,sideAnimOld,w,coof-sideAnimOld);
            if(side) g.setClip(0,h-coof,w,coof-sideAnimOld);
        } else g.setClip(0,0,w,h);
        g.setColor(0);
        g.fillRect(0, 0, w, h);
        IniFile lang = Main.getGameText();
        
        int arWidth = w / 34;
        int arHeight = h / 34;
        if (Main.verticalShopScroll == false && items.size()>0) { //Draw arrows
            drawArrow(g, 4, h / 4, arWidth + 4, h / 4 - arHeight / 2, arWidth + 4, h / 4 + arHeight / 2);
            drawArrow(g, w - 4, h / 4, w - 4 - arWidth, h / 4 - arHeight / 2, w - 4 - arWidth, h / 4 + arHeight / 2);
        }

        Font font = getFont();
        
        if(items.size()>0) g.drawImage(iconItem, w / 2, h / 2, 3); //Draw item
        
        font.drawString(g, lang.get("HEALTH") + ":" + player.getHp(), w - 2, 2, 24); //24 = 3
        font.drawString(g, lang.get("MONEY") + ":" + player.money, 0, 0, 0); //10 = 1
        
        if(items.size()>0) { //Draw item info
            int ymove = iconItem.getHeight() / 2 + 2;
            if(!proportionalInventory) ymove = h/2 - font.height();
            
            font.drawString(g, itemName, w/2, h/2 - ymove-font.height(), 17);
            if(itemCount>1) {
                font.drawString(g, lang.get("ITEMS_COUNT") + ":" + String.valueOf(itemCount), w/2, h/2 - ymove, 17);
            }
            
            if(itemScript!=null) font.drawString(g, lang.get("USE"), w/2, h - font.height() , 17);
        } else { //Draw empty inventory
            set(Main.getFont(), null, lang.get("BACK"));
            font.drawString(g, lang.get("EMPTY_INVENTORY"), w/2, h/2 , 17);
        }

        
        drawSoftKeys(g);
        sideAnimOld=coof;
        if( (coof<w && !Main.verticalShopScroll) || (coof<h && Main.verticalShopScroll) ) {
            try{Thread.sleep(5L);}catch(Exception exc){}
            repaint();
        } else {
            paint=false;
            dragIgnore=false;
        }
    }

    private void drawArrow(Graphics g, int x1, int y1, int x2, int y2, int x3, int y3) {
        int miny = Math.min(y1, Math.min(y2, y3));
        int maxy = Math.max(y1, Math.max(y2, y3));
        int sizey = (maxy - miny) / 3;
        int clipX = g.getClipX();
        int clipY = g.getClipY();
        int clipWidth = g.getClipWidth();
        int clipHeight = g.getClipHeight();

        for (int i = 0; i < sizey; ++i) {
            int bright = Math.min(255, i * 255 / sizey);
            g.setColor(bright, bright, bright);
            g.setClip(clipX, Math.max(clipY,miny + (maxy - miny) * i / sizey), clipWidth, Math.min(clipHeight,maxy));
            g.fillTriangle(x1, y1, x2, y2, x3, y3);
        }
        g.setClip(clipX, clipY, clipWidth, clipHeight);
    }
    
    
    protected final void onRightSoftKey() {
        try {
            destroy();
            System.gc();
            Thread.sleep(20L);
        } catch (Exception var2) {}

        gameScreen.start();
        Main.setCurrent(gameScreen);
        gameScreen = null;
    }

    protected final void onLeftSoftKey() {
        if(items.size() <= 0 || !itemCanThrow) return;
        
        int oldCount = items.itemAtCount(index);
        
        gameScreen.scene.dropItem(ItemsEngine.items[items.itemAt(index)].get("NAME"), 1, player);
        items.removeItem(items.itemAt(index),1);
        
        String rmsScript = ItemsEngine.items[items.itemAt(index)].get("ON_THROW");
        if(rmsScript!=null) gameScreen.runScript(StringTools.cutOnStrings(rmsScript, ';'));
        
        if(oldCount==1) {
            if(index>=items.size()) index = items.size()-1;
            loadItem();
        } else itemCount--;
        
        if(!paint) repaint();
    }
    
    protected final void onKey5() {
        if(items.size()<=0 || itemScript==null) return;
        
        gameScreen.runScript(StringTools.cutOnStrings(itemScript, ';'));
        
        if(index>=items.size()) {
            index = items.size()-1;
        }
        
        loadItem();
        
        if(!paint) repaint();
    }

    protected final void onKey6() {
        if(items.size()<=0) return;
        
        if(Main.verticalShopScroll || paint) return;
        dragIgnore=true;
        
        index++;
        index %= items.size();
        
        loadItem();
        side=true; sideAnimBegin=System.currentTimeMillis(); sideAnimOld=0;
        if(!paint) repaint();
    }

    protected final void onKey4() {
        if(items.size()<=0) return;
        
        if(Main.verticalShopScroll || paint) return;
        dragIgnore=true;
        
        index--;
        if(index < 0) index += items.size();
        
        loadItem();
        side=false; sideAnimBegin=System.currentTimeMillis(); sideAnimOld=0;
        if(!paint) repaint();
    }

    protected final void onKey8() {
        if(items.size()<=0) return;
        
        if(!Main.verticalShopScroll || paint) return;
        dragIgnore=true;
        
        index++;
        index %= items.size();
        
        loadItem();
        side=true; sideAnimBegin=System.currentTimeMillis(); sideAnimOld=0;
        if(!paint) repaint();
    }

    protected final void onKey2() {
        if(items.size()<=0) return;
        
        if(!Main.verticalShopScroll || paint) return;
        dragIgnore=true;
        
        index--;
        if(index < 0) index += items.size();
        
        loadItem();
        side=false; sideAnimBegin=System.currentTimeMillis(); sideAnimOld=0;
        if(!paint) repaint();
    }
}
