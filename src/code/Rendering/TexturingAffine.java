package code.Rendering;

import code.Gameplay.Map.LightMapper;
import code.HUD.DeveloperMenu;
import code.Math.MathUtils;
import code.utils.Main;

/**
 *
 * @author Roman Lahin
 */
public class TexturingAffine {
    public final static int fp = 12, FP = 1<<fp;
    public final static int fpPosition = 14, FPPosition = 1<<fpPosition;
    
    public static final void paint(DirectX7 g3d, Texture texture,
            Vertex a, int au, int av,
            Vertex b, int bu, int bv,
            Vertex c, int cu, int cv,int fogc,int fog,int sz2,
            int af,int bf,int cf,
            int ag,int bg,int cg,
            int ab,int bb,int cb,
            int nx,int ny,int nz) {
        if(DeveloperMenu.renderPolygonsOverwrite) {
            TexturingAffine.paintOverwrite(g3d, a, b, c);
            return;
        }
        if(texture.rImg.alphaMixing == false && texture.drawmode == 4) {
            fog = 0;
        }

        if(texture.rImg.w != 256) {
            if((texture.mip != null && texture.mip[0].w < 255) || (texture.mip == null && texture.rImg.w < 255)) {
                if(au == 255) au = 256;
                if(bu == 255) bu = 256;
                if(cu == 255) cu = 256;
            }
            au = au * texture.rImg.w / 256;
            bu = bu * texture.rImg.w / 256;
            cu = cu * texture.rImg.w / 256;
        }

        if(texture.rImg.h != 256) {
            if((texture.mip != null && texture.mip[0].h < 256) || (texture.mip == null && texture.rImg.h < 256)) {
                if(av == 255) av = 256;
                if(bv == 255) bv = 256;
                if(cv == 255) cv = 256;
            }
            av = av * texture.rImg.h / 256;
            bv = bv * texture.rImg.h / 256;
            cv = cv * texture.rImg.h / 256;
        }

switch(fog)
{

case 0:{
    paintAffine_0(g3d,texture,a,au,av,b,bu,bv,c,cu,cv);
    break;
}
case 1:{
    if(Main.fogQ>0) {paintAffine_1(g3d,texture,a,au,av,b,bu,bv,c,cu,cv,fogc,sz2);}
    else {paintAffine_0(g3d,texture,a,au,av,b,bu,bv,c,cu,cv); }
    break;
}
case 2:{
    paintAffine_2(g3d,texture,a,au,av,b,bu,bv,c,cu,cv,fogc,sz2);
    break;
}
case 3:{
    if(Main.fogQ>0) {paintAffine_3(g3d,texture,a,au,av,b,bu,bv,c,cu,cv,fogc,sz2);}
    else {paintAffine_0(g3d,texture,a,au,av,b,bu,bv,c,cu,cv); }
    break;
}
case 4:{
    paintAffine_4(g3d,texture,a,au,av,b,bu,bv,c,cu,cv,fogc,sz2);
    break;
}
case 5:{
    paintAffine_5(g3d,texture,a,au,av,b,bu,bv,c,cu,cv,fogc,sz2,af,bf,cf,nx,ny,nz);
    break;
}
case 6:{
    if(Main.fogQ==2) {
        af = 0xff + a.rz * 0xFF / DirectX7.fDist;
        if (af > 0xFF) af = 0xFF;
        if (af < 0) af = 0;
        bf = 0xff + b.rz * 0xFF / DirectX7.fDist;
        if (bf > 0xFF) bf = 0xFF;
        if (bf < 0) bf = 0;
        cf = 0xff + c.rz * 0xFF / DirectX7.fDist;
        if (cf > 0xFF) cf = 0xFF;
        if (cf < 0) cf = 0;
        paintAffine_9(g3d,texture,a,au,av,b,bu,bv,c,cu,cv,fogc,sz2,af,bf,cf,nx,ny,nz);
    }
    else if(Main.fogQ==1) {paintAffine_5(g3d,texture,a,au,av,b,bu,bv,c,cu,cv,fogc,sz2,af,bf,cf,nx,ny,nz);}
    else {paintAffine_0(g3d,texture,a,au,av,b,bu,bv,c,cu,cv); }
    break;
}
case 7:{
    paint_glass(g3d,texture,a,au,av,b,bu,bv,c,cu,cv,fogc,sz2);
    break;
}
case 8:{
    paintFill(g3d,a,b,c,texture,au,av);
    break;
}
case 9:{
    if(Main.fogQ>=1 && (af != 255 || bf != 255 || cf != 255)) {paintAffine_9(g3d,texture,a,au,av,b,bu,bv,c,cu,cv,fogc,sz2,af,bf,cf,nx,ny,nz);}
    else {paintAffine_0(g3d,texture,a,au,av,b,bu,bv,c,cu,cv); }
    break;
}
case 10:{
    paintDitherGradient(g3d,texture,a,af,b,bf,c,cf);
    break;
}
case 11:{
af=0xff+a.rz*0xFF/DirectX7.waterDistance;
if(af>0xFF) af=0xFF;
if(af<0) af=0;
bf=0xff+b.rz*0xFF/DirectX7.waterDistance;
if(bf>0xFF) bf=0xFF;
if(bf<0) bf=0;
cf=0xff+c.rz*0xFF/DirectX7.waterDistance;
if(cf>0xFF) cf=0xFF;
if(cf<0) cf=0;
paintDitherGradient(g3d,texture,a,af,b,bf,c,cf);
break;
}
case 12:{
af=0xff+a.rz*0xFF/DirectX7.fDist;
if(af>0xFF) af=0xFF;
if(af<0) af=0;
bf=0xff+b.rz*0xFF/DirectX7.fDist;
if(bf>0xFF) bf=0xFF;
if(bf<0) bf=0;
cf=0xff+c.rz*0xFF/DirectX7.fDist;
if(cf>0xFF) cf=0xFF;
if(cf<0) cf=0;
paintDitherGradient(g3d,texture,a,af,b,bf,c,cf);
break;
}
case 13:{
    if(Main.fogQ>=1) {
        if(af==ag && af==ab && bf==bg && bf==bb && cf==cb && cf==cg) {
            if(af == 255 && bf == 255 && cf == 255) 
                paintAffine_0(g3d,texture,a,au,av,b,bu,bv,c,cu,cv);
            else
                paintAffine_9(g3d,texture,a,au,av,b,bu,bv,c,cu,cv,fogc,sz2,af,bf,cf,nx,ny,nz); 
        } else if(Main.fogQ==1) paintAffine_9(g3d,texture,a,au,av,b,bu,bv,c,cu,cv,fogc,sz2,(af+ag+ab)/3,(bf+bg+bb)/3,(cf+cg+cb)/3,nx,ny,nz); 
        else paintAffine_13(g3d,texture,a,au,av,b,bu,bv,c,cu,cv,fogc,sz2,af,bf,cf,ag,bg,cg,ab,bb,cb,nx,ny,nz);
    }
    else {paintAffine_0(g3d,texture,a,au,av,b,bu,bv,c,cu,cv); }
    break;
}



}

    }
    /*
    static final void paintAffine_0(DirectX7 g3d, Texture texture,
            Vertex a, int au, int av,
            Vertex b, int bu, int bv,
            Vertex c, int cu, int cv) { //TEXCOMP



    //----------------------------
    //|          A               |
    //|        ****              |
    //|      *******             |
    //|    **********            |
    //|  B************           |
    //|     ***********          |
    //|        *********         |
    //|-----------@@@@@@@--------|
    //|              *****       |
    //|                 ***      |
    //|                    C     |
    //----------------------------

        //��������� �������
        if(b.sy < a.sy) {
            Vertex t = a; a = b; b = t;
            int tb = au; au = bu; bu = tb;
            tb = av; av = bv; bv = tb;
        }
        if(c.sy < a.sy) {
            Vertex t = c; c = a; a = t;
            int tb = cu; cu = au; au = tb;
            tb = cv; cv = av; av = tb;
        }
        if(c.sy < b.sy) {
            Vertex t = b; b = c; c = t;
            int tb = bu; bu = cu; cu = tb;
            tb = bv; bv = cv; cv = tb;
        }
        if(a.sy == c.sy) return;

        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        //final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������
        int x1, x2, u, v, tempI;
        
        int col, pos, id;
        final int lengthTEX5 = tex.length/5;
        final int widthBIT = texture.rImg.widthBIT-2;
        final int fp4 = fp+2;
        
        tempI = c.sy-a.sy;

        final int dx_start = ((c.sx - a.sx)<<fpPosition) / tempI;//
        final int du_start = ((cu - au)<<fp) / tempI; //
        final int dv_start = ((cv - av)<<fp) / tempI;//

        int dx_end=0, du_end=0, dv_end=0; // ����������
        if(b.sy != a.sy) {
            tempI = b.sy-a.sy;

            dx_end = ((b.sx - a.sx)<<fpPosition) / tempI;
            du_end = ((bu - au)<<fp) / tempI;
            dv_end = ((bv - av)<<fp) / tempI;
        }


        tempI = b.sy - a.sy;
        int x_start = (a.sx<<fpPosition) + dx_start * tempI; //������ �����
        int u_start = (au<<fp) + du_start * tempI; //
        int v_start = (av<<fp) + dv_start * tempI;//

        int x_end = b.sx<<fpPosition; //����� �����
        int u_end = bu<<fp;  //
        int v_end = bv<<fp; //

        tempI = (x_start-x_end) >> fpPosition;  // �������� ������
        if(tempI == 0) return;         //
        final int du = (u_start-u_end)/tempI; //
        final int dv = (v_start-v_end)/tempI; //
        


        x_end = x_start = a.sx<<fpPosition;
        u_end = u_start = au<<fp;
        v_end = v_start = av<<fp;


        int y_start = a.sy; //������� �� Y �� �������
        int y_end = c.sy < g3d.height ? c.sy : g3d.height; // ����� �� Y �� �������

        for(;y_start<y_end; y_start++,
                        x_start += dx_start,
                        u_start += du_start,
                        v_start += dv_start,
        
                        x_end += dx_end,
                        u_end += du_end,
                        v_end += dv_end) { //рисуем все горизонтальные линии

            if(y_start == b.sy) {
                if(c.sy == b.sy) return;
                tempI = b.sy - a.sy;
                x_start = (a.sx<<fpPosition) + dx_start * tempI; //начало линии
                u_start = (au<<fp) + du_start * tempI; //
                v_start = (av<<fp) + dv_start * tempI;//
                x_end = b.sx<<fpPosition; //
                u_end = bu<<fp; //
                v_end = bv<<fp;//конец линии
                tempI = c.sy - b.sy;                  //
                dx_end = ((c.sx - b.sx)<<fpPosition) / tempI;//
                du_end = ((cu - bu)<<fp) / tempI;   //
                dv_end = ((cv - bv)<<fp) / tempI;  //прищения для конца линии
            }

            if(y_start<0) continue;

            if (x_start > x_end) {
                x1 = x_end >> fpPosition;
                u = u_end;
                v = v_end;
                x2 = x_start >> fpPosition;
            } else {
                x1 = x_start >> fpPosition;
                u = u_start;
                v = v_start;
                x2 = x_end >> fpPosition;
            }



            if(x1 < 0) {
                u -= du*x1;
                v -= dv*x1;
                x1 = 0;
            }


            if(x2 > g3d.width) x2 = g3d.width;

            tempI = g3d.width * y_start;
            x1 += tempI; //от куда рисовать в массиве
            x2 += tempI; //до куда рисовать в массив
            u-=du;
            v-=dv;
            while (x1 < x2) {
                pos = ((((((v += dv) >>> fp4) << widthBIT) + ((u += du) >>> fp4))&0x7fffffff) % lengthTEX5)*5;
                if((col = tex[pos+((tex[pos+4] >> (( (((v>>fp)&3)<<2) + ((u>>fp)&3))<<1))&3)]) != 0) rgb[x1] = col;
                x1++;
            }

        }

    }

*/

static final void paintAffine_0(DirectX7 g3d, Texture texture,
            Vertex a, int au, int av,
            Vertex b, int bu, int bv,
            Vertex c, int cu, int cv) {



    //----------------------------
    //|          A               |
    //|        ****              |
    //|      *******             |
    //|    **********            |
    //|  B************           |
    //|     ***********          |
    //|        *********         |
    //|-----------@@@@@@@--------|
    //|              *****       |
    //|                 ***      |
    //|                    C     |
    //----------------------------

        //��������� �������
        if(b.sy < a.sy) {
            Vertex t = a; a = b; b = t;
            int tb = au; au = bu; bu = tb;
            tb = av; av = bv; bv = tb;
        }
        if(c.sy < a.sy) {
            Vertex t = c; c = a; a = t;
            int tb = cu; cu = au; au = tb;
            tb = cv; cv = av; av = tb;
        }
        if(c.sy < b.sy) {
            Vertex t = b; b = c; c = t;
            int tb = bu; bu = cu; cu = tb;
            tb = bv; bv = cv; cv = tb;
        }
        if(a.sy == c.sy) return;

        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������
        int x1, x2, u, v, tempI;
        int col;
        tempI = c.sy-a.sy;

        final int dx_start = ((c.sx - a.sx)<<fpPosition) / tempI;//
        final int du_start = ((cu - au)<<fp) / tempI; //
        final int dv_start = ((cv - av)<<fp) / tempI;//

        int dx_end=0, du_end=0, dv_end=0; // ����������
        if(b.sy != a.sy) {
            tempI = b.sy-a.sy;

            dx_end = ((b.sx - a.sx)<<fpPosition) / tempI;
            du_end = ((bu - au)<<fp) / tempI;
            dv_end = ((bv - av)<<fp) / tempI;
        }


        tempI = b.sy - a.sy;
        int x_start = (a.sx<<fpPosition) + dx_start * tempI; //������ �����
        int u_start = (au<<fp) + du_start * tempI; //
        int v_start = (av<<fp) + dv_start * tempI;//

        int x_end = b.sx<<fpPosition; //����� �����
        int u_end = bu<<fp;  //
        int v_end = bv<<fp; //

        tempI = (x_start-x_end) >> fpPosition;  // �������� ������
        if(tempI == 0) return;         //
        final int du = (u_start-u_end)/tempI; //
        final int dv = (v_start-v_end)/tempI; //
        


        x_end = x_start = a.sx<<fpPosition;
        u_end = u_start = au<<fp;
        v_end = v_start = av<<fp;


        int y_start = a.sy; //������� �� Y �� �������
        int y_end = c.sy < g3d.height ? c.sy : g3d.height; // ����� �� Y �� �������

for(;y_start<y_end; y_start++,
                x_start += dx_start,
                u_start += du_start,
                v_start += dv_start,

                x_end += dx_end,
                u_end += du_end,
                v_end += dv_end) { //рисуем все горизонтальные линии

            if(y_start == b.sy) {
                if(c.sy == b.sy) return;
                tempI = b.sy - a.sy;
                x_start = (a.sx<<fpPosition) + dx_start * tempI; //начало линии
                u_start = (au<<fp) + du_start * tempI; //
                v_start = (av<<fp) + dv_start * tempI;//
                x_end = b.sx<<fpPosition; //
                u_end = bu<<fp; //
                v_end = bv<<fp;//конец линии
                tempI = c.sy - b.sy;                  //
                dx_end = ((c.sx - b.sx)<<fpPosition) / tempI;//
                du_end = ((cu - bu)<<fp) / tempI;   //
                dv_end = ((cv - bv)<<fp) / tempI;  //прищения для конца линии
            }

            if(y_start<0) continue;

            if (x_start > x_end) {
                //tempI = x_end & FPPosition;
                x1 = x_end >> fpPosition;
                u = u_end;
                v = v_end;
                x2 = x_start >> fpPosition;
            } else {
                //tempI = x_start & FPPosition;
                x1 = x_start >> fpPosition;
                u = u_start;
                v = v_start;
                x2 = x_end >> fpPosition;
            }

            if(x1 < 0) {
                u -= du*x1;
                v -= dv*x1;
                x1 = 0;
            }


            if(x2 > g3d.width) x2 = g3d.width;

            tempI = g3d.width * y_start;
            x1 += tempI; //от куда рисовать в массиве
            x2 += tempI; //до куда рисовать в массив
            u -= du; v -= dv;
            
            line_0(x1, x2, rgb, tex, lengthBIT, widthBIT, u, v,  du, dv);
            
}

}


