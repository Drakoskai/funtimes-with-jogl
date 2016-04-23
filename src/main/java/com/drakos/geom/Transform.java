package com.drakos.geom;

import com.jogamp.opengl.math.FloatUtil;

/**
 *
 * @author Drakos
 */
public class Transform {

    private float[] scaleMatrix = new float[16];
    private float[] zRotationMatrix = new float[16];
    private float[] modelToClipMatrix = new float[16];

    public Transform() {
        scaleMatrix = FloatUtil.makeScale(scaleMatrix, true, 0.5f, 0.5f, 0.5f);
    }

    public float[] getModelToClipMatrix() {
        return modelToClipMatrix;
    }

    public void rotate(float x, float y, float z) {
        zRotationMatrix = FloatUtil.makeRotationEuler(zRotationMatrix, 0, x, y, z);
    }

    public void scale(float x, float y, float z) {
        scaleMatrix = FloatUtil.makeScale(scaleMatrix, true, x, y, z);
    }

    public void update() {
        modelToClipMatrix = FloatUtil.multMatrix(scaleMatrix, zRotationMatrix);
    }
}
