package code.Rendering;
import code.Gameplay.Map.LightMapper;
import code.HUD.DeveloperMenu;
import code.Math.MathUtils;
import code.utils.Main;
import code.Rendering.Texture;
/**
 *
 * @author Roman Lahin
 */
public class TexturingPers {
   public final static int fp = 12, FP = 1<<fp;
   public static int move = 150;

   /*
   static {
       Texture tex=Texture.createTexture("/dither pattern.png");
       String out=" ";
       for(int i=0;i<tex.rImg.img.length;i++) {
           out+=(tex.rImg.img[i]&0xff)*4096/0xff+" ,";
       }
       System.out.println(out);
       ditherPattern=new short[]{0 ,2039 ,514 ,2570 ,128 ,2168 ,642 ,2698 ,3084 ,1028 ,3581 ,
           1542 ,3196 ,1156 ,3710 ,1670 ,771 ,2810 ,257 ,2296 ,899 ,2939 ,369 ,2441 ,3838 ,
           1799 ,3324 ,1285 ,3967 ,1927 ,3453 ,1397 ,192 ,2248 ,706 ,2746 ,64 ,2120 ,578 ,
           2618 ,3260 ,1220 ,3774 ,1734 ,3132 ,1092 ,3646 ,1606 ,963 ,3019 ,449 ,2505 ,
           819 ,2875 ,321 ,2377 ,4031 ,1975 ,3517 ,1477 ,3903 ,1863 ,3389 ,1333 ,};
       if((col=tex[((( 
        ((v+=dv)>>>fp) +(((dither=ditherPattern[ditherY+(x1)%8])<=(v%FP))?1:0))<<widthBIT) 
        +((u+=du)>>>fp)+((dither<=(u%FP))?1:0)) &lengthBIT]) != 0) rgb[x1]=col; 

x1++;
}
   }*/
        /*
    static Vertex ta = new Vertex();
    static Vertex tb = new Vertex();
    static Vertex tc = new Vertex();
    
    public static final void paint(DirectX7 g3d, Texture texture,
            Vertex a, int au, int av,
            Vertex b, int bu, int bv,
            Vertex c, int cu, int cv,int fogc,byte dmode,int fogi,int q,
            int al,int bl,int cl,
            int ag,int bg,int cg,
            int ab,int bb,int cb,
            short nx,short ny,short nz) {
        
        NearClipper.set(a,au,av,b,bu,bv,c,cu,cv,al,bl,cl,ag,bg,cg,ab,bb,cb);
        if(!NearClipper.clip(-1)) return;
        
        NearClipper.project(g3d);
        
        ta.sx = NearClipper.verts[0].x;
        ta.sy = NearClipper.verts[0].y;
        ta.rz = NearClipper.verts[0].z;
        
        for(int i=0; i<NearClipper.vertexCount-1; i++) {
            tb.sx = NearClipper.verts[i+1].x;
            tb.sy = NearClipper.verts[i+1].y;
            tb.rz = NearClipper.verts[i+1].z;
            tc.sx = NearClipper.verts[i+2].x;
            tc.sy = NearClipper.verts[i+2].y;
            tc.rz = NearClipper.verts[i+2].z;
            
            paintPersSub(g3d,texture,
                    ta, NearClipper.verts[0].u, NearClipper.verts[0].v,
                    tb, NearClipper.verts[i+1].u, NearClipper.verts[i+1].v,
                    tc, NearClipper.verts[i+2].u, NearClipper.verts[i+2].v,
                    fogc, dmode, fogi, q,
                    NearClipper.verts[0].r, NearClipper.verts[i+1].r, NearClipper.verts[i+2].r,
                    NearClipper.verts[0].g, NearClipper.verts[i+1].g, NearClipper.verts[i+2].g,
                    NearClipper.verts[0].b, NearClipper.verts[i+1].b, NearClipper.verts[i+2].b,
                    nx, ny, nz);
        }
    }*/
    
