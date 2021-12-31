package code.Gameplay.Map;

import code.Collision.HeightComputer;
import code.Collision.Height;
import code.Collision.Ray;
import code.Collision.RayCast;
import code.Rendering.MultyTexture;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.Mesh;
import code.Rendering.Meshes.Polygon3V;
import code.Rendering.RenderObject;
import code.Rendering.Vertex;
import code.Math.Vector3D;
import code.Rendering.Meshes.Polygon4V;
import code.Collision.SphereCast;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Vector;
import code.HUD.DeveloperMenu;
import code.Math.MathUtils2;
import code.Rendering.Meshes.BoundingBox;
import code.Rendering.Meshes.ColorLightedPolygon3V;
import code.Rendering.Meshes.ColorLightedPolygon4V;
import code.Rendering.Meshes.LightedPolygon3V;
import code.Rendering.Meshes.LightedPolygon4V;
import code.utils.Main;
import javax.microedition.lcdui.Graphics;

public class Room {

   private final int id;
   private final int rid;
   
   private Mesh[][] mesh;
   private BoundingBox[][] bbmesh;
   
   public Mesh fullMesh = null;
   private Mesh[][] renderMesh;
   private BoundingBox[][] bbrenderMesh;
   
   private final int minx;
   private final int maxx;
   private final int minz;
   private final int maxz;
   private final int miny;
   private final int maxy;
   private boolean openSky;
   private Portal[] portals;
   public int x1,y1,x2,y2;
   public String[] stepSound=null;
   public String jumpSound=null;
   public static int chunkSize;
   public static int chunkSizeRender=0;
   public String reverb;
   
   private Vector objects = new Vector();
   public Light[] lights;


   public Room(Mesh meshs, int id) {
      this.rid=id;
      this.id = id;
      this.minx = meshs.minX();
      this.maxx = meshs.maxX();
      this.minz = meshs.minZ();
      this.maxz = meshs.maxZ();
      this.miny = meshs.minY();
      this.maxy = meshs.maxY();
      this.openSky = openSkyTest(meshs);
      this.fullMesh=meshs;
      
      if(chunkSize>0) {
          Object[] tmp = cut(meshs,chunkSize,false);
          mesh = (Mesh[][])tmp[0];
          bbmesh = (BoundingBox[][])tmp[1];
      }
      
      if(chunkSizeRender>0) {
          Object[] tmp = cut(meshs, chunkSizeRender,true);
          renderMesh = (Mesh[][]) tmp[0];
          bbrenderMesh = (BoundingBox[][]) tmp[1];

          if(renderMesh.length<=2 || renderMesh[0].length<=2) {
              renderMesh=null;
              bbrenderMesh = null;
          }
      }
   }
   
   private Object[] cut(final Mesh mesh, final int size, boolean clever) {
        int minxx = minx;
        int maxxx = maxx;
        int minzz = minz;
        int maxzz = maxz;

        maxxx -= minxx;
        maxzz -= minzz;
        minxx = minzz = 0;
        final int sizex = maxxx - minxx;
        final int sizez = maxzz - minzz;

        final int mapw = sizex/size+1;
        final int maph = sizez/size+1;
        Mesh[][] map = new Mesh[mapw][maph];
        BoundingBox[][] bbmap = new BoundingBox[mapw][maph];
        Object[] out = new Object[]{ map, bbmap };
        
        Vector bufPol = new Vector();
        Vector bufVer = new Vector();
        boolean[] usedPols = null;
        if(clever) usedPols = new boolean[mesh.getPolygons().length];

        for(int z=0; z<maph; z++) {
            for(int x=0; x<mapw; x++) {
                bufPol.removeAllElements();
                bufVer.removeAllElements();
                getPolygonsInSquare(x*size+minx, z*size+minz, size, mesh.getPolygons(), bufVer, bufPol, clever, usedPols);
                Vertex[] nVertexs = new Vertex[bufVer.size()]; bufVer.copyInto(nVertexs);
                RenderObject[] nPolygons = new RenderObject[bufPol.size()]; bufPol.copyInto(nPolygons);
                map[x][z] = new Mesh(nVertexs,nPolygons);
                map[x][z].setTexture(mesh.getTexture());
                bbmap[x][z] = new BoundingBox(map[x][z]);
            }
        }
        return out;
    }

