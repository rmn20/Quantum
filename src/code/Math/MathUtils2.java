package code.Math;

import code.Rendering.Vertex;


/**
 * математические функции
 * @author DDDEN!SSS
 */
public class MathUtils2 {

    private static final Vector3D tVec = new Vector3D();
    
    /**
     * быстрое вычисление обратного корня
     * @param val число из которого вычисляется обратный корень
     * @return обратный корень т.е. 1/корень
     */
    
    public static float invSqrt(float val) {
        float xhalf = 0.5f*val;
        int i = Float.floatToIntBits(val);
        i = 1597463007 - (i>>1);
        val = Float.intBitsToFloat(i);
        return val * (1.5F - xhalf * val * val);
    }
    
    /**
     * создать нормаль
     * @param a вершина треугольника
     * @param b вершина треугольника
     * @param с вершина треугольника
     * @return нормаль
     */
    public static Vector3D createNormal(Vector3D a, Vector3D b, Vector3D c) {
        Vector3D nor = new Vector3D();
        calcNormal(nor, a.x, a.y, a.z, b.x, b.y, b.z, c.x, c.y, c.z);
        return nor;
    }
    
    /**
     * вычислить нормаль
     * @param a вершина треугольника
     * @param b вершина треугольника
     * @param с вершина треугольника
     */
    public static void calcNormal(Vector3D nor, Vector3D a, Vector3D b, Vector3D c) {
        calcNormal(nor, a.x, a.y, a.z, b.x, b.y, b.z, c.x, c.y, c.z);
    }
    
    public static Vector3D calcNormal( Vertex a, Vertex b, Vertex c) {
        return calcNormal(a.x, a.y, a.z, b.x, b.y, b.z, c.x, c.y, c.z);
    }
    /**
     * вычислить нормаль
     */
    public static void calcNormal(Vector3D nor, int ax, int ay, int az, int bx, int by, int bz, int cx, int cy, int cz) {
        double x = (long)(ay-by)*(az-cz) - (long)(az-bz)*(ay-cy);
        double y = (long)(az-bz)*(ax-cx) - (long)(ax-bx)*(az-cz);
        double z = (long)(ax-bx)*(ay-cy) - (long)(ay-by)*(ax-cx);
        double sqrt = Math.sqrt(x*x + y*y + z*z)/4096;
        nor.set((int)(x/sqrt), (int)(y/sqrt), (int)(z/sqrt));
    }
    

    public static Vector3D calcNormal(int ax, int ay, int az, int bx, int by, int bz, int cx, int cy, int cz) {
       Vector3D nor=new Vector3D(0,0,0);
        double x = (long)(ay-by)*(az-cz) - (long)(az-bz)*(ay-cy);
        double y = (long)(az-bz)*(ax-cx) - (long)(ax-bx)*(az-cz);
        double z = (long)(ax-bx)*(ay-cy) - (long)(ay-by)*(ax-cx);
        double sqrt = Math.sqrt(x*x + y*y + z*z)/4096;
        nor.set((int)(x/sqrt), (int)(y/sqrt), (int)(z/sqrt));
        return nor;
    }
    
    public static int computePolygonY(Vertex a, int nx, int ny, int nz, int px, int py, int pz) {
        if(ny >= 0) return Integer.MAX_VALUE;
        else return py - ((px - a.x)*nx + (py - a.y)*ny + (pz - a.z)*nz) / ny;
    }
    
    /**
     * с какой стороны отрезка точка
     */
    public static long side(int px, int py, int ax, int ay, int bx, int by) {
        return (bx-ax)*(py-ay) - (px-ax)*(by-ay);
    }
    
    /**
     * расстояние между плоскостью и точкой
     * @param a вершина плоскости
     * @param nor нормаль плоскости
     * @param px x элемент точки
     * @param py y элемент точки
     * @param pz z элемент точки
     * @return расстояние в квадрате
     */
    public static int distanceToFace(Vector3D a, Vector3D nor, int px, int py, int pz) {
        tVec.set(px-a.x, py-a.y, pz-a.z);
        return tVec.dot(nor)>>12;
    }
    
    public static int distanceToFace(Vertex a, Vector3D nor, int px, int py, int pz) {
        tVec.set(px-a.x, py-a.y, pz-a.z);
        return tVec.dot(nor)>>12;
    }

