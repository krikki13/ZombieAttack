package zombieattack;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static zombieattack.Main.window;
import static zombieattack.World.*;

import org.joml.Vector4f;
import zombieattack.World;

abstract class Room{
    float BORDER_MIN_X;
    float BORDER_MAX_X;
    float BORDER_MIN_Y;
    float BORDER_MAX_Y;
    float BORDER_MIN_Z;
    float BORDER_MAX_Z;
    List<GameObject> roomObjects = new LinkedList<>();
    List<GameObject> transparentRoomObjects = new LinkedList<>();
    float fog;

    abstract void createGameObjects();
    void renderObjects() {
        Fog.density = fog;
        for(GameObject go : roomObjects)
            go.render();
    }
    void renderTransparentObjects() {
        for(GameObject go : transparentRoomObjects)
            go.render();
    }

    boolean isInRoom(Vector3f position) {
        if(position.x >= BORDER_MIN_X && position.x <= BORDER_MAX_X
                && position.y >= BORDER_MIN_Y && position.y <= BORDER_MAX_Y &&
                position.z >= BORDER_MIN_Z && position.z <= BORDER_MAX_Z)
            return true;
        return false;
    }
    float toClosestWall(Vector3f position){
        return Math.min(Math.min(Math.abs(position.x - BORDER_MIN_X), Math.abs(position.x - BORDER_MAX_X)),
                       Math.min(Math.abs(position.z - BORDER_MIN_Z), Math.abs(position.z - BORDER_MAX_Z)));
    }
    public void update(){ }
    public void setZombies(){ }

}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
class R1 extends Room{
    public R1() {
        fog = 0;
        BORDER_MIN_X = -3;
        BORDER_MAX_X = 3;
        BORDER_MIN_Y = 0;
        BORDER_MAX_Y = 5;
        BORDER_MIN_Z = -28;
        BORDER_MAX_Z = 10.5f;
    }

