package code.Gameplay.Map;

import code.Rendering.Meshes.Mesh;
import code.Rendering.Meshes.Polygon3V;
import code.Rendering.RenderObject;
import code.Rendering.Vertex;
import code.Math.Vector3D;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.Polygon4V;
import java.util.Vector;
import code.utils.Main;

public class HouseCreator {

   public static House create(Mesh[] meshes,boolean np,String lightdataFile) {
       House house;
       
       for (int i = 0; i < meshes.length; i++) {
               meshes[i].optimize();
       }
       if (lightdataFile != null && (DirectX7.standartDrawmode == 9 || DirectX7.standartDrawmode == 13)) {
           LightMapper.loadLightMap(meshes, lightdataFile);
       }
       
       if (meshes.length > 1) {
           RenderObject[] ro = meshes[meshes.length - 1].getPolygons(); // meshPortals
           Room[] rooms = new Room[meshes.length - 1]; // rooms
           
           // создание комнат

            for (int i=0; i<meshes.length-1; i++) {
                rooms[i] = new Room(meshes[i], i);
            }
           for (int i=0; i<rooms.length; i++) {
               Room room = rooms[i];
               RenderObject[] polygons = ro;
               Vector portalVerts = new Vector();

               for (int i2 = 0; i2 < polygons.length; ++i2) {
                   RenderObject pol = polygons[i2];
                   Vertex[] vertices;
                   if (pol instanceof Polygon3V) {
                       Polygon3V var10 = (Polygon3V) pol;
                       vertices = new Vertex[]{var10.a, var10.b, var10.c};
                   } else if (pol instanceof Polygon4V) {
                       Polygon4V var35 = (Polygon4V) pol;
                       vertices = new Vertex[]{var35.a, var35.b, var35.c, var35.d};
                   } else {
                       vertices = null;
                   }

                   if (isExistsCommonCoords(room.getMesh(), vertices)) {
                       Mesh mesh = room.getMesh();
                       Vector3D var34 = computeCentre(mesh.getVertices());
                       Vector3D var11 = computeCentre(vertices);
                       Vertex var27 = vertices[0];
                       Vertex var10001 = vertices[1];
                       Vertex var31 = vertices[2];
                       Vertex var13 = var10001;
                       Vertex var12 = var27;
                       long var18 = (long) (var27.y - var13.y) * (long) (var12.z - var31.z) - (long) (var12.z - var13.z) * (long) (var12.y - var31.y);
                       long var20 = (long) (var12.z - var13.z) * (long) (var12.x - var31.x) - (long) (var12.x - var13.x) * (long) (var12.z - var31.z);
                       long var22 = (long) (var12.x - var13.x) * (long) (var12.y - var31.y) - (long) (var12.y - var13.y) * (long) (var12.x - var31.x);
                       double var24 = Math.sqrt((double) (var18 * var18 + var20 * var20 + var22 * var22)) / 4096.0D;
                       int var33 = (int) ((double) var18 / var24);
                       int var37 = (int) ((double) var20 / var24);
                       int var38 = (int) ((double) var22 / var24);
                       Vector3D var32 = new Vector3D(var33, var37, var38);
                       var34.x -= var11.x;
                       var34.y -= var11.y;
                       var34.z -= var11.z;
                       if (var32.dot(var34) >= 0) {
                           reverse(vertices);
                       }

                       portalVerts.addElement(new Portal(vertices));
                   }
               }

               Portal[] portals = new Portal[portalVerts.size()];
               portalVerts.copyInto(portals);
               room.setPortals(portals);
               findRooms(room, rooms);
           }

           countPortals(rooms);
           Room[][] neighbours = createNeighbours(rooms);
           house = new House(rooms, neighbours);
       } else {
           RenderObject[] polygons = meshes[0].getPolygons(); // meshPortals
           Room[] rooms = new Room[1]; // rooms

           // создание комнат
           Room[][] neighbours = new Room[1][1];
           
           rooms[0] = new Room(meshes[0], 0);
           neighbours[0][0] = rooms[0];

           house = new House(rooms, neighbours);
       }
       

       if (lightdataFile == null && (DirectX7.standartDrawmode == 9 || DirectX7.standartDrawmode == 13)) {
           LightMapper.generateLightMapSaveThread(house, meshes, lightdataFile);
       }

      return house;
   }

