package code.Rendering.Meshes;

import code.Math.MathUtils;
import code.Rendering.DirectX7;
import code.Rendering.RenderObject;
import code.Rendering.Texture;
import code.Rendering.TexturingAffine;
import code.Rendering.TexturingPers;
import code.Rendering.Vertex;
import code.utils.Main;
import java.util.Random;

/**
 *
 * @author DDDENISSS
 */
public class Polygon4V extends RenderObject {

    public Vertex a, b, c, d;
    public int au, av;
    public int bu, bv;
    public int cu, cv;
    public int du, dv;
    public int tex = 0;
    
    private static int q, sz2;
    private static int sizex, sizey, sizeu, sizev;
    private static int addmip, fog;
    private static Random r;

    public Polygon4V(Polygon4V p) {
        nx = p.nx;
        ny = p.ny;
        nz = p.nz;
        
        a = p.a;
        au = p.au;
        av = p.av;

        b = p.b;
        bu = p.bu;
        bv = p.bv;

        c = p.c;
        cu = p.cu;
        cv = p.cv;

        d = p.d;
        du = p.du;
        dv = p.dv;
        
        tex = p.tex;
    }

    public Polygon4V(
            Vertex a2, Vertex b2, Vertex c2, Vertex d2, 
            byte au, byte av, 
            byte bu, byte bv, 
            byte cu, byte cv, 
            byte du, byte dv
            ) {

        super(a2, b2, c2);
        int oldnx = nx, oldny = ny, oldnz = nz;
        calculateNormals(a2, c2, d2);
        
        nx = nx / 2 + oldnx / 2;
        ny = ny / 2 + oldny / 2;
        nz = nz / 2 + oldnz / 2;
        
        this.a = a2;
        this.au = au & 0xff;
        this.av = av & 0xff;

        this.b = b2;
        this.bu = bu & 0xff;
        this.bv = bv & 0xff;

        this.c = c2;
        this.cu = cu & 0xff;
        this.cv = cv & 0xff;

        this.d = d2;
        this.du = du & 0xff;
        this.dv = dv & 0xff;
    }

    public final void szCalcfogAdd() {
        sz = a.rz;
        if(d.rz < sz) sz = d.rz;
        if(c.rz < sz) sz = c.rz;
        if(b.rz < sz) sz = b.rz;
        
        sz2 = -sz / 255;
        if(DirectX7.fDist / 255 != 0) sz2 = -sz / (DirectX7.fDist / 255);

        if(sz2 > 255) sz2 = 255;
        if(sz2 < 0) sz2 = 0;
        
        sz2 = 255 - sz2;
        int cr = (DirectX7.fogc >> 16) & 0xff;
        int cg = (DirectX7.fogc >> 8) & 0xff;
        int cb = DirectX7.fogc & 0xff;

        cr = cr - sz2;
        if(cr < 0) cr = 0;
        cg = cg - sz2;
        if(cg < 0) cg = 0;
        cb = cb - sz2;
        if(cb < 0) cb = 0;

        sz2 = (cr << 16) | (cg << 8) | cb;
    }

    public final boolean isVisible(int x1, int y1, int x2, int y2) {
        
        if(a.sx < x1 && b.sx < x1 && c.sx < x1 && d.sx < x1) return false;
        if(a.sx > x2 && b.sx > x2 && c.sx > x2 && d.sx > x2) return false;
        if(a.sy < y1 && b.sy < y1 && c.sy < y1 && d.sy < y1) return false;
        if(a.sy > y2 && b.sy > y2 && c.sy > y2 && d.sy > y2) return false;
        
        if((a.sx - b.sx) * (b.sy - d.sy) <= (a.sy - b.sy) * (b.sx - d.sx) && 
                (c.sx - d.sx) * (d.sy - b.sy) <= (c.sy - d.sy) * (d.sx - b.sx)) {
            return false;
        }

        sz = a.rz;
        if(d.rz < sz) sz = d.rz;
        if(c.rz < sz) sz = c.rz;
        if(b.rz < sz) sz = b.rz;
        
        if(sz > 0) return false;
        if(-sz > DirectX7.drDist) return false;

        sz = a.rz + b.rz + c.rz + d.rz/*-MathUtils.pLength(size(a.x,b.x,c.x,d.x),size(a.y,b.y,c.y,d.y),size(a.z,b.z,c.z,d.z))*/;
        if(ny > 4000 || ny < -4000) sz += Main.floorOffsetSZ;
        
        return true;
    }

