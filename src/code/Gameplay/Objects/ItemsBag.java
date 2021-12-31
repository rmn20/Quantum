package code.Gameplay.Objects;

import code.AI.Player;
import code.Gameplay.Inventory.*;
import code.Gameplay.GameScreen;
import code.Gameplay.Map.RoomObject;
import code.Gameplay.Map.House;
import code.Math.Matrix;
import code.Math.Vector3D;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.Mesh;
import code.Rendering.Meshes.Sprite;
import code.Rendering.MultyTexture;
import code.utils.Asset;
import code.utils.IniFile;
import code.utils.Main;
import code.utils.StringTools;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class ItemsBag extends RoomObject {
    public static int addsz=-1;
    public static Matrix matrix = new Matrix();

    public Vector3D pos = new Vector3D(0, 0, 0);
    public Mesh mesh;
    public Sprite spr;
    
    public ItemList items;

    public ItemsBag(Vector3D pos2) {
        activable = true;
        clickable = true;
        radius = 800;
        
        
        items = new ItemList();
        
        pos.set(pos2);
        
        
        //Junk initialization
        float scale = Main.settings.getFloat("BAG_SCALE",1.0f);
        String meshStr = Main.settings.get("BAG_MESH");
        String tex = Main.settings.get("BAG_TEXTURE");
        if(meshStr!=null) {
            mesh = Asset.getMeshCloneDynamic(meshStr, scale, scale, scale);
            mesh.setTexture(new MultyTexture(tex,true));
        } else {
            spr = new Sprite(Asset.getTexture(tex),(int)scale);
        }
        
        if(addsz==-1) addsz = Main.settings.getInt("BAG_ADDSZ",4500);
    }

    public final void destroy() {}

    public final void render(DirectX7 g3d, int x1, int y1, int x2, int y2) {
        matrix.setPosition(pos);
        Matrix mat = g3d.computeFinalMatrix(matrix);
        
        if(mesh!=null) {
            g3d.transformAndProjectVertices(mesh, mat);
            g3d.addMesh(mesh, x1, y1, x2, y2, mesh.getTexture());
            mesh.increaseMeshSz(addsz);
        } else {
            spr.updateFrame();
            spr.getPosition().set(pos.x,pos.y,pos.z);
            spr.project(g3d.getInvCamera(), g3d);
            if (!spr.isVisible(x1, y1, x2, y2)) return;

            g3d.addRenderObject(spr, x1, y1, x2, y2);
            spr.sz += addsz;
        }
    }

    public final int getPosX() {
        return this.pos.x;
    }

    public final int getPosZ() {
        return this.pos.z;
    }

    public final int getPosY() {
        return this.pos.y;
    }

    public final void setPos(int x, int y, int z) {
        pos.set(x, y, z);
    }

    public void activate(House house, Player player, GameScreen gs) {

        message = Main.getGameText().get("ITEMS_PICKEDUP")+":";
        for(int i=0;i<items.size();i++) {
            int itemId = items.itemAt(i);
            int count = items.itemAtCount(i);
            IniFile item = ItemsEngine.items[itemId];
            
            String itemName = item.get("NAME");
            String tmp = Main.getGameText().get(itemName+"_ITEM");
            if(tmp!=null) itemName = tmp;
            
            message+='*'+itemName;
            if(items.itemAtCount(i)>1) message+=" "+String.valueOf(count)+"x";
            
            player.items.addItem(itemId,count);
            
            String pickupScript = item.get("ON_PICKUP");
            if(pickupScript!=null) gs.runScript(StringTools.cutOnStrings(pickupScript, ';'));
        }
        prMsg(gs);

        house.removeObject(this);
    }
    
    public void writeSave(DataOutputStream dos) throws IOException {
        dos.writeInt(pos.x);
        dos.writeInt(pos.y);
        dos.writeInt(pos.z);
        
        items.writeSave(dos);
    }
    
    public void loadSave(DataInputStream dis) throws IOException {
        pos.set(dis.readInt(), dis.readInt(), dis.readInt());
        
        items.loadSave(dis);
    }
}
