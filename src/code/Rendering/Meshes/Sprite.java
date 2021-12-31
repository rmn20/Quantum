package code.Rendering.Meshes;

import code.Math.Matrix;
import code.Rendering.DirectX7;
import code.Math.MathUtils;
import code.Rendering.RenderObject;
import code.Rendering.Texture;
import code.Rendering.Vertex;
import code.utils.Main;

public final class Sprite extends RenderObject {

   public Texture[] textures;
   public Vertex pos = new Vertex();
   public Vertex size = new Vertex();
   public boolean mirX = false;
   public boolean mirY = false;
   public int scale = 5;
   public int offsetX = 0;
   public int offsetY = 0;
   public byte mode=0;
   public byte cutoff=0;
   public int anim_index=0;
   public boolean fog=false;
   public long animationBegin=0L;
   public float animation_speed=1.0F;
   public boolean limiter=false;
   public int color=0;/*
   0 - white 
   1 - red
   2 - green
   3 - blue
   4 - violet
   */

   public Sprite(int var1) {}
   
   public Sprite(Texture[] texs,int sc,float animsp) {
   textures=texs;
   scale=sc;
   animation_speed=animsp;
   }
   
   public Sprite(Texture texs,int sc) {
   textures=new Texture[1];
   textures[0]=texs;
   scale=sc;
   }

    public void setMode(String tmp) {
        if(!tmp.equals("NULL")) {
            if(tmp.equals("ADD")) {
                mode = 1;
            } else if(tmp.equals("MUL")) {
                mode = 2;
            } else if(tmp.equals("OPAQUE") && textures[0].rImg.alphaMixing) {
                mode = 3;
            }
        } else {
            mode = 0;
            if(textures[0].rImg.alphaMixing) mode = 3;
        }
    }
    
    public void setFog(String tmp) {
        if(!tmp.equals("NULL")) {
            if(tmp.equals("MIX")) fog = true;
        } else if(DirectX7.standartDrawmode == 6 || 
                DirectX7.standartDrawmode == 3 || 
                DirectX7.standartDrawmode == 1) {
            fog = true;
        }
    }

   public final void setScale(int scale) {
      this.scale = scale;
   }

   public final void destroy() {
      this.textures = null;
      this.pos = null;
      this.size = null;
   }

   public final void setOffset(int x, int y) {
      this.offsetX = x;
      this.offsetY = y;
   }

   public final Vertex getPosition() {
      return this.pos;
   }

   public final void setTextures(Texture[] textures) {
      this.textures = textures;
   }

   public final int getHeight() {
      if(textures[0].mip!=null) textures[0].rImg=textures[0].mip[0];
      return this.textures[this.anim_index].rImg.h * this.scale;
   }
      public final int getWidth() {
      if(textures[0].mip!=null) textures[0].rImg=textures[0].mip[0];
      return this.textures[this.anim_index].rImg.w * this.scale;
   }

   public final boolean isVisible(int x1, int y1, int x2, int y2) {
this.sz = this.pos.rz*4-MathUtils.pLength(size.sx-pos.sx,size.sy-pos.sy);
if( -this.pos.rz>DirectX7.drDist ) return false;
if(cutoff==0) {

    if(sz>0) return false; //за экраном
    if(pos.sx >= x2 || pos.sy >= y2) return false; //справа || снизу
    if(size.sx <= x1 || size.sy <= y1) return false;
    return true;

}
else if(cutoff==1) {
if(sz>getHeight()*4) return false; //за экраном


if(pos.sx-scale >= x2 || pos.sy-scale >= y2) return false; //справа || снизу
if(size.sx+scale <= x1 || size.sy+scale <= y1) return false;
        return true;

}


return false;
   }

