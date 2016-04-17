package com.drakos.shader;

import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Drakos
 */
public class ShaderContainer {

    private static final String SHADERS_ROOT = "/shaders";
    private final Map<Integer, ShaderProgram> shaders = new HashMap<>();

    public int initShaderProgram(GL4 gl4, String shader) {
        ShaderCode vertShader = ShaderCode.create(gl4, GL_VERTEX_SHADER, ShaderContainer.class, SHADERS_ROOT, null, shader, "vert", null, true);
        ShaderCode fragShader = ShaderCode.create(gl4, GL_FRAGMENT_SHADER, ShaderContainer.class, SHADERS_ROOT, null, shader, "frag", null, true);

        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.add(vertShader);
        shaderProgram.add(fragShader);

        shaderProgram.init(gl4);
        shaderProgram.link(gl4, System.out);

        shaders.put(shaderProgram.id(), shaderProgram);

        return shaderProgram.id();
    }

    public void disposeShader(int id, GL4 gl) {
        ShaderProgram program = shaders.remove(id);
        program.destroy(gl);
    }

    public void dispose(GL4 gl) {
        Collection<ShaderProgram> allShaders = shaders.values();
        for (ShaderProgram program : allShaders) {
            program.destroy(gl);
        }
        
        shaders.clear();
    }
}
