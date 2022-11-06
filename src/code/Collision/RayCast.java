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

public final class RayCast {

   private static final Vector3D temp = new Vector3D();
   private static final Vector3D colPoint = new Vector3D();
   private static final Vector3D normal = new Vector3D();
   private static final Vector3D v1 = new Vector3D();
   private static final Vector3D v2 = new Vector3D();
   private static final Vector3D v3 = new Vector3D();
   private static final Vector3D v4 = new Vector3D();
   private static boolean roomNeed=false;
   private static int room=-1;

      public static void superFastRayCast(Mesh mesh, Ray ray) {
        RenderObject[] elements = mesh.getPolygons();
        final Vector3D start = ray.start;
        final Vector3D dir = ray.dir;
        
        int dirLen = dir.length();
        for(int i=0; i<elements.length; i++) {
            RenderObject poly = elements[i];
            long dis = Long.MAX_VALUE;
            int mat = -1;
            
            if(poly instanceof Polygon4V) {
                Polygon4V p = (Polygon4V) poly;
                
                normal.x = p.nx; normal.y = p.ny; normal.z = p.nz;
                
                v1.x = p.a.x; v1.y = p.a.y; v1.z = p.a.z;
                v2.x = p.b.x; v2.y = p.b.y; v2.z = p.b.z;
                v3.x = p.c.x; v3.y = p.c.y; v3.z = p.c.z;
                v4.x = p.d.x; v4.y = p.d.y; v4.z = p.d.z;
                dis = rayTracing(ray, v1, v2, v3, v4, normal, start, dir, colPoint, v1);
                mat = p.tex;
            } else if(poly instanceof Polygon3V) {
                Polygon3V p = (Polygon3V) poly;

                normal.x = p.nx; normal.y = p.ny; normal.z = p.nz;
                
                v1.x = p.a.x; v1.y = p.a.y; v1.z = p.a.z;
                v2.x = p.b.x; v2.y = p.b.y; v2.z = p.b.z;
                v3.x = p.c.x; v3.y = p.c.y; v3.z = p.c.z;
                dis = rayTracing(ray, v1, v2, v3, normal, start, dir, colPoint, v1);
                mat = p.tex;
            }
            
            if(mat!=-1) {
                Texture tex = mesh.getTexture().textures[mat];
                boolean castShadow = tex.castShadow;
                boolean collidable = tex.collision;
                if(ray.ignoreNonShadowed && !castShadow) continue;
                if(ray.onlyCollidable && !collidable) continue;
            }
            
            if(dis != Long.MAX_VALUE && ray.origPol!=poly && dis>=0)  {
                long distance = dirLen*dis>>12;
                if(distance > Integer.MAX_VALUE) distance=Integer.MAX_VALUE;
                if(distance < ray.distance) {
                    ray.collision=true;
                    ray.distance=(int)distance;
                    ray.collisionPoint.set(colPoint);
                    ray.triangle=poly;
                    if(!ray.findNearest) return;
                }
            }
        }
        
    }

