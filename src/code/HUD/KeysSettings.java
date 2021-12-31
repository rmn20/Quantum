package code.HUD;

import code.HUD.Base.GameKeyboard;
import code.HUD.Base.Selectable;
import code.utils.IniFile;
import code.utils.Main;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class KeysSettings extends Selectable {

    private Main main;
    private Setting setting;
    private Object background;
    private boolean pauseScreen;
    private boolean set;
    private int hei;
    private int h;
    private byte keysFolder = 0;
    // 0 - Движение
    // 1 - Режим прицеливания
    // 2 - Действия
    int keysCount = 8;

    public KeysSettings(Main main, Setting setting, Object background, boolean pauseScreen) {
        this.main = main;
        this.setting = setting;
        this.background = background;
        this.pauseScreen = pauseScreen;
        setItems();

        hei = getHeight() / 2 - this.getHeight() * Main.getDisplaySize() / 200;
        h = getHeight() * Main.getDisplaySize() / 100;

    }

    private void setItems() {
        if(keysFolder == 2) keysCount=7;
        else keysCount = 8;
        
        String[] var2 = new String[4+keysCount];
        boolean[] ms = new boolean[4+keysCount];
        IniFile txt = Main.getGameText();
        int indexOld = 4;
        if(list!=null) indexOld = list.getIndex();
        int i = 0;
        
        if(keysFolder==0) var2[i] = txt.get("MAIN_KEYS");
        else if(keysFolder==1) var2[i] = txt.get("SIGHT_KEYS");
        else if(keysFolder==2) var2[i] = txt.get("OTHER_KEYS");
        ms[i] = true;
        i++;
        
        var2[i] = "";
        ms[i] = true;
        i++;
        
        /*var2[i] = txt.getString2("LOAD_KEYS_PRESET");
        ms[i] = false;
        i++;*/
        var2[i] = txt.get("RESET_ALL_KEYS");
        ms[i] = false;
        i++;
        
        var2[i] = "";
        ms[i] = true;
        i++;
        
        if(keysFolder<=1) {
            String tmp = "";
            int move=getMove();
            
            if(GameKeyboard.hasKeyCodes[ (i+move)*2 ]) {
                try { 
                    tmp = Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2 ]);
                } catch(Exception e) {
                    tmp = Integer.toString( GameKeyboard.keyCodes[ (i+move)*2 ] );
                }
            }
            if(GameKeyboard.hasKeyCodes[ (i+move)*2+1 ]) {
                try { 
                    tmp = tmp + " " + Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2+1 ]);
                } catch(Exception e) {
                    tmp = tmp + " " + Integer.toString( GameKeyboard.keyCodes[ (i+move)*2+1 ] );
                }
            }
            var2[i] = txt.get("WALK_FORWARD_KEY")+": "+tmp;
            ms[i] = false;
            i++;
            tmp = "";
            
            if(GameKeyboard.hasKeyCodes[ (i+move)*2 ]) {
                try { 
                    tmp = Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2 ]);
                } catch(Exception e) {
                    tmp = Integer.toString( GameKeyboard.keyCodes[ (i+move)*2 ] );
                }
            }
            if(GameKeyboard.hasKeyCodes[ (i+move)*2+1 ]) {
                try { 
                    tmp = tmp + " " + Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2+1 ]);
                } catch(Exception e) {
                    tmp = tmp + " " + Integer.toString( GameKeyboard.keyCodes[ (i+move)*2+1 ] );
                }
            }
            var2[i] = txt.get("WALK_BACKWARD_KEY")+": "+tmp;
            ms[i] = false;
            i++;
            tmp = "";
            
            if(GameKeyboard.hasKeyCodes[ (i+move)*2 ]) {
                try { 
                    tmp = Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2 ]);
                } catch(Exception e) {
                    tmp = Integer.toString( GameKeyboard.keyCodes[ (i+move)*2 ] );
                }
            }
            if(GameKeyboard.hasKeyCodes[ (i+move)*2+1 ]) {
                try { 
                    tmp = tmp + " " + Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2+1 ]);
                } catch(Exception e) {
                    tmp = tmp + " " + Integer.toString( GameKeyboard.keyCodes[ (i+move)*2+1 ] );
                }
            }
            var2[i] = txt.get("WALK_LEFT_KEY")+": "+tmp;
            ms[i] = false;
            i++;
            tmp = "";
            
            if(GameKeyboard.hasKeyCodes[ (i+move)*2 ]) {
                try { 
                    tmp = Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2 ]);
                } catch(Exception e) {
                    tmp = Integer.toString( GameKeyboard.keyCodes[ (i+move)*2 ] );
                }
            }
            if(GameKeyboard.hasKeyCodes[ (i+move)*2+1 ]) {
                try { 
                    tmp = tmp + " " + Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2+1 ]);
                } catch(Exception e) {
                    tmp = tmp + " " + Integer.toString( GameKeyboard.keyCodes[ (i+move)*2+1 ] );
                }
            }
            var2[i] = txt.get("WALK_RIGHT_KEY")+": "+tmp;
            ms[i] = false;
            i++;
            tmp = "";
            
            
            
            if(GameKeyboard.hasKeyCodes[ (i+move)*2 ]) {
                try { 
                    tmp = Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2 ]);
                } catch(Exception e) {
                    tmp = Integer.toString( GameKeyboard.keyCodes[ (i+move)*2 ] );
                }
            }
            if(GameKeyboard.hasKeyCodes[ (i+move)*2+1 ]) {
                try { 
                    tmp = tmp + " " + Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2+1 ]);
                } catch(Exception e) {
                    tmp = tmp + " " + Integer.toString( GameKeyboard.keyCodes[ (i+move)*2+1 ] );
                }
            }
            var2[i] = txt.get("LOOK_LEFT_KEY")+": "+tmp;
            ms[i] = false;
            i++;
            tmp = "";
            
            if(GameKeyboard.hasKeyCodes[ (i+move)*2 ]) {
                try { 
                    tmp = Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2 ]);
                } catch(Exception e) {
                    tmp = Integer.toString( GameKeyboard.keyCodes[ (i+move)*2 ] );
                }
            }
            if(GameKeyboard.hasKeyCodes[ (i+move)*2+1 ]) {
                try { 
                    tmp = tmp + " " + Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2+1 ]);
                } catch(Exception e) {
                    tmp = tmp + " " + Integer.toString( GameKeyboard.keyCodes[ (i+move)*2+1 ] );
                }
            }
            var2[i] = txt.get("LOOK_RIGHT_KEY")+": "+tmp;
            ms[i] = false;
            i++;
            tmp = "";
            
            if(GameKeyboard.hasKeyCodes[ (i+move)*2 ]) {
                try { 
                    tmp = Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2 ]);
                } catch(Exception e) {
                    tmp = Integer.toString( GameKeyboard.keyCodes[ (i+move)*2 ] );
                }
            }
            if(GameKeyboard.hasKeyCodes[ (i+move)*2+1 ]) {
                try { 
                    tmp = tmp + " " + Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2+1 ]);
                } catch(Exception e) {
                    tmp = tmp + " " + Integer.toString( GameKeyboard.keyCodes[ (i+move)*2+1 ] );
                }
            }
            var2[i] = txt.get("LOOK_UP_KEY")+": "+tmp;
            ms[i] = false;
            i++;
            tmp = "";
            
            if(GameKeyboard.hasKeyCodes[ (i+move)*2 ]) {
                try { 
                    tmp = Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2 ]);
                } catch(Exception e) {
                    tmp = Integer.toString( GameKeyboard.keyCodes[ (i+move)*2 ] );
                }
            }
            if(GameKeyboard.hasKeyCodes[ (i+move)*2+1 ]) {
                try { 
                    tmp = tmp + " " + Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2+1 ]);
                } catch(Exception e) {
                    tmp = tmp + " " + Integer.toString( GameKeyboard.keyCodes[ (i+move)*2+1 ] );
                }
            }
            var2[i] = txt.get("LOOK_DOWN_KEY")+": "+tmp;
            ms[i] = false;
            i++;
            tmp = "";
        } else if(keysFolder==2) {
            String tmp = "";
            int move=getMove();
            
            if(GameKeyboard.hasKeyCodes[ (i+move)*2 ]) {
                try { 
                    tmp = Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2 ]);
                } catch(Exception e) {
                    tmp = Integer.toString( GameKeyboard.keyCodes[ (i+move)*2 ] );
                }
            }
            if(GameKeyboard.hasKeyCodes[ (i+move)*2+1 ]) {
                try { 
                    tmp = tmp + " " + Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2+1 ]);
                } catch(Exception e) {
                    tmp = tmp + " " + Integer.toString( GameKeyboard.keyCodes[ (i+move)*2+1 ] );
                }
            }
            var2[i] = txt.get("FIRE_KEY")+": "+tmp;
            ms[i] = false;
            i++;
            tmp = "";
            
            if(GameKeyboard.hasKeyCodes[ (i+move)*2 ]) {
                try { 
                    tmp = Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2 ]);
                } catch(Exception e) {
                    tmp = Integer.toString( GameKeyboard.keyCodes[ (i+move)*2 ] );
                }
            }
            if(GameKeyboard.hasKeyCodes[ (i+move)*2+1 ]) {
                try { 
                    tmp = tmp + " " + Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2+1 ]);
                } catch(Exception e) {
                    tmp = tmp + " " + Integer.toString( GameKeyboard.keyCodes[ (i+move)*2+1 ] );
                }
            }
            var2[i] = txt.get("JUMP_KEY")+": "+tmp;
            ms[i] = false;
            i++;
            tmp = "";
            
            if(GameKeyboard.hasKeyCodes[ (i+move)*2 ]) {
                try { 
                    tmp = Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2 ]);
                } catch(Exception e) {
                    tmp = Integer.toString( GameKeyboard.keyCodes[ (i+move)*2 ] );
                }
            }
            if(GameKeyboard.hasKeyCodes[ (i+move)*2+1 ]) {
                try { 
                    tmp = tmp + " " + Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2+1 ]);
                } catch(Exception e) {
                    tmp = tmp + " " + Integer.toString( GameKeyboard.keyCodes[ (i+move)*2+1 ] );
                }
            }
            var2[i] = txt.get("INVENTORY_KEY")+": "+tmp;
            ms[i] = false;
            i++;
            tmp = "";
            
            if(GameKeyboard.hasKeyCodes[ (i+move)*2 ]) {
                try { 
                    tmp = Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2 ]);
                } catch(Exception e) {
                    tmp = Integer.toString( GameKeyboard.keyCodes[ (i+move)*2 ] );
                }
            }
            if(GameKeyboard.hasKeyCodes[ (i+move)*2+1 ]) {
                try { 
                    tmp = tmp + " " + Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2+1 ]);
                } catch(Exception e) {
                    tmp = tmp + " " + Integer.toString( GameKeyboard.keyCodes[ (i+move)*2+1 ] );
                }
            }
            var2[i] = txt.get("NEXT_WEAPON_KEY")+": "+tmp;
            ms[i] = false;
            i++;
            tmp = "";
            
            if(GameKeyboard.hasKeyCodes[ (i+move)*2 ]) {
                try { 
                    tmp = Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2 ]);
                } catch(Exception e) {
                    tmp = Integer.toString( GameKeyboard.keyCodes[ (i+move)*2 ] );
                }
            }
            if(GameKeyboard.hasKeyCodes[ (i+move)*2+1 ]) {
                try { 
                    tmp = tmp + " " + Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2+1 ]);
                } catch(Exception e) {
                    tmp = tmp + " " + Integer.toString( GameKeyboard.keyCodes[ (i+move)*2+1 ] );
                }
            }
            var2[i] = txt.get("PREVIOUS_WEAPON_KEY")+": "+tmp;
            ms[i] = false;
            i++;
            tmp = "";
            
            if(GameKeyboard.hasKeyCodes[ (i+move)*2 ]) {
                try { 
                    tmp = Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2 ]);
                } catch(Exception e) {
                    tmp = Integer.toString( GameKeyboard.keyCodes[ (i+move)*2 ] );
                }
            }
            if(GameKeyboard.hasKeyCodes[ (i+move)*2+1 ]) {
                try { 
                    tmp = tmp + " " + Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2+1 ]);
                } catch(Exception e) {
                    tmp = tmp + " " + Integer.toString( GameKeyboard.keyCodes[ (i+move)*2+1 ] );
                }
            }
            var2[i] = txt.get("SIGHT_MODE_KEY")+": "+tmp;
            ms[i] = false;
            i++;
            tmp = "";
            
            move=22-10;
            
            if(/*Main.flashlight*/false) {
            if(GameKeyboard.hasKeyCodes[ (i+move)*2 ]) {
                try { 
                    tmp = Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2 ]);
                } catch(Exception e) {
                    tmp = Integer.toString( GameKeyboard.keyCodes[ (i+move)*2 ] );
                }
            }
            if(GameKeyboard.hasKeyCodes[ (i+move)*2+1 ]) {
                try { 
                    tmp = tmp + " " + Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2+1 ]);
                } catch(Exception e) {
                    tmp = tmp + " " + Integer.toString( GameKeyboard.keyCodes[ (i+move)*2+1 ] );
                }
            }
            var2[i] = txt.get("FLASHLIGHT_KEY")+": "+tmp;
            ms[i] = false;
            i++;
            tmp = "";
            }
            move=23-10;
            
            if(GameKeyboard.hasKeyCodes[ (i+move)*2 ]) {
                try { 
                    tmp = Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2 ]);
                } catch(Exception e) {
                    tmp = Integer.toString( GameKeyboard.keyCodes[ (i+move)*2 ] );
                }
            }
            if(GameKeyboard.hasKeyCodes[ (i+move)*2+1 ]) {
                try { 
                    tmp = tmp + " " + Main.mainCanvas.getKeyName(GameKeyboard.keyCodes[ (i+move)*2+1 ]);
                } catch(Exception e) {
                    tmp = tmp + " " + Integer.toString( GameKeyboard.keyCodes[ (i+move)*2+1 ] );
                }
            }
            var2[i] = txt.get("INTERACT_KEY")+": "+tmp;
            ms[i] = false;
            i++;
            tmp = "";
        }

        set(Main.getFont(), var2, (String) txt.get("RESET_KEYS"), txt.get("BACK"), ms);
        list.setIndex(indexOld);
        list.left=true;
    }

    private int getMove() {
        if(keysFolder==1) return 14-4;
        if(keysFolder==2) return 8-4;
        return -4;
    }
    
    protected final void paint(Graphics g) {
        g.setColor(0);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (background instanceof Image) {
            g.drawImage((Image) background, 0, 0, 0);
        } else if (this.background instanceof int[]) {
            g.drawRGB((int[]) background, 0, getWidth(), 0, hei, getWidth(), h, false);
        }

        if (pauseScreen) {
            list.drawBck(g, getWidth() / 8, Main.getFont().height(), getWidth() * 6 / 8, getHeight() - Main.getFont().height()*2);
            Main.drawBckDialog(g, getHeight() - Main.getFont().height(), getHeight());
        }
        
        list.draw(g, getWidth() / 8, Main.getFont().height(), getWidth() * 6 / 8, getHeight() - Main.getFont().height() * 2);
        drawSoftKeys(g);
    }

    protected final void onRightSoftKey() {
        Main.setCurrent(setting);
    }

    protected final void onLeftSoftKey() {
        if(list.getIndex()<4) return;
        
        int index = (list.getIndex()+getMove())*2;
        if(keysFolder == 2 && list.getIndex()>=10) {
            index = (list.getIndex()-10+23)*2;
        }
        GameKeyboard.hasKeyCodes[index]=false;
        GameKeyboard.hasKeyCodes[index+1]=false;
        
        setItems();
        repaint();
    }
    
    protected final void keyPressed(int keyCode) {
        if(!set) super.keyPressed(keyCode);
        else {
            int ki = (list.getIndex()+getMove())*2;
            if(keysFolder == 2 && list.getIndex()>=10) {
                ki = (list.getIndex()-10+23)*2;
            }
            setKey(keyCode, ki );
            
            set=false;
            list.redact=false;
            setItems();
            repaint();
        }
    }

    private void setKey(int keyCode, int index) {
        if( GameKeyboard.hasKeyCodes[index] && GameKeyboard.hasKeyCodes[index+1] ) {
            GameKeyboard.hasKeyCodes[index]=false;
            GameKeyboard.hasKeyCodes[index+1]=false;
        }
        
        if( !GameKeyboard.hasKeyCodes[index] ) {
            GameKeyboard.hasKeyCodes[index]=true;
            GameKeyboard.keyCodes[index]=keyCode;
        } else if( !GameKeyboard.hasKeyCodes[index+1] ) {
            GameKeyboard.hasKeyCodes[index+1]=true;
            GameKeyboard.keyCodes[index+1]=keyCode;
        }
    }
    
    protected final void onKey5() {
        if(list.getIndex()==3) {
            keys.initKeycodes();
            
            setItems();
            repaint();
        }
        
        if(list.getIndex()>=4) {
            set=true;
            list.redact=true;
            repaint();
        }
    }
    protected final void onKey6() {
        keysFolder++;
        if(keysFolder>2) keysFolder=0;
        setItems();
        if(list.getIndex()>=keysCount) list.setIndex(keysCount);
        repaint();
    }
    protected final void onKey4() {
        keysFolder--;
        if(keysFolder<0) keysFolder=2;
        setItems();
        if(list.getIndex()>=keysCount) list.setIndex(keysCount);
        repaint();
    }

}
