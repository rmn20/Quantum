package javax.microedition.lcdui;
public class List extends Screen {
    public static final int IMPLICIT = 4, EXCLUSIVE = 1, MULTIPLE = 2;
    public List(String title, int listType) {}
    public int append(String s, Image i) { return 0; }
    public void insert(int i, String s, Image im) {}
    public void delete(int i) {}
    public void deleteAll() {}
    public void set(int i, String s, Image im) {}
    public String getString(int i) { return null; }
    public int size() { return 0; }
    public int getSelectedIndex() { return 0; }
    public void setSelectedIndex(int i, boolean sel) {}
    public boolean isSelected(int i) { return false; }
    public void setFitPolicy(int p) {}
    public void setSelectedFlags(boolean[] a) {}
}
