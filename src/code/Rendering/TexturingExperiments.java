package code.Rendering;

/**
 *
 * @author Roman Lahin
 */

public class TexturingExperiments {
    
    static Line[] lines = new Line[4];
    static Vertex[] vertsBuffer = new Vertex[4];
    static int[] uTexCoords = new int[4];
    static int[] vTexCoords = new int[4];
    
    public static void renderQuad(DirectX7 g3d, Texture tex,
            Vertex a, int au, int av,
            Vertex b, int bu, int bv,
            Vertex c, int cu, int cv,
            Vertex d, int du, int dv) {
        vertsBuffer[0] = a; uTexCoords[0] = au; vTexCoords[0] = av;
        vertsBuffer[1] = b; uTexCoords[1] = bu; vTexCoords[1] = bv;
        vertsBuffer[2] = c; uTexCoords[2] = cu; vTexCoords[2] = cv;
        vertsBuffer[3] = d; uTexCoords[3] = du; vTexCoords[3] = dv;
        renderPolygon(g3d, tex, vertsBuffer, uTexCoords, vTexCoords);
    }
    
    public static void renderPolygon(DirectX7 g3d, Texture tex, Vertex[] verts, int[] uTex, int[] vTex) {
        
        if(lines.length<verts.length) lines = new Line[verts.length];
        
        Vertex[] srtVerts = new Vertex[verts.length];
        System.arraycopy(verts,0,srtVerts,0,verts.length);
        
        for(int size = srtVerts.length-1; size>=1; size--) {
            for(int i = 1; i<=size; i++) {
                if(srtVerts[i-1].sy>srtVerts[i].sy) {
                    Vertex tmp = srtVerts[i-1];
                    srtVerts[i-1] = srtVerts[i];
                    srtVerts[i] = tmp;
                }
            }
        }
        
        
        for(int i=0;i<verts.length;i++) {
            
        }
        
    }

}

class Line {
    int y;
    int x_start, x_end;
    int u_start, u_end;
    int v_start, v_end;
    
    int dx_start, dx_end;
    int du_start, du_end;
    int dv_start, dv_end;
}
