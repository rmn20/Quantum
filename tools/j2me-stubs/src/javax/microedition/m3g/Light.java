package javax.microedition.m3g;
public class Light extends Node {
    public static final int AMBIENT = 128, DIRECTIONAL = 129, OMNI = 130, SPOT = 131;
    public Light() {}
    public void setMode(int m) {}
    public int getMode() { return 0; }
    public void setColor(int argb) {}
    public int getColor() { return 0; }
    public void setIntensity(float i) {}
    public void setAttenuation(float constant, float linear, float quadratic) {}
    public void setSpotAngle(float a) {}
    public void setSpotExponent(float e) {}
}
