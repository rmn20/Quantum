package code.Rendering;

import code.utils.Asset;
import code.Rendering.Meshes.Mesh;
import code.utils.GameIni;
import code.utils.StringTools;
import code.utils.WeaponCreator;

public class MultyTexture {

    public Texture[] textures;

    MultyTexture() {}

    public MultyTexture(String files, boolean perspectiveCorrect) {
        String[] texList;
        texList = GameIni.cutOnStrings(files, ',', ';');

        textures = new Texture[texList.length];

        for(int i=0; i<texList.length; i++) {

            String[] anims = StringTools.cutOnStrings(texList[i], ':');
            
            if(anims.length==1) {
                textures[i] = Asset.getTexture(anims[0]);
                textures[i].setPerspectiveCorrection(perspectiveCorrect);
            } else {
                textures[i] = Asset.getTexture(anims[1]);
                textures[i].animation_speed=Float.parseFloat(anims[0].trim());
                textures[i].animMIP = new Texture[anims.length-1];
                for(int x=1; x<anims.length; x++) {
                    textures[i].animMIP[x-1]=Asset.getTexture(anims[x]);
                }
            
            
                textures[i].setPerspectiveCorrection(perspectiveCorrect);
            
            }
        }
    }
    
    
    public MultyTexture(String[] texList) {

        textures = new Texture[texList.length];

        for(int i = 0; i < texList.length; i++) {
            System.out.println("Loading Texture:" + i);
            String[] texInfo = GameIni.cutOnStrings(texList[i], ',', ';');

            
            String[] animation = StringTools.cutOnStrings(texInfo[0], ':');
            textures[i] = Asset.getTexture(animation[animation.length>1?1:0].trim());
            if(animation.length>1) {
                textures[i].animation_speed=Float.parseFloat(animation[0].trim());
                textures[i].animMIP = new Texture[animation.length-1];
                
                for(int i2=1;i2<animation.length;i2++) {
                    textures[i].animMIP[i2-1]=Asset.getTexture(animation[i2].trim());
                }
            }
            textures[i].setPerspectiveCorrection(true);
            
            if(texInfo.length>=2) {
                if(!texInfo[1].toLowerCase().trim().equals("std")) { 
                    textures[i].drawmode=(byte)StringTools.parseInt(texInfo[1]);
                }
            }
            
            if(texInfo.length>=3) textures[i].addsz=StringTools.parseInt(texInfo[2]);
            if(texInfo.length>=4) textures[i].setPerspectiveCorrection(StringTools.parseInt(texInfo[3])==1);
            if(texInfo.length>=5) textures[i].castShadow=StringTools.parseInt(texInfo[4])==1;
            if(texInfo.length>=6) textures[i].collision=StringTools.parseInt(texInfo[5])==1;
        }
    }

    public MultyTexture(Texture tex) {
        textures = new Texture[1];
        textures[0] = tex;
    }
    
    public MultyTexture(int i) {
        textures = new Texture[i];
    }

    
    public void updateAnimation() {
        for(int i=0;i<textures.length;i++) {
            textures[i].updateAnimation();
        }
    }

}
