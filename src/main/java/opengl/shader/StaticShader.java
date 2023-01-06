package opengl.shader;

import opengl.entities.Camera;
import opengl.toolbox.Maths;
import org.joml.Matrix4f;

public class StaticShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/main/resources/shaders/vertexShader.glsl";
    private static final String FRAGMENT_FILE = "src/main/resources/shaders/fragmentShader.glsl";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrixToStack(location_transformationMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera) {
        super.loadMatrixToStack(location_viewMatrix, Maths.createViewMatrix(camera));
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrixToStack(location_projectionMatrix, matrix);
    }
}