   public final void project(Matrix matrix, DirectX7 g3d) {
      if(textures[0].mip!=null) textures[0].rImg=textures[0].mip[0];
      int var3 = this.textures[this.anim_index].rImg.w * this.scale*(textures[0].rImg.scale==0?2:1);
      int var4 = this.getHeight()*(textures[0].rImg.scale<2?2:1);
      this.pos.transform(matrix);
      this.pos.sx += this.offsetX*(textures[0].rImg.scale==0?2:1);
      this.pos.sy += this.offsetY*(textures[0].rImg.scale<2?2:1);
      this.pos.sx -= var3 / 2;
      this.pos.sy += var4;
      this.size.sx = this.pos.sx + var3;
      this.size.sy = this.pos.sy - var4;
      this.size.rz = this.pos.rz;
      if(pos.rz>0 && cutoff==1) this.size.sy+=pos.rz;
      this.pos.project(g3d);
      this.size.project(g3d);
   }
   
   
   public final void project(DirectX7 g3d) {
      if(textures[0].mip!=null) textures[0].rImg=textures[0].mip[0];
      int var3 = this.textures[this.anim_index].rImg.w * this.scale*(textures[0].rImg.scale==0?2:1);
      int var4 = this.getHeight()*(textures[0].rImg.scale<2?2:1);
      this.pos.sx += this.offsetX*(textures[0].rImg.scale==0?2:1);
      this.pos.sy += this.offsetY*(textures[0].rImg.scale<2?2:1);
      this.pos.sx -= var3 / 2;
      this.pos.sy += var4;
      this.size.sx = this.pos.sx + var3;
      this.size.sy = this.pos.sy - var4;
      this.size.rz = this.pos.rz;
      if(pos.rz>0 && cutoff==1) size.sy+=pos.rz;
      this.pos.project(g3d);
      this.size.project(g3d);
   }
public final void renderFast(DirectX7 g3d, Texture texture) {
render(g3d,null);
}
public final void updateFrame() {
    if(this.textures.length>1) {
long time=(System.currentTimeMillis()-this.animationBegin);
time=time%(long)(this.textures.length*1000/this.animation_speed);
this.anim_index=(int)(time*this.animation_speed/1000)%this.textures.length;
if(limiter) {
time=(System.currentTimeMillis()-this.animationBegin);
this.anim_index=(int)(time*this.animation_speed/1000);
if(this.anim_index>=this.textures.length) this.anim_index=this.textures.length-1;
}
}
}
   public final void render(DirectX7 g3d, Texture texture) {
       

      texture = this.textures[this.anim_index];
      if(texture.mip!=null) texture.rImg=texture.mip[0];
      int x_start = this.pos.sx;
      int y_start = this.pos.sy;
      int x_end = this.size.sx;
      int y_end = this.size.sy;
      int tmp;
      if(x_start > x_end) {
         tmp = x_start; x_start = x_end; x_end = tmp;
      }

      if(y_start > y_end) {
         tmp = y_start; y_start = y_end; y_end = tmp;
      }

      int[] tex = texture.rImg.img;
      int texw = texture.rImg.w;
      int texh = texture.rImg.h;
      int texLength = tex.length;
      int[] rgb = g3d.display;
      int rgbWidth = g3d.width;
      int u1=0;
      int v1=0;
      int u2=texture.rImg.w;
      int v2=texture.rImg.h;

      int col;
      if(this.mirX) {
         u1 = texw;
         u2 = 0;
      }

      if(this.mirY) {
         v1 = texh;
         v2 = 0;
      }

      u1 <<= 12;
      u2 <<= 12;
      v1 <<= 12;
      v2 <<= 12;
      if(x_end != x_start && y_end != y_start) {
         int du = (u2 - u1) / (x_end - x_start);
         int dv = (v2 - v1) / (y_end - y_start);
         int u_start=u1, v_start=v1;

         if(y_start < 0) {
            v_start -= dv * y_start;
            y_start = 0;
         }

         if(y_end > g3d.height) y_end = g3d.height;
         

         if(x_start < 0) {
            u_start = u1 - du * x_start;
            x_start = 0;
         }

         if(x_end > rgbWidth) x_end = rgbWidth;
         
if(this.mode==1) {renderAdd(g3d,texture,x_start,y_start,x_end,y_end,u_start,v_start,du,dv); return;}
else if(this.mode==2 && color==0) {renderMul(g3d,texture,x_start,y_start,x_end,y_end,u_start,v_start,du,dv); return;}
else if(this.mode==2 && color==1) {renderMulRed(g3d,texture,x_start,y_start,x_end,y_end,u_start,v_start,du,dv); return;}
else if(this.mode==2 && color==2) {renderMulGreen(g3d,texture,x_start,y_start,x_end,y_end,u_start,v_start,du,dv); return;}
else if(this.mode==2 && color==3) {renderMulBlue(g3d,texture,x_start,y_start,x_end,y_end,u_start,v_start,du,dv); return;}
else if(this.mode==2 && color==4) {renderMulViolet(g3d,texture,x_start,y_start,x_end,y_end,u_start,v_start,du,dv); return;}
else if(this.mode==2 && color==5) {renderMulYellow(g3d,texture,x_start,y_start,x_end,y_end,u_start,v_start,du,dv); return;}
else if(this.mode==2 && color==6) {renderMulAqua(g3d,texture,x_start,y_start,x_end,y_end,u_start,v_start,du,dv); return;}
else if(this.mode==3 && texture.rImg.alphaMixing && this.fog) {renderOpaqueFog(g3d,texture,x_start,y_start,x_end,y_end,u_start,v_start,du,dv); return;}
else if(this.mode==3 && texture.rImg.alphaMixing) {renderOpaque(g3d,texture,x_start,y_start,x_end,y_end,u_start,v_start,du,dv); return;}
else if(this.fog) {renderF(g3d,texture,x_start,y_start,x_end,y_end,u_start,v_start,du,dv); return;}  //X1 fog

             
         while(y_start < y_end) {
            int x1 = x_start + rgbWidth * y_start;
            texh = x_end + rgbWidth * y_start;
u2 = (v_start & -4096) * texw + u_start;
            for(; texh-x1>=4; x1+=4) {
               if((col = tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]) != 0) rgb[x1] = col;
               u2 += du;
               if((col = tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]) != 0) rgb[x1+1] = col;
               u2 += du;
               if((col = tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]) != 0) rgb[x1+2] = col;
               u2 += du;
               if((col = tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]) != 0) rgb[x1+3] = col;
               u2 += du;
            }
            for(; x1 < texh; x1+=1) {
               if((col = tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]) != 0) rgb[x1] = col;
               u2 += du;
            }

            ++y_start;
            v_start += dv;
         }

      }
   }
   
   
      final void rendernF(DirectX7 g3d, Texture texture,int x_start,int y_start,int x_end,int y_end,int u_start,int v_start,int du,int dv) {
      int[] tex = texture.rImg.img;
      int[] rgb = g3d.display;
      int rgbWidth = g3d.width;
      int texw = texture.rImg.w;
      int texh = texture.rImg.h;
      int texLength = tex.length;
      int u2,col;
                while(y_start < y_end) {
            int x1 = x_start + rgbWidth * y_start;
            texh = x_end + rgbWidth * y_start;
u2 = (v_start & -4096) * texw + u_start;
if(Main.s60Optimization==1) {
            for(; texh-x1>=4; x1+=4) {
               if((col = tex[(u2 >>> 12 & Integer.MAX_VALUE) % texLength]) != 0) rgb[x1] = col;
               u2 += du;
               if((col = tex[(u2 >>> 12 & Integer.MAX_VALUE) % texLength]) != 0) rgb[x1+1] = col;
               u2 += du;
               if((col = tex[(u2 >>> 12 & Integer.MAX_VALUE) % texLength]) != 0) rgb[x1+2] = col;
               u2 += du;
               if((col = tex[(u2 >>> 12 & Integer.MAX_VALUE) % texLength]) != 0) rgb[x1+3] = col;
               u2 += du;
            }
}
if(Main.s60Optimization>=0) {
            for(; texh-x1>=2; x1+=2) {
               
               if( (col = tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength])!= 0) rgb[x1] = col;
               u2 += du;
               if((col = tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]) != 0) rgb[x1+1] = col;
               u2 += du;
            }
}
            for(; x1 < texh; x1+=1) {
               if((col = tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]) != 0) rgb[x1] = col;
               u2 += du;
            }

            ++y_start;
            v_start += dv;
         }

      }
      
         final void renderF(DirectX7 g3d, Texture texture,int x_start,int y_start,int x_end,int y_end,int u_start,int v_start,int du,int dv) {
      int[] tex = texture.rImg.img;
      int[] rgb = g3d.display;
      int rgbWidth = g3d.width;
      int texw = texture.rImg.w;
      int texh = texture.rImg.h;
      int texLength = tex.length;
      int u2,col,power;
      power=0;
if(DirectX7.fDist!=0) {
power=-this.pos.rz*255/(DirectX7.fDist);
}
if(power>255) power=255;
if(power<0) power=0;
int invpower=255-power;
int f2 =(DirectX7.fogc&0xFF00FF)*power;
int f22=(DirectX7.fogc&0x00FF00)*power;
if(DirectX7.standartDrawmode==6) f2=f22=0;
                while(y_start < y_end) {
            int x1 = x_start + rgbWidth * y_start;
            texh = x_end + rgbWidth * y_start;
u2 = (v_start & -4096) * texw + u_start;
if(Main.s60Optimization==1) {
    for(;texh-x1>=4; x1+=4) {
if((col = tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]) != 0)  rgb[x1]=  (   (  ( (col&0xFF00FF)*invpower + f2)  &0xFF00FF00  )  | (   ( (col&0x00FF00)*invpower  + f22)  &0x00FF0000  )   ) >>>8;
u2 += du;  
if((col = tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]) != 0)  rgb[x1+1]=  (   (  ( (col&0xFF00FF)*invpower + f2)  &0xFF00FF00  )  | (   ( (col&0x00FF00)*invpower  + f22)  &0x00FF0000  )   ) >>>8;
u2 += du;  
if((col = tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]) != 0)  rgb[x1+2]=  (   (  ( (col&0xFF00FF)*invpower + f2)  &0xFF00FF00  )  | (   ( (col&0x00FF00)*invpower  + f22)  &0x00FF0000  )   ) >>>8;
u2 += du;  
if((col = tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]) != 0)  rgb[x1+3]=  (   (  ( (col&0xFF00FF)*invpower + f2)  &0xFF00FF00  )  | (   ( (col&0x00FF00)*invpower  + f22)  &0x00FF0000  )   ) >>>8;
u2 += du;  
}
}

