package code.Gameplay.Objects;

import code.AI.Player;
import code.Collision.Height;
import code.Collision.HeightComputer;
import code.Gameplay.Map.Scene;
import code.Math.Matrix;
import code.Collision.Ray;
import code.Collision.RayCast;
import code.Rendering.MultyTexture;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.Mesh;
import code.Rendering.Meshes.Morphing;
import code.Rendering.Meshes.MeshClone;
import code.Math.Vector3D;
import code.Collision.SphereCast;
import code.Gameplay.GameScreen;
import code.Gameplay.Map.House;
import code.Rendering.Meshes.BoundingBox;

public final class MeshObject extends GameObject {
    
public static final int OPEN = 1, CLOSE = 2, LOOP = 0; //Типы состояние анимации
public static final int STAY_BEGIN = 0, DRIVE = 1, STAY_END = 2, DRIVE_BACK = 3; //Типы состояния лифта
private static final Vector3D dir = new Vector3D();
private static final Vector3D side = new Vector3D();
private static final Vector3D up = new Vector3D();
private static final Vector3D tVec = new Vector3D();
private static short[][] animData; //Позиции вертексов в анимации
private static MeshClone meshData; //Клонирователь мешей
    
public int state = LOOP; //Состояние анимации
public int LIFT_STATE = STAY_BEGIN; //Состояние лифта
public boolean LIFT_PAUSED=false; //Лифт остановлен
public long pauseTime = 0; //Время сна лифта
public long pauseBeginTime = 0; //Время начала сна лифта
public Vector3D playerFollowLift; //Смещение игрока при активации лифта относительно позиции лифта

public long clickTime,liftClickTime; //Время активации анимации и лифта
public Vector3D[] poses; //Координаты движения лифта
public int[] timeToMove; //Время на движение лифта к каждой точке
public int clickAnim; //Последний кадр анимации на момент смены состояния анимации
public float animSpeed=0.0F; //Скорость анимации
public int animType=0; //Тип анимации

public Morphing animation; //Сам меш с анимацией
public MultyTexture tex; //Материалы
public BoundingBox boundingBox; //BoundingBox
public boolean realtimeLighting = false; //Освещение в реальном времени

public int addsz=1500; //Смещение в буффере экрана
public boolean precCol=false; //Точная физика столкновений

public boolean liftReUse = true; //Лифт может быть использован повторно
public boolean liftCanBePaused = false; //Лифт можно поставить на паузу
public boolean liftCycled = false; //Лифт зациклен
public boolean liftRotateToMove = true; //Лифт поворачивается в сторону движения
public int liftSmoothMove = 0; //Расстояние лифта для сглаживания
public boolean disactivateOnEnd = false; //В конце маршрута лифта объект больше нельзя активировать
public boolean ignoreWeaponRayCast = false; //Игнорировать попадания оружия

private int distPerSec=1000; //Скорость лифта в секунду? В опциях не указывается?


    public MeshObject(Mesh[] meshes, MultyTexture tex2, int xx, int yy, int zz, boolean realtimeLighting) {

//Подготовка лифта
        clickTime = GameScreen.time;
        clickAnim = 0;
        liftClickTime = GameScreen.time;
        state = LOOP;
        LIFT_STATE = STAY_BEGIN;
        poses = new Vector3D[1];
        poses[0] = new Vector3D(xx, yy, zz);

//Загрузка меша и его анимации
        Mesh mesh = meshes[0];
        meshData = new MeshClone(mesh);
        meshData.lighting = realtimeLighting;
        animation = new Morphing(
                Morphing.create(meshes, 0, meshes.length), 
                //Morphing.createNormals(meshes), 
                meshData.copy());
        meshData.destroy();
        meshData = null;
        getCharacter().getTransform().setIdentity();
        animation.getMesh().setTexture(tex2);
        boundingBox = new BoundingBox(animation);

//Общая подготовка игрового объекта и материалов
        this.tex = tex2;
        this.set(xx, yy, zz);
        setHp(Integer.MAX_VALUE/2); //Делю на два на случай повышения здоровья
        this.realtimeLighting = realtimeLighting;
    }

    public void set(int xx, int yy, int zz) {
        character.reset();
        character.getTransform().setPosition(xx, yy, zz);
        int xsize = animation.getMesh().maxX() - animation.getMesh().minX();
        int zsize = animation.getMesh().maxX() - animation.getMesh().minX();
        int ysize = animation.getMesh().maxY() - animation.getMesh().minY();
        setCharacterSize(xsize / 2, zsize / 2, ysize);
        animation.setFrame(0);
    }

    public void destroy() {}

    //Физика соприкосновения с мешем
    public boolean sphereCollisionTest(Vector3D pos, int rad) {
        boundingBox.reSort(character.transform);
        if( !SphereCast.isSphereAABBCollision(pos, rad, 
                boundingBox.getMinX(), 
                boundingBox.getMaxX(), 
                boundingBox.getMinZ(), 
                boundingBox.getMaxZ()) ) return false;
        setFrame();
        return SphereCast.sphereCast(animation.getMesh(), getCharacter().getTransform(), pos, rad);
    }

