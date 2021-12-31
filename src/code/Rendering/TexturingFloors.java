package code.Rendering;

import code.utils.Main;

/**
 *
 * @author Roman Lahin
 */
public class TexturingFloors {
   public final static int fp = 12, FP = 1<<fp;
   
   
   static final void paintFloor(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            final int dx_start,final int dwz_start,final int duz_start,final int dvz_start,
            final int dx_end, final int dwz_end, final int duz_end, final int dvz_end,
            final int dwz, final int duz, final int dvz,int fogc,int sz2) {

    
        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������

        int tempI;
        long subtexel_a, subtexel_b;
        int uz_a, uz_b, vz_a, vz_b, wz_a, wz_b; // ���� � ����� �������
        int x1, x2, length;
        int u, v, du, dv;
        int u_2, v_2; //����� �����
        final int duz4=duz*sz2;
        final int dvz4=dvz*sz2;
        final int dwz4=dwz*sz2;
        du = dv = 0;
        int col = 0;
        int x_begin;
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
if(length!=0) {
            u = (int) (((long) uz_a << fp) / wz_a);
            v = (int) (((long) vz_a << fp) / wz_a);
            du = ((int) (((long) uz_b << fp) / wz_b) - u)  / length;
            dv = ((int) (((long) vz_b << fp) / wz_b) - v)  / length;

while(x2-x1>=7) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) rgb[x1]=col; 
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) rgb[x1+1]=col; 
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) rgb[x1+2]=col; 
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) rgb[x1+3]=col; 
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) rgb[x1+4]=col; 
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) rgb[x1+5]=col; 
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) rgb[x1+6]=col; 
x1+=7;
}
                while(x1<x2) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) rgb[x1]=col; 
