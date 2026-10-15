package javax.microedition.media.protocol;
import java.io.IOException;
import javax.microedition.media.Controllable;
public abstract class DataSource implements Controllable {
    public DataSource(String locator) {}
    public String getLocator() { return null; }
    public abstract String getContentType();
    public abstract void connect() throws IOException;
    public abstract void disconnect();
    public abstract void start() throws IOException;
    public abstract void stop() throws IOException;
    public abstract SourceStream[] getStreams();
}
