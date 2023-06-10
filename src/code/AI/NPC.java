package code.AI;

import code.Gameplay.Map.House;
import code.Gameplay.Map.Portal;
import code.Gameplay.Map.Scene;
import code.Gameplay.Objects.GameObject;
import code.Math.MathUtils;
import code.Math.Matrix;
import code.Math.Vector3D;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.BoundingBox;
import code.Rendering.Meshes.MeshImage;
import code.Rendering.Meshes.Sprite;
import code.Rendering.MultyTexture;
import code.Rendering.Vertex;
import java.util.Vector;

public final class NPC extends Bot {

    private static final Matrix tmpMatrix = new Matrix();

    public int animspeed = 140;//140
    public int attackanimspeed = 700;//700
    public int damage = 1;//1
    public float attackradius = 1.2F;//1.2F
    public int jumpheight = 140;//140
    public float jumpspeed = 1.2F;//1.2F
    public int speed = 140;//140
    public int reacttimer = 8;//8
    public int attacktimer = 8;//8
    
    public static final int WALK_STATE = 1;
    public static final int ATTACK_STATE = 2;
    
    public int attackState = ATTACK_STATE;//2
    public int whenEnemyIsFar = WALK_STATE;//1
    public int whenEnemyIsNear = ATTACK_STATE;//2
    
    public int moneyOnDeath = 0;
    public int fragsOnDeath = 0;
    public int fragsOnAnyDeath = 0;
    public int damageSleepTime = 0;
    private long lastDamage = 0L;
    public long maxEnemyDistance = -1L;
    public boolean attackOnDamageOnlyPlayer = false;
    public int spawnerId = -1;

    private int maxHp = 100;
    public int model_height; // высота по y
    private int state = -1;

    public MeshImage meshImage;

    public int[] toAttack;
    public int[] toFollow;
    
    public boolean inPlayerTeam;
    public int[] friendlyFire;
    
    public String[] onDeath;

    private GameObject enemy;
    private GameObject follower;
    public Vector unicalEnemies;
    private boolean walkingToEnemy;
    
    public final MultyTexture mt;
    private final Vector3D dir = new Vector3D();

    public Sprite stayFront, stayBack, staySide;
    public Sprite attackFront, attackBack, attackSide;
    public Sprite deathFront, deathBack, deathSide;
    public Sprite damageFront, damageBack, damageSide;
    public Sprite walkFront, walkBack, walkSide;
    public Sprite currentSprite, nextFront, nextBack, nextSide;
    
    public Sprite muzzleFlash;
    public Vertex muzzleFlashPos;
    public int muzzleFlashTimer;
    public long lastAttack;

    public BoundingBox boundingBox;

    public NPC(Vector3D pos, MeshImage mi, int hp, MultyTexture mts) {
        maxHp = hp;
        meshImage = mi;
        if (mi != null) {
            model_height = mi.getMesh().maxY() - mi.getMesh().minY();
            boundingBox = new BoundingBox(meshImage.getAnimation());
        }
        mt = mts;
        fraction = 3;//Default bot fraction
        toAttack = new int[]{1, 2};//Attack Zombie and Bigzombie fractions
        toFollow = new int[]{0};//Follow player fraction
        name = "NPC";//For scripts
        visiblityCheck = true;
        init(pos);
    }

    public final void init(Vector3D pos) {
        if (meshImage != null) meshImage.setFrame(0);
        super.set(pos);
        setHp(maxHp);
        
        dir.set(pos.x + 50, pos.y, pos.z + 50);
        lookAt(pos.x + 50, pos.z + 50);
        
        setSprite(stayFront, staySide, stayBack);
        if (stayFront != null) model_height = stayFront.getHeight()*((stayFront.textures[0].rImg.scale<2)?2:1);
        
        setCharacterSize(model_height);
        
        if (unicalEnemies != null) unicalEnemies.removeAllElements();
        lastDamage = 0;
        state = -1;
        enemy = follower = null;
        currentSprite = null;
        walkingToEnemy = false;
    }
    
