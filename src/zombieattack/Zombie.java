package zombieattack;


import org.joml.Vector3f;

import static zombieattack.Bullet.horizontalDistance;
import static zombieattack.Main.player;
import static zombieattack.World.FLOOR_LEVEL;
import static zombieattack.World.shaderProgram;
import static zombieattack.World.zombies;

/**
 * Controls the zombie. It contains {@link GameObject} for all body parts of the zombie: head, body, 2 arms and 2 legs.
 * Zombie will walk and turn towards the camera automatically all that needs to be done is to call {@link #update()}
 * every update and this class will automatically update position for zombie game items.
 *
 */
public class Zombie {
    private GameObject zombieHead;
    private GameObject zombieBody;
    private GameObject zombieLeftArm;
    private GameObject zombieRightArm;
    private GameObject zombieLeftLeg;
    private GameObject zombieRightLeg;

    float health = 10;
    // walking animation
    private final float WALKING_SPEED=0.02f;
    private final float STEP_LENGTH=0.4f;
    private final float LEG_LENGTH=1.2f;
    private float currentStep;
    private boolean walking = false;
    private boolean rightLegGoingForward;

    private int walkingAround = 0;

    // punching animation
    private float t=0;
    private final float MAX_T = 45;
    private boolean armGoingUp = true;
    private boolean punching = false;

    private Mesh zombieHeadMesh;
    private static Mesh zombieBodyMesh;
    private static Mesh zombieArmMesh;
    private static Mesh zombieLegMesh;
    private static Texture zombieTexture;
    private static Texture deadTexture;
    private static Material deadMaterial;
    private static Material zombieMaterial;
    // višina zombija je 3.2, telo se začne pri 1.4, glava pa pri 2.4
    // če želimo da zombi stoji na tleh z y=0 mora biti zombie.position.y=1
    // te višine se potem rahlo spreminjajo ko zombi hodi
    // zombijeva glava je 0.8 x 0.8 x 0.8 (levo-desno x višina x naprej-nazaj)
    // zombijevo telo je  0.8 x 1.2 x 0.4
    // roke in noge       0.4 x 1.2 x 0.4

    static SoundSource walkSound;
    static SoundSource growlSound;

    private Vector3f position;
    private Vector3f direction;
    private float rotationY;
    int deadTimer = -1;

    // do kdaj naj zombi stoji na mestu in čaka (update se ne bo izvajal)
    boolean wait = false;

