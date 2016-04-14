package com.drakos.geom;

import com.drakos.util.GLBuffer;
import static com.drakos.util.GLBuffer.ELEMENT;
import static com.drakos.util.GLBuffer.VERTEX;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL.GL_UNSIGNED_INT;
import com.jogamp.opengl.GL4;
import java.nio.Buffer;

/**
 *
 * @author Drakos
 */
public class Triangle {

    private final int vertexCount = 3;
    private final float[] vertexData = new float[]{
        -1, -1,/**/ 1, 0, 0,
        +0, +2,/**/ 0, 0, 1,
        +1, -1,/**/ 0, 1, 0
    };
    private final int elementCount = 3;
    private final int[] elementData = new int[]{
        0, 2, 1
    };
    private final Geometry mesh;

    public Triangle() {
        mesh = new Geometry();
        mesh.setVertexData(vertexCount, vertexData);
        mesh.setElementData(GL_TRIANGLES, elementCount, elementData);
    }

    public void drawTriangle(GL4 gl) {
        gl.glDrawElements(GL_TRIANGLES, getCount(ELEMENT), GL_UNSIGNED_INT, 0);
    }

    public Buffer getBuffer(GLBuffer type) {
        return mesh.getBuffer(type);
    }

    public int getCount(GLBuffer type) {
        return mesh.getCount(type);
    }

    public long getSizeInBytes(GLBuffer type) {
        return mesh.getSizeInBytes(VERTEX);
    }

    public void rotate() {

    }
}
