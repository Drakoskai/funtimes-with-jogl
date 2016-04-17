package com.drakos.geom;

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
public class Triangle {

    private final int vertexCount = 3;
    private final float[] vertexData = new float[]{
        -1, -1,/**/ 1, 0, 0,
        +0, +2,/**/ 0, 0, 1,
        +1, -1,/**/ 0, 1, 0};
    private final int elementCount = 3;
    private final int[] elementData = new int[]{
        0, 2, 1
    };

    private final Geometry mesh;
    private float[] scaleMatrix = new float[16];
    private float[] zRotationMatrix = new float[16];
    private float[] modelToClipMatrix = new float[16];

    private int shaderProgId;
    private int voaId;

    private float h = 0.5f;
    private float w = 0.5f;
    private float d = 0.5f;

    public Triangle() {
        mesh = new Geometry();
        mesh.setVertexData(vertexCount, vertexData);
        mesh.setElementData(GL_TRIANGLES, elementCount, elementData);
    }

    public void init(GL4 gl) {

    }

    public void setShader(int shader) {
        shaderProgId = shader;
    }

    public void setVoaId(int voaId) {
        this.voaId = voaId;
    }

    public void setSize(float h, float w, float d) {
        this.h = h;
        this.w = w;
        this.d = d;
    }

    public float[] getSize() {
        return new float[]{h, w, d};
    }

    public void update(float dt, ByteBuffer transformPointer) {
        scaleMatrix = FloatUtil.makeScale(scaleMatrix, true, h, w, d);
        zRotationMatrix = FloatUtil.makeRotationEuler(zRotationMatrix, 0, 0, 0, dt);
        modelToClipMatrix = FloatUtil.multMatrix(scaleMatrix, zRotationMatrix);

        transformPointer.asFloatBuffer().put(modelToClipMatrix);
    }

    public void drawTriangle(GL4 gl) {
        gl.glUseProgram(shaderProgId);
        gl.glBindVertexArray(voaId);
        gl.glDrawElements(GL_TRIANGLES, getCount(ELEMENT), GL_UNSIGNED_INT, 0);
    }

    public Buffer getBuffer(GLBuffer type) {
        return mesh.getBuffer(type);
    }

    public int getCount(GLBuffer type) {
        return mesh.getCount(type);
    }

    public int getStride(GLBuffer type) {
        return mesh.getStride(type);
    }

    public long getSizeInBytes(GLBuffer type) {
        return mesh.getSizeInBytes(VERTEX);
    }

    public void dispose() {

    }
}
