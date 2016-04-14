package com.drakos.geom;

import com.drakos.util.GLBuffer;
import static com.drakos.util.GLBuffer.ELEMENT;
import static com.drakos.util.GLBuffer.VERTEX;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.GLBuffers;
import java.nio.Buffer;

/**
 *
 * @author Drakos
 */
public class Geometry {

    private final int[] mode;
    private final int[] count;
    private final int[] size;
    private final int[] glType;
    private final Buffer[] buffer;

    public Geometry() {
        this.mode = new int[4];
        this.count = new int[4];
        this.size = new int[4];
        this.glType = new int[4];
        this.buffer = new Buffer[4];
    }

    public int getMode(GLBuffer type) {
        return this.mode[type.id()];
    }

    public void setMode(GLBuffer type, int mode) {
        this.mode[type.id()] = mode;
    }

    public int getCount(GLBuffer type) {
        return this.count[type.id()];
    }

    public int getSize(GLBuffer type) {
        return this.size[type.id()];
    }

    public int getGLType(GLBuffer type) {
        return this.glType[type.id()];
    }

    public Buffer getBuffer(GLBuffer type) {
        return this.buffer[type.id()];
    }

    public void setElementData(int mode, int count, int[] src) {
        this.setMode(ELEMENT, mode);
        this.buffer[ELEMENT.id()] = GLBuffers.newDirectIntBuffer(src);
        this.size[ELEMENT.id()] = 1;
        this.glType[ELEMENT.id()] = GL.GL_UNSIGNED_INT;
        this.count[ELEMENT.id()] = count;
    }

    public void setVertexData(int size, float[] src) {
        this.buffer[VERTEX.id()] = GLBuffers.newDirectFloatBuffer(src);
        this.size[VERTEX.id()] = size;
        this.glType[VERTEX.id()] = GL.GL_FLOAT;
        this.count[VERTEX.id()] = 3;
    }

    public long getSizeInBytes(GLBuffer type) {
        return this.bufferSize(type);
    }

    private long bufferSize(GLBuffer type) {
        long sz = 0L;
        if (this.buffer[type.id()] != null) {
            sz = this.sizeOf(this.glType[type.id()]) * this.getCount(type) * this.size[type.id()];
        }
        return sz;
    }

    private long sizeOf(int glType) {
        long sz = 0L;
        switch (glType) {
            case GL2.GL_BYTE:
                sz = 1L;
                break;
            case GL2.GL_SHORT:
            case GL2.GL_UNSIGNED_SHORT:
                sz = 2L;
                break;
            case GL2.GL_INT:
            case GL2.GL_UNSIGNED_INT:
            case GL2.GL_FLOAT:
                sz = 4L;
                break;
            case GL2.GL_DOUBLE:
                sz = 8L;
                break;
        }
        return sz;
    }
}
