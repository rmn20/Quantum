package javax.microedition.m3g;
public class Camera extends Node {
    public Camera() {}
    public void setPerspective(float fovy, float aspectRatio, float near, float far) {}
    public void setParallel(float height, float aspectRatio, float near, float far) {}
    public void getTransformTo(Camera target, Transform t) {}
    public float[] getProjection(Transform t) { return null; }
}
