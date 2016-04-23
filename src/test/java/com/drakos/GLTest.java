package com.drakos;

import com.drakos.geom.Cube;
import com.drakos.util.DrawContext;
import com.drakos.util.GLDebugOutputListener;
import com.jogamp.newt.Display;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.opengl.GLWindow;
import static com.jogamp.opengl.GL.GL_DONT_CARE;
import static com.jogamp.opengl.GL2ES2.GL_DEBUG_SEVERITY_HIGH;
import static com.jogamp.opengl.GL2ES2.GL_DEBUG_SEVERITY_MEDIUM;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;
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
    private DrawContext dc;
    private Cube model;

    private long start;
    private long now;

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
        GL4 gl4 = drawable.getGL().getGL4();

        initDebug(gl4);
        dc = new DrawContext(drawable);

        model = new Cube(dc);

        start = System.currentTimeMillis();
    }

    private void initDebug(GL4 gl4) {
        glWindow.getContext().addGLDebugListener(new GLDebugOutputListener());
        gl4.glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, 0, null, false);
        gl4.glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DEBUG_SEVERITY_HIGH, 0, null, true);
        gl4.glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DEBUG_SEVERITY_MEDIUM, 0, null, true);
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        dc.initFrame(drawable);
        now = System.currentTimeMillis();
        float delta = (float) (now - start) / 1000;

        model.update(delta);
        dc.drawScene(model.getMesh());
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL4 gl4 = drawable.getGL().getGL4();
        gl4.glViewport(x, y, width, height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        dc.initFrame(drawable);
        dc.dispose();
        model.getMesh().dispose(dc);

        isRunning.set(false);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        float delta = (float) (now - start) / 1000;
        float[] rotation = model.getRotation();
        float rotX = rotation[0];
        float rotY = rotation[1];
        float rotZ = rotation[2];

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            glWindow.destroy();
        }

        if (e.getKeyCode() == KeyEvent.VK_Q) {
            rotZ += 0.1f * delta;
        }

        if (e.getKeyCode() == KeyEvent.VK_E) {
            rotZ -= 0.1f * delta;
        }

        if (e.getKeyCode() == KeyEvent.VK_W) {
            rotX += 0.1f * delta;
        }

        if (e.getKeyCode() == KeyEvent.VK_S) {
            rotX -= 0.1f * delta;
        }

        if (e.getKeyCode() == KeyEvent.VK_A) {
            rotY += 0.1f * delta;
        }

        if (e.getKeyCode() == KeyEvent.VK_D) {
            rotY -= 0.1f * delta;
        }

        model.setRotation(rotX, rotY, rotZ);
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