    public static final void paint(DirectX7 g3d, Texture texture,
            Vertex a, int au, int av,
            Vertex b, int bu, int bv,
            Vertex c, int cu, int cv,int fogc,int dmode,int qz,int q,
            int al,int bl,int cl,
            int ag,int bg,int cg,
            int ab,int bb,int cb,int nx,int ny,int nz) {
        
        if (DeveloperMenu.renderPolygonsOverwrite) {
            TexturingAffine.paintOverwrite(g3d, a, b, c);
            return;
        }
        if (texture.rImg.alphaMixing == false && texture.drawmode == 4) {
            dmode = 0;
        }

        if (texture.rImg.w != 256) {
            if((texture.mip!=null && texture.mip[0].w<255) || (texture.mip==null && texture.rImg.w<255)) {
                if(au == 255) au = 256;
                if(bu == 255) bu = 256;
                if(cu == 255) cu = 256;
            }
            au = (au * texture.rImg.w) >> 8;
            bu = (bu * texture.rImg.w) >> 8;
            cu = (cu * texture.rImg.w) >> 8;
        }
        
        if (texture.rImg.h != 256) {
            if((texture.mip!=null && texture.mip[0].h<256) || (texture.mip==null && texture.rImg.h<256)) {
                if(av == 255) av = 256;
                if(bv == 255) bv = 256;
                if(cv == 255) cv = 256;
            }
            av = (av * texture.rImg.h) >> 8;
            bv = (bv * texture.rImg.h) >> 8;
            cv = (cv * texture.rImg.h) >> 8;
        }

        if (dmode == 8) {
            TexturingAffine.paintFill(g3d, a, b, c, texture, au, av);
            return;
        }

        if (dmode == 10) {
            TexturingAffine.paintDitherGradient(g3d, texture, a, al, b, bl, c, cl);
            return;
        }

        if (dmode == 11) {
            al = 0xff + a.rz * 0xFF / DirectX7.waterDistance;
            if (al > 0xFF) al = 0xFF;
            if (al < 0) al = 0;
            bl = 0xff + b.rz * 0xFF / DirectX7.waterDistance;
            if (bl > 0xFF) bl = 0xFF;
            if (bl < 0) bl = 0;
            cl = 0xff + c.rz * 0xFF / DirectX7.waterDistance;
            if (cl > 0xFF) cl = 0xFF;
            if (cl < 0) cl = 0;
            TexturingAffine.paintDitherGradient(g3d, texture, a, al, b, bl, c, cl);
            return;
        }

        if (dmode == 12) {
            al = 0xff + a.rz * 0xFF / DirectX7.fDist;
            if (al > 0xFF) al = 0xFF;
            if (al < 0) al = 0;
            bl = 0xff + b.rz * 0xFF / DirectX7.fDist;
            if (bl > 0xFF) bl = 0xFF;
            if (bl < 0) bl = 0;
            cl = 0xff + c.rz * 0xFF / DirectX7.fDist;
            if (cl > 0xFF) cl = 0xFF;
            if (cl < 0) cl = 0;
            TexturingAffine.paintDitherGradient(g3d, texture, a, al, b, bl, c, cl);
            return;
        }

        boolean colorLighting=!(al==ag && ag==ab && bl==bg && bg==bb && cl==cb && cb==cg);
        
        if(b.sy < a.sy) {
            Vertex t = a; a = b; b = t;
            int tb = au; au = bu; bu = tb;
            tb = av; av = bv; bv = tb;
            tb = al; al = bl; bl = tb;
            tb = ag; ag = bg; bg = tb;
            tb = ab; ab = bb; bb = tb;
        }
        if(c.sy < a.sy) {
            Vertex t = c; c = a; a = t;
            int tb = cu; cu = au; au = tb;
            tb = cv; cv = av; av = tb;
            tb = cl; cl = al; al = tb;
            tb = cg; cg = ag; ag = tb;
            tb = cb; cb = ab; ab = tb;
        }
        if(c.sy < b.sy) {
            Vertex t = b; b = c; c = t;
            int tb = bu; bu = cu; cu = tb;
            tb = bv; bv = cv; cv = tb;
            tb = bl; bl = cl; cl = tb;
            tb = bg; bg = cg; cg = tb;
            tb = bb; bb = cb; cb = tb;
        }
        

        if(a.sy == c.sy) return;

        int af, bf, cf;
        af = bf = cf = 255;
        if (DirectX7.fDist > 1) {
            af = 0xff + a.rz * 0xFF / DirectX7.fDist;
            if (af > 0xFF) af = 0xFF;
            if (af < 0) af = 0;
            bf = 0xff + b.rz * 0xFF / DirectX7.fDist;
            if (bf > 0xFF) bf = 0xFF;
            if (bf < 0) bf = 0;
            cf = 0xff + c.rz * 0xFF / DirectX7.fDist;
            if (cf > 0xFF) cf = 0xFF;
            if (cf < 0) cf = 0;
        }
        
        if (texture.drawmode == 9 || texture.drawmode == 13) {
            af = al;
            bf = bl;
            cf = cl;
        }
        if(texture.drawmode == 13 && Main.fogQ==1) {
            colorLighting=false;
            af = ag = ab = (af+ag+ab)/3;
            bf = bg = bb = (bf+bg+bb)/3;
            cf = cg = cb = (cf+cg+cb)/3;
        }
        
        if (LightMapper.cameraVectorLight && texture.drawmode == 6) {
            af = af * MathUtils.calcLight(nx, ny, nz, a.x - g3d.getCamera().m03, a.y - g3d.getCamera().m13, a.z - g3d.getCamera().m23) / 255;
            bf = bf * MathUtils.calcLight(nx, ny, nz, b.x - g3d.getCamera().m03, b.y - g3d.getCamera().m13, b.z - g3d.getCamera().m23) / 255;
            cf = cf * MathUtils.calcLight(nx, ny, nz, c.x - g3d.getCamera().m03, c.y - g3d.getCamera().m13, c.z - g3d.getCamera().m23) / 255;
        }
        if ((dmode == 5 || dmode == 6 || dmode == 9) && af < 1 && bf < 1 && cf < 1) {
            TexturingAffine.paintFill(g3d, a, b, c, 0);
            return;
        }


        final int W_UNIT = texture.rImg.W_UNIT;

        int tempI =(int)( (W_UNIT>>fp) + move);
        int awz, bwz, cwz;
        
        if(a.rz<0) awz = tempI/(-a.rz+move); else awz = tempI/move;
        if(b.rz<0) bwz = tempI/(-b.rz+move); else bwz = tempI/move;
        if(c.rz<0) cwz = tempI/(-c.rz+move); else cwz = tempI/move;


        int auz = au*awz; int avz = av*awz;
        int buz = bu*bwz; int bvz = bv*bwz;
        int cuz = cu*cwz; int cvz = cv*cwz;

        tempI = c.sy - a.sy;
        int dx_start = ((c.sx - a.sx)<<fp) / tempI;
        int dwz_start = ((cwz - awz)<<fp) / tempI;
        int duz_start = ((cuz - auz)<<fp) / tempI;
        int dvz_start = ((cvz - avz)<<fp) / tempI;
        int df_start = ((cf - af)<<fp) / tempI;
        int dg_start = ((cg - ag)<<fp) / tempI;
        int db_start = ((cb - ab)<<fp) / tempI;
        
        int dx_end=0, dwz_end=0, duz_end=0, dvz_end=0, df_end=0, dg_end=0, db_end=0;
        if(b.sy != a.sy) {
            tempI = b.sy - a.sy;
            dx_end = ((b.sx - a.sx)<<fp) / tempI;
            dwz_end = ((bwz - awz)<<fp) / tempI;
            duz_end = ((buz - auz)<<fp) / tempI ;
            dvz_end = ((bvz - avz)<<fp) / tempI ;
            df_end = ((bf - af)<<fp) / tempI;
            dg_end = ((bg - ag)<<fp) / tempI;
            db_end = ((bb - ab)<<fp) / tempI;
        }

        tempI = b.sy - a.sy;
        int x_start = (a.sx<<fp) + dx_start* tempI;
        int wz_start = (awz<<fp) + dwz_start* tempI;
        int uz_start = (auz<<fp) + duz_start* tempI;
        int vz_start = (avz<<fp) + dvz_start* tempI;
        int f_start = (af<<fp) + df_start * tempI;
        int g_start = (ag<<fp) + dg_start * tempI;
        int b_start = (ab<<fp) + db_start * tempI;


        int x_end = b.sx<<fp;
        int wz_end = bwz<<fp;
        int uz_end = buz<<fp;
        int vz_end = bvz<<fp;
        int f_end = bf<<fp;
        int g_end = bg<<fp;
        int b_end = bb<<fp;


        tempI = (x_start - x_end)>>fp;
        if( tempI == 0 ) return;
        final int duz = (uz_start - uz_end)/ tempI;// �������� ������
        final int dvz = (vz_start - vz_end) / tempI;//
        final int dwz = (wz_start - wz_end)/ tempI;//
        final int df = (f_start - f_end) / tempI;//
        final int dg = (g_start - g_end) / tempI;//
        final int db = (b_start - b_end) / tempI;//


        
        int tempII = c.sy-a.sy;
        int du_start = ((cu - au)<<fp) / tempII; //
        int dv_start = ((cv - av)<<fp) / tempII;//

        tempII = b.sy - a.sy;
        int x_start2 = (a.sx<<fp) + dx_start * tempII; //������ �����
        int x_end2 = b.sx<<fp; //����� �����
        int u_start = (au<<fp) + du_start * tempII; //
        int v_start = (av<<fp) + dv_start * tempII;//

        int u_end = bu<<fp;  //
        int v_end = bv<<fp; //

        tempII = (x_start2-x_end2) >> fp;  // �������� ������
        int du=0,dv=0;
        if(tempII == 0) return;
        du = (u_start-u_end)/tempII; //
        dv = (v_start-v_end)/tempII; //
        
        x_start = x_end = a.sx<<fp;
        wz_start = wz_end = awz<<fp;
        uz_start = uz_end = auz<<fp;
        vz_start = vz_end = avz<<fp;
        f_start = f_end = af<<fp;
        g_start = g_end = ag<<fp;
        b_start = b_end = ab<<fp;

        int dx_start2=dx_start;
        int duz_start2=duz_start;
        int dvz_start2=dvz_start;
        int dwz_start2=dwz_start;
        int df_start2=df_start;
        int dg_start2=dg_start;
        int db_start2=db_start;
        
        if(b.sy>0) {
            int y_start = a.sy; //������� �� Y �� �������

            if(y_start<0) {
                x_start -= dx_start*y_start;
                wz_start -= dwz_start*y_start;
                uz_start -= duz_start*y_start;
                vz_start -= dvz_start*y_start;
                f_start -= df_start*y_start;
                g_start -= dg_start*y_start;
                b_start -= db_start*y_start;

                x_end -= dx_end*y_start;
                wz_end -= dwz_end*y_start;
                uz_end -= duz_end*y_start;
                vz_end -= dvz_end*y_start;
                f_end -= df_end*y_start;
                g_end -= dg_end*y_start;
                b_end -= db_end*y_start;
                y_start = 0;
            }
            final int y_end = b.sy<g3d.height ? b.sy : g3d.height;

        

        if (dx_start>dx_end) {
        int tmp=x_start; x_start=x_end; x_end = tmp;
        tmp=dx_start; dx_start=dx_end; dx_end = tmp;
        
        tmp=uz_start; uz_start=uz_end; uz_end = tmp;
        tmp=vz_start; vz_start=vz_end; vz_end = tmp;
        tmp=wz_start; wz_start=wz_end; wz_end = tmp;
        
        tmp=duz_start; duz_start=duz_end; duz_end = tmp;
        tmp=dvz_start; dvz_start=dvz_end; dvz_end = tmp;
        tmp=dwz_start; dwz_start=dwz_end; dwz_end = tmp;
        
        f_start=f_end; g_start=g_end; b_start=b_end;
        df_start=df_end; dg_start=dg_end; db_start=db_end;
        }

                switch (dmode) {
                    case 0: {
                        paintMiniTrianglePers_0(g3d, texture, y_start, y_end,
                                x_start, wz_start, uz_start, vz_start,
                                x_end, wz_end, uz_end, vz_end,
                                dx_start, dwz_start, duz_start, dvz_start,
                                dx_end, dwz_end, duz_end, dvz_end,
                                dwz, duz, dvz, fogc, q);
                        break;

                    }
                    case 1: {
                        paintMiniTrianglePers_1(g3d, texture, y_start, y_end,
                                x_start, wz_start, uz_start, vz_start,
                                x_end, wz_end, uz_end, vz_end,
                                dx_start, dwz_start, duz_start, dvz_start,
                                dx_end, dwz_end, duz_end, dvz_end,
                                dwz, duz, dvz, fogc, qz, q);
                        break;
                    }
                    case 2: {
                        paintMiniTrianglePers_2(g3d, texture, y_start, y_end,
                                x_start, wz_start, uz_start, vz_start,
                                x_end, wz_end, uz_end, vz_end,
                                dx_start, dwz_start, duz_start, dvz_start,
                                dx_end, dwz_end, duz_end, dvz_end,
                                dwz, duz, dvz, fogc, q);
                        break;
                    }
                    case 3: {
                        paintMiniTrianglePers_3(g3d, texture, y_start, y_end,
                                x_start, wz_start, uz_start, vz_start,
                                x_end, wz_end, uz_end, vz_end,
                                dx_start, dwz_start, duz_start, dvz_start,
                                dx_end, dwz_end, duz_end, dvz_end,
                                dwz, duz, dvz, fogc, qz, q, a, b, c, f_start, f_end, df_start, df_end, df);
                        break;
                    }
                    case 4: {
                        paintMiniTrianglePers_4(g3d, texture, y_start, y_end,
                                x_start, wz_start, uz_start, vz_start,
                                x_end, wz_end, uz_end, vz_end,
                                dx_start, dwz_start, duz_start, dvz_start,
                                dx_end, dwz_end, duz_end, dvz_end,
                                dwz, duz, dvz, fogc, q);
                        break;
                    }
                    case 5: {
                        paintMiniTrianglePers_5(g3d, texture, y_start, y_end,
                                x_start, wz_start, uz_start, vz_start,
                                x_end, wz_end, uz_end, vz_end,
                                dx_start, dwz_start, duz_start, dvz_start,
                                dx_end, dwz_end, duz_end, dvz_end,
                                dwz, duz, dvz, fogc, qz, q, a, b, c, f_start, f_end, df_start, df_end, df);
                        break;
                    }
                    case 6: {
                        paintMiniTrianglePers_5(g3d, texture, y_start, y_end,
                                x_start, wz_start, uz_start, vz_start,
                                x_end, wz_end, uz_end, vz_end,
                                dx_start, dwz_start, duz_start, dvz_start,
                                dx_end, dwz_end, duz_end, dvz_end,
                                dwz, duz, dvz, fogc, qz, q, a, b, c, f_start, f_end, df_start, df_end, df);
                        break;
                    }
                    case 7: {
                        paintPers_glass(g3d, texture, y_start, y_end,
                                x_start, wz_start, uz_start, vz_start,
                                x_end, wz_end, uz_end, vz_end,
                                dx_start, dwz_start, duz_start, dvz_start,
                                dx_end, dwz_end, duz_end, dvz_end,
                                dwz, duz, dvz, fogc, q);
                        break;
                    }
                    case 9: {
                        paintMiniTrianglePers_5(g3d, texture, y_start, y_end,
                                x_start, wz_start, uz_start, vz_start,
                                x_end, wz_end, uz_end, vz_end,
                                dx_start, dwz_start, duz_start, dvz_start,
                                dx_end, dwz_end, duz_end, dvz_end,
                                dwz, duz, dvz, fogc, qz, q, a, b, c, f_start, f_end, df_start, df_end, df);
                        break;
                    }
                    case 13: {
                        if (colorLighting) {
                            paintMiniTrianglePers_13(g3d, texture, y_start, y_end,
                                    x_start, wz_start, uz_start, vz_start,
                                    x_end, wz_end, uz_end, vz_end,
                                    dx_start, dwz_start, duz_start, dvz_start,
                                    dx_end, dwz_end, duz_end, dvz_end,
                                    dwz, duz, dvz, fogc, qz, q, a, b, c, f_start, f_end, df_start, df_end, df,
                                    g_start, g_end, dg_start, dg_end, dg,
                                    b_start, b_end, db_start, db_end, db);
                        } else {
                            paintMiniTrianglePers_5(g3d, texture, y_start, y_end,
                                x_start, wz_start, uz_start, vz_start,
                                x_end, wz_end, uz_end, vz_end,
                                dx_start, dwz_start, duz_start, dvz_start,
                                dx_end, dwz_end, duz_end, dvz_end,
                                dwz, duz, dvz, fogc, qz, q, a, b, c, f_start, f_end, df_start, df_end, df);
                        }
                        break;
                    }
                }
            }
        //************************************************//
        if(c.sy == b.sy || c.sy < 0) return;
        tempI = b.sy - a.sy;
        duz_start = duz_start2;
        dvz_start = dvz_start2;
        dwz_start = dwz_start2;
        dx_start = dx_start2;
        df_start = df_start2;
        dg_start = dg_start2;
        db_start = db_start2;
        x_start = (a.sx<<fp) + dx_start* tempI;
        wz_start = (awz<<fp) + dwz_start* tempI;
        uz_start = (auz<<fp) + duz_start* tempI;
        vz_start = (avz<<fp) + dvz_start* tempI;
        f_start = (af<<fp) + df_start * tempI;
        g_start = (ag<<fp) + dg_start * tempI;
        b_start = (ab<<fp) + db_start * tempI;


        x_end = b.sx<<fp;
        wz_end = bwz<<fp;
        uz_end = buz<<fp;
        vz_end = bvz<<fp;
        f_end = bf<<fp;
        g_end = bg<<fp;
        b_end = bb<<fp;
        tempI = c.sy - b.sy;
        dx_end = ((c.sx - b.sx)<<fp) / tempI;
        dwz_end = ((cwz - bwz)<<fp) / tempI;
        duz_end = ((cuz - buz)<<fp) / tempI;
        dvz_end = ((cvz - bvz)<<fp) / tempI;
        df_end = ((cf - bf)<<fp) / tempI;
        dg_end = ((cg - bg)<<fp) / tempI;
        db_end = ((cb - bb)<<fp) / tempI;



        int y_start = b.sy; //������� �� Y �� �������

        if(y_start<0) {
            x_start -= dx_start*y_start;
            wz_start -= dwz_start*y_start;
            uz_start -= duz_start*y_start;
            vz_start -= dvz_start*y_start;
            f_start -= df_start*y_start;
            g_start -= dg_start*y_start;
            b_start -= db_start*y_start;

            x_end -= dx_end*y_start;
            wz_end -= dwz_end*y_start;
            uz_end -= duz_end*y_start;
            vz_end -= dvz_end*y_start;
            f_end -= df_end*y_start;
            g_end -= dg_end*y_start;
            b_end -= db_end*y_start;
            y_start = 0;
        }


        final int y_end = c.sy<g3d.height ? c.sy : g3d.height;


        if (x_start>x_end) {
        int tmp=x_start; x_start=x_end; x_end = tmp;
        tmp=dx_start; dx_start=dx_end; dx_end = tmp;
        
        tmp=uz_start; uz_start=uz_end; uz_end = tmp;
        tmp=vz_start; vz_start=vz_end; vz_end = tmp;
        tmp=wz_start; wz_start=wz_end; wz_end = tmp;
        
        tmp=duz_start; duz_start=duz_end; duz_end = tmp;
        tmp=dvz_start; dvz_start=dvz_end; dvz_end = tmp;
        tmp=dwz_start; dwz_start=dwz_end; dwz_end = tmp;
        
        f_start=f_end; g_start=g_end; b_start=b_end;
        df_start=df_end; dg_start=dg_end; db_start=db_end;
        }
        
            switch (dmode) {
                case 0: {
                    paintMiniTrianglePers_0(g3d, texture, y_start, y_end,
                            x_start, wz_start, uz_start, vz_start,
                            x_end, wz_end, uz_end, vz_end,
                            dx_start, dwz_start, duz_start, dvz_start,
                            dx_end, dwz_end, duz_end, dvz_end,
                            dwz, duz, dvz, fogc, q);
                    break;
                }
                case 1: {
                    paintMiniTrianglePers_1(g3d, texture, y_start, y_end,
                            x_start, wz_start, uz_start, vz_start,
                            x_end, wz_end, uz_end, vz_end,
                            dx_start, dwz_start, duz_start, dvz_start,
                            dx_end, dwz_end, duz_end, dvz_end,
                            dwz, duz, dvz, fogc, qz, q);
                    break;
                }
                case 2: {
                    paintMiniTrianglePers_2(g3d, texture, y_start, y_end,
                            x_start, wz_start, uz_start, vz_start,
                            x_end, wz_end, uz_end, vz_end,
                            dx_start, dwz_start, duz_start, dvz_start,
                            dx_end, dwz_end, duz_end, dvz_end,
                            dwz, duz, dvz, fogc, q);
                    break;
                }
                case 3: {
                    paintMiniTrianglePers_3(g3d, texture, y_start, y_end,
                            x_start, wz_start, uz_start, vz_start,
                            x_end, wz_end, uz_end, vz_end,
                            dx_start, dwz_start, duz_start, dvz_start,
                            dx_end, dwz_end, duz_end, dvz_end,
                            dwz, duz, dvz, fogc, qz, q, a, b, c, f_start, f_end, df_start, df_end, df);
                    break;
                }
                case 4: {
                    paintMiniTrianglePers_4(g3d, texture, y_start, y_end,
                            x_start, wz_start, uz_start, vz_start,
                            x_end, wz_end, uz_end, vz_end,
                            dx_start, dwz_start, duz_start, dvz_start,
                            dx_end, dwz_end, duz_end, dvz_end,
                            dwz, duz, dvz, fogc, q);
                    break;
                }
                case 5: {
                    paintMiniTrianglePers_5(g3d, texture, y_start, y_end,
                            x_start, wz_start, uz_start, vz_start,
                            x_end, wz_end, uz_end, vz_end,
                            dx_start, dwz_start, duz_start, dvz_start,
                            dx_end, dwz_end, duz_end, dvz_end,
                            dwz, duz, dvz, fogc, qz, q, a, b, c, f_start, f_end, df_start, df_end, df);
                    break;
                }
                case 6: {
                    paintMiniTrianglePers_5(g3d, texture, y_start, y_end,
                            x_start, wz_start, uz_start, vz_start,
                            x_end, wz_end, uz_end, vz_end,
                            dx_start, dwz_start, duz_start, dvz_start,
                            dx_end, dwz_end, duz_end, dvz_end,
                            dwz, duz, dvz, fogc, qz, q, a, b, c, f_start, f_end, df_start, df_end, df);
                    break;
                }
                case 7: {
                    paintPers_glass(g3d, texture, y_start, y_end,
                            x_start, wz_start, uz_start, vz_start,
                            x_end, wz_end, uz_end, vz_end,
                            dx_start, dwz_start, duz_start, dvz_start,
                            dx_end, dwz_end, duz_end, dvz_end,
                            dwz, duz, dvz, fogc, q);
                    break;
                }
                case 9: {
                    paintMiniTrianglePers_5(g3d, texture, y_start, y_end,
                            x_start, wz_start, uz_start, vz_start,
                            x_end, wz_end, uz_end, vz_end,
                            dx_start, dwz_start, duz_start, dvz_start,
                            dx_end, dwz_end, duz_end, dvz_end,
                            dwz, duz, dvz, fogc, qz, q, a, b, c, f_start, f_end, df_start, df_end, df);
                    break;
                }
                case 13: {
                    if (colorLighting) {
                        paintMiniTrianglePers_13(g3d, texture, y_start, y_end,
                                x_start, wz_start, uz_start, vz_start,
                                x_end, wz_end, uz_end, vz_end,
                                dx_start, dwz_start, duz_start, dvz_start,
                                dx_end, dwz_end, duz_end, dvz_end,
                                dwz, duz, dvz, fogc, qz, q, a, b, c, f_start, f_end, df_start, df_end, df,
                                g_start, g_end, dg_start, dg_end, dg,
                                b_start, b_end, db_start, db_end, db);
                    } else {
                        paintMiniTrianglePers_5(g3d, texture, y_start, y_end,
                                x_start, wz_start, uz_start, vz_start,
                                x_end, wz_end, uz_end, vz_end,
                                dx_start, dwz_start, duz_start, dvz_start,
                                dx_end, dwz_end, duz_end, dvz_end,
                                dwz, duz, dvz, fogc, qz, q, a, b, c, f_start, f_end, df_start, df_end, df);
                    }

                    break;
                }

        }
    }

    

