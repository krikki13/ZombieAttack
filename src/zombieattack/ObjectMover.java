package zombieattack;

import static org.lwjgl.glfw.GLFW.*;

public class ObjectMover {
    static Positionable selectedPositionable = null;

    static void selectObject(Positionable obj) {
        selectedPositionable = obj;
    }

    static void update(double delta) {
        if (selectedPositionable != null) {
            if (Controller.isKeyPressed(GLFW_KEY_UP)) selectedPositionable.getPosition().x += delta * 3;
            if (Controller.isKeyPressed(GLFW_KEY_DOWN)) selectedPositionable.getPosition().x -= delta * 3;
            if (Controller.isKeyPressed(GLFW_KEY_LEFT)) selectedPositionable.getPosition().z -= delta * 3;
            if (Controller.isKeyPressed(GLFW_KEY_RIGHT)) selectedPositionable.getPosition().z += delta * 3;
            if (Controller.isKeyPressed(GLFW_KEY_COMMA)) selectedPositionable.getPosition().y += delta * 3;
            if (Controller.isKeyPressed(GLFW_KEY_PERIOD)) selectedPositionable.getPosition().y -= delta * 3;

            if (selectedPositionable instanceof SpotLight && Controller.isKeyPressed(GLFW_KEY_LEFT_ALT)) {
                if (Controller.isKeyPressed(GLFW_KEY_UP)) selectedPositionable.addRotation((float) delta * 30, 0, 0);
                if (Controller.isKeyPressed(GLFW_KEY_DOWN)) selectedPositionable.addRotation((float) - delta * 30, 0, 0);
                if (Controller.isKeyPressed(GLFW_KEY_LEFT)) selectedPositionable.addRotation(0, 0, (float) - delta * 30);
                if (Controller.isKeyPressed(GLFW_KEY_RIGHT)) selectedPositionable.addRotation(0, 0, (float) delta * 30);
                if (Controller.isKeyPressed(GLFW_KEY_COMMA)) selectedPositionable.addRotation(0, (float) delta * 30, 0);
                if (Controller.isKeyPressed(GLFW_KEY_PERIOD)) selectedPositionable.addRotation(0, (float) - delta * 30, 0);
            }
        }
    }
}
