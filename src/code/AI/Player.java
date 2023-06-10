package code.AI;

import code.Gameplay.Arsenal;
import code.Gameplay.GameScreen;
import code.Gameplay.Inventory.ItemList;
import code.Gameplay.Map.House;
import code.Gameplay.Map.RoomObject;
import code.Gameplay.Map.Scene;
import code.Gameplay.Objects.GameObject;
import code.Gameplay.Weapon;
import code.HUD.Base.HUDInfo;
import code.HUD.DeveloperMenu;
import code.Math.Matrix;
import code.Math.Vector3D;
import code.Rendering.Camera;
import code.Rendering.DirectX7;
import code.utils.Asset;
import code.utils.FPS;
import code.utils.Main;
import code.utils.WeaponCreator;
import java.util.Vector;

public final class Player extends GameObject {
    public static String[] sndWalk = null;
    public static String sndJump = null;
    public static boolean arcadeJumpPhysics = false;
    public static boolean fallDamage = true;
    
    public static Vector toAddOnStart = new Vector();
    public static Vector usedPoints = new Vector();
    
    public int money = 0;
    public int frags = 0;
    public int stepIndex = 0;
    public Arsenal arsenal;
    private boolean damage = false;
    
    public boolean zoom = false;
    public float fov = Main.stdFov;
    public int stdFov = Main.stdFov, zoomFov = Main.zoomFov;
    public long lastZoomAction = 0L;
    private int falldist = 0;
    private long lastStep = 0, lastYCheck = 0;
    private int lastY = 0;
    public float rotateX = 0.0f, rotateY = 0.0f;
    
    public ItemList items;
    
    int walkFrame, attackFrame, muzzleFrame;
    Camera cam;
	
	TPPose scenePose;
    
    public Player(int wG3D, int hG3D, Vector3D pos, Object hudInfo, TPPose scenePose) {
        set(wG3D, hG3D, pos, hudInfo, scenePose);
    }

    public final void set(int wG3D, int hG3D, Vector3D pos, Object hudInfo, TPPose scenePose) {
        name = "PLAYER";
        character.reset();
        character.getTransform().setPosition(0, 0, 0);
        if (pos != null) character.getTransform().setPosition(pos.x, pos.y, pos.z);
        setHp(100);
        frags = 0;
        setCharacterSize(400, 1503);
        falldist = 0;
        money = 0;
        rotateX = rotateY = 0;

        arsenal = new Arsenal(wG3D, hG3D);
        if(hudInfo != null) { //todo удалить эту хрень
            money = ((HUDInfo) hudInfo).money;
            int[] ammos = ((HUDInfo) hudInfo).ammo;
            Weapon[] weapons = arsenal.getWeapons();

            for (int i=0; i<weapons.length; i++) {
                if (weapons[i] != null) weapons[i].reset();

                if (ammos[i] == -1) weapons[i] = null;
                else {
                    weapons[i] = WeaponCreator.createWeapon(i);
                    weapons[i].setAmmo(ammos[i]);
                }
            }
            if (arsenal.current != -1) arsenal.currentWeapon().createSprite(wG3D, hG3D);
        }
        
        items = new ItemList();
        character.fly = (DeveloperMenu.fly > 0);
        walkFrame = 0;
        attackFrame = Integer.MAX_VALUE;
        muzzleFrame = 0;
		this.scenePose = scenePose;
        if(currentPose() != null) {
            cam = new Camera();
            if(TPPose.meshPoses != null) TPPose.applyRenderModes(TPPose.meshPoses);
            if(scenePose != null) TPPose.applyRenderModes(new TPPose[]{scenePose});
            if(TPPose.radius != 0) setCharacterSize(TPPose.radius, TPPose.height);
        }
    }

    public final void destroy() {
        money = 0;
    }

    public final void render(DirectX7 g3d, int x1, int y1, int x2, int y2) {
        if(Main.stepSound != null && Main.isFootsteps && Main.footsteps != 0) {
            if((character.speed.x != 0 || character.speed.z != 0)
                    && character.onFloor == true
                    && System.currentTimeMillis() - lastStep > 450
                    && !character.fly) {
                
                String[] soundz = Main.stepSound;
                if(sndWalk != null)  soundz = sndWalk;
                if(soundz != null && stepIndex >= soundz.length) stepIndex = 0;
                
                if(soundz != null) {
                    try {
                        Asset.getSound(soundz[stepIndex]).setVolume(Main.footsteps);
                        Asset.getSound(soundz[stepIndex]).start(0);
                        stepIndex++;
                    } catch (Exception var2) {
                        System.err.println("ERROR in Step sound: " + var2);

                    }
                }
                lastStep = System.currentTimeMillis();
            }
        }
        
        if(arsenal.currentWeapon() != null) arsenal.currentWeapon().renderSplinter(g3d);
        
        if(currentPose() != null) {
            currentPose().draw(this, g3d, x1, y1, x2, y2);
        }
    }
    