    public void render(DirectX7 g3d, Texture texture) {

        if(Main.persQ == 0 && Main.mipMapping == false) {
            renderFast(g3d, texture);
            return;
        }

        fog = texture.drawmode;
        boolean pers = Main.persQ > 0;

        if((Main.mipMapping == true && texture.mip != null) || pers) {
            sizex = size(a.sx, b.sx, c.sx, d.sx);
            sizey = size(a.sy, b.sy, c.sy, d.sy);
        }



        if(fog == 5) {
            sz2 = 0xff - MathUtils.calcLight(nx, ny, nz, DirectX7.lightdirx, DirectX7.lightdiry, DirectX7.lightdirz);
        } else if(fog == 1) {
            szCalcfogAdd();
        }

        q = (ny > 4000 || ny < -4000) ? 9999999 : Main.q;

        if(Main.mipMapping == true && texture.mip != null) {
            texture.rImg = texture.mip[0];
            if(texture.drawmode < 10 || texture.drawmode > 12) {
                addmip = q > 999 ? 2 : 3;
                sizeu = size(au, bu, cu, du) * texture.rImg.w >> 8;
                sizev = size(av, bv, cv, dv) * texture.rImg.h >> 8;
                if(((sizex + sizey) >> 1) * addmip < sizeu + sizev) {
                    texture.rImg = texture.mip[1];
                    if(texture.mip != null) {
                        sizeu >>= 1;
                        sizev >>= 1;
                        if(((sizex + sizey) >> 1) * addmip < sizeu + sizev) {
                            texture.rImg = texture.mip[2];
                        }
                    }
                }
            } else {
                if(sizex < texture.rImg.w || sizey < texture.rImg.w) {
                    texture.rImg = texture.mip[1];
                    if(texture.mip != null && (sizex < texture.rImg.w || sizey < texture.rImg.w)) {
                        texture.rImg = texture.mip[2];
                    }
                }
            }

        }

        if(pers) {
            pers = (((sizex > (Main.persQ < 3 ? 70 : 50) || sizey > (Main.persQ < 3 ? 100 : 70)) && texture.perspectiveCorrection) || Main.persQ == 4);
        }

        boolean base = (fog != 3 && fog != 6) || Main.fogQ == 0;

        if(!base) {
            base = min(a.rz, b.rz, c.rz, d.rz) == (a.rz < c.rz ? a.rz : c.rz);
        }

        if(base) {

            if(pers) {
                TexturingPers.paint(g3d, texture,
                        a, ( (au )), ( (av )),
                        b, ( (bu )), ( (bv )),
                        d, ( (du )), ( (dv )), DirectX7.fogc, fog, sz2, q, 255, 255, 255, 255, 255, 255, 255, 255, 255, nx, ny, nz);
                TexturingPers.paint(g3d, texture,
                        b, ( (bu )), ( (bv )),
                        c, ( (cu )), ( (cv )),
                        d, ( (du )), ( (dv )), DirectX7.fogc, fog, sz2, q, 255, 255, 255, 255, 255, 255, 255, 255, 255, nx, ny, nz);
            } else {

                TexturingAffine.paint(g3d, texture,
                        a, ( (au )), ( (av )),
                        b, ( (bu )), ( (bv )),
                        d, ( (du )), ( (dv )), DirectX7.fogc, fog, sz2, 255, 255, 255, 255, 255, 255, 255, 255, 255, nx, ny, nz);
                TexturingAffine.paint(g3d, texture,
                        b, ( (bu )), ( (bv )),
                        c, ( (cu )), ( (cv )),
                        d, ( (du )), ( (dv )), DirectX7.fogc, fog, sz2, 255, 255, 255, 255, 255, 255, 255, 255, 255, nx, ny, nz);
            }
        } else {
            if(pers) {
                TexturingPers.paint(g3d, texture,
                        a, ( (au )), ( (av )),
                        b, ( (bu )), ( (bv )),
                        c, ( (cu )), ( (cv )), DirectX7.fogc, fog, sz2, q, 255, 255, 255, 255, 255, 255, 255, 255, 255, nx, ny, nz);
                TexturingPers.paint(g3d, texture,
                        a, ( (au )), ( (av )),
                        c, ( (cu )), ( (cv )),
                        d, ( (du )), ( (dv )), DirectX7.fogc, fog, sz2, q, 255, 255, 255, 255, 255, 255, 255, 255, 255, nx, ny, nz);
            } else {

                TexturingAffine.paint(g3d, texture,
                        a, ( (au )), ( (av )),
                        b, ( (bu )), ( (bv )),
                        c, ( (cu )), ( (cv )), DirectX7.fogc, fog, sz2, 255, 255, 255, 255, 255, 255, 255, 255, 255, nx, ny, nz);
                TexturingAffine.paint(g3d, texture,
                        a, ( (au )), ( (av )),
                        c, ( (cu )), ( (cv )),
                        d, ( (du )), ( (dv )), DirectX7.fogc, fog, sz2, 255, 255, 255, 255, 255, 255, 255, 255, 255, nx, ny, nz);
            }


        }/*
         if(Main.mipMapping==true && texture.mip!=null) texture.rImg=texture.mip[0];
         TexturingExperiments.renderQuad(g3d,texture,
         a,au&0xff,av&0xff,
         b,bu&0xff,bv&0xff,
         c,cu&0xff,cv&0xff,
         d,du&0xff,dv&0xff);*/
    }

