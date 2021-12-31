package code.Rendering;

import code.utils.Asset;
import code.utils.ImageResize;
import code.utils.Main;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Vector;
import javax.microedition.lcdui.Image;

public class RawImage {

    public int[] img;
    public int w, h;
    public byte scale = 2;
    public boolean isPalette = false;
    public int widthBIT;
    public int widthBITmode10;
    public int W_UNIT;
    public boolean alphaMixing = false;

    static RawImage error = new RawImage(new int[]{0xff0000, 0xff0000, 0xff0000, 0xff0000}, 2, 2);

    public RawImage(int[] rgb, int w2, int h2) {
        this.img = rgb;
        this.w = w2;
        this.h = h2;
        
        widthBIT = widthToBIT(w);
        widthBITmode10 = widthToBIT(w * w);
        W_UNIT = wUnitGen(Math.max(w, h));
        alphaMixing = prepareAlpha(rgb);
        //this.img = TextureCompressor.compress(w, h, rgb);
    }
    
    
    
    private static RawImage imageToRawImage(Image img) throws Throwable {
        return imageToRawImage(img,2);
    }
    
    private static RawImage imageToRawImage(Image image, int size) {
        try {
            int w = image.getWidth();
            int h = image.getHeight();
            int[] img = new int[w * h];
            image.getRGB(img, 0, w, 0, 0, w, h);
        
            int scale = size;
            if(scale > Main.pixelsQ) scale = Main.pixelsQ;
            
            if(Main.pixelsQ == 0 && w > 1 && h > 1) {
                img = ImageResize.cubic2XDesize(img, w, h); 
                w/=2; 
                h/=2;
                scale=0;
            } else if(Main.pixelsQ != 2 && h > 1) {
                img = ImageResize.cubic2XVertDesize(img, w, h);
                h/=2;
                scale=1;
            }
        
            RawImage ri = new RawImage(img,w,h);
            ri.scale = (byte) scale;
            return ri;
        } catch (Throwable ex) {
            if(ex instanceof OutOfMemoryError) {
                boolean desized = Asset.desizeSomething();
                if(desized) return imageToRawImage(image,size-1);
            }
            
            System.err.println("ERROR in imageToRawImage: " + ex);
            return error;
        }
    }
    
