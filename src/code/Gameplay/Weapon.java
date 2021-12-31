package code.Gameplay;

import code.AI.Bot;
import code.AI.Player;
import code.AI.misc.Splinter;
import code.AI.misc.Trace;
import code.Collision.Ray;
import code.Gameplay.Map.Character;
import code.Gameplay.Map.House;
import code.Gameplay.Objects.GameObject;
import code.Gameplay.Objects.MeshObject;
import code.HUD.DeveloperMenu;
import code.Math.MathUtils2;
import code.Math.Matrix;
import code.Math.Vector3D;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.Polygon3V;
import code.Rendering.Meshes.Polygon4V;
import code.Rendering.RenderObject;
import code.utils.Asset;
import code.utils.FPS;
import code.utils.ImageResize;
import code.utils.Main;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

public final class Weapon {

    private static final Splinter splinter = new Splinter();
    private static final Ray ray = new Ray();

    private final short damageValue; // Урон от выстрела
    private final short delay; // Задержка между выстрелами
    private final short shotTime; // Продолжительность выстрела
    public float frame = -1.0f; // Кол-во кадров прорисованных во время выстрела и после. Если -1, нет выстрела.
    private final String fileWeapon, fileFire;
    public String fileSight, fileSightWeapon;
    public String filePatron, filePatronLow;
    private final float kW; // коэф. смещения вспышки по горизонтали относительно правого нижнего угла спрайта оружия
    private final float kH; // коэф. смещения вспышки по вертикали
    public boolean patronbuy = true;
    private Image imgWeapon, imgFire;
    private Image sight, sightWeapon;
    public Image imgPatron, imgPatronLow;
    private long distance = 999999999L;
    public boolean meleeAnim = false;
    private short dx_fire = 1; // смещение вспышки по горизонтали относительно правого нижнего угла спрайта оружия
    private short dy_fire = 1; // смещение вспышки по вертикали
    public short dx_max = 0; // максимально возможное dx
    public short dy_max = 0; // максимально возможное dy
    private short dx = 0; // смещение спрайта оружия по горизонтали (от левого нижнего угла) в текущем кадре
    private short dy = 0; // смещение спрайта оружия по вертикали (от левого нижнего угла) в текущем кадре
    private short widthShift = 2; // смещение смешения оружия по горизонтали при ходьбе
    private short heightShift = 5; // смещение смешения оружия по вертикали при ходьбе
    private boolean twoHands = false; // Оружие в двух руках
    public boolean leftHand = false;
    private boolean shake = false; // Тряска оружия
    public boolean newanim = false;
    public Magazine magazine;
    public int stdFov, zoomFov;
    public boolean hasZoom;
    public String shoot = null;
    public String reload = null;
    public boolean canShoot = true;
    public boolean ignoreSightOnDraw = false;
    public float attackIntensity = 1.0F;
    public boolean debugWeapon = false;

    public int weaponId = 1;
    public boolean customPos = false;
    public float customPosX = 1.0F;
    public boolean centreAlign = false;
    
    public boolean shootLight=true;
    
    public int playerPose = 0;

    public Weapon(String fileWeapon, String fileFire, float kW, float kH, int damageValue, int delay, int shotTime, boolean twoHands, int capacity, int reloadTime, long distance, boolean pb, int weaponId) {
        this.distance = distance;
        this.patronbuy = pb;
        this.fileWeapon = fileWeapon;
        this.fileFire = fileFire;
        this.kW = kW;
        this.kH = kH;
        this.damageValue = (short) damageValue;
        this.delay = (short) delay;
        this.shotTime = (short) shotTime;
        this.twoHands = twoHands;
        magazine = new Magazine(capacity, reloadTime);
        newanim = false;
        this.weaponId = weaponId;
        stdFov = Main.stdFov;
        zoomFov = Main.zoomFov;
        hasZoom = Main.hasZoom;
    }

    public final void reset() {
        imgWeapon = imgFire = sight = sightWeapon = null;
    }

