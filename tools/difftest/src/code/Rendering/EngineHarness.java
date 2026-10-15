package code.Rendering;

import code.Collision.Ray;
import code.Collision.RayCast;
import code.Collision.SphereCast;
import code.Collision.HeightComputer;
import code.Math.Vector3D;
import code.Rendering.Meshes.Mesh;
import code.Rendering.Meshes.Polygon3V;
import code.Rendering.Meshes.Polygon4V;
import java.util.Random;

/** Differential harness for the collision side of the engine. */
public class EngineHarness {

    private static long h = 0xcbf29ce484222325L;

    private static void mix(int v) {
        h ^= (v & 0xffffffffL);
        h *= 0x100000001b3L;
    }

    private static void section(String name) {
        System.out.println("HASH " + name + " " + Long.toHexString(h));
        h = 0xcbf29ce484222325L;
    }

    private static Texture solid(int color, boolean collidable, boolean shadow) {
        int[] px = new int[64];
        for (int i = 0; i < px.length; i++) px[i] = color;
        Texture t = new Texture(new RawImage(px, 8, 8), false, null);
        t.collision = collidable;
        t.castShadow = shadow;
        return t;
    }

    public static void main(String[] args) {
        Random r = new Random(20260101L);

        // ---- a 8x8 quad floor + a few triangles ---------------------------
        final int N = 8;
        final int step = 400;
        Vertex[] verts = new Vertex[(N + 1) * (N + 1) + 9];
        int vi = 0;
        for (int z = 0; z <= N; z++) {
            for (int x = 0; x <= N; x++) {
                verts[vi++] = new Vertex(x * step, (x * z) % 300, z * step);
            }
        }
        for (int i = 0; i < 9; i++) verts[vi++] = new Vertex(r.nextInt(3000), r.nextInt(600), r.nextInt(3000));

        RenderObject[] polys = new RenderObject[N * N + 3];
        int pi = 0;
        for (int z = 0; z < N; z++) {
            for (int x = 0; x < N; x++) {
                Vertex a = verts[z * (N + 1) + x];
                Vertex b = verts[z * (N + 1) + x + 1];
                Vertex c = verts[(z + 1) * (N + 1) + x + 1];
                Vertex d = verts[(z + 1) * (N + 1) + x];
                polys[pi++] = new Polygon4V(a, b, c, d,
                        (byte) 0, (byte) 0, (byte) 255, (byte) 0,
                        (byte) 255, (byte) 255, (byte) 0, (byte) 255);
            }
        }
        polys[pi++] = new Polygon3V(verts[verts.length - 1], verts[verts.length - 2], verts[verts.length - 3],
                (byte) 0, (byte) 0, (byte) 1, (byte) 0, (byte) 1, (byte) 1);
        polys[pi++] = new Polygon3V(verts[verts.length - 4], verts[verts.length - 5], verts[verts.length - 6],
                (byte) 0, (byte) 0, (byte) 1, (byte) 0, (byte) 1, (byte) 1);
        Polygon3V last = new Polygon3V(verts[verts.length - 7], verts[verts.length - 8], verts[verts.length - 9],
                (byte) 0, (byte) 0, (byte) 1, (byte) 0, (byte) 1, (byte) 1);
        last.tex = (byte) 1;   // second material, non collidable
        polys[pi] = last;

        MultyTexture mt = new MultyTexture();
        mt.textures = new Texture[2];
        mt.textures[0] = solid(0x112233, true, true);
        mt.textures[1] = solid(0x445566, false, false);

        Mesh mesh = new Mesh(verts, polys, mt);

        // ---- raycasts -----------------------------------------------------
        for (int pass = 0; pass < 4; pass++) {
            for (int i = 0; i < 2000; i++) {
                Ray ray = new Ray();
                ray.getStart().set(r.nextInt(3200), 600 + r.nextInt(400), r.nextInt(3200));
                ray.getDir().set(r.nextInt(400) - 200, -200 - r.nextInt(400), r.nextInt(400) - 200);
                ray.findNearest = (i & 1) == 0;
                ray.infinity = (i & 4) == 0;
                ray.onlyCollidable = (i & 2) == 0;
                ray.ignoreNonShadowed = (i & 8) == 0;
                ray.origPol = ((i & 16) == 0) ? polys[i % polys.length] : null;

                if (pass == 0) RayCast.rayCast(mesh, ray);
                else if (pass == 1) RayCast.rayCast(mesh, ray, true);
                else if (pass == 2) RayCast.superFastRayCast(mesh, ray);
                else RayCast.rayCast(mesh, ray, 0);

                mix(ray.isCollision() ? 1 : 0);
                mix(ray.getDistance());
                Vector3D cp = ray.getCollisionPoint();
                mix(cp.x); mix(cp.y); mix(cp.z);
                RenderObject t = ray.getTriangle();
                int idx = -1;
                for (int k = 0; k < polys.length; k++) {
                    if (polys[k] == t) { idx = k; break; }
                }
                mix(idx);
            }
            section("raycast-pass" + pass);
        }

        // ---- height computation ------------------------------------------
        for (int i = 0; i < 1000; i++) {
            mix(HeightComputer.computeHeight(mesh, r.nextInt(3200), r.nextInt(900), r.nextInt(3200)));
        }
        section("height");

        System.out.println("DONE");
    }
}
