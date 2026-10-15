package harness;

import code.Rendering.DirectX7;
import code.Rendering.RawImage;
import code.Rendering.RenderObject;
import code.Rendering.RenderObjectBuffer;
import code.Rendering.Texture;
import code.Rendering.TexturingAffine;
import code.Rendering.TexturingPers;
import code.Rendering.Vertex;
import code.Rendering.Meshes.Mesh;
import code.Rendering.Meshes.Polygon3V;
import code.Rendering.Meshes.Polygon4V;
import code.Rendering.Meshes.Sprite;
import code.Rendering.MultyTexture;
import code.Math.Matrix;
import code.Collision.HeightComputer;
import code.Collision.Ray;
import code.Collision.RayCast;
import code.utils.Main;
import java.util.Random;

public class Bench {

    // ---- precomputed, deterministic triangle soup ------------------------
    static final int TRI = 2000;
    static final int[] ax = new int[TRI], ay = new int[TRI], az = new int[TRI];
    static final int[] bx = new int[TRI], by = new int[TRI], bz = new int[TRI];
    static final int[] cx = new int[TRI], cy = new int[TRI], cz = new int[TRI];
    static final int[] dx = new int[TRI], dy = new int[TRI], dz = new int[TRI];
    static final int[] au = new int[TRI], av = new int[TRI];
    static final int[] bu = new int[TRI], bv = new int[TRI];
    static final int[] cu = new int[TRI], cv = new int[TRI];
    static final int[] du = new int[TRI], dv = new int[TRI];
    static final int[] mode = new int[TRI];

    static {
        Random r = new Random(20240607L);
        for (int i = 0; i < TRI; i++) {
            // triangles that reliably land on screen and cover a real area
            ax[i] = r.nextInt(240); ay[i] = r.nextInt(120);
            bx[i] = r.nextInt(240); by[i] = ay[i] + 40 + r.nextInt(140);
            cx[i] = r.nextInt(240); cy[i] = by[i] + 40 + r.nextInt(140);
            dx[i] = r.nextInt(240); dy[i] = by[i] + r.nextInt(40);
            az[i] = bz[i] = cz[i] = dz[i] = -(500 + r.nextInt(30000));
            au[i] = r.nextInt(256); av[i] = r.nextInt(256);
            bu[i] = r.nextInt(256); bv[i] = r.nextInt(256);
            cu[i] = r.nextInt(256); cv[i] = r.nextInt(256);
            du[i] = r.nextInt(256); dv[i] = r.nextInt(256);
            mode[i] = r.nextInt(14);
        }
    }

    static Vertex[] A, B, C, D;

    private static Texture makeTexture(int size, int seed, boolean alpha, boolean perspective) {
        Random r = new Random(seed);
        int[] px = new int[size * size];
        for (int i = 0; i < px.length; i++) {
            int c = r.nextInt();
            if ((i & 31) == 0) c = 0;
            if (alpha && (i & 7) == 0) c = 0x80000000 | (c & 0xffffff);
            px[i] = c;
        }
        return new Texture(new RawImage(px, size, size), perspective, null);
    }

    private static Texture makeMipTexture(int size, int seed, boolean perspective) {
        Texture t = makeTexture(size, seed, false, perspective);
        t.mip = new RawImage[3];
        t.mip[0] = t.rImg;
        t.mip[1] = new RawImage(makeTexture(size / 2, seed + 1, false, perspective).rImg.img, size / 2, size / 2);
        t.mip[2] = new RawImage(makeTexture(size / 4, seed + 2, false, perspective).rImg.img, size / 4, size / 4);
        return t;
    }

    static long time(String name, int reps, Runnable r) {
        r.run();
        r.run();
        long t0 = System.currentTimeMillis();
        for (int i = 0; i < reps; i++) r.run();
        long dt = System.currentTimeMillis() - t0;
        System.out.println("BENCH " + name + " " + dt);
        return dt;
    }

