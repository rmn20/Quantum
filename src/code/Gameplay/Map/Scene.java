package code.Gameplay.Map;
import code.Gameplay.Objects.GameObject;
import code.AI.Zombie;
import code.AI.Bot;
import code.AI.NPC;
import code.AI.misc.Corpse;
import code.AI.BigZombie;
import code.Math.Matrix;
import code.AI.Player;
import code.Gameplay.GameScreen;
import code.Gameplay.Objects.ItemsBag;
import code.Gameplay.Objects.NPCSpawner;
import code.Rendering.DirectX7;
import code.Math.Vector3D;
import code.Gameplay.Respawn;
import code.HUD.DeveloperMenu;
import code.Rendering.Meshes.Sprite;
import code.utils.FPS;
import code.utils.Main;
import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;

public class Scene {

   private final Random random;
   private Vector respawns;
   public String[] need=null;
   private Bot[] bots;
   private int frame;
   public boolean exitWithoutWait;
   public int[] botLimiter;
   public DirectX7 g3d;
   public House house;
   private int miny;
   private Respawn start;
   private Vector tVec=new Vector();
   private Respawn finish;
   private Respawn[] enemies; // респауны ботов
   private int frequency;
   private int max_enemy_count; // Максимально возможное кол-во ботов на сцене
   private int enemy_count; // Кол-во добавленных ботов
   private int part;
   public boolean deleteAnPart;
   public boolean alwaysExit;
   public static int gravity=-20;
   public Vector rmsBots;
   public boolean[] rmsBotsKilled;
   public Vector rmsObjects;
   public boolean[] rmsObjectsDestroyed;
   public Dijkstra ways;
   
   public Vector scriptsToExec;

    public Scene(int width, int height, House house, Respawn start, Respawn finish, Respawn[] enemies, int max_enemy_count, int frequency, Main main, int zombie_count, boolean genWays) {
        alwaysExit = false;
        random = new Random();
        respawns = new Vector();
        scriptsToExec = new Vector();
        frame = 0;
        need = null;
        g3d = new DirectX7(width, height);
        this.house = house;
        this.start = start;
        this.finish = finish;
        this.enemies = enemies;
        this.max_enemy_count = max_enemy_count;
        this.frequency = frequency;
        countWorldSize(house);
        if(genWays) ways = new Dijkstra(house);
        
        if (max_enemy_count < zombie_count) zombie_count = max_enemy_count;
        if (zombie_count < 1) zombie_count = 1;
        
        bots = new Bot[zombie_count];
        bots[0] = new BigZombie(new Vector3D());

        exitWithoutWait = false;
        if (bots.length > 1) {
            for (width = 1; width < bots.length; ++width) {
                bots[width] = new Zombie(new Vector3D());
            }
        }
        
        botLimiter = null;
   }

   public final void reset() {
      Vector var1 = house.getObjects();
      int var2 = 0;
      if(rmsBots!=null) rmsBots.removeAllElements();
      if(rmsObjects!=null) rmsObjects.removeAllElements();

      while(var2 < var1.size()) {
      house.removeObject((RoomObject)var1.elementAt(var2));
      var2++;
      }
     var2 = 0;
if(enemies!=null) {
      while(var2 < enemies.length) {
                  Respawn resp=enemies[var2];
if(resp.mode>=-127) resp.mode=-127;
var2++;
      }
}
      this.frame = 0;
      this.enemy_count = 0;
      this.part = -1;
   }


   public final void destroy() {
      g3d.destroy();
      g3d = null;
      house.destroy();
      scriptsToExec.removeAllElements();
      scriptsToExec = null;
      house = null;
      need = null;
       if (bots.length > 0) {
           for (int i = 0; i < bots.length; ++i) {
               bots[i].destroy();
               bots[i] = null;
           }
       }
      bots = null;
      botLimiter=null;
      if(rmsBots!=null) rmsBots.removeAllElements();
      if(rmsObjects!=null) rmsBots.removeAllElements();
      rmsBots=rmsObjects=null;
   }

   private void countWorldSize(House house) {
      int var2 = Integer.MAX_VALUE;
      int var3 = Integer.MIN_VALUE;
      int var4 = Integer.MAX_VALUE;
      int var5 = Integer.MIN_VALUE;
      int var6 = Integer.MAX_VALUE;
      int var7 = Integer.MIN_VALUE;
      Room[] var11 = house.getRooms();

      for(int var8 = 0; var8 < var11.length; ++var8) {
         Room var9 = var11[var8];
         var2 = Math.min(var2, var9.getMinX());
         var4 = Math.min(var4, var9.getMinY());
         var6 = Math.min(var6, var9.getMinZ());
         var3 = Math.max(var3, var9.getMaxX());
         var5 = Math.max(var5, var9.getMaxY());
         var7 = Math.max(var7, var9.getMaxZ());
      }

      System.out.println("World size:");
      System.out.println("Size X " + (var3 - var2));
      System.out.println("Size Y " + (var5 - var4));
      System.out.println("Size Z " + (var7 - var6));
      this.miny = var4;
   }

