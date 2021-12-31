package code.Math;

import code.Rendering.DirectX7;
import code.Rendering.Vertex;

public final class MathUtils {

    public static int randomCall = 0;

public static int calcLight(int nx,int ny,int nz,int sx,int sy,int sz,int f) {

while(Math.abs(sx)>30000 || Math.abs(sy)>30000 || Math.abs(sz)>30000) {
sx/=2; sy/=2; sz/=2;    
}   
while(Math.abs(nx)>30000 || Math.abs(ny)>30000 || Math.abs(nz)>30000) {
nx/=2; ny/=2; nz/=2;    
}   
    
double nw=Math.sqrt((double)(nx*nx+ny*ny+nz*nz));//��� ������������

double lw=Math.sqrt((double)(sx*sx+sy*sy+sz*sz));//��� ������������

if(nw<1) nw=1;
if(lw<1) lw=1;

double l=((nx*sx)+(ny*sy)+(nz*sz))/(nw*lw);

return Math.min(Math.max((int)(l*256+f),0)*255/(255+f),255);
}

public static int calcLight(int nx,int ny,int nz,int sx,int sy,int sz) {

while(Math.abs(sx)>30000 || Math.abs(sy)>30000 || Math.abs(sz)>30000) {
sx/=2; sy/=2; sz/=2;    
}   
while(Math.abs(nx)>30000 || Math.abs(ny)>30000 || Math.abs(nz)>30000) {
nx/=2; ny/=2; nz/=2;    
}   
    
double nw=Math.sqrt((double)(nx*nx+ny*ny+nz*nz));//��� ������������

double lw=Math.sqrt((double)(sx*sx+sy*sy+sz*sz));//��� ������������

if(nw<1) nw=1;
if(lw<1) lw=1;

double l=((nx*sx)+(ny*sy)+(nz*sz))/(nw*lw);

return Math.min(Math.max((int)(l*256),0),255);
}

public static int microCalcLight(int nx,int ny,int nz,int sx,int sy,int sz) {
    while(sx > 30000 || sy > 30000 || sz > 30000 || sx < -30000 || sy < -30000 || sz < -30000) {
        sx >>= 1;
        sy >>= 1;
        sz >>= 1;
    }
    
    double lw = Math.sqrt((double) (sx * sx + sy * sy + sz * sz));//��� ������������
    
    if(lw < 1) lw=1;
    int vec = (int) (((nx * sx) + (ny * sy) + (nz * sz)) / (lw * 4096) * 255);
    
    if(vec > 255) return 255;
    return vec;
}

public static int getAnglez(int x,int y,int x1,int y1) {
      
int anglez=0;

int dist=Math.abs(x-x1)+Math.abs(y-y1);
if(dist==0) return 0;
int xx=(x-x1)*4000/dist;
int yy=(y-y1)*4000/dist;

if(xx<4000 && xx>0 && yy>0) xx=0;

if(xx>0) anglez+=xx*360;
if(xx<0) anglez-=xx*180;
if(yy>0) anglez+=yy*90;
if(yy<0) anglez-=yy*270;

anglez=anglez/4000;
anglez-=90;
anglez=fixDegree(anglez);

    return anglez;
} 
public static int getAnglezHQ(int x,int y,int x1,int y1) {
      
int anglez=0;

int dist=Math.abs(x-x1)+Math.abs(y-y1);
if(dist==0) return 0;

int xx=(x-x1)*4000/dist;
int yy=(y-y1)*4000/dist;

if(xx<4000 && xx>0 && yy>0) xx=0;

if(xx>0) anglez+=xx*2048;
if(xx<0) anglez-=xx*1024;
if(yy>0) anglez+=yy*512;
if(yy<0) anglez-=yy*1536;

anglez=anglez/4000;
anglez-=512;
anglez=fixDegree2(anglez);

    return anglez;
} 


    public static int fixDegree(int degree) {
        while(degree<0) degree+=360;
        while(degree>=360) degree-=360;
        return degree;
    }


    public static int fixDegree2(int degree) {
        while(degree<0) degree+=2048;
        while(degree>=2048) degree-=2048;
        return degree;
    }

    
    public static int mix(int a,int b,int i,int max) {
        if(i==0) return a;
        if(i==max) return b;
        
        return (a*(max-i)+b*i)/max;
    }
    
    public static int pLength(int x,int y,int z) {
        if(!DirectX7.useAutoWMove) return 0;
        return (int)Math.sqrt(x*x+y*y+z*z);
    }
    
    public static int pLength(int x,int y) {
        if(!DirectX7.useAutoWMove) return 0;
        return (int)Math.sqrt(x*x+y*y+(x+y)*(x+y)/4);
    }
    
    public static int angleDistance(int a,int b) {
        int min = Integer.MAX_VALUE;
        
        for(int i=-360;i<=360;i+=360) {
            min = Math.min(min,Math.abs(a+i-b));
        }
        
        return min;
    }
    
    public static int preudoRandom(long time, int val) {
        
        if(time<0) time*=-1;
        time += randomCall*255255;
        time%=val;
        randomCall++;
        return (int)time;
    }
    
    public static int round(double d) {
        int dd = (int)d;
        double last = Math.abs(d)-Math.abs(dd);
        int ad = d>0?1:-1;
        return dd+(last>=0.5?ad:0);
    }
    
    public static int ceil(double d) {
        int dd = (int)d;
        int ad = d>0?1:-1;
        return dd+(Math.floor(d)!=d?ad:0);
    }
}
