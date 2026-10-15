package harness;

import code.Rendering.DirectX7;
import code.Rendering.RawImage;
import code.Rendering.RenderObject;
import code.Rendering.RenderObjectBuffer;
import code.Rendering.TMPElement;
import code.Rendering.Texture;
import code.Rendering.TexturingAffine;
import code.Rendering.TexturingPers;
import code.Rendering.Vertex;
import code.Rendering.Meshes.Polygon3V;
import code.Rendering.Meshes.Polygon4V;
import code.Rendering.Meshes.Sprite;
import code.Math.Matrix;
import code.Math.MathUtils;
import code.utils.Main;
import java.util.Random;

/**
 * Differential harness: drives the engine's rasterizer over deterministic
 * pseudo-random input and prints a hash of every stage so two builds of the
 * engine can be compared for pixel-exact output.
 */
public class Harness {

    private static long h = 0xcbf29ce484222325L;

    private static void mix(int v) {
        h ^= (v & 0xffffffffL);
        h *= 0x100000001b3L;
    }

    private static void section(String name) {
        System.out.println("HASH " + name + " " + Long.toHexString(h));
        h = 0xcbf29ce484222325L;
    }

    private static void hashBuffer(int[] buf) {
        for (int i = 0; i < buf.length; i++) mix(buf[i]);
    }

    private static Texture makeTexture(int size, int seed, boolean alpha, boolean perspective) {
        Random r = new Random(seed);
        int[] px = new int[size * size];
        for (int i = 0; i < px.length; i++) {
            int c = r.nextInt();
            if ((i & 31) == 0) c = 0;            // transparent texels
            if (alpha && (i & 7) == 0) c = 0x80000000 | (c & 0xffffff);
            px[i] = c;
        }
        RawImage ri = new RawImage(px, size, size);
        Texture t = new Texture(ri, perspective, null);
        return t;
    }

    private static Texture makeMipTexture(int size, int seed, boolean alpha, boolean perspective) {
        Texture t = makeTexture(size, seed, alpha, perspective);
        t.mip = new RawImage[3];
        t.mip[0] = t.rImg;
        t.mip[1] = new RawImage(makeTexture(size / 2, seed + 1, alpha, perspective).rImg.img, size / 2, size / 2);
        t.mip[2] = new RawImage(makeTexture(size / 4, seed + 2, alpha, perspective).rImg.img, size / 4, size / 4);
        return t;
    }

    private static Vertex[] tri(Random r) {
        Vertex[] v = new Vertex[3];
        for (int i = 0; i < 3; i++) {
            v[i] = new Vertex();
            v[i].sx = r.nextInt(400) - 40;
            v[i].sy = r.nextInt(300) - 30;
            v[i].rz = -r.nextInt(20000);
        }
        return v;
    }

    private static void affinePass(DirectX7 g3d, Texture tex, Random r, int count) {
        int[] uv = new int[6];
        for (int i = 0; i < count; i++) {
            Vertex[] v = tri(r);
            for (int k = 0; k < 6; k++) uv[k] = r.nextInt(300) - 20;
            TexturingAffine.paint(g3d, tex,
                    v[0], uv[0], uv[1], v[1], uv[2], uv[3], v[2], uv[4], uv[5],
                    DirectX7.fogc, r.nextInt(14), r.nextInt(256),
                    r.nextInt(256), r.nextInt(256), r.nextInt(256),
                    r.nextInt(256), r.nextInt(256), r.nextInt(256),
                    r.nextInt(256), r.nextInt(256), r.nextInt(256),
                    r.nextInt(8000) - 4000, r.nextInt(16000) - 8000, r.nextInt(8000) - 4000);
        }
    }

    private static void persPass(DirectX7 g3d, Texture tex, Random r, int count) {
        int[] uv = new int[6];
        for (int i = 0; i < count; i++) {
            Vertex[] v = tri(r);
            for (int k = 0; k < 6; k++) uv[k] = r.nextInt(300) - 20;
            TexturingPers.paint(g3d, tex,
                    v[0], uv[0], uv[1], v[1], uv[2], uv[3], v[2], uv[4], uv[5],
                    DirectX7.fogc, r.nextInt(14), r.nextInt(256), r.nextInt(40) + 4,
                    r.nextInt(256), r.nextInt(256), r.nextInt(256),
                    r.nextInt(256), r.nextInt(256), r.nextInt(256),
                    r.nextInt(256), r.nextInt(256), r.nextInt(256),
                    r.nextInt(8000) - 4000, r.nextInt(16000) - 8000, r.nextInt(8000) - 4000);
        }
    }

