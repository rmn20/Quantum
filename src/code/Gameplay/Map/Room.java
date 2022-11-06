package code.Gameplay.Map;

import code.Collision.HeightComputer;
import code.Collision.Height;
import code.Collision.Ray;
import code.Collision.RayCast;
import code.Rendering.MultyTexture;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.Mesh;
import code.Rendering.Meshes.Polygon3V;
import code.Rendering.RenderObject;
import code.Rendering.Vertex;
import code.Math.Vector3D;
import code.Rendering.Meshes.Polygon4V;
import code.Collision.SphereCast;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Vector;
import code.HUD.DeveloperMenu;
import code.Math.MathUtils2;
import code.Rendering.Meshes.BoundingBox;
import code.Rendering.Meshes.ColorLightedPolygon3V;
import code.Rendering.Meshes.ColorLightedPolygon4V;
import code.Rendering.Meshes.LightedPolygon3V;
import code.Rendering.Meshes.LightedPolygon4V;
import code.utils.Main;
import java.io.ByteArrayInputStream;
import javax.microedition.lcdui.Graphics;

public class Room {

    private final int id;
    private final int rid;
    private Mesh[][] mesh;
    private BoundingBox[][] bbmesh;
    public Mesh fullMesh = null;
    private Mesh[][] renderMesh;
    private BoundingBox[][] bbrenderMesh;
    private final int minx;
    private final int maxx;
    private final int minz;
    private final int maxz;
    private final int miny;
    private final int maxy;
    private boolean openSky;
    private Portal[] portals;
    public int x1, y1, x2, y2;
    public String[] stepSound = null;
    public String jumpSound = null;
    public static int chunkSize;
    public static int chunkSizeRender = 0;
    public String reverb;
    private Vector objects = new Vector();
    public Light[] lights;

    public Room(Mesh meshs, int id) {
        this.rid = id;
        this.id = id;
        this.minx = meshs.minX();
        this.maxx = meshs.maxX();
        this.minz = meshs.minZ();
        this.maxz = meshs.maxZ();
        this.miny = meshs.minY();
        this.maxy = meshs.maxY();
        this.fullMesh = meshs;
        
        if(chunkSize > 0) {
            Object[] tmp = cut(meshs, chunkSize, false);
            mesh = (Mesh[][]) tmp[0];
            bbmesh = (BoundingBox[][]) tmp[1];
        }

        if(chunkSizeRender > 0) {
            Object[] tmp = cut(meshs, chunkSizeRender, true);
            renderMesh = (Mesh[][]) tmp[0];
            bbrenderMesh = (BoundingBox[][]) tmp[1];

            if(renderMesh.length <= 2 || renderMesh[0].length <= 2) {
                renderMesh = null;
                bbrenderMesh = null;
            }
        }
        
        this.openSky = openSkyTest();
    }

    private Object[] cut(final Mesh mesh, final int size, boolean dontCopyUsed) {
        int minxx = minx;
        int maxxx = maxx;
        int minzz = minz;
        int maxzz = maxz;

        maxxx -= minxx;
        maxzz -= minzz;
        minxx = minzz = 0;
        final int sizex = maxxx - minxx;
        final int sizez = maxzz - minzz;

        final int mapw = sizex / size + 1;
        final int maph = sizez / size + 1;
        
        Mesh[][] map = new Mesh[mapw][maph];
        BoundingBox[][] bbmap = new BoundingBox[mapw][maph];
        Object[] out = new Object[]{map, bbmap};

        Vector bufVer = new Vector(mesh.getVertices().length);
        Vector bufPol = new Vector(mesh.getPolygons().length);
        
        int[] polsCount = new int[] {mesh.getPolygons().length};
        RenderObject[] pols = new RenderObject[polsCount[0]];
        System.arraycopy(mesh.getPolygons(), 0, pols, 0, pols.length);
                
        Vertex[] verts = mesh.getVertices();
        for(int i=0; i<verts.length; i++) {
            verts[i].rz = -1; //Vert id in list
        }

        for(int z = 0; z < maph; z++) {
            for(int x = 0; x < mapw; x++) {
                
                for(int i=bufVer.size()-1; i>=0; i--) {
                    ((Vertex) bufVer.elementAt(i)).rz = -1; //Vert id in list
                }
        
                bufVer.removeAllElements();
                bufPol.removeAllElements();
                
                getPolygonsInSquare(
                        x * size + minx, 
                        z * size + minz, 
                        size, 
                        pols, 
                        bufVer, bufPol, 
                        polsCount,
                        dontCopyUsed);
                
                Vertex[] nVertexs = new Vertex[bufVer.size()];
                bufVer.copyInto(nVertexs);
                RenderObject[] nPolygons = new RenderObject[bufPol.size()];
                bufPol.copyInto(nPolygons);
                
                map[x][z] = new Mesh(nVertexs, nPolygons);
                map[x][z].setTexture(mesh.getTexture());
                bbmap[x][z] = new BoundingBox(map[x][z]);
            }
        }
        return out;
    }

