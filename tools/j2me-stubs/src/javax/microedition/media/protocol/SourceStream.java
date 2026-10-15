package javax.microedition.media.protocol;
import java.io.IOException;
import javax.microedition.media.Controllable;
public interface SourceStream extends Controllable {
    int SEEK_SET = 0, SEEK_CUR = 1, SEEK_END = 2, NOT_SEEKABLE = 0, RANDOM_ACCESSIBLE = 1;
    ContentDescriptor getContentDescriptor();
    long getContentLength();
    int getSeekType();
    long seek(long where) throws IOException;
    long tell();
    int read(byte[] b, int off, int len) throws IOException;
}
