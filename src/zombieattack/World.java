package zombieattack;

import com.sun.org.apache.regexp.internal.RE;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class World {
    private static Texture assetsTexture;
    private static Texture militaryTexture;
    // MESHES
    static Mesh doorMesh;
    static Mesh doorLockMesh;
    static Mesh doorRedLockMesh;
    static Mesh doorGreenLockMesh;
    static Mesh signAuthPerMesh;
    static Mesh signExitLeftMesh;
    static Mesh signExitDownMesh;
    static Mesh tileQuartzMesh;
    static Mesh tileIronMesh;
    static Mesh glassPaneMesh;
    static Mesh logoMesh;
    static Mesh logotipMesh;
    static Mesh halfCircleMesh;
    static Mesh gunMesh;
    static Mesh bulletMesh;
    static Mesh holoMesh;

    static float FLOOR_LEVEL = 0;


    private Door door;
    Gun gun;
    static ShaderProgram shaderProgram;
    static List<Room> rooms = new LinkedList<>();
    static List<Zombie> zombies = new LinkedList<>();


    public World(Player player, ShaderProgram shaderProgram){
        this.shaderProgram = shaderProgram;
        if(assetsTexture == null)
            createAssetMeshes();

        Room r1 = new R1();
        rooms.add(r1);
        r1.createGameObjects();
        Room r2 = new R2();
        rooms.add(r2);
        r2.createGameObjects();
        Room r3 = new R3();
        rooms.add(r3);
        r3.createGameObjects();
        Room r4 = new R4();
        rooms.add(r4);
        Room r5 = new R5();
        rooms.add(r5);
        r5.createGameObjects();

        player.inRoom = r1;
        gun = new Gun(player);


    }
    private static void createAssetMeshes() {
        try {
            assetsTexture = new Texture("resources/textures/assets.png");
            militaryTexture = new Texture("resources/textures/military_texture.png");
            signAuthPerMesh = OBJLoader.loadMesh("resources/models/things/authorized_personnel_sign.obj");
            signExitLeftMesh = OBJLoader.loadMesh("resources/models/things/sign_exit_left.obj");
            signExitDownMesh = OBJLoader.loadMesh("resources/models/things/sign_exit_down.obj");
            tileQuartzMesh = OBJLoader.loadMesh("resources/models/things/tile_quartz.obj");
            tileIronMesh = OBJLoader.loadMesh("resources/models/things/tile_iron.obj");
            glassPaneMesh = OBJLoader.loadMesh("resources/models/things/glass_pane.obj");
            doorMesh = OBJLoader.loadMesh("resources/models/things/door.obj");
            doorLockMesh = OBJLoader.loadMesh("resources/models/things/door_lock.obj");
            doorRedLockMesh = OBJLoader.loadMesh("resources/models/things/red_lock.obj");
            doorGreenLockMesh = OBJLoader.loadMesh("resources/models/things/green_lock.obj");
            logoMesh = OBJLoader.loadMesh("resources/models/things/logo.obj");
            logotipMesh = OBJLoader.loadMesh("resources/models/things/logotip.obj");
            halfCircleMesh = OBJLoader.loadMesh("resources/models/things/half_circle.obj");
            gunMesh = OBJLoader.loadMesh("resources/models/things/gun.obj");
            bulletMesh = OBJLoader.loadMesh("resources/models/things/bullet.obj");
            holoMesh = OBJLoader.loadMesh("resources/models/things/holographic_sight.obj");

            Material material = new Material(assetsTexture);

            material.createUniforms(shaderProgram);
            signAuthPerMesh.setMaterial(material);
            tileQuartzMesh.setMaterial(material);
            doorMesh.setMaterial(material);
            tileIronMesh.setMaterial(material);
            doorLockMesh.setMaterial(material);
            logoMesh.setMaterial(material);
            logotipMesh.setMaterial(material);


            Material reflective = new Material(assetsTexture);
            reflective.reflectance = 100;
            reflective.diffuse = new Vector3f(0.05f, 0.05f, 0.05f);
            reflective.specular = new Vector3f(0.95f, 0.95f, 0.95f);
            reflective.setUniforms(shaderProgram);
            glassPaneMesh.setMaterial(reflective);

            Material glowing = new Material(assetsTexture);
            glowing.ambient = new Vector3f(0.5f,0.5f,0.5f);
            glowing.diffuse = new Vector3f(0.7f,0.7f,0.7f);
            glowing.setUniforms(shaderProgram);
            doorRedLockMesh.setMaterial(glowing);
            doorGreenLockMesh.setMaterial(glowing);

            Material holo = new Material(assetsTexture);
            holo.ambient = new Vector3f(0.7f,0.7f,0.7f);
            holo.diffuse = new Vector3f(0.05f,0.05f,0.05f);
            holo.specular = new Vector3f(0.2f, 0.2f, 0.2f);
            holo.reflectance = 1;
            holo.setUniforms(shaderProgram);
            holoMesh.setMaterial(holo);

            Material gunMaterial = new Material(militaryTexture);
            gunMaterial.ambient = new Vector3f(0.3f,0.3f,0.3f);
            gunMaterial.diffuse = new Vector3f(0.03f,0.03f,0.03f);
            gunMaterial.specular = new Vector3f(0.03f,0.03f,0.03f);
            gunMaterial.reflectance = 0.2f;
            gunMaterial.setUniforms(shaderProgram);
            gunMesh.setMaterial(gunMaterial);

            signExitLeftMesh.setMaterial(glowing);
            signExitDownMesh.setMaterial(glowing);

            Material noTextureMaterial = new Material(null);
            bulletMesh.setMaterial(noTextureMaterial);
            halfCircleMesh.setMaterial(noTextureMaterial);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    // posodobi se lokacija vseh nestatičnih objektov
    public void update(){
        gun.update();
        for(Room room : rooms) {
            room.update();
        }
        for(Zombie zombie : zombies)
            zombie.update();
    }
    public void render(){
        gun.gunObject.render();
        if(gun.bullet != null)
            gun.bullet.bulletObject.render();
        for(Room room : rooms) {
            room.renderObjects();
        }

        // Uporabljen iterator, ker je prihajalo do napak zaradi brisanja med iteriranjem v zanki
        Iterator<Zombie> zombieIterator = zombies.iterator();
        Zombie zombie;
        try { // try za vsak slučaj
            while (zombieIterator.hasNext()) {
                zombie = zombieIterator.next();
                if (zombie.deadTimer >= 100) {
                    zombieIterator.remove();
                } else {
                    if (zombie.deadTimer > 0) zombie.deadTimer++;
                    zombie.render();
                }
            }
        }catch(ConcurrentModificationException exception){}

        // ker si vsaka soba nastavi neko svojo vrednost
        Fog.density = Fog.DEFAULT_DENSITY;
    }
    public void renderTransparentObjects(){
        for(Room room : rooms) {
            room.renderTransparentObjects();
        }
        gun.holographicSight.render();
    }

    /**
     * Izračuna pozicijo objekta (child), ki se premika skupaj z nekim drugim objektom (parent).
     *
     * @param parentPos pozicija očeta
     * @param parentRotationY rotacija očeta okoli Y osi v stopinjah
     * @param childOffset vektor razdalje med očetom in otrokom (lokalno)
     * @return vrne pozicijo otroka (globalno)
     */
    static Vector3f calculateNewPosition(Vector3f parentPos, float parentRotationY, Vector3f childOffset){
        parentRotationY = (float) Math.toRadians(parentRotationY);
        return new Vector3f((float)(Math.cos(parentRotationY)*childOffset.x + Math.sin(parentRotationY)*childOffset.z), childOffset.y, (float)(-Math.sin(parentRotationY)*childOffset.x + Math.cos(parentRotationY)*childOffset.z)).add(parentPos);
    }
}

class Door extends Positionable{
    static final int SIGN_NONE = 0;
    static final int SIGN_AUTHORIZED_PERSONNEL = 1;
    static final boolean AXIS_SIDE_LEFT = false;
    static final boolean AXIS_SIDE_RIGHT = true;

    private boolean axisSide;
    private boolean locked;
    private int sign;
    GameObject[] doorParts;

    Door(Room room, boolean axisSide, boolean locked){
        this(room, axisSide, locked, 0);
    }
    Door(Room room, boolean axisSide, boolean locked, int sign){
        this.axisSide = axisSide;
        this.locked = locked;
        if(sign<0 || sign>1){
            this.sign = 0;
        }else{
            this.sign = sign;
        }
        if(sign == 0)
            doorParts = new GameObject[3];
        else
            doorParts = new GameObject[4];

        doorParts[0] = new GameObject(World.doorMesh, World.shaderProgram); // vrata
        room.roomObjects.add(doorParts[0]);
        doorParts[1] = new GameObject(World.doorLockMesh, World.shaderProgram); // ključavnica
        room.roomObjects.add(doorParts[1]);
        if(locked)
            doorParts[2] = new GameObject(World.doorRedLockMesh, World.shaderProgram); // lučka
        else
            doorParts[2] = new GameObject(World.doorGreenLockMesh, World.shaderProgram); // lučka
        room.roomObjects.add(doorParts[2]);
        if(sign != 0) {
            switch(sign){
                case 1: doorParts[3] = new GameObject(World.signAuthPerMesh, World.shaderProgram);
                    doorParts[3].setPosition(World.calculateNewPosition(position, rotation.y, new Vector3f( this.axisSide ? -1f : 1f, 2.78f, 0)));
            }
            room.roomObjects.add(doorParts[3]);
        }
        if(!axisSide){
            for (int i = 0; i < 3; i++)
                doorParts[i].setScale(-1,1,1);

        }
    }

    @Override
    void setPosition(Vector3f position) {
        super.setPosition(position);
        for (int i = 0; i < 3; i++) {
            doorParts[i].setPosition(position);
        }
        if(doorParts.length == 4){
            doorParts[3].setPosition(World.calculateNewPosition(position, rotation.y, new Vector3f( this.axisSide ? -1f : 1f, 2.78f, 0)));
        }
    }

    @Override
    void setPosition(float x, float y, float z) {
        this.setPosition(new Vector3f(x,y,z));
    }
}