    private static RawImage qctToRawImage(String path) {
        InputStream is = (new Object()).getClass().getResourceAsStream(path);
        DataInputStream dis = new DataInputStream(is);
        try {
            dis.skip(3); //format name
            int headerSize = dis.readUnsignedShort();
            dis.readUnsignedByte(); //Format version
            int w = dis.readUnsignedShort(); 
            int h = dis.readUnsignedShort();
            int colorCount  = 1<<(dis.readUnsignedByte()+1);
            int alphaType = dis.readUnsignedByte(); //Alpha
            headerSize-=7;
            
            int lastColorReplace = 0;
            if(alphaType >= 1) { lastColorReplace = dis.readUnsignedByte(); headerSize--;}
                
            if(headerSize>0) dis.skip(headerSize);
            
            int[] img = new int[w * h];
            int[] colors = new int[colorCount];
            
            for(int i=0;i<colorCount;i++) {
                colors[i] = 0xff000000 | (dis.readUnsignedByte()<<16) | (dis.readUnsignedByte()<<8) | dis.readUnsignedByte();
            }
            
            int[] tmpColors = new int[4];
            
            for(int y=0;y<h/2;y++) {
                int yp = y*w<<1;
                for(int x=0;x<w/2;x++) {
                    int xp = yp+(x<<1);
                    
                    int c0id = dis.readUnsignedByte();
                    int c3id = dis.readUnsignedByte();
                    
                    boolean hasAlpha = c0id>c3id;
                    tmpColors[0] = colors[c0id]; tmpColors[3] = colors[c3id];
                    int rb0 = tmpColors[0]&0xff00ff;
                    int rb3 = tmpColors[3]&0xff00ff;
                    int g0 = tmpColors[0]&0x00ff00;
                    int g3 = tmpColors[3]&0x00ff00;
                    
                    if(hasAlpha) {
                        tmpColors[2] = 0;
                        tmpColors[1] = ( (
                                ((rb0 + rb3) & 0x1fe01de) |
                                ((g0 + g3) & 0x001fe00)
                                ) >> 1) | 0xff000000;
                    } else {
                        tmpColors[1] = ( (
                                ((rb0*171 + rb3*85) & 0xff00ff00) |
                                ((g0*171 + g3*85) & 0x00ff0000)
                                ) >> 8) | 0xff000000;
                        tmpColors[2] = ( (
                                ((rb0*85 + rb3*171) & 0xff00ff00) |
                                ((g0*85 + g3*171) & 0x00ff0000)
                                ) >> 8) | 0xff000000;
                    }
                    
                    int ids = dis.readByte();
                    
                    img[xp] = tmpColors[ids&3];
                    img[xp+1] = tmpColors[(ids>>>2)&3];
                    img[xp+w] = tmpColors[(ids>>>4)&3];
                    img[xp+w+1] = tmpColors[(ids>>>6)&3];
                    
                }
            }
            
            if(alphaType == 2) {
                for(int i=0;i<img.length;i++) {
                    if((img[i]>>>24)==0) continue;
                    
                    int solid = dis.readUnsignedByte();
                    int transparent = dis.readUnsignedByte();
                    
                    i+=solid;
                    if(transparent==0) {i--;continue;}
                    int c = 0;
                    for(;i<img.length;i++) {
                        if((img[i]>>>24)==0) continue;
                        img[i] = (dis.readUnsignedByte()<<24) | (img[i]&0xffffff);
                        
                        c++;
                        if(c==transparent) break;
                    }
                }
            }
        
            if(Main.pixelsQ == 0) {img = ImageResize.cubic2XDesize(img, w, h); w/=2; h/=2;}
            if(Main.pixelsQ == 1) {img = ImageResize.cubic2XVertDesize(img, w, h);h/=2;}
        
            RawImage ri = new RawImage(img,w,h);
            ri.scale = (byte) Main.pixelsQ;
            
            dis.close();
            return ri;
        } catch (Throwable ex) {
            try {
                dis.close();
            } catch(Exception e) {}
            
            System.err.println("ERROR in qctToRawImage: " + ex);
            return error;
        }
    }
    
    public static RawImage createRawImage(String file) {
        try {
            String format = file;
            if(format.indexOf('.')>-1) format = format.substring(format.indexOf('.')+1).toLowerCase();
            else format = null;
            
            if(format.equals("qct")) {
                return qctToRawImage(file);
            }
            return imageToRawImage(Image.createImage(file));
        } catch (Throwable ex) {
            if(ex instanceof OutOfMemoryError) return error;
            
            System.err.println("ERROR in createRawImage " + file + ": " + ex);
            return error;
        }
    }
    
    
    public static RawImage createMipRawImage(RawImage base) {
        if(base.w<2 || base.h<2) return null;
        
        try {
            int[] img = ImageResize.mipMap(base.img,base.w,base.h);
            return new RawImage(img,base.w/2,base.h/2);
        } catch(Exception e) {
            return null;
        }
    }


    private static int widthToBIT(int w) {
        for (int var1 = 0; var1 < 32; ++var1) {
            if (w >> var1 == 1 && 1 << var1 == w) {
                return var1;
            }
        }

        return 0;
    }

    private static int wUnitGen(int w) {
        if (w <= 256) {
            return 1 << 30;
        } else if (w <= 512) {
            return 1 << 29;
        } else if (w <= 1024) {
            return 1 << 28;
        } else if (w <= 2048) {
            return 1 << 27;
        } else if (w <= 4096) {
            return 1 << 26;
        } else if (w <= 8192) {
            return 1 << 25;
        } else {
            return 1 << 24;
        }
    }

    private static final boolean prepareAlpha(int[] pix) {
        boolean alphaMixing = false;
        
        for (int i = 0; i < pix.length; i++) {
            int alpha = (pix[i] >>> 24) & 0xff;
            if (alpha > 0 && alpha < 255) alphaMixing = true;
            if (alpha == 0) pix[i] = 0;
        }
        return alphaMixing;

    }
}

class TextureCompressor {
    
