package javax.microedition.m3g;
public class Transform {
    public Transform() {}
    public Transform(Transform t) {}
    public void setIdentity() {}
    public void set(Transform t) {}
    public void set(Matrix4F m) {}
    public void set(float[] m) {}
    public void get(float[] m) {}
    public void invert() {}
    public void transpose() {}
    public void postMultiply(Transform t) {}
    public void preMultiply(Transform t) {}
    public void postScale(float sx, float sy, float sz) {}
    public void postTranslate(float tx, float ty, float tz) {}
    public void postRotate(float angle, float ax, float ay, float az) {}
    public void transform(float[] v) {}
    public void transform(float[] v, boolean w) {}
}
