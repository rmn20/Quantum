package javax.microedition.io.file;
public interface FileSystemListener {
    void rootChanged(int state, String rootName);
}
