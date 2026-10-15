package javax.microedition.m3g;
public class Graphics3D {
    public static final int DITHER = 1, ANTIALIAS = 2, TRUE_COLOR = 4;
    public static Graphics3D getInstance() { return null; }
    public void bindTarget(Object target) {}
    public void bindTarget(Object target, boolean depthBuffer, int antialiasingFlags) {}
    public Object getTarget() { return null; }
    public void releaseTarget() {}
    public void setDepthRange(float near, float far) {}
    public void setViewport(int x, int y, int width, int height) {}
    public int getViewportX() { return 0; }
    public int getViewportY() { return 0; }
    public int getViewportWidth() { return 0; }
    public int getViewportHeight() { return 0; }
    public float getDepthRangeNear() { return 0; }
    public float getDepthRangeFar() { return 0; }
    public int getMaxViewportWidth() { return 0; }
    public int getMaxViewportHeight() { return 0; }
    public int getMaxTextureDimension() { return 0; }
    public int getMaxSpriteCropDimension() { return 0; }
    public int getProperties() { return 0; }
    public int getLightCount() { return 0; }
    public Light getLight(int index, Transform t) { return null; }
    public void addLight(Light l, Transform t) {}
    public void setLight(int index, Light l, Transform t) {}
    public void clear(Background bg) {}
    public void render(Node node) {}
    public void render(VertexBuffer vertices, IndexBuffer triangles, Appearance appearance, Transform transform) {}
    public void render(VertexBuffer vertices, IndexBuffer triangles, Appearance appearance, Transform transform, int scope) {}
    public void setCamera(Camera camera, Transform transform) {}
    public Camera getCamera(Transform transform) { return null; }
    public int render(java.io.InputStream pngData) { return 0; }
    public void flush() {}
    public static boolean isSupported() { return false; }
}