    public static void line_0(int x1, final int x2,
            final int[] rgb, final int[] tex, final int lengthBIT, final int widthBIT, 
            int u, int v, final int du, final int dv) {
        int col;
            
        while(x2 - x1 >= 6) {
            if((col = tex[((((v += dv) >>> fp) << widthBIT) + ((u += du) >>> fp)) & lengthBIT]) != 0) 
                rgb[x1] = col;
            if((col = tex[((((v += dv) >>> fp) << widthBIT) + ((u += du) >>> fp)) & lengthBIT]) != 0) 
                rgb[x1 + 1] = col;
            if((col = tex[((((v += dv) >>> fp) << widthBIT) + ((u += du) >>> fp)) & lengthBIT]) != 0) 
                rgb[x1 + 2] = col;
            if((col = tex[((((v += dv) >>> fp) << widthBIT) + ((u += du) >>> fp)) & lengthBIT]) != 0) 
                rgb[x1 + 3] = col;
            if((col = tex[((((v += dv) >>> fp) << widthBIT) + ((u += du) >>> fp)) & lengthBIT]) != 0) 
                rgb[x1 + 4] = col;
            if((col = tex[((((v += dv) >>> fp) << widthBIT) + ((u += du) >>> fp)) & lengthBIT]) != 0) 
                rgb[x1 + 5] = col;
            x1 += 6;
        }

        while(x1 < x2) {
            if((col = tex[((((v += dv) >>> fp) << widthBIT) + ((u += du) >>> fp)) & lengthBIT]) != 0) 
                rgb[x1] = col;
            x1++;
        }
    }



static final void paintAffine_1(DirectX7 g3d, Texture texture,
            Vertex a, int au, int av,
            Vertex b, int bu, int bv,
            Vertex c, int cu, int cv,int fogc,int sz2) {


int colf =sz2&0xFEFEFE; //set some lsb to 0
    //----------------------------
    //|          A               |
    //|        ****              |
    //|      *******             |
    //|    **********            |
    //|  B************           |
    //|     ***********          |
    //|        *********         |
    //|-----------@@@@@@@--------|
    //|              *****       |
    //|                 ***      |
    //|                    C     |
    //----------------------------

        //��������� �������
        if(b.sy < a.sy) {
            Vertex t = a; a = b; b = t;
            int tb = au; au = bu; bu = tb;
            tb = av; av = bv; bv = tb;
        }
        if(c.sy < a.sy) {
            Vertex t = c; c = a; a = t;
            int tb = cu; cu = au; au = tb;
            tb = cv; cv = av; av = tb;
        }
        if(c.sy < b.sy) {
            Vertex t = b; b = c; c = t;
            int tb = bu; bu = cu; cu = tb;
            tb = bv; bv = cv; cv = tb;
        }
        if(a.sy == c.sy) return;


        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������
        int x1, x2, u, v, tempI;
        int col,ca;

        tempI = c.sy - a.sy;                        // ����������
        final int dx_start = ((c.sx - a.sx)<<fpPosition) / tempI;//
        final int du_start = ((cu - au)<<fp) / tempI; //
        final int dv_start = ((cv - av)<<fp) / tempI;//

        int dx_end=0, du_end=0, dv_end=0; // ����������
        if(b.sy != a.sy) {
            tempI = b.sy - a.sy;
            dx_end = ((b.sx - a.sx)<<fpPosition) / tempI;
            du_end = ((bu - au)<<fp) / tempI;
            dv_end = ((bv - av)<<fp) / tempI;
        }


        tempI = b.sy - a.sy;
        int x_start = (a.sx<<fpPosition) + dx_start * tempI; //������ �����
        int u_start = (au<<fp) + du_start * tempI; //
        int v_start = (av<<fp) + dv_start * tempI;//

        int x_end = b.sx<<fpPosition; //����� �����
        int u_end = bu<<fp;  //
        int v_end = bv<<fp; //

        tempI = (x_start-x_end) >> fpPosition;  // �������� ������
        if(tempI == 0) return;         //
        final int du = (u_start-u_end)/tempI; //
        final int dv = (v_start-v_end)/tempI; //

        x_end = x_start = a.sx<<fpPosition;
        u_end = u_start = au<<fp;
        v_end = v_start = av<<fp;


        int y_start = a.sy; //������� �� Y �� �������
        int y_end = c.sy < g3d.height ? c.sy : g3d.height; // ����� �� Y �� �������




        for(;y_start<y_end; y_start++,
                x_start += dx_start,
                u_start += du_start,
                v_start += dv_start,

                x_end += dx_end,
                u_end += du_end,
                v_end += dv_end) { //������ ��� �������������� �����

            if(y_start == b.sy) {
                if(c.sy == b.sy) return;
                tempI = b.sy - a.sy;
                x_start = (a.sx<<fpPosition) + dx_start * tempI; //������ �����
                u_start = (au<<fp) + du_start * tempI; //
                v_start = (av<<fp) + dv_start * tempI;//
                x_end = b.sx<<fpPosition; //
                u_end = bu<<fp; //
                v_end = bv<<fp;//����� �����
                tempI = c.sy - b.sy;                  //
                dx_end = ((c.sx - b.sx)<<fpPosition) / tempI;//
                du_end = ((cu - bu)<<fp) / tempI;   //
                dv_end = ((cv - bv)<<fp) / tempI;  //�������� ��� ����� �����
            }

            if(y_start<0) continue;

            if (x_start > x_end) {
                x1 = x_end >> fpPosition;
                u = u_end;
                v = v_end;
                x2 = x_start >> fpPosition;
            } else {
                tempI = x_start%FP;
                x1 = x_start >> fpPosition;
                u = u_start;
                v = v_start;
                x2 = x_end >> fpPosition;
            }



            if(x1 < 0) {
                u -= du*x1;
                v -= dv*x1;
                x1 = 0;
            }





            if(x2 > g3d.width) x2 = g3d.width;

            tempI = g3d.width * y_start;
            x1 += tempI; //�� ���� �������� � �������
            x2 += tempI; //�� ���� �������� � ������

u -= du;
v -= dv;

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
}
static final void paintAffine_2(DirectX7 g3d, Texture texture,
            Vertex a, int au, int av,
            Vertex b, int bu, int bv,
            Vertex c, int cu, int cv,int fogc,int sz2) {



    //----------------------------
    //|          A               |
    //|        ****              |
    //|      *******             |
    //|    **********            |
    //|  B************           |
    //|     ***********          |
    //|        *********         |
    //|-----------@@@@@@@--------|
    //|              *****       |
    //|                 ***      |
    //|                    C     |
    //----------------------------

        //��������� �������
        if(b.sy < a.sy) {
            Vertex t = a; a = b; b = t;
            int tb = au; au = bu; bu = tb;
            tb = av; av = bv; bv = tb;
        }
        if(c.sy < a.sy) {
            Vertex t = c; c = a; a = t;
            int tb = cu; cu = au; au = tb;
            tb = cv; cv = av; av = tb;
        }
        if(c.sy < b.sy) {
            Vertex t = b; b = c; c = t;
            int tb = bu; bu = cu; cu = tb;
            tb = bv; bv = cv; cv = tb;
        }
        if(a.sy == c.sy) return;


        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������
        int x1, x2, u, v, tempI;
        int col,ca;

        tempI = c.sy - a.sy;                        // ����������
        final int dx_start = ((c.sx - a.sx)<<fpPosition) / tempI;//
        final int du_start = ((cu - au)<<fp) / tempI; //
        final int dv_start = ((cv - av)<<fp) / tempI;//

        int dx_end=0, du_end=0, dv_end=0; // ����������
        if(b.sy != a.sy) {
            tempI = b.sy - a.sy;
            dx_end = ((b.sx - a.sx)<<fpPosition) / tempI;
            du_end = ((bu - au)<<fp) / tempI;
            dv_end = ((bv - av)<<fp) / tempI;
        }


        tempI = b.sy - a.sy;
        int x_start = (a.sx<<fpPosition) + dx_start * tempI; //������ �����
        int u_start = (au<<fp) + du_start * tempI; //
        int v_start = (av<<fp) + dv_start * tempI;//

        int x_end = b.sx<<fpPosition; //����� �����
        int u_end = bu<<fp;  //
        int v_end = bv<<fp; //

        tempI = (x_start-x_end) >> fpPosition;  // �������� ������
        if(tempI == 0) return;         //
        final int du = (u_start-u_end)/tempI; //
        final int dv = (v_start-v_end)/tempI; //

        x_end = x_start = a.sx<<fpPosition;
        u_end = u_start = au<<fp;
        v_end = v_start = av<<fp;


        int y_start = a.sy; //������� �� Y �� �������
        int y_end = c.sy < g3d.height ? c.sy : g3d.height; // ����� �� Y �� �������




        for(;y_start<y_end; y_start++,
                x_start += dx_start,
                u_start += du_start,
                v_start += dv_start,

                x_end += dx_end,
                u_end += du_end,
                v_end += dv_end) { //������ ��� �������������� �����

            if(y_start == b.sy) {
                if(c.sy == b.sy) return;
                tempI = b.sy - a.sy;
                x_start = (a.sx<<fpPosition) + dx_start * tempI; //������ �����
                u_start = (au<<fp) + du_start * tempI; //
                v_start = (av<<fp) + dv_start * tempI;//
                x_end = b.sx<<fpPosition; //
                u_end = bu<<fp; //
                v_end = bv<<fp;//����� �����
                tempI = c.sy - b.sy;                  //
                dx_end = ((c.sx - b.sx)<<fpPosition) / tempI;//
                du_end = ((cu - bu)<<fp) / tempI;   //
                dv_end = ((cv - bv)<<fp) / tempI;  //�������� ��� ����� �����
            }

            if(y_start<0) continue;

            if (x_start > x_end) {
                x1 = x_end >> fpPosition;
                u = u_end;
                v = v_end;
                x2 = x_start >> fpPosition;
            } else {
                x1 = x_start >> fpPosition;
                u = u_start;
                v = v_start;
                x2 = x_end >> fpPosition;
            }


            if(x1 < 0) {
                u -= du*x1;
                v -= dv*x1;
                x1 = 0;
            }


            if(x2 > g3d.width) x2 = g3d.width;

            tempI = g3d.width * y_start;
            x1 += tempI; //�� ���� �������� � �������
            x2 += tempI; //�� ���� �������� � ������

u -= du;
v -= dv;

if(Main.s60Optimization==1) {
while(x2-x1>=4) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1]=(col=(col&0xFEFEFE)+(rgb[x1]&0xFEFEFE))|((( col>>>8 ) &0x010101)*0xFF);
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1+1]=(col=(col&0xFEFEFE)+(rgb[x1+1]&0xFEFEFE))|((( col>>>8 ) &0x010101)*0xFF);
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1+2]=(col=(col&0xFEFEFE)+(rgb[x1+2]&0xFEFEFE))|((( col>>>8 ) &0x010101)*0xFF);
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1+3]=(col=(col&0xFEFEFE)+(rgb[x1+3]&0xFEFEFE))|((( col>>>8 ) &0x010101)*0xFF);
x1+=4;

}
}
while(x1<x2) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1]=(col=(col&0xFEFEFE)+(rgb[x1]&0xFEFEFE))|((( col>>>8 ) &0x010101)*0xFF);
x1++;
}

}
}
/*
//Subtract
col=((0xFFFFFF-col)&0xFEFEFE)+((0xFFFFFF-rgb[x1])&0xFEFEFE);
col|=(( col>>8 ) &0x010101)*0xFF;  //clamp color to 0 - 255
rgb[x1]=ca| (0xFFFFFF-col);
*/

