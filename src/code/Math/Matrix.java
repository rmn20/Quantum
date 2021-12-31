package code.Math;

public final class Matrix {

   private static final short[] sin = new short[360];
   private static final short[] cos = new short[360];
   public static final int fp=14;
   public static final int FP=1<<fp;
   public int m00 = FP;
   public int m01 = 0;
   public int m02 = 0;
   public int m03 = 0;
   
   public int m10 = 0;
   public int m11 = FP;
   public int m12 = 0;
   public int m13 = 0;
   
   public int m20 = 0;
   public int m21 = 0;
   public int m22 = FP;
   public int m23 = 0;
   
   public int m30 = 0;
   public int m31 = 0;
   public int m32 = 0;
   public int m33 = FP;


   public final void setIdentity() {
      this.m00 = FP;
      this.m01 = 0;
      this.m02 = 0;
      this.m03 = 0;
      
      this.m10 = 0;
      this.m11 = FP;
      this.m12 = 0;
      this.m13 = 0;
      
      this.m20 = 0;
      this.m21 = 0;
      this.m22 = FP;
      this.m23 = 0;
   }

   public final void set(Matrix m) {
      this.m00 = m.m00;
      this.m01 = m.m01;
      this.m02 = m.m02;
      this.m03 = m.m03;
      
      this.m10 = m.m10;
      this.m11 = m.m11;
      this.m12 = m.m12;
      this.m13 = m.m13;
      
      this.m20 = m.m20;
      this.m21 = m.m21;
      this.m22 = m.m22;
      this.m23 = m.m23;
   }
   
   
      public final void set(int mm00,int mm01,int mm02, int mm03,int mm10,int mm11,int mm12, int mm13,int mm20,int mm21,int mm22, int mm23) {
      this.m00 = mm00;
      this.m01 = mm01;
      this.m02 = mm02;
      this.m03 = mm03;
      
      this.m10 = mm10;
      this.m11 = mm11;
      this.m12 = mm12;
      this.m13 = mm13;
      
      this.m20 = mm20;
      this.m21 = mm21;
      this.m22 = mm22;
      this.m23 = mm23;
   }

    public void getPosition(Vector3D v) {
        v.set(m03, m13, m23);
    }

   public final void invert() {
      long var1 = ((long)this.m00 * (long)this.m03 >> fp) + ((long)this.m10 * (long)this.m13 >> fp) + ((long)this.m20 * (long)this.m23 >> fp);
      long var3 = ((long)this.m01 * (long)this.m03 >> fp) + ((long)this.m11 * (long)this.m13 >> fp) + ((long)this.m21 * (long)this.m23 >> fp);
      long var5 = ((long)this.m02 * (long)this.m03 >> fp) + ((long)this.m12 * (long)this.m13 >> fp) + ((long)this.m22 * (long)this.m23 >> fp);
      this.m03 = (int)(-var1);
      this.m13 = (int)(-var3);
      this.m23 = (int)(-var5);
      int var7 = this.m01;
      this.m01 = this.m10;
      this.m10 = var7;
      var7 = this.m02;
      this.m02 = this.m20;
      this.m20 = var7;
      var7 = this.m12;
      this.m12 = this.m21;
      this.m21 = var7;
   }

   private static int fixDegree(int degree) {
      while(degree < 0) {
         degree += 360;
      }

      while(degree >= 360) {
         degree -= 360;
      }

      return degree;
   }
   


   public final void setRotX(int degree) {
      degree = fixDegree(degree);
      this.m00 = FP;
      this.m01 = 0;
      this.m02 = 0;
      this.m03 = 0;
      this.m10 = 0;
      this.m11 = cos[degree];
      this.m12 = -sin[degree];
      this.m13 = 0;
      this.m20 = 0;
      this.m21 = sin[degree];
      this.m22 = cos[degree];
      this.m23 = 0;
   }
   


