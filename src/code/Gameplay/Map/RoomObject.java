package code.Gameplay.Map;

import code.AI.Bot;
import code.utils.DelayedDialog;
import code.Gameplay.GameScreen;
import code.AI.Player;
import code.Rendering.DirectX7;
import code.Rendering.Vertex;
import code.Math.Vector3D;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import code.Gameplay.Objects.MeshObject;
import code.Gameplay.Objects.SpriteObject;
import code.AI.NPC;
import code.Gameplay.Arsenal;
import code.Gameplay.Objects.GameObject;
import code.Gameplay.Inventory.ItemsEngine;
import code.HUD.Base.TextView;
import code.utils.GameIni;
import code.utils.Main;
import code.utils.StringTools;
import code.utils.WeaponCreator;
import java.util.Random;

public abstract class RoomObject {
   
   private static Random rand = new Random();
   private static final Vertex vert=new Vertex();
   
   private int oldX;
   private int oldZ;
   private int oldY;
   private int part=-1;
   private int toPart=-1;
   public boolean activable=false;
   public boolean clickable=false;
   public boolean singleUse=false;
   public boolean destroyOnUse=false;
   public boolean alwaysActivate=false;
   public String desc=null;
   public String name=null;
   public String sound=null;
   public String[] need=null;
   public String[] additional=null;
   public String errMessage=null;
   public String message=null;
   public boolean hideWhenUnusable=false;
   public long lastActivate=-1;
   public long timeToReset=0;
   public long radius=4000L;
   public long messageTimeOut=-1L;
   public int messageType=0;
   public int pointRadius=150;
   public int pointHeight=150;
   public Vector3D pointOffset=null;
   public boolean needToPoint=false;
   public boolean squarePoint=false;
   public boolean dynamicPoint=false;
   public boolean visible=true;
   public boolean activateOnlyOne=false;
   public boolean reloadDestroy=true;

   public long errMessageTimeOut=-1L;
   public long errMessageDelay=0;
   public long messageDelay=0;
   public int errMessageType=0;
   
   public boolean near=true;
   public boolean activated=false;
   
   //public SoundSource3D snd3d;
   
   public abstract void render(DirectX7 g3d, int x1, int y1, int x2, int y2);

   public final int getPart() {
      return this.part;
   }

   public final void setPart(int part) {
      this.part = part;
   }
   
   public final int getNewPart() {
      return this.toPart;
   }

   public final void setNewPart(int toPart) {
      this.toPart = toPart;
   }

   public abstract int getPosX();

   public abstract int getPosZ();

   public abstract int getPosY();
   
   public abstract void setPos(int x,int y,int z);

   protected boolean isNeedRecomputePart() {
      int x = this.getPosX();
      int z = this.getPosZ();
      int y = this.getPosY();
      if(this.oldX != x || this.oldZ != z || this.oldY != y || this.part == -1) {
         this.oldX = x;
         this.oldZ = z;
         this.oldY = y;
         return true;
      } 
         return false;
      
   }

    public boolean isAllCollected(Vector points, Player player,House house, GameScreen gs) {
        if(need == null) return true;
        for (int i = 0; i < need.length; i++) {
            if( !containsCollected( points, need[i] ,player,house,gs,this)) return false;
        }
        return true;
    }
    
    public static boolean contains(int[] in,int[] check) {
        if(in == null || check==null) return false;
        for(int i=0;i<check.length;i++) {
            int need=check[i];
            boolean contains=false;
            for(int x=0;x<in.length || contains;x++) {
                if(in[x]==need) contains=true;
            }
            if(!contains) return false;
        }
        return true;
    }
    
    public static boolean containsCollected(Vector points, String name, Player player,House house, GameScreen gs,RoomObject th) {
        if(name!=null) {
            boolean inverse=false;
            if(name.indexOf('!')==0) {
                inverse=true;
                name=name.substring(1,name.length());
            }
            String hcName=name.toUpperCase();
        if(hcName.indexOf("WEAPON_")==0) {
            int i=StringTools.parseInt(name.substring(7,name.length()));
            if(player.arsenal.weapons[i]!=null) return !inverse;
        } else if(hcName.indexOf('=')!=-1 || hcName.indexOf('>')!=-1 || hcName.indexOf('<')!=-1) {
            int charind=hcName.indexOf('=');
            if(charind==-1) charind=hcName.indexOf('>');
            if(charind==-1) charind=hcName.indexOf('<');
            char operator=hcName.charAt(charind);
            char operator2=hcName.charAt(charind-1);
            int var1; 
            if(operator2=='>' || operator2=='<') var1=readVar(name.substring(0,charind-1),player,house,gs,th);
            else var1=readVar(name.substring(0,charind),player,house,gs,th);
            int var2=readVar(name.substring(charind+1,name.length()),player,house,gs,th);
            if(operator=='=' && operator2=='>') if(var1>=var2) return !inverse;
            if(operator=='=' && operator2=='<') if(var1<=var2) return !inverse;
            if(operator=='=') if(var1==var2) return !inverse;
            if(operator=='>') if(var1>var2) return !inverse;
            if(operator=='<') if(var1<var2) return !inverse;
            return inverse;
        } else if(hcName.equals("FALSE")) {
            return inverse;
        } else if(hcName.equals("TRUE")) {
            return !inverse;
        } else {
        for (int i = 0; i < points.size(); i++) {
            String pName = ((String)points.elementAt(i));
            if( pName != null && pName.equals( name ) ) return !inverse;
        }
        }
        return inverse;
        }
        else
        {
            return false;
        }
    }
    
    public boolean contains(Vector points, String name) {
        if(name!=null) {
            if(this.singleUse==false) return false;
            boolean inverse=true;
            if(name.indexOf('!')==0) {
                inverse=false;
                name=name.substring(1,name.length());
            }
        for (int i = 0; i < points.size(); i++) {
            String pName = ((String)points.elementAt(i));
            if( pName != null ) if(pName.equals( name ) ) return inverse;
        }
        return !inverse;
        }
        else
        {
            return (this.activated==true && this.singleUse==true);
        }
    }
    
    
    public static boolean containsSimple(Vector points, String name) {
        if (name == null) return false;
        
        boolean inverse = true;
        if (name.indexOf('!') == 0) {
            inverse = false;
            name = name.substring(1, name.length());
        }
        
        for (int i=0; i<points.size(); i++) {
            String pName = ((String) points.elementAt(i));
            if(pName!=null && pName.equals(name)) return inverse;
        }
        return !inverse;
    }
    
