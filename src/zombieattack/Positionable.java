package zombieattack;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Positionable {
    protected Vector3f position = new Vector3f(0f, 0f, 0f);
    protected Vector3f rotation = new Vector3f(0, 0, 0);
    protected Vector3f scale = new Vector3f(1f, 1f, 1f);

    Vector3f getPosition() {
        return position;
    }

    void setPosition(float x, float y, float z) {
        position.x = x;
        position.y = y;
        position.z = z;
    }
    void setPosition(Vector3f position) {
        this.position = position;
    }

    Quaternionf getRotationQuat() {
        Quaternionf rotationQuat = new Quaternionf();
        rotationQuat.identity();
        rotationQuat.rotateX((float) Math.toRadians(rotation.x)).rotateY((float) Math.toRadians(rotation.y)).rotateZ((float) Math.toRadians(rotation.z));
        return rotationQuat;
    }
    Quaternionf getInvertedRotationQuat() {
        Quaternionf rotationQuat = new Quaternionf();
        rotationQuat.identity();
        rotationQuat.rotateZ((float) Math.toRadians(rotation.z)).rotateY((float) Math.toRadians(rotation.y)).rotateX((float) Math.toRadians(rotation.x));
        return rotationQuat;
    }

    Vector3f getRotation() {
        return rotation;
    }

    void setRotation(float x, float y, float z) {
        if (x >= 360) x %= 360;
        if (y >= 360) y %= 360;
        if (z >= 360) z %= 360;

        rotation = new Vector3f(x, y, z);
    }
    void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }
    void setRotationY(float y) {
        if (y >= 360) y %= 360;
        rotation.y = y;
    }
    void setRotationZ(float z) {
        if (z >= 360) z %= 360;
        rotation.z = z;
    }
    void setRotationX(float x) {
        if (x >= 360) x %= 360;
        rotation.x = x;
    }

    void addRotation(float x, float y, float z) {
        setRotation(rotation.x + x, rotation.y + y, rotation.z + z);
    }

    Vector3f getScale() {
        return scale;
    }
    void setScale(float x, float y, float z) {
        scale.x = x;
        scale.y = y;
        scale.z = z;
    }
}