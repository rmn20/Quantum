package code.Gameplay.Map;

import code.Collision.Height;
import code.Math.Matrix;
import code.Gameplay.Objects.MeshObject;
import code.Collision.Ray;
import code.HUD.DeveloperMenu;
import code.Rendering.DirectX7;
import code.Rendering.Texture;
import code.Math.Vector3D;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;

public final class House {
    public static boolean l2dRoomRendering;
    public static boolean boxRoomTesting;

    private Room[] rooms;
    private Room[][] neighbours;
    private Vector roomsToRender = new Vector(); // ? или renderedRooms
    private final Vector renderedPortals = new Vector();
    private Skybox skybox;
    private final Vector tVec = new Vector();
    private final Vector tVec2 = new Vector();
    private final Vector tmpObj = new Vector();

    public House(Room[] rooms, Room[][] neighbours) {
        this.rooms = rooms;
        this.neighbours = neighbours;

    }

    public final void destroy() {
        for(int i = 0; i < this.rooms.length; ++i) {
            rooms[i].destroy();
            rooms[i] = null;
        }

        rooms = null;
        neighbours = null;
        roomsToRender.removeAllElements();
        renderedPortals.removeAllElements();
        
        roomsToRender = null;
        if(skybox != null) {
            skybox.destroy();
            skybox = null;
        }
        tVec.removeAllElements();
        tVec2.removeAllElements();
        tmpObj.removeAllElements();
    }

    public final void setTexture(Texture texture) {
        for(int i = 0; i < rooms.length; ++i) {
            rooms[i].getMesh().setTexture(texture);
        }
    }

    public final void setSkybox(Skybox skybox) {
        this.skybox = skybox;
    }

    public final Skybox getSkybox() {
        return skybox;
    }

    public final Room[] getRooms() {
        return rooms;
    }

    public final Room[] getNeighbourRooms(int part) {
        return neighbours[part];
    }

    private Vector getNearRooms(int part) {
        roomsToRender.removeAllElements();
        
        if(part != -1) {
            if(rooms[part] != null) roomsToRender.addElement(rooms[part]);

            Room[] nei = neighbours[part];

            for(int i=0; i<nei.length; i++) {
                if(nei[i] != null) roomsToRender.addElement(nei[i]);
            }

        }
        return roomsToRender;
    }

    public final boolean sphereCast(int part, Vector3D pos, int rad) {
        boolean col = false;

        Vector nearRms = getNearRooms(part); //Spherecast rooms
        if(nearRms.size() > 0) {
            for(int i=0; i<nearRms.size(); i++) {
                Room room = (Room) nearRms.elementAt(i);
                col |= room.sphereCast(pos, rad);
            }
        }

        getNearObjects(tVec, part); //Spherecast meshobjects
        if(tVec.size() >= 1) {
            for(int i = 0; i < tVec.size(); i++) {

                Object obj = tVec.elementAt(i);
                if(obj instanceof MeshObject) {
                    MeshObject point = (MeshObject) obj;
                    if(point.precCol) {
                        col |= point.sphereCollisionTest(pos, rad);
                    }
                }

            }
        }

        return col;
    }

    public final boolean sphereCast(int parts[], Vector3D pos, int rad) {
        renderedPortals.removeAllElements();
        boolean col = false;

        for(int i = 0; i < parts.length; i++) { //Spherecast rooms
            int part = parts[i];
            Vector nearRms = getNearRooms(part);

            if(nearRms.size() >= 1) {
                for(int i2 = 0; i2 < nearRms.size(); i2++) {

                    Room room = (Room) nearRms.elementAt(i2);
                    if(!renderedPortals.contains(room)) {
                        col |= room.sphereCast(pos, rad);
                        renderedPortals.addElement(room);
                    }

                }
            }

        }

        getNearObjects(tVec, parts[0]); //Spherecast meshobjects only in one part cause other SHOULD be nighbour
        if(tVec.size() >= 1) {
            for(int i = 0; i < tVec.size(); i++) {

                Object obj = tVec.elementAt(i);
                if(obj instanceof MeshObject) {
                    MeshObject point = (MeshObject) obj;
                    if(point.precCol) {
                        col |= point.sphereCollisionTest(pos, rad);
                    }
                }

            }
        }

        return col;
    }

