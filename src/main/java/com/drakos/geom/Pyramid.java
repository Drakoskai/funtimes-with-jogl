package com.drakos.geom;

import com.drakos.util.DrawContext;
import static com.jogamp.opengl.GL.GL_TRIANGLES;

/**
 *
 * @author Drakos
 */
public class Pyramid implements Shape {

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

    private float rotX = 0;
    private float rotY = 0;
    private float rotZ = 0;

    public Pyramid(DrawContext dc) {
        mesh = new Mesh(dc, vertexData, elementData, GL_TRIANGLES);
        mesh.shaderProgId = dc.initShader(Pyramid.class, "shape");
        transform = new Transform();
    }

    @Override
    public void setSize(float h, float w, float d) {
        this.h = h;
        this.w = w;
        this.d = d;
    }

    @Override
    public void setRotation(float x, float y, float z) {
        this.rotX = x;
        this.rotY = y;
        this.rotZ = z;
    }

    @Override
    public float[] getRotation() {
        return new float[]{rotX, rotY, rotZ};
    }

    @Override
    public float[] getSize() {
        return new float[]{h, w, d};
    }

    @Override
    public void update(float dt) {
        transform.rotate(rotX, rotY, rotZ);
        transform.scale(h, w, d);
        transform.update();
        mesh.update(transform);
    }

    @Override
    public Mesh getMesh() {
        return mesh;
    }
}
