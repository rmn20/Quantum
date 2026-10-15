package javax.microedition.m3g;
public class Group extends Node {
    public Group() {}
    public void addChild(Node n) {}
    public void removeChild(Node n) {}
    public Node getChild(int i) { return null; }
    public int getChildCount() { return 0; }
    public boolean pick(int scope, float x, float y, Camera cam, RayIntersection ri) { return false; }
    public boolean pick(int scope, float[] origin, float[] dir, RayIntersection ri) { return false; }
}