    public static void main(String[] args) {
        final DirectX7 g3d = new DirectX7(240, 320);
        final Texture flat = makeTexture(256, 999, false, false);
        final Texture mip = makeMipTexture(256, 4242, true);
        A = new Vertex[TRI]; B = new Vertex[TRI]; C = new Vertex[TRI]; D = new Vertex[TRI];
        for (int i = 0; i < TRI; i++) {
            A[i] = new Vertex(ax[i], ay[i], az[i]); A[i].sx = ax[i]; A[i].sy = ay[i]; A[i].rz = az[i];
            B[i] = new Vertex(bx[i], by[i], bz[i]); B[i].sx = bx[i]; B[i].sy = by[i]; B[i].rz = bz[i];
            C[i] = new Vertex(cx[i], cy[i], cz[i]); C[i].sx = cx[i]; C[i].sy = cy[i]; C[i].rz = cz[i];
            D[i] = new Vertex(dx[i], dy[i], dz[i]); D[i].sx = dx[i]; D[i].sy = dy[i]; D[i].rz = dz[i];
        }
        Main.mipMapping = true;
        Main.persQ = 2;
        Main.fogQ = 2;
        final int reps = Integer.parseInt(args.length > 0 ? args[0] : "10");

        time("affine-paint", reps, new Runnable() {
            public void run() {
                for (int i = 0; i < TRI; i++) {
                    TexturingAffine.paint(g3d, flat,
                            A[i], au[i], av[i], B[i], bu[i], bv[i], C[i], cu[i], cv[i],
                            DirectX7.fogc, mode[i], 128,
                            255, 255, 255, 255, 255, 255, 255, 255, 255,
                            1000, 6000, 1000);
                }
            }
        });

        time("pers-paint", reps, new Runnable() {
            public void run() {
                for (int i = 0; i < TRI; i++) {
                    TexturingPers.paint(g3d, mip,
                            A[i], au[i], av[i], B[i], bu[i], bv[i], C[i], cu[i], cv[i],
                            DirectX7.fogc, mode[i], 128, 24,
                            255, 255, 255, 255, 255, 255, 255, 255, 255,
                            1000, 6000, 1000);
                }
            }
        });

        time("poly4-render-mip", reps, new Runnable() {
            public void run() {
                for (int i = 0; i < TRI; i++) {
                    Polygon4V p = new Polygon4V(A[i], B[i], C[i], D[i],
                            (byte) au[i], (byte) av[i], (byte) bu[i], (byte) bv[i],
                            (byte) cu[i], (byte) cv[i], (byte) du[i], (byte) dv[i]);
                    p.isVisible(0, 0, 240, 320);
                    p.render(g3d, mip);
                }
            }
        });

        time("poly4-render-fast", reps, new Runnable() {
            public void run() {
                for (int i = 0; i < TRI; i++) {
                    Polygon4V p = new Polygon4V(A[i], B[i], C[i], D[i],
                            (byte) au[i], (byte) av[i], (byte) bu[i], (byte) bv[i],
                            (byte) cu[i], (byte) cv[i], (byte) du[i], (byte) dv[i]);
                    p.isVisible(0, 0, 240, 320);
                    p.renderFast(g3d, flat);
                }
            }
        });

        time("poly3-render-mip", reps, new Runnable() {
            public void run() {
                for (int i = 0; i < TRI; i++) {
                    Polygon3V p = new Polygon3V(A[i], B[i], C[i],
                            (byte) au[i], (byte) av[i], (byte) bu[i], (byte) bv[i],
                            (byte) cu[i], (byte) cv[i]);
                    p.isVisible(0, 0, 240, 320);
                    p.render(g3d, mip);
                }
            }
        });

        time("sprite-render", reps, new Runnable() {
            public void run() {
                for (int i = 0; i < TRI; i++) {
                    Sprite s = new Sprite(mip, 2);
                    s.pos.set(ax[i], ay[i], az[i]);
                    s.pos.sx = ax[i]; s.pos.sy = ay[i]; s.pos.rz = az[i];
                    s.size.set(ax[i] + 64, ay[i] + 64, az[i]);
                    s.size.sx = ax[i] + 64; s.size.sy = ay[i] + 64; s.size.rz = az[i];
                    s.isVisible(0, 0, 240, 320);
                    s.render(g3d, mip);
                }
            }
        });

        final RenderObjectBuffer buf = new RenderObjectBuffer();
        final Polygon3V[] objs = new Polygon3V[20000];
        Random rr = new Random(7L);
        for (int i = 0; i < objs.length; i++) {
            Vertex a = new Vertex(0, 0, 0), b = new Vertex(1, 0, 0), c = new Vertex(0, 1, 0);
            a.sx = 10; a.sy = 10; a.rz = -1000;
            b.sx = 20; b.sy = 10; b.rz = -1000;
            c.sx = 10; c.sy = 20; c.rz = -1000;
            objs[i] = new Polygon3V(a, b, c, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
            objs[i].sz = rr.nextInt(100000);
        }
        // ---- sort isolated from the buffer filling ----------------------
        //All sz equal: the degenerate key distribution the buffer test above
        //actually produces (isVisible() recomputes sz from the vertex rz).
        final RenderObjectBuffer sbufEq = new RenderObjectBuffer();
        sbufEq.addRenderObjects(objs, 0, 0, 10, 10);
        time("sort-only-20k-equal-keys", 20, new Runnable() {
            public void run() { sbufEq.sort(0, sbufEq.getSize() - 1); }
        });

        //Distinct keys, added in shuffled order, so the first sort sees a
        //genuinely unsorted array.
        final Polygon3V[] objs2 = new Polygon3V[20000];
        int[] order = new int[objs2.length];
        for (int i = 0; i < order.length; i++) order[i] = i;
        for (int i = order.length - 1; i > 0; i--) {
            int j = rr.nextInt(i + 1);
            int t = order[i]; order[i] = order[j]; order[j] = t;
        }
        for (int i = 0; i < objs2.length; i++) {
            Vertex a = new Vertex(0, 0, 0), b = new Vertex(1, 0, 0), c = new Vertex(0, 1, 0);
            int d = -(1000 + order[i]);
            a.sx = 10; a.sy = 10; a.rz = d;
            b.sx = 20; b.sy = 10; b.rz = d;
            c.sx = 10; c.sy = 20; c.rz = d;
            objs2[i] = new Polygon3V(a, b, c, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
        }
        final RenderObjectBuffer sbufRnd = new RenderObjectBuffer();
        sbufRnd.addRenderObjects(objs2, 0, 0, 10, 10);
        time("sort-only-20k-distinct-keys", 20, new Runnable() {
            public void run() { sbufRnd.sort(0, sbufRnd.getSize() - 1); }
        });

        time("buffer-add-sort-20k", reps, new Runnable() {
            public void run() {
                buf.reset();
                buf.addRenderObjects(objs, 0, 0, 10, 10);
                buf.sort(0, buf.getSize() - 1);
            }
        });

        final Vertex[] verts = new Vertex[8000];
        Random r2 = new Random(9L);
        for (int i = 0; i < verts.length; i++) {
            verts[i] = new Vertex(r2.nextInt(4000) - 2000, r2.nextInt(4000) - 2000, r2.nextInt(4000) - 2000);
        }
        time("transform-project-8k", reps, new Runnable() {
            public void run() {
                Matrix m = new Matrix();
                m.m00 = 4000; m.m11 = 4000; m.m22 = 4000; m.m33 = 4096;
                DirectX7.transform(verts, m);
                g3d.transformAndProjectVertices(verts, m);
            }
        });

        // a fresh buffer has to grow from zero: this is the reallocation cost
        time("buffer-grow-20k-fresh", reps, new Runnable() {
            public void run() {
                RenderObjectBuffer fresh = new RenderObjectBuffer();
                fresh.addRenderObjects(objs, 0, 0, 10, 10);
                fresh.sort(0, fresh.getSize() - 1);
            }
        });

        // same, but big enough that the array copying dominates the timing
        final Polygon3V[] big = new Polygon3V[100000];
        for (int i = 0; i < big.length; i++) big[i] = objs[i % objs.length];
        time("buffer-grow-100k-fresh", 3, new Runnable() {
            public void run() {
                RenderObjectBuffer fresh = new RenderObjectBuffer();
                fresh.addRenderObjects(big, 0, 0, 10, 10);
                fresh.sort(0, fresh.getSize() - 1);
            }
        });

        // ---- collision / culling paths ---------------------------------
        final int GN = 8, GSTEP = 400;
        final Vertex[] gverts = new Vertex[(GN + 1) * (GN + 1)];
        int gvi = 0;
        for (int z = 0; z <= GN; z++) {
            for (int x = 0; x <= GN; x++) gverts[gvi++] = new Vertex(x * GSTEP, (x * z) % 300, z * GSTEP);
        }
        final RenderObject[] gpolys = new RenderObject[GN * GN];
        int gpi = 0;
        for (int z = 0; z < GN; z++) {
            for (int x = 0; x < GN; x++) {
                gpolys[gpi++] = new Polygon4V(
                        gverts[z * (GN + 1) + x], gverts[z * (GN + 1) + x + 1],
                        gverts[(z + 1) * (GN + 1) + x + 1], gverts[(z + 1) * (GN + 1) + x],
                        (byte) 0, (byte) 0, (byte) 255, (byte) 0,
                        (byte) 255, (byte) 255, (byte) 0, (byte) 255);
            }
        }
        //MultyTexture() is package-private, so use the public (int) ctor
        final MultyTexture gmt = new MultyTexture(1);
        int[] px = new int[64];
        gmt.textures[0] = new Texture(new RawImage(px, 8, 8), false, null);
        gmt.textures[0].collision = true;   //Ray.onlyCollidable defaults to true
        gmt.textures[0].castShadow = true;  //and ignoreNonShadowed would skip otherwise
        final Mesh gmesh = new Mesh(gverts, gpolys, gmt);

        final Ray[] rays = new Ray[400];
        Random r3 = new Random(555L);
        for (int i = 0; i < rays.length; i++) {
            rays[i] = new Ray();
            rays[i].getStart().set(r3.nextInt(3200), 900, r3.nextInt(3200));
            rays[i].getDir().set(r3.nextInt(200) - 100, -1500, r3.nextInt(200) - 100);
        }
        time("raycast-400", reps, new Runnable() {
            public void run() {
                for (int i = 0; i < rays.length; i++) {
                    rays[i].reset();
                    RayCast.rayCast(gmesh, rays[i]);
                }
            }
        });

        final int[] hpos = new int[400 * 3];
        for (int i = 0; i < hpos.length; i++) hpos[i] = r3.nextInt(3200);
        time("compute-height-400", reps, new Runnable() {
            public void run() {
                for (int i = 0; i < 400; i++) {
                    HeightComputer.computeHeight(gmesh, hpos[i * 3], 900, hpos[i * 3 + 1]);
                }
            }
        });

        final RenderObjectBuffer addBuf = new RenderObjectBuffer();
        time("add-render-objects", reps, new Runnable() {
            public void run() {
                addBuf.reset();
                for (int i = 0; i < 200; i++) addBuf.addRenderObjects(gpolys, gmt, 0, 0, 240, 320);
            }
        });

        time("clear-display", 500, new Runnable() {
            public void run() { g3d.clearDisplay(0); }
        });
    }
}