    public void give(String[] name,Player player,House house,GameScreen gs) {
        if(name==null) return;
    for(int num=0;num<name.length;num++) {
        boolean inverse=false;
        String newName=name[num];
        if(newName==null) continue;
        String tmp;
        if(newName.indexOf('!')==0) { inverse=true; newName=newName.substring(1,newName.length());}
        String hcName=newName.toUpperCase();
        if(newName.charAt(0)=='{') {
                int counter=1;
                for(int num2=num+1;num2<name.length;num2++){
                    if(name[num2].charAt(0)=='}') 
                    {counter--;
                    if(counter==0) {num=num2;continue;}
                    } else if(name[num2].charAt(0)=='{') counter++;
                }
        } else if(newName.charAt(0)=='}') { continue;
        } else if(hcName.indexOf("WEAPON_")==0) {
            int i=readVar(newName.substring(7,newName.length()),player,house,gs,this);
            if(!inverse) if(player.arsenal.weapons[i]==null) {
                player.arsenal.weapons[i]=WeaponCreator.createWeapon(i);
                player.arsenal.weapons[i].setAmmo(0);
                if(player.arsenal.current!=-1) 
                    if(player.arsenal.currentWeapon().getDamageValue()<player.arsenal.weapons[i].getDamageValue()) 
                        player.arsenal.current=i;
            }
            if(inverse) {
                player.arsenal.weapons[i]=null;
                player.arsenal.next();
            }
        } else if(hcName.equals("END_SCRIPT")) {
            return;
        } else if(hcName.equals("REDRAW")) {
            //todo
        } else if(hcName.equals("SAVE_GAME")) {
            if(gs.player.getHp()>0) {
                Main.saveGame(gs.levelNumber,player,gs.scene);
                Main.saveObjects(gs.levelNumber, player, gs.scene);
            }
        } else if(hcName.indexOf("DROP_ITEM(")==0) {
            String vars[]=StringTools.cutOnStrings(newName.substring(10,newName.length()-1),',');
            int x = readVar(vars[1],player,house,gs,this);
            int y = readVar(vars[2],player,house,gs,this);
            int z = readVar(vars[3],player,house,gs,this);
            int count = 1;
            if(vars.length>=5) count = readVar(vars[4],player,house,gs,this);
            
            gs.scene.dropItem(vars[0], count, x, y, z);
            return;
        } else if(hcName.indexOf("LOAD_LEVEL(")==0) {
            String vars[]=StringTools.cutOnStrings(newName.substring(11,newName.length()-1),',');
            Vector3D pos=new Vector3D(player.getPosX(),player.getPosY(),player.getPosZ());
            boolean saveMus=false;
            boolean fullMove=false;
			boolean showLoad = true;
            gs.newPos=pos;
            if(vars.length>=4) gs.newPos=new Vector3D(readVar(vars[1],player,house,gs,this),readVar(vars[2],player,house,gs,this),readVar(vars[3],player,house,gs,this));
            if(vars.length>=5) {
				int rotY = readVar(vars[4],player,house,gs,this);
				if(rotY != 0) {
					player.rotYn(rotY);
					player.updateMatrix();
				}
			}
            if(vars.length>=6) saveMus=readBoolean(vars[5],player,house,gs,this);
            if(vars.length>=7) fullMove=readBoolean(vars[6],player,house,gs,this);
            if(vars.length>=8) showLoad=readBoolean(vars[6],player,house,gs,this);
            gs.loadLevel(readVar(vars[0],player,house,gs,this), pos, !saveMus, fullMove, showLoad);
            return;
        } else if(hcName.indexOf("EXEC ")==0) {
            gs.runScriptFromFile(newName.substring(5), this);
        } else if(hcName.indexOf("SHOW_MESSAGE(")==0) {
            String vars[]=StringTools.cutOnStrings(newName.substring(13,newName.length()-1),',');
            String msg=readString(vars[0], player, house, gs, this);
            int time=readVar(vars[1],player,house,gs,this);
            int delay=1;
            int type=1;
            if(vars.length>2) type=readVar(vars[2],player,house,gs,this);
            GameScreen.delayDialogs.addElement(new DelayedDialog(time,type,msg,delay));
        } else if(hcName.indexOf("OPENTITLESCREEN()")==0) {
            gs.openTitleScreenScript();
            return;
        } else if(hcName.indexOf("REMOVEARSENAL()")==0) {
            player.arsenal.removeAll();
            return;
        } else if(hcName.indexOf("IF")==0) {
            boolean i=readBoolean(newName.substring(2,newName.length()),player,house,gs,this);
            if(num==name.length-1) continue;
            if(i==true) {
            if(name[num+1].charAt(0)=='{') {
                num++; continue;
            }
            } else {
                if(name[num+1].charAt(0)!='{') num+=1;
                continue; 
            }
            
        } else if(hcName.indexOf("MONEY")==0) {
            if(newName.charAt(5)=='=') {
                player.money=readVar(newName.substring(6,newName.length()),player,house,gs,this);
                continue;
            }
            int i=readVar(newName.substring(7,newName.length()),player,house,gs,this);
            if(inverse) i*=-1;
            if(newName.charAt(5)=='-') i*=-1;
            player.money+=i;
        } else if(hcName.indexOf("AMMO_")==0) {
            tmp=newName.substring(5,newName.length());
            int index=tmp.indexOf('=');
            char operation='0';
            if(index-1>=0) operation=tmp.charAt(index-1);
            boolean oper=false;
            if(operation=='+'||operation=='-') {index-=1;oper=true;}
            int weapon=readVar(tmp.substring(0,index),player,house,gs,this);
            if(!oper) {
                player.arsenal.weapons[weapon].setAmmo(readVar(tmp.substring(index+1,tmp.length()),player,house,gs,this));
                continue;
            }
            int i=readVar(tmp.substring(index+2,tmp.length()),player,house,gs,this);
            if(inverse) i*=-1;
            if(operation=='-') i*=-1;
            if(player.arsenal.weapons[weapon]!=null) player.arsenal.weapons[weapon].magazine.addAmmo(i);
        } else if(hcName.indexOf("FRAGS")==0) {
            if(newName.charAt(4)=='=') {
                player.frags=readVar(newName.substring(5,newName.length()),player,house,gs,this);
                continue;
            }
            int i=readVar(newName.substring(6,newName.length()),player,house,gs,this);
            if(inverse) i*=-1;
            if(newName.charAt(4)=='-') i*=-1;
            player.frags+=i;
        } else if(newName.indexOf(':')!=-1) {
            String objname=newName.substring(0,newName.indexOf(':'));
            String command=newName.substring(newName.indexOf(':')+1,newName.length());
            String hcCommand=command.toUpperCase();
            Vector objs=house.getObjects();
            RoomObject obj=null;
            if(objname.equalsIgnoreCase("system")) {
                getSetValueSystem(player,house,gs,command,inverse,true,null);
                continue;
            }
            if(objname.equalsIgnoreCase("player")) obj=player;
            if(objname.equalsIgnoreCase("self")) obj=this;
            
            
            if (objs.size() > 0 && obj == null) {
                for (int i = 0; i < objs.size(); i++) {
                    if (((RoomObject) objs.elementAt(i)).name != null) {
                        if (((RoomObject) objs.elementAt(i)).name.equals(objname)) obj = ((RoomObject) objs.elementAt(i));
                    }
                }
            }
            
            if(obj==null) continue;
            if(hcCommand.equals("ACTIVATE")) {
            obj.activate(house,player,gs);
            } else {
                getSetValue(player,house,gs,obj,command,inverse,true,null,this);
            }
            /*else if(hcCommand.indexOf("SOUND=")==0) {
                obj.sound=(readString(hcCommand.substring(6,hcCommand.length()),player,house,gs));
            } */
        } else {
        if(inverse) Player.usedPoints.removeElement(newName);
        if(!inverse) if(!containsSimple(Player.usedPoints,newName)) Player.usedPoints.addElement(newName);
        }  
    }    
    }
    