    public static int[] compress(int w, int h, int[] rgb) {
        int[] img = new int[w/4*h/4*5];
        Vector colors = new Vector();
        boolean hasAlpha = false;
        
        int im = 0;
        for(int y = 0; y<h/4; y++) {
            for(int x = 0; x<w/4; x++) {
                int ids = 0x00000000;
                
                colors.removeAllElements();
                for(int xx = x*4; xx<x*4+4; xx++) {
                    for(int yy = y*4; yy<y*4+4; yy++) {
                        int color = rgb[xx + yy*w];
                        
                        if( ((color>>24)&0xff) == 0 ) {
                            hasAlpha = true;
                        } else {
                            if(!hasColor(colors,color)) colors.addElement(new Integer(color));
                        }
                        
                    }
                }
                
                int[] sColors = sortColors(colors);
                
                int[] outcolors = new int[4];
                
                if(hasAlpha) {
                    outcolors[0] = 0x00000000; //alpha
                    if(sColors.length<=3) {
                        if(sColors.length>=1) outcolors[1] = sColors[0];
                        if(sColors.length>=2) outcolors[2] = sColors[1];
                        if(sColors.length>=3) outcolors[3] = sColors[2];
                    } else {
                        outcolors[1] = sColors[0];
                        outcolors[2] = sColors[sColors.length/2];
                        outcolors[3] = sColors[sColors.length-1];
                    }
                } else {
                    if(sColors.length<=4) {
                        if(sColors.length>=1) outcolors[0] = sColors[0];
                        if(sColors.length>=2) outcolors[1] = sColors[1];
                        if(sColors.length>=3) outcolors[2] = sColors[2];
                        if(sColors.length>=4) outcolors[3] = sColors[3];
                    } else {
                        outcolors[0] = sColors[0];
                        outcolors[1] = sColors[sColors.length/3];
                        outcolors[2] = sColors[sColors.length*2/3];
                        outcolors[3] = sColors[sColors.length-1];
                    }
                }
                
                int ind = 0;
                for(int yy = y*4; yy<y*4+4; yy++) {
                    for(int xx = x*4; xx<x*4+4; xx++) {
                        int color = rgb[xx + yy*w];
                        int id;
                        
                        if( ((color>>24)&0xff) == 0 ) {
                            id = 0; //alpha
                        } else {
                            id = findNearest(outcolors,color);
                        }
                        
                        ids|= (id&0x3)<<(ind*2);
                        ind++;
                    }
                }
                img[im*5+0] = outcolors[0];
                img[im*5+1] = outcolors[1];
                img[im*5+2] = outcolors[2];
                img[im*5+3] = outcolors[3];
                img[im*5+4] = ids;
                im++;
            }
        }
        
        return img;
    }
    
    static boolean hasColor(Vector vec, int color) {
        for(int i=0;i<vec.size();i++) {
            if( ((Integer)vec.elementAt(i)).intValue() == color ) return true;
        }
        
        return false;
    }
    
    static int[] sortColors(Vector vec) {
        int[] out = new int[vec.size()];
        
        for(int i=0;i<vec.size();i++) { //Copy
            out[i] = ((Integer)vec.elementAt(i)).intValue();
        }
        
        
        for(int end=out.length-1;end>0;end--) { //Sort
            for(int i=1;i<=end;i++) {
                if(out[i-1]>out[i]) {
                    int t = out[i-1];
                    out[i-1] = out[i];
                    out[i] = t;
                }
            }
        }
        
        return out;
    }
    
    static int findNearest(int[] arr, int col) {
        
        int nearest = 0;
        int dist = Integer.MAX_VALUE;
        int r = (col>>16)&0xff;
        int g = (col>>8)&0xff;
        int b = col&0xff;
        
        for(int i=0;i<arr.length;i++) {
            if(arr[i] == 0) continue;
            
            int rr = (arr[i]>>16)&0xff;
            int gg = (arr[i]>>8)&0xff;
            int bb = arr[i]&0xff;
            
            
            int ndist = Math.abs(rr-r) + Math.abs(gg-g) + Math.abs(bb-b);
            if(ndist<dist) {
                nearest = i;
                dist = ndist;
            }
        }
        
        return nearest;
    }
    
}
