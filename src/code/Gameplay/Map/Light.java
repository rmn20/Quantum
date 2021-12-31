package code.Gameplay.Map;

import code.Math.Vector3D;

/**
 *
 * @author Roman Lahin
 */
public class Light {

    public int[] color;
    public Vector3D pos;
    public Vector3D direction;
    public short ceilingFix, floorFix;
    public int part;
    
    public Light(int[] pos,int[] color) {
        this.pos=new Vector3D(pos[0],pos[1],pos[2]);
        this.color=color;
        floorFix=ceilingFix=0;
    }
}