   private int random(int x) {
      return Math.abs(this.random.nextInt()) % x;
   }

   public final Vector3D getStartPoint() {
      if(this.start==null) return null;
      return this.start.point;
   }
   public final Vector3D getFinishPoint() {
       if(this.finish==null) return null;
      return this.finish.point;
   }

   // добавляет в Vector respawns респауны ботов из комнаты (?) под номером part 
   private void addRespawn(int part, Vector respawns) {
       if(enemies!=null) {
               for(int var3 = 0; var3 < this.enemies.length; ++var3) {
         Respawn var4;
         if((var4 = this.enemies[var3]).part == part) {
if(var4.mode==-128 || var4.mode<var4.cmode)
{
            respawns.addElement(var4);
}
         }
else
{
if(this.enemies[var3].respa==true) this.enemies[var3].mode=-127;
}
      }
       }
      

   }

   public final int render(Graphics g,int[] parts,Player player) {
      int count = house.render(g,g3d,parts,player.getCharacter().getTransform().m03,player.getCharacter().getTransform().m23);

      return count;
   }
   
   public final int render(Graphics g,int part,Player player) {
      int count = house.render(g,g3d,part,player.getCharacter().getTransform().m03,player.getCharacter().getTransform().m23);

      return count;
   }

   public final void flush(Graphics g, int x, int y) {
      g3d.flush(g, 0, y);
   }