    private static void getPolygonsInSquare(int x1, int z1, int size, RenderObject[]  polygons, Vector putVer, Vector putPol, boolean clever, boolean[] used) {
        for(int i=0; i<polygons.length; i++) {
            if(clever && used[i]) continue;
            RenderObject el = polygons[i];
            if(!ifInSquare(el, x1, z1, size) ) continue;
            putPol.addElement(el);
            if(clever) used[i] = true;
            
            if( el instanceof Polygon3V) {
                Polygon3V pol = (Polygon3V) el;
                if(!putVer.contains(pol.a)) putVer.addElement(pol.a);
                if(!putVer.contains(pol.b)) putVer.addElement(pol.b);
                if(!putVer.contains(pol.c)) putVer.addElement(pol.c);
            }
            if( el instanceof Polygon4V) {
                Polygon4V pol = (Polygon4V) el;
                if(!putVer.contains(pol.a)) putVer.addElement(pol.a);
                if(!putVer.contains(pol.b)) putVer.addElement(pol.b);
                if(!putVer.contains(pol.c)) putVer.addElement(pol.c);
                if(!putVer.contains(pol.d)) putVer.addElement(pol.d);
            }
        }
    }

    private static boolean ifInSquare(RenderObject el, int x, int z, int size) {
        if( el instanceof Polygon3V) {
            Polygon3V pol = (Polygon3V) el;
            int minx = Math.min(pol.a.x,Math.min(pol.b.x,pol.c.x));
            int minz = Math.min(pol.a.z,Math.min(pol.b.z,pol.c.z));
            int maxx = Math.max(pol.a.x,Math.max(pol.b.x,pol.c.x));
            int maxz = Math.max(pol.a.z,Math.max(pol.b.z,pol.c.z));
            return ifInSquare(x, z, size, minx, minz, maxx, maxz);
            /*return ifInSquare(x, z, size, pol.a.x, pol.a.z) ||
                    ifInSquare(x, z, size, pol.b.x, pol.b.z) ||
                    ifInSquare(x, z, size, pol.c.x, pol.c.z);*/
        }
        if( el instanceof Polygon4V) {
            Polygon4V pol = (Polygon4V) el;
            int minx = Math.min(pol.a.x,Math.min(pol.b.x,Math.min(pol.c.x,pol.d.x)));
            int minz = Math.min(pol.a.z,Math.min(pol.b.z,Math.min(pol.c.z,pol.d.z)));
            int maxx = Math.max(pol.a.x,Math.max(pol.b.x,Math.max(pol.c.x,pol.d.x)));
            int maxz = Math.max(pol.a.z,Math.max(pol.b.z,Math.max(pol.c.z,pol.d.z)));
            return ifInSquare(x, z, size, minx, minz, maxx, maxz);
            /*return ifInSquare(x, z, size, pol.a.x, pol.a.z) ||
                    ifInSquare(x, z, size, pol.b.x, pol.b.z) ||
                    ifInSquare(x, z, size, pol.c.x, pol.c.z) ||
                    ifInSquare(x, z, size, pol.d.x, pol.d.z);*/
        }
        return false;
    }

    private static boolean ifInSquare(int x, int z, int size, int minx, int minz, int maxx, int maxz) {
        return !(minx>x+size || minz>z+size || maxx<x || maxz<z);
    }
    /*private static boolean ifInSquare(int x, int z, int size, int pointx, int pointz) {
        return pointx>=x && pointx<=x+size && pointz>=z && pointz<=z+size;
    }*/


    private boolean openSkyTest(Mesh meshs) {
        final int cx = (maxx+minx)/2;
        final int cz = (maxz+minz)/2;
        final int cy = (maxy*2+miny*4)/6;
        RenderObject[] pols = meshs.getPolygons();
        for(int i=0; i<pols.length; i++) {
            RenderObject obj = pols[i];
            if( isPointOnPolygon(cx, cz, obj) && obj.ny>2048) {
                int centerY=0;
                if(obj instanceof Polygon3V) {
                    Polygon3V pol=(Polygon3V)obj;
                    centerY=(pol.a.y+pol.b.y+pol.c.y)/3;
                } else if(obj instanceof Polygon4V) {
                    Polygon4V pol=(Polygon4V)obj;
                    centerY=(pol.a.y+pol.b.y+pol.c.y+pol.d.y)/4;
                }
                
                if(cy<=centerY) return false;
            }
        }
        return true;
    }
    

