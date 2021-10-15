package zombieattack;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static zombieattack.Main.UNIF_MVMAT;

public class GameObject extends Positionable {
    Mesh mesh;
    ShaderProgram shaderProgram;

    public void setColor(Vector4f color) {
        this.color = color;
    }

    Vector4f color = new Vector4f(0.5f, 0.5f, 0.5f, 1);
    float fogDensity = -1;

    GameObject(Mesh mesh, ShaderProgram shaderProgram) {
        this.mesh = mesh;
        this.shaderProgram = shaderProgram;
    }

    void render() {
        shaderProgram.bind();
        Matrix4f mvMatrix = Transformation.getModelViewMatrix(position, rotation, scale);
        Matrix4f view1 = new Matrix4f(Transformation.getViewMatrix());
        view1.mul(mvMatrix);
        shaderProgram.setUniform(UNIF_MVMAT, view1);
        if (mesh.material != null && mesh.material.texture != null) {
            shaderProgram.setUniform("texSampler", 0);
        } else {
            shaderProgram.setUniform("color", color);
        }
        if (mesh.material != null && mesh.material.normalMap != null) {
            shaderProgram.setUniform("normalMap", 1);
        }
        if (mesh.material != null) mesh.material.setUniforms(shaderProgram);
        SpotLight.setUniforms(shaderProgram);
        if(fogDensity == -1)
            Fog.setUniforms(shaderProgram);
        else
            Fog.setUniforms(shaderProgram, fogDensity);
        mesh.render();
    }
}