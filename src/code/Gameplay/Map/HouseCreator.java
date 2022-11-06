package code.Gameplay.Map;

import code.Math.MathUtils2;
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

    public static House create(Mesh[] meshes, boolean np, String lightdataFile, boolean optimizeLevel) {
        House house;

        if(optimizeLevel) for(int i = 0; i < meshes.length; i++) {
            meshes[i].optimize();
        }
        
        if(lightdataFile != null && (DirectX7.standartDrawmode == 9 || DirectX7.standartDrawmode == 13)) {
            LightMapper.loadLightMap(meshes, lightdataFile);
        }

        if(meshes.length > 1) {
            RenderObject[] portalPols = meshes[meshes.length - 1].getPolygons(); // meshPortals
            Room[] rooms = new Room[meshes.length - 1]; // rooms

            // создание комнат

            for(int i = 0; i < meshes.length - 1; i++) {
                rooms[i] = new Room(meshes[i], i);
            }
            
            for(int i = 0; i < rooms.length; i++) {
                Room room = rooms[i];
                Vector portalsList = new Vector();

                for(int t = 0; t < portalPols.length; ++t) {
                    RenderObject portal = portalPols[t];
                    Vertex[] portalVerts;
                    
                    if(portal instanceof Polygon4V) {
                        Polygon4V var35 = (Polygon4V) portal;
                        portalVerts = new Vertex[]{var35.a, var35.b, var35.c, var35.d};
                    } else if(portal instanceof Polygon3V) {
                        Polygon3V var10 = (Polygon3V) portal;
                        portalVerts = new Vertex[]{var10.a, var10.b, var10.c};
                    } else {
                        portalVerts = null;
                    }

                    if(isExistsCommonCoords(room.getMesh(), portalVerts)) {
                        Mesh mesh = room.getMesh();
                        
                        Vector3D roomCenter = computeCentre(mesh.getVertices());
                        Vector3D portalCenter = computeCentre(portalVerts);
                        
                        Vector3D var32 = MathUtils2.calcNormal(portalVerts[0], portalVerts[1], portalVerts[2]);
                        
                        roomCenter.add(-portalCenter.x, -portalCenter.y, -portalCenter.z);
                        
                        if(var32.dot(roomCenter) >= 0) {
                            reverse(portalVerts);
                        }

                        portalsList.addElement(new Portal(portalVerts));
                    }
                }

                Portal[] portals = new Portal[portalsList.size()];
                portalsList.copyInto(portals);
                
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


        if(lightdataFile == null && (DirectX7.standartDrawmode == 9 || DirectX7.standartDrawmode == 13)) {
            LightMapper.generateLightMapSaveThread(house, meshes, lightdataFile);
        }

        return house;
    }

    private static void countPortals(Room[] roomsList) {
        int portalsFound = 0;
        int roomsFound = 0;

        for(int i = 0; i < roomsList.length; ++i) {
            
            Portal[] portals = roomsList[i].getPortals();
            if(portals == null || portals.length == 0) roomsFound++;

            for(int t = 0; t < portals.length; ++t) {
                if(portals[t].getRoom() == null) portalsFound++;
            }
        }

        if(portalsFound > 0) {
            System.out.println("HouseCreator: " + portalsFound + " порталам не найдены комнаты");
        }

        if(roomsFound > 0) {
            System.out.println("HouseCreator: " + roomsFound + " комнатам не найдены порталы");
        }

    }

    // из MeshImage: private static Vertex computeCentre(Vertex[] vertices) {
    private static Vector3D computeCentre(Vertex[] vertices) {
        long x = 0L;
        long y = 0L;
        long z = 0L;

        for(int i = 0; i < vertices.length; ++i) {
            Vertex v = vertices[i];
            x += v.x;
            y += v.y;
            z += v.z;
        }

        x /= vertices.length;
        y /= vertices.length;
        z /= vertices.length;
        
        return new Vector3D((int) x, (int) y, (int) z);
    }

    private static void reverse(Vertex[] vertices) {
        Vertex[] newList = new Vertex[vertices.length];

        for(int i = 0; i < newList.length; ++i) {
            newList[i] = vertices[vertices.length - 1 - i];
        }

        System.arraycopy(newList, 0, vertices, 0, newList.length);
    }

    private static void findRooms(Room room, Room[] rooms) {
        Portal[] portals = room.getPortals();
        int i = 0;

        while(i < portals.length) {
            Portal portal = portals[i];
            
            int t = 0;
            while(true) {
                if(t < rooms.length) {
                    Room var6 = rooms[t];
                    if(var6 == room || !isExistsCommonCoords(var6.getMesh(), portal.getVertices())) {
                        ++t;
                        continue;
                    }

                    portal.setRoom(var6);
                }

                i++;
                break;
            }
        }

    }

    private static Room[][] createNeighbours(Room[] rooms) {
        Room[][] neighbours = new Room[rooms.length][];
        for(int i = 0; i < neighbours.length; i++) {
            Vector vRooms = getRooms(rooms[i]);
            neighbours[i] = new Room[vRooms.size()];
            vRooms.copyInto(neighbours[i]);
        }
        return neighbours;
    }

    private static Vector getRooms(Room room) {
        Portal[] portals = room.getPortals();
        Vector rooms = new Vector();
        for(int i = 0; i < portals.length; i++) {
            Portal p = portals[i];
            Room secondRoom = p.getRoom();
            if(secondRoom != null && !rooms.contains(secondRoom)) {
                rooms.addElement(secondRoom);
            }
        }
        return rooms;
    }

    private static boolean isExistsCommonCoords(Mesh mesh, Vertex[] poly) {
        int i = 0;

        while(i < poly.length) {
            Vertex v = poly[i];
            Vertex[] verts = mesh.getVertices();
            int var5 = 0;

            while(true) {
                boolean var10000;
                if(var5 < verts.length) {
                    Vertex var6;
                    if((var6 = verts[var5]).x / 50 != v.x / 50 || var6.y / 50 != v.y / 50 || var6.z / 50 != v.z / 50) {
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

                ++i;
                break;
            }
        }

        return false;
    }
}