    private static void getPolygonsInSquare(
            int x1, int z1, int size, 
            RenderObject[] polygons, 
            Vector putVer, Vector putPol, 
            int[] polsCountArr, 
            boolean dontCopyUsed
            ) {
        
        int polsCount = polsCountArr[0];
        
        for(int i = 0; i < polsCount; i++) {
            
            RenderObject el = polygons[i];
            if(!ifItersectsSquare(el, x1, z1, size)) {
                continue;
            }
            
            if(dontCopyUsed || ifInsideSquare(el, x1, z1, size)) {
                polygons[i] = polygons[polsCount - 1];
                polygons[polsCount - 1] = el;
                
                polsCount--;
                i--;
            }
            
            putPol.addElement(el);

            if(el instanceof Polygon4V) {
                Polygon4V pol = (Polygon4V) el;
                
                if(pol.a.rz == -1) {
                    pol.a.rz = 1;
                    putVer.addElement(pol.a);
                }
                
                if(pol.b.rz == -1) {
                    pol.b.rz = 1;
                    putVer.addElement(pol.b);
                }
                
                if(pol.c.rz == -1) {
                    pol.c.rz = 1;
                    putVer.addElement(pol.c);
                }
                
                if(pol.d.rz == -1) {
                    pol.d.rz = 1;
                    putVer.addElement(pol.d);
                }
            } else if(el instanceof Polygon3V) {
                Polygon3V pol = (Polygon3V) el;
                
                if(pol.a.rz == -1) {
                    pol.a.rz = 1;
                    putVer.addElement(pol.a);
                }
                
                if(pol.b.rz == -1) {
                    pol.b.rz = 1;
                    putVer.addElement(pol.b);
                }
                
                if(pol.c.rz == -1) {
                    pol.c.rz = 1;
                    putVer.addElement(pol.c);
                }
            }
        }
        
        polsCountArr[0] = polsCount;
    }

    private static boolean ifItersectsSquare(RenderObject el, int x, int z, int size) {
        if(el instanceof Polygon4V) {
            Polygon4V pol = (Polygon4V) el;
            int minx = Math.min(pol.a.x, Math.min(pol.b.x, Math.min(pol.c.x, pol.d.x)));
            int minz = Math.min(pol.a.z, Math.min(pol.b.z, Math.min(pol.c.z, pol.d.z)));
            int maxx = Math.max(pol.a.x, Math.max(pol.b.x, Math.max(pol.c.x, pol.d.x)));
            int maxz = Math.max(pol.a.z, Math.max(pol.b.z, Math.max(pol.c.z, pol.d.z)));
            return ifItersectsSquare(x, z, size, minx, minz, maxx, maxz);
        } else if(el instanceof Polygon3V) {
            Polygon3V pol = (Polygon3V) el;
            int minx = Math.min(pol.a.x, Math.min(pol.b.x, pol.c.x));
            int minz = Math.min(pol.a.z, Math.min(pol.b.z, pol.c.z));
            int maxx = Math.max(pol.a.x, Math.max(pol.b.x, pol.c.x));
            int maxz = Math.max(pol.a.z, Math.max(pol.b.z, pol.c.z));
            return ifItersectsSquare(x, z, size, minx, minz, maxx, maxz);
        }
        return false;
    }

