package zombieattack;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transformation {
    private static Matrix4f projectionMatrix = new Matrix4f();
    private static Matrix4f modelViewMatrix = new Matrix4f();
    private static Matrix4f viewMatrix = new Matrix4f();

    public static Matrix4f getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;        
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

    public static Matrix4f getModelViewMatrix(Vector3f offset, Vector3f rotation, Vector3f scale) {
        Quaternionf quaternionf = new Quaternionf();
        quaternionf.rotateZ((float) Math.toRadians(rotation.z));
        quaternionf.rotateY((float) Math.toRadians(rotation.y));
        quaternionf.rotateX((float) Math.toRadians(rotation.x));
        modelViewMatrix.identity().
                translate(offset).
                rotate(quaternionf).
                scale(scale);
        return modelViewMatrix;
    }

    public static Matrix4f getViewMatrix() {
        return viewMatrix;
    }
    public static void updateViewMatrix(Player camera){
        Vector3f cameraPos = camera.getPosition();
        Vector3f rotation = camera.getRotation();

        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
    }
}
