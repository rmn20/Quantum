package code.Gameplay.Objects;
import code.AI.Player;
import code.Gameplay.GameScreen;
import code.Gameplay.Map.RoomObject;
import code.Gameplay.Map.House;
import code.Rendering.DirectX7;
import code.Math.Vector3D;
import code.utils.Asset;
import code.utils.Main;
import java.util.Vector;
import java.lang.Math;

public final class LVLChange extends RoomObject {
public Vector3D pos;
public int lvl=1;
public Vector3D np;
public int pRot=0;
public boolean saveMus=false;
public boolean fullMove=false;
public boolean showLoadScreen = true;

public LVLChange(int x,int y,int z,Vector3D pos2,int l) {
this.pos=new Vector3D(x,y,z);
this.np=new Vector3D(pos2.x,pos2.y,pos2.z);
this.lvl=l;
}


   public final void destroy() {

   }

public final void render(DirectX7 g3d, int x1, int y1, int x2, int y2) {
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
public final void setPos(int x,int y,int z) {
    pos.set(x,y,z);
}


public void activate(House house,Player player,GameScreen gs) {
if(lastActivate>=0 && (lastActivate+timeToReset>GameScreen.time)) return;
if(!isAllCollected(Player.usedPoints,player,house,gs)) {
if(errMessage!=null) errMsg(gs);
return;
}
if(sound!=null && Main.isSounds && Main.sounds!=0) Asset.getSound(sound).start(Main.sounds);
/*if(
singleUse==true &&
contains(Player.usedPoints,name)) {
if(destroyOnUse) house.removeObject(this);
return;
}*/
if(!singleUse || !activated) if(message!=null) prMsg(gs);

if(destroyOnUse) house.removeObject(this);

if(singleUse==false || this.activated==false) give(additional,player,house,gs);
if(!contains(Player.usedPoints,name)) {
    give(name,player);
    this.activated=true;
if(timeToReset > 0) lastActivate=GameScreen.time;
    
    gs.newPos=new Vector3D(np.x,np.y,np.z);
    player.rotYn(pRot);
    gs.loadLevel(lvl, pos, !saveMus, fullMove, showLoadScreen);
}






}




}