static final void paintAffine_3(DirectX7 g3d, Texture texture,
            Vertex a, int au, int av,
            Vertex b, int bu, int bv,
            Vertex c, int cu, int cv,int fogc,int sz2) {



    //----------------------------
    //|          A               |
    //|        ****              |
    //|      *******             |
    //|    **********            |
    //|  B************           |
    //|     ***********          |
    //|        *********         |
    //|-----------@@@@@@@--------|
    //|              *****       |
    //|                 ***      |
    //|                    C     |
    //----------------------------

        //��������� �������
        if(b.sy < a.sy) {
            Vertex t = a; a = b; b = t;
            int tb = au; au = bu; bu = tb;
            tb = av; av = bv; bv = tb;
        }
        if(c.sy < a.sy) {
            Vertex t = c; c = a; a = t;
            int tb = cu; cu = au; au = tb;
            tb = cv; cv = av; av = tb;
        }
        if(c.sy < b.sy) {
            Vertex t = b; b = c; c = t;
            int tb = bu; bu = cu; cu = tb;
            tb = bv; bv = cv; cv = tb;
        }
        if(a.sy == c.sy) return;

if(DirectX7.fDist/255!=0) {
sz2=-max(a.rz,b.rz,c.rz)/(DirectX7.fDist/255);
}
if(sz2>255) sz2=255;
if(sz2<0) sz2=0;

        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������
        int x1, x2, u, v, tempI;
        int col,ca;

int isz2=255-sz2;
int f2 =(fogc&0xFF00FF)*sz2;
int f22=(fogc&0x00FF00)*sz2;
        tempI = c.sy - a.sy;                        // ����������
        final int dx_start = ((c.sx - a.sx)<<fpPosition) / tempI;//
        final int du_start = ((cu - au)<<fp) / tempI; //
        final int dv_start = ((cv - av)<<fp) / tempI;//

        int dx_end=0, du_end=0, dv_end=0; // ����������
        if(b.sy != a.sy) {
            tempI = b.sy - a.sy;
            dx_end = ((b.sx - a.sx)<<fpPosition) / tempI;
            du_end = ((bu - au)<<fp) / tempI;
            dv_end = ((bv - av)<<fp) / tempI;
        }


        tempI = b.sy - a.sy;
        int x_start = (a.sx<<fpPosition) + dx_start * tempI; //������ �����
        int u_start = (au<<fp) + du_start * tempI; //
        int v_start = (av<<fp) + dv_start * tempI;//

        int x_end = b.sx<<fpPosition; //����� �����
        int u_end = bu<<fp;  //
        int v_end = bv<<fp; //

        tempI = (x_start-x_end) >> fpPosition;  // �������� ������
        if(tempI == 0) return;         //
        final int du = (u_start-u_end)/tempI; //
        final int dv = (v_start-v_end)/tempI; //

        x_end = x_start = a.sx<<fpPosition;
        u_end = u_start = au<<fp;
        v_end = v_start = av<<fp;


        int y_start = a.sy; //������� �� Y �� �������
        int y_end = c.sy < g3d.height ? c.sy : g3d.height; // ����� �� Y �� �������

if(Main.fogQ==2 && Main.forceLQFog==false) { paintAffine_3_HQ(
         x_start, x_end,
         y_start, y_end,
         u_start, u_end,
         v_start, v_end,
         dx_start, dx_end,
         du_start, du_end,
         dv_start, dv_end,
         a,  b,  c,
         au, av,
         bu, bv,
         cu, cv,
         du, dv,
         g3d,  texture); return;}
paintAffine_3_LQ(
         x_start, x_end,
         y_start, y_end,
         u_start, u_end,
         v_start, v_end,
         dx_start, dx_end,
         du_start, du_end,
         dv_start, dv_end,
         a,  b,  c,
         au, av,
         bu, bv,
         cu, cv,
         du, dv,
         g3d,  texture, isz2,f2,f22); return;

            }
