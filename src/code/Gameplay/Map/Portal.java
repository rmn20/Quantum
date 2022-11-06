package code.Gameplay.Map;

import code.Math.MathUtils2;
import code.Math.Matrix;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.Polygon3V;
import code.Rendering.RenderObject;
import code.Rendering.Vertex;
import code.Math.Vector3D;
import code.Rendering.Meshes.Polygon4V;
import javax.microedition.lcdui.Graphics;

/**
 *
 * @author DDDENISSS
 */
public class Portal {
    public Room room;
    private Vertex[] vers;
    private Vertex[] proj;
    private int size;
    private Vector3D nor;
    private int minx, maxx, miny, maxy, minz, maxz; //viewport

    private boolean[] overlappVertexs = new boolean[8];
    private boolean overlappCentre;
    private int centreX, centreY;
    

    public Vertex[] getVertices() {
        return this.vers;
    }

    public Portal(Vertex[] portal) {
        this.vers = portal;
        nor = createNormal(vers[0], vers[1], vers[2]);
        if(portal.length != 4) System.out.println( "PORTAL: предупреждение: нестандартное количество вершин в портале "+portal.length );
        proj = new Vertex[8];
        for (int i = 0; i < proj.length; i++) {
            proj[i] = new Vertex();
        }
    }
    private Vector3D createNormal(Vertex a, Vertex b, Vertex c) {
        Vector3D nor = new Vector3D();
        MathUtils2.calcNormal(nor, a.x, a.y, a.z, b.x, b.y, b.z, c.x, c.y, c.z);
        return nor;
    }

    public void destroy() {
        room = null;
        for (int i = 0; i < vers.length; i++) {
            vers[i] = null;
        }
        vers = null;
        proj = null;
    }


    public final Vertex[] getPortal() {
        return vers;
    }
    public final Room getRoom() {
        return room;
    }
    public final void setRoom(Room r) {
        room = r;
    }

    public final int getMinX() {
        return minx;
    }
    public final int getMinY() {
        return miny;
    }
    public final int getMaxX() {
        return maxx;
    }
    public final int getMaxY() {
        return maxy;
    }

    public final boolean isVisible(DirectX7 g3d, int x1, int y1, int x2, int y2) {
        Matrix cam = g3d.getCamera();

        
        if(MathUtils2.distanceToFace(vers[0], nor, cam.m03, cam.m13, cam.m23) < 500 && distanceToMidpoint(cam.m03, cam.m13, cam.m23)) {
            minx = x1;
            miny = y1;
            maxx = x2;
            maxy = y2;
            size = 0;
            return true;
        }

        size = proj(g3d, x1, y1, x2, y2, vers, proj);
        return isVisible2(g3d, x1, y1, x2, y2);
    }
    
    
    private boolean distanceToMidpoint(int px, int py, int pz) {
        Vertex v = vers[0];
        int x=v.x;
        int y=v.y;
        int z=v.z;
        maxx = x;
        minx = x;
        maxy = y;
        miny = y;
        minz = z;
        maxz = z;
        
        for(int i=1;i<vers.length;i++) {
            v = vers[i];
            if(v.x < minx) minx = v.x;
            if(v.x > maxx) maxx = v.x;
            if(v.y < miny) miny = v.y;
            if(v.y > maxy) maxy = v.y;
            if(v.z < minz) minz = v.z;
            if(v.z > maxz) maxz = v.z;
            
            x+=v.x;
            y+=v.y;
            z+=v.z;
        }
        x/=vers.length;
        y/=vers.length;
        z/=vers.length;

        int dx = Math.abs(px-x);
        int dy = Math.abs(py-y);
        int dz = Math.abs(pz-z);

        if(dx>maxx-minx+1000) return false;
        if(dy>maxy-miny+1000) return false;
        if(dz>maxz-minz+1000) return false;
        
        return true;
    }
    
    private final boolean isVisible2(DirectX7 g3d,int x1, int y1, int x2, int y2) { //проверить
        Vertex v = proj[0];
        
        maxx = v.sx;
        minx = v.sx;
        maxy = v.sy;
        miny = v.sy;
        minz = v.rz;
        maxz = v.rz;
        for(int i=1; i<size; i++) {
            v = proj[i];
            if(v.sx < minx) minx = v.sx;
            if(v.sx > maxx) maxx = v.sx;
            if(v.sy < miny) miny = v.sy;
            if(v.sy > maxy) maxy = v.sy;
            if(v.rz < minz) minz = v.rz;
            if(v.rz > maxz) maxz = v.rz;
        }

        return !(maxx < x1 || maxy < y1 || minx > x2 || miny > y2 || minz > 0 || -maxz > DirectX7.drDist);
    }

