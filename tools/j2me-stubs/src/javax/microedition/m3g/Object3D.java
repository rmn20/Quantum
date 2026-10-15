package javax.microedition.m3g;
public abstract class Object3D {
    public void setUserObject(Object o) {}
    public Object getUserObject() { return null; }
    public int getUserID() { return 0; }
    public void setUserID(int id) {}
    public int getReferences(Object3D[] refs) { return 0; }
    public Object3D duplicate() { return null; }
    public void addAnimationTrack(AnimationTrack a) {}
    public AnimationTrack getAnimationTrack(int i) { return null; }
    public int getAnimationTrackCount() { return 0; }
    public void removeAnimationTrack(AnimationTrack a) {}
    public Object3D find(int userID) { return null; }
}
