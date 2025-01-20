package code.utils;

import code.Gameplay.Inventory.ItemsEngine;
import code.AI.NPC;
import code.AI.misc.Splinter;
import code.Math.Matrix;
import code.Gameplay.Weapon;
import code.HUD.SplashScreen;
import code.Gameplay.Shop;
import code.HUD.Base.Font;
import code.AI.Player;
import code.Rendering.Texture;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import code.Gameplay.GameScreen;
import code.Gameplay.InventoryScreen;
import code.Gameplay.Map.RoomObject;
import code.Gameplay.Map.Scene;
import code.Gameplay.Objects.ItemsBag;
import code.Gameplay.Objects.NPCSpawner;
import code.HUD.Base.GameKeyboard;
import code.HUD.DeveloperMenu;
import code.HUD.GameHelp;
import code.HUD.LoadingScreen;
import code.HUD.Menu;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;
import code.Math.Vector3D;
import code.utils.canvas.MainCanvas;
import code.utils.canvas.MyCanvas;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

public final class Main extends MIDlet {

    private boolean run = false;
    public static int lastLevel; // номер поледнего уровня
    private static Font font;
    public static IniFile gameText;

    public static String Blood = "/blood.png";
    public static float bloodscale, splinterscale;
    public static boolean fullScreenSight = false;

    private static String savename;
    public static boolean corpses = false, hideHud = false, blood = false, frameskip = false, mipMapping = true;
    public static int displaySize = 80;

    public static int floorOffsetSZ = 0;

    public static int lang = -1;
    public static int q = 24;
    public static String life_icon, hand_icon, money_icon, patron_icon, sight_icon,
            skull_icon, background_logo, background, shop_patron, shop_medkit, low_life_icon,
            low_patron_icon, patron_no_weapon_icon;

    public static String[] splash;
    public static String[] stepSound = null;
    public static String jumpSound = null;
    public static String menuMusic = null;

    public static boolean canSave = true, canSelectLevel,
            levelSelectorLoadData = false, pauseScreenSave = false, saveOnQuit;
    public static boolean hasZoom = true;

    public static int stdFov = 74;
    public static int zoomFov = 44;
    public static String[] langs = null;
    public static Image bcks = null;
    public static Image bcks2 = null;
    public static boolean updateOnlyNear = false;
    public static boolean updateOnlyNearPhysics = false;
    public static GameIni settings;
    public static boolean playerHasInventory;

    public static boolean hidesight;
    public static int fogQ = 2; //0-off 1 - lq 2 - hq
    public static int pixelsQ = 2; //0-4xmax 1 - 2xmax 2 - 1xmax
    public static int persQ = 2;
    public static int availableLevel = 1; // номер доступного уровня
    public static boolean forceLQFog = false;
    public static boolean originalSight = false;
    public static boolean originalUseIcon = false;

    public static int music = 100;
    public static int sounds = 100;
    public static int footsteps = 100;
    public static int mouseSpeed = 66;

    public static boolean isMusic = true;
    public static boolean isSounds = true;
    public static boolean isFootsteps = true;
    public static Sound musicPlayer = Sound.createMusicPlayer();

    public static boolean verticalShopScroll = false;

    public static MainCanvas mainCanvas;
    public static final int s60Optimization = 0;
    /*
     n73 -1
     n86 0
     n95 1
     */
    public static boolean levelCounter = true;
    public static boolean resizeWeapons = false;
    public static boolean symbian = false; // Autodetect

    public static boolean isExist(String file) {
        try {
            InputStream is = file.getClass().getResourceAsStream(file);
            if(is == null) {
                return false;
            } else {
                is.close();
                return true;
            }
        } catch (Exception exc) {
            return false;
        }
    }

    protected final void startApp() {
        if(!run) init();
    }

