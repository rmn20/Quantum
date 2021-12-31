package code.Rendering;

import code.HUD.DeveloperMenu;
import code.Rendering.Meshes.Mesh;
import code.Math.Matrix;
import javax.microedition.lcdui.Graphics;

public final class DirectX7 {

   public int width, height;
   public int[] display;
   public boolean flushAlpha = false;
   public Matrix camera, invCamera, finalCamera;
   public int centreX, centreY;
   public int distX, distY;
   public int fovX, fovY;
   
   public static int fDist = 1;
   public static int drDist = 30000000;
   public static int waterDistance = 10000;
   public static byte standartDrawmode = 0;
   public static int fogc = 0x5500ff;
   public static short lightdirx = 0;
   public static short lightdiry = 0;
   public static short lightdirz = 0;
   
   private final RenderObjectBuffer buffer;
   public int lx,ly,lz = 0;
   public int shootIntensity = 0;
   public int shootLength = 0;
   public static boolean useAutoWMove = true; //wtf is this
   public boolean flashlightEnabled = false;

   public DirectX7(int width, int height) {

this.width = width;
this.height = height;

camera = new Matrix();
invCamera = new Matrix();
finalCamera = new Matrix();
buffer = new RenderObjectBuffer();
resize(width,height);
lx=ly=lz=0;
}
   
public void resize(int width, int height) {
this.width = width;
this.height = height;

display = new int[width * height];

centreX = width / 2;
centreY = height / 2;

updateFov(74);
}

public final void updateFov(int fov) {
distX = (int) (centreX/Math.tan(fov*Math.PI/360));
distY = (int) (centreY/Math.tan(fov*Math.PI/360));
distX = distX*height/width;

fovY=fov;
fovX=fov*width/height;
}
   public final void destroy() {

DirectX7.fDist = 1;
DirectX7.drDist = 30000000;
DirectX7.standartDrawmode = 0;
DirectX7.fogc = 0x5500ff;
camera = invCamera = finalCamera = null;
this.buffer.resetTex();
   }


   public final int getWidth() {
      return this.width;
   }

   public final int getHeight() {
      return this.height;
   }

   public final int[] getDisplay() {
      return this.display;
   }

   public final void setCamera(Matrix matrix) {
      invCamera.set(matrix);
      camera.set(matrix);
      invCamera.invert();
   }

   public final Matrix getInvCamera() {
      return invCamera;
   }

   public final Matrix getCamera() {
      return camera;
   }

   public final Matrix computeFinalMatrix(Matrix matrix) {
      finalCamera.mul(invCamera, matrix);
      return finalCamera;
   }

    public final void addRenderObject(RenderObject obj) {
        addRenderObject(obj, 0, 0, this.width, this.height);
    }

    public final void addRenderObject(RenderObject obj, int x1, int y1, int x2, int y2) {
        buffer.addRenderObject(obj, x1, y1, x2, y2);
    }

    public final void addRenderObject(RenderObject obj, Texture tex) {
        buffer.addRenderObject(obj, tex, 0, 0, this.width, this.height);

    }

    public final void addRenderObjectDT(RenderObject obj, Texture tex) {
        buffer.addRenderObjectDT(obj, tex, 0, 0, this.width, this.height);
    }

    public final void addMesh(Mesh mesh, int x1, int y1, int x2, int y2) {
        buffer.addRenderObjects(mesh.getPolygons(), mesh.getTexture(), x1, y1, x2, y2);
    }

    public final void addMesh(Mesh mesh, int x1, int y1, int x2, int y2, MultyTexture tex) {
        buffer.addRenderObjects(mesh.getPolygons(), tex, x1, y1, x2, y2);
    }


   public final void render() {
       lx = camera.m03;
       ly = camera.m13;
       lz = camera.m23;
        buffer.sort(0,buffer.getSize()-1);
        if(DeveloperMenu.renderPolygonsOverwrite) clearDisplay(0);
        TMPElement[] buffer2 = buffer.getBuffer();

        int i = buffer.getSize()-1;
        for(; i>=0; i--) {
            TMPElement element = buffer2[i];
            element.obj.render(this, element.tex);
        }
        buffer.reset();
   }
   
