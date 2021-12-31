package code.Gameplay.Objects;
import code.AI.Player;
import code.Gameplay.GameScreen;
import code.Gameplay.Map.RoomObject;
import code.Gameplay.Map.House;
import code.Rendering.DirectX7;
import code.Math.Vector3D;
import code.utils.Asset;
import code.utils.Main;
import javax.microedition.lcdui.Image;

public final class Image2D extends RoomObject {
public Vector3D pos=new Vector3D(0,0,0);
public Image img;
public long timer=0L;

public Image2D(Vector3D pos2,Image img2,long timer2) {
    this.activable=true;
    this.singleUse=true;
    this.destroyOnUse=true;
    this.img=img2;
    this.timer=timer2;
    pos.set(pos2);
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
if(lastActivate>0 && (lastActivate+timeToReset>GameScreen.time)) return;
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
if(singleUse==false || this.activated==false) give(additional,player,house,gs);
if(!contains(Player.usedPoints,name)) {
    give(name,player);
    gs.overlay=img;
    gs.overlayTimeOut=timer;
    gs.overlayStart=System.currentTimeMillis();
    this.activated=true;
if(lastActivate>=0)lastActivate=GameScreen.time;}


if(destroyOnUse) house.removeObject(this);




}




}