   public static void rayCast(Mesh mesh, Ray ray, boolean reCalcNorm) {
        RenderObject[] elements = mesh.getPolygons();
        final Vector3D start = ray.start;
        final Vector3D dir = ray.dir;

        final int sx = start.x, sy = start.y, sz = start.z;
        final int ex = sx+dir.x, ey = sy+dir.y, ez = sz+dir.z;

        final int x1 = min(sx, ex);
        final int y1 = min(sy, ey);
        final int z1 = min(sz, ez);

        final int x2 = max(sx, ex);
        final int y2 = max(sy, ey);
        final int z2 = max(sz, ez);
        long tmp;
        
        int dirLen = dir.length();
        for(int i=0; i<elements.length; i++) {
            RenderObject poly = elements[i];
            
            long dis = Long.MAX_VALUE;
            int mat = -1;
            
            if(poly instanceof Polygon3V) {
                Polygon3V p = (Polygon3V) poly;
                Vertex a = p.a, b = p.b, c = p.c;
                if(!ray.infinity) {
                    if( max(a.x, b.x, c.x) < x1 ) continue;
                    if( min(a.x, b.x, c.x) > x2 ) continue;

                    if( max(a.z, b.z, c.z) < z1 ) continue;
                    if( min(a.z, b.z, c.z) > z2 ) continue;

                    if( max(a.y, b.y, c.y) < y1 ) continue;
                    if( min(a.y, b.y, c.y) > y2 ) continue;
                }

                if(!reCalcNorm) {
                    normal.x = p.nx; normal.y = p.ny; normal.z = p.nz;
                } else {
                    long xx = (long)(a.y-b.y)*(a.z-c.z) - (long)(a.z-b.z)*(a.y-c.y);
                    long yy = (long)(a.z-b.z)*(a.x-c.x) - (long)(a.x-b.x)*(a.z-c.z);
                    long zz = (long)(a.x-b.x)*(a.y-c.y) - (long)(a.y-b.y)*(a.x-c.x);
                    double sqrt = Math.sqrt(xx*xx + yy*yy + zz*zz)/4096;
                    normal.x=(int)(xx/sqrt);
                    normal.y=(int)(yy/sqrt);
                    normal.z=(int)(zz/sqrt);
                }
                
                v1.set(a.x, a.y, a.z);
                v2.set(b.x, b.y, b.z);
                v3.set(c.x, c.y, c.z);
                dis = rayTracing(ray, v1, v2, v3, normal, start, dir, colPoint, v1);
                mat = p.tex;
            } else  if(poly instanceof Polygon4V) {
                Polygon4V p = (Polygon4V) poly;
                Vertex a = p.a, b = p.b, c = p.c, d = p.d;
                if(!ray.infinity) {
                    if( max(a.x, b.x, c.x, d.x) < x1 ) continue;
                    if( min(a.x, b.x, c.x, d.x) > x2 ) continue;

                    if( max(a.z, b.z, c.z, d.z) < z1 ) continue;
                    if( min(a.z, b.z, c.z, d.z) > z2 ) continue;

                    if( max(a.y, b.y, c.y, d.y) < y1 ) continue;
                    if( min(a.y, b.y, c.y, d.y) > y2 ) continue;
                }
                
                if(!reCalcNorm) {
                    normal.x = p.nx; normal.y = p.ny; normal.z = p.nz;
                } else {
                    long xx = (long)(a.y-b.y)*(a.z-d.z) - (long)(a.z-b.z)*(a.y-d.y);
                    long yy = (long)(a.z-b.z)*(a.x-d.x) - (long)(a.x-b.x)*(a.z-d.z);
                    long zz = (long)(a.x-b.x)*(a.y-d.y) - (long)(a.y-b.y)*(a.x-d.x);
                    double sqrt = Math.sqrt(xx*xx + yy*yy + zz*zz)/4096;
                    normal.x=(int)(xx/sqrt);
                    normal.y=(int)(yy/sqrt);
                    normal.z=(int)(zz/sqrt);
                }
                
                v1.set(a.x, a.y, a.z);
                v2.set(b.x, b.y, b.z);
                v3.set(c.x, c.y, c.z);
                v4.set(d.x, d.y, d.z);
                dis = rayTracing(ray, v1, v2, v3, v4, normal, start, dir, colPoint, v1);
                mat = p.tex;
            }
            
            if(mat!=-1) {
                Texture tex = mesh.getTexture().textures[mat];
                boolean castShadow = tex.castShadow;
                boolean collidable = tex.collision;
                if(ray.ignoreNonShadowed && !castShadow) continue;
                if(ray.onlyCollidable && !collidable) continue;
            }
            
            if(dis != Long.MAX_VALUE && ray.origPol!=poly && dis>=0)  {
                long distance = dirLen*dis>>12;
                if(distance > Integer.MAX_VALUE) distance=Integer.MAX_VALUE;
                if(distance < ray.distance) {
                    ray.collision=true;
                    ray.distance=(int)distance;
                    ray.collisionPoint.set(colPoint);
                    ray.triangle=poly;
                    if(!ray.findNearest) return;
                }
            }
        }
        
    }
   
