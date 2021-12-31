package code.Rendering.Meshes;

import code.Math.Matrix;
import code.Rendering.DirectX7;
import code.Rendering.MultyTexture;
import code.Rendering.RenderObject;
import code.Rendering.Texture;
import code.Rendering.Vertex;




/**
 * класс для представление меша как элемента буфера. нужен для рендеринга одного меша в нескольких местах
 * @author DDDENISSS
 */
public class MeshImage extends RenderObject {

    private Matrix matrix = new Matrix();
    private int addMiddleZ = 0;
    private Vertex centre;
    
    private Mesh mesh;
    private Morphing animation;
    private MultyTexture tex;
    private int meshRadius;
    private int x1,y1,x2,y2;
    private int frame=0;


    public MeshImage(Mesh mesh, Morphing animation) {
        this(mesh);
        setAnimation(animation);
    }
    
    public MeshImage(Mesh mesh) {
        this.mesh = mesh;
        
        long sx = 0, sy = 0, sz = 0;
        Vertex[] vertexs = mesh.getVertices();
        for (int i = 0; i < vertexs.length; i++) {
            Vertex v = vertexs[i];
            sx += v.x;
            sy += v.y;
            sz += v.z;
        }
        sx /= vertexs.length;
        sy /= vertexs.length;
        sz /= vertexs.length;
        centre = new Vertex((int)sx, (int)sy, (int)sz);
        meshRadius=max(mesh.maxX()-mesh.minX(),mesh.maxZ()-mesh.minZ());
    }
    

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
    
    public Mesh getMesh() {
        return mesh;
    }
    
    public void setAnimation(Morphing animation) {
        this.animation = animation;
    }
    
    public void setTexture(MultyTexture animation) {
        this.tex = animation;
    }
    
    public Morphing getAnimation() {
        return animation;
    }

    /**
     * задать смещение средней Z координаты меша
     * нужно, чтобы полигоны пола не перекрывали обьект
     * @param addMiddleZ смещение средней Z координаты
     */
    public void setAddMiddleZ(int addMiddleZ) {
        this.addMiddleZ = addMiddleZ;
    }
    
    /**
     * задать матрицу трансформации
     * @param matrix 
     */
    public void setMatrix(Matrix matrix) {
        this.matrix.set(matrix);
        centre.transform(matrix);
        sz = centre.rz*4;
    }

    public void setFrame(int f) {
        this.frame=f;
    }
    
    public void render(DirectX7 g3d, Texture texture) {
        if(tex!=null) tex.updateAnimation();
        
        if(animation != null) {
            animation.setFrameNI(frame);
            mesh.setAnimation(animation);
            //if(animation.normalsExist()) mesh.rotateNormals(matrix);
        }
        
        g3d.transformAndProjectVertices(mesh, matrix);
        if(tex!=null) mesh.setTexture(tex);
        mesh.render(g3d, x1, y1, x2, y2);
    }
    
    public void renderFast(DirectX7 g3d, Texture texture) {
        if(tex!=null) tex.updateAnimation();
        if(animation != null) {
            animation.setFrameNI(frame);
            mesh.setAnimation(animation);
        } 
        
        g3d.transformAndProjectVertices(mesh, matrix);
        if(this.tex!=null) mesh.setTexture(tex);
        mesh.render(g3d, x1, y1, x2, y2);
    }

    public boolean isVisible(int x1, int y1, int x2, int y2) {
if( sz-meshRadius > 0 ) return false;
if( -sz/4-meshRadius>DirectX7.drDist ) return false;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        return true;
    }
   private static final int max(int x,int y) {
   if(x>y) return x;
   return y;
   }

}

