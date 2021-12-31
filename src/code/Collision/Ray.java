package code.Collision;

import code.Math.Vector3D;
import code.Rendering.RenderObject;

public final class Ray {

    boolean collision;
    public int distance;
    RenderObject triangle = null;
    public RenderObject origPol = null;
    public boolean doubleSide;
    public boolean findNearest;
    public boolean infinity;
    public boolean ignoreNonShadowed;
    public boolean onlyCollidable = true;

    int numRoom = -1;

    final Vector3D collisionPoint = new Vector3D();
    final Vector3D start = new Vector3D();
    final Vector3D dir = new Vector3D();

    public Ray() {
        reset();
    }

    public final void setTriangle(RenderObject i) {
        triangle = i;
    }

    public final RenderObject getTriangle() {
        return triangle;
    }

    public final void setNumRoom(int i) {
        numRoom = i;
    }

    public final int getNumRoom() {
        return numRoom;
    }

    public final boolean isCollision() {
        return collision;
    }

    public final int getDistance() {
        return distance;
    }

    public final Vector3D getCollisionPoint() {
        return collisionPoint;
    }

    public final void reset() {
        collision = false;
        distance = Integer.MAX_VALUE;
        origPol = null;
    }

    public final Vector3D getStart() {
        return start;
    }

    public final Vector3D getDir() {
        return dir;
    }
}
