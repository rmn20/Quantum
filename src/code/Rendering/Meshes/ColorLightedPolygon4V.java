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
public class ColorLightedPolygon4V extends Polygon4V {

    public volatile int ar,ag,ab;
    public volatile int br,bg,bb;
    public volatile int cr,cg,cb;
    public volatile int dr,dg,db;
    
    private static int q,sz2;
    private static int sizex,sizey,sizeu,sizev;
    private static int addmip,fog;
    
    public ColorLightedPolygon4V(ColorLightedPolygon4V p) {
        super((Polygon4V)p);
        ar=p.ar; ag=p.ag; ab=p.ab;
        br=p.br; bg=p.bg; bb=p.bb;
        cr=p.cr; cg=p.cg; cb=p.cb;
        dr=p.dr; dg=p.dg; db=p.db;
    }
    
    public ColorLightedPolygon4V(Vertex a2, Vertex b2, Vertex c2, Vertex d2, byte au, byte av, byte bu, byte bv, byte cu, byte cv, byte du, byte dv) {
        super(a2, b2, c2, d2, au, av, bu, bv, cu, cv, du, dv);
        ar=ag=ab=br=bg=bb=cr=cg=cb=dr=dg=db=255;
    }
    
public final void render(DirectX7 g3d, Texture texture) {
if(Main.persQ==0 && Main.mipMapping==false) {renderFast(g3d,texture); return;}

fog=texture.drawmode;
sizex=size(a.sx, b.sx, c.sx, d.sx);
sizey=size(a.sy, b.sy, c.sy, d.sy);


int[] al=getLight(a,ar,ag,ab,g3d);
int[] bl=getLight(b,br,bg,bb,g3d);
int[] cl=getLight(c,cr,cg,cb,g3d);
int[] dl=getLight(d,dr,dg,db,g3d);


if(fog==5) {
sz2=0xff-(int)MathUtils.calcLight(nx, ny, nz,(int)DirectX7.lightdirx,(int)DirectX7.lightdiry,(int)DirectX7.lightdirz);
}

if(fog==1) szCalcfogAdd();


q=(ny>4000 || ny<-4000) ? 9999999 : Main.q;



if(Main.mipMapping==true && texture.mip!=null) {
texture.rImg=texture.mip[0];
if(texture.drawmode<10 || texture.drawmode>12) {
addmip=q>999? 2 : 3;
sizeu=size(au, bu, cu, du)*texture.rImg.w>>8;
sizev=size(av, bv, cv, dv)*texture.rImg.h>>8;
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
/*Texturing.paintQuad(g3d, texture,
                a, ((int)(au&0xff)), ((int)(av&0xff)),
                b, ((int)(bu&0xff)), ((int)(bv&0xff)),
                c, ((int)(cu&0xff)), ((int)(cv&0xff)),
                d, ((int)(du&0xff)), ((int)(dv&0xff)));*/

boolean pers=(( texture.perspectiveCorrection && (sizex>70-(Main.persQ<3?0:20) || sizey>100-(Main.persQ<3?0:30) ) )&& Main.persQ!=0 || Main.persQ==4);


        if(pers) {
            if(a.rz<0 || b.rz<0 || d.rz<0) TexturingPers.paint(g3d, texture,
                a, ((au)), ((av)),
                b, ((bu)), ((bv)),
                d, ((du)), ((dv)),DirectX7.fogc,fog,sz2,q,al[0],bl[0],dl[0],al[1],bl[1],dl[1],al[2],bl[2],dl[2],nx,ny,nz);
        if(c.rz<0 || b.rz<0 || d.rz<0)  TexturingPers.paint(g3d, texture,
                b, ((bu)), ((bv)),
                c, ((cu)), ((cv)),
                d, ((du)), ((dv)),DirectX7.fogc,fog,sz2,q,bl[0],cl[0],dl[0],bl[1],cl[1],dl[1],bl[2],cl[2],dl[2],nx,ny,nz);
        } else {

           if(a.rz<0 || b.rz<0 || d.rz<0)  TexturingAffine.paint(g3d, texture,
                a, ((au)), ((av)),
                b, ((bu)), ((bv)),
                d, ((du)), ((dv)),DirectX7.fogc,fog,sz2,al[0],bl[0],dl[0],al[1],bl[1],dl[1],al[2],bl[2],dl[2],nx,ny,nz);
        if(c.rz<0 || b.rz<0 || d.rz<0) TexturingAffine.paint(g3d, texture,
                b, ((bu)), ((bv)),
                c, ((cu)), ((cv)),
                d, ((du)), ((dv)),DirectX7.fogc,fog,sz2,bl[0],cl[0],dl[0],bl[1],cl[1],dl[1],bl[2],cl[2],dl[2],nx,ny,nz);
       }

    }
    
    
    
public final void renderFast(DirectX7 g3d, Texture texture) {

if(texture.drawmode==5) {
sz2=0xff-(int)MathUtils.calcLight(nx, ny, nz,(int)DirectX7.lightdirx,(int)DirectX7.lightdiry,(int)DirectX7.lightdirz);
if(sz2<0) sz2=0;
}


if(texture.drawmode==1) szCalcfogAdd();

        TexturingAffine.paint(g3d, texture,
                a, (au), (av),
                b, (bu), (bv),
                d, (du), (dv&0xff),DirectX7.fogc,texture.drawmode,sz2,ar,br,dr,ag,bg,dg,ab,bb,db,nx,ny,nz);
        TexturingAffine.paint(g3d, texture,
                b, (bu), (bv),
                c, (cu), (cv),
                d, (du), (dv),DirectX7.fogc,texture.drawmode,sz2,br,cr,dr,bg,cg,dg,bb,cb,db,nx,ny,nz);
       
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
