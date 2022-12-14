package opengl.entities;

import opengl.model.TexturedModel;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

public class Entity {

    private TexturedModel model;
    private Vector3f position;
    private float rotX, rotY, rotZ;
    private float scale;

    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }

    public void increasePosition(float dx, float dy, float dz) {
        position.x += dx;
        position.y += dy;
        position.z += dz;
    }

    public void increaseRotation(float dx, float dy, float dz) {
        rotX += dx;
        rotY += dy;
        rotZ += dz;
    }

    public void move(int key) {
        switch (key) {
            case GLFW_KEY_UP: rotX += 0.02f; break;
            case GLFW_KEY_DOWN: rotX -= 0.02f; break;
            case GLFW_KEY_RIGHT: rotZ += 0.02f; break;
            case GLFW_KEY_LEFT: rotZ -= 0.02f; break;
        }
    }

    public void increaseScale(float scaling) {
        scale += scaling;
    }

    public TexturedModel getModel() {
        return model;
    }

    public void setModel(TexturedModel model) {
        this.model = model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getRotX() {
        return rotX;
    }

    public void setRotX(float rotX) {
        this.rotX = rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public void setRotZ(float rotZ) {
        this.rotZ = rotZ;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
