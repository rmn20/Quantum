package code.Rendering;

import code.Rendering.Meshes.Polygon3V;
import code.Rendering.Meshes.Polygon4V;

/**
 *
 * @author DDDENISSS
 */
public class RenderObjectBuffer {
    
    private TMPElement[] buffer = new TMPElement[0];
    private int size = 0;

    public RenderObjectBuffer() {
    }
    
    public void addRenderObjects(final RenderObject[] objects, final Texture tex, final int x1, final int y1, final int x2, final int y2) {
        final int count = objects.length;
        for(int i=0; i<count; i++) {
            RenderObject obj = objects[i];
            if(obj.isVisible(x1, y1, x2, y2)) {
                if(size >= buffer.length) increaseBuffer();
                TMPElement el = buffer[size];
                el.obj = obj;
                el.tex = tex;
                size++;
            }
        }
    }
    
    public void addRenderObjects(final RenderObject[] objects, final int x1, final int y1, final int x2, final int y2) {
        final int count = objects.length;
        for(int i=0; i<count; i++) {
            RenderObject obj = objects[i];
            if(obj.isVisible(x1, y1, x2, y2)) {
                if(size >= buffer.length) increaseBuffer();
                TMPElement el = buffer[size];
                el.obj = obj;
                el.tex = null;
                size++;
            }
        }
    }

    public void addRenderObjects(final RenderObject[] objects, final MultyTexture tex, final int x1, final int y1, final int x2, final int y2) {
        final int count = objects.length;
        for(int i=0; i<count; i++) {
            RenderObject obj = objects[i];
            if(obj.isVisible(x1, y1, x2, y2)) {
                if(size >= buffer.length) increaseBuffer();
                TMPElement el = buffer[size];
                el.obj = obj;
                el.tex = tex.textures[0];

//a polygon is either a quad or a triangle, never both
if(obj instanceof Polygon4V) {
    el.tex=tex.textures[((Polygon4V)(obj)).tex];
} else if(obj instanceof Polygon3V) {
el.tex=tex.textures[((Polygon3V)(obj)).tex];
}
                size++;
            }
        }
    }


    public void addRenderObject(final TMPElement obj, final int x1, final int y1, final int x2, final int y2) {
            RenderObject objz = obj.obj;
            if(objz.isVisible(x1, y1, x2, y2)) {
                if(size >= buffer.length) increaseBuffer();
                TMPElement el = buffer[size];
                el.obj=obj.obj;
                el.tex=obj.tex;
                size++;
            }
        
    }
    
    public void addRenderObject(final RenderObject obj, final Texture tex, final int x1, final int y1, final int x2, final int y2) {
        if( obj.isVisible(x1, y1, x2, y2) ) {
            if(size >= buffer.length) increaseBuffer();
            TMPElement el = buffer[size];
            el.obj = obj;
            el.tex = tex;
            size++;
        }
    }

    public void addRenderObjectDT(final RenderObject obj, final Texture tex, final int x1, final int y1, final int x2, final int y2) {
            if(size >= buffer.length) increaseBuffer();
            TMPElement el = buffer[size];
            el.obj = obj;
            el.tex = tex;
            size++;
        
    }

    public void addRenderObject(final RenderObject obj, final MultyTexture tex, final int x1, final int y1, final int x2, final int y2) {
        if( obj.isVisible(x1, y1, x2, y2) ) {
            if(size >= buffer.length) increaseBuffer();
            TMPElement el = buffer[size];
            el.obj = obj;
            el.tex = tex.textures[0];
//a polygon is either a quad or a triangle, never both
if(obj instanceof Polygon4V) {
el.tex=tex.textures[((Polygon4V)(obj)).tex];
} else if(obj instanceof Polygon3V) {
el.tex=tex.textures[((Polygon3V)(obj)).tex];
}

            size++;
        }
    }


    public void addRenderObject(final RenderObject obj, final int x1, final int y1, final int x2, final int y2) {
        if( obj.isVisible(x1, y1, x2, y2) ) {
            if(size >= buffer.length) increaseBuffer();
            TMPElement el = buffer[size];
            el.obj = obj;
            el.tex = null;

            size++;
        }
    }
    
    private void increaseBuffer() {
        //Growing by a fixed 50 made filling a buffer with N objects cost
        //O(N^2/50) array copies and N/50 reallocations (a 20000 polygon frame
        //reallocates 400 times). Growing by 50% keeps that amortised to O(N)
        //while wasting at most half a buffer worth of extra slots.
        final int increase = buffer.length > 100 ? buffer.length >> 1 : 50;
        TMPElement[] newBuffer = new TMPElement[buffer.length+increase];
        System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
        for (int i = buffer.length; i < newBuffer.length; i++) {
            newBuffer[i] = new TMPElement();
        }
        buffer = newBuffer;
    }

    //Two ints (start/end) per pending range. Recursing into the smaller half
    //first keeps the depth at log2(size), so this is room for ~2^30 objects.
    private int[] sortStack = new int[128];

    public void sort(int start, int end) {
        final TMPElement[] list = buffer;
        int[] stack = sortStack;
        int sp = 0;
        TMPElement tmp;

        for(;;) {
            //Отрисовка идёт с конца списка
            while(start < end) {
                int opora = list[(start+end)>>1].obj.sz;

                int first = start, second = end;

                while(first <= second) {
                    while(list[first].obj.sz>opora) {first++;}
                    while(list[second].obj.sz<opora) {second--;}

                    if(first <= second) {
                        tmp = list[first];
                        list[first] = list[second];
                        list[second] = tmp;
                        first++; second--;
                    }
                }

                //Visit the smaller half right away and remember the bigger one:
                //both halves are sorted exactly like the recursive version did,
                //they are just no longer stacked on the java call stack
                //(deep or already sorted buffers used to blow it up).
                int pushStart, pushEnd;
                if(second - start < end - first) {
                    pushStart = first;
                    pushEnd = end;
                    end = second;
                } else {
                    pushStart = start;
                    pushEnd = second;
                    start = first;
                }

                if(sp == stack.length) {
                    int[] bigger = new int[stack.length << 1];
                    System.arraycopy(stack, 0, bigger, 0, sp);
                    sortStack = stack = bigger;
                }
                stack[sp++] = pushStart;
                stack[sp++] = pushEnd;
            }

            if(sp == 0) return;
            sp -= 2;
            start = stack[sp];
            end = stack[sp+1];
        }
    }
    
    public void reset() {
        size = 0;
    }

    public void resetTex() {
        for(int i=0;i<buffer.length;i++) {
((TMPElement)buffer[i]).tex=null;
}
    }
    


    public TMPElement[] getBuffer() {
        return buffer;
    }

    public int getSize() {
        return size;
    }
    
}


