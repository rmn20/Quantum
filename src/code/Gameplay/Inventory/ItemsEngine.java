package code.Gameplay.Inventory;

import code.utils.IniFile;
import code.utils.Main;

/**
 *
 * @author Roman Lahin
 */
public class ItemsEngine {
    public static IniFile[] items;
    
    public static void init() {
        if(Main.isExist("/items.txt")) { //Load items
            Object[] tmp = IniFile.createGroups("/items.txt");
            String[] names = (String[])tmp[0];
            items = (IniFile[])tmp[1];
            
            for(int i=0; i<names.length; i++) {
                items[i].put("NAME", names[i]);
            }
        }
    }
    
    public static int getItemId(String name) { //Get item id from name
        
        for(int i=0;i<items.length;i++) {
            if(items[i].get("NAME").equals(name)) return i;
        }
        
        (new Exception("Wrong item name: "+name+" !")).printStackTrace();
        return -1;
    }
}