   private static void countPortals(Room[] rooms) {
      int var1 = 0;
      int var2 = 0;

      for(int var3 = 0; var3 < rooms.length; ++var3) {
         Portal[] var4;
         if((var4 = rooms[var3].getPortals()) == null || var4.length == 0) {
            ++var2;
         }

         for(int var5 = 0; var5 < var4.length; ++var5) {
            if(var4[var5].getRoom() == null) {
               ++var1;
            }
         }
      }

      if(var1 > 0) {
         System.out.println("HouseCreator: " + var1 + " порталам не найдены комнаты");
      }

      if(var2 > 0) {
         System.out.println("HouseCreator: " + var2 + " комнатам не найдены порталы");
      }

   }

   // из MeshImage: private static Vertex computeCentre(Vertex[] vertices) {
   private static Vector3D computeCentre(Vertex[] vertices) {
      long var1 = 0L;
      long var3 = 0L;
      long var5 = 0L;

      for(int var7 = 0; var7 < vertices.length; ++var7) {
         Vertex var8 = vertices[var7];
         var1 += (long)var8.x;
         var3 += (long)var8.y;
         var5 += (long)var8.z;
      }

      var1 /= (long)vertices.length;
      var3 /= (long)vertices.length;
      var5 /= (long)vertices.length;
      return new Vector3D((int)var1, (int)var3, (int)var5);
   }
   
   private static void reverse(Vertex[] vertices) {
      Vertex[] var1 = new Vertex[vertices.length];

      for(int var2 = 0; var2 < var1.length; ++var2) {
         var1[var2] = vertices[vertices.length - 1 - var2];
      }

      System.arraycopy(var1, 0, vertices, 0, var1.length);
   }

   private static void findRooms(Room room, Room[] rooms) {
      Portal[] var2 = room.getPortals();
      int var3 = 0;

      while(var3 < var2.length) {
         Portal var4 = var2[var3];
         int var5 = 0;

         while(true) {
            if(var5 < rooms.length) {
               Room var6;
               if((var6 = rooms[var5]) == room || !isExistsCommonCoords(var6.getMesh(), var4.getVertices())) {
                  ++var5;
                  continue;
               }

               var4.setRoom(var6);
            }

            ++var3;
            break;
         }
      }

   }

   private static Room[][] createNeighbours(Room[] rooms) {
        Room[][] neighbours = new Room[rooms.length][];
        for(int i=0; i<neighbours.length; i++) {
            Vector vRooms = getRooms(rooms[i]);
            neighbours[i] = new Room[vRooms.size()];
            vRooms.copyInto( neighbours[i] );
        }
        return neighbours;
   }
   
   private static Vector getRooms(Room room) {
        Portal[] portals = room.getPortals();
        Vector rooms = new Vector();
        for(int i=0; i<portals.length; i++) {
            Portal p = portals[i];
            Room secondRoom = p.getRoom();
            if(secondRoom!=null && !rooms.contains(secondRoom)) rooms.addElement(secondRoom);
        }
        return rooms;
    }


   private static boolean isExistsCommonCoords(Mesh mesh, Vertex[] poly) {
      int var2 = 0;

      while(var2 < poly.length) {
         Vertex var4 = poly[var2];
         Vertex[] var3 = mesh.getVertices();
         int var5 = 0;

         while(true) {
            boolean var10000;
            if(var5 < var3.length) {
               Vertex var6;
               if((var6 = var3[var5]).x / 50 != var4.x / 50 || var6.y / 50 != var4.y / 50 || var6.z / 50 != var4.z / 50) {
                  ++var5;
                  continue;
               }

               var10000 = true;
            } else {
               var10000 = false;
            }

            if(var10000) {
               return true;
            }

            ++var2;
            break;
         }
      }

      return false;
   }



  

}
