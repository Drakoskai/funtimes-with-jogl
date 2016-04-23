package com.drakos.shader;

import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 *
 * @author Drakos
 */
public class ShaderContainer {

    private static final String SHADERS_ROOT = "/shaders";
    private final Map<Class, ShaderProgram> shadersByClass = new HashMap<>();
    private final Map<String, ShaderProgram> shadersByName = new HashMap<>();

    public int initShaderProgram(GL4 gl4, Class id, String shader) {
        int shaderId;
        if (!shadersByName.containsKey(shader)) {
            ShaderCode vertShader = ShaderCode.create(gl4, GL_VERTEX_SHADER, ShaderContainer.class, SHADERS_ROOT, null, shader, "vert", null, true);
            ShaderCode fragShader = ShaderCode.create(gl4, GL_FRAGMENT_SHADER, ShaderContainer.class, SHADERS_ROOT, null, shader, "frag", null, true);

            ShaderProgram shaderProgram = new ShaderProgram();
            shaderProgram.add(vertShader);
            shaderProgram.add(fragShader);

            shaderProgram.init(gl4);
            shaderProgram.link(gl4, System.out);

            shadersByClass.put(id, shaderProgram);
            shadersByName.put(shader, shaderProgram);
            shaderId = shaderProgram.id();
        } else {
            ShaderProgram shaderProgram = shadersByName.get(shader);
            shaderId = shaderProgram.id();
            if (!shadersByClass.containsKey(id)) {
                shadersByClass.put(id, shaderProgram);
            }
        }

        return shaderId;
    }

    public void disposeShader(String id, GL4 gl) {
        ShaderProgram program = shadersByName.remove(id);
        Optional<Class> key = Optional.empty();
        for (Entry<Class, ShaderProgram> entry : shadersByClass.entrySet()) {
            if (entry.getValue().equals(program)) {
                key = Optional.of(entry.getKey());
                break;
            }
        }
        if (key.isPresent()) {
            shadersByClass.remove(key.get());
        }
        program.destroy(gl);
    }

    public void disposeShader(Class id, GL4 gl) {
        ShaderProgram program = shadersByClass.remove(id);
        Optional<String> key = Optional.empty();
        for (Entry<String, ShaderProgram> entry : shadersByName.entrySet()) {
            if (entry.getValue().equals(program)) {
                key = Optional.of(entry.getKey());
                break;
            }
        }
        if (key.isPresent()) {
            shadersByName.remove(key.get());
        }
        program.destroy(gl);
    }

    public void dispose(GL4 gl) {
        Collection<ShaderProgram> allShaders = shadersByClass.values();
        for (ShaderProgram program : allShaders) {
            program.destroy(gl);
        }

        shadersByClass.clear();
    }

    public int getShader(String id) {
        return this.shadersByName.get(id).id();
    }

    public int getShader(Class id) {
        return this.shadersByClass.get(id).id();
    }
}