    private static boolean ifInsideSquare(RenderObject el, int x, int z, int size) {
        if(el instanceof Polygon4V) {
            Polygon4V pol = (Polygon4V) el;
            int minx = Math.min(pol.a.x, Math.min(pol.b.x, Math.min(pol.c.x, pol.d.x)));
            int minz = Math.min(pol.a.z, Math.min(pol.b.z, Math.min(pol.c.z, pol.d.z)));
            int maxx = Math.max(pol.a.x, Math.max(pol.b.x, Math.max(pol.c.x, pol.d.x)));
            int maxz = Math.max(pol.a.z, Math.max(pol.b.z, Math.max(pol.c.z, pol.d.z)));
            return ifInsideSquare(x, z, size, minx, minz, maxx, maxz);
        } else if(el instanceof Polygon3V) {
            Polygon3V pol = (Polygon3V) el;
            int minx = Math.min(pol.a.x, Math.min(pol.b.x, pol.c.x));
            int minz = Math.min(pol.a.z, Math.min(pol.b.z, pol.c.z));
            int maxx = Math.max(pol.a.x, Math.max(pol.b.x, pol.c.x));
            int maxz = Math.max(pol.a.z, Math.max(pol.b.z, pol.c.z));
            return ifInsideSquare(x, z, size, minx, minz, maxx, maxz);
        }
        return false;
    }

    private static boolean ifItersectsSquare(int x, int z, int size, int minx, int minz, int maxx, int maxz) {
        return !(minx > x + size || minz > z + size || maxx < x || maxz < z);
    }

    private static boolean ifInsideSquare(int x, int z, int size, int minx, int minz, int maxx, int maxz) {
        return (minx > x && minz > z && maxx < x + size && maxz < z + size);
    }

    private boolean openSkyTest() {
        final int cx = (maxx + minx) / 2;
        final int cz = (maxz + minz) / 2;
        final int cy = (maxy * 2 + miny * 4) / 6;
        
        Mesh testMesh = fullMesh;
        
        if(mesh != null) {
            int xx = (cx - minx) / chunkSize;
            int zz = (cz - minz) / chunkSize;
            
            if(xx >= mesh.length || xx < 0) return true;
            if(zz >= mesh[0].length || zz < 0) return true;
            
            testMesh = mesh[xx][zz];
        }
        
        RenderObject[] pols = testMesh.getPolygons();
        
        for(int i = 0; i < pols.length; i++) {
            RenderObject obj = pols[i];
            if(isPointOnPolygon(cx, cz, obj) && obj.ny > 2048) {
                int centerY = 0;
                
                if(obj instanceof Polygon4V) {
                    Polygon4V pol = (Polygon4V) obj;
                    centerY = (pol.a.y + pol.b.y + pol.c.y + pol.d.y) / 4;
                } else if(obj instanceof Polygon3V) {
                    Polygon3V pol = (Polygon3V) obj;
                    centerY = (pol.a.y + pol.b.y + pol.c.y) / 3;
                }

                if(cy <= centerY) {
                    return false;
                }
            }
        }
        return true;
    }

    public final void destroy() {
        this.mesh = null;
        this.fullMesh = null;
        this.bbmesh = null;
        
        this.renderMesh = null;
        this.bbrenderMesh = null;

        if(portals != null) {
            for(int var1 = 0; var1 < this.portals.length; ++var1) {
                this.portals[var1].destroy();
                this.portals[var1] = null;
            }
        }

        this.portals = null;
    }

    public final void setPortals(Portal[] portals) {
        this.portals = portals;
    }

    public final void addPortal(Portal portal) {

        Portal[] ports = new Portal[this.portals.length + 1];

        int i = 0;
        while(i < this.portals.length) {
            ports[i] = this.portals[i];
            i++;
        }
        ports[i + 1] = portal;

        this.portals = null;
        this.portals = ports;
        System.gc();
    }

    public final Portal[] getPortals() {
        return this.portals;
    }

    public final Mesh/*RoomMesh[][]*/ getMesh() {
        return this.fullMesh;
    }

    public final int getId() {
        return this.id;
    }

