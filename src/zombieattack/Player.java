package zombieattack;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static zombieattack.Main.*;

public class Player extends Positionable {
    static Room inRoom;
    static float health = 10;
    static SoundSource ouchSound;
    static boolean killed = false;
    static boolean won = false;

    void update(double delta) {
        float rotationFactor = 0.2f;
        float rx = - Controller.mousePos.y * rotationFactor;
        float ry = Controller.mousePos.x * rotationFactor;

        setRotation(rx, ry, getRotation().z);

        float movementFactor = 4;
        float rotationY = (float) Math.toRadians(rotation.y);
        Vector3f prevPos = new Vector3f(position);
        if(Controller.isKeyPressed(GLFW_KEY_W)){
            position.x += Math.sin(rotationY) * movementFactor * delta;
            position.z -= Math.cos(rotationY) * movementFactor * delta;
        }else if(Controller.isKeyPressed(GLFW_KEY_S)){
            position.x -= Math.sin(rotationY) * movementFactor * delta;
            position.z += Math.cos(rotationY) * movementFactor * delta;
        }
        if(Controller.isKeyPressed(GLFW_KEY_D)){
            position.x -= Math.sin(rotationY - Math.PI/2) * movementFactor * delta;
            position.z += Math.cos(rotationY - Math.PI/2) * movementFactor * delta;
        }else if(Controller.isKeyPressed(GLFW_KEY_A)){
            position.x += Math.sin(rotationY - Math.PI/2) * movementFactor * delta;
            position.z -= Math.cos(rotationY - Math.PI/2) * movementFactor * delta;
        }
        /*if (Controller.isKeyPressed(GLFW_KEY_LEFT_SHIFT)) {
            getPosition().y -= movementFactor * delta;
        }else if (Controller.isKeyPressed(GLFW_KEY_SPACE)) {
            getPosition().y += movementFactor * delta;
        }*/
        float min = Float.MAX_VALUE;
        int inRoomCounter = 0;
        for (int i = 0; i < World.rooms.size(); i++) {
            if(World.rooms.get(i).isInRoom(position)) {
                inRoomCounter++;
                float temp = World.rooms.get(i).toClosestWall(position);
                if (temp < min) {
                    min = temp;
                }
            }
        }
        if(inRoomCounter==0 || inRoomCounter == 1 && min < 0.8f){
            position = prevPos;
        }
        /*if (Controller.isKeyPressed(GLFW_KEY_I)) {
            ObjectMover.selectObject(Main.gameObjects.get(Main.gameObjects.size() - 1));
        }
        if (Controller.isKeyPressed(GLFW_KEY_O)) {
            ObjectMover.selectObject(SpotLight.list.get(0));
        }*/
        ObjectMover.update(delta);
        //System.out.printf("X %.3f Y %.3f Z %.3f XR %.3f YR %.3f ZR %.3f\n", getPosition().x, getPosition().y, getPosition().z, getRotation().x, getRotation().y, getRotation().z);

        updateProjectionMatrix();
        SoundSource.updateListenerLocation(getPosition(), getRotationQuat());
    }

    void updateProjectionMatrix() {
        Matrix4f projMat = Transformation.getProjectionMatrix((float) Math.toRadians(45), WINDOW_WIDTH, WINDOW_HEIGHT, 0.1f, 100.f);

        for (ShaderProgram sp : Main.shaderProgramList) {
            sp.bind();
            sp.setUniform(UNIF_PMAT, projMat);
        }
        glUseProgram(0);
    }
    public void hit(float damage){
        System.out.println("OUCH ("+health+" left)");
        if (!ouchSound.playing) ouchSound.play();
        health -= damage;
        if(health <= 0) dead();
    }
    private void dead(){
        killed = true;
        System.out.println("YOU DIED");
        glfwSetWindowShouldClose(window, true);
    }
}