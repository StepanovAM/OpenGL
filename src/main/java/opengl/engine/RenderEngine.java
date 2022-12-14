package opengl.engine;

import opengl.entities.Camera;
import opengl.entities.Entity;
import opengl.entities.Light;
import opengl.model.RawModel;
import opengl.model.TexturedModel;
import opengl.shader.StaticShader;
import opengl.texture.ModelTexture;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class RenderEngine {

    // The window handle
    private long window;
    private Camera camera;
    private Entity entity;


    public static void main(String[] args) {
        new RenderEngine().run();
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(1280, 720, "Hello World!", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop

            camera.move(key);
            entity.move(key);
        });

        glfwSetCursorPosCallback(window, (window, x, y) -> {
            camera.setCursor(x/1280, y/720);
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        glEnable(GL_DEPTH_TEST);


        Loader loader = new Loader();
        StaticShader staticShader = new StaticShader();
        Renderer renderer = new Renderer(staticShader);


        RawModel model = OBJLoader.loadObjModel("src/main/resources/dragon/dragon.obj", loader);
        ModelTexture texture = new ModelTexture(loader.loadTexture("src/main/resources/dragon/white.png"));
        TexturedModel texturedModel = new TexturedModel(model, texture);

        entity = new Entity(texturedModel, new Vector3f(0, 0, -25), 0, 0, 0, 1);

        Light light = new Light(new Vector3f(-10,10,-20), new Vector3f(1,1,1));

        camera = new Camera();

        entity.increasePosition(0, -5, 0);
        while (!glfwWindowShouldClose(window)) {
//            entity.increaseRotation(0, 0.01f, 0);

            renderer.prepare();
            staticShader.start();
            staticShader.loadLight(light);
            staticShader.loadViewMatrix(camera);
            renderer.render(entity, staticShader);
            staticShader.stop();


            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }

        staticShader.cleanUp();
        loader.cleanUp();
    }

}



// textured cube
//float[] vertices = {
//        -0.5f,0.5f,-0.5f,
//        -0.5f,-0.5f,-0.5f,
//        0.5f,-0.5f,-0.5f,
//        0.5f,0.5f,-0.5f,
//
//        -0.5f,0.5f,0.5f,
//        -0.5f,-0.5f,0.5f,
//        0.5f,-0.5f,0.5f,
//        0.5f,0.5f,0.5f,
//
//        0.5f,0.5f,-0.5f,
//        0.5f,-0.5f,-0.5f,
//        0.5f,-0.5f,0.5f,
//        0.5f,0.5f,0.5f,
//
//        -0.5f,0.5f,-0.5f,
//        -0.5f,-0.5f,-0.5f,
//        -0.5f,-0.5f,0.5f,
//        -0.5f,0.5f,0.5f,
//
//        -0.5f,0.5f,0.5f,
//        -0.5f,0.5f,-0.5f,
//        0.5f,0.5f,-0.5f,
//        0.5f,0.5f,0.5f,
//
//        -0.5f,-0.5f,0.5f,
//        -0.5f,-0.5f,-0.5f,
//        0.5f,-0.5f,-0.5f,
//        0.5f,-0.5f,0.5f
//
//};
//
//    float[] textureCoords = {
//            0,0,
//            0,1,
//            1,1,
//            1,0,
//            0,0,
//            0,1,
//            1,1,
//            1,0,
//            0,0,
//            0,1,
//            1,1,
//            1,0,
//            0,0,
//            0,1,
//            1,1,
//            1,0,
//            0,0,
//            0,1,
//            1,1,
//            1,0,
//            0,0,
//            0,1,
//            1,1,
//            1,0
//
//
//    };
//
//    int[] indices = {
//            0,1,3,
//            3,1,2,
//            4,5,7,
//            7,5,6,
//            8,9,11,
//            11,9,10,
//            12,13,15,
//            15,13,14,
//            16,17,19,
//            19,17,18,
//            20,21,23,
//            23,21,22
//
//    };
