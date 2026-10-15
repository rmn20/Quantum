package javax.microedition.m3g;
public class Mesh extends Node {
    public Mesh() {}
    public Mesh(VertexBuffer vb, IndexBuffer ib, Appearance ap) {}
    public Mesh(VertexBuffer vb, IndexBuffer[] submeshes, Appearance[] appearances) {}
    public void setVertexBuffer(VertexBuffer vb) {}
    public VertexBuffer getVertexBuffer() { return null; }
    public void setSubmesh(int index, IndexBuffer ib, Appearance ap) {}
    public IndexBuffer getIndexBuffer(int index) { return null; }
    public Appearance getAppearance(int index) { return null; }
    public int getSubmeshCount() { return 0; }
}