    public static int getSetValue(Player player,House house,GameScreen gs,
            RoomObject obj,String command,boolean inverse,boolean set,String buffer,RoomObject th) {
        
        if(obj==null) return 0;
        GameObject gobj=null;
        if(obj instanceof GameObject) gobj=(GameObject)obj;
        
        String hcCommand=command.toUpperCase();
        boolean toggle=false;
        if(hcCommand.indexOf("TOGGLE_")!=-1) {
                toggle=true; 
                hcCommand=hcCommand.substring(7,hcCommand.length());
                command=command.substring(7,command.length());
        }
        if(hcCommand.indexOf("HP")==0 && obj instanceof GameObject) {
            GameObject obj2=(GameObject)obj;
            if(!set) return obj2.getHp()*(inverse?-1:1);
            if(hcCommand.charAt(2)=='=') {
                obj2.setHp(readVar(hcCommand.substring(3,hcCommand.length()),player,house,gs,th));
                } else {
            int i=readVar(hcCommand.substring(4,hcCommand.length()),player,house,gs,th);
            if(inverse) i*=-1;
            if(hcCommand.charAt(2)=='-') i*=-1;
            obj2.damage(-i);
            }
            } else if(obj instanceof Player && hcCommand.indexOf("GIVE(")==0) {
                String vars[]=StringTools.cutOnStrings(command.substring(5,command.length()-1),',');
                int count = 1;
                if(vars.length>=2) count = readVar(vars[1],player,house,gs,th);
                player.items.addItem(vars[0], count);
                
                String pckScript = ItemsEngine.items[ItemsEngine.getItemId(vars[0])].get("ON_GIVE");
                if(pckScript!=null) gs.runScript(StringTools.cutOnStrings(pckScript, ';'));
                
                return 1;
            } else if(obj instanceof Player && hcCommand.indexOf("TAKE(")==0) {
                String vars[]=StringTools.cutOnStrings(command.substring(5,command.length()-1),',');
                int count = 1;
                if(vars.length>=2) count = readVar(vars[1],player,house,gs,th);
                player.items.removeItem(vars[0], count);
                
                
               String rmsScript = ItemsEngine.items[ItemsEngine.getItemId(vars[0])].get("ON_TAKE");
               if(rmsScript!=null) gs.runScript(StringTools.cutOnStrings(rmsScript, ';'));
        
                return 1;
            } else if(hcCommand.equals("FLASHLIGHT")) {
                if(!set) return (gs.scene.getG3D().flashlightEnabled?!inverse:inverse)?1:0;
                if(!toggle) gs.scene.getG3D().flashlightEnabled=!inverse;
                else gs.scene.getG3D().flashlightEnabled ^= true;
            } else if(obj instanceof Player && hcCommand.indexOf("ITEM_COUNT_")==0) {
                return player.items.itemsCount(command.substring(11,command.length()));
            } else if(hcCommand.equals("ACTIVABLE")) {
                if(!set) return (obj.activable?!inverse:inverse)?1:0;
                if(!toggle) obj.activable=!inverse;
                else obj.activable ^= true;
            } else if(hcCommand.equals("CLICKABLE")) {
                if(!set) return (obj.clickable?!inverse:inverse)?1:0;
                if(!toggle) obj.clickable=!inverse;
                else obj.clickable ^= true;
            } else if(hcCommand.equals("SINGLEUSE")) {
                if(!set) return (obj.singleUse?!inverse:inverse)?1:0;
                if(!toggle) obj.singleUse=!inverse;
                else obj.singleUse ^= true;
            } else if(hcCommand.equals("DESTROYONUSE")) {
                if(!set) return (obj.destroyOnUse?!inverse:inverse)?1:0;
                if(!toggle) obj.destroyOnUse=!inverse;
                else obj.destroyOnUse ^= true;
            } else if(hcCommand.equals("HIDEWHENUNUSABLE")) {
                if(!set) return (obj.hideWhenUnusable?!inverse:inverse)?1:0;
                if(!toggle) obj.hideWhenUnusable=!inverse;
                else obj.hideWhenUnusable ^= true;
            } else if(hcCommand.indexOf("NEEDTOPOINT")==0) {
                int ntp=0;
                if(obj.needToPoint) ntp=1;
                if(obj.squarePoint) ntp=2;
                if(!set) return ntp;
                if(hcCommand.charAt(11)=='=') {
                ntp=readVar(command.substring(12,command.length()),player,house,gs,th);
            } else {
            int i=readVar(command.substring(13,command.length()),player,house,gs,th);
            if(hcCommand.charAt(11)=='-') i*=-1;
            if(inverse) i*=-1;
            ntp+=i;
                }
                if(ntp==0) { obj.needToPoint=false; obj.squarePoint=false; }
                else if(ntp==1) { obj.needToPoint=true; obj.squarePoint=false; }
                else { obj.needToPoint=true; obj.squarePoint=true; }
            } else if(hcCommand.indexOf("X")==0) {
                if(!set) return obj.getPosX()*(inverse?-1:1);
                if(hcCommand.charAt(1)=='=') {
                obj.setPos(readVar(command.substring(2,command.length()),player,house,gs,th),obj.getPosY(),obj.getPosZ());
            } else {     
            int i=readVar(command.substring(3,command.length()),player,house,gs,th);
            if(hcCommand.charAt(1)=='-') i*=-1;
            if(inverse) i*=-1;
            obj.setPos(obj.getPosX()+readVar(command.substring(2,command.length()),player,house,gs,th),obj.getPosY(),obj.getPosZ());
            }
            } else if(hcCommand.indexOf("Y")==0) {
                if(!set) return obj.getPosY()*(inverse?-1:1);
                if(hcCommand.charAt(1)=='=') {
                obj.setPos(obj.getPosX(),readVar(command.substring(2,command.length()),player,house,gs,th),obj.getPosZ());
            } else {     
            int i=readVar(command.substring(3,command.length()),player,house,gs,th);
            if(hcCommand.charAt(1)=='-') i*=-1;
            if(inverse) i*=-1;
            obj.setPos(obj.getPosX(),obj.getPosY()+readVar(command.substring(2,command.length()),player,house,gs,th),obj.getPosZ());
            }
            } else if(hcCommand.indexOf("Z")==0) {
                if(!set) return obj.getPosZ()*(inverse?-1:1);
                if(hcCommand.charAt(1)=='=') {
                obj.setPos(obj.getPosX(),obj.getPosY(),readVar(command.substring(2,command.length()),player,house,gs,th));
            } else {     
            int i=readVar(command.substring(3,command.length()),player,house,gs,th);
            if(hcCommand.charAt(1)=='-') i*=-1;
            if(inverse) i*=-1;
            obj.setPos(obj.getPosX(),obj.getPosY(),obj.getPosZ()+readVar(command.substring(2,command.length()),player,house,gs,th));
            }
            } else if(hcCommand.equals("VISIBLE")) {
                if(!set) return (obj.visible?!inverse:inverse)?1:0;
                if(!toggle) obj.visible=!inverse;
                else obj.visible ^= true;
            } else if(hcCommand.indexOf("NAME")==0) {
                if(!set && buffer==null) {return 0;}
                if(!set) {buffer=obj.name;return 0;}
                obj.name=(readString(hcCommand.substring(5,hcCommand.length()),player,house,gs,th));
            } else if(hcCommand.indexOf("DESC")==0) {
                if(!set && buffer==null) {return 0;}
                if(!set) {buffer=obj.desc;return 0;}
                obj.desc=(readString(hcCommand.substring(5,hcCommand.length()),player,house,gs,th));
            } else if(hcCommand.indexOf("NEED")==0) {
                if(!set) {return 0;}
                obj.need=GameIni.cutOnStrings(readString(hcCommand.substring(5,hcCommand.length()),player,house,gs,th),',',';');
            } else if(hcCommand.indexOf("SOUND")==0) {
                if(!set && buffer==null) {return 0;}
                if(!set) {buffer=obj.sound;return 0;}
                obj.sound=(readString(hcCommand.substring(6,hcCommand.length()),player,house,gs,th));
            } else if(hcCommand.indexOf("ERRMSG")==0) {
                if(!set && buffer==null) {return 0;}
                if(!set) {buffer=obj.errMessage;return 0;}
                obj.errMessage=(readString(hcCommand.substring(7,hcCommand.length()),player,house,gs,th));
            } else if(hcCommand.indexOf("MESSAGE")==0) {
                if(!set && buffer==null) {return 0;}
                if(!set) {buffer=obj.message;return 0;}
                obj.sound=(readString(hcCommand.substring(9,hcCommand.length()),player,house,gs,th));
            } else if(hcCommand.indexOf("ROOM")==0 && !set) {
                return obj.getPart();
            } else if(hcCommand.indexOf("FOLLOW=")==0 && obj instanceof NPC && set) {
                NPC obj2=(NPC)obj;
                String str=readString(hcCommand.substring(7,hcCommand.length()),player,house,gs,th);
                if(str==null) obj2.toFollow=null;
                if(str!=null && str.length()<=0) obj2.toFollow=null;
                if(str!=null && str.length()>0) {
                    String[] ntf=GameIni.cutOnStrings(str,',',';');
                    int[] toFollow=new int[ntf.length];
                    for(int i=0;i<ntf.length;i++) {toFollow[i]=readVar(ntf[i],player,house,gs,th);}
                    obj2.toFollow=toFollow;
                }
            } else if(hcCommand.indexOf("FOLLOWS(")==0 && obj instanceof NPC && !set) {
                NPC obj2=(NPC)obj;
                String[] ntf=GameIni.cutOnStrings(
                        readString(hcCommand.substring(7,hcCommand.length()-1),player,house,gs,th),',',';');
                int[] needToFollow=new int[ntf.length];
                for(int i=0;i<ntf.length;i++) {needToFollow[i]=readVar(ntf[i],player,house,gs,th);}
                return (contains(obj2.toFollow,needToFollow)?1:0);
            } else if(hcCommand.indexOf("SPEED_X")==0 && obj instanceof GameObject) {
                if(!set) return gobj.character.speed.x*(inverse?-1:1);
                if(hcCommand.charAt(7)=='=') { 
                gobj.character.speed.x=readVar(command.substring(8,command.length()),player,house,gs,th);
            } else {     
            int i=readVar(command.substring(9,command.length()),player,house,gs,th);
            if(hcCommand.charAt(7)=='-') i*=-1;
            if(inverse) i*=-1;
            gobj.character.speed.x+=readVar(command.substring(8,command.length()),player,house,gs,th);
            }
            } else if(hcCommand.indexOf("SPEED_Y")==0 && obj instanceof GameObject) {
                if(!set) return gobj.character.speed.y*(inverse?-1:1);
                if(hcCommand.charAt(7)=='=') {
                gobj.character.speed.y=readVar(command.substring(8,command.length()),player,house,gs,th);
            } else {     
            int i=readVar(command.substring(9,command.length()),player,house,gs,th);
            if(hcCommand.charAt(7)=='-') i*=-1;
            if(inverse) i*=-1;
            gobj.character.speed.y+=readVar(command.substring(8,command.length()),player,house,gs,th);
            }
            } else if(hcCommand.indexOf("SPEED_Z")==0 && obj instanceof GameObject) {
                if(!set) return gobj.character.speed.z*(inverse?-1:1);
                if(hcCommand.charAt(7)=='=') {
                gobj.character.speed.z=readVar(command.substring(8,command.length()),player,house,gs,th);
            } else {     
            int i=readVar(command.substring(9,command.length()),player,house,gs,th);
            if(hcCommand.charAt(7)=='-') i*=-1;
            if(inverse) i*=-1;
            gobj.character.speed.z+=readVar(command.substring(8,command.length()),player,house,gs,th);
            }
            } else if(hcCommand.indexOf("ANIMATION_SPEED")==0 && obj instanceof MeshObject) {
                MeshObject mobj=(MeshObject)obj;
                if(!set) return (int)mobj.animSpeed*(inverse?-1:1);
                if(hcCommand.charAt(15)=='=') {
                mobj.animSpeed=readVar(command.substring(16,command.length()),player,house,gs,th);
            } else {     
            int i=readVar(command.substring(17,command.length()),player,house,gs,th);
            if(hcCommand.charAt(15)=='-') i*=-1;
            if(inverse) i*=-1;
            mobj.animSpeed+=readVar(command.substring(16,command.length()),player,house,gs,th);
            }
            } else if(hcCommand.indexOf("ANIMATION_TYPE")==0 && obj instanceof MeshObject) {
                MeshObject mobj=(MeshObject)obj;
                if(!set) return (int)mobj.animSpeed*(inverse?-1:1);
                if(hcCommand.charAt(14)=='=') {
                mobj.animSpeed=readVar(command.substring(15,command.length()),player,house,gs,th);
            } else {     
            int i=readVar(command.substring(16,command.length()),player,house,gs,th);
            if(hcCommand.charAt(14)=='-') i*=-1;
            if(inverse) i*=-1;
            mobj.animSpeed+=readVar(command.substring(15,command.length()),player,house,gs,th);
            }
            }
        
        
        return 0;
    }
    
    
    public static int getSetValueSystem(Player player,House house,GameScreen gs,
            String command,boolean inverse,boolean set,String buffer) {
        
        String hcCommand=command.toUpperCase();
        boolean toggle=false;
        if(hcCommand.indexOf("TOGGLE_")!=-1) {
                toggle=true; 
                hcCommand=hcCommand.substring(7,hcCommand.length());
                command=command.substring(7,command.length());
        }
        
        if(hcCommand.equals("SYMBIAN_DEVICE")) {
                if(!set) return (Main.symbian?!inverse:inverse)?1:0;
                if(!toggle) Main.symbian=!inverse;
                else Main.symbian ^= true;
        }
        
        
        return 0;
    }
    
    
    public static int readVar(String var,Player player,House house,GameScreen gs,RoomObject th) {
        if(var==null) return 0;
        if(var.length()==0) return 0;
        var=var.trim();
        int ind; 
        String hcVar=var.toUpperCase();
        if((ind=var.indexOf('('))>-1)  {
            String s=var.substring(0,ind)
                +readVar(var.substring(ind+1,var.indexOf(')')),player,house,gs,th);
            if(ind+1<var.length()) s=s+var.substring(var.indexOf(')')+1,var.length());
            return readVar(s,player,house,gs,th);
        }
        if((ind=var.indexOf('+'))>0) return readVar(var.substring(0,ind),player,house,gs,th)+readVar(var.substring(ind+1,var.length()),player,house,gs,th);
        if((ind=var.indexOf('-'))>0) return readVar(var.substring(0,ind),player,house,gs,th)-readVar(var.substring(ind+1,var.length()),player,house,gs,th);
        
        if((ind=var.indexOf('*'))>-1) return readVar(var.substring(0,ind),player,house,gs,th)*readVar(var.substring(ind+1,var.length()),player,house,gs,th);
        if((ind=var.indexOf('/'))>-1) return readVar(var.substring(0,ind),player,house,gs,th)/readVar(var.substring(ind+1,var.length()),player,house,gs,th);
        
        if(testNumeric(var)) return StringTools.parseInt(var);
        else if(hcVar.equals("MONEY")) return player.money;
        else if(hcVar.equals("FRAGS")) return player.frags;
        else if(hcVar.indexOf("AMMO_")==0) {
            String tmp=var.substring(5,var.length());
            int weapon=readVar(tmp,player,house,gs,th);
            if(player.arsenal.weapons[weapon]!=null) return player.arsenal.weapons[weapon].magazine.ammo;
            return 0;
        } else if(hcVar.indexOf("RANDOM_")==0) {
            int max=readVar(var.substring(7,var.length()),player,house,gs,th);
            return rand.nextInt(max+1);
        } else if(var.indexOf(':')!=-1) {
            String objname=var.substring(0,var.indexOf(':'));
            String command=var.substring(var.indexOf(':')+1,var.length());
            String hcCommand=command.toUpperCase();
            RoomObject obj=null;
            Vector objs=house.getObjects();
            if(objname.equalsIgnoreCase("player")) obj=player;
            if(objname.equalsIgnoreCase("self")) obj=th;
if(objs.size()>0 && obj==null) {
for(int i=0;i<objs.size();i++) {
    if(((RoomObject)objs.elementAt(i)).name.equals(objname)) {
        obj=((RoomObject)objs.elementAt(i));
        break;
    }
}
}
return getSetValue(player,house,gs,obj,command,false,false,null,th);


        }
        if(containsCollected(Player.usedPoints,var,player,house,gs,th)) return 1;
        return 0;
    }
    
