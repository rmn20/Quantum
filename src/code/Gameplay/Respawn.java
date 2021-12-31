package code.Gameplay;

import code.Gameplay.Map.House;
import code.Math.Vector3D;

public class Respawn {

    public final Vector3D point;
    public final int part;
    public byte mode = -128;
    public byte cmode = -128;
    public boolean respa = false;

    public Respawn(Vector3D point, House house) {
        this.point = point;
        part = house.calcPart(-1, point.x, point.y, point.z);
        if(part == -1) {
            System.out.println("ERROR: неправильная точка старта " + point.x + " " + point.y + " " + point.z);
        }

    }

}