    //Физика просчитывания пола меша(лол)
    public void computeHeight(Height height) {
        boundingBox.reSort(character.transform);
        Vector3D pos = height.getPosition();
        if( !HeightComputer.isPointAABBCollision(pos.x, pos.z, 
                boundingBox.getMinX(),
                boundingBox.getMaxX(), 
                boundingBox.getMinZ(), 
                boundingBox.getMaxZ())) return;
        setFrame();
        HeightComputer.computeHeight(animation.getMesh(), getCharacter().getTransform(), height, playerFollowLift==null);
    }

    //Бросание луча в меш
    public final void rayCast(Ray ray) {
        setFrame();
        Vector3D var2 = ray.getStart();
        Vector3D var3 = ray.getDir();
        int var4 = Math.min(var2.x, var2.x + var3.x);
        int var5 = Math.min(var2.z, var2.z + var3.z);
        int var6 = Math.max(var2.x, var2.x + var3.x);
        int var7 = Math.max(var2.z, var2.z + var3.z);
        boundingBox.reSort(character.transform);

        if (var4 <= boundingBox.getMaxX() && var5 <= boundingBox.getMaxZ() && var6 >= boundingBox.getMinX() && var7 >= boundingBox.getMinZ()) {
            RayCast.rayCast(animation.getMesh(), ray, character.getTransform());
        }
    }

    //Поворот меша
    public final void lookAt(int x, int z) {
        Matrix pos = this.getCharacter().getTransform();

        dir.set(pos.m03 - x, 0, pos.m23 - z); 
        dir.setLength(-Matrix.FP);

        tVec.set(pos.m02, 0, pos.m22); 
        tVec.setLength(Matrix.FP);

        tVec.interpolation(dir, 5);

        setDir(pos, tVec.x, tVec.z);

    }

    public final void lookAtDirect(int x, int z) {
        //Почти как lookAt, просто более резкий
        Matrix pos = this.getCharacter().getTransform();

        dir.set(pos.m03-x, 0, pos.m23-z); 
        dir.setLength( -Matrix.FP );

        setDir(pos, dir.x, dir.z);
    }
    
    
    //Нужно для работы lookAt
    private void setDir(Matrix m, int dirX, int dirZ) {
        dir.set(dirX, 0, dirZ);
        dir.setLength(Matrix.FP);
        if (equals(dir.x, dir.y, dir.z, m.m02, m.m12, m.m22)) return;
        
        up.set(0, Matrix.FP, 0);

        side.cross(up, dir, Matrix.fp);
        side.setLength(Matrix.FP);

        if (dir.lengthSquared() != 0 && side.lengthSquared() != 0) {
            m.setDir(dir.x, dir.y, dir.z);
            m.setSide(side.x, side.y, side.z);
            m.setUp(up.x, up.y, up.z);
        }
    }
    
  
    //Сравнивает близость двух точек(равенство)
    private static boolean equals(int ax, int ay, int az, int bx, int by, int bz) {
        return (Math.abs(ax - bx) < 20
                && Math.abs(ay - by) < 20
                && Math.abs(az - bz) < 20);
    }
    
