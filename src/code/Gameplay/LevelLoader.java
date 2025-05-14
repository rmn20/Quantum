package code.Gameplay;

import code.Gameplay.Objects.Image2D;
import code.Gameplay.Objects.LVLChange;
import code.Gameplay.Objects.KeyObject;
import code.Gameplay.Objects.ShopObject;
import code.Gameplay.Objects.MeshObject;
import code.Gameplay.Objects.NPCSpawner;
import code.Gameplay.Objects.SpriteObject;
import code.Gameplay.Objects.Teleport;
import code.Gameplay.Map.HouseCreator;
import code.Gameplay.Map.Light;
import code.Gameplay.Map.House;
import code.Gameplay.Map.Scene;
import code.Gameplay.Map.RoomObject;
import code.Gameplay.Map.LightMapper;
import code.Gameplay.Map.Room;
import code.Gameplay.Map.Skybox;
import code.AI.Player;
import code.AI.BigZombie;
import code.AI.NPC;
import code.AI.Bot;
import code.AI.TPPose;
import code.AI.Zombie;
import code.utils.WeatherGenerator;
import code.utils.Asset;
import code.utils.Main;
import code.utils.ImageResize;
import code.Rendering.MultyTexture;
import code.Rendering.DirectX7;
import code.Rendering.Meshes.Mesh;
import code.Rendering.Texture;
import code.Rendering.Meshes.MeshImage;
import code.Rendering.Meshes.Sprite;
import code.Rendering.Vertex;
import code.Math.Vector3D;
import code.utils.GameIni;
import code.utils.StringTools;
import java.util.Vector;
import javax.microedition.lcdui.Image;

/**
 *
 * @author Roman Lahin
 */
public class LevelLoader {

    public static boolean defaultOneBot = false;
    private final static String[] levelGroups = new String[]{
        "WTEX_BEGIN", "WTEX_END"
    };

    private static String[] cutLevelFile(String file) {
        int index = file.indexOf('[');
        if(index > -1) {

            int tmpIndex = file.indexOf(levelGroups[0]);
            //Обрезаем информацию о текстурах
            if(tmpIndex > -1) {
                int ind2 = file.indexOf(levelGroups[1]);
                String textures = file.substring(tmpIndex + levelGroups[1].length() + 3, ind2 - 2);

                file = file.substring(0, tmpIndex - 2)
                        + file.substring(ind2 + levelGroups[1].length() + 1, file.length());

                index = file.indexOf('[');
                if(index > -1) file = file.substring(0, index - 1);

                return new String[]{file, textures};
            }

            return new String[]{file.substring(0, index - 1)};
        }
        return new String[]{file};
   }

