package code.Gameplay;

import code.utils.QFPS;
import code.utils.StringTools;
import code.utils.IniFile;
import code.AI.*;
import code.Gameplay.Map.*;
import code.HUD.Base.Font;
import code.HUD.Base.GameKeyboard;
import code.HUD.DeveloperMenu;
import code.HUD.GUIScreen;
import code.HUD.GameHelp;
import code.HUD.Menu;
import code.HUD.PauseScreen;
import code.HUD.TitleScreen;
import code.Math.Matrix;
import code.Math.Vector3D;
import code.Rendering.Camera;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.Mesh;
import code.utils.*;
import code.utils.canvas.*;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class GameScreen extends MyCanvas {

    public final Main main;
    public final Font font;
    public Player player;
    public Scene scene;
    public GameIni levelIni;
    
    public DialogScreen dialogScreen;
    public WeatherGenerator wg;
    
    public static long time;
    public boolean doubleBright = false;

    public static Vector lines = new Vector();
    public static String mus = "/music.mid", levelFile = "/level_1.txt";
    public static int width, height; // Р’С‹СЃРѕС‚Р° СЌРєСЂР°РЅР°
    public static Vector delayDialogs = new Vector();
    public static boolean bloom = false;

    private boolean finishDraw = true;
    private Object lookAtObj = null;
    public long musTime = 0, usmem = 0;
    public Vector3D newPos;
    public final int levelNumber; // РќРѕРјРµСЂ СѓСЂРѕРІРЅСЏ
    private final Object hudInfo; //
    public int[] shopItems = null;
    private GameKeyboard keys;
    private int key; // РљРѕРґ РЅР°Р¶Р°С‚РѕР№ РєР»Р°РІРёС€Рё
    private int pointerX, pointerY; // y С‚РѕС‡РєРё РЅР°Р¶Р°С‚РёСЏ РЅР° СЌРєСЂР°РЅ
    private int dirX, dirY; // y РІРµРєС‚РѕСЂР°, РІ РЅР°РїСЂР°РІР»РµРЅРёРё РєРѕС‚РѕСЂРѕРіРѕ РїСЂРѕРІРµР»Рё РїР°Р»СЊС†РµРј РїРѕ СЌРєСЂР°РЅСѓ (dirY=y2-y1)
    public boolean run, paused;
    private int msToEnd = 0, msToExit = 0;
    private int lastHP, lastAmmo, lastMoney, lastFrags; // СЃС‡РµС‚С‡РёРє С„СЂР°РіРѕРІ
    private Image imgSight, imgLife, imgPatron, imgMoney, imgSkull, 
            imgHand, imgLifeLow, imgPatronLow, imgPatronNoWeapon;
    public String customMessage = null;
    public boolean customMessagePause = false;
    public long customMessageEndTime = 0L;
    public Image overlay;
    public long overlayStart = 0L, overlayTimeOut = 0L;

    public byte[] vignette;
    public boolean changed = true, firstUpdate = true, fullMoveLvl = false;

    /*public Camera camFollow = null;
    public Bot botFollow = null;*/

    public GameScreen(Main main, int levelNumber, Object hudInfo) {
        /*if(camFollow != null) {
            camFollow.x = 200;
            camFollow.y = 1600;
            camFollow.z = 300;
            camFollow.angle = 0;
        }*/
        
        levelFile = "/level" + levelNumber + ".txt";
        this.levelNumber = levelNumber;
        this.main = main;
        this.hudInfo = hudInfo;
        font = Main.getFont();

        width = getWidth();
        height = getHeight();
        vignette = null;
        finishDraw = true;
        lookAtObj = null;
        delayDialogs.removeAllElements();
        musTime = 0;
        bloom = false;
        keys = new GameKeyboard();
        
        try {
            imgLife = createImage2(Main.getGameText().getDef(Main.life_icon, Main.life_icon));
            imgPatron = createImage2(Main.getGameText().getDef(Main.patron_icon, Main.patron_icon));
            imgMoney = createImage2(Main.getGameText().getDef(Main.money_icon, Main.money_icon));
            imgPatronNoWeapon = createImage2(Main.getGameText().getDef(
                    Main.patron_no_weapon_icon, Main.patron_no_weapon_icon));

            if(Main.low_life_icon != null) imgLifeLow = createImage2(Main.getGameText().getDef(Main.low_life_icon, Main.low_life_icon));
            if(Main.low_patron_icon != null) imgPatronLow = createImage2(Main.getGameText().getDef(Main.low_patron_icon, Main.low_patron_icon));

            imgSkull = createImage2(Main.getGameText().getDef(Main.skull_icon, Main.skull_icon));

            try {
                scene = LevelLoader.createScene(width, height * Main.getDisplaySize() / 100, levelFile, main, this);
            } catch(Exception ex) {
                ex.printStackTrace();
            }
            
            String path = Main.getGameText().getDef(Main.sight_icon, Main.sight_icon);

            if(!Main.originalSight) {
                if(!Main.fullScreenSight) imgSight = createImage2(path);
                else imgSight = ImageResize.createImage(path, width, height * Main.displaySize / 100);
            } else {
                imgSight = Image.createImage(path);
            }

            path = Main.getGameText().getDef(Main.hand_icon, Main.hand_icon);

            if(Main.originalUseIcon) imgHand = Image.createImage(path);
            else imgHand = createImage2(path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        player = new Player(scene.getG3D().getWidth(), (int) (height * (Main.getDisplaySize() / 100.0F)), scene.getStartPoint(), hudInfo, scene.camPose);
        if(!Main.isLastLevel(levelNumber)) {
            String st = (new IniFile(StringTools.getStringFromResource("/level" + (levelNumber+1) + ".txt"), false)).get("START");

            if(st != null) {
                int[] ps = GameIni.createPos(st, ',');
                newPos = new Vector3D(ps[0], ps[1], ps[2]);
            } else {
                newPos = new Vector3D(0, 0, 0);
            }
        }
        Player.usedPoints.removeAllElements();
        LevelLoader.loadObjects(levelFile, levelIni, scene, player, false);
        scene.removeKilledBots();

        scene.getHouse().addObject(player);
        dialogScreen = new DialogScreen(font, scene.getG3D(), main, this);
        boolean hasPlayer = Main.musicPlayer.hasPlayer();
        init();
        if(!hasPlayer) startMus(0);
        changed = true;
        time = 0;
    }

    private Image createImage2(String file) {
        if(width < height) {
            return ImageResize.createImage(file, width / 240f, width / 240f);
        } else {
            return ImageResize.createImage(file, height / 320f, height / 320f);
        }

    }

    private void drawMessageSimple(Graphics g, String str) {
        if(str == null) return;

        DirectX7 g3d = scene.getG3D();
        int y = GameScreen.height / 2;

        Main.drawBck(g, y - font.height() / 2, y + font.height() / 2);
        font.drawString(g, str, g3d.getWidth() / 2, y, 3);
    }

    public void destroy() {
        destroy(true);
    }

    public void destroy(boolean destroyMus) {
        try {
            newPos = null;
            lookAtObj = null;
            delayDialogs.removeAllElements();
            scene.destroy();
            scene = null;
            Asset.clear();
            player.destroy();
            player = null;
            Zombie.model = BigZombie.model = null;
            Zombie.texture = BigZombie.texture = null;
            dialogScreen = null;
            imgSight = imgLife = imgHand = imgPatron = imgPatronLow = imgMoney = imgSkull = imgPatronNoWeapon = null;
            if(destroyMus) destroyMusic();
            Mesh.resetBuffer();
            System.gc();
            time = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawMessage(Graphics g, String str) {
        DirectX7 g3d = scene.getG3D();
        int y = height / 2 + (height * Math.min(Main.getDisplaySize(), 80) / 100) / 2 - font.height() / 2;

        Main.drawBck(g, y - font.height() / 2, y + font.height() / 2);
        font.drawString(g, str, g3d.getWidth() / 2, y, 1 | 2);
    }

    private void drawMessageCustom(Graphics g) {
        DirectX7 g3d = scene.getG3D();
        int lsize = lines.size();
        
        int x = g3d.getWidth() / 2;
        int y = height / 2 + (height * Math.min(Main.getDisplaySize(), 80) / 100) / 2 
                - font.height() / 2 - font.height() * (lsize - 1);

        Main.drawBck(g, y - font.height() / 2, y + lsize * font.height() - font.height() / 2);
        for(int i=0; i<lsize; i++) {
            String str = (String) lines.elementAt(i);
            font.drawString(g, str, x, y + i * font.height(), 1 | 2);
        }

    }

    private void applyVignette(int[] scr) {
        int size = Math.min(scr.length, vignette.length);
        int i = 0;
        int d, c;
        while(size - i >= 8) {
            d = vignette[i] + 128;
            c = scr[i];
            scr[i] = ((((c & 0xff00ff) * d) & 0xff00ff00) | (((c & 0xff00) * d) & 0xff0000)) >>> 8;
            c = scr[i+1];
            scr[i+1] = ((((c & 0xff00ff) * d) & 0xff00ff00) | (((c & 0xff00) * d) & 0xff0000)) >>> 8;
            d = vignette[i+2] + 128;
            c = scr[i+2];
            scr[i+2] = ((((c & 0xff00ff) * d) & 0xff00ff00) | (((c & 0xff00) * d) & 0xff0000)) >>> 8;
            c = scr[i+3];
            scr[i+3] = ((((c & 0xff00ff) * d) & 0xff00ff00) | (((c & 0xff00) * d) & 0xff0000)) >>> 8;
            d = vignette[i+4] + 128;
            c = scr[i+4];
            scr[i+4] = ((((c & 0xff00ff) * d) & 0xff00ff00) | (((c & 0xff00) * d) & 0xff0000)) >>> 8;
            c = scr[i+5];
            scr[i+5] = ((((c & 0xff00ff) * d) & 0xff00ff00) | (((c & 0xff00) * d) & 0xff0000)) >>> 8;
            d = vignette[i + 6] + 128;
            c = scr[i+6];
            scr[i+6] = ((((c & 0xff00ff) * d) & 0xff00ff00) | (((c & 0xff00) * d) & 0xff0000)) >>> 8;
            c = scr[i+7];
            scr[i+7] = ((((c & 0xff00ff) * d) & 0xff00ff00) | (((c & 0xff00) * d) & 0xff0000)) >>> 8;
            i += 8;
        }
        
        while(i < size) {
            d = vignette[i] + 128;
            c = scr[i];
            scr[i] = ((((c & 0xff00ff) * d) & 0xff00ff00) | (((c & 0xff00) * d) & 0xff0000)) >>> 8;
            i++;
        }
    }

    public final void applySub(int[] scr) {
        int l = 0xff;//(int)((time/20)%511);
        int i = scr.length - 1;

        while(i >= 5) {
            int col = scr[i];
            col = ((((col & 0xFF00FF) * l) & 0xFE00FE00)
                    | (((col & 0xFF00) * l) & 0xFE0000)) >>> 7;
            scr[i] = col | ((col >> 8) & 0x010101) * 0xff; // Лимитер
            col = scr[i - 1];
            col = ((((col & 0xFF00FF) * l) & 0xFE00FE00)
                    | (((col & 0xFF00) * l) & 0xFE0000)) >>> 7;
            scr[i - 1] = col | ((col >> 8) & 0x010101) * 0xff; // Лимитер
            col = scr[i - 2];
            col = ((((col & 0xFF00FF) * l) & 0xFE00FE00)
                    | (((col & 0xFF00) * l) & 0xFE0000)) >>> 7;
            scr[i - 2] = col | ((col >> 8) & 0x010101) * 0xff; // Лимитер
            col = scr[i - 3];
            col = ((((col & 0xFF00FF) * l) & 0xFE00FE00)
                    | (((col & 0xFF00) * l) & 0xFE0000)) >>> 7;
            scr[i - 3] = col | ((col >> 8) & 0x010101) * 0xff; // Лимитер
            col = scr[i - 4];
            col = ((((col & 0xFF00FF) * l) & 0xFE00FE00)
                    | (((col & 0xFF00) * l) & 0xFE0000)) >>> 7;
            scr[i - 4] = col | ((col >> 8) & 0x010101) * 0xff; // Лимитер
            col = scr[i - 5];
            col = ((((col & 0xFF00FF) * l) & 0xFE00FE00)
                    | (((col & 0xFF00) * l) & 0xFE0000)) >>> 7;
            scr[i - 5] = col | ((col >> 8) & 0x010101) * 0xff; // Лимитер
            i -= 6;
        }

        while(i >= 0) {
            int col = scr[i];
            col = ((((col & 0xFF00FF) * l) & 0xFE00FE00)
                    | (((col & 0xFF00) * l) & 0xFE0000)) >>> 7;
            scr[i] = col | ((col >> 8) & 0x010101) * 0xff;
            i--;
        }
    }

    public final void paint(Graphics g) {
        if(paused || !run || !finishDraw) return;
        finishDraw = false;
        
        DirectX7 g3d = scene.getG3D();
        
        boolean redrawInfo = changed;
        if(!redrawInfo) g.setClip(0, height / 2 - g3d.getHeight() / 2, width, g3d.getHeight());
        changed = false;

        long begintime = System.currentTimeMillis();

        if(MainCanvas.pstros && (
                MainCanvas.mouseX != MainCanvas.emulatorScreenWidth / 2 ||
                MainCanvas.mouseY != MainCanvas.emulatorScreenHeight / 2
                )) {

            float mouseSpeed = (!player.zoom ? 0.4f : 0.175f) * Main.mouseSpeed / 50;
            float xRot = (MainCanvas.mouseX - MainCanvas.emulatorScreenWidth / 2) * mouseSpeed;
            float yRot = (MainCanvas.mouseY - MainCanvas.emulatorScreenHeight / 2) * mouseSpeed;
            if(player.canLookY()) player.rotYn(-xRot);
            if(player.canLookX()) player.rotXn(-yRot);
            
            if(wg != null) {
                if(player.canLookY()) wg.moveY((int) -yRot, QFPS.frameTime);
                if(player.canLookX()) wg.moveX2((int) -xRot, QFPS.frameTime);
            }
        }

        g.setClip(0, 0, getWidth(), getHeight());
        g.translate(0, 0);
        
        boolean playerDied = player.isDead();
        int playerHeight = player.getEyesHeight();

        Matrix playerMat = player.getCharacter().getTransform();
        int renderFrom;
        player.setCamera(g3d);
        renderFrom = player.getRenderPart(scene);

        if(wg != null) wg.update(QFPS.frameTime, -(g3d.camera.m12 * 90 / Matrix.FP), player.character.underRoof);
        /*if(camFollow != null) {
            if(botFollow != null && botFollow.isDead()) botFollow = null;
            if(botFollow == null) {
                Vector objects = scene.getHouse().getObjects();
                for(int i = 0; i < objects.size(); i++) {
                    if(objects.elementAt(i) instanceof NPC) {
                        Bot bt = (Bot) objects.elementAt(i);
                        if(!bt.isDead() && bt.observable != null) {
                            botFollow = bt;
                            break;
                        }
                    }
                }
            }
            if(botFollow != null && player.character.getSpeed().x == 0 && player.character.getSpeed().z == 0) {
                camFollow.x = botFollow.observable[0];
                camFollow.y = botFollow.observable[1];
                camFollow.z = botFollow.observable[2];
                camFollow.set(botFollow.character.transform);
                camFollow.getCamera().rotY(180);
                camFollow.setPart(botFollow.getPart());
                g3d.setCamera(camFollow.getCamera());
            } else {
                camFollow.setPart(player.getPart());
            }
        }*/
        int partsCount = 1;
        Weapon weapon = null;
        if(player != null && player.arsenal != null) weapon = player.arsenal.currentWeapon();
        try {
            if(weapon != null) {
                if(weapon.hasZoom == false) player.zoom = false;
                player.stdFov = weapon.stdFov;
                player.zoomFov = weapon.zoomFov;
            } else {
                if(!Main.hasZoom) {
                    if(player.zoom) player.zoom = false;
                } else {
                    player.stdFov = Main.stdFov;
                    player.zoomFov = Main.zoomFov;
                }
            }
            
            if((player.zoom && player.fov != player.zoomFov) || (player.fov != player.stdFov && !player.zoom)) {
                int zoomStep = player.zoom? player.zoomFov - player.stdFov : player.stdFov - player.zoomFov;
                player.fov += (QFPS.currentTime - player.lastZoomAction) * zoomStep / 150;
                
                int max = Math.max(player.zoomFov, player.stdFov);
                int min = Math.min(player.zoomFov, player.stdFov);
                
                if(player.fov > max) player.fov = max;
                else if(player.fov < min) player.fov = min;

                scene.getG3D().updateFov((int) player.fov);
            }
            player.lastZoomAction = QFPS.currentTime;

            boolean lighting = false;
            if(wg != null && wg.lighting) lighting = (System.currentTimeMillis() - wg.lastLighting) < 135;

            Skybox sky = scene.getHouse().getSkybox();
            if(sky != null) {
                if(lighting) {
                    sky.lighting = true;
                    sky.lastLighting = wg.lastLighting;
                }
				
				Camera cam = player.getCamera();
				if(cam != null) {
					sky.rotateX = cam.currentRotX;
					sky.rotateY = cam.currentRotY;
				} else {
					sky.rotateX = player.rotateX;
					sky.rotateY = player.rotateY;
				}
            }
            
            if(!House.l2dRoomRendering) {
                int[]partsMass = House.getParts(scene.getHouse(), renderFrom, g3d.camera);
                partsCount = partsMass.length;
                scene.render(g, partsMass, player);
            } else scene.render(g, renderFrom, player);

            g3d.render();
            g3d.shootIntensity -= QFPS.frameTime;
            if(g3d.shootIntensity < 0) g3d.shootIntensity = 0;
            
            if(lighting && !player.character.underRoof && wg != null) {
				Camera cam = player.getCamera();
				wg.lightingEffect(g3d, cam != null ? cam.currentRotX : player.rotateX);
			}

            if(vignette != null) applyVignette(g3d.getDisplay()); //maybe i should remove all these vignette stuff?
        } catch (Exception ext) {
            if(DeveloperMenu.debugMode) ext.printStackTrace();
        }
        
        int screenY = (height - g3d.getHeight()) / 2;
        try {
            if(playerDied) deathScr(g3d.getDisplay());
            if(player.isDamaged()) damageScr(g3d.getDisplay());
            if(bloom) fastbloomScr(g3d);
            if(doubleBright) applySub(scene.getG3D().display);
        } catch (Exception ext) {
            if(DeveloperMenu.debugMode) ext.printStackTrace();
        }
        
        try {
            scene.flush(g, 0, screenY);
        } catch (Exception ext) {
            if(DeveloperMenu.debugMode) ext.printStackTrace();
        }

        if(DeveloperMenu.drawPortals) scene.house.drawPortals(g, 0, screenY);

        if(wg != null) {
            g.clipRect(0, screenY, g3d.getWidth(), g3d.getHeight());
            wg.paint(g, screenY);
            g.clipRect(0, 0, width, height);
            if(!redrawInfo) g.setClip(0, screenY, width, g3d.getHeight());
        }

        if(overlay != null) {
            if(System.currentTimeMillis() - overlayStart < overlayTimeOut) {
                g.drawImage(overlay, getWidth() / 2, getHeight() / 2, 3);
            } else overlay = null;
        }

        if(!Main.hideHud) {

            if(!playerDied && player.arsenal.current != -1) {
                g.setClip(0, screenY, g3d.getWidth(), g3d.getHeight());
                
                if(player.show2D()) player.arsenal.drawWeapon(g, screenY, g3d.getWidth(), g3d.getHeight(), this);
                player.arsenal.drawReloadAnimation(g, screenY, g3d.getWidth(), g3d.getHeight());
                
                g.setClip(0, 0, width, height);
                if(!redrawInfo) g.setClip(0, screenY, width, g3d.getHeight());
            }
            
            if((weapon != null || !Main.hidesight) && 
                    ((weapon != null && !player.zoom) || weapon == null || (player.canAttack() && !player.show2D()))
                    ) {
                if(imgHand == null || lookAtObj == null || !MainCanvas.pstros) {
                    g.drawImage(imgSight, g3d.getWidth() / 2, screenY + g3d.getHeight() / 2, 3);
                }
            }
            
            boolean lowAmmo, lowHP;
            lowAmmo = lowHP = false;
            
			String infoText = null;
			
            if(playerDied) {
				infoText = Main.getGameText().get("GAME_OVER");
            } else if(msToEnd > 0) {

				if(!scene.exitWithoutWait) {
					infoText = Main.getGameText().get(Main.isLastLevel(levelNumber) ? "GAME_COMPLETE" : "LEVEL_COMPLETE");
				}

            } else if(msToExit > 0 && msToExit < 3000) {
                infoText = Main.getGameText().get("FIND_EXIT");
            }
            
			if(infoText != null) drawMessageSimple(g, infoText);
            
            if(player.getHp() <= 15 && (customMessage == null || imgLifeLow != null)) {
                lowHP = true; changed = true; redrawInfo = true;
            }
            
            if(weapon != null && weapon.patronbuy && weapon.getAmmo() <= weapon.lowPatronAmount
                    && (customMessage == null || imgPatronLow != null)) {
                lowAmmo = true; changed = true; redrawInfo = true;
            }
            
            if(QFPS.frames == 0 && DeveloperMenu.debugMode) {
                usmem = Runtime.getRuntime().totalMemory() / 1024 - Runtime.getRuntime().freeMemory() / 1024;
            }

            int screenH4Icons;
            if(Main.getDisplaySize() <= 80) {
                screenH4Icons = (int) (height * (Main.getDisplaySize() / 100F));
            } else screenH4Icons = height * 8 / 10;

            int iconY = (height - screenH4Icons) / 4;
            int iconYDown = height - iconY;
            
            if(redrawInfo || Main.getDisplaySize() > 80) {
                g.setClip(0, 0, width, height);
                g.setColor(0);
                
                if(Main.getDisplaySize() < 100) {
                    g.fillRect(0, 0, width, screenY);
                    g.fillRect(0, screenY + g3d.getHeight(), width, height - (screenY + g3d.getHeight()));
                }
                
                if(imgMoney != null) {
                    g.drawImage(imgMoney, 4, iconY, 6);
                    font.drawString(g, " " + player.money, 4 + imgMoney.getWidth(), iconY, 6);
                }

                if(!scene.alwaysExit && imgSkull != null) {
                    g.drawImage(imgSkull, width - 4, iconY, 10);
                    font.drawString(g, player.frags + "/" + scene.getEnemyCount(), width - 4 - imgSkull.getWidth(), iconY, 10);
                }
                
                if(imgLife != null) {
                    if(imgHand != null && lookAtObj != null && !MainCanvas.pstros) {
                        if(Main.displaySize > 80) g.drawImage(imgHand, 4, iconYDown - imgLife.getHeight(), 6);
                        else g.drawImage(imgHand, 4, (height + (height * Main.displaySize / 100)) / 2 - imgHand.getHeight() - 4, 4 | 16);
                    }
                    
                    g.drawImage((lowHP && imgLifeLow != null) ? imgLifeLow : imgLife, 4, iconYDown, 6);
                    font.drawString(g, " " + player.getHp(), imgLife.getWidth(), iconYDown, 6);
                } else {
                    if(imgHand != null && lookAtObj != null && !MainCanvas.pstros) {
                        g.drawImage(imgHand, 4, iconYDown, 6);
                    }
                }
                
                Image imgPatron2 = weapon == null ? imgPatronNoWeapon : imgPatron;
                Image imgPatronLow2 = imgPatronLow;
                if(weapon != null) {
                    if(weapon.imgPatron == null && weapon.filePatron != null) 
                        weapon.imgPatron = createImage2(weapon.filePatron);
                    if(weapon.imgPatronLow == null && weapon.filePatronLow != null) 
                        weapon.imgPatron = createImage2(weapon.filePatron);
                    
                    if(weapon.imgPatron != null) imgPatron2 = weapon.imgPatron;
                    if(weapon.imgPatronLow != null) imgPatronLow2 = weapon.imgPatronLow;
                }
                
                if(imgPatron2 != null) {
                    g.drawImage((lowAmmo && imgPatronLow2 != null) ? 
                            imgPatronLow2 : imgPatron2, width - 4, iconYDown, 10);
                    
                    if(weapon != null && weapon.patronbuy) 
                        font.drawString(g, weapon.getRounds() + "/" + weapon.getAmmo() + " ", 
                            width - imgPatron2.getWidth(), iconYDown, 10);
                }
                
				
                if(imgLifeLow == null && lowHP && scene.getFrame() / 8 % 2 == 0) {
					String text = Main.getGameText().get("BUY_MEDICINE_CHEST");
                    drawMessage(g, text);
				}
                
                if(imgPatronLow2 == null && lowAmmo && scene.getFrame() / 8 % 2 == 0) {
					String text = Main.getGameText().get("BUY_PATRONS");
                    drawMessage(g, text);
				}
            }
            
            if(imgHand != null && lookAtObj != null && MainCanvas.pstros) {
                g.drawImage(imgHand, width / 2, height / 2, Graphics.VCENTER | Graphics.HCENTER);
            }
            
            Object tmps = scene.findObject(true, player, screenY, this);
            
            if(tmps != lookAtObj || tmps != null) {
                changed = true;
                lookAtObj = tmps;
            }
            
            if(!paused && DeveloperMenu.debugMode) {
                int moneyWidth = 0;
                if(imgMoney != null) moneyWidth = imgMoney.getWidth();
                
                if(DeveloperMenu.showFps) font.drawString(g, ":" + QFPS.fps + ":", 44 + moneyWidth, iconY, 6);
                //this.font.drawString(g, ":" + this.player.getCharacter().getTransform().getRotZ() + ":", 74 + this.imgMoney.getWidth(), (GameScreen.height - icohei) / 4, 6);
                
                if(DeveloperMenu.showRoomID) {
                    font.drawString(g, ":" + partsCount + ":", 94 + moneyWidth, iconY, 6);
                    font.drawString(g, ":" + player.getPart() + ":", 114 + moneyWidth, iconY, 6);
                }
                
                if(DeveloperMenu.showRam) font.drawString(g, ":" + Long.toString(usmem) + ":", 144 + moneyWidth, iconY, 6);

                if(DeveloperMenu.showPlayerPos) {
                    String crds = playerMat.m03 + ", " + playerMat.m13 + ", " + playerMat.m23 + ";";
                    font.drawString(g, crds, width / 2, iconY + font.height() * 2, 1 | 2);
                }

                changed = true;
            }

            if(customMessage != null) {
                drawMessageCustom(g);
                changed = true;

                if(customMessagePause) {
                    try {
                        Thread.sleep(customMessageEndTime);
                        QFPS.miniReset();
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                    
                    customMessagePause = false;
                    customMessage = null;
                }
                
                if(System.currentTimeMillis() > customMessageEndTime) {
                    customMessage = null;
                }

            }
            
            if(DeveloperMenu.drawLevelPoints && DeveloperMenu.debugMode) scene.drawPoints(g, screenY, player);
        }
        
        run(begintime);
        finishDraw = true;
        QFPS.frame();
        repaint();
        //this.serviceRepaints();
    }

    protected final void pointerPressed(int x, int y) {
        if(paused) return;
        if(MainCanvas.pstros) {
            keys.keyPressed(keys.FIRE);
            return;
        }

        this.pointerX = x;
        this.pointerY = y;

        if(GUIScreen.isLeftSoft(x, y, getWidth(), getHeight()))
            keyPressed(keys.SOFT_LEFT);
        else if(GUIScreen.isRightSoft(x, y, getWidth(), getHeight()))
            keyPressed(keys.SOFT_RIGHT);

    }

    protected final void mouseScrollUp() {
        player.nextWeapon();
    }

    protected final void mouseScrollDown() {
        player.previousWeapon();
    }
    
    private void fastbloomScr(DirectX7 g3d) {
        int scr[] = g3d.getDisplay();

        int rgb2[] = new int[g3d.getHeight() / 10 * g3d.getWidth() / 10];
        ImageResize.bilinearResizeRGBnA_toLow(scr, rgb2, g3d.getWidth(), g3d.getHeight(), g3d.getWidth() / 10, g3d.getHeight() / 10);
        for(int i = 0; i < rgb2.length; i++) {
            int col = rgb2[i];
            rgb2[i] = ((((col & 0xFF00FF) * (127) + (0xff000000 & 0xFF00FF) * 128) & 0xFF00FF00) | (((col & 0x00FF00) * (127) + (0xff000000 & 0x00FF00) * 128) & 0x00FF0000)) >>> 8;
        }

        int rgb3[] = new int[scr.length];
        ImageResize.bilinearResizeRGBnA(rgb2, rgb3, g3d.getWidth() / 10, g3d.getHeight() / 10, g3d.getWidth(), g3d.getHeight());
        rgb2 = null;

        for(int i = 0; i < scr.length; i++) {
            int col = (scr[i] & 0xFEFEFE) + (rgb3[i] & 0xFEFEFE);  //add color
            scr[i] = col | (((col >> 8) & 0x010101) * 0xFF);  //clamp color to 0 - 255
        }
        rgb3 = null;
    }
    
    private void damageScr(int[] scr) {
        for(int i = 0; i < scr.length; i++) {
            scr[i] = scr[i] & 0xffff0000;
        }
    }

    private void deathScr(int[] scr) {
        for(int i = 0; i < scr.length; ++i) {
            int col = scr[i];
            int r = (col) >> 16 & 0xff;
            int g = col >> 8 & 0xff;
            col &= 255;
            col = (r + g + col) / 3;
            scr[i] = col << 16 | col << 8 | col;
        }
    }

    protected final void pointerDragged(int x, int y) {
        if(paused || MainCanvas.pstros) return;
        dirX += (x - pointerX) * 200;
        dirY += (y - pointerY) * 200;
        pointerX = x;
        pointerY = y;
    }

    protected final void pointerReleased(int x, int y) {
        if(paused) return;
        if(MainCanvas.pstros) {
            keys.keyReleased(keys.FIRE);
            return;
        }

        pointerX = x;
        pointerY = y;
        dirX = dirY = 0;
    }

    protected final void keyPressed(int key) {
        if(paused || firstUpdate || player == null) return;

        this.key = key;
        keys.keyPressed(key);

        if(paused) {
            QFPS.miniReset();
        } else if(GameKeyboard.isSightKey(key)) {
            player.zoom = !player.zoom;
            player.lastZoomAction = System.currentTimeMillis();
            return;
        } else if(key == keys.SOFT_RIGHT) {
            Weapon weapon = player.arsenal.currentWeapon();

            if(!player.isDead() && !player.canAttackNoSight() && player.canAttackSight()
                    && weapon != null && weapon.hasZoom && player.zoom) {
                player.zoom = false;
                player.lastZoomAction = System.currentTimeMillis();
            } else openPause();
        }

        if(GameKeyboard.isUseKey(key) && !player.isDead()) {
            Matrix var4 = player.getCharacter().getTransform();
            scene.getG3D().lx = var4.m03;
            scene.getG3D().ly = var4.m13;
            scene.getG3D().lz = var4.m23;
            System.out.println(var4.m03 + "," + var4.m13 + "," + var4.m23 + ";");

            RoomObject ro = scene.activateObject(true, player, height / 2 - scene.getG3D().getHeight() / 2, this);
            if(ro != null) return;
        }

        if(GameKeyboard.isInventoryKey(key) && !player.isDead()) {
            if(Main.playerHasInventory) {
                openInventory();
            } else if(shopItems != null) {
                openShop(shopItems, null, null);
            }
        }

    }

    protected final void keyReleased(int key) {
        if(paused || firstUpdate || player == null) return;
        this.key = 0;
        keys.keyReleased(key);
    }

    public final void run(long frameStart) {
        if(paused || player == null) return;
        if(firstUpdate) {
            firstUpdate = false;
            return;
        }

        if(run) {
            scene.activateObject(false, player, height / 2 - scene.getG3D().getHeight() / 2, this);
            time += QFPS.frameTime;
            
            try {
                code.Gameplay.Map.Character playeChar = player.getCharacter();
                DirectX7 g3d = scene.getG3D();
                
                if(!playeChar.onFloor && wg != null) {
                    wg.moveY2(playeChar.getSpeed().y, QFPS.frameTime);
                }
                
                if(!player.isDead()) {
					int forward = 0, right = 0;
					
                    if(keys.isWalkForward(player.zoom)) {
                        forward += 1;
                        if(player.canWalk() &&  wg != null && !playeChar.underRoof) {
                            wg.move(5, QFPS.frameTime, -(g3d.camera.m12 * 90 / Matrix.FP));
                        }
                    }

                    if(keys.isWalkBackward(player.zoom)) {
                        forward -= 1;
                        if(player.canWalk() &&  wg != null && !playeChar.underRoof) {
                            wg.moveB(5, QFPS.frameTime, -(g3d.camera.m12 * 90 / Matrix.FP));
                        }
                    }

                    if((player.isSwapStrafeLook() && keys.isWalkLeft(player.zoom)) || (!player.isSwapStrafeLook() && keys.isLookLeft(player.zoom))) {
                        player.rotLeft();
                        if(player.canLookY() && wg != null) wg.moveX2((!player.zoom ? 5 : 1) * Main.mouseSpeed / 50, QFPS.frameTime);
                    }

                    if((player.isSwapStrafeLook() && keys.isWalkRight(player.zoom)) || (!player.isSwapStrafeLook() && keys.isLookRight(player.zoom))) {
                        player.rotRight();
                        if(player.canLookY() && wg != null) wg.moveX2(-(!player.zoom ? 5 : 1) * Main.mouseSpeed / 50, QFPS.frameTime);
                    }

                    if((!player.isSwapStrafeLook() && keys.isWalkLeft(player.zoom)) || (player.isSwapStrafeLook() && keys.isLookLeft(player.zoom))) {
                        right -= 1;
                        if(player.canWalk() && wg != null) wg.moveX(5, QFPS.frameTime);
                    }

                    if((!player.isSwapStrafeLook() && keys.isWalkRight(player.zoom)) || (player.isSwapStrafeLook() && keys.isLookRight(player.zoom))) {
                        right += 1;
                        if(player.canWalk() && wg != null) wg.moveX(-5, QFPS.frameTime);
                    }
					
					if(right != 0 || forward != 0) player.walk(right, forward);
                    
                    Weapon weapon = player.arsenal.currentWeapon();

                    if(keys.isPlayerShooting()) {
                        if(!player.canAttackNoSight() && player.canAttackSight() && 
                                weapon != null && weapon.hasZoom && !player.zoom) {
                            player.zoom = true;
                            player.lastZoomAction = System.currentTimeMillis();
                            keys.releasePlayerShoot();
                        } else player.fire(g3d);
                    }

                    if(keys.isLookDown(player.zoom)) {
                        player.rotDown();
                        if(player.canLookX() && wg != null) wg.moveY(-(!player.zoom ? 4 : 3) * Main.mouseSpeed / 50, QFPS.frameTime);
                    }

                    if(keys.isLookUp(player.zoom)) {
                        player.rotUp();
                        if(player.canLookX() && wg != null) wg.moveY((!player.zoom ? 4 : 3) * Main.mouseSpeed / 50, QFPS.frameTime);
                    }

                    if(GameKeyboard.isJumpKey(key)) player.jump();

                    if(GameKeyboard.isNextWeaponKey(key)) {
                        key = 0;
                        player.nextWeapon();
                    }

                    if(GameKeyboard.isPreviousWeaponKey(key)) {
                        key = 0;
                        player.previousWeapon();
                    }

                    if(dirX != 0 || dirY != 0) {
                        if(dirY != 0) {
                            int rotSpeed = dirY * -(!player.zoom ? 2 : 1) * Main.mouseSpeed / 50 / height;
                            player.rotXn(rotSpeed);
                            if(wg != null) wg.moveY(rotSpeed, QFPS.frameTime);
                            
                            if(dirY == 1) dirY = 0;
                            dirY = dirY / 2;
                        }

                        if(dirX != 0) {
                            int rotSpeed = dirX * -(!player.zoom ? 4 : 2) * Main.mouseSpeed / 50 / height;
                            player.rotYn(rotSpeed);
                            if(wg != null) wg.moveX2(rotSpeed, QFPS.frameTime);
                            
                            if(dirX == 1) dirX = 0;
                            dirX = dirX / 2;
                        }
                    }

                }

                if(player.isTimeToRenew()) {
                    paused = true;
                    restartGame();
                    return;
                }
                
                if(!delayDialogs.isEmpty()) {
                    for(int i = 0; i < delayDialogs.size(); i++) {
                        
                        if(((DelayedDialog) delayDialogs.elementAt(i)).update(this)) { 
                            delayDialogs.removeElementAt(i); //dialog was activated
                        }
                    }
                }

                scene.update(player, this);
                if(scene.getFrame() % 2 == 0) {
                    if(msToEnd == 0 && scene.isLevelCompleted(player)) msToEnd = 1;

                    if(msToExit == 0 && scene.isWinner(player)) msToExit = 1;
                }

                if(msToEnd > 0) {
                    msToEnd += QFPS.frameTime;
                    if(scene.exitWithoutWait) msToEnd = Integer.MAX_VALUE;
                }

                if(msToExit > 0) {
                    msToExit += QFPS.frameTime;
                    if(scene.exitWithoutWait) msToExit = Integer.MAX_VALUE;
                }

                if(msToEnd > 3000) {
                    changed = true;
                    
                    if(!Main.isLastLevel(levelNumber)) {
                        if(scene.getFinishPoint() != null) {
                            loadLevel(levelNumber + 1, scene.getFinishPoint(), true, fullMoveLvl);
                        }
                    } else gameEnd();
                    
                    return;
                }

                if(!changed) {
                    Weapon weapon = player.arsenal.currentWeapon();
                    changed = player.getHp() != lastHP || player.money != lastMoney || player.frags != lastFrags;
                    
                    if(weapon != null) changed |= weapon.getRounds() != lastAmmo;
                    
                    if(changed) {
                        lastHP = player.getHp();
                        if(weapon != null) lastAmmo = weapon.getRounds();
                        lastMoney = player.money;
                        lastFrags = player.frags;
                    }
                }

                long sleepTime = 15 - (System.currentTimeMillis() - frameStart);
                if(sleepTime < 1L) sleepTime = 1L;
                
                if(Main.frameskip || QFPS.frameTime < 15) Thread.sleep(sleepTime);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    public final void start() {
        changed = true;
        run = true;

        QFPS.reset();
    }

    public void startMus(long t) {
        try {
            if(Main.musicPlayer.hasPlayer()) {
                Main.musicPlayer.setVolume(Main.music);
                Main.musicPlayer.start(t);
            }

            QFPS.miniReset();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private void stop() {
        run = false;
    }

    private void stopMus() {
        if(Main.musicPlayer.hasPlayer()) Main.musicPlayer.stop();
    }

    public void destroyMusic() {
        if(Main.musicPlayer.hasPlayer()) {
            Main.musicPlayer.destroy();
        }
    }

    public void init() {
        try {
            if(Main.isMusic && Main.music != 0) {
                if(!Main.musicPlayer.hasPlayer()) Main.musicPlayer.loadFile(mus);
                QFPS.miniReset();
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }

    }

    public void sizeChanged(int w, int h) {
        width = w;
        height = h;
        scene.getG3D().resize(width, (int) (height * ( Main.getDisplaySize() / 100.0F)));
    }

    public void showDialog(String str) {
        QFPS.miniReset();
        keys.reset();
        stop();
        run = false;
        dialogScreen.set(str, font, scene.getG3D(), main, this);
        Main.setCurrentRepaint(dialogScreen);
    }

    public final void openShop(int[] items, String[] files, int[] prices) {
        QFPS.miniReset();
        stop();
        run = false;
        Main.setCurrent(new Shop(main, this, player, items, files, prices));
    }

    public final void openInventory() {
        QFPS.miniReset();
        stop();
        run = false;
        Main.setCurrent(new InventoryScreen(main, this, player));
    }

    public void loadLevel(int lvl, Vector3D stp) {
        loadLevel(lvl, stp, true, false);
    }

    public void loadLevel(int lvl, Vector3D stp, boolean stopMus, boolean changePos) {
        if(player == null) return;
        main.addAvailableLevel(lvl - 1);
        
        if(newPos != null && stp != null && player != null) {
            if(player.getCharacter().getTransform() != null) {
                if(changePos) {
                    player.getCharacter().getTransform().setPosition(newPos);
                } else {
                    player.getCharacter().getTransform().subPosition(stp.x, stp.y, stp.z);
                    player.getCharacter().getTransform().addPosition(newPos);
                }
            }
        }
        
        Main.saveGame(lvl, player, scene);
        Main.saveObjects(levelNumber, player, scene);
        stop();
        if(stopMus) stopMus();
        destroy(stopMus);
        
        if(GameHelp.needToShow(levelNumber, 1, true)) {
            Main.setCurrent(new GameHelp(main, null, levelNumber, true, hudInfo));
        } else {
            Main.loadLevel(true, true, lvl, hudInfo, main, null, 1);
        }
    }

    public void restartGame() {
        stop();
        msToEnd = msToExit = 0;
        scene.reset();
        player.setPart(-1);
        time = 0;
        player.set(scene.getG3D().getWidth(), scene.getG3D().getHeight(), scene.getStartPoint(), hudInfo, scene.camPose);
        
        if(Main.getContinueLevel() == levelNumber) {
            Main.loadGame(player, width, height, scene);
            scene.getG3D().updateFov((int) player.fov);
            Main.loadPosition(player);
            Main.loadObjects(player, width, height, scene, levelNumber);
        } else {
            Player.usedPoints.removeAllElements();
            //player.set(scene.getG3D().getWidth(), scene.getG3D().getHeight(), scene.getStartPoint(), hudInfo);
            Main.loadGame(player, width, height, scene);
            scene.getG3D().updateFov((int) player.fov);
            Main.loadObjects(player, width, height, scene, Main.getContinueLevel());
        }
        
        player.copyNewToUsed();
        scene.getHouse().addObject(player);
        LevelLoader.loadObjects(levelFile, levelIni, scene, player, true);
        scene.deleteUsedObjects(player);
        scene.removeKilledBots();

        lookAtObj = null;
        paused = false;
        delayDialogs.removeAllElements();
        start();
        stopMus();
        startMus(0);
    }

    public final void openPause() {
        paused = true;
        if(Main.musicPlayer.hasPlayer()) {
            musTime = Main.musicPlayer.getTime();
            stopMus();
        }
        
        stop();
        QFPS.miniReset();
        Main.setCurrent(new PauseScreen(main, this, scene.getG3D().getDisplay(), musTime));
    }
    
    public void gameEnd() {
        stopMus();
        MyCanvas scr;
        
        
        if(GameHelp.needToShow(levelNumber, 1, true)) {
            scr = new GameHelp(main, null, levelNumber, true, hudInfo);
        } else {
            if(TitleScreen.hasTitleScreen()) scr = new TitleScreen(main, null);
            else scr = new Menu(main);
        }

        Main.setCurrent(scr);
    }
    
    public void openTitleScreenScript() {
        if(TitleScreen.hasTitleScreen()) {
            stopMus();
            Main.setCurrent(new TitleScreen(main, null));
        }
    }

    public final void resize() {
        try {
            if(!Main.originalSight) {
                if(!Main.fullScreenSight) imgSight = createImage2(Main.sight_icon);
                else imgSight = ImageResize.createImage(Main.sight_icon, width, height * Main.displaySize / 100);
            } else imgSight = Image.createImage(Main.sight_icon);
            
        } catch (Exception exp) {}
        
        if(player.arsenal.currentWeapon() != null) {
            player.arsenal.currentWeapon().reset();
        }
    }

    public void runScriptFromFile(String path) {
        runScriptFromFile(path, player);
    }

    public void runScriptFromFile(String path, RoomObject obj) {
        String[] script = LevelLoader.createScript(StringTools.getStringFromResource(path));

        obj.give(script, player, scene.getHouse(), this);
    }

    public void runScript(String[] script) {
        runScript(script, player);
    }

    public void runScript(String[] script, RoomObject obj) {
        obj.give(script, player, scene.getHouse(), this);
    }

    public boolean readBooleanFromScript(String val) {
        return RoomObject.readBoolean(val, player, scene.getHouse(), this, player);
    }
}
