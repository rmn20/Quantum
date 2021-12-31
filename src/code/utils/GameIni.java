package code.utils;

import java.util.Hashtable;

/**
 *
 * @author Roman Lahin
 */
public class GameIni extends IniFile {

    public static GameIni createGameIniFromResource(String file) {
        return createGameIniFromResource(file, false);
    }
    
    public static GameIni createGameIniFromResource(String file, boolean keys) {
        file = StringTools.getStringFromResource(file);
        return new GameIni(file, keys);
    }
    
    public static Object[] createGroups(String file) {
        Object[] tmp = IniFile.createGroups(file);
        IniFile[] oldGroups = (IniFile[])tmp[1];
        GameIni[] groups = new GameIni[oldGroups.length];
        
        for(int i=0; i<groups.length; i++) {
            groups[i] = new GameIni(oldGroups[i]);
        }
        
        return new Object[]{tmp[0],groups};
    }
    
    public static String[] cutOnStrings(String str, char d, char d2) {
        if(str == null) return null;
        
        char d3 = d;
        if(str.indexOf(d2) >= 0) d3 = d2;
        return StringTools.cutOnStrings(str, d3);
    }
    
    public static int[] cutOnInts(String str, char d, char d2) {
        if(str == null) return null;
        
        char d3 = d;
        if(str.indexOf(d2) >= 0) d3 = d2;
        return createPos(str, d3);
    }
    
    public static int[] createPos(String str, char d) {
        if(str == null) return null;
        
        String[] tmp = StringTools.cutOnStrings(str, d);
        int[] out = new int[tmp.length];
        
        for(int i=0; i<out.length; i++) {
            String s = tmp[i];
            while(s.charAt(0)==';') s = s.substring(1);
            while(s.charAt(s.length()-1)==';') s = s.substring(0,s.length()-1);
            
            out[i] = StringTools.parseInt(s);
        }
        
        return out;
    }
    
    public GameIni(IniFile ini) {
        super(ini.hashtable);
    }
    
    public GameIni(Hashtable hash) {
        super(hash);
    }
    
    public GameIni(String str, boolean keys) {
        super(str,keys);
    }
    
    public String getNoLang(String key) {
        return super.get(key);
    }
    
    public String getNoLang(String key, String def) {
        return super.getDef(key, def);
    }
    
    public int getIntNoLang(String key, int def) {
        return super.getInt(key, def);
    }
    
    public String get(String key) {
        return getDef(key, null);
    }
    
    public String get(String group, String key) {
        return getDef(group, key, null);
    }
    
    public String getDef(String key, String def) {
        String out = super.get(key);
        if(out != null) return Main.getGameText()==null?out:Main.getGameText().getDef(out,out);
        
        else return def;
    }
    
    public String getDef(String group, String key, String def) {
        String out = super.get(group, key);
        if(out != null) return Main.getGameText()==null?out:Main.getGameText().getDef(out,out);
        
        return def;
    }
    
    public static int startsWith(String str, String[] list) {
        for(int i=0; i<list.length; i++) {
            if(str.startsWith(list[i])) return i;
        }
        
        return -1;
    }

}