    public Zombie(Vector3f position){
        this.position = position;
        this.direction = new Vector3f(0,0,1);
        try {
            walkSound = new SoundSource("resources/sounds/zombie.wav");
            walkSound.setPosition(this.position);
            growlSound = new SoundSource("resources/sounds/zombie_growl.wav");
            growlSound.setPosition(this.position);
            growlSound.setVolume(1f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(zombieTexture == null) createZombieMeshes();
        try {
            zombieHeadMesh = OBJLoader.loadMesh("resources/models/zombie/head.obj");
            zombieHeadMesh.setMaterial(zombieMaterial);
        } catch (Exception e) {
            e.printStackTrace();
        }
        zombieHead = new GameObject(zombieHeadMesh, shaderProgram);
        zombieBody = new GameObject(zombieBodyMesh, shaderProgram);
        zombieLeftArm = new GameObject(zombieArmMesh, shaderProgram);
        zombieRightArm = new GameObject(zombieArmMesh, shaderProgram);
        zombieLeftLeg = new GameObject(zombieLegMesh, shaderProgram);
        zombieRightLeg = new GameObject(zombieLegMesh, shaderProgram);
        zombieRightArm.setRotation(-90,0,0);
        zombieLeftArm.setRotation(-90,0,0);

        updateZombiePosition();
        updateZombieRotation();

        // da ni tistega glasnega zvoka na začetku
        try {
            walkSound.setVolume(0.1f);
            walkSound.startPlayingAfter((long) (Math.random() + 1) * 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void createZombieMeshes() {
        try {
            zombieTexture = new Texture("resources/textures/zombie_minecraft.png");
            deadTexture = new Texture("resources/textures/zombie_dead.png");
            zombieBodyMesh = OBJLoader.loadMesh("resources/models/zombie/body.obj");
            zombieArmMesh = OBJLoader.loadMesh("resources/models/zombie/arm.obj");
            zombieLegMesh = OBJLoader.loadMesh("resources/models/zombie/leg.obj");
            deadMaterial = new Material(deadTexture);
            zombieMaterial = new Material(zombieTexture);
            zombieBodyMesh.setMaterial(zombieMaterial);
            zombieArmMesh.setMaterial(zombieMaterial);
            zombieLegMesh.setMaterial(zombieMaterial);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    private void updateZombiePosition(){
        if (deadTimer > 0) return;
        zombieHead.setPosition(calculateNewPosition(new Vector3f(0,FLOOR_LEVEL+1.8f,0), rotationY).add(position));
        zombieBody.setPosition(calculateNewPosition(new Vector3f(0,FLOOR_LEVEL+0.8f,0), rotationY).add(position));
        zombieLeftArm.setPosition(calculateNewPosition(new Vector3f(0.605f,FLOOR_LEVEL+1.4f,0), rotationY).add(position));
        zombieRightArm.setPosition(calculateNewPosition(new Vector3f(-0.605f,FLOOR_LEVEL+1.4f,0), rotationY).add(position));
        zombieLeftLeg.setPosition(calculateNewPosition(new Vector3f(0.22f,FLOOR_LEVEL+0.2f,0), rotationY).add(position));
        zombieRightLeg.setPosition(calculateNewPosition(new Vector3f(-0.20f,FLOOR_LEVEL+0.2f,0), rotationY).add(position));

        /*leftHand.set(calculateNewPosition(new Vector3f(0.8f,FLOOR_LEVEL+1.4f,1.2f), rotationY).add(position));
        rightHand.set(calculateNewPosition(new Vector3f(-0.8f,FLOOR_LEVEL+1.4f,1.2f), rotationY).add(position));*/
    }
    private void updateZombieRotation(){
        if (deadTimer > 0) return;
        zombieHead.setRotationY(rotationY);
        zombieBody.setRotationY(rotationY);
        zombieLeftArm.setRotationY(rotationY);
        zombieRightArm.setRotationY(rotationY);
        zombieLeftLeg.setRotationY(rotationY);
        zombieRightLeg.setRotationY(rotationY);
    }

    /**
     * Call this method every frame, so that position and rotations for zombie related game items will be updated.
     * However zombie will not render his objects, you still need to do that via {@link GameObject}.
     *
     */
    public void update() {
        if(wait) return;
        Vector3f cameraPosition;
        cameraPosition = player.getPosition();

        Vector3f zombieToCam=new Vector3f();
        cameraPosition.sub(position, zombieToCam);
        zombieToCam.y=0;
        if(walkingAround<=0) {
            //float newRotation = (float) Math.toDegrees(zombieToCam.angle(new Vector3f(0, 0, 1)));
            float newRotation = (float) Math.toDegrees(Math.acos(new Vector3f(zombieToCam).normalize().dot(new Vector3f(0,0,1))));
            if (zombieToCam.x < 0)
                newRotation *= -1;
            // da zombiji ne delajo nekih obratov brez razloga
            if(Math.abs(rotationY - newRotation) > 180){
                if (newRotation > rotationY)
                    rotationY -= 5;
                else if (newRotation < rotationY)
                    rotationY += 5;
                if(Math.abs(rotationY)>180)
                    rotationY *= -1;
            }else {
                if (newRotation > rotationY + 5)
                    rotationY += 5;
                else if (newRotation < rotationY - 5)
                    rotationY -= 5;
                else
                    rotationY = newRotation;
            }
        }else{
            walkingAround--;
        }

        // WALKING
        if(zombieToCam.length()>2 && deadTimer < 1){
            direction = calculateNewPosition(new Vector3f(0,0,1), rotationY).normalize();
            if(canWalk()) {
                walking = true;
                position = new Vector3f(position).add(direction.mul(WALKING_SPEED));
                calculateWalkingStuff();
            }else {
                System.out.println("Cant walk");
            }
        }else{
            if(walking){
                zombieRightLeg.setRotationX(0);
                zombieLeftLeg.setRotationX(0);
                walking = false;
            }
        }

        walkSound.setPosition(position);
        float volume = 1/zombieToCam.length();
        walkSound.setVolume(volume);

        updateZombiePosition();
        updateZombieRotation();

        // PUNCHING
        if(zombieToCam.length()<2 && !walking && deadTimer < 1){
            calculatePunchingStuff();
            if (!growlSound.playing) {
                walkSound.stopPlaying();
                growlSound.play();
            }
        }else {
            if (punching) {
                zombieRightArm.setRotationY(rotationY);
                zombieRightArm.setRotationX(-90);
                punching = false;
                if (!walkSound.playing) walkSound.startPlaying();
            }
        }
    }


    private void calculateWalkingStuff(){
        if(currentStep>STEP_LENGTH) {
            rightLegGoingForward = false;
        }else if(currentStep<-STEP_LENGTH){
            rightLegGoingForward = true;
        }
        if(rightLegGoingForward)
            currentStep += WALKING_SPEED;
        else
            currentStep -= WALKING_SPEED;
        zombieRightLeg.setRotationX((float) Math.toDegrees(Math.sin(currentStep / LEG_LENGTH)));
        zombieLeftLeg.setRotationX((float) -Math.toDegrees( Math.sin(currentStep / LEG_LENGTH)));
        float loweredBy = LEG_LENGTH - (float) Math.sqrt(LEG_LENGTH*LEG_LENGTH - currentStep*currentStep);
        position.y = 1+FLOOR_LEVEL-loweredBy;

    }

    private void calculatePunchingStuff(){
        punching = true;
        if(armGoingUp) {
            if (t < MAX_T) { // zombie is lifting his arm
                t += 3f;
                zombieRightArm.setRotationY(rotationY-t);
                zombieRightArm.setRotationX(-90-t);
            }else{
                armGoingUp = false;
            }
        }else{
            t -= 8f;
            if(t>0){  // zombie is hitting
                zombieRightArm.setRotationY(rotationY-t);
                zombieRightArm.setRotationX(-90+t);

            }
            zombieRightArm.setRotationY(rotationY);
            zombieRightArm.setRotationX(-90);
            armGoingUp = true;
            t = 0;
            player.hit(1);
        }

    }
    private boolean canWalk(){
        boolean cantGoLeft = true;
        boolean cantGoRight = true;
        Vector3f leftHand = calculateNewPosition(new Vector3f(0.8f,FLOOR_LEVEL+1.4f,1.2f), rotationY).add(position);
        Vector3f rightHand = calculateNewPosition(new Vector3f(-0.8f,FLOOR_LEVEL+1.4f,1.2f), rotationY).add(position);

        // če je ob steni
        boolean zombieInRoom1 = World.rooms.get(0).isInRoom(position);
        boolean zombieInRoom3 = World.rooms.get(2).isInRoom(position);
        for (int i = 0; i < World.rooms.size(); i++) {
            // ker so nekak zombiji najdl pot v kotu sobe 3
            if(i==2 && zombieInRoom1) continue;
            if(i==0 && zombieInRoom3) continue;
            if (World.rooms.get(i).isInRoom(leftHand)) {
                cantGoLeft = false;
            }
            if (World.rooms.get(i).isInRoom(rightHand)) {
                cantGoRight = false;
            }
            if(!cantGoLeft && !cantGoRight)
                break;
        }
        if(cantGoLeft && cantGoRight) // obtičal je v kotu
            return false;

        for(Zombie fellowZombie : World.zombies){
            if(fellowZombie == this) continue;
            if(horizontalDistance(position, fellowZombie.getPosition()) < 1.3f){
                Vector3f fellowZombiePointer = new Vector3f(fellowZombie.getPosition()).sub(position);
                if(fellowZombiePointer.dot(direction)>0){
                    Vector3f leftSide = new Vector3f(0,1,0).cross(direction);
                    if(fellowZombiePointer.dot(leftSide) < 0){
                        cantGoRight = true;
                    }else{
                        cantGoLeft = true;
                    }
                }
            }
            if(!cantGoLeft) {
                float leftHandDistance = horizontalDistance(leftHand, fellowZombie.getPosition());
                if (leftHandDistance > 1.7f) continue;
                if (leftHandDistance < 1.2f) {
                    cantGoLeft = true;
                    continue;
                }
            }
            if(!cantGoRight && horizontalDistance(rightHand, fellowZombie.getPosition()) < 1.2f){
                cantGoRight = true;
            }
            if(cantGoLeft && cantGoRight) // ne more nikamor
                return false;
        }
        if(cantGoLeft) {
            rotationY -= 10;
            walkingAround = 30;
        }if(cantGoRight){
            rotationY += 10;
            walkingAround = 30;
        }
        return true;
    }

    private Vector3f calculateNewPosition(Vector3f pos, float rotationY1){
        rotationY1 = (float) Math.toRadians(rotationY1);
        return new Vector3f((float)(Math.cos(rotationY1)*pos.x + Math.sin(rotationY1)*pos.z), pos.y, (float)(-Math.sin(rotationY1)*pos.x + Math.cos(rotationY1)*pos.z));
    }
    public void setPosition(Vector3f position){
        this.position = position;
    }

    /**
     * Višina zombija. Se malo zniža ko zombi hodi.
     *
     * @return višina zombija
     */
    public float getCurrentHeight(){
        return position.y + 2.2f;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getDirection() {
        return direction;
    }
    public void render(){
        zombieHead.render();
        zombieBody.render();
        zombieLeftArm.render();
        zombieRightArm.render();
        zombieLeftLeg.render();
        zombieRightLeg.render();
    }

    public void hit(float damage){
        health -= damage;
        if(health <= 0) {
            zombieHead.mesh.setMaterial(deadMaterial);
            deadTimer = 1;
        }
    }

    public void setFogDensity(float fog) {
        zombieHead.fogDensity = fog;
        zombieBody.fogDensity = fog;
        zombieLeftArm.fogDensity = fog;
        zombieRightArm.fogDensity = fog;
        zombieLeftLeg.fogDensity = fog;
        zombieRightLeg.fogDensity = fog;
    }
}