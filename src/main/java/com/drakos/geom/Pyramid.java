package com.drakos.geom;

import com.drakos.util.DrawContext;
import static com.jogamp.opengl.GL.GL_TRIANGLES;

/**
 *
 * @author Drakos
 */
public class Pyramid {

    private final float[] vertexData = new float[]{
        -1f, -1f, 0f,
        0f, -1f, 1f,
        1f, -1f, 0f,
        0f, 1f, 0f
    };
    private final int[] elementData = new int[]{
        0, 3, 1,
        1, 3, 2,
        2, 3, 0,
        0, 1, 2
    };

    private final Mesh mesh;
    private final Transform transform;

    private float h = 0.5f;
    private float w = 0.5f;
    private float d = 0.5f;

    private float rotX = 1;
    private float rotY = 1;
    private float rotZ = 1;

    public Pyramid(DrawContext dc) {
        mesh = new Mesh(dc, vertexData, elementData, GL_TRIANGLES);
        mesh.shaderProgId = dc.initShader(Pyramid.class, "shape");
        transform = new Transform();
    }

    public void setSize(float h, float w, float d) {
        this.h = h;
        this.w = w;
        this.d = d;
    }

    public void setRotation(float x, float y, float z) {
        this.rotX = x;
        this.rotY = y;
        this.rotZ = z;
    }

    public float[] getRotation() {
        return new float[]{rotX, rotY, rotZ};
    }

    public float[] getSize() {
        return new float[]{h, w, d};
    }

    public void update(float dt) {
        transform.rotate(dt * rotX, dt * rotY, dt * rotZ);
        transform.scale(h, w, d);
        transform.update();
        mesh.update(transform);
    }

    public Mesh getMesh() {
        return mesh;
    }
}
