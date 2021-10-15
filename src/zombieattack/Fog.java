package zombieattack;

import org.joml.Vector3f;

public class Fog extends Positionable {
    static Vector3f color = new Vector3f(0.3f, 0.3f, 0.3f);
    final static float DEFAULT_DENSITY = 0.05f;
    static float density = DEFAULT_DENSITY;

    static void createUniforms(ShaderProgram shaderProgram) {
        shaderProgram.createUniform("fog.color");
        shaderProgram.createUniform("fog.density");
    }

    static void setUniforms(ShaderProgram shaderProgram) {
        shaderProgram.setUniform("fog.color", color);
        shaderProgram.setUniform("fog.density", density);
    }
    static void setUniforms(ShaderProgram shaderProgram, float fogDensity) {
        shaderProgram.setUniform("fog.color", color);
        shaderProgram.setUniform("fog.density", fogDensity);
    }
}