package code.AI;

import code.Gameplay.Map.Room;
import code.Math.Matrix;
import code.Rendering.Camera;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.Mesh;
import code.Rendering.Meshes.MeshClone;
import code.Rendering.Meshes.Morphing;
import code.Rendering.Meshes.Sprite;
import code.Rendering.MultyTexture;
import code.Rendering.Texture;
import code.Rendering.Vertex;
import code.utils.Asset;
import code.utils.FPS;
import code.utils.GameIni;
import code.utils.Main;
import code.utils.StringTools;

/**
 *
 * @author Roman Lahin
 */
public class TPPose {
    public static TPPose[] meshPoses;
    static MultyTexture mt;
	static Mesh[] models;
	static Mesh clone;
    static byte[] defDrawModes;
    public static boolean inited;
    static int radius, height;
	
    static Matrix mat = new Matrix(), tmp = new Matrix();
    
    public String poseName;
    Morphing walk, attack, secondWalk, secondAttack;
    Morphing walkSight, attackSight, secondWalkSight, secondAttackSight;
    MultyTexture secondMt;
    public byte[] drawModesSecond;
    public Sprite muzzleFlash;
    public Vertex muzzleFlashPos;
    public int muzzleFlashTimer;
    int animationSpeedAttack, animationSpeed;
    
    boolean show3D, show2D, show3DSight, show2DSight, showSecond, showSecondSight;
    boolean canWalk, canWalkSight, canJump, canJumpSight, canLookX, canLookXSight, canLookY, canLookYSight;
    boolean canAttack, canAttackSight;
	boolean swapStrafeLook, swapStrafeLookSight, rotToWalkDir, rotToWalkDirSight;
	boolean camAbsolutePos, camAbsolutePosSight, camAbsoluteRot, camAbsoluteRotSight;
    
    int camX, camY, camZ, camRotX, camRotY, camXSight, camYSight, camZSight, camRotXSight, camRotYSight;
    int camSmoothSteps, camSmoothStepsSight;
	
    boolean rotModelX, rotModelXSight;
    int rotModelY, rotModelYSight;
    
    float lookSpeed, lookSpeedSight;
    
    public static void init() {
        inited = true;
        if(!Main.isExist("/thirdperson.txt")) return;
        
        Object[] obj = GameIni.createGroups("/thirdperson.txt");
        
        String[] names = (String[]) obj[0];
        GameIni[] groups = (GameIni[]) obj[1];
        
        meshPoses = new TPPose[groups.length-1];
        
        GameIni set = groups[0];
        float scale = set.getFloat("SCALE", 1f);
        
        String tmp = set.get("MODEL");
        MeshClone mc = null; 
        if(tmp != null) {
            models = Room.loadMeshes(tmp, scale, scale, scale);
            mt = new MultyTexture(set.get("TEX"), false);
			
            mc = new MeshClone(models[0]);
            clone = mc.copy();
            mc.destroy();
            
            int sizex = clone.maxX() - clone.minX();
            int sizez = clone.maxZ() - clone.minZ();
            radius = (int) (Math.sqrt(sizex*sizex + sizez*sizez) / 2);
            height = clone.maxY() - clone.minY();
        }
        
        tmp = set.get("DRAW_MODES");
        if(tmp != null) defDrawModes = loadModes(tmp);
        
        for(int i=0; i<meshPoses.length; i++) {
            meshPoses[i] = new TPPose(names[i+1], models, clone, groups[i+1], groups[1]);
        }
    }
    
    public static TPPose loadPoseExternal(String path) {
        if(!Main.isExist(path)) return null;
        
        Object[] obj = GameIni.createGroups(path);
        
        String[] names = (String[]) obj[0];
        GameIni[] groups = (GameIni[]) obj[1];
        
        TPPose meshPose = new TPPose(names[0], models, clone, groups[0], groups[0]);
		
		return meshPose;
    }
    
    static byte[] loadModes(String tmp) {
        String[] cut = GameIni.cutOnStrings(tmp, ',', ';');
        byte[] out = new byte[cut.length];
        
        for(int i=0; i<out.length; i++) {
            if(cut[i].trim().equalsIgnoreCase("std")) out[i] = Byte.MIN_VALUE;
            else out[i] = StringTools.parseByte(cut[i]);
        }
        
        return out;
    }
    
