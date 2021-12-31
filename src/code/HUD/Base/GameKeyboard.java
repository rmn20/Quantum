package code.HUD.Base;

import code.utils.Keyboard;
import code.utils.Main;
import code.utils.canvas.MyCanvas;
import javax.microedition.lcdui.Canvas;

/**
 *
 * @author Roman Lahin
 */
public class GameKeyboard extends Keyboard {
    
    private int[] pressedNums = new int[10];
    private boolean[] pressed = new boolean[10];
    
    public static int[] keyCodes;
    public static boolean[] hasKeyCodes;

    public GameKeyboard() {
        super(Main.mainCanvas);
        if(keyCodes==null) initKeycodes();
        if(keyCodes!=null && keyCodes.length!=48) initKeycodes();
    }
    
    public void reset() {
        super.reset();
        if(pressed!=null) for(int i=0;i<pressed.length;i++) pressed[i]=false;
    }
    
    public void keyInput(final int key, boolean action) {
        for(int i=0;i<pressed.length;i++) {
            if(pressed[i]==!action) {
                if(!action && pressedNums[i]==key) {
                    pressed[i]=false;
                }
                
                if(action) {
                    pressedNums[i]=key;
                    pressed[i]=true;
                    break;
                }
            }
        }
        
        super.keyInput(key, action);
    }
    
    public boolean isPressed(int key) {
        for(int i=0;i<pressed.length;i++) {
            if(pressed[i] && pressedNums[i]==key) return true;
        }
        return false;
    }
    
    public void initKeycodes() {
        /*
        Идти вперёд 0 1
        Идти назад 2 3
        Идти влево 4 5
        Идти вправо 6 7
        
        Смотреть влево 8 9
        Смотреть вправо 10 11
        Смотреть вверх 12 13
        Смотреть вниз 14 15
        
        Огонь 16 17
        Прыжок 18 19
        Инвентарь/магазин 20 21
        Следующее оружие 22 23
        Предыдущее оружие 24 25
        Режим прицеливания 26 27
        
        Идти вперёд целясь 28 29
        Идти назад целясь 30 31
        Идти влево целясь 32 33
        Идти вправо целясь 34 35
        
        Смотреть влево целясь 36 37
        Смотреть вправо целясь 38 39
        Смотреть вверх целясь 40 41
        Смотреть вниз целясь 42 43
        
        Фонарь 44 45
        Взаимодействие 46 47
        */
        keyCodes = new int[]{
            UP,Canvas.KEY_NUM2,
            DOWN,Canvas.KEY_NUM8,
            Canvas.KEY_NUM7,0,
            Canvas.KEY_NUM9,0,
            
            LEFT,Canvas.KEY_NUM4,
            RIGHT,Canvas.KEY_NUM6,
            Canvas.KEY_NUM3,0,
            Canvas.KEY_NUM1,0,
            
            FIRE,Canvas.KEY_NUM5,
            Canvas.KEY_NUM0,0,
            SOFT_LEFT,0,
            Canvas.KEY_POUND,0,
            0,0,
            Canvas.KEY_STAR,0,
            
            Canvas.KEY_NUM3,0,
            Canvas.KEY_NUM1,0,
            Canvas.KEY_NUM7,0,
            Canvas.KEY_NUM9,0,
            
            LEFT,Canvas.KEY_NUM4,
            RIGHT,Canvas.KEY_NUM6,
            UP,Canvas.KEY_NUM2,
            DOWN,Canvas.KEY_NUM8,
            
            0,0,
            SOFT_LEFT,0,
        };
        
        hasKeyCodes = new boolean[]{
            true,true,
            true,true,
            true,false,
            true,false,
            
            true,true,
            true,true,
            true,false,
            true,false,
            
            true,true,
            true,false,
            true,false,
            true,false,
            false,false,
            true,false,
            
            true,false,
            true,false,
            true,false,
            true,false,
            
            true,true,
            true,true,
            true,true,
            true,true,
            
            false,false,
            true,false
        };
    }
    
    public boolean isWalkForward(boolean sight) {
        if(!sight && hasKeyCodes[0] && isPressed(keyCodes[0])) return true;
        if(!sight && hasKeyCodes[1] && isPressed(keyCodes[1])) return true;
        
        if(sight && hasKeyCodes[28] && isPressed(keyCodes[28])) return true;
        if(sight && hasKeyCodes[29] && isPressed(keyCodes[29])) return true;
        return false;
    }
    
    public boolean isWalkBackward(boolean sight) {
        if(!sight && hasKeyCodes[2] && isPressed(keyCodes[2])) return true;
        if(!sight && hasKeyCodes[3] && isPressed(keyCodes[3])) return true;
        
        if(sight && hasKeyCodes[30] && isPressed(keyCodes[30])) return true;
        if(sight && hasKeyCodes[31] && isPressed(keyCodes[31])) return true;
        return false;
    }
    
