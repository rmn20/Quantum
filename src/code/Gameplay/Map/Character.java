package code.Gameplay.Map;
import code.Collision.Height;
import code.HUD.DeveloperMenu;
import code.Math.MathUtils2;
import code.Math.Matrix;
import code.Rendering.RenderObject;
import code.Math.Vector3D;
import code.utils.FPS;

public final class Character {
   private Matrix matrix =new Matrix();
   private static Vector3D tmpVec = new Vector3D(); // ? collisionPoint из Ray
   private int player_radius; // ?
   private int player_height; // ?
   public Matrix transform = new Matrix();
   public Vector3D speed = new Vector3D();
   public Vector3D speedNoFriction = new Vector3D();
   public boolean onFloor;
   private boolean colz = true;
   private boolean colz_2 = true;
   private boolean col; // col и� Character.collisionTest(Home home)
   private boolean updatable = true;
   public boolean underRoof; // Под крышей
   private Height height = new Height();
   public RenderObject oldFloorPoly;
   private int oldFloorCx, oldFloorCz, oldFloorCy;
   
   public boolean fly = false;




   public Character(int radius, int height) {
      this.reset();
      this.set(0, 0);
   }

   public final void set(int radius, int height) {
      this.player_radius = radius;
      this.player_height = height;
   }
   public final void set(int radius,int radiusz, int height) {
      this.player_radius = radius;
      this.player_height = height;
   }

public final void splitRadHeight() {
int tmp= this.player_radius;
      this.player_radius = this.player_height;
      this.player_height = tmp;   }


   public final void reset() {
      onFloor = col = false;
      speed.set(0, 0, 0);
      transform.setIdentity();
   }

   public final void collisionTest(int part, House house) {
        if(!colz || (fly && DeveloperMenu.fly==2) ) return;

        tmpVec.set(transform.m03, transform.m13 + player_height, transform.m23);
        col = house.sphereCast(part, tmpVec, player_radius);
        int oldPosX=transform.m03;
        int oldPosZ=transform.m23;
        if(col) {
            transform.m03 = tmpVec.x;
            transform.m13 = tmpVec.y-player_height;
            transform.m23 = tmpVec.z;
        }
        
        height.setUnderRoof(false);
        height.getPosition().set(transform.m03, transform.m13+this.player_height, transform.m23);
        height.setHeight( transform.m13 );
        height.setPolygon( null );
        house.computeHeightFull(part, height);
        underRoof = height.isUnderRoof();
        
        RenderObject floorPoly = height.getPolygon();
        onFloor = floorPoly!=null?floorPoly.ny<=-2048:false;
        
        tmpVec.set(transform.m03, height.getHeight() + player_height, transform.m23);
        if(onFloor) {
            if(oldFloorPoly == floorPoly) {
                transform.m03 += height.getCentreX() - oldFloorCx;
                transform.m13 += height.getCentreY() - oldFloorCy;
                transform.m23 += height.getCentreZ() - oldFloorCz;
            }
            transform.m13 = height.getHeight();
            oldFloorCx = height.getCentreX();
            oldFloorCz = height.getCentreZ();
            oldFloorCy = height.getCentreY();
        }
        
        oldFloorPoly = floorPoly;
        if(!height.updatePosition()) oldFloorPoly=null;
   }

   // ? расстояние до другого персонажа
   public final long distance(Character character) {
      Matrix var2 = this.transform;
      Matrix var5 = character.transform;
      int var3 = var2.m03 - var5.m03;
      int var4 = var2.m13 - var5.m13;
      int var6 = var2.m23 - var5.m23;
      return (long)var3 * (long)var3 + (long)var4 * (long)var4 + (long)var6 * (long)var6;
   }


   public final int distance(int x,int y,int z) {
      Matrix var2 = this.transform;
      
      int var3 = Math.abs(var2.m03 - x);
      int var4 = Math.abs(var2.m13 - y);
      int var6 = Math.abs(var2.m23 - z);
      return var3+var4+var6;
   }
   