    public final void rayCast(int part, Ray ray, boolean rayIsFromWeapons) {
        if(part == -1) return;

        Vector nearRms = getNearRooms(part); //Raycast rooms
        for(int i = 0; i < nearRms.size(); i++) {
            ((Room) nearRms.elementAt(i)).rayCast(ray);
        }

        getNearObjects(tVec, part); //Raycast meshobjects
        for(int i = 0; i < tVec.size(); i++) {
            Object obj = tVec.elementAt(i);

            if(obj instanceof MeshObject) {
                MeshObject point = (MeshObject) obj;
                if(point.precCol && (!rayIsFromWeapons || !point.ignoreWeaponRayCast)) {
                    point.rayCast(ray);
                }
            }
        }

    }

    public final void rayCastNoObjects(int part, Ray ray) {
        if(part == -1) return;

        Vector nearRms = getNearRooms(part); //Raycast rooms
        for(int i = 0; i < nearRms.size(); i++) {
            ((Room) nearRms.elementAt(i)).rayCast(ray);
        }
    }

    public void computeHeight(int part, Height height) {
        if(part == -1) return;
        rooms[part].computeHeight(height);

        Room[] near = neighbours[part];
        for(int i=0; i<near.length; i++) {
            if(near[i] != null) near[i].computeHeight(height);
        }
    }

    public void computeHeightFull(int part, Height height) {
        if(part == -1) return;
        computeHeight(part, height);

        getNearObjects(tVec, part);
        for(int i = 0; i < tVec.size(); i++) {
            Object obj = tVec.elementAt(i);

            if(obj instanceof MeshObject) {
                MeshObject point = (MeshObject) obj;
                if(point.precCol) {
                    point.computeHeight(height);
                }
            }
        }
    }

    public final void getNearObjects(Vector buf, int part) {
        buf.removeAllElements();
        if(part == -1) return;
        
        if(rooms.length == 1) {
            Vector list2 = rooms[0].getObjects();
            
            for(int i=list2.size()-1; i>=0; i--) {
                buf.addElement(list2.elementAt(i));
            }
            
            return;
        }

        rooms[part].getObjects(buf);
        Room[] near = neighbours[part];

        for(int i = 0; i < near.length; i++) {
            if(near[i] != null) {
                near[i].getObjects(buf);
            }
        }
    }

    public final Vector getNearObjects(int part) {
        tmpObj.removeAllElements();
        if(part == -1) return tmpObj;
        
        if(rooms.length == 1) {
            Vector list2 = rooms[0].getObjects();
            
            for(int i=list2.size()-1; i>=0; i--) {
                tmpObj.addElement(list2.elementAt(i));
            }
            
            return tmpObj;
        }

        rooms[part].getObjects(tmpObj);

        Room[] near = neighbours[part];
        if(near == null) return tmpObj;

        for(int i = 0; i < near.length; i++) {
            if(near[i] != null) {
                near[i].getObjects(tmpObj);
            }
        }

        return tmpObj;
    }
    
    private final void renderSkybox(Graphics g, DirectX7 g3d) {
        if(skybox != null && (skybox.draw || skybox.skyboxAlways)) {
            skybox.render(g, g3d);
            g3d.render();
            skybox.resetViewport();
        }
    }

    public final int render(Graphics g, DirectX7 g3d, int part[], int cx, int cz) {
        renderSkybox(g,g3d);
        if(part.length == 0) return 0;
        
        roomsToRender.removeAllElements();
        renderedPortals.removeAllElements();
        
        for(int x=part.length-1; x>=0; x--) {
            if(part[x] == -1) continue;
            renderRoom(g3d, rooms[part[x]], 0, 0, g3d.getWidth(), g3d.getHeight(), cx, cz, false);
        }

        if(roomsToRender.size() > 0) {
            for(int x=0; x<roomsToRender.size(); x++) {
                Room room = (Room) roomsToRender.elementAt(x);
                room.render(g3d, cx, cz);
                room.renderObjects(g3d);
            }
        }

        return roomsToRender.size();

    }