    void createGameObjects() {
        GameObject go;
        // tla (quartz-i) in stene (iron)
        for (int i = -3; i < 8; i++) {
            float f = -1;
            if(i >= 1)
                f = i / 160.0f;
            // tla
            go = new GameObject(tileQuartzMesh, shaderProgram);
            go.setPosition(-1.5f,FLOOR_LEVEL, -3*i); go.setScale(1.5f,1,1.5f);
            go.fogDensity = f;
            roomObjects.add(go);
            go = new GameObject(tileQuartzMesh, shaderProgram);
            go.setPosition(1.5f,FLOOR_LEVEL,-3*i); go.setScale(1.5f,1,1.5f);go.fogDensity = f;
            roomObjects.add(go);
            // strop
            go = new GameObject(tileQuartzMesh, shaderProgram);
            go.setPosition(-1.5f,FLOOR_LEVEL+5, -3*i); go.setScale(1.5f,1,1.5f);go.fogDensity = f;

            roomObjects.add(go);
            go = new GameObject(tileQuartzMesh, shaderProgram);
            go.setPosition(1.5f,FLOOR_LEVEL+5,-3*i); go.setScale(1.5f,1,1.5f);go.fogDensity = f;
            roomObjects.add(go);
            // leva stena
            if(i == 3 || i == 4) {
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(-3f, FLOOR_LEVEL + 0.85f, -3 * i);go.fogDensity = f;
                go.setScale(0.85f, 1f, 1.5f);
                go.setRotation(0, 0, -90);
                roomObjects.add(go);
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(-3f, FLOOR_LEVEL + 4.6f, -3 * i);go.fogDensity = f;
                go.setScale(0.4f, 0.1f, 1.5f);
                go.setRotation(0, 0, -90);
                roomObjects.add(go);
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(-3.25f, FLOOR_LEVEL + 1.7f, -3 * i);go.fogDensity = f;
                go.setScale(0.25f, 1f, 1.5f);
                roomObjects.add(go);
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(-3.25f, FLOOR_LEVEL + 4.2f, -3 * i);go.fogDensity = f;
                go.setScale(0.25f, -1f, 1.5f);
                roomObjects.add(go);
            }else {
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(-3f, FLOOR_LEVEL + 1.25f, -3 * i);
                go.setScale(1.25f, 1, 1.5f);
                go.fogDensity = f;
                go.setRotation(0, 0, -90);
                roomObjects.add(go);
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(-3f, FLOOR_LEVEL + 3.75f, -3 * i);
                go.setScale(1.25f, 1, 1.5f);
                go.fogDensity = f;
                go.setRotation(0, 0, -90);
                roomObjects.add(go);
            }

            // desna stena
            go = new GameObject(tileIronMesh, shaderProgram);
            go.setPosition(3f,FLOOR_LEVEL+1.25f, -3*i); go.setScale(1.25f,1,1.5f);go.fogDensity = f;
            go.setRotation(0,0,90);
            roomObjects.add(go);
            go = new GameObject(tileIronMesh, shaderProgram);
            go.setPosition(3f,FLOOR_LEVEL+3.75f, -3*i); go.setScale(1.25f,1,1.5f);go.fogDensity = f;
            go.setRotation(0,0,90);
            roomObjects.add(go);
        }
        // ozke stene ob oknu
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-3.25f, FLOOR_LEVEL + 2.95f, -13.5f);
        go.setScale(0.25f, 1, 1.5f);
        go.setRotation(90,0,0);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-3.25f, FLOOR_LEVEL + 2.95f, -7.5f);
        go.setScale(0.25f, 1, 1.5f);
        go.setRotation(-90,0,0);
        roomObjects.add(go);

        // tla proti koncu hodnika
        for(int i=-24; i>=-27; i-=3){
            for (int j = -1; j <= 1; j+=2) {
                //tla
                go = new GameObject(tileQuartzMesh, shaderProgram);
                go.setPosition(j*1.5f, FLOOR_LEVEL, i);
                go.setScale(1.5f, 1, 1.5f);
                go.fogDensity = 0.05f;
                roomObjects.add(go);
                // strop
                go = new GameObject(tileQuartzMesh, shaderProgram);
                go.setPosition(j*1.5f, FLOOR_LEVEL + 5, i);
                go.setScale(1.5f, 1, 1.5f);
                go.fogDensity = 0.05f;
                roomObjects.add(go);

            }
            // stena na koncu
            go = new GameObject(tileIronMesh, shaderProgram);
            go.setPosition(3f, FLOOR_LEVEL + 1.25f, i);
            go.setScale(1.25f, 1, 1.5f);
            go.setRotation(0,0,90);
            go.fogDensity = 0.05f;
            roomObjects.add(go);
            go = new GameObject(tileIronMesh, shaderProgram);
            go.setPosition(3f, FLOOR_LEVEL + 3.75f, i);
            go.setScale(1.25f, 1, 1.5f);
            go.setRotation(0,0,90);
            go.fogDensity = 0.05f;
            roomObjects.add(go);

        }

        // logo na tleh
        go = new GameObject(logoMesh, shaderProgram);
        go.setPosition(-0.86f,FLOOR_LEVEL+0.05f, -7);
        transparentRoomObjects.add(go);
        go = new GameObject(logotipMesh, shaderProgram);
        go.setPosition(0.65f,FLOOR_LEVEL+0.05f, -7);
        transparentRoomObjects.add(go);

        // stena na začetku
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-1.5f, FLOOR_LEVEL + 1.25f, 10.5f); go.setScale(1.25f, 1, 1.5f);
        go.setRotation(90, 0, 90);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(1.5f, FLOOR_LEVEL + 1.25f, 10.5f);go.setScale(1.25f, 1, 1.5f);
        go.setRotation(90, 0, 90);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-1.5f, FLOOR_LEVEL + 3.75f, 10.5f);go.setScale(1.25f, 1, 1.5f);
        go.setRotation(90, 0, 90);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(1.5f, FLOOR_LEVEL + 3.75f, 10.5f);go.setScale(1.25f, 1, 1.5f);
        go.setRotation(90, 0, 90);
        roomObjects.add(go);

        // stena na koncu hodnika
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-1.5f, FLOOR_LEVEL + 1.25f, -28.5f); go.setScale(1.25f, 1, 1.5f);
        go.setRotation(90, 0, 90);
        go.fogDensity = 0.05f;
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-1.5f, FLOOR_LEVEL + 3.75f, -28.5f);go.setScale(1.25f, 1, 1.5f);
        go.setRotation(90, 0, 90);
        go.fogDensity = 0.05f;
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(1.5f, FLOOR_LEVEL + 1.25f, -28.5f);go.setScale(1.25f, 1, 1.5f);
        go.setRotation(90, 0, 90);
        go.fogDensity = 0.05f;
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(1.5f, FLOOR_LEVEL + 3.75f, -28.5f);go.setScale(1.25f, 1, 1.5f);
        go.setRotation(90, 0, 90);
        go.fogDensity = 0.05f;
        roomObjects.add(go);


        // luč
        go = new GameObject(halfCircleMesh, shaderProgram);
        go.setPosition(new Vector3f(0, FLOOR_LEVEL+4.96f, -7));
        go.setScale(1,0.5f,1);
        go.setColor(new Vector4f(1,1,1,1));
        roomObjects.add(go);

        Door door = new Door(this, Door.AXIS_SIDE_RIGHT, true, 1);
        door.setPosition(new Vector3f(1, FLOOR_LEVEL, -28.5f));
        for (int i = 0; i < door.doorParts.length; i++) {
            door.doorParts[i].fogDensity = 0.05f;
        }

        door = new Door(this, Door.AXIS_SIDE_RIGHT, true, 0);
        door.setPosition(new Vector3f(2, FLOOR_LEVEL, 10.5f));
        door = new Door(this, Door.AXIS_SIDE_LEFT, true, 0);
        door.setPosition(new Vector3f(-2, FLOOR_LEVEL, 10.5f));

        go = new GameObject(signExitLeftMesh, shaderProgram);
        go.setPosition(0f,FLOOR_LEVEL+4.5f, -28.5f);
        go.setScale(1.4f, 1.4f, 1);
        go.fogDensity = 0.05f;
        roomObjects.add(go);

        go = new GameObject(halfCircleMesh, shaderProgram);
        go.setPosition(new Vector3f(0, FLOOR_LEVEL+4.96f, -26));
        go.setScale(1,0.5f,1);
        go.setColor(new Vector4f(1,1,1,1));
        go.fogDensity = 0.05f;
        roomObjects.add(go);

        go = new GameObject(glassPaneMesh, shaderProgram);
        go.setPosition(-3.25f, FLOOR_LEVEL + 2.95f, -10.5f);
        go.setScale(1.225f, 1f, 3f);
        go.setRotation(0, 0, 90);
        transparentRoomObjects.add(go);

    }
}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
class R2 extends Room{
    boolean firstTime = true;
    public R2() {
        fog = 0.07f;
        BORDER_MIN_X = -17.5f;
        BORDER_MAX_X = 3;
        BORDER_MIN_Y = 0;
        BORDER_MAX_Y = 5;
        BORDER_MIN_Z = -28.5f;
        BORDER_MAX_Z = -22.5f;
    }

