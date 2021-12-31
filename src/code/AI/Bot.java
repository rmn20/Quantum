package code.AI;

import code.AI.misc.Blood;
import code.AI.misc.Trace;
import code.Collision.Ray;
import code.Gameplay.Map.House;
import code.Gameplay.Map.Portal;
import code.Gameplay.Map.Scene;
import code.Gameplay.Objects.GameObject;
import code.Math.MathUtils;
import code.Math.Matrix;
import code.Math.Vector3D;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.Mesh;
import code.Rendering.Meshes.Polygon4V;
import code.Rendering.RenderObject;
import code.Rendering.Vertex;
import code.utils.FPS;
import code.utils.Main;
import java.util.Vector;

public abstract class Bot extends GameObject {

    public static boolean cleverPathfinfing = false;
    private static final Ray ray = new Ray();
    private static final Vector3D tmpVec = new Vector3D(); 
    private static final Vector3D side = new Vector3D();
    private static final Vector3D up = new Vector3D();
    private static final Vector3D dir = new Vector3D();

    private Blood blood = new Blood(this);
    private final Vector3D bloodSpeed = new Vector3D();
    private final Vector3D bloodPos = new Vector3D();
    private Trace bloodwall = null;
    public int fraction = 0;
    public byte deathFall = 1;
    public boolean visiblityCheck = false;
    public boolean hasBlood = true;
    //public int[] observable;

    public void set(Vector3D pos) {
        character.reset();
        character.getTransform().setPosition(pos.x, pos.y, pos.z);
        setPart(-1);
        blood.reset();
    }

    public void destroy() {
        blood.destroy();
        bloodwall = null;
        blood = null;
    }

    protected final boolean isNeedRecomputePart() {
        return super.isNeedRecomputePart();
    }

    protected final void renderBlood(DirectX7 g3d, int sz) {
        if(blood.isBleeding()) {
            blood.render(g3d, 3250, bloodPos);
            bloodPos.add(-bloodSpeed.x * FPS.frameTime / 50, -bloodSpeed.y * FPS.frameTime / 50, -bloodSpeed.z * FPS.frameTime / 50);
        }

    }

    public boolean damage(GameObject obj, int dmg) {
        if (dmg > 0) {
            Matrix mat = character.getTransform();

            if(hasBlood) {
                bloodPos.set(mat.m03, mat.m13 + character.getHeight(), mat.m23);
    
                if(obj != null) {
                    Matrix mat2 = obj.getCharacter().getTransform();
                    tmpVec.set(mat.m03 - mat2.m03, mat.m13 - mat2.m13, mat.m23 - mat2.m23);
    
                    tmpVec.setLength2(dmg * character.getRadius() / 400);
                    character.getSpeed().set(tmpVec.x, tmpVec.y, tmpVec.z);
    
                    tmpVec.setLength2(60);
                    bloodSpeed.set(tmpVec);
                }
    
                blood.bleed();
            }
        }
        return super.damage(obj, dmg);
    }

    // Поведение (движение и поиск врага или падение при 0 hp)
    public final void update(Scene scene, Player player) {
        if (!isDead()) action(scene);
        else {
            if(bloodwall==null && Main.blood && hasBlood) if(character.getTransform().m11 == 16384 || deathFall==0) spawnBlood(scene);
            if(deathFall == 1) drop(scene);
            else if(deathFall == 2) dropSide(scene);
        }
        super.update(scene, player);
    }

    protected abstract void action(Scene scene);

    protected void spawnBlood(Scene scene) {
            ray.reset();
            Matrix mat = this.getCharacter().getTransform();
            ray.getStart().set(mat.m03, mat.m13 + 1, mat.m23);
            ray.getDir().set(0, -10, 0);
            ray.reset();
            scene.getHouse().rayCast(getPart(), ray, false);

            if (ray.isCollision()) {
                bloodwall = createTrace(ray.getCollisionPoint(), ray.getTriangle());
                bloodwall.setPart(ray.getNumRoom());
                scene.getHouse().addObject(bloodwall);
            }
    }

    // Падение при 0 hp

    protected void drop(Scene scene) {
        if(character.getTransform().m11 > 0) {
            character.drop(-8);
        }
    }
    
    protected void dropSide(Scene scene) {
        if(character.getTransform().m11 > 0) {
            character.dropSide(-8);
        }
    }

    protected final boolean notCollided(House house, GameObject obj) {
        if(!cleverPathfinfing) {
            return getPart()==obj.getPart();
        }
        Matrix objMat = obj.getCharacter().getTransform();
        Matrix mat = character.getTransform();
        ray.reset();
        ray.getStart().set(mat.m03, mat.m13 + character.getHeight(), mat.m23);
        ray.getDir().set(objMat.m03 - mat.m03, objMat.m13 - mat.m13, objMat.m23 - mat.m23);
        house.rayCast(getPart(), ray, false);
        return !ray.isCollision();
    }