    public final void initStaySprite() {
        setSprite(stayFront, staySide, stayBack);
        if (stayFront != null) model_height = stayFront.getHeight()*((stayFront.textures[0].rImg.scale<2)?2:1);
    }

    public final void destroy() {
        super.destroy();
        enemy = follower = null;
        meshImage = null;
        boundingBox = null;
    }

    public final boolean checkVisiblity(DirectX7 g3d, int x, int y, int z, int x1, int y1, int x2, int y2) {

        if (boundingBox != null) {
            tmpMatrix.setIdentity();
            tmpMatrix.setPosition(x, y, z);
            Matrix fmat = g3d.computeFinalMatrix(tmpMatrix);
            return boundingBox.isVisible(g3d, fmat, x1, y1, x2, y2);
        }

        if (stayFront != null) {
            stayFront.getPosition().set(x, y, z);
            stayFront.project(g3d.getInvCamera(), g3d);
            return stayFront.isVisible( x1, y1, x2, y2);
        }

        return false;
    }
    
    public final void render(DirectX7 g3d, int x1, int y1, int x2, int y2) {
        if(!visible) return;
        

        if(meshImage != null) {
            Matrix mat = character.getTransform();
            Matrix fmat = g3d.computeFinalMatrix(mat);
            if(boundingBox != null && !boundingBox.isVisible(g3d, fmat, x1, y1, x2, y2)) return;

            if(mat.m11 == 16384) { //if not falling
                if(state == WALK_STATE) meshImage.setFrame(getFrameInter() * animspeed / 100);
                if(state == ATTACK_STATE) meshImage.setFrame(getFrameInter() * attackanimspeed / 100);
            }

            meshImage.setMatrix(fmat);
            meshImage.setTexture(mt);
            g3d.addRenderObject(meshImage, x1, y1, x2, y2);
            meshImage.sz += character.getRadius();
            if (character.oldFloorPoly != null && character.oldFloorPoly.sz > meshImage.sz) character.oldFloorPoly.sz = meshImage.sz - 1;
        } else {
            setSprite(g3d);
            
            if(currentSprite != null) {
                currentSprite.getPosition().set(getPosX(), getPosY(), getPosZ());

                currentSprite.updateFrame();
                currentSprite.project(g3d.getInvCamera(), g3d);

                if(!currentSprite.isVisible(x1, y1, x2, y2)) return;

                g3d.addRenderObject(currentSprite, x1, y1, x2, y2);
                currentSprite.sz += character.getRadius() * 2;
                if(character.oldFloorPoly != null && character.oldFloorPoly.sz > currentSprite.sz)
                    character.oldFloorPoly.sz = currentSprite.sz - 1;
            }
        }
        
        if(muzzleFlash != null && System.currentTimeMillis() - lastAttack <= muzzleFlashTimer) {
            muzzleFlashPos.transform(character.transform);
            muzzleFlash.pos.set(muzzleFlashPos.sx, muzzleFlashPos.sy - muzzleFlash.getHeight() / 2, muzzleFlashPos.rz);
            muzzleFlash.project(g3d.getInvCamera(), g3d);
            g3d.addRenderObject(muzzleFlash, x1, y1, x2, y2);
            muzzleFlash.sz += character.getRadius();
        }
        renderBlood(g3d, 1500);
    }
    
