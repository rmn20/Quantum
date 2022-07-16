package code.Gameplay.Map;

import code.AI.Player;
import code.Math.MathUtils;
import code.Math.Matrix;
import code.Rendering.MultyTexture;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.M3GParser;
import code.Rendering.Meshes.Mesh;
import code.Rendering.Texture;
import code.utils.Asset;
import javax.microedition.lcdui.Graphics;

public class Skybox {

    private byte mode = 0;
	
    private Matrix matrix;
    private Mesh mesh;
    public MultyTexture texture;
	
    private int color, groundColor;
    private Texture tex;
	private float repeatX, repeatY;
	private int lowestDegree;
	private float horizonScale;
	private float horizonOffset;
	
    public int x1;
    public int y1;
    public int x2;
    public int y2;
	
    private boolean resetViewport;
    public boolean skyboxAlways = false;
    public boolean draw = true;
	
    public Texture skyLighting;
    public boolean lighting = false;
    public long lastLighting = -1;
    
    public float rotateX, rotateY;
    
    public Skybox() {
    }

    public Skybox(String modelPath, String texturePath) {
        resetViewport = false;
        skyboxAlways = false;
        mode = 0;
		
        matrix = new Matrix();
		texture = new MultyTexture(texturePath, true);
        mesh = Room.loadMeshes(modelPath, 7.0F, 7.0F, 7.0F, texture)[0];
    }

    public Skybox(int colors) {
        resetViewport = false;
        skyboxAlways = false;
        mode = 1;
        color = colors;
    }

    public Skybox(Texture colors, int lowestDegree, float horizonScale, float horizonOffset) {
        resetViewport = false;
        skyboxAlways = false;
        mode = 3;
        tex = colors;
		
        this.lowestDegree = lowestDegree;
        this.horizonScale = horizonScale;
		this.horizonOffset = horizonOffset;
    }

    public Skybox(Texture colors, 
			float repeatX, float repeatY,
			int lowestDegree, 
			float horizonScale, float horizonOffset,
			int color, int groundColor) {
        resetViewport = false;
        skyboxAlways = false;
        mode = 2;
        tex = colors;
		
		this.repeatX = repeatX;
		this.repeatY = repeatY;
        this.lowestDegree = lowestDegree;
        this.horizonScale = horizonScale;
		this.horizonOffset = horizonOffset;
		
		this.color = color;
		this.groundColor = groundColor;
    }

    public void destroy() {
        matrix = null;
        texture = null;
        
        if (mesh != null) mesh.destroy();
        mesh = null;

        tex = null;

    }

    public void resetViewport() {
        resetViewport = true;
        draw = false;
    }

    public void addViewport(int ax1, int ay1, int ax2, int ay2) {
        if (resetViewport) {
            resetViewport = false;
            x1 = ax1;
            y1 = ay1;
            x2 = ax2;
            y2 = ay2;
        } else {
            if (ax1 < x1) x1 = ax1;
            if (ay1 < y1) y1 = ay1;
            if (ax2 > x2) x2 = ax2;
            if (ay2 > y2) y2 = ay2;
        }
    }

	public final void render(Graphics g, DirectX7 g3d) {
		if(skyboxAlways) {
			x1 = 0;
			y1 = 0;
			x2 = g3d.width;
			y2 = g3d.height;
		}

		if(lighting) {
			if(skyLighting == null) g3d.clearDisplay(0xe5e5e5);
			else if(mode == 3) renderGradient(g3d, skyLighting, lowestDegree, horizonScale, horizonOffset);
			else renderGradient(g3d, skyLighting, -91, 1, 0);

			if(lighting) drawLighting(g3d);
			lighting = false;
			
		} else if(mode == 0) {
			matrix.setIdentity();
			matrix.setPosition(g3d.getCamera().m03, g3d.getCamera().m13, g3d.getCamera().m23);
			Matrix fm = g3d.computeFinalMatrix(matrix);
			
			g3d.transformAndProjectVertices(mesh, fm);
			mesh.getTexture().updateAnimation();
			g3d.addMesh(mesh, x1, y1, x2, y2);
			
		} else if(mode == 1) {
			g3d.clearDisplay(color);
			
		} else if(mode == 2) {
			render2DSkybox(g3d);

		} else if(mode == 3) {
			renderGradient(g3d, tex, lowestDegree, horizonScale, horizonOffset);
			
		}
    }
    
