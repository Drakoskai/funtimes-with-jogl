package com.drakos.geom;

import com.drakos.util.BufferUtils;
import com.drakos.util.GLBuffer;
import static com.drakos.util.GLBuffer.ELEMENT;
import static com.drakos.util.GLBuffer.VERTEX;
import com.drakos.util.Semantic;
import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_MAP_INVALIDATE_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_MAP_WRITE_BIT;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL.GL_UNSIGNED_INT;
import static com.jogamp.opengl.GL2ES3.GL_UNIFORM_BUFFER;
import static com.jogamp.opengl.GL2ES3.GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT;
import com.jogamp.opengl.GL4;
import static com.jogamp.opengl.GL4.GL_MAP_COHERENT_BIT;
import static com.jogamp.opengl.GL4.GL_MAP_PERSISTENT_BIT;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.util.GLBuffers;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author Drakos
 */
public class Triangle {

    private final IntBuffer bufferName = GLBuffers.newDirectIntBuffer(GLBuffer.MAX.id());

    private final int vertexCount = 3;
    private final float[] vertexData = new float[]{
        -1, -1,0,/**/ 1, 0, 0,
        +0, +2,0,/**/ 0, 0, 1,
        +1, -1,0,/**/ 0, 1, 0};

    private final int elementCount = 3;
    private final int[] elementData = new int[]{
        0, 2, 1
    };

    private final Geometry mesh;

    private final int matrixDimensions = 4 * 4;

    private float[] scaleMatrix = new float[matrixDimensions];
    private float[] zRotationMatrix = new float[matrixDimensions];
    private float[] modelToClipMatrix = new float[matrixDimensions];
    private ByteBuffer transformPointer;

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
        Buffer vertexBuffer = getBuffer(VERTEX);
        Buffer elementBuffer = getBuffer(ELEMENT);

        gl.glCreateBuffers(GLBuffer.MAX.id(), bufferName);
        gl.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(VERTEX.id()));

        gl.glBufferStorage(GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, 0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferName.get(ELEMENT.id()));

        gl.glBufferStorage(GL_ELEMENT_ARRAY_BUFFER, elementBuffer.capacity() * Integer.BYTES, elementBuffer, 0);

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_UNIFORM_BUFFER, bufferName.get(GLBuffer.TRANSFORM.id()));

        IntBuffer uniformBufferOffset = GLBuffers.newDirectIntBuffer(1);

        gl.glGetIntegerv(GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT, uniformBufferOffset);
        int uniformBlockSize = Math.max(matrixDimensions * Float.BYTES, uniformBufferOffset.get(0));

        gl.glBufferStorage(GL_UNIFORM_BUFFER, uniformBlockSize, null, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);

        BufferUtils.destroyDirectBuffer(uniformBufferOffset);

        gl.glBindBuffer(GL_UNIFORM_BUFFER, 0);

        BufferUtils.destroyDirectBuffer(vertexBuffer);
        BufferUtils.destroyDirectBuffer(elementBuffer);

        gl.glVertexArrayElementBuffer(voaId, bufferName.get(GLBuffer.ELEMENT.id()));
        gl.glVertexArrayVertexBuffer(voaId, Semantic.Stream._0, bufferName.get(GLBuffer.VERTEX.id()), 0, mesh.getStride(VERTEX));

        transformPointer = gl.glMapNamedBufferRange(bufferName.get(GLBuffer.TRANSFORM.id()), // buffer
                0, // offset
                matrixDimensions * Float.BYTES, // size
                GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT | GL_MAP_INVALIDATE_BUFFER_BIT); // flags

        gl.glEnable(GL_DEPTH_TEST);
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

    public void update(float dt) {
        scaleMatrix = FloatUtil.makeScale(scaleMatrix, true, h, w, d);
        zRotationMatrix = FloatUtil.makeRotationEuler(zRotationMatrix, 0, 0, 0, dt);
        modelToClipMatrix = FloatUtil.multMatrix(scaleMatrix, zRotationMatrix);

        transformPointer.asFloatBuffer().put(modelToClipMatrix);
    }

    public void drawTriangle(GL4 gl) {
        gl.glBindBufferBase(GL_UNIFORM_BUFFER, Semantic.Uniform.TRANSFORM0, bufferName.get(GLBuffer.TRANSFORM.id()));
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

    public void dispose(GL4 gl) {
        gl.glUnmapNamedBuffer(bufferName.get(GLBuffer.TRANSFORM.id()));
        gl.glDeleteBuffers(GLBuffer.MAX.id(), bufferName);
        BufferUtils.destroyDirectBuffer(bufferName);
    }
}
