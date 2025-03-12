package code.utils;

import code.AI.TPPose;
import code.Gameplay.Weapon;

public class WeaponCreator {

    public static Weapon createWeapon(int index) {
        Object[] file = GameIni.createGroups("/weapons.txt");
        String[] names = (String[])file[0];
        GameIni[] groups = (GameIni[])file[1];
        
        for(int i=0; i<names.length; i++) {
            
            if(names[i].equals(Integer.toString(index))) {
                GameIni obj = groups[i];

                float kw = 1.0F;
                if(obj.get("KW") != null) kw = obj.getFloat("KW");
                float kh = 1.0F;
                if(obj.get("KH") != null) kh = obj.getFloat("KH");
                
                Weapon wp = new Weapon(
                        obj.get("IMAGE"),
                        obj.get("FIRE"),
                        kw, kh,
                        obj.getInt("DAMAGE"),
                        obj.getInt("DELAY"),
                        obj.getInt("SHOTTIME"),
                        obj.getInt("TWOHANDS", 0) == 1,
                        obj.getInt("CAPACITY", 1),
                        obj.getInt("RELOADTIME"),
                        obj.getLong("DISTANCE"),
                        obj.getInt("PATRONBUY", 1) == 1,
                        index);

                wp.shoot = obj.get("SHOOT_SOUND");
                if(wp.shoot != null && Main.isSounds && Main.sounds > 0) Asset.getSound(wp.shoot);
                
                wp.reload = obj.get("RELOAD_SOUND");
                if(wp.reload != null && Main.isSounds && Main.sounds > 0) Asset.getSound(wp.reload);

                wp.newanim = obj.getInt("NEWFIREANIM", wp.newanim?1:0) == 1;

                wp.canShoot = obj.getInt("CANSHOOT", wp.canShoot?1:0) == 1;
                
                String tmp = obj.get("MELEEANIM");
                if(tmp != null && tmp.equals("1")) {
                    wp.meleeAnim = true;
                    wp.attackIntensity = 5.0F;
                }
                
                wp.ignoreSightOnDraw = obj.getInt("IGNORE_SIGHT_IMAGE", wp.ignoreSightOnDraw?1:0) == 1;
                
                wp.fileSight = obj.get("SIGHT_IMAGE");
                wp.fileSightWeapon = obj.get("SIGHT_WEAPON_IMAGE");
                
                wp.filePatron = obj.get("PATRON_ICON");
                wp.filePatronLow = obj.get("LOW_PATRON_ICON");

                if(obj.get("X_POS") != null) {
                    wp.customPos = true;
                    wp.customPosX = obj.getFloat("X_POS");
                }
                
                wp.centreAlign = obj.getInt("CENTRE_ALIGHT", wp.centreAlign?1:0) == 1;
                wp.debugWeapon = obj.getInt("DEBUG_WEAPON", wp.debugWeapon?1:0) == 1;

                wp.leftHand = obj.getInt("LEFT_HAND", wp.leftHand?1:0) == 1;
                wp.shootLight = obj.getInt("SHOOT_LIGHT", wp.shootLight?1:0) == 1;

                String str = obj.get("ZOOM");
                if(str != null) {
                    int[] snds = GameIni.cutOnInts(str, ';', ',');
                    wp.stdFov = snds[0];
                    wp.zoomFov = snds[1];
                    wp.hasZoom = snds[2] == 1;
                }
                
                str = obj.get("PLAYER_POSE");
                if(str != null && TPPose.meshPoses != null) {
                    TPPose[] poses = TPPose.meshPoses;
                    
                    for(int x=0; x<poses.length; x++) {
                        
                        if(poses[x].poseName.equals(str)) {
                            wp.playerPose = x;
                            break;
                        }
                    }
                }
				
                str = obj.get("AMMO_PRICE_PERCENTAGE");
				if(str != null) wp.ammoPriceFactor = StringTools.parseFloat(str) / 100f;
				
				wp.ammoBundled = obj.getInt("AMMO_BUNDLED", wp.ammoBundled);
				wp.ammoInShop = obj.getInt("AMMO_IN_SHOP", wp.ammoInShop);
				
				wp.lowPatronAmount = obj.getInt("LOW_PATRON_AMOUNT", wp.lowPatronAmount);
                
                return wp;
            }
            
        }

        return null;
    }

}