    // ?
    public final void createSprite(int width_g3d, int height_g3d) {
        float coof=(float) (height_g3d) / (256.0F);
        float coof2=(float) (width_g3d) / (240.0F);
        if(customPos || !Main.resizeWeapons) coof2=coof;
        
        if(fileWeapon != null) {
            imgWeapon = ImageResize.createImage(fileWeapon, coof2, coof);
            this.dx_fire = (short) ((int) ((float) imgWeapon.getWidth() * kW));
            this.dy_fire = (short) ((int) ((float) imgWeapon.getHeight() * kH));
            this.dx_max = (short) (imgWeapon.getWidth() / 5);
            this.dy_max = (short) (imgWeapon.getHeight() / 5);
            if(attackIntensity > 1.0f) {
                dx_max = (short) (imgWeapon.getWidth() / 2);
                dy_max = (short) (imgWeapon.getHeight() / 2);
            }
        }
        
        if(fileFire != null) imgFire = ImageResize.createImage(fileFire, coof2, coof);
    }

    private void createSights(int width, int height) {
        float coof = (float) (height) / (256.0F);
        if (sight == null && fileSight != null) 
            sight = ImageResize.createImage(fileSight, width, height);
            
        if (sightWeapon == null && fileSightWeapon != null) 
            sightWeapon = ImageResize.createImage(fileSightWeapon, coof, coof);
    }

    // true, если происходит выстрел
    private boolean isFire() {
        return frame >= 0;
    }

    public short getDamageValue() {
        return damageValue;
    }
    public final void draw(Graphics g, int x, int y, int width, int height) {
        if (imgWeapon == null && fileWeapon != null) createSprite(width, height);
        
        if ((sight == null && fileSight != null) || (sightWeapon == null && fileSightWeapon != null)) 
            createSights(width, height);
        
        short dxb, dxmb;
        dxb = dx;
        dxmb = dx_max;
        if (meleeAnim) {
            dx = (short) (dx_max - dx);
            dy = (short) (dy_max - dy);
        }
        
        int oldWidth=width;
        if (customPos) {
            x = (int) ((width - (height * 240 / 256)) * customPosX / 2);
            width = (int) (height * 240 * customPosX / 256);
        } else {
            x = 0;
        }
        if (centreAlign && imgWeapon != null) x += imgWeapon.getWidth() / 2;
        

        if (!newanim) {
            if (twoHands || !leftHand) {
            if (isFire()) {
                if (imgFire != null) 
                    g.drawImage(imgFire, width - dx_fire + dx + x, height - dy_fire + dy + y, 3); // 3=2+1 = VCENTER+HCENTER
                
            }
            if(imgWeapon != null) 
                g.drawImage(imgWeapon, width + dx + x, height + dy + y, 40); // 40=32+8 = BOTTOM+RIGHT
            }
            
            if (twoHands || leftHand) {
                if (isFire() && imgFire != null) 
                        g.drawRegion(imgFire, 0, 0, imgFire.getWidth(), imgFire.getHeight(), 2, oldWidth-(width - dx_fire + dx + x), height - dy_fire + dy + y, 3); // 3=2+1 = VCENTER+HCENTER
                
                if(imgWeapon != null) 
                    g.drawRegion(imgWeapon, 0, 0, imgWeapon.getWidth(), imgWeapon.getHeight(), 2, oldWidth-(width + dx + x), height + dy + y, 36); // 36=32+4 = BOTTOM+LEFT
            }
        } else {
            if (twoHands || !leftHand) {
            if (isFire()) {
                if(imgFire != null) 
                    g.drawImage(imgFire, width + dx + x, height + dy + y, 40); // 40=32+8 = BOTTOM+RIGHT
            } else if(imgWeapon != null) {
                g.drawImage(imgWeapon, width + dx + x, height + dy + y, 40); // 40=32+8 = BOTTOM+RIGHT
            }
            }

            if (twoHands || leftHand) {
                if (isFire()) {
                    if (imgFire != null) 
                        g.drawRegion(imgFire, 0, 0, imgFire.getWidth(), imgFire.getHeight(), 2, oldWidth-(width + dx + x), height + dy + y, 36);// 3=2+1 = VCENTER+HCENTER
                } else if(imgWeapon != null) {
                    g.drawRegion(imgWeapon, 0, 0, imgWeapon.getWidth(), imgWeapon.getHeight(), 2, oldWidth-(width + dx + x), height + dy + y, 36); // 36=32+4 = BOTTOM+LEFT
                }
            }
        }
        if (meleeAnim) dy = (short) (dy_max - dy);
        dx_max = dxmb;
        dx = dxb;

    }

