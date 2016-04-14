package com.drakos;

import com.jogamp.opengl.GL4;
import java.nio.ByteBuffer;

/**
 *
 * @author Drakos
 */
public interface Prerenderable {

    public void prerender(ByteBuffer transformPointer, float dt, GL4 gl, int program, int voaId);
}