    private static void polyPass(DirectX7 g3d, Texture tex, Random r, int count) {
        for (int i = 0; i < count; i++) {
            Vertex a = new Vertex(r.nextInt(400) - 40, r.nextInt(300) - 30, -r.nextInt(20000));
            Vertex b = new Vertex(r.nextInt(400) - 40, r.nextInt(300) - 30, -r.nextInt(20000));
            Vertex c = new Vertex(r.nextInt(400) - 40, r.nextInt(300) - 30, -r.nextInt(20000));
            Vertex d = new Vertex(r.nextInt(400) - 40, r.nextInt(300) - 30, -r.nextInt(20000));
            a.sx = a.x; a.sy = a.y; a.rz = a.z;
            b.sx = b.x; b.sy = b.y; b.rz = b.z;
            c.sx = c.x; c.sy = c.y; c.rz = c.z;
            d.sx = d.x; d.sy = d.y; d.rz = d.z;

            Polygon4V p = new Polygon4V(a, b, c, d,
                    (byte) r.nextInt(256), (byte) r.nextInt(256),
                    (byte) r.nextInt(256), (byte) r.nextInt(256),
                    (byte) r.nextInt(256), (byte) r.nextInt(256),
                    (byte) r.nextInt(256), (byte) r.nextInt(256));
            p.render(g3d, tex);
            p.renderFast(g3d, tex);

            Polygon3V t = new Polygon3V(a, b, c,
                    (byte) r.nextInt(256), (byte) r.nextInt(256),
                    (byte) r.nextInt(256), (byte) r.nextInt(256),
                    (byte) r.nextInt(256), (byte) r.nextInt(256));
            t.render(g3d, tex);
            t.renderFast(g3d, tex);
        }
    }

    /** renders with the texture drawmode cycling, so the shared fog state of
     *  Polygon4V/Polygon3V is carried over between different drawmodes */
    private static void polyPassModes(DirectX7 g3d, Texture tex, Random r, int count, int mode) {
        for (int i = 0; i < count; i++) {
            Vertex a = new Vertex(r.nextInt(400) - 40, r.nextInt(300) - 30, -r.nextInt(20000));
            Vertex b = new Vertex(r.nextInt(400) - 40, r.nextInt(300) - 30, -r.nextInt(20000));
            Vertex c = new Vertex(r.nextInt(400) - 40, r.nextInt(300) - 30, -r.nextInt(20000));
            Vertex d = new Vertex(r.nextInt(400) - 40, r.nextInt(300) - 30, -r.nextInt(20000));
            a.sx = a.x; a.sy = a.y; a.rz = a.z;
            b.sx = b.x; b.sy = b.y; b.rz = b.z;
            c.sx = c.x; c.sy = c.y; c.rz = c.z;
            d.sx = d.x; d.sy = d.y; d.rz = d.z;
            tex.drawmode = (byte) (mode < 0 ? (i % 14) : mode);

            Polygon4V p = new Polygon4V(a, b, c, d,
                    (byte) r.nextInt(256), (byte) r.nextInt(256),
                    (byte) r.nextInt(256), (byte) r.nextInt(256),
                    (byte) r.nextInt(256), (byte) r.nextInt(256),
                    (byte) r.nextInt(256), (byte) r.nextInt(256));
            p.isVisible(0, 0, 240, 320);
            p.render(g3d, tex);
            p.renderFast(g3d, tex);

            Polygon3V t = new Polygon3V(a, b, c,
                    (byte) r.nextInt(256), (byte) r.nextInt(256),
                    (byte) r.nextInt(256), (byte) r.nextInt(256),
                    (byte) r.nextInt(256), (byte) r.nextInt(256));
            t.isVisible(0, 0, 240, 320);
            t.render(g3d, tex);
            t.renderFast(g3d, tex);
        }
    }

    private static void spritePass(DirectX7 g3d, Texture tex, Random r, int count) {
        for (int i = 0; i < count; i++) {
            Sprite s = new Sprite(tex, r.nextInt(4) + 1);
            int px = r.nextInt(400) - 40, py = r.nextInt(300) - 30, pz = -r.nextInt(20000);
            s.pos.x = px; s.pos.y = py; s.pos.z = pz;
            s.pos.sx = px; s.pos.sy = py; s.pos.rz = pz;
            s.size.x = px + 64; s.size.y = py + 64; s.size.z = pz;
            s.size.sx = px + 64; s.size.sy = py + 64; s.size.rz = pz;
            s.setScale(r.nextInt(4) + 1);
            s.mode = (byte) (r.nextInt(4));
            s.fog = r.nextBoolean();
            s.render(g3d, tex);
            s.renderFast(g3d, tex);
        }
    }