    public static void applyRenderModes(TPPose[] meshPoses) {
        if(defDrawModes != null && mt != null) applyModes(mt.textures, defDrawModes);
        
        for(int i=0; i<meshPoses.length; i++) {
            MultyTexture mt = meshPoses[i].secondMt;
            byte[] modes = meshPoses[i].drawModesSecond;
            
            if(mt != null && modes != null) applyModes(mt.textures, modes);
        }
    }
    
    private static void applyModes(Texture[] texs, byte[] modes) {
        int len = texs.length > modes.length ? modes.length : texs.length;
        
        for(int i=0; i<len; i++) {
            if(modes[i] == Byte.MIN_VALUE) texs[i].drawmode = DirectX7.standartDrawmode;
            texs[i].drawmode = modes[i];
        }
    }
    
    public TPPose(String poseName, Mesh[] meshes, Mesh clone, GameIni ini, GameIni def) {
        this.poseName = poseName;
        
		walk = makeMorphing("WALK", meshes, clone, ini, def, true);
		attack = makeMorphing("ATTACK", meshes, clone, ini, def, false);
		walkSight = makeMorphing("WALK_SIGHT", meshes, clone, ini, def, false);
		attackSight = makeMorphing("ATTACK_SIGHT", meshes, clone, ini, def, false);
        
        String tmp = ini.getDef("SECOND_DRAW_MODES", def.get("SECOND_DRAW_MODES"));
        if(tmp != null) drawModesSecond = loadModes(tmp);
        
        Mesh[] secondModels = null;
        Mesh cloneSecond = null;
        tmp = ini.getDef("SECOND_MODEL", def.get("SECOND_MODEL"));
        if(tmp != null) {
            float scale = ini.getFloat("SECOND_SCALE", def.getFloat("SECOND_SCALE", 1f));
            secondModels = Asset.getMeshes(tmp, scale, scale, scale);
            secondMt = new MultyTexture(ini.getDef("SECOND_TEX", def.get("SECOND_TEX")), false);
            
            MeshClone mc = new MeshClone(secondModels[0]);
            cloneSecond = mc.copy();
            mc.destroy();
        }
        
        secondWalk = makeMorphing("SECOND_WALK", secondModels, cloneSecond, ini, def, true);
        secondAttack = makeMorphing("SECOND_ATTACK", secondModels, cloneSecond, ini, def, false);
        secondWalkSight = makeMorphing("SECOND_WALK_SIGHT", secondModels, cloneSecond, ini, def, false);
        secondAttackSight = makeMorphing("SECOND_ATTACK_SIGHT", secondModels, cloneSecond, ini, def, false);
        
        show3D = ini.getInt("SHOW_3D", def.getInt("SHOW_3D", 1)) == 1;
        show2D = ini.getInt("SHOW_2D", def.getInt("SHOW_2D", 0)) == 1;
        showSecond = ini.getInt("SECOND_SHOW_3D", def.getInt("SECOND_SHOW_3D", secondWalk == null ? 0 : 1)) == 1;
        
        show3DSight = ini.getInt("SHOW_3D_SIGHT", def.getInt("SHOW_3D_SIGHT", 1)) == 1;
        show2DSight = ini.getInt("SHOW_2D_SIGHT", def.getInt("SHOW_2D_SIGHT", 0)) == 1;
        showSecondSight = 
                ini.getInt("SECOND_SHOW_3D_SIGHT", def.getInt("SECOND_SHOW_3D_SIGHT", secondWalk == null ? 0 : 1)) == 1;
        
        canWalk = ini.getInt("CAN_WALK", def.getInt("CAN_WALK", 1)) == 1;
        canWalkSight = ini.getInt("CAN_WALK_SIGHT", def.getInt("CAN_WALK_SIGHT", 1)) == 1;
        
        canJump = ini.getInt("CAN_JUMP", def.getInt("CAN_JUMP", 1)) == 1;
        canJumpSight = ini.getInt("CAN_JUMP_SIGHT", def.getInt("CAN_JUMP_SIGHT", 1)) == 1;
        
        canLookX = ini.getInt("CAN_LOOK_X", def.getInt("CAN_LOOK_X", 1)) == 1;
        canLookXSight = ini.getInt("CAN_LOOK_X_SIGHT", def.getInt("CAN_LOOK_X_SIGHT", 1)) == 1;
        
        canLookY = ini.getInt("CAN_LOOK_Y", def.getInt("CAN_LOOK_Y", 1)) == 1;
        canLookYSight = ini.getInt("CAN_LOOK_Y_SIGHT", def.getInt("CAN_LOOK_Y_SIGHT", 1)) == 1;
        
        canAttack = ini.getInt("CAN_ATTACK", def.getInt("CAN_ATTACK", 1)) == 1;
        canAttackSight = ini.getInt("CAN_ATTACK_SIGHT", def.getInt("CAN_ATTACK_SIGHT", 1)) == 1;
        
        swapStrafeLook = ini.getInt("SWAP_STRAFE_LOOK", def.getInt("SWAP_STRAFE_LOOK", 0)) == 1;
        swapStrafeLookSight = ini.getInt("SWAP_STRAFE_LOOK_SIGHT", def.getInt("SWAP_STRAFE_LOOK_SIGHT", 0)) == 1;
        
        camAbsolutePos = ini.getInt("CAM_ABSOLUTE_POS", def.getInt("CAM_ABSOLUTE_POS", 0)) == 1;
        camAbsolutePosSight = ini.getInt("CAM_ABSOLUTE_POS_SIGHT", def.getInt("CAM_ABSOLUTE_POS_SIGHT", 0)) == 1;
        
        camAbsoluteRot = ini.getInt("CAM_ABSOLUTE_ROT", def.getInt("CAM_ABSOLUTE_ROT", 0)) == 1;
        camAbsoluteRotSight = ini.getInt("CAM_ABSOLUTE_ROT_SIGHT", def.getInt("CAM_ABSOLUTE_ROT_SIGHT", 0)) == 1;
        
        rotToWalkDir = ini.getInt("ROTATE_TO_WALK_DIR", def.getInt("ROTATE_TO_WALK_DIR", 0)) == 1;
        rotToWalkDirSight = ini.getInt("ROTATE_TO_WALK_DIR_SIGHT", def.getInt("ROTATE_TO_WALK_DIR_SIGHT", 0)) == 1;
        
        lookSpeed = ini.getFloat("LOOK_SPEED", def.getFloat("LOOK_SPEED", 1f));
        lookSpeedSight = ini.getFloat("LOOK_SPEED_SIGHT", def.getFloat("LOOK_SPEED_SIGHT", 0.71f));
        
        tmp = ini.getDef("CAM_POS", def.get("CAM_POS"));
        if(tmp != null) {
            int[] pos = StringTools.cutOnInts(tmp, ',');
            camX = pos[0]; camY = pos[1]; camZ = pos[2];
        }
        
        camRotX = ini.getInt("CAM_ROT_X", def.getInt("CAM_ROT_X", 0));
        camRotY = ini.getInt("CAM_ROT_Y", def.getInt("CAM_ROT_Y", 0));
        rotModelY = ini.getInt("MODEL_ROT_Y", def.getInt("MODEL_ROT_Y", 0));
        camSmoothSteps = ini.getInt("CAM_SMOOTH_STEPS", def.getInt("CAM_SMOOTH_STEPS", 3));
        
        tmp = ini.getDef("CAM_POS_SIGHT", def.get("CAM_POS_SIGHT"));
        if(tmp != null) {
            int[] pos = StringTools.cutOnInts(tmp, ',');
            camXSight = pos[0]; camYSight = pos[1]; camZSight = pos[2];
        }
        
        camRotXSight = ini.getInt("CAM_ROT_X_SIGHT", def.getInt("CAM_ROT_X_SIGHT", 0));
        camRotYSight = ini.getInt("CAM_ROT_Y_SIGHT", def.getInt("CAM_ROT_Y_SIGHT", 0));
        rotModelYSight = ini.getInt("MODEL_ROT_Y_SIGHT", def.getInt("MODEL_ROT_Y_SIGHT",0));
        camSmoothStepsSight = ini.getInt("CAM_SMOOTH_STEPS_SIGHT", def.getInt("CAM_SMOOTH_STEPS_SIGHT", 2));
        
        rotModelX = ini.getInt("ROT_MODEL_X", def.getInt("ROT_MODEL_X", 0)) == 1;
        rotModelXSight = ini.getInt("ROT_MODEL_X_SIGHT", def.getInt("ROT_MODEL_X_SIGHT", 0)) == 1;
        
        if ((tmp = ini.getDef("MUZZLE_FLASH_POS", def.get("MUZZLE_FLASH_POS"))) != null) {
            muzzleFlashPos = new Vertex(GameIni.cutOnInts(tmp, ',', ';'));
            muzzleFlash = new Sprite(Asset.getTexture(
                    ini.getDef("MUZZLE_FLASH", def.get("MUZZLE_FLASH"))), 
                    ini.getInt("MUZZLE_FLASH_SCALE", def.getInt("MUZZLE_FLASH_SCALE", 1)));
                    
            if(muzzleFlash.textures[0].rImg.alphaMixing) muzzleFlash.mode = 3;
            muzzleFlash.fog = false;
            muzzleFlashTimer = ini.getInt("MUZZLE_FLASH_TIMER", def.getInt("MUZZLE_FLASH_TIMER", 0));
        }
        
        animationSpeed = ini.getInt("ANIMATION_SPEED", def.getInt("ANIMATION_SPEED", 100));
        animationSpeedAttack = ini.getInt("ANIMATION_SPEED_ATTACK", def.getInt("ANIMATION_SPEED_ATTACK", animationSpeed));
    }
    
