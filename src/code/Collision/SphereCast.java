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

public final class SphereCast {

    private static final Vector3D temp = new Vector3D();
    private static final Vector3D nor = new Vector3D();
    private static final Vector3D v1 = new Vector3D(),
                                  v2 = new Vector3D(),
                                  v3 = new Vector3D(),
                                  v4 = new Vector3D();

    public static boolean isSphereAABBCollision(Vector3D pos, int rad, int minx, int maxx, int minz, int maxz) {
        return !(pos.x+rad < minx || pos.z+rad < minz || pos.x-rad > maxx || pos.z-rad > maxz);
    }
    

    public static boolean sphereCast(Mesh mesh, Matrix matrix, Vector3D pos, int rad) {
        DirectX7.transformSave(mesh,matrix);
        return sphereCast(mesh,pos,rad,true);
    }
    
    public static boolean sphereCast(Mesh mesh, Vector3D pos, int rad) {
        return sphereCast(mesh,pos,rad,false);
    }
    
    public static boolean sphereCast(Mesh mesh, Vector3D pos, int rad, boolean recalc) {
        RenderObject[] elements = mesh.getPolygons();
        boolean col = false;
        for(int i=0; i<elements.length; i++) {
            RenderObject poly = elements[i];
            int dis = Integer.MAX_VALUE;
            int mat = -1;
            
            if(poly instanceof Polygon4V) {
                Polygon4V p = (Polygon4V) poly;
                final Vertex a = p.a;
                final Vertex b = p.b;
                final Vertex c = p.c;
                final Vertex d = p.d;
                mat = p.tex;
                
                
                final int ax = a.x, ay = a.y, az = a.z;
                final int bx = b.x, by = b.y, bz = b.z;
                final int cx = c.x, cy = c.y, cz = c.z;
                final int dx = d.x, dy = d.y, dz = d.z;
                
                int maxx=ax;
                if(bx>maxx) maxx=bx;
                if(cx>maxx) maxx=cx;
                if(dx>maxx) maxx=dx;
                
                int minx=ax;
                if(bx<minx) minx=bx;
                if(cx<minx) minx=cx;
                if(dx<minx) minx=dx;
                
                int maxy=ay;
                if(by>maxy) maxy=by;
                if(cy>maxy) maxy=cy;
                if(dy>maxy) maxy=dy;
                
                int miny=ay;
                if(by<miny) miny=by;
                if(cy<miny) miny=cy;
                if(dy<miny) miny=dy;
                
                int maxz=az;
                if(bz>maxz) maxz=bz;
                if(cz>maxz) maxz=cz;
                if(dz>maxz) maxz=dz;
                
                int minz=az;
                if(bz<minz) minz=bz;
                if(cz<minz) minz=cz;
                if(dz<minz) minz=dz;
                if(maxx < pos.x-rad) continue;
                if(minx > pos.x+rad) continue;
                if(maxz < pos.z-rad) continue;
                if(minz > pos.z+rad) continue;
                if(maxy < pos.y-rad) continue;
                if(miny > pos.y+rad) continue;

                v1.x=ax; v1.y=ay; v1.z=az;
                v2.x=bx; v2.y=by; v2.z=bz;
                v3.x=cx; v3.y=cy; v3.z=cz;
                v4.x=dx; v4.y=dy; v4.z=dz;
                nor.x=p.nx;
                nor.y=p.ny;
                nor.z=p.nz;
                if(recalc) MathUtils2.calcNormal(nor, v1, v2, v4);
                dis = distanceSphereToPolygon(v1, v2, v3, v4, nor, pos, rad);
            } else if(poly instanceof Polygon3V) {
                Polygon3V p = (Polygon3V) poly;
                final Vertex a = p.a;
                final Vertex b = p.b;
                final Vertex c = p.c;
                mat = p.tex;
                
                
                final int ax = a.x, ay = a.y, az = a.z;
                final int bx = b.x, by = b.y, bz = b.z;
                final int cx = c.x, cy = c.y, cz = c.z;
                
                int maxx=ax;
                if(bx>maxx) maxx=bx;
                if(cx>maxx) maxx=cx;
                
                int minx=ax;
                if(bx<minx) minx=bx;
                if(cx<minx) minx=cx;
                
                int maxy=ay;
                if(by>maxy) maxy=by;
                if(cy>maxy) maxy=cy;
                
                int miny=ay;
                if(by<miny) miny=by;
                if(cy<miny) miny=cy;
                
                int maxz=az;
                if(bz>maxz) maxz=bz;
                if(cz>maxz) maxz=cz;
                
                int minz=az;
                if(bz<minz) minz=bz;
                if(cz<minz) minz=cz;
                
                if(maxx < pos.x-rad) continue;
                if(minx > pos.x+rad) continue;
                if(maxz < pos.z-rad) continue;
                if(minz > pos.z+rad) continue;
                if(maxy < pos.y-rad) continue;
                if(miny > pos.y+rad) continue;

                v1.x=ax; v1.y=ay; v1.z=az;
                v2.x=bx; v2.y=by; v2.z=bz;
                v3.x=cx; v3.y=cy; v3.z=cz;
                nor.x=p.nx;
                nor.y=p.ny;
                nor.z=p.nz;
                if(recalc) MathUtils2.calcNormal(nor, v1, v2, v3);
                dis = distanceSphereToPolygon(v1, v2, v3, nor, pos, rad);
            }
            
            if(mat!=-1) {
                Texture tex = mesh.getTexture().textures[mat];
                if(!tex.collision) continue;
            }
            
            if(dis != Integer.MAX_VALUE && dis > 0) {
                pos.add(-nor.x*dis>>12, -nor.y*dis>>12, -nor.z*dis>>12);
                col = true;
            }
        }
        return col;
    }
    
