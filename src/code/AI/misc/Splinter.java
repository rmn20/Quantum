package code.AI.misc;

import code.Rendering.DirectX7;
import code.Rendering.Meshes.Sprite;
import code.Rendering.Texture;
import code.utils.FPS;
import code.utils.Main;

public class Splinter {

    public static Texture texture = null;
    private static byte mir = 0;
    private int frame = Integer.MAX_VALUE;
    public static Sprite sprite = new Sprite(5);

    public Splinter() {
    }
    
    public static void cache() {
        sprite.setTextures(new Texture[]{texture});
        sprite.setScale(5);
        sprite.mode = (byte) 3;
        if (!texture.rImg.alphaMixing) sprite.mode = (byte) 0;
    }

    public final void set(int x, int y, int z) {
        sprite.getPosition().set(x, y, z);
        frame = 0;
        sprite.mirX = false;
        sprite.mirY = false;
        if (mir >= 2) sprite.mirX = true;
        if (mir == 1 || mir == 2) sprite.mirY = true;
        mir++;
        if (mir > 3) mir = 0;
        

    }

    public final void project(DirectX7 g3d) {
        sprite.setScale((int) (frame / 2 * Main.splinterscale));
        sprite.setOffset(0, -sprite.getHeight() / 2 - frame * 4);
        sprite.project(g3d.getInvCamera(), g3d);
        sprite.isVisible( 0, 0, g3d.width, g3d.height);
    }

    public final void render(DirectX7 g3d, int sz) {
        frame += FPS.frameTime / 5;
        project(g3d);
        g3d.addRenderObject( sprite);
        sprite.sz += 1500;
    }

    // true, если проигрывается анимация осколка
    public final boolean isShatters() {
        return frame < 30;
    }

}