    @Override
    public void update() {
        if(firstTime && Main.player.position.z < (BORDER_MAX_Z + 2)){
            setZombies();
            firstTime = false;
        }
    }

    @Override
    public void setZombies(){
        Zombie zombie = new Zombie(new Vector3f(-12, 1, -24));
        zombie.setFogDensity(fog);
        World.zombies.add(zombie);
        zombie = new Zombie(new Vector3f(-6, 1, -24));
        zombie.setFogDensity(fog);
        World.zombies.add(zombie);
    }
    void createGameObjects() {
        GameObject go;
        // tla (quartz-i) in stene (iron)
        for (int i = 1; i < 6; i++) {
            // tla in strop
            go = new GameObject(tileQuartzMesh, shaderProgram);
            go.setPosition(-1.5f-3f*i,FLOOR_LEVEL, -27); go.setScale(1.5f,1,1.5f);
            roomObjects.add(go);
            if(i==4 || i==5) {
                go = new GameObject(tileQuartzMesh, shaderProgram);
                go.setPosition(-1.5f-3f*i,FLOOR_LEVEL,-23.75f); go.setScale(1.5f,1,1.75f);
                roomObjects.add(go);
                go = new GameObject(tileQuartzMesh, shaderProgram);
                go.setPosition(-1.5f-3f*i,FLOOR_LEVEL+5,-23.75f); go.setScale(1.5f,1,1.75f);
                roomObjects.add(go);
            }else{
                go = new GameObject(tileQuartzMesh, shaderProgram);
                go.setPosition(-1.5f-3f*i,FLOOR_LEVEL,-24f); go.setScale(1.5f,1,1.5f);
                roomObjects.add(go);
                go = new GameObject(tileQuartzMesh, shaderProgram);
                go.setPosition(-1.5f-3f*i,FLOOR_LEVEL+5,-24f); go.setScale(1.5f,1,1.5f);
                roomObjects.add(go);
            }
            go = new GameObject(tileQuartzMesh, shaderProgram);
            go.setPosition(-1.5f - 3f * i, FLOOR_LEVEL + 5, -27);
            go.setScale(1.5f, 1, 1.5f);
            roomObjects.add(go);
            // desne stene
            go = new GameObject(tileIronMesh, shaderProgram);
            go.setPosition(-1.5f-3f*i,FLOOR_LEVEL+1.25f, -28.5f); go.setScale(1.25f,1,1.5f);
            go.setRotation(90,0,90);
            roomObjects.add(go);
            go = new GameObject(tileIronMesh, shaderProgram);
            go.setPosition(-1.5f-3f*i,FLOOR_LEVEL+3.75f,-28.5f); go.setScale(1.25f,1,1.5f);
            go.setRotation(90,0,90);
            roomObjects.add(go);
            // leve stene
            if(i!=4 && i!=5) {
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(-1.5f - 3f * i, FLOOR_LEVEL + 1.25f, -22.5f);
                go.setScale(1.25f, 1, 1.5f);
                go.setRotation(-90, 0, 90);
                roomObjects.add(go);
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(-1.5f - 3f * i, FLOOR_LEVEL + 3.75f, -22.5f);
                go.setScale(1.25f, 1, 1.5f);
                go.setRotation(-90, 0, 90);
                roomObjects.add(go);
            }
        }
        // stena na koncu
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-17.5f,FLOOR_LEVEL+1.25f, -23.75f); go.setScale(1.25f,1,1.75f);
        go.setRotation(0,0,-90);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-17.5f,FLOOR_LEVEL+3.75f,-23.75f); go.setScale(1.25f,1,1.75f);
        go.setRotation(0,0,-90);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-17.5f,FLOOR_LEVEL+1.25f, -27f); go.setScale(1.25f,1,1.5f);
        go.setRotation(0,0,-90);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-17.5f,FLOOR_LEVEL+3.75f,-27f); go.setScale(1.25f,1,1.5f);
        go.setRotation(0,0,-90);
        roomObjects.add(go);

        // ozke stene v prehodu
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-12f,FLOOR_LEVEL+1.25f, -22.25f); go.setScale(1.25f,1,0.25f);
        go.setRotation(0,0,90);
        go.fogDensity = 0.055f;
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-12f,FLOOR_LEVEL+3.75f,-22.25f); go.setScale(1.25f,1,0.25f);
        go.setRotation(0,0,90);
        go.fogDensity = 0.055f;
        roomObjects.add(go);



    }
}
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
class R3 extends Room{
    boolean firstTime = true;
    List<Zombie> zombies= new ArrayList<>();

