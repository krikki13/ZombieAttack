package zombieattack;

import org.joml.Vector3f;

public class Material {
    Vector3f ambient = new Vector3f(0.15f, 0.15f, 0.15f);
    Vector3f diffuse = new Vector3f(0.5f, 0.5f, 0.5f);
    Vector3f specular = new Vector3f(0.5f, 0.5f, 0.5f);
    float reflectance = 15f;
    Texture texture;
    Texture normalMap = null;

    Material(Texture texture) {
        this.texture = texture;
    }

    Material(Texture texture, Texture normalMap) {
        this.texture = texture;
        this.normalMap = normalMap;
    }

    void createUniforms(ShaderProgram shaderProgram) {
        shaderProgram.createUniform("material.ambient");
        shaderProgram.createUniform("material.diffuse");
        shaderProgram.createUniform("material.specular");
        shaderProgram.createUniform("material.hasTexture");
        shaderProgram.createUniform("material.reflectance");
        shaderProgram.createUniform("material.hasNormalMap");
    }

    void setUniforms(ShaderProgram shaderProgram) {
        shaderProgram.setUniform("material", this);
    }

}