    static final void paintMiniTrianglePers_0(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            final int dx_start,final int dwz_start,final int duz_start,final int dvz_start,
            final int dx_end, final int dwz_end, final int duz_end, final int dvz_end,
            final int dwz, final int duz, final int dvz,int fogc,int sz2) {

        if(sz2>999) {
            TexturingFloors.paintFloor(g3d, texture, y_start, y_end,
                    x_start, wz_start, uz_start, vz_start,
                    x_end, wz_end, uz_end, vz_end,
                    dx_start, dwz_start, duz_start, dvz_start,
                    dx_end, dwz_end, duz_end, dvz_end,
                    dwz, duz, dvz,fogc,sz2);
            return;
        }
            
        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������

        int tempI;
        long subtexel_a, subtexel_b;
        int uz_a, uz_b, vz_a, vz_b, wz_a, wz_b; // ���� � ����� �������
        int x1, x2, length;
        int u, v, du, dv;
        final int duz4=duz*sz2;
        final int dvz4=dvz*sz2;
        final int dwz4=dwz*sz2;
        //int uz_2, vz_2, wz_2;
        //int u_2, v_2; //����� �����
        int col;
        int lineEnd;
        int sz21 = sz2+1;
        int du2, dv2;

        for (int y = y_start; y < y_end;x_start += dx_start,
            wz_start += dwz_start,
            uz_start += duz_start,
            vz_start += dvz_start,

            x_end += dx_end,
            wz_end += dwz_end,
            uz_end += duz_end,
            vz_end += dvz_end,
            y++
            ) {
            
            x1 = x_start; x2 = x_end;
            uz_a = uz_start; uz_b = uz_end;
            vz_a = vz_start; vz_b = vz_end;
            wz_a = wz_start; wz_b = wz_end;

            subtexel_a = x1%FP;
            subtexel_b = x2%FP;

            uz_a -= (duz * subtexel_a) >> fp;//
            vz_a -= (dvz * subtexel_a) >> fp;// ������������� ��������
            wz_a -= (dwz * subtexel_a) >> fp;//

            uz_b -= (duz * subtexel_b) >> fp;//
            vz_b -= (dvz * subtexel_b) >> fp;// ������������� ��������
            wz_b -= (dwz * subtexel_b) >> fp;//

            x1 >>=fp;
            x2 >>=fp;

            if(x1<0) {
                uz_a -= duz*x1;
                vz_a -= dvz*x1;
                wz_a -= dwz*x1;
                x1 = 0;
            }
            if(x2>g3d.width) {
                x2 -= g3d.width;
                uz_b -= duz*x2;
                vz_b -= dvz*x2;
                wz_b -= dwz*x2;
                x2 = g3d.width;
            }
            if(wz_a == 0 || wz_b == 0) return;
            tempI = y*g3d.width;
            x1 += tempI;
            x2 += tempI;
            length = x2 - x1;


            
            u = (int) (((long) uz_a << fp) / wz_a);
            v = (int) (((long) vz_a << fp) / wz_a);

            
            while(length>0) {
                if(length >= sz2) {
                    lineEnd = x1 + sz2;
                    du = (((int) (((long) (uz_a += duz4) << fp) / (wz_a += dwz4)) - u)/sz21);
                    dv = (((int) (((long) (vz_a += dvz4) << fp) / wz_a) - v)/sz21);
                    length -= sz2;
                } else {
                    lineEnd = x2;
                    du = ((int) (((long) uz_b << fp) / wz_b) - u) / length;
                    dv = ((int) (((long) vz_b << fp) / wz_b) - v) / length;
                    length = 0;
                }
                
                du2 = du<<1; dv2 = dv<<1;
                
                while(lineEnd-x1>=6)
                {
                if((col=tex[((((v+dv)>>>fp)<<widthBIT) + ((u+du)>>>fp)) &lengthBIT]) != 0) rgb[x1]=col; 
                if((col=tex[((((v+dv2)>>>fp)<<widthBIT) + ((u+du2)>>>fp)) &lengthBIT]) != 0) rgb[x1+1]=col; 
                u+=du2; v+=dv2;
                if((col=tex[((((v+dv)>>>fp)<<widthBIT) + ((u+du)>>>fp)) &lengthBIT]) != 0) rgb[x1+2]=col; 
                if((col=tex[((((v+dv2)>>>fp)<<widthBIT) + ((u+du2)>>>fp)) &lengthBIT]) != 0) rgb[x1+3]=col; 
                u+=du2; v+=dv2;
                if((col=tex[((((v+dv)>>>fp)<<widthBIT) + ((u+du)>>>fp)) &lengthBIT]) != 0) rgb[x1+4]=col; 
                if((col=tex[((((v+dv2)>>>fp)<<widthBIT) + ((u+du2)>>>fp)) &lengthBIT]) != 0) rgb[x1+5]=col; 
                u+=du2; v+=dv2;
                x1+=6;
                }
                
                while(x1<lineEnd)
                {
                if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) rgb[x1]=col; 
                x1++;
                }
                
            }

        }
    }