   public final void drawLine(int x1, int y1, int x2, int y2, int fat, int col) {
       int tmp;
       if(y1>y2) {tmp=y1; y1=y2; y2=tmp;tmp=x1; x1=x2; x2=tmp;}
       
       if( (x1>width && x2>width) || y1>height || (x2<0 && x1<0) || y2<0) return;
       int ySize = y2 - y1;
       int halfFat = fat/2;
       if(fat==1) halfFat = 0;
       
       int y=y1;
       if(y<0) y=0;
       int yEnd = y2;
       if(yEnd>height) yEnd = height;
       
       int perc,x,xx1,xx2;
       for(;y<yEnd;y++) {
           perc = y-y1;
           x = (x1*(ySize-perc) + x2*perc) / ySize;
           
           tmp = y*width;
           xx1 = x-halfFat;
           xx2 = xx1 + fat;
           if(xx1>width || xx2<0) continue;
           if(xx1<0) xx1=0;
           if(xx2>=width) xx2=width-1;
           xx1+=tmp; xx2+=tmp;
           
           while(xx1<xx2) {
               display[xx1] = col; xx1++;
           }
       }
       
       
   }

   public final void flush(Graphics g, int x, int y) {
       if(DeveloperMenu.renderPolygonsOverwrite) for(int i=display.length-1;i>=0;i--) {
           int c=display[i];
           if(c==0) display[i]=0xffffff;
           else if(c==1) display[i]=0x00ff00;
           else if(c==2) display[i]=0xffff00;
           else if(c==3) display[i]=0xff8800;
           else if(c==4) display[i]=0xff0000;
           else if(c==5) display[i]=0x880000;
       }
g.drawRGB(display, 0, width, x, y, width, height,flushAlpha );
   }


    public static void transform(Mesh ms, Matrix m) {
Vertex[] vers=ms.getVertices();

        final int m00 = m.m00 >> 2;
        final int m01 = m.m01 >> 2;
        final int m02 = m.m02 >> 2;
        final int m03 = m.m03;

        final int m10 = m.m10 >> 2;
        final int m11 = m.m11 >> 2;
        final int m12 = m.m12 >> 2;
        final int m13 = m.m13;

        final int m20 = m.m20 >> 2;
        final int m21 = m.m21 >> 2;
        final int m22 = m.m22 >> 2;
        final int m23 = m.m23;

        final int fp =  Matrix.fp-2;

        Vertex ver;
        int x, y, z;
        for(int i=vers.length-1; i>=0; i--) {
            ver = vers[i];
            x = ver.x;
            y = ver.y;
            z = ver.z;

            ver.sx = (x * m00 >> fp) + (y * m01 >> fp) + (z * m02 >> fp) + m03;
            ver.sy = (x * m10 >> fp) + (y * m11 >> fp) + (z * m12 >> fp) + m13;
            ver.rz = (x * m20 >> fp) + (y * m21 >> fp) + (z * m22 >> fp) + m23;
        }



}
    
    public static void transform(Vertex[] vers, Matrix m) {

        if(m!=null) {
        final int m00 = m.m00 >> 2;
        final int m01 = m.m01 >> 2;
        final int m02 = m.m02 >> 2;
        final int m03 = m.m03;

        final int m10 = m.m10 >> 2;
        final int m11 = m.m11 >> 2;
        final int m12 = m.m12 >> 2;
        final int m13 = m.m13;

        final int m20 = m.m20 >> 2;
        final int m21 = m.m21 >> 2;
        final int m22 = m.m22 >> 2;
        final int m23 = m.m23;

        final int fp =  Matrix.fp-2;

        Vertex ver;
        int x, y, z;
        for(int i=vers.length-1; i>=0; i--) {
            ver = vers[i];
            x = ver.x;
            y = ver.y;
            z = ver.z;

            ver.sx = (x * m00 >> fp) + (y * m01 >> fp) + (z * m02 >> fp) + m03;
            ver.sy = (x * m10 >> fp) + (y * m11 >> fp) + (z * m12 >> fp) + m13;
            ver.rz = (x * m20 >> fp) + (y * m21 >> fp) + (z * m22 >> fp) + m23;
        }
        } else {
            Vertex ver;
            for(int i=vers.length-1; i>=0; i--) {
            ver = vers[i];

            ver.sx = ver.x;
            ver.sy = ver.y;
            ver.rz = ver.z;
            } 
            
        }



}
    