    public final int render(Graphics g, DirectX7 g3d, int part, int cx, int cz) {
        renderSkybox(g,g3d);
        if(part == -1) return 0;

        roomsToRender.removeAllElements();
        renderedPortals.removeAllElements();
        
        renderRoom(g3d, rooms[part], 0, 0, g3d.getWidth(), g3d.getHeight(), cx, cz, false);

        for(int i=0; i<roomsToRender.size(); i++) {
            Room room = (Room) roomsToRender.elementAt(i);
            room.render(g3d, cx, cz);
            room.renderObjects(g3d);
        }

        return roomsToRender.size();
    }

    private void renderRoom(DirectX7 g3d, Room mainRoom, int x1, int y1, int x2, int y2, int cx, int cz, boolean renderRooms) {
        if(mainRoom == null) return;
        
        if(!roomsToRender.contains(mainRoom)) {
            roomsToRender.addElement(mainRoom);
            mainRoom.setViewport(x1, y1, x2, y2);
        } else mainRoom.addViewport(x1, y1, x2, y2);
        
        if(renderRooms) mainRoom.render(g3d, cx, cz);

        if(mainRoom.isOpenSky() && skybox != null) {
            skybox.addViewport(x1, y1, x2, y2);
            skybox.draw = true;
        }

        Portal[] portals = mainRoom.getPortals();

        if(portals == null) return;
        if(portals.length == 0) return;

        for(int i=0; i<portals.length; i++) {
            Portal portal = portals[i];
            if(!portal.isVisible(g3d, x1, y1, x2, y2)) continue;

            Room room = portal.getRoom();
            boolean renderedBefore = roomsToRender.contains(room);

            int pMinX = portal.getMinX();
            int pMinY = portal.getMinY();
            int pMaxX = portal.getMaxX();
            int pMaxY = portal.getMaxY();
            if(pMinX < x1) pMinX = x1;
            if(pMinY < y1) pMinY = y1;
            if(pMaxX > x2) pMaxX = x2;
            if(pMaxY > y2) pMaxY = y2;

            int sizex = pMaxX - pMinX;
            int sizey = pMaxY - pMinY;
            if(sizex <= 0 || sizey <= 0) continue;

            if(!renderedPortals.contains(portal)) renderedPortals.addElement(portal);

            if(room != null) {

                if(renderedBefore && room.viewportContains(pMinX, pMinY, pMaxX, pMaxY)) continue;

                renderRoom(g3d, room, pMinX, pMinY, pMaxX, pMaxY, cx, cz, renderRooms);

            } else if(skybox != null) {
                skybox.addViewport(pMinX, pMinY, pMaxX, pMaxY);;
                skybox.draw = true;
            }


        }

    }
    
    public void drawPortals(Graphics g, int x, int y) {
        for(int i=0; i<renderedPortals.size(); i++) {
            Portal p = (Portal) renderedPortals.elementAt(i);
            p.paint(g, x, y);
        }
        
        for(int i=0; i<roomsToRender.size(); i++) {
            Room room = (Room) roomsToRender.elementAt(i);
            room.paint(g, x, y);
        }
    }

    public final Vector getObjects() {
        tVec2.removeAllElements();
        for(int i=0; i<rooms.length; i++) {
            rooms[i].getObjects(tVec2);
        }
        return tVec2;
    }

    public void addObject(RoomObject obj) {
        recomputePart(obj);
        
        if(obj.getPart() == -1) recomputePart(obj, false);
        
        if(obj.getPart() == -1 && DeveloperMenu.debugMode) {
            System.out.println("House: Объект "+obj.toString()+" находится вне карты и не может быть добавлен");
            System.out.println(obj.getPosX() + " " + obj.getPosY() + " " + obj.getPosZ());
        }
    }

    public void removeObject(RoomObject obj) {
        if(obj.getPart() == -1) return;
        
        rooms[obj.getPart()].removeObject(obj);
    }

    public void recomputePart(RoomObject obj) {
        recomputePart(obj, true);
    }
    