    public void renderFast(DirectX7 g3d, Texture texture) {
        if(texture.drawmode == 5) {
            sz2 = 0xff - MathUtils.calcLight(nx, ny, nz, DirectX7.lightdirx, DirectX7.lightdiry, DirectX7.lightdirz);
        }

        if(texture.drawmode == 1) {
            szCalcfogAdd();
        }

        TexturingAffine.paint(g3d, texture,
                a, (au & 0xff), (av & 0xff),
                b, (bu & 0xff), (bv & 0xff),
                d, (du & 0xff), (dv & 0xff), DirectX7.fogc, texture.drawmode, sz2, 255, 255, 255, 255, 255, 255, 255, 255, 255, nx, ny, nz);
        TexturingAffine.paint(g3d, texture,
                b, (bu & 0xff), (bv & 0xff),
                c, (cu & 0xff), (cv & 0xff),
                d, (du & 0xff), (dv & 0xff), DirectX7.fogc, texture.drawmode, sz2, 255, 255, 255, 255, 255, 255, 255, 255, 255, nx, ny, nz);

    }

    private static int size(int a, int b, int c, int d) {
        int t;
        if(b < a) {
            t = a;
            a = b;
            b = t;
        }
        if(c < a) {
            t = c;
            c = a;
            a = t;
        }
        if(c < b) {
            t = b;
            b = c;
            c = t;
        }
        int min = a < d ? a : d;
        int max = c > d ? c : d;
        return max - min;
    }

    private static final int min(int a, int b, int c, int d) {
        int t;
        if(a == b || b == d || b == c || d == c) {
            return min(a, c, d);
        } else if(a == c) {
            return min(a, b, d);
        } else if(a == d) {
            return min(a, b, c);
        }
        if(b < a) {
            t = a;
            a = b;
            b = t;
        }
        if(c < a) {
            t = c;
            c = a;
            a = t;
        }
        if(c < b) {
            t = b;
            b = c;
            c = t;
        }
        int min = a < d ? a : d;
        return min;
    }

    private static final int min(int a, int b, int c) {
        //its not max its min lol
        if(a == b || b == c) {
            return a < c ? a : c;
        }
        if(a == c) {
            return a < b ? a : b;
        }
        if(a < b && a < c) {
            return a;
        }
        if(b < a && b < c) {
            return b;
        }
        return c;
    }
}
