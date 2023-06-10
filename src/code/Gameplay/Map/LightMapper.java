package code.Gameplay.Map;

import code.Rendering.Meshes.*;
import code.Rendering.*;
import code.Collision.*;
import code.Gameplay.GameScreen;
import code.HUD.Base.TextView;
import code.Math.MathUtils;
import code.Math.Vector3D;
import code.utils.Main;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.OutputConnection;
import javax.microedition.io.file.FileConnection;
/**
 *
 * @author Roman Lahin
 */
public class LightMapper implements Runnable {
    
    public static int aoDistance,aoIntensity;
    public static int[] ambientLight,skyLight,sunLight,giIntensity,giFallOff,lumFromTextures;
    public static int ambientLightMid,skyLightIntensityMid,sunLightIntensityMid; //Для освещения мешей(todo)
    public static int giRays;
    public static boolean cameraVectorLight;;
    public static boolean bwGI, bwTexGI, allRooms, fastCalc, slCheap;
    public static int raysC;
    public static Ray ray;
    
    static final int meterUnit = 885;
    public static final int sqrMeter = meterUnit*meterUnit;
    public static Light[] lights;
    
    public final static int perPolygonSleep=1;
    private static House thouse;
    private static Mesh[] tmeshes;
    private static String tpath;
    protected Thread thread;
    private boolean run;
    
    public static int smoothMax = 2700;
    
    static int[] rm,gm,bm,countm;
    static long[] dm;
    static boolean[] validm;
    static int[] posesm;
    
    static {
        reset();
    }
    
    public static void reset() {
        aoDistance=2048;
        aoIntensity=0;
        ambientLight=new int[]{64,64,64};
        ambientLightMid=64;
        skyLight=new int[]{512,512,515};
        skyLightIntensityMid=512;
        sunLight=new int[]{0,0,0};
        sunLightIntensityMid=0;
        cameraVectorLight=false;
        giRays=0;
        giIntensity=new int[]{768,768,768};
        giFallOff=new int[]{230,230,230};
        smoothMax=2700;
        bwGI=false;
		bwTexGI=false;
        lumFromTextures=new int[]{0,0,0};
        allRooms=true;
        raysC=7;
        fastCalc = true;
        slCheap = false;
    }
    
    public static void setFastCalc(boolean fast) {
        if(fast) raysC = 17;
        fastCalc = fast;
    }
    
    public static void setRays(int rays) {
        raysC = rays*2+1;
    }
    
    public static void setslCheap(boolean i) {
        slCheap = i;
    }
    
    public static void ambientLightSet(int[] c) {
        if(c==null) return;
        
        ambientLight[0]=c[0];
        if(c.length==1) {
            ambientLight[1]=ambientLight[2]=ambientLight[0];
        } else {  
            ambientLight[1]=c[1];
            ambientLight[2]=c[2];
        }
        ambientLightMid=(ambientLight[1]+ambientLight[2]+ambientLight[0])/3;
    }
    
    public static void lumFromTexturesSet(int[] c) {
        if(c==null) return;
        
        lumFromTextures[0]=c[0];
        if(c.length==1) {
            lumFromTextures[1]=lumFromTextures[2]=lumFromTextures[0];
        } else {  
            lumFromTextures[1]=c[1];
            lumFromTextures[2]=c[2];
        }
    }
    
    public static void skyLightIntensitySet(int[] c) {
        if(c==null) return;
        
        skyLight[0]=c[0];
        if(c.length==1) {
            skyLight[1]=skyLight[2]=skyLight[0];
        } else {  
            skyLight[1]=c[1];
            skyLight[2]=c[2];
        }
        skyLightIntensityMid=(skyLight[1]+skyLight[2]+skyLight[0])/3;
    }
    
    public static void sunLightIntensitySet(int[] c) {
        if(c==null) return;
        
        sunLight[0]=c[0];
        if(c.length==1) {
            sunLight[1]=sunLight[2]=sunLight[0];
        } else {  
            sunLight[1]=c[1];
            sunLight[2]=c[2];
        }
        sunLightIntensityMid=(sunLight[1]+sunLight[2]+sunLight[0])/3;
    }
    
    public static void giIntensitySet(int[] c) {
        if(c==null) return;
        
        giIntensity[0]=c[0];
        if(c.length==1) {
            giIntensity[1]=giIntensity[2]=giIntensity[0];
        } else {  
            giIntensity[1]=c[1];
            giIntensity[2]=c[2];
        }
    }
    
    public static void giFallOffSet(int[] c) {
        if(c==null) return;
        
        giFallOff[0]=c[0];
        if(c.length==1) {
            giFallOff[1]=giFallOff[2]=giFallOff[0];
        } else {  
            giFallOff[1]=c[1];
            giFallOff[2]=c[2];
        }
    }
    
    public static final void generateLightMapSaveThread(House house2, Mesh[] meshes2, String path2) {
        tpath=path2;
        tmeshes=meshes2;
        thouse=house2;
        new LightMapper().start();
    }
    
    public void start() {
        if(run) return;
        run = true;
        thread = new Thread(this);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }
    
    protected void stop() {
        if(!run) return;
        run = false;
        thread = null;
    }
    
    public static final void generateLightMap(House house, Mesh[] meshes) {
        long totalBeginTime=System.currentTimeMillis();
        Room[] rooms=house.getRooms();
        ray = new Ray();
        ray.findNearest = true;
        ray.infinity = true;
        ray.ignoreNonShadowed = true;
        ray.onlyCollidable = false;
        
        rm = new int[raysC*raysC*raysC];
        gm = new int[rm.length];
        bm = new int[rm.length];
        dm = new long[rm.length];
        countm = new int[rm.length];
        posesm = new int[rm.length*3];
        validm = new boolean[rm.length];
        
        for(int x=0; x<raysC; x++) {
            for(int y=0; y<raysC; y++) {
                for(int z=0; z<raysC; z++) {
                    Vector3D t = new Vector3D(x-raysC/2,y-raysC/2,z-raysC/2);
                    Vector3D tn = new Vector3D();
                    tn.set(t); tn.setLength(raysC/2);
                    countm[(tn.x+raysC/2)+(tn.y+raysC/2)*raysC+(tn.z+raysC/2)*raysC*raysC]++;
                    if(t.equals(tn)) validm[x+y*raysC+z*raysC*raysC] = true;
                    posesm[(x+y*raysC+z*raysC*raysC)*3] = tn.x;
                    posesm[(x+y*raysC+z*raysC*raysC)*3+1] = tn.y;
                    posesm[(x+y*raysC+z*raysC*raysC)*3+2] = tn.z;
                }
            }
        }
        
        for(int i=0;i<rooms.length;i++) {
            Room room=rooms[i];
            if(room!=null) zeroBrightness(room);
        }
        
        print("Rays quality: "+(raysC-1)/2);
        
        ray.findNearest = false;
        long beginTime=System.currentTimeMillis();
        print("Generating sun and sky light...");
        for(int i=0;i<rooms.length;i++) {
            Room room=rooms[i];
            if(room!=null) calculateSkyLight(house,room,meshes);
        }
        ray.reset();
        ray.findNearest = true;
        
        print("Sun and sky light done in: "+(System.currentTimeMillis()/1000-beginTime/1000)+" seconds");
        
        if(aoIntensity!=0) {
            beginTime=System.currentTimeMillis();
            print("Generating ambient occulusion...");
            for(int i=0;i<rooms.length;i++) {
                Room room=rooms[i];
                if(room!=null) calculateAO(house,room,meshes);
            }
            ray.reset();
        
            print("Ambient occulusion done in: "+(System.currentTimeMillis()/1000-beginTime/1000)+" seconds");
        }
        
        ray.findNearest = false;
        beginTime=System.currentTimeMillis();
        
        print("Generating lights...");
        if(lights != null) for(int i=0;i<rooms.length;i++) {
            Room room=rooms[i];
            if(room!=null) calculateLights(house,room,meshes);
        }
        ray.reset();
        ray.findNearest = true;
        print("Lights done in: "+(System.currentTimeMillis()/1000-beginTime/1000)+" seconds");
        
        
        
        if(giRays>0) {
            beginTime = System.currentTimeMillis();
            print("Generating GI...");
            
            Mesh[] oldMeshes = new Mesh[rooms.length];
            
            for(int i=0; i<oldMeshes.length; i++) {
                Mesh mesh = rooms[i].fullMesh;
                RenderObject[] ro=new RenderObject[rooms[i].fullMesh.getPolygons().length];
                oldMeshes[i] = new Mesh(mesh.getVertices(),ro,mesh.getTexture());
                
                for(int x=0;x<ro.length;x++) {
                    RenderObject rOld=mesh.getPolygons()[x];
                    if(rOld instanceof LightedPolygon4V) {
                        ro[x]=new LightedPolygon4V((LightedPolygon4V)rOld);
                    } else if(rOld instanceof LightedPolygon3V) {
                        ro[x]=new LightedPolygon3V((LightedPolygon3V)rOld);
                    } else if(rOld instanceof ColorLightedPolygon4V) {
                        ro[x]=new ColorLightedPolygon4V((ColorLightedPolygon4V)rOld);
                    } else if(rOld instanceof ColorLightedPolygon3V) {
                        ro[x]=new ColorLightedPolygon3V((ColorLightedPolygon3V)rOld);
                    } else if(rOld instanceof Polygon4V) {
                        ro[x]=new Polygon4V((Polygon4V)rOld);
                    } else if(rOld instanceof Polygon3V) {
                        ro[x]=new Polygon3V((Polygon3V)rOld);
                    }
                }
            }
            Mesh[] reflected = new Mesh[rooms.length];
            
            for(int i=0; i<reflected.length; i++) {
                Mesh mesh = rooms[i].fullMesh;
                RenderObject[] ro=new RenderObject[rooms[i].fullMesh.getPolygons().length];
                reflected[i] = new Mesh(mesh.getVertices(),ro,mesh.getTexture());
                
                for(int x=0;x<ro.length;x++) {
                    RenderObject rOld=mesh.getPolygons()[x];
                    if(rOld instanceof LightedPolygon4V) {
                        ro[x]=new LightedPolygon4V((LightedPolygon4V)rOld);
                    } else if(rOld instanceof LightedPolygon3V) {
                        ro[x]=new LightedPolygon3V((LightedPolygon3V)rOld);
                    } else if(rOld instanceof ColorLightedPolygon4V) {
                        ro[x]=new ColorLightedPolygon4V((ColorLightedPolygon4V)rOld);
                    } else if(rOld instanceof ColorLightedPolygon3V) {
                        ro[x]=new ColorLightedPolygon3V((ColorLightedPolygon3V)rOld);
                    } else if(rOld instanceof Polygon4V) {
                        ro[x]=new Polygon4V((Polygon4V)rOld);
                    } else if(rOld instanceof Polygon3V) {
                        ro[x]=new Polygon3V((Polygon3V)rOld);
                    }
                }
            }
            
            int threads=0; 
            while(threads<giRays) {
                if(threads>0) {
                    lumFromTextures[0] = lumFromTextures[1] = lumFromTextures[2] = 0;
                }
                for(int i=0; i<rooms.length; i++) {
                    Room room = rooms[i];
                    if(room!=null) calculateGI(house, room, meshes, oldMeshes, reflected[i]);
                }
            
            
                    
                for(int i2=0; i2<reflected.length; i2++) {
                    Mesh mesh = reflected[i2];
                    RenderObject[] ro=new RenderObject[mesh.getPolygons().length];
                    oldMeshes[i2] = new Mesh(mesh.getVertices(),ro,mesh.getTexture());
                    
                    for(int x=0;x<ro.length;x++) {
                        RenderObject rOld=mesh.getPolygons()[x];
                        if(rOld instanceof LightedPolygon4V) {
                            ro[x]=new LightedPolygon4V((LightedPolygon4V)rOld);
                        } else if(rOld instanceof LightedPolygon3V) {
                            ro[x]=new LightedPolygon3V((LightedPolygon3V)rOld);
                        } else if(rOld instanceof ColorLightedPolygon4V) {
                            ro[x]=new ColorLightedPolygon4V((ColorLightedPolygon4V)rOld);
                        } else if(rOld instanceof ColorLightedPolygon3V) {
                            ro[x]=new ColorLightedPolygon3V((ColorLightedPolygon3V)rOld);
                        } else if(rOld instanceof Polygon4V) {
                            ro[x]=new Polygon4V((Polygon4V)rOld);
                        } else if(rOld instanceof Polygon3V) {
                            ro[x]=new Polygon3V((Polygon3V)rOld);
                        }
                    }
                }
                print("GI ray "+(threads+1)+" done!");
                threads++;
                ray.reset();
            }
            print("GI done in: " + (System.currentTimeMillis() / 1000 - beginTime / 1000) + " seconds");
        }
        
        ray = null;
        rm = gm = bm = countm = null;
        dm = null;
        validm = null;
        posesm = null;
        
        print("Lightmapping done in: "+(System.currentTimeMillis()/1000-totalBeginTime/1000)+" seconds");
        
    }
    
