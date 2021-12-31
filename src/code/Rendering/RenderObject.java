package code.Rendering;
import code.Gameplay.Map.LightMapper;
import code.Math.MathUtils;

public abstract class RenderObject {

   public int sz;
   public short nx, ny, nz;
   public static final int normal_fp = 12, normal_FP = 1 << normal_fp;


   public RenderObject() {
      nx = ny = nz = 0;
   }

   // computeNormal
   public RenderObject(Vertex ver1, Vertex ver2, Vertex ver3) {
        long x = (long)(ver1.y-ver2.y)*(ver1.z-ver3.z) - (long)(ver1.z-ver2.z)*(ver1.y-ver3.y);
        long y = (long)(ver1.z-ver2.z)*(ver1.x-ver3.x) - (long)(ver1.x-ver2.x)*(ver1.z-ver3.z);
        long z = (long)(ver1.x-ver2.x)*(ver1.y-ver3.y) - (long)(ver1.y-ver2.y)*(ver1.x-ver3.x);
        double sqrt = Math.sqrt(x*x + y*y + z*z)/(1<<normal_fp);

        nx = (short) (x/sqrt);
        ny = (short) (y/sqrt);
        nz = (short) (z/sqrt);
    
   }
   
   public void calculateNormals(Vertex ver1, Vertex ver2, Vertex ver3) {
        long x = (long)(ver1.y-ver2.y)*(ver1.z-ver3.z) - (long)(ver1.z-ver2.z)*(ver1.y-ver3.y);
        long y = (long)(ver1.z-ver2.z)*(ver1.x-ver3.x) - (long)(ver1.x-ver2.x)*(ver1.z-ver3.z);
        long z = (long)(ver1.x-ver2.x)*(ver1.y-ver3.y) - (long)(ver1.y-ver2.y)*(ver1.x-ver3.x);
        double sqrt = Math.sqrt(x*x + y*y + z*z)/(1<<normal_fp);

        nx = (short) (x/sqrt);
        ny = (short) (y/sqrt);
        nz = (short) (z/sqrt);
    
   }
   
   public void calculateNormalsProjected(Vertex ver1, Vertex ver2, Vertex ver3) {
        long x = (long)(ver1.sy-ver2.sy)*(ver1.rz-ver3.rz) - (long)(ver1.rz-ver2.rz)*(ver1.sy-ver3.sy);
        long y = (long)(ver1.rz-ver2.rz)*(ver1.sx-ver3.sx) - (long)(ver1.sx-ver2.sx)*(ver1.rz-ver3.rz);
        long z = (long)(ver1.sx-ver2.sx)*(ver1.sy-ver3.sy) - (long)(ver1.sy-ver2.sy)*(ver1.sx-ver3.sx);
        double sqrt = Math.sqrt(x*x + y*y + z*z)/(1<<normal_fp);

        nx = (short) (x/sqrt);
        ny = (short) (y/sqrt);
        nz = (short) (z/sqrt);
    
   }

   public abstract void render(DirectX7 g3d, Texture texture);
   
   public abstract void renderFast(DirectX7 g3d, Texture texture);

   public abstract boolean isVisible(int x1, int y1, int x2, int y2);
   
   public int getLight(Vertex a, int la, DirectX7 g3d) {
       int lightLen = g3d.flashlightEnabled?6500:5000;
       int lita=(-a.rz<DirectX7.fDist || DirectX7.fDist <= 1)?255:0;
       int fa=0;
       int vec=255;
       if( (DirectX7.fDist > 1 && LightMapper.cameraVectorLight) || ((g3d.shootIntensity > 0 || g3d.flashlightEnabled) && -a.rz<lightLen) ) {
           vec=MathUtils.microCalcLight(nx,ny,nz,a.x-g3d.camera.m03,a.y-g3d.camera.m13,a.z-g3d.camera.m23);
       }
           
       if(DirectX7.fDist > 1 && -a.rz<DirectX7.fDist) {
           lita = 0xff + a.rz * 0xFF / DirectX7.fDist;
           if(LightMapper.cameraVectorLight) lita=lita*vec/255;
           if(lita < 0) lita = 0;
           if(lita > 0xFF) lita = 0xFF;
       }
       if((g3d.shootIntensity > 0 || g3d.flashlightEnabled) && -a.rz<lightLen) {
           fa = (0xff + a.rz * 0xFF / lightLen) * vec / 255;
           if(!g3d.flashlightEnabled) fa = fa * g3d.shootIntensity / g3d.shootLength;
           if(fa < 0) fa = 0;
           if(fa > 0xFF) return 0xff;
       }
       
       int la2 = (((la + 128) * lita ) >>8 ) + fa;
       if(la2 > 255) return 255;
       return la2;
   }
   
   
   public int[] getLight(Vertex a, int la, int la2, int la3, DirectX7 g3d) {
       int lightLen = g3d.flashlightEnabled?6500:5000;
       int lita=(-a.rz<DirectX7.fDist || DirectX7.fDist <= 1)?255:0;
       int fa=0;
       int vec=255;
       if( (DirectX7.fDist > 1 && LightMapper.cameraVectorLight) || ((g3d.shootIntensity > 0 || g3d.flashlightEnabled) && -a.rz<lightLen) ) {
           vec=MathUtils.microCalcLight(nx,ny,nz,a.x-g3d.camera.m03,a.y-g3d.camera.m13,a.z-g3d.camera.m23);
       }
           
       if(DirectX7.fDist > 1 && -a.rz<DirectX7.fDist) {
           lita = 0xff + a.rz * 0xFF / DirectX7.fDist;
           if(LightMapper.cameraVectorLight) lita=lita*vec/255;
           if(lita < 0) lita = 0;
           if(lita > 0xFF) lita = 0xFF;
       }
       if((g3d.shootIntensity > 0 || g3d.flashlightEnabled) && -a.rz<lightLen) {
           fa = (0xff + a.rz * 0xFF / lightLen) * vec / 255;
           if(!g3d.flashlightEnabled) fa = fa * g3d.shootIntensity / g3d.shootLength;
           if(fa < 0) fa = 0;
           if(fa > 0xFF) fa = 0xff;
       }
       
       int[] out=new int[3];
       out[0] = ((la + 128) * lita) / 255 + fa;
       if (out[0] > 255) out[0]=255;
       out[1] = ((la2 + 128) * lita) / 255 + fa;
       if (out[1] > 255) out[1]=255;
       out[2] = ((la3 + 128) * lita) / 255 + fa;
       if (out[2] > 255) out[2]=255;
       
       return out;
   }
}
