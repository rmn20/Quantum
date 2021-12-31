package code.HUD;

import code.Rendering.DirectX7;
import code.Rendering.Texture;
import code.Rendering.TexturingPers;
import code.Rendering.Vertex;
import code.utils.Main;
import javax.microedition.lcdui.Graphics;
import code.Rendering.TexturingAffine;
import code.utils.canvas.MyCanvas;

final class Benchmark extends GUIScreen {
    
    private Main main;
    private Menu menu;
    private DirectX7 g3d;
    private Texture tex;
    private Vertex a, b, c;
    private long AffTime = 0;
    private long PersTime = 0;
    private long PersTimeFloor = 0;
    private byte rendermode = 0;

    public Benchmark(Main main, Menu menu) {
        this.main = main;
        this.menu = menu;
        this.setFont(Main.getFont());
        this.setSoftKeysNames("Change Render Mode", "Back");
        g3d = new DirectX7(256, 256);
        tex = Texture.createTexture("/images/icon.png");
        a = new Vertex(0, 0, 0);
        a.sy = 0;
        a.sx = 0;
        a.rz = 0;
        b = new Vertex(0, 0, 0);
        b.sy = 0;
        b.sx = 255;
        b.rz = -100;
        c = new Vertex(0, 0, 0);
        c.sy = 255;
        c.sx = 0;
        c.rz = -25;
        test(true);
    }

    protected final void paint(Graphics g) {
        this.menu.drawBackground(g);
        g.setColor(0xffffff);
        g.translate(this.getWidth() / 2, this.getHeight() / 2);
        g.drawString("Render Mode: " + rendermode, 0, -50, Graphics.BASELINE | Graphics.HCENTER);
        g.drawString("Affine Time: " + Long.toString(AffTime) + " ms", 0, -20, Graphics.BASELINE | Graphics.HCENTER);
        g.drawString("Perspective Time: " + Long.toString(PersTime) + " ms", 0, 20, Graphics.BASELINE | Graphics.HCENTER);
        g.drawString("Perspective Time Floors: " + Long.toString(PersTimeFloor) + " ms", 0, 60, Graphics.BASELINE | Graphics.HCENTER);

        g.translate(-this.getWidth() / 2, -this.getHeight() / 2);
        this.drawSoftKeys(g);
    }

    protected final void onLeftSoftKey() {
        rendermode += 1;
        if(rendermode > 13) {
            rendermode = 0;
        }
        onKey5();
    }

    protected final void onRightSoftKey() {

        g3d.destroy();
        a = null;
        b = null;
        c = null;
        rendermode = rendermode;
        System.gc();

        Main.setCurrent((MyCanvas) menu);
    }

    protected final void onKey2() {
        this.repaint();
    }

    protected final void onKey5() {
        a.sy = 0;
        a.sx = 0;
        a.rz = 0;
        b.sy = 0;
        b.sx = 256;
        b.rz = -100;
        c.sy = 256;
        c.sx = 0;
        c.rz = -25;
//Testing Time
        test(true);

        this.repaint();

    }

    protected final void onKey8() {
        this.repaint();
    }

    private void test(boolean tp) {
        int count = tp?200:2000;
        
        long bTime = System.currentTimeMillis();
        for(int i=0; i<count; i++) {
            TexturingPers.paint(g3d, tex,
                    a, 0, 0,
                    b, 0xff, 0,
                    c, 0, 0xff, 0x000000, rendermode, 0, 24, 128, 192, 128, 128, 128, 192, 128, 128, 128, (short) 128, (short) 128, (short) 128);
        }
        PersTime = System.currentTimeMillis() - bTime;

        bTime = System.currentTimeMillis();
        for(int i=0; i<count; i++) {
            TexturingPers.paint(g3d, tex,
                    a, 0, 0,
                    b, 0xff, 0,
                    c, 0, 0xff, 0x000000, rendermode, 0, 9999, 128, 192, 128, 128, 128, 192, 128, 128, 128, (short) 0, (short) 4096, (short) 0);
        }
        PersTimeFloor = System.currentTimeMillis() - bTime;

        bTime = System.currentTimeMillis();
        for(int i=0; i<count; i++) {
            TexturingAffine.paint(g3d, tex,
                    a, 0, 0,
                    b, 0xff, 0,
                    c, 0, 0xff, 0x000000, rendermode, 0, 128, 192, 128, 128, 128, 192, 128, 128, 128, (short) 128, (short) 128, (short) 128);
        }
        AffTime = System.currentTimeMillis() - bTime;

    }
}