    private static void zeroBrightness(Room room) {
        RenderObject[] objs = room.fullMesh.getPolygons();
        
        
        for(int i=0;i<objs.length;i++) {
            if(objs[i] instanceof LightedPolygon3V) {
                LightedPolygon3V pol=(LightedPolygon3V)objs[i];
                pol.la=pol.lb=pol.lc=0;
            } else if(objs[i] instanceof LightedPolygon4V) {
                LightedPolygon4V pol=(LightedPolygon4V)objs[i];
                pol.la=pol.lb=pol.lc=pol.ld=0;
            } else if(objs[i] instanceof ColorLightedPolygon3V) {
                ColorLightedPolygon3V pol=(ColorLightedPolygon3V)objs[i];
                pol.ar=pol.ag=pol.ab=pol.br=pol.bg=pol.bb=pol.cr=pol.cg=pol.cb=0;
            } else if(objs[i] instanceof ColorLightedPolygon4V) {
                ColorLightedPolygon4V pol=(ColorLightedPolygon4V)objs[i];
                pol.ar=pol.ag=pol.ab=pol.br=pol.bg=pol.bb=pol.cr=pol.cg=pol.cb=pol.dr=pol.dg=pol.db=0;
            }
            
            if(perPolygonSleep!=0 && (i&3)==0) try{Thread.sleep(perPolygonSleep);} catch(Exception e) {};
        }
        
        
        
    }
    
    private static void calculateSkyLight(House house, Room room, Mesh[] meshes) {
        Vertex tmp=new Vertex();
        RenderObject[] objs = room.fullMesh.getPolygons();
        
        
        for(int i=0;i<objs.length;i++) {
            if(objs[i] instanceof LightedPolygon3V) {
                
                LightedPolygon3V pol=(LightedPolygon3V)objs[i];
                tmp.set(pol.a); tmp.add(pol.b.x,pol.b.y,pol.b.z); tmp.add(pol.c.x,pol.c.y,pol.c.z);  tmp.div(3,3,3);
                int litA=skySunLight(house,room,meshes,pol.a,pol.nx,pol.ny,pol.nz,tmp,pol).average();
                int litB=skySunLight(house,room,meshes,pol.b,pol.nx,pol.ny,pol.nz,tmp,pol).average();
                int litC=skySunLight(house,room,meshes,pol.c,pol.nx,pol.ny,pol.nz,tmp,pol).average();
                
                pol.la=Math.min(255,pol.la+litA);
                pol.lb=Math.min(255,pol.lb+litB);
                pol.lc=Math.min(255,pol.lc+litC);
                
            } else if(objs[i] instanceof LightedPolygon4V) {
                
                LightedPolygon4V pol=(LightedPolygon4V)objs[i];
                tmp.set(pol.a); tmp.add(pol.b.x,pol.b.y,pol.b.z); tmp.add(pol.c.x,pol.c.y,pol.c.z);
                tmp.add(pol.d.x,pol.d.y,pol.d.z); tmp.div(4,4,4);
                int litA=skySunLight(house,room,meshes,pol.a,pol.nx,pol.ny,pol.nz,tmp,pol).average();
                int litB=skySunLight(house,room,meshes,pol.b,pol.nx,pol.ny,pol.nz,tmp,pol).average();
                int litC=skySunLight(house,room,meshes,pol.c,pol.nx,pol.ny,pol.nz,tmp,pol).average();
                int litD=skySunLight(house,room,meshes,pol.d,pol.nx,pol.ny,pol.nz,tmp,pol).average();
                
                pol.la=Math.min(255,pol.la+litA);
                pol.lb=Math.min(255,pol.lb+litB);
                pol.lc=Math.min(255,pol.lc+litC);
                pol.ld=Math.min(255,pol.ld+litD);
                
            } else if(objs[i] instanceof ColorLightedPolygon3V) {
                
                ColorLightedPolygon3V pol=(ColorLightedPolygon3V)objs[i];
                tmp.set(pol.a); tmp.add(pol.b.x,pol.b.y,pol.b.z); tmp.add(pol.c.x,pol.c.y,pol.c.z);  tmp.div(3,3,3);
                Vector3D litA=skySunLight(house,room,meshes,pol.a,pol.nx,pol.ny,pol.nz,tmp,pol);
                Vector3D litB=skySunLight(house,room,meshes,pol.b,pol.nx,pol.ny,pol.nz,tmp,pol);
                Vector3D litC=skySunLight(house,room,meshes,pol.c,pol.nx,pol.ny,pol.nz,tmp,pol);
                
                pol.ar=Math.min(255,pol.ar+litA.x);
                pol.ag=Math.min(255,pol.ag+litA.y);
                pol.ab=Math.min(255,pol.ab+litA.z);
                pol.br=Math.min(255,pol.br+litB.x);
                pol.bg=Math.min(255,pol.bg+litB.y);
                pol.bb=Math.min(255,pol.bb+litB.z);
                pol.cr=Math.min(255,pol.cr+litC.x);
                pol.cg=Math.min(255,pol.cg+litC.y);
                pol.cb=Math.min(255,pol.cb+litC.z);
            } else if(objs[i] instanceof ColorLightedPolygon4V) {
                
                ColorLightedPolygon4V pol=(ColorLightedPolygon4V)objs[i];
                tmp.set(pol.a); tmp.add(pol.b.x,pol.b.y,pol.b.z); tmp.add(pol.c.x,pol.c.y,pol.c.z);
                tmp.add(pol.d.x,pol.d.y,pol.d.z); tmp.div(4,4,4);
                Vector3D litA=skySunLight(house,room,meshes,pol.a,pol.nx,pol.ny,pol.nz,tmp,pol);
                Vector3D litB=skySunLight(house,room,meshes,pol.b,pol.nx,pol.ny,pol.nz,tmp,pol);
                Vector3D litC=skySunLight(house,room,meshes,pol.c,pol.nx,pol.ny,pol.nz,tmp,pol);
                Vector3D litD=skySunLight(house,room,meshes,pol.d,pol.nx,pol.ny,pol.nz,tmp,pol);
                
                pol.ar=Math.min(255,pol.ar+litA.x);
                pol.ag=Math.min(255,pol.ag+litA.y);
                pol.ab=Math.min(255,pol.ab+litA.z);
                pol.br=Math.min(255,pol.br+litB.x);
                pol.bg=Math.min(255,pol.bg+litB.y);
                pol.bb=Math.min(255,pol.bb+litB.z);
                pol.cr=Math.min(255,pol.cr+litC.x);
                pol.cg=Math.min(255,pol.cg+litC.y);
                pol.cb=Math.min(255,pol.cb+litC.z);
                pol.dr=Math.min(255,pol.dr+litD.x);
                pol.dg=Math.min(255,pol.dg+litD.y);
                pol.db=Math.min(255,pol.db+litD.z);
            }
            if(perPolygonSleep!=0) try{Thread.sleep(perPolygonSleep);} catch(Exception e) {};
        }
        
        
        
    }
    