    void renderGradient(DirectX7 g3d, Texture texs, 
			int lowestDegree, float horizonScale, float horizonOffset) {
        if (x2 - x1 == 0 || y2 - y1 == 0) return;
            
        int horizonY = (int) (g3d.height/2 + rotateX*g3d.height/g3d.fovY);
        horizonY = (horizonY-g3d.centreY) *g3d.distY /(11 + g3d.distY) + g3d.centreY;
		horizonY -= horizonOffset * g3d.height / g3d.fovY;
            
        int ySize = (int) (float) (90*horizonScale*g3d.height/g3d.fovY);
        
        int rx1 = y1*g3d.width;
        int yy2 = y2;
        
        if(lowestDegree!=-91) {
            int horCrop = (int) (g3d.height/2 - lowestDegree*g3d.height/g3d.fovY  + rotateX*g3d.height/g3d.fovY);
            horCrop = (horCrop-g3d.centreY) *g3d.distY / (11 + g3d.distY) + g3d.centreY;
            if(y2>horCrop) y2=horCrop;
        }
        
        int rx2 = y2*g3d.width;
        
        final int rgb[] = g3d.display;
        final int tex[] = texs.rImg.img;
        int step = ySize / texs.rImg.h;
        if(step==0) step=1;

        int yp,yp2,col,mix;
        
        int gradientBegin = horizonY - ySize;
        if(gradientBegin>=g3d.height) return;
        
        if(gradientBegin > 0 && gradientBegin<y2) {
            int rx3 = gradientBegin * g3d.width;
            col =   (
                    (  ( (tex[0]&0xFF00FF)*255)  &0xFF00FF00  )| 
                    (  ( (tex[0]&0x00FF00)*255)  &0x00FF0000  )   
                    ) >>>8;
            
            while( rx3 - rx1 >= 6 ) {
                rgb[rx1]=rgb[rx1+1]=rgb[rx1+2]=rgb[rx1+3]=rgb[rx1+4]=rgb[rx1+5]=col;
                rx1+=6;
            }
            
            while( rx1 < rx3 ) {
                rgb[rx1]=col;
                rx1++;
            }
        }
        
        gradientBegin = horizonY + ySize;
        
        if(gradientBegin > 0 && gradientBegin<y2) {
            rx2 = gradientBegin * g3d.width;
        }
        
        while (rx1 < rx2) {
            int yy = rx1/g3d.width;
            
            int mix2 = (yy-horizonY);
            if(mix2<0) mix2=-mix2;
            yp = texs.rImg.h - mix2/step;
            yp2 = yp-1;
            
            if(yp>=texs.rImg.h) yp=texs.rImg.h-1;
            if(yp2>=texs.rImg.h) yp2=yp;
            if(yp<0) yp=0;
            if(yp2<0) yp2=0;
            
            mix2 = (mix2%step)*255/step;
            
            col = tex[yp];
            int col2 = tex[yp2];

            col =   (
                    (  ( (col&0xFF00FF)*(mix = 255-mix2) + (col2&0xFF00FF)*mix2)  &0xFF00FF00  )| 
                    (  ( (col&0x00FF00)*mix + (col2&0x00FF00)*mix2)  &0x00FF0000  )   
                    ) >>>8;
            
            int rx3 = rx1+g3d.width;
            
            
            while( rx3 - rx1 >= 6 ) {
                rgb[rx1]=col;
                rgb[rx1+1]=col;
                rgb[rx1+2]=col;
                rgb[rx1+3]=col;
                rgb[rx1+4]=col;
                rgb[rx1+5]=col;
                rx1+=6;
            }
            
            while( rx1 < rx3 ) {
                rgb[rx1]=col;
                rx1++;
            }
            
        }
        
        if(gradientBegin > 0 && gradientBegin<y2) {
            rx2 = y2*g3d.width;
            col =   (
                    (  ( (tex[0]&0xFF00FF)*255)  &0xFF00FF00  )| 
                    (  ( (tex[0]&0x00FF00)*255)  &0x00FF0000  )   
                    ) >>>8;
            
            while( rx2 - rx1 >= 6 ) {
                rgb[rx1]=rgb[rx1+1]=rgb[rx1+2]=rgb[rx1+3]=rgb[rx1+4]=rgb[rx1+5]=col;
                rx1+=6;
            }
            
            while( rx1 < rx2 ) {
                rgb[rx1]=col;
                rx1++;
            }
        }

    }