   public final void destroy() {
      this.mesh = null;
      this.fullMesh = null;
      
      if(portals!=null) {
      for(int var1 = 0; var1 < this.portals.length; ++var1) {
         this.portals[var1].destroy();
         this.portals[var1] = null;
      }
      }

      this.portals = null;
   }

   public final void setPortals(Portal[] portals) {
      this.portals = portals;
   }

public final void addPortal(Portal portal) {

Portal[] ports=new Portal[this.portals.length+1];

int i=0; while(i<this.portals.length) {
ports[i]=this.portals[i];
i++;
}
ports[i+1]=portal;

this.portals=null;
this.portals = ports;
System.gc();
   }


   public final Portal[] getPortals() {
      return this.portals;
   }

   public final Mesh/*RoomMesh[][]*/ getMesh() {
      return this.fullMesh;
   }

   public final int getId() {
      return this.id;
   }

   public final boolean isOpenSky() {
      return this.openSky;
   }

   public final void setViewport(int x1, int y1, int x2, int y2) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
   }
   
   public final void addViewport(int x1, int y1, int x2, int y2) {
      if(x1<this.x1) this.x1 = x1;
      if(y1<this.y1) this.y1 = y1;
      if(x2>this.x2) this.x2 = x2;
      if(y2>this.y2) this.y2 = y2;
   }
   
   public final boolean viewportContains(int x1, int y1, int x2, int y2) {
       return !(x1>=this.x2 || y1>=this.y2 || x2<this.x1 || y2<this.y1);
   }

   public final void render(DirectX7 g3d,int x,int z) {
       fullMesh.getTexture().updateAnimation();
       if(renderMesh!=null) {
       int zz;
       int xx=(x-minx)/chunkSizeRender-1;
       int zstart=(z-minz)/chunkSizeRender-1;
       int xend=(x-minx)/chunkSizeRender+1;
       int zend=(z-minz)/chunkSizeRender+1;
           if(xx>renderMesh.length-1) return;
           if(zstart>renderMesh[0].length-1) return;
           if(xend<0) return;
           if(zend<0) return;
           xx=Math.min(Math.max(xx,0),renderMesh.length-1);
           xend=Math.min(Math.max(xend,0),renderMesh.length-1);
           zstart=Math.min(Math.max(zstart,0),renderMesh[0].length-1);
           zend=Math.min(Math.max(zend,0),renderMesh[0].length-1);
       
       for(;xx<=xend;xx++) {
          for(zz=zstart;zz<=zend;zz++) {
          if(bbrenderMesh[xx][zz].isVisible(g3d,x1,y1,x2,y2)) {
          g3d.transformAndProjectVertices(renderMesh[xx][zz], g3d.getInvCamera());
          g3d.addMesh(renderMesh[xx][zz], x1, y1, x2, y2);
          renderMesh[xx][zz].applySz();
          }
          }
       }
       }
      else {
      g3d.transformAndProjectVertices(fullMesh, g3d.getInvCamera());
      g3d.addMesh(fullMesh, x1, y1, x2, y2);
      fullMesh.applySz();
      }
   }

    public final void renderObjects(DirectX7 g3d) {
        renderObjects(g3d,x1,y1,x2,y2);
    }

    public final void renderObjects(DirectX7 g3d, int x1, int y1, int x2, int y2) {
        for(int i=0; i<objects.size(); i++) {
            RoomObject obj = (RoomObject) objects.elementAt(i);
            obj.render(g3d, x1, y1, x2, y2);
        }
    }

   /* void rayCast(Ray ray)
   * и из RayCast
   * public static boolean isRayAABBCollision(Ray ray, int minx, int maxx, int minz, int maxz)
   */
   public final void rayCast(Ray ray) {
      Vector3D var2 = ray.getStart();
      Vector3D var3 = ray.getDir();
      /*int var4 = Math.min(var2.x, var2.x + var3.x);
      int var5 = Math.min(var2.z, var2.z + var3.z);
      int var6 = Math.max(var2.x, var2.x + var3.x);
      int var7 = Math.max(var2.z, var2.z + var3.z);
       if(var4 <= maxx && var5 <= maxz && var6 >= minx && var7 >= minz) {*/
         RayCast.rayCast(this.fullMesh, ray,rid);
     // }
   }
   
   public final void rayCastNonCheck(Ray ray) {
      RayCast.rayCast(this.fullMesh, ray,rid);
   }

   /* boolean sphereCast(Vector3D pos, int rad)
   * и из SphereCast
   * public static boolean isSphereAABBCollision(Vector3D pos, int rad, int minx, int maxx, int minz, int maxz)
   */
   public final boolean sphereCast(Vector3D pos, int rad) {
       boolean collision=false;
       if(this.mesh!=null) {
       int xx=0;
       int zz=0;
       int zstart=0;
       int xend=mesh.length-1;
       int zend=mesh[0].length-1;
       
           xx=(pos.x-minx)/chunkSize-1;
           zstart=(pos.z-minz)/chunkSize-1;
           xend=(pos.x-minx)/chunkSize+1;
           zend=(pos.z-minz)/chunkSize+1;
           if(xx>mesh.length-1) return false;
           if(zstart>mesh[0].length-1) return false;
           if(xend<0) return false;
           if(zend<0) return false;
           xx=Math.min(Math.max(xx,0),mesh.length-1);
           xend=Math.min(Math.max(xend,0),mesh.length-1);
           zstart=Math.min(Math.max(zstart,0),mesh[0].length-1);
           zend=Math.min(Math.max(zend,0),mesh[0].length-1);
       
       for(;xx<=xend;xx++) {
          for(zz=zstart;zz<=zend;zz++) {
      collision|= pos.x + rad >= bbmesh[xx][zz].minx && 
              pos.z + rad >= bbmesh[xx][zz].minz && 
              pos.x - rad <= bbmesh[xx][zz].maxx &&
              pos.z - rad <= bbmesh[xx][zz].maxz?SphereCast.sphereCast(mesh[xx][zz], pos, rad):false;
          }
       }
   }
       else { collision|= pos.x + rad >= minx &&
              pos.z + rad >= minz && 
              pos.x - rad <= maxx &&
              pos.z - rad <= maxz?SphereCast.sphereCast(fullMesh, pos, rad):false;
       }
       return collision;
   }

   public final boolean isPointInRoomBox(int x, int y, int z) {
      return x >= minx && z >= minz && y >= miny && x <= maxx && z <= maxz && (openSky || y <= maxy);
   }

    public final int isPointOnMesh(int x, int y, int z) {
        if(!isPointInRoomBox(x, y, z)) return -1;
                
        RenderObject[] objs = fullMesh.getPolygons();

        for(int i=0; i<objs.length; i++) {
            if(isPointOnPolygon(x, y, z, objs[i])) return i;
        }

        return -1;
    }


    public void computeHeight(Height height) {
        Vector3D pos = height.getPosition();
        if(mesh != null) {
            int xx = (pos.x - minx) / chunkSize;
            int zz = (pos.z - minz) / chunkSize;
            if(xx >= mesh.length || xx < 0) return;
            if(zz >= mesh[0].length || zz < 0) return;
            if(HeightComputer.isPointAABBCollision(pos.x, pos.z, bbmesh[xx][zz].minx - 500, bbmesh[xx][zz].maxx + 500, bbmesh[xx][zz].minz - 500, bbmesh[xx][zz].maxz + 500)) {
                HeightComputer.computeHeight(mesh[xx][zz], height);
            }
        } else {
            if(HeightComputer.isPointAABBCollision(pos.x, pos.z, minx - 500, maxx + 500, minz - 500, maxz + 500)) {
                HeightComputer.computeHeight(this.fullMesh, height);
            }
        }
    }


    private static boolean isPointOnPolygon(int x, int z, RenderObject obj) {
        Vertex v1;
        Vertex v2;
        Vertex v3;
        Vertex v4;
        if(obj instanceof Polygon3V) {
            Polygon3V p = (Polygon3V) obj;
            v1 = p.a;
            v2 = p.b;
            v3 = p.c;
            return MathUtils2.isPointOnPolygon(x, z,
                    v1.x, v1.z,
                    v2.x, v2.z,
                    v3.x, v3.z,
                    p.ny);

        } else if(obj instanceof Polygon4V) {
            Polygon4V p = (Polygon4V) obj;
            v1 = p.a;
            v2 = p.b;
            v3 = p.c;
            v4 = p.d;
            return MathUtils2.isPointOnPolygon(x, z,
                    v1.x, v1.z,
                    v2.x, v2.z,
                    v3.x, v3.z,
                    v4.x, v4.z,
                    p.ny);
        }
        return false;
    }
    
    private static boolean isPointOnPolygon(int x, int y, int z, RenderObject obj) {
        Vertex v1;
        Vertex v2;
        Vertex v3;
        Vertex v4;
        if(obj instanceof Polygon3V) {
            Polygon3V p = (Polygon3V) obj;
            if(p.ny >= 0) return false;
            v1 = p.a;
            v2 = p.b;
            v3 = p.c;
            if(MathUtils2.computePolygonY(v1, p.nx, p.ny, p.nz, x, y, z) <= y) 
                return MathUtils2.isPointOnPolygon(x, z, v3.x, v3.z, v2.x, v2.z, v1.x, v1.z);
            
            return false;

        } else if(obj instanceof Polygon4V) {
            Polygon4V p = (Polygon4V) obj;
            if(p.ny >= 0) return false;
            v1 = p.a;
            v2 = p.b;
            v3 = p.c;
            v4 = p.d;
            if(MathUtils2.computePolygonY(v1, p.nx, p.ny, p.nz, x, y, z) <= y) 
                return MathUtils2.isPointOnPolygon(x, z, v4.x, v4.z, v3.x, v3.z, v2.x, v2.z, v1.x, v1.z);
            
            return false;
        }
        
        return false;
    }


   public final int getMinX() {
      return this.minx;
   }
   
   public final int getMaxZ() {
      return this.maxz;
   }
      
   public final int getMinZ() {
      return this.minz;
   }

   public final int getMaxX() {
      return this.maxx;
   }

   public final int getMinY() {
      return this.miny;
   }
   
   public final int getMaxY() {
      return this.maxy;
   }

   public boolean isOnRoom(int x,int y) {
       return !(x>x2+10
               || x<x1-10
               || y>y2+10
               || y<y1-10);
   }
   
   public void addObject(RoomObject obj) {
        if(!objects.contains(obj)) {
            objects.addElement( obj );
        } else {
            if(DeveloperMenu.debugMode) System.out.println( "Room: такой обьект уже содержится" );
    }
   }
   
    public void removeObject(RoomObject obj) {
        boolean remove = objects.removeElement(obj);
        if(!remove && DeveloperMenu.debugMode) {
            System.out.println( "Room: такого обьекта не было" );
        }
    }

    public Vector getObjects() {
        return objects;
    }
    
    public void getObjects(Vector buf) {
        for (int i = 0; i < objects.size(); i++) {
            RoomObject obj = (RoomObject)objects.elementAt(i);
            if(!buf.contains(obj)) buf.addElement( (RoomObject)obj );
        }
    }

   //public aq() {}

   public static Mesh loadMesh(String file, float scaleX, float scaleY, float scaleZ,MultyTexture texs) {
      Mesh var12 = null;
      InputStream var13 = null;
      DataInputStream var14 = null;

      try {
         var13 = (new Object()).getClass().getResourceAsStream(file);
         var14 = new DataInputStream(var13);
         var12 = createFrom3d(file, var14, 1.0F, 1.0F, 1.0F,texs);
      } catch (Exception var10) {
         System.err.println("ERROR in Loader.Load: " + var10);
      } finally {
         try {
            var14.close();
            var13.close();
         } catch (Exception var9) {
            ;
         }

      }

      return var12;
   }

   public static Mesh[] loadMeshes(String file, float scaleX, float scaleY, float scaleZ,MultyTexture texs) {
      Mesh[] var4 = null;
      InputStream var5 = null;
      DataInputStream var6 = null;

      try {
         var5 = (new Object()).getClass().getResourceAsStream(file);
         var4 = new Mesh[(var6 = new DataInputStream(var5)).readInt()];

         for(int var7 = 0; var7 < var4.length; ++var7) {
            var4[var7] = createFrom3d(file, var6, scaleX, scaleY, scaleZ,texs);
         }
      } catch (Exception var14) {
         System.err.println("ERROR in Loader.Load: " + var14);
      } finally {
         try {
            var6.close();
            var5.close();
         } catch (Exception var13) {
            ;
         }

      }

      return var4;
   }
