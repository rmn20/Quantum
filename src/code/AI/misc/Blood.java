package code.AI.misc;

import code.Gameplay.Objects.GameObject;
import code.Math.Vector3D;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.Sprite;
import code.Rendering.Texture;
import code.utils.FPS;
import code.utils.Main;

public class Blood {

    public static Texture blood = null;
    private static byte mir = 0;
    private int frame = 255;
    private Sprite sprite = new Sprite(5);

    public Blood(GameObject obj) {
        sprite.setTextures(new Texture[]{blood});
        sprite.setScale(5);
        sprite.mode = (byte) (blood.rImg.alphaMixing?3:0);
        sprite.mirX = mir >= 2;
        sprite.mirY = mir == 1 || mir == 2;
        
        mir = (byte) ((mir+1)%4);
    }

    public final void reset() {
        sprite.setScale(5);
        frame = 255;
    }

    public final void destroy() {
        sprite.destroy();
        sprite = null;
    }

    public final void bleed() {
        frame = 0;
        sprite.mirX = !sprite.mirX;
        sprite.mirY = !sprite.mirX;
    }

    public final void render(DirectX7 g3d, int sz, Vector3D pos) {
        sprite.getPosition().set(pos.x, pos.y, pos.z);
        frame += FPS.frameTime / 5;
        sprite.setScale((int) (frame / 2 * Main.bloodscale));
        sprite.setOffset(0, -sprite.getHeight() / 2 - frame * 4);
        sprite.project(g3d.getInvCamera(), g3d);
        g3d.addRenderObject(sprite);
        sprite.sz += sz;
    }

    public final boolean isBleeding() {
        return frame < 70;
    }

}
