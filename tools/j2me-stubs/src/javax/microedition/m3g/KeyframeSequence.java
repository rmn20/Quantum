package javax.microedition.m3g;
public class KeyframeSequence extends Object3D {
    public static final int LINEAR = 176, SLERP = 177, SPLINE = 178, SQUAD = 179, STEP = 180;
    public static final int CONSTANT = 192, LOOP = 193;
    public KeyframeSequence(int numKeyframes, int vectorSize, int interpolation) {}
    public void setRepeatMode(int mode) {}
    public void setDuration(int duration) {}
    public void setKeyframe(int index, int time, float[] value) {}
    public void setValidRange(int first, int last) {}
}