for(; x1 < texh; x1+=1) {
if((col = tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]) != 0)  rgb[x1]=  (   (  ( (col&0xFF00FF)*invpower + f2)  &0xFF00FF00  )  | (   ( (col&0x00FF00)*invpower  + f22)  &0x00FF0000  )   ) >>>8;
u2 += du;  
}


            ++y_start;
            v_start += dv;
         }

      }
         
      final void renderAdd(DirectX7 g3d, Texture texture,int x_start,int y_start,int x_end,int y_end,int u_start,int v_start,int du,int dv) {
      int[] tex = texture.rImg.img;
      int[] rgb = g3d.display;
      int rgbWidth = g3d.width;
      int texw = texture.rImg.w;
      int texh = texture.rImg.h;
      int texLength = tex.length;
      int u2,col;
                while(y_start < y_end) {
            int x1 = x_start + rgbWidth * y_start;
            texh = x_end + rgbWidth * y_start;
u2 = (v_start & -4096) * texw + u_start;
if(Main.s60Optimization==1) {
            for(; texh-x1>=3; x1+=3) {
                   col = tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength];
                   col=(col&0xFEFEFE)+(rgb[x1]&0xFEFEFE);
                   col|=(( col>>8 ) &0x010101)*0xFF;
                   rgb[x1]=col;
               u2 += du;
                   col = tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength];
                   col=(col&0xFEFEFE)+(rgb[x1+1]&0xFEFEFE);
                   col|=(( col>>8 ) &0x010101)*0xFF;
                   rgb[x1+1]=col;
               u2 += du;                   
               col = tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength];
                   col=(col&0xFEFEFE)+(rgb[x1+2]&0xFEFEFE);
                   col|=(( col>>8 ) &0x010101)*0xFF;
                   rgb[x1+2]=col;
               u2 += du;
            }
}
            for(; x1 < texh; x1++) {
                   col = tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength];
                   col=(col&0xFEFEFE)+(rgb[x1]&0xFEFEFE);
                   col|=(( col>>8 ) &0x010101)*0xFF;
                   rgb[x1]=col;
               u2 += du;
            }

            ++y_start;
            v_start += dv;
         }

      }
      
      final void renderMulRed(DirectX7 g3d, Texture texture,int x_start,int y_start,int x_end,int y_end,int u_start,int v_start,int du,int dv) {
      int[] tex = texture.rImg.img;
      int[] rgb = g3d.display;
      int rgbWidth = g3d.width;
      int texw = texture.rImg.w;
      int texh = texture.rImg.h;
      int texLength = tex.length;
      int u2,col,bri,col2;
                while(y_start < y_end) {
            int x1 = x_start + rgbWidth * y_start;
            texh = x_end + rgbWidth * y_start;
u2 = (v_start & -4096) * texw + u_start;
           if(Main.s60Optimization==1) {
               for(; texh-x1>=2; x1+=2) {
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1]; // Получаем цвет
col2=col;
col=((  ( (col&0xFF0000)*bri )  &0xFE000000  )) >>>6; // Затемняем и осветляем
rgb[x1]=(col|(( col>>8 ) &0x030303)*0xFF)|(col2&0x00FFFF); // Лимитер
}
u2+=du;
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1+1]; // Получаем цвет
col2=col;
col=((  ( (col&0xFF0000)*bri )  &0xFE000000  )) >>>6; // Затемняем и осветляем
rgb[x1]=(col|(( col>>8 ) &0x030303)*0xFF)|(col2&0x00FFFF); // Лимитер
}
u2+=du;
            }
           }
            for(; x1 < texh; x1++) {
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1]; // Получаем цвет
col2=col;
col=((  ( (col&0xFF0000)*bri )  &0xFE000000  )) >>>6; // Затемняем и осветляем
rgb[x1]=(col|(( col>>8 ) &0x030303)*0xFF)|(col2&0x00FFFF); // Лимитер
}
u2+=du;
            }

            ++y_start;
            v_start += dv;
         }

      }
      
            final void renderMulGreen(DirectX7 g3d, Texture texture,int x_start,int y_start,int x_end,int y_end,int u_start,int v_start,int du,int dv) {
      int[] tex = texture.rImg.img;
      int[] rgb = g3d.display;
      int rgbWidth = g3d.width;
      int texw = texture.rImg.w;
      int texh = texture.rImg.h;
      int texLength = tex.length;
      int u2,col,bri,col2;
                while(y_start < y_end) {
            int x1 = x_start + rgbWidth * y_start;
            texh = x_end + rgbWidth * y_start;
u2 = (v_start & -4096) * texw + u_start;
           if(Main.s60Optimization==1) {
               for(; texh-x1>=2; x1+=2) {
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1]; // Получаем цвет
col2=col;
col=((  ( (col&0x00FF00)*bri )  &0x00FE0000  )) >>>6; // Затемняем и осветляем
rgb[x1]=(col|(( col>>8 ) &0x030303)*0xFF)|(col2&0xFF00FF); // Лимитер
}
u2+=du;
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1+1]; // Получаем цвет
col2=col;
col=((  ( (col&0x00FF00)*bri )  &0x00FE0000  )) >>>6; // Затемняем и осветляем
rgb[x1]=(col|(( col>>8 ) &0x030303)*0xFF)|(col2&0xFF00FF); // Лимитер
}
u2+=du;
            }
           }
            for(; x1 < texh; x1++) {
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1]; // Получаем цвет
col2=col;
col=((  ( (col&0x00FF00)*bri )  &0x00FE0000  )) >>>6; // Затемняем и осветляем
rgb[x1]=(col|(( col>>8 ) &0x030303)*0xFF)|(col2&0xFF00FF); // Лимитер
}
u2+=du;
            }

            ++y_start;
            v_start += dv;
         }

      }
            
