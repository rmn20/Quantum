package code.Rendering.Meshes;

import code.Gameplay.Map.LightMapper;
import code.Math.MathUtils;
import code.Rendering.DirectX7;
import code.Rendering.Texture;
import code.Rendering.TexturingAffine;
import code.Rendering.TexturingPers;
import code.Rendering.Vertex;
import code.utils.Main;

/**
 *
 * @author Roman Lahin
 */
public class LightedPolygon4V extends Polygon4V {

    public volatile byte la,lb,lc,ld;
    
    private static int q,sz2;
    private static short sizex,sizey,sizeu,sizev;
    private static byte addmip,fog;
    
    public LightedPolygon4V(LightedPolygon4V p) {
        super((Polygon4V)p);
        la=p.la; lb=p.lb; lc=p.lc; ld=p.ld;
    }
    
    public LightedPolygon4V(ColorLightedPolygon4V p) {
        super((Polygon4V)p);
        la=(byte)((p.ar+p.ag+p.ab)/3); 
        lb=(byte)((p.br+p.bg+p.bb)/3); 
        lc=(byte)((p.cr+p.cg+p.cb)/3); 
        ld=(byte)((p.dr+p.dg+p.db)/3);
    }
    
    public LightedPolygon4V(Vertex a2, Vertex b2, Vertex c2, Vertex d2, byte au, byte av, byte bu, byte bv, byte cu, byte cv, byte du, byte dv) {
        super(a2, b2, c2, d2, au, av, bu, bv, cu, cv, du, dv);
        la=lb=lc=ld=127;
    }
    
public final void render(DirectX7 g3d, Texture texture) {
if(Main.persQ==0 && Main.mipMapping==false) {renderFast(g3d,texture); return;}

fog=texture.drawmode;
sizex=(short)size(a.sx, b.sx, c.sx, d.sx);
sizey=(short)size(a.sy, b.sy, c.sy, d.sy);
int la2=getLight(a,la,g3d);
int lb2=getLight(b,lb,g3d);
int lc2=getLight(c,lc,g3d);
int ld2=getLight(d,ld,g3d);

if(fog==5) {
sz2=0xff-(int)MathUtils.calcLight(nx, ny, nz,(int)DirectX7.lightdirx,(int)DirectX7.lightdiry,(int)DirectX7.lightdirz);
}

if(fog==1) szCalcfogAdd();


q=(ny>4000 || ny<-4000) ? 9999999 : Main.q;



if(Main.mipMapping==true && texture.mip!=null) {
texture.rImg=texture.mip[0];
if(texture.drawmode<10 || texture.drawmode>12) {
addmip=(byte)(q>999? 2 : 3);
sizeu=(short)(size(au&0xff, bu&0xff, cu&0xff, du&0xff)*texture.rImg.w>>8);
sizev=(short)(size(av&0xff, bv&0xff, cv&0xff, dv&0xff)*texture.rImg.h>>8);
if(((sizex+sizey)>>1)*addmip<sizeu+sizev) {
texture.rImg=texture.mip[1];
if(texture.mip!=null) {
sizeu>>=1;sizev>>=1;
if(((sizex+sizey)>>1)*addmip<sizeu+sizev) texture.rImg=texture.mip[2];
}
}
} else {
if(sizex<texture.rImg.w || sizey<texture.rImg.w)  {
    texture.rImg=texture.mip[1];
    if(texture.mip!=null && (sizex<texture.rImg.w || sizey<texture.rImg.w)) {
    texture.rImg=texture.mip[2];
    }
}
}

}

boolean pers=(( texture.perspectiveCorrection && (sizex>70-(Main.persQ<3?0:20) || sizey>100-(Main.persQ<3?0:30) ) )&& Main.persQ!=0 || Main.persQ==4);


        if(pers) {
        TexturingPers.paint(g3d, texture,
                a, au&0xff, av&0xff,
                b, bu&0xff, bv&0xff,
                d, du&0xff, dv&0xff,DirectX7.fogc,fog,sz2,q,la2,lb2,ld2,la2,lb2,ld2,la2,lb2,ld2,nx,ny,nz);
        TexturingPers.paint(g3d, texture,
                b, bu&0xff, bv&0xff,
                c, cu&0xff, cv&0xff,
                d, du&0xff, dv&0xff,DirectX7.fogc,fog,sz2,q,lb2,lc2,ld2,lb2,lc2,ld2,lb2,lc2,ld2,nx,ny,nz);
        } else {
        TexturingAffine.paint(g3d, texture,
                a, au&0xff, av&0xff,
                b, bu&0xff, bv&0xff,
                d, du&0xff, dv&0xff,DirectX7.fogc,fog,sz2,la2,lb2,ld2,la2,lb2,ld2,la2,lb2,ld2,nx,ny,nz);
        TexturingAffine.paint(g3d, texture,
                b, bu&0xff, bv&0xff,
                c, cu&0xff, cv&0xff,
                d, du&0xff, dv&0xff,DirectX7.fogc,fog,sz2,lb2,lc2,ld2,lb2,lc2,ld2,lb2,lc2,ld2,nx,ny,nz);
       }

    }
    
    
    
public final void renderFast(DirectX7 g3d, Texture texture) {

if(texture.drawmode==5) {
sz2=0xff-(int)MathUtils.calcLight(nx, ny, nz,(int)DirectX7.lightdirx,(int)DirectX7.lightdiry,(int)DirectX7.lightdirz);
if(sz2<0) sz2=0;
}


if(texture.drawmode==1) szCalcfogAdd();

        TexturingAffine.paint(g3d, texture,
                a, au&0xff, av&0xff,
                b, bu&0xff, bv&0xff,
                d, du&0xff, dv&0xff,DirectX7.fogc,texture.drawmode,sz2,la+128,lb+128,ld+128,la+128,lb+128,ld+128,la+128,lb+128,ld+128,nx,ny,nz);
        TexturingAffine.paint(g3d, texture,
                b, bu&0xff, bv&0xff,
                c, cu&0xff, cv&0xff,
                d, du&0xff, dv&0xff,DirectX7.fogc,texture.drawmode,sz2,lb+128,lc+128,ld+128,lb+128,lc+128,ld+128,lb+128,lc+128,ld+128,nx,ny,nz);
       
    }


    private static int size(int a, int b, int c, int d) {
int t;
        if(b < a) {
            t = a; a = b; b = t;
        }
        if(c < a) {
            t = c; c = a; a = t;
        }
        if(c < b) {
            t = b; b = c; c= t;
        }
        int min = a<d ? a : d;
        int max = c>d ? c : d;

        return max - min;
    }

   private static final int min(int a,int b,int c,int d) {
        int t;
        if(a==b || b==d || b==c || d==c) return min(a,c,d);
        if(a==c) return min(a,b,d);
        if(a==d) return min(a,b,c);
        if(b < a) {
            t = a; a = b; b = t;
        }
        if(c < a) {
            t = c; c = a; a = t;
        }
        if(c < b) {
            t = b; b = c; c= t;
        }
        int min = a<d ? a : d;
        return min;
   }

   private static final int min(int a,int b,int c) {
       //its not max its min lol
   if(a==b || b==c) return a<c ? a : c;
   if(a==c) return a<b ? a : b;
   if(a<b && a<c) return a;
   if(b<a && b<c) return b;
   return c;
   }
    
}
