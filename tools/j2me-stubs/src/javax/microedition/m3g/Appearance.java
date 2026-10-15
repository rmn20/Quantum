package javax.microedition.m3g;
public class Appearance extends Object3D {
    public Appearance() {}
    public void setTexture2D(Texture2D t) {}
    public Texture2D getTexture2D() { return null; }
    //M3G 1.1 (JSR-184 1.1) multi-texture-unit accessors
    public void setTexture(int index, Texture2D t) {}
    public Texture2D getTexture(int index) { return null; }
    public void setPolygonMode(PolygonMode p) {}
    public PolygonMode getPolygonMode() { return null; }
    public void setMaterial(Material m) {}
    public Material getMaterial() { return null; }
    public void setCompositingMode(CompositingMode c) {}
    public CompositingMode getCompositingMode() { return null; }
    public void setFog(Fog f) {}
    public Fog getFog() { return null; }
    public void setLayer(int l) {}
    public int getLayer() { return 0; }
}
