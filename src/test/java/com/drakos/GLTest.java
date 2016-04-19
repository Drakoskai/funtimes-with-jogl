package com.drakos;

import com.drakos.geom.Triangle;
import com.drakos.shader.ShaderContainer;
import com.drakos.util.BufferUtils;
import static com.drakos.util.GLAttrib.COLOR;
import static com.drakos.util.GLAttrib.POSITION;
import com.drakos.util.GLDebugOutputListener;
import com.drakos.util.Semantic;
import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.opengl.GLWindow;
import static com.jogamp.opengl.GL.GL_DONT_CARE;
import static com.jogamp.opengl.GL2ES2.GL_DEBUG_SEVERITY_HIGH;
import static com.jogamp.opengl.GL2ES2.GL_DEBUG_SEVERITY_MEDIUM;
import static com.jogamp.opengl.GL2ES3.GL_COLOR;
import static com.jogamp.opengl.GL2ES3.GL_DEPTH;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.GLBuffers;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;
import org.junit.Test;

/**
 *
 * @author Drakos
 */
public class GLTest implements GLEventListener {
    
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    
    private GLWindow glWindow;
    private Animator animator;
    private final ShaderContainer shaderContainer = new ShaderContainer();
    
    private final IntBuffer vertexArrayName = GLBuffers.newDirectIntBuffer(1);
    private final Triangle tri = new Triangle();
    private final Triangle tri2 = new Triangle();
    
    private long start;
    private long now;
    
    private FloatBuffer clearColor = GLBuffers.newDirectFloatBuffer(new float[]{1.0f, 0.5f, 0.0f, 1.0f});
    private FloatBuffer clearDepth = GLBuffers.newDirectFloatBuffer(new float[]{1.0f});
    
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
        initVertexArray(gl4);
        tri.init(gl4);
        tri2.init(gl4);
        tri.setShader(shaderContainer.initShaderProgram(gl4, "hello-triangle"));
        tri2.setShader(shaderContainer.initShaderProgram(gl4, "hello-triangle"));
        start = System.currentTimeMillis();
    }
    
    private void initDebug(GL4 gl4) {
        glWindow.getContext().addGLDebugListener(new GLDebugOutputListener());
        gl4.glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, 0, null, false);
        gl4.glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DEBUG_SEVERITY_HIGH, 0, null, true);
        gl4.glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DEBUG_SEVERITY_MEDIUM, 0, null, true);
    }
    
    private void initVertexArray(GL4 gl4) {
        
        gl4.glCreateVertexArrays(1, vertexArrayName);
        
        gl4.glVertexArrayAttribBinding(vertexArrayName.get(0), POSITION.getIndex(), Semantic.Stream._0);
        gl4.glVertexArrayAttribBinding(vertexArrayName.get(0), COLOR.getIndex(), Semantic.Stream._0);
        
        gl4.glVertexArrayAttribFormat(vertexArrayName.get(0), POSITION.getIndex(), POSITION.getSize(), POSITION.getType(), false, POSITION.getOffset());
        gl4.glVertexArrayAttribFormat(vertexArrayName.get(0), COLOR.getIndex(), COLOR.getSize(), COLOR.getType(), false, COLOR.getOffset());
        
        gl4.glEnableVertexArrayAttrib(vertexArrayName.get(0), POSITION.getIndex());
        gl4.glEnableVertexArrayAttrib(vertexArrayName.get(0), COLOR.getIndex());
        
        tri.setVoaId(vertexArrayName.get(0));
        tri2.setVoaId(vertexArrayName.get(0));
        
        BufferUtils.destroyDirectBuffer(vertexArrayName);
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        GL4 gl4 = drawable.getGL().getGL4();
        
        gl4.glClearBufferfv(GL_COLOR, 0, clearColor);
        gl4.glClearBufferfv(GL_DEPTH, 0, clearDepth);
        
        now = System.currentTimeMillis();
        
        float delta = (float) (now - start) / 1000;
        
        tri.update(delta);
        tri2.update(delta);
        tri.drawTriangle(gl4);
        tri2.drawTriangle(gl4);
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        System.out.println("reshape");
        GL4 gl4 = drawable.getGL().getGL4();
        gl4.glViewport(x, y, width, height);
    }
    
    @Override
    public void dispose(GLAutoDrawable drawable) {
        System.out.println("dispose");
        
        GL4 gl4 = drawable.getGL().getGL4();
        
        tri.dispose(gl4);
        tri2.dispose(gl4);
        
        gl4.glDeleteVertexArrays(1, vertexArrayName);
        
        BufferUtils.destroyDirectBuffer(clearColor);
        BufferUtils.destroyDirectBuffer(clearDepth);
        
        isRunning.set(false);
    }
}