    /** a polygon whose projected vertices actually survive isVisible() */
    private static Polygon3V visibleTri(int i) {
        Vertex a = new Vertex(0, 0, 0);
        Vertex b = new Vertex(1, 0, 0);
        Vertex c = new Vertex(0, 1, 0);
        a.sx = 10; a.sy = 10; a.rz = -1000 - i;
        b.sx = 20; b.sy = 10; b.rz = -1000 - i;
        c.sx = 10; c.sy = 20; c.rz = -1000 - i;
        return new Polygon3V(a, b, c, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
    }

    private static void bufferPass(Random r) {
        RenderObjectBuffer buf = new RenderObjectBuffer();
        int n = 3000;
        RenderObject[] objs = new RenderObject[n];
        for (int i = 0; i < n; i++) objs[i] = visibleTri(i);
        buf.addRenderObjects(objs, 0, 0, 100, 100);
        System.out.println("INFO buffer-added=" + buf.getSize() + " of " + n);
        // isVisible() overwrites sz, so the sort keys are assigned afterwards
        for (int i = 0; i < buf.getSize(); i++) {
            objs[i].sz = r.nextInt(4000) - 2000;
            if ((i & 3) == 0) objs[i].sz = 1234;      // lots of equal keys
        }
        buf.sort(0, buf.getSize() - 1);
        TMPElement[] b2 = buf.getBuffer();
        for (int i = 0; i < buf.getSize(); i++) mix(b2[i].obj.sz);
        mix(buf.getSize());
        // sorted check
        boolean sorted = true;
        for (int i = 1; i < buf.getSize(); i++) {
            if (b2[i].obj.sz > b2[i - 1].obj.sz) { sorted = false; break; }
        }
        System.out.println("INFO sorted=" + sorted);
        section("buffer-sort");
        buf.reset();

        // worst case orders: the recursive version needed one stack frame each
        for (int variant = 0; variant < 3; variant++) {
            RenderObjectBuffer b = new RenderObjectBuffer();
            RenderObject[] o = new RenderObject[4000];
            for (int i = 0; i < o.length; i++) o[i] = visibleTri(i);
            b.addRenderObjects(o, 0, 0, 100, 100);
            for (int i = 0; i < b.getSize(); i++) {
                int v = i;
                if (variant == 1) v = o.length - i;          // reverse sorted
                if (variant == 2) v = 7;                     // all equal
                o[i].sz = v;
            }
            b.sort(0, b.getSize() - 1);
            TMPElement[] e = b.getBuffer();
            for (int i = 0; i < b.getSize(); i++) mix(e[i].obj.sz);
            mix(b.getSize());
            section("buffer-sort-variant" + variant);
            b.reset();
        }
    }

    private static void transformPass(DirectX7 g3d, Random r) {
        Matrix m = new Matrix();
        m.m00 = 1000; m.m11 = 1000; m.m22 = 1000; m.m33 = 4096;
        m.m01 = 300; m.m10 = -300; m.m12 = 200; m.m21 = -200;
        m.m03 = 12345; m.m13 = -6789; m.m23 = 4321;
        Vertex[] verts = new Vertex[200];
        for (int i = 0; i < verts.length; i++) {
            verts[i] = new Vertex(r.nextInt(4000) - 2000, r.nextInt(4000) - 2000, r.nextInt(4000) - 2000);
        }
        DirectX7.transform(verts, m);
        for (int i = 0; i < verts.length; i++) { mix(verts[i].sx); mix(verts[i].sy); mix(verts[i].rz); }
        g3d.transformAndProjectVertices(verts, m);
        for (int i = 0; i < verts.length; i++) { mix(verts[i].sx); mix(verts[i].sy); mix(verts[i].rz); }
        DirectX7.transformSave(verts, m);
        for (int i = 0; i < verts.length; i++) { mix(verts[i].x); mix(verts[i].y); mix(verts[i].z); }
        DirectX7.transformReturn(verts);
        for (int i = 0; i < verts.length; i++) { mix(verts[i].x); mix(verts[i].y); mix(verts[i].z); }
        Matrix inv = new Matrix();
        inv.set(m);
        inv.invert();
        mix(inv.m00); mix(inv.m11); mix(inv.m22); mix(inv.m33);
        Matrix prod = new Matrix();
        prod.mul(m, inv);
        mix(prod.m00); mix(prod.m11); mix(prod.m22); mix(prod.m33);
        for (int i = 0; i < 500; i++) {
            mix(MathUtils.calcLight(r.nextInt(16000) - 8000, r.nextInt(16000) - 8000,
                    r.nextInt(16000) - 8000, r.nextInt(8000) - 4000, r.nextInt(8000) - 4000,
                    r.nextInt(8000) - 4000));
        }
    }

    public static void main(String[] args) {
        DirectX7 g3d = new DirectX7(240, 320);
        g3d.updateFov(74);
        Random r = new Random(12345L);

        // ---- math / transform stage -------------------------------------
        transformPass(g3d, r);
        section("transform");

        // ---- render object buffer sorting -------------------------------
        bufferPass(r);

        // ---- plain (non mip, non perspective) textures ------------------
        Texture flat = makeTexture(256, 999, false, false);
        Main.mipMapping = false;
        Main.persQ = 0;
        for (int fq = 0; fq <= 2; fq++) {
            Main.fogQ = fq;
            g3d.clearDisplay(0x010203);
            affinePass(g3d, flat, r, 400);
            hashBuffer(g3d.display);
            section("affine-flat-fogQ" + fq);

            g3d.clearDisplay(0x010203);
            persPass(g3d, flat, r, 400);
            hashBuffer(g3d.display);
            section("pers-flat-fogQ" + fq);

            g3d.clearDisplay(0x010203);
            polyPass(g3d, flat, r, 300);
            hashBuffer(g3d.display);
            section("poly-flat-fogQ" + fq);

            g3d.clearDisplay(0x010203);
            spritePass(g3d, flat, r, 200);
            hashBuffer(g3d.display);
            section("sprite-flat-fogQ" + fq);
        }

        // ---- mip-mapped, perspective corrected --------------------------
        Texture mip = makeMipTexture(256, 4242, false, true);
        int[] persQs = new int[]{1, 2, 3, 4};
        for (int pq = 0; pq < persQs.length; pq++) {
            Main.persQ = persQs[pq];
            Main.mipMapping = true;
            for (int fq = 0; fq <= 2; fq++) {
                Main.fogQ = fq;
                g3d.clearDisplay(0x040506);
                polyPass(g3d, mip, r, 300);
                hashBuffer(g3d.display);
                section("poly-mip-persQ" + persQs[pq] + "-fogQ" + fq);

                g3d.clearDisplay(0x040506);
                persPass(g3d, mip, r, 400);
                hashBuffer(g3d.display);
                section("pers-mip-persQ" + persQs[pq] + "-fogQ" + fq);

                g3d.clearDisplay(0x040506);
                affinePass(g3d, mip, r, 400);
                hashBuffer(g3d.display);
                section("affine-mip-persQ" + persQs[pq] + "-fogQ" + fq);
            }
        }

        // ---- every drawmode, both fixed and cycling --------------------
        Main.persQ = 2;
        Main.mipMapping = true;
        for (int fq = 0; fq <= 2; fq++) {
            Main.fogQ = fq;
            for (int dm = -1; dm < 14; dm++) {
                g3d.clearDisplay(0x0a0b0c);
                polyPassModes(g3d, mip, r, 120, dm);
                hashBuffer(g3d.display);
                section("poly-drawmode" + dm + "-fogQ" + fq);
            }
        }

        // ---- alpha textures (drawmode 4 alpha mixing) -------------------
        Texture alpha = makeMipTexture(64, 777, true, true);
        alpha.drawmode = 4;
        Main.persQ = 2;
        Main.mipMapping = true;
        Main.fogQ = 2;
        g3d.clearDisplay(0x070809);
        polyPass(g3d, alpha, r, 300);
        affinePass(g3d, alpha, r, 300);
        persPass(g3d, alpha, r, 300);
        hashBuffer(g3d.display);
        section("alpha-mix");

        // ---- drawLine / clearDisplay ------------------------------------
        g3d.clearDisplay(0);
        for (int i = 0; i < 100; i++) {
            g3d.drawLine(r.nextInt(300) - 50, r.nextInt(360) - 40,
                    r.nextInt(300) - 50, r.nextInt(360) - 40, r.nextInt(5) + 1, r.nextInt());
        }
        hashBuffer(g3d.display);
        section("drawline");

        // ---- non power of two texture (modulo fallback path) -----------
        Texture odd = makeTexture(100, 5150, false, true);
        odd.drawmode = 0;
        Main.persQ = 0;
        Main.mipMapping = false;
        Main.fogQ = 2;
        g3d.clearDisplay(0x0d0e0f);
        spritePass(g3d, odd, r, 400);
        polyPass(g3d, odd, r, 200);
        affinePass(g3d, odd, r, 200);
        persPass(g3d, odd, r, 200);
        hashBuffer(g3d.display);
        section("non-pow2-texture");

        Texture odd2 = makeTexture(48, 6161, false, false);
        odd2.drawmode = 1;
        g3d.clearDisplay(0x0d0e0f);
        spritePass(g3d, odd2, r, 400);
        hashBuffer(g3d.display);
        section("non-pow2-texture-48");

        System.out.println("DONE");
    }
}
