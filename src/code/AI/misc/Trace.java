package code.AI.misc;

import code.AI.Player;
import code.Gameplay.GameScreen;
import code.Gameplay.Map.House;
import code.Gameplay.Objects.GameObject;
import code.Math.Matrix;
import code.Math.Vector3D;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.Polygon4V;
import code.Rendering.RenderObject;
import code.Rendering.Texture;
import code.Rendering.Vertex;
import code.utils.Asset;
import code.utils.Main;

public final class Trace extends GameObject {

    private static Player player;
    private final Polygon4V pol4;
    private final Vertex pos;
    private int f = 0;
    private final Vector3D vec = new Vector3D(1002, 1002, 1002);
    private final Texture tex;

    public Trace(int f, int f1, int f2, Vector3D v3d1, Vector3D v3d2, Vector3D v3d3, Vector3D v3d4, Vector3D size) {
        tex = Asset.getTexture(Main.Blood);
        tex.setPerspectiveCorrection(true);

        character.setCollision(true);
        character.setCollidable(false);
        character.setOnFloor(true);
        character.setSpeedZero();
        character.getTransform().setPosition(f, f1, f2);

        final Matrix mat = new Matrix();
        mat.setIdentity();
        mat.setPosition((int) (f), (int) (f1), (int) (f2));
        pos = new Vertex(f, f1, f2);
        if (size.x == 0) vec.x = 996;
        if (size.y == 0) vec.y = 996;
        if (size.z == 0) vec.z = 996;
        
        Vertex v1 = new Vertex(v3d1.x, v3d1.y, v3d1.z);
        Vertex v2 = new Vertex(v3d2.x, v3d2.y, v3d2.z);
        Vertex v3 = new Vertex(v3d3.x, v3d3.y, v3d3.z);
        Vertex v4 = new Vertex(v3d4.x, v3d4.y, v3d4.z);
        v1.mul(-1700, -1700, -1700);
        v2.mul(-1700, -1700, -1700);
        v3.mul(-1700, -1700, -1700);
        v4.mul(-1700, -1700, -1700);
        v1.div(size.x, size.y, size.z);
        v2.div(size.x, size.y, size.z);
        v3.div(size.x, size.y, size.z);
        v4.div(size.x, size.y, size.z);
        v1.transformFE(mat);
        v2.transformFE(mat);
        v3.transformFE(mat);
        v4.transformFE(mat);
        pol4 = new Polygon4V(v1, v2, v3, v4, (byte) 0, (byte) 0, (byte) 0xff, (byte) 0, (byte) 0xff, (byte) 0xff, (byte) 0, (byte) 0xff);
    }

    public final void render(DirectX7 g3d, int x1, int y1, int x2, int y2) {
        if (f <= 2400) setHp(10000);
        
        f++;
        character.setCollision(false);

        pol4.a.transform(g3d.getInvCamera());
        pol4.b.transform(g3d.getInvCamera());
        pol4.c.transform(g3d.getInvCamera());
        pol4.d.transform(g3d.getInvCamera());
        pol4.a.project(g3d);
        pol4.b.project(g3d);
        pol4.c.project(g3d);
        pol4.d.project(g3d);

        if (pol4.isVisible(x1, y1, x2, y2)) {

            if (f < 250) {
                pol4.a.sub(pos.x, pos.y, pos.z);
                pol4.b.sub(pos.x, pos.y, pos.z);
                pol4.c.sub(pos.x, pos.y, pos.z);
                pol4.d.sub(pos.x, pos.y, pos.z);

                pol4.a.mul(vec.x, vec.y, vec.z);
                pol4.b.mul(vec.x, vec.y, vec.z);
                pol4.c.mul(vec.x, vec.y, vec.z);
                pol4.d.mul(vec.x, vec.y, vec.z);

                pol4.a.div(1000, 1000, 1000);
                pol4.b.div(1000, 1000, 1000);
                pol4.c.div(1000, 1000, 1000);
                pol4.d.div(1000, 1000, 1000);

                pol4.a.add(pos.x, pos.y, pos.z);
                pol4.b.add(pos.x, pos.y, pos.z);
                pol4.c.add(pos.x, pos.y, pos.z);
                pol4.d.add(pos.x, pos.y, pos.z);
            }

            if (f > 2400) setHp(0);
            

            if (pol4.sz > -45000) {
                g3d.addRenderObjectDT((RenderObject) pol4, tex);
                pol4.sz += 3002;
                if (character.oldFloorPoly != null) pol4.sz = character.oldFloorPoly.sz + 3000;
            }
        }

    }

    public void activate(House house, Player player, GameScreen gs) {
    }

}
