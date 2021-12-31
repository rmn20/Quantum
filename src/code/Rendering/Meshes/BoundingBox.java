package code.Rendering.Meshes;

import code.Math.Matrix;
import code.Rendering.DirectX7;
import code.Rendering.Vertex;


/**
 *
 * @author DDDENISSS
 */
public class BoundingBox {
    
    private static Vertex[] vertices = new Vertex[] {
            new Vertex(0, 0, 0),
            new Vertex(0, 0, 0),
            new Vertex(0, 0, 0),
            new Vertex(0, 0, 0),
            
            new Vertex(0, 0, 0),
            new Vertex(0, 0, 0),
            new Vertex(0, 0, 0),
            new Vertex(0, 0, 0)
        };
    public int minx, miny, maxx, maxy, minz, maxz;
    private static int minpx, minpy, maxpx, maxpy, minpz, maxpz;
    
    public BoundingBox(Mesh mesh) {
        minx = mesh.minX();
        miny = mesh.minY();
        minz = mesh.minZ();
        maxx = mesh.maxX();
        maxy = mesh.maxY();
        maxz = mesh.maxZ();
    }

    public BoundingBox(Morphing anim) {
        minx = Integer.MAX_VALUE;
        miny = Integer.MAX_VALUE;
        minz = Integer.MAX_VALUE;
        
        maxx = Integer.MIN_VALUE;
        maxy = Integer.MIN_VALUE;
        maxz = Integer.MIN_VALUE;
        
        short[][] vertices = anim.getVertices();
        for (int j = 0; j < vertices.length; j++) {
            short[] vers = vertices[j];
            for (int i = 0; i < vers.length/3; i++) {
                int x = vers[i*3];
                int y = vers[i*3+1];
                int z = vers[i*3+2];
                
                minx = Math.min(minx, x);
                miny = Math.min(miny, y);
                minz = Math.min(minz, z);
                
                maxx = Math.max(maxx, x);
                maxy = Math.max(maxy, y);
                maxz = Math.max(maxz, z);
            }
        }
    }
    
    public void set(int minX, int minY, int minZ,
                    int maxX, int maxY, int maxZ) {
        vertices[0].set(minX, minY, minZ);
        vertices[1].set(maxX, minY, minZ);
        vertices[2].set(minX, minY, maxZ);
        vertices[3].set(maxX, minY, maxZ);
            
        vertices[4].set(minX, maxY, minZ);
        vertices[5].set(maxX, maxY, minZ);
        vertices[6].set(minX, maxY, maxZ);
        vertices[7].set(maxX, maxY, maxZ);
    }
    
    public boolean isVisible(DirectX7 g3d, int x1, int y1, int x2, int y2) {
        return isVisible(g3d,g3d.invCamera,x1,y1,x2,y2);
    }
    
    public boolean isVisible(DirectX7 g3d, Matrix matrix, int x1, int y1, int x2, int y2) {
        set(minx, miny, minz, maxx, maxy, maxz);
        g3d.transformAndProjectVertices( vertices, matrix );
        
        Vertex v = vertices[0];
        int minX,maxX;
        minX = maxX = v.sx;
        int minY,maxY;
        minY = maxY = v.sy;
        int minZ,maxZ;
        minZ = maxZ = v.rz;
        for (int i = 1; i < vertices.length; i++) {
            v = vertices[i];
            if(v.sx < minX) minX = v.sx;
            if(v.sy < minY) minY = v.sy;
            if(v.rz < minZ) minZ = v.rz;
            if(v.rz > maxZ) maxZ = v.rz;
            
            if(v.sx > maxX) maxX = v.sx;
            if(v.sy > maxY) maxY = v.sy;
        }
        return !( maxX < x1 || 
                  minX > x2 || 
                  maxY < y1 || 
                  minY > y2 || 
                  minZ >= 0 ||
                  -maxZ>DirectX7.drDist);
    }
    
    public void reSort(Matrix matrix) {
        set(minx, miny, minz, maxx, maxy, maxz);
        DirectX7.transformSave( vertices, matrix );
        
        Vertex v = vertices[0];
        minpx = maxpx = v.x;
        minpy = maxpy = v.y;
        minpz = maxpz = v.z;
        for (int i = 1; i < vertices.length; i++) {
            v = vertices[i];
            if(v.x < minpx) minpx = v.x;
            if(v.y < minpy) minpy = v.y;
            if(v.z < minpz) minpz = v.z;
            
            if(v.x > maxpx) maxpx = v.x;
            if(v.y > maxpy) maxpy = v.y;
            if(v.z > maxpz) maxpz = v.z;
        }
    }

    public int getMinX() {
        return minpx;
    }

    public int getMinY() {
        return minpy;
    }

    public int getMinZ() {
        return minpz;
    }
    
    public int getMaxZ() {
        return maxpz;
    }

    public int getMaxX() {
        return maxpx;
    }

    public int getMaxY() {
        return maxpy;
    }
    
    
}