    /**
     * расстояние между точкой и отрезком
     * @param point точка
     * @param a вершина отрезка
     * @param b вершина отрезка
     * @return расстояние в квадрате
     */
    public static int distanceToLine(Vector3D point, Vector3D a, Vector3D b) {
        int dx = b.x - a.x;
        int dy = b.y - a.y;
        int dz = b.z - a.z;

        int wx = point.x - a.x;
        int wy = point.y - a.y;
        int wz = point.z - a.z;

        final int fp = 14;
        long dp = (long)dx*dx + dy*dy + dz*dz;
        long dt = 0;
        if(dp != 0) dt = (((long)wx*dx + wy*dy + wz*dz)<<fp) / dp;
        if(dt < 0) dt = 0;
        if(dt > 16384) dt = 16384;

        dx = a.x + (int)(dx * dt >> fp);
        dy = a.y + (int)(dy * dt >> fp);
        dz = a.z + (int)(dz * dt >> fp);

        dx -= point.x;
        dy -= point.y;
        dz -= point.z;
        return dx*dx + dy*dy + dz*dz;
    }
    
    /**
     * расстояние между точкой и лучом
     * @param point точка
     * @param a вершина отрезка
     * @param dir направление отрезка
     * @return расстояние в квадрате
     */
    public static int distanceToRay(Vector3D point, Vector3D a, Vector3D dir) {
        int dx = dir.x;
        int dy = dir.y;
        int dz = dir.z;

        int wx = point.x - a.x;
        int wy = point.y - a.y;
        int wz = point.z - a.z;

        final int fp = 14;
        long dp = dx*dx + dy*dy + dz*dz;
        long dt = 0;
        if(dp != 0) dt = (((long)wx*dx + wy*dy + wz*dz)<<fp) / dp;
        if(dt < 0) dt = 0;
        if(dt > 16384) dt = 16384;

        dx = a.x + (int)(dx * dt >> fp);
        dy = a.y + (int)(dy * dt >> fp);
        dz = a.z + (int)(dz * dt >> fp);

        dx -= point.x;
        dy -= point.y;
        dz -= point.z;
        return dx*dx + dy*dy + dz*dz;
    }

    /**
     * метод проверяет лежит ли точка на полигоне
     * @param point точка
     * @param a вершина полигона
     * @param b вершина полигона
     * @param c вершина полигона
     * @param d вершина полигона
     * @param normal нормаль полигона
     * @return true - точка на полигоне
     */
    public static boolean isPointOnPolygon(Vector3D point, Vector3D a, Vector3D b, Vector3D c, Vector3D d, Vector3D normal) {
        return isPointOnPolygon(point.x, point.y, point.z,
                a.x, a.y, a.z,
                b.x, b.y, b.z,
                c.x, c.y, c.z,
                d.x, d.y, d.z,
                normal.x, normal.y, normal.z);
    }
    
    public static boolean isPointOnPolygon(int pointx, int pointy, int pointz, 
            int ax, int ay, int az, 
            int bx, int by, int bz, 
            int cx, int cy, int cz, 
            int dx, int dy, int dz, 
            int normalx, int normaly, int normalz) {
        final int nx = normalx>0 ? normalx : -normalx;
        final int ny = normaly>0 ? normaly : -normaly;
        final int nz = normalz>0 ? normalz : -normalz;

        if(nx >= ny && nx >= nz) {
            if(normalx >= 0) {
                return isPointOnPolygon( pointz, pointy, az, ay, bz, by, cz, cy, dz, dy );
            } else {
                return isPointOnPolygon( pointz, pointy, dz, dy, cz, cy, bz, by, az, ay );
            }
        }
        if(ny >= nx && ny >= nz) {
            if(normaly >= 0) {
                return isPointOnPolygon( pointx, pointz, ax, az, bx, bz, cx, cz, dx, dz );
            } else {
                return isPointOnPolygon( pointx, pointz, dx, dz, cx, cz, bx, bz, ax, az );
            }
        }
        if(nz >= nx && nz >= ny) {
            if(normalz <= 0) {
                return isPointOnPolygon( pointx, pointy, ax, ay, bx, by, cx, cy, dx, dy );
            } else {
                return isPointOnPolygon( pointx, pointy, dx, dy, cx, cy, bx, by, ax, ay );
            }
        }
        return true;
    }
    