x1++;
                }
}
                



 //Paste here
        }
    }
   
   static final void paintFloor_1(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            final int dx_start,final int dwz_start,final int duz_start,final int dvz_start,
            final int dx_end, final int dwz_end, final int duz_end, final int dvz_end,
            final int dwz, final int duz, final int dvz,int sz2) {

    
        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������

        int tempI;
        long subtexel_a, subtexel_b;
        int uz_a, uz_b, vz_a, vz_b, wz_a, wz_b; // ���� � ����� �������
        int x1, x2, length;
        int u, v, du, dv;
        int u_2, v_2; //����� �����
int col;
int x_begin;
int colf =sz2&0xFEFEFE; //set some lsb to 0
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
if(length!=0) {
            u = (int) (((long) uz_a << fp) / wz_a);
            v = (int) (((long) vz_a << fp) / wz_a);

            du = ((int) (((long) uz_b << fp) / wz_b) - u)  / length;
            dv = ((int) (((long) vz_b << fp) / wz_b) - v)  / length;

                if(Main.s60Optimization == 1) {
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
                    
                } else if(Main.s60Optimization == 0) {
                
                while(x2-x1>=2) {
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1]=(col=(col&0xFEFEFE)+colf)|((( col>>>8 ) &0x010101)*0xFF);
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1+1]=(col=(col&0xFEFEFE)+colf)|((( col>>>8 ) &0x010101)*0xFF);
x1+=2;
                }
                }
                while(x1<x2) {
if((col=tex[((((v += dv)>>>fp)<<widthBIT) + ((u += du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1]=(col=(col&0xFEFEFE)+colf)|((( col>>>8 ) &0x010101)*0xFF);
x1+=1;
}
}
                



 //Paste here
        }
    }
   
   
   
   static final void paintFloor_2(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            final int dx_start,final int dwz_start,final int duz_start,final int dvz_start,
            final int dx_end, final int dwz_end, final int duz_end, final int dvz_end,
            final int dwz, final int duz, final int dvz) {

    
        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������

        int tempI;
        long subtexel_a, subtexel_b;
        int uz_a, uz_b, vz_a, vz_b, wz_a, wz_b; // ���� � ����� �������
        int x1, x2, length;
        int u, v, du, dv;
        int u_2, v_2; //����� �����
int col;
int x_begin;
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
if(length!=0) {
            u = (int) (((long) uz_a << fp) / wz_a);
            v = (int) (((long) vz_a << fp) / wz_a);

            du = ((int) (((long) uz_b << fp) / wz_b) - u)  / length;
            dv = ((int) (((long) vz_b << fp) / wz_b) - v)  / length;

                if(Main.s60Optimization == 1) {
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
                    
                } else if(Main.s60Optimization == 0) {
                
                while(x2-x1>=2) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1]=(col=(col&0xFEFEFE)+(rgb[x1]&0xFEFEFE))|((( col>>>8 ) &0x010101)*0xFF);
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1+1]=(col=(col&0xFEFEFE)+(rgb[x1+1]&0xFEFEFE))|((( col>>>8 ) &0x010101)*0xFF);
x1+=2;
                }
                }
                while(x1<x2) {
if((col=tex[((((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp)) &lengthBIT]) != 0) 
    rgb[x1]=(col=(col&0xFEFEFE)+(rgb[x1]&0xFEFEFE))|((( col>>>8 ) &0x010101)*0xFF);
x1+=1;
}
}
                



 //Paste here
        }
    }
   
   
   static final void paintFloor_3(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            final int dx_start,final int dwz_start,final int duz_start,final int dvz_start,
            final int dx_end, final int dwz_end, final int duz_end, final int dvz_end,
            final int dwz, final int duz, final int dvz,int fogc,int sz2,Vertex a,Vertex b,Vertex c,
            int f_start,int f_end,
final int df_start,final int df_end,
final int df
) {

    
        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������

        int tempI;
        long subtexel_a, subtexel_b;
        int uz_a, uz_b, vz_a, vz_b, wz_a, wz_b; // ���� � ����� �������
        int x1, x2, length;
        int u, v, du, dv;
        int u_2, v_2; //����� �����
        du = dv = 0;
        int col,ca;
if(DirectX7.fDist/255!=0) {
sz2=-TexturingPers.min(a.rz,b.rz,c.rz)/(DirectX7.fDist/255);
}
if(sz2>255) sz2=255;
if(sz2<0) sz2=0;
int isz2=(255-sz2);
int f2=( fogc&0xFF00FF)*sz2;
int f22=(fogc&0x00FF00)*sz2;
int x_begin;
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
if(length!=0) {
            u = (int) (((long) uz_a << fp) / wz_a);
            v = (int) (((long) vz_a << fp) / wz_a);
            u_2 = (int) (((long) uz_b << fp) / wz_b);
            v_2 = (int) (((long) vz_b << fp) / wz_b);

            du = (u_2 - u)  / length;
            dv = (v_2 - v)  / length;

                if(Main.s60Optimization == 1) {
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
                    
                } else if(Main.s60Optimization == 0) {
                
                while(x2-x1>=3) {
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
x1+=3;
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
                



 //Paste here
        }
    }
   
    
   
   static final void paintFloor_4(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            final int dx_start,final int dwz_start,final int duz_start,final int dvz_start,
            final int dx_end, final int dwz_end, final int duz_end, final int dvz_end,
            final int dwz, final int duz, final int dvz) {

    
        final int[] tex = texture.rImg.img;
        final int lengthBIT = tex.length-1;
        final int widthBIT = texture.rImg.widthBIT;
        final int[] rgb = g3d.display; //�������

        int tempI;
        long subtexel_a, subtexel_b;
        int uz_a, uz_b, vz_a, vz_b, wz_a, wz_b; // ���� � ����� �������
        int x1, x2, length;
        int u, v, du, dv;
        int u_2, v_2; //����� �����
        du = dv = 0;
int col, ca, cols;
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
if(length!=0) {
            u = (int) (((long) uz_a << fp) / wz_a);
            v = (int) (((long) vz_a << fp) / wz_a);
            du = ((int) (((long) uz_b << fp) / wz_b) - u)  / length;
            dv = ((int) (((long) vz_b << fp) / wz_b) - v)  / length;

                if(Main.s60Optimization == 1) {
                while(x2-x1>=3) {
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
x1+=3;
}    
                } else if(Main.s60Optimization == 0) {
                
                while(x2-x1>=2) {
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
x1+=2;
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

 //Paste here
        }
    }
   
   static final void paintFloor_3_HQ(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            final int dx_start, final int dwz_start, final int duz_start, final int dvz_start,
            final int dx_end, final int dwz_end, final int duz_end, final int dvz_end,
            final int dwz, final int duz, final int dvz,Vertex a,Vertex b,Vertex c,
            int f_start,int f_end,
final int df_start,final int df_end,
final int df
) {


    
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
int col,f,c1,isz,isz2,c2;
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
            if(length > 0) { // ������������ ���������� �������
            u = (int) (((long) uz_a << fp) / wz_a); //������� ���������� � ��������
            v = (int) (((long) vz_a << fp) / wz_a); //

                du = ((int) (((long) uz_b << fp) / wz_b) - u) / length;
                dv = ((int) (((long) vz_b << fp) / wz_b) - v) / length;
isz=0xFF-(isz2=f>>>fp);
c1=(fogcolor)*isz;
c2=(fogcolor2)*isz;
while(x2-x1>=2)
{
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + (((u+=du))>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1]=(   
            (  ( (col&0xFF00FF)*isz2 + c1)  &0xFF00FF00  )| 
            (  ( (col&0x00FF00)*isz2 + c2)  &0x00FF0000  )   
            ) >>>8;
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + (((u+=du))>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1+1]=(   
            (  ( (col&0xFF00FF)*isz2 + c1)  &0xFF00FF00  )| 
            (  ( (col&0x00FF00)*isz2 + c2)  &0x00FF0000  )   
            ) >>>8;
x1+=2;
}
while(x1<x2)
{
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + (((u+=du))>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1]=(   
            (  ( (col&0xFF00FF)*isz2 + c1)  &0xFF00FF00  )| 
            (  ( (col&0x00FF00)*isz2 + c2)  &0x00FF0000  )   
            ) >>>8;
x1++;
}
}






 //Paste here
        }
    }
    
    static final void paintFloor_6_HQ(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            final int dx_start, final int dwz_start, final int duz_start, final int dvz_start,
            final int dx_end, final int dwz_end, final int duz_end, final int dvz_end,
            final int dwz, final int duz, final int dvz,Vertex a,Vertex b,Vertex c,
            int f_start,int f_end,
final int df_start,final int df_end,
int df
) {


    
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
int col,f;
        if(df>0) df-=1;
        if(df<0) df+=1;

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
            f = f_start;

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
            if(length > 0) { // ������������ ���������� �������
            u = (int) (((long) uz_a << fp) / wz_a); //������� ���������� � ��������
            v = (int) (((long) vz_a << fp) / wz_a); //

                du = ((int) (((long) uz_b << fp) / wz_b) - u) / length;
                dv = ((int) (((long) vz_b << fp) / wz_b) - v) / length;
int isz2=f>>fp;
if(isz2<0) isz2=0;
while(x2-x1>=2)
{
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1]=(   
        (  ( (col&0xFF00FF)* isz2)  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)* isz2)  &0x00FF0000  )   
        ) >>>8;
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1+1]=(   
        (  ( (col&0xFF00FF)* isz2)  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)* isz2)  &0x00FF0000  )   
        ) >>>8;