    public final void drawSight(Graphics g, int x, int y, int width, int height) {
        if (ignoreSightOnDraw) draw(g, x, y, width, height);
        
        if ((sight == null && fileSight != null) || (sightWeapon == null && fileSightWeapon != null)) 
            createSights(width, height);
        
        if (sight == null && sightWeapon == null) return;
        
        if (meleeAnim) {
            dx = (short) (dx_max - dx);
            dy = (short) (dy_max - dy);
        }
        
        if (sightWeapon != null) {
            if (!twoHands) {
                g.drawImage(sightWeapon, width / 2, height + y + dy, Graphics.BOTTOM | Graphics.HCENTER);
            } else {
                g.drawImage(sightWeapon, width / 2, height + y + dy, Graphics.BOTTOM | Graphics.LEFT);
                g.drawRegion(sightWeapon, 0, 0, sightWeapon.getWidth(), sightWeapon.getHeight(), Sprite.TRANS_MIRROR, width / 2, height + y + dy, Graphics.BOTTOM | Graphics.RIGHT);
            }
        }
        if (sight != null) g.drawImage(sight, 0, y, Graphics.TOP | Graphics.LEFT);
        
        if (meleeAnim) {
            dx = (short) (dx_max - dx);
            dy = (short) (dy_max - dy);
        }
    }

    public void renderSplinter(DirectX7 g3d) {
        splinter.project(g3d);
        if (splinter.isShatters()) splinter.render(g3d, 1500);
    }

    public final void enableShake() {
        shake = true;
    }

    public final int getRounds() {
        return magazine.rounds;
    }

    public int getAmmo() {
        return magazine.ammo;
    }

    public void setAmmo(int number) {
        magazine.setAmmo(number);
        magazine.recount();
    }

    // true, если нужно перезаряжаться (продолжать перезарядку)
    public final boolean isReloading() {
        return magazine.isReloading();
    }

    public final int reloadingPercentage() {
        return magazine.percentage();
    }