    public boolean show2D(Player player) {
        return (player.zoom ? show2DSight : show2D);
    }
    
    public boolean canWalk(Player player) {
        return (player.zoom ? canWalkSight : canWalk);
    }
    
    public boolean canJump(Player player) {
        return (player.zoom ? canJumpSight : canJump);
    }
    
    public boolean canLookX(Player player) {
        return (player.zoom ? canLookXSight : canLookX);
    }
    
    public boolean canLookY(Player player) {
        return (player.zoom ? canLookYSight : canLookY);
    }
    
    public boolean canAttack(Player player) {
        return (player.zoom ? canAttackSight : canAttack);
    }
	
	public boolean isSwapStrafeLook(Player player) {
		return (player.zoom ? swapStrafeLookSight : swapStrafeLook);
	}
	
	public boolean isRotToWalkDir(Player player) {
		return (player.zoom ? rotToWalkDirSight : rotToWalkDir);
	}
    
    public void update(Camera cam, Player player) {
        boolean zoom = player.zoom;
        cam.x = zoom ? camXSight : camX;
        cam.y = zoom ? camYSight : camY;
        cam.z = zoom ? camZSight : camZ;
        cam.rotX = zoom ? camRotXSight : camRotX;
        cam.rotY = zoom ? camRotYSight : camRotY;
        cam.smoothSteps = zoom ? camSmoothStepsSight : camSmoothSteps;
        boolean absolutePos = zoom ? camAbsolutePosSight : camAbsolutePos;
        boolean absoluteRot = zoom ? camAbsoluteRotSight : camAbsoluteRot;
        
		if(!canLookX(player)) {
			player.rotateX = 0;
			player.updateMatrix();
		}
        Matrix plmat = player.character.getTransform();
        
        cam.set(plmat, player.rotateX, player.rotateY, absolutePos, absoluteRot);
    }
    