    public boolean isWalkLeft(boolean sight) {
        if(!sight && hasKeyCodes[4] && isPressed(keyCodes[4])) return true;
        if(!sight && hasKeyCodes[5] && isPressed(keyCodes[5])) return true;
        
        if(sight && hasKeyCodes[32] && isPressed(keyCodes[32])) return true;
        if(sight && hasKeyCodes[33] && isPressed(keyCodes[33])) return true;
        return false;
    }
    
    public boolean isWalkRight(boolean sight) {
        if(!sight && hasKeyCodes[6] && isPressed(keyCodes[6])) return true;
        if(!sight && hasKeyCodes[7] && isPressed(keyCodes[7])) return true;
        
        if(sight && hasKeyCodes[34] && isPressed(keyCodes[34])) return true;
        if(sight && hasKeyCodes[35] && isPressed(keyCodes[35])) return true;
        return false;
    }
    
    public boolean isLookLeft(boolean sight) {
        if(!sight && hasKeyCodes[8] && isPressed(keyCodes[8])) return true;
        if(!sight && hasKeyCodes[9] && isPressed(keyCodes[9])) return true;
        
        if(sight && hasKeyCodes[36] && isPressed(keyCodes[36])) return true;
        if(sight && hasKeyCodes[37] && isPressed(keyCodes[37])) return true;
        return false;
    }
    
    public boolean isLookRight(boolean sight) {
        if(!sight && hasKeyCodes[10] && isPressed(keyCodes[10])) return true;
        if(!sight && hasKeyCodes[11] && isPressed(keyCodes[11])) return true;
        
        if(sight && hasKeyCodes[38] && isPressed(keyCodes[38])) return true;
        if(sight && hasKeyCodes[39] && isPressed(keyCodes[39])) return true;
        return false;
    }
    
    public boolean isLookUp(boolean sight) {
        if(!sight && hasKeyCodes[12] && isPressed(keyCodes[12])) return true;
        if(!sight && hasKeyCodes[13] && isPressed(keyCodes[13])) return true;
        
        if(sight && hasKeyCodes[40] && isPressed(keyCodes[40])) return true;
        if(sight && hasKeyCodes[41] && isPressed(keyCodes[41])) return true;
        return false;
    }
    
    public boolean isLookDown(boolean sight) {
        if(!sight && hasKeyCodes[14] && isPressed(keyCodes[14])) return true;
        if(!sight && hasKeyCodes[15] && isPressed(keyCodes[15])) return true;
        
        if(sight && hasKeyCodes[42] && isPressed(keyCodes[42])) return true;
        if(sight && hasKeyCodes[43] && isPressed(keyCodes[43])) return true;
        return false;
    }
    
    public boolean isPlayerShooting() {
        if(hasKeyCodes[16] && isPressed(keyCodes[16])) return true;
        if(hasKeyCodes[17] && isPressed(keyCodes[17])) return true;
        return false;
    }
    
    public void releasePlayerShoot() {
        if(hasKeyCodes[16]) keyInput(keyCodes[16], false);
        if(hasKeyCodes[17]) keyInput(keyCodes[17], false);
    }
    
    public static boolean isSightKey(int key) {
        if(hasKeyCodes[26] && keyCodes[26]==key) return true;
        if(hasKeyCodes[27] && keyCodes[27]==key) return true;
        return false;
    }
    
    public static boolean isUseKey(int key) {
        if(hasKeyCodes[46] && keyCodes[46]==key) return true;
        if(hasKeyCodes[47] && keyCodes[47]==key) return true;
        return false;
    }
    
    public static boolean isInventoryKey(int key) {
        if(hasKeyCodes[20] && keyCodes[20]==key) return true;
        if(hasKeyCodes[21] && keyCodes[21]==key) return true;
        return false;
    }
    
    public static boolean isJumpKey(int key) {
        if(hasKeyCodes[18] && keyCodes[18]==key) return true;
        if(hasKeyCodes[19] && keyCodes[19]==key) return true;
        return false;
    }
    
    public static boolean isNextWeaponKey(int key) {
        if(hasKeyCodes[22] && keyCodes[22]==key) return true;
        if(hasKeyCodes[23] && keyCodes[23]==key) return true;
        return false;
    }
    
    public static boolean isPreviousWeaponKey(int key) {
        if(hasKeyCodes[24] && keyCodes[24]==key) return true;
        if(hasKeyCodes[25] && keyCodes[25]==key) return true;
        return false;
    }
}
