package javax.microedition.media;
import java.io.IOException;
import java.io.InputStream;
public final class Manager {
    public static final String TONE_DEVICE_LOCATOR = "tone_device";
    public static Player createPlayer(String locator) throws IOException, MediaException { return null; }
    public static Player createPlayer(InputStream source, String contentType) throws IOException, MediaException { return null; }
    public static Player createPlayer(javax.microedition.media.protocol.DataSource source) throws IOException, MediaException { return null; }
    public static String[] getSupportedContentTypes(String protocol) { return new String[0]; }
    public static String[] getSupportedProtocols(String contentType) { return new String[0]; }
    public static void playTone(int note, int duration, int volume) throws MediaException {}
}