    public void draw(Player player, DirectX7 g3d, int x1, int y1, int x2, int y2) {
        boolean zoom = player.zoom;
        Matrix plmat = player.character.getTransform();
        
        if(zoom ? show3DSight : show3D) {
            mat.set(plmat);
            tmp.setRotY(180 + (zoom ? rotModelYSight : rotModelY));
            mat.mul(tmp);
            if(!(zoom ? rotModelXSight : rotModelX)) {
                tmp.setRotX((int)player.rotateX);
                mat.mul(tmp);
            }
            
            Morphing thisAttack = attack(zoom);
            Morphing thisSecondAttack = secondAttack(zoom);
            
            Morphing anim = walk(zoom);
            Morphing second = secondWalk(zoom);
            
            if(thisAttack != null || thisSecondAttack != null) {
                Morphing sec = thisAttack == null ? thisSecondAttack : thisAttack;
                int len = sec.getMaxFrame();
                if(player.attackFrame <= len) {
                    if(thisAttack != null) {
                        anim = thisAttack;
                        anim.setFrameNI(player.attackFrame);
                    }
                    if(thisSecondAttack != null) {
                        second = thisSecondAttack;
                        second.setFrameNI(player.attackFrame);
                    }
                    
                    player.attackFrame += animationSpeedAttack * FPS.frameTime / 50;
                }
            }
            
            if(anim != null && (anim == walk || anim == walkSight)) anim.setFrameNI(player.walkFrame);
            if(second != null && (second == secondWalk || second == secondWalkSight)) second.setFrameNI(player.walkFrame);

            if(player.character.speed.x != 0 || player.character.speed.z != 0) {
                Morphing w = walk(zoom);
                player.walkFrame += animationSpeed * FPS.frameTime / 50;
                if(w != null && w.getMaxFrame() != 0) player.walkFrame %= w.getMaxFrame();
            }

            if(anim != null) {
                anim.setFrame(anim.getFrame());
                g3d.transformAndProjectVertices(anim.getMesh(), g3d.computeFinalMatrix(mat));
                g3d.addMesh(anim.getMesh(), x1, y1, x2, y2, mt);
                if(radius != 0) anim.getMesh().increaseMeshSz(radius * 4);
            }
            
            if((zoom ? showSecondSight : showSecond) && second != null) {
                second.setFrame(second.getFrame());
                g3d.transformAndProjectVertices(second.getMesh(), g3d.computeFinalMatrix(mat));
                g3d.addMesh(second.getMesh(), x1, y1, x2, y2, secondMt);
                if(radius != 0) second.getMesh().increaseMeshSz(radius * 4);
            }
            
            if(muzzleFlash != null && player.muzzleFrame > 0) {
                muzzleFlashPos.transform(mat);
                muzzleFlash.pos.set(muzzleFlashPos.sx, muzzleFlashPos.sy - muzzleFlash.getHeight() / 2, muzzleFlashPos.rz);
                muzzleFlash.project(g3d.getInvCamera(), g3d);
                g3d.addRenderObject(muzzleFlash, x1, y1, x2, y2);
                muzzleFlash.sz += radius * 4;
                player.muzzleFrame -= FPS.frameTime;
            }
        }
    }
    
