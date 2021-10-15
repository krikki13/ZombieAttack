package zombieattack;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Scanner;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class ShaderProgram {
    int vertexShader;
    int fragmentShader;
    int shaderProgram;

    private HashMap<String, Integer> uniforms = new HashMap<>();

    /**
     * Prebere vertex shader datoteko in zamenja vse pojavitve POINT_LIGHT_COUNT s številom luči v seznamu PointLight list.
     * @param filePath Pot do datoteke, ki vsebuje vertex shader kodo
     */
    void loadVertexShader(String filePath) {
        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        String vertexString = readFile(filePath);
        vertexString = vertexString.replaceAll("SPOT_LIGHT_COUNT", String.valueOf(SpotLight.list.size()));
        glShaderSource(vertexShader, vertexString);
        glCompileShader(vertexShader);
        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) != GL_TRUE) throw new RuntimeException(glGetShaderInfoLog(vertexShader));
    }

    /**
     * Prebere fragment shader datoteko in zamenja vse pojavitve POINT_LIGHT_COUNT s številom luči v seznamu PointLight list.
     * @param filePath Pot do datoteke, ki vsebuje fragment shader kodo
     */
    void loadFragmentShader(String filePath) {
        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        String fragmentString = readFile(filePath);
        fragmentString = fragmentString.replaceAll("SPOT_LIGHT_COUNT", String.valueOf(SpotLight.list.size()));
        glShaderSource(fragmentShader, fragmentString);
        glCompileShader(fragmentShader);
        if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) != GL_TRUE) throw new RuntimeException(glGetShaderInfoLog(fragmentShader));
    }

    private static String readFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                sb.append(scanner.nextLine()).append('\n');
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    void compileProgram() {
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        if (vertexShader != 0) glDetachShader(shaderProgram, vertexShader);
        if (fragmentShader != 0) glDetachShader(shaderProgram, fragmentShader);
        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) != GL_TRUE) throw new RuntimeException(glGetProgramInfoLog(shaderProgram));
    }

    void createUniform(String name) {
        int unif = glGetUniformLocation(shaderProgram, name);
        if (unif < 0) throw new RuntimeException("Could not find uniform: " + name);
        uniforms.put(name, unif);
    }

    void setUniform(String name, int value) {
        glUniform1i(uniforms.get(name), value);
    }

    void setUniform(String name, float value) {
        glUniform1f(uniforms.get(name), value);
    }

    void setUniform(String name, Matrix4f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);
            matrix.get(fb);
            glUniformMatrix4fv(uniforms.get(name), false, fb);
        }
    }

    void setUniform(String name, Matrix3f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = stack.mallocFloat(9);
            matrix.get(fb);
            glUniformMatrix3fv(uniforms.get(name), false, fb);
        }
    }

    void setUniform(String name, SpotLight spotLight) {
        // spot light's position and direction is transformed to view coordinates
        Vector4f lightPos = new Vector4f(spotLight.getPosition(), 1.0f);
        lightPos.mul(Transformation.getViewMatrix());

        Vector4f lightDir = new Vector4f(spotLight.getConeDirection(), 0);
        lightDir.mul(Transformation.getViewMatrix());

        setUniform(name + ".position",  new Vector3f(lightPos.x, lightPos.y, lightPos.z));
        setUniform(name + ".spotColor", spotLight.getColor());
        setUniform(name + ".intensity", spotLight.getIntensity());
        setUniform(name + ".coneDirection", new Vector3f(lightDir.x, lightDir.y, lightDir.z));
        setUniform(name + ".cutOffAngle", spotLight.getCutOff());
    }

    void setUniform(String name, Material material) {
        setUniform(name + ".ambient", material.ambient);
        setUniform(name + ".diffuse", material.diffuse);
        setUniform(name + ".specular", material.specular);
        setUniform(name + ".hasTexture", material.texture != null ? 1 : 0);
        setUniform(name + ".hasNormalMap", material.normalMap != null ? 1 : 0);
        setUniform(name + ".reflectance", material.reflectance);
    }

    void setUniform(String name, Vector3f vector) {
        glUniform3f(uniforms.get(name), vector.x, vector.y, vector.z);
    }

    void setUniform(String name, Vector4f vector) {
        glUniform4f(uniforms.get(name), vector.x, vector.y, vector.z, vector.w);
    }

    void bind() {
        glUseProgram(shaderProgram);
    }

    void unbind() {
        glUseProgram(0);
    }
}
