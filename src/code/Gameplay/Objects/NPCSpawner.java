package code.Gameplay.Objects;
import code.AI.NPC;
import code.AI.Player;
import code.Gameplay.GameScreen;
import code.Gameplay.Map.RoomObject;
import code.Gameplay.Map.House;
import code.Gameplay.Map.Scene;
import code.Rendering.DirectX7;
import code.Math.Vector3D;
import code.utils.Asset;
import code.utils.Main;
import java.util.Vector;

public final class NPCSpawner extends RoomObject {
public Vector3D pos=new Vector3D(0,0,0);
public int rot=0;
public NPC[] bots;
public boolean visible=false;
public long lastVisiblityCheck=0L;
private boolean dead=false;
private long lastSpawn=0L;
public int respawnIn=5000;
public int canSpawn=1;
public int spawned=0;
public long distanceToSpawn=-1L;
public boolean visiblityChecker=true;
private boolean distanceAccess=true;
private long lastDistanceCheck=0l;
public String[] onSpawn;

public int spawnerId = -1;


public NPCSpawner(Vector3D pos2) {
pos.set(pos2);
}


   public final void destroy() {
   }

public final void render(DirectX7 g3d, int x1, int y1, int x2, int y2) {
    if(System.currentTimeMillis()-lastVisiblityCheck>=500 && visiblityChecker) {visible=visiblyCheck(g3d,x1,y1,x2,y2); lastVisiblityCheck=System.currentTimeMillis();}
   }

public final boolean visiblyCheck(DirectX7 g3d, int x1, int y1, int x2, int y2) {
    boolean vis=false;
    for(int i=0;i<bots.length;i++) {
        vis=vis||bots[i].checkVisiblity(g3d, pos.x, pos.y, pos.z, x1, y1, x2, y2);
    }
    return vis;
}

public final boolean deathCheck() {
    boolean dea=true;
    for(int i=0;i<bots.length;i++) {
        dea=dea&&bots[i].isDead();
    }
    return dea;
}

public final boolean renewCheck() {
    boolean dea=false;
    for(int i=0;i<bots.length;i++) {
        dea=dea||bots[i].isTimeToRenew();
    }
    return dea;
}

public final void update(Scene scene, Player player, GameScreen gs) {
    if(dead!=deathCheck()) {dead=deathCheck(); lastSpawn=System.currentTimeMillis();}
    if((canSpawn<=spawned && canSpawn!=-1) || (!renewCheck() && spawned>0)) {return;}
    else {
        if((System.currentTimeMillis()-lastVisiblityCheck>=1000) || !visiblityChecker) visible=false;
        if(distanceAccess==false) if( !visible
                /*&& System.currentTimeMillis()-lastSpawn>=respawnIn*/) if(distance(player)>(distanceToSpawn*distanceToSpawn)) {
                distanceAccess=true;
                lastDistanceCheck=System.currentTimeMillis();
                }
        
        if( !visible && System.currentTimeMillis()-lastDistanceCheck>8000 && System.currentTimeMillis()-lastSpawn>=respawnIn && Main.updateOnlyNear
                ) {distanceAccess=true;lastDistanceCheck=System.currentTimeMillis();}
        if( !visible && System.currentTimeMillis()-lastSpawn>=respawnIn && (distanceToSpawn==-1 || distanceAccess)) {
        spawn(scene.getHouse(),scene,gs);
        }
    }
}

private long distance(Player player) {
  return  (player.getPosX()-getPosX())*(player.getPosX()-getPosX())+
          (player.getPosY()-getPosY())*(player.getPosY()-getPosY())+
          (player.getPosZ()-getPosZ())*(player.getPosZ()-getPosZ());
}

public final void spawn(House house,Scene scene,GameScreen gs) {
    if(canSpawn(house,scene.botLimiter,bots[0].fraction)) {
    for(int i=0;i<bots.length;i++) {
    house.removeObject(bots[i]);
    bots[i].init(pos);
    bots[i].getCharacter().getTransform().m03+=i*10;
    bots[i].getCharacter().rotY(rot);
    house.addObject(bots[i]);
    spawned++;
    }
    distanceAccess=false;
    lastDistanceCheck=System.currentTimeMillis();
    if(onSpawn!=null) give(onSpawn,gs.player,house,gs);
    //return;
    }
    //System.out.println("cant spawn");
}
/*private boolean mulCanSpawn(House house,Scene scene) {
    boolean out=true;
    for(int i=0;i<bots.length;i++) {
    out=out&&canSpawn(house,scene.botLimiter,bots[i].fraction);
    }
    return out;
}*/
public final void setPos(int x,int y,int z) {
    pos.set(x,y,z);
}
private boolean canSpawn(House house,int[] limiter,int frac) {
    if(limiter==null) return true;
    int count=0;
    int max=-1;
    
    for(int i=0;i<limiter.length/2;i++) {
    if(limiter[i*2]==frac) {max=limiter[i*2+1]; break;}
    }
    if(max==-1) return true;
     
    Vector objs;
    if(Main.updateOnlyNear) objs=house.getNearObjects(getPart());
            else objs=house.getObjects();
    
    if(objs==null) return true;
    if(objs.isEmpty()) return true;    
        
    for(int i=0;i<objs.size();i++) {
    RoomObject obj=(RoomObject)objs.elementAt(i);
    if(obj instanceof NPC) if(((NPC)obj).fraction==frac) count++;
    }
    
    if(count+bots.length>max) return false;
    
    return true;
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
if(!singleUse || !activated) give(additional,player,house,gs);
if(!contains(Player.usedPoints,name)) {
    give(name,player);
    spawn(house,gs.scene,gs);
    this.activated=true;
if(lastActivate>=0)lastActivate=GameScreen.time;}


if(destroyOnUse) house.removeObject(this);




}




}
