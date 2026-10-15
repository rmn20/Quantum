package javax.microedition.lcdui;
public class Form extends Screen {
    public Form(String title) {}
    public Form(String title, Item[] items) {}
    public int append(String s) { return 0; }
    public int append(Image img) { return 0; }
    public int append(Item i) { return 0; }
    public void insert(int i, Item it) {}
    public void delete(int i) {}
    public void deleteAll() {}
    public void set(int i, Item it) {}
    public Item get(int i) { return null; }
    public int size() { return 0; }
    public void setItemStateListener(ItemStateListener l) {}
}