    public void nextWeapon() {
        int old = arsenal.current;
        arsenal.next();
        if(old != arsenal.current) {
            attackFrame = Integer.MAX_VALUE;
            muzzleFrame = 0;
        }
    }
    public void previousWeapon() {
        int old = arsenal.current;
        arsenal.previous();
        if(old != arsenal.current) {
            attackFrame = Integer.MAX_VALUE;
            muzzleFrame = 0;
        }
    }

    public final void update(Scene scene, Player player) {
        super.update(scene, player);
        
        sndWalk = scene.getHouse().getRooms()[getPart()].stepSound;
        sndJump = scene.getHouse().getRooms()[getPart()].jumpSound;
        
        if(fallDamage) {
        if (character.onFloor == false && System.currentTimeMillis() - lastYCheck < 1000) {
            falldist += lastY - getPosY();
        }
        if(falldist > 7000 && character.onFloor == true) {
            damage = true;
            damage((falldist - 7000) / 40);
        }
        if(falldist > 11000 && character.onFloor == false) {
            damage = true;
            damage(100);
        }
        lastYCheck = System.currentTimeMillis();
        lastY = getPosY();
        if (character.onFloor || character.fly) falldist = 0;
        }
        
        if(arsenal.current != -1) {
            if (arsenal.currentWeapon() != null) {
                GameObject obj = arsenal.currentWeapon().update(scene.getHouse(), this);
                if(obj instanceof Zombie) {
                    money += Zombie.moneyOnDeath;
                    frags++;
                } else if (obj instanceof BigZombie) {
                    money += BigZombie.moneyOnDeath;
                    frags++;
                } else if (obj instanceof NPC) {
                    NPC npc = (NPC) obj;
                    frags += npc.fragsOnDeath;
                    money += npc.moneyOnDeath;
                    scene.runScript(new Object[]{npc,npc.onDeath});
                }
            }
        }
		
		if(cam != null) {
            if(currentPose() != null) currentPose().update(cam, this);
			cam.calcPart(scene.getHouse());
			if(cam.getPart() == -1 && getPart() != -1) cam.setPart(getPart());
		}
    }
    
    public TPPose currentPose() {
		if(scenePose != null) return scenePose;
        if(TPPose.meshPoses == null) return null;
        Weapon wp = arsenal.currentWeapon();
        
        if(wp == null) return TPPose.meshPoses[0];
        return TPPose.meshPoses[wp.playerPose];
    }
    
    public boolean canWalk() {
        TPPose tppose = currentPose();
        if(tppose == null) return true;
        return tppose.canWalk(this);
    }
    
    public boolean canJump() {
        TPPose tppose = currentPose();
        if(tppose == null) return true;
        return tppose.canJump(this);
    }
    
    public boolean canLookX() {
        TPPose tppose = currentPose();
        if(tppose == null) return true;
        return tppose.canLookX(this);
    }
    
    public boolean canLookY() {
        TPPose tppose = currentPose();
        if(tppose == null) return true;
        return tppose.canLookY(this);
    }
    
    public boolean canAttack() {
        TPPose tppose = currentPose();
        if(tppose == null) return true;
        return tppose.canAttack(this);
    }
    
    public boolean canAttackSight() {
        TPPose tppose = currentPose();
        if(tppose == null) return true;
        return tppose.canAttackSight;
    }
    
    public boolean canAttackNoSight() {
        TPPose tppose = currentPose();
        if(tppose == null) return true;
        return tppose.canAttack;
    }
    
    public boolean isSwapStrafeLook() {
        TPPose tppose = currentPose();
        if(tppose == null) return false;
        return tppose.isSwapStrafeLook(this);
    }
    
    public boolean isRotToWalkDir() {
        TPPose tppose = currentPose();
        if(tppose == null) return false;
        return tppose.isRotToWalkDir(this);
    }
    
    public boolean show2D() {
        TPPose tppose = currentPose();
        if(tppose == null) return true;
        return tppose.show2D(this);
    }
    
    public float lookSpeed() {
        TPPose tppose = currentPose();
        if(tppose == null) return (zoom ? 0.71f : 1f);
        return tppose.lookSpeed(this);
    }
    
    public void setCamera(DirectX7 g3d) {
        Matrix playerMat = character.getTransform();
        int playerHeight = getEyesHeight();
        if(cam == null) {
            playerMat.m13 += playerHeight;
            g3d.setCamera(playerMat);
            playerMat.m13 -= playerHeight;
        } else {
            g3d.setCamera(cam.getCamera());
        }
    }
    
    public Camera getCamera() {
        return cam;
    }
    
    public int getRenderPart(Scene scene) {
        if(cam == null) {
            return getPart();
        } else {
            //cam.calcPart(scene.getHouse());
            return cam.getPart();
        }
    }
    
    public int getEyesHeight() {
        int height = 1503;//character.getHeight();
        
        if(isDead()) {
            height = (int) (height / Math.max(0.4F * getFrame(), 1F));
            if(height < character.getRadius()) height = character.getRadius();
        }
        
        return height;
    }

