package code.Rendering;

/**
 *
 * @author Roman Lahin
 */
public class NearClipper {
    public static RenderVertex[] verts;
    public static int vertexCount = 3;
    
    static {
        verts = new RenderVertex[9];
        for(int i=0;i<verts.length;i++) {
            verts[i] = new RenderVertex();
        }
    }
    
    static void insertVertex(int index, 
            int x, int y, int z,
            int r, int g, int b,
            int u, int v) {
        RenderVertex newVertex = verts[verts.length - 1];
        newVertex.set(x,y,z,r,g,b,u,v);
        for(int i = verts.length - 1; i > index; i--) {
            verts[i] = verts[i - 1];
        }
        verts[index] = newVertex;
        vertexCount++;
    }
    
    static void deleteVertex(int index) {
        RenderVertex deleted = verts[index];
        for(int i = index; i < verts.length - 1; i++) {
            verts[i] = verts[i + 1];
        }
        verts[verts.length - 1] = deleted;
        vertexCount--;
    }
    
    public static void set(
            Vertex a, int au, int av,
            Vertex b, int bu, int bv,
            Vertex c, int cu, int cv,
            int ar,int br,int cr,
            int ag,int bg,int cg,
            int ab,int bb,int cb) {
        verts[0].set(a.sx,a.sy,a.rz,ar,ag,ab,au,av);
        verts[1].set(b.sx,b.sy,b.rz,br,bg,bb,bu,bv);
        verts[2].set(c.sx,c.sy,c.rz,cr,cg,cb,cu,cv);
        
        vertexCount = 3;
    }

    public static boolean clip(int clipZ) {

        boolean isCompletelyHidden = true;

        // insert vertices so all edges are either completly
        // in front or behind the clip plane
        for(int i = 0; i < vertexCount; i++) {
            int next = (i + 1) % vertexCount;
            RenderVertex v1 = verts[i];
            RenderVertex v2 = verts[next];
            if(v1.z < clipZ) isCompletelyHidden = false;
            
            // ensure v1.z < v2.z
            if(v1.z > v2.z) {
                RenderVertex temp = v1; v1 = v2; v2 = temp;
            }
            
            if(v1.z < clipZ && v2.z > clipZ) {
                int mul =  (clipZ - v1.z);
                int div = (v2.z - v1.z);
                insertVertex(next, 
                        v1.x + (mul * (v2.x - v1.x) / div), v1.y + (mul * (v2.y - v1.y) / div), clipZ,
                        v1.r + (mul * (v2.r - v1.r) / div), v1.g + (mul * (v2.g - v1.g) / div), v1.b + (mul * (v2.b - v1.b) / div),
                        v1.u + (mul * (v2.u - v1.u) / div), v1.v + (mul * (v2.v - v1.v) / div));
                // skip the vertex we just created
                i++;
            }
        }

        if(isCompletelyHidden) return false;

        // delete all vertices that have z > clipZ
        for(int i = vertexCount - 1; i >= 0; i--) {
            if(verts[i].z > clipZ) {
                deleteVertex(i);
            }
        }

        return (vertexCount >= 3);
    }
    
    public static void project(DirectX7 g3d) {
        int sx,sy,rz;
        
        for(int i=0;i<vertexCount;i++) {
            RenderVertex ver = verts[i];
            sx = ver.x;
            sy = -ver.y;
            rz = ver.z;

            if(rz <= 0) {
                sx = sx * g3d.distX / (rz + g3d.distX) ;
                sy = sy * g3d.distY / (rz + g3d.distY) ;
            } 

            ver.x = sx + g3d.centreX;
            ver.y = sy + g3d.centreY;
        }
    }

}

class RenderVertex {
    public int x,y,z;
    public int r,g,b;
    public int u,v;
    
    public void set(int x, int y, int z, int r, int g, int b, int u, int v) {
        this.x = x; this.y = y; this.z = z;
        this.r = r; this.g = g; this.b = b;
        this.u = u; this.v = v;
    }
    
}