    private static void calculateAO(House house, Room room, Mesh[] meshes) {
        Vertex tmp=new Vertex();
        RenderObject[] objs = room.fullMesh.getPolygons();
        
        
        for(int i=0;i<objs.length;i++) {
            if(objs[i] instanceof LightedPolygon3V) {
                
                LightedPolygon3V pol=(LightedPolygon3V)objs[i];
                tmp.set(pol.a); tmp.add(pol.b.x,pol.b.y,pol.b.z); tmp.add(pol.c.x,pol.c.y,pol.c.z);  tmp.div(3,3,3);
                
                pol.la=mul(ambientOcculusion(house,room,meshes,pol.a,pol.nx,pol.ny,pol.nz,tmp,pol),pol.la);
                pol.lb=mul(ambientOcculusion(house,room,meshes,pol.b,pol.nx,pol.ny,pol.nz,tmp,pol),pol.lb);
                pol.lc=mul(ambientOcculusion(house,room,meshes,pol.c,pol.nx,pol.ny,pol.nz,tmp,pol),pol.lc);
            } else if(objs[i] instanceof LightedPolygon4V) {
                
                LightedPolygon4V pol=(LightedPolygon4V)objs[i];
                tmp.set(pol.a); tmp.add(pol.b.x,pol.b.y,pol.b.z); tmp.add(pol.c.x,pol.c.y,pol.c.z);
                tmp.add(pol.d.x,pol.d.y,pol.d.z); tmp.div(4,4,4);
                
                pol.la=mul(ambientOcculusion(house,room,meshes,pol.a,pol.nx,pol.ny,pol.nz,tmp,pol),pol.la);
                pol.lb=mul(ambientOcculusion(house,room,meshes,pol.b,pol.nx,pol.ny,pol.nz,tmp,pol),pol.lb);
                pol.lc=mul(ambientOcculusion(house,room,meshes,pol.c,pol.nx,pol.ny,pol.nz,tmp,pol),pol.lc);
                pol.ld=mul(ambientOcculusion(house,room,meshes,pol.d,pol.nx,pol.ny,pol.nz,tmp,pol),pol.ld);
            } else if(objs[i] instanceof ColorLightedPolygon3V) {
                
                ColorLightedPolygon3V pol=(ColorLightedPolygon3V)objs[i];
                tmp.set(pol.a); tmp.add(pol.b.x,pol.b.y,pol.b.z); tmp.add(pol.c.x,pol.c.y,pol.c.z);  tmp.div(3,3,3);
                
                int aoA=ambientOcculusion(house,room,meshes,pol.a,pol.nx,pol.ny,pol.nz,tmp,pol);
                int aoB=ambientOcculusion(house,room,meshes,pol.b,pol.nx,pol.ny,pol.nz,tmp,pol);
                int aoC=ambientOcculusion(house,room,meshes,pol.c,pol.nx,pol.ny,pol.nz,tmp,pol);
                
                pol.ar=mul(aoA,pol.ar);
                pol.ag=mul(aoA,pol.ag);
                pol.ab=mul(aoA,pol.ab);
                pol.br=mul(aoB,pol.br);
                pol.bg=mul(aoB,pol.bg);
                pol.bb=mul(aoB,pol.bb);
                pol.cr=mul(aoC,pol.cr);
                pol.cg=mul(aoC,pol.cg);
                pol.cb=mul(aoC,pol.cb);
            } else if(objs[i] instanceof ColorLightedPolygon4V) {
                
                ColorLightedPolygon4V pol=(ColorLightedPolygon4V)objs[i];
                tmp.set(pol.a); tmp.add(pol.b.x,pol.b.y,pol.b.z); tmp.add(pol.c.x,pol.c.y,pol.c.z);
                tmp.add(pol.d.x,pol.d.y,pol.d.z); tmp.div(4,4,4);
                
                
                int aoA=ambientOcculusion(house,room,meshes,pol.a,pol.nx,pol.ny,pol.nz,tmp,pol);
                int aoB=ambientOcculusion(house,room,meshes,pol.b,pol.nx,pol.ny,pol.nz,tmp,pol);
                int aoC=ambientOcculusion(house,room,meshes,pol.c,pol.nx,pol.ny,pol.nz,tmp,pol);
                int aoD=ambientOcculusion(house,room,meshes,pol.d,pol.nx,pol.ny,pol.nz,tmp,pol);
                
                pol.ar=mul(aoA,pol.ar);
                pol.ag=mul(aoA,pol.ag);
                pol.ab=mul(aoA,pol.ab);
                pol.br=mul(aoB,pol.br);
                pol.bg=mul(aoB,pol.bg);
                pol.bb=mul(aoB,pol.bb);
                pol.cr=mul(aoC,pol.cr);
                pol.cg=mul(aoC,pol.cg);
                pol.cb=mul(aoC,pol.cb);
                pol.dr=mul(aoD,pol.dr);
                pol.dg=mul(aoD,pol.dg);
                pol.db=mul(aoD,pol.db);
            }
            if(perPolygonSleep!=0) try{Thread.sleep(perPolygonSleep);} catch(Exception e) {};
        }
        
        
        
    }
    
    private static final int mul(int mul, int bri) {
        return (bri*mul+1)>>8;
    }
    
    private static void calculateLights(House house, Room room, Mesh[] meshes) {
        Vertex tmp=new Vertex();
        RenderObject[] objs = room.fullMesh.getPolygons();
        
        for(int i=0;i<objs.length;i++) {
            if(objs[i] instanceof LightedPolygon3V) {
                
                LightedPolygon3V pol=(LightedPolygon3V)objs[i];
                tmp.set(pol.a); tmp.add(pol.b.x,pol.b.y,pol.b.z); tmp.add(pol.c.x,pol.c.y,pol.c.z);  tmp.div(3,3,3);
                
                pol.la=add(lightCalcMini(house,room,meshes,pol.a,pol.nx,pol.ny,pol.nz,tmp).average(),pol.la);
                pol.lb=add(lightCalcMini(house,room,meshes,pol.b,pol.nx,pol.ny,pol.nz,tmp).average(),pol.lb);
                pol.lc=add(lightCalcMini(house,room,meshes,pol.c,pol.nx,pol.ny,pol.nz,tmp).average(),pol.lc);
            } else if(objs[i] instanceof LightedPolygon4V) {
                
                LightedPolygon4V pol=(LightedPolygon4V)objs[i];
                tmp.set(pol.a); tmp.add(pol.b.x,pol.b.y,pol.b.z); tmp.add(pol.c.x,pol.c.y,pol.c.z);
                tmp.add(pol.d.x,pol.d.y,pol.d.z); tmp.div(4,4,4);
                
                pol.la=add(lightCalcMini(house,room,meshes,pol.a,pol.nx,pol.ny,pol.nz,tmp).average(),pol.la);
                pol.lb=add(lightCalcMini(house,room,meshes,pol.b,pol.nx,pol.ny,pol.nz,tmp).average(),pol.lb);
                pol.lc=add(lightCalcMini(house,room,meshes,pol.c,pol.nx,pol.ny,pol.nz,tmp).average(),pol.lc);
                pol.ld=add(lightCalcMini(house,room,meshes,pol.d,pol.nx,pol.ny,pol.nz,tmp).average(),pol.ld);
            } else if(objs[i] instanceof ColorLightedPolygon3V) {
                
                ColorLightedPolygon3V pol=(ColorLightedPolygon3V)objs[i];
                tmp.set(pol.a); tmp.add(pol.b.x,pol.b.y,pol.b.z); tmp.add(pol.c.x,pol.c.y,pol.c.z);  tmp.div(3,3,3);
                
                Vector3D lA=lightCalcMini(house,room,meshes,pol.a,pol.nx,pol.ny,pol.nz,tmp);
                Vector3D lB=lightCalcMini(house,room,meshes,pol.b,pol.nx,pol.ny,pol.nz,tmp);
                Vector3D lC=lightCalcMini(house,room,meshes,pol.c,pol.nx,pol.ny,pol.nz,tmp);
                
                pol.ar=add(lA.x,pol.ar);
                pol.ag=add(lA.y,pol.ag);
                pol.ab=add(lA.z,pol.ab);
                pol.br=add(lB.x,pol.br);
                pol.bg=add(lB.y,pol.bg);
                pol.bb=add(lB.z,pol.bb);
                pol.cr=add(lC.x,pol.cr);
                pol.cg=add(lC.y,pol.cg);
                pol.cb=add(lC.z,pol.cb);
            } else if(objs[i] instanceof ColorLightedPolygon4V) {
                
                ColorLightedPolygon4V pol=(ColorLightedPolygon4V)objs[i];
                tmp.set(pol.a); tmp.add(pol.b.x,pol.b.y,pol.b.z); tmp.add(pol.c.x,pol.c.y,pol.c.z);
                tmp.add(pol.d.x,pol.d.y,pol.d.z); tmp.div(4,4,4);
                
                
                Vector3D lA=lightCalcMini(house,room,meshes,pol.a,pol.nx,pol.ny,pol.nz,tmp);
                Vector3D lB=lightCalcMini(house,room,meshes,pol.b,pol.nx,pol.ny,pol.nz,tmp);
                Vector3D lC=lightCalcMini(house,room,meshes,pol.c,pol.nx,pol.ny,pol.nz,tmp);
                Vector3D lD=lightCalcMini(house,room,meshes,pol.d,pol.nx,pol.ny,pol.nz,tmp);
                
                pol.ar=add(lA.x,pol.ar);
                pol.ag=add(lA.y,pol.ag);
                pol.ab=add(lA.z,pol.ab);
                pol.br=add(lB.x,pol.br);
                pol.bg=add(lB.y,pol.bg);
                pol.bb=add(lB.z,pol.bb);
                pol.cr=add(lC.x,pol.cr);
                pol.cg=add(lC.y,pol.cg);
                pol.cb=add(lC.z,pol.cb);
                pol.dr=add(lD.x,pol.dr);
                pol.dg=add(lD.y,pol.dg);
                pol.db=add(lD.z,pol.db);
            }
            if(perPolygonSleep!=0) try{Thread.sleep(perPolygonSleep);} catch(Exception e) {};
        }
        
        
        
    }
    