static final void paintMiniTrianglePers_1(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            final int dx_start, final int dwz_start, final int duz_start, final int dvz_start,
            final int dx_end, final int dwz_end, final int duz_end, final int dvz_end,
            final int dwz, final int duz, final int dvz,int fogc,int sz2,int q) {
    if(Main.fogQ==0) {
        paintMiniTrianglePers_0(g3d, texture, y_start, y_end,
                    x_start, wz_start, uz_start, vz_start,
                    x_end, wz_end, uz_end, vz_end,
                    dx_start, dwz_start, duz_start, dvz_start,
                    dx_end, dwz_end, duz_end, dvz_end,
                    dwz, duz, dvz, fogc,q);
        return;
    }
    if(sz2>999) {
            TexturingFloors.paintFloor_1(g3d, texture, y_start, y_end,
                    x_start, wz_start, uz_start, vz_start,
                    x_end, wz_end, uz_end, vz_end,
                    dx_start, dwz_start, duz_start, dvz_start,
                    dx_end, dwz_end, duz_end, dvz_end,
                    dwz, duz, dvz,sz2);
            return;
        }
        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������

        int tempI;
        long subtexel_a, subtexel_b;
        int uz_a, uz_b, vz_a, vz_b, wz_a, wz_b; // ���� � ����� �������
        int x1, x2, length;
        int u, v, du, dv;
        int uz_2, vz_2, wz_2, u_2, v_2; //����� �����
        final int duz4=duz*q;
        final int dvz4=dvz*q;
        final int dwz4=dwz*q;
int col,ca;
int colf =sz2&0xFEFEFE;
        for (int y = y_start; y < y_end;x_start += dx_start,
            wz_start += dwz_start,
            uz_start += duz_start,
            vz_start += dvz_start,

            x_end += dx_end,
            wz_end += dwz_end,
            uz_end += duz_end,
            vz_end += dvz_end,
y++
) {
            x1 = x_start; x2 = x_end;
            uz_a = uz_start; uz_b = uz_end;
            vz_a = vz_start; vz_b = vz_end;
            wz_a = wz_start; wz_b = wz_end;

            subtexel_a = x1%FP;
            subtexel_b = x2%FP;

            uz_a -= (duz * subtexel_a) >> fp;//
            vz_a -= (dvz * subtexel_a) >> fp;// ������������� ��������
            wz_a -= (dwz * subtexel_a) >> fp;//

            uz_b -= (duz * subtexel_b) >> fp;//
            vz_b -= (dvz * subtexel_b) >> fp;// ������������� ��������
            wz_b -= (dwz * subtexel_b) >> fp;//

            x1 >>=fp;
            x2 >>=fp;

            if(x1<0) {
                uz_a -= duz*x1;
                vz_a -= dvz*x1;
                wz_a -= dwz*x1;
                x1 = 0;
            }
            if(x2>g3d.width) {
                x2 -= g3d.width;
                uz_b -= duz*x2;
                vz_b -= dvz*x2;
                wz_b -= dwz*x2;
                x2 = g3d.width;
            }

            tempI = y*g3d.width;
            x1 += tempI;
            x2 += tempI;
            length = x2 - x1;


            if(wz_a == 0) return;
            u = (int) (((long) uz_a << fp) / wz_a); //������� ���������� � ��������
            v = (int) (((long) vz_a << fp) / wz_a); //



//Paste SUb fog here
for(;length>=q; length-=q) {
                du = (((int) (((long) (uz_a += duz4) << fp) / (wz_a += dwz4)) - u)/q); //��������� du � dv �� ����
                dv = (((int) (((long) (vz_a += dvz4) << fp) / wz_a) - v)/q);


                    for(int i=x1+q;x1<i;)
{
if(Main.s60Optimization<1) {
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1]=(col=(col&0xFEFEFE)+colf)|((( col>>>8 ) &0x010101)*0xFF);
x1+=1;
}
if(Main.s60Optimization==1) {
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1]=(col=(col&0xFEFEFE)+colf)|((( col>>>8 ) &0x010101)*0xFF);
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1+1]=(col=(col&0xFEFEFE)+colf)|((( col>>>8 ) &0x010101)*0xFF);
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1+2]=(col=(col&0xFEFEFE)+colf)|((( col>>>8 ) &0x010101)*0xFF);
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1+3]=(col=(col&0xFEFEFE)+colf)|((( col>>>8 ) &0x010101)*0xFF);
x1+=4;
}
}

                           }
            if(length > 0) { // ������������ ���������� �������
                du = ((int) (((long) uz_b << fp) / wz_b) - u) / length;
                dv = ((int) (((long) vz_b << fp) / wz_b) - v) / length;
if(Main.s60Optimization==1) {
                                while(x2-x1>=4) {
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1]=(col=(col&0xFEFEFE)+colf)|((( col>>>8 ) &0x010101)*0xFF);
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1+1]=(col=(col&0xFEFEFE)+colf)|((( col>>>8 ) &0x010101)*0xFF);
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1+2]=(col=(col&0xFEFEFE)+colf)|((( col>>>8 ) &0x010101)*0xFF);
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1+3]=(col=(col&0xFEFEFE)+colf)|((( col>>>8 ) &0x010101)*0xFF);
x1+=4;
                }
}
while(x1<x2) {
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1]=(col=(col&0xFEFEFE)+colf)|((( col>>>8 ) &0x010101)*0xFF);
x1++;
}



            }






 //Paste here
        }
    }

static final void paintMiniTrianglePers_2(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            final int dx_start, final int dwz_start, final int duz_start, final int dvz_start,
            final int dx_end, final int dwz_end, final int duz_end, final int dvz_end,
            final int dwz, final int duz, final int dvz,int fogc,int sz2) {
if(sz2>999) {
            TexturingFloors.paintFloor_2(g3d, texture, y_start, y_end,
                    x_start, wz_start, uz_start, vz_start,
                    x_end, wz_end, uz_end, vz_end,
                    dx_start, dwz_start, duz_start, dvz_start,
                    dx_end, dwz_end, duz_end, dvz_end,
                    dwz, duz, dvz);
            return;
        }
        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������

        int tempI;
        long subtexel_a, subtexel_b;
        int uz_a, uz_b, vz_a, vz_b, wz_a, wz_b; // ���� � ����� �������
        int x1, x2, length;
        int u, v, du, dv;
        int uz_2, vz_2, wz_2, u_2, v_2; //����� �����
        final int duz4=duz*sz2;
        final int dvz4=dvz*sz2;
        final int dwz4=dwz*sz2;
int col,ca;
        for (int y = y_start; y < y_end;x_start += dx_start,
            wz_start += dwz_start,
            uz_start += duz_start,
            vz_start += dvz_start,

            x_end += dx_end,
            wz_end += dwz_end,
            uz_end += duz_end,
            vz_end += dvz_end,
y++
) {
            x1 = x_start; x2 = x_end;
            uz_a = uz_start; uz_b = uz_end;
            vz_a = vz_start; vz_b = vz_end;
            wz_a = wz_start; wz_b = wz_end;

            subtexel_a = x1%FP;
            subtexel_b = x2%FP;

            uz_a -= (duz * subtexel_a) >> fp;//
            vz_a -= (dvz * subtexel_a) >> fp;// ������������� ��������
            wz_a -= (dwz * subtexel_a) >> fp;//

            uz_b -= (duz * subtexel_b) >> fp;//
            vz_b -= (dvz * subtexel_b) >> fp;// ������������� ��������
            wz_b -= (dwz * subtexel_b) >> fp;//

            x1 >>=fp;
            x2 >>=fp;

            if(x1<0) {
                uz_a -= duz*x1;
                vz_a -= dvz*x1;
                wz_a -= dwz*x1;
                x1 = 0;
            }
            if(x2>g3d.width) {
                x2 -= g3d.width;
                uz_b -= duz*x2;
                vz_b -= dvz*x2;
                wz_b -= dwz*x2;
                x2 = g3d.width;
            }

            tempI = y*g3d.width;
            x1 += tempI;
            x2 += tempI;
            length = x2 - x1;


            if(wz_a == 0) return;
            u = (int) (((long) uz_a << fp) / wz_a); //������� ���������� � ��������
            v = (int) (((long) vz_a << fp) / wz_a); //



//Paste SUb fog here
for(;length>=sz2; length-=sz2) {
                du = (((int) (((long) (uz_a += duz4) << fp) / (wz_a += dwz4)) - u)/sz2); //��������� du � dv �� ����
                dv = (((int) (((long) (vz_a += dvz4) << fp) / wz_a) - v)/sz2);


                    for(int i=x1+sz2;x1<i;)
{
if(Main.s60Optimization<1) { 
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1]=(col=(col&0xFEFEFE)+(rgb[x1]&0xFEFEFE))|((( col>>>8 ) &0x010101)*0xFF);
x1++;
}
if(Main.s60Optimization==1) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1]=(col=(col&0xFEFEFE)+(rgb[x1]&0xFEFEFE))|((( col>>>8 ) &0x010101)*0xFF);
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1+1]=(col=(col&0xFEFEFE)+(rgb[x1+1]&0xFEFEFE))|((( col>>>8 ) &0x010101)*0xFF);
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1+2]=(col=(col&0xFEFEFE)+(rgb[x1+2]&0xFEFEFE))|((( col>>>8 ) &0x010101)*0xFF);
x1+=3;
}


}

                           }
            if(length > 0) { // ������������ ���������� �������
                du = ((int) (((long) uz_b << fp) / wz_b) - u) / length;
                dv = ((int) (((long) vz_b << fp) / wz_b) - v) / length;
                if(Main.s60Optimization==1) {
while(x2-x1>=3) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1]=(col=(col&0xFEFEFE)+(rgb[x1]&0xFEFEFE))|((( col>>>8 ) &0x010101)*0xFF);
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1+1]=(col=(col&0xFEFEFE)+(rgb[x1+1]&0xFEFEFE))|((( col>>>8 ) &0x010101)*0xFF);
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1+2]=(col=(col&0xFEFEFE)+(rgb[x1+2]&0xFEFEFE))|((( col>>>8 ) &0x010101)*0xFF);
x1+=3;
}
                }
                while(x1<x2) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1]=(col=(col&0xFEFEFE)+(rgb[x1]&0xFEFEFE))|((( col>>>8 ) &0x010101)*0xFF);