    public static final Scene createScene(int width, int height, String file, Main main, GameScreen gs) {
        if(!TPPose.inited) TPPose.init();
		
        String file2 = StringTools.getStringFromResource(file);
        String oldF = file2;
        
        String[] data = cutLevelFile(file2);
        file2 = data[0];

        GameIni lvl = new GameIni(file2,false);
        GameIni setting = Main.settings;

        String zombiemod = getStringdef("ZOMBIE_MODEL",lvl,setting,"/zombie.png");
        String zombietex = getStringdef("ZOMBIE_TEXTURE",lvl,setting,"/zombie.png");
        String bigzombiemod = getStringdef("BIGZOMBIE_MODEL",lvl,setting,"/big_zombie.png");
        String bigzombietex = getStringdef("BIGZOMBIE_TEXTURE",lvl,setting,"/big_zombie.png");
        Zombie.model = BigZombie.model = null;
        Zombie.texture = BigZombie.texture = null;
        
        float zombiescale = getFloat("ZOMBIE_SCALE",lvl,setting,4.5f);
        float bigzombiescale = getFloat("BIGZOMBIE_SCALE",lvl,setting,50);
        
        Zombie.animSpeed = getInt(""
                + "ZOMBIE_ANIMSPEED",lvl,setting,140);//140
        BigZombie.animSpeed = getInt("BIGZOMBIE_ANIMSPEED",lvl,setting,135);//135
        
        Zombie.attackAnimSpeed = getInt("ZOMBIE_ATTACKANIMSPEED",lvl,setting,700);//700
        BigZombie.attackAnimSpeed = getInt("BIGZOMBIE_ATTACKANIMSPEED",lvl,setting,270);//270
        
        Zombie.attackDamage = getInt("ZOMBIE_DAMAGE",lvl,setting,1);//1
        BigZombie.attackDamage = getInt("BIGZOMBIE_DAMAGE",lvl,setting,7);//7
        
        Zombie.attackRadius = getFloat("ZOMBIE_ATTACKRADIUS",lvl,setting,1.2F);//1.2F
        BigZombie.attackRadius = getFloat("BIGZOMBIE_ATTACKRADIUS",lvl,setting,1.2F);//1.2F
        
        Zombie.jumpHeight = getInt("ZOMBIE_JUMPHEIGHT",lvl,setting,140);//140
        BigZombie.jumpHeight = getInt("BIGZOMBIE_JUMPHEIGHT",lvl,setting,202);//202
        
        Zombie.jumpSpeed = getFloat("ZOMBIE_JUMPSPEED",lvl,setting,1.2F);//1.2F
        BigZombie.jumpSpeed = getFloat("BIGZOMBIE_JUMPSPEED",lvl,setting,1.2F);//1.2F
        
        BigZombie.jumpHeight2 = getInt("BIGZOMBIE_JUMPHEIGHT2",lvl,setting,202);//202
        BigZombie.jumpSpeed2 = getFloat("BIGZOMBIE_JUMPSPEED2",lvl,setting,1.2F);//1.5F
        
        Zombie.walkSpeed = getInt("ZOMBIE_SPEED",lvl,setting,140);//140
        BigZombie.walkSpeed = getInt("BIGZOMBIE_SPEED",lvl,setting,135);//135
        
        Zombie.reactTimer = getInt("ZOMBIE_REACTTIMER",lvl,setting,8);//8
        BigZombie.reactTimer = getInt("BIGZOMBIE_REACTTIMER",lvl,setting,8);//8
        
        Zombie.attackTimer = getInt("ZOMBIE_ATTACKTIMER",lvl,setting,8);//8
        BigZombie.attackTimer = getInt("BIGZOMBIE_ATTACKTIMER",lvl,setting,14);//14
        
        Zombie.AI = getInt("ZOMBIE_AI",lvl,setting,1);//1
        BigZombie.AI = getInt("BIGZOMBIE_AI",lvl,setting,1);//1
        
        Zombie.enemyReaction = getInt("ZOMBIE_PLAYERREACTION",lvl,setting,2);//2
        BigZombie.enemyReaction = getInt("BIGZOMBIE_PLAYERREACTION",lvl,setting,2);//2
        
        Zombie.attackState = getInt("ZOMBIE_ATTACKTRIGGER",lvl,setting,2);//2
        BigZombie.attackState = getInt("BIGZOMBIE_ATTACKTRIGGER",lvl,setting,2);//2
        
        Zombie.moneyOnDeath = getInt("ZOMBIE_PRICE",lvl,setting,10);
        BigZombie.moneyOnDeath = getInt("BIGZOMBIE_PRICE",lvl,setting,30);
        
        Zombie.maxHP = getInt("LIFE_ZOMBIE",lvl,setting,100);
        BigZombie.max_hp = getInt("LIFE_BIG_ZOMBIE",lvl,setting,400);
        
        Zombie.attackTo = GameIni.cutOnInts(getStringdef("ZOMBIE_ATTACK",lvl,setting,"0,3"), ',',';');
        BigZombie.attackTo = GameIni.cutOnInts(getStringdef("BIGZOMBIE_ATTACK",lvl,setting,"0,3"), ',',';');
        
        defaultOneBot = getBoolean("SPAWN_ONE_ENEMY", lvl, setting, false);
        Bot.cleverPathfinfing = getBoolean("BOTS_CLEVER_PATHFINDING", lvl, setting, true);
        
        /*if(getInt("OBSERVER", lvl, setting, 0)==1) {
            gs.camFollow = new Camera();
            gs.camFollow.angle=0;
        }*/

        float levelScale = getFloat("WORLD_SCALE", lvl, setting, 1.0f);
        Main.floorOffsetSZ = getInt("FLOOR_OFFSETSZ", lvl, setting, 0);
        Main.fullScreenSight = getBoolean("MAXIMIZE_SIGHT", lvl, setting, false);
        Main.originalSight = getBoolean("ORIGINAL_SIGHT", lvl, setting, false);

        Main.sight_icon = getStringdef("SIGHT_ICON", lvl, setting, "/sight.png");

        //Level Setting
        Main.originalUseIcon = getBoolean("ORIGINAL_USE_ICON", lvl, setting, false);

        Zombie.fallDeath = (byte)getInt("ZOMBIE_FALL_ON_DEATH", lvl, setting, 1);
        BigZombie.fallDeath = (byte)getInt("BIGZOMBIE_FALL_ON_DEATH", lvl, setting, 1);
        
        Zombie.bloodHas = getBoolean("ZOMBIE_HAS_BLOOD", lvl, setting, true);
        BigZombie.bloodHas = getBoolean("BIGZOMBIE_HAS_BLOOD", lvl, setting, true);

        Main.Blood = getString("BLOOD_TEX", lvl, setting);
        if(Main.Blood != null) Asset.getTexture(Main.Blood);

        GameScreen.bloom = getBoolean("BLOOM", lvl, setting, false);
        DirectX7.lightdiry = (short) getInt("LIGHT_Y", lvl, setting, 4096);
        DirectX7.lightdirx = (short) getInt("LIGHT_X", lvl, setting, -4096);
        DirectX7.lightdirz = (short) getInt("LIGHT_Z", lvl, setting, 4096);

        DirectX7.setFogDist(getInt("FOGD", lvl, setting, 1));
        DirectX7.setDrDist(getInt("DIST", lvl, setting, Integer.MAX_VALUE));
        DirectX7.standartDrawmode = (byte) getInt("DMODE", lvl, setting, 0);

        String c = getStringdef("FOGCR", lvl, setting, "NULL");
        if(!c.equals("NULL")) {
            int g = getInt("FOGCG", lvl, setting, 0);
            int b = getInt("FOGCB", lvl, setting, 0);
            DirectX7.fogc = (StringTools.parseInt(c) << 16) | (g << 8) | b;
        }

        c = getStringdef("FOGC", lvl, setting, "NULL");
        if(!c.equals("NULL")) DirectX7.fogc = StringTools.getRGB(c,',');
        
        MultyTexture mt;
        if(data.length == 1) mt = new MultyTexture(lvl.get("WORLD_TEXTURE"), true);
        else mt = new MultyTexture(StringTools.cutOnStrings(data[1], '\n'));

        Mesh[] meshes = Room.loadMeshes(lvl.get("WORLD_MODEL"), levelScale, levelScale, levelScale, mt);

        c = lvl.get("DRAW_MODES");
        if(c != null) Asset.applyMeshEffects(meshes, c);

        c = lvl.get("MAP_ADDSZ");
        if(c != null) {
            int[] asz = GameIni.cutOnInts(c, ',', ';');
            
            for(int i=0; i<asz.length; i++) mt.textures[i].addsz = asz[i];
        }

        Main.forceLQFog = getBoolean("LQFOG", lvl, setting, false);
        
        Main.updateOnlyNear = getBoolean("UPDATE_ONLY_NEAR", lvl, setting, false);
        Main.updateOnlyNearPhysics = getBoolean("UPDATE_ONLY_NEAR_PHYSICS", lvl, setting, Main.updateOnlyNear);
       
        String skyboxModel = getStringdef("SKYBOX_MODEL", lvl, setting, "NULL");
        String skyboxTexture = getStringdef("SKYBOX_TEXTURE", lvl, setting, "NULL");
        String skybox2D = getStringdef("SKYBOX_2D", lvl, setting, "NULL");
        String skyboxGradient = getStringdef("SKYBOX_GRADIENT", lvl, setting, "NULL");
        String skyboxLighting = getStringdef("SKYBOX_LIGHTING", lvl, setting, "NULL");
		
		String skyboxColor = getStringdef("SKYBOX_COLOR", lvl, setting, "NULL");
		String skyboxGroundColor = getStringdef("SKYBOX_GROUND_COLOR", lvl, setting, "NULL");

        Skybox skybox = null;
        byte oldf = DirectX7.standartDrawmode;
        DirectX7.standartDrawmode = 0;

        if(!skyboxModel.equals("NULL") && !skyboxTexture.equals("NULL")) {
			skybox = new Skybox(skyboxModel, skyboxTexture);

			boolean pers = getBoolean("SKYBOX_PERS", lvl, setting, true);

			Texture[] texs = skybox.texture.textures;
			for(int ti = 0; ti < texs.length; ti++)
				texs[ti].setPerspectiveCorrection(pers);

			String modesz = getStringdef("SKYBOX_MODES", lvl, setting, "NULL");
			if(!modesz.equals("NULL")) Asset.applyMeshEffects(skybox.getMesh(), modesz);
			
        } else if(!skyboxColor.equals("NULL") && 
                skyboxModel.equals("NULL") && skybox2D.equals("NULL")) {
           
            int[] rgb = GameIni.cutOnInts(skyboxColor, ',', ';');
            
			int col;
			if(rgb.length == 1) col = 0x010101 * rgb[0];
			else col = (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
					
            skybox = new Skybox(col);
       
        } else if (!skybox2D.equals("NULL")) {
			
			int color = 0, groundColor = 0;
			
			if(!skyboxColor.equals("NULL")) {
				int[] rgb = GameIni.cutOnInts(skyboxColor, ',', ';');
				color = (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
			}
			
			if(!skyboxGroundColor.equals("NULL")) {
				int[] rgb = GameIni.cutOnInts(skyboxGroundColor, ',', ';');
				groundColor = (rgb[0] << 16) | (rgb[1] << 8) | rgb[2];
			}
            
            float horizonScale = 1.0f;
            int lowestDegree = -91;
            float horizonOffset = 0.0f;
			
            String[] tmp = GameIni.cutOnStrings(skybox2D,',',';');
            if(tmp.length>=2) horizonScale = StringTools.parseFloat(tmp[1]);
            if(tmp.length>=3) lowestDegree = StringTools.parseInt(tmp[2]);
            if(tmp.length>=4) horizonOffset = StringTools.parseFloat(tmp[3]);
			
            float xs = getFloat("SKYBOX_REPEAT_X", lvl, setting, 2.0f);
            float ys = getFloat("SKYBOX_REPEAT_Y", lvl, setting, 1.0f);
			
            skybox = new Skybox(Asset.getTexture(tmp[0]), xs, ys, 
					lowestDegree, horizonScale, horizonOffset,
					color, groundColor);
           
        } else if (!skyboxGradient.equals("NULL")) {
            
            float horizonScale = 1.0f;
            int lowestDegree = -91;
            float horizonOffset = 0.0f;
			
            String[] tmp = GameIni.cutOnStrings(skyboxGradient,',',';');
            if(tmp.length>=2) horizonScale = StringTools.parseFloat(tmp[1]);
            if(tmp.length>=3) lowestDegree = StringTools.parseInt(tmp[2]);
            if(tmp.length>=4) horizonOffset = StringTools.parseFloat(tmp[3]);
                
            skybox = new Skybox(Asset.getTexture(tmp[0]), lowestDegree, horizonScale, horizonOffset);
           
        }

        if(skybox != null) {
            if(!skyboxLighting.equals("NULL")) skybox.skyLighting = Asset.getTexture(skyboxLighting);
            skybox.skyboxAlways = getBoolean("SKYBOX_ALWAYS", lvl, setting, false);
            skybox.addViewport(0, 0, width, height);
        }

        DirectX7.standartDrawmode = oldf;

        Room.chunkSize = getInt("CHUNK_COLLISION_SIZE", lvl, setting, 5000);
        Room.chunkSizeRender = getInt("CHUNK_SIZE", lvl, setting, 0);
       
        LightMapper.reset();
        LightMapper.setFastCalc(!getBoolean("LIGHTMAPPER_RAYCASTING", lvl, setting, !LightMapper.fastCalc));
        LightMapper.setslCheap(getBoolean("SKY_LIGHT_CHEAP", lvl, setting, LightMapper.slCheap));
        
        LightMapper.ambientLightSet(GameIni.cutOnInts(getString("AMBIENT_LIGHT", lvl, setting), ',', ';'));
        LightMapper.skyLightIntensitySet(GameIni.cutOnInts(getString("SKY_LIGHT", lvl, setting), ',', ';'));
        LightMapper.sunLightIntensitySet(GameIni.cutOnInts(getString("SUN_LIGHT", lvl, setting), ',', ';'));
        LightMapper.aoDistance = getInt("AO_DISTANCE", lvl, setting, LightMapper.aoDistance);
        LightMapper.aoIntensity = getInt("AO_INTENSITY", lvl, setting, LightMapper.aoIntensity);
        LightMapper.giRays = getInt("GI_RAYS", lvl, setting, LightMapper.giRays);
        LightMapper.lumFromTexturesSet(GameIni.cutOnInts(getString("GI_LUM_FROM_TEXTURES", lvl, setting), ',', ';'));
        LightMapper.giIntensitySet(GameIni.cutOnInts(getString("GI_INTENSITY", lvl, setting), ',', ';'));
        LightMapper.giFallOffSet(GameIni.cutOnInts(getString("GI_FALLOFF", lvl, setting), ',', ';'));
        LightMapper.cameraVectorLight = getBoolean("CAMERA_VECTOR_LIGHT", lvl, setting, LightMapper.cameraVectorLight);
        LightMapper.bwGI = getBoolean("GI_BW", lvl, setting, LightMapper.bwGI);
        LightMapper.bwTexGI = getBoolean("GI_BW_TEX", lvl, setting, LightMapper.bwTexGI);
        LightMapper.smoothMax = (int) (8192 * getFloat("PHONG_ANGLE", lvl, setting, 29.6631f) / 90);//29.6631f
        gs.doubleBright = getBoolean("DOUBLE_BRIGHT", lvl, setting, false);
        LightMapper.allRooms = getBoolean("LIGHTMAPPER_ALLROOMS", lvl, setting, LightMapper.allRooms);
        
        String rays = getString("LIGHT_RAYS", lvl, setting);
        if(rays!=null) LightMapper.setRays(StringTools.parseInt(rays));

        Player.arcadeJumpPhysics = getBoolean("ARCADE_JUMP_PHYSICS", lvl, setting, false);
        Player.walkSpeed = getInt("PLAYER_SPEED", lvl, setting, 150);

        loadLights(oldF, lvl);
        House house = HouseCreator.create(meshes, false, lvl.get("LIGHTMAP"), getBoolean("OPTIMIZE_LEVEL_MODEL", lvl, setting, true));

        Respawn start = null;
        String tmp = lvl.get("START");
        if(tmp != null) start = readPoints(tmp, house)[0];
        
        Respawn finish = null;
        tmp = lvl.get("FINISH");
        if(tmp != null) finish = readPoints(tmp, house)[0];

        Respawn[] enemies = null;
        tmp = lvl.get("ENEMIES");
        if(tmp != null) enemies = readPoints(tmp, house);
           
        int enemyCount = 0;
        if(enemies != null) enemyCount = enemies.length;
        tmp = lvl.get("ENEMY_COUNT");
        if(tmp != null) enemyCount = StringTools.parseInt(tmp);

        int frequency = 2000;
        tmp = getStringdef("FREQUENCY", lvl, setting, "NULL");
        if(!tmp.equals("NULL")) frequency = getInt("FREQUENCY", lvl, setting);
       
        boolean botsInLevel = enemies != null;
        if(botsInLevel) botsInLevel = enemies.length > 0;
       
        if(enemyCount>0 && botsInLevel) {
            BigZombie.texture = new MultyTexture(bigzombietex, false);
            Zombie.texture = new MultyTexture(zombietex, false);

            Zombie.model = Asset.getMeshImageDynamic(zombiemod, zombiescale, zombiescale, zombiescale);
            Zombie.model_height = Zombie.model.getAnimation().getMesh().maxY() - Zombie.model.getAnimation().getMesh().minY();

            BigZombie.model = Asset.getMeshImageDynamic(bigzombiemod, bigzombiescale, bigzombiescale, bigzombiescale);
            BigZombie.model_height = BigZombie.model.getAnimation().getMesh().maxY() - BigZombie.model.getAnimation().getMesh().minY();

            tmp = getStringdef("ZOMBIE_DRAW_MODES", lvl, setting, "NULL");
            if(!tmp.equals("NULL")) Asset.applyMeshEffects(Zombie.model.getMesh(), tmp);
            
            tmp = getStringdef("BIGZOMBIE_DRAW_MODES", lvl, setting, "NULL");
            if(!tmp.equals("NULL")) Asset.applyMeshEffects(BigZombie.model.getMesh(), tmp);
           
        } else {
            Zombie.texture = null;
            Zombie.model = null;
            BigZombie.texture = null;
            BigZombie.model = null;
        }

       
        Scene scene = new Scene(width, height, house, start, finish, enemies, enemyCount, frequency,main,
                getInt("ZOMBIE_COUNT", lvl, setting, 5),
                getInt("GENERATE_BOTS_WAYS", lvl, setting, 1)==1);
        
        house.setSkybox(skybox);
        if(LightMapper.lights != null) house.sortLights(LightMapper.lights);
        scene.g3d.updateFov(Main.stdFov);
        
        tmp = lvl.get("NEED_TO_EXIT");
        if(tmp != null) scene.need = StringTools.cutOnStrings(lvl.get("NEED_TO_EXIT"), ',');
       
       
        scene.deleteAnPart=getInt("DELETE_IN_DISTANCE",lvl,setting,0)==1;
        scene.alwaysExit=getInt("ALWAYS_EXIT",lvl,setting,1)==1;
        GameScreen.mus=getStringdef("MUSIC",lvl,setting,"/music.mid");
        scene.exitWithoutWait=getInt("NEW_EXIT",lvl,setting,0)==1;

        if(getString("PLAYER_SHOP", lvl, setting) != null) {
            String sh = getNoLang("PLAYER_SHOP", lvl, setting);
            if (sh.equals("OFF")) gs.shopItems = null;
            else gs.shopItems = StringTools.cutOnInts(sh, ',');
        }

        String weatherType = getString("WEATHER", lvl, setting);
        if(weatherType != null) {
            int particlesCount = getInt("WEATHER_PARTICLES_COUNT", lvl, setting, 170);
            
            boolean isRain = weatherType.equals("RAIN");
            int[] colors = new int[4];
            colors[0] = 0xaabbdd; // Rain near colors
            colors[1] = 0x666666; // Rain far colors
            colors[2] = 0xffffff; // Snow near colors
            colors[3] = 0xcaccdf; // Snow far colors
            
            String userClrs = getString("WEATHER_COLOR_NEAR", lvl, setting);
            if(userClrs!=null) {
                int[] tmp2 = GameIni.cutOnInts(userClrs, ',', ';');
                colors[isRain?0:2] = (tmp2[0]<<16) | (tmp2[1]<<8) | tmp2[2];
            }
            userClrs = getString("WEATHER_COLOR_FAR", lvl, setting);
            if(userClrs!=null) {
                int[] tmp2 = GameIni.cutOnInts(userClrs, ',', ';');
                colors[isRain?1:3] = (tmp2[0]<<16) | (tmp2[1]<<8) | tmp2[2];
            }
            
            
            int[] lightingTimes = null;
            userClrs = getString("WEATHER_LIGHTING", lvl, setting); //Lightings
            if(userClrs!=null) lightingTimes = GameIni.cutOnInts(userClrs, ',', ';');
            
            int particlesSize = getInt("WEATHER_PARTICLES_SIZE", lvl, setting, isRain?22:4);
                    
            gs.wg = new WeatherGenerator(GameScreen.width, GameScreen.height * Main.getDisplaySize() / 100, 
                    particlesCount, 
                    colors[0], colors[1], colors[2], colors[3], 
                    lightingTimes!=null, lightingTimes,
                    isRain?4:particlesSize, isRain?particlesSize:22,
                    GameScreen.width / 2, GameScreen.height / 2, 
                    2, 10);
            gs.wg.generate = (byte) (isRain?1:2);
            gs.wg.createParticles();
        }

        String str = getString("WALK_SOUND", lvl, setting);

        if(str != null) {
            String[] snds = GameIni.cutOnStrings(str, ';', ',');
            Main.stepSound = snds;
            for (int i=0; i<snds.length; i++) {
                if (Main.isFootsteps && Main.footsteps != 0) Asset.getSound(snds[i]);
            }
        }

        
        Main.jumpSound = getString("JUMP_SOUND",lvl,setting);
        if (Main.isFootsteps && Main.footsteps != 0 && Main.jumpSound!=null) Asset.getSound(Main.jumpSound);
        
        gs.levelIni = lvl;

        String rev=getString("REVERB",lvl,setting);
        for(int i=0; i<house.getRooms().length; i++) {
            Room rm = house.getRooms()[i];
            str = lvl.get("WALK_SOUND_" + i);
            String reverb=getStringdef("REVERB_" + i,lvl,setting,rev);
                    
            if (str != null) {
                String[] snds = GameIni.cutOnStrings(str, ';', ',');
                rm.stepSound = snds;
                for (int i2 = 0; i2 < snds.length; i2++) {
                    if (Main.isFootsteps && Main.footsteps != 0) Asset.getSound(snds[i2]);
                }
            }
            rm.reverb=reverb;

            rm.jumpSound = lvl.get("JUMP_SOUND_" + i);
            if (rm.jumpSound!=null && Main.isFootsteps && Main.footsteps != 0) Asset.getSound(rm.jumpSound);

        }

        str = getString("PLAYER_ZOOM", lvl, setting);

        if (str != null) {
            int[] snds = GameIni.cutOnInts(str, ';', ',');
            Main.stdFov = snds[0];
            Main.zoomFov = snds[1];
            Main.hasZoom = false;
            if (snds[2] == 1) Main.hasZoom = true;
        }


        House.l2dRoomRendering = getBoolean("L2D_ROOM_RENDERING", lvl, setting, false);
        House.boxRoomTesting = getBoolean("BOX_ROOM_TESTING", lvl, setting, false);
        Player.fallDamage = getBoolean("FALL_DAMAGE", lvl, setting, true);
        gs.fullMoveLvl = getBoolean("LEVEL_END_CHANGEPOS", lvl, setting, false);

        if (getString("SPAWN_LIMITER", lvl, setting) != null) {
            scene.botLimiter = GameIni.cutOnInts(getString("SPAWN_LIMITER", lvl, setting), ',', ';');
        }

        str = getString("VIGNETTE", lvl, setting);
        if(str != null) {
            Image img = ImageResize.createImage(str, scene.g3d.getWidth(), scene.g3d.getHeight());
            int[] vignette = new int[img.getWidth() * img.getHeight()];
            img.getRGB(vignette, 0, width, 0, 0, img.getWidth(), img.getHeight());
            gs.vignette = new byte[vignette.length];
            
            for (int i = 0; i < vignette.length; i++) {
                gs.vignette[i] = (byte) ((vignette[i] & 0xff) - 128);
            }
            
        }
		
		scene.camPose = null;
		str = getString("SCENE_CAMERA_POSE", lvl, setting);
		if(str != null) scene.camPose = TPPose.loadPoseExternal(str);
        
        return scene;
   }

    private static Respawn[] readPoints(String file, House house) {
        if (file == null) {
            return null;
        } else {
            StringBuffer points = new StringBuffer();

            for (int i = 0; i < file.length(); ++i) {
                char ch;
                if((ch = file.charAt(i)) != 32) points.append(ch);
            }

            String[] poses = StringTools.cutOnStrings(points.toString(), ';');
            Respawn[] spawn = new Respawn[poses.length];

            for (int i=0; i<spawn.length; i++) {
                int[] vals = StringTools.cutOnInts(poses[i], ',');
                
                Vector3D pos = new Vector3D(vals[0], vals[1], vals[2]);
                spawn[i] = new Respawn(pos, house);
                if (vals.length >= 4) {
                    spawn[i].mode = -127;
                    spawn[i].cmode = (byte) (-127 + vals[3]);
                    if(vals.length == 5 && vals[4] == 1) spawn[i].respa = true;
                } else if (defaultOneBot) {
                    spawn[i].mode = -127;
                    spawn[i].cmode = -126;
                }
            }

            return spawn;
        }

    }
   
    public static final void loadLights(String file, GameIni lvl) {
        int pos = 0;
        if (file == null) return;
        Vector light = new Vector();

        for (; file.indexOf('[') >= 0;) {
            
            pos = file.indexOf('[');
            int endpos = file.indexOf(']');
            String objectType = file.substring(pos + 1, endpos);
            String object = file.substring(endpos + 1);
            endpos = object.indexOf('[');

            if (endpos >= 0) {
                file = object.substring(endpos);
                object = object.substring(0, endpos - 1);
            }

            GameIni obj = new GameIni(object,false);
            String key = obj.get("PRESET");

            if(objectType.indexOf("LIGHT") > -1) {

                String[] poses = StringTools.cutOnStrings(obj.get("POS"), ';');
                for (int count = 0; count < poses.length; count++) {
                    
                    int brightness = getInt("BRIGHTNESS", obj, Main.settings, key, lvl, 255);
                    
                    int[] color = new int[3];
                    String col = getString("COLOR", obj, Main.settings, key);
                    if(col != null) {
                        color = GameIni.cutOnInts(col, ',', ';');
                        color[0] = color[0] * brightness / 255;
                        if(color.length>1) {
                            color[1] = color[1] * brightness / 255;
                            color[2] = color[2] * brightness / 255;
                        } else {
                            int tmp = color[0];
                            color = new int[3];
                            color[1] = color[2] = color[0] = tmp;
                            
                        }
                        
                    } else {
                        color[0] = color[1] = color[2] = brightness;
                    }

                    Light l = new Light(StringTools.cutOnInts(poses[count], ','), color);
                    
                    String tmp;
                    if((tmp = getString("DIRECTION", obj, Main.settings, key)) != null) {
                        l.direction = new Vector3D(StringTools.cutOnInts(tmp, ','));
                    } else if((tmp = getString("LOOK_AT", obj, Main.settings, key)) != null) {
                        l.direction = new Vector3D(StringTools.cutOnInts(tmp, ','));
                        l.direction.add(-l.pos.x, -l.pos.y, -l.pos.z);
                    } 
                    
                    l.ceilingFix = (short) getInt("CEILING_FIX", obj, Main.settings, key, lvl, 0);
                    l.floorFix = (short) getInt("FLOOR_FIX", obj, Main.settings, key, lvl, 0);
                    l.part = (int) getInt("ROOM_ID", obj, Main.settings, key, lvl, -1);
                    light.addElement(l);
                }
            }

            if (endpos < 0) break;
        }
        
        
        LightMapper.lights = null;
        if (light.size() > 0) {
            Light[] l2 = new Light[light.size()];
            
            for (int i = 0; i < l2.length; i++) {
                l2[i] = (Light) light.elementAt(i);
            }
            
            LightMapper.lights = l2;
        }

    }
    
        public static final void loadObjects(String level, GameIni lvl, Scene scene, Player p, boolean onlyObjs) {
        int pos = 0;
        String file = StringTools.getStringFromResource(level);
        
        //RMS Spawners
        int spawnersCount = 0;
        if(scene.rmsBots==null) {
            scene.rmsBots = new Vector();
        }
        //RMS Objects
        if(scene.rmsObjects==null) {
            scene.rmsObjects = new Vector();
        }
        
        //Scripts engine base
        RoomObject lastAddedObject = null;
        int cmdIndex = -1;
        
        for (; file.indexOf('[') >= 0;) {
            pos = file.indexOf('[');
            int endpos = file.indexOf(']');
            String objectType = file.substring(pos + 1, endpos);
            String object = file.substring(endpos + 1);
            endpos = object.indexOf('[');
            if (endpos >= 0) {
                file = object.substring(endpos);
                object = object.substring(0, endpos - 1);
            }

            GameIni obj = new GameIni(object,false);
            String key = obj.get("PRESET");

            if( (cmdIndex=objectType.indexOf("ON_ACTIVATE")) > -1) {
                Vector objs = scene.getHouse().getObjects();
                
                if (cmdIndex > 0) {
                    if (objs.size() > 0) {
                        
                        String newName = objectType.substring(0, cmdIndex - 1);
                        for (int i = 0; i < objs.size(); i++) {
                            RoomObject robj = ((RoomObject) objs.elementAt(i));

                            if (robj != null && robj.name != null && robj.name.equals(newName)) {
                                robj.additional = createScript(object);
                            }
                        }
                        
                    }
                } else if (lastAddedObject != null) {
                    lastAddedObject.additional = createScript(object);
                }

                
            } else if( (cmdIndex=objectType.indexOf("ON_SPAWN")) > -1) {
                Vector objs = scene.getHouse().getObjects();
                
                    
                if(cmdIndex > 0) {
                    if (objs.size() > 0) {
                        
                        String newName = objectType.substring(0, cmdIndex - 1);
                        for (int i = 0; i < objs.size(); i++) {
                            RoomObject robj = ((RoomObject) objs.elementAt(i));

                            if (robj != null && robj.name != null && robj.name.equals(newName) && robj instanceof NPCSpawner) {
                                ((NPCSpawner) robj).onSpawn = createScript(object);
                            }
                        }
                        
                    }
                } else if (lastAddedObject != null && lastAddedObject instanceof NPCSpawner) {
                    ((NPCSpawner) lastAddedObject).onSpawn = createScript(object);
                }

                
            } else if( (cmdIndex=objectType.indexOf("ON_DEATH")) > -1) {
                Vector objs = scene.getHouse().getObjects();
                
                    
                if(cmdIndex > 0) {
                    if(objs.size() > 0) {
                        
                        String newName = objectType.substring(0, cmdIndex - 1);
                        for (int i = 0; i < objs.size(); i++) {
                            RoomObject robj = ((RoomObject) objs.elementAt(i));

                            if (robj != null && robj.name != null && robj.name.equals(newName) && robj instanceof NPC) {
                                ((NPC) robj).onDeath = createScript(object);
                            }
                        }
                        
                    }
                } else if (lastAddedObject != null && lastAddedObject instanceof NPC) {
                    ((NPC) lastAddedObject).onDeath = createScript(object);
                }

                
            } else if( (cmdIndex=objectType.indexOf("MESSAGE")) > -1) {
                Vector objs = scene.getHouse().getObjects();
                String msg = object.replace('\n', '@');;
                
                if (cmdIndex > 0) {
                    if (objs.size() > 0) {
                        
                        String newName = objectType.substring(0, cmdIndex - 1);
                        for (int i = 0; i < objs.size(); i++) {
                            RoomObject robj = ((RoomObject) objs.elementAt(i));

                            if (robj != null && robj.name != null && robj.name.equals(newName)) {
                                robj.message = msg;
                            }
                        }
                        
                    }
                } else if (lastAddedObject != null) {
                    lastAddedObject.message = msg;
                }

            } else if( (cmdIndex=objectType.indexOf("ERRMSG")) > -1) {
                Vector objs = scene.getHouse().getObjects();
                String msg = object.replace('\n', '@');
                
                if (cmdIndex > 0) {
                    if (objs.size() > 0) {
                        
                        String newName = objectType.substring(0, cmdIndex - 1);
                        for (int i = 0; i < objs.size(); i++) {
                            RoomObject robj = ((RoomObject) objs.elementAt(i));

                            if (robj != null && robj.name != null && robj.name.equals(newName)) {
                                robj.errMessage = msg;
                            }
                        }
                        
                    }
                } else if (lastAddedObject != null) {
                    lastAddedObject.errMessage = msg;
                }

            } else if (objectType.indexOf("OBJECT") > -1) {

                String[] poses = StringTools.cutOnStrings(obj.get("POS"), ';');
                for (int count = 0; count < poses.length; count++) {

                    int[] ps = StringTools.cutOnInts(poses[count], ',');

                    KeyObject t = new KeyObject(new Vector3D(ps[0], ps[1], ps[2]));

                    loadRM(obj, (RoomObject) t, lvl, key);
                    lastAddedObject = t;
                    scene.getHouse().addObject((RoomObject) t);
                }
            } else if (objectType.indexOf("TELEPORT") > -1) {

                String[] poses = StringTools.cutOnStrings(obj.get("POS"), ';');
                for (int count = 0; count < poses.length; count++) {

                    int[] ps = StringTools.cutOnInts(poses[count], ',');
                    int[] ps2 = StringTools.cutOnInts(getString("NEW_POS", obj, Main.settings, key), ',');

                    Teleport t = new Teleport(new Vector3D(ps[0], ps[1], ps[2]), new Vector3D(ps2[0], ps2[1], ps2[2]));
                    loadRM(obj, (RoomObject) t, lvl, key);

                    t.pRot = getInt("ROT", obj, Main.settings, key, 0);
                    lastAddedObject = t;
                    scene.getHouse().addObject((RoomObject) t);
                }
            } else if (objectType.indexOf("NPC") > -1) {

                String[] poses = StringTools.cutOnStrings(obj.get("POS"), ';');
                for (int count = 0; count < poses.length; count++) {

                    int[] ps = StringTools.cutOnInts(poses[count], ',');
                    NPC t = loadNPC(obj, key, ps);

                    String str;
                    NPCSpawner spawn = null;
                    if ((str = getString("SPAWN", obj, Main.settings, key)) != null) {
                        int[] sets = StringTools.cutOnInts(str, ',');
                        spawn = new NPCSpawner(new Vector3D(ps[0], ps[1], ps[2]));
                        spawn.canSpawn = sets[0];
                        if (sets.length >= 2) spawn.respawnIn = sets[1];
                        if (sets.length == 3) spawn.rot = sets[2];
                        //spawn.bot=t;
                        loadRM(obj, (RoomObject) spawn, lvl, key, "SPAWNER_");
                        
                        spawn.visiblityChecker = getInt("SPAWNER_IGNORE_VISIBLITY_CHECK", obj, Main.settings, key, 
                                spawn.visiblityChecker?1:0)==1;
                        
                        spawn.visiblityChecker = getInt("SPAWNER_SAVE", obj, Main.settings, key, 
                                spawn.visiblityChecker?1:0)==1;
                        
                        spawn.distanceToSpawn = getLong("SPAWNER_DISTANCE", obj, Main.settings, key, spawn.distanceToSpawn);
                        
                        if (getString("SPAWNER_ON_SPAWN", obj, Main.settings, key) != null) {
                            spawn.onSpawn = StringTools.cutOnStrings(getString("SPAWNER_ON_SPAWN", obj, Main.settings, key), ',');
                        }
                        
                        if (getString("SPAWNER_COUNT", obj, Main.settings, key) != null) {
                            spawn.bots = new NPC[Math.max(1, getInt("SPAWNER_COUNT", obj, Main.settings, key))];
                            spawn.bots[0] = t;
                            for (int i = 1; i < spawn.bots.length; i++) {
                                spawn.bots[i] = loadNPC(obj, key, ps);
                                spawn.bots[i].name += "_" + String.valueOf(i);
                            }
                        } else {
                            spawn.bots = new NPC[1];
                            spawn.bots[0] = t;
                            spawn.bots[0].name += "_1";
                        }
                    }
                    
                    
                    boolean saveInRms = getInt("SAVE_IN_RMS", obj, Main.settings, key, 0)==1;
                    
                    if(saveInRms) spawnersCount++;
                    loadRM(obj, (RoomObject) t, lvl, key);
                    if (spawn == null) {
                        if(saveInRms) {
                            t.spawnerId = spawnersCount-1;
                            scene.rmsBots.addElement(t);
                        }
                        lastAddedObject = t;
                        scene.getHouse().addObject((RoomObject) t);
                    } else {
                        if(saveInRms) {
                            spawn.spawnerId = spawnersCount-1;
                            scene.rmsBots.addElement(spawn);
                        }
                        lastAddedObject = spawn;
                        scene.getHouse().addObject((RoomObject) spawn);
                    }

                }
            } else if (objectType.indexOf("SPRITE") > -1) {
                String[] poses = StringTools.cutOnStrings(obj.get("POS"), ';');
                
                for(int count = 0; count < poses.length; count++) {
                    String[] texs = StringTools.cutOnStrings(getString("TEX", obj, Main.settings, key), ',');

                    Texture[] textures = new Texture[texs.length];
                    for (int ti = 0; ti < texs.length; ti++) {
                        textures[ti] = Asset.getTextureNM(texs[ti]);
                    }

                    SpriteObject spr = new SpriteObject();

                    spr.spr.setTextures(textures);

                    spr.spr.setScale(getInt("SCALE", obj, Main.settings, key,spr.spr.scale));
                    spr.addsz = getInt("ADDSZ", obj, Main.settings, key, spr.addsz);

                    String tmp=getString("OFFSET", obj, Main.settings, key, "NULL");
                    if (!tmp.equals("NULL")) {
                        if (tmp.equals("MID")) {
                            spr.spr.setOffset(0, -spr.spr.getHeight() / 2);
                        } else if (tmp.equals("UP")) {
                            spr.spr.setOffset(0, -spr.spr.getHeight());
                        }
                    }

                    spr.spr.animation_speed = getFloat("ANIMATION_SPEED", obj, Main.settings, key, spr.spr.animation_speed);

                    tmp = getString("MODE", obj, Main.settings, key, "NULL");
                    spr.spr.setMode(tmp);
                    tmp = getString("FOG", obj, Main.settings, key, "NULL");
                    spr.spr.setFog(tmp);
                    spr.spr.color = getInt("COLOR", obj, Main.settings, key, spr.spr.color);
                    
                    tmp = getString("CUTOFF", obj, Main.settings, key, "NULL");
                    if (!tmp.equals("NULL")) {
                        if (tmp.equals("UP")) spr.spr.cutoff = (byte) 1;
                    }

                    tmp = getString("FLICKER", obj, Main.settings, key, "NULL");
                    if (!tmp.equals("NULL")) {
                        int[] flc = StringTools.cutOnInts(tmp, ',');
                        boolean[] flicker = new boolean[flc.length];
                        for (int fi = 0; fi < flc.length; fi++) {
                            flicker[fi] = flc[fi]==1;
                        }
                        spr.flicker = flicker;
                    }

                    tmp = getString("COLOR_ANIM", obj, Main.settings, key, "NULL");
                    if (!tmp.equals("NULL")) {
                        int[] flc = StringTools.cutOnInts(tmp, ',');
                        spr.colorFlicker = flc;
                    }
                    
                    int[] ps = StringTools.cutOnInts(poses[count], ',');
                    spr.spr.getPosition().set(ps[0], ps[1], ps[2]);
                    loadRM(obj, (RoomObject) spr, lvl, key);
                    lastAddedObject = spr;
                    scene.getHouse().addObject((RoomObject) spr);

                }
            } else if (objectType.indexOf("MESH") > -1) {

                String[] poses = StringTools.cutOnStrings(obj.get("POS"), ';');
                for (int count = 0; count < poses.length; count++) {
                    int[] ps = StringTools.cutOnInts(poses[count], ',');
                    float scale = getFloat("MODEL_SCALE", obj, Main.settings, key, 1.0f);
                    scale = getFloat("SCALE", obj, Main.settings, key, scale);

                    Mesh[] meshes = Asset.getMeshes(getString("MODEL", obj, Main.settings, key), scale, scale, scale);
                    MultyTexture mt = new MultyTexture(getString("TEX", obj, Main.settings, key),false);

                    MeshObject mobj = new MeshObject(meshes, mt, ps[0], ps[1], ps[2], 
                            getBoolean("REALTIME_LIGHTING", obj, Main.settings, key, lvl, false));

                    loadRM(obj, (RoomObject) mobj, lvl, key);
                    String modes = getString("DRAW_MODES", obj, Main.settings, key);

                    Mesh meshz = mobj.animation.getMesh();
                    if (modes != null) Asset.applyMeshEffects(meshz, modes);

                    int rotY = getInt("ROTATE_Y", obj, Main.settings, key, 0);
                    rotY = getInt("ROT", obj, Main.settings, key, rotY);
                    if (rotY != 0) mobj.getCharacter().getTransform().rotY(rotY);

                    mobj.animSpeed = getFloat("ANIMATION_SPEED", obj, Main.settings, key, mobj.animSpeed);

                    mobj.getCharacter().setCollision(getBoolean("PHYSICS", obj, Main.settings, key, true));
                    mobj.getCharacter().setCollidable(getBoolean("COLLIDER", obj, Main.settings, key, true));
                    mobj.getCharacter().setUpdatable(getBoolean("NOCOLL_UPDATE", obj, Main.settings, key, mobj.getCharacter().isCollidable()));

                    mobj.setFriction(getFloat("FRICTION", obj, Main.settings, key, mobj.getFriction()));

                    mobj.setHp(getInt("HP", obj, Main.settings, key, mobj.getHp()));

                    float cscalex = getFloat("COLLISION_SCALE_X", obj, Main.settings, key, 1.0f);
                    float cscaley = getFloat("COLLISION_SCALE_Y", obj, Main.settings, key, 1.0f);

                    int xsize = mobj.animation.getMesh().maxX() - mobj.animation.getMesh().minX();
                    int zsize = mobj.animation.getMesh().maxZ() - mobj.animation.getMesh().minZ();
                    int ysize = mobj.animation.getMesh().maxY() - mobj.animation.getMesh().minY();

                    mobj.setCharacterSize((int) ((float) ((xsize) / 2 * cscalex)), (int) ((float) ((zsize) / 2 * cscalex)), (int) ((float) (ysize * cscaley)));

                    String tmp = getString("LIFT_POS", obj, Main.settings, key, "NULL");
                    if (!tmp.equals("NULL")) {

                        String[] poses2 = StringTools.cutOnStrings(tmp, ';');

                        mobj.poses = new Vector3D[poses2.length + 1];
                        mobj.poses[0] = new Vector3D(ps);

                        for (int i = 0; i < poses2.length; i++) {
                            int[] ps2 = StringTools.cutOnInts(poses2[i], ',');
                            mobj.poses[i + 1] = new Vector3D(ps2);
                        }
                        mobj.liftCycled = getBoolean("LIFT_LOOP", obj, Main.settings, key, lvl, false);

                        tmp = getString("LIFT_TIMER", obj, Main.settings, key, "NULL");
                        if (!tmp.equals("NULL")) {
                            mobj.timeToMove = StringTools.cutOnInts(tmp, ',');
                        } else {
                            int speed = getInt("LIFT_SPEED", obj, Main.settings, key, lvl, 2000);
                            mobj.timeToMove = new int[mobj.poses.length - (mobj.liftCycled ? 0 : 1)];
                            for (int i = 0; i < mobj.timeToMove.length; i++) {
                                int i2 = i + 1;
                                if (i2 >= mobj.poses.length) {
                                    i2 = 0;
                                }
                                Vector3D a = mobj.poses[i];
                                Vector3D b = mobj.poses[i2];
                                mobj.timeToMove[i] = (int) (Math.sqrt(
                                        (double) (a.x - b.x) * (double) (a.x - b.x)
                                        + (double) (a.y - b.y) * (double) (a.y - b.y)
                                        + (double) (a.z - b.z) * (double) (a.z - b.z))
                                        * 1000 / speed);
                            }
                        }

                        mobj.lookAtDirect(mobj.poses[1].x, mobj.poses[1].z);
                        mobj.activable = getInt("ACTIVABLE", obj, Main.settings, key, lvl, 1) == 1; //wtf
                        mobj.clickable = getInt("CLICKABLE", obj, Main.settings, key, lvl, 1) == 1;
                        mobj.liftReUse = getInt("LIFT_REUSE", obj, Main.settings, key, lvl, 1) == 1;
                        mobj.disactivateOnEnd = getInt("LIFT_DISACTIVABLE_ON_END", obj, Main.settings, key, lvl, 0) == 1;
                        mobj.liftSmoothMove = getInt("LIFT_SMOOTH_MOVE", obj, Main.settings, key, lvl, 0);
                        mobj.liftRotateToMove = getInt("LIFT_SMOOTH_ROTATE", obj, Main.settings, key, lvl, 0) == 1;
                        mobj.liftCanBePaused = getInt("LIFT_CAN_BE_PAUSED", obj, Main.settings, key, lvl, 0) == 1;
                        tmp = getString("LIFT_PLAYER_FOLLOW", obj, Main.settings, key);
                        if (tmp != null) mobj.playerFollowLift = new Vector3D(StringTools.cutOnInts(tmp, ','));
                    }
                    mobj.addsz = getInt("ADDSZ", obj, Main.settings, key, mobj.addsz);

                    tmp = getString("ANIMATION_TYPE", obj, Main.settings, key, "NULL");
                    if (!tmp.equals("NULL")) {
                        int at = StringTools.parseInt(tmp);
                        if (at < 2) mobj.animType = at;

                        if (at == 2) {
                            mobj.animType = 2;
                            mobj.state = MeshObject.CLOSE;
                            mobj.activable = true;
                            mobj.clickable = true;
                        }
                    }
                    
                    mobj.ignoreWeaponRayCast = getInt("IGNORE_WEAPON_RAYCAST", obj, Main.settings, key, lvl, 0) == 1;
                    mobj.precCol = getInt("PRECISE_COLLISION", obj, Main.settings, key, 0) == 1;
                    
                    lastAddedObject = mobj;
                    scene.getHouse().addObject((RoomObject) mobj);
                }
            } else if (objectType.indexOf("SHOP") > -1) {

                String[] poses = StringTools.cutOnStrings(obj.get("POS"), ';');
                for (int count = 0; count < poses.length; count++) {
                    int[] ps = StringTools.cutOnInts(poses[count], ',');
					
                    int[] items = null;
                    String[] files = null;
                    int[] prices = null;
                    
                    String tmp = getString("SHOP", obj, Main.settings, key, "NULL");
                    if (!tmp.equals("NULL")) items = StringTools.cutOnInts(tmp, ',');
					
                    tmp = getString("PRICES", obj, Main.settings, key, "NULL");
                    if (!tmp.equals("NULL")) prices = StringTools.cutOnInts(tmp, ',');
                    
                    tmp = getString("FILES", obj, Main.settings, key, "NULL");
                    if (!tmp.equals("NULL")) files = StringTools.cutOnStrings(tmp, ',');
                    
                    ShopObject mobj = new ShopObject(items, prices, files, ps[0], ps[1], ps[2]);

                    loadRM(obj, (RoomObject) mobj, lvl, key);
                    lastAddedObject = mobj;
                    scene.getHouse().addObject((RoomObject) mobj);
                }
            } else if (objectType.indexOf("LEVELCHANGE") > -1) {

                String[] poses = StringTools.cutOnStrings(obj.get("POS"), ';');
                for (int count = 0; count < poses.length; count++) {
                    int[] ps = StringTools.cutOnInts(poses[count], ',');

                    int[] ps2 = GameIni.createPos(getString("START_POS", obj, Main.settings, key), ',');
					
					int levelNumber = getInt("LEVEL", obj, Main.settings, key);

                    LVLChange mobj = new LVLChange(ps[0], ps[1], ps[2], new Vector3D(ps2[0], ps2[1], ps2[2]), levelNumber);

                    mobj.pRot = getInt("ROT", obj, Main.settings, key, mobj.pRot);
                    mobj.saveMus = getInt("SAVEMUSIC", obj, Main.settings, key, 0) == 1;
                    mobj.fullMove = getInt("FULLMOVE", obj, Main.settings, key, lvl, 0) == 1;
                    mobj.showLoadScreen = getInt("SHOW_LOAD_SCREEN", obj, Main.settings, key, lvl, 1) == 1;

                    mobj.activable = true;
                    loadRM(obj, (RoomObject) mobj, lvl, key);
                    lastAddedObject = mobj;
                    scene.getHouse().addObject((RoomObject) mobj);

                }
            } else if (objectType.indexOf("IMAGE2D") > -1) {

                String[] poses = StringTools.cutOnStrings(obj.get("POS"), ';');
                for (int count = 0; count < poses.length; count++) {
                    int[] ps = StringTools.cutOnInts(poses[count], ',');
                    Image img = null;
                    try {
                        String tmp = getString("IMAGE", obj, Main.settings, key, "NULL");
                        if (!tmp.equals("NULL")) {
                            String scaleType = getString("RESIZE", obj, Main.settings, key, "PROPORTIONAL3D");
                            img = Image.createImage(tmp);
                            float scalex = getFloat("SCALE_X", obj, Main.settings, key, 1.0f);
                            float scaley = getFloat("SCALE_Y", obj, Main.settings, key, 1.0f);


                            if (scaleType.equalsIgnoreCase("PROPORTIONAL3D")) {
                                img = ImageResize.bilinearScaleImage(img,
                                        (float) (scene.g3d.height * scalex / img.getHeight()),
                                        (float) (scene.g3d.height * scaley / img.getHeight()));
                            } else if (scaleType.equalsIgnoreCase("FULL3DSCREEN")) {
                                img = ImageResize.bilinearScaleImage(img,
                                        (float) (scene.g3d.width * scalex / img.getWidth()),
                                        (float) (scene.g3d.height * scaley / img.getHeight()));
                            } else if (scaleType.equalsIgnoreCase("PROPORTIONAL")) {
                                img = ImageResize.bilinearScaleImage(img,
                                        (float) (scene.g3d.height * scalex / img.getHeight()),
                                        (float) (scene.g3d.height * 100 / Main.displaySize * scaley / img.getHeight()));
                            } else if (scaleType.equalsIgnoreCase("FULLSCREEN")) {
                                img = ImageResize.bilinearResizeImage(img,
                                        scene.g3d.width, scene.g3d.height * 100 / Main.displaySize);
                            } else if (scaleType.equalsIgnoreCase("ORIGINALSIZE")) {
                                img = ImageResize.bilinearScaleImage(img, scaley, scaley);
                            }

                        }
                    } catch (Exception exp) {
                    }
                    long timeout = getLong("TIMEOUT", obj, Main.settings, key, 1000l);

                    Image2D mobj = new Image2D(new Vector3D(ps[0], ps[1], ps[2]), img, timeout);

                    loadRM(obj, (RoomObject) mobj, lvl, key);
                    lastAddedObject = mobj;
                    scene.getHouse().addObject((RoomObject) mobj);

                }
            }
            
            if(lastAddedObject!=null && lastAddedObject.destroyOnUse && lastAddedObject.reloadDestroy && lastAddedObject.name==null) {
                scene.rmsObjects.addElement(lastAddedObject);
            }

            if (endpos < 0) break;

        }
    }

    public static NPC loadNPC(GameIni obj, String key, int[] ps) {
        float scale = getFloat("SCALE", obj, Main.settings, key, 1.0f);
        scale = getFloat("MODEl_SCALE", obj, Main.settings, key, scale);
        
        int hp = getInt("HP", obj, Main.settings, key, 100);

        MultyTexture mt = null;
        String str = getString("TEX", obj, Main.settings, key, "NULL");
        if (!str.equals("NULL")) mt = new MultyTexture(getString("TEX", obj, Main.settings, key),false);

        String modes = getString("DRAW_MODES", obj, Main.settings, key);
        MeshImage meim = null;
        str = getString("MODEL", obj, Main.settings, key, "NULL");
        if (!str.equals("NULL")) meim = Asset.getMeshImageDynamic(str, scale, scale, scale);
        
        NPC t = new NPC(new Vector3D(ps[0], ps[1], ps[2]), meim, hp, mt);
        
        String mode = getString("MODE", obj, Main.settings, key, "NULL");
        String fog = getString("FOG", obj, Main.settings, key, "NULL");
        int color = getInt("COLOR", obj, Main.settings, key, 0);

        t.damageFront = loadSpriteAnim("DAMAGE_ANIM", obj, key, mode, fog, color);
        t.deathFront = loadSpriteAnim("DEATH_ANIM", obj, key, mode, fog, color);
        if(t.deathFront != null) t.deathFront.limiter = true;
        
        t.attackFront = loadSpriteAnim("ATTACK_ANIM", obj, key, mode, fog, color);
        t.stayFront = loadSpriteAnim("STAY_ANIM", obj, key, mode, fog, color);
        if(t.stayFront != null) {
            t.model_height = t.stayFront.getHeight() * ((t.stayFront.textures[0].rImg.scale < 2) ? 2 : 1);
            t.setCharacterSize(t.model_height);
        }
        
        t.walkFront = loadSpriteAnim("WALK_ANIM", obj, key, mode, fog, color);
        
        t.damageBack = loadSpriteAnim("DAMAGE_BACK_ANIM", obj, key, mode, fog, color);
        t.deathBack = loadSpriteAnim("DEATH_BACK_ANIM", obj, key, mode, fog, color);
        if(t.deathBack != null) t.deathBack.limiter = true;
        
        t.attackBack = loadSpriteAnim("ATTACK_BACK_ANIM", obj, key, mode, fog, color);
        t.stayBack = loadSpriteAnim("STAY_BACK_ANIM", obj, key, mode, fog, color);
        t.walkBack = loadSpriteAnim("WALK_BACK_ANIM", obj, key, mode, fog, color);
        
        t.damageSide = loadSpriteAnim("DAMAGE_SIDE_ANIM", obj, key, mode, fog, color);
        t.deathSide = loadSpriteAnim("DEATH_SIDE_ANIM", obj, key, mode, fog, color);
        if(t.deathSide != null) t.deathBack.limiter = true;
        
        t.attackSide = loadSpriteAnim("ATTACK_SIDE_ANIM", obj, key, mode, fog, color);
        t.staySide = loadSpriteAnim("STAY_SIDE_ANIM", obj, key, mode, fog, color);
        t.walkSide = loadSpriteAnim("WALK_SIDE_ANIM", obj, key, mode, fog, color);

        Mesh meshz = null;

        if (t.meshImage != null) {
            meshz = t.meshImage.getMesh();
            meshz.setTexture(mt);
        }

        t.animspeed = getInt("ANIMSPEED", obj, Main.settings, key, t.animspeed);
        t.attackanimspeed = getInt("ATTACKANIMSPEED", obj, Main.settings, key, t.attackanimspeed);
        t.damage = getInt("DAMAGE", obj, Main.settings, key, t.damage);
        t.attackradius = getFloat("ATTACKRADIUS", obj, Main.settings, key, t.attackradius);
        t.jumpheight = getInt("JUMPHEIGHT", obj, Main.settings, key, t.jumpheight);
        t.jumpspeed = getFloat("JUMPSPEED", obj, Main.settings, key, t.jumpspeed);
        t.speed = getInt("SPEED", obj, Main.settings, key, t.speed);
        t.reacttimer = getInt("REACTTIMER", obj, Main.settings, key, t.reacttimer);
        t.attacktimer = getInt("ATTACKTIMER", obj, Main.settings, key, t.attacktimer);
        t.whenEnemyIsFar = getInt("AI", obj, Main.settings, key, t.whenEnemyIsFar);
        t.whenEnemyIsNear = getInt("ENEMYREACTION", obj, Main.settings, key, t.whenEnemyIsNear);
        t.attackState = getInt("ATTACKTRIGGER", obj, Main.settings, key, t.attackState);
        t.fraction = getInt("FRACTION", obj, Main.settings, key, t.fraction);
        t.fragsOnDeath = getInt("FRAGSONDEATH", obj, Main.settings, key, t.fragsOnDeath);
        t.fragsOnAnyDeath = getInt("FRAGS_ON_ANY_DEATH", obj, Main.settings, key, t.fragsOnAnyDeath);
        t.moneyOnDeath = getInt("MONEYONDEATH", obj, Main.settings, key, t.moneyOnDeath);
        t.damageSleepTime = getInt("SLEEPONDAMAGE", obj, Main.settings, key, t.damageSleepTime);
        t.maxEnemyDistance = getLong("MAXENEMYDISTANCE", obj, Main.settings, key, t.maxEnemyDistance);
        t.visiblityCheck = getInt("VISIBILITY_CHECK", obj, Main.settings, key, 0)==1;
        t.deathFall = (byte) getInt("FALL_ON_DEATH", obj, Main.settings, key, 1);
        t.hasBlood = (byte) getInt("HAS_BLOOD", obj, Main.settings, key, 1) == 1;
        
        /*String observable = getString("OBSERVABLE", obj, Main.settings, key);
        if(observable!=null) t.observable = GameIni.cutOnInts(observable, ',',';');*/
        
        str = getString("ATTACK", obj, Main.settings, key);
        if(str != null) {
            if(str.equalsIgnoreCase("all")) t.toAttack = null;
            else t.toAttack = GameIni.cutOnInts(str, ',', ';');
        }

        str = getString("FOLLOW", obj, Main.settings, key);
        if(str != null) {
            if(str.equalsIgnoreCase("all")) t.toFollow = null;
            else t.toFollow = GameIni.cutOnInts(str, ',', ';');
        }
        
        t.inPlayerTeam = getInt("IN_PLAYER_TEAM", obj, Main.settings, key, t.inPlayerTeam?1:0) == 1;

        str = getString("FRIENDLY_FIRE", obj, Main.settings, key);
        if(str != null) t.friendlyFire = GameIni.cutOnInts(str, ',', ';');
        
        str = getString("ATTACKONDAMAGE", obj, Main.settings, key);
        if (str != null) {
            if (StringTools.parseInt(str) == 1) t.unicalEnemies = new Vector();
        }

        str = getString("ATTACKONDAMAGE_OP", obj, Main.settings, key);
        if(str != null) {
            if(StringTools.parseInt(str) == 1) {
                if (t.unicalEnemies == null) t.unicalEnemies = new Vector();
                t.attackOnDamageOnlyPlayer = true;
            }
        }

        int rotY = getInt("ROT", obj, Main.settings, key, 0);
        if (rotY != 0) t.getCharacter().rotY(rotY);

        if ((str = getString("MUZZLE_FLASH_POS", obj, Main.settings, key)) != null) {
            t.muzzleFlashPos = new Vertex(GameIni.cutOnInts(str, ',', ';'));
            t.muzzleFlash = new Sprite(Asset.getTexture(getString("MUZZLE_FLASH", obj, Main.settings, key)), getInt("MUZZLE_FLASH_SCALE", obj, Main.settings, key));
            if (t.muzzleFlash.textures[0].rImg.alphaMixing) t.muzzleFlash.mode = 3;
            t.muzzleFlash.fog = false;
            t.lastAttack = 0L;
            t.muzzleFlashTimer = getInt("MUZZLE_FLASH_TIMER", obj, Main.settings, key);
        }
        
        str = getString("ON_DEATH", obj, Main.settings, key);
        if(str != null) t.onDeath = loadScriptFromFile(str);

        if (modes != null && meshz != null) Asset.applyMeshEffects(meshz, modes);
        
        t.getCharacter().setCollision(getInt("PHYSICS", obj, Main.settings, key, 1) == 1);
        t.getCharacter().setCollidable(getInt("COLLIDER", obj, Main.settings, key, 1) == 1);
        t.getCharacter().setUpdatable(getInt("NOCOLL_UPDATE", obj, Main.settings, key, t.getCharacter().isCollidable()?1:0) == 1);
        
        float cscalex = t.getCharacter().getRadius() * getFloat("COLLISION_SCALE_X", obj, Main.settings, key, 1.0f);
        float cscaley = t.getCharacter().getHeight()* getFloat("COLLISION_SCALE_Y", obj, Main.settings, key, 1.0f);
        
        t.setCharacterSize((int)cscalex, (int) cscaley);

        t.initStaySprite();
        return t;
    }
    
    public static Sprite loadSpriteAnim(String name, GameIni obj, String key,
            String mode, String fog, int color) {
        String str = getString(name, obj, Main.settings, key);
        if(str == null) return null;
        
        String[] texs = GameIni.cutOnStrings(str, ',', ';');
        Texture[] textures = new Texture[texs.length - 2];
        
        for(int i = 0; i < textures.length; i++) {
            textures[i] = Asset.getTextureNM(texs[i+2]);
        }
        
        Sprite spr = new Sprite(textures, StringTools.parseInt(texs[1]), StringTools.parseFloat(texs[0]));
        
        spr.setMode(mode);
        spr.setFog(fog);
        spr.color = color;
        
        return spr;
    }

    public static void loadRM(GameIni txt, RoomObject obj, GameIni lvl, String key) {
        loadRM(txt, obj, lvl, key, "");
    }

    public static void loadRM(GameIni txt, RoomObject obj, GameIni lvl, String key, String bN) {

        obj.activable = getBoolean(bN + "ACTIVABLE", txt, Main.settings, key, obj.activable);
        obj.clickable = getBoolean(bN + "CLICKABLE", txt, Main.settings, key, obj.clickable);
        obj.singleUse = getBoolean(bN + "SINGLEUSE", txt, Main.settings, key, obj.singleUse);
        obj.destroyOnUse = getBoolean(bN + "DESTROYONUSE", txt, Main.settings, key, obj.destroyOnUse);

        obj.name = getString(bN + "NAME", txt, Main.settings, key, obj.name);
        obj.desc = getString(bN + "DESC", txt, Main.settings, key, obj.desc);

        obj.need = StringTools.cutOnStrings(getString(bN + "NEED", txt, Main.settings, key), ',');

        String tmp = getString(bN + "ON_ACTIVATE", txt, Main.settings, key);
        if(tmp != null) obj.additional = loadScriptFromFile(tmp);

        obj.alwaysActivate = getBoolean(bN + "ALWAYS_ACTIVATE", txt, Main.settings, key, obj.alwaysActivate);

        tmp = getString(bN + "MESSAGE", txt, Main.settings, key);
        if(tmp != null) obj.message = DialogScreen.loadTextFromFile(tmp);

        tmp = getString(bN + "ERRMESSAGE", txt, Main.settings, key);
        if(tmp != null) obj.errMessage = DialogScreen.loadTextFromFile(tmp);

        obj.radius = getLong(bN + "RADIUS", txt, Main.settings, key, obj.radius);
        
        obj.messageTimeOut = getLong(bN + "MESSAGETIMEOUT", txt, Main.settings, key, obj.messageTimeOut);
        obj.messageType = getInt(bN + "MESSAGETYPE", txt, Main.settings, key, obj.messageType);
        obj.errMessageTimeOut = getLong(bN + "ERRMESSAGETIMEOUT", txt, Main.settings, key, obj.errMessageTimeOut);
        obj.errMessageType = getInt(bN + "ERRMESSAGETYPE", txt, Main.settings, key, obj.errMessageType);

        obj.messageDelay = getLong(bN + "MSGDELAY", txt, Main.settings, key, obj.messageDelay);
        obj.errMessageDelay = getLong(bN + "ERRMSGDELAY", txt, Main.settings, key, obj.errMessageDelay);

        tmp = getString(bN + "REUSETIMER", txt, Main.settings, key);
        if(tmp != null) {
            obj.timeToReset = StringTools.parseLong(tmp);
            obj.hideWhenUnusable = true;
        }

        obj.hideWhenUnusable = getBoolean(bN + "HIDEREUSE", txt, Main.settings, key, obj.hideWhenUnusable);


        tmp = getString(bN + "NEEDTOPOINT", txt, Main.settings, key);
        if(tmp != null) {
            if (tmp.equals("1")) {
                obj.needToPoint = true;
            } else if (tmp.equals("2")) {
                obj.needToPoint = true;
                obj.squarePoint = true;
            }
        }

        if(obj.needToPoint == true && 
                (obj instanceof SpriteObject || obj instanceof MeshObject || obj instanceof NPC)) {
            obj.dynamicPoint = true;
        }

        obj.dynamicPoint = getBoolean(bN + "DYNAMICPOINT", txt, Main.settings, key, obj.dynamicPoint);

        if(obj.needToPoint && !obj.dynamicPoint) {
            if(obj instanceof SpriteObject) {
                SpriteObject spr = (SpriteObject) obj;
                obj.pointRadius = spr.spr.getWidth() / 2;
                obj.pointHeight = spr.spr.getHeight() / 2;
                obj.pointOffset = new Vector3D(0, spr.spr.getHeight() / 2 + spr.spr.offsetY, 0);
            } else if(obj instanceof MeshObject) {
                MeshObject mo = (MeshObject) obj;

                int sizex = (mo.animation.getMesh().maxX() - mo.animation.getMesh().minX());
                int sizey = (mo.animation.getMesh().maxY() - mo.animation.getMesh().minY());
                int sizez = (mo.animation.getMesh().maxZ() - mo.animation.getMesh().minZ());

                obj.pointRadius = (sizex + sizez) / 4;
                obj.pointHeight = sizey / 2;
                obj.pointOffset = new Vector3D(
                        mo.animation.getMesh().maxX() - sizex / 2,
                        mo.animation.getMesh().maxY() - sizey / 2,
                        mo.animation.getMesh().maxZ() - sizez / 2
                );

            } else if(obj instanceof NPC) {
                NPC mo = (NPC) obj;

                int sizex = (mo.meshImage.getMesh().maxX() - mo.meshImage.getMesh().minX());
                int sizey = (mo.meshImage.getMesh().maxY() - mo.meshImage.getMesh().minY());
                int sizez = (mo.meshImage.getMesh().maxZ() - mo.meshImage.getMesh().minZ());

                obj.pointRadius = (sizex + sizez) / 4;
                obj.pointHeight = sizey / 2;
                obj.pointOffset = new Vector3D(
                        mo.meshImage.getMesh().maxX() - sizex / 2,
                        mo.meshImage.getMesh().maxY() - sizey / 2,
                        mo.meshImage.getMesh().maxZ() - sizez / 2
                );

            }

        }

        tmp = getString(bN + "POINTRADIUS", txt, Main.settings, key);
        if(tmp != null) {
            obj.pointRadius = StringTools.parseInt(tmp);
            obj.pointHeight = obj.pointRadius;
        }

        obj.pointHeight = getInt(bN + "POINTHEIGHT", txt, Main.settings, key, obj.pointHeight);

        tmp = getString(bN + "POINTOFFSET", txt, Main.settings, key);
        if (tmp != null) {
            int[] ps = StringTools.cutOnInts(tmp, ',');
            obj.pointOffset = new Vector3D(ps[0], ps[1], ps[2]);
        }

        tmp = getString(bN + "SOUND_ON_ACTIVATE", txt, Main.settings, key);
        if (tmp != null) {
            obj.sound = tmp;
            if (Main.isSounds && Main.sounds != 0) {
                Asset.getSound(tmp);
                //if(Main.audio3D) Audio3DEngine.create3DSoundSource(obj,tmp);
            }
        }

        obj.visible = getBoolean(bN + "VISIBLE", txt, Main.settings, key, obj.visible);

        obj.activateOnlyOne = getBoolean(bN + "ACTIVATE_ONLY_THIS", txt, Main.settings, key, lvl, obj.activateOnlyOne);
        obj.reloadDestroy = getBoolean(bN + "DESTROY_ON_LEVEL_RELOAD", txt, Main.settings, key, lvl, obj.reloadDestroy);

        tmp = getString(bN + "ROOM_ID", txt, Main.settings, key);
        if (tmp != null) obj.setNewPart(StringTools.parseInt(tmp));
    }

    private static String getString(String s, GameIni s1, GameIni s2) {
        return s1.getDef(s, s2.get(s));
    }

    private static String getNoLang(String s, GameIni s1, GameIni s2) {
        return s1.getNoLang(s, s2.getNoLang(s));
    }

    private static int getInt(String s, GameIni s1, GameIni s2) {
        return getInt(s,s1,s2,0);
    }

    private static int getInt(String s, GameIni s1, GameIni s2, int def) {
        return s1.getInt(s, s2.getInt(s,def));
    }
    
    private static boolean getBoolean(String s, GameIni s1, GameIni s2, boolean def) {
        return s1.getInt(s, s2.getInt(s,def?1:0)) == 1;
    }

    private static float getFloat(String s, GameIni s1, GameIni s2) {
        return getFloat(s,s1,s2,0);
    }

    private static String getString(String s, GameIni s1, GameIni s2, String key) {
        return s1.getDef(s, key==null?s2.get(s):s2.getDef(key, s, s2.get(s)));
    }

    private static String getString(String s, GameIni s1, GameIni s2, String key, String def) {
        return s1.getDef(s, key==null?s2.getDef(s, def):s2.getDef(key,s, s2.getDef(s, def) ));
    }

    private static String getStringdef(String s, GameIni s1, GameIni s2, String def) {
        return s1.getDef(s, s2.getDef(s, def));
    }

    private static int getInt(String s, GameIni s1, GameIni s2, String key) {
        return getInt(s,s1,s2,key,0);
    }

    private static int getInt(String s, GameIni s1, GameIni s2, String key, int def) {
        String tmp = getString(s,s1,s2,key);
        if(tmp != null) return StringTools.parseInt(tmp);
        return def;
    }
    
    private static boolean getBoolean(String s, GameIni s1, GameIni s2, String key, boolean def) {
        return getInt(s, s1, s2, key, def?1:0) == 1;
    }
    
    private static boolean getBoolean(String s, GameIni s1, GameIni s2, String key, GameIni lvl, boolean def) {
        return getInt(s, s1, s2, key, lvl, def?1:0) == 1;
    }

    private static int getInt(String s, GameIni s1, GameIni s2, String key, GameIni lvl, int def) {
        return s1.getInt(s, key==null?lvl.getInt(s, s2.getInt(s, def)):s2.getInt(key, s, lvl.getInt(s, s2.getInt(s, def)) ));
    }

    private static float getFloat(String s, GameIni s1, GameIni s2, float def) {
        return s1.getFloat(s, s2.getFloat(s,def));
    }

    private static float getFloat(String s, GameIni s1, float def) {
        return s1.getFloat(s,def);
    }

    private static long getLong(String s, GameIni s1, GameIni s2, String key) {
        return getLong(s,s1,s2,key,0);
    }

    private static long getLong(String s, GameIni s1, GameIni s2, String key, long def) {
        String tmp = getString(s,s1,s2,key);
        if(tmp != null) return StringTools.parseLong(tmp);
        return def;
    }

    private static float getFloat(String s, GameIni s1, GameIni s2, String key) {
        return getFloat(s,s1,s2,key,0);
    }

    private static float getFloat(String s, GameIni s1, GameIni s2, String key, float def) {
        String tmp = getString(s,s1,s2,key);
        if(tmp != null) return StringTools.parseFloat(tmp);
        return def;
    }


    public static String[] createScript(String str) {
        Vector tmp = new Vector();
        String[] strs = StringTools.cutOnStrings(str, '\n');

        for (int i = 0; i < strs.length; ++i) {
            if (strs[i] != null) {
                strs[i] = strs[i].trim();
                if (strs[i].indexOf("//") != 0 && !strs[i].equals(" ")) {
                    if (strs[i].indexOf("//") > 0) strs[i] = strs[i].substring(0, strs[i].indexOf("//"));
                    
                    
                    if ((strs[i].charAt(0) == '}' || strs[i].charAt(0) == '{') && strs[i].length() > 1) {
                        tmp.addElement(strs[i].substring(1, strs[i].length()));
                        tmp.addElement(strs[i].substring(0, 1));
                    } else if ((strs[i].charAt(strs[i].length() - 1) == '}' || strs[i].charAt(strs[i].length() - 1) == '{') && strs[i].length() > 1) {
                        tmp.addElement(strs[i].substring(0, strs[i].length() - 1));
                        tmp.addElement(strs[i].substring(strs[i].length() - 1, strs[i].length()));
                    } else {
                        tmp.addElement(strs[i]);
                    }
                }
            }
        }

        strs = new String[tmp.size()];
        tmp.copyInto(strs);
        return strs;
    }
    
    public static String[] loadScriptFromFile(String text) {
        if(text==null) return null;
        
        if(text.charAt(0)!='/' || !text.toLowerCase().endsWith(".txt")) return StringTools.cutOnStrings(text, ';');
        
        text = StringTools.getStringFromResource(text);
        return createScript(text);
    }
    
}