final void renderMulBlue(DirectX7 g3d, Texture texture,int x_start,int y_start,int x_end,int y_end,int u_start,int v_start,int du,int dv) {
      int[] tex = texture.rImg.img;
      int[] rgb = g3d.display;
      int rgbWidth = g3d.width;
      int texw = texture.rImg.w;
      int texh = texture.rImg.h;
      int texLength = tex.length;
      int u2,col,bri,col2;
                while(y_start < y_end) {
            int x1 = x_start + rgbWidth * y_start;
            texh = x_end + rgbWidth * y_start;
u2 = (v_start & -4096) * texw + u_start;
           if(Main.s60Optimization==1) {
               for(; texh-x1>=2; x1+=2) {
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1]; // Получаем цвет
col2=col;
col=((  ( (col&0x0000FF)*bri )  &0x0000FE00  )) >>>6; // Затемняем и осветляем
rgb[x1]=(col|(( col>>8 ) &0x030303)*0xFF)|(col2&0xFFFF00); // Лимитер
}
u2+=du;
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1+1]; // Получаем цвет
col2=col;
col=((  ( (col&0x0000FF)*bri )  &0x0000FE00  )) >>>6; // Затемняем и осветляем
rgb[x1]=(col|(( col>>8 ) &0x030303)*0xFF)|(col2&0xFFFF00); // Лимитер
}
u2+=du;
            }
           }
            for(; x1 < texh; x1++) {
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1]; // Получаем цвет
col2=col;
col=((  ( (col&0x0000FF)*bri )  &0x0000FE00  )) >>>6; // Затемняем и осветляем
rgb[x1]=(col|(( col>>8 ) &0x030303)*0xFF)|(col2&0xFFFF00); // Лимитер
}
u2+=du;
            }

            ++y_start;
            v_start += dv;
         }

      }