    private final static int add(int add, int bri) {
        return add + bri;
    }
    
    private static void calculateGI(House house, Room room, Mesh[] meshes, Mesh[] oldMeshes, Mesh reflection) {
        Vertex tmp=new Vertex();
        RenderObject[] objs = room.fullMesh.getPolygons();
        
        
        for(int i=0;i<objs.length;i++) {
            if(objs[i] instanceof LightedPolygon3V) {
                
                LightedPolygon3V pol=(LightedPolygon3V)objs[i];
                LightedPolygon3V pol2=(LightedPolygon3V)reflection.getPolygons()[i];
                tmp.set(pol.a); tmp.add(pol.b.x,pol.b.y,pol.b.z); tmp.add(pol.c.x,pol.c.y,pol.c.z);  tmp.div(3,3,3);
                
                int lA=GI(house,meshes,oldMeshes,pol.a,pol.nx,pol.ny,pol.nz,tmp).average();
                int lB=GI(house,meshes,oldMeshes,pol.b,pol.nx,pol.ny,pol.nz,tmp).average();
                int lC=GI(house,meshes,oldMeshes,pol.c,pol.nx,pol.ny,pol.nz,tmp).average();
                
                pol.la=add(lA,pol.la);
                pol.lb=add(lB,pol.lb);
                pol.lc=add(lC,pol.lc);
                pol2.la=Math.max(0,Math.min(255,lA));
                pol2.lb=Math.max(0,Math.min(255,lB));
                pol2.lc=Math.max(0,Math.min(255,lC));
            } else if(objs[i] instanceof LightedPolygon4V) {
                
                LightedPolygon4V pol=(LightedPolygon4V)objs[i];
                LightedPolygon4V pol2=(LightedPolygon4V)reflection.getPolygons()[i];
                tmp.set(pol.a); tmp.add(pol.b.x,pol.b.y,pol.b.z); tmp.add(pol.c.x,pol.c.y,pol.c.z);
                tmp.add(pol.d.x,pol.d.y,pol.d.z); tmp.div(4,4,4);
                
                int lA=GI(house,meshes,oldMeshes,pol.a,pol.nx,pol.ny,pol.nz,tmp).average();
                int lB=GI(house,meshes,oldMeshes,pol.b,pol.nx,pol.ny,pol.nz,tmp).average();
                int lC=GI(house,meshes,oldMeshes,pol.c,pol.nx,pol.ny,pol.nz,tmp).average();
                int lD=GI(house,meshes,oldMeshes,pol.d,pol.nx,pol.ny,pol.nz,tmp).average();
                
                pol.la=add(lA,pol.la);
                pol.lb=add(lB,pol.lb);
                pol.lc=add(lC,pol.lc);
                pol.ld=add(lD,pol.ld);
                pol2.la=Math.max(0,Math.min(255,lA));
                pol2.lb=Math.max(0,Math.min(255,lB));
                pol2.lc=Math.max(0,Math.min(255,lC));
                pol2.ld=Math.max(0,Math.min(255,lC));
            } else if(objs[i] instanceof ColorLightedPolygon3V) {
                
                ColorLightedPolygon3V pol=(ColorLightedPolygon3V)objs[i];
                ColorLightedPolygon3V pol2=(ColorLightedPolygon3V)reflection.getPolygons()[i];
                tmp.set(pol.a); tmp.add(pol.b.x,pol.b.y,pol.b.z); tmp.add(pol.c.x,pol.c.y,pol.c.z);  tmp.div(3,3,3);
                
                Vector3D lA=GI(house,meshes,oldMeshes,pol.a,pol.nx,pol.ny,pol.nz,tmp);
                Vector3D lB=GI(house,meshes,oldMeshes,pol.b,pol.nx,pol.ny,pol.nz,tmp);
                Vector3D lC=GI(house,meshes,oldMeshes,pol.c,pol.nx,pol.ny,pol.nz,tmp);
                
                pol.ar=add(lA.x,pol.ar);
                pol.ag=add(lA.y,pol.ag);
                pol.ab=add(lA.z,pol.ab);
                pol.br=add(lB.x,pol.br);
                pol.bg=add(lB.y,pol.bg);
                pol.bb=add(lB.z,pol.bb);
                pol.cr=add(lC.x,pol.cr);
                pol.cg=add(lC.y,pol.cg);
                pol.cb=add(lC.z,pol.cb);
                
                pol2.ar=Math.max(0,Math.min(255,lA.x));
                pol2.ag=Math.max(0,Math.min(255,lA.y));
                pol2.ab=Math.max(0,Math.min(255,lA.z));
                pol2.br=Math.max(0,Math.min(255,lB.x));
                pol2.bg=Math.max(0,Math.min(255,lB.y));
                pol2.bb=Math.max(0,Math.min(255,lB.z));
                pol2.cr=Math.max(0,Math.min(255,lC.x));
                pol2.cg=Math.max(0,Math.min(255,lC.y));
                pol2.cb=Math.max(0,Math.min(255,lC.z));
            } else if(objs[i] instanceof ColorLightedPolygon4V) {
                
                ColorLightedPolygon4V pol=(ColorLightedPolygon4V)objs[i];
                ColorLightedPolygon4V pol2=(ColorLightedPolygon4V)reflection.getPolygons()[i];
                tmp.set(pol.a); tmp.add(pol.b.x,pol.b.y,pol.b.z); tmp.add(pol.c.x,pol.c.y,pol.c.z);
                tmp.add(pol.d.x,pol.d.y,pol.d.z); tmp.div(4,4,4);
                
                
                Vector3D lA=GI(house,meshes,oldMeshes,pol.a,pol.nx,pol.ny,pol.nz,tmp);
                Vector3D lB=GI(house,meshes,oldMeshes,pol.b,pol.nx,pol.ny,pol.nz,tmp);
                Vector3D lC=GI(house,meshes,oldMeshes,pol.c,pol.nx,pol.ny,pol.nz,tmp);
                Vector3D lD=GI(house,meshes,oldMeshes,pol.d,pol.nx,pol.ny,pol.nz,tmp);
                
                
                pol.ar=add(lA.x,pol.ar);
                pol.ag=add(lA.y,pol.ag);
                pol.ab=add(lA.z,pol.ab);
                pol.br=add(lB.x,pol.br);
                pol.bg=add(lB.y,pol.bg);
                pol.bb=add(lB.z,pol.bb);
                pol.cr=add(lC.x,pol.cr);
                pol.cg=add(lC.y,pol.cg);
                pol.cb=add(lC.z,pol.cb);
                pol.dr=add(lD.x,pol.dr);
                pol.dg=add(lD.y,pol.dg);
                pol.db=add(lD.z,pol.db);
                
                pol2.ar=Math.max(0,Math.min(255,lA.x));
                pol2.ag=Math.max(0,Math.min(255,lA.y));
                pol2.ab=Math.max(0,Math.min(255,lA.z));
                pol2.br=Math.max(0,Math.min(255,lB.x));
                pol2.bg=Math.max(0,Math.min(255,lB.y));
                pol2.bb=Math.max(0,Math.min(255,lB.z));
                pol2.cr=Math.max(0,Math.min(255,lC.x));
                pol2.cg=Math.max(0,Math.min(255,lC.y));
                pol2.cb=Math.max(0,Math.min(255,lC.z));
                pol2.dr=Math.max(0,Math.min(255,lD.x));
                pol2.dg=Math.max(0,Math.min(255,lD.y));
                pol2.db=Math.max(0,Math.min(255,lD.z));
            }
            if(perPolygonSleep!=0) try{Thread.sleep(perPolygonSleep);} catch(Exception e) {};
        }
        
        
        
    }
    
    private static int calculateNormal(Mesh[] meshes, Vector3D norm, int nx, int ny, int nz, Vertex vert, int maxRot, int D, boolean div) {
        int polys=0;
		norm.set(0, 0, 0);
		
        for(int r=0;r<meshes.length-(meshes.length>1?1:0);r++) {
            polys+=calcMeshNormals(meshes[r].getPolygons(),norm,nx,ny,nz,vert,maxRot,polys,D);
        }
        
        if(polys>1 && div) norm.setLength(4096);
        return polys;
    }
        
    private static int calcMeshNormals(
            RenderObject[] objs,
            Vector3D norm,
            int nx,int ny,int nz, 
            Vertex vert, int maxRot,int polys,int D) {
        
        for(int i=0;i<objs.length;i++) {
            if(objs[i] instanceof Polygon3V) {
                Polygon3V pol=(Polygon3V)objs[i];
                if(distance(pol.a,vert,D) || 
                    distance(pol.b,vert,D) || 
                    distance(pol.c,vert,D)) {
                    if(distance(pol.nx,pol.ny,pol.nz,nx,ny,nz,maxRot)) {
                    norm.add(pol.nx,pol.ny,pol.nz);
                    polys++;
                } }
            } else if(objs[i] instanceof Polygon4V) {
                Polygon4V pol=(Polygon4V)objs[i];
                 if(distance(pol.a,vert,D) || 
                    distance(pol.b,vert,D) || 
                    distance(pol.c,vert,D) || 
                    distance(pol.d,vert,D)) {
                    if(distance(pol.nx,pol.ny,pol.nz,nx,ny,nz,maxRot)) {
                    norm.add(pol.nx,pol.ny,pol.nz);
                    polys++;
                } }
            }
        }
    return polys;
    }
    