x1+=2;
}
while(x1<x2)
{
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1]=(   
        (  ( (col&0xFF00FF)* isz2)  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)* isz2)  &0x00FF0000  )   
        ) >>>8;
x1++;
}
}






 //Paste here
        }
    }
    
    static final void paintFloor_9_HQ(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            final int dx_start, final int dwz_start, final int duz_start, final int dvz_start,
            final int dx_end, final int dwz_end, final int duz_end, final int dvz_end,
            final int dwz, final int duz, final int dvz,Vertex a,Vertex b,Vertex c,
            int f_start,int f_end,
final int df_start,final int df_end,
int df
) {


    
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
        int col,f,isz2;
        if(df>0) df-=1;
        if(df<0) df+=1;

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
            if(f<0) f=0;
            int df2=df;
            if(f+df*length<0) df2=(0-f)/length;


            if(wz_a == 0) return;
            if(length > 0) { // ������������ ���������� �������
                
            du = ((int) (((long) uz_b << fp) / wz_b) - (u = (int) (((long) uz_a << fp) / wz_a))) / length;
            dv = ((int) (((long) vz_b << fp) / wz_b) - (v = (int) (((long) vz_a << fp) / wz_a))) / length;
while(x2-x1>=2)
{
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1]=(   
        (  ( (col&0xFF00FF)*(isz2=(f+=df2)>>>fp))  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)* isz2)                &0x00FF0000  )   
        ) >>>8;
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1+1]=(   
        (  ( (col&0xFF00FF)*(isz2=(f+=df2)>>>fp))  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)* isz2)                &0x00FF0000  )   
        ) >>>8;
