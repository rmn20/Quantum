package code.Rendering.Meshes;

import code.Rendering.Vertex;

public final class Morphing {
    public static final int fp = 10, FP = 1 << fp;

    private short[][] animation;
    //private byte[][] normals;
    private Mesh mesh;
    private int frame = 0;
	public boolean morphEnabled = true;

    public static short[][] create(Mesh[] meshes, int start, int end) {
        short[][] anim = new short[end-start][];
        int c = 0;

        for(int i = start; i < end; ++i) {
            Mesh mesh = meshes[i];
            Vertex[] verts = mesh.getVertices();
            short[] meshVerts = new short[verts.length * 3];

            for(int x = 0; x < verts.length; ++x) {
                Vertex vert = verts[x];
                meshVerts[x * 3] = (short) vert.x;
                meshVerts[x * 3 + 1] = (short) vert.y;
                meshVerts[x * 3 + 2] = (short) vert.z;
            }

            anim[c] = meshVerts;
            c++;
        }

        return anim;
    }
    
    /*public static byte[][] createNormals(Mesh[] meshes) {
        RenderObject[] m1Pols = meshes[0].getPolygons();
        byte[][] norms = new byte[meshes.length][m1Pols.length*3];
        
        Vertex[] backup = new Vertex[meshes[0].getVertices().length];
        Vertex[] verts = meshes[0].getVertices();
        for(int i=0; i<verts.length; i++) {
            backup[i] = new Vertex(verts[i]);
        }
        
        for(int i=0; i<meshes.length; i++) {
            Mesh mesh = meshes[i];
            Vertex[] meshVerts = mesh.getVertices();
            byte[] meshNorms = norms[i];
            
            for(int x=0; x<verts.length; x++) {
                verts[x].set(meshVerts[x]);
            }
            
            mesh.recalculateNormals(null);

            for(int x=0; x<m1Pols.length; x++) {
                RenderObject ro = m1Pols[x];
                meshNorms[x*3] = (byte) ((ro.nx * 127) >> RenderObject.normal_fp);
                meshNorms[x*3+1] = (byte) ((ro.ny * 127) >> RenderObject.normal_fp);
                meshNorms[x*3+2] = (byte) ((ro.nz * 127) >> RenderObject.normal_fp);
            }
        }

        for(int i=0; i<verts.length; i++) {
            verts[i].set(backup[i]);
        }
        
        return norms;
    }*/

    public Morphing() {
    }

    public Morphing(short[][] animation/*, byte[][] normals*/, Mesh mesh) {
        this.animation = animation;
        //this.normals = normals;
        this.mesh = mesh;
    }

    public final void destroy() {
        this.animation = null;
        this.mesh = null;
    }

    public short[][] getVertices() {
        return animation;
    }

    public int getMaxFrame() {
        return FP * animation.length;
    }

    public int getFrame() {
        return frame;
    }
    
    /*public boolean normalsExist() {
        return normals != null;
    }*/

    public final void setFrameNI(int frame) {
        final int maxFrame = getMaxFrame();
        while(frame < 0)
            frame += maxFrame;
        if(maxFrame != 0) frame %= maxFrame;
        else frame = 0;

        this.frame = frame;
    }

    public final void setFrame(int frame) {
        setFrameNI(frame);
        interpolation(mesh.getVertices());
        //if(normals != null) interpolationNormals(mesh.getPolygons());
    }

    void interpolation(Vertex[] versRes) {
        int aFrame = frame / FP;
        
        int af1 = 0, af2 = 0;
        if(animation.length != 0) {
            af1 = aFrame % animation.length;
            af2 = (aFrame + 1) % animation.length;
        }
        final short[] versA = animation[af1];
        final short[] versB = animation[af2];
        
        final int kinv = frame % FP;
        final int k = FP - kinv;
		
		boolean morphEnabled = this.morphEnabled;
        
        for(int i = 0; i < versRes.length; i++) {
            final int n = i * 3;
            final int ax = versA[n];
            final int ay = versA[n + 1];
            final int az = versA[n + 2];
			
			if(morphEnabled) {
				final int bx = versB[n];
				final int by = versB[n + 1];
				final int bz = versB[n + 2];

				versRes[i].set(
						(ax * k >> fp) + (bx * kinv >> fp),
						(ay * k >> fp) + (by * kinv >> fp),
						(az * k >> fp) + (bz * kinv >> fp)
				);
			} else {
				versRes[i].set(ax, ay, az);
			}
        }
    }

    /*void interpolationNormals(RenderObject[] pols) {
        int aFrame = frame / FP;
        
        final byte[] normsA = normals[aFrame % normals.length];
        final byte[] normsB = normals[(aFrame + 1) % normals.length];
        
        final int kinv = frame % FP;
        final int k = FP - kinv;
        
        for(int i = 0; i < pols.length; i++) {
            RenderObject ro = pols[i];
            final int n = i * 3;
            final int ax = normsA[n] * RenderObject.normal_FP / 127;
            final int ay = normsA[n + 1] * RenderObject.normal_FP / 127;
            final int az = normsA[n + 2] * RenderObject.normal_FP / 127;

            final int bx = normsB[n] * RenderObject.normal_FP / 127;
            final int by = normsB[n + 1] * RenderObject.normal_FP / 127;
            final int bz = normsB[n + 2] * RenderObject.normal_FP / 127;

            ro.nx = (short) ((ax * k >> fp) + (bx * kinv >> fp));
            ro.ny = (short) ((ay * k >> fp) + (by * kinv >> fp));
            ro.nz = (short) ((az * k >> fp) + (bz * kinv >> fp));
        }
    }*/

    public final Mesh getMesh() {
        return this.mesh;
    }

    public final short[][] getAnimation() {
        return this.animation;
    }
}
