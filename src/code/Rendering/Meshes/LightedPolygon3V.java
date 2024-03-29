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
public class LightedPolygon3V extends Polygon3V {

    public volatile int la,lb,lc;
    
    private static int q,sz2;
    private static int sizex,sizey,sizeu,sizev;
    private static int addmip,fog;
    
    public LightedPolygon3V(LightedPolygon3V p) {
        super((Polygon3V)p);
        la=p.la; lb=p.lb; lc=p.lc;
    }
    
    public LightedPolygon3V(ColorLightedPolygon3V p) {
        super((Polygon3V)p);
        la=((p.ar+p.ag+p.ab)/3); 
        lb=((p.br+p.bg+p.bb)/3); 
        lc=((p.cr+p.cg+p.cb)/3);
    }
    
    public LightedPolygon3V(Vertex a2, Vertex b2, Vertex c2, byte au, byte av, byte bu, byte bv, byte cu, byte cv) {
        super(a2, b2, c2, au, av, bu, bv, cu, cv);
        la=lb=lc=255;
    }
    
public final void render(DirectX7 g3d, Texture texture) {
        if(Main.persQ==0 && Main.mipMapping==false) {renderFast(g3d,texture); return;}
fog=texture.drawmode;

sizex=size(a.sx, b.sx, c.sx);
sizey=size(a.sy, b.sy, c.sy);

int la2=getLight(a,la,g3d);
int lb2=getLight(b,lb,g3d);
int lc2=getLight(c,lc,g3d);


if(fog==5) {
sz2=0xff-MathUtils.calcLight(nx, ny, nz,DirectX7.lightdirx,DirectX7.lightdiry,DirectX7.lightdirz);
}

if(fog==1) szCalcfogAdd();


q=(ny>4000 || ny<-4000) ? 9999999 : Main.q;



if(Main.mipMapping==true && texture.mip!=null) {
texture.rImg=texture.mip[0];
if(texture.drawmode<10 || texture.drawmode>12) {
addmip=( q>999 ? 2 : 3);
sizeu=(size(au, bu, cu)*texture.rImg.w>>8);
sizev=(size(av, bv, cv)*texture.rImg.h>>8);
if(((sizex+sizey)>>1)*addmip<sizeu+sizev) {
texture.rImg=texture.mip[1];
if(texture.mip!=null) {
sizeu>>=1;sizev>>=1;
if(((sizex+sizey)>>1)*addmip<sizeu+sizev) {
texture.rImg=texture.mip[2];
}
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


        if(( texture.perspectiveCorrection && (sizex>30-(Main.persQ<3?0:15) || sizey>30-(Main.persQ<3?0:15) ) )&& Main.persQ!=0 || Main.persQ==4) {
            TexturingPers.paint(g3d,
                    texture,
                a, ((au)), ((av)),
                b, ((bu)), ((bv)),
                c, ((cu)), ((cv)),DirectX7.fogc,fog,sz2,q,la2,lb2,lc2,la2,lb2,lc2,la2,lb2,lc2,nx,ny,nz);
        } else {

            TexturingAffine.paint(g3d,
                    texture,
                a, ((au)), ((av)),
                b, ((bu)), ((bv)),
                c, ((cu)), ((cv)),DirectX7.fogc,fog,sz2,la2,lb2,lc2,la2,lb2,lc2,la2,lb2,lc2,nx,ny,nz);
    }
    
}
    
public void renderFast(DirectX7 g3d, Texture texture) {

if(texture.drawmode==5) {
sz2=0xff-(int)MathUtils.calcLight(nx, ny, nz,(int)DirectX7.lightdirx,(int)DirectX7.lightdiry,(int)DirectX7.lightdirz);
if(sz2<0) sz2=0;
}

if(texture.drawmode==1) if(fog==1) szCalcfogAdd();

            TexturingAffine.paint(g3d,
                    texture,
                a, (au), (av),
                b, (bu), (bv),
                c, (cu), (cv),DirectX7.fogc,texture.drawmode,sz2,la,lb,lc,la,lb,lc,la,lb,lc,nx,ny,nz);
    
    
}
    private static final int size(int a, int b, int c) {
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
        return c - a;
    }
    
}
