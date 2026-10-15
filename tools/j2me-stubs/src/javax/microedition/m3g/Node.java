package javax.microedition.m3g;
public abstract class Node extends Transformable {
    public static final int NONE = 144, ORIGIN = 145, X_AXIS = 146, Y_AXIS = 147, Z_AXIS = 148;
    public void setAlphaFactor(float f) {}
    public float getAlphaFactor() { return 0; }
    public void setRenderingEnable(boolean e) {}
    public boolean isRenderingEnabled() { return false; }
    public void setPickingEnable(boolean e) {}
    public boolean isPickingEnabled() { return false; }
    public void setScope(int s) {}
    public int getScope() { return 0; }
    public void setAlignment(Node zRef, int zTarget, Node yRef, int yTarget) {}
    public Group getParent() { return null; }
    public boolean getTransformTo(Node target, Transform t) { return false; }
    public int rayIntersect(int scope, float[] origin, float[] dir, RayIntersection ri) { return 0; }
}