   public final void mul(Matrix m) {
      this.mul(this, m);
   }

   public final void mul(Matrix m2, Matrix m1) {
        long t00 = (m2.m00*m1.m00       +       m2.m01*m1.m10 +       m2.m02*m1.m20 + (long)m2.m03*m1.m30)>>fp;
        long t01 = (m2.m00*m1.m01       +       m2.m01*m1.m11 +       m2.m02*m1.m21 + (long)m2.m03*m1.m31)>>fp;
        long t02 = (m2.m00*m1.m02       +       m2.m01*m1.m12 +       m2.m02*m1.m22 + (long)m2.m03*m1.m32)>>fp;
        long t03 = ((long)m2.m00*m1.m03 + (long)m2.m01*m1.m13 + (long)m2.m02*m1.m23 + (long)m2.m03*m1.m33)>>fp;

        long t10 = (m2.m10*m1.m00       +       m2.m11*m1.m10 +       m2.m12*m1.m20 + (long)m2.m13*m1.m30)>>fp;
        long t11 = (m2.m10*m1.m01       +       m2.m11*m1.m11 +       m2.m12*m1.m21 + (long)m2.m13*m1.m31)>>fp;
        long t12 = (m2.m10*m1.m02       +       m2.m11*m1.m12 +       m2.m12*m1.m22 + (long)m2.m13*m1.m32)>>fp;
        long t13 = ((long)m2.m10*m1.m03 + (long)m2.m11*m1.m13 + (long)m2.m12*m1.m23 + (long)m2.m13*m1.m33)>>fp;

        long t20 = (m2.m20*m1.m00       +       m2.m21*m1.m10 +       m2.m22*m1.m20 + (long)m2.m23*m1.m30)>>fp;
        long t21 = (m2.m20*m1.m01       +       m2.m21*m1.m11 +       m2.m22*m1.m21 + (long)m2.m23*m1.m31)>>fp;
        long t22 = (m2.m20*m1.m02       +       m2.m21*m1.m12 +       m2.m22*m1.m22 + (long)m2.m23*m1.m32)>>fp;
        long t23 = ((long)m2.m20*m1.m03 + (long)m2.m21*m1.m13 + (long)m2.m22*m1.m23 + (long)m2.m23*m1.m33)>>fp;

        m00 = (int) t00;
        m01 = (int) t01;
        m02 = (int) t02;
        m03 = (int) t03;

        m10 = (int) t10;
        m11 = (int) t11;
        m12 = (int) t12;
        m13 = (int) t13;

        m20 = (int) t20;
        m21 = (int) t21;
        m22 = (int) t22;
        m23 = (int) t23;

   }

    public void rotY(int degree) {
        degree = fixDegree(degree);
        int c = cos[degree];
        int s = sin[degree];

        int xX = (m00 * c + m20 * s) >> fp;
        int xY = (m01 * c + m21 * s) >> fp;
        int xZ = (m02 * c + m22 * s) >> fp;

        int zX = (m20 * c - m00 * s) >> fp;
        int zY = (m21 * c - m01 * s) >> fp;
        int zZ = (m22 * c - m02 * s) >> fp;

        m00 = xX;
        m01 = xY;
        m02 = xZ;

        m20 = zX;
        m21 = zY;
        m22 = zZ;
    }
   
    
    public void setRotY(int degree) {
        degree = fixDegree(degree);
        m00 = cos[degree];
        m01 = 0;
        m02 = sin[degree];
        m03 = 0;
        
        m10 = 0;
        m11 = FP;
        m12 = 0;
        m13 = 0;
        
        m20 = -sin[degree];
        m21 = 0;
        m22 = cos[degree];
        m23 = 0;
    }
    
