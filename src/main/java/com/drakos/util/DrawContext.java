package com.drakos.util;

import com.drakos.geom.Mesh;
import com.drakos.shader.ShaderContainer;
import static com.drakos.util.GLAttrib.POSITION;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;
import static com.jogamp.opengl.GL2ES3.GL_DEPTH;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.util.GLBuffers;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author Drakos
 */
public class DrawContext {

    public GL4 gl;
    public int vaoId;
    private final IntBuffer vaoName = GLBuffers.newDirectIntBuffer(1);
    private final FloatBuffer clearColor = GLBuffers.newDirectFloatBuffer(new float[]{1.0f, 0.5f, 0.0f, 1.0f});
    private final FloatBuffer clearDepth = GLBuffers.newDirectFloatBuffer(new float[]{1.0f});
    private final ShaderContainer shaderContainer = new ShaderContainer();

    public DrawContext(GLAutoDrawable drawable) {
        this.gl = drawable.getGL().getGL4();
        initVertexArray();
    }

    private void initVertexArray() {
        gl.glCreateVertexArrays(1, vaoName);
        vaoId = vaoName.get(0);
        gl.glVertexArrayAttribBinding(vaoId, POSITION.getIndex(), 0);
        gl.glVertexArrayAttribFormat(vaoId, POSITION.getIndex(), POSITION.getSize(), POSITION.getType(), false, POSITION.getOffset());
        gl.glEnableVertexArrayAttrib(vaoId, POSITION.getIndex());
    }

    public int initShader(Class id, String shader) {
        return shaderContainer.initShaderProgram(gl, id, shader);
    }

    public void initFrame(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL4();
        gl.glClearBufferfv(GL_COLOR, 0, clearColor);
        gl.glClearBufferfv(GL_DEPTH, 0, clearDepth);
    }

    public void drawScene(Mesh... meshes) {
        for (Mesh mesh : meshes) {
            mesh.draw(this);
        }
    }

    public void dispose() {
        gl.glDeleteVertexArrays(1, vaoName);
        BufferUtils.destroyDirectBuffer(clearColor);
        BufferUtils.destroyDirectBuffer(clearDepth);
    }

    public int getShader(String name) {
        return this.shaderContainer.getShader(name);
    }

    public int getShader(Class<?> aClass) {
        return this.shaderContainer.getShader(aClass);
    }
}