static final void paintAffine_3_LQ(
        int x_start,int x_end,
        int y_start,int y_end,
        int u_start,int u_end,
        int v_start,int v_end,
        int dx_start,int dx_end,
        int du_start,int du_end,
        int dv_start,int dv_end,
        Vertex a, Vertex b, Vertex c,
        int au,int av,
        int bu,int bv,
        int cu,int cv,
        int du,int dv,
        DirectX7 g3d, Texture texture, int isz2,int f2,int f22) {
    
        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������
    int tempI,u,v,x1,x2,col;
    for(;y_start<y_end; y_start++,
                x_start += dx_start,
                u_start += du_start,
                v_start += dv_start,

                x_end += dx_end,
                u_end += du_end,
                v_end += dv_end) { //рисуем все горизонтальные линии

            if(y_start == b.sy) {
                if(c.sy == b.sy) return;
                tempI = b.sy - a.sy;
                x_start = (a.sx<<fpPosition) + dx_start * tempI; //начало линии
                u_start = (au<<fp) + du_start * tempI; //
                v_start = (av<<fp) + dv_start * tempI;//

                x_end = b.sx<<fpPosition; //
                u_end = bu<<fp; //
                v_end = bv<<fp;//конец линии
                tempI = c.sy - b.sy;                  //
                dx_end = ((c.sx - b.sx)<<fpPosition) / tempI;//
                du_end = ((cu - bu)<<fp) / tempI;   //
                dv_end = ((cv - bv)<<fp) / tempI;  //прищения для конца линии
            }

            if(y_start<0) continue;

            if (x_start > x_end) {
                x1 = x_end >> fpPosition;
                u = u_end;
                v = v_end;
                x2 = x_start >> fpPosition;
            } else {
                x1 = x_start >> fpPosition;
                u = u_start;
                v = v_start;
                x2 = x_end >> fpPosition;
            }



            if(x1 < 0) {
                u -= du*x1;
                v -= dv*x1;
                x1 = 0;
            }


            if(x2 > g3d.width) x2 = g3d.width;

            tempI = g3d.width * y_start;
            x1 += tempI; //от куда рисовать в массиве
            x2 += tempI; //до куда рисовать в массив

u -= du;
v -= dv;
if(Main.s60Optimization==1) {
while(x2-x1>=4) {
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
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0)  
    rgb[x1+2]=  (   
            (  ( (col&0xFF00FF)*isz2 + f2)  &0xFF00FF00  ) | 
            (  ( (col&0x00FF00)*isz2 +f22)  &0x00FF0000  )   
            ) >>>8;
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0)  
    rgb[x1+3]=  (   
            (  ( (col&0xFF00FF)*isz2 + f2)  &0xFF00FF00  ) | 
            (  ( (col&0x00FF00)*isz2 +f22)  &0x00FF0000  )   
            ) >>>8;
x1+=4;
}
}
if(Main.s60Optimization == 0) {
while(x2-x1>=2) {
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
while(x1<x2) {
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0)  
    rgb[x1]=  (   
            (  ( (col&0xFF00FF)*isz2 + f2)  &0xFF00FF00  ) | 
            (  ( (col&0x00FF00)*isz2 +f22)  &0x00FF0000  )   
            ) >>>8;
x1++;
}




}
}
static final void paintAffine_3_HQ(
        int x_start,int x_end,
        int y_start,int y_end,
        int u_start,int u_end,
        int v_start,int v_end,
        int dx_start,int dx_end,
        int du_start,int du_end,
        int dv_start,int dv_end,
        Vertex a, Vertex b, Vertex c,
        int au,int av,
        int bu,int bv,
        int cu,int cv,
        int du,int dv,
        DirectX7 g3d, Texture texture) {
    
    int fogcolor=DirectX7.fogc;
int fogcolor2=fogcolor&0x00FF00;
fogcolor=fogcolor&0xFF00FF;
int af=-a.rz*0xFF/DirectX7.fDist;
if(af>0xFF) af=0xFF;
if(af<0) af=0;
af=0xFF-af;
int bf=-b.rz*0xFF/DirectX7.fDist;
if(bf>0xFF) bf=0xFF;
if(bf<0) bf=0;
bf=0xFF-bf;
int cf=-c.rz*0xFF/DirectX7.fDist;
if(cf>0xFF) cf=0xFF;
if(cf<0) cf=0;
cf=0xFF-cf;


            final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������
    int tempI,u,v,x1,x2,col,f,tf,tf2;
            tempI = c.sy - a.sy;                        // РїСЂРёСЂР°С‰РµРЅРёСЏ
        final int df_start = ((cf - af)<<fp) / tempI;//
        int df_end=0; // РїСЂРёСЂР°С‰РµРЅРёСЏ
        if(b.sy != a.sy) {
            tempI = b.sy - a.sy;
            df_end = ((bf - af)<<fp) / tempI;
        }
tempI = b.sy - a.sy;

        int f_start = (af<<fp) + df_start * tempI;//


        int f_end = bf<<fp; //

        tempI = (((a.sx<<fpPosition) + dx_start * tempI)-(b.sx<<fpPosition)) >> fpPosition;  // РїРѕР»СѓС‡Р°РµРј РґРµР»СЊС‚С‹
        if(tempI == 0) return;         //

        final int df = (f_start-f_end)/tempI; //
        f_end = f_start = af<<fp;

    for(;y_start<y_end; y_start++,
                x_start += dx_start,
                u_start += du_start,
                v_start += dv_start,
                f_start += df_start,

                x_end += dx_end,
                u_end += du_end,
                v_end += dv_end,
                f_end += df_end) { //рисуем все горизонтальные линии

            if(y_start == b.sy) {
                if(c.sy == b.sy) return;
                tempI = b.sy - a.sy;
                x_start = (a.sx<<fpPosition) + dx_start * tempI; //начало линии
                u_start = (au<<fp) + du_start * tempI; //
                v_start = (av<<fp) + dv_start * tempI;//
                f_start = (af<<fp) + df_start * tempI;//

                x_end = b.sx<<fpPosition; //
                u_end = bu<<fp; //
                v_end = bv<<fp;//конец линии
                f_end = bf<<fp;//РєРѕРЅРµС† Р»РёРЅРёРё
                tempI = c.sy - b.sy;                  //
                dx_end = ((c.sx - b.sx)<<fpPosition) / tempI;//
                du_end = ((cu - bu)<<fp) / tempI;   //
                dv_end = ((cv - bv)<<fp) / tempI;  //прищения для конца линии
                df_end = ((cf - bf)<<fp) / tempI;  //РїСЂРёС‰РµРЅРёСЏ РґР»СЏ РєРѕРЅС†Р° Р»РёРЅРёРё

            }

            if(y_start<0) continue;

            if (x_start > x_end) {
                x1 = x_end >> fpPosition;
                u = u_end;
                v = v_end;
                f = f_end;
                x2 = x_start >> fpPosition;
            } else {
                x1 = x_start >> fpPosition;
                u = u_start;
                v = v_start;
                f = f_start;
                x2 = x_end >> fpPosition;
            }



            if(x1 < 0) {
                u -= du*x1;
                v -= dv*x1;
                f -= df*x1;

                x1 = 0;
            }


            if(x2 > g3d.width) x2 = g3d.width;

            tempI = g3d.width * y_start;
            x1 += tempI; //от куда рисовать в массиве
            x2 += tempI; //до куда рисовать в массив

u -= du;
v -= dv;

if(Main.s60Optimization==1) {
while(x2-x1>=2) {
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
while(x1<x2) {

if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1]=(   
            (  ( fogcolor*(tf2=0xFF-(tf=(f+=df)>>>fp)) + (col&0xFF00FF)*tf)  &0xFF00FF00  )| 
            (  ( fogcolor2*tf2                         + (col&0x00FF00)*tf)  &0x00FF0000  )   
            ) >>>8;
x1++;
}




}
}

static final void paintAffine_4(DirectX7 g3d, Texture texture,
            Vertex a, int au, int av,
            Vertex b, int bu, int bv,
            Vertex c, int cu, int cv,int fogc,int sz2) {



    //----------------------------
    //|          A               |
    //|        ****              |
    //|      *******             |
    //|    **********            |
    //|  B************           |
    //|     ***********          |
    //|        *********         |
    //|-----------@@@@@@@--------|
    //|              *****       |
    //|                 ***      |
    //|                    C     |
    //----------------------------

        //��������� �������
        if(b.sy < a.sy) {
            Vertex t = a; a = b; b = t;
            int tb = au; au = bu; bu = tb;
            tb = av; av = bv; bv = tb;
        }
        if(c.sy < a.sy) {
            Vertex t = c; c = a; a = t;
            int tb = cu; cu = au; au = tb;
            tb = cv; cv = av; av = tb;
        }
        if(c.sy < b.sy) {
            Vertex t = b; b = c; c = t;
            int tb = bu; bu = cu; cu = tb;
            tb = bv; bv = cv; cv = tb;
        }
        if(a.sy == c.sy) return;


        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������
        int x1, x2, u, v, tempI;
        int col,ca,cols;

        tempI = c.sy - a.sy;                        // ����������
        final int dx_start = ((c.sx - a.sx)<<fpPosition) / tempI;//
        final int du_start = ((cu - au)<<fp) / tempI; //
        final int dv_start = ((cv - av)<<fp) / tempI;//

        int dx_end=0, du_end=0, dv_end=0; // ����������
        if(b.sy != a.sy) {
            tempI = b.sy - a.sy;
            dx_end = ((b.sx - a.sx)<<fpPosition) / tempI;
            du_end = ((bu - au)<<fp) / tempI;
            dv_end = ((bv - av)<<fp) / tempI;
        }


        tempI = b.sy - a.sy;
        int x_start = (a.sx<<fpPosition) + dx_start * tempI; //������ �����
        int u_start = (au<<fp) + du_start * tempI; //
        int v_start = (av<<fp) + dv_start * tempI;//

        int x_end = b.sx<<fpPosition; //����� �����
        int u_end = bu<<fp;  //
        int v_end = bv<<fp; //

        tempI = (x_start-x_end) >> fpPosition;  // �������� ������
        if(tempI == 0) return;         //
        final int du = (u_start-u_end)/tempI; //
        final int dv = (v_start-v_end)/tempI; //

        x_end = x_start = a.sx<<fpPosition;
        u_end = u_start = au<<fp;
        v_end = v_start = av<<fp;


        int y_start = a.sy; //������� �� Y �� �������
        int y_end = c.sy < g3d.height ? c.sy : g3d.height; // ����� �� Y �� �������




        for(;y_start<y_end; y_start++,
                x_start += dx_start,
                u_start += du_start,
                v_start += dv_start,

                x_end += dx_end,
                u_end += du_end,
                v_end += dv_end) { //������ ��� �������������� �����

            if(y_start == b.sy) {
                if(c.sy == b.sy) return;
                tempI = b.sy - a.sy;
                x_start = (a.sx<<fpPosition) + dx_start * tempI; //������ �����
                u_start = (au<<fp) + du_start * tempI; //
                v_start = (av<<fp) + dv_start * tempI;//
                x_end = b.sx<<fpPosition; //
                u_end = bu<<fp; //
                v_end = bv<<fp;//����� �����
                tempI = c.sy - b.sy;                  //
                dx_end = ((c.sx - b.sx)<<fpPosition) / tempI;//
                du_end = ((cu - bu)<<fp) / tempI;   //
                dv_end = ((cv - bv)<<fp) / tempI;  //�������� ��� ����� �����
            }

            if(y_start<0) continue;

            if (x_start > x_end) {
                x1 = x_end >> fpPosition;
                u = u_end;
                v = v_end;
                x2 = x_start >> fpPosition;
            } else {
                x1 = x_start >> fpPosition;
                u = u_start;
                v = v_start;
                x2 = x_end >> fpPosition;
            }



            if(x1 < 0) {
                u -= du*x1;
                v -= dv*x1;
                x1 = 0;
            }





            if(x2 > g3d.width) x2 = g3d.width;

            tempI = g3d.width * y_start;
            x1 += tempI; //�� ���� �������� � �������
            x2 += tempI; //�� ���� �������� � ������

u -= du;
v -= dv;
if(Main.s60Optimization==1) {
while(x2-x1>=4) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1]= (   
        (  ( (col&0xFF00FF)*(ca=col >>> 24) + ( (cols=rgb[x1])&0xFF00FF)*(0xff-ca))  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*ca + ( cols&0x00FF00)*(0xff-ca))  &0x00FF0000  )   
        ) >>>8;
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1+1]= (   
        (  ( (col&0xFF00FF)*(ca=col >>> 24) + ( (cols=rgb[x1+1])&0xFF00FF)*(0xff-ca))  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*ca + ( cols&0x00FF00)*(0xff-ca))  &0x00FF0000  )   
        ) >>>8;
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1+2]= (   
        (  ( (col&0xFF00FF)*(ca=col >>> 24) + ( (cols=rgb[x1+2])&0xFF00FF)*(0xff-ca))  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*ca + ( cols&0x00FF00)*(0xff-ca))  &0x00FF0000  )   
        ) >>>8;
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1+3]= (   
        (  ( (col&0xFF00FF)*(ca=col >>> 24) + ( (cols=rgb[x1+3])&0xFF00FF)*(0xff-ca))  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*ca + ( cols&0x00FF00)*(0xff-ca))  &0x00FF0000  )   
        ) >>>8;
x1+=4;
}
}

while(x1<x2) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1]= (   
        (  ( (col&0xFF00FF)*(ca=col >>> 24) + ( (cols=rgb[x1])&0xFF00FF)*(0xff-ca))  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*ca + ( cols&0x00FF00)*(0xff-ca))  &0x00FF0000  )   
        ) >>>8;
x1++;
}

}
}






