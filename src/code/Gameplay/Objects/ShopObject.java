package code.Gameplay.Objects;

import code.AI.Player;
import code.Gameplay.Arsenal;
import code.Gameplay.GameScreen;
import code.Gameplay.Map.RoomObject;
import code.Gameplay.Map.House;
import code.Rendering.DirectX7;
import code.Math.Vector3D;
import code.Gameplay.Shop;
import code.utils.Asset;
import code.utils.Main;

public final class ShopObject extends RoomObject {
private Vector3D pos;
private int[] newshop,prices;
private String[] files;

public ShopObject(int[] ns,int[] p,String[] f,int x,int y,int z) {
    this.pos=new Vector3D(x,y,z);
    this.newshop=ns;
    for(int i=0;i<this.newshop.length;i++) {
    if(this.newshop[i]==-1) this.newshop[i]=Shop.weapon_count;
    }
    this.prices=p;
    this.files=f;
    this.activable=true;
    this.clickable=true;
    }


   public final void destroy() {
      this.pos=null;
      this.newshop=this.prices=null;
      this.files=null;
   }

public final void render(DirectX7 g3d, int x1, int y1, int x2, int y2) {

   }

public final int getPosX() {
      return this.pos.x;
   }
public final void setPos(int x,int y,int z) {
    pos.set(x,y,z);
}

   public final int getPosZ() {
      return this.pos.z;
   }

public final int getPosY() {
      return this.pos.y;
   }

public void activate(House house,Player player,GameScreen gs) {
if(lastActivate>0 && (lastActivate+timeToReset>GameScreen.time)) return;
if(!isAllCollected(Player.usedPoints,player,house,gs)) {
if(errMessage!=null) errMsg(gs);
return;
}
if(sound!=null && Main.isSounds && Main.sounds!=0) Asset.getSound(sound).start(Main.sounds);

/*if((
singleUse==true &&
contains(Player.usedPoints,name))) {
if(destroyOnUse) house.removeObject(this);
return;
}*/

if(!singleUse || !activated) if(message!=null) prMsg(gs);

if(singleUse==false || this.activated==false) give(additional,player,house,gs);
if(!contains(Player.usedPoints,name)) {
    System.arraycopy(Shop.bckFiles,0,Shop.files,0,Shop.files.length);
    System.arraycopy(Shop.bckPrices,0,Shop.prices,0,Shop.prices.length);
    give(name,player);
    Shop.items=this.newshop;
    Shop.index=this.newshop[0];
    this.activated=true;
    if(this.prices!=null) for(int i=0;i<this.newshop.length;i++) Shop.prices[newshop[i]]=this.prices[i];
    if(this.files!=null) for(int i=0;i<this.newshop.length;i++) Shop.files[newshop[i]]=this.files[i];
if(lastActivate>=0)lastActivate=GameScreen.time;
    gs.openShop();
}



if(destroyOnUse) house.removeObject(this);




}




}