   public final int distanceSqr(int x,int y,int z) {
      Matrix var2 = transform;
      
      int var3 = (var2.m03 - x)*(var2.m03 - x);
      int var4 = (var2.m13 - y)*(var2.m13 - y);
      int var6 = (var2.m23 - z)*(var2.m23 - z);
      return var3+var4+var6;
   }


public static void collisionTest(Character body1, Character body2) {
        if(!body1.isCollidable() || !body2.isCollidable()) return;
        Matrix pos1 = body1.transform;
        Matrix pos2 = body2.transform;
        
        int rSum = body1.player_radius + body2.player_radius;

        int dx = pos1.m03 - pos2.m03;
        int dy = pos1.m13 - pos2.m13;
        int dz = pos1.m23 - pos2.m23;

        if( Math.abs(dx)>rSum ||
            Math.abs(dy)>rSum ||
            Math.abs(dz)>rSum ) return;

        long r = (long)dx*dx + (long)dy*dy + (long)dz*dz;
        if (r < rSum*rSum) {
            if(r != 0) {
                r = (int) (1 / MathUtils2.invSqrt(r));
            } else {
                dx = 1;
            }

            int dis = (int) (rSum - r);
            Vector3D dir = new Vector3D(dx, dy, dz);
            dir.setLength(dis / 2);


            if(body1.isCollider()) body1.speedNoFriction.add(dir.x, dir.y, dir.z);
            if(body2.isCollider()) body2.speedNoFriction.add(-dir.x, -dir.y, -dir.z);
        }
    }
    
   private static void move(Matrix matrix, int dx, int dy, int dz) {
      matrix.m03 += dx * FPS.frameTime / 50;
      matrix.m13 += dy * FPS.frameTime / 50;
      matrix.m23 += dz * FPS.frameTime / 50;
   }
   private static void move2(Matrix matrix, int dx, int dy, int dz) {
      matrix.m03 += dx;
      matrix.m13 += dy;
      matrix.m23 += dz;
   }

   public final void moveZ(int d) {
      if(fly) {
         speed.x += transform.m02 * d >> 14;
         speed.y += transform.m12 * d >> 14;
         speed.z += transform.m22 * d >> 14;
      } else if(onFloor) {
         speed.x += transform.m02 * d >> 14;
         speed.z += transform.m22 * d >> 14;
      }
   }

   public final void moveFree(Vector3D v) {
      if(fly || onFloor) {
         speed.add(v.x, v.y, v.z);
      }
   }

   public final void moveX(int d) {
      if(onFloor || fly) {
         speed.x += transform.m00 * d >> 14;
         speed.z += transform.m20 * d >> 14;
      }
   }
   
    public final void drop(int angle) {
        matrix.setRotX(angle * FPS.frameTime / 50);
        this.transform.mul(matrix);
    }
    
    public final void dropSide(int angle) {
        matrix.setRotZ(angle * FPS.frameTime / 50);
        this.transform.mul(matrix);
    }

    public final void rotY(int angle) {
        transform.rotY(angle);
    }
    
   public final void jump(int jump, float force) {
      if(onFloor) {
         this.speed.y += jump;
         this.speed.x = (int)((float)this.speed.x * force);
         this.speed.y = (int)((float)this.speed.y * force);
         this.speed.z = (int)((float)this.speed.z * force);
      }

   }
   public final void jumpArcade(int jump, float force) {
         this.speed.y += jump;
         this.speed.x = (int)((float)this.speed.x * force);
         this.speed.y = (int)((float)this.speed.y * force);
         this.speed.z = (int)((float)this.speed.z * force);
   }

    public final void update() {
        if(colz || fly) {
            tmpVec.set(speed.x * FPS.frameTime / 50 + speedNoFriction.x, 
                    speed.y * FPS.frameTime / 50 + speedNoFriction.y, 
                    speed.z * FPS.frameTime / 50 + speedNoFriction.z);

            speedNoFriction.set(0, 0, 0);

            int rad = (int) ((float) player_radius * 0.8F);
            if(tmpVec.lengthSquared() > rad * rad) tmpVec.setLength(rad);

            transform.m03 += tmpVec.x;
            transform.m13 += tmpVec.y;
            transform.m23 += tmpVec.z;
        }
    }

   public final Matrix getTransform() {
      return this.transform;
   }

   public final int getRadius() {
      return this.player_radius;
   }

   public final int getHeight() {
      return this.player_height;
   }

   public final Vector3D getSpeed() {
      return this.speed;
   }
   public final void setSpeedZero() {
      this.speed.set(0,0,0);
   }

   public final boolean isOnFloor() {
      return this.onFloor;
   }
   public final void setOnFloor(boolean floord) {
      this.onFloor=floord;
   }

   public final boolean isCollision() {
      return this.col;
   }

public final void setCollision(boolean col) {
      this.colz=col;
   }

   public final boolean isCollider() {
      return this.colz;
   }

    public final void setCollidable(boolean col) {
        colz_2 = col;
        updatable = col;
    }

    public final boolean isCollidable() {
        return this.colz_2;
    }

    public final void setUpdatable(boolean upd) {
        updatable = upd;
    }
    
    public final boolean isUpdatable() {
        return updatable;
    }


}