    public static boolean readBoolean(String var,Player player,House house,GameScreen gs,RoomObject th) {
        var=var.trim();
        int ind;
        if((ind=var.indexOf('('))>-1) {
            String s=var.substring(0,ind)
                +readBoolean(var.substring(ind+1,var.indexOf(')')),player,house,gs,th);
            
            if(ind+1<var.length()) s=s+var.substring(var.indexOf(')')+1,var.length());
            boolean bol=readBoolean(s,player,house,gs,th);
            if(ind>=1) if(var.charAt(ind-1)=='!') return !bol;
            return bol;
        }
        
        if((ind=var.indexOf('|'))>-1) return readBoolean(var.substring(0,ind),player,house,gs,th)||readBoolean(var.substring(ind+1,var.length()),player,house,gs,th);
        if((ind=var.indexOf('&'))>-1) return readBoolean(var.substring(0,ind),player,house,gs,th)&&readBoolean(var.substring(ind+1,var.length()),player,house,gs,th);
        
        return containsCollected(Player.usedPoints,var,player,house,gs,th);
    }
    
    public static String readString(String var, Player player, House house, GameScreen gs, RoomObject th) {
        var=var.trim();
        int ind;
        
        if((ind=var.indexOf('['))>-1) {
            String s=var.substring(0,ind)
                +'"'+String.valueOf(readVar(var.substring(ind+1,var.indexOf(']')),player,house,gs,th))+'"';
            if(ind+1<var.length()) s=s+var.substring(var.indexOf(']')+1,var.length());
                
                return readString(s,player,house,gs,th);
        }
        
        if((ind=var.indexOf('('))>-1) {
            String s=var.substring(0,ind)
                +'"'+readString(var.substring(ind+1,var.indexOf(')')),player,house,gs,th)+'"';
                if(ind+1<var.length()) s=s+var.substring(var.indexOf(')')+1,var.length());
            return readString(s,player,house,gs,th);
        }
        
        if((ind=var.indexOf('+'))>-1) {
            if(var.charAt(ind-1)=='"' && var.charAt(ind+1)=='"') {
                return readString(var.substring(0,ind),player,house,gs,th)
                +readString(var.substring(ind+1,var.length()),player,house,gs,th);
            }
        }
            
        if(var.charAt(0)=='"') return var.substring(1,var.length()-1);
        
        return "null";
    }
    
