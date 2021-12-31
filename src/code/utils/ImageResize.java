package code.utils;

/**
 *
 * @author DDDEN!SSS
 */
import javax.microedition.lcdui.Image;

public class ImageResize {

    public static Image createImage(String file, int w, int h) {
        try {
            return bilinearResizeImage(Image.createImage(file), w, h);
        } catch (Exception var2) {
            System.out.println("ERROR create image " + file);
            return null;
        }
    }
    
    public static Image createImage(String file, float w, float h) {
        try {
            return bilinearScaleImage(Image.createImage(file), w, h);
        } catch (Exception var2) {
            System.out.println("ERROR create image " + file);
            return null;
        }
    }
    
    public static Image createImage(Image file, int w, int h) {
        return bilinearResizeImage(file, w, h);
    }
    
    public static Image createImage(Image file, float w, float h) {
        return bilinearScaleImage(file, w, h);
    }
    
    public static Image createImageProportional(String file, int w, int h) {
        try {
            if (w < h) {
                return bilinearResizeImage(Image.createImage(file), w, w);
            } else {
                return bilinearResizeImage(Image.createImage(file), h, h);
            }

        } catch (Exception var2) {
            System.out.println("ERROR create image " + file);
            return null;
        }
    }
    
    public static Image createImageProportional(String file, float w, float h) {
        try {
            if (w < h) {
                return bilinearScaleImage(Image.createImage(file), w, w);
            } else {
                return bilinearScaleImage(Image.createImage(file), h, h);
            }

        } catch (Exception var2) {
            System.out.println("ERROR create image " + file);
            return null;
        }
    }
    
    public static Image bilinearScaleImage(Image img, float scaleX, float scaleY) {
        return bilinearResizeImage( img, (int)(img.getWidth()*scaleX), (int)(img.getHeight()*scaleY) );
    }