    private static void calcPosAndNorm(Vector3D pos, Vector3D norm, Vertex vert, Mesh[] meshes, Vertex polCentre, int normR) {
        Vector3D bckNorm = new Vector3D(norm.x, norm.y, norm.z);
		int polys=calculateNormal(meshes, pos, norm.x,norm.y, norm.z, vert, 6000, 3, false);
		
        if(polys>1) {
            final int r=6;
            pos.setLength(4096);
            
            int mx=pos.x/256; if(mx>-r && mx<r && pos.x!=0) mx=pos.x*r/Math.abs(pos.x);
            int my=pos.y/256; if(my>-r && my<r && pos.y!=0) my=pos.y*r/Math.abs(pos.y);
            int mz=pos.z/256; if(mz>-r && mz<r && pos.z!=0) mz=pos.z*r/Math.abs(pos.z);
            
            pos.set(vert.x-mx,vert.y-my,vert.z-mz);
        } else {
            pos.set(
                    (vert.x*3+polCentre.x)/4-norm.x/256,
                    (vert.y*3+polCentre.y)/4-norm.y/256,
                    (vert.z*3+polCentre.z)/4-norm.z/256);
        }
        
        calculateNormal(meshes, norm, bckNorm.x, bckNorm.y, bckNorm.z, vert, smoothMax, normR, true);
    }
    
    private static boolean distance(Vertex a, Vertex b, int D) {
       return !(Math.abs(a.x-b.x)>D || 
               Math.abs(a.y-b.y)>D ||
               Math.abs(a.z-b.z)>D);
    }
    
    public static long distanceSqr(Vertex a, Vector3D b) {
       return (a.x-b.x)*(a.x-b.x)+
               (a.y-b.y)*(a.y-b.y)+
               (a.z-b.z)*(a.z-b.z);
    }
    
    private static boolean distance(int x1, int y1, int z1, int x, int y, int z, int D) {
       return (Math.abs(x1-x)<D && 
               Math.abs(y1-y)<D &&
               Math.abs(z1-z)<D);
    }
    
    private static void castRay(House house, Room room, Mesh[] meshes, Ray ray) {
        Mesh[] msh = getMeshes(house,room,meshes);
        
        castRay(msh,ray);
    }
    
    private static void castRay(Mesh[] msh, Ray ray) {
        for(int i=0;i<msh.length;i++) {RayCast.rayCast(msh[i], ray);}
    }
    
    private static Mesh[] getMeshes(House house, Room room, Mesh[] meshes) {
        if(allRooms) {
            Mesh[] msh = new Mesh[meshes.length-(meshes.length>1?1:0)];
            System.arraycopy(meshes, 0, msh, 0, msh.length);
            return msh;
        } else {
            Room[] neighbours = house.getNeighbourRooms(room.getId());
            int neighboursCount = 0;
            if(neighbours != null) neighboursCount = neighbours.length;

            Mesh[] msh = new Mesh[1 + neighboursCount];

            msh[0] = room.getMesh();
            for(int x=1; x<msh.length; x++) {
                if(neighbours[x-1] != null) msh[x] = neighbours[x-1].getMesh();
            }
            
            return msh;
        }
    }
    
    private static Vector3D getCenter(RenderObject ro) {
        if(ro instanceof Polygon3V) {
            Polygon3V p3 = (Polygon3V)ro;
            return new Vector3D( (p3.a.x+p3.b.x+p3.c.x)/3,
                (p3.a.y+p3.b.y+p3.c.y)/3,
                (p3.a.z+p3.b.z+p3.c.z)/3 );
        } else if(ro instanceof Polygon4V) {
            Polygon4V p4 = (Polygon4V)ro;
            return new Vector3D( (p4.a.x+p4.b.x+p4.c.x+p4.d.x)>>2,
                (p4.a.y+p4.b.y+p4.c.y+p4.d.y)>>2,
                (p4.a.z+p4.b.z+p4.c.z+p4.d.z)>>2 );
        }
        
        return new Vector3D(0,0,0);
    }
   
    private static Vertex getBounds(Vector3D pos, RenderObject ro) {
        Vertex out = new Vertex(Integer.MAX_VALUE,Integer.MAX_VALUE,Integer.MAX_VALUE);
        out.sx = Integer.MIN_VALUE; out.sy = Integer.MIN_VALUE; out.rz = Integer.MIN_VALUE;
        
        Vector3D v = new Vector3D(0,0,0);
        
        if(ro instanceof Polygon3V) {
            Polygon3V p3 = (Polygon3V)ro;
            v.set(p3.a.x-pos.x,p3.a.y-pos.y,p3.a.z-pos.z);
            v.setLengthRound(raysC/2);
            out.pmax(v.x,v.y,v.z); out.min(v.x,v.y,v.z);
            
            v.set(p3.b.x-pos.x,p3.b.y-pos.y,p3.b.z-pos.z);
            v.setLengthRound(raysC/2);
            out.pmax(v.x,v.y,v.z); out.min(v.x,v.y,v.z);
            
            v.set(p3.c.x-pos.x,p3.c.y-pos.y,p3.c.z-pos.z);
            v.setLengthRound(raysC/2);
            out.pmax(v.x,v.y,v.z); out.min(v.x,v.y,v.z);
        } else if(ro instanceof Polygon4V) {
            Polygon4V p4 = (Polygon4V)ro;
            v.set(p4.a.x-pos.x,p4.a.y-pos.y,p4.a.z-pos.z);
            v.setLengthRound(raysC/2);
            out.pmax(v.x,v.y,v.z); out.min(v.x,v.y,v.z);
            
            v.set(p4.b.x-pos.x,p4.b.y-pos.y,p4.b.z-pos.z);
            v.setLengthRound(raysC/2);
            out.pmax(v.x,v.y,v.z); out.min(v.x,v.y,v.z);
            
            v.set(p4.c.x-pos.x,p4.c.y-pos.y,p4.c.z-pos.z);
            v.setLengthRound(raysC/2);
            out.pmax(v.x,v.y,v.z); out.min(v.x,v.y,v.z);
            
            v.set(p4.d.x-pos.x,p4.d.y-pos.y,p4.d.z-pos.z);
            v.setLengthRound(raysC/2);
            out.pmax(v.x,v.y,v.z); out.min(v.x,v.y,v.z);
        }
        
        return out;
    }
    
    private static boolean isShadowCaster(Mesh m, RenderObject ro) {
        if(ro instanceof Polygon3V) {
            Polygon3V p = (Polygon3V)ro;
            if(p.tex>0) return m.getTexture().textures[p.tex].castShadow;
        } else if(ro instanceof Polygon4V) {
            Polygon4V p = (Polygon4V)ro;
            if(p.tex>0) return m.getTexture().textures[p.tex].castShadow;
        }
        
        return true;
    }
    
    private static Vector3D getPolColor(MultyTexture mt, RenderObject ro, int lit) {
        int tex = 0;
        int u = 0, v = 0;
        int origLightR = 0, origLightG = 0, origLightB = 0;
        Vector3D out = new Vector3D();
        if(lit == 0) return out;

        if(ro instanceof LightedPolygon4V) {

            LightedPolygon4V p = (LightedPolygon4V) ro;
            origLightR = origLightG = origLightB = (p.la + p.lb + p.lc + p.ld) / 4;
            tex = p.tex;
            u = ((p.au & 0xff) + (p.bu & 0xff) + (p.cu & 0xff) + (p.du & 0xff)) / 4;
            v = ((p.av & 0xff) + (p.bv & 0xff) + (p.cv & 0xff) + (p.dv & 0xff)) / 4;

        } else if(ro instanceof LightedPolygon3V) {

            LightedPolygon3V p = (LightedPolygon3V) ro;
            origLightR = origLightG = origLightB = (p.la + p.lb + p.lc) / 3;
            tex = p.tex;
            u = ((p.au & 0xff) + (p.bu & 0xff) + (p.cu & 0xff)) / 3;
            v = ((p.av & 0xff) + (p.bv & 0xff) + (p.cv & 0xff)) / 3;

        } else if(ro instanceof ColorLightedPolygon4V) {

            ColorLightedPolygon4V p = (ColorLightedPolygon4V) ro;
            origLightR = (p.ar + p.br + p.cr + p.dr) / 4;
            origLightG = (p.ag + p.bg + p.cg + p.dg) / 4;
            origLightB = (p.ab + p.bb + p.cb + p.db) / 4;
            tex = p.tex;
            u = ((p.au & 0xff) + (p.bu & 0xff) + (p.cu & 0xff) + (p.du & 0xff)) / 4;
            v = ((p.av & 0xff) + (p.bv & 0xff) + (p.cv & 0xff) + (p.dv & 0xff)) / 4;

        } else if(ro instanceof ColorLightedPolygon3V) {

            ColorLightedPolygon3V p = (ColorLightedPolygon3V) ro;
            origLightR = (p.ar + p.br + p.cr) / 3;
            origLightG = (p.ag + p.bg + p.cg) / 3;
            origLightB = (p.ab + p.bb + p.cb) / 3;
            tex = p.tex;
            u = ((p.au & 0xff) + (p.bu & 0xff) + (p.cu & 0xff)) / 3;
            v = ((p.av & 0xff) + (p.bv & 0xff) + (p.cv & 0xff)) / 3;

        } else if(ro instanceof Polygon4V) {

            LightedPolygon4V p = (LightedPolygon4V) ro;
            origLightR = origLightG = origLightB = 255;
            tex = p.tex;
            u = ((p.au & 0xff) + (p.bu & 0xff) + (p.cu & 0xff) + (p.du & 0xff)) / 4;
            v = ((p.av & 0xff) + (p.bv & 0xff) + (p.cv & 0xff) + (p.dv & 0xff)) / 4;

        } else if(ro instanceof Polygon3V) {

            LightedPolygon3V p = (LightedPolygon3V) ro;
            origLightR = origLightG = origLightB = 255;
            tex = p.tex;
            u = ((p.au & 0xff) + (p.bu & 0xff) + (p.cu & 0xff)) / 3;
            v = ((p.av & 0xff) + (p.bv & 0xff) + (p.cv & 0xff)) / 3;

        }

        Texture te = mt.textures[tex];

        if(te.drawmode != 9 && te.drawmode != 10 && te.drawmode != 13 && 
                (lumFromTextures[0]!=0 || lumFromTextures[1]!=0 || lumFromTextures[2]!=0) ) {
            origLightR = lumFromTextures[0];
            origLightG = lumFromTextures[1];
            origLightB = lumFromTextures[2];
        }

        if(origLightR + origLightG + origLightB > 0) {

            int tew = te.rImg.w;
            int[] teimg = te.rImg.img;
            u = u * te.rImg.w / 256;
            v = v * te.rImg.h / 256;
            if(te.mip != null) {
                teimg = te.mip[2].img;
                u = u * te.mip[2].w / te.rImg.w;
                v = v * te.mip[2].h / te.rImg.h;
                tew = te.mip[2].w;
            }

            int col = teimg[(u + v * tew) % teimg.length];

            out.x = origLightR * giFallOff[0] / 255 * ((col >> 16) & 0xff) / 255 * 4 * lit;
            out.y = origLightG * giFallOff[1] / 255 * ((col >> 8) & 0xff) / 255 * 4 * lit;
            out.z = origLightB * giFallOff[2] / 255 * (col & 0xff) / 255 * 4 * lit;
        }
        
		if(bwTexGI) out.set(out.average());
        return out;
    }
    
