package code.Rendering.Meshes;

import code.Gameplay.Map.Light;
import code.Gameplay.Map.LightMapper;
import code.Gameplay.Map.Room;
import code.Math.MathUtils;
import code.Math.Matrix;
import code.Rendering.DirectX7;
import code.Rendering.MultyTexture;
import code.Rendering.RenderObject;
import code.Rendering.RenderObjectBuffer;
import code.Rendering.TMPElement;
import code.Rendering.Texture;
import code.Rendering.Vertex;
import java.util.Vector;

public class Mesh {

    private MultyTexture texture = null;
    private Vertex[] vertices;
    private RenderObject[] polygons;
    static RenderObjectBuffer buffer = new RenderObjectBuffer();
    static Matrix tmpMatrix = new Matrix();

    public Mesh() {}

    public Mesh(Vertex[] vertices, RenderObject[] polygons) {
        this.vertices = vertices;
        this.polygons = polygons;
    }

    public Mesh(Vertex[] vertices, RenderObject[] polygons, MultyTexture tex) {
        this.vertices = vertices;
        this.polygons = polygons;
        this.texture = tex;
    }

    public void optimize() {
        int old = vertices.length;
        Vector buf = new Vector();
        for(int i = 0; i < vertices.length; i++) {
            Vertex v = vertices[i];
            int s = search(buf, v);
            if(s == -1) {
                buf.addElement(v);
            } else {
                replace(polygons, v, (Vertex) buf.elementAt(s));
            }
        }

        vertices = new Vertex[buf.size()];
        buf.copyInto(vertices);
    }

    private static int search(Vector vertices, Vertex vertex) {
        final int E = 10;
        for(int i = 0; i < vertices.size(); i++) {
            Vertex v = (Vertex) vertices.elementAt(i);
            if(v == vertex) {
                continue;
            }
            if(Math.abs(v.x - vertex.x) < E
                    && Math.abs(v.y - vertex.y) < E
                    && Math.abs(v.z - vertex.z) < E) {
                return i;
            }
        }
        return -1;
    }

    private static void replace(RenderObject[] polygons, Vertex oldVertex, Vertex newVertex) {
        for(int i = 0; i < polygons.length; i++) {
            if(polygons[i] instanceof Polygon3V) {
                Polygon3V poly = (Polygon3V) polygons[i];
                if(poly.a == oldVertex) {
                    poly.a = newVertex;
                }
                if(poly.b == oldVertex) {
                    poly.b = newVertex;
                }
                if(poly.c == oldVertex) {
                    poly.c = newVertex;
                }
            }
            if(polygons[i] instanceof Polygon4V) {
                Polygon4V poly = (Polygon4V) polygons[i];
                if(poly.a == oldVertex) {
                    poly.a = newVertex;
                }
                if(poly.b == oldVertex) {
                    poly.b = newVertex;
                }
                if(poly.c == oldVertex) {
                    poly.c = newVertex;
                }
                if(poly.d == oldVertex) {
                    poly.d = newVertex;
                }
            }
        }
    }

    public void destroy() {
        texture = null;

        for(int i=0; i<polygons.length; i++) {
            polygons[i] = null;
        }
        polygons = null;

        for(int i = 0; i<vertices.length; i++) {
            vertices[i] = null;
        }
        vertices = null;
    }

    public void setTexture(Texture texture) {
        this.texture = new MultyTexture(texture);
        
        for(int i=0; i<polygons.length; i++) {
            if(this.polygons[i] instanceof Polygon4V) {
                ((Polygon4V) (polygons[i])).tex = 0;
            } else {
                ((Polygon3V) (polygons[i])).tex = 0;
            }

        }

    }

    public void setTexture(MultyTexture texture) {
        this.texture = texture;
    }

    public void resetTexture() {
        texture = null;
    }

    public int maxX() {
        int max = Integer.MIN_VALUE;

        for(int i=0; i<vertices.length; i++) {
            if(vertices[i].x > max) max = vertices[i].x;
        }

        return max;
    }
    
    public int maxY() {
        int max = Integer.MIN_VALUE;

        for(int i=0; i<vertices.length; i++) {
            if(vertices[i].y > max) max = vertices[i].y;
        }

        return max;
    }
    
