package javax.microedition.media.control;
import javax.microedition.media.Control;
public interface ToneControl extends Control {
    byte VERSION = 1, TEMPO = 2, REPEAT = 3, SET_VOLUME = 4, SILENCE = 5;
    void setSequence(byte[] sequence, int offset, int length);
}
