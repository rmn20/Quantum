package javax.microedition.media.control;
import javax.microedition.media.Control;
public interface VolumeControl extends Control {
    void setLevel(int level);
    int getLevel();
    void setMute(boolean mute);
    boolean isMute();
}
