package javax.microedition.m3g;
public class CompositingMode extends Object3D {
    public static final int REPLACE = 64, MODULATE = 65, MODULATE_ALPHA = 66;
    public CompositingMode() {}
    public void setBlending(int mode) {}
    public int getBlending() { return 0; }
    public void setAlphaFactor(float f) {}
    public void setThreshold(float t) {}
    public void setDepthWriteEnable(boolean e) {}
    public void setDepthOffset(float factor, float units) {}
    public void setDepthTestEnable(boolean e) {}
    public void setDepthFunc(int f) {}
    public void setColorWriteEnable(boolean e) {}
    public void setAlphaWriteEnable(boolean e) {}
}