x1++;
                }



            }






 //Paste here
        }
    }





static final void paintMiniTrianglePers_3(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            final int dx_start, final int dwz_start, final int duz_start, final int dvz_start,
            final int dx_end, final int dwz_end, final int duz_end, final int dvz_end,
            final int dwz, final int duz, final int dvz,int fogc,int sz2,int q,Vertex a,Vertex b,Vertex c,
            int f_start,int f_end,
final int df_start,final int df_end,
final int df
) {

    if(Main.fogQ==2 && Main.forceLQFog==false) {
    paintMiniTrianglePers_3_HQ(g3d, texture, y_start, y_end,
                    x_start, wz_start, uz_start, vz_start,
                    x_end, wz_end, uz_end, vz_end,
                    dx_start, dwz_start, duz_start, dvz_start,
                    dx_end, dwz_end, duz_end, dvz_end,
                    dwz, duz, dvz,fogc, sz2, q,a,b,c,f_start,f_end,df_start,df_end,df); return;
    }
    if(Main.fogQ==0) {
        paintMiniTrianglePers_0(g3d, texture, y_start, y_end,
                    x_start, wz_start, uz_start, vz_start,
                    x_end, wz_end, uz_end, vz_end,
                    dx_start, dwz_start, duz_start, dvz_start,
                    dx_end, dwz_end, duz_end, dvz_end,
                    dwz, duz, dvz, fogc,q);
        return;
    }
    if(q>999) {
        TexturingFloors.paintFloor_3(g3d, texture, y_start, y_end,
                    x_start, wz_start, uz_start, vz_start,
                    x_end, wz_end, uz_end, vz_end,
                    dx_start, dwz_start, duz_start, dvz_start,
                    dx_end, dwz_end, duz_end, dvz_end,
                    dwz, duz, dvz,fogc, sz2,a,b,c,f_start,f_end,df_start,df_end,df); return;
    }
    
        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������

        int tempI;
        long subtexel_a, subtexel_b;
        int uz_a, uz_b, vz_a, vz_b, wz_a, wz_b; // ���� � ����� �������
        int x1, x2, length;
        int u, v, du, dv;
        int uz_2, vz_2, wz_2, u_2, v_2; //����� �����
        final int duz4=duz*q;
        final int dvz4=dvz*q;
        final int dwz4=dwz*q;
int col,ca;
if(DirectX7.fDist/255!=0) {
sz2=-min(a.rz,b.rz,c.rz)/(DirectX7.fDist/255);
}
if(sz2>255) sz2=255;
if(sz2<0) sz2=0;
int isz2=(255-sz2);
int f2= (fogc&0xFF00FF)*sz2;
int f22=(fogc&0x00FF00)*sz2;
        for (int y = y_start; y < y_end;x_start += dx_start,
            wz_start += dwz_start,
            uz_start += duz_start,
            vz_start += dvz_start,

            x_end += dx_end,
            wz_end += dwz_end,
            uz_end += duz_end,
            vz_end += dvz_end,
y++
) {
            x1 = x_start; x2 = x_end;
            uz_a = uz_start; uz_b = uz_end;
            vz_a = vz_start; vz_b = vz_end;
            wz_a = wz_start; wz_b = wz_end;

            subtexel_a = x1%FP;
            subtexel_b = x2%FP;

            uz_a -= (duz * subtexel_a) >> fp;//
            vz_a -= (dvz * subtexel_a) >> fp;// ������������� ��������
            wz_a -= (dwz * subtexel_a) >> fp;//

            uz_b -= (duz * subtexel_b) >> fp;//
            vz_b -= (dvz * subtexel_b) >> fp;// ������������� ��������
            wz_b -= (dwz * subtexel_b) >> fp;//

            x1 >>=fp;
            x2 >>=fp;

            if(x1<0) {
                uz_a -= duz*x1;
                vz_a -= dvz*x1;
                wz_a -= dwz*x1;
                x1 = 0;
            }
            if(x2>g3d.width) {
                x2 -= g3d.width;
                uz_b -= duz*x2;
                vz_b -= dvz*x2;
                wz_b -= dwz*x2;
                x2 = g3d.width;
            }

            tempI = y*g3d.width;
            x1 += tempI;
            x2 += tempI;
            length = x2 - x1;


            if(wz_a == 0) return;
            u = (int) (((long) uz_a << fp) / wz_a); //������� ���������� � ��������
            v = (int) (((long) vz_a << fp) / wz_a); //



//Paste SUb fog here
for(;length>=q; length-=q) {

                du = (((int) (((long) (uz_a += duz4) << fp) / (wz_a += dwz4)) - u)/q); //��������� du � dv �� ����
                dv = (((int) (((long) (vz_a += dvz4) << fp) / wz_a) - v)/q);


                    for(int i=x1+q;x1<i;)
{
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0)  
    rgb[x1]=  (   
            (  ( (col&0xFF00FF)*isz2 + f2)  &0xFF00FF00  ) | 
            (  ( (col&0x00FF00)*isz2 +f22)  &0x00FF0000  )   
            ) >>>8;
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0)  
    rgb[x1+1]=  (   
            (  ( (col&0xFF00FF)*isz2 + f2)  &0xFF00FF00  ) | 
            (  ( (col&0x00FF00)*isz2 +f22)  &0x00FF0000  )   
            ) >>>8;
x1+=2;
}
                           }
            if(length > 0) { // ������������ ���������� �������
                du = ((int) (((long) uz_b << fp) / wz_b) - u) / length;
                dv = ((int) (((long) vz_b << fp) / wz_b) - v) / length;

while(x1<x2) {
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0)  
    rgb[x1]=  (   
            (  ( (col&0xFF00FF)*isz2 + f2)  &0xFF00FF00  ) | 
            (  ( (col&0x00FF00)*isz2 +f22)  &0x00FF0000  )   
            ) >>>8;
x1++;
}



            }






 //Paste here
        }
    }

static final void paintMiniTrianglePers_3_HQ(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            final int dx_start, final int dwz_start, final int duz_start, final int dvz_start,
            final int dx_end, final int dwz_end, final int duz_end, final int dvz_end,
            final int dwz, final int duz, final int dvz,int fogc,int sz2,int q,Vertex a,Vertex b,Vertex c,
            int f_start,int f_end,
final int df_start,final int df_end,
final int df
) {

if(q>999) {
        TexturingFloors.paintFloor_3_HQ(g3d, texture, y_start, y_end,
                    x_start, wz_start, uz_start, vz_start,
                    x_end, wz_end, uz_end, vz_end,
                    dx_start, dwz_start, duz_start, dvz_start,
                    dx_end, dwz_end, duz_end, dvz_end,
                    dwz, duz, dvz,a,b,c,f_start,f_end,df_start,df_end,df); return;
    }
    
        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������

        int tempI;
        long subtexel_a, subtexel_b;
        int uz_a, uz_b, vz_a, vz_b, wz_a, wz_b; // ���� � ����� �������
        int x1, x2, length;
        int u, v, du, dv;
        int uz_2, vz_2, wz_2, u_2, v_2; //����� �����
        final int duz4=duz*q;
        final int dvz4=dvz*q;
        final int dwz4=dwz*q;
int col,f,tf,tf2;
int fogcolor=DirectX7.fogc;
int fogcolor2=fogcolor&0x00FF00;
fogcolor=fogcolor&0xFF00FF;

        for (int y = y_start; y < y_end;x_start += dx_start,
            wz_start += dwz_start,
            uz_start += duz_start,
            vz_start += dvz_start,
            f_start+=df_start,

            x_end += dx_end,
            wz_end += dwz_end,
            uz_end += duz_end,
            vz_end += dvz_end,
            f_end+=df_end,
y++
) {
            x1 = x_start; x2 = x_end;
            uz_a = uz_start; uz_b = uz_end;
            vz_a = vz_start; vz_b = vz_end;
            wz_a = wz_start; wz_b = wz_end;
            f=f_start;

            subtexel_a = x1%FP;
            subtexel_b = x2%FP;

            uz_a -= (duz * subtexel_a) >> fp;//
            vz_a -= (dvz * subtexel_a) >> fp;// ������������� ��������
            wz_a -= (dwz * subtexel_a) >> fp;//

            uz_b -= (duz * subtexel_b) >> fp;//
            vz_b -= (dvz * subtexel_b) >> fp;// ������������� ��������
            wz_b -= (dwz * subtexel_b) >> fp;//

            x1 >>=fp;
            x2 >>=fp;

            if(x1<0) {
                uz_a -= duz*x1;
                vz_a -= dvz*x1;
                wz_a -= dwz*x1;
                f -= df*x1;
                x1 = 0;
            }
            if(x2>g3d.width) {
                x2 -= g3d.width;
                uz_b -= duz*x2;
                vz_b -= dvz*x2;
                wz_b -= dwz*x2;
                x2 = g3d.width;
            }

            tempI = y*g3d.width;
            x1 += tempI;
            x2 += tempI;
            length = x2 - x1;


            if(wz_a == 0) return;
            u = (int) (((long) uz_a << fp) / wz_a); //������� ���������� � ��������
            v = (int) (((long) vz_a << fp) / wz_a); //



//Paste SUb fog here
for(;length>=q; length-=q) {
                du = (((int) (((long) (uz_a += duz4) << fp) / (wz_a += dwz4)) - u)/q); //��������� du � dv �� ����
                dv = (((int) (((long) (vz_a += dvz4) << fp) / wz_a) - v)/q);

int i=x1+q;
while(i-x1>=2) {
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1]=(   
            (  ( fogcolor*(tf2=0xFF-(tf=(f+=df)>>>fp)) + (col&0xFF00FF)*tf)  &0xFF00FF00  )| 
            (  ( fogcolor2*tf2                         + (col&0x00FF00)*tf)  &0x00FF0000  )   
            ) >>>8;
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1+1]=(   
            (  ( fogcolor*(tf2=0xFF-(tf=(f+=df)>>>fp)) + (col&0xFF00FF)*tf)  &0xFF00FF00  )| 
            (  ( fogcolor2*tf2                         + (col&0x00FF00)*tf)  &0x00FF0000  )   
            ) >>>8;
x1+=2;
}

                           }
            if(length > 0) { // ������������ ���������� �������
                du = ((int) (((long) uz_b << fp) / wz_b) - u) / length;
                dv = ((int) (((long) vz_b << fp) / wz_b) - v) / length;

while(x1<x2)
{
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1]=(   
            (  ( fogcolor*(tf2=0xFF-(tf=(f+=df)>>>fp)) + (col&0xFF00FF)*tf)  &0xFF00FF00  )| 
            (  ( fogcolor2*tf2                         + (col&0x00FF00)*tf)  &0x00FF0000  )   
            ) >>>8;
x1++;
}



            }






 //Paste here
        }
    }