    public final boolean isOpenSky() {
        return this.openSky;
    }

    public final void setViewport(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public final void addViewport(int x1, int y1, int x2, int y2) {
        if(x1 < this.x1) this.x1 = x1;
        if(y1 < this.y1) this.y1 = y1;
        if(x2 > this.x2) this.x2 = x2;
        if(y2 > this.y2) this.y2 = y2;
    }

    public final boolean viewportContains(int x1, int y1, int x2, int y2) {
        return !(x1 >= this.x2 || y1 >= this.y2 || x2 < this.x1 || y2 < this.y1);
    }

    public final void render(DirectX7 g3d, int x, int z) {
        fullMesh.getTexture().updateAnimation();
        
        if(renderMesh != null) {
            int zz;
            int xx = (x - minx) / chunkSizeRender - 1;
            int zstart = (z - minz) / chunkSizeRender - 1;
            int xend = (x - minx) / chunkSizeRender + 1;
            int zend = (z - minz) / chunkSizeRender + 1;
            
            if(xx > renderMesh.length - 1) return;
            if(zstart > renderMesh[0].length - 1) return;
            if(xend < 0) return;
            if(zend < 0) return;
            
            xx = Math.min(Math.max(xx, 0), renderMesh.length - 1);
            xend = Math.min(Math.max(xend, 0), renderMesh.length - 1);
            zstart = Math.min(Math.max(zstart, 0), renderMesh[0].length - 1);
            zend = Math.min(Math.max(zend, 0), renderMesh[0].length - 1);

            for(; xx <= xend; xx++) {
                for(zz = zstart; zz <= zend; zz++) {
                    if(bbrenderMesh[xx][zz].isVisible(g3d, x1, y1, x2, y2)) {
                        g3d.transformAndProjectVertices(renderMesh[xx][zz], g3d.getInvCamera());
                        g3d.addMesh(renderMesh[xx][zz], x1, y1, x2, y2);
                        renderMesh[xx][zz].applySz();
                    }
                }
            }
        } else {
            g3d.transformAndProjectVertices(fullMesh, g3d.getInvCamera());
            g3d.addMesh(fullMesh, x1, y1, x2, y2);
            fullMesh.applySz();
        }
    }

    public final void renderObjects(DirectX7 g3d) {
        renderObjects(g3d, x1, y1, x2, y2);
    }

    public final void renderObjects(DirectX7 g3d, int x1, int y1, int x2, int y2) {
        for(int i = 0; i < objects.size(); i++) {
            RoomObject obj = (RoomObject) objects.elementAt(i);
            obj.render(g3d, x1, y1, x2, y2);
        }
    }

    /* void rayCast(Ray ray)
     * и из RayCast
     * public static boolean isRayAABBCollision(Ray ray, int minx, int maxx, int minz, int maxz)
     */
    public final void rayCast(Ray ray) {
        if(mesh != null) {
            Vector3D start = ray.getStart();
            Vector3D end = ray.getDir();
            
            int minx = (Math.min(start.x, start.x + end.x) - this.minx) / chunkSize;
            int minz = (Math.min(start.z, start.z + end.z) - this.minz) / chunkSize;
            int maxx = (Math.max(start.x, start.x + end.x) - this.minx) / chunkSize;
            int maxz = (Math.max(start.z, start.z + end.z) - this.minz) / chunkSize;
            
            minx = Math.min(Math.max(minx, 0), mesh.length - 1);
            minz = Math.min(Math.max(minz, 0), mesh[0].length - 1);
            maxx = Math.min(Math.max(maxx, 0), mesh.length - 1);
            maxz = Math.min(Math.max(maxz, 0), mesh[0].length - 1);
            
            for(int xx = minx; xx <= maxx; xx++) {
                for(int zz = minz; zz <= maxz; zz++) {
                    RayCast.rayCast(mesh[xx][zz], ray, rid);
                }
            }
        } else {
            RayCast.rayCast(fullMesh, ray, rid);
        }
    }

    /* boolean sphereCast(Vector3D pos, int rad)
     * и из SphereCast
     * public static boolean isSphereAABBCollision(Vector3D pos, int rad, int minx, int maxx, int minz, int maxz)
     */
    public final boolean sphereCast(Vector3D pos, int rad) {
        boolean collision = false;
        
        if(this.mesh != null) {
            int xx = 0;
            int zz = 0;
            int zstart = 0;
            int xend = mesh.length - 1;
            int zend = mesh[0].length - 1;

            xx = (pos.x - rad - minx) / chunkSize;
            zstart = (pos.z - rad - minz) / chunkSize;
            xend = (pos.x + rad - minx) / chunkSize;
            zend = (pos.z + rad - minz) / chunkSize;
            
            if(xx > mesh.length - 1) return false;
            if(zstart > mesh[0].length - 1) return false;
            if(xend < 0) return false;
            if(zend < 0) return false;
            
            xx = Math.min(Math.max(xx, 0), mesh.length - 1);
            xend = Math.min(Math.max(xend, 0), mesh.length - 1);
            zstart = Math.min(Math.max(zstart, 0), mesh[0].length - 1);
            zend = Math.min(Math.max(zend, 0), mesh[0].length - 1);

            for(; xx <= xend; xx++) {
                for(zz = zstart; zz <= zend; zz++) {
                    collision |= pos.x + rad >= bbmesh[xx][zz].minx
                            && pos.z + rad >= bbmesh[xx][zz].minz
                            && pos.x - rad <= bbmesh[xx][zz].maxx
                            && pos.z - rad <= bbmesh[xx][zz].maxz ? SphereCast.sphereCast(mesh[xx][zz], pos, rad) : false;
                }
            }
        } else {
            collision |= pos.x + rad >= minx
                    && pos.z + rad >= minz
                    && pos.x - rad <= maxx
                    && pos.z - rad <= maxz ? SphereCast.sphereCast(fullMesh, pos, rad) : false;
        }
        
        return collision;
    }

    public final boolean isPointInRoomBox(int x, int y, int z) {
        return x >= minx && z >= minz && y >= miny && x <= maxx && z <= maxz && (openSky || y <= maxy);
    }

    public final int isPointOnMesh(int x, int y, int z) {
        if(!isPointInRoomBox(x, y, z)) {
            return -1;
        }
        
        Mesh testMesh = fullMesh;
        
        if(mesh != null) {
            int xx = (x - minx) / chunkSize;
            int zz = (z - minz) / chunkSize;
            
            if(xx >= mesh.length || xx < 0) return -1;
            if(zz >= mesh[0].length || zz < 0) return -1;
            
            testMesh = mesh[xx][zz];
        }

        RenderObject[] pols = testMesh.getPolygons();

        for(int i = 0; i < pols.length; i++) {
            if(isPointOnPolygon(x, y, z, pols[i])) {
                return i;
            }
        }

        return -1;
    }

    public void computeHeight(Height height) {
        Vector3D pos = height.getPosition();
        if(mesh != null) {
            int xx = (pos.x - minx) / chunkSize;
            int zz = (pos.z - minz) / chunkSize;
            if(xx >= mesh.length || xx < 0) return;
            if(zz >= mesh[0].length || zz < 0) return;
            
            if(HeightComputer.isPointAABBCollision(pos.x, pos.z, bbmesh[xx][zz].minx - 500, bbmesh[xx][zz].maxx + 500, bbmesh[xx][zz].minz - 500, bbmesh[xx][zz].maxz + 500)) {
                HeightComputer.computeHeight(mesh[xx][zz], height);
            }
        } else {
            if(HeightComputer.isPointAABBCollision(pos.x, pos.z, minx - 500, maxx + 500, minz - 500, maxz + 500)) {
                HeightComputer.computeHeight(this.fullMesh, height);
            }
        }
    }

    private static boolean isPointOnPolygon(int x, int z, RenderObject obj) {
        Vertex v1;
        Vertex v2;
        Vertex v3;
        Vertex v4;
        
        if(obj instanceof Polygon4V) {
            Polygon4V p = (Polygon4V) obj;
            v1 = p.a;
            v2 = p.b;
            v3 = p.c;
            v4 = p.d;
            return MathUtils2.isPointOnPolygon(x, z,
                    v1.x, v1.z,
                    v2.x, v2.z,
                    v3.x, v3.z,
                    v4.x, v4.z,
                    p.ny);
        } else if(obj instanceof Polygon3V) {
            Polygon3V p = (Polygon3V) obj;
            v1 = p.a;
            v2 = p.b;
            v3 = p.c;
            return MathUtils2.isPointOnPolygon(x, z,
                    v1.x, v1.z,
                    v2.x, v2.z,
                    v3.x, v3.z,
                    p.ny);
        }
        
        return false;
    }

    private static boolean isPointOnPolygon(int x, int y, int z, RenderObject obj) {
        Vertex v1;
        Vertex v2;
        Vertex v3;
        Vertex v4;
        
        if(obj instanceof Polygon4V) {
            Polygon4V p = (Polygon4V) obj;
            if(p.ny >= 0) {
                return false;
            }
            v1 = p.a;
            v2 = p.b;
            v3 = p.c;
            v4 = p.d;
            if(MathUtils2.computePolygonY(v1, p.nx, p.ny, p.nz, x, y, z) <= y) {
                return MathUtils2.isPointOnPolygon(x, z, v4.x, v4.z, v3.x, v3.z, v2.x, v2.z, v1.x, v1.z);
            }

            return false;
        } else if(obj instanceof Polygon3V) {
            Polygon3V p = (Polygon3V) obj;
            if(p.ny >= 0) {
                return false;
            }
            v1 = p.a;
            v2 = p.b;
            v3 = p.c;
            if(MathUtils2.computePolygonY(v1, p.nx, p.ny, p.nz, x, y, z) <= y) {
                return MathUtils2.isPointOnPolygon(x, z, v3.x, v3.z, v2.x, v2.z, v1.x, v1.z);
            }

            return false;
        }

        return false;
    }

    public final int getMinX() {
        return this.minx;
    }

    public final int getMaxZ() {
        return this.maxz;
    }

    public final int getMinZ() {
        return this.minz;
    }

    public final int getMaxX() {
        return this.maxx;
    }

    public final int getMinY() {
        return this.miny;
    }

    public final int getMaxY() {
        return this.maxy;
    }

    public boolean isOnRoom(int x, int y) {
        return !(x > x2 + 10
                || x < x1 - 10
                || y > y2 + 10
                || y < y1 - 10);
    }

    public void addObject(RoomObject obj) {
        if(!objects.contains(obj)) {
            objects.addElement(obj);
        } else {
            if(DeveloperMenu.debugMode) {
                System.out.println("Room: такой обьект уже содержится");
            }
        }
    }

    public void removeObject(RoomObject obj) {
        boolean remove = objects.removeElement(obj);
        if(!remove && DeveloperMenu.debugMode) {
            System.out.println("Room: такого обьекта не было");
        }
    }

    public Vector getObjects() {
        return objects;
    }

    public void getObjects(Vector buf) {
        for(int i = 0; i < objects.size(); i++) {
            RoomObject obj = (RoomObject) objects.elementAt(i);
            if(!buf.contains(obj)) {
                buf.addElement((RoomObject) obj);
            }
        }
    }

    public static Mesh[] loadMeshes(String file, float scaleX, float scaleY, float scaleZ) {
        return loadMeshes(file, scaleX, scaleY, scaleZ, null);
    }

    public static Mesh[] loadMeshes(String file, float scaleX, float scaleY, float scaleZ, MultyTexture mt) {
        Mesh[] meshes = null;
        InputStream is = null;
        DataInputStream dis = null;
        ByteArrayInputStream bais = null;
        
        try {
            is = (new Object()).getClass().getResourceAsStream(file);
            dis = new DataInputStream(is);
            byte[] data = new byte[dis.available()];
            dis.read(data);
            dis.close();
            
            bais = new ByteArrayInputStream(data);
            dis = new DataInputStream(bais);
            
            meshes = new Mesh[dis.readInt()];

            for(int i = 0; i < meshes.length; ++i) {
                meshes[i] = createFrom3d(file, dis, scaleX, scaleY, scaleZ, mt);
            }
        } catch(Exception ex) {
            System.err.println("ERROR in Loader.Load: " + ex);
        } finally {
            try {
                dis.close();
            } catch(Exception ex) {}
            
            try {
                bais.close();
            } catch(Exception ex) {}
        }

        return meshes;
    }

    private static Mesh createFrom3d(
            String file, DataInputStream is,
            float scaleX, float scaleY, float scaleZ,
            MultyTexture mt) throws Exception {
        Vertex[] verts = new Vertex[is.readShort()];

        for(int i = 0; i < verts.length; ++i) {
            int x = (int) (is.readShort() * scaleX);
            int y = (int) (is.readShort() * scaleY);
            int z = (int) (is.readShort() * scaleZ);
            verts[i] = new Vertex(x, y, z);
        }

        Polygon3V[] pols3v = new Polygon3V[is.readShort()];
        int mat = 0;

        for(int i = 0; i < pols3v.length; ++i) {
            short va = is.readShort();
            if(va == -32768) {
                mat = (int) (is.readShort());
                va = is.readShort();
            }

            short vb = is.readShort();
            short vc = is.readShort();

            byte au = is.readByte();
            byte av = is.readByte();
            byte bu = is.readByte();
            byte bv = is.readByte();
            byte cu = is.readByte();
            byte cv = is.readByte();

            Vertex a = verts[va];
            Vertex b = verts[vb];
            Vertex c = verts[vc];

            if(DirectX7.standartDrawmode == 9 && Main.fogQ >= 1) {
                pols3v[i] = new LightedPolygon3V(c, b, a, cu, cv, bu, bv, au, av);
            } else if(DirectX7.standartDrawmode == 13 && Main.fogQ >= 1) {
                pols3v[i] = new ColorLightedPolygon3V(c, b, a, cu, cv, bu, bv, au, av);
            } else {
                pols3v[i] = new Polygon3V(c, b, a, cu, cv, bu, bv, au, av);
            }

            pols3v[i].tex = (byte) mat;

        }

        Polygon4V[] pols4v = new Polygon4V[is.readShort()];
        mat = 0;

        for(int i = 0; i < pols4v.length; ++i) {
            short va = is.readShort();
            if(va == -32768) {
                mat = (int) (is.readShort());
                va = is.readShort();
            }
            short vb = is.readShort();
            short vc = is.readShort();
            short vd = is.readShort();
            
            byte au = is.readByte();
            byte av = is.readByte();
            byte bu = is.readByte();
            byte bv = is.readByte();
            byte cu = is.readByte();
            byte cv = is.readByte();
            byte du = is.readByte();
            byte dv = is.readByte();
            
            Vertex a = verts[va];
            Vertex b = verts[vb];
            Vertex c = verts[vc];
            Vertex d = verts[vd];

            if(DirectX7.standartDrawmode == 9 && Main.fogQ >= 1) {
                pols4v[i] = new LightedPolygon4V(d, c, b, a, du, dv, cu, cv, bu, bv, au, av);
            } else if(DirectX7.standartDrawmode == 13 && Main.fogQ >= 1) {
                pols4v[i] = new ColorLightedPolygon4V(d, c, b, a, du, dv, cu, cv, bu, bv, au, av);
            } else {
                pols4v[i] = new Polygon4V(d, c, b, a, du, dv, cu, cv, bu, bv, au, av);
            }

            pols4v[i].tex = (byte) mat;
        }

        RenderObject[] var30 = new RenderObject[pols3v.length + pols4v.length];
        int pols = 0;

        for(int i = 0; i < pols3v.length; ++i) {
            var30[pols] = pols3v[i];
            pols++;
        }

        for(int i = 0; i < pols4v.length; ++i) {
            var30[pols] = pols4v[i];
            pols++;
        }

        System.out.println("Mesh [" + file + "] вершин: " + verts.length + " полигонов: " + var30.length);
        return new Mesh(verts, var30, mt);
    }

    public void paint(Graphics g, int x, int y) {
        g.setColor(0x00ff00);
        g.drawRect(x + x1, y + y1, x2 - x1 - 1, y2 - y1 - 1);
        g.setColor(0xffffff);
    }
}
