package code.Rendering;

public class ByteRawImage {

    public byte[] img;
    public int w, h;
    public byte scale = 2;

    public ByteRawImage(byte[] rgb, int w2, int h2) {
        this.img = rgb;
        this.w = w2;
        this.h = h2;
    }
}
