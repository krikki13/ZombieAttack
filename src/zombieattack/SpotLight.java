package zombieattack;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static zombieattack.Main.UNIF_SPOT_LIGHT;
import static zombieattack.Main.WINDOW_HEIGHT;
import static zombieattack.Main.WINDOW_WIDTH;

public class SpotLight extends Positionable {
    static List<SpotLight> list = new ArrayList<>();

    Vector3f color = new Vector3f(0.5f, 0.5f, 0.5f);
    float intensity = 1f;
    float angle = 120f;

    void setColor(Vector3f color) {
        this.color = color;
    }

    void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    Vector3f getColor() {
        return color;
    }

    float getIntensity() {
        return intensity;
    }

    void setAngle(float angle) {
        this.angle = angle;
    }

    float getCutOff() {
        return (float) Math.cos(Math.toRadians(angle));
    }

    Vector3f getConeDirection() {
        return new Vector3f(1,1,1).normalize().rotate(getRotationQuat());
    }

    static void createUniforms(ShaderProgram shaderProgram) {
        for (int i = 0; i < list.size(); i++) {
            shaderProgram.createUniform(UNIF_SPOT_LIGHT + "[" + i + "].position");
            shaderProgram.createUniform(UNIF_SPOT_LIGHT + "[" + i + "].spotColor");
            shaderProgram.createUniform(UNIF_SPOT_LIGHT + "[" + i + "].intensity");
            shaderProgram.createUniform(UNIF_SPOT_LIGHT + "[" + i + "].coneDirection");
            shaderProgram.createUniform(UNIF_SPOT_LIGHT + "[" + i + "].cutOffAngle");
        }
    }

    static void setUniforms(ShaderProgram shaderProgram) {
        for (int i = 0; i < list.size(); i++) shaderProgram.setUniform(UNIF_SPOT_LIGHT + "[" + i + "]", list.get(i));
    }
}