    public int maxZ() {
        int max = Integer.MIN_VALUE;

        for(int i=0; i<vertices.length; i++) {
            if(vertices[i].z > max) max = vertices[i].z;
        }

        return max;
    }

    public int minX() {
        int min = Integer.MAX_VALUE;

        for(int i=0; i<vertices.length; i++) {
            if(vertices[i].x < min) min = vertices[i].x;
        }

        return min;
    }
    
    public int minY() {
        int min = Integer.MAX_VALUE;

        for(int i=0; i<vertices.length; i++) {
            if(vertices[i].y < min) min = vertices[i].y;
        }

        return min;
    }
    
    public int minZ() {
        int min = Integer.MAX_VALUE;

        for(int i=0; i<vertices.length; i++) {
            if(vertices[i].z < min) min = vertices[i].z;
        }

        return min;
    }

    public MultyTexture getTexture() {
        return texture;
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public RenderObject[] getPolygons() {
        return polygons;
    }

    void render(DirectX7 g3d) {
        render(g3d, 0, 0, g3d.width, g3d.height);
    }


    void render(DirectX7 g3d, int x1, int y1, int x2, int y2) {
        final RenderObject[] elements = this.polygons;

        buffer.addRenderObjects(elements, texture, x1, y1, x2, y2);
        buffer.sort(0, buffer.getSize() - 1);
        TMPElement[] buffer2 = Mesh.buffer.getBuffer();

        int i = Mesh.buffer.getSize() - 1;
        for(; i>=0; i--) {
            TMPElement element = buffer2[i];
            element.obj.renderFast(g3d, element.tex);
        }
        buffer.reset();
    }
    
    public static void resetBuffer() {
        buffer.resetTex();
        buffer.reset();
    }
    
    public void applySz() {
       final RenderObject[] elements = this.polygons;
       
       for(int i=0;i<elements.length;i++) {
           RenderObject obj=elements[i];
           if(obj instanceof Polygon4V) obj.sz+=texture.textures[((Polygon4V)obj).tex].addsz;
           else if(obj instanceof Polygon3V) obj.sz+=texture.textures[((Polygon3V)obj).tex].addsz;
       }
    }

    //Смещение z меша
    public void increaseMeshSz(int z) {
        for (int i=0; i<polygons.length; i++) polygons[i].sz += z;
    }
    
    public void setAnimation(Morphing animation) {
        animation.interpolation(vertices);
        //animation.interpolationNormals(polygons);
    }

    public void recalculateNormals(Matrix transform) {
        Vertex normal = null;
        if(transform != null) {
            normal = new Vertex(0,0,0);
            tmpMatrix.set(transform);
            tmpMatrix.setPosition(0,0,0);
        }
        
        for(int i=0; i<polygons.length; i++) {
            RenderObject ro =  polygons[i];
            
            if(ro instanceof Polygon3V) {
                Polygon3V p = (Polygon3V)ro;
                p.calculateNormals(p.a, p.b, p.c);
            } else if(ro instanceof Polygon4V) {
                Polygon4V p = (Polygon4V)ro;
                p.calculateNormals(p.a, p.b, p.c);
            }
            
            if(transform != null) {
                normal.set(ro.nx, ro.ny, ro.nz);
                normal.transform(tmpMatrix);
                ro.nx = (short) normal.x;
                ro.ny = (short) normal.y;
                ro.nz = (short) normal.z;
            }
        }
    }
    
    public void rotateNormals(Matrix transform) {
        Vertex normal = new Vertex(0,0,0);
        tmpMatrix.set(transform);
        tmpMatrix.setPosition(0, 0, 0);
        
        for(int i=0; i<polygons.length; i++) {
            RenderObject ro =  polygons[i];
            
            normal.set(ro.nx, ro.ny, ro.nz);
            normal.transform(tmpMatrix);
            ro.nx = (short) normal.x;
            ro.ny = (short) normal.y;
            ro.nz = (short) normal.z;
        }
    }

    public void updateLighting(Matrix transform, boolean smoothNormals, Room room) {
        
        Light[] lights=room.lights;
        if(lights==null) return;
        Vertex normal = new Vertex(0,0,0);
        DirectX7.transform(this, transform);
        tmpMatrix.set(transform);
        tmpMatrix.setPosition(0,0,0);
        
        for(int i=0;i<polygons.length;i++) {
            RenderObject ro=polygons[i];
            if(ro instanceof LightedPolygon3V) {
                LightedPolygon3V p = (LightedPolygon3V)ro;
                normal.set(p.nx,p.ny,p.nz);
                normal.transform(tmpMatrix);
                p.la=getVertexLit(p.la,normal,p.a,room);
                p.lb=getVertexLit(p.lb,normal,p.b,room);
                p.lc=getVertexLit(p.lc,normal,p.c,room);
            } else if(ro instanceof LightedPolygon4V) {
                LightedPolygon4V p = (LightedPolygon4V)ro;
                normal.set(p.nx,p.ny,p.nz);
                normal.transform(tmpMatrix);
                p.la=getVertexLit(p.la,normal,p.a,room);
                p.lb=getVertexLit(p.lb,normal,p.b,room);
                p.lc=getVertexLit(p.lc,normal,p.c,room);
                p.ld=getVertexLit(p.ld,normal,p.d,room);
            }
        }
        
    }
    
    private static final Vertex getNormal(Mesh mesh, Vertex v) {
        int count=0;
        final Vertex norm=new Vertex();
        for(int i=0;i<mesh.polygons.length;i++) {
            RenderObject ro=mesh.polygons[i];
            if(ro instanceof LightedPolygon3V) {
                LightedPolygon3V p = (LightedPolygon3V)ro;
                if( 
                        (p.a.sx==v.sx && p.a.sy==v.sy && p.a.rz==v.rz) ||
                        (p.b.sx==v.sx && p.b.sy==v.sy && p.b.rz==v.rz) ||
                        (p.c.sx==v.sx && p.c.sy==v.sy && p.c.rz==v.rz) ) {
                norm.x=norm.x+p.nx; norm.y=norm.y+p.ny; norm.z=norm.z+p.nz; count++;
            }
            } else if(ro instanceof LightedPolygon4V) {
                LightedPolygon4V p = (LightedPolygon4V)ro;
                if( 
                        (p.a.sx==v.sx && p.a.sy==v.sy && p.a.rz==v.rz) ||
                        (p.b.sx==v.sx && p.b.sy==v.sy && p.b.rz==v.rz) ||
                        (p.c.sx==v.sx && p.c.sy==v.sy && p.c.rz==v.rz) ||
                        (p.d.sx==v.sx && p.d.sy==v.sy && p.d.rz==v.rz) ) {
                norm.x=norm.x+p.nx; norm.y=norm.y+p.ny; norm.z=norm.z+p.nz; count++;
            }
            }
        }
        if(count!=0) {norm.x/=count; norm.y/=count; norm.z/=count;}
        return norm;
    }
    
    private byte getVertexLit(byte orig, Vertex norm, Vertex v, Room room) {
        long lit=0;
        Light[] lights=room.lights;
        Vertex tmp=new Vertex(v.sx,v.sy,v.rz);
        
        
        for(int i=0; i<lights.length; i++) {
            Light light=lights[i];
            long distSqr=LightMapper.distanceSqr(tmp,light.pos);
            long intensity=(light.color[0]+light.color[1]+light.color[2])/3*LightMapper.sqrMeter/Math.max(1,distSqr)*8;
            
            if(distSqr<0) intensity=0;
            if(intensity>1) {
                if(light.direction==null) {
                    intensity=intensity*MathUtils.calcLight(norm.sx,norm.sy,norm.rz, v.sx-light.pos.x,v.sy-light.pos.y,v.rz-light.pos.z)/255;
                } else {
                    intensity=intensity*MathUtils.calcLight(light.direction.x,light.direction.y,light.direction.z, 
                            v.sx-light.pos.x,v.sy-light.pos.y,v.rz-light.pos.z)/255;  
                    intensity=intensity*MathUtils.calcLight(norm.sx,norm.sy,norm.rz, v.sx-light.pos.x,v.sy-light.pos.y,v.rz-light.pos.z)/255;
                }
            }
            
            if(intensity>1) lit+=intensity;
        }
        
        return (byte)Math.max(Math.min(127,lit-128+LightMapper.ambientLightMid),-128);
    }
}