   public final void update(Player player, GameScreen gs) {
       
       while(!scriptsToExec.isEmpty()) {
           Object[] mass = (Object[])scriptsToExec.elementAt(0);
           if(mass[0]!=null) {
               ((RoomObject)mass[0]).give((String[])mass[1], player, house, gs);
           }
           scriptsToExec.removeElementAt(0);
       }
       
       
      int playerPart = player.getPart();
      Vector objects;
      if(Main.updateOnlyNear) objects= house.getNearObjects(player.getPart());
      else objects= house.getObjects();
      int i;
      GameObject obj;
      int tmppart;
      
      // Уборка ботов из дальних комнат и провалившихся ботов 
      if(this.frame % 5 == 0) { // 5
         for(i = 0; i < objects.size(); ++i) {
if(objects.elementAt(i) instanceof GameObject) {
            obj = (GameObject)objects.elementAt(i);

            tmppart = obj.getPart();
            
            // Проверка, в той же комнате или в соседней находится игрок
            boolean near;

            if(tmppart != -1 && playerPart != -1) {
               if(playerPart == tmppart) {
                  near = true;
               } else {
                  Room[] var12 = house.getNeighbourRooms(playerPart);
                  int i2 = 0;

                  while(true) {
                     if(i2 >= var12.length) {
                        near = false;
                        break;
                     }

                     if(var12[i2].getId() == tmppart) {
                        near = true;
                        break;
                     }

                     ++i2;
                  }
               }
            } else {
               near = false;
            }


if(!(obj instanceof Zombie || obj instanceof BigZombie) || deleteAnPart==false) near=true;

            // Если игрок и бот в разных комнатах, или бот провалился, то убрать его
            if(!near || obj.getPart() == -1 || obj.getCharacter().getTransform().m13 < miny << 1) {

               if((obj instanceof Zombie || obj instanceof BigZombie)) {
if(!obj.isDead()) {
--enemy_count;
house.removeObject(obj);
}
}
else
{
if(!(obj instanceof Player)) house.removeObject(obj);
}
            }
         }
}
      }

      i = 0;
if(Main.updateOnlyNear) objects= house.getNearObjects(player.getPart());
      else objects= house.getObjects();
      // Уборка трупов, если подошло убирать
      while(i < objects.size()) {
          if(objects.elementAt(i) instanceof GameObject) {
         if((obj = (GameObject)objects.elementAt(i)).isTimeToRenew()) {

if(Main.corpses) {
if(obj instanceof Zombie)
{
Zombie zom=(Zombie)obj;
Corpse corpse=new Corpse(zom.meshImage.getAnimation().getFrame(),zom.getCharacter().getTransform(),zom.meshImage,Zombie.texture);
house.addObject(corpse);
}
if(obj instanceof BigZombie)
{
BigZombie zom=(BigZombie)obj;
Corpse corpse=new Corpse(zom.meshImage.getAnimation().getFrame(),zom.getCharacter().getTransform(),zom.meshImage,BigZombie.texture);
house.addObject(corpse);
}
if(obj instanceof NPC)
{
NPC npc=(NPC)obj;
if(npc.currentSprite!=null)
{
Corpse corpse=new Corpse(0,npc.getCharacter().getTransform(),null,null);
corpse.spr=new Sprite(npc.currentSprite.textures[npc.currentSprite.textures.length-1],npc.currentSprite.scale);
corpse.spr.mirX=npc.currentSprite.mirX;
house.addObject(corpse);
}
else
{
if(npc.meshImage!=null) {
    Corpse corpse=new Corpse(npc.meshImage.getAnimation().getFrame(),npc.getCharacter().getTransform(),npc.meshImage,npc.mt);
    house.addObject(corpse);    
}
}
}
}


            house.removeObject(obj);
            
         }
         }
            ++i;
         
      }
      
if(Main.updateOnlyNear) objects= house.getNearObjects(player.getPart());
      else objects= house.getObjects();

      // Расстановка ботов
      if(this.part != playerPart || this.frame % this.frequency == 0) {
         int px = player.getPosX();
         int pz = player.getPosZ();
         int rad = player.getCharacter().getRadius() * 7;
         
         // Если кол-во добавленных ботов < макс. кол-ва ботов, то 
         if(this.enemy_count < this.max_enemy_count) {

            for(int i2 = 0; i2 < bots.length && enemy_count < max_enemy_count; ++i2) {
               Bot bot = bots[i2];
               if((!(bot instanceof BigZombie) || random(8) == 0) && !objects.contains(bot)) {
                  Respawn respawn;
                  if(playerPart == -1) {
                     respawn = null;
                  } else {
                     respawns.removeAllElements();
                     // Расстановка респаунов ботов в той комнате, где нажодится игрок
                     addRespawn(playerPart, respawns);
                     Room[] rooms = house.getNeighbourRooms(playerPart);

                     // Расстановка респаунов ботов в соседних от игрока комнатах
                     if(rooms!=null && rooms.length>0) {
                     for(int i3 = 0; i3 < rooms.length; ++i3) {
                        if(rooms[i3]!= null)addRespawn(rooms[i3].getId(), respawns);
                     }
                     }

                     // Выбор рандомного респауна
                     respawn = respawns.isEmpty()?null:(Respawn)respawns.elementAt(random(respawns.size()));
                  }

                  // Если респаун не нулевой, 
                  // тогда, если крадрат растояния от игрока до респауна > (радиус_игрока*7)^2,
                  // то добавить бота в комнату
                  if(respawn != null) {
                     if(respawn.mode==-128 || respawn.mode<respawn.cmode)
{
                     Vector3D point = respawn.point;
                     int z = point.z;
                     int x = point.x;
                     x = px - x;
                     z = pz - z;
                     if(z * z + x * x > rad * rad) {

                        bot.set(point);
   if(respawn.mode>-128 && respawn.cmode<127)
{
respawn.mode++;
}
house.addObject((RoomObject)bot);
                        this.enemy_count++;

}
                     }

                  }
               }
            }
         }

         this.part = playerPart;
      }

      recomputePart(player, gs);
      ++this.frame;
   }
   
   
   public final void recomputePart(Player player, GameScreen gs) {
        
        house.recomputePart(player);//Recompute part for player
        Vector objects;
        if (Main.updateOnlyNear) objects = house.getNearObjects(player.getPart());
        else objects = house.getObjects();

        if (objects.size() > 0) {

            // recomputePart
            for (int i=0; i<objects.size(); i++) {
                RoomObject ob = (RoomObject) objects.elementAt(i);

                if (!(ob instanceof Player)) house.recomputePart(ob); //Recompute part for every object except player
                Vector nearObjects = null;
                if(Main.updateOnlyNearPhysics) nearObjects = house.getNearObjects(ob.getPart()); //Get near objects list for far objects test
                
                // collisionTest

                if (objects.elementAt(i) instanceof GameObject) {
                    GameObject obj = (GameObject) objects.elementAt(i);
                    
                    for (int i2 = i + 1; i2 < objects.size(); ++i2) {
                        if (objects.elementAt(i2) instanceof GameObject) {
                            GameObject obj2 = (GameObject) objects.elementAt(i2);
                            
                            if(Main.updateOnlyNearPhysics && !nearObjects.contains(obj2)) continue; //Skip far objects
                            
                            if(obj2!=obj) Character.collisionTest(obj2.getCharacter(), obj.getCharacter());
                        }

                    }

                    // update + gravity
                    Vector3D speed = obj.getCharacter().getSpeed();
                    if (obj.getPart() != -1 && obj.getCharacter().isCollider() && !(obj instanceof Player && DeveloperMenu.debugMode && obj.character.fly)) {
                        speed.y += gravity * FPS.frameTime / 50;
                    }
                    obj.update(this, player);
                } else if (objects.elementAt(i) instanceof NPCSpawner) {
                    ((NPCSpawner) objects.elementAt(i)).update(this, player, gs);
                }
            }

        }
    }

