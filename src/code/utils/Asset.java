package code.utils;

import code.Gameplay.Map.Room;
import code.Rendering.Meshes.Mesh;
import code.Rendering.Texture;
import code.Rendering.Meshes.Morphing;
import code.Rendering.Meshes.MeshImage;
import code.Rendering.RawImage;
import java.util.Hashtable;
import java.util.Enumeration;

// ? Хранилище
public class Asset {

    private static Hashtable table = new Hashtable();
    private static int repeat = 0;

    public static final void clear() {
        try {
            Enumeration elements = table.elements();
            while (elements.hasMoreElements() == true) {
                Object obj = elements.nextElement();
                if (obj instanceof Sound) {
                    ((Sound) (obj)).destroy();
                } else if (obj instanceof Mesh) {
                    ((Mesh) (obj)).destroy();
                } else if (obj instanceof Mesh[]) {
                    Mesh[] meshes = (Mesh[]) obj;
                    for (int xx = 0; xx < meshes.length; xx++) {
                        meshes[xx].destroy();
                    }
                }

            }
            table.clear();
            System.gc();
            repeat=0;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    
    public static final boolean desizeSomething() {
        try {
            Enumeration elements = table.elements();
            Enumeration keys = table.keys();
            
            RawImage rImg = null;
            while (elements.hasMoreElements() == true) {
                Object obj = elements.nextElement();
                String key = (String)keys.nextElement();
                if (obj instanceof RawImage && key.startsWith("RIMG_")) {
                    RawImage trImg = (RawImage)obj;
                    if(trImg.scale==0 || !trImg.isPalette) continue;
                    
                    if(rImg==null) rImg=trImg;
                    else if(trImg.scale>rImg.scale) rImg=trImg;
                }
            }
            
            if(rImg!=null && rImg.scale>0) {
                if (rImg.scale == 1) {
                    rImg.img = ImageResize.cubic2XHorDesize(rImg.img, rImg.w, rImg.h);
                    rImg.w /= 2;
                    rImg.scale = 0;
                } else {
                    rImg.img = ImageResize.cubic2XVertDesize(rImg.img, rImg.w, rImg.h);
                    rImg.h /= 2;
                    rImg.scale = 1;
                }
                return true;
            }
            
            return false;
            
        } catch(Throwable t) {
            return false;
        }
    }
    
    /*public static final void prefetchAudio() {
        try {
            Enumeration elements = table.elements();
            while (elements.hasMoreElements() == true) {
                Object obj = elements.nextElement();
                if (obj instanceof Sound) {
                    ((Sound) (obj)).player.prefetch();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/

    public static final Mesh[] getMeshes(String file, float scaleX, float scaleY, float scaleZ) {
        Mesh[] mesh = (Mesh[]) table.get("MESH_" + file + "_" + scaleX + "_" + scaleY + "_" + scaleZ);
        if (mesh == null) {

            mesh = Room.loadMeshes(file, scaleX, scaleY, scaleZ);

            table.put("MESH_" + file + "_" + scaleX + "_" + scaleY + "_" + scaleZ, mesh);
        }
        return mesh;
    }

    public static final Mesh getMeshCloneDynamic(String file, float scaleX, float scaleY, float scaleZ) {
        Mesh mesh = Room.loadMeshes(file, scaleX, scaleY, scaleZ)[0];
        return mesh;
    }

    public static final Mesh getMeshClone(String file, float scaleX, float scaleY, float scaleZ) {
        Mesh mesh = (Mesh) table.get("MESHCLONE_" + file + "_" + scaleX + "_" + scaleY + "_" + scaleZ);
        if (mesh == null) {

            mesh = Room.loadMeshes(file, scaleX, scaleY, scaleZ)[0];

            table.put("MESHCLONE_" + file + "_" + scaleX + "_" + scaleY + "_" + scaleZ, mesh);
        }
        return mesh;
    }

    public static final short[][] getAnimation(String file, float scaleX, float scaleY, float scaleZ) {
        short[][] mesh = (short[][]) table.get("ANIMATION_" + file + "_" + scaleX + "_" + scaleY + "_" + scaleZ);
        if (mesh == null) {
            Mesh[] meshes = getMeshes(file,scaleX,scaleY,scaleZ);
            mesh = Morphing.create(meshes, 0, meshes.length);
            table.put("ANIMATION_" + file + "_" + scaleX + "_" + scaleY + "_" + scaleZ, mesh);
        }
        
        return mesh;
    }
    
    /*public static final byte[][] getAnimNorms(String file, float scaleX, float scaleY, float scaleZ) {
        byte[][] mesh = (byte[][]) table.get("ANIMNORMS_" + file + "_" + scaleX + "_" + scaleY + "_" + scaleZ);
        if (mesh == null) {
            mesh = Morphing.createNormals(getMeshes(file,scaleX,scaleY,scaleZ));
            table.put("ANIMNORMS_" + file + "_" + scaleX + "_" + scaleY + "_" + scaleZ, mesh);
        }
        
        return mesh;
    }*/

    public static final Morphing getMorphing(String file, float scaleX, float scaleY, float scaleZ) {
        Morphing mesh = (Morphing) table.get("MORPHING_" + file + "_" + scaleX + "_" + scaleY + "_" + scaleZ);
        
        if (mesh == null) {
            mesh = new Morphing(
                    getAnimation(file, scaleX, scaleY, scaleZ), 
                    /*getAnimNorms(file, scaleX, scaleY, scaleZ),*/ 
                    getMeshClone(file, scaleX, scaleY, scaleZ));

            table.put("MORPHING_" + file + "_" + scaleX + "_" + scaleY + "_" + scaleZ, mesh);
        }
        
        return mesh;
    }

    public static final MeshImage getMeshImage(String file, float scaleX, float scaleY, float scaleZ) {
        MeshImage mesh = (MeshImage) table.get("MESHIMAGE_" + file + "_" + scaleX + "_" + scaleY + "_" + scaleZ);
        if (mesh == null) {

            mesh = new MeshImage(getMeshCloneDynamic(file, scaleX, scaleY, scaleZ), getMorphing(file, scaleX, scaleY, scaleZ));

            table.put("MESHIMAGE_" + file + "_" + scaleX + "_" + scaleY + "_" + scaleZ, mesh);
        }
        return mesh;
    }

    public static final MeshImage getMeshImageDynamic(String file, float scaleX, float scaleY, float scaleZ) {
        MeshImage mesh = new MeshImage(getMeshClone(file, scaleX, scaleY, scaleZ), getMorphing(file, scaleX, scaleY, scaleZ));
        return mesh;
    }

    public static final Texture getTexture(String file) {
        Texture texture = Texture.createTexture(file);
        texture.mip = (RawImage[])table.get("MIPMAPS_" + file);
        return texture;
    }

    public static final RawImage getRawImage(String file) {
        RawImage rImg = (RawImage) table.get("RIMG_" + file);

        if (rImg == null) {
            rImg = RawImage.createRawImage(file);
            table.put("RIMG_" + file, rImg);
            RawImage[] mips = null;
            if(Main.mipMapping) {
                RawImage rmImg1 = RawImage.createMipRawImage(rImg);
                if(rmImg1!=null) {
                    table.put("RIMGMIP1_" + file, rmImg1);
                    RawImage rmImg2 = RawImage.createMipRawImage(rImg);
                    if(rmImg2!=null) table.put("RIMGMIP2_" + file, rmImg2);
                    else {
                        rmImg2 = rmImg1;
                    }
                    mips = new RawImage[] {rImg,rmImg1,rmImg2};
                    table.put("MIPMAPS_" + file, mips);
                }
            }
        }
        return rImg;
    }

    public static final Texture getTextureNM(String file) {
        return Texture.createTexture(file);
    }

    public static final Sound getSound(String file) {
        if(Main.sounds == 0 || !Main.isSounds) return null;
        
        try {
            Sound sound = (Sound) table.get("SND_" + file);
            if (sound == null) {
                sound = new Sound(file);
                table.put("SND_" + file, sound);
            }
            return sound;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    
    public static final Sound getFootsteps(String file) {
        if(Main.footsteps == 0 || !Main.isFootsteps) return null;
        
        try {
            Sound sound = (Sound) table.get("SND_" + file);
            if (sound == null) {
                sound = new Sound(file);
                table.put("SND_" + file, sound);
            }
            return sound;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Asset() {
    }

    public static final void applyMeshEffects(Mesh[] var12, String effects) {

        applyMeshEffects(var12[0], effects);

    }

    public static final void applyMeshEffects(Mesh var12, String effects) {

        if (effects == null) {
            return;
        }
        String[] mods = GameIni.cutOnStrings(effects, ',', ';');
        Texture[] texs = var12.getTexture().textures;
        if (texs == null) {
            return;
        }
        int length = texs.length;
        if (mods.length < length) {
            length = mods.length;
        }

        for (int i = 0; i < length; i++) {
            if (texs[i] != null && mods[i] != null) {
                texs[i].setDrawMode((byte) Integer.parseInt(mods[i]));
            }
        }

    }

}