public static Mesh[] loadMeshes(String file, float scaleX, float scaleY, float scaleZ) {
      Mesh[] var4 = null;
      InputStream var5 = null;
      DataInputStream var6 = null;

      try {
         var5 = (new Object()).getClass().getResourceAsStream(file);
         var4 = new Mesh[(var6 = new DataInputStream(var5)).readInt()];

         for(int var7 = 0; var7 < var4.length; ++var7) {
            var4[var7] = createFrom3d(file, var6, scaleX, scaleY, scaleZ,null);
         }
      } catch (Exception var14) {
         System.err.println("ERROR in Loader.Load: " + var14);
      } finally {
         try {
            var6.close();
            var5.close();
         } catch (Exception var13) {
            ;
         }

      }

      return var4;
   }

   private static Mesh createFrom3d(String file, DataInputStream is, float scaleX, float scaleY, float scaleZ,MultyTexture mt) throws Exception {
      Vertex[] var5 = new Vertex[is.readShort()];
int thisMaterial=0;
      int var7;
      int var8;
      int var9;
      for(int var6 = 0; var6 < var5.length; ++var6) {
         var7 = (int)((float)is.readShort() * scaleX);
         var8 = (int)((float)is.readShort() * scaleY);
         var9 = (int)((float)is.readShort() * scaleZ);
         var5[var6] = new Vertex(var7, var8, var9);
      }

      Polygon3V[] var27 = new Polygon3V[is.readShort()];

      byte var10;
      byte var11;
      byte var12;
      byte var13;
      short var18;
      short var31;
      for(var7 = 0; var7 < var27.length; ++var7) {
         short var29 = is.readShort();
if(var29==-32768)
{
thisMaterial=(int)(is.readShort());
var29 = is.readShort();
}

         var31 = is.readShort();
         var18 = is.readShort();
         byte var21 = is.readByte();
         byte var24 = is.readByte();
         var10 = is.readByte();
         var11 = is.readByte();
         var12 = is.readByte();
         var13 = is.readByte();
         Vertex var14 = var5[var29];
         Vertex var15 = var5[var31];
         Vertex var16 = var5[var18];
         if(DirectX7.standartDrawmode==9 && Main.fogQ>=1) var27[var7] = new LightedPolygon3V(var16, var15, var14, var12, var13, var10, var11, var21, var24);
         else if(DirectX7.standartDrawmode==13 && Main.fogQ>=1) var27[var7] = new ColorLightedPolygon3V(var16, var15, var14, var12, var13, var10, var11, var21, var24);
         else var27[var7] = new Polygon3V(var16, var15, var14, var12, var13, var10, var11, var21, var24);
      
         var27[var7].tex=(byte)thisMaterial;

      }

      Polygon4V[] var28 = new Polygon4V[is.readShort()];
thisMaterial=0;

      for(var8 = 0; var8 < var28.length; ++var8) {
         var31 = is.readShort();
if(var31==-32768)
{
thisMaterial=(int)(is.readShort());
var31 = is.readShort();
}
         var18 = is.readShort();
         short var22 = is.readShort();
         short var25 = is.readShort();
         var10 = is.readByte();
         var11 = is.readByte();
         var12 = is.readByte();
         var13 = is.readByte();
         byte var34 = is.readByte();
         byte var33 = is.readByte();
         byte var35 = is.readByte();
         byte var17 = is.readByte();
         Vertex var32 = var5[var31];
         Vertex var19 = var5[var18];
         Vertex var23 = var5[var22];
         Vertex var26 = var5[var25];
         
         if(DirectX7.standartDrawmode==9 && Main.fogQ>=1) var28[var8] = new LightedPolygon4V(var26, var23, var19, var32, var35, var17, var34, var33, var12, var13, var10, var11);
         else if(DirectX7.standartDrawmode==13 && Main.fogQ>=1) var28[var8] = new ColorLightedPolygon4V(var26, var23, var19, var32, var35, var17, var34, var33, var12, var13, var10, var11);
         else var28[var8] = new Polygon4V(var26, var23, var19, var32, var35, var17, var34, var33, var12, var13, var10, var11);
         
         var28[var8].tex=(byte)thisMaterial;


      }

      RenderObject[] var30 = new RenderObject[var27.length + var28.length];
      var9 = 0;

      int var20;
      for(var20 = 0; var20 < var27.length; ++var20) {
         var30[var9] = var27[var20];
         ++var9;
      }

      for(var20 = 0; var20 < var28.length; ++var20) {
         var30[var9] = var28[var20];
         ++var9;
      }

      System.out.println("Mesh [" + file + "] вершин: " + var5.length + " полигонов: " + var30.length);
      return new Mesh(var5, var30,mt);
   }
   
   public void paint(Graphics g, int x, int y) {
       g.setColor(0x00ff00);
       g.drawRect(x+x1,y+y1,x2-x1-1,y2-y1-1);
       g.setColor(0xffffff);
   }

}
