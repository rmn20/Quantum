package javax.microedition.m3g;
public class TriangleStripArray extends IndexBuffer {
    public TriangleStripArray(int[] indices, int[] stripLengths) {}
    public TriangleStripArray(VertexArray indices, int[] stripLengths) {}
    //Not in the published JSR-184 API, but src/code/Rendering/Meshes/M3GParser.java:177
    //calls new TriangleStripArray(0, stripLen). Stub kept so the project compiles
    //unmodified; see tools/j2me-stubs/README.md.
    public TriangleStripArray(int vertexCount, int[] stripLengths) {}
}
