package code.AI.misc;

import code.Gameplay.Objects.GameObject;
import code.Math.Matrix;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.BoundingBox;
import code.Rendering.Meshes.MeshImage;
import code.Rendering.Meshes.Sprite;
import code.Rendering.MultyTexture;

public final class Corpse extends GameObject {
    
    private final MultyTexture mtex;
    private int f = 0;
    private final int frame;
    private final Matrix mat;
    private final MeshImage meshImage;
    public Sprite spr;
    private BoundingBox boundingBox;

    public Corpse(int frame2, Matrix matrix2, MeshImage mesh, MultyTexture tex) {
        mat = new Matrix();
        mat.set(matrix2);
        if (mesh != null) {
            meshImage = new MeshImage(mesh.getMesh(), mesh.getAnimation());
            boundingBox = new BoundingBox(mesh.getAnimation());
        } else {meshImage=null;}
        
        mtex = tex;
        frame = frame2;
        character.reset();
        character.getTransform().set(mat);
        character.setCollision(false);
        character.setCollidable(false);
        character.setOnFloor(true);
        character.setSpeedZero();

    }


    protected final boolean isNeedRecomputePart() {
        return (getFrame() == 0);
    }   
    
/*
     public final void update(Scene scene) {
     if(this.boled==false) {
     this.boled=true;
     System.out.println("Spawning PV");
     Matrix var6 = this.getCharacter().getTransform();

     Vertex[] verts=this.animation.getMesh().getVertices();
     for(int i=0;i<verts.length;i++) {
     verts[i].transform(var6);

     PhysVert pv=new PhysVert(verts[i]);
     scene.getHouse().addObject(pv);
     verts[i]=pv.pos;
     }
     }
     }*/
      // Падение при 0 hp

    public final void render(DirectX7 g3d, int x1, int y1, int x2, int y2) {
        if (f <= 3600) setHp(100);
        f++;
        if (f > 3600) setHp(0);
        
        if (meshImage != null) {
            meshImage.getAnimation().setFrame(frame);
            Matrix fmat = g3d.computeFinalMatrix(mat);
            if (!boundingBox.isVisible(g3d, fmat, x1, y1, x2, y2)) return;
            

            meshImage.setMatrix(fmat);
            meshImage.setTexture(mtex);

            g3d.addRenderObject(meshImage, x1, y1, x2, y2);
            meshImage.sz += 5900;
            if (character.oldFloorPoly != null && character.oldFloorPoly.sz > meshImage.sz) 
                character.oldFloorPoly.sz = meshImage.sz - 1;
        } else {
            spr.getPosition().set(getPosX(), getPosY(), getPosZ());
            spr.updateFrame();
            spr.project(g3d.getInvCamera(), g3d);
            if (!spr.isVisible( x1, y1, x2, y2)) return;
            
            g3d.addRenderObject( spr, x1, y1, x2, y2);
            spr.sz += 5900;
            if (character.oldFloorPoly != null && character.oldFloorPoly.sz > spr.sz) 
                character.oldFloorPoly.sz = spr.sz - 1;

        }
    }


}
