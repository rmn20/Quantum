package code.Gameplay;

import code.AI.Player;
import code.HUD.Base.Font;
import code.HUD.GUIScreen;
import code.utils.GameIni;
import code.utils.ImageResize;
import code.utils.IniFile;
import code.utils.Main;
import code.utils.WeaponCreator;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class Shop extends GUIScreen {

    public static int weapon_count = 0;
    public static String[] files;
    public static int[] prices;
    public static String[] bckFiles;
    public static int[] bckPrices;
    public static int index = 0; // Текущий номер товара
    public static int[] items;
    public static int[] allitems;
    private static boolean proportionalShop = true;
    public static int[] defaultArsenal = null;

    public static void initShop() {
        int pos;
        GameIni settings = Main.settings;

        boolean exists = Main.isExist("/weapons.txt");

        if (settings.get("DEFAULT_ARSENAL") != null) {
            defaultArsenal = GameIni.cutOnInts(settings.get("DEFAULT_ARSENAL"), ',', ';');
        } else {
            if (exists) {
                defaultArsenal = new int[1];
                defaultArsenal[0] = 0;
            } else {
                defaultArsenal = new int[1];
                defaultArsenal[0] = -1;
            }
        }

        if(exists) {
            Object[] tmp = GameIni.createGroups("/weapons.txt");
            String[] names = (String[])tmp[0];
            GameIni[] groups = (GameIni[])tmp[1];
            int len = names.length;
            
            weapon_count = len;
            allitems = new int[len];
            for(int i = 0; i < len; i++) {allitems[i] = i;}
            
            items = new int[len];
            for(int i = 0; i < len; i++) {items[i] = i;}
            
            files = new String[len];
            prices = new int[len];
            bckFiles = new String[len];
            bckPrices = new int[len];

            for(int i=0; i<len; i++) {
                GameIni obj = groups[i];

                files[i] = obj.get("SHOP_IMAGE");
                prices[i] = obj.getInt("PRICE", 0);
                bckFiles[i] = obj.get("SHOP_IMAGE");
                bckPrices[i] = obj.getInt("PRICE", 0);
            }
        } else {
            weapon_count = 2;
            allitems = new int[2];
            for (int i = 0; i < 2; i++) {allitems[i] = i;}
            items = new int[2];
            for (int i = 0; i < 2; i++) {items[i] = i;}
            files = new String[2];
            prices = new int[2];
            bckFiles = new String[2];
            bckPrices = new int[2];
        }

        files[files.length - 1] = Main.shop_medkit;
        prices[files.length - 1] = settings.getInt("PRICE_MEDICINE_CHEST");
        bckFiles[files.length - 1] = Main.shop_medkit;
        bckPrices[files.length - 1] = settings.getInt("PRICE_MEDICINE_CHEST");

        if (settings.getInt("SHOP_PROPORTIONAL",1) == 0) {
            proportionalShop = false;
        }
    }
    
    private GameScreen gameScreen;
    private Player player;
    private Image iconItem;
    private Image iconPatron;
    private static boolean side = false; // 0 - from left; 1 - from right
    private static long sideAnimBegin = 0L;
    private static int sideAnimOld = 0;
    private static long sideAnimTime = 130L;
    private static boolean paint = false;//Происходит ди отрисовка

    public Shop(Main main, GameScreen gameScreen, Player player) {
        this.gameScreen = gameScreen;
        this.player = player;
        IniFile lang = Main.getGameText();
        set(Main.getFont(), lang.get("BUY"), lang.get("BACK"));
        iconPatron = ImageResize.createImageProportional(Main.shop_patron,getWidth()/240F,getHeight()/320F);
        reset();
        sideAnimBegin=0L;
        inverseHScroll=true;
        inverseVScroll=true;
    }

    public final void destroy() {
        super.destroy();
        iconItem = iconPatron = null;
        player = null;
    }

    private void reset() {
        String path = Shop.files[Shop.index];
        if (Main.getGameText().get(path) != null) path = Main.getGameText().get(path);
        

        if (proportionalShop) {
            iconItem = ImageResize.createImageProportional(path,getWidth()/240F,getHeight()/320F);
            return;
        }
        iconItem = ImageResize.createImage(path,(int)getWidth(),(int)getHeight());

    }


    // цена текущего товара
    private int price() {
        return index == files.length - 1 ? prices[index] : (isNotPurchased() ? prices[index] : ((isPurchased() && isPatron()) ? prices[index] / 3 : 32767));
    }

    // true, если аптечкку можно купить (есть деньги и hp<100)
    private boolean isAvailableAidKit() {
        return index == files.length - 1 && player.getHp() == 100 ? false : player.money >= price();
    }
    
    // false, если аптечкку можно купить (есть деньги)
    private boolean isAvailableAidKitPrice() {
        return index != files.length - 1 ? false : player.money < price();
    }

    // true, если оружие еще не куплено
    private boolean isNotPurchased() {
        Weapon[] weapons = player.arsenal.getWeapons();
        return index >= 0 && index < files.length - 1 && weapons[index] == null;
    }

    // true, если можно купить патроны
    private boolean isPatron() {
        Weapon[] weapons = player.arsenal.getWeapons();
        if(index >= 0 && index < files.length - 1 && weapons[index] != null) {
            return weapons[index].patronbuy;
        }
        return false;
    }

    private boolean isPurchased() {
        Weapon[] weapons = player.arsenal.getWeapons();
        return index >= 0 && index < files.length - 1 && weapons[index] != null;
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
        if (isAvailableAidKit()) {
            setLeftSoft(lang.get("BUY"));
        } else {
            if(isPatron() || isNotPurchased() || isAvailableAidKitPrice()) setLeftSoft(lang.get("NOTENOUGHMONEY"));
            else setLeftSoft("");
        }
        
        int arWidth = w / 34;
        int arHeight = h / 34;
        if (Main.verticalShopScroll == false) {
            drawArrow(g, 4, h / 4, arWidth + 4, h / 4 - arHeight / 2, arWidth + 4, h / 4 + arHeight / 2);
            drawArrow(g, w - 4, h / 4, w - 4 - arWidth, h / 4 - arHeight / 2, w - 4 - arWidth, h / 4 + arHeight / 2);
        }

        Font font = getFont();
        font.drawString(g, lang.get("MONEY") + ":" + player.money, w - 2, 2, 24);
        
        g.drawImage(this.iconItem, w / 2, h / 2, 3);
        if (isPurchased() && isPatron()) 
            g.drawImage(iconPatron, w / 2 - iconItem.getWidth() / 2, h / 2 + iconItem.getHeight() / 2, 36);
        

        if (lang.get("CENA") != null && (isPatron() || isNotPurchased() || (index == files.length - 1))) 
            font.drawString(g, lang.get("CENA") + ":" + price(), w / 2, h / 2 + iconItem.getHeight() / 2 + 2, 17);
        
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
            int bright;
            if (this.isAvailableAidKit()) {
                bright = Math.min(255, i * 255 / sizey);
                g.setColor(bright, bright, bright);
            } else {
                bright = Math.min(255, i * 255 / sizey);
                g.setColor(bright, 0, 0);
            }
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
        if (!isAvailableAidKit()) return;
        
            Weapon[] weapons;
            if (isNotPurchased()) {
                weapons = player.arsenal.getWeapons();
                player.pay(price());
                weapons[index] = WeaponCreator.createWeapon(index);
                weapons[index].setAmmo(100);
                if (player.arsenal.current != -1) {
                    if (player.arsenal.currentWeapon().getDamageValue() < player.arsenal.weapons[index].getDamageValue()) {
                        player.arsenal.current = index;
                    }
                }
                if(!paint) repaint();
            } else if (isPurchased() && isPatron()) {
                weapons = player.arsenal.getWeapons();
                player.pay(price());
                weapons[index].addAmmo(100);
                if(!paint) repaint();
            } else {
                if (index == files.length - 1) {
                    player.pay(price());
                    player.setHp(100);
                    if(!paint) repaint();
                }

            }
    }

    protected final void onKey6() {
        if (Main.verticalShopScroll || paint) return;
        dragIgnore=true;
        
        Shop.index++;
        Shop.index %= Shop.files.length;
        if (!isContains()) {
            onKey6(); return;
        }
        
        reset();
        side=true; sideAnimBegin=System.currentTimeMillis(); sideAnimOld=0;
        if(!paint) repaint();
    }

    protected final void onKey4() {
        if (Main.verticalShopScroll || paint) return;
        dragIgnore=true;
        
        Shop.index--;
        if (Shop.index < 0) Shop.index += Shop.files.length;
        
        if (!isContains()) {
            onKey4();return;
        }
        
        reset();
        side=false; sideAnimBegin=System.currentTimeMillis(); sideAnimOld=0;
        if(!paint) repaint();
    }

    protected final void onKey8() {
        if (!Main.verticalShopScroll || paint) return;
        dragIgnore=true;
        
        Shop.index++;
        Shop.index %= Shop.files.length;
        if (!isContains()) {
            onKey8();return;
        }
        
        reset();
        side=true; sideAnimBegin=System.currentTimeMillis(); sideAnimOld=0;
        if(!paint) repaint();
    }

    protected final void onKey2() {
        if (!Main.verticalShopScroll || paint) return;
        dragIgnore=true;
        
        Shop.index--;
        if (Shop.index < 0) Shop.index += Shop.files.length;
        if (!isContains()) {
            onKey2();return;
        }
        
        reset();
        side=false; sideAnimBegin=System.currentTimeMillis(); sideAnimOld=0;
        if(!paint) repaint();
    }

    private boolean isContains() {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == Shop.index) return true;
            
        }
        return false;
    }
}
