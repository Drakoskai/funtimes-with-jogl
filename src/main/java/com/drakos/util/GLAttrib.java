package com.drakos.util;

import static com.jogamp.opengl.GL.GL_FLOAT;

/**
 *
 * @author Drakos
 */
public enum GLAttrib {
    POSITION(0, 2, GL_FLOAT, 0),
    NORMAL(1, 3, GL_FLOAT, -1),//not implemented
    COLOR(3, 3, GL_FLOAT, 2 * Float.BYTES);

    private final int index;
    private final int size;
    private final int type;
    private final int offset;

    private GLAttrib(int index, int size, int type, int offset) {
        this.index = index;
        this.size = size;
        this.type = type;
        this.offset = offset;
    }

    public int getIndex() {
        return index;
    }

    public int getSize() {
        return size;
    }

    public int getType() {
        return type;
    }

    public int getOffset() {
        return offset;
    }
}
