package code.Rendering.Meshes;

import code.Rendering.RenderObjectBuffer;
import code.Rendering.TMPElement;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.m3g.Background;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.m3g.TriangleStripArray;
import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;
import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.Material;
import javax.microedition.m3g.PolygonMode;
import javax.microedition.m3g.Texture2D;
/**
 *
 * @author Roman Lahin
 */
public class M3GParser {
    public Graphics3D iG3D;
    public Camera cam;
    public Background iBackground;
    private final RenderObjectBuffer buffer;
    
    public M3GParser(int x, int y) {
        iG3D = Graphics3D.getInstance();
        cam = new Camera();
        cam.setParallel( y,              // field of view
            (float)x/ (float)y,  // aspectRatio
            1.0f,      // near clipping plane
            100000000.0f ); // far clipping plane
        buffer = new RenderObjectBuffer();
    } 
    
    public static Appearance appearanceGenerator(String file) {
        try{
        Appearance ap = new Appearance();
        Image2D img2d = new Image2D(Image2D.RGB, Image.createImage(file));

        Texture2D texture = new Texture2D(img2d);
        texture.setFiltering(Texture2D.FILTER_NEAREST,
                Texture2D.FILTER_NEAREST);
        texture.setBlending(Texture2D.FUNC_REPLACE);

        ap.setTexture(0, texture);
        Material mat = new Material();
        mat.setColor(Material.AMBIENT, 0xffffffff);
        mat.setColor(Material.DIFFUSE, 0xffffffff);
        mat.setColor(Material.EMISSIVE, 0xffffffff);
        PolygonMode pm = new PolygonMode();
        pm.setCulling(PolygonMode.CULL_NONE);
        ap.setPolygonMode(pm);
        ap.setMaterial(mat);
        return ap;
        } catch(Exception exc) {
            System.out.println("Error in M3GParser:appearanceGenerator");
            System.out.println("Error: "+exc.toString());
        }
        return null;
    }
    
    public final void meshRender(Mesh mesh, int x1, int y1, int x2, int y2, Appearance ap,code.Rendering.DirectX7 g3d,Graphics g) {
        buffer.addRenderObjects(mesh.getPolygons(), x1, y1, x2, y2);
        buffer.sort(0,buffer.getSize()-1);
        
        TMPElement[] buffer2 = buffer.getBuffer();

        int i = buffer.getSize()-1;
        int objs=0;
        for(; i>=0; i--) {
            TMPElement element = buffer2[i];
            if(element.obj instanceof Polygon4V) objs+=2;
            else if(element.obj instanceof Polygon3V) objs+=1;
        }
        
        int i2=0; i = buffer.getSize()-1;
        short[] vert = new short[objs*9];
        short[] texs = new short[objs*6];  
        int[] stripLen = new int[objs];
        
        for(; i>=0; i--) {
            TMPElement element = buffer2[i];
            if(element.obj instanceof Polygon4V) {
            Polygon4V pol = (Polygon4V) element.obj;
            
            vert[i2]=(short)(pol.a.sx-g3d.width/2);
            vert[i2+1]=(short)(-pol.a.sy+g3d.height/2);
            vert[i2+2]=(short)(pol.a.rz/40);
            
            vert[i2+3]=(short)(pol.b.sx-g3d.width/2);
            vert[i2+4]=(short)(-pol.b.sy+g3d.height/2);
            vert[i2+5]=(short)(pol.b.rz/40);
            
            vert[i2+6]=(short)(pol.c.sx-g3d.width/2);
            vert[i2+7]=(short)(-pol.c.sy+g3d.height/2);
            vert[i2+8]=(short)(pol.c.rz/40);
            
            vert[i2+9]=(short)(pol.c.sx-g3d.width/2);
            vert[i2+10]=(short)(-pol.c.sy+g3d.height/2);
            vert[i2+11]=(short)(pol.c.rz/40);
            
            vert[i2+12]=(short)(pol.d.sx-g3d.width/2);
            vert[i2+13]=(short)(-pol.d.sy+g3d.height/2);
            vert[i2+14]=(short)(pol.d.rz/40);
            
            vert[i2+15]=(short)(pol.a.sx-g3d.width/2);
            vert[i2+16]=(short)(-pol.a.sy+g3d.height/2);
            vert[i2+17]=(short)(pol.a.rz/40);
            
            texs[i2/3*2]=(short)(pol.au&0xff);
            texs[i2/3*2+1]=(short)(pol.av&0xff);
            
            texs[i2/3*2+2]=(short)(pol.bu&0xff);
            texs[i2/3*2+3]=(short)(pol.bv&0xff);
            
            texs[i2/3*2+4]=(short)(pol.cu&0xff);
            texs[i2/3*2+5]=(short)(pol.cv&0xff);
            
            texs[i2/3*2+6]=(short)(pol.cu&0xff);
            texs[i2/3*2+7]=(short)(pol.cv&0xff);
            
            texs[i2/3*2+8]=(short)(pol.du&0xff);
            texs[i2/3*2+9]=(short)(pol.dv&0xff);
            
            texs[i2/3*2+10]=(short)(pol.au&0xff);
            texs[i2/3*2+11]=(short)(pol.av&0xff);
            
            stripLen[i2/9]=3; stripLen[i2/9+1]=3;
            
            i2+=18;
            }
            else if(element.obj instanceof Polygon3V) {
            Polygon3V pol = (Polygon3V) element.obj;
            
            vert[i2]=(short)(pol.a.sx-g3d.width/2);
            vert[i2+1]=(short)(-pol.a.sy+g3d.height/2);
            vert[i2+2]=(short)(pol.a.rz/40);
            
            vert[i2+3]=(short)(pol.b.sx-g3d.width/2);
            vert[i2+4]=(short)(-pol.b.sy+g3d.height/2);
            vert[i2+5]=(short)(pol.b.rz/40);
            
            vert[i2+6]=(short)(pol.c.sx-g3d.width/2);
            vert[i2+7]=(short)(-pol.c.sy+g3d.height/2);
            vert[i2+8]=(short)(pol.c.rz/40);
            
            texs[i2/3*2]=(short)(pol.au&0xff);
            texs[i2/3*2+1]=(short)(pol.av&0xff);
            
            texs[i2/3*2+2]=(short)(pol.bu&0xff);
            texs[i2/3*2+3]=(short)(pol.bv&0xff);
            
            texs[i2/3*2+4]=(short)(pol.cu&0xff);
            texs[i2/3*2+5]=(short)(pol.cv&0xff);
            
            stripLen[i2/9]=3;
            
            i2+=9;
            }
        }
        
        VertexArray vertArray = new VertexArray(vert.length / 3, 3, 2);
        vertArray.set(0, vert.length/3, vert);
        VertexArray texArray = new VertexArray(texs.length / 2, 2, 2);
        texArray.set(0, texs.length/2, texs);
        
        
        VertexBuffer iVb = new VertexBuffer();
        iVb.setPositions(vertArray, 1.0f, null);      // unit scale, zero bias
        iVb.setTexCoords(0, texArray, 0.00390625f, null);    // unit scale, zero bias
 
        // create the index buffer for our object (this tells how to
        // create triangle strips from the contents of the vertex buffer).
        TriangleStripArray iIb = new TriangleStripArray( 0, stripLen );
        
        //iG3D.clear(iBackground);
        iG3D.bindTarget(g);
        iG3D.setCamera(cam, null);
        iG3D.render(iVb, iIb, ap, null);
        buffer.reset();
        iG3D.releaseTarget();
    }
    
}
