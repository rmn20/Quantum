package javax.microedition.lcdui;
import java.io.InputStream;
import java.io.IOException;
public class Image {
    public static Image createImage(int w, int h) { return null; }
    public static Image createImage(Image src) { return null; }
    public static Image createImage(String name) throws IOException { return null; }
    public static Image createImage(byte[] d, int off, int len) throws IOException { return null; }
    public static Image createImage(InputStream is) throws IOException { return null; }
    public static Image createImage(Image img, int x, int y, int w, int h, int transform) { return null; }
    public static Image createRGBImage(int[] rgb, int w, int h, boolean pa) { return null; }
    public Graphics getGraphics() { return null; }
    public int getWidth() { return 0; }
    public int getHeight() { return 0; }
    public boolean isMutable() { return false; }
    public void getRGB(int[] rgb, int off, int scanlen, int x, int y, int w, int h) {}
}