    protected GameObject findBot(Vector objs, GameObject ignore, int[] fractions) {
        long dis = Long.MAX_VALUE;
        GameObject bot = null;
        if (objs.isEmpty()) return null;
        
        for(int i = 0; i < objs.size(); ++i) {
            if(objs.elementAt(i) instanceof GameObject) {
                GameObject obj = (GameObject) objs.elementAt(i);

                if(!obj.isDead() && (obj instanceof Bot || obj instanceof Player) && obj != ignore) {
                    long dis2 = getCharacter().distance(obj.getCharacter());
                    if(dis2 >= dis) continue;

                    if(visiblityCheck && !canSeeCheck(this, obj)) continue;

                    if(obj instanceof Bot) {
                        if(contains(fractions, ((Bot) obj).fraction)) {
                            dis = dis2;
                            bot = obj;
                        }
                    } else {
                        if(contains(fractions, 0)) {
                            dis = dis2;
                            bot = obj;
                        }
                    }

                }
            }
        }
        
        return bot;
    }
    
    public static boolean canSeeCheck(GameObject observer, GameObject observable) {
        int lookDir = MathUtils.getAnglez(
                0, 0, 
                observer.getCharacter().transform.m02, 
                observer.getCharacter().transform.m22);

        int observableDir = MathUtils.getAnglez(
                observer.getCharacter().transform.m03, 
                observer.getCharacter().transform.m23,
                observable.getCharacter().transform.m03, 
                observable.getCharacter().transform.m23);

        int angleDistance = MathUtils.angleDistance(lookDir, observableDir);

        boolean result = angleDistance < 90;

        result |= observer.character.distance(observable.character) < 150;
        
        return result;
    }

    
    protected final void lookAt(int x, int z) {
        Matrix pos = this.getCharacter().getTransform();

        dir.set(pos.m03 - x, 0, pos.m23 - z); //направление до цели
        dir.setLength(-Matrix.FP);

        if (dir.x == 0 && dir.y == 0) return;
        setDir(pos, dir.x, dir.z);
    }
    private void setDir(Matrix m, int dirX, int dirZ) {
        dir.set(dirX, 0, dirZ);
        dir.setLength(Matrix.FP);
        if (equals(dir.x, dir.y, dir.z, m.m02, m.m12, m.m22)) return;
        
        up.set(0, Matrix.FP, 0);

        side.cross(up, dir, Matrix.fp);
        side.setLength(Matrix.FP);

        if (dir.lengthSquared() != 0 && side.lengthSquared() != 0) {
            m.setDir(dir.x, dir.y, dir.z);
            m.setSide(side.x, side.y, side.z);
            m.setUp(up.x, up.y, up.z);
        }
    }

    protected static void increaseMeshSz(Mesh mesh, int z) {
        RenderObject[] objs = mesh.getPolygons();
        for(int i=0;i<objs.length;i++) {
            objs[i].sz += z;
        }
    }

    // Возвращает портал, соединяющий комнаты по номерами part1 и part2. Если общего портала нет, возращает null
    protected static Portal commonPortal(House house, int part1, int part2) {
        Portal[] portals = house.getRooms()[part1].getPortals(); //
        if(portals==null) return null;
        
        for (int i=0;i<portals.length;i++) {
            Portal portal=portals[i];
            if (portal.getRoom()!=null && portal.getRoom().getId() == part2) return portal;
        }
        return null;
    }

    // из MeshImage: private static Vertex computeCentre(Vertex[] vertices) {
    protected static void computeCentre(Portal portal, Vector3D center) {
        Vertex[] vers = portal.getVertices();
        int sx=0, sy=0, sz=0;
        for (int i=0; i<vers.length; i++) {
            Vertex v = vers[i];
            sx += v.x;
            sy += v.y;
            sz += v.z;
        }
        sx /= vers.length;
        sy /= vers.length;
        sz /= vers.length;
        center.set(sx, sy, sz);
    }

    public static boolean contains(int[] list, int need) {
        if (list == null) return true;
        for (int i=0; i<list.length; i++) {
            if (list[i] == need) return true;
        }
        return false;
    }

    private static boolean equals(int ax, int ay, int az, int bx, int by, int bz) {
        return (Math.abs(ax - bx) < 20
                && Math.abs(ay - by) < 20
                && Math.abs(az - bz) < 20);
    }

    protected static long sqr(int x) {
        return (long) x * (long) x;
    }    
    
    public Trace createTrace(Vector3D vector3f, RenderObject meshr) {
        int minx = 0;
        int miny = 0;
        int minz = 0;
        int maxx = 0;
        int maxy = 0;
        int maxz = 0;
        int posx,posy,posz;
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
        return new Trace(vector3f.x, vector3f.y, vector3f.z, v1, v2, v3, v4, new Vector3D(Math.abs(minx) + Math.abs(maxx), Math.abs(miny) + Math.abs(maxy), Math.abs(minz) + Math.abs(maxz)));
    }


}