final void renderMulViolet(DirectX7 g3d, Texture texture,int x_start,int y_start,int x_end,int y_end,int u_start,int v_start,int du,int dv) {
      int[] tex = texture.rImg.img;
      int[] rgb = g3d.display;
      int rgbWidth = g3d.width;
      int texw = texture.rImg.w;
      int texh = texture.rImg.h;
      int texLength = tex.length;
      int u2,col,bri,col2;
                while(y_start < y_end) {
            int x1 = x_start + rgbWidth * y_start;
            texh = x_end + rgbWidth * y_start;
u2 = (v_start & -4096) * texw + u_start;
           if(Main.s60Optimization==1) {
               for(; texh-x1>=2; x1+=2) {
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1]; // Получаем цвет
col2=col;
col=((  ( (col&0xFF00FF)*bri )  &0xFE00FE00  )) >>>6; // Затемняем и осветляем
rgb[x1]=(col|(( col>>8 ) &0x030303)*0xFF)|(col2&0x00FF00); // Лимитер
}
u2+=du;
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1+1]; // Получаем цвет
col2=col;
col=((  ( (col&0xFF00FF)*bri )  &0xFE00FE00  )) >>>6; // Затемняем и осветляем
rgb[x1]=(col|(( col>>8 ) &0x030303)*0xFF)|(col2&0x00FF00); // Лимитер
}
u2+=du;
            }
           }
            for(; x1 < texh; x1++) {
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1]; // Получаем цвет
col2=col;
col=((  ( (col&0xFF00FF)*bri )  &0xFE00FE00  )) >>>6; // Затемняем и осветляем но не все каналы
rgb[x1]=(col|(( col>>8 ) &0x030303)*0xFF)|(col2&0x00FF00); // Лимитер + возвращаем нужный канал
}
u2+=du;
            }

            ++y_start;
            v_start += dv;
         }

      }
      
