package javax.microedition.m3g;
public class PolygonMode extends Object3D {
    public static final int CULL_BACK = 160, CULL_FRONT = 161, CULL_NONE = 162;
    public static final int SHADE_FLAT = 164, SHADE_SMOOTH = 165;
    public static final int WINDING_CCW = 168, WINDING_CW = 169;
    public PolygonMode() {}
    public void setCulling(int mode) {}
    public int getCulling() { return 0; }
    public void setShading(int mode) {}
    public int getShading() { return 0; }
    public void setWinding(int mode) {}
    public int getWinding() { return 0; }
    public void setTwoSidedLightingEnable(boolean e) {}
    public boolean isTwoSidedLightingEnabled() { return false; }
    public void setPerspectiveCorrectionEnable(boolean e) {}
    public boolean isPerspectiveCorrectionEnabled() { return false; }
}
