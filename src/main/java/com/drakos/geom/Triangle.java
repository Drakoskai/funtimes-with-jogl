package com.drakos.geom;

import com.drakos.Prerenderable;
import com.drakos.Renderable;
import com.drakos.util.GLBuffer;
import static com.drakos.util.GLBuffer.ELEMENT;
import static com.drakos.util.GLBuffer.VERTEX;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL.GL_UNSIGNED_INT;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.math.FloatUtil;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 *
 * @author Drakos
 */
public class Triangle implements Renderable, Prerenderable {

    private final int vertexCount = 3;
    private final float[] vertexData = new float[]{
        -1, -1,/**/ 1, 0, 0,
        +0, +2,/**/ 0, 0, 1,
        +1, -1,/**/ 0, 1, 0
    };

    private final int colorCount = 3;
    private final float[] colorData = new float[]{
        1, 0, 0,
        0, 0, 1,
        0, 1, 0
    };

    private final int elementCount = 3;
    private final int[] elementData = new int[]{
        0, 2, 1
    };
    
    private final Geometry mesh;
    
    private float[] scaleMatrix = new float[16];
    private float[] zRotazionMatrix = new float[16];
    private float[] modelToClipMatrix = new float[16];

    private float h = 0.5f;
    private float w = 0.5f;
    private float d = 0.5f;

    public Triangle() {
        mesh = new Geometry();
        mesh.setVertexData(vertexCount, vertexData);
        mesh.setElementData(GL_TRIANGLES, elementCount, elementData);
    }

    public void setSize(float h, float w, float d) {
        this.h = h;
        this.w = w;
        this.d = d;
    }

    public float[] getSize() {
        return new float[]{h, w, d};
    }

    @Override
    public void prerender(ByteBuffer transformPointer, float dt, GL4 gl, int program, int voaId) {
        scaleMatrix = FloatUtil.makeScale(scaleMatrix, true, h, w, d);
        zRotazionMatrix = FloatUtil.makeRotationEuler(zRotazionMatrix, 0, 0, 0, dt);
        modelToClipMatrix = FloatUtil.multMatrix(scaleMatrix, zRotazionMatrix);

        transformPointer.asFloatBuffer().put(modelToClipMatrix);

        fillVBO(gl, program, voaId);
    }

    public void fillVBO(GL4 gl, int program, int voaId) {
        gl.glUseProgram(program);
        gl.glBindVertexArray(voaId);
    }

    @Override
    public void render(GL4 gl) {
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
}