static final void paintMiniTrianglePers_4(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            final int dx_start, final int dwz_start, final int duz_start, final int dvz_start,
            final int dx_end, final int dwz_end, final int duz_end, final int dvz_end,
            final int dwz, final int duz, final int dvz,int fogc,int sz2) {

if(sz2>999) {
        TexturingFloors.paintFloor_4(g3d, texture, y_start, y_end,
                    x_start, wz_start, uz_start, vz_start,
                    x_end, wz_end, uz_end, vz_end,
                    dx_start, dwz_start, duz_start, dvz_start,
                    dx_end, dwz_end, duz_end, dvz_end,
                    dwz, duz, dvz); return;
    }
        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������

        int tempI;
        long subtexel_a, subtexel_b;
        int uz_a, uz_b, vz_a, vz_b, wz_a, wz_b; // ���� � ����� �������
        int x1, x2, length;
        int u, v, du, dv;
        int uz_2, vz_2, wz_2, u_2, v_2; //����� �����
        final int duz4=duz*sz2;
        final int dvz4=dvz*sz2;
        final int dwz4=dwz*sz2;
int col,ca,cols;
        for (int y = y_start; y < y_end;x_start += dx_start,
            wz_start += dwz_start,
            uz_start += duz_start,
            vz_start += dvz_start,

            x_end += dx_end,
            wz_end += dwz_end,
            uz_end += duz_end,
            vz_end += dvz_end,
y++
) {
            x1 = x_start; x2 = x_end;
            uz_a = uz_start; uz_b = uz_end;
            vz_a = vz_start; vz_b = vz_end;
            wz_a = wz_start; wz_b = wz_end;

            subtexel_a = x1%FP;
            subtexel_b = x2%FP;

            uz_a -= (duz * subtexel_a) >> fp;//
            vz_a -= (dvz * subtexel_a) >> fp;// ������������� ��������
            wz_a -= (dwz * subtexel_a) >> fp;//

            uz_b -= (duz * subtexel_b) >> fp;//
            vz_b -= (dvz * subtexel_b) >> fp;// ������������� ��������
            wz_b -= (dwz * subtexel_b) >> fp;//

            x1 >>=fp;
            x2 >>=fp;

            if(x1<0) {
                uz_a -= duz*x1;
                vz_a -= dvz*x1;
                wz_a -= dwz*x1;
                x1 = 0;
            }
            if(x2>g3d.width) {
                x2 -= g3d.width;
                uz_b -= duz*x2;
                vz_b -= dvz*x2;
                wz_b -= dwz*x2;
                x2 = g3d.width;
            }

            tempI = y*g3d.width;
            x1 += tempI;
            x2 += tempI;
            length = x2 - x1;


            if(wz_a == 0) return;
            u = (int) (((long) uz_a << fp) / wz_a); //������� ���������� � ��������
            v = (int) (((long) vz_a << fp) / wz_a); //



//Paste SUb fog here
for(;length>=sz2; length-=sz2) {

                du = (((int) (((long) (uz_a += duz4) << fp) / (wz_a += dwz4)) - u)/sz2); //��������� du � dv �� ����
                dv = (((int) (((long) (vz_a += dvz4) << fp) / wz_a) - v)/sz2);


                    for(int i=x1+sz2;x1<i;)
{
if(Main.s60Optimization<0) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) {
rgb[x1]= (   
        (  ( (col&0xFF00FF)*(ca=col >>> 24) + ( (cols=rgb[x1])&0xFF00FF)*(0xff-ca))  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*ca + ( cols&0x00FF00)*(0xff-ca))  &0x00FF0000  )   
        ) >>>8;
}
x1++;
}
if(Main.s60Optimization>=0) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) {
rgb[x1]= (   
        (  ( (col&0xFF00FF)*(ca=col >>> 24) + ( (cols=rgb[x1])&0xFF00FF)*(0xff-ca))  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*ca + ( cols&0x00FF00)*(0xff-ca))  &0x00FF0000  )   
        ) >>>8;
}
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) {
rgb[x1+1]= (   
        (  ( (col&0xFF00FF)*(ca=col >>> 24) + ( (cols=rgb[x1+1])&0xFF00FF)*(0xff-ca))  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*ca + ( cols&0x00FF00)*(0xff-ca))  &0x00FF0000  )   
        ) >>>8;
}
x1+=2;
}
}
                           }
            if(length > 0) { // ������������ ���������� �������
                du = ((int) (((long) uz_b << fp) / wz_b) - u) / length;
                dv = ((int) (((long) vz_b << fp) / wz_b) - v) / length;
                if(Main.s60Optimization==1) {
while(x2-x1>=2) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) {
rgb[x1]= (   
        (  ( (col&0xFF00FF)*(ca=col >>> 24) + ( (cols=rgb[x1])&0xFF00FF)*(0xff-ca))  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*ca + ( cols&0x00FF00)*(0xff-ca))  &0x00FF0000  )   
        ) >>>8;
}
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) {
rgb[x1+1]= (   
        (  ( (col&0xFF00FF)*(ca=col >>> 24) + ( (cols=rgb[x1+1])&0xFF00FF)*(0xff-ca))  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*ca + ( cols&0x00FF00)*(0xff-ca))  &0x00FF0000  )   
        ) >>>8;
}
x1+=2;
}
                }

                for(; x1<x2;) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) {
rgb[x1]= (   
        (  ( (col&0xFF00FF)*(ca=col >>> 24) + ( (cols=rgb[x1])&0xFF00FF)*(0xff-ca))  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*ca + ( cols&0x00FF00)*(0xff-ca))  &0x00FF0000  )   
        ) >>>8;
}
x1++;
                }



            }






 //Paste here
        }
    }




static final void paintMiniTrianglePers_5(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            final int dx_start, final int dwz_start, final int duz_start, final int dvz_start,
            final int dx_end, final int dwz_end, final int duz_end, final int dvz_end,
            final int dwz, final int duz, final int dvz,int fogc,int sz2,int q,Vertex a,Vertex b,Vertex c,
            int f_start,int f_end,
final int df_start,final int df_end,
final int df) {
    if( ((texture.drawmode==9 || texture.drawmode==13) && Main.fogQ>=1) || (Main.fogQ==2 && Main.forceLQFog==false && texture.drawmode==6)) {
    paintMiniTrianglePers_6_HQ(g3d, texture, y_start, y_end,
                    x_start, wz_start, uz_start, vz_start,
                    x_end, wz_end, uz_end, vz_end,
                    dx_start, dwz_start, duz_start, dvz_start,
                    dx_end, dwz_end, duz_end, dvz_end,
                    dwz, duz, dvz,fogc, sz2, q,a,b,c,f_start,f_end,df_start,df_end,df); return; 
        
    }
    if(Main.fogQ==0) {
    paintMiniTrianglePers_0(g3d, texture, y_start, y_end,
                    x_start, wz_start, uz_start, vz_start,
                    x_end, wz_end, uz_end, vz_end,
                    dx_start, dwz_start, duz_start, dvz_start,
                    dx_end, dwz_end, duz_end, dvz_end,
                    dwz, duz, dvz, fogc,q); return; 
        
    }
        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������

        int tempI;
        long subtexel_a, subtexel_b;
        int uz_a, uz_b, vz_a, vz_b, wz_a, wz_b; // ���� � ����� �������
        int x1, x2, length;
        int u, v, du, dv;
        int uz_2, vz_2, wz_2, u_2, v_2; //����� �����
        final int duz4=duz*q;
        final int dvz4=dvz*q;
        final int dwz4=dwz*q;
int col,ca;
if(texture.getDrawMode()==6) {
if(DirectX7.fDist/255!=0) {
sz2=-min(a.rz,b.rz,c.rz)/(DirectX7.fDist/255);
}
if(sz2>255) sz2=255;
if(sz2<0) sz2=0;
}
int isz2=(255-sz2);

        for (int y = y_start; y < y_end;x_start += dx_start,
            wz_start += dwz_start,
            uz_start += duz_start,
            vz_start += dvz_start,

            x_end += dx_end,
            wz_end += dwz_end,
            uz_end += duz_end,
            vz_end += dvz_end,
y++
) {
            x1 = x_start; x2 = x_end;
            uz_a = uz_start; uz_b = uz_end;
            vz_a = vz_start; vz_b = vz_end;
            wz_a = wz_start; wz_b = wz_end;

            subtexel_a = x1%FP;
            subtexel_b = x2%FP;

            uz_a -= (duz * subtexel_a) >> fp;//
            vz_a -= (dvz * subtexel_a) >> fp;// ������������� ��������
            wz_a -= (dwz * subtexel_a) >> fp;//

            uz_b -= (duz * subtexel_b) >> fp;//
            vz_b -= (dvz * subtexel_b) >> fp;// ������������� ��������
            wz_b -= (dwz * subtexel_b) >> fp;//

            x1 >>=fp;
            x2 >>=fp;

            if(x1<0) {
                uz_a -= duz*x1;
                vz_a -= dvz*x1;
                wz_a -= dwz*x1;
                x1 = 0;
            }
            if(x2>g3d.width) {
                x2 -= g3d.width;
                uz_b -= duz*x2;
                vz_b -= dvz*x2;
                wz_b -= dwz*x2;
                x2 = g3d.width;
            }

            tempI = y*g3d.width;
            x1 += tempI;
            x2 += tempI;
            length = x2 - x1;


            if(wz_a == 0) return;
            u = (int) (((long) uz_a << fp) / wz_a); //������� ���������� � ��������
            v = (int) (((long) vz_a << fp) / wz_a); //



//Paste SUb fog here
for(;length>=q; length-=q) {

                du = (((int) (((long) (uz_a += duz4) << fp) / (wz_a += dwz4)) - u)/q); //��������� du � dv �� ����
                dv = (((int) (((long) (vz_a += dvz4) << fp) / wz_a) - v)/q);


                    for(int i=x1+q;x1<i;)
{
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT])!= 0) 
    rgb[x1]= (   
        (  ( (col&0xFF00FF)*isz2)  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*isz2)  &0x00FF0000  )   
        ) >>>8;