static final void paintAffine_5(DirectX7 g3d, Texture texture,
            Vertex a, int au, int av,
            Vertex b, int bu, int bv,
            Vertex c, int cu, int cv,int fogc,int sz2,int af,int bf,int cf,int nx,int ny,int nz) {


    //----------------------------
    //|          A               |
    //|        ****              |
    //|      *******             |
    //|    **********            |
    //|  B************           |
    //|     ***********          |
    //|        *********         |
    //|-----------@@@@@@@--------|
    //|              *****       |
    //|                 ***      |
    //|                    C     |
    //----------------------------

            
        //��������� �������
        if(b.sy < a.sy) {
            Vertex t = a; a = b; b = t;
            int tb = au; au = bu; bu = tb;
            tb = av; av = bv; bv = tb;
        }
        if(c.sy < a.sy) {
            Vertex t = c; c = a; a = t;
            int tb = cu; cu = au; au = tb;
            tb = cv; cv = av; av = tb;
        }
        if(c.sy < b.sy) {
            Vertex t = b; b = c; c = t;
            int tb = bu; bu = cu; cu = tb;
            tb = bv; bv = cv; cv = tb;
        }
        if(a.sy == c.sy) return;
        

if(texture.getDrawMode()==6) {
if(DirectX7.fDist/255!=0) {
sz2=-max(a.rz,b.rz,c.rz)/(DirectX7.fDist/255);
}
if(sz2>255) sz2=255;
if(sz2<0) sz2=0;
}
        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������
        int x1, x2, u, v, tempI;
        int col,ca;

        tempI = c.sy - a.sy;                        // ����������
        final int dx_start = ((c.sx - a.sx)<<fpPosition) / tempI;//
        final int du_start = ((cu - au)<<fp) / tempI; //
        final int dv_start = ((cv - av)<<fp) / tempI;//

        int dx_end=0, du_end=0, dv_end=0; // ����������
        if(b.sy != a.sy) {
            tempI = b.sy - a.sy;
            dx_end = ((b.sx - a.sx)<<fpPosition) / tempI;
            du_end = ((bu - au)<<fp) / tempI;
            dv_end = ((bv - av)<<fp) / tempI;
        }


        tempI = b.sy - a.sy;
        int x_start = (a.sx<<fpPosition) + dx_start * tempI; //������ �����
        int u_start = (au<<fp) + du_start * tempI; //
        int v_start = (av<<fp) + dv_start * tempI;//

        int x_end = b.sx<<fpPosition; //����� �����
        int u_end = bu<<fp;  //
        int v_end = bv<<fp; //

        tempI = (x_start-x_end) >> fpPosition;  // �������� ������
        if(tempI == 0) return;         //
        final int du = (u_start-u_end)/tempI; //
        final int dv = (v_start-v_end)/tempI; //

        x_end = x_start = a.sx<<fpPosition;
        u_end = u_start = au<<fp;
        v_end = v_start = av<<fp;


        int y_start = a.sy; //������� �� Y �� �������
        int y_end = c.sy < g3d.height ? c.sy : g3d.height; // ����� �� Y �� �������


int isz2=255-sz2;
paintAffine_5_draw(
         x_start, x_end,
         y_start, y_end,
         u_start, u_end,
         v_start, v_end,
         dx_start, dx_end,
         du_start, du_end,
         dv_start, dv_end,
         a,  b,  c,
         au, av,
         bu, bv,
         cu, cv,
         du, dv,
         g3d,  texture,isz2);

}


static final void paintAffine_5_draw(
        int x_start,int x_end,
        int y_start,int y_end,
        int u_start,int u_end,
        int v_start,int v_end,
        int dx_start,int dx_end,
        int du_start,int du_end,
        int dv_start,int dv_end,
        Vertex a, Vertex b, Vertex c,
        int au,int av,
        int bu,int bv,
        int cu,int cv,
        int du,int dv,
        DirectX7 g3d, Texture texture,int isz2) {



        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������
        int tempI,u,v,x1,x2,col;
            for(;y_start<y_end; y_start++,
                x_start += dx_start,
                u_start += du_start,
                v_start += dv_start,

                x_end += dx_end,
                u_end += du_end,
                v_end += dv_end) { //рисуем все горизонтальные линии

            if(y_start == b.sy) {
                if(c.sy == b.sy) return;
                tempI = b.sy - a.sy;
                x_start = (a.sx<<fpPosition) + dx_start * tempI; //начало линии
                u_start = (au<<fp) + du_start * tempI; //
                v_start = (av<<fp) + dv_start * tempI;//
                x_end = b.sx<<fpPosition; //
                u_end = bu<<fp; //
                v_end = bv<<fp;//конец линии
                tempI = c.sy - b.sy;                  //
                dx_end = ((c.sx - b.sx)<<fpPosition) / tempI;//
                du_end = ((cu - bu)<<fp) / tempI;   //
                dv_end = ((cv - bv)<<fp) / tempI;  //прищения для конца линии
            }

            if(y_start<0) continue;

            if (x_start > x_end) {
                x1 = x_end >> fpPosition;
                u = u_end;
                v = v_end;
                x2 = x_start >> fpPosition;
            } else {
                x1 = x_start >> fpPosition;
                u = u_start;
                v = v_start;
                x2 = x_end >> fpPosition;
            }



            if(x1 < 0) {
                u -= du*x1;
                v -= dv*x1;
                x1 = 0;
            }





            if(x2 > g3d.width) x2 = g3d.width;

            tempI = g3d.width * y_start;
            x1 += tempI; //�� ���� �������� � �������
            x2 += tempI; //�� ���� �������� � ������

u -= du;
v -= dv;
if(Main.s60Optimization==1) {
while(x2-x1>=6) {
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
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT])!= 0) 
    rgb[x1+2]= (   
        (  ( (col&0xFF00FF)*isz2)  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*isz2)  &0x00FF0000  )   
        ) >>>8;
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT])!= 0) 
    rgb[x1+3]= (   
        (  ( (col&0xFF00FF)*isz2)  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*isz2)  &0x00FF0000  )   
        ) >>>8;
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT])!= 0) 
    rgb[x1+4]= (   
        (  ( (col&0xFF00FF)*isz2)  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*isz2)  &0x00FF0000  )   
        ) >>>8;
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT])!= 0) 
    rgb[x1+5]= (   
        (  ( (col&0xFF00FF)*isz2)  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)*isz2)  &0x00FF0000  )   
        ) >>>8;
x1+=6;
}
}
if(Main.s60Optimization == 0) {
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
}

static final void paintAffine_9(DirectX7 g3d, Texture texture,
            Vertex a, int au, int av,
            Vertex b, int bu, int bv,
            Vertex c, int cu, int cv,int fogc,int sz2,int ar,int br,int cr,int nx,int ny,int nz) {


    //----------------------------
    //|          A               |
    //|        ****              |
    //|      *******             |
    //|    **********            |
    //|  B************           |
    //|     ***********          |
    //|        *********         |
    //|-----------@@@@@@@--------|
    //|              *****       |
    //|                 ***      |
    //|                    C     |
    //----------------------------

        //��������� �������
        if(b.sy < a.sy) {
            Vertex t = a; a = b; b = t;
            int tb = au; au = bu; bu = tb;
            tb = av; av = bv; bv = tb;
            tb = ar; ar = br; br = tb;
        }
        if(c.sy < a.sy) {
            Vertex t = c; c = a; a = t;
            int tb = cu; cu = au; au = tb;
            tb = cv; cv = av; av = tb;
            tb = cr; cr = ar; ar = tb;
        }
        if(c.sy < b.sy) {
            Vertex t = b; b = c; c = t;
            int tb = bu; bu = cu; cu = tb;
            tb = bv; bv = cv; cv = tb;
            tb = br; br = cr; cr = tb;
        }
        if(a.sy == c.sy) return;

        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������
        int x1, x2, u, v, tempI, rC, gC ,bC, isz2, rT;
        int col,ca;

        tempI = c.sy - a.sy;                        // ����������
        final int dx_start = ((c.sx - a.sx)<<fpPosition) / tempI;//
        final int du_start = ((cu - au)<<fp) / tempI; //
        final int dv_start = ((cv - av)<<fp) / tempI;//
        final int dr_start = ((cr - ar)<<fp) / tempI;//

        int dx_end=0, du_end=0, dv_end=0, dr_end=0; // ����������
        if(b.sy != a.sy) {
            tempI = b.sy - a.sy;
            dx_end = ((b.sx - a.sx)<<fpPosition) / tempI;
            du_end = ((bu - au)<<fp) / tempI;
            dv_end = ((bv - av)<<fp) / tempI;
            dr_end = ((br - ar)<<fp) / tempI;
        }


        tempI = b.sy - a.sy;
        int x_start = (a.sx<<fpPosition) + dx_start * tempI; //������ �����
        int u_start = (au<<fp) + du_start * tempI; //
        int v_start = (av<<fp) + dv_start * tempI;//
        int r_start = (ar<<fp) + dr_start * tempI;//

        int x_end = b.sx<<fpPosition; //����� �����
        int u_end = bu<<fp;  //
        int v_end = bv<<fp; //
        int r_end = br<<fp; //

        tempI = (x_start-x_end) >> fpPosition;  // �������� ������
        if(tempI == 0) return;         //
        final int du = (u_start-u_end)/tempI; //
        final int dv = (v_start-v_end)/tempI; //
        int dr = (r_start-r_end)/tempI; //
        
        
        if(dr>0) dr-=1;
        if(dr<0) dr+=1;

        x_end = x_start = a.sx<<fpPosition;
        u_end = u_start = au<<fp;
        v_end = v_start = av<<fp;
        r_end = r_start = ar<<fp;


        int y_start = a.sy; //������� �� Y �� �������
        int y_end = c.sy < g3d.height ? c.sy : g3d.height; // ����� �� Y �� �������

    if (LightMapper.cameraVectorLight && texture.drawmode == 6) {
        ar = ar * MathUtils.calcLight(nx, ny, nz, a.x - g3d.getCamera().m03, a.y - g3d.getCamera().m13, a.z - g3d.getCamera().m23) / 255;
        br = br * MathUtils.calcLight(nx, ny, nz, b.x - g3d.getCamera().m03, b.y - g3d.getCamera().m13, b.z - g3d.getCamera().m23) / 255;
        cr = cr * MathUtils.calcLight(nx, ny, nz, c.x - g3d.getCamera().m03, c.y - g3d.getCamera().m13, c.z - g3d.getCamera().m23) / 255;
    }

    
    paint9Mini(
         x_start, x_end,
         y_start, y_end,
         u_start, u_end,
         v_start, v_end,
         r_start, r_end,
         dx_start, dx_end,
         du_start, du_end,
         dv_start, dv_end,
         dr_start, dr_end,
         a,  b,  c,
         au, av,
         bu, bv,
         cu, cv,
         du, dv,
         dr, ar, br, cr,
         g3d,  texture);
    
    
}