    public void setRotZ(int degree) {
        degree = fixDegree(degree);
        m00 = cos[degree];
        m01 = -sin[degree];
        m02 = 0;
        m03 = 0;
        
        m10 = sin[degree];
        m11 = cos[degree];
        m12 = 0;
        m13 = 0;
        
        m20 = 0;
        m21 = 0;
        m22 = FP;
        m23 = 0;
    }
   public void rotZ(int degree) {
        degree = fixDegree(degree);
	int c = cos[degree];
	int s = sin[degree];
        
        int yX = (m10 * c + m00 * s) >> fp;
	int yY = (m11 * c + m01 * s) >> fp;
	int yZ = (m12 * c + m02 * s) >> fp;

	int xX = (m00 * c - m10 * s) >> fp;
	int xY = (m01 * c - m11 * s) >> fp;
	int xZ = (m02 * c - m12 * s) >> fp;

	m10 = yX;
	m11 = yY;
	m12 = yZ;

	m00 = xX;
	m01 = xY;
	m02 = xZ;
    }



   public final void setPosition(int x, int y, int z) {
      this.m03 = x;
      this.m13 = y;
      this.m23 = z;
   }
   
   public final void setPosition(Vector3D pos) {
      this.m03 = pos.x;
      this.m13 = pos.y;
      this.m23 = pos.z;
   }
   
   public final void addPosition(Vector3D pos) {
      this.m03 += pos.x;
      this.m13 += pos.y;
      this.m23 += pos.z;
   }
   
   public final void divPosition(int x, int y, int z) {
      this.m03 /= x;
      this.m13 /= y;
      this.m23 /= z;
   }
      
   public final void subPosition(int x, int y, int z) {
      this.m03 = this.m03 -x;
      this.m13 = this.m13 -y;
      this.m23 =this.m23 - z;
   }

   public final void addPosition(int x, int y, int z) {
      this.m03 = this.m03 +x;
      this.m13 = this.m13 +y;
      this.m23 =this.m23 + z;
   }


   public final void setSide(int x, int y, int z) {
      this.m00 = x;
      this.m10 = y;
      this.m20 = z;
   }
   
   public final void scale(int x, int y, int z) {
        m00 = m00 * x >> fp;
        m10 = m10 * x >> fp;
        m20 = m20 * x >> fp;

        m01 = m01 * y >> fp;
        m11 = m11 * y >> fp;
        m21 = m21 * y >> fp;

        m02 = m02 * z >> fp;
        m12 = m12 * z >> fp;
        m22 = m22 * z >> fp;
   }

   public final void setUp(int x, int y, int z) {
      this.m01 = x;
      this.m11 = y;
      this.m21 = z;
   }
   

   public final void setDir(int x, int y, int z) {
      this.m02 = x;
      this.m12 = y;
      this.m22 = z;
   }
    public void translate(int x, int y, int z) {
        if(x != 0) {
            m03 += m00*x>>fp;
            m13 += m10*x>>fp;
            m23 += m20*x>>fp;
        }
        if(y != 0) {
            m03 += m01*y>>fp;
            m13 += m11*y>>fp;
            m23 += m21*y>>fp;
        }
        if(z != 0) {
            m03 += m02*z>>fp;
            m13 += m12*z>>fp;
            m23 += m22*z>>fp;
        }
    }

   static {
      for(int var0 = 0; var0 < 360; ++var0) {
         sin[var0] = (short)((Math.sin(Math.toRadians(var0)) * FP));
         cos[var0] = (short)((Math.cos(Math.toRadians(var0)) * FP));
      }

   }

   public static int cos(int deg) {
       while(deg<0) deg+=360;
       return cos[deg%360];
   }
   
   public static int sin(int deg) {
       while(deg<0) deg+=360;
       return sin[deg%360];
   }

public int getRotZ() {
	int rt=MathUtils.getAnglez(0,0,-m02,-m22);
rt=MathUtils.fixDegree(rt);
return rt;
    }

public int getRotZHQ() {
	int rt=MathUtils.getAnglezHQ(0,0,-m02,-m22);
return rt;
    }

}
