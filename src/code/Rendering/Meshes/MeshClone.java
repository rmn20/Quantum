package code.Rendering.Meshes;

import code.Rendering.DirectX7;
import code.Rendering.MultyTexture;
import code.Rendering.RenderObject;
import code.Rendering.Vertex;
import java.util.Vector;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

// ? Хранилище
public class MeshClone {

    private int[] vertexCoords;
    private int[] poly3vData;
    private int[] poly4vData;
    private MultyTexture texture;
    public boolean lighting = false;

    public MeshClone() {}

    public MeshClone(Mesh mesh) {
        Vertex[] var2 = mesh.getVertices();
        RenderObject[] var3 = mesh.getPolygons();
        this.vertexCoords = verticesToArray(var2);
        this.poly3vData = polygons3VToArray(var2, var3);
        this.poly4vData = polygons4VToArray(var2, var3);
        this.texture = mesh.getTexture();
    }

    public void destroy() {
        Vertex[] var2 = null;
        RenderObject[] var3 = null;
        this.vertexCoords = null;
        this.poly3vData = null;
        this.poly4vData = null;
        this.texture = null;
    }

   // из Morphing:
    // private static int[] verticesToArray(Vertex[] vertices)
    private static int[] verticesToArray(Vertex[] vertices) {
        int[] var1 = new int[vertices.length * 3];

        for(int var2 = 0; var2 < vertices.length; ++var2) {
            Vertex var3 = vertices[var2];
            var1[var2 * 3] = var3.x;
            var1[var2 * 3 + 1] = var3.y;
            var1[var2 * 3 + 2] = var3.z;
        }

        return var1;
    }

    // ?
    private static int[] polygons3VToArray(Vertex[] vertices, RenderObject[] polygons) {
        Vector var2 = new Vector();

        for(int var3 = 0; var3 < polygons.length; ++var3) {
            if(polygons[var3] instanceof Polygon3V) {
                var2.addElement(polygons[var3]);
            }
        }

        int[] var6 = new int[var2.size() * 10];

        for(int var5 = 0; var5 < var2.size(); ++var5) {
            Polygon3V var4 = (Polygon3V) var2.elementAt(var5);
            var6[var5 * 10] = (int) search(vertices, var4.a);
            var6[var5 * 10 + 1] = (int) search(vertices, var4.b);
            var6[var5 * 10 + 2] = (int) search(vertices, var4.c);
            var6[var5 * 10 + 3] = (int) var4.au;
            var6[var5 * 10 + 4] = (int) var4.av;
            var6[var5 * 10 + 5] = (int) var4.bu;
            var6[var5 * 10 + 6] = (int) var4.bv;
            var6[var5 * 10 + 7] = (int) var4.cu;
            var6[var5 * 10 + 8] = (int) var4.cv;
            var6[var5 * 10 + 9] = (int) var4.tex;
        }

        return var6;
    }

    // ?
    private static int[] polygons4VToArray(Vertex[] vertices, RenderObject[] polygons) {
        Vector var2 = new Vector();

        for(int var3 = 0; var3 < polygons.length; ++var3) {
            if(polygons[var3] instanceof Polygon4V) {
                var2.addElement(polygons[var3]);
            }
        }

        int[] var6 = new int[var2.size() * 13];

        for(int var5 = 0; var5 < var2.size(); ++var5) {
            Polygon4V var4 = (Polygon4V) var2.elementAt(var5);
            var6[var5 * 13] = (int) search(vertices, var4.a);
            var6[var5 * 13 + 1] = (int) search(vertices, var4.b);
            var6[var5 * 13 + 2] = (int) search(vertices, var4.c);
            var6[var5 * 13 + 3] = (int) search(vertices, var4.d);
            var6[var5 * 13 + 4] = (int) var4.au;
            var6[var5 * 13 + 5] = (int) var4.av;
            var6[var5 * 13 + 6] = (int) var4.bu;
            var6[var5 * 13 + 7] = (int) var4.bv;
            var6[var5 * 13 + 8] = (int) var4.cu;
            var6[var5 * 13 + 9] = (int) var4.cv;
            var6[var5 * 13 + 10] = (int) var4.du;
            var6[var5 * 13 + 11] = (int) var4.dv;
            var6[var5 * 13 + 12] = (int) var4.tex;

        }

        return var6;
    }

   // из Mesh:
    // private static int search(Object[] objs, Object obj)
    private static int search(Object[] objs, Object obj) {
        for(int var2 = 0; var2 < objs.length; ++var2) {
            if(objs[var2] == obj) {
                return var2;
            }
        }

        return -1;
    }

