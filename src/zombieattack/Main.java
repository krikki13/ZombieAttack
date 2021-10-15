package zombieattack;

import com.sun.glass.ui.Window;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.openal.ALC10.alcCloseDevice;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;


import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static zombieattack.World.FLOOR_LEVEL;
import static zombieattack.World.zombies;

public class Main {
    static int WINDOW_WIDTH = 1900;
    static int WINDOW_HEIGHT = 1080;
    static final String UNIF_PMAT = "pMatrix";
    static final String UNIF_MVMAT = "mvMatrix";
    static final String UNIF_SPOT_LIGHT = "spotLight";

    static List<ShaderProgram> shaderProgramList = new ArrayList<>();
    static Player player = new Player();
    static World world;

    static long window;

    static SoundSource loseSound;
    static SoundSource winSound;

    public void run() {
        System.out.println("LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        glfwTerminate();
        //glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation will print the error message in System.err.
        //GLFWErrorCallback.createPrint(System.err).set();   ### Zakomentiral sem zato, ker mi je crashalo zaradi tega

        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
        //glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3); // s temi 4 vrsticami zakomentiranimi
        //glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2); // PlaysTv igro razpozna in začne snemati
        //glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        //glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);



        long monitor = glfwGetPrimaryMonitor();
        GLFWVidMode vidmode = glfwGetVideoMode(monitor);
        WINDOW_WIDTH = vidmode.width();
        WINDOW_HEIGHT = vidmode.height();
        window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "Zombie Attack", monitor, NULL);

        if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        glfwSetCursorPosCallback(window, Controller.instance);
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1); // za vsync
        glfwShowWindow(window);
    }

    private void loop() {
        GL.createCapabilities();

        glClearColor(0.2f, 0.2f, 0.2f, 0f);
        glClearDepth(1.0);
        glEnable(GL_POLYGON_SMOOTH);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        try{
            SoundSource.init();
        }catch (Exception e) {
            e.printStackTrace();
        }


        // Luči je treba ustvarit pred shaderjem, saj se takrat prebere, koliko je luči in posodobi v shaderju
        SpotLight sl = new SpotLight();
        sl.setColor(new Vector3f(0.9f, 0.9f, 0.9f));
        // strop je na 4, luč je malo višje zato da osvetli še del stropa
        sl.setPosition(new Vector3f(0, FLOOR_LEVEL+5.5f, -7));
        sl.setRotation(-35,0,45);
        sl.setIntensity(0.5f);
        sl.setAngle(70f);
        SpotLight.list.add(sl);

        SpotLight sl2 = new SpotLight();
        sl2.setColor(new Vector3f(0.9f, 0.9f, 0.9f));
        // strop je na 4, luč je malo višje zato da osvetli še del stropa
        sl2.setPosition(new Vector3f(0, FLOOR_LEVEL+5.5f, -26));
        sl2.setRotation(-25,0,45);
        sl2.setIntensity(0.5f);
        sl2.setAngle(70f);
        SpotLight.list.add(sl2);

        SpotLight sl3 = new SpotLight();
        sl3.setColor(new Vector3f(0.9f, 0.9f, 0.9f));
        // strop je na 4, luč je malo višje zato da osvetli še del stropa
        sl3.setPosition(new Vector3f(-15, FLOOR_LEVEL+5.5f, -10));
        sl3.setRotation(-35,0,45);
        sl3.setIntensity(1f);
        sl3.setAngle(70f);
        SpotLight.list.add(sl3);

        ShaderProgram sp = new ShaderProgram();
        shaderProgramList.add(sp);
        sp.loadVertexShader("resources/shaders/shader.v");
        sp.loadFragmentShader("resources/shaders/shader.f");
        sp.compileProgram();
        sp.createUniform(UNIF_PMAT);
        sp.createUniform(UNIF_MVMAT);
        sp.createUniform("texSampler");
        sp.createUniform("color");
        sp.createUniform("normalMap");
        SpotLight.createUniforms(sp);
        Fog.createUniforms(sp);

        player.setPosition(0,FLOOR_LEVEL+2.85f,0);
        try {
            loseSound = new SoundSource("resources/sounds/neutralized.wav");
            loseSound.setVolume(1f);
            winSound = new SoundSource("resources/sounds/victory.wav");
            winSound.setVolume(1f);
            player.ouchSound = new SoundSource("resources/sounds/ouch.wav");
            player.ouchSound.setVolume(0.5f);
        } catch (Exception e) {
            e.printStackTrace();
        }

        world = new World(player, sp);

        double frameTime;
        double lastFrameTime = glfwGetTime();
        double delta;

        while (!glfwWindowShouldClose(window)) {
            frameTime = glfwGetTime();
            delta = frameTime - lastFrameTime;
            lastFrameTime = frameTime;

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            player.update(delta);

            Transformation.updateViewMatrix(player);
            world.update();

            glEnable(GL_DEPTH_TEST);
            glDepthFunc(GL_LEQUAL);
            glDisable(GL_BLEND);
            world.render();

            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            world.renderTransparentObjects();

            glfwSwapBuffers(window); // swap the color buffers
            glfwPollEvents();
        }

        if (Player.killed) {
            try {
                Thread.sleep(1000);
                loseSound.play();
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (Player.won) {
            try {
                Thread.sleep(500);
                winSound.play();
                Thread.sleep(5050);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (SoundSource ss : SoundSource.list) ss.clean();
        alcCloseDevice(SoundSource.deviceId);
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
