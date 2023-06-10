package code.Gameplay.Objects;

import code.Gameplay.GameScreen;
import code.AI.Player;
import code.Gameplay.Map.Character;
import code.Gameplay.Map.House;
import code.Gameplay.Map.RoomObject;
import code.Gameplay.Map.Scene;
import code.Math.Vector3D;
import code.utils.Asset;
import code.utils.FPS;
import code.utils.Main;

public abstract class GameObject extends RoomObject {

    private int frame;
    private int frame100;
    private float friction = 4f;
    public final Character character = new Character(0, 0);
    private int hp;
    public long DeathTime = 0l;

    public final void setCharacterSize(int modelHeight) {
        character.set((int) (modelHeight / 2.5F), (int) (modelHeight * 0.75F));
    }

    public final void setCharacterSize(int w, int h) {
        character.set(w, h);
    }

    public final void setCharacterSize(int w, int wz, int h) {
        character.set(w, wz, h);
    }

    protected final void jump(int jump, float force) {
        character.jump(jump, force);
    }

    public void update(Scene scene, Player player) {
        if(character.isUpdatable()) {
            character.update();
            character.collisionTest(getPart(), scene.getHouse());
            
            if(character.isOnFloor() || character.fly) {
                Vector3D speed = character.getSpeed();

                speed.x = (int) ((float) (speed.x / friction));
                speed.y = (int) ((float) (speed.y / (friction/**
                         * 200.0F/(float)fps.getFps()
                         */
                        )));
                speed.z = (int) ((float) (speed.z / friction));
            }
        }

        frame++;
        frame100 += 100 * FPS.frameTime / 50;
    }

    // true - если персонаж убит
    public boolean damage(GameObject obj, int dmg) {
        return damage(dmg);
    }

    public boolean damage(int dmg) {
        boolean oldDead = isDead();
        hp -= dmg;
        if(hp < 0) hp = 0;

        if(oldDead != isDead()) {
            frame = 0;
            frame100 = 0;
            DeathTime = System.currentTimeMillis();
            return true;
        } else {
            return false;
        }
    }

    public final Character getCharacter() {
        return character;
    }

    public final int getHp() {
        return hp;
    }

    public final boolean isDead() {
        return hp <= 0;
    }

    public boolean isTimeToRenew() {
        return isDead() && frame > 25 * 50 / (FPS.frameTime==0?1:FPS.frameTime);
    }

    public final void setHp(int hp) {
        this.hp = hp;
    }

    public final int getFrame() {
        return frame;
    }

    public final void setFriction(float f) {
        friction = f;
    }

    public final float getFriction() {
        return friction;
    }

    public final int getFrameInter() {
        return this.frame100;
    }

    public final int getFrameInterDiv() {
        return this.frame100 / 100;
    }

    public final int getPosX() {
        return this.character.getTransform().m03;
    }

    public final int getPosZ() {
        return this.character.getTransform().m23;
    }

    public final int getPosY() {
        return this.character.getTransform().m13;
    }

    public final void setPos(int x, int y, int z) {
        character.getTransform().m03 = x;
        character.getTransform().m13 = y;
        character.getTransform().m23 = z;
    }

    public void activate(House house, Player player, GameScreen gs) {
        if(lastActivate > 0 && (lastActivate + timeToReset > GameScreen.time)) return;
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
            if(lastActivate >= 0) lastActivate = GameScreen.time;
        }

        if(destroyOnUse) house.removeObject(this);

    }

}