    public final boolean damage(GameObject obj, int dmg) {
        if (DeveloperMenu.debugMode && dmg > 0 && DeveloperMenu.godMode) return true;
        if (dmg > 0) damage = true;
        if (dmg < 0 && getHp() - dmg > 100) return super.damage(obj, -(100 - getHp()));
        
        return super.damage(obj, dmg);
    }

    public final boolean isDamaged() {
        boolean tmp = damage;
        damage = false;
        return tmp;
    }

    public final void pay(int price) {
        money -= price;
    }

    public final boolean isTimeToRenew() {
        return isDead() && (System.currentTimeMillis() - DeathTime) > 3000;
    }

    public final void fire(DirectX7 g3d) {
        if(arsenal.current != -1 && canAttack()) {
            if(arsenal.currentWeapon().fire(g3d)) {
                attackFrame = 0;
                TPPose tp = currentPose();
                if(tp != null) muzzleFrame = tp.muzzleFlashTimer;
            }
        }
    }

    public final void jump() {
        if(!canJump()) return;
        if(!arcadeJumpPhysics) jump(150, 1.2F);
        else character.jumpArcade(150, 1.2F);
        if (character.onFloor && Main.isFootsteps && Main.footsteps != 0) {
            try {
                String snd = Main.jumpSound;
                if (sndJump != null) snd = sndJump;
                //STARTED=400
                if(snd!=null && Asset.getSound(snd).getState()!=400) {
                    Asset.getSound(snd).setVolume(Main.footsteps);
                    Asset.getSound(snd).start();
                }
            } catch (Exception var2) {
                System.err.println("ERROR in Step sound: " + var2);

            }
        }

    }
    
    public void updateMatrix() {
        int x=character.transform.m03;
        int y=character.transform.m13;
        int z=character.transform.m23;
        
        if(rotateX<-80) rotateX=-80;
        if(rotateX>80) rotateX=80;
        
        while(rotateY>360) rotateY-=360;
        while(rotateY<0) rotateY+=360;
        
        character.transform.setIdentity();
        character.transform.setRotX((int)rotateX);
        character.transform.setPosition(x,y,z);
        character.transform.rotY((int)rotateY);
    }

    public final void rotYn(float i) {
        if(!canLookY()) return;
        rotateY += i;
        updateMatrix();
    }
    
    public final void rotXn(float i) {
        if(!canLookX()) return;
        rotateX += i;
        updateMatrix();
    }
    
    public final void rotLeft() {
        if(!canLookY()) return;
        rotYn((7f * lookSpeed() * Main.mouseSpeed / 50.0F) * FPS.frameTime / 50f);
    }

    public final void rotRight() {
        if(!canLookY()) return;
        rotYn(-(7f * lookSpeed() * Main.mouseSpeed / 50.0F) * FPS.frameTime / 50f);
    }
    
     public final void rotUp() {
        if(!canLookX()) return;
        rotXn((7f * lookSpeed() * Main.mouseSpeed / 100.0F) * FPS.frameTime / 50f);
    }

    public final void rotDown() {
        if(!canLookX()) return;
        rotXn(-(7f * lookSpeed() * Main.mouseSpeed / 100.0F) * FPS.frameTime / 50f);
    }
	
	public void walk(int right, int forward) {
		if(!canWalk()) return;
		
		if(cam != null && isRotToWalkDir()) {
			
			float rotY = cam.currentRotY;
			float modelRot = 0;

			modelRot = ((float) Math.floor((rotY + 22.5) / 45.0)) * 45;
			if(forward < 0) modelRot += 180;
			modelRot -= right*90/(forward!=0?(forward>0?2:-2):1);
			
			int walkSpeed = (character.fly?350:150);
			
			Vector3D dir = new Vector3D((int) (-Math.sin(modelRot * Math.PI / 180) * walkSpeed), 0, (int) (-Math.cos(modelRot * Math.PI / 180) * walkSpeed));
			
			if(character.fly || character.onFloor) {
				rotateY = modelRot;
				updateMatrix();
				
				character.moveFree(dir);
			}
		} else {
			character.moveZ((character.fly?-350:-150) * forward);
			character.moveX((character.fly?350:150) * right);
		}
		
		if((forward != 0 || right != 0) && arsenal.currentWeapon() != null) arsenal.currentWeapon().enableShake();
	}

    public final Object getHUDInfo() {
        Weapon[] weapons = arsenal.getWeapons();
        int[] ammos = new int[weapons.length];

        for (int i=0; i<weapons.length; i++) {
            if (weapons[i] != null) ammos[i] = weapons[i].getAmmo() + weapons[i].getRounds();
            else ammos[i] = -1;
        }

        return new HUDInfo(money, ammos);
    }

    public void activate(House house, Player player, GameScreen gs) {
    }
    
    public void copyNewToUsed() {
        if(toAddOnStart.isEmpty()) return;
        
        while(!toAddOnStart.isEmpty()) {
            if(!RoomObject.containsSimple(usedPoints,(String)toAddOnStart.firstElement())) usedPoints.addElement(toAddOnStart.firstElement());
            toAddOnStart.removeElementAt(0);
        }
    }
    
}
