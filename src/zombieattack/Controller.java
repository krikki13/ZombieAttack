package zombieattack;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWCursorPosCallback;

import static org.lwjgl.glfw.GLFW.*;
import static zombieattack.Main.WINDOW_HEIGHT;
import static zombieattack.Main.WINDOW_WIDTH;

public class Controller extends GLFWCursorPosCallback {
    private static final int HALF_WIN_WIDTH = WINDOW_WIDTH / 2;
    private static final int HALF_WIN_HEIGHT = WINDOW_HEIGHT / 2;
    static Controller instance = new Controller();

    static Vector2f mousePos = new Vector2f();

    boolean initialized = false;

    @Override
    public void invoke(long window, double xpos, double ypos) {
        if (!initialized) {
            glfwSetCursorPos(window, WINDOW_WIDTH / 2, WINDOW_HEIGHT / 2);
            initialized = true;
        }
        mousePos.x = (float) xpos - HALF_WIN_WIDTH;
        mousePos.y = (float) (HALF_WIN_HEIGHT - ypos);
    }

    static boolean isKeyPressed(int key) {
        return glfwGetKey(Main.window, key) == GLFW_PRESS;
    }
    static boolean isMouseButtonPressed(int mouseButton) {
        return glfwGetMouseButton(Main.window, mouseButton) == GLFW_PRESS;
    }
}