    private static int distanceSphereToPolygon(Vector3D a, Vector3D b, Vector3D c, Vector3D nor,
            Vector3D point, int rad) {
        temp.x=point.x-a.x;
        temp.y=point.y-a.y;
        temp.z=point.z-a.z;
        int dot = temp.dot(nor)>>12; //расстояние до плоскости
        if(dot > rad) return Integer.MAX_VALUE;
        //проекция на плоскость
        temp.x=point.x-(nor.x*dot>>12);
        temp.y=point.y-(nor.y*dot>>12);
        temp.z=point.z-(nor.z*dot>>12);
        if(MathUtils2.isPointOnPolygon(temp, a, b, c, nor)) {
            int dis = dot;
            if(dot<0) dis = -dot;
            dis = rad - dis;
            return dis;
        }

        final int len1 = MathUtils2.distanceToLine(point, a, b);
        final int len2 = MathUtils2.distanceToLine(point, b, c);
        final int len3 = MathUtils2.distanceToLine(point, c, a);

        int min = len1;
        if(len2 < min) min = len2;
        if(len3 < min) min = len3;
        if(min <= rad*rad) return rad - (int)(1/MathUtils2.invSqrt(min));
        return Integer.MAX_VALUE;
    }

    private static int distanceSphereToPolygon(Vector3D a, Vector3D b, Vector3D c, Vector3D d, Vector3D nor,
            Vector3D point, int rad) {
        temp.x=point.x-a.x;
        temp.y=point.y-a.y;
        temp.z=point.z-a.z;
        int dot = temp.dot(nor)>>12;//расстояние до плоскости
        if(dot > rad) return Integer.MAX_VALUE;
        //проекция на плоскость
        temp.x=point.x-(nor.x*dot>>12);
        temp.y=point.y-(nor.y*dot>>12);
        temp.z=point.z-(nor.z*dot>>12);
        if(MathUtils2.isPointOnPolygon(temp, a, b, c, d, nor)) {
            int dis = dot;
            if(dot<0) dis = -dot;
            dis = rad - dis;
            return dis;
        }

        final int len1 = MathUtils2.distanceToLine(point, a, b);
        final int len2 = MathUtils2.distanceToLine(point, b, c);
        final int len3 = MathUtils2.distanceToLine(point, c, d);
        final int len4 = MathUtils2.distanceToLine(point, d, a);

        int min = len1;
        if(len2 < min) min = len2;
        if(len3 < min) min = len3;
        if(len4 < min) min = len4;
        if(min <= rad*rad) return rad - (int)(1/MathUtils2.invSqrt(min));
        return Integer.MAX_VALUE;
    }


}