    void render2DSkybox(DirectX7 g3d) {
		if(x2 - x1 == 0 || y2 - y1 == 0) return;
			
		int beginx = (int) ((360 - rotateY - g3d.fovX / 2) * tex.rImg.w * repeatX / 360);
		int endx = (int) ((360 - rotateY + g3d.fovX / 2) * tex.rImg.w * repeatX / 360);
            
        int horizonY = (int) (g3d.height/2 + rotateX*g3d.height/g3d.fovY);
        horizonY = (horizonY-g3d.centreY) *g3d.distY /(11 + g3d.distY) + g3d.centreY;
		horizonY -= horizonOffset * g3d.height / g3d.fovY;
            
        int ySize = (int) (float) (90*horizonScale*g3d.height/g3d.fovY);

		int beginy = horizonY - ySize;
		int endy = horizonY + ySize;
		if(endy <= beginy) endy = beginy + 1;
        
        if(lowestDegree!=-91) {
            int horCrop = (int) (g3d.height/2 - lowestDegree*g3d.height/g3d.fovY  + rotateX*g3d.height/g3d.fovY);
            horCrop = (horCrop-g3d.centreY) *g3d.distY / (11 + g3d.distY) + g3d.centreY;
            if(y2>horCrop) y2=horCrop;
        }
		
        final int fp = 12;

        int sx = beginx << fp;

        final int width = g3d.width;

        final int xstep = ((endx << fp) - sx) / width;
        final int ystep = (int) ((tex.rImg.h << fp) * repeatY) / (endy - beginy);
        render2D(g3d, sx, beginy, endy, xstep, ystep);
    }

    void render2D(DirectX7 g3d, int uStart, int startY, int endY, int du, int dv) {

        final int rgb[] = g3d.display;
        final int width = g3d.width;
        final int fp = 12;
		
        final int texRgb[] = tex.rImg.img;
		
        final int texW = tex.rImg.w;
        final int texWlen = tex.rImg.w - 1;
		final int texWBits = tex.rImg.widthBIT;
        final int texH = tex.rImg.h;
		
        while (uStart < 0) uStart += texW << fp;

		int texStartY = Math.max(y1, Math.min(y2, startY));
		int texEndY = Math.max(y1, Math.min(y2, endY));
		
		for(int y=y1; y<texStartY; y++) {
			int yy = y * width;
			fillLine(rgb, x1 + yy, x2 + yy, color);
		}
		
		int v = dv * (texStartY - startY);
        for (int y = texStartY; y < texEndY; y++) {
			
            int u = uStart - du;
            int rx1 = y * width;
            int rx2 = rx1 + width;
			
            if (x1 > 0) {
                rx1 += x1;
                u += du * x1;
            }
            if (x2 < width) rx2 -= width - x2;
			
            int ysa = ((v >>> fp) % texH) << texWBits;

            while (rx2 - rx1 >= 4) {
                rgb[rx1] = texRgb[(((u += du) >>> fp) & texWlen) + ysa];
                rgb[rx1 + 1] = texRgb[(((u += du) >>> fp) & texWlen) + ysa];
                rgb[rx1 + 2] = texRgb[(((u += du) >>> fp) & texWlen) + ysa];
                rgb[rx1 + 3] = texRgb[(((u += du) >>> fp) & texWlen) + ysa];
                rx1 += 4;
            }
            while (rx1 < rx2) {
                rgb[rx1] = texRgb[(((u += du) >>> fp) & texWlen) + ysa];
                rx1 += 1;
            }
			
            v += dv;
        }
		
		for(int y=texEndY; y<y2; y++) {
			int yy = y * width;
			fillLine(rgb, x1 + yy, x2 + yy, groundColor);
		}

    }
	
