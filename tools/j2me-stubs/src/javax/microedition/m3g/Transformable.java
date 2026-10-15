package javax.microedition.m3g;
public abstract class Transformable extends Object3D {
    public void setTransform(Transform t) {}
    public void getTransform(Transform t) {}
    public void getCompositeTransform(Transform t) {}
    public void setOrientation(float angle, float ax, float ay, float az) {}
    public void postRotate(float angle, float ax, float ay, float az) {}
    public void preRotate(float angle, float ax, float ay, float az) {}
    public void setScale(float sx, float sy, float sz) {}
    public void setTranslation(float tx, float ty, float tz) {}
    public void getOrientation(float[] angleAxis) {}
    public void getScale(float[] xyz) {}
    public void getTranslation(float[] xyz) {}
}
