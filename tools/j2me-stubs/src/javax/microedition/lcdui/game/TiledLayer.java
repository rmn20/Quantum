package javax.microedition.lcdui.game;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
public class TiledLayer extends Layer {
    public TiledLayer(int cols, int rows, Image img, int tw, int th) {}
    public int createAnimatedTile(int tile) { return 0; }
    public void setAnimatedTile(int a, int tile) {}
    public void setCell(int c, int r, int tile) {}
    public int getCell(int c, int r) { return 0; }
    public void fillCells(int c, int r, int nc, int nr, int tile) {}
    public int getCellWidth() { return 0; }
    public int getCellHeight() { return 0; }
    public int getColumns() { return 0; }
    public int getRows() { return 0; }
    public void setStaticTileSet(Image img, int tw, int th) {}
    public void paint(Graphics g) {}
}