       private static long rayTracing(Ray ray, Vector3D a, Vector3D b, Vector3D c, 
               Vector3D nor, Vector3D start, Vector3D dir, Vector3D pos, Vector3D check) {
        pos.set(start.x-check.x, start.y-check.y, start.z-check.z);
        int dot = (int)(dir.dotLong(nor)>>12);
        if(dot <= 0) return Long.MAX_VALUE;
        dot = (int)(-pos.dotLong(nor) / dot);
        if(dot <-1 || (dot > 4096 && !ray.infinity) ) return Long.MAX_VALUE;
        pos.set( start.x+(dir.x*dot>>12),
                 start.y+(dir.y*dot>>12),
                 start.z+(dir.z*dot>>12) );
        if(MathUtils2.isPointOnPolygon(pos, a, b, c, nor)) return dot;
        return Long.MAX_VALUE;
    }
       
    private static long rayTracing(Ray ray, Vector3D a, Vector3D b, Vector3D c, Vector3D d, 
            Vector3D nor, Vector3D start, Vector3D dir, Vector3D pos, Vector3D check) {
        pos.set(start.x-check.x, start.y-check.y, start.z-check.z);
        int dot = (int)(dir.dotLong(nor)>>12);
        if(dot <= 0) return Long.MAX_VALUE;
        dot = (int)(-pos.dotLong(nor) / dot);
        if(dot <-1  || (dot > 4096 && !ray.infinity) ) return Long.MAX_VALUE;
        pos.set( start.x+(dir.x*dot>>12),
                 start.y+(dir.y*dot>>12),
                 start.z+(dir.z*dot>>12) );
        if(MathUtils2.isPointOnPolygon(pos, a, b, c, d, nor)) return dot;
        return Long.MAX_VALUE;
    }
    
    public static long isRayOnPolygon(Vector3D a, Vector3D b, Vector3D c, Vector3D d, 
            Vector3D nor, Vector3D start, Vector3D dir) {
        temp.set(start.x-a.x, start.y-a.y, start.z-a.z);
        int dot = (int)(dir.dotLong(nor)>>12);
        if(dot <= 0) return Long.MAX_VALUE;
        dot = (int)(-temp.dotLong(nor) / dot);
        if(dot <-1) return Long.MAX_VALUE;
        temp.set( start.x+(dir.x*dot>>12),
                 start.y+(dir.y*dot>>12),
                 start.z+(dir.z*dot>>12) );
        if(MathUtils2.isPointOnPolygon(temp, a, b, c, d, nor)) return dot*dir.length()>>12;
        return Long.MAX_VALUE;
    }
    
    public static long isRayOnPolygon(Vector3D a, Vector3D b, Vector3D c, 
            Vector3D nor, Vector3D start, Vector3D dir) {
        temp.set(start.x-a.x, start.y-a.y, start.z-a.z);
        int dot = (int)(dir.dotLong(nor)>>12);
        if(dot <= 0) return Long.MAX_VALUE;
        dot = (int)(-temp.dotLong(nor) / dot);
        if(dot <-1) return Long.MAX_VALUE;
        temp.set( start.x+(dir.x*dot>>12),
                 start.y+(dir.y*dot>>12),
                 start.z+(dir.z*dot>>12) );
        if(MathUtils2.isPointOnPolygon(temp, a, b, c, nor)) return dot*dir.length()>>12;
        return Long.MAX_VALUE;
    }

    
    private static int max(int a, int b, int c, int d) {
        return max(max(a, b), max(c, d));
    }
    private static int min(int a, int b, int c, int d) {
        return min(min(a, b), min(c, d));
    }
    
    private static int max(int a, int b, int c) {
        return max(a, max(b, c));
    }
    private static int min(int a, int b, int c) {
        return min(a, min(b, c));
    }
    
    private static int max(int a, int b) {
        return a>b ? a : b;
    }
    private static int min(int a, int b) {
        return a<b ? a : b;
    }

    public static void rayCast(Mesh mesh, Ray ray, Matrix mat) {
        DirectX7.transformSave(mesh, mat);
        rayCast(mesh, ray, true);
        DirectX7.transformReturn(mesh);
    }

    public static void rayCast(Mesh mesh, Ray ray, int rom) {
        roomNeed = true;
        room = rom;
        rayCast(mesh, ray);
        roomNeed = false;
    }

    public static void rayCast(Mesh mesh, Ray ray) {
        rayCast(mesh, ray, false);
    }

}