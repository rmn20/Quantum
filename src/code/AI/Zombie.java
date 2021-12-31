package code.AI;

import code.Gameplay.Map.House;
import code.Gameplay.Map.Portal;
import code.Gameplay.Map.Scene;
import code.Gameplay.Objects.GameObject;
import code.Math.Matrix;
import code.Math.Vector3D;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.BoundingBox;
import code.Rendering.Meshes.MeshImage;
import code.Rendering.MultyTexture;
import code.utils.FPS;
import java.util.Vector;

public final class Zombie extends Bot {

    public static byte fallDeath = 1;
    public static boolean bloodHas = true;
    public static int maxHP = 400;
    public static int model_height = 2000;
    private static BoundingBox boundingBox;
    public static int[] attackTo;
    public static int enemyReaction = 2; //2
    public static int animSpeed = 140; //140
    public static int attackDamage = 1; //1
    public static int walkSpeed = 140; //140
    public static int attackAnimSpeed = 700; //700
    public static float jumpSpeed = 1.2F; //1.2F
    public static int reactTimer = 8; //8
    public static MultyTexture texture;
    public static int jumpHeight = 140; //140
    public static int attackState = 2; //2
    public static MeshImage model;
    public static float attackRadius = 1.2F; //1.2F
    public static int moneyOnDeath = 10; //10
    public static int attackTimer = 8; //8
    public static int AI = 1; //1
    private int state = -1; // состояние (-1 - стоять, 1 - идти, 2 - атаковать игрока)
    public MeshImage meshImage;
    private GameObject enemy = null;
    private boolean notCol = false;
    private final Vector3D dir = new Vector3D();

    {
        if (model != null) {
            meshImage = new MeshImage(model.getMesh(), model.getAnimation());
            boundingBox = new BoundingBox(model.getAnimation());
        }
    }

    public Zombie(Vector3D pos) {
        name = "ZOMBIE";
        fraction = 1;
        character.getTransform().setIdentity();
        set(pos);
        deathFall=Zombie.fallDeath;
        hasBlood = BigZombie.bloodHas;
    }

    public final void set(Vector3D pos) {
        super.set(pos);
        setHp(maxHP);
        setCharacterSize(model_height);
        dir.set(pos.x, pos.y, pos.z + 50);
        lookAt(pos.x, pos.z + 50);
        notCol = false;
    }

    public final void destroy() {
        super.destroy();
        enemy = null;
        meshImage = null;
    }

    public final void render(DirectX7 g3d, int x1, int y1, int x2, int y2) {
        if (!visible) return;
        
        Matrix mat = getCharacter().getTransform();
        Matrix finalMatrix = g3d.computeFinalMatrix(mat);
        if (boundingBox.isVisible(g3d, finalMatrix, x1, y1, x2, y2) == false) return;
        
        if (mat.m11 == 16384) {
            if (state == 1) meshImage.setFrame(getFrameInter() * animSpeed / 100);
            if (state == 2) meshImage.setFrame(getFrameInter() * attackAnimSpeed / 100);
        }

        meshImage.setMatrix(finalMatrix);
        meshImage.setTexture(texture);

        g3d.addRenderObject(meshImage, x1, y1, x2, y2);

        meshImage.sz += this.character.getRadius();
        if (character.oldFloorPoly != null && character.oldFloorPoly.sz > meshImage.sz) character.oldFloorPoly.sz = meshImage.sz - 1;
       
       
        renderBlood(g3d, 1500);
    }

    protected final void action(Scene scene) {
        if (Math.abs(getFrameInterDiv()) % reactTimer == 1) {
            House house = scene.getHouse();
            Vector objects = house.getObjects();
            if (enemy != null && enemy.isDead() || enemy != null && FPS.frames == 0) {
                enemy = null;
            }

            enemy = findBot(objects, this, attackTo);

            if (enemy != null) {
                Vector3D walkTo = this.dir;
                if (notCollided(house, enemy) && scene.getHouse().isNear(getPart(), enemy.getPart())) {
                    Matrix var8 = enemy.getCharacter().getTransform();
                    walkTo.set(var8.m03, var8.m13, var8.m23);
                    notCol = true;
                } else {
                    int nextPart = scene.getNext(getPart(), enemy.getPart());
                    Portal portal=commonPortal(house, getPart(), nextPart);
                    if (portal != null) computeCentre(portal, walkTo);
                    notCol = false;
                }

                lookAt(dir.x, dir.z);

                long distance = character.distance(enemy.getCharacter());
                if (notCol && distance <= sqr(character.getRadius() + enemy.getCharacter().getRadius()) * attackRadius) {
                    state = enemyReaction;
                } else {
                    if (character.isCollision()) 
                        character.jump(jumpHeight, jumpSpeed);
                    state = AI;
                }
            } else state = -1;
        }

        if (state == 1) moveZ(walkSpeed);
        if (state == attackState && getFrameInterDiv() % attackTimer == 0 && notCol) 
            enemy.damage(this, attackDamage);
        

    }

    protected final void drop(Scene scene) {
        super.drop(scene);
        state = -1;
    }

}