   public final House getHouse() {
      return this.house;
   }

   public final DirectX7 getG3D() {
      return this.g3d;
   }

   public final int getFrame() {
      return this.frame;
   }

   public final int getEnemyCount() {
      return this.max_enemy_count;
   }

   public final boolean isLevelCompleted(Player player) {
      if(this.finish==null) return false;
      
      Character var3;
      Matrix var4 = (var3 = player.getCharacter()).getTransform();   
      Vector3D var2 = this.finish.point;
      int var10000 = var4.m03;
      int var10001 = var4.m13;
      int var10002 = var4.m23;
      int var10003 = var2.x;
      int var10004 = var2.y;
      int var10005 = var2.z;
      int var8 = var3.getRadius()*2;
      int var7 = var10005;
      int var6 = var10004;
      int var5 = var10003;
      int var11 = var10002;
      int var10 = var10001;
      int var9 = var10000;
      var9 = var5 - var9;
      var10 = var6 - var10;
      var11 = var7 - var11;
      return (Math.abs(var9) <= var8 && Math.abs(var10) <= var8 && Math.abs(var11) <= var8?var9 * var9 + var10 * var10 + var11 * var11 <= var8 * var8:false) && this.isWinner(player);
   }

   public final boolean isWinner(Player player) {
if(!isAllCollected(Player.usedPoints)) return false;
if(player.frags >= this.max_enemy_count) return true;
if(alwaysExit==true) return true;
return false;
}
    public boolean isAllCollected(Vector points) {
        if(need == null) return true;
        for (int i = 0; i < need.length; i++) {
            if( !contains( points, need[i] ) ) return false;
        }
        return true;
    }
    public boolean contains(Vector points, String name) {
        for (int i = 0; i < points.size(); i++) {
            String pName = ((String)points.elementAt(i));
            if( pName != null && pName.equals( name ) ) return true;
        }
        return false;
    }



public RoomObject findObject(boolean isclickable2,Player player,int yy,GameScreen gs) {
Vector objs;
if(Main.updateOnlyNear) objs= house.getNearObjects(player.getPart());
      else objs= house.getObjects();

for(int i=0;i<objs.size();i++) {

RoomObject obj=(RoomObject)objs.elementAt(i);

if(obj.activable==true && obj.clickable==isclickable2)
{
if(distance(obj,(RoomObject)player)<=obj.radius*obj.radius && (obj.clickable==true || obj.near==false) && (obj.activated==false || obj.singleUse==false) && obj.check(g3d,yy))
{
obj.near=true;
boolean ret=true;
if(obj.need!=null) if(!obj.isAllCollected(Player.usedPoints,player,house,gs) && obj.errMessage==null) ret=false;
if(ret && !(obj.lastActivate>0 && (obj.lastActivate+obj.timeToReset>GameScreen.time))) return obj;
}
if(distance(obj,(RoomObject)player)>obj.radius*obj.radius)
{
obj.near=false;
}

}
}

return null;
}

public RoomObject activateObject(boolean isclickable2,Player player,int yy,GameScreen gs) {
Vector objs;
if(Main.updateOnlyNear) objs= house.getNearObjects(player.getPart());
      else objs= house.getObjects();
RoomObject used=null;
for(int i=0;i<objs.size();i++) {

RoomObject obj=(RoomObject)objs.elementAt(i);

if(obj.activable==true && obj.clickable==isclickable2)
{
if(distance(obj,(RoomObject)player)<=obj.radius*obj.radius && (obj.clickable==true || (obj.near==false||obj.alwaysActivate)) && (obj.activated==false || obj.singleUse==false) && obj.check(g3d,yy))
{
obj.near=true;
boolean ret=true;
if(obj.need!=null) if(!obj.isAllCollected(Player.usedPoints,player,house,gs) && obj.errMessage==null) ret=false;
if(ret && !(obj.lastActivate>0 && (obj.lastActivate+obj.timeToReset>GameScreen.time))) { 
obj.activate(this.house,player,gs);
used=obj;
if(obj.activateOnlyOne) return used;
}
}
if(distance(obj,(RoomObject)player)>obj.radius*obj.radius)
{
obj.near=false;
}

}
}

return used;
}

