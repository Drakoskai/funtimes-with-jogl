package com.drakos.geom;

import com.drakos.util.BufferUtils;
import com.drakos.util.DrawContext;
import com.drakos.util.GLBuffer;
import static com.drakos.util.GLBuffer.ELEMENT;
import static com.drakos.util.GLBuffer.VERTEX;
import com.drakos.util.Semantic;
import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
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
import com.jogamp.opengl.util.GLBuffers;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author Drakos
 */
public class Mesh {

    IntBuffer vboName;
    ByteBuffer transformPointer;
    int vertexCount;
    Geometry geometry;
    int shaderProgId;

    public Mesh(DrawContext dc, float[] vertices, int[] elements, int elementType) {
        geometry = new Geometry();
        geometry.setVertexData(vertices);
        geometry.setElementData(elementType, elements);
        init(dc);
    }

    private void init(DrawContext dc) {
        GL4 gl = dc.gl;
        vboName = GLBuffers.newDirectIntBuffer(GLBuffer.NAME.id());
        Buffer vertexBuffer = geometry.getBuffer(VERTEX);
        Buffer elementBuffer = geometry.getBuffer(ELEMENT);

        gl.glCreateBuffers(GLBuffer.NAME.id(), vboName);
        gl.glBindBuffer(GL_ARRAY_BUFFER, vboName.get(VERTEX.id()));
        gl.glBufferStorage(GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, 0);
        gl.glBindBuffer(GL_ARRAY_BUFFER, 0);
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboName.get(ELEMENT.id()));
        gl.glBufferStorage(GL_ELEMENT_ARRAY_BUFFER, elementBuffer.capacity() * Integer.BYTES, elementBuffer, 0);
        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        gl.glBindBuffer(GL_UNIFORM_BUFFER, vboName.get(GLBuffer.TRANSFORM.id()));
        IntBuffer uniformBufferOffset = GLBuffers.newDirectIntBuffer(1);
        gl.glGetIntegerv(GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT, uniformBufferOffset);
        int uniformBlockSize = Math.max(16 * Float.BYTES, uniformBufferOffset.get(0));
        gl.glBufferStorage(GL_UNIFORM_BUFFER, uniformBlockSize, null, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);
        BufferUtils.destroyDirectBuffer(uniformBufferOffset);
        gl.glBindBuffer(GL_UNIFORM_BUFFER, 0);

        BufferUtils.destroyDirectBuffer(vertexBuffer);
        BufferUtils.destroyDirectBuffer(elementBuffer);

        gl.glVertexArrayElementBuffer(dc.vaoId, vboName.get(GLBuffer.ELEMENT.id()));
        gl.glVertexArrayVertexBuffer(dc.vaoId, Semantic.Stream._0, vboName.get(GLBuffer.VERTEX.id()), 0, geometry.getStride(VERTEX));

        transformPointer = dc.gl.glMapNamedBufferRange(vboName.get(GLBuffer.TRANSFORM.id()), // buffer
                0, // offset
                16 * Float.BYTES, // size
                GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT | GL_MAP_INVALIDATE_BUFFER_BIT
        );
    }

    public void update(Transform transform) {
        transformPointer.asFloatBuffer().put(transform.getModelToClipMatrix());
    }

    public void draw(DrawContext dc) {
        dc.gl.glBindBufferBase(GL_UNIFORM_BUFFER, Semantic.Uniform.TRANSFORM0, vboName.get(GLBuffer.TRANSFORM.id()));
        dc.gl.glUseProgram(shaderProgId);
        dc.gl.glBindVertexArray(dc.vaoId);
        dc.gl.glDrawElements(GL_TRIANGLES, geometry.getCount(ELEMENT), GL_UNSIGNED_INT, 0);
    }

    public void dispose(DrawContext dc) {
        dc.gl.glUnmapNamedBuffer(vboName.get(GLBuffer.TRANSFORM.id()));
        dc.gl.glDeleteBuffers(GLBuffer.NAME.id(), vboName);
        BufferUtils.destroyDirectBuffer(vboName);
    }
}
