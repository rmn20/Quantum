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

    public static int weaponCount = 0;
    public static String[] defaultFiles;
    public static int[] defaultPrices;
    public static int[] defaultItems;
	
    private static boolean proportionalShop = true;
    public static int[] defaultArsenal = null;
    public static int[] defaultArsenalAmmo = null;

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
		
        if (settings.get("DEFAULT_ARSENAL_AMMO") != null) {
			defaultArsenalAmmo = GameIni.cutOnInts(settings.get("DEFAULT_ARSENAL_AMMO"), ',', ';');
		}

        if(exists) {
            Object[] tmp = GameIni.createGroups("/weapons.txt");
            String[] names = (String[])tmp[0];
            GameIni[] groups = (GameIni[])tmp[1];
            
            weaponCount = names.length;
            
            defaultFiles = new String[weaponCount + 1];
            defaultPrices = new int[weaponCount + 1];
            defaultItems = new int[weaponCount + 1];

            for(int i=0; i<weaponCount; i++) {
                GameIni obj = groups[i];

                defaultFiles[i] = obj.get("SHOP_IMAGE");
                defaultPrices[i] = obj.getInt("PRICE", 0);
				defaultItems[i] = i;
            }
        } else {
            weaponCount = 1;
			
            defaultFiles = new String[2];
            defaultPrices = new int[2];
			defaultItems = new int[] {0, -1};
        }

        defaultFiles[weaponCount] = Main.shop_medkit;
        defaultPrices[weaponCount] = settings.getInt("PRICE_MEDICINE_CHEST");
        defaultItems[weaponCount] = -1;

        if (settings.getInt("SHOP_PROPORTIONAL",1) == 0) {
            proportionalShop = false;
        }
    }
    
    private static boolean side = false; // 0 - from left; 1 - from right
    private static long sideAnimTime = 130L;
    private static boolean paint = false;//Происходит ди отрисовка
	
    private GameScreen gameScreen;
    private Player player;
    private Image iconItem;
    private Image iconPatron;
	
	private int[] items;
	private String[] files;
	private int[] prices;
	private int index;
	
    private long sideAnimBegin = 0L;
    private int sideAnimOld = 0;

    public Shop(Main main, GameScreen gameScreen, Player player, int[] items, String[] files, int[] prices) {
        this.gameScreen = gameScreen;
        this.player = player;
		
		this.items = items;
		this.files = files;
		this.prices = prices;
		
		setMissingData();
		
        IniFile lang = Main.getGameText();
        set(Main.getFont(), lang.get("BUY"), lang.get("BACK"));
        iconPatron = ImageResize.createImageProportional(Main.shop_patron, getWidth()/240F, getHeight()/320F);
        setImage();
		
        inverseHScroll=true;
        inverseVScroll=true;
    }
	
	private void setMissingData() {
		if(items == null) {
			items = Shop.defaultItems;
			if(files == null) files = Shop.defaultFiles;
			if(prices == null) prices = Shop.defaultPrices;
			
			return;
		}
		
		if(files == null) {
			files = new String[items.length];
			
			for(int i =0; i < files.length; i++) {
				int idx = items[i];
				if(idx == -1) idx = defaultFiles.length - 1;
				files[i] = defaultFiles[idx];
			}
		}
		
		if(prices == null) {
			prices = new int[items.length];
			
			for(int i =0; i < prices.length; i++) {
				int idx = items[i];
				if(idx == -1) idx = defaultPrices.length - 1;
				prices[i] = defaultPrices[idx];
			}
		}
	}

    public final void destroy() {
        super.destroy();
        iconItem = iconPatron = null;
        player = null;
    }

    private void setImage() {
        String path = files[index];
        if (Main.getGameText().get(path) != null) path = Main.getGameText().get(path);
        
        if (proportionalShop) {
            iconItem = ImageResize.createImageProportional(path, getWidth() / 240F, getHeight() / 320F);
        } else {
			iconItem = ImageResize.createImage(path, (int)getWidth(),(int)getHeight());
		}
    }
	
	private boolean isMedkit() {
		return items[index] == -1;
	}
	
	private boolean isAllowedToBuy() {
		if(isMedkit()) {
			return player.getHp() < 100;
		} else {
			Weapon[] weapons = player.arsenal.getWeapons();
			Weapon weapon = weapons[items[index]];

			return weapon == null || weapon.patronbuy;
		}
	}
	
    private int price() {
		int price = prices[index];
		
		if(!isMedkit()) {
			Weapon[] weapons = player.arsenal.getWeapons();
			Weapon weapon = weapons[items[index]];

			if(weapon != null) {
				if(weapon.patronbuy) price = (int) (price * weapon.ammoPriceFactor);
				else price = Integer.MAX_VALUE;
			}
		}

		return price;
    }

    protected final void paint(Graphics g) {
		paint = true;
		int w = getWidth();
		int h = getHeight();
		
		int coof = (Main.verticalShopScroll ? h : w);
		
		if(System.currentTimeMillis() - sideAnimBegin < sideAnimTime) {
			if(sideAnimTime != 0) {
				coof = (int) ((System.currentTimeMillis() - sideAnimBegin) * coof / sideAnimTime);
			}
		}
		
		if(coof < w && !Main.verticalShopScroll) {
			if(!side) g.setClip(sideAnimOld, 0, coof - sideAnimOld, h);
			if(side) g.setClip(w - coof, 0, coof - sideAnimOld, h);
		} else if(coof < h && Main.verticalShopScroll) {
			if(!side) g.setClip(0, sideAnimOld, w, coof - sideAnimOld);
			if(side) g.setClip(0, h - coof, w, coof - sideAnimOld);
		} else g.setClip(0, 0, w, h);
		
		g.setColor(0);
		g.fillRect(0, 0, w, h);
		IniFile lang = Main.getGameText();
		
		if(!isAllowedToBuy()) {
			setLeftSoft("");
		} else {
			setLeftSoft(lang.get(player.money >= price() ? "BUY" : "NOTENOUGHMONEY"));
		}

		int arWidth = w / 34;
		int arHeight = h / 34;
		if(!Main.verticalShopScroll) {
			drawArrow(g, 4, h / 4, arWidth + 4, h / 4 - arHeight / 2, arWidth + 4, h / 4 + arHeight / 2);
			drawArrow(g, w - 4, h / 4, w - 4 - arWidth, h / 4 - arHeight / 2, w - 4 - arWidth, h / 4 + arHeight / 2);
		}

		Font font = getFont();
		font.drawString(g, lang.get("MONEY") + ":" + player.money, w - 2, 2, 24);

		g.drawImage(this.iconItem, w / 2, h / 2, 3);
		
		boolean printPrice = true;
		if(!isMedkit() && player.arsenal.getWeapons()[items[index]] != null) {
			if(!player.arsenal.getWeapons()[items[index]].patronbuy) {
				printPrice = false;
			} else {
				g.drawImage(iconPatron, w / 2 - iconItem.getWidth() / 2, h / 2 + iconItem.getHeight() / 2, 36);
			}
		}

		if(lang.get("CENA") != null && printPrice) {
			font.drawString(g, lang.get("CENA") + ":" + price(), w / 2, h / 2 + iconItem.getHeight() / 2 + 2, 17);
		}

		drawSoftKeys(g);
		sideAnimOld = coof;
		if((coof < w && !Main.verticalShopScroll) || (coof < h && Main.verticalShopScroll)) {
			try {
				Thread.sleep(5L);
			} catch(Exception exc) {
			}
			repaint();
		} else {
			paint = false;
			dragIgnore = false;
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
		
		int price = price();

        for (int i = 0; i < sizey; ++i) {
            if (player.money >= price) {
                int brightness = Math.min(255, i * 255 / sizey);
                g.setColor(brightness, brightness, brightness);
            } else {
                int brightness = Math.min(255, i * 255 / sizey);
                g.setColor(brightness, 0, 0);
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
		if(player.money < price()) return;
		
		if(isMedkit()) {
			if(player.getHp() < 100) {
				player.pay(price());
				player.setHp(100);
				if(!paint) repaint();
			}
		} else {
			int weaponIdx = items[index];
			
			Weapon[] weapons = player.arsenal.getWeapons();
			Weapon weapon = weapons[weaponIdx];
			
			if(weapon == null) {
				player.pay(price());
				
				weapon = WeaponCreator.createWeapon(weaponIdx);
				weapon.setAmmo(weapon.ammoBundled);
				weapons[weaponIdx] = weapon;
				
				int playerWeapon = player.arsenal.current;
				
				if(playerWeapon == -1 || weapons[playerWeapon].getDamageValue() < weapon.getDamageValue()) {
					player.arsenal.current = weaponIdx;
				}
				
				if(!paint) repaint();
			} else {
				player.pay(price());
				weapon.addAmmo(weapon.ammoInShop);
				if(!paint) repaint();
			}
		}
	}

    protected final void onKey6() {
        if(Main.verticalShopScroll || paint) return;
        dragIgnore=true;
        
        index++;
        index %= files.length;
        
        setImage();
        side=true; sideAnimBegin=System.currentTimeMillis(); sideAnimOld=0;
        if(!paint) repaint();
    }

    protected final void onKey4() {
        if(Main.verticalShopScroll || paint) return;
        dragIgnore=true;
        
        index--;
        if(index < 0) index += files.length;
        
        setImage();
        side=false; sideAnimBegin=System.currentTimeMillis(); sideAnimOld=0;
        if(!paint) repaint();
    }

    protected final void onKey8() {
        if(!Main.verticalShopScroll || paint) return;
        dragIgnore=true;
        
        index++;
        index %= files.length;
        
        setImage();
        side=true; sideAnimBegin=System.currentTimeMillis(); sideAnimOld=0;
        if(!paint) repaint();
    }

    protected final void onKey2() {
        if (!Main.verticalShopScroll || paint) return;
        dragIgnore=true;
        
        index--;
        if(index < 0) index += files.length;
        
        setImage();
        side=false; sideAnimBegin=System.currentTimeMillis(); sideAnimOld=0;
        if(!paint) repaint();
    }
}
