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
public class ColorLightedPolygon3V extends Polygon3V {

    public volatile byte ar,ag,ab;
    public volatile byte br,bg,bb;
    public volatile byte cr,cg,cb;
    
    private static int q,sz2;
    private static short sizex,sizey,sizeu,sizev;
    private static byte addmip,fog;
    
    public ColorLightedPolygon3V(ColorLightedPolygon3V p) {
        super((Polygon3V)p);
        ar=p.ar; ag=p.ag; ab=p.ab;
        br=p.br; bg=p.bg; bb=p.bb;
        cr=p.cr; cg=p.cg; cb=p.cb;
    }
    
    public ColorLightedPolygon3V(Vertex a2, Vertex b2, Vertex c2, byte au, byte av, byte bu, byte bv, byte cu, byte cv) {
        super(a2, b2, c2, au, av, bu, bv, cu, cv);
        ar=ag=ab=br=bg=bb=cr=cg=cb=127;
    }
    
public final void render(DirectX7 g3d, Texture texture) {
        if(Main.persQ==0 && Main.mipMapping==false) {renderFast(g3d,texture); return;}
fog=texture.drawmode;

sizex=(short)size(a.sx, b.sx, c.sx);
sizey=(short)size(a.sy, b.sy, c.sy);

int[] al=getLight(a,ar,ag,ab,g3d);
int[] bl=getLight(b,br,bg,bb,g3d);
int[] cl=getLight(c,cr,cg,cb,g3d);


if(fog==5) {
sz2=0xff-(int)MathUtils.calcLight(nx, ny, nz,(int)DirectX7.lightdirx,(int)DirectX7.lightdiry,(int)DirectX7.lightdirz);
}

if(fog==1) szCalcfogAdd();


q=(ny>4000 || ny<-4000) ? 9999999 : Main.q;



if(Main.mipMapping==true && texture.mip!=null) {
texture.rImg=texture.mip[0];
if(texture.drawmode<10 || texture.drawmode>12) {
addmip=(byte)( q>999 ? 2 : 3);
sizeu=(short)(size(au&0xff, bu&0xff, cu&0xff)*texture.rImg.w>>8);
sizev=(short)(size(av&0xff, bv&0xff, cv&0xff)*texture.rImg.h>>8);
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
                a, ((int)(au&0xff)), ((int)(av&0xff)),
                b, ((int)(bu&0xff)), ((int)(bv&0xff)),
                c, ((int)(cu&0xff)), ((int)(cv&0xff)),DirectX7.fogc,fog,sz2,q,al[0],bl[0],cl[0],al[1],bl[1],cl[1],al[2],bl[2],cl[2],nx,ny,nz);
        } else {

            TexturingAffine.paint(g3d,
                    texture,
                a, ((int)(au&0xff)), ((int)(av&0xff)),
                b, ((int)(bu&0xff)), ((int)(bv&0xff)),
                c, ((int)(cu&0xff)), ((int)(cv&0xff)),DirectX7.fogc,fog,sz2,al[0],bl[0],cl[0],al[1],bl[1],cl[1],al[2],bl[2],cl[2],nx,ny,nz);
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
                a, (au&0xff), (av&0xff),
                b, (bu&0xff), (bv&0xff),
                c, (cu&0xff), (cv&0xff),DirectX7.fogc,texture.drawmode,sz2,ar+128,br+128,cr+128,ag+128,bg+128,cg+128,ab+128,bb+128,cb+128,nx,ny,nz);
    
    
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