    public void deleteUsedObjects(Player player) {
        Vector objs = house.getObjects();
        for(int i = 0; i < objs.size(); i++) {

            RoomObject obj = (RoomObject) objs.elementAt(i);

            if(RoomObject.containsSimple(Player.usedPoints, obj.name) && obj.reloadDestroy && obj.destroyOnUse) {
                house.removeObject(obj);
            }
        }
        
        if(rmsObjectsDestroyed != null && rmsObjects != null) {
            if(!rmsObjects.isEmpty()) {

                for(int i = 0; i < rmsObjectsDestroyed.length; i++) {
                    if(rmsObjectsDestroyed[i] && rmsObjectsDestroyed.length <= rmsObjects.size()) {
                        RoomObject obj = (RoomObject) rmsObjects.elementAt(i);
                        house.removeObject(obj);
                    }
                }

            }
        }
    }

public void drawPoints(Graphics g,int yy,Player player) {
Vector objs = house.getNearObjects(player.getPart());
for(int i=0;i<objs.size();i++) {
RoomObject obj=(RoomObject)objs.elementAt(i);
long dist=distance(obj,(RoomObject)player);
if(dist<1600000000 && house.isNear(obj.getPart(),player.getPart())) obj.drawDebug(g, g3d, yy,dist<=obj.radius*obj.radius,house);


}
}



    public final long distance(RoomObject obj1, RoomObject obj2) {
        int var3 = obj1.getPosX() - obj2.getPosX();
        int var4 = obj1.getPosY() - obj2.getPosY();
        int var6 = obj1.getPosZ() - obj2.getPosZ();
        return (long) var3 * (long) var3 + (long) var4 * (long) var4 + (long) var6 * (long) var6;
    }
    
    
    public void removeKilledBots() {

        if (rmsBotsKilled != null && rmsBots != null) {
            if (!rmsBots.isEmpty()) {

                for (int i = 0; i < rmsBotsKilled.length; i++) {
                    if (rmsBotsKilled[i] && rmsBotsKilled.length <= rmsBots.size()) {
                        RoomObject obj = (RoomObject) rmsBots.elementAt(i);
                        house.removeObject(obj);
                    }
                }

            }
        }
    }
    
    
    public int getNext(int start, int finish) {
        if(ways==null) return finish;
        return ways.getNext(start, finish);
    }
    
    public Vector getItemBags() {
        Vector items = new Vector();
        
        Vector objs = house.getObjects();
        
        for(int i=0;i<objs.size();i++) {
            RoomObject obj = (RoomObject)objs.elementAt(i);
            
            if(obj instanceof ItemsBag) items.addElement((ItemsBag)obj);
        }
        
        return items;
    }
    
    public Player findPlayer() {
        Vector objects = house.getObjects();
        
        for(int i=0; i<objects.size(); i++) {
            RoomObject obj = (RoomObject) objects.elementAt(i);
            
            if(obj instanceof Player) return (Player)obj;
        }
        
        return null;
    }
    
    
    public void dropItem(String name, int count, Player player) {
        dropItem(name, count, player.getPosX(), player.getPosY(), player.getPosZ(), player.getPart());
    }
    
    public void dropItem(String name, int count, int x, int y, int z) {
        dropItem(name, count, x, y, z, -1);
    }
    
    public void dropItem(String name, int count, int x, int y, int z, int part) {
        if(count <= 0) return;
        
        Vector objs = house.getObjects();
        ItemsBag bag = null;
        
        for(int i=0;i<objs.size();i++) { //Search for nearest bag
            RoomObject obj = (RoomObject)objs.elementAt(i);
            int dist = (x-obj.getPosX())*(x-obj.getPosX()) + (y-obj.getPosY())*(y-obj.getPosY()) + (z-obj.getPosZ())*(z-obj.getPosZ());
            if(dist>800*800) continue; //Skip bag if it too far
            
            if(obj instanceof ItemsBag) {
                bag = (ItemsBag)obj; break;
            }
        }
        
        if(bag==null) {
            bag = new ItemsBag(new Vector3D(x,y,z));
            house.addObject(bag);
        }
        
        bag.items.addItem(name, count);
    }

    public void runScript(Object[] obj) {
        scriptsToExec.addElement(obj);
    }

}