    private static long isRayOnPol(Vector3D start, int x, int y, int z, RenderObject ro) {
        Vector3D dir = new Vector3D(x,y,z);
        
        if(ro instanceof Polygon3V) {
            Polygon3V p = (Polygon3V)ro;
            Vector3D a = new Vector3D(p.a.x,p.a.y,p.a.z);
            Vector3D b = new Vector3D(p.b.x,p.b.y,p.b.z);
            Vector3D c = new Vector3D(p.c.x,p.c.y,p.c.z);
            Vector3D norm = new Vector3D(p.nx,p.ny,p.nz);
            return RayCast.isRayOnPolygon(a, b, c, norm, start, dir);
        } else if(ro instanceof Polygon4V) {
            Polygon4V p = (Polygon4V)ro;
            Vector3D a = new Vector3D(p.a.x,p.a.y,p.a.z);
            Vector3D b = new Vector3D(p.b.x,p.b.y,p.b.z);
            Vector3D c = new Vector3D(p.c.x,p.c.y,p.c.z);
            Vector3D d = new Vector3D(p.d.x,p.d.y,p.d.z);
            Vector3D norm = new Vector3D(p.nx,p.ny,p.nz);
            return RayCast.isRayOnPolygon(a, b, c, d, norm, start, dir);
        }
        
        return Long.MAX_VALUE;
    }
    
    private static int ambientOcculusion(House house, Room room, Mesh[] meshes, Vertex vert, int nx, int ny, int nz, Vertex polCentre, RenderObject cur) {
        
        Vector3D pos=new Vector3D(0,0,0);
        Vector3D norm=new Vector3D(nx,ny,nz);
        
        calcPosAndNorm(pos,norm,vert,meshes,polCentre,1);
        
        Mesh[] msh = getMeshes(house,room,meshes);
        
        int aoLit;
        if(fastCalc) aoLit = fastAO(msh,pos,norm);
        else aoLit = slowAO(msh,pos,norm);
        
        int ao=Math.min(0xff,Math.max(0xff-aoLit*aoIntensity/255,0));
        
        return Math.max(0,Math.min(ao,255));
    }
    
    private static int slowAO(Mesh[] msh, Vector3D pos, Vector3D norm) {
        int skydomelight=0;
        int rays=0;
        
        ray.getStart().set(pos);
        
        for(int x=-raysC/2;x<=raysC/2;x+=1) {
            for(int y=-raysC/2;y<=raysC/2;y+=1) {
                for(int z=-raysC/2;z<=raysC/2;z+=1) {
                    int p = x+y*raysC+z*raysC*raysC;
                    if(!validm[p]) continue;
                    
                    rays++;
                    int shadow2 = 0;
                    int lit = MathUtils.calcLight(-x, -y, -z, norm.x, norm.y, norm.z);
                    ray.reset();
                    ray.getDir().set(x, y, z);
                    ray.getDir().setLength(4096);
                    castRay(msh, ray);
                    
                    if(ray.isCollision() && ray.getDistance() < aoDistance) {
                        shadow2 += Math.min(0xff, Math.max(aoDistance - ray.getDistance(), 0) * 255 / aoDistance);
                    }

                    skydomelight += shadow2 * lit / 255;
                }
            }
        }

        return skydomelight / rays;
    }
    
    private static int fastAO(Mesh[] msh, Vector3D pos, Vector3D norm) {
        int ao=0;
        int rays=0;
        
        for(int x=0; x<raysC; x++) {
            for(int y=0; y<raysC; y++) {
                for(int z=0; z<raysC; z++) {
                    int p = x + y * raysC + z * raysC * raysC;
                    rm[p] = 0;
                    dm[p] = Long.MAX_VALUE;
                }
            }
        }

        for(int i=0; i<msh.length; i++) {
            RenderObject[] pols = msh[i].getPolygons();

            for(int x = 0; x < pols.length; x++) {
                if(!isShadowCaster(msh[i], pols[x])) continue;

                Vector3D c = getCenter(pols[x]);
                Vector3D dir = new Vector3D(c.x - pos.x, c.y - pos.y, c.z - pos.z);

                int lit = MathUtils.calcLight(dir.x, dir.y, dir.z, -norm.x, -norm.y, -norm.z);
                long d = (c.x - pos.x) * (c.x - pos.x)
                        + (c.y - pos.y) * (c.y - pos.y)
                        + (c.z - pos.z) * (c.z - pos.z);

                Vertex b = getBounds(pos, pols[x]);

                for(int xx=b.x; xx<=b.sx; xx++) {
                    for(int yy=b.y; yy<=b.sy; yy++) {
                        for(int zz=b.z; zz<=b.rz; zz++) {
                            int tx = xx, ty = yy, tz = zz;
                            int p = (tx+raysC/2) + (ty+raysC/2)*raysC + (tz+raysC/2)*raysC*raysC;
                            if(!validm[p]) {
                                tx = posesm[p*3];
                                ty = posesm[p*3+1];
                                tz = posesm[p*3+2];
                                p = (tx+raysC/2) + (ty+raysC/2)*raysC + (tz+raysC/2)*raysC*raysC;
                            }

                            d = isRayOnPol(pos, tx, ty, tz, pols[x]);
                            if(d < dm[p] && d < aoDistance && d != Long.MAX_VALUE) {
                                dm[p] = d;
                                rm[p] = Math.min(0xff, Math.max(aoDistance - (int) d, 0) * 255 / aoDistance) * lit / 255;
                            }
                        }
                    }
                }

            }
        }

        for(int x=0; x<raysC; x++) {
            for(int y=0; y<raysC; y++) {
                for(int z=0; z<raysC; z++) {
                    int p = x + y*raysC + z*raysC*raysC;
                    if(!validm[p]) continue;

                    ao += rm[p] * countm[p];
                    rays += countm[p];
                }
            }
        }
        ao/=rays;
        
        return ao;
    }
    
    private static Vector3D lightCalcMini(House house, Room room, Mesh[] meshes, Vertex vert, int nx, int ny, int nz, Vertex polCentre) {
        Vector3D pos=new Vector3D(0,0,0);
        Vector3D norm=new Vector3D(nx,ny,nz);
        
        calcPosAndNorm(pos,norm,vert,meshes,polCentre,1);
        
        Vector3D lit=new Vector3D(0,0,0);
        Light[] lightsMassive = lights;
        if(!allRooms) {
            lightsMassive = room.lights;
            if(lightsMassive==null) return lit;
            if(lightsMassive.length==0) return lit;
        }
        
        for(int i=0;i<lightsMassive.length;i++) {
            Light light=lightsMassive[i];
            int[] clampedColor=new int[3];
            System.arraycopy(light.color, 0, clampedColor, 0, 3);
            
            while (Math.abs(clampedColor[0]) > 30000 || Math.abs(clampedColor[1]) > 30000 || Math.abs(clampedColor[2]) > 30000) {
                nx /= 2;ny /= 2;nz /= 2;
            }
            double nw = Math.sqrt((double) (clampedColor[0] * clampedColor[0] + clampedColor[1] * clampedColor[1] + clampedColor[2] * clampedColor[2]));
            if(nw<1) nw=1;
            clampedColor[0]*=255;
            clampedColor[1]*=255;
            clampedColor[2]*=255;
            clampedColor[0]/=nw;
            clampedColor[1]/=nw;
            clampedColor[2]/=nw;
            
            long distSqr=distanceSqr(vert,light.pos);
            long intensity=255*sqrMeter/Math.max(1,distSqr)*8;
            int fix=0;
            if(norm.y<-4090) fix=light.floorFix;
            else if(norm.y>4090) fix=light.ceilingFix;
            
            if(distSqr<0) intensity=0;
            if(intensity>1) {
                if(light.direction==null) {
                    intensity=intensity*MathUtils.calcLight(norm.x,norm.y,norm.z, vert.x-light.pos.x,vert.y-light.pos.y,vert.z-light.pos.z,fix)/255;
                } else {
                    Vector3D direction=light.direction;
                    intensity=intensity*MathUtils.calcLight(direction.x,direction.y,direction.z, 
                            vert.x-light.pos.x,vert.y-light.pos.y,vert.z-light.pos.z)/255;  
                    intensity=intensity*MathUtils.calcLight(norm.x,norm.y,norm.z, vert.x-light.pos.x,vert.y-light.pos.y,vert.z-light.pos.z,fix)/255;
                }
                if(intensity>1) {
                ray.reset();
                ray.getDir().set(light.pos.x-pos.x,light.pos.y-pos.y,light.pos.z-pos.z);
                ray.getStart().set(pos);
                castRay(house,room,meshes,ray);
                if(ray.isCollision() && ray.getDistance()*ray.getDistance()<=distSqr && ray.getDistance()>0) intensity=0;
                }
            }
            
            if(intensity>1) {
                lit.add(
                        Math.min(clampedColor[0],(int)intensity*light.color[0]/255),
                        Math.min(clampedColor[1],(int)intensity*light.color[1]/255),
                        Math.min(clampedColor[2],(int)intensity*light.color[2]/255));
            }
        }
        
        return lit;
    }
    