    public void recomputePart(RoomObject obj, boolean rayCast) {
        if(!obj.isNeedRecomputePart()) return;

        int x = obj.getPosX();
        int z = obj.getPosZ();
        int y = obj.getPosY();
        int oldPart = obj.getPart();
        int newPart = calcPart(oldPart, x, y, z, rayCast);

        if(obj.getNewPart() != -1) {
            newPart = obj.getNewPart();
            obj.setNewPart(-1);
        }
        if(newPart == -1) newPart = oldPart;

        if(newPart != -1) {
            if(newPart != oldPart || oldPart == -1) {
                if(oldPart != -1) rooms[oldPart].removeObject(obj);
                
                rooms[newPart].addObject(obj);
                obj.setPart(newPart);
            }
        }

    }

    public int calcPart(int oldPart, int x, int y, int z) {
        return calcPart(oldPart, x, y, z, true);
    }
    
    public int calcPart(int oldPart, int x, int y, int z, boolean rayCast) {
        if(rooms.length == 1) return 0;

        if(oldPart != -1) {
            if(rayCast && rooms[oldPart].isPointOnMesh(x, y, z) != -1) return oldPart;
            if(!rayCast && rooms[oldPart].isPointInRoomBox(x, y, z)) return oldPart;

            Room[] neighbours2 = neighbours[oldPart];
            for(int i=0; i<neighbours2.length; i++) {
                Room room = neighbours2[i];
                if(room == null) continue;
                
                if(rayCast && room.isPointOnMesh(x, y, z) != -1) return room.getId();
                else if(!rayCast && room.isPointInRoomBox(x, y, z)) return room.getId();
            }
        }

        for(int i=0; i<rooms.length; i++) {
            Room room = rooms[i];
            
            if(rayCast && room.isPointOnMesh(x, y, z) != -1) return i;
            else if(!rayCast && room.isPointInRoomBox(x, y, z)) return i;
        }

        return -1;
    }

    public static int[] getParts(House house, int part, Matrix cam) {
        if(part == -1) return new int[]{0};
        Room[] rooms = house.getNeighbourRooms(part);
        if(rooms == null || rooms.length==0) return new int[]{part};
        
        int[] out = new int[rooms.length+1];
        out[0] = part;
        int roomsCount = 1;

        int x = cam.m03;
        int y = cam.m13;
        int z = cam.m23;

        for(int i=0; i<rooms.length; i++) {
            Room room = rooms[i];
            
            if(!boxRoomTesting && room.isPointOnMesh(x, y, z) != -1) {
                out[roomsCount] = room.getId();
                roomsCount++;
            } else if(boxRoomTesting && room.isPointInRoomBox(x, y, z)) {
                out[roomsCount] = room.getId();
                roomsCount++;
            } 
        }

        int[] out2 = new int[roomsCount];
        System.arraycopy(out, 0, out2, 0, roomsCount);
        
        return out2;
    }

    public boolean isNear(int id1, int id2) {
        if(id1 == -1 || id2 == -1) return false;
        if(id1 == id2) return true;
        if(id1 >= neighbours.length) return false;

        Room[] rooms = neighbours[id1];
        if(rooms == null) return false;

        for(int i=0; i<rooms.length; i++) {
            Room room = rooms[i];
            if(room != null && room.getId() == id2) return true;
        }

        return false;
    }

    public void sortLights(Light[] lights) {
        for(int i=0; i<lights.length; i++) {
            if(lights[i].part == -1) lights[i].part = calcPart(-1, lights[i].pos.x, lights[i].pos.y, lights[i].pos.z);
        }

        Vector lits = new Vector();
        for(int i=0; i<rooms.length; i++) {
            Room room = rooms[i];
            if(room == null) continue;
            
            lits.removeAllElements();
            int id = room.getId();
            for(int x=0; x<lights.length; x++) {
                if(isNear(id, lights[x].part)) lits.addElement(lights[x]);
            }
            
            if(lits.size() > 0) {
                room.lights = new Light[lits.size()];
                for(int x=0; x<lits.size(); x++) {
                    room.lights[x] = (Light) lits.elementAt(x);
                }
            }
            
        }
    }

}
