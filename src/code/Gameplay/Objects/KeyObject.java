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

public final class KeyObject extends RoomObject {
public Vector3D pos=new Vector3D(0,0,0);

    public KeyObject(Vector3D pos2) {
        this.activable = true;
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
	public void activate(House house, Player player, GameScreen gs) {
		if(lastActivate >= 0 && (lastActivate + timeToReset > GameScreen.time)) return;
		if(!isAllCollected(Player.usedPoints, player, house, gs)) {
			if(errMessage != null) errMsg(gs);
			return;
		}
		if(sound != null && Main.isSounds && Main.sounds != 0) Asset.getSound(sound).start(Main.sounds);

		if(!singleUse || !activated) if(message != null) prMsg(gs);
		if(singleUse == false || this.activated == false) give(additional, player, house, gs);
		if(!contains(Player.usedPoints, name)) {
			give(name, player);
			this.activated = true;
			if(timeToReset > 0) lastActivate = GameScreen.time;
		}


		if(destroyOnUse) house.removeObject(this);




	}




}
