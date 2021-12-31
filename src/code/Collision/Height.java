package code.Collision;

import code.Math.Matrix;
import code.Rendering.RenderObject;
import code.Math.Vector3D;


/**
 * класс для хранения дынных о высоте меша. высотой считается Y координата меша
 * @author DDDENISSS
 */
public class Height {
    
    private int height = Integer.MIN_VALUE;
    private final Vector3D position = new Vector3D();
    private RenderObject polygon = null;
    private int cx, cz, cy;
    private Matrix oldMatrix;
    private boolean updatePos;
    private boolean underRoof;

    public Height() {}
    
    public void reset() {
        polygon = null;
        cx = cz = height = Integer.MIN_VALUE;
        oldMatrix = null;
        updatePos = false;
        underRoof = false;
    }
    
    void set(int height, RenderObject polygon, int cx, int cz, int cy, Matrix oldMatrix, boolean updatePos) {
        this.height = height;
        this.polygon = polygon;
        this.cx = cx;
        this.cz = cz;
        this.cy = cy;
        this.oldMatrix=oldMatrix;
        this.updatePos=updatePos;
    }
    
    public void setPolygon(RenderObject polygon) {
        this.polygon = polygon;
    }
    public RenderObject getPolygon() {
        return polygon;
    }

    /**
     * x центр полигона над которым стоим
     * @return x центр
     */
    public int getCentreX() {
        return cx;
    }
    
    /**
     * я центр полигона над которым стоим
     * @return я центр
     */
    public int getCentreZ() {
        return cz;
    }
    
    /**
     * я центр полигона над которым стоим
     * @return я центр
     */
    public int getCentreY() {
        return cy;
    }

    /**
     * задать текущую высоту
     * новая высота будет вычисляться только, если она выше текущей
     * @param height текущая высота
     */
    public void setHeight(int height) {
        this.height = height;
    }
    public int getHeight() {
        return height;
    }
    
    public void setMatrix(Matrix matrix) {
        this.oldMatrix = matrix;
    }
    public Matrix getMatrix() {
        return oldMatrix;
    }
    
    public void setUnderRoof(boolean x) {
        underRoof = x;
    }
    
    public boolean updatePosition() {
        return updatePos;
    }
    
    public Vector3D getPosition() {
        return position;
    }
    
    public boolean isUnderRoof() {
        return underRoof;
    }
    
    
}