    public static boolean testNumeric(String var) {
        char c=var.charAt(0);
        if(c=='-' || c=='0' || c=='1' || c=='2' || c=='3' || c=='4' || c=='5' || c=='6' || c=='7' || c=='8' || c=='9') return true;
       return false;
    }
    
    public void give(String name,Player player) {
        String newName=name;
        if(newName==null) return;
        boolean inverse=false;
        if(newName.indexOf('!')==0) { inverse=true; newName=newName.substring(1,newName.length());}
if(inverse) Player.usedPoints.removeElement(newName);
if(!inverse) if(!containsSimple(Player.usedPoints,newName)) Player.usedPoints.addElement(newName);
    }

public abstract void activate(House house,Player player,GameScreen gs);


public void errMsg(GameScreen gs) {
    if(errMessageDelay>0) {GameScreen.delayDialogs.addElement(new DelayedDialog(errMessageTimeOut,errMessageType,errMessage,errMessageDelay)); return;}
    if(errMessageType>0) {
gs.customMessage=errMessage;
gs.customMessagePause=false;
gs.customMessageEndTime=System.currentTimeMillis()+errMessageTimeOut;
if(errMessageType==2) {
gs.customMessagePause=true;
gs.customMessageEndTime=errMessageTimeOut;
}
GameScreen.lines.removeAllElements();
TextView.createLines(errMessage, GameScreen.lines, gs.font, GameScreen.width);
    }
if(errMessageType==0) {
gs.showDialog(errMessage);
}


    
}

public void prMsg(GameScreen gs) {
        if(messageDelay>0) {GameScreen.delayDialogs.addElement(new DelayedDialog(messageTimeOut,messageType,message,messageDelay)); return;}
        if(messageType>0) {
gs.customMessage=message;
gs.customMessagePause=false;
gs.customMessageEndTime=System.currentTimeMillis()+messageTimeOut;
if(messageType==2) {
gs.customMessagePause=true;
gs.customMessageEndTime=messageTimeOut;
}
GameScreen.lines.removeAllElements();
TextView.createLines(message, GameScreen.lines, gs.font, GameScreen.width);
}
if(messageType==0) {
gs.showDialog(message);
}
        
}


public void drawDebug(Graphics g,DirectX7 g3d,int yy,boolean canReact,House house) {
    if(activable==false) return;
    if(squarePoint==true) {drawDebugSquare(g,g3d,yy,canReact,house); return;}
    
    vert.set(this.getPosX(),this.getPosY(),this.getPosZ());
    if(pointOffset!=null) vert.set(this.getPosX()+pointOffset.x,this.getPosY()+pointOffset.y,this.getPosZ()+pointOffset.z);
    vert.transform(g3d.getInvCamera());
    vert.project(g3d);
    int px=vert.sx;
    int py=vert.sy;
    
    if(part!=-1) {
    Room rm=house.getRooms()[part];
    if(rm!=null) if(!rm.isOnRoom(px,py)) return;
        }
    
    int rad = pointRadius * g3d.distX; 
    if((-vert.rz + g3d.distX)>0) rad/= (-vert.rz + g3d.distX);
    int height = pointHeight * g3d.distY;
    if((-vert.rz + g3d.distY)>0) height/= (-vert.rz + g3d.distY);
    if(this.dynamicPoint==true) {
        
        if(this instanceof MeshObject) {  
            MeshObject obj=(MeshObject)this;
            obj.boundingBox.isVisible(g3d,  g3d.computeFinalMatrix(obj.getCharacter().getTransform()), 0, 0, g3d.width, g3d.height);
            vert.sx=(obj.boundingBox.getMaxX()+obj.boundingBox.getMinX())/2;
            vert.sy=(obj.boundingBox.getMaxY()+obj.boundingBox.getMinY())/2;
            rad=(obj.boundingBox.getMaxX()-obj.boundingBox.getMinX())/2;
            height=(obj.boundingBox.getMaxY()-obj.boundingBox.getMinY())/2;
        } else if(this instanceof NPC) {
            NPC obj=(NPC)this;
            if(obj.meshImage!=null) {
            obj.boundingBox.isVisible(g3d,  g3d.computeFinalMatrix(obj.getCharacter().getTransform()), 0, 0, g3d.width, g3d.height);
            vert.sx=(obj.boundingBox.getMaxX()+obj.boundingBox.getMinX())/2;
            vert.sy=(obj.boundingBox.getMaxY()+obj.boundingBox.getMinY())/2;
            rad=(obj.boundingBox.getMaxX()-obj.boundingBox.getMinX())/2;
            height=(obj.boundingBox.getMaxY()-obj.boundingBox.getMinY())/2;
            } else {
                if(obj.currentSprite==null) return;
                obj.currentSprite.project(g3d.getInvCamera(), g3d);
                vert.sx=(obj.currentSprite.pos.sx+obj.currentSprite.size.sx)/2;
                vert.sy=(obj.currentSprite.pos.sy+obj.currentSprite.size.sy)/2;
                rad=(obj.currentSprite.size.sx-obj.currentSprite.pos.sx)/2;
                height=(obj.currentSprite.size.sy-obj.currentSprite.pos.sy)/2;
            }
        } else if(this instanceof SpriteObject) {
            SpriteObject obj=(SpriteObject)this;
            obj.spr.project(g3d.getInvCamera(), g3d);
            vert.sx=(obj.spr.pos.sx+obj.spr.size.sx)/2;
            vert.sy=(obj.spr.pos.sy+obj.spr.size.sy)/2;
            rad=(obj.spr.size.sx-obj.spr.pos.sx)/2;
            height=(obj.spr.size.sy-obj.spr.pos.sy)/2;
        }
        
    }
    if(vert.rz < 0) {
        g.setColor(0,0,0);
    g.fillArc(px-3, py-3+yy, 7, 7, 0, 360);
    
    g.setColor(255,0,0);
    int xx=(vert.sx-g3d.width/2);
    xx*=xx;
    int yyy=(vert.sy-g3d.height/2);
    if(height==0) height=1;
    yyy=(yyy*yyy)*rad/height;
    
    if(xx+yyy<rad*rad && canReact==true) g.setColor(0,255,0);
    
    if(needToPoint==false) g.setColor(255,255,255);
    g.fillArc(px-2, py-2+yy, 5, 5, 0, 360);
    if(needToPoint==true) g.drawArc(vert.sx-rad, vert.sy-height+yy, rad*2, height*2, 0, 360);
    if(needToPoint==true && this instanceof Bot) g.drawArc(vert.sx-rad/2, vert.sy-height+yy, rad, height*2/5, 0, 360);
    printName(g,px,py+yy);

    
    
    g.setColor(255,255,255);
            }
}

public void drawDebugSquare(Graphics g,DirectX7 g3d,int yy,boolean canReact,House house) {
    if(activable==false) return;
    
    vert.set(this.getPosX(),this.getPosY(),this.getPosZ());
    if(pointOffset!=null) vert.set(this.getPosX()+pointOffset.x,this.getPosY()+pointOffset.y,this.getPosZ()+pointOffset.z);
    vert.transform(g3d.getInvCamera());
    vert.project(g3d);
        int px=vert.sx;
    int py=vert.sy;
    
        if(part!=-1) {
    Room rm=house.getRooms()[part];
    if(rm!=null) if(!rm.isOnRoom(px,py)) return;
        }
        
    int rad = pointRadius * g3d.distX / (-vert.rz + g3d.distX);
    int height = pointHeight * g3d.distY / (-vert.rz + g3d.distY);
        if(this.dynamicPoint==true) {
        
        if(this instanceof MeshObject) {
            MeshObject obj=(MeshObject)this;
            obj.boundingBox.isVisible(g3d,  g3d.computeFinalMatrix(obj.getCharacter().getTransform()), 0, 0, g3d.width, g3d.height);
            vert.sx=(obj.boundingBox.getMaxX()+obj.boundingBox.getMinX())/2;
            vert.sy=(obj.boundingBox.getMaxY()+obj.boundingBox.getMinY())/2;
            rad=(obj.boundingBox.getMaxX()-obj.boundingBox.getMinX())/2;
            height=(obj.boundingBox.getMaxY()-obj.boundingBox.getMinY())/2;
        } else if(this instanceof NPC) {
            NPC obj=(NPC)this;
            if(obj.meshImage!=null) {
            obj.boundingBox.isVisible(g3d,  g3d.computeFinalMatrix(obj.getCharacter().getTransform()), 0, 0, g3d.width, g3d.height);
            vert.sx=(obj.boundingBox.getMaxX()+obj.boundingBox.getMinX())/2;
            vert.sy=(obj.boundingBox.getMaxY()+obj.boundingBox.getMinY())/2;
            rad=(obj.boundingBox.getMaxX()-obj.boundingBox.getMinX())/2;
            height=(obj.boundingBox.getMaxY()-obj.boundingBox.getMinY())/2;
            } else {
                if(obj.currentSprite==null) return;
                obj.currentSprite.project(g3d.getInvCamera(), g3d);
                vert.sx=(obj.currentSprite.pos.sx+obj.currentSprite.size.sx)/2;
                vert.sy=(obj.currentSprite.pos.sy+obj.currentSprite.size.sy)/2;
                rad=(obj.currentSprite.size.sx-obj.currentSprite.pos.sx)/2;
                height=(obj.currentSprite.size.sy-obj.currentSprite.pos.sy)/2;
            }
        } else if(this instanceof SpriteObject) {
            SpriteObject obj=(SpriteObject)this;
            obj.spr.project(g3d.getInvCamera(), g3d);
            vert.sx=(obj.spr.pos.sx+obj.spr.size.sx)/2;
            vert.sy=(obj.spr.pos.sy+obj.spr.size.sy)/2;
            rad=(obj.spr.size.sx-obj.spr.pos.sx)/2;
            height=(obj.spr.size.sy-obj.spr.pos.sy)/2;
        }
        
    }
    if(vert.rz < 0) {
g.setColor(0,0,0);
    g.fillArc(px-3, py-3+yy, 7, 7, 0, 360);

    g.setColor(255,0,0);
    int xx=(vert.sx-g3d.width/2);
    xx*=xx;
    int yyy=(vert.sy-g3d.height/2);
    yyy*=yyy;
    if(xx<rad*rad && yyy<height*height && canReact==true) g.setColor(0,255,0);
    if(needToPoint==false) g.setColor(255,255,255);
    g.fillArc(px-2, py-2+yy, 5, 5, 0, 360);
    if(needToPoint==true) g.drawRect(vert.sx-rad, vert.sy-height+yy, rad*2, height*2);
    if(needToPoint==true && this instanceof Bot) g.drawRect(vert.sx-rad/2, vert.sy-height+yy, rad, height*2/5);
printName(g,px,py+yy);
    g.setColor(255,255,255);
            }
}

private void printName(Graphics g,int px,int py) {
    
            if(name!=null) {
        int col=g.getColor();
        g.setColor(0);
        g.drawString(name,px+5,py-5,Graphics.BASELINE|Graphics.LEFT);
        g.drawString(name,px+4,py-5,Graphics.BASELINE|Graphics.LEFT);
        g.drawString(name,px+6,py-5,Graphics.BASELINE|Graphics.LEFT);
        g.drawString(name,px+4,py-4,Graphics.BASELINE|Graphics.LEFT);
        g.drawString(name,px+6,py-4,Graphics.BASELINE|Graphics.LEFT);
        g.drawString(name,px+4,py-6,Graphics.BASELINE|Graphics.LEFT);
        g.drawString(name,px+6,py-6,Graphics.BASELINE|Graphics.LEFT);
        g.setColor(col);
        g.drawString(name,px+5,py-5,Graphics.BASELINE|Graphics.LEFT);
    }
    
}
public boolean check(DirectX7 g3d,int yy) {
    if(activable==false) return false;
    if(needToPoint==false) return true;
    vert.set(this.getPosX(),this.getPosY(),this.getPosZ());
    if(pointOffset!=null) vert.set(this.getPosX()+pointOffset.x,this.getPosY()+pointOffset.y,this.getPosZ()+pointOffset.z);
    
    vert.transform(g3d.getInvCamera());
    vert.project(g3d);
    
    int rad = pointRadius * g3d.distX; 
    if((-vert.rz + g3d.distX)>0) rad/= (-vert.rz + g3d.distX);
    int height = pointHeight * g3d.distY;
    if((-vert.rz + g3d.distY)>0) height/= (-vert.rz + g3d.distY);
            if(this.dynamicPoint==true) {
        
        if(this instanceof MeshObject) {
            MeshObject obj=(MeshObject)this;
            obj.boundingBox.isVisible(g3d,  g3d.computeFinalMatrix(obj.getCharacter().getTransform()), 0, 0, g3d.width, g3d.height);
            vert.sx=(obj.boundingBox.getMaxX()+obj.boundingBox.getMinX())/2;
            vert.sy=(obj.boundingBox.getMaxY()+obj.boundingBox.getMinY())/2;
            rad=(obj.boundingBox.getMaxX()-obj.boundingBox.getMinX())/2;
            height=(obj.boundingBox.getMaxY()-obj.boundingBox.getMinY())/2;
        } else if(this instanceof NPC) {
            NPC obj=(NPC)this;
            if(obj.meshImage!=null) {
            obj.boundingBox.isVisible(g3d,  g3d.computeFinalMatrix(obj.getCharacter().getTransform()), 0, 0, g3d.width, g3d.height);
            vert.sx=(obj.boundingBox.getMaxX()+obj.boundingBox.getMinX())/2;
            vert.sy=(obj.boundingBox.getMaxY()+obj.boundingBox.getMinY())/2;
            rad=(obj.boundingBox.getMaxX()-obj.boundingBox.getMinX())/2;
            height=(obj.boundingBox.getMaxY()-obj.boundingBox.getMinY())/2;
            } else {
                if(obj.currentSprite==null) return false;
                obj.currentSprite.project(g3d.getInvCamera(), g3d);
                vert.sx=(obj.currentSprite.pos.sx+obj.currentSprite.size.sx)/2;
                vert.sy=(obj.currentSprite.pos.sy+obj.currentSprite.size.sy)/2;
                rad=(obj.currentSprite.size.sx-obj.currentSprite.pos.sx)/2;
                height=(obj.currentSprite.size.sy-obj.currentSprite.pos.sy)/2;
            }
        } else if(this instanceof SpriteObject) {
            SpriteObject obj=(SpriteObject)this;
            obj.spr.project(g3d.getInvCamera(), g3d);
            vert.sx=(obj.spr.pos.sx+obj.spr.size.sx)/2;
            vert.sy=(obj.spr.pos.sy+obj.spr.size.sy)/2;
            rad=(obj.spr.size.sx-obj.spr.pos.sx)/2;
            height=(obj.spr.size.sy-obj.spr.pos.sy)/2;
        }
        
    }
    if(vert.rz < 0) {

        if(squarePoint==true) {
    int xx=(vert.sx-g3d.width/2);
    xx*=xx;
    int yyy=(vert.sy-g3d.height/2);
    yyy*=yyy;
    if(xx<rad*rad && yyy<height*height) return true;
        }
        else
        {
    int xx=(vert.sx-g3d.width/2);
    xx*=xx;
    int yyy=(vert.sy-g3d.height/2);
    if(height==0) height=1;
    yyy=(yyy*yyy)*rad/height;
    if(xx+yyy<rad*rad) return true;
        }
            }
    return false;
}
  
}