x1+=2;
}
while(x1<x2)
{
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) 
    rgb[x1]=(   
        (  ( (col&0xFF00FF)*(isz2=(f+=df2)>>>fp))  &0xFF00FF00  )| 
        (  ( (col&0x00FF00)* isz2)                &0x00FF0000  )   
        ) >>>8;
x1++;
}
}






 //Paste here
        }
    }
    
    
    
    static final void paintFloor_13(final DirectX7 g3d, final Texture texture, int y_start, final int y_end,
            int x_start, int wz_start, int uz_start, int vz_start,
            int x_end, int wz_end, int uz_end, int vz_end,
            int dx_start, int dwz_start, int duz_start, int dvz_start,
            int dx_end, int dwz_end, int duz_end, int dvz_end,
            final int dwz, final int duz, final int dvz,Vertex a,Vertex b,Vertex c,
            int r_start,int r_end,int dr_start,int dr_end,int dr,
            int g_start,int g_end,int dg_start,int dg_end,int dg,
            int b_start,int b_end,int db_start,int db_end,int db
) {


    
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
        int col,rC,gC,bC;
        int rT,gT,bT;
        
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
            int dr2=dr;
            int dg2=dg;
            int db2=db;
            if(rC+dr*length<0) dr2=(0-rC)/length;
            if(gC+dg*length<0) dg2=(0-gC)/length;
            if(bC+db*length<0) db2=(0-bC)/length;


            if(wz_a == 0) return;
            if(length > 0) { // ������������ ���������� �������
            du = ((int) (((long) uz_b << fp) / wz_b) - (u = (int) (((long) uz_a << fp) / wz_a))) / length;
            dv = ((int) (((long) vz_b << fp) / wz_b) - (v = (int) (((long) vz_a << fp) / wz_a))) / length;
            if(Main.s60Optimization>0) {
while(x2-x1>=2)
{
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) rgb[x1]=
        (
        (  ( (col&0xFF0000)*((rC+=dr2)>>>fp))  &0xFF000000) | 
        (  ( (col&0x00FF00)*((gC+=dg2)>>>fp))  &0x00FF0000) |
        (  ( (col&0x0000FF)*((bC+=db2)>>>fp))  ) 
        ) >>>8;
if((col= tex[ ( (((v+=dv)>>>fp)<<widthBIT) + ((u+=du)>>>fp) ) & lengthBIT ])  != 0) rgb[x1+1]=
        (
        (  ( (col&0xFF0000)*((rC+=dr2)>>>fp))  &0xFF000000) | 
        (  ( (col&0x00FF00)*((gC+=dg2)>>>fp))  &0x00FF0000) |
        (  ( (col&0x0000FF)*((bC+=db2)>>>fp))  ) 
        ) >>>8;
x1+=2;
}
            }
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
}