    public void project(Vertex[] vertices,int rz) {
        Vertex ver = null;
        int sx, sy;
        for(int i=vertices.length-1; i>=0; i--) {
            ver = vertices[i];
            sx = ver.sx;
            sy = -ver.sy;

            if(rz <= 0) {
                sx = sx * distX / (rz + distX) ;
                sy = sy * distY / (rz + distY) ;
            } 
            
            ver.sx = (short)(sx+centreX);
            ver.sy = (short)(sy+centreY);
        }
    }

public static void returnMesh(Mesh ms) {
Vertex[] vers=ms.getVertices();

        Vertex ver;
        for(int i=vers.length-1; i>=0; i--) {
            ver = vers[i];
            ver.x=ver.sx;
            ver.y=ver.sy;
            ver.z=ver.rz;
        }
}


public static void transformSave(Object ms, Matrix m) {
Vertex[] vers;
if(ms instanceof Mesh) vers = ((Mesh)ms).getVertices();
else if(ms instanceof Vertex[]) vers = (Vertex[]) ms;
else return;

        final int m00 = m.m00 >> 2;
        final int m01 = m.m01 >> 2;
        final int m02 = m.m02 >> 2;
        final int m03 = m.m03;

        final int m10 = m.m10 >> 2;
        final int m11 = m.m11 >> 2;
        final int m12 = m.m12 >> 2;
        final int m13 = m.m13;

        final int m20 = m.m20 >> 2;
        final int m21 = m.m21 >> 2;
        final int m22 = m.m22 >> 2;
        final int m23 = m.m23;

        final int fp =  Matrix.fp-2;

        Vertex ver;
        int x, y, z;
        for(int i=vers.length-1; i>=0; i--) {
            ver = vers[i];
            x = ver.x;
            y = ver.y;
            z = ver.z;
            ver.sx=x;
            ver.sy=y;
            ver.rz=z;
            ver.x = (x * m00 >> fp) + (y * m01 >> fp) + (z * m02 >> fp) + m03;
            ver.y = (x * m10 >> fp) + (y * m11 >> fp) + (z * m12 >> fp) + m13;
            ver.z = (x * m20 >> fp) + (y * m21 >> fp) + (z * m22 >> fp) + m23;
        }



}

    public static void transformReturn(Object ms) {
        Vertex[] vers;
        if (ms instanceof Mesh) {
            vers = ((Mesh) ms).getVertices();
        } else if(ms instanceof Vertex[]) {
            vers = (Vertex[]) ms;
        } else return;

        Vertex ver;
        int x, y, z;
        for(int i=vers.length-1; i>=0; i--) {
            ver = vers[i];
            x = ver.x;
            y = ver.y;
            z = ver.z;
            ver.sx = x;
            ver.sy = y;
            ver.rz = z;
            ver.x = ver.sx;
            ver.y = ver.sy;
            ver.z = ver.rz;
        }

    }


