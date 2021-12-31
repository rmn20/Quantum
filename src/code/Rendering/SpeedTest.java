package code.Rendering;

import code.utils.Main;

public class SpeedTest {
    
    public final static int fp = 12, FP = 1<<fp;
    public final static int fpPosition = 14, FPPosition = 1<<fpPosition;
    //private static int du, dv, du2, dv2, du3, dv3, du4, dv4, du5, dv5, du6, dv6;
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
        int du, dv;
        int dus = (u_start-u_end)/tempI; //
        int dvs = (v_start-v_end)/tempI; //
        


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
                du = dus; dv = dvs;
                if(x2!=x1) {
                    du = (u_start - u_end)/(x2-x1);
                    dv = (v_start - v_end)/(x2-x1);
                }
            } else {
                x1 = x_start >> fpPosition;
                u = u_start;
                v = v_start;
                x2 = x_end >> fpPosition;
                du = dus; dv = dvs;
                if(x2!=x1) {
                    du = (u_end - u_start)/(x2-x1);
                    dv = (v_end - v_start)/(x2-x1);
                }
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
if(Main.s60Optimization>-1) {
while(x2-x1>=6) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) rgb[x1]=col; 
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) rgb[x1+1]=col; 
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) rgb[x1+2]=col; 
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) rgb[x1+3]=col; 
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) rgb[x1+4]=col; 
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) rgb[x1+5]=col; 
x1+=6;
}
}
while(x1<x2) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) rgb[x1]=col; 
x1++;
}




}

}
    
    
/*
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
        int csy = c.sy, bsy = b.sy, asy = a.sy; //cause its faster
        if(asy == csy) return;
        int csx = c.sx, bsx = b.sx, asx = a.sx; //cause its faster
        int w = g3d.width;

        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������
        int x1, x2, u, v, tempI;
        tempI = csy-asy;

        final int dx_start = ((csx - asx)<<fpPosition) / tempI;//
        final int du_start = ((cu - au)<<fp) / tempI; //
        final int dv_start = ((cv - av)<<fp) / tempI;//

        int dx_end=0, du_end=0, dv_end=0; // ����������
        if(bsy != asy) {
            tempI = bsy-asy;

            dx_end = ((bsx - asx)<<fpPosition) / tempI;
            du_end = ((bu - au)<<fp) / tempI;
            dv_end = ((bv - av)<<fp) / tempI;
        }


        tempI = bsy - asy;
        int x_start = (asx<<fpPosition) + dx_start * tempI; //������ �����
        int u_start = (au<<fp) + du_start * tempI; //
        int v_start = (av<<fp) + dv_start * tempI;//

        tempI = (x_start-(bsx<<fpPosition)) >> fpPosition;  // �������� ������
        if(tempI == 0) return;         //
        
        du = (u_start-(bu<<fp))/tempI; du2 = du<<1; du3 = du2+du; du4 = du2<<1; du5 = du4+du; du6 = du3<<1;
        dv = (v_start-(bv<<fp))/tempI; dv2 = dv<<1; dv3 = dv2+dv; dv4 = dv2<<1; dv5 = dv4+dv; dv6 = dv3<<1;
        
        int x_end = x_start = asx<<fpPosition;
        int u_end = u_start = au<<fp;
        int v_end = v_start = av<<fp;


        int y_start = asy ; //������� �� Y �� �������
        int y_end = csy < g3d.height ? c.sy : g3d.height; // ����� �� Y �� �������
        
        tempI = bsy - asy;
        int max = csy - asy;
        int casx = (asx * (max - tempI) + csx * tempI) / max;
        if(y_start<0) {
            if(bsy<0) {
                tempI = bsy - y_start;
                y_start+=tempI;
                x_start += dx_start*tempI;
                u_start += du_start*tempI;
                v_start += dv_start*tempI;
                x_end += dx_end*tempI;
                u_end += du_end*tempI;
                v_end += dv_end*tempI;
                
                if(csy == bsy) return;
                tempI = bsy - asy;
                x_start = (asx<<fpPosition) + dx_start * tempI; //начало линии
                u_start = (au<<fp) + du_start * tempI; //
                v_start = (av<<fp) + dv_start * tempI;//
                x_end = bsx<<fpPosition; //
                u_end = bu<<fp; //
                v_end = bv<<fp;//конец линии
                tempI = csy - bsy;                  //
                dx_end = ((csx - bsx)<<fpPosition) / tempI;//
                du_end = ((cu - bu)<<fp) / tempI;   //
                dv_end = ((cv - bv)<<fp) / tempI;  //�������� ��� ����� �����
            }
            tempI = -y_start;
            y_start+=tempI;
            x_start += dx_start*tempI;
            u_start += du_start*tempI;
            v_start += dv_start*tempI;
            x_end += dx_end*tempI;
            u_end += du_end*tempI;
            v_end += dv_end*tempI;
        }
        int bsyW = bsy * w;
        y_start *= w; y_end*=w;
        
        if(bsx>=casx) {
            cycleNorm(y_start, y_end, w, x_start, u_start, v_start,
            dx_start, du_start, dv_start,
            x_end, u_end, v_end, dx_end, du_end, dv_end, bsyW,
            rgb, tex, widthBIT,
            a, au, av,
            b, bu, bv,
            c, cu, cv);
    } else {
            cycleInv(y_start, y_end, w, x_start, u_start, v_start,
            dx_start, du_start, dv_start,
            x_end, u_end, v_end, dx_end, du_end, dv_end, bsyW,
            rgb, tex, widthBIT,
            a, au, av,
            b, bu, bv,
            c, cu, cv);
    }

}
    
    private static void cycleNorm(int y_start, final int y_end, final int w, int x_start, int u_start, int v_start,
            final int dx_start, final int du_start, final int dv_start,
            int x_end, int u_end, int v_end, int dx_end, int du_end, int dv_end, int bsyW,
            final int[] rgb, final int[] tex, final int widthBIT,
            final Vertex a, final int au, final int av,
            final Vertex b, final int bu, final int bv,
            final Vertex c, final int cu, final int cv) {
        int x1, x2, u, v, tempI;
        int csy = c.sy, bsy = b.sy, asy = a.sy; //cause its faster
        if(asy == csy) return;
        int csx = c.sx, bsx = b.sx, asx = a.sx; //cause its faster
        for(;y_start<y_end; y_start+=w,
                    x_start += dx_start,
                    u_start += du_start,
                    v_start += dv_start,

                    x_end += dx_end,
                    u_end += du_end,
                    v_end += dv_end) { //рисуем все горизонтальные линии
                
                if(y_start == bsyW) {
                    if(csy == bsy) return;
                    tempI = bsy - asy;
                    x_start = (asx<<fpPosition) + dx_start * tempI; //начало линии
                    u_start = (au<<fp) + du_start * tempI; //
                    v_start = (av<<fp) + dv_start * tempI;//
                    x_end = bsx<<fpPosition; //
                    u_end = bu<<fp; //
                    v_end = bv<<fp;//конец линии
                    tempI = csy - bsy;                  //
                    dx_end = ((csx - bsx)<<fpPosition) / tempI;//
                    du_end = ((cu - bu)<<fp) / tempI;   //
                    dv_end = ((cv - bv)<<fp) / tempI;  //�������� ��� ����� �����
                }
                
                x1 = x_start >> fpPosition;
                u = u_start;
                v = v_start;
                x2 = x_end >> fpPosition;
                
                if(x1 < 0) {
                    u -= du*x1;
                    v -= dv*x1;
                    x1 = 0;
                }
                
                if(x2 > w) x2 = w;
                x1+=y_start; x2+=y_start;
                
                if(Main.s60Optimization>-1) {
            while(x2-x1>=6) {
                rgb[x1]=tex[((v>>>fp<<widthBIT) + (u>>>fp)) ];
                rgb[x1+1]=tex[(((v+dv>>>fp)<<widthBIT) + (u+du>>>fp)) ];
                rgb[x1+2]=tex[(((v+dv2>>>fp)<<widthBIT) + (u+du2>>>fp)) ];
                rgb[x1+3]=tex[(((v+dv3>>>fp)<<widthBIT) + (u+du3>>>fp)) ];
                rgb[x1+4]=tex[(((v+dv4>>>fp)<<widthBIT) + (u+du4>>>fp)) ];
                rgb[x1+5]=tex[(((v+dv5>>>fp)<<widthBIT) + (u+du5>>>fp)) ];
                x1+=6; u+=du6; v+=dv6;
            }
        }
        u-=du;
        v-=dv;
        while(x1<x2) {
            rgb[x1]=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp))]; x1++;
        }
            }
    }
    private static void cycleInv(int y_start, final int y_end, final int w, int x_start, int u_start, int v_start,
            final int dx_start, final int du_start, final int dv_start,
            int x_end, int u_end, int v_end, int dx_end, int du_end, int dv_end, int bsyW,
            final int[] rgb, final int[] tex, final int widthBIT,
            final Vertex a, final int au, final int av,
            final Vertex b, final int bu, final int bv,
            final Vertex c, final int cu, final int cv) {
        int x1, x2, u, v, tempI;
        int csy = c.sy, bsy = b.sy, asy = a.sy; //cause its faster
        if(asy == csy) return;
        int csx = c.sx, bsx = b.sx, asx = a.sx; //cause its faster
        for(;y_start<y_end; y_start+=w,
                    x_start += dx_start,
                    u_start += du_start,
                    v_start += dv_start,

                    x_end += dx_end,
                    u_end += du_end,
                    v_end += dv_end) { //рисуем все горизонтальные линии
                
                if(y_start == bsyW) {
                    if(csy == bsy) return;
                    tempI = bsy - asy;
                    x_start = (asx<<fpPosition) + dx_start * tempI; //начало линии
                    u_start = (au<<fp) + du_start * tempI; //
                    v_start = (av<<fp) + dv_start * tempI;//
                    x_end = bsx<<fpPosition; //
                    u_end = bu<<fp; //
                    v_end = bv<<fp;//конец линии
                    tempI = csy - bsy;                  //
                    dx_end = ((csx - bsx)<<fpPosition) / tempI;//
                    du_end = ((cu - bu)<<fp) / tempI;   //
                    dv_end = ((cv - bv)<<fp) / tempI;  //�������� ��� ����� �����
                }
                
                x1 = x_end >> fpPosition;
                u = u_end;
                v = v_end;
                x2 = x_start >> fpPosition;
                
                if(x1 < 0) {
                    u -= du*x1;
                    v -= dv*x1;
                    x1 = 0;
                }
                
                if(x2 > w) x2 = w;
                x1+=y_start; x2+=y_start;
                
                if(Main.s60Optimization>-1) {
            while(x2-x1>=6) {
                rgb[x1]=tex[((v>>>fp<<widthBIT) + (u>>>fp)) ];
                rgb[x1+1]=tex[(((v+dv>>>fp)<<widthBIT) + (u+du>>>fp)) ];
                rgb[x1+2]=tex[(((v+dv2>>>fp)<<widthBIT) + (u+du2>>>fp)) ];
                rgb[x1+3]=tex[(((v+dv3>>>fp)<<widthBIT) + (u+du3>>>fp)) ];
                rgb[x1+4]=tex[(((v+dv4>>>fp)<<widthBIT) + (u+du4>>>fp)) ];
                rgb[x1+5]=tex[(((v+dv5>>>fp)<<widthBIT) + (u+du5>>>fp)) ];
                x1+=6; u+=du6; v+=dv6;
            }
        }
        u-=du;
        v-=dv;
        while(x1<x2) {
            rgb[x1]=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp))]; x1++;
        }
            }
    }*/
    /*
    private static void drawLine(int x1, final int x2, 
            final int[] rgb, final int[] tex, 
            int u, int v, 
            final int widthBIT) {
        
        if(Main.s60Optimization>-1) {
            while(x2-x1>=6) {
                rgb[x1]=tex[((v>>>fp<<widthBIT) + (u>>>fp)) ];
                rgb[x1+1]=tex[(((v+dv>>>fp)<<widthBIT) + (u+du>>>fp)) ];
                rgb[x1+2]=tex[(((v+dv2>>>fp)<<widthBIT) + (u+du2>>>fp)) ];
                rgb[x1+3]=tex[(((v+dv3>>>fp)<<widthBIT) + (u+du3>>>fp)) ];
                rgb[x1+4]=tex[(((v+dv4>>>fp)<<widthBIT) + (u+du4>>>fp)) ];
                rgb[x1+5]=tex[(((v+dv5>>>fp)<<widthBIT) + (u+du5>>>fp)) ];
                x1+=6; u+=du6; v+=dv6;
            }
        }
        u-=du;
        v-=dv;
        while(x1<x2) {
            rgb[x1]=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp))]; x1++;
        }
    }*/
    
    /*
    
        
        if(y_start<0) {
            if(bsy<0) {
                tempI = bsy - y_start;
                y_start+=tempI;
                x_start += dx_start*tempI;
                u_start += du_start*tempI;
                v_start += dv_start*tempI;
                x_end += dx_end*tempI;
                u_end += du_end*tempI;
                v_end += dv_end*tempI;
                
                if(csy == bsy) return;
                tempI = bsy - asy;
                x_start = (asx<<fpPosition) + dx_start * tempI; //начало линии
                u_start = (au<<fp) + du_start * tempI; //
                v_start = (av<<fp) + dv_start * tempI;//
                x_end = bsx<<fpPosition; //
                u_end = bu<<fp; //
                v_end = bv<<fp;//конец линии
                tempI = csy - bsy;                  //
                dx_end = ((csx - bsx)<<fpPosition) / tempI;//
                du_end = ((cu - bu)<<fp) / tempI;   //
                dv_end = ((cv - bv)<<fp) / tempI;  //�������� ��� ����� �����
            }
            tempI = -y_start;
            y_start+=tempI;
            x_start += dx_start*tempI;
            u_start += du_start*tempI;
            v_start += dv_start*tempI;
            x_end += dx_end*tempI;
            u_end += du_end*tempI;
            v_end += dv_end*tempI;
        }
    */
    
}
