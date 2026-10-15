package javax.microedition.m3g;
public class VertexBuffer extends Object3D {
    public VertexBuffer() {}
    public void setPositions(VertexArray positions, float scale, int[] bias) {}
    public void setNormals(VertexArray normals) {}
    public void setColors(VertexArray colors) {}
    public void setTexCoords(int index, VertexArray texCoords, float scale, int[] bias) {}
    public VertexArray getPositions(float[] scaleBias) { return null; }
    public VertexArray getNormals() { return null; }
    public VertexArray getColors() { return null; }
    public VertexArray getTexCoords(int index, float[] scaleBias) { return null; }
    public void setDefaultColor(int rgba) {}
    public int getDefaultColor() { return 0; }
}