    public void transformAndProjectVertices(Mesh ms, Matrix matrix) {
        Vertex[] vertices=ms.getVertices();


        final int m00 = matrix.m00 >> 2;
        final int m01 = matrix.m01 >> 2;
        final int m02 = matrix.m02 >> 2;
        final int m03 = matrix.m03;

        final int m10 = matrix.m10 >> 2;
        final int m11 = matrix.m11 >> 2;
        final int m12 = matrix.m12 >> 2;
        final int m13 = matrix.m13;

        final int m20 = matrix.m20 >> 2;
        final int m21 = matrix.m21 >> 2;
        final int m22 = matrix.m22 >> 2;
        final int m23 = matrix.m23;

        final int fp =  Matrix.fp-2;

        Vertex ver;
        int x, y, z;
        int sx, sy, rz;

        for(int i=vertices.length-1; i>=0; i--) {
            ver = vertices[i];
            x = ver.x;
            y = ver.y;
            z = ver.z;

            sx = m03;
if(m00!=0) sx+= (x * m00 >> fp);
if(m01!=0) sx+= (y * m01 >> fp);
if(m02!=0) sx+= (z * m02 >> fp);

            sy = -m13;
if(m10!=0) sy-= (x * m10 >> fp);
if(m11!=0) sy-= (y * m11 >> fp);
if(m12!=0) sy-= (z * m12 >> fp);

rz = m23;
if(m20!=0) rz+= (x * m20 >> fp);
if(m21!=0) rz+= (y * m21 >> fp);
if(m22!=0) rz+= (z * m22 >> fp);

//int rz2=MathUtils.pLength(sx*100/centreX,sy*100/centreY,0);
if(rz<0) {
sx = sx *distX /(-rz/*+rz2*/ + distX) ;
sy = sy *distY /(-rz/*+rz2*/ + distY) ;
} /*else {
sx = sx *distX /(1+rz2 + distX) ;
sy = sy *distY /(1+rz2 + distY) ;
}*/

ver.sx = sx+centreX;//(this.height-sy)*this.width/this.height;
ver.sy = sy+centreY;//sx*this.height/this.width;
ver.rz = rz;

/*
//if(rz<0) {
sx = sx *3/distX;//*distX /(-rz + distX) ;
sy = sy *3/distY;//*distY /(-rz + distY) ;
//}

ver.sx = sx+centreX;//(this.height-sy)*this.width/this.height;
ver.sy = sy+centreY;//sx*this.height/this.width;
ver.rz = rz;
*/
        }

    



}


    public void transformAndProjectVertices(Vertex[] vertices, Matrix matrix) {


        final int m00 = matrix.m00 >> 2;
        final int m01 = matrix.m01 >> 2;
        final int m02 = matrix.m02 >> 2;
        final int m03 = matrix.m03;

        final int m10 = matrix.m10 >> 2;
        final int m11 = matrix.m11 >> 2;
        final int m12 = matrix.m12 >> 2;
        final int m13 = matrix.m13;

        final int m20 = matrix.m20 >> 2;
        final int m21 = matrix.m21 >> 2;
        final int m22 = matrix.m22 >> 2;
        final int m23 = matrix.m23;

        final int fp =  Matrix.fp-2;

        Vertex ver;
        int x, y, z;
        int sx, sy, rz;

        
        for(int i=vertices.length-1; i>=0; i--) {
            ver = vertices[i];
            x = ver.x;
            y = ver.y;
            z = ver.z;

            sx = m03;
if(m00!=0) sx+= (x * m00 >> fp);
if(m01!=0) sx+= (y * m01 >> fp);
if(m02!=0) sx+= (z * m02 >> fp);

            sy = -m13;
if(m10!=0) sy-= (x * m10 >> fp);
if(m11!=0) sy-= (y * m11 >> fp);
if(m12!=0) sy-= (z * m12 >> fp);

            rz = m23;
if(m20!=0) rz+= (x * m20 >> fp);
if(m21!=0) rz+= (y * m21 >> fp);
if(m22!=0) rz+= (z * m22 >> fp);

if(rz<0) {
sx = sx *distX /(-rz + distX) ;
sy = sy *distY /(-rz + distY) ;
}

ver.sx = sx+centreX;//(this.height-sy)*this.width/this.height;
ver.sy = sy+centreY;//sx*this.height/this.width;
ver.rz = rz;
       }

    



}

public static void setFogDist(int d)
{
fDist=d;
}

public static void setDrDist(int d)
{
drDist=d;
}


public final void clearDisplay(int col) {

   int length = display.length;
   int i=0;
   //if(Main.s60Optimization==true) {
   for(; length-i>5;i+=5) {
      display[i] = col;
      display[i+1] = col;
      display[i+2] = col;
      display[i+3] = col;
      display[i+4] = col;
   }
   //}

   for(; i<length;i++) {
      display[i] = col;
   }


}


}
