package javax.microedition.m3g;
public class Material extends Object3D {
    public static final int AMBIENT = 1024, DIFFUSE = 2048, EMISSIVE = 4096, SPECULAR = 8192;
    public Material() {}
    public void setColor(int target, int argb) {}
    public int getColor(int target) { return 0; }
    public void setShininess(float s) {}
    public float getShininess() { return 0; }
    public void setVertexColorTrackingEnable(boolean e) {}
    public boolean isVertexColorTrackingEnabled() { return false; }
}
