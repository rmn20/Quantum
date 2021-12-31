package code.Gameplay.Inventory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Roman Lahin
 */
public class ItemList {
    
    private int[] items;
    private int[] itemsCount;
    private int maxItems = 0;

    public ItemList() {
        set(16);
    }
    
    public ItemList(int startSize) {
        set(startSize);
    }
    
    private void set(int count) {
        items = new int[count];
        itemsCount = new int[count];
        maxItems = 0;
    }
    
    public void addItem(String name, int count) {
        addItem(ItemsEngine.getItemId(name),count);
    }
    
    public void addItem(int id, int count) {
        if(count <= 0) return;
        
        for(int i=0;i<maxItems;i++) {
            if(items[i]==id) {
                itemsCount[i]+=count; return;
            }
        }
        
        if(maxItems>=items.length) {
            int[] newItems = new int[(items.length/64)*64+64];
            int[] newItemsCount = new int[newItems.length];
            
            System.arraycopy(items, 0, newItems, 0, items.length);
            System.arraycopy(itemsCount, 0, newItemsCount, 0, itemsCount.length);
            
            items = newItems; itemsCount = newItemsCount;
        }
        items[maxItems] = id;
        itemsCount[maxItems] = count;
        
        maxItems++;
    }
    
    public void removeItem(String name, int count) {
        removeItem(ItemsEngine.getItemId(name),count);
    }
    
    public void removeItem(int id, int count) {
        if(count <= 0) return;
        
        for(int i=0;i<maxItems;i++) {
            if(items[i]==id) {
                itemsCount[i]-=count;
                
                if(itemsCount[i]<=0) {
                    maxItems--;
                    items[i] = items[maxItems];
                    itemsCount[i] = itemsCount[maxItems];
                }
                return;
            }
        }
        
    }
    
    public int itemsCount(String name) {
        return itemsCount(ItemsEngine.getItemId(name));
    }
    
    public int itemsCount(int id) {
        int count = 0;
        
        for(int i=0;i<maxItems;i++) {
            if(items[i]==id) count+=itemsCount[i];
        }
        
        return count;
    }
    
    public int size() {
        return maxItems;
    }
    
    public int itemAt(int id) {
        return items[id];
    }
    
    public int itemAtCount(int id) {
        return itemsCount[id];
    }
    
    public void writeSave(DataOutputStream dos) throws IOException {
        dos.writeInt(maxItems);
        
        for(int i=0;i<maxItems;i++) {
            dos.writeInt(items[i]);
            dos.writeInt(itemsCount[i]);
        }
    }
    
    public void loadSave(DataInputStream dis) throws IOException {
        maxItems = dis.readInt();
        if(items.length<maxItems) {
            items = new int[(maxItems/16)*16+16];
            itemsCount = new int[items.length];
        }
        
        for(int i=0;i<maxItems;i++) {
            items[i] = dis.readInt();
            itemsCount[i] = dis.readInt();
        }
    }
    
}
