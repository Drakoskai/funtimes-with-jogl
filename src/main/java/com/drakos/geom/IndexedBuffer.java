package com.drakos.geom;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author Drakos
 */
public class IndexedBuffer {

    private final IntBuffer indices;
    private final FloatBuffer vertices;
    private final int indexCount;
    private final int vertexCount;

    public IndexedBuffer(int indexCount, IntBuffer indices, int vertexCount, FloatBuffer vertices) {
        this.indices = indices;
        this.vertices = vertices;
        this.indexCount = indexCount;
        this.vertexCount = vertexCount;
    }

    public int getIndexCount() {
        return this.indexCount;
    }

    public IntBuffer getIndices() {
        return this.indices;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

    public FloatBuffer getVertices() {
        return this.vertices;
    }
}
