package com.drakos;

import com.drakos.geom.Triangle;
import com.drakos.util.GLBuffer;
import com.drakos.util.BufferUtils;
import static com.drakos.util.GLBuffer.ELEMENT;
import static com.drakos.util.GLBuffer.VERTEX;
import com.drakos.util.GLDebugOutputListener;
import com.drakos.util.Semantic;
import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.opengl.GLWindow;
import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_DONT_CARE;
import static com.jogamp.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_MAP_INVALIDATE_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_MAP_WRITE_BIT;
import static com.jogamp.opengl.GL2ES2.GL_DEBUG_SEVERITY_HIGH;
import static com.jogamp.opengl.GL2ES2.GL_DEBUG_SEVERITY_MEDIUM;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;
import static com.jogamp.opengl.GL2ES3.GL_DEPTH;
import static com.jogamp.opengl.GL2ES3.GL_UNIFORM_BUFFER;
import static com.jogamp.opengl.GL2ES3.GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT;
import com.jogamp.opengl.GL4;
import static com.jogamp.opengl.GL4.GL_MAP_COHERENT_BIT;
import static com.jogamp.opengl.GL4.GL_MAP_PERSISTENT_BIT;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import org.junit.Test;

/**
 *
 * @author Drakos
 */
public class GLTest implements GLEventListener, KeyListener {

    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    private GLWindow glWindow;
    private Animator animator;
    private final String SHADERS_ROOT = "/shaders";
    private final String SHADERS_NAME = "hello-triangle";

    private final Triangle tri = new Triangle();

    private float[] scale = new float[16];
    private float[] zRotazion = new float[16];
    private float[] modelToClip = new float[16];

    private long start;
    private long now;
    private IntBuffer bufferName = GLBuffers.newDirectIntBuffer(GLBuffer.MAX.id());
    private IntBuffer vertexArrayName = GLBuffers.newDirectIntBuffer(1);
    private int programName;
    private FloatBuffer clearColor = GLBuffers.newDirectFloatBuffer(new float[]{1.0f, 0.5f, 0.0f, 1.0f});
    private FloatBuffer clearDepth = GLBuffers.newDirectFloatBuffer(new float[]{1.0f});
    private ByteBuffer transformPointer;

    @Test
    public void Run() {
        Display display = NewtFactory.createDisplay(null);
        Screen screen = NewtFactory.createScreen(display, 0);
        GLProfile glProfile = GLProfile.get(GLProfile.GL4);
        GLCapabilities glCapabilities = new GLCapabilities(glProfile);
        glWindow = GLWindow.create(screen, glCapabilities);

        glWindow.setSize(1024, 768);
        glWindow.setPosition(50, 50);
        glWindow.setUndecorated(false);
        glWindow.setAlwaysOnTop(false);
        glWindow.setFullscreen(false);
        glWindow.setPointerVisible(true);
        glWindow.confinePointer(false);
        glWindow.setTitle("Hello Triangle");
        glWindow.setContextCreationFlags(GLContext.CTX_OPTION_DEBUG);
        glWindow.setVisible(true);
        glWindow.addGLEventListener(this);
        glWindow.addKeyListener(this);

        animator = new Animator(glWindow);
        animator.start();

        while (isRunning.get()) {
            LockSupport.parkNanos(1);
        }
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        System.out.println("init");

        GL4 gl4 = drawable.getGL().getGL4();

        initDebug(gl4);

        initBuffers(gl4);

        initVertexArray(gl4);

        initProgram(gl4);

        // map the transform buffer and keep it mapped
        transformPointer = gl4.glMapNamedBufferRange(bufferName.get(GLBuffer.TRANSFORM.id()), // buffer
                0, // offset
                16 * Float.BYTES, // size
                GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT | GL_MAP_INVALIDATE_BUFFER_BIT); // flags

        gl4.glEnable(GL_DEPTH_TEST);

        start = System.currentTimeMillis();
    }