    private static Vector3D GI(House house, Mesh[] meshes, Mesh[] oldMeshes, Vertex vert, int nx, int ny, int nz, Vertex polCentre) {
        
        
        Vector3D pos=new Vector3D(0,0,0);
        Vector3D norm=new Vector3D(nx,ny,nz);
        
        calcPosAndNorm(pos,norm,vert,meshes,polCentre,1);
        
        Vector3D out;
        if(fastCalc) out = fastGI(oldMeshes, pos, norm);
        else out = slowGI(oldMeshes, pos, norm);
        
        if(bwGI) out.set(out.average());
        
        out.mul(giIntensity[0],giIntensity[1],giIntensity[2]);
        out.div(255,255,255);
        
        return out;
    }
    
    private static Vector3D slowGI(Mesh[] oldMeshes, Vector3D pos, Vector3D norm) {
        long gir=0;
        long gig=0;
        long gib=0;
        int rays=0;
        
        ray.getStart().set(pos);
        MultyTexture mt = oldMeshes[0].getTexture();
        
        for(int x=0; x<raysC; x++) {
            for(int y=0; y<raysC; y++) {
                for(int z=0; z<raysC; z++) {
                    int p = x+y*raysC+z*raysC*raysC;
                    if(!validm[p]) continue;
                    
                    rays++;
                    ray.reset();
                    
                    Vector3D dir = ray.getDir();
                    dir.set(x-raysC/2, y-raysC/2, z-raysC/2);
                    dir.setLength(4096);
                    
                    for(int i=0; i<oldMeshes.length; i++) RayCast.superFastRayCast(oldMeshes[i], ray);
                    
                    if(ray.isCollision() && ray.getDistance() > 0) {
                        int lit = MathUtils.calcLight(dir.x, dir.y, dir.z, -norm.x, -norm.y, -norm.z);
                        Vector3D color = getPolColor(mt, ray.getTriangle(), lit);
                        
                        gir += color.x;
                        gig += color.y;
                        gib += color.z;
                    }
                }
            }
        }
        
        gir/=rays*255;
        gig/=rays*255;
        gib/=rays*255;
        
        return new Vector3D((int)gir,(int)gig,(int)gib);
    }
    
    private static Vector3D fastGI(Mesh[] oldMeshes, Vector3D pos, Vector3D norm) {
        long gir=0;
        long gig=0;
        long gib=0;
        int rays=0;
        
        for(int x=0; x<raysC; x++) {
            for(int y=0; y<raysC; y++) {
                for(int z=0; z<raysC; z++) {
                    int p = x+y*raysC+z*raysC*raysC;
                    rm[p] = gm[p] = bm[p] = 0;
                    
                    if(!validm[p]) dm[p] = Long.MIN_VALUE;
                    else dm[p] = Long.MAX_VALUE;
                }
            }
        }
            
        for(int i=0; i<oldMeshes.length; i++) {
            RenderObject[] pols = oldMeshes[i].getPolygons();
            MultyTexture mt = oldMeshes[i].getTexture();

            for(int x = 0; x < pols.length; x++) {
                RenderObject pol = pols[x];
                if(!isShadowCaster(oldMeshes[i], pol)) continue;

                Vector3D c = getCenter(pol);
                Vector3D n = new Vector3D(pol.nx,pol.ny,pol.nz);
                Vector3D dir = new Vector3D(c.x - pos.x, c.y - pos.y, c.z - pos.z);
                
                if(dir.dotLong(n)>>12 <= 0) continue;
                int lit = MathUtils.calcLight(dir.x, dir.y, dir.z, -norm.x, -norm.y, -norm.z);
                
                long d = (c.x - pos.x) * (c.x - pos.x)
                        + (c.y - pos.y) * (c.y - pos.y)
                        + (c.z - pos.z) * (c.z - pos.z);
                
                Vector3D color = getPolColor(mt, pols[x], lit);

                Vertex b = getBounds(pos, pols[x]);

                for(int xx=b.x; xx<=b.sx; xx++) {
                    for(int yy=b.y; yy<=b.sy; yy++) {
                        for(int zz=b.z; zz<=b.rz; zz++) {
                            int tx = xx, ty = yy, tz = zz;
                            int p = (tx+raysC/2) + (ty+raysC/2)*raysC + (tz+raysC/2)*raysC*raysC;
                            if(!validm[p]) {
                                tx = posesm[p*3];
                                ty = posesm[p*3+1];
                                tz = posesm[p*3+2];
                                p = (tx+raysC/2) + (ty+raysC/2)*raysC + (tz+raysC/2)*raysC*raysC;
                            }
                            
                            d = isRayOnPol(pos,tx,ty,tz,pols[x]);
                            if(dm[p] > d && d != Long.MAX_VALUE) {
                                dm[p] = d;
                                rm[p] = color.x;
                                gm[p] = color.y;
                                bm[p] = color.z;
                            }
                        }
                    }
                }

            }
        }

        for(int x=0; x<raysC; x++) {
            for(int y=0; y<raysC; y++) {
                for(int z=0; z<raysC; z++) {
                    int p = x + y*raysC + z*raysC*raysC;
                    if(!validm[p]) continue;

                    gir += rm[p];
                    gig += gm[p];
                    gib += bm[p];
                    rays++;
                }
            }
        }
        
        gir/=rays*255;
        gig/=rays*255;
        gib/=rays*255;
        
        return new Vector3D((int)gir,(int)gig,(int)gib);
    }
    
    private static Vector3D skySunLight(House house, Room room, Mesh[] meshes, Vertex vert, int nx, int ny, int nz, Vertex polCentre, RenderObject cur) {
        
        int skyLitDirectional=0;
        int shadow=0xff;
        
        Vector3D pos=new Vector3D(0,0,0);
        Vector3D norm=new Vector3D(nx,ny,nz);
        
        calcPosAndNorm(pos,norm,vert,meshes,polCentre,5);
        
        if(sunLight[0] != 0 || sunLight[1] != 0 || sunLight[2] != 0) {
            skyLitDirectional = MathUtils.calcLight(
                    DirectX7.lightdirx, DirectX7.lightdiry, DirectX7.lightdirz,
                    norm.x, norm.y, norm.z);
            
            ray.reset();
            ray.getStart().set(pos);
            
            ray.getDir().set(-DirectX7.lightdirx, -DirectX7.lightdiry, -DirectX7.lightdirz);
            
            ray.getDir().setLength(30000);
            castRay(house, room, meshes, ray);
            ray.getDir().setLength(4096); //Подстраховка
            castRay(house, room, meshes, ray);
            if(ray.isCollision()) shadow = 0;
            ray.reset();
        }
        
        
        int skydomelight = 0;
        
        if(skyLight[0]!=0 || skyLight[1]!=0 || skyLight[2]!=0) {
            calculateNormal(meshes,norm,nx,ny,nz,vert,smoothMax,1,true);
            Mesh[] msh = getMeshes(house, room, meshes);
            
            if(fastCalc) skydomelight = fastSL(msh,norm,pos,cur);
            else skydomelight = slowSL(msh,norm,pos);
        }
        
        return new Vector3D( 
        (shadow*skyLitDirectional/255*sunLight[0]/255)+(skydomelight*skyLight[0]/255)+ambientLight[0],
        (shadow*skyLitDirectional/255*sunLight[1]/255)+(skydomelight*skyLight[1]/255)+ambientLight[1],
        (shadow*skyLitDirectional/255*sunLight[2]/255)+(skydomelight*skyLight[2]/255)+ambientLight[2]);
    }
    
