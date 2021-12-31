package code.Rendering;

/**
 *
 * @author Roman Lahin
 */
public class VertexNormal extends Vertex {

   public VertexNormal() {}

   public VertexNormal(int x, int y, int z) {
      this.set(x, y, z);
   }
   
   public VertexNormal(Vertex ver) {
      x=ver.x; y=ver.y; z=ver.z; sx=ver.sx; sy=ver.sy; rz=ver.rz;
   }
   
   public VertexNormal(int[] ver) {
      x=ver[0]; y=ver[1]; z=ver[2];
   }
}