    // ? из Mesh: public Mesh copy()
    public final Mesh copy() {
        int[] var3 = this.vertexCoords;
        Vertex[] var4 = new Vertex[this.vertexCoords.length / 3];

        int var5;
        for(var5 = 0; var5 < var4.length; ++var5) {
            var4[var5] = new Vertex(var3[var5 * 3], var3[var5 * 3 + 1], var3[var5 * 3 + 2]);
        }
        Vertex[] var1;
        Vertex[] var10000 = var1 = var4;
        int[] var13 = this.poly3vData;
        Vertex[] var11 = var10000;
        Polygon3V[] var16 = new Polygon3V[var13.length / 10];

        int var6;
        short var7;
        short var8;
        short var9;
        for(var6 = 0; var6 < var16.length; ++var6) {
            var7 = (short) var13[var6 * 10];
            var8 = (short) var13[var6 * 10 + 1];
            var9 = (short) var13[var6 * 10 + 2];
            if(!lighting) {
                var16[var6] = new Polygon3V(var11[var7], var11[var8], var11[var9], (byte) var13[var6 * 10 + 3], (byte) var13[var6 * 10 + 4], (byte) var13[var6 * 10 + 5], (byte) var13[var6 * 10 + 6], (byte) var13[var6 * 10 + 7], (byte) var13[var6 * 10 + 8]);
            } else if(DirectX7.standartDrawmode == 9) {
                var16[var6] = new LightedPolygon3V(
                        var11[var7], var11[var8], var11[var9],
                        (byte) var13[var6 * 10 + 3], (byte) var13[var6 * 10 + 4],
                        (byte) var13[var6 * 10 + 5], (byte) var13[var6 * 10 + 6],
                        (byte) var13[var6 * 10 + 7], (byte) var13[var6 * 10 + 8]);
            } else {
                var16[var6] = new ColorLightedPolygon3V(
                        var11[var7], var11[var8], var11[var9],
                        (byte) var13[var6 * 10 + 3], (byte) var13[var6 * 10 + 4],
                        (byte) var13[var6 * 10 + 5], (byte) var13[var6 * 10 + 6],
                        (byte) var13[var6 * 10 + 7], (byte) var13[var6 * 10 + 8]);
            }

            var16[var6].tex = (byte) var13[var6 * 10 + 9];
        }
        Polygon3V[] var2 = var16;
        var13 = this.poly4vData;
        var11 = var1;
        Polygon4V[] var15 = new Polygon4V[var13.length / 13];

        for(var6 = 0; var6 < var15.length; ++var6) {
            var7 = (short) var13[var6 * 13];
            var8 = (short) var13[var6 * 13 + 1];
            var9 = (short) var13[var6 * 13 + 2];
            short var10 = (short) var13[var6 * 13 + 3];
            if(!lighting) {
                var15[var6] = new Polygon4V(var11[var7], var11[var8], var11[var9], var11[var10], (byte) var13[var6 * 13 + 4], (byte) var13[var6 * 13 + 5], (byte) var13[var6 * 13 + 6], (byte) var13[var6 * 13 + 7], (byte) var13[var6 * 13 + 8], (byte) var13[var6 * 13 + 9], (byte) var13[var6 * 13 + 10], (byte) var13[var6 * 13 + 11]);
            } else if(DirectX7.standartDrawmode == 9) {
                var15[var6] = new LightedPolygon4V(
                        var11[var7], var11[var8], var11[var9], var11[var10],
                        (byte) var13[var6 * 13 + 4], (byte) var13[var6 * 13 + 5],
                        (byte) var13[var6 * 13 + 6], (byte) var13[var6 * 13 + 7],
                        (byte) var13[var6 * 13 + 8], (byte) var13[var6 * 13 + 9],
                        (byte) var13[var6 * 13 + 10], (byte) var13[var6 * 13 + 11]);
            } else {
                var15[var6] = new ColorLightedPolygon4V(
                        var11[var7], var11[var8], var11[var9], var11[var10],
                        (byte) var13[var6 * 13 + 4], (byte) var13[var6 * 13 + 5],
                        (byte) var13[var6 * 13 + 6], (byte) var13[var6 * 13 + 7],
                        (byte) var13[var6 * 13 + 8], (byte) var13[var6 * 13 + 9],
                        (byte) var13[var6 * 13 + 10], (byte) var13[var6 * 13 + 11]);
            }
            var15[var6].tex = (byte) var13[var6 * 13 + 12];

        }

        Polygon4V[] var12 = var15;
        RenderObject[] var14 = new RenderObject[var16.length + var15.length];
        var5 = 0;

        for(var6 = 0; var6 < var2.length; ++var5) {
            var14[var5] = var2[var6];
            ++var6;
        }

        for(var6 = 0; var6 < var12.length; ++var5) {
            var14[var5] = var12[var6];
            ++var6;
        }

        Mesh var17;
        var17 = new Mesh(var1, var14, this.texture);
        return var17;
    }
}