    public R3(){
        fog = 0.1f;
        BORDER_MIN_X = -21.5f;
        BORDER_MAX_X = -3.5f;
        BORDER_MIN_Y = 0;
        BORDER_MAX_Y = 5;
        BORDER_MIN_Z = -21.75f;
        BORDER_MAX_Z = 1.25f;
        setZombies();
    }
    @Override
    public void update() {
        if(firstTime && Main.player.position.x < -12 ){
            for(Zombie zombie1 : zombies)
                zombie1.wait = false;
            firstTime = false;
        }
    }

    @Override
    public void setZombies(){
        Zombie zombie = new Zombie(new Vector3f(-12, 1, -6));
        zombies.add(zombie);
        zombie = new Zombie(new Vector3f(-15, 1, -15));
        zombies.add(zombie);
        zombie = new Zombie(new Vector3f(-6, 1, -18));
        zombies.add(zombie);
        zombie = new Zombie(new Vector3f(-5, 1, -5));
        zombies.add(zombie);
        zombie = new Zombie(new Vector3f(-18, 1, -5));
        zombies.add(zombie);
        for(Zombie zombie1 : zombies){
            zombie1.wait = true;
            zombie1.setFogDensity(fog);
            World.zombies.add(zombie1);
        }
    }


    @Override
    void createGameObjects() {
        GameObject go;
        for (float z = -18f; z <= 0; z+=3) {
            for (float x = -3.5f-1.5f; x >= -20; x-=3) {
                // tla
                go = new GameObject(tileQuartzMesh, shaderProgram);
                go.setPosition(x,FLOOR_LEVEL,z); go.setScale(1.5f,1,1.5f);
                roomObjects.add(go);
                go = new GameObject(tileQuartzMesh, shaderProgram);
                go.setPosition(x,FLOOR_LEVEL+5,z); go.setScale(1.5f,1,1.5f);
                roomObjects.add(go);
            }

            // oddaljena stena od startne točke
            go = new GameObject(tileIronMesh, shaderProgram);
            go.setPosition(-21.5f,FLOOR_LEVEL+1.25f,z); go.setScale(1.25f,1,1.5f);
            go.setRotation(0,0,-90);
            roomObjects.add(go);
            go = new GameObject(tileIronMesh, shaderProgram);
            go.setPosition(-21.5f,FLOOR_LEVEL+3.75f,z); go.setScale(1.25f,1,1.5f);
            go.setRotation(0,0,-90);
            roomObjects.add(go);
            if(z>-8 || z<-12){
                // bližja stena
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(-3.5f,FLOOR_LEVEL+1.25f,z); go.setScale(1.25f,1,1.5f);
                go.setRotation(0,0,90);
                roomObjects.add(go);
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(-3.5f,FLOOR_LEVEL+3.75f,z); go.setScale(1.25f,1,1.5f);
                go.setRotation(0,0,90);
                roomObjects.add(go);
            }else{
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(-3.5f, FLOOR_LEVEL + 0.85f, z);
                go.setScale(0.85f, 1f, 1.5f);
                go.setRotation(0, 0, 90);
                roomObjects.add(go);
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(-3.5f, FLOOR_LEVEL + 4.6f, z);
                go.setScale(0.4f, 0.1f, 1.5f);
                go.setRotation(0, 0, 90);
                roomObjects.add(go);
            }
        }
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-3.5f,FLOOR_LEVEL+1.25f,-20.75f); go.setScale(1.25f,1,1.25f);
        go.setRotation(0,0,90);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-3.5f,FLOOR_LEVEL+3.75f,-20.75f); go.setScale(1.25f,1,1.25f);
        go.setRotation(0,0,90);
        roomObjects.add(go);

        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-21.5f,FLOOR_LEVEL+1.25f,-20.75f); go.setScale(1.25f,1,1.25f);
        go.setRotation(0,0,-90);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-21.5f,FLOOR_LEVEL+3.75f,-20.75f); go.setScale(1.25f,1,1.25f);
        go.setRotation(0,0,-90);
        roomObjects.add(go);

        // ob deformiranem robu
        for (float x = -3.5f-1.5f; x >= -20; x-=3) {
            go = new GameObject(tileQuartzMesh, shaderProgram);
            go.setPosition(x,FLOOR_LEVEL,-20.75f); go.setScale(1.5f,1,1.25f);
            roomObjects.add(go);
            go = new GameObject(tileQuartzMesh, shaderProgram);
            go.setPosition(x,FLOOR_LEVEL+5,-20.75f); go.setScale(1.5f,1,1.25f);
            roomObjects.add(go);
            if(x == -11) {
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(x+0.25f, FLOOR_LEVEL + 1.25f, -22f);
                go.setScale(1.25f, 1, 1.25f);
                go.setRotation(90, 0, 90);
                roomObjects.add(go);
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(x+0.25f, FLOOR_LEVEL + 3.75f, -22f);
                go.setScale(1.25f, 1, 1.25f);
                go.setRotation(90, 0, 90);
                roomObjects.add(go);
            }else if(x==-20){
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(x+0.5f, FLOOR_LEVEL + 1.25f, -22f);
                go.setScale(1.25f, 1, 2);
                go.setRotation(90, 0, 90);
                roomObjects.add(go);
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(x+0.5f, FLOOR_LEVEL + 3.75f, -22f);
                go.setScale(1.25f, 1, 2f);
                go.setRotation(90, 0, 90);
                roomObjects.add(go);
            }else if (x!=-14 && x!=-17){
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(x, FLOOR_LEVEL + 1.25f, -22f);
                go.setScale(1.25f, 1, 1.5f);
                go.setRotation(90, 0, 90);
                roomObjects.add(go);
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(x, FLOOR_LEVEL + 3.75f, -22f);
                go.setScale(1.25f, 1, 1.5f);
                go.setRotation(90, 0, 90);
                roomObjects.add(go);
            }
        }

        go = new GameObject(halfCircleMesh, shaderProgram);
        go.setPosition(new Vector3f(-15, FLOOR_LEVEL+4.96f, -10));
        go.setScale(1,0.5f,1);
        go.setColor(new Vector4f(1,1,1,1));
        roomObjects.add(go);

        // stran ob dvigalu
        for (float x = -3.5f-1.5f; x >= -20; x-=3) {
            if(x == -8){
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(x-0.5f, FLOOR_LEVEL + 1.25f, 1.5f);
                go.setScale(1.25f, 2, 2f);
                go.setRotation(-90, 0, 90);
                roomObjects.add(go);
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(x-0.5f, FLOOR_LEVEL + 3.75f, 1.5f);
                go.setScale(1.25f, 2, 2f);
                go.setRotation(-90, 0, 90);
                roomObjects.add(go);
            }else if(x > -9 || x < -14) {
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(x, FLOOR_LEVEL + 1.25f, 1.5f);
                go.setScale(1.25f, 2, 1.5f);
                go.setRotation(-90, 0, 90);
                roomObjects.add(go);
                go = new GameObject(tileIronMesh, shaderProgram);
                go.setPosition(x, FLOOR_LEVEL + 3.75f, 1.5f);
                go.setScale(1.25f, 2, 1.5f);
                go.setRotation(-90, 0, 90);
                roomObjects.add(go);
            }
        }
        // majhen del stene nad dvigalom
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-11.75f, FLOOR_LEVEL + 4.6f, 1.5f);
        go.setScale(0.4f, 1, 1.25f);
        go.setRotation(-90, 0, 90);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-14.25f, FLOOR_LEVEL + 4.6f, 1.5f);
        go.setScale(0.4f, 1, 1.25f);
        go.setRotation(-90, 0, 90);
        roomObjects.add(go);
        // znak za izhod nad dvigalom
        go = new GameObject(signExitDownMesh, shaderProgram);
        go.setPosition(-13f, FLOOR_LEVEL + 4.6f, 1.5f);
        go.setScale(1.5f, 1.5f, 1);
        roomObjects.add(go);
    }
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// prehod
class R4 extends Room{
    public R4(){
        BORDER_MIN_X = -17.5f;
        BORDER_MAX_X = -12.5f;
        BORDER_MIN_Y = 0;
        BORDER_MAX_Y = 5;
        BORDER_MIN_Z = -27;
        BORDER_MAX_Z = -17;

    }