    private static int fastSL(Mesh[] msh, Vector3D norm, Vector3D posCheck, RenderObject cur) {
        int skydomelight=0;
        int rays=0;
       
        for(int x=0; x<raysC; x++) {
            for(int y=0; y<raysC; y++) {
                for(int z=0; z<raysC; z++) {
                    int p = x + y*raysC + z*raysC*raysC;
                    
                    if(!validm[p] || y<raysC/2) rm[p] = 0;
                    else {
                        Vector3D dir = new Vector3D(x - raysC/2, y - raysC/2, z - raysC/2);
                        rm[p] = MathUtils.calcLight(dir.x, dir.y, dir.z, -norm.x, -norm.y, -norm.z);
                    }

                    gm[p] = rm[p];
                }
            }
        }

        for(int i=0; i<msh.length; i++) {
            RenderObject[] pols = msh[i].getPolygons();

            for(int x=0; x<pols.length; x++) {
                RenderObject pol = pols[x];
                if(cur == pol) continue;
                if(!isShadowCaster(msh[i], pol)) continue;

                Vector3D c = getCenter(pol);
                Vector3D n = new Vector3D(pol.nx,pol.ny,pol.nz);
                Vector3D dir = new Vector3D(c.x - posCheck.x, c.y - posCheck.y, c.z - posCheck.z);

                int lit = dir.y<0?0:255;

                if(lit != 0) lit = MathUtils.calcLight(dir.x, dir.y, dir.z, -norm.x, -norm.y, -norm.z);
                if(lit != 0) lit = MathUtils.calcLight(dir.x, dir.y, dir.z, n.x, n.y, n.z)<255?0:lit;

                Vertex b = getBounds(posCheck, pols[x]);

                for(int xx = b.x; xx <= b.sx; xx++) {
                    for(int yy = b.y; yy <= b.sy; yy++) {
                        for(int zz = b.z; zz <= b.rz; zz++) {
                            int tx = xx, ty = yy, tz = zz;
                            int p = (tx+raysC/2) + (ty+raysC / 2)*raysC + (tz+raysC/2)*raysC*raysC;
                            if(!validm[p]) {
                                tx = posesm[p*3];
                                ty = posesm[p*3+1];
                                tz = posesm[p*3+2];
                                p = (tx+raysC/2) + (ty+raysC / 2)*raysC + (tz+raysC/2)*raysC*raysC;
                            }

                            if(slCheap || isRayOnPol(posCheck, tx, ty, tz, pols[x]) != Long.MAX_VALUE) {
                                rm[p] = Math.min(rm[p], lit*gm[p]/255);
                            }
                        }
                    }
                }

            }
        }

        for(int x=0; x<raysC-1; x++) {
            for(int y=raysC/2; y<raysC-1; y++) {
                for(int z=0; z<raysC-1; z++) {
                    int p = x + y*raysC + z*raysC*raysC;
                    if(!validm[p]) continue;

                    int v = rm[p];
                    skydomelight += v * countm[p];
                    rays += countm[p];
                }
            }
        }

        return skydomelight / rays;
    }
    
    private static int slowSL(Mesh[] msh, Vector3D norm, Vector3D posCheck) {
        int skydomelight=0;
        int rays=0;
        ray.getStart().set(posCheck);
       
        for(int x=-raysC; x<raysC; x+=2) {
            for(int y=0; y<raysC; y ++) {
                for(int z=-raysC; z<raysC; z+=2) {
                    rays++;
                    int lit = MathUtils.calcLight(-x, -y, -z, norm.x, norm.y, norm.z);
                    
                    if(lit>0) {
                        ray.reset();
                        ray.getDir().set(x, y, z);
                        ray.getDir().setLength(4096);
                        castRay(msh, ray);
                        if(ray.isCollision()) lit = 0;
                    }

                    skydomelight += lit;
                }
            }
        }

        return skydomelight / rays;
    }
    
    public static void saveLightMap(Mesh[] meshes, String lightdataFile) {
    try {
          
      OutputConnection con = (OutputConnection) Connector.open("file:///root/lightmap.vla", Connector.WRITE);

      FileConnection fc = (FileConnection)con;
      if(!fc.exists()) fc.create();

      OutputStream out = con.openOutputStream();
      DataOutputStream dos = new DataOutputStream(out);

      for(int i=0;i<meshes.length;i++) {
          RenderObject[] objs=meshes[i].getPolygons();
          for(int x=0;x<objs.length;x++) {
              if(objs[x] instanceof LightedPolygon3V) {
                  LightedPolygon3V pol=(LightedPolygon3V)objs[x];
                  dos.writeByte(pol.la - 128);
                  dos.writeByte(pol.lb - 128);
                  dos.writeByte(pol.lc - 128);
              } else if(objs[x] instanceof LightedPolygon4V) {
                  LightedPolygon4V pol=(LightedPolygon4V)objs[x];
                  dos.writeByte(pol.la - 128);
                  dos.writeByte(pol.lb - 128);
                  dos.writeByte(pol.lc - 128);
                  dos.writeByte(pol.ld - 128);
              } else if(objs[x] instanceof ColorLightedPolygon3V) {
                  ColorLightedPolygon3V pol=(ColorLightedPolygon3V)objs[x];
                  boolean colored=!(
                          pol.ar==pol.ag && pol.ag==pol.ab &&
                          pol.br==pol.bg && pol.bg==pol.bb &&
                          pol.cr==pol.cg && pol.cg==pol.cb);
                  
                  dos.writeByte(pol.ar - 128);
                  dos.writeByte(pol.br - 128);
                  dos.writeByte(pol.cr - 128);
                  dos.writeBoolean(colored);
                  if(colored) {
                      dos.writeByte(pol.ag - 128);
                      dos.writeByte(pol.ab - 128);
                      dos.writeByte(pol.bg - 128);
                      dos.writeByte(pol.bb - 128);
                      dos.writeByte(pol.cg - 128);
                      dos.writeByte(pol.cb - 128);
                  }
              } else if(objs[x] instanceof ColorLightedPolygon4V) {
                  ColorLightedPolygon4V pol=(ColorLightedPolygon4V)objs[x];
                  boolean colored=!(
                          pol.ar==pol.ag && pol.ag==pol.ab &&
                          pol.br==pol.bg && pol.bg==pol.bb &&
                          pol.cr==pol.cg && pol.cg==pol.cb &&
                          pol.dr==pol.dg && pol.dg==pol.db);
                  
                  dos.writeByte(pol.ar - 128);
                  dos.writeByte(pol.br - 128);
                  dos.writeByte(pol.cr - 128);
                  dos.writeByte(pol.dr - 128);
                  dos.writeBoolean(colored);
                  if(colored) {
                      dos.writeByte(pol.ag - 128);
                      dos.writeByte(pol.ab - 128);
                      dos.writeByte(pol.bg - 128);
                      dos.writeByte(pol.bb - 128);
                      dos.writeByte(pol.cg - 128);
                      dos.writeByte(pol.cb - 128);
                      dos.writeByte(pol.dg - 128);
                      dos.writeByte(pol.db - 128);
                  }
              }
          }
      }
      
          
            dos.close();
            con.close();
      } catch(Exception exc) {
          print("Lightmap save error");
          System.out.println(exc.getMessage());
      }
    }
    
    public static void loadLightMap(Mesh[] meshes, String lightdataFile) {
    try {
          
      InputStream is = new Object().getClass().getResourceAsStream(lightdataFile);
      DataInputStream dis = new DataInputStream(is);
      
      for(int i=0;i<meshes.length;i++) {
          RenderObject[] objs=meshes[i].getPolygons();
          for(int x=0;x<objs.length;x++) {
              if(objs[x] instanceof LightedPolygon3V) {
                  LightedPolygon3V pol=(LightedPolygon3V)objs[x];
                  pol.la=dis.readByte() + 128;
                  pol.lb=dis.readByte() + 128;
                  pol.lc=dis.readByte() + 128;
              } else if(objs[x] instanceof LightedPolygon4V) {
                  LightedPolygon4V pol=(LightedPolygon4V)objs[x];
                  pol.la=dis.readByte() + 128;
                  pol.lb=dis.readByte() + 128;
                  pol.lc=dis.readByte() + 128;
                  pol.ld=dis.readByte() + 128;
              } else if(objs[x] instanceof ColorLightedPolygon3V) {
                  ColorLightedPolygon3V pol=(ColorLightedPolygon3V)objs[x];
                  pol.ar = dis.readByte() + 128;
                  pol.br = dis.readByte() + 128;
                  pol.cr = dis.readByte() + 128;
                  if (dis.readBoolean()) {
                      pol.ag = dis.readByte() + 128;
                      pol.ab = dis.readByte() + 128;
                      pol.bg = dis.readByte() + 128;
                      pol.bb = dis.readByte() + 128;
                      pol.cg = dis.readByte() + 128;
                      pol.cb = dis.readByte() + 128;
                      if(Main.fogQ==1) objs[x] = new LightedPolygon3V(pol);
                  } else {
                      pol.ag = pol.ab = pol.ar;
                      pol.bg = pol.bb = pol.br;
                      pol.cg = pol.cb = pol.cr;
                      objs[x] = new LightedPolygon3V(pol);
                  }
              } else if(objs[x] instanceof ColorLightedPolygon4V) {
                  ColorLightedPolygon4V pol=(ColorLightedPolygon4V)objs[x];
                  pol.ar = dis.readByte() + 128;
                  pol.br = dis.readByte() + 128;
                  pol.cr = dis.readByte() + 128;
                  pol.dr = dis.readByte() + 128;
                  if (dis.readBoolean()) {
                      pol.ag = dis.readByte() + 128;
                      pol.ab = dis.readByte() + 128;
                      pol.bg = dis.readByte() + 128;
                      pol.bb = dis.readByte() + 128;
                      pol.cg = dis.readByte() + 128;
                      pol.cb = dis.readByte() + 128;
                      pol.dg = dis.readByte() + 128;
                      pol.db = dis.readByte() + 128;
                      if(Main.fogQ==1) objs[x] = new LightedPolygon4V(pol);
                  } else {
                      pol.ag = pol.ab = pol.ar;
                      pol.bg = pol.bb = pol.br;
                      pol.cg = pol.cb = pol.cr;
                      pol.dg = pol.db = pol.dr;
                      objs[x] = new LightedPolygon4V(pol);
                  }
              }
          }
      }
      
            dis.close();
            is.close();
            System.gc();
      } catch(Exception exc) {
          System.out.println("Lightmap load error: "+exc.getMessage());
      }
    }

    public void run() {
        if(!run) return;
        generateLightMap(thouse,tmeshes);
        saveLightMap(tmeshes,tpath);
        print("lightmap saved!");
        stop();
        thouse=null;
        tmeshes=null;
        tpath=null;
    }
    
    private static void print(String text) {
        if(Main.mainCanvas.getScreen() instanceof GameScreen) {
            GameScreen gs = (GameScreen)Main.mainCanvas.getScreen();
            
            gs.customMessage = "";
            gs.customMessagePause = false;
            gs.customMessageEndTime = System.currentTimeMillis() + 2000;
            GameScreen.lines.removeAllElements();
            TextView.createLines(text, GameScreen.lines, gs.font, GameScreen.width);
        }
        
        System.out.println(text);
    }
}