    // Анимация ходьбы. Нанисение урона врагу. Задание точки столкновения осколка выстрела (splinter).
    // Возвращает ссылку на врага, если он убит, иначе null
    public final GameObject update(House house, GameObject player) {
        if (canShoot) magazine.update();
        
        int anima = 29; //wrong wrong wrong
        if (attackIntensity > 1.0f) anima = 17;
        
        boolean fire = (frame == 0);
        if (isFire() && canShoot) {
            frame += 1.0F * FPS.frameTime / 50f;
            if (frame > shotTime) frame = (short) (-delay);
        }

        if (frame < -1) {
            frame += 1.0F * FPS.frameTime / 16.6f; //wrong fps fix
            if (frame > -1) frame = -1;
            
        }

        if (isFire() && canShoot) {
            dx = (short) (dx + (Math.abs(widthShift) << 1) * FPS.frameTime / anima * attackIntensity);
            dy = (short) (dy + (Math.abs(heightShift) << 1) * FPS.frameTime / anima * attackIntensity);
        }

        if (shake && frame >= -1) {
            if (anima != 17) {
                dx += (byte) (widthShift * FPS.frameTime / anima);
                dy += (byte) (heightShift * FPS.frameTime / anima);
            }
            shake = false;
        } else {
            dx = (short) (dx + -dx * FPS.frameTime / anima / 6);
            dy = (short) (dy + -dy * FPS.frameTime / anima / 6);
        }

        if (dy <= 0) {
            dy = 0;
            heightShift = (short) (-heightShift);
        }

        if (dy > dy_max) {
            dy = dy_max;
            heightShift = (short) (-heightShift);
        }

        if (dx <= 0) {
            dx = 0;
            widthShift = (short) (-widthShift);
        }

        if (dx >= dx_max) {
            dx = dx_max;
            widthShift = (short) (-widthShift);
        }

        if (fire && canShoot) {
            Matrix mat = player.getCharacter().getTransform();
            ray.reset();
            Vector3D start = ray.getStart();
            Vector3D end = ray.getDir();
            start.set(mat.m03, mat.m13 + ((Player)player).getEyesHeight(), mat.m23);
            end.set(-mat.m02 << 1, -mat.m12 << 1, -mat.m22 << 1);
            ray.reset();
            house.rayCast(player.getPart(), ray, true);
            if (ray.isCollision()) end.setLength(ray.getDistance());

            GameObject pobj = player;
            Character pchar = player.getCharacter();

            GameObject damagedEnemy = null;
            long dist = Integer.MAX_VALUE;
            Vector objects = house.getObjects();
            Vector3D headPos = new Vector3D();

            for (int var8 = 0; var8 < objects.size(); ++var8) {
                GameObject obj;
                if (objects.elementAt(var8) instanceof GameObject) {
                    obj = (GameObject) objects.elementAt(var8);
                    if(obj instanceof MeshObject) if(((MeshObject)obj).ignoreWeaponRayCast) continue;
                    if (obj != pobj && !obj.isDead()) {
                        Character chr = obj.getCharacter();
                        Matrix mat2 = chr.getTransform();
                        headPos.set(mat2.m03, mat2.m13 + chr.getHeight(), mat2.m23);
                        int radius = chr.getRadius();
                        long dist2;
                        if (MathUtils2.distanceToRay(headPos, start, end) < radius * radius) {
                            Vector3D var35;
                            int x = headPos.x - start.x;
                            int y = headPos.y - start.y;
                            int z = headPos.z - start.z;
                            dist2 = (long) x * (long) x + (long) y * (long) y + (long) z * (long) z;
                        } else {
                            dist2 = Integer.MAX_VALUE;
                        }

                        if (dist2 < dist) {
                            damagedEnemy = obj;
                            dist = dist2;
                        }
                    }
                }
            }

            fire = damagedEnemy != null;
            boolean damaged = false;
            if (fire) {
                if (damagedEnemy.getCharacter().distance(pchar) < distance * distance) 
                    damaged = damagedEnemy.damage(player, damageValue);
            }

            if (ray.isCollision()) {
                Vector3D colPoint = ray.getCollisionPoint();
                if (!fire && ray.getDistance() < distance) splinter.set(colPoint.x, colPoint.y, colPoint.z);
                
                if (fire && Main.blood && pobj instanceof Bot && damageValue>0) {
                    if(((Bot)pobj).hasBlood) {
                        Trace trace = createTrace(colPoint, ray.getTriangle());
                        trace.setPart(ray.getNumRoom());
                        house.addObject(trace);
                    }
                }
                
                if (DeveloperMenu.debugMode && (debugWeapon || DeveloperMenu.showShootCollision)) {
                    System.out.println("Shoot Collision: "+colPoint.x + "," + colPoint.y + "," + colPoint.z);
                }
                
            }

            if(damaged) return damagedEnemy;
        }
        return null;
    }

    //? Если есть патроны в магазине, начать анимацию выстрела и пересчитаь кол-во патронов в магазине, иначе начать перезарядку.
    public final boolean fire(DirectX7 g3d) {
        if (!canShoot) return false;
        
        if (patronbuy == false) {
            magazine.addAmmo(1);
            magazine.takeRounds(-1);
            if (twoHands) {
                magazine.addAmmo(1);
                magazine.takeRounds(-1);
            }
        }

        if (frame == -1) {
            if (magazine.rounds > 0) {
                frame = 0;
                if(shootLight) g3d.shootIntensity=g3d.shootLength=shotTime*90;
                
                if (shoot != null && Main.isSounds && Main.sounds != 0) {
                    Asset.getSound(reload).stop();
                    Asset.getSound(shoot).setVolume(Main.sounds);
                    Asset.getSound(shoot).start();
                }

                magazine.takeRounds(twoHands ? 2 : 1);
                return true;
            }

            if (!magazine.isReloading()) {
                magazine.reload();
                if (magazine.isReloading() && reload != null && Main.isSounds && Main.sounds != 0) {
                    Asset.getSound(reload).stop();
                    Asset.getSound(reload).setVolume(Main.sounds);
                    Asset.getSound(reload).start(0);
                }
            }
        }

        return false;
    }
    public final boolean isTwoHands() {
        return twoHands;
    }
    public final void addAmmo(int number) {
        magazine.addAmmo(number);
    }