    @Override
    void createGameObjects() {

    }
}
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
class R5 extends Room{
    boolean firstTime = true;
    int openingDoor = -1;
    List<Zombie> zombies= new ArrayList<>();
    GameObject elevatorLeftDoor;
    GameObject elevatorRightDoor;

    public R5(){
        fog = 0.1f;
        BORDER_MIN_X = -15.5f;
        BORDER_MAX_X = -10.5f;
        BORDER_MIN_Y = 0;
        BORDER_MAX_Y = 5;
        BORDER_MIN_Z = 0f;
        BORDER_MAX_Z = 7.5f;
        setZombies();
    }



    @Override
    public void update() {
        if(firstTime && Math.abs(Main.player.position.x+13f)<4 && Main.player.position.z > -2 ){
            openingDoor = 0;
            firstTime = false;
        }
        // ko pride igralec prvič blizu vrat se začnejo odpirati in ko so odprta grejo zombiji ven
        if(openingDoor>=0){
            if(openingDoor<60) {
                if(openingDoor == 30){
                    for(Zombie zombie1 : zombies)
                        zombie1.wait = false;
                }
                elevatorLeftDoor.position.x += 0.04f;
                elevatorRightDoor.position.x -= 0.04f;
                openingDoor++;
            }else{
                openingDoor = -1;
            }

        }
        if(World.zombies.size() == 0 && Main.player.position.x<-10f && Main.player.position.z>3){
            System.out.println("YOU WIN");
            Main.player.won = true;
            glfwSetWindowShouldClose(window, true);
        }

    }

