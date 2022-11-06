package code.Collision;

import code.Math.MathUtils2;
import code.Math.Matrix;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.Mesh;
import code.Rendering.Meshes.Polygon3V;
import code.Rendering.RenderObject;
import code.Rendering.Vertex;
import code.Math.Vector3D;
import code.Rendering.Meshes.Polygon4V;
import code.Rendering.Texture;


/**
 * класс для вычисления высоты меша
 * @author DDDENISSS
 */
public class HeightComputer {
    /*
     * класс вычисляет самую высокую точку меша в точку XZ, но ниже Y координаты
     */
    
    private static final Vector3D nor = new Vector3D();
    
    private static final Height height = new Height();
    
    public static boolean isPointAABBCollision(int x, int z, int minx, int maxx, int minz, int maxz) {
        return !(x < minx || z < minz || x > maxx || z > maxz);
    }
    
    
    /**
     * вычислить высоту меша в точке x, y, z. Высота вычислится только, если больше, чем y
     * @param mesh меш
     * @param x позиция
     * @param y позиция
     * @param z позиция
     * @return высота
     */
    public static int computeHeight(Mesh mesh, int x, int y, int z) {
        height.reset();
        height.getPosition().set(x, y, z);
        computeHeight(mesh, height);
        return height.getHeight();
    }
    
    
    public static void computeHeight(Mesh mesh, Matrix matrix, Height height) { //вычислить Y координату в точке XZ.
        computeHeight(mesh, matrix, height, true);
    }
    
    public static void computeHeight(Mesh mesh, Height height) {
        computeHeight(mesh, null, height, true);
    }
    
    public static void computeHeight(Mesh mesh, Matrix matrix, Height height, boolean updatePos) { //вычислить Y координату в точке XZ.
        if(matrix!=null) DirectX7.transformSave(mesh,matrix);
        final RenderObject[] polygons = mesh.getPolygons();
        Vector3D pos = height.getPosition();
        final int x = pos.x, y = pos.y, z = pos.z;
        
        
        for(int i=0; i<polygons.length; i++) {
            RenderObject el = polygons[i];
            int polyY = Integer.MAX_VALUE;
            int cx = Integer.MAX_VALUE, cz = Integer.MAX_VALUE, cy = Integer.MAX_VALUE;
            int mat = -1;
            
            if(el instanceof Polygon4V) {
                Polygon4V p = (Polygon4V) el;
                Vertex a = p.a;
                Vertex b = p.b;
                Vertex c = p.c;
                Vertex d = p.d;
                mat = p.tex;
                
                final int ax = a.x, az = a.z;
                final int bx = b.x, bz = b.z;
                final int dx = d.x, dz = d.z;
                
                int maxx=ax;
                if(bx>maxx) maxx=bx;
                if(c.x>maxx) maxx=c.x;
                if(dx>maxx) maxx=dx;
                
                int minx=ax;
                if(bx<minx) minx=bx;
                if(c.x<minx) minx=c.x;
                if(dx<minx) minx=dx;
                
                int maxz=az;
                if(bz>maxz) maxz=bz;
                if(c.z>maxz) maxz=c.z;
                if(dz>maxz) maxz=dz;
                
                int minz=az;
                if(bz<minz) minz=bz;
                if(c.z<minz) minz=c.z;
                if(dz<minz) minz=dz;
                
                if( minx > x ) continue;
                if( minz > z ) continue;
                if( maxx < x ) continue;
                if( maxz < z ) continue;
                
                if(el.ny >= 0) {//нормаль повернута вниз
                    if(el.ny>2048 && ((a.y + b.y + c.y + d.y) >> 2)>y) height.setUnderRoof(true);
                    continue;
                } 
                
                if(MathUtils2.isPointOnPolygon(x, z, ax, az, bx, bz, c.x, c.z, dx, dz, p.ny) ) {
                    polyY = (y - ((x-ax)*p.nx + (y-a.y)*p.ny + (z-az)*p.nz) / p.ny);
                    cx = (ax + bx + c.x + dx) >> 2;
                    cz = (az + bz + c.z + dz) >> 2;
                    cy = (a.y + b.y + c.y + d.y) >> 2;
                }
            } else if(el instanceof Polygon3V) {
                Polygon3V p = (Polygon3V) el;
                Vertex a = p.a;
                Vertex b = p.b;
                Vertex c = p.c;
                mat = p.tex;
               
                final int ax = a.x, az = a.z;
                final int bx = b.x, bz = b.z;
                
                int maxx=ax;
                if(bx>maxx) maxx=bx;
                if(c.x>maxx) maxx=c.x;
                
                int minx=ax;
                if(bx<minx) minx=bx;
                if(c.x<minx) minx=c.x;
                
                int maxz=az;
                if(bz>maxz) maxz=bz;
                if(c.z>maxz) maxz=c.z;
                
                int minz=az;
                if(bz<minz) minz=bz;
                if(c.z<minz) minz=c.z;
                
                if( minx > x ) continue;
                if( minz > z ) continue;
                if( maxx < x ) continue;
                if( maxz < z ) continue;
                
                if(el.ny >= 0) {//нормаль повернута вниз
                    if(el.ny>2048 && ((a.y + b.y + c.y) / 3)>y) height.setUnderRoof(true);
                    continue;
                } 
                
                if(MathUtils2.isPointOnPolygon(x, z, a.x, a.z, b.x, b.z, c.x, c.z, p.ny) ) {
                    polyY = (y - ((x-ax)*p.nx + (y-a.y)*p.ny + (z-az)*p.nz) / p.ny);
                    cx = (a.x + b.x + c.x) / 3;
                    cz = (a.z + b.z + c.z) / 3;
                    cy = (a.y + b.y + c.y) / 3;
                }
            }
            
            if(mat!=-1) {
                Texture tex = mesh.getTexture().textures[mat];
                if(!tex.collision) continue;
            }
            
            if(polyY < y && polyY > height.getHeight()) { //высота должна быть ниже текущей Y и выше уже найденной
                height.set(polyY, el, cx, cz, cy, matrix, updatePos);
            }
        }
        //if(matrix!=null) DirectX7.transformReturn(mesh);
    }
    

    


}