x1++;
}
                           }

            if(length > 0) { // ������������ ���������� �������
                du = ((int) (((long) uz_b << fp) / wz_b) - u) / length;
                dv = ((int) (((long) vz_b << fp) / wz_b) - v) / length;
if(Main.s60Optimization==1) {
while(x2-x1>=2) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT])!= 0) 
    rgb[x1]= (   
        (  ( (col&0xFF00FF)*isz2)  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*isz2)  &0x00FF0000  )   
        ) >>>8;
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT])!= 0) 
    rgb[x1+1]= (   
        (  ( (col&0xFF00FF)*isz2)  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*isz2)  &0x00FF0000  )   
        ) >>>8;
x1+=2;

}
}
                while(x1<x2) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT])!= 0) 
    rgb[x1]= (   
        (  ( (col&0xFF00FF)*isz2)  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*isz2)  &0x00FF0000  )   
        ) >>>8;
x1++;
}



            }






 //Paste here
        }
    }

static final void paintMiniTrianglePers_6_HQ(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            final int dx_start, final int dwz_start, final int duz_start, final int dvz_start,
            final int dx_end, final int dwz_end, final int duz_end, final int dvz_end,
            final int dwz, final int duz, final int dvz,int fogc,int sz2,int q,Vertex a,Vertex b,Vertex c,
            int f_start,int f_end,
final int df_start,final int df_end,
int df
) {

    if(q>999 && texture.drawmode==6) {
        TexturingFloors.paintFloor_6_HQ(g3d, texture, y_start, y_end,
                    x_start, wz_start, uz_start, vz_start,
                    x_end, wz_end, uz_end, vz_end,
                    dx_start, dwz_start, duz_start, dvz_start,
                    dx_end, dwz_end, duz_end, dvz_end,
                    dwz, duz, dvz,a,b,c,f_start,f_end,df_start,df_end,df); return;
    } else if(q>999 && texture.drawmode==9) {
        TexturingFloors.paintFloor_9_HQ(g3d, texture, y_start, y_end,
                    x_start, wz_start, uz_start, vz_start,
                    x_end, wz_end, uz_end, vz_end,
                    dx_start, dwz_start, duz_start, dvz_start,
                    dx_end, dwz_end, duz_end, dvz_end,
                    dwz, duz, dvz,a,b,c,f_start,f_end,df_start,df_end,df); return;
    } 
        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������

        int tempI;
        long subtexel_a, subtexel_b;
        int uz_a, uz_b, vz_a, vz_b, wz_a, wz_b; // ���� � ����� �������
        int x1, x2, length;
        int u, v, du, dv, df2, isz2;
        int uz_2, vz_2, wz_2, u_2, v_2; //����� �����
        final int duz4=duz*q;
        final int dvz4=dvz*q;
        final int dwz4=dwz*q;
        int col,f;
        for (int y = y_start; y < y_end;x_start += dx_start,
            wz_start += dwz_start,
            uz_start += duz_start,
            vz_start += dvz_start,
            f_start+=df_start,

            x_end += dx_end,
            wz_end += dwz_end,
            uz_end += duz_end,
            vz_end += dvz_end,
y++
) {
            x1 = x_start; x2 = x_end;
            uz_a = uz_start; uz_b = uz_end;
            vz_a = vz_start; vz_b = vz_end;
            wz_a = wz_start; wz_b = wz_end;
            f=f_start;

            subtexel_a = x1%FP;
            subtexel_b = x2%FP;

            uz_a -= (duz * subtexel_a) >> fp;//
            vz_a -= (dvz * subtexel_a) >> fp;// ������������� ��������
            wz_a -= (dwz * subtexel_a) >> fp;//

            uz_b -= (duz * subtexel_b) >> fp;//
            vz_b -= (dvz * subtexel_b) >> fp;// ������������� ��������
            wz_b -= (dwz * subtexel_b) >> fp;//

            x1 >>=fp;
            x2 >>=fp;

            if(x1<0) {
                uz_a -= duz*x1;
                vz_a -= dvz*x1;
                wz_a -= dwz*x1;
                f -= df*x1;
                x1 = 0;
            }
            if(x2>g3d.width) {
                x2 -= g3d.width;
                uz_b -= duz*x2;
                vz_b -= dvz*x2;
                wz_b -= dwz*x2;
                x2 = g3d.width;
            }

            tempI = y*g3d.width;
            x1 += tempI;
            x2 += tempI;
            length = x2 - x1;


            if(wz_a == 0) return;
            u = (int) (((long) uz_a << fp) / wz_a); //������� ���������� � ��������
            v = (int) (((long) vz_a << fp) / wz_a); //
            if(f<0) f=0;
            df2=df;
            if(f+df*length<0) df2=-f/length;
            

//Paste SUb fog here
for(;length>=q; length-=q) {
                du = (((int) (((long) (uz_a += duz4) << fp) / (wz_a += dwz4)) - u)/q); //��������� du � dv �� ����
                dv = (((int) (((long) (vz_a += dvz4) << fp) / wz_a) - v)/q);
int i=x1+q;
if(Main.s60Optimization==1) {
while(i-x1>=3)
{
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1]=(   
        (  ( (col&0xFF00FF)* (isz2=(f+=df2)>>>fp))  &0xFF00FF00  )|
        (  ( (col&0x00FF00)* isz2)  &0x00FF0000  )   
        ) >>>8;
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1+1]=(   
        (  ( (col&0xFF00FF)* (isz2=(f+=df2)>>>fp))  &0xFF00FF00  )|
        (  ( (col&0x00FF00)* isz2)  &0x00FF0000  )   
        ) >>>8;
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1+2]=(   
        (  ( (col&0xFF00FF)* (isz2=(f+=df2)>>>fp))  &0xFF00FF00  )|
        (  ( (col&0x00FF00)* isz2)  &0x00FF0000  )   
        ) >>>8;
x1+=3;
}
} else {
while(x1<i)
{
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1]=(
        (  ( (col&0xFF00FF)* (isz2=(f+=df2)>>>fp))  &0xFF00FF00  )|
        (  ( (col&0x00FF00)* isz2)  &0x00FF0000  )   
        ) >>>8;
x1++;
}
}
                           }


            if(length > 0) { // ������������ ���������� �������
                du = ((int) (((long) uz_b << fp) / wz_b) - u) / length;
                dv = ((int) (((long) vz_b << fp) / wz_b) - v) / length;
if(Main.s60Optimization==1) {
while(x2-x1>=2)
{
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1]=(   
        (  ( (col&0xFF00FF)* (isz2=(f+=df2)>>>fp))  &0xFF00FF00  )|
        (  ( (col&0x00FF00)* isz2)  &0x00FF0000  )   
        ) >>>8;
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1+1]=(   
        (  ( (col&0xFF00FF)* (isz2=(f+=df2)>>>fp))  &0xFF00FF00  )|
        (  ( (col&0x00FF00)* isz2)  &0x00FF0000  )   
        ) >>>8;
x1+=2;
}
}
while(x1<x2)
{
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1]=(   
        (  ( (col&0xFF00FF)* (isz2=(f+=df2)>>>fp))  &0xFF00FF00  )|
        (  ( (col&0x00FF00)* isz2)  &0x00FF0000  )   
        ) >>>8;
x1++;
}



            }






 //Paste here
        }
    }