    @Override
    public void setZombies(){
        Zombie zombie = new Zombie(new Vector3f(-11, 1, 3));
        zombies.add(zombie);
        zombie = new Zombie(new Vector3f(-11, 1, 6));
        zombies.add(zombie);
        zombie = new Zombie(new Vector3f(-14, 1, 3));
        zombies.add(zombie);
        zombie = new Zombie(new Vector3f(-14, 1, 6));
        zombies.add(zombie);
        for(Zombie zombie1 : zombies){
            zombie1.wait = true;
            zombie1.setFogDensity(fog);
            World.zombies.add(zombie1);
        }

    }


    @Override
    void createGameObjects() {
        GameObject go;
        // tla in strop
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-11.75f, FLOOR_LEVEL, 3f);
        go.setScale(1.25f, 1, 1.5f);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-14.25f, FLOOR_LEVEL, 3f);
        go.setScale(1.25f, 1, 1.5f);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-11.75f, FLOOR_LEVEL+4.2f, 3f);
        go.setScale(1.25f, 1, 1.5f);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-14.25f, FLOOR_LEVEL+4.2f, 3f);
        go.setScale(1.25f, 1, 1.5f);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-11.75f, FLOOR_LEVEL, 6f);
        go.setScale(1.25f, 1, 1.5f);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-14.25f, FLOOR_LEVEL, 6f);
        go.setScale(1.25f, 1, 1.5f);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-11.75f, FLOOR_LEVEL+4.2f, 6f);
        go.setScale(1.25f, 1, 1.5f);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-14.25f, FLOOR_LEVEL+4.2f, 6f);
        go.setScale(1.25f, 1, 1.5f);
        roomObjects.add(go);

        // stene zadnje
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-11.75f, FLOOR_LEVEL+1.25f, 7.5f);
        go.setScale(1.25f, -1, 1.25f);
        go.setRotation(90,0,90);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-11.75f, FLOOR_LEVEL+3.75f, 7.5f);
        go.setScale(1.25f, -1, 1.25f);
        go.setRotation(90,0,90);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-14.25f, FLOOR_LEVEL+1.25f, 7.5f);
        go.setScale(1.25f, -1, 1.25f);
        go.setRotation(90,0,90);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-14.25f, FLOOR_LEVEL+3.75f, 7.5f);
        go.setScale(1.25f, -1, 1.25f);
        go.setRotation(90,0,90);
        roomObjects.add(go);

        // stene desno
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-15.5f, FLOOR_LEVEL+1.25f, 3f);
        go.setScale(1.25f, -1, 1.5f);
        go.setRotation(0,0,90);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-15.5f, FLOOR_LEVEL+3.75f, 3f);
        go.setScale(1.25f, -1, 1.5f);
        go.setRotation(0,0,90);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-15.5f, FLOOR_LEVEL+1.25f, 6f);
        go.setScale(1.25f, -1, 1.5f);
        go.setRotation(0,0,90);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-15.5f, FLOOR_LEVEL+3.75f, 6f);
        go.setScale(1.25f, -1, 1.5f);
        go.setRotation(0,0, 90);
        roomObjects.add(go);
        // stene levo
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-10.5f, FLOOR_LEVEL+1.25f, 3f);
        go.setScale(1.25f, -1, 1.5f);
        go.setRotation(0,0,-90);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-10.5f, FLOOR_LEVEL+3.75f, 3f);
        go.setScale(1.25f, -1, 1.5f);
        go.setRotation(0,0,-90);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-10.5f, FLOOR_LEVEL+1.25f, 6f);
        go.setScale(1.25f, -1, 1.5f);
        go.setRotation(0,0,-90);
        roomObjects.add(go);
        go = new GameObject(tileIronMesh, shaderProgram);
        go.setPosition(-10.5f, FLOOR_LEVEL+3.75f, 6f);
        go.setScale(1.25f, -1, 1.5f);
        go.setRotation(0,0,-90);
        roomObjects.add(go);

        // vrata dvigala
        elevatorLeftDoor = new GameObject(doorMesh, shaderProgram);
        elevatorLeftDoor.setPosition(-13, FLOOR_LEVEL+0.2f, 2f);
        elevatorLeftDoor.setScale(-1.25f, 1, 1.4f);
        roomObjects.add(elevatorLeftDoor);
        elevatorRightDoor = new GameObject(doorMesh, shaderProgram);
        elevatorRightDoor.setPosition(-13f, FLOOR_LEVEL+0.2f, 2f);
        elevatorRightDoor.setScale(1.25f, 1, 1.4f);
        roomObjects.add(elevatorRightDoor);

    }

}