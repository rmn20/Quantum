package code.Rendering;

import code.Math.Matrix;
import code.utils.Main;

public class Vertex {

    public int x;
    public int y;
    public int z;

    public int sx;
    public int sy;
    public int rz;
    private static Main main;

    public Vertex() {
    }

    public Vertex(int x, int y, int z) {
        this.set(x, y, z);
    }

    public Vertex(Vertex ver) {
        x = ver.x;
        y = ver.y;
        z = ver.z;
        sx = ver.sx;
        sy = ver.sy;
        rz = ver.rz;
    }

    public Vertex(int[] ver) {
        x = ver[0];
        y = ver[1];
        z = ver[2];
    }

    public final void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public final void set(Vertex ver) {
        x = ver.x;
        y = ver.y;
        z = ver.z;
        sx = ver.sx;
        sy = ver.sy;
        rz = ver.rz;
    }

    public void pmax(int x, int y, int z) {
        this.sx = Math.max(x,this.sx);
        this.sy = Math.max(y,this.sy);
        this.rz = Math.max(z,this.rz);
    }

    public void min(int x, int y, int z) {
        this.x = Math.min(x,this.x);
        this.y = Math.min(y,this.y);
        this.z = Math.min(z,this.z);
    }
   
    public final void mul(int x, int y, int z) {
        this.x = this.x * x;
        this.y = this.y * y;
        this.z = this.z * z;
    }

    public final void sub(int x, int y, int z) {
        this.x = this.x - x;
        this.y = this.y - y;
        this.z = this.z - z;
    }

    public final void add(int x, int y, int z) {
        this.x = this.x + x;
        this.y = this.y + y;
        this.z = this.z + z;
    }

    public final void div(int x, int y, int z) {
        if(x != 0) {
            this.x = this.x / x;
        }
        if(y != 0) {
            this.y = this.y / y;
        }
        if(z != 0) {
            this.z = this.z / z;
        }
    }

   public final void transform(Matrix matrix) {
      int fpss=Matrix.fp-2;
      int m00 = matrix.m00 >> 2;
      int m01 = matrix.m01 >> 2;
      int m02 = matrix.m02 >> 2;
      int m03 = matrix.m03;
      int m10 = matrix.m10 >> 2;
      int m11 = matrix.m11 >> 2;
      int m12 = matrix.m12 >> 2;
      int m13 = matrix.m13;
      int m20 = matrix.m20 >> 2;
      int m21 = matrix.m21 >> 2;
      int m22 = matrix.m22 >> 2;
      int m23 = matrix.m23;
      this.sx = ((this.x * m00 >> fpss) + (this.y * m01 >> fpss) + (this.z * m02 >> fpss) + m03);
      this.sy = ((this.x * m10 >> fpss) + (this.y * m11 >> fpss) + (this.z * m12 >> fpss) + m13);
      this.rz = ((this.x * m20 >> fpss) + (this.y * m21 >> fpss) + (this.z * m22 >> fpss) + m23);
   }

public final void transformFE(Matrix matrix) {
int fpss=Matrix.fp-2;
      int var2 = matrix.m00 >> 2;
      int var3 = matrix.m01 >> 2;
      int var4 = matrix.m02 >> 2;
      int var5 = matrix.m03;
      int var6 = matrix.m10 >> 2;
      int var7 = matrix.m11 >> 2;
      int var8 = matrix.m12 >> 2;
      int var9 = matrix.m13;
      int var10 = matrix.m20 >> 2;
      int var11 = matrix.m21 >> 2;
      int var12 = matrix.m22 >> 2;
      int var13 = matrix.m23;
      this.sx = this.x;
      this.sy = this.y;
      this.rz = this.z;
      this.x = (this.x * var2 >> fpss) + (this.y * var3 >> fpss) + (this.z * var4 >> fpss) + var5;
      this.y = (this.x * var6 >> fpss) + (this.y * var7 >> fpss) + (this.z * var8 >> fpss) + var9;
      this.z = (this.x * var10 >> fpss) + (this.y * var11 >> fpss) + (this.z * var12 >> fpss) + var13;
   }

   // из Graphics3D: public void project(Vertex vertex)
   public final void project(DirectX7 g3d) {
      if(this.rz <= 0) {
         this.sx =  (this.sx * g3d.distX / (-this.rz + g3d.distX) + g3d.centreX);
         this.sy =  (-this.sy * g3d.distY / (-this.rz + g3d.distY) + g3d.centreY);
      } else {
         this.sx += g3d.centreX;
         this.sy =  (-this.sy + g3d.centreY);
      }
   }
   
}