final void renderMulYellow(DirectX7 g3d, Texture texture,int x_start,int y_start,int x_end,int y_end,int u_start,int v_start,int du,int dv) {
      int[] tex = texture.rImg.img;
      int[] rgb = g3d.display;
      int rgbWidth = g3d.width;
      int texw = texture.rImg.w;
      int texh = texture.rImg.h;
      int texLength = tex.length;
      int u2,col,bri,col2;
                while(y_start < y_end) {
            int x1 = x_start + rgbWidth * y_start;
            texh = x_end + rgbWidth * y_start;
u2 = (v_start & -4096) * texw + u_start;
           if(Main.s60Optimization==1) {
               for(; texh-x1>=2; x1+=2) {
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1]; // Получаем цвет
col2=col;
col=(((  ( (col&0xFF0000)*bri )  &0xFE000000  )) | ((  ( (col&0x00FF00)*bri )  &0x00FE0000  ))) >>>6; // Затемняем и осветляем
rgb[x1]=(col|(( col>>8 ) &0x030303)*0xFF)|(col2&0x0000FF); // Лимитер
}
u2+=du;
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1+1]; // Получаем цвет
col2=col;
col=(((  ( (col&0xFF0000)*bri )  &0xFE000000  )) | ((  ( (col&0x00FF00)*bri )  &0x00FE0000  ))) >>>6; // Затемняем и осветляем
rgb[x1]=(col|(( col>>8 ) &0x030303)*0xFF)|(col2&0x0000FF); // Лимитер
}
u2+=du;
            }
           }
            for(; x1 < texh; x1++) {
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1]; // Получаем цвет
col2=col;
col=(((  ( (col&0xFF0000)*bri )  &0xFE000000  )) | ((  ( (col&0x00FF00)*bri )  &0x00FE0000  ))) >>>6; // Затемняем и осветляем
rgb[x1]=(col|(( col>>8 ) &0x030303)*0xFF)|(col2&0x0000FF); // Лимитер
}
u2+=du;
            }

            ++y_start;
            v_start += dv;
         }

      }