    //Получает следующую координату лифта
    //В tmp записывается координата
    //coof - время работы лифта в мс
    //summ - максимальное время работы лифта в мс
    private void getNewPos(Vector3D tmp, long coof, int summ) {
        int a=0;
        int sub=0;
        
        while(coof>summ && liftCycled) {coof-=summ;} //Цикличность анимации лифта
        while(coof<0 && liftCycled) {coof+=summ;}
        if(coof>summ) coof=summ;
        if(coof<0) coof=0;
        
        for(int i=0;i<timeToMove.length;i++) {
            sub+=timeToMove[i];
            if(coof>=sub) {a++;}
        }
 
        if(a>poses.length-1 && liftCycled) a=0;
        if(a>poses.length-1) a=poses.length-1;
        if(a<0) a=0;
        
        int b=a+1;
        if(b>poses.length-1 && liftCycled) b=0;
        if(b>poses.length-1) b=poses.length-1;
        if(b<0) b=0;
        sub=0;
        
        if(a>timeToMove.length-1) a=timeToMove.length-1;
        for(int i=0;i<a;i++) sub+=timeToMove[i];       
        int c=(int)((coof-sub)*1000/timeToMove[a]);

        int d=1000-c;
        
        tmp.set(
                (poses[a].x*d+poses[b].x*c)/1000,
                (poses[a].y*d+poses[b].y*c)/1000,
                (poses[a].z*d+poses[b].z*c)/1000);
    }
    
    
    public void update(Scene scene,Player player) {
    
    if(LIFT_STATE==STAY_BEGIN && timeToMove!=null) character.getTransform().setPosition(poses[0]);
    else if(LIFT_STATE==STAY_END && timeToMove!=null) character.getTransform().setPosition(poses[poses.length-1]);
    else if((LIFT_STATE==DRIVE || LIFT_STATE==DRIVE_BACK) && timeToMove!=null) {
        if(!LIFT_PAUSED) {
        int summ=0;
        for(int i=0;i<timeToMove.length;i++) summ+=timeToMove[i];
        long curTime=GameScreen.time-pauseTime;
        long coof=curTime-liftClickTime;
        
        if(coof>=summ && liftCycled) {coof-=summ; liftClickTime+=summ;}
        
        if(LIFT_STATE==DRIVE && coof>=summ) {LIFT_STATE=STAY_END;activable=(!disactivateOnEnd)&activable;}
        else if(LIFT_STATE==DRIVE_BACK && coof>=summ) LIFT_STATE=STAY_BEGIN;
        else {
            if(LIFT_STATE==DRIVE_BACK) coof=summ-coof;
        int a=0;
        Vector3D tmp = new Vector3D();
        
        getNewPos(tmp,coof,summ);
        character.transform.setPosition(tmp);
        
        if(liftSmoothMove!=0) {
        getNewPos(tmp,coof+((LIFT_STATE==DRIVE_BACK)?-1000:1000),summ);
        Vector3D oldP=new Vector3D(tmp.x,tmp.y,tmp.z);
        int distPerSec2=(int)Math.sqrt(
                (tmp.x-oldP.x)*(tmp.x-oldP.x)+
                (tmp.y-oldP.y)*(tmp.y-oldP.y)+
                (tmp.z-oldP.z)*(tmp.z-oldP.z));
        
        if(distPerSec2!=0) distPerSec=liftSmoothMove*1000/distPerSec2;
        
        getNewPos(tmp,coof+((LIFT_STATE==DRIVE_BACK)?-distPerSec:distPerSec),summ);
        character.transform.addPosition(tmp);
        character.transform.divPosition(2,2,2);
        
        if(liftRotateToMove) lookAtDirect(tmp.x,tmp.z);
       
        
        }
        }
        
        if(playerFollowLift!=null) player.character.transform.setPosition(
                getPosX()+playerFollowLift.x, 
                getPosY()+playerFollowLift.y, 
                getPosZ()+playerFollowLift.z);
    } else {
            pauseTime+=GameScreen.time-pauseBeginTime;
            pauseBeginTime=GameScreen.time;
        }
    }
    
    setFrame();

        
    if(realtimeLighting && getPart()!=-1) {
      //Graphics3D.transform(animation.getMesh(), character.transform`);
      animation.getMesh().updateLighting(character.transform,true,scene.getHouse().getRooms()[getPart()]); //smoothNormals,lights
    }

    super.update(scene, player);
    }

    public final void render(DirectX7 g3d, int x1, int y1, int x2, int y2) {
        if (!visible) return;
            
        if (lastActivate >= 0 && (lastActivate + timeToReset > GameScreen.time) && hideWhenUnusable) return;

        Matrix mat = getCharacter().getTransform();
        mat = g3d.computeFinalMatrix(mat);
        setFrame();
        boundingBox.reSort(mat);
        
        if (!boundingBox.isVisible(g3d, mat, x1, y1, x2, y2)) return;

        animation.getMesh().getTexture().updateAnimation();
        g3d.transformAndProjectVertices(animation.getMesh(), mat);
        g3d.addMesh(animation.getMesh(), x1, y1, x2, y2, tex);
        animation.getMesh().increaseMeshSz(addsz);
    }

    public void setFrame() {
        if (state == LOOP) {
            animation.setFrame((int) ((float) (getFrameInter() * animSpeed / 100)));

        } else if (state == OPEN) {
            long frame = (long) (clickAnim + (GameScreen.time - clickTime) * animSpeed * 20 / 1000);
            if (frame >= animation.getMaxFrame() - Morphing.FP) frame = animation.getMaxFrame() - Morphing.FP;

            animation.setFrame((int) frame);
        } else if (state == CLOSE) {
            long frame = (long) (clickAnim - (GameScreen.time - clickTime) * animSpeed * 20 / 1000);
            if (frame < 0) frame = 0;
            
            animation.setFrame((int) frame);
        }
    }

public void activate(House house,Player player,GameScreen gs) {
if(lastActivate>=0 && (lastActivate+timeToReset>GameScreen.time)) return;

    if( !(singleUse && (contains(Player.usedPoints,name) || activated)) ) {
    if(isAllCollected(Player.usedPoints,player,house,gs)) {
    if(state==CLOSE) state=OPEN;
    else if(state==OPEN) state=CLOSE;
    clickAnim=animation.getFrame();    
    clickTime=GameScreen.time;
    
    if(poses.length>1) {
        
        if(LIFT_STATE==DRIVE || LIFT_STATE==DRIVE_BACK && liftCanBePaused) {LIFT_PAUSED=!LIFT_PAUSED; pauseBeginTime=GameScreen.time;}
        
        if(LIFT_STATE==STAY_BEGIN|| LIFT_STATE==STAY_END) liftClickTime=GameScreen.time;
        
        if(LIFT_STATE==STAY_BEGIN) {LIFT_STATE=DRIVE; pauseTime=0;}
        if(LIFT_STATE==STAY_END && liftReUse) {LIFT_STATE=DRIVE_BACK; pauseTime=0;}
    }
    
    }
    }
    
    super.activate(house,player,gs);
}

}