    public void setSprite(DirectX7 g3d) {
        Sprite nextSpr = null;
        int degree = 0;
        
        if(g3d.camera != null) {
            degree = MathUtils.fixDegree(180 + MathUtils.getAnglez(getPosX(), getPosZ(), 
                    g3d.camera.m03, g3d.camera.m23) - character.getTransform().getRotZ());
        }
        
        if(nextFront != null) nextSpr = nextFront;
        
        if(nextBack != null && degree > 90 && degree < 270) nextSpr = nextBack;
        
        if(nextSide != null && degree > 45 && degree < 135) {
            nextSide.mirX = false;
            nextSpr = nextSide;
        }
        
        if(nextSide != null && degree > 225 && degree < 315) {
            nextSide.mirX = true;
            nextSpr = nextSide;
        }

        if(currentSprite != null && nextSpr != null && (currentSprite == deathFront || currentSprite == deathSide || currentSprite == deathBack)) return;
        
        if(currentSprite != null && nextSpr != null && 
                (currentSprite == damageFront || currentSprite == damageSide || currentSprite == damageBack ||
                 currentSprite == attackFront || currentSprite == attackSide || currentSprite == attackBack)
                && !
                (nextSpr == damageFront || nextSpr == damageSide || nextSpr == damageBack)) {//Checking for animation end
            if((long) (currentSprite.animationBegin - System.currentTimeMillis()) 
                    >= 
                    -currentSprite.textures.length * 1000 / currentSprite.animation_speed) return;  
        }

        if(nextSpr == null || nextSpr == currentSprite) return;
        currentSprite = nextSpr;
        
        if(currentSprite != null) currentSprite.animationBegin = System.currentTimeMillis();
    }

    protected final void action(Scene scene) {
        if (System.currentTimeMillis() - lastDamage < damageSleepTime) return;
        
        if (Math.abs(getFrameInterDiv()) % reacttimer == 2) {
            House house = scene.getHouse();
            Vector objs = house.getObjects();
            
            //Remove killed
            if(unicalEnemies != null && !unicalEnemies.isEmpty()) {
                for(int i = 0; i < unicalEnemies.size(); ++i) {
                    GameObject tmp = (GameObject) unicalEnemies.elementAt(i);

                    if(tmp.isDead()) {
                        unicalEnemies.removeElementAt(i);
                        i--;
                    }
                }
            }
            
            //Forget if enemy is dead
            if(enemy != null && enemy.isDead()) enemy = null;
            
            GameObject oldenemy = enemy;
            enemy = findBot(objs, this, toAttack);
            
            unicalEnemy_search:
            if(unicalEnemies != null) {
                GameObject enemy2 = findBot(unicalEnemies, this, null);
                if(enemy2 == null) break unicalEnemy_search;
                
                //Select nearest
                if(enemy == null) enemy = enemy2;
                else if(character.distance(enemy2.getCharacter()) < character.distance(enemy.getCharacter())) {
                    enemy = enemy2;
                }
            }
            
            //Select nearest
            if(oldenemy != null) {

                if(enemy == null) enemy = oldenemy;
                else if(character.distance(oldenemy.getCharacter()) < character.distance(enemy.getCharacter())) {
                    enemy = oldenemy;
                }

            }
            
            //Forget enemy if it's too far
            if(enemy != null && maxEnemyDistance > -1) {
                if(character.distance(enemy.getCharacter()) >= maxEnemyDistance * maxEnemyDistance) 
                    enemy = null;
            }
            
            follower = findBot(objs, this, toFollow);

            GameObject bot = enemy != null ? enemy : follower;
            
            if(bot != null) {
                //If follower more than in ~10 meters
                if(follower != null && character.distance(follower.getCharacter()) >= 90000000L) bot = follower;
                
                //If follower is less than in 3.38 meters
                if(follower != null && bot == follower && character.distance(follower.getCharacter()) < 9000000) {
                    bot = enemy;
                }
                
                state = -1;
                
                //forget bot if bot is a new enemy and enemy isnt visible
                if(bot != null && 
                   bot == enemy && 
                   enemy != oldenemy && 
                   !notCollided(house, bot)
                  ) {
                    bot = null;
                    enemy = null;
                }
                
                if(bot != null) {
                    
                    if(scene.getHouse().isNear(getPart(), bot.getPart())) {
                        Matrix mat = bot.getCharacter().getTransform();
                        dir.set(mat.m03, mat.m13, mat.m23);
                        walkingToEnemy = true;
                    } else {
                        int nextPart = scene.getNext(getPart(), bot.getPart());
                        Portal portal = commonPortal(house, getPart(), nextPart);
                        if(portal != null) computeCentre(portal, dir);
                        walkingToEnemy = false;
                    }

                    lookAt(dir.x, dir.z);

                    long dist = character.distance(bot.getCharacter());
                    if (walkingToEnemy && 
                            dist <= sqr(character.getRadius() + bot.getCharacter().getRadius()) * attackradius) {
                        if(bot == enemy) state = whenEnemyIsNear;
                        
                    } else {
                        if (character.isCollision()) character.jump(jumpheight, jumpspeed);
                        state = whenEnemyIsFar;
                        
                    }
                }
                
            }
        }
        
        if (state == WALK_STATE) walk(speed);
        
        if (state == attackState && getFrameInterDiv() % attacktimer == 0 && enemy != null && walkingToEnemy) 
            attack(scene, enemy, damage);
        

    }