    private final boolean isNeighbor(RenderObject el) {
        if(el instanceof Polygon4V) {
            Polygon4V p = (Polygon4V) el;
            if( isMyVertex(p.a) ) return true;
            if( isMyVertex(p.b) ) return true;
            if( isMyVertex(p.c) ) return true;
            if( isMyVertex(p.d) ) return true;
        } else if(el instanceof Polygon3V) {
            Polygon3V p = (Polygon3V) el;
            if( isMyVertex(p.a) ) return true;
            if( isMyVertex(p.b) ) return true;
            if( isMyVertex(p.c) ) return true;
        }
        
        return false;
    }
    
    private final boolean isMyVertex(Vertex v) {
        final int E = 400;
        for (int i = 0; i < vers.length; i++) {
            Vertex t = vers[i];
            int dx = t.x-v.x;
            int dy = t.y-v.y;
            int dz = t.z-v.z;

            if(dx<0) dx = -dx;
            if(dy<0) dy = -dy;
            if(dz<0) dz = -dz;

            if( dx<E && dy<E && dz<E ) return true;
        }
        return false;
    }

    private static final boolean isFaceToDisplay(int x1, int y1, int x2, int y2, int x3, int y3) {
        return (x1-x2)*(y2-y3)  >  (y1-y2)*(x2-x3);
    }

    public void paint(Graphics g, int x, int y) {
        if(room != null) g.setColor( 0xff0000 );
        else g.setColor(0);

        for(int i=0; i<size; i++) {
            Vertex a = proj[i];
            Vertex b = proj[ (i+1)%size ];
            g.drawLine(a.sx+x, a.sy+y, b.sx+x, b.sy+y);
        }

        g.setColor(0);
        for(int i=0; i<size; i++) {
            if(!overlappVertexs[i]) continue;
            
            Vertex a = proj[i];
            g.fillRect(a.sx+x, a.sy+y, 3, 3);
        }

        if(overlappCentre && size!=0) g.fillRect(centreX + x, centreY + y, 3, 3);

    }


    static final int proj(DirectX7 g3d, int x1, int y1, int x2, int y2, Vertex[] srcVers, Vertex[] bufProj) {
        Matrix invCam = g3d.getInvCamera();
        for(int i=0; i<srcVers.length; i++)
            srcVers[i].transform(invCam);

        int size = 0;
        for(int i=0; i<srcVers.length; i++) {
            int na = i;
            int nb = i+1;
            if(nb > srcVers.length-1) nb = 0;

            Vertex a = srcVers[na];
            Vertex b = srcVers[nb];

            if(a.rz > 0 && b.rz > 0) continue; //вершины за экраном
            if(a.rz < 0 && b.rz < 0) { //вершины перед экраном
                setVertex(bufProj[size], a);
                size++;
            }
            if(a.rz < 0 && b.rz > 0) {
                setVertex(bufProj[size], a);
                size++;
                intersection(a, b, bufProj[size]);
                size++;
            }
            if(a.rz > 0 && b.rz < 0) {
                intersection(a, b, bufProj[size]);
                size++;
            }
        }


        for(int i=0; i<size; i++) {
            Vertex v = bufProj[i];
            v.project(g3d);
            if(v.rz >= 0) { //за экраном
                if( v.sx>x1 && v.sx<x2 ) {
                    v.sx = v.sx > (x2+x1)/2 ? x2 : x1;
                }
                if( v.sy>y1 && v.sy<y2 ) {
                    v.sy = v.sy > (y2+y1)/2 ? y2 : y1;
                }
            }
        }

        return size;
    }
    private static final Vertex getVertex(Vertex[] vers, int size, int i) {
        if(i<0) i+= size;
        i %= size;
        return vers[i];
    }

    private static final void intersection(Vertex a, Vertex b, Vertex res) { //найти точку пересечения с плоскостью экрана
        final int fp = 12;
        int zlen = b.rz - a.rz;
        if(zlen == 0) zlen = 1;
        int dx = ((b.sx - a.sx)<<fp) / zlen;
        int dy = ((b.sy - a.sy)<<fp) / zlen;

        int x = a.sx - ((dx * a.rz)>>fp);
        int y = a.sy - ((dy * a.rz)>>fp);

        res.x = res.sx = x;
        res.y = res.sy = y;
        res.z = res.rz = 0;
    }
    private static final void setVertex(Vertex res, Vertex src) {
        res.x = res.sx = src.sx;
        res.y = res.sy = src.sy;
        res.z = res.rz = src.rz;
    }

}