    public static Image bilinearResizeImage(Image img, int nw, int nh) {
        if (img.getWidth() == nw && img.getHeight() == nh) return img;
        int[] src = new int[img.getWidth() * img.getHeight()];
        img.getRGB(src, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
        int[] dest = new int[nw * nh];
        bilinearResizeRGB(src, dest, img.getWidth(), img.getHeight(), nw, nh, true);
        return Image.createRGBImage(dest, nw, nh, true);
    }
    public static void bilinearResizeRGB(int[] srcPixels, int[] destPixels, int srcw, int srch, int destw, int desth, boolean alpha) {
        final int FP_SHIFT = 16;
	final int FP_MASK = (1 << FP_SHIFT) - 1;


        int x, y, rx, ry, ix, iy, fx, fy;
        int idx1, idx2, idx3, idx4;
        int a, r, g, b;

        try {
            if (alpha) {
                for (x = 0; x < destw; x++) {
                    for (y = 0; y < desth; y++) {
                        rx = (x << FP_SHIFT) / destw * srcw;
                        ry = (y << FP_SHIFT) / desth * srch;

                        ix = rx >>> FP_SHIFT;
                        iy = ry >>> FP_SHIFT;

                        fx = rx & FP_MASK;
                        fy = ry & FP_MASK;

                        idx1 = idx2 = idx3 = idx4 = ix + iy * srcw;

                        if (ix < srcw - 1) {
                            idx2++;
                            idx4++;
                        }

                        if (iy < srch - 1) {
                            idx3 += srcw;
                            idx4 += srcw;
                        }
                        
                        a = (((((srcPixels[idx1] >> 24) & 0xFF) * (FP_MASK - fx) + ((srcPixels[idx2] >> 24) & 0xFF) * fx) >>> FP_SHIFT) * (FP_MASK - fy) + ((((srcPixels[idx3] >> 24) & 0xFF) * (FP_MASK - fx) + ((srcPixels[idx4] >> 24) & 0xFF) * fx) >>> FP_SHIFT) * fy) >>> FP_SHIFT;
                        r = (((((srcPixels[idx1] >> 16) & 0xFF) * (FP_MASK - fx) + ((srcPixels[idx2] >> 16) & 0xFF) * fx) >>> FP_SHIFT) * (FP_MASK - fy) + ((((srcPixels[idx3] >> 16) & 0xFF) * (FP_MASK - fx) + ((srcPixels[idx4] >> 16) & 0xFF) * fx) >>> FP_SHIFT) * fy) >>> FP_SHIFT;
                        g = (((((srcPixels[idx1] >> 8) & 0xFF) * (FP_MASK - fx) + ((srcPixels[idx2] >> 8) & 0xFF) * fx) >>> FP_SHIFT) * (FP_MASK - fy) + ((((srcPixels[idx3] >> 8) & 0xFF) * (FP_MASK - fx) + ((srcPixels[idx4] >> 8) & 0xFF) * fx) >>> FP_SHIFT) * fy) >>> FP_SHIFT;
                        b = ((((srcPixels[idx1] & 0xFF) * (FP_MASK - fx) + (srcPixels[idx2] & 0xFF) * fx) >>> FP_SHIFT) * (FP_MASK - fy) + (((srcPixels[idx3] & 0xFF) * (FP_MASK - fx) + (srcPixels[idx4] & 0xFF) * fx) >>> FP_SHIFT) * fy) >>> FP_SHIFT;

                        destPixels[x + y * destw] = (a << 24) | (r << 16) | (g << 8) | b;
                    }
                }
            } else {
                for (x = 0; x < destw; x++) {
                    for (y = 0; y < desth; y++) {
                        rx = (x << FP_SHIFT) / destw * srcw;
                        ry = (y << FP_SHIFT) / desth * srch;

                        ix = rx >>> FP_SHIFT;
                        iy = ry >>> FP_SHIFT;

                        fx = rx & FP_MASK;
                        fy = ry & FP_MASK;

                        idx1 = idx2 = idx3 = idx4 = ix + iy * srcw;

                        if (ix < srcw - 1) {
                            idx2++;
                            idx4++;
                        }

                        if (iy < srch - 1) {
                            idx3 += srcw;
                            idx4 += srcw;
                        }

                        r = (((((srcPixels[idx1] >> 16) & 0xFF) * (FP_MASK - fx) + ((srcPixels[idx2] >> 16) & 0xFF) * fx) >>> FP_SHIFT) * (FP_MASK - fy) + ((((srcPixels[idx3] >> 16) & 0xFF) * (FP_MASK - fx) + ((srcPixels[idx4] >> 16) & 0xFF) * fx) >>> FP_SHIFT) * fy) >>> FP_SHIFT;
                        g = (((((srcPixels[idx1] >> 8) & 0xFF) * (FP_MASK - fx) + ((srcPixels[idx2] >> 8) & 0xFF) * fx) >>> FP_SHIFT) * (FP_MASK - fy) + ((((srcPixels[idx3] >> 8) & 0xFF) * (FP_MASK - fx) + ((srcPixels[idx4] >> 8) & 0xFF) * fx) >>> FP_SHIFT) * fy) >>> FP_SHIFT;
                        b = ((((srcPixels[idx1] & 0xFF) * (FP_MASK - fx) + (srcPixels[idx2] & 0xFF) * fx) >>> FP_SHIFT) * (FP_MASK - fy) + (((srcPixels[idx3] & 0xFF) * (FP_MASK - fx) + (srcPixels[idx4] & 0xFF) * fx) >>> FP_SHIFT) * fy) >>> FP_SHIFT;

                        destPixels[x + y * destw] = (r << 16) | (g << 8) | b;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {}
    }

public static void bilinearResizeRGBnA(int[] srcPixels, int[] destPixels, int srcw, int srch, int destw, int desth) {
        final int FP_SHIFT = 16;
	final int FP_MASK = (1 << FP_SHIFT) - 1;


        int x, y, rx, ry, ix, iy, fx, fy;
        int idx1, idx2, idx3, idx4;
        int a, r, g, b;

        try {
            {
                for (x = 0; x < destw; x++) {
                    for (y = 0; y < desth; y++) {
                        rx = (x << FP_SHIFT) / destw * srcw;
                        ry = (y << FP_SHIFT) / desth * srch;

                        ix = rx >>> FP_SHIFT;
                        iy = ry >>> FP_SHIFT;

                        fx = rx & FP_MASK;
                        fy = ry & FP_MASK;

                        idx1 = idx2 = idx3 = idx4 = ix + iy * srcw;

                        if (ix < srcw - 1) {
                            idx2++;
                            idx4++;
                        }

                        if (iy < srch - 1) {
                            idx3 += srcw;
                            idx4 += srcw;
                        }

                        r = (((((srcPixels[idx1] >> 16) & 0xFF) * (FP_MASK - fx) + ((srcPixels[idx2] >> 16) & 0xFF) * fx) >>> FP_SHIFT) * (FP_MASK - fy) + ((((srcPixels[idx3] >> 16) & 0xFF) * (FP_MASK - fx) + ((srcPixels[idx4] >> 16) & 0xFF) * fx) >>> FP_SHIFT) * fy) >>> FP_SHIFT;
                        g = (((((srcPixels[idx1] >> 8) & 0xFF) * (FP_MASK - fx) + ((srcPixels[idx2] >> 8) & 0xFF) * fx) >>> FP_SHIFT) * (FP_MASK - fy) + ((((srcPixels[idx3] >> 8) & 0xFF) * (FP_MASK - fx) + ((srcPixels[idx4] >> 8) & 0xFF) * fx) >>> FP_SHIFT) * fy) >>> FP_SHIFT;
                        b = ((((srcPixels[idx1] & 0xFF) * (FP_MASK - fx) + (srcPixels[idx2] & 0xFF) * fx) >>> FP_SHIFT) * (FP_MASK - fy) + (((srcPixels[idx3] & 0xFF) * (FP_MASK - fx) + (srcPixels[idx4] & 0xFF) * fx) >>> FP_SHIFT) * fy) >>> FP_SHIFT;

                        destPixels[x + y * destw] = (r << 16) | (g << 8) | b;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {}
    }
public static void bilinearResizeRGBnA_toLow(int[] srcPixels, int[] destPixels, int srcw, int srch, int destw, int desth) {
        final int FP_SHIFT = 16;
	final int FP_MASK = (1 << FP_SHIFT) - 1;


        int x, y, rx, ry,xx,yy;
        int idx1;
        int a, r, g, b;

        try {
            {
                for (x = 0; x < destw; x++) {
                    for (y = 0; y < desth; y++) {
                        
r=g=b=0;
a=1;
xx=x*srcw/destw;
yy=y*srch/desth;
int xxf=xx+srcw/destw;
int yyf=yy+srch/desth;
for (; xx < xxf; xx+=2) {
for (; yy < yyf; yy+=2) {
rx =xx;
ry = yy;
if (rx > srcw) 
{
rx=srcw;
}
if (ry > srch) 
{
rx=srch;
}
idx1 = rx + ry * srcw;
r += (srcPixels[idx1] >> 16) & 0xff;
g += (srcPixels[idx1] >> 8) & 0xff;
b += srcPixels[idx1]& 0xff;
a++;
}
}
//Enter_norrmal
r=r/a;
if(r>255) r=255;
g=g/a;
if(g>255) g=255;
b=b/a;
if(b>255) b=255;
destPixels[x + y * destw] =(255<<24)| (r << 16) | (g << 8) | b;



                                            }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {}
    }

public static int[] mipMap(int[] original,int w,int h) {

int nw=w/2;
int nh=h/2;
int[] out= new int[nw*nh];


for(int y=0;y<nh;y++) {


for(int x=0;x<nw;x++) {
int col=original[(x*2+y*2*w) ];
int aa = (col >> 24) & 0xff;
int ar = (col >> 16) & 0xff;
int ag = (col >> 8) & 0xff;
int ab = col & 0xff;
col=original[(x*2+y*2*w+1) & (original.length-1) ];
//int ba = (col >> 24) & 0xff;
int br = (col >> 16) & 0xff;
int bg = (col >> 8) & 0xff;
int bb = col & 0xff;
col=original[(x*2+y*2*w+1+w) & (original.length-1)];
//int ca = (col >> 24) & 0xff;
int cr = (col >> 16) & 0xff;
int cg = (col >> 8) & 0xff;
int cb = col & 0xff;
col=original[(x*2+y*2*w+w) & (original.length-1)];
//int da = (col >> 24) & 0xff;
int dr = (col >> 16) & 0xff;
int dg = (col >> 8) & 0xff;
int db = col & 0xff;
int or=(ar+br+cr+dr)/4;
int og=(ag+bg+cg+dg)/4;
int ob=(ab+bb+cb+db)/4;
out[x+y*nw]=(aa << 24) | (or << 16) | (og << 8) | ob;

}


}


return out;
}


public static int[] cubic2XDesize(int[] original,int w,int h) {

int nw=w/2;
int nh=h/2;
int[] out= new int[nw*nh];


for(int y=0;y<nh;y++) {


for(int x=0;x<nw;x++) {
int col=original[(((x*2)%w)+((y*2)%h)*w)];
int aa = (col >> 24) & 0xff;
int ar = (col >> 16) & 0xff;
int ag = (col >> 8) & 0xff;
int ab = col & 0xff;
col=original[(((x*2)%w)+((y*2+1)%h)*w)];
//int ba = (col >> 24) & 0xff;
int br = (col >> 16) & 0xff;
int bg = (col >> 8) & 0xff;
int bb = col & 0xff;
col=original[(((x*2+1)%w)+((y*2+1)%h)*w)];
//int ca = (col >> 24) & 0xff;
int cr = (col >> 16) & 0xff;
int cg = (col >> 8) & 0xff;
int cb = col & 0xff;
col=original[(((x*2+1)%w)+((y*2)%h)*w)];
//int da = (col >> 24) & 0xff;
int dr = (col >> 16) & 0xff;
int dg = (col >> 8) & 0xff;
int db = col & 0xff;
int or=(ar+br+cr+dr)/4;
int og=(ag+bg+cg+dg)/4;
int ob=(ab+bb+cb+db)/4;
out[x+y*nw]=(aa << 24) | (or << 16) | (og << 8) | ob;

}


}


return out;
}

public static int[] cubic2XVertDesize(int[] original,int w,int h) {

int nw=w;
int nh=h/2;
int[] out= new int[nw*nh];

for(int y=0;y<nh;y++) {


for(int x=0;x<nw;x++) {
int col=original[x+(((y*2)%h)*w)];
int aa = (col >> 24) & 0xff;
int ar = (col >> 16) & 0xff;
int ag = (col >> 8) & 0xff;
int ab = col & 0xff;
col=original[x+(((y*2+1)%h)*w)];
//int ba = (col >> 24) & 0xff;
int br = (col >> 16) & 0xff;
int bg = (col >> 8) & 0xff;
int bb = col & 0xff;
int or=(ar+br)/2;
int og=(ag+bg)/2;
int ob=(ab+bb)/2;
out[x+y*nw]=(aa << 24) | (or << 16) | (og << 8) | ob;

}


}


return out;
}


public static int[] cubic2XHorDesize(int[] original,int w,int h) {

int nw=w/2;
int nh=h;
int[] out= new int[nw*nh];

for(int y=0;y<nh;y++) {


for(int x=0;x<nw;x++) {
int col=original[x+((y%h)*(w*2))];
int aa = (col >> 24) & 0xff;
int ar = (col >> 16) & 0xff;
int ag = (col >> 8) & 0xff;
int ab = col & 0xff;
col=original[x+((y%h)*(w*2+1))];
//int ba = (col >> 24) & 0xff;
int br = (col >> 16) & 0xff;
int bg = (col >> 8) & 0xff;
int bb = col & 0xff;
int or=(ar+br)/2;
int og=(ag+bg)/2;
int ob=(ab+bb)/2;
out[x+y*nw]=(aa << 24) | (or << 16) | (og << 8) | ob;

}


}


return out;
}

}