    public Trace createTrace(Vector3D vector3f, RenderObject meshr) {

        int minx = 0;
        int miny = 0;
        int minz = 0;
        int maxx = 0;
        int maxy = 0;
        int maxz = 0;
        int posx;
        int posy;
        int posz;
        Vector3D v1 = new Vector3D(0, 0, 0);
        Vector3D v2 = new Vector3D(0, 0, 0);
        Vector3D v3 = new Vector3D(0, 0, 0);
        Vector3D v4 = new Vector3D(0, 0, 0);
        if (meshr instanceof Polygon4V) {
            Polygon4V p4v = (Polygon4V) meshr;
            posx = (p4v.a.x + p4v.b.x + p4v.c.x + p4v.d.x) / 4;
            posy = (p4v.a.y + p4v.b.y + p4v.c.y + p4v.d.y) / 4;
            posz = (p4v.a.z + p4v.b.z + p4v.c.z + p4v.d.z) / 4;
            minx = Math.min(Math.min(Math.min(p4v.a.x, p4v.b.x), p4v.c.x), p4v.d.x) - posx;
            miny = Math.min(Math.min(Math.min(p4v.a.y, p4v.b.y), p4v.c.y), p4v.d.y) - posy;
            minz = Math.min(Math.min(Math.min(p4v.a.z, p4v.b.z), p4v.c.z), p4v.d.z) - posz;
            maxx = Math.max(Math.max(Math.max(p4v.a.x, p4v.b.x), p4v.c.x), p4v.d.x) - posx;
            maxy = Math.max(Math.max(Math.max(p4v.a.y, p4v.b.y), p4v.c.y), p4v.d.y) - posy;
            maxz = Math.max(Math.max(Math.max(p4v.a.z, p4v.b.z), p4v.c.z), p4v.d.z) - posz;

            v1.set(p4v.a.x - posx, p4v.a.y - posy, p4v.a.z - posz);
            v2.set(p4v.b.x - posx, p4v.b.y - posy, p4v.b.z - posz);
            v3.set(p4v.c.x - posx, p4v.c.y - posy, p4v.c.z - posz);
            v4.set(p4v.d.x - posx, p4v.d.y - posy, p4v.d.z - posz);

        }

        if (meshr instanceof Polygon3V) {
            Polygon3V p3v = (Polygon3V) meshr;
            posx = (p3v.a.x + p3v.b.x + p3v.c.x) / 3;
            posy = (p3v.a.y + p3v.b.y + p3v.c.y) / 3;
            posz = (p3v.a.z + p3v.b.z + p3v.c.z) / 3;
            minx = Math.min(Math.min(p3v.a.x, p3v.b.x), p3v.c.x) - posx;
            miny = Math.min(Math.min(p3v.a.y, p3v.b.y), p3v.c.y) - posy;
            minz = Math.min(Math.min(p3v.a.z, p3v.b.z), p3v.c.z) - posz;
            maxx = Math.max(Math.max(p3v.a.x, p3v.b.x), p3v.c.x) - posx;
            maxy = Math.max(Math.max(p3v.a.y, p3v.b.y), p3v.c.y) - posy;
            maxz = Math.max(Math.max(p3v.a.z, p3v.b.z), p3v.c.z) - posz;

            v1.set(p3v.a.x - posx, p3v.a.y - posy, p3v.a.z - posz);
            v2.set(p3v.b.x - posx, p3v.b.y - posy, p3v.b.z - posz);
            v3.set(p3v.c.x - posx, p3v.c.y - posy, p3v.c.z - posz);
            v4.set(p3v.c.x - posx, p3v.c.y - posy, p3v.c.z - posz);

        }

        return new Trace(vector3f.x, vector3f.y, vector3f.z, v1, v2, v3, v4, new Vector3D(Math.abs(minx) + Math.abs(maxx), Math.abs(miny) + Math.abs(maxy), Math.abs(minz) + Math.abs(maxz)));
    }


}