	private void fillLine(int[] rgb, int x1, int x2, int col) {
		while(x2 - x1 >= 6) {
			rgb[x1] = col;
			rgb[x1 + 1] = col;
			rgb[x1 + 2] = col;
			rgb[x1 + 3] = col;
			rgb[x1 + 4] = col;
			rgb[x1 + 5] = col;
			x1 += 6;
		}

		while(x1 < x2) {
			rgb[x1] = col;
			x1++;
		}
	}

    public Mesh getMesh() {
        return mesh;
    }
    
    public void drawLighting(DirectX7 g3d) {
        MathUtils.randomCall = 0;
        
        if(MathUtils.preudoRandom(lastLighting,8)==0) return;
        
        int horizonY = (int) (g3d.height/2 + rotateX*g3d.height/g3d.fovY );
        horizonY = (horizonY-g3d.centreY) *g3d.distY/(11 + g3d.distY) + g3d.centreY;
            
        int ySize = 90*g3d.height/g3d.fovY;
        
        int fat = 3*74/g3d.fovY;
        if(fat<1) fat=1;
        
        int by = horizonY-ySize;
        int bx = MathUtils.preudoRandom(lastLighting,g3d.width);
        int deg20 = g3d.width*20/g3d.fovX;
        int mx = bx + (MathUtils.preudoRandom(lastLighting,100) - 50)*deg20/100;
        int my = horizonY - ySize/3 + MathUtils.preudoRandom(lastLighting,100)*g3d.height*10/g3d.fovY/100;
        
        g3d.drawLine(bx, by, mx, my, fat, 0xffffff);
        if(MathUtils.preudoRandom(lastLighting,4)==0) {
            int rnd = (MathUtils.preudoRandom(lastLighting,100) - 50)*deg20/25;
            g3d.drawLine(mx/2+bx/2, my/2+by/2, mx+rnd, horizonY, fat, 0xffffff); //Additional spark idk
        }
        
        int sparksCount = 2 + MathUtils.preudoRandom(lastLighting,3); //Lightings count
        int fat2 = fat-1;
        if(fat2<1) fat2=1;
        for(int i=0;i<sparksCount;i++) {
            int rnd = (MathUtils.preudoRandom(lastLighting,100) - 50)*deg20/50; //Lighting direction
            if(MathUtils.preudoRandom(lastLighting,4)==0) { //Subdiv lighting
                int rnd2 = (MathUtils.preudoRandom(lastLighting,100) - 50)*deg20/150; //Small lightings direction
                g3d.drawLine(mx,my,mx+rnd/2,horizonY/2+my/2,fat2,0xfdfdfd);
                
                g3d.drawLine(mx+rnd/2,horizonY/2+my/2,mx+rnd/2+rnd2/2,horizonY,fat2,0xfdfdfd);
                g3d.drawLine(mx+rnd/2,horizonY/2+my/2,mx+rnd/2-rnd2/2,horizonY,fat2,0xfdfdfd);
            } else g3d.drawLine(mx,my,mx+rnd,horizonY,fat2,0xfdfdfd); //One lighting
        }
        
    }
}