final void renderMulAqua(DirectX7 g3d, Texture texture,int x_start,int y_start,int x_end,int y_end,int u_start,int v_start,int du,int dv) {
      int[] tex = texture.rImg.img;
      int[] rgb = g3d.display;
      int rgbWidth = g3d.width;
      int texw = texture.rImg.w;
      int texh = texture.rImg.h;
      int texLength = tex.length;
      int u2,col,bri,col2;
                while(y_start < y_end) {
            int x1 = x_start + rgbWidth * y_start;
            texh = x_end + rgbWidth * y_start;
u2 = (v_start & -4096) * texw + u_start;
           if(Main.s60Optimization==1) {
               for(; texh-x1>=2; x1+=2) {
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1]; // Получаем цвет
col2=col;
col=(((  ( (col&0x0000FF)*bri )  &0x0000FE00  )) | ((  ( (col&0x00FF00)*bri )  &0x00FE0000  ))) >>>6; // Затемняем и осветляем
rgb[x1]=(col|(( col>>8 ) &0x030303)*0xFF)|(col2&0xFF0000); // Лимитер
}
u2+=du;
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1+1]; // Получаем цвет
col2=col;
col=(((  ( (col&0x0000FF)*bri )  &0x0000FE00  )) | ((  ( (col&0x00FF00)*bri )  &0x00FE0000  ))) >>>6; // Затемняем и осветляем
rgb[x1]=(col|(( col>>8 ) &0x030303)*0xFF)|(col2&0xFF0000); // Лимитер
}
u2+=du;
            }
           }
            for(; x1 < texh; x1++) {
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1]; // Получаем цвет
col2=col;
col=(((  ( (col&0x0000FF)*bri )  &0x0000FE00  )) | ((  ( (col&0x00FF00)*bri )  &0x00FE0000  ))) >>>6; // Затемняем и осветляем
rgb[x1]=(col|(( col>>8 ) &0x030303)*0xFF)|(col2&0xFF0000); // Лимитер
}
u2+=du;
            }

            ++y_start;
            v_start += dv;
         }

      }

      
            final void renderMul(DirectX7 g3d, Texture texture,int x_start,int y_start,int x_end,int y_end,int u_start,int v_start,int du,int dv) {
      int[] tex = texture.rImg.img;
      int[] rgb = g3d.display;
      int rgbWidth = g3d.width;
      int texw = texture.rImg.w;
      int texh = texture.rImg.h;
      int texLength = tex.length;
      int u2,col,bri;
                while(y_start < y_end) {
            int x1 = x_start + rgbWidth * y_start;
            texh = x_end + rgbWidth * y_start;
u2 = (v_start & -4096) * texw + u_start;
           if(Main.s60Optimization==1) {
               for(; texh-x1>=2; x1+=2) {
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1]; // Получаем цвет
col=((  ( (col&0xFF00FF)*bri )  &0xFE00FE00  )  | (   ( (col&0x00FF00)*bri  )  &0x00FE0000  )   ) >>>6; // Затемняем и осветляем
rgb[x1]=col|(( col>>8 ) &0x030303)*0xFF; // Лимитер
}
u2+=du;
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1+1]; // Получаем цвет
col=((  ( (col&0xFF00FF)*bri )  &0xFE00FE00  )  | (   ( (col&0x00FF00)*bri  )  &0x00FE0000  )   ) >>>6; // Затемняем и осветляем
rgb[x1+1]=col|(( col>>8 ) &0x030303)*0xFF; // Лимитер
}
u2+=du;
            }
           }
            for(; x1 < texh; x1++) {
bri=((tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength]&0x000000FF)); // Получаем яркость
if(bri!=69) {
col=rgb[x1]; // Получаем цвет
col=((  ( (col&0xFF00FF)*bri )  &0xFE00FE00  )  | (   ( (col&0x00FF00)*bri  )  &0x00FE0000  )   ) >>>6; // Затемняем и осветляем
rgb[x1]=col|(( col>>8 ) &0x030303)*0xFF; // Лимитер
}
u2+=du;
            }

            ++y_start;
            v_start += dv;
         }

      }
      
      
      
      final void renderOpaque(DirectX7 g3d, Texture texture,int x_start,int y_start,int x_end,int y_end,int u_start,int v_start,int du,int dv) {
      int[] tex = texture.rImg.img;
      int[] rgb = g3d.display;
      int rgbWidth = g3d.width;
      int texw = texture.rImg.w;
      int texh = texture.rImg.h;
      int texLength = tex.length;
      int u2,col,ca,cols;
                while(y_start < y_end) {
            int x1 = x_start + rgbWidth * y_start;
            texh = x_end + rgbWidth * y_start;
u2 = (v_start & -4096) * texw + u_start;
           if(Main.s60Optimization==1) {
               for(; texh-x1>=3; x1+=3) {
col=tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength];
ca=(col >> 24) & 0xff;
if(ca!=0) {
cols=rgb[x1];
rgb[x1]=(   (  ( (col&0xFF00FF)*(ca ) + ( cols&0xFF00FF)*(255-ca))  &0xFF00FF00  )  | (   ( (col&0x00FF00)*(ca )  + (cols&0x00FF00)*(255-ca))  &0x00FF0000  )   ) >>>8;
}
u2+=du;

col=tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength];
ca=(col >> 24) & 0xff;
if(ca!=0) {
cols=rgb[x1+1];
rgb[x1+1]=(   (  ( (col&0xFF00FF)*(ca ) + ( cols&0xFF00FF)*(255-ca))  &0xFF00FF00  )  | (   ( (col&0x00FF00)*(ca )  + (cols&0x00FF00)*(255-ca))  &0x00FF0000  )   ) >>>8;
}
u2+=du;
col=tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength];
ca=(col >> 24) & 0xff;
if(ca!=0) {
cols=rgb[x1+2];
rgb[x1+2]=(   (  ( (col&0xFF00FF)*(ca ) + ( cols&0xFF00FF)*(255-ca))  &0xFF00FF00  )  | (   ( (col&0x00FF00)*(ca )  + (cols&0x00FF00)*(255-ca))  &0x00FF0000  )   ) >>>8;
}
u2+=du;
            }
           }
            for(; x1 < texh; x1++) {
col=tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength];
ca=(col >> 24) & 0xff;
if(ca!=0) {
cols=rgb[x1];
rgb[x1]=(   (  ( (col&0xFF00FF)*(ca ) + ( cols&0xFF00FF)*(255-ca))  &0xFF00FF00  )  | (   ( (col&0x00FF00)*(ca )  + (cols&0x00FF00)*(255-ca))  &0x00FF0000  )   ) >>>8;
}
u2+=du;
            }

            ++y_start;
            v_start += dv;
         }

      }
      
      final void renderOpaqueFog(DirectX7 g3d, Texture texture,int x_start,int y_start,int x_end,int y_end,int u_start,int v_start,int du,int dv) {
      int[] tex = texture.rImg.img;
      int[] rgb = g3d.display;
      int rgbWidth = g3d.width;
      int texw = texture.rImg.w;
      int texh = texture.rImg.h;
      int texLength = tex.length;
      int u2,col,ca,cols,power;
      power=0;
if(DirectX7.fDist!=0) {
power=-this.pos.rz*255/(DirectX7.fDist);
}
if(power>255) power=255;
if(power<0) power=0;
int invpower=255-power;
int f2 =(DirectX7.fogc&0xFF00FF)*power;
int f22=(DirectX7.fogc&0x00FF00)*power;
if(DirectX7.standartDrawmode==6) f2=f22=0;
                while(y_start < y_end) {
            int x1 = x_start + rgbWidth * y_start;
            texh = x_end + rgbWidth * y_start;
u2 = (v_start & -4096) * texw + u_start;
           if(Main.s60Optimization==1) {
               for(; texh-x1>=2; x1+=2) {
col=tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength];
ca=(col >> 24) & 0xff;
if(ca!=0) {
cols=rgb[x1];
col=(   (  ( (col&0xFF00FF)*invpower + f2)  &0xFF00FF00  )  | (   ( (col&0x00FF00)*invpower  + f22)  &0x00FF0000  )   ) >>>8;
rgb[x1]=(   (  ( (col&0xFF00FF)*(ca ) + ( cols&0xFF00FF)*(255-ca))  &0xFF00FF00  )  | (   ( (col&0x00FF00)*(ca )  + (cols&0x00FF00)*(255-ca))  &0x00FF0000  )   ) >>>8;
}
u2+=du;

col=tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength];
ca=(col >> 24) & 0xff;
if(ca!=0) {
cols=rgb[x1+1];
col=(   (  ( (col&0xFF00FF)*invpower + f2)  &0xFF00FF00  )  | (   ( (col&0x00FF00)*invpower  + f22)  &0x00FF0000  )   ) >>>8;
rgb[x1+1]=(   (  ( (col&0xFF00FF)*(ca ) + ( cols&0xFF00FF)*(255-ca))  &0xFF00FF00  )  | (   ( (col&0x00FF00)*(ca )  + (cols&0x00FF00)*(255-ca))  &0x00FF0000  )   ) >>>8;
}
u2+=du;
            }
           }
            for(; x1 < texh; x1++) {
col=tex[(u2 >> 12 & Integer.MAX_VALUE) % texLength];
ca=(col >> 24) & 0xff;
if(ca!=0) {
cols=rgb[x1];
col=(   (  ( (col&0xFF00FF)*invpower + f2)  &0xFF00FF00  )  | (   ( (col&0x00FF00)*invpower  + f22)  &0x00FF0000  )   ) >>>8;
rgb[x1]=(   (  ( (col&0xFF00FF)*(ca ) + ( cols&0xFF00FF)*(255-ca))  &0xFF00FF00  )  | (   ( (col&0x00FF00)*(ca )  + (cols&0x00FF00)*(255-ca))  &0x00FF0000  )   ) >>>8;
}
u2+=du;
            }

            ++y_start;
            v_start += dv;
         }

      }
}
