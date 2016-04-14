package com.drakos.util;

/**
 *
 * @author Drakos
 */
public enum GLBuffer {

    VERTEX(0),
    ELEMENT(1),
    TRANSFORM(2),
    MAX(3);
    private final int id;

    private GLBuffer(final int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }
}
