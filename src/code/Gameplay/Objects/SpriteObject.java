package code.Gameplay.Objects;

import code.AI.Player;
import code.Gameplay.GameScreen;
import code.Gameplay.Map.RoomObject;
import code.Gameplay.Map.House;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.Sprite;
import code.utils.Asset;
import code.utils.QFPS;
import code.utils.Main;

public final class SpriteObject extends RoomObject {

    public Sprite spr = new Sprite(0);
    public int addsz = 0;
    public boolean[] flicker = null;
    public int[] colorFlicker = null;
    private int intframe = 0;

    public SpriteObject() {
        addsz = 2000;
        flicker = null;
        intframe = 0;
    }

    public final void destroy() {
        spr.destroy();
    }

    public final void render(DirectX7 g3d, int x1, int y1, int x2, int y2) {
        if(!visible) return;

        if(lastActivate >= 0 && (lastActivate + timeToReset > GameScreen.time) && hideWhenUnusable) return;

        spr.updateFrame();
        spr.project(g3d.getInvCamera(), g3d);
        if(!spr.isVisible(x1, y1, x2, y2)) return;
        
        int oldFrame = intframe;
        intframe += 100 * QFPS.frameTime / 50;

        if(flicker != null && !flicker[(oldFrame / 125) % (flicker.length)]) return;

        if(colorFlicker != null) spr.color = colorFlicker[(oldFrame / 125) % (colorFlicker.length)];

        g3d.addRenderObject(spr, x1, y1, x2, y2);
        spr.sz += addsz;
    }

    public final int getPosX() {
        return spr.getPosition().x;
    }

    public final int getPosZ() {
        return spr.getPosition().z;
    }

    public final int getPosY() {
        return spr.getPosition().y;
    }

    public final void setPos(int x, int y, int z) {
        spr.getPosition().set(x, y, z);
    }

    public void activate(House house, Player player, GameScreen gs) {
        if(lastActivate >= 0 && (lastActivate + timeToReset > GameScreen.time)) return;
        if(!isAllCollected(Player.usedPoints, player, house, gs)) {
            if(errMessage != null) errMsg(gs);
            return;
        }
        if(sound != null && Main.isSounds && Main.sounds != 0) Asset.getSound(sound).start(Main.sounds);

        /*if((
         singleUse==true &&
         contains(Player.usedPoints,name))) {
         if(destroyOnUse) house.removeObject(this);
         return;
         }*/
        if(!singleUse || !activated) if(message != null) prMsg(gs);
        if(!singleUse || !activated) give(additional, player, house, gs);
        if(!contains(Player.usedPoints, name)) {
            give(name, player);
            this.activated = true;
            if(timeToReset > 0) lastActivate = GameScreen.time;
        }

        if(destroyOnUse) house.removeObject(this);

    }

}