    private void initDebug(GL4 gl4) {
        glWindow.getContext().addGLDebugListener(new GLDebugOutputListener());
        gl4.glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, 0, null, false);
        gl4.glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DEBUG_SEVERITY_HIGH, 0, null, true);
        gl4.glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DEBUG_SEVERITY_MEDIUM, 0, null, true);
    }

    private void initBuffers(GL4 gl4) {

        Buffer vertexBuffer = tri.getBuffer(VERTEX);
        Buffer elementBuffer = tri.getBuffer(ELEMENT);

        gl4.glCreateBuffers(GLBuffer.MAX.id(), bufferName);
        gl4.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(VERTEX.id()));

        gl4.glBufferStorage(GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, 0);

        gl4.glBindBuffer(GL_ARRAY_BUFFER, 0);

        gl4.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferName.get(ELEMENT.id()));

        gl4.glBufferStorage(GL_ELEMENT_ARRAY_BUFFER, elementBuffer.capacity() * Integer.BYTES, elementBuffer, 0);

        gl4.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

        gl4.glBindBuffer(GL_UNIFORM_BUFFER, bufferName.get(GLBuffer.TRANSFORM.id()));

        IntBuffer uniformBufferOffset = GLBuffers.newDirectIntBuffer(1);
        gl4.glGetIntegerv(GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT, uniformBufferOffset);
        int uniformBlockSize = Math.max(16 * Float.BYTES, uniformBufferOffset.get(0));

        gl4.glBufferStorage(GL_UNIFORM_BUFFER, uniformBlockSize, null, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);

        BufferUtils.destroyDirectBuffer(uniformBufferOffset);

        gl4.glBindBuffer(GL_UNIFORM_BUFFER, 0);

        BufferUtils.destroyDirectBuffer(vertexBuffer);
        BufferUtils.destroyDirectBuffer(elementBuffer);
    }

    private void initVertexArray(GL4 gl4) {

        gl4.glCreateVertexArrays(1, vertexArrayName);

        gl4.glVertexArrayAttribBinding(vertexArrayName.get(0), Semantic.Attr.POSITION, Semantic.Stream._0);
        gl4.glVertexArrayAttribBinding(vertexArrayName.get(0), Semantic.Attr.COLOR, Semantic.Stream._0);

        gl4.glVertexArrayAttribFormat(vertexArrayName.get(0), Semantic.Attr.POSITION, 2, GL_FLOAT, false, 0);
        gl4.glVertexArrayAttribFormat(vertexArrayName.get(0), Semantic.Attr.COLOR, 3, GL_FLOAT, false, 2 * Float.BYTES);

        gl4.glEnableVertexArrayAttrib(vertexArrayName.get(0), Semantic.Attr.POSITION);
        gl4.glEnableVertexArrayAttrib(vertexArrayName.get(0), Semantic.Attr.COLOR);

        gl4.glVertexArrayElementBuffer(vertexArrayName.get(0), bufferName.get(GLBuffer.ELEMENT.id()));
        gl4.glVertexArrayVertexBuffer(vertexArrayName.get(0), Semantic.Stream._0, bufferName.get(GLBuffer.VERTEX.id()), 0, (3 + 2) * Float.BYTES);
    }

    private void initProgram(GL4 gl4) {

        ShaderCode vertShader = ShaderCode.create(gl4, GL_VERTEX_SHADER, this.getClass(), SHADERS_ROOT, null, SHADERS_NAME, "vert", null, true);
        ShaderCode fragShader = ShaderCode.create(gl4, GL_FRAGMENT_SHADER, this.getClass(), SHADERS_ROOT, null, SHADERS_NAME, "frag", null, true);

        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.add(vertShader);
        shaderProgram.add(fragShader);

        shaderProgram.init(gl4);

        programName = shaderProgram.program();

        shaderProgram.link(gl4, System.out);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl4 = drawable.getGL().getGL4();

        gl4.glClearBufferfv(GL_COLOR, 0, clearColor);
        gl4.glClearBufferfv(GL_DEPTH, 0, clearDepth);
        now = System.currentTimeMillis();
        float diff = (float) (now - start) / 1000;
        scale = FloatUtil.makeScale(scale, true, 0.5f, 0.5f, 0.5f);
        zRotazion = FloatUtil.makeRotationEuler(zRotazion, 0, 0, 0, diff);
        modelToClip = FloatUtil.multMatrix(scale, zRotazion);

        transformPointer.asFloatBuffer().put(modelToClip);

        gl4.glUseProgram(programName);
        gl4.glBindVertexArray(vertexArrayName.get(0));

        gl4.glBindBufferBase(GL_UNIFORM_BUFFER, Semantic.Uniform.TRANSFORM0, bufferName.get(GLBuffer.TRANSFORM.id()));

        tri.drawTriangle(gl4);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        System.out.println("dispose");

        GL4 gl4 = drawable.getGL().getGL4();

        gl4.glUnmapNamedBuffer(bufferName.get(GLBuffer.TRANSFORM.id()));

        gl4.glDeleteProgram(programName);
        gl4.glDeleteVertexArrays(1, vertexArrayName);
        gl4.glDeleteBuffers(GLBuffer.MAX.id(), bufferName);

        BufferUtils.destroyDirectBuffer(vertexArrayName);
        BufferUtils.destroyDirectBuffer(bufferName);

        BufferUtils.destroyDirectBuffer(clearColor);
        BufferUtils.destroyDirectBuffer(clearDepth);

        isRunning.set(false);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        System.out.println("reshape");
        GL4 gl4 = drawable.getGL().getGL4();
        gl4.glViewport(x, y, width, height);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            animator.stop();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