    protected final void drop(Scene scene) {
        super.drop(scene);
        state = -1;
    }

    private void walk(int spd) {
        character.moveZ(spd);
        setSprite(walkFront, walkSide, walkBack);
    }

    private void attack(Scene scene, GameObject en, int dam) {
        boolean wasAlive = !en.isDead();
        
        en.damage(this, dam);
        setSprite(attackFront, attackSide, attackBack);
        if(muzzleFlash!=null) lastAttack=System.currentTimeMillis();
        
        if(inPlayerTeam && wasAlive && en.isDead()) {
            if(en instanceof NPC) {
                NPC npc = (NPC) en;
                
                Player player = scene.findPlayer();
                if(player != null) {
                    player.frags += npc.fragsOnDeath;
                    player.money += npc.moneyOnDeath;
                }
                
                scene.runScript(new Object[]{npc, npc.onDeath});
            }
        }
    }

    public void setSprite(Sprite front, Sprite side, Sprite back) {
        nextFront = front;
        nextSide = side;
        nextBack = back;
    }
    
    public boolean isTimeToRenew() {
        if(currentSprite != null) {
            if(!(currentSprite == deathFront || currentSprite == deathSide || currentSprite == deathBack) && isDead()) {
                setSprite(deathFront, deathSide, deathBack);
            }
            if(currentSprite.animationBegin - System.currentTimeMillis() < -currentSprite.textures.length * 1000 / currentSprite.animation_speed
                    && (currentSprite == deathFront || currentSprite == deathSide || currentSprite == deathBack)) {
                return isDead();
            }
            return false;
        }
        
        return super.isTimeToRenew();
    }

    public boolean damage(GameObject obj, int dmg) {
        if (friendlyFire != null) {
            if (obj instanceof Bot) {
                if(Bot.contains(friendlyFire, (((Bot) obj).fraction))) return false;
            } else if (obj instanceof Player) {
                if(Bot.contains(friendlyFire, 0)) return false;
            }
        }
        
        if(dmg >= 0) {
            if (System.currentTimeMillis() - lastDamage > damageSleepTime) lastDamage = System.currentTimeMillis();
            int tf = -1;
            if(obj instanceof Player) tf = 0;
            
            if(obj instanceof Bot && !attackOnDamageOnlyPlayer) tf = ((Bot) obj).fraction;
            
            boolean canAttack = false;
            
            if(tf != -1) {
                boolean inToAttack = contains(toAttack, tf);
                canAttack |= inToAttack;
                
                if(unicalEnemies != null && !inToAttack) {
                        if(!unicalEnemies.contains(obj)) unicalEnemies.addElement(obj);
                        canAttack |= true;
                }
            }
            if(getHp() - dmg > 0) setSprite(damageFront, damageSide, damageBack);
            
            if(enemy == null && canAttack) enemy = obj;
        }
        
        return super.damage(obj, dmg);
    }
}