    /**
     * метод проверяет лежит ли точка на полигоне
     * @param point точка
     * @param a вершина полигона
     * @param b вершина полигона
     * @param c вершина полигона
     * @param normal нормаль полигона
     * @return true - точка на полигоне
     */
    public static boolean isPointOnPolygon(Vector3D point, Vector3D a, Vector3D b, Vector3D c, Vector3D normal) {
        return isPointOnPolygon(point.x, point.y, point.z,
                a.x, a.y, a.z,
                b.x, b.y, b.z,
                c.x, c.y, c.z,
                normal.x, normal.y, normal.z);
    }
    
    public static boolean isPointOnPolygon(int pointx, int pointy, int pointz, 
            int ax, int ay, int az, 
            int bx, int by, int bz, 
            int cx, int cy, int cz, 
            int normalx, int normaly, int normalz) {
        final int nx = normalx>0 ? normalx : -normalx;
        final int ny = normaly>0 ? normaly : -normaly;
        final int nz = normalz>0 ? normalz : -normalz;

        if(nx >= ny && nx >= nz) {
            if(normalx >= 0) {
                return isPointOnPolygon( pointz, pointy, az, ay, bz, by, cz, cy );
            } else {
                return isPointOnPolygon( pointz, pointy, cz, cy, bz, by, az, ay );
            }
        }
        if(ny >= nx && ny >= nz) {
            if(normaly >= 0) {
                return isPointOnPolygon( pointx, pointz, ax, az, bx, bz, cx, cz );
            } else {
                return isPointOnPolygon( pointx, pointz, cx, cz, bx, bz, ax, az );
            }
        }
        if(nz >= nx && nz >= ny) {
            if(normalz <= 0) {
                return isPointOnPolygon( pointx, pointy, ax, ay, bx, by, cx, cy );
            } else {
                return isPointOnPolygon( pointx, pointy, cx, cy, bx, by, ax, ay );
            }
        }
        return true;
    }
    
    /**
     * проверить лежит ли точка на полигоне
     * @return true - точка на полигоне
     */
    public static boolean isPointOnPolygon(int px, int pz, 
                                           int ax, int az,
                                           int bx, int bz,
                                           int cx, int cz, 
                                           int dx, int dz,
                                           int norY) {
        if(norY >= 0) {
            return isPointOnPolygon(px, pz, ax, az, bx, bz, cx, cz, dx, dz);
        }
        if(norY < 0) {
            return isPointOnPolygon(px, pz, dx, dz, cx, cz, bx, bz, ax, az);
        }
        return false;
    }
    
    /**
     * проверить лежит ли точка на полигоне
     * @return true - точка на полигоне
     */
    public static boolean isPointOnPolygon(int px, int pz, 
                                           int ax, int az,
                                           int bx, int bz,
                                           int cx, int cz, 
                                           int norY) {
        if(norY >= 0) {
            return isPointOnPolygon(px, pz, ax, az, bx, bz, cx, cz);
        }
        if(norY < 0) {
            return isPointOnPolygon(px, pz, cx, cz, bx, bz, ax, az);
        }
        return false;
    }
    
    /**
     * проверить лежит ли точка на полигоне
     * @return true - точка на полигоне
     */
    public static boolean isPointOnPolygon(int px, int py, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4) {
        return  (x2-x1)*(py-y1) <= (px-x1)*(y2-y1) &&
                (x3-x2)*(py-y2) <= (px-x2)*(y3-y2) &&
                (x4-x3)*(py-y3) <= (px-x3)*(y4-y3) &&
                (x1-x4)*(py-y4) <= (px-x4)*(y1-y4); //справа от линии
    }
    
    /**
     * проверить лежит ли точка на полигоне
     * @return true - точка на полигоне
     */
    public static boolean isPointOnPolygon(int px, int py, int x1, int y1, int x2, int y2, int x3, int y3) {
        return  (x2-x1)*(py-y1) <= (px-x1)*(y2-y1) &&
                (x3-x2)*(py-y2) <= (px-x2)*(y3-y2) && 
                (x1-x3)*(py-y3) <= (px-x3)*(y1-y3); //справа от линии
    }
}