    private void init() {
        run = true;
        //if(audio3D) Audio3DEngine.init();

        settings = GameIni.createGameIniFromResource("/setting.txt", true);

        savename = settings.getDef("SAVE_NAME", "ZOMBIE");
        DeveloperMenu.debugMode = settings.getInt("DEBUG", 0) == 1;

        splinterscale = settings.getFloat("SPLINTER_SCALE", 1.0f);
        bloodscale = settings.getFloat("BLOOD_SCALE", 1.0f);

        canSave = settings.getInt("CANSAVE", 1) == 1;
        saveOnQuit = settings.getInt("SAVESTATE", 0) == 1;
        canSelectLevel = settings.getInt("CANSELECTLEVEL", 1) == 1;
        levelSelectorLoadData = settings.getInt("LEVELSELECTOR_LOAD_DATA", 1) == 1;
        pauseScreenSave = settings.getInt("PAUSE_SCREEN_SAVE", 0) == 1;

        String tmp = settings.getNoLang("SHOPSCROLL");
        if(tmp != null) verticalShopScroll = tmp.equals("VERTICAL");

        hidesight = settings.getInt("HIDESIGHT", 0) == 1;
        menuMusic = settings.getNoLang("MENU_MUSIC");

        setLanguage("/languages/english.txt");

        Splinter.texture = Texture.createTexture(settings.getNoLang("SPLINTER_SPRITE", "/splinter.png"));
        Splinter.cache();

        String bloodTexture = settings.getNoLang("BLOOD_SPRITE", "/blood.png");
        if(bloodTexture != null) code.AI.misc.Blood.blood = Texture.createTexture(bloodTexture);

        tmp = settings.getNoLang("TEXT_BCK");
        if(tmp != null) {
            try {
                bcks = Image.createImage(tmp);
            } catch (Exception var3) {
                System.out.println("No text background");
            }
        }

        tmp = settings.getNoLang("DIALOG_BCK");
        if(tmp != null) {
            try {
                bcks2 = Image.createImage(tmp);
            } catch (Exception var3) {
                System.out.println("No dialog background");
            }
        }

        life_icon = settings.getNoLang("LIFE_ICON", "/life.png");
        low_life_icon = settings.getNoLang("LOW_LIFE_ICON", null);
        money_icon = settings.getNoLang("MONEY_ICON", "/money.png");
        patron_icon = settings.getNoLang("PATRON_ICON", "/patron.png");
        low_patron_icon = settings.getNoLang("LOW_PATRON_ICON", "/patron_low.png");
        patron_no_weapon_icon = settings.getNoLang("PATRON_NO_WEAPON_ICON", "/patron_no_weapon.png");
        skull_icon = settings.getNoLang("SKULL_ICON", "/skull.png");
        hand_icon = settings.getNoLang("HAND_ICON", "/hand.png");
        background_logo = settings.getNoLang("BACKGROUND_LOGO", "/background.png");
        background = settings.getNoLang("BACKGROUND", "/background2.png");
        shop_patron = settings.getNoLang("SHOP_PATRON_ICON", "/icon_patron.png");
        shop_medkit = settings.getNoLang("SHOP_MEDKIT_ICON", "/icon_medicine_chest.png");
        splash = GameIni.cutOnStrings(settings.getNoLang("SPLASH", "/splash.png"), ',', ';');
        levelCounter = settings.getInt("COUNT_LEVELS", 0) == 1;

        playerHasInventory = settings.getInt("PLAYER_HAS_INVENTORY", 0) == 1;
        InventoryScreen.proportionalInventory = settings.getInt("INVENTORY_PROPORTIONAL", 1) == 1;
        ItemsEngine.init();

        try {
            if(RmsUtils.hasStore(savename)) {
                byte[] var2 = RmsUtils.openStore(savename);
                ByteArrayInputStream var5 = new ByteArrayInputStream(var2);
                DataInputStream var6 = new DataInputStream(var5);
                music = var6.readInt();
                sounds = var6.readInt();
                footsteps = var6.readInt();
                displaySize = var6.readInt();
                availableLevel = var6.readInt();
                setPersQ(var6.readInt());
                fogQ = var6.readInt();
                pixelsQ = var6.readInt();
                frameskip = var6.readBoolean();
                corpses = var6.readBoolean();
                blood = var6.readBoolean();
                mipMapping = var6.readBoolean();
                DeveloperMenu.debugMode = var6.readBoolean();
                lang = var6.readInt();
                mouseSpeed = var6.readInt();
                hideHud = var6.readBoolean();
                resizeWeapons = var6.readBoolean();
                lastLevel = var6.readInt();

                try {
                    int codes = var6.readInt();
                    GameKeyboard.keyCodes = new int[codes];
                    GameKeyboard.hasKeyCodes = new boolean[codes];
                    for(int i = 0; i < codes; i++) {
                        if(var6.readBoolean()) {
                            GameKeyboard.hasKeyCodes[i] = true;
                            GameKeyboard.keyCodes[i] = var6.readInt();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error in keycodes loading");
                    GameKeyboard.keyCodes = null;
                }

                if(isExist("/level" + (lastLevel + 1) + ".txt") || !isExist("/level" + (lastLevel) + ".txt")) updateLevelCount();
            } else {
                throw (new Exception());
            }
        } catch (Exception exc) {
            defaultSettings();
        }

        Shop.initShop();

        tmp = settings.getNoLang("OPEN_LEVELS");
        if(tmp != null) {
            if(tmp.equals("ALL")) availableLevel = lastLevel;
            else availableLevel = Math.max(availableLevel, StringTools.parseInt(tmp));
        }

        langs = StringTools.cutOnStrings(StringTools.getStringFromResource("/languages/languages.txt"), ',');

        if(lang != -1) {
            String path = "/languages/" + langs[lang].toLowerCase() + ".txt";
            setLanguage(path);
        }

        mainCanvas = new MainCanvas(this);
        setCurrent(new SplashScreen(this));
    }

    private static void defaultSettings() {
        music = 100;
        sounds = 100;
        footsteps = 100;
        displaySize = 80;
        availableLevel = 1;
        persQ = 2;
        q = 24;
        pixelsQ = 2;
        fogQ = 2; //0-off 1 - lq 2 - hq

        corpses = false;
        blood = false;
        mipMapping = true;
        lang = -1;
        mouseSpeed = 66;
        hideHud = false;
        resizeWeapons = settings.getIntNoLang("RESIZE_WEAPONS", 0) == 1;

        symbian = false;
        String platform = System.getProperty("microedition.platform");
        String osName = System.getProperty("os.name");
        if(osName != null) {
            osName = osName.toLowerCase();
            symbian = osName.equals("symbian");
        } else {
            String[] devicesS60 = new String[]{
                "NokiaE71", "NokiaE66", "NokiaE63", "Nokia6124", "NokiaN95", "NokiaN82",
                "NokiaN81", "NokiaN76", "NokiaE90", "NokiaE51", "Nokia6290", "Nokia6121",
                "Nokia6110", "Nokia5700", "NokiaN77", "NokiaE65", "NokiaE61", "NokiaN93",
                "NokiaN92", "NokiaN91", "NokiaN80", "NokiaN75", "NokiaN73", "NokiaN71",
                "NokiaE70", "NokiaE62", "NokiaE60", "NokiaE50", "Nokia3250", "NokiaN91",
                "NokiaN72", "NokiaN90", "NokiaN70", "Nokia6681", "Nokia6680", "Nokia6630"
            };

            for(int i = 0; i < devicesS60.length; i++) {
                if(platform.indexOf(devicesS60[i]) > -1) {
                    symbian = true;
                    break;
                }
            }

        }

        frameskip = !symbian;

        boolean maxQuality = platform.equals("pstros");
        if(!maxQuality && osName != null) {
            maxQuality
                    = osName.indexOf("win") > -1
                    || osName.indexOf("mac") > -1
                    || osName.indexOf("nix") > -1
                    || osName.indexOf("nux") > -1
                    || osName.indexOf("aix") > -1
                    || osName.indexOf("android") > -1;
        }
        if(maxQuality) {
            //displaySize = 100;
            frameskip = true;
            setPersQ(3);
        }

        updateLevelCount();
    }

    private static void updateLevelCount() {
        for(lastLevel = 0; isExist("/level" + (lastLevel + 1) + ".txt"); lastLevel++);
    }

    protected final void pauseApp() {
        if(mainCanvas.getScreen() instanceof GameScreen) {
            GameScreen scr = (GameScreen) mainCanvas.getScreen();
            scr.openPause();
        }
    }

    protected final void destroyApp(boolean var1) {
    }

    public static final void setCurrent(MyCanvas canvas) {
        mainCanvas.setScreen(canvas);
    }

    public final void resetCanvas() {
        Display.getDisplay(this).setCurrent(mainCanvas);
    }

    public final void setCanvas(Displayable disp) {
        Display.getDisplay(this).setCurrent(disp);
    }

    public static final void setCurrentRepaint(MyCanvas canvas) {
        mainCanvas.setScreen(canvas);
        canvas.repaint();
    }

    public final void setLanguage(String file) {
        gameText = IniFile.createFromResource(file);
        Main.font = new Font(gameText.get("FONT"));
    }

    public static final Font getFont() {
        return font;
    }

    public static IniFile getGameText() {
        return gameText;
    }

    public static final boolean isFrameskip() {
        return frameskip;
    }

    public static final boolean isCorpses() {
        return corpses;
    }

    public static final boolean isMipMapping() {
        return mipMapping;
    }

    public final boolean isBlood() {
        return blood;
    }

    public static final void setDisplaySize(int size) {
        if(size < 50) size = 50;
        if(size > 100) size = 100;

        displaySize = size;
    }

    public static final int getDisplaySize() {
        return displaySize;
    }

    public final int getAvailableLevelCount() {
        return Math.min(availableLevel, Main.lastLevel);
    }

    public final void setAvailableLevelCount(int i) {
        availableLevel = i;
        saveSettingToStore();
    }

    public final void addAvailableLevel(int level) {
        if(level + 1 > availableLevel && level + 1 <= Main.lastLevel) {
            availableLevel = level + 1;

            saveSettingToStore();
        }

    }

    public static final boolean isLastLevel(int level) {
        return level >= lastLevel;
    }

    public final void saveSettingToStore() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeInt(music);
            dos.writeInt(sounds);
            dos.writeInt(footsteps);
            dos.writeInt(displaySize);
            dos.writeInt(availableLevel);
            dos.writeInt(persQ);
            dos.writeInt(fogQ);
            dos.writeInt(pixelsQ);
            dos.writeBoolean(frameskip);
            dos.writeBoolean(corpses);
            dos.writeBoolean(blood);
            dos.writeBoolean(mipMapping);
            dos.writeBoolean(DeveloperMenu.debugMode);
            dos.writeInt(lang);
            dos.writeInt(mouseSpeed);
            dos.writeBoolean(hideHud);
            dos.writeBoolean(resizeWeapons);
            dos.writeInt(lastLevel);

            dos.writeInt(GameKeyboard.keyCodes.length);
            for(int i = 0; i < GameKeyboard.keyCodes.length; i++) {
                if(GameKeyboard.hasKeyCodes[i]) {
                    dos.writeBoolean(true);
                    dos.writeInt(GameKeyboard.keyCodes[i]);
                } else {
                    dos.writeBoolean(false);
                }
            }

            byte[] data = baos.toByteArray();
            dos.close();
            baos.close();

            RmsUtils.removeStore(savename);
            RmsUtils.saveStore(savename, data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void drawBck(Graphics g, int beginy, int endy) {
        if(bcks == null) return;

        int imgh = bcks.getHeight();
        int imgw = bcks.getWidth();
        int iy = 0;
        for(int y = beginy - 1; y > beginy - imgh; y--) {
            g.drawRegion(bcks, 0, imgh - 2 - iy, imgw, 1, 0, 0, y, 0);
            iy++;
        }

        for(int y = beginy; y < endy; y++) {
            g.drawRegion(bcks, 0, imgh - 1, imgw, 1, 0, 0, y, 0);
        }

        iy = imgh - 1;
        for(int y = endy; y < endy + imgh; y++) {
            g.drawRegion(bcks, 0, iy, imgw, 1, 0, 0, y, 0);
            iy--;
        }

    }

    public static void drawBckDialog(Graphics g, int beginy, int endy) {
        if(bcks2 == null) return;

        int imgh = bcks2.getHeight();
        int imgw = bcks2.getWidth();
        int iy = 0;
        for(int y = beginy - 1; y > beginy - imgh; y--) {
            g.drawRegion(bcks2, 0, imgh - 2 - iy, imgw, 1, 0, 0, y, 0);
            iy++;
        }

        for(int y = beginy; y < endy; y++) {
            g.drawRegion(bcks2, 0, imgh - 1, imgw, 1, 0, 0, y, 0);
        }

        iy = imgh - 1;
        for(int y = endy; y < endy + imgh; y++) {
            g.drawRegion(bcks2, 0, iy, imgw, 1, 0, 0, y, 0);
            iy--;
        }

    }

    public static final void saveGame(int levelNum, Player player, Scene scene) {
        if(!canSave) return;

        try {
            String savname = savename + "_Player";

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeInt(-3);
            /*
             Список версий:
             1 - бета билд от 12.03.2019
             2 - Новый билд с сохранением убитых ботов
             3 - Добавлена инф-ция о фонаре
             */
            dos.writeInt(levelNum + 1); //Level number

            Matrix pos = player.getCharacter().getTransform();
            dos.writeInt(pos.m03); //x
            dos.writeInt(pos.m13); //y
            dos.writeInt(pos.m23); //z
            player.updateMatrix();
            dos.writeInt((int) player.rotateY << 12);
            dos.writeInt((int) player.rotateX << 12);

            dos.writeInt(player.getCharacter().getSpeed().x); //X
            dos.writeInt(player.getCharacter().getSpeed().y); //Y
            dos.writeInt(player.getCharacter().getSpeed().z); //Z    

            dos.writeInt(player.money); //Money
            dos.writeInt(player.getHp()); //HP
            dos.writeInt((int) player.fov);
            dos.writeInt(player.stdFov);
            dos.writeInt(player.zoomFov);
            dos.writeBoolean(player.zoom);
            dos.writeBoolean(scene.getG3D().flashlightEnabled);

            int weapons = 0;
            int weapons2 = 0;
            Weapon[] weaps = player.arsenal.getWeapons();
            if(weaps != null) {
                weapons = weaps.length;
            }
            for(int i = 0; i < weapons; i++) {
                if(weaps[i] != null) {
                    weapons2++;
                }
            }

            System.out.println("write weapons count");
            dos.writeInt(weapons2); //Weapons count
            System.out.println("write weapon current");
            dos.writeInt(player.arsenal.current);
            for(int i = 0; i < weapons; i++) {

                Weapon weapon = weaps[i];
                if(weapon != null) {
                    dos.writeInt(i);
                    dos.writeShort(weapon.magazine.ammo);
                    dos.writeShort(weapon.magazine.rounds);
                }
            }

            System.out.println("write keys");

            if(Player.usedPoints != null) {
                int el = Player.usedPoints.size();
                dos.writeInt(el);
                for(int i = 0; i < el; i++) {
                    dos.writeUTF((String) (Player.usedPoints.elementAt(i)));
                }
            } else {
                dos.writeInt(0);
            }

            if(playerHasInventory) player.items.writeSave(dos); //Save inventory

            System.out.println("Game saved");

            byte[] data = baos.toByteArray();
            dos.close();
            baos.close();

            RmsUtils.removeStore(savname);
            RmsUtils.saveStore(savname, data);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static final void saveObjects(int levelNum, Player player, Scene scene) {
        if(!canSave) return;

        try {
            String savname = savename + "_lvl" + String.valueOf(levelNum);

            boolean saveBots = scene.rmsBots != null;
            if(saveBots) saveBots = !scene.rmsBots.isEmpty();
            Vector bags = scene.getItemBags(); //Save item bags

            if(!saveBots && bags.isEmpty() && scene.rmsObjects.isEmpty()) return;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            if(saveBots) { //save bots
                dos.writeInt(scene.rmsBots.size());

                for(int i = 0; i < scene.rmsBots.size(); i++) {
                    RoomObject obj = (RoomObject) scene.rmsBots.elementAt(i);
                    if(obj instanceof NPC) {
                        dos.writeBoolean(((NPC) obj).isDead());
                    } else if(obj instanceof NPCSpawner) {
                        dos.writeBoolean(((NPCSpawner) obj).deathCheck());
                    } else {
                        dos.writeBoolean(false);
                    }
                }

            } else {
                dos.writeInt(0);
            }

            dos.writeInt(bags.size()); //Item bags

            for(int i = 0; i < bags.size(); i++) {
                ItemsBag obj = (ItemsBag) bags.elementAt(i);
                obj.writeSave(dos);
            }

            dos.writeInt(scene.rmsObjects.size()); //Save rms objects

            for(int i = 0; i < scene.rmsObjects.size(); i++) {
                RoomObject obj = (RoomObject) scene.rmsObjects.elementAt(i);
                dos.writeBoolean(obj.activated);
            }

            System.out.println("Game saved");

            byte[] data = baos.toByteArray();
            dos.close();
            baos.close();

            RmsUtils.removeStore(savname);
            RmsUtils.saveStore(savname, data);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static final void loadGame(Player player, int w, int h, Scene scene) {
        if(!canSave) return;

        try {
            if(RmsUtils.hasStore(savename + "_Player")) {
                byte[] data = RmsUtils.openStore(savename + "_Player");
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                DataInputStream dis = new DataInputStream(bais);

                int version = dis.readInt(); //Level number / Version
                if(version < 0) {
                    dis.readInt();
                }

                if(version >= 0) {
                    dis.readInt(); //X 
                    dis.readInt(); //Y 
                    dis.readInt(); //Z

                    dis.readInt(); //X 
                    dis.readInt(); //Y 
                    dis.readInt(); //Z

                    dis.readInt(); //X 
                    dis.readInt(); //Y 
                    dis.readInt(); //Z

                    dis.readInt(); //X 
                    dis.readInt(); //Y 
                    dis.readInt(); //Z

                    dis.readInt(); //X 
                    dis.readInt(); //Y 
                    dis.readInt(); //Z
                } else if(version <= -1) {
                    dis.readInt(); //X 
                    dis.readInt(); //Y 
                    dis.readInt(); //Z

                    dis.readInt(); //rotateY
                    dis.readInt(); //rotateX

                    dis.readInt(); //X 
                    dis.readInt(); //Y 
                    dis.readInt(); //Z
                }

                player.money = dis.readInt();
                player.setHp(dis.readInt());

                player.fov = dis.readInt();
                player.stdFov = dis.readInt();
                player.zoomFov = dis.readInt();
                player.zoom = dis.readBoolean();
                if(version <= -3) scene.getG3D().flashlightEnabled = dis.readBoolean();

                int weapc = dis.readInt();
                int current = dis.readInt();
                player.arsenal.destroy();
                player.arsenal.current = current;
                Weapon[] weapons = new Weapon[Shop.weaponCount];

                for(int i = 0; i < weapc; i++) {
                    int tw = dis.readInt();
                    weapons[tw] = WeaponCreator.createWeapon(tw);
                    weapons[tw].createSprite(w, h * displaySize / 100);
                    weapons[tw].magazine.set(dis.readShort(), dis.readShort());
                }
                player.arsenal.weapons = weapons;
                Player.usedPoints.removeAllElements();

                int objs = dis.readInt();

                for(int i = 0; i < objs; i++) {
                    Player.usedPoints.addElement(dis.readUTF());
                }

                if(version <= -2) {
                    if(playerHasInventory) player.items.loadSave(dis);
                }

                dis.close();
                bais.close();

            }
        } catch (Exception e) {
            System.out.println("Error reading save file");
        }

    }

    public static final void loadObjects(Player player, int w, int h, Scene scene, int levelNumber) {
        if(!canSave) return;

        String savname = savename + "_lvl" + String.valueOf(levelNumber);
        try {
            if(RmsUtils.hasStore(savname)) {
                byte[] data = RmsUtils.openStore(savname);
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                DataInputStream dis = new DataInputStream(bais);

                int savedBots = dis.readInt();
                if(savedBots == 0) {
                    scene.rmsBotsKilled = null;
                } else {
                    scene.rmsBotsKilled = new boolean[savedBots];
                    for(int i = 0; i < savedBots; i++) {
                        scene.rmsBotsKilled[i] = dis.readBoolean();
                    }
                }

                int savedBags = dis.readInt();

                for(int i = 0; i < savedBags; i++) {
                    ItemsBag bag = new ItemsBag(new Vector3D(0, 0, 0));
                    bag.loadSave(dis);
                    scene.getHouse().addObject(bag);
                }

                int savedObjs = dis.readInt();
                if(savedObjs == 0) {
                    scene.rmsObjectsDestroyed = null;
                } else {
                    scene.rmsObjectsDestroyed = new boolean[savedObjs];
                    for(int i = 0; i < savedObjs; i++) {
                        scene.rmsObjectsDestroyed[i] = dis.readBoolean();
                    }
                }

                dis.close();
                bais.close();

            }
        } catch (Exception e) {
            System.out.println("Error reading objects file");
        }

    }

    public static final void loadPosition(Player player) {
        if(!canSave) return;

        try {
            if(RmsUtils.hasStore(savename + "_Player")) {
                byte[] data = RmsUtils.openStore(savename + "_Player");
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                DataInputStream dis = new DataInputStream(bais);

                int version = dis.readInt(); //Level number / Version
                if(version < 0) dis.readInt();

                if(version >= 0) {

                    player.getCharacter().getTransform().set(
                            dis.readInt() * 0, dis.readInt() * 0, dis.readInt() * 0,
                            dis.readInt(),
                            dis.readInt() * 0, dis.readInt() * 0, dis.readInt() * 0,
                            dis.readInt(),
                            dis.readInt() * 0, dis.readInt() * 0, dis.readInt() * 0,
                            dis.readInt());

                    player.getCharacter().getSpeed().set(dis.readInt(), dis.readInt(), dis.readInt());
                    player.updateMatrix();

                } else if(version <= -1) {

                    player.getCharacter().getTransform().setIdentity();
                    player.getCharacter().getTransform().setPosition(dis.readInt(), dis.readInt(), dis.readInt());

					player.rotYn(dis.readInt() / 4096.0F);
					player.rotXn(dis.readInt() / 4096.0F);
                    player.getCharacter().getSpeed().set(dis.readInt(), dis.readInt(), dis.readInt());
                    player.updateMatrix();

                }

                dis.close();
                bais.close();

            }
        } catch (Exception e) {
            System.out.println("Error reading player position save");
        }

    }

    public static final void loadLevel(boolean loadSave, boolean loadPos, int levelNumber, Object hudInfo, Main main, Menu menu) {
        loadLevel(loadSave, loadPos, levelNumber, hudInfo, main, menu, 1);

    }

    public static final void loadLevel(boolean loadSave, boolean loadPos, int levelNumber, Object hudInfo, Main main, Menu menu, int helpState) {
        try {
            System.gc();
            Thread.sleep(5L);

            if(!GameHelp.needToShow(levelNumber, helpState, false)) {
                if(menu != null) menu.destroy();
            
                LoadingScreen ls = new LoadingScreen(main, levelNumber, loadSave, loadPos);
                setCurrent(ls);

            } else {
                GameHelp gh = new GameHelp(main, menu, levelNumber, false, hudInfo, helpState);
                GameHelp.loadSave = loadSave;
                GameHelp.loadpos = loadPos;
                setCurrent(gh);
            }

        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    public static final void removeSave() {
        if(!canSave) return;

        RmsUtils.removeStore(savename + "_Player");

        for(int i = 0; i < lastLevel; i++) {
            RmsUtils.removeStore(savename + "_lvl" + String.valueOf(i));
        }

    }

    public static final int getContinueLevel() {
        if(!canSave) return 1;

        try {
            if(RmsUtils.hasStore(savename + "_Player")) {
                byte[] data = RmsUtils.openStore(savename + "_Player");

                ByteArrayInputStream baos = new ByteArrayInputStream(data);
                DataInputStream dis = new DataInputStream(baos);

                int lvl = dis.readInt();
                if(lvl < 0) lvl = dis.readInt();

                dis.close();
                baos.close();

                return lvl - 1;
            }
            return 1;
        } catch (Exception e) {
            System.out.println("Error getting continue level number");
            return 1;
        }

    }

    public static final boolean hasSave() {
        if(!canSave) return false;

        try {
            if(!RmsUtils.hasStore(savename + "_Player")) return false;

            byte[] var2 = RmsUtils.openStore(savename + "_Player");
            ByteArrayInputStream var5 = new ByteArrayInputStream(var2);
            DataInputStream var6 = new DataInputStream(var5);

            int lvl = var6.readInt();
            if(lvl < 0) lvl = var6.readInt();

            return lvl != 0;
        } catch (Exception e) {
            return false;
        }

    }

    public static void setPersQ(int pq) {
        persQ = pq;
        q = 12;
        if(persQ <= 2) q = 24;
    }
}
