package code.Rendering;

import code.utils.Asset;

public class Texture {

    public boolean perspectiveCorrection;
    public RawImage rImg;
    public ByteRawImage brImg;
    public RawImage[] mip = null;
    public RawImage[] brMip = null;
    public byte drawmode = DirectX7.standartDrawmode;
    public int addsz = 0;
    public boolean castShadow = true;
    public boolean collision = true;

    public Texture[] animMIP;
    public float animation_speed = 1.0f;

    Texture() {
    }

    public Texture(RawImage ri, boolean perspectiveCorrection, ByteRawImage bri) {
        setTexture(ri,perspectiveCorrection,bri);
    }
    
    void setTexture(RawImage ri, boolean perspectiveCorrection, ByteRawImage bri) {
        this.perspectiveCorrection = perspectiveCorrection;
        rImg = ri;
        drawmode = DirectX7.standartDrawmode;
        brImg = bri;
    }

    public int[] getPixels() {
        return this.rImg.img;
    }

    public byte getDrawMode() {
        return this.drawmode;
    }

    public void setDrawMode(byte dr) {
        drawmode = dr;
    }

    public void setPerspectiveCorrection(boolean perspectiveCorrection) {
        this.perspectiveCorrection = perspectiveCorrection;
    }

    public static Texture createTexture(String file) {
        RawImage rimg = Asset.getRawImage(file);
        return new Texture(rimg,false,null);
    }

    public void updateAnimation() {
        if(animMIP == null) return;
        long time = System.currentTimeMillis();
        time = time % (long) (animMIP.length * 1000 / animation_speed);
        int anim_index = (int) (time * animation_speed / 1000) % animMIP.length;

        rImg = animMIP[anim_index].rImg;
        mip = animMIP[anim_index].mip;
        brMip = animMIP[anim_index].brMip;
    }
}
