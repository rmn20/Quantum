package javax.microedition.m3g;
public class Matrix4F {
    public Matrix4F() {}
    public void setIdentity() {}
    public void set(Matrix4F m) {}
    public void set(float[] m) {}
    public void set(float a00, float a01, float a02, float a03, float a10, float a11, float a12,
                    float a13, float a20, float a21, float a22, float a23, float a30, float a31,
                    float a32, float a33) {}
    public void get(float[] m) {}
    public void transform(float[] v) {}
    public void transpose() {}
    public void invert() {}
}