private static final void paint9Mini(
        int x_start,int x_end,
        int y_start,int y_end,
        int u_start,int u_end,
        int v_start,int v_end,
        int r_start,int r_end,
        int dx_start,int dx_end,
        int du_start,int du_end,
        int dv_start,int dv_end,
        int dr_start,int dr_end,
        Vertex a, Vertex b, Vertex c,
        int au,int av,
        int bu,int bv,
        int cu,int cv,
        int du,int dv,
        int dr,int ar,int br,int cr,
        DirectX7 g3d, Texture texture) {
    
    
    
    final int[] tex = texture.rImg.img;
    final int lengthBIT = tex.length-1;
    final int widthBIT = texture.rImg.widthBIT;
    final int[] rgb = g3d.display; //�������
    int tempI,u,v,x1,x2,col, rC, isz2, rT, dr2;
    
    
    for(;y_start<y_end; y_start++,
                x_start += dx_start,
                u_start += du_start,
                v_start += dv_start,
                r_start += dr_start,

                x_end += dx_end,
                u_end += du_end,
                v_end += dv_end,
                r_end += dr_end) { //рисуем все горизонтальные линии
        
        if (y_start == b.sy) {
            if (c.sy == b.sy) return;
            tempI = b.sy - a.sy;
            x_start = (a.sx << fpPosition) + dx_start * tempI; //начало линии
            u_start = (au << fp) + du_start * tempI; //
            v_start = (av << fp) + dv_start * tempI;//
            r_start = (ar << fp) + dr_start * tempI;//

            x_end = b.sx << fpPosition; //
            u_end = bu << fp; //
            v_end = bv << fp;//конец линии
            r_end = br << fp;//РєРѕРЅРµС† Р»РёРЅРёРё
            
            tempI = c.sy - b.sy;                  //
            dx_end = ((c.sx - b.sx) << fpPosition) / tempI;//
            du_end = ((cu - bu) << fp) / tempI;   //
            dv_end = ((cv - bv) << fp) / tempI;  //прищения для конца линии
            dr_end = ((cr - br) << fp) / tempI;  //РїСЂРёС‰РµРЅРёСЏ РґР»СЏ РєРѕРЅС†Р° Р»РёРЅРёРё
        }

            if(y_start<0) continue;

            if (x_start > x_end) {
                x1 = x_end >> fpPosition;
                u = u_end;
                v = v_end;
                rC = r_end;
                x2 = x_start >> fpPosition;
            } else {
                x1 = x_start >> fpPosition;
                u = u_start;
                v = v_start;
                rC = r_start;
                x2 = x_end >> fpPosition;
            }



            if(x1 < 0) {
                u -= du*x1;
                v -= dv*x1;
                rC -= dr*x1;
                x1 = 0;
            }


            if(x2 > g3d.width) x2 = g3d.width;

            tempI = g3d.width * y_start;
            x1 += tempI; //от куда рисовать в массиве
            x2 += tempI; //до куда рисовать в массив
            int length = x2-x1;

u -= du;
v -= dv;
if(rC<0) rC=0;
dr2=dr;
if(rC+dr*length<0) dr2=-rC/length;

if(Main.s60Optimization==1) {
while(x2-x1>=2) {
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1]=(   
        (  ( (col&0xFF00FF)*(rT=(rC+=dr2)>>>fp))  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)* rT)                &0x00FF0000  )   
        ) >>>8;
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1+1]=(   
        (  ( (col&0xFF00FF)*(rT=(rC+=dr2)>>>fp))  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)* rT)                &0x00FF0000  )   
        ) >>>8;
x1+=2;
}
}
while(x1<x2) {
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1]=(   
        (  ( (col&0xFF00FF)*(rT=(rC+=dr2)>>>fp))  &0xFF00FF00  )+
        (  ( (col&0x00FF00)* rT)                &0x00FF0000  )   
        ) >>>8;
x1++;
}

}
    
    
}





static final void paintAffine_13(DirectX7 g3d, Texture texture,
            Vertex a, int au, int av,
            Vertex b, int bu, int bv,
            Vertex c, int cu, int cv,int fogc,int sz2,
            int ar,int br,int cr,
            int ag,int bg,int cg,
            int ab,int bb,int cb,
            int nx,int ny,int nz) {


    //----------------------------
    //|          A               |
    //|        ****              |
    //|      *******             |
    //|    **********            |
    //|  B************           |
    //|     ***********          |
    //|        *********         |
    //|-----------@@@@@@@--------|
    //|              *****       |
    //|                 ***      |
    //|                    C     |
    //----------------------------

            
        //��������� �������
        if(b.sy < a.sy) {
            Vertex t = a; a = b; b = t;
            int tb = au; au = bu; bu = tb;
            tb = av; av = bv; bv = tb;
            tb = ar; ar = br; br = tb;
            tb = ag; ag = bg; bg = tb;
            tb = ab; ab = bb; bb = tb;
        }
        if(c.sy < a.sy) {
            Vertex t = c; c = a; a = t;
            int tb = cu; cu = au; au = tb;
            tb = cv; cv = av; av = tb;
            tb = cr; cr = ar; ar = tb;
            tb = cg; cg = ag; ag = tb;
            tb = cb; cb = ab; ab = tb;
        }
        if(c.sy < b.sy) {
            Vertex t = b; b = c; c = t;
            int tb = bu; bu = cu; cu = tb;
            tb = bv; bv = cv; cv = tb;
            tb = br; br = cr; cr = tb;
            tb = bg; bg = cg; cg = tb;
            tb = bb; bb = cb; cb = tb;
        }
        if(a.sy == c.sy) return;

        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������
        int x1, x2, u, v, tempI, rC, gC ,bC, isz2, rT, gT, bT;
        int col,ca;

        tempI = c.sy - a.sy;                        // ����������
        final int dx_start = ((c.sx - a.sx)<<fpPosition) / tempI;//
        final int du_start = ((cu - au)<<fp) / tempI; //
        final int dv_start = ((cv - av)<<fp) / tempI;//
        final int dr_start = ((cr - ar)<<fp) / tempI;//
        final int dg_start = ((cg - ag)<<fp) / tempI;//
        final int db_start = ((cb - ab)<<fp) / tempI;//

        int dx_end=0, du_end=0, dv_end=0, dr_end=0, dg_end=0, db_end=0; // ����������
        if(b.sy != a.sy) {
            tempI = b.sy - a.sy;
            dx_end = ((b.sx - a.sx)<<fpPosition) / tempI;
            du_end = ((bu - au)<<fp) / tempI;
            dv_end = ((bv - av)<<fp) / tempI;
            dr_end = ((br - ar)<<fp) / tempI;
            dg_end = ((bg - ag)<<fp) / tempI;
            db_end = ((bb - ab)<<fp) / tempI;
        }


        tempI = b.sy - a.sy;
        int x_start = (a.sx<<fpPosition) + dx_start * tempI; //������ �����
        int u_start = (au<<fp) + du_start * tempI; //
        int v_start = (av<<fp) + dv_start * tempI;//
        int r_start = (ar<<fp) + dr_start * tempI;//
        int g_start = (ag<<fp) + dg_start * tempI;//
        int b_start = (ab<<fp) + db_start * tempI;//

        int x_end = b.sx<<fpPosition; //����� �����
        int u_end = bu<<fp;  //
        int v_end = bv<<fp; //
        int r_end = br<<fp; //
        int g_end = bg<<fp; //
        int b_end = bb<<fp; //

        tempI = (x_start-x_end) >> fpPosition;  // �������� ������
        int tempI2 = tempI;  // �������� ������
        /*if(tempI>0) tempI2+=1;
                else tempI2-=1;*/
        if(tempI == 0 || tempI2 == 0) return;         //
        final int du = (u_start-u_end)/tempI; //
        final int dv = (v_start-v_end)/tempI; //
        int dr = (r_start-r_end)/tempI; //
        int dg = (g_start-g_end)/tempI; //
        int db = (b_start-b_end)/tempI; //
        

        x_end = x_start = a.sx<<fpPosition;
        u_end = u_start = au<<fp;
        v_end = v_start = av<<fp;
        r_end = r_start = ar<<fp;
        g_end = g_start = ag<<fp;
        b_end = b_start = ab<<fp;


        int y_start = a.sy; //������� �� Y �� �������
        int y_end = c.sy < g3d.height ? c.sy : g3d.height; // ����� �� Y �� �������

    paint13Mini(
         x_start, x_end,
         y_start, y_end,
         u_start, u_end,
         v_start, v_end,
         r_start, r_end,
         g_start, g_end,
         b_start, b_end,
         dx_start, dx_end,
         du_start, du_end,
         dv_start, dv_end,
         dr_start, dr_end,
         dg_start, dg_end,
         db_start, db_end,
         a,  b,  c,
         au, av,
         bu, bv,
         cu, cv,
         du, dv,
         dr, dg, db,
         ar, ag, ab,
         br, bg, bb,
         cr, cg, cb,
         g3d,  texture);
    
}


