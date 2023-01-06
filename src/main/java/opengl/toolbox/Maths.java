package opengl.toolbox;

import opengl.entities.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Maths {

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(translation);
        matrix.rotate(rx, new Vector3f(1, 0, 0));
        matrix.rotate(ry, new Vector3f(0, 1, 0));
        matrix.rotate(rz, new Vector3f(0, 0, 1));
        matrix.scale(scale);
        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0));
        matrix.rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0));
        matrix.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1));
        Vector3f position = camera.getPosition();
        Vector3f negativePosition = new Vector3f(-position.x, -position.y, -position.z);
        matrix.translate(negativePosition);
        return matrix;
    }
}