    float lookSpeed(Player player) {
        return player.zoom ? lookSpeedSight : lookSpeed;
    }

    private static Morphing makeMorphing(String animName, Mesh[] models, Mesh clone, GameIni ini, GameIni def, boolean createIfNo) {
        if(models == null || clone == null) return null;
        
        int walkStart = 0, walkEnd = 1;
        String tmp = ini.getDef(animName, def.get(animName));
        if(tmp != null) {
            int[] cycle = StringTools.cutOnInts(tmp, '-');
            walkStart = cycle[0]; walkEnd = cycle[cycle.length==1?0:1]+1;
        } else if(!createIfNo) return null;
        
        short[][] anim = Morphing.create(models, walkStart, walkEnd);
        return new Morphing(anim, clone);
    }

    private Morphing walk(boolean zoom) {
        if(walkSight == null) return walk;
        return zoom ? walkSight : walk;
    }
    
    private Morphing secondWalk(boolean zoom) {
        if(secondWalkSight == null) return secondWalk;
        return zoom ? secondWalkSight : secondWalk;
    }
    
    private Morphing attack(boolean zoom) {
        if(attackSight == null) return attack;
        return zoom ? attackSight : attack;
    }
    
    private Morphing secondAttack(boolean zoom) {
        if(secondAttackSight == null) return secondAttack;
        return zoom ? secondAttackSight : secondAttack;
    }
}