private static final void paint13Mini(
        int x_start,int x_end,
        int y_start,int y_end,
        int u_start,int u_end,
        int v_start,int v_end,
        int r_start,int r_end,
        int g_start,int g_end,
        int b_start,int b_end,
        int dx_start,int dx_end,
        int du_start,int du_end,
        int dv_start,int dv_end,
        int dr_start,int dr_end,
        int dg_start,int dg_end,
        int db_start,int db_end,
        Vertex a, Vertex b, Vertex c,
        int au,int av,
        int bu,int bv,
        int cu,int cv,
        int du,int dv,
        int dr,int dg,int db,
        int ar,int ag,int ab,
        int br,int bg,int bb,
        int cr,int cg,int cb,
        DirectX7 g3d, Texture texture) {
    
    final int[] tex = texture.rImg.img;
    final int lengthBIT = tex.length-1;
    final int widthBIT = texture.rImg.widthBIT;
    final int[] rgb = g3d.display; //�������
    int tempI,u,v,x1,x2,col, rC, gC ,bC, dr2, dg2, db2;
    int rb, gg;
    
    
    for(;y_start<y_end; y_start++,
                x_start += dx_start,
                u_start += du_start,
                v_start += dv_start,
                r_start += dr_start,
                g_start += dg_start,
                b_start += db_start,

                x_end += dx_end,
                u_end += du_end,
                v_end += dv_end,
                r_end += dr_end,
                g_end += dg_end,
                b_end += db_end) { //рисуем все горизонтальные линии
        
        if (y_start == b.sy) {
            if (c.sy == b.sy) return;
            tempI = b.sy - a.sy;
            x_start = (a.sx << fpPosition) + dx_start * tempI; //начало линии
            u_start = (au << fp) + du_start * tempI; //
            v_start = (av << fp) + dv_start * tempI;//
            r_start = (ar << fp) + dr_start * tempI;//
            g_start = (ag << fp) + dg_start * tempI;//
            b_start = (ab << fp) + db_start * tempI;//

            x_end = b.sx << fpPosition; //
            u_end = bu << fp; //
            v_end = bv << fp;//конец линии
            r_end = br << fp;//РєРѕРЅРµС† Р»РёРЅРёРё
            g_end = bg << fp;//РєРѕРЅРµС† Р»РёРЅРёРё
            b_end = bb << fp;//РєРѕРЅРµС† Р»РёРЅРёРё
            
            tempI = c.sy - b.sy;                  //
            dx_end = ((c.sx - b.sx) << fpPosition) / tempI;//
            du_end = ((cu - bu) << fp) / tempI;   //
            dv_end = ((cv - bv) << fp) / tempI;  //прищения для конца линии
            dr_end = ((cr - br) << fp) / tempI;  //РїСЂРёС‰РµРЅРёСЏ РґР»СЏ РєРѕРЅС†Р° Р»РёРЅРёРё
            dg_end = ((cg - bg) << fp) / tempI;  //РїСЂРёС‰РµРЅРёСЏ РґР»СЏ РєРѕРЅС†Р° Р»РёРЅРёРё
            db_end = ((cb - bb) << fp) / tempI;  //РїСЂРёС‰РµРЅРёСЏ РґР»СЏ РєРѕРЅС†Р° Р»РёРЅРёРё
        }

            if(y_start<0) continue;

            if (x_start > x_end) {
                x1 = x_end >> fpPosition;
                u = u_end;
                v = v_end;
                rC = r_end;
                gC = g_end;
                bC = b_end;
                x2 = x_start >> fpPosition;
            } else {
                x1 = x_start >> fpPosition;
                u = u_start;
                v = v_start;
                rC = r_start;
                gC = g_start;
                bC = b_start;
                x2 = x_end >> fpPosition;
            }

            if(x1 < 0) {
                u -= du*x1;
                v -= dv*x1;
                rC -= dr*x1;
                gC -= dg*x1;
                bC -= db*x1;
                x1 = 0;
            }


            if(x2 > g3d.width) x2 = g3d.width;

            tempI = g3d.width * y_start;
            x1 += tempI; //от куда рисовать в массиве
            x2 += tempI; //до куда рисовать в массив
            if(rC<0) rC=0;
            if(gC<0) gC=0;
            if(bC<0) bC=0;
            int length=x2-x1;
            dr2=dr;dg2=dg;db2=db;
            if(rC+dr*length<0) dr2=-rC/length;
            if(gC+dg*length<0) dg2=-gC/length;
            if(bC+db*length<0) db2=-bC/length;
            v-=dv;
            u-=du;
            //line13(x1,x2,tex,u,v,du,dv,rC,gC,bC,rgb,dr2,dg2,db2,widthBIT,lengthBIT);
            /*   while(x1<x2) {
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) {
    rb = ( (col&0xFF0000)*((rC+=dr2)>>>fp)) | 
         ( (col&0x0000FF)*((bC+=db2)>>>fp));
    gg = ( (col&0x00FF00)*((gC+=dg2)>>>fp)) ;
    
    rgb[x1]= ((rb&0x7f807f80) | (((rb>>8)&0x800080)*0xff) | (gg&0x7f8000) | (((gg>>8)&0x8000)*0xff))>>>7;
}
x1++;
}*/
while(x1<x2) {
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
/*
static final void line13(int x1, final int x2,
        final int[] tex, int u, int v, final int du, final int dv, int rC, int gC, int bC,
        final int[] rgb, final int dr2, final int dg2, final int db2,
        final int widthBIT, final int lengthBIT) {
    int col;
    while(x1<x2) {
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) rgb[x1]=
        (
        (  ( (col&0xFF0000)*((rC+=dr2)>>>fp))  &0xFF000000) | 
        (  ( (col&0x00FF00)*((gC+=dg2)>>>fp))  &0x00FF0000) |
        (  ( (col&0x0000FF)*((bC+=db2)>>>fp))  ) 
        ) >>>8;
x1++;
}
}*/

/*
static final void line13(int x1, final int x2,
        final int[] tex, int u, int v, final int du, final int dv, int rC, int gC, int bC,
        final int[] rgb, final int dr2, final int dg2, final int db2,
        final int widthBIT, final int lengthBIT) {
    int col, rb, gg;
    while(x1<x2) {
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) {
    rb = ( (col&0xFF0000)*((rC+=dr2)>>>fp)) | 
         ( (col&0x0000FF)*((bC+=db2)>>>fp));
    gg = ( (col&0x00FF00)*((gC+=dg2)>>>fp)) ;
    
    rgb[x1]= ((rb&0x7f807f80) | (((rb>>8)&0x800080)*0xff) | (gg&0x7f8000) | (((gg>>8)&0x8000)*0xff))>>>7;
}
x1++;
}
}
*/


static final void paintDitherGradient(DirectX7 g3d, Texture texture,
            Vertex a, int au2,
            Vertex b, int bu2,
            Vertex c, int cu2) {



    //----------------------------
    //|          A               |
    //|        ****              |
    //|      *******             |
    //|    **********            |
    //|  B************           |
    //|     ***********          |
    //|        *********         |
    //|-----------@@@@@@@--------|
    //|              *****       |
    //|                 ***      |
    //|                    C     |
    //----------------------------

        //��������� �������
        if(b.sy < a.sy) {
            Vertex t = a; a = b; b = t;
            int tb = au2; au2 = bu2; bu2 = tb;
        }
        if(c.sy < a.sy) {
            Vertex t = c; c = a; a = t;
            int tb = cu2; cu2 = au2; au2 = tb;
        }
        if(c.sy < b.sy) {
            Vertex t = b; b = c; c = t;
            int tb = bu2; bu2 = cu2; cu2 = tb;
        }
        if(a.sy == c.sy) return;

        int wInH=texture.rImg.h/texture.rImg.w;
        
        int au=au2*wInH/256;
        int bu=bu2*wInH/256;
        int cu=cu2*wInH/256;
        if(au>wInH-1) au=wInH-1;
        if(bu>wInH-1) bu=wInH-1;
        if(cu>wInH-1) cu=wInH-1;
        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length/wInH;
        final int lengthBIT2 = lengthBIT-1;
        final int lengthBIT3 = tex.length-1;
        final int texWidth = texture.rImg.w;
        final int widthBIT = texture.rImg.widthBITmode10;
        final int[] rgb = g3d.display; //�������
        int x1, x2, u, v, tempI;
        int col;
        tempI = c.sy-a.sy;

        final int dx_start = ((c.sx - a.sx)<<fpPosition) / tempI;//
        final int du_start = ((cu - au)<<fp) / tempI; //

        int dx_end=0, du_end=0, dv_end=0; // ����������
        if(b.sy != a.sy) {
            tempI = b.sy-a.sy;

            dx_end = ((b.sx - a.sx)<<fpPosition) / tempI;
            du_end = ((bu - au)<<fp) / tempI;
        }


        tempI = b.sy - a.sy;
        int x_start = (a.sx<<fpPosition) + dx_start * tempI; //������ �����
        int u_start = (au<<fp) + du_start * tempI; //

        int x_end = b.sx<<fpPosition; //����� �����
        int u_end = bu<<fp;  //

        tempI = (x_start-x_end) >> fpPosition;  // �������� ������
        if(tempI == 0) return;         //
        int du = (u_start-u_end)/tempI; //
        


        x_end = x_start = a.sx<<fpPosition;
        u_end = u_start = au<<fp;


        int y_start = a.sy; //������� �� Y �� �������
        int y_end = c.sy < g3d.height ? c.sy : g3d.height; // ����� �� Y �� �������
        int yy = y_start*texWidth/*-g3d.width*y_start*/;
        int wMove=texWidth-g3d.width;
        int tmpu;
        if(du>0) du-=1;
        if(du<0) du+=1;
for(;y_start<y_end; y_start++,
                x_start += dx_start,
                u_start += du_start,

                x_end += dx_end,
                u_end += du_end) { //рисуем все горизонтальные линии

            if(y_start == b.sy) {
                if(c.sy == b.sy) return;
                tempI = b.sy - a.sy;
                x_start = (a.sx<<fpPosition) + dx_start * tempI; //начало линии
                u_start = (au<<fp) + du_start * tempI; //
                x_end = b.sx<<fpPosition; //
                u_end = bu<<fp; //
                tempI = c.sy - b.sy;                  //
                dx_end = ((c.sx - b.sx)<<fpPosition) / tempI;//
                du_end = ((cu - bu)<<fp) / tempI;   //
            }

            if(y_start<0) continue;

            if (x_start > x_end) {
                x1 = x_end >> fpPosition;
                u = u_end;
                x2 = x_start >> fpPosition;
            } else {
                x1 = x_start >> fpPosition;
                u = u_start;
                x2 = x_end >> fpPosition;
            }



            if(x1 < 0) {
                u -= du*x1;
                x1 = 0;
            }


            if(x2 > g3d.width) x2 = g3d.width;

            tempI = g3d.width * y_start;
            x1 += tempI; //от куда рисовать в массиве
            x2 += tempI; //до куда рисовать в массив
            yy+=wMove;
            tmpu=u>>fp;
while(x2-x1>=7) {
rgb[x1]=tex[(((x1+yy)&lengthBIT2)+(tmpu<<widthBIT))&lengthBIT3];  
u+=du; tmpu=u>>fp; x1++;
rgb[x1]=tex[(((x1+yy)&lengthBIT2)+(tmpu<<widthBIT))&lengthBIT3]; 
u+=du; tmpu=u>>fp; x1++;
rgb[x1]=tex[(((x1+yy)&lengthBIT2)+(tmpu<<widthBIT))&lengthBIT3]; 
u+=du; tmpu=u>>fp; x1++;
rgb[x1]=tex[(((x1+yy)&lengthBIT2)+(tmpu<<widthBIT))&lengthBIT3]; 
u+=du; tmpu=u>>fp; x1++;
rgb[x1]=tex[(((x1+yy)&lengthBIT2)+(tmpu<<widthBIT))&lengthBIT3]; 
u+=du; tmpu=u>>fp; x1++;
rgb[x1]=tex[(((x1+yy)&lengthBIT2)+(tmpu<<widthBIT))&lengthBIT3]; 
u+=du; tmpu=u>>fp; x1++;
rgb[x1]=tex[(((x1+yy)&lengthBIT2)+(tmpu<<widthBIT))&lengthBIT3]; 
u+=du; tmpu=u>>fp; x1++;
}

while(x1<x2) {
rgb[x1]=tex[(((x1+yy)&lengthBIT2)+(tmpu<<widthBIT))&lengthBIT3]; 
u+=du; tmpu=u>>fp;
x1++;
}




}

}

static final void paint_glass(DirectX7 g3d, Texture texture,
            Vertex a, int au, int av,
            Vertex b, int bu, int bv,
            Vertex c, int cu, int cv,int fogc,int sz2) {



    //----------------------------
    //|          A               |
    //|        ****              |
    //|      *******             |
    //|    **********            |
    //|  B************           |
    //|     ***********          |
    //|        *********         |
    //|-----------@@@@@@@--------|
    //|              *****       |
    //|                 ***      |
    //|                    C     |
    //----------------------------

        //��������� �������
        if(b.sy < a.sy) {
            Vertex t = a; a = b; b = t;
            int tb = au; au = bu; bu = tb;
            tb = av; av = bv; bv = tb;
        }
        if(c.sy < a.sy) {
            Vertex t = c; c = a; a = t;
            int tb = cu; cu = au; au = tb;
            tb = cv; cv = av; av = tb;
        }
        if(c.sy < b.sy) {
            Vertex t = b; b = c; c = t;
            int tb = bu; bu = cu; cu = tb;
            tb = bv; bv = cv; cv = tb;
        }
        if(a.sy == c.sy) return;


        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������
        final int w = g3d.width;
        int x1, x2, u, v, tempI, col, coord;
        int g3dlen = rgb.length-1;
        int scale=64;
        scale=scale*(g3d.getHeight()+g3d.getWidth())/(240+320);

        tempI = c.sy - a.sy;                        // ����������
        final int dx_start = ((c.sx - a.sx)<<fpPosition) / tempI;//
        final int du_start = ((cu - au)<<fp) / tempI; //
        final int dv_start = ((cv - av)<<fp) / tempI;//

        int dx_end=0, du_end=0, dv_end=0; // ����������
        if(b.sy != a.sy) {
            tempI = b.sy - a.sy;
            dx_end = ((b.sx - a.sx)<<fpPosition) / tempI;
            du_end = ((bu - au)<<fp) / tempI;
            dv_end = ((bv - av)<<fp) / tempI;
        }


        tempI = b.sy - a.sy;
        int x_start = (a.sx<<fpPosition) + dx_start * tempI; //������ �����
        int u_start = (au<<fp) + du_start * tempI; //
        int v_start = (av<<fp) + dv_start * tempI;//

        int x_end = b.sx<<fpPosition; //����� �����
        int u_end = bu<<fp;  //
        int v_end = bv<<fp; //

        tempI = (x_start-x_end) >> fpPosition;  // �������� ������
        if(tempI == 0) return;         //
        final int du = (u_start-u_end)/tempI; //
        final int dv = (v_start-v_end)/tempI; //

        x_end = x_start = a.sx<<fpPosition;
        u_end = u_start = au<<fp;
        v_end = v_start = av<<fp;


        int y_start = a.sy; //������� �� Y �� �������
        int y_end = c.sy < g3d.height ? c.sy : g3d.height; // ����� �� Y �� �������


        for(;y_start<y_end; y_start++,
                x_start += dx_start,
                u_start += du_start,
                v_start += dv_start,

                x_end += dx_end,
                u_end += du_end,
                v_end += dv_end) { //������ ��� �������������� �����

            if(y_start == b.sy) {
                if(c.sy == b.sy) return;
                tempI = b.sy - a.sy;
                x_start = (a.sx<<fpPosition) + dx_start * tempI; //������ �����
                u_start = (au<<fp) + du_start * tempI; //
                v_start = (av<<fp) + dv_start * tempI;//
                x_end = b.sx<<fpPosition; //
                u_end = bu<<fp; //
                v_end = bv<<fp;//����� �����
                tempI = c.sy - b.sy;                  //
                dx_end = ((c.sx - b.sx)<<fpPosition) / tempI;//
                du_end = ((cu - bu)<<fp) / tempI;   //
                dv_end = ((cv - bv)<<fp) / tempI;  //�������� ��� ����� �����
            }

            if(y_start<0) continue;

            if (x_start > x_end) {
                x1 = x_end >> fpPosition;
                u = u_end;
                v = v_end;
                x2 = x_start >> fpPosition;
            } else {
                x1 = x_start >> fpPosition;
                u = u_start;
                v = v_start;
                x2 = x_end >> fpPosition;
            }


            if(x1 < 0) {
                u -= du*x1;
                v -= dv*x1;
                x1 = 0;
            }





            if(x2 > g3d.width) x2 = g3d.width;

            tempI = g3d.width * y_start;
            x1 += tempI; //�� ���� �������� � �������
            x2 += tempI; //�� ���� �������� � ������

            u -= du;
            v -= dv;

        for(; x1 < x2; x1++) {
            col = tex[((((v += dv) >>> fp) << widthBIT) + ((u += du) >>> fp)) & lengthBIT];
            coord = ((((col & 0xff0000) - 0x800000) * scale) >> 24) + ((((col & 0xff00) - 0x8000) * scale) >> 16) * w + x1;
            if(coord < 0) rgb[x1] = rgb[0];
            else if(coord > g3dlen) rgb[x1] = rgb[g3dlen];
            else rgb[x1] = rgb[coord];
        }

    }
}




static final void paintFill(DirectX7 g3d,
            Vertex a,
            Vertex b,
            Vertex c,Texture texture,int au,int av) {
int col = texture.rImg.img[((av<<texture.rImg.widthBIT)+au)&(texture.rImg.img.length-1)];
paintFill(g3d,a,b,c,col);
}


static final void paintFill(DirectX7 g3d,
            Vertex a,
            Vertex b,
            Vertex c,int col) {



    //----------------------------
    //|          A               |
    //|        ****              |
    //|      *******             |
    //|    **********            |
    //|  B************           |
    //|     ***********          |
    //|        *********         |
    //|-----------@@@@@@@--------|
    //|              *****       |
    //|                 ***      |
    //|                    C     |
    //----------------------------
        //��������� �������
        if(b.sy < a.sy) {
            Vertex t = a; a = b; b = t;
        }
        if(c.sy < a.sy) {
            Vertex t = c; c = a; a = t;
        }
        if(c.sy < b.sy) {
            Vertex t = b; b = c; c = t;
        }
        if(a.sy == c.sy) return;



        final int[] rgb = g3d.display; //�������
        int x1, x2, tempI;

        tempI = c.sy - a.sy;                        // ����������
        final int dx_start = ((c.sx - a.sx)<<fpPosition) / tempI;//

        int dx_end=0;// ����������
        if(b.sy != a.sy) {
            tempI = b.sy - a.sy;
            dx_end = ((b.sx - a.sx)<<fpPosition) / tempI;
            }


        tempI = b.sy - a.sy;
        int x_start = (a.sx<<fpPosition) + dx_start * tempI; //������ �����

        int x_end = b.sx<<fpPosition; //����� �����

        tempI = (x_start-x_end) >> fpPosition;  // �������� ������
        if(tempI == 0) return;         //

        x_end = x_start = a.sx<<fpPosition;


        int y_start = a.sy; //������� �� Y �� �������
        int y_end = c.sy < g3d.height ? c.sy : g3d.height; // ����� �� Y �� �������
int xs,xe,dxe;
xs=xe=dxe=0;
fastFill(
        y_start, y_end, 
        x_start, x_end,
        dx_start, dx_end,
        a, b, c,
        g3d,
        tempI, rgb, col);
       
}

static final void fastFill(
        int y_start,final int y_end, 
        int x_start,int x_end,
        int dx_start,int dx_end,
        final Vertex a,final Vertex b,final Vertex c,
        final DirectX7 g3d,
        int tempI,final int[] rgb,final int col) {
int x1,x2;
for(;y_start<y_end; y_start++,
x_start += dx_start,
x_end += dx_end) {
    
if(y_start == b.sy) {
                if(c.sy == b.sy) return;
                x_start = (a.sx<<fpPosition) + dx_start * (b.sy - a.sy);
                x_end = b.sx<<fpPosition;
                dx_end = ((c.sx - b.sx)<<fpPosition) / (c.sy - b.sy);//
}
if(y_start<0) continue;

x1=(x_start > x_end)?x_end >> fpPosition:x_start >> fpPosition;
x2=(x_start > x_end)?x_start >> fpPosition:x_end >> fpPosition;

if(x1 < 0) x1 = 0;

if(x2 > g3d.width) x2 = g3d.width;

tempI = g3d.width * y_start;
x1 += tempI; //�� ���� �������� � �������
x2 += tempI; //�� ���� �������� � ������

while(x2-x1>=9) {
rgb[x1]=col;
rgb[x1+1]=col;
rgb[x1+2]=col;
rgb[x1+3]=col;
rgb[x1+4]=col;
rgb[x1+5]=col;
rgb[x1+6]=col;
rgb[x1+7]=col;
rgb[x1+8]=col;
x1+=9;
}
while(x2-x1>=4) {
rgb[x1]=col;
rgb[x1+1]=col;
rgb[x1+2]=col;
rgb[x1+3]=col;
x1+=4;
}

while(x1<x2) {
rgb[x1]=col;
x1++;
}



}
}

/*
    static final void fastFill(
            int y_start, final int y_end,
            int x_start, int x_end,
            int dx_start, int dx_end,
            final Vertex a, final Vertex b, final Vertex c,
            final DirectX7 g3d,
            int tempI, final int[] rgb, final int col) {
        int x1, x2;
        if (colorBuffer.length < g3d.width) {
            colorBuffer = new int[g3d.width];
        }
        
        for(int i=0;i<g3d.width;i++) {
            colorBuffer[i] = col;
        }
        
        for (;y_start < y_end; 
                y_start++, 
                x_start += dx_start, 
                x_end += dx_end) {

            if(y_start == b.sy) {
                if(c.sy == b.sy) return;
                x_start = (a.sx << fpPosition) + dx_start * (b.sy - a.sy);
                x_end = b.sx << fpPosition;
                dx_end = ((c.sx - b.sx) << fpPosition) / (c.sy - b.sy);//
            }
            if(y_start < 0) continue;

            x1 = (x_start > x_end) ? x_end >> fpPosition : x_start >> fpPosition;
            x2 = (x_start > x_end) ? x_start >> fpPosition : x_end >> fpPosition;

            if(x1 < 0) x1 = 0;
            if(x2 > g3d.width) x2 = g3d.width;
            
            
            if(x1 < x2) System.arraycopy(colorBuffer, 0, rgb, x1+g3d.width*y_start, x2-x1);
        }
    }
*/


static final void paintOverwrite(DirectX7 g3d,
            Vertex a,
            Vertex b,
            Vertex c) {



    //----------------------------
    //|          A               |
    //|        ****              |
    //|      *******             |
    //|    **********            |
    //|  B************           |
    //|     ***********          |
    //|        *********         |
    //|-----------@@@@@@@--------|
    //|              *****       |
    //|                 ***      |
    //|                    C     |
    //----------------------------
        //��������� �������
        if(b.sy < a.sy) {
            Vertex t = a; a = b; b = t;
        }
        if(c.sy < a.sy) {
            Vertex t = c; c = a; a = t;
        }
        if(c.sy < b.sy) {
            Vertex t = b; b = c; c = t;
        }
        if(a.sy == c.sy) return;



        final int[] rgb = g3d.display; //�������
        int x1, x2, tempI;

        tempI = c.sy - a.sy;                        // ����������
        final int dx_start = ((c.sx - a.sx)<<fpPosition) / tempI;//

        int dx_end=0;// ����������
        if(b.sy != a.sy) {
            tempI = b.sy - a.sy;
            dx_end = ((b.sx - a.sx)<<fpPosition) / tempI;
            }


        tempI = b.sy - a.sy;
        int x_start = (a.sx<<fpPosition) + dx_start * tempI; //������ �����

        int x_end = b.sx<<fpPosition; //����� �����

        tempI = (x_start-x_end) >> fpPosition;  // �������� ������
        if(tempI == 0) return;         //

        x_end = x_start = a.sx<<fpPosition;


        int y_start = a.sy; //������� �� Y �� �������
        int y_end = c.sy < g3d.height ? c.sy : g3d.height; // ����� �� Y �� �������
int xs,xe,dxe;
xs=xe=dxe=0;
fastOverwrite(
        y_start, y_end, 
        x_start, x_end,
        dx_start, dx_end,
        a, b, c,
        g3d,
        tempI, rgb);
       
}

static final void fastOverwrite(
        int y_start,final int y_end, 
        int x_start,int x_end,
        int dx_start,int dx_end,
        final Vertex a,final Vertex b,final Vertex c,
        final DirectX7 g3d,
        int tempI,final int[] rgb) {
int x1,x2;
for(;y_start<y_end; y_start++,
x_start += dx_start,
x_end += dx_end) {
    
if(y_start == b.sy) {
                if(c.sy == b.sy) return;
                x_start = (a.sx<<fpPosition) + dx_start * (b.sy - a.sy);
                x_end = b.sx<<fpPosition;
                dx_end = ((c.sx - b.sx)<<fpPosition) / (c.sy - b.sy);//
}
if(y_start<0) continue;

x1=(x_start > x_end)?x_end >> fpPosition:x_start >> fpPosition;
x2=(x_start > x_end)?x_start >> fpPosition:x_end >> fpPosition;

if(x1 < 0) x1 = 0;

if(x2 > g3d.width) x2 = g3d.width;

tempI = g3d.width * y_start;
x1 += tempI; //�� ���� �������� � �������
x2 += tempI; //�� ���� �������� � ������

while(x2-x1>=9) {
rgb[x1]+=1;
rgb[x1+1]+=1;
rgb[x1+2]+=1;
rgb[x1+3]+=1;
rgb[x1+4]+=1;
rgb[x1+5]+=1;
rgb[x1+6]+=1;
rgb[x1+7]+=1;
rgb[x1+8]+=1;
x1+=9;
}
while(x2-x1>=4) {
rgb[x1]+=1;
rgb[x1+1]+=1;
rgb[x1+2]+=1;
rgb[x1+3]+=1;
x1+=4;
}

while(x1<x2) {
rgb[x1]+=1;
x1++;
}



}
}
public static int sqr(int x) {
    return x*x;
}

   private static final int max(int a,int b,int c) {
       //its not max its min lol
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
   
}