static final void paintMiniTrianglePers_13(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            int dx_start, int dwz_start, int duz_start, int dvz_start,
            int dx_end, int dwz_end, int duz_end, int dvz_end,
            final int dwz, final int duz, final int dvz,int fogc,int sz2,int q,Vertex a,Vertex b,Vertex c,
            int r_start,int r_end,int dr_start,int dr_end,int dr,
            int g_start,int g_end,int dg_start,int dg_end,int dg,
            int b_start,int b_end,int db_start,int db_end,int db
) {


    if(Main.fogQ==0) {
    paintMiniTrianglePers_0(g3d, texture, y_start, y_end,
                    x_start, wz_start, uz_start, vz_start,
                    x_end, wz_end, uz_end, vz_end,
                    dx_start, dwz_start, duz_start, dvz_start,
                    dx_end, dwz_end, duz_end, dvz_end,
                    dwz, duz, dvz, fogc,q); return; 
        
    }
    if(q>999 && texture.drawmode==13) {
        TexturingFloors.paintFloor_13(g3d, texture, y_start, y_end,
                    x_start, wz_start, uz_start, vz_start,
                    x_end, wz_end, uz_end, vz_end,
                    dx_start, dwz_start, duz_start, dvz_start,
                    dx_end, dwz_end, duz_end, dvz_end,
                    dwz, duz, dvz,a,b,c,
                    r_start,r_end,dr_start,dr_end,dr,
                    g_start,g_end,dg_start,dg_end,dg,
                    b_start,b_end,db_start,db_end,db); return;
    } 
        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������

        int tempI;
        long subtexel_a, subtexel_b;
        int uz_a, uz_b, vz_a, vz_b, wz_a, wz_b; // ���� � ����� �������
        int x1, x2, length;
        int u, v, du, dv;
        final int duz4=duz*q;
        final int dvz4=dvz*q;
        final int dwz4=dwz*q;
        int col,rC,gC,bC,dr2,dg2,db2;
        int FPq = FP / q;
        int rb;

        for (int y = y_start; y < y_end;x_start += dx_start,
            wz_start += dwz_start,
            uz_start += duz_start,
            vz_start += dvz_start,
            r_start+=dr_start,
            g_start+=dg_start,
            b_start+=db_start,

            x_end += dx_end,
            wz_end += dwz_end,
            uz_end += duz_end,
            vz_end += dvz_end,
            y++
) {
            
            x1 = x_start; x2 = x_end;
            uz_a = uz_start; uz_b = uz_end;
            vz_a = vz_start; vz_b = vz_end;
            wz_a = wz_start; wz_b = wz_end;
            rC=r_start; gC=g_start; bC=b_start;

            subtexel_a = x1%FP;
            subtexel_b = x2%FP;

            uz_a -= (duz * subtexel_a) >> fp;//
            vz_a -= (dvz * subtexel_a) >> fp;// ������������� ��������
            wz_a -= (dwz * subtexel_a) >> fp;//

            uz_b -= (duz * subtexel_b) >> fp;//
            vz_b -= (dvz * subtexel_b) >> fp;// ������������� ��������
            wz_b -= (dwz * subtexel_b) >> fp;//

            x1 >>=fp;
            x2 >>=fp;

            if(x1<0) {
                uz_a -= duz*x1;
                vz_a -= dvz*x1;
                wz_a -= dwz*x1;
                rC -= dr*x1;
                gC -= dg*x1;
                bC -= db*x1;
                x1 = 0;
            }
            if(x2>g3d.width) {
                x2 -= g3d.width;
                uz_b -= duz*x2;
                vz_b -= dvz*x2;
                wz_b -= dwz*x2;
                x2 = g3d.width;
            }

            tempI = y*g3d.width;
            x1 += tempI;
            x2 += tempI;
            length = x2 - x1;
            if(rC<0) rC=0;
            if(gC<0) gC=0;
            if(bC<0) bC=0;
            dr2=dr; dg2=dg; db2=db;
            if(rC+dr*length<0) dr2=-rC/length;
            if(gC+dg*length<0) dg2=-gC/length;
            if(bC+db*length<0) db2=-bC/length;


            if(wz_a == 0) return;
            u = (int) (((long) uz_a << fp) / wz_a); 
            v = (int) (((long) vz_a << fp) / wz_a); 
            
for(;length>=q; length-=q) {
                du = (((int) (((long) (uz_a += duz4) << fp) / (wz_a += dwz4)) - u)*FPq)>>fp; 
                dv = (((int) (((long) (vz_a += dvz4) << fp) / wz_a) - v)*FPq)>>fp;
int i=x1+q;
if(Main.s60Optimization>0) {
while(i-x1>=2)
{
if((col=tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) rgb[x1]=
        (
        (  ( (col&0xFF0000)*((rC+=dr2)>>>fp))  &0xFF000000) | 
        (  ( (col&0x00FF00)*((gC+=dg2)>>>fp))  &0x00FF0000) |
        (  ( (col&0x0000FF)*((bC+=db2)>>>fp))  ) 
        ) >>>8;
if((col=tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) rgb[x1+1]=
        (
        (  ( (col&0xFF0000)*((rC+=dr2)>>>fp))  &0xFF000000) | 
        (  ( (col&0x00FF00)*((gC+=dg2)>>>fp))  &0x00FF0000) |
        (  ( (col&0x0000FF)*((bC+=db2)>>>fp))  ) 
        ) >>>8;
x1+=2;
}
} else {
while(x1<i) {
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) rgb[x1]=
        (
        (  ( (col&0xFF0000)*((rC+=dr2)>>>fp))  &0xFF000000) | 
        (  ( (col&0x00FF00)*((gC+=dg2)>>>fp))  &0x00FF0000) |
        (  ( (col&0x0000FF)*((bC+=db2)>>>fp))  ) 
        ) >>>8;
x1++;
}
}
                           }
            if(length > 0) { // ������������ ���������� �������

                du = ((int) (((long) uz_b << fp) / wz_b) - u) / length;
                dv = ((int) (((long) vz_b << fp) / wz_b) - v) / length;

while(x1<x2)
{
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) rgb[x1]=
        (
        (  ( (col&0xFF0000)*((rC+=dr2)>>>fp))  &0xFF000000) | 
        (  ( (col&0x00FF00)*((gC+=dg2)>>>fp))  &0x00FF0000) |
        (  ( (col&0x0000FF)*((bC+=db2)>>>fp))  ) 
        ) >>>8;
x1++;
}



            }


 //Paste here
        }
    }

static final void paintPers_glass(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            final int dx_start, final int dwz_start, final int duz_start, final int dvz_start,
            final int dx_end, final int dwz_end, final int duz_end, final int dvz_end,
            final int dwz, final int duz, final int dvz,int fogc,int sz2) {


        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������
        final int g3dlen = g3d.display.length-1;
        final int w = g3d.width;
        int scale=64;
        scale=scale*(g3d.getHeight()+g3d.getWidth())/(240+320);

        int tempI;
        long subtexel_a, subtexel_b;
        int uz_a, uz_b, vz_a, vz_b, wz_a, wz_b; // ���� � ����� �������
        int x1, x2, length;
        int u, v, du, dv;
        int uz_2, vz_2, wz_2, u_2, v_2; //����� �����
        final int duz4=duz*sz2;
        final int dvz4=dvz*sz2;
        final int dwz4=dwz*sz2;
        int col,coord;
        for (int y = y_start; y < y_end;x_start += dx_start,
            wz_start += dwz_start,
            uz_start += duz_start,
            vz_start += dvz_start,

            x_end += dx_end,
            wz_end += dwz_end,
            uz_end += duz_end,
            vz_end += dvz_end,
y++
) {
            if (x_start > x_end) {
                x1 = x_end; x2 = x_start;
                uz_a = uz_end; uz_b = uz_start;
                vz_a = vz_end; vz_b = vz_start;
                wz_a = wz_end; wz_b = wz_start;
            } else {
                x1 = x_start; x2 = x_end;
                uz_a = uz_start; uz_b = uz_end;
                vz_a = vz_start; vz_b = vz_end;
                wz_a = wz_start; wz_b = wz_end;
            }

            subtexel_a = x1%FP;
            subtexel_b = x2%FP;

            uz_a -= (duz * subtexel_a) >> fp;//
            vz_a -= (dvz * subtexel_a) >> fp;// ������������� ��������
            wz_a -= (dwz * subtexel_a) >> fp;//

            uz_b -= (duz * subtexel_b) >> fp;//
            vz_b -= (dvz * subtexel_b) >> fp;// ������������� ��������
            wz_b -= (dwz * subtexel_b) >> fp;//

            x1 >>=fp;
            x2 >>=fp;

            if(x1<0) {
                uz_a -= duz*x1;
                vz_a -= dvz*x1;
                wz_a -= dwz*x1;
                x1 = 0;
            }
            if(x2>g3d.width) {
                x2 -= g3d.width;
                uz_b -= duz*x2;
                vz_b -= dvz*x2;
                wz_b -= dwz*x2;
                x2 = g3d.width;
            }

            tempI = y*g3d.width;
            x1 += tempI;
            x2 += tempI;
            length = x2 - x1;


            if(wz_a == 0) return;
            u = (int) (((long) uz_a << fp) / wz_a); //������� ���������� � ��������
            v = (int) (((long) vz_a << fp) / wz_a); //



//Paste SUb fog here
for(;length>=sz2; length-=sz2) {
                du = (((int) (((long) (uz_a += duz4) << fp) / (wz_a += dwz4)) - u)/sz2); //��������� du � dv �� ����
                dv = (((int) (((long) (vz_a += dvz4) << fp) / wz_a) - v)/sz2);


                    for(int i=x1+sz2;x1<i; x1+=1)
{

col = tex[((((v += dv) >>> fp) << widthBIT) + ((u += du) >>> fp)) & lengthBIT];
            coord = ((((col & 0xff0000) - 0x800000) * scale) >> 24) + ((((col & 0xff00) - 0x8000) * scale) >> 16) * w + x1;
            if(coord < 0) rgb[x1] = rgb[0];
            else if(coord > g3dlen) rgb[x1] = rgb[g3dlen];
            else rgb[x1] = rgb[coord];
}
                           }
            if(length > 0) { // ������������ ���������� �������
                u_2 = (int) (((long) uz_b << fp) / wz_b);
                v_2 = (int) (((long) vz_b << fp) / wz_b);

                du = (u_2 - u) / length;
                dv = (v_2 - v) / length;
                for(; x1<x2;) {
col = tex[((((v += dv) >>> fp) << widthBIT) + ((u += du) >>> fp)) & lengthBIT];
            coord = ((((col & 0xff0000) - 0x800000) * scale) >> 24) + ((((col & 0xff00) - 0x8000) * scale) >> 16) * w + x1;
            if(coord < 0) rgb[x1] = rgb[0];
            else if(coord > g3dlen) rgb[x1] = rgb[g3dlen];
            else rgb[x1] = rgb[coord];
x1++;
                }



            }






 //Paste here
        }
    }



   static final int min(int a,int b,int c) {
   if(a==b || b==c) return min(a,c);
   if(a==c) return min(a,b);
   if(a<b && a<c) return a;
   if(b<a && b<c) return b;
   return c;
   }

   private static final int min(int a,int b) {
   if(a<b) return a;
   return b;
   }
   
   private static final int max(int a,int b) {
   if(a>b) return a;
   return b;
   }
   
   
   /*Код умножения с овербрайтом
   col=
        (
        (  ( (col&0xFF0000)*((rC+=dr2)>>>fp))  &0xFE000000) | 
        (  ( (col&0x00FF00)*((gC+=dg2)>>>fp))  &0x00FE0000) |
        (  ( (col&0x0000FF)*((bC+=db2)>>>fp))  &0x0000FE00) 
        ) >>>7;
     rgb[x1]=(col|(col>>>8&0x030303)*0xFF); // Лимитер
   
   */
}
