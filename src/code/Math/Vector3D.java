package code.Math;

import code.utils.QFPS;

public final class Vector3D {

    public int x;
    public int y;
    public int z;

    public Vector3D() {}

    public Vector3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3D(int[] ps) {
         if(ps.length>=3) {
          x=ps[0]; y=ps[1]; z=ps[2];   
         } else {
             x=y=z=0;
         }
    }

    public final void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public final void set(int x) {
        this.x = x;
        this.y = x;
        this.z = x;
    }

    public final void add(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public final void div(int x, int y, int z) {
        this.x /= x;
        this.y /= y;
        this.z /= z;
    }

    public final void mul(int x, int y, int z) {
        this.x *= x;
        this.y *= y;
        this.z *= z;
    }

    public final void set(Vector3D v) {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    public int length() {
        return (int) Math.sqrt(x * x + y * y + z * z);
    }

    public int lengthSquared() {
        return x * x + y * y + z * z;
    }

    public final void setLength(int len) {
        if((x * x + y * y + z * z) != len * len) {
            while (Math.abs(x) + Math.abs(y) + Math.abs(z) > 30000) {
                x /= 2;
                y /= 2;
                z /= 2;
            }

            double sqr = Math.sqrt(x*x+y*y+z*z);
            x = (int)(double)(x / sqr * len);
            y = (int)(double)(y / sqr * len);
            z = (int)(double)(z / sqr * len);
        }
    }

    public final void setLength2(int len) {
        if(len == 0) {
            x = y = z = 0;
            return;
        }
        int oldlen = Math.max(Math.max(Math.abs(x), Math.abs(y)), Math.abs(z));
        if (oldlen == 0) System.out.println("Cant set vector length: old length is zero");
        if (oldlen == 0) return;
        x = x * len / oldlen;
        y = y * len / oldlen;
        z = z * len / oldlen;

    }
    
    public final void setLengthRound(int len) {
        if((x * x + y * y + z * z) != len * len) {
            while (Math.abs(x) + Math.abs(y) + Math.abs(z) > 30000) {
                x /= 2;
                y /= 2;
                z /= 2;
            }

            double sqr = Math.sqrt(x*x+y*y+z*z);
            x = MathUtils.ceil(x / sqr * len);
            y = MathUtils.ceil(y / sqr * len);
            z = MathUtils.ceil(z / sqr * len);
        }
    }

    public void interpolation(Vector3D v, int s) {
        x += (v.x - x) * 50 / s / (QFPS.frameTime==0?1:QFPS.frameTime);
        y += (v.y - y) * 50 / s / (QFPS.frameTime==0?1:QFPS.frameTime);
        z += (v.z - z) * 50 / s / (QFPS.frameTime==0?1:QFPS.frameTime);
    }

    public int dot(Vector3D v) {
        return x * v.x + y * v.y + z * v.z;
    }
    
    public long dotLong(Vector3D v) {
        return (long)x * v.x + y * v.y + z * v.z;
    }

    public void cross(Vector3D vector3f, Vector3D vector3f1) {
        x = (int) (float) (vector3f.y * vector3f1.z / 32768 - vector3f1.y * vector3f.z / 32768);
        y = (int) (float) (vector3f.z * vector3f1.x / 32768 - vector3f1.z * vector3f.x / 32768);
        z = (int) (float) (vector3f.x * vector3f1.y / 32768 - vector3f1.x * vector3f.y / 32768);
    }

    public void cross(Vector3D a, Vector3D b, int fp) {
        x = (a.y * b.z - b.y * a.z) >> fp;
        y = (a.z * b.x - b.z * a.x) >> fp;
        z = (a.x * b.y - b.x * a.y) >> fp;
    }

    public void mul(Matrix matrix) {

        final int m00 = matrix.m00 >> 2;
        final int m01 = matrix.m01 >> 2;
        final int m02 = matrix.m02 >> 2;
        final int m03 = matrix.m03;

        final int m10 = matrix.m10 >> 2;
        final int m11 = matrix.m11 >> 2;
        final int m12 = matrix.m12 >> 2;
        final int m13 = matrix.m13;

        final int m20 = matrix.m20 >> 2;
        final int m21 = matrix.m21 >> 2;
        final int m22 = matrix.m22 >> 2;
        final int m23 = matrix.m23;

        final int fp = Matrix.fp - 2;

        x = m03;
        if (m00 != 0) {
            x += (x * m00 >> fp);
        }
        if (m01 != 0) {
            x += (y * m01 >> fp);
        }
        if (m02 != 0) {
            x += (z * m02 >> fp);
        }

        y = -m13;
        if (m10 != 0) {
            y -= (x * m10 >> fp);
        }
        if (m11 != 0) {
            y -= (y * m11 >> fp);
        }
        if (m12 != 0) {
            y -= (z * m12 >> fp);
        }

        z = m23;
        if (m20 != 0) {
            z += (x * m20 >> fp);
        }
        if (m21 != 0) {
            z += (y * m21 >> fp);
        }
        if (m22 != 0) {
            z += (z * m22 >> fp);
        }
    }

    public final String toString() {
        return x + " " + y + " " + z;
    }
    
    public int average() {
        return (x+y+z)/3;
    }
    
    public boolean equals(Vector3D v) {
        return (x==v.x && y==v.y && z==v.z);
    }
}
