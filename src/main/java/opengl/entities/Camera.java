package opengl.entities;

import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private Vector3f position = new Vector3f(0, 0, 0);
    private float pitch;
    private float yaw;
    private float roll;

    public void setCursor(double cx, double cy) {
        System.out.println("cx: " + cx + " cy:" + cy);
//        pitch = (float) cx;
//        yaw = (float) cy;
    }


    public void move(int key) {
        switch (key) {
            case GLFW_KEY_S: position.z += 0.02f; break;
            case GLFW_KEY_W: position.z -= 0.02f; break;
            case GLFW_KEY_D: position.x += 0.02f; break;
            case GLFW_KEY_A: position.x -= 0.02f; break;
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }
}
