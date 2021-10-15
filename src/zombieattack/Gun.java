package zombieattack;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;

class Gun{
    private Player player;
    GameObject gunObject;
    float recoil = 100;
    Bullet bullet;
    GameObject holographicSight;
    SoundSource shotSound;

    Gun(Player player){
        this.player = player;

        gunObject = new GameObject(World.gunMesh, World.shaderProgram);
        holographicSight = new GameObject(World.holoMesh, World.shaderProgram);
        Quaternionf rotationQuat = new Quaternionf();
        rotationQuat.identity();
        rotationQuat.rotateZ((float) Math.toRadians(-player.rotation.z)).rotateY((float) Math.toRadians(-player.rotation.y)).rotateX((float) Math.toRadians(-player.rotation.x));
        gunObject.setPosition(rotationQuat.transform(new Vector3f(0.4f, -0.5f, -0.3f)).add(player.position));
        gunObject.setRotation(-player.rotation.x, -player.rotation.y, -player.rotation.z);
        holographicSight.setPosition(rotationQuat.transform(new Vector3f(0.4f, -0.5f, -0.3f)).add(player.position));
        holographicSight.setRotation(-player.rotation.x, -player.rotation.y, -player.rotation.z);

        try {
            shotSound = new SoundSource("resources/sounds/shot.wav");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update() {
        // merjenje
        if(Controller.isMouseButtonPressed(GLFW_MOUSE_BUTTON_2)){
            Quaternionf rotationQuat = new Quaternionf();
            rotationQuat.identity();
            rotationQuat.rotateZ((float) Math.toRadians(-player.rotation.z)).rotateY((float) Math.toRadians(-player.rotation.y)).rotateX((float) Math.toRadians(-player.rotation.x));
            gunObject.setPosition(rotationQuat.transform(new Vector3f(0.0f, -0.34f, -1.25f)).add(player.position));
            holographicSight.setPosition(rotationQuat.transform(new Vector3f(0.0f, -0.34f, -1.25f)).add(player.position));
        }else{
            Quaternionf rotationQuat = new Quaternionf();
            rotationQuat.identity();
            rotationQuat.rotateZ((float) Math.toRadians(-player.rotation.z)).rotateY((float) Math.toRadians(-player.rotation.y)).rotateX((float) Math.toRadians(-player.rotation.x));
            gunObject.setPosition(rotationQuat.transform(new Vector3f(0.4f, -0.5f, -0.3f)).add(player.position));
            holographicSight.setPosition(rotationQuat.transform(new Vector3f(0.4f, -0.5f, -0.3f)).add(player.position));
        }

        // recoil
        if(recoil > 1.5f) {
            gunObject.setRotation(-player.rotation.x, -player.rotation.y, -player.rotation.z);
            holographicSight.setRotation(-player.rotation.x, -player.rotation.y, -player.rotation.z);
            recoil += 0.5f;
        }else {
            gunObject.setRotation(-player.rotation.x +1- (float) Math.pow(recoil, 2), -player.rotation.y, -player.rotation.z);
            holographicSight.setRotation(-player.rotation.x +1- (float) Math.pow(recoil, 2), -player.rotation.y, -player.rotation.z);
            recoil += 0.5f;
        }
        // streljanje
        if(bullet != null)
            bullet.update();
        else if(recoil > 10f && Controller.isMouseButtonPressed(GLFW_MOUSE_BUTTON_1)){
            bullet = new Bullet(this);
            recoil = -1f;
            shotSound.setPosition(getPosition());
            shotSound.play();
        }


    }
    public Vector3f getPosition(){
        return gunObject.position;
    }
    public Vector3f getRotation(){
        return gunObject.rotation;
    }
}
class Bullet{
    GameObject bulletObject;
    Vector3f direction;
    Gun gun;
    // za koliko se premakne metek med 2 frame-oma
    final static float BULLET_SPEED = 3f;
    // koliko je lahko največja razdalja med metkom in zombijom da še lahko pride do
    // zadetka (da se gre računati bolj natančno)
    final static float maxDistanceHit = (float) Math.sqrt(BULLET_SPEED*BULLET_SPEED + Math.sqrt(0.16+0.04));

    Bullet(Gun gun){
        this.gun = gun;
        bulletObject = new GameObject(World.bulletMesh, World.shaderProgram);
        bulletObject.setColor(new Vector4f(0.2f,0.2f,0.2f,1));
        Vector3f bulletPosition = gun.gunObject.getInvertedRotationQuat().transform(new Vector3f(0,0.03f,-1.6f));
        direction = new Vector3f(bulletPosition).normalize().mul(BULLET_SPEED);
        bulletObject.setPosition(bulletPosition.add(gun.getPosition()));
        bulletObject.setRotation(gun.getRotation());
    }
    public void update() {
        boolean itIsInOneRoom = false;
        for(Room room : World.rooms) {
            if (room.isInRoom(bulletObject.position)) {
                itIsInOneRoom = true;
                break;
            }
        }
        if(!itIsInOneRoom){
            System.out.println("OUT OF ROOM");
            bulletObject = null;
            gun.bullet = null;
            return;
        }
        for (Zombie zombie : World.zombies) {
            // metek je že nad zombijom oziroma pod njegovim trupom
            if ((bulletObject.position.y > zombie.getCurrentHeight() && direction.y > 0) || (bulletObject.position.y < zombie.getCurrentHeight() - 1.8f && direction.y < 0)) continue;
            if (horizontalDistance(bulletObject.position, zombie.getPosition()) > maxDistanceHit) continue;

            // preveri se če je bil zombi zadet v sprednji del telesa (2 atribut pove koliko naprej od zombija (njegove pozicije) je telo, drugi pa koliko glava)
            if(checkForFrontHit(zombie, new Vector3f(zombie.getDirection()).mul(0.2f), new Vector3f(zombie.getDirection()).mul(0.4f))) return;
            // če je zombi rahlo obrnjen na stran je lahko zadet tudi v bok (predvideva se da zombi ne bo nikoli obrnjen stran in se ga v hrbet ne da zadeti)
            Vector3f zombieOrientation = new Vector3f(direction).sub(zombie.getDirection());
            if(zombieOrientation.length() > 0.05f){
                Vector3f crossZombie = new Vector3f(0,1,0).cross(zombie.getDirection());
                if(zombieOrientation.dot(crossZombie) > 0) { // na zombija se strela z leve zato je treba gledati še njegovo levo stran
                    if (checkForSideHit(zombie, new Vector3f(crossZombie).mul(-0.4f), new Vector3f(crossZombie).mul(-0.4f), crossZombie.negate())) return;
                }else { // na zombija se strela z desne zato je treba gledati še njegovo desno stran
                    if (checkForSideHit(zombie, new Vector3f(crossZombie).mul(0.4f), new Vector3f(crossZombie).mul(0.4f), crossZombie)) return;
                }
            }
        }

        bulletObject.setPosition(bulletObject.position.add(direction));
        bulletObject.render();
    }

    static float distance(Vector3f pos1, Vector3f pos2){
        return (float) Math.sqrt(Math.pow(pos1.x-pos2.x,2) + Math.pow(pos1.y-pos2.y,2) + Math.pow(pos1.z-pos2.z,2));
    }

    static float horizontalDistance(Vector3f pos1, Vector3f pos2){
        return (float) Math.sqrt(Math.pow(pos1.x-pos2.x,2) + Math.pow(pos1.z-pos2.z,2));
    }

    static Vector3f calculatePlaneIntersection(Vector3f linePoint, Vector3f lineVector, Vector3f planePoint, Vector3f planeNormal){
        float t = (planePoint.dot(planeNormal) + planeNormal.dot(-linePoint.x,-linePoint.y,-linePoint.z)) /  planeNormal.dot(lineVector);
        return new Vector3f(linePoint.x + lineVector.x*t, linePoint.y + lineVector.y*t, linePoint.z + lineVector.z*t);
    }

    boolean checkForFrontHit(Zombie zombie, Vector3f planeOffsetVectorBody, Vector3f planeOffsetVectorHead){
        // PREVERJANJE KOLIZIJE S TELESOM ZOMBIJA
        Vector3f planePoint = new Vector3f(zombie.getPosition()).add(planeOffsetVectorBody);
        Vector3f intersection = calculatePlaneIntersection(bulletObject.position, direction, planePoint, zombie.getDirection());
        if (intersection.y > 1.4f && intersection.y < 2.4f && horizontalDistance(intersection, planePoint) < 0.4f) {
            System.out.println("A HIT!");
            //bulletObject.setPosition(intersection);
            //World.rooms.get(0).roomObjects.add(bulletObject);
            gun.bullet = null;
            zombie.hit(3);
            return true;
        }
        // PREVERJANJE KOLIZIJE Z GLAVO ZOMBIJA
        planePoint = new Vector3f(zombie.getPosition()).add(planeOffsetVectorHead);
        intersection = calculatePlaneIntersection(bulletObject.position, direction, planePoint, zombie.getDirection());
        if (intersection.y > 2.4f && intersection.y < 3.2f && horizontalDistance(intersection, planePoint) < 0.4f) {
            System.out.println("A HEADSHOT!");
            //bulletObject.setPosition(intersection);
            //World.rooms.get(0).roomObjects.add(bulletObject);
            gun.bullet = null;
            zombie.hit(5);
            return true;
        }
        return false;
    }

    boolean checkForSideHit(Zombie zombie, Vector3f planeOffsetVectorBody, Vector3f planeOffsetVectorHead, Vector3f planeDirection){
        // PREVERJANJE KOLIZIJE S TELESOM ZOMBIJA OD STRANI
        Vector3f planePoint = new Vector3f(zombie.getPosition()).add(planeOffsetVectorBody);
        Vector3f intersection = calculatePlaneIntersection(bulletObject.position, direction, planePoint, planeDirection);
        if (intersection.y > 1.4f && intersection.y < 2.4f && horizontalDistance(intersection, planePoint) < 0.2f) {
            System.out.println("A SIDE HIT!");
            //bulletObject.setPosition(intersection);
            //World.rooms.get(0).roomObjects.add(bulletObject);
            gun.bullet = null;
            zombie.hit(3);
            return true;
        }
        // PREVERJANJE KOLIZIJE Z GLAVO ZOMBIJA OD STRANI
        planePoint = new Vector3f(zombie.getPosition()).add(planeOffsetVectorHead);
        intersection = calculatePlaneIntersection(bulletObject.position, direction, planePoint, planeDirection);
        if (intersection.y > 2.4f && intersection.y < 3.2f && horizontalDistance(intersection, planePoint) < 0.4f) {
            System.out.println("A SIDE HEADSHOT!");
            //bulletObject.setPosition(intersection);
            //World.rooms.get(0).roomObjects.add(bulletObject);
            gun.bullet = null;
            zombie.hit(5);
            return true;
        }
        return false;
    }
}