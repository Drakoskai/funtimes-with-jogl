package com.drakos.geom;

/**
 *
 * @author Drakos
 */
public interface Shape {

    public Mesh getMesh();

    public float[] getRotation();

    public float[] getSize();

    public void setRotation(float x, float y, float z);

    public void setSize(float h, float w, float d);

    public void update(float dt);

}
