package javax.microedition.m3g;
public class AnimationTrack extends Object3D {
    public static final int ALPHA = 256, AMBIENT_COLOR = 257, COLOR = 258, CROP = 259,
        DENSITY = 260, DIFFUSE_COLOR = 261, EMISSIVE_COLOR = 262, FAR_DISTANCE = 263,
        FIELD_OF_VIEW = 264, INTENSITY = 265, MORPH_WEIGHTS = 266, NEAR_DISTANCE = 267,
        ORIENTATION = 268, PICKABILITY = 269, SCALE = 270, SHININESS = 271, SPECULAR_COLOR = 272,
        SPOT_ANGLE = 273, SPOT_EXPONENT = 274, TRANSLATION = 275, VISIBILITY = 276;
    public AnimationTrack(KeyframeSequence s, int property) {}
    public KeyframeSequence getKeyframeSequence() { return null; }
    public int getTargetProperty() { return 0; }
    public void setController(AnimationController c) {}
    public AnimationController getController() { return null; }
}
