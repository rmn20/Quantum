package javax.microedition.io.file;
import java.io.IOException;
import java.util.Enumeration;
import javax.microedition.io.StreamConnection;
public interface FileConnection extends StreamConnection {
    boolean exists();
    boolean isDirectory();
    void create() throws IOException;
    boolean mkdir();
    boolean delete() throws IOException;
    boolean rename(String newName) throws IOException;
    long totalSize() throws IOException;
    long availableSize() throws IOException;
    long usedSize() throws IOException;
    String getURL();
    String getName();
    String getPath();
    String getFileSystemName() throws IOException;
    boolean isHidden() throws IOException;
    void setHidden(boolean h) throws IOException;
    void setReadable(boolean r) throws IOException;
    void setWritable(boolean w) throws IOException;
    boolean canRead();
    boolean canWrite();
    Enumeration list() throws IOException;
    Enumeration list(String filter, boolean includeHidden) throws IOException;
    long lastModified();
}
