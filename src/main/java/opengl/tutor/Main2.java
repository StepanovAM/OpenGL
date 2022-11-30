package opengl.tutor;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main2 {
    // The window handle
    private long window;

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
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(1000, 800, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop

                if (key == GLFW_KEY_LEFT)
                    zAlpha = zAlpha+2;
                if (key == GLFW_KEY_RIGHT)
                    zAlpha = zAlpha-2;
                if (key == GLFW_KEY_UP)
                    xAlpha = xAlpha+2 > 180 ? 180 : xAlpha+2;
                if (key == GLFW_KEY_DOWN)
                    xAlpha = xAlpha-2 < 0 ? 0 : xAlpha-2;
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
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
        // Set the clear color
        glClearColor(0.5f, 0.5f, 0.5f, 0.5f);

        glShadeModel(GL_FLAT);

        float vertices[] = {
                -1,-1,1,
                -1,1,1,
                1,1,1,
                1,-1,1,

                -1,-1,-1,
                -1,1,-1,
                1,1,-1,
                1,-1,-1
        };

        int indices[] = {
                0,1,2,
                0,2,3,
                3,2,6,
                3,6,7,
                4,5,1,
                4,1,0,
                7,6,5,
                7,5,4,
                1,5,6,
                1,6,2,
                4,0,3,
                4,3,7
        };

        float colors [] = {
                0,0,0,
                1,1,0,
                1,1,1,
                0,1,1,

                0,1,0,
                0,0,1,
                1,0,1,
                1,0,1
        };

        FloatBuffer buff = BufferUtils.createFloatBuffer(vertices.length);
        buff.put(vertices);
        buff.flip();

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, buff, GL_STATIC_DRAW);

        IntBuffer buff1 = BufferUtils.createIntBuffer(indices.length);
        buff1.put(indices);
        buff1.flip();

        int ibo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buff1, GL_STATIC_DRAW);

        FloatBuffer colorBuff = BufferUtils.createFloatBuffer(colors.length);
        colorBuff.put(colors);
        colorBuff.flip();

//        int colorB = glGenBuffers();
//        glBindBuffer(GL_COLOR_ARRAY, colorB);
//        glBufferData(GL_COLOR_ARRAY, colorBuff, GL_STATIC_DRAW);


        glLoadIdentity();
        glOrtho(-5,5, -5,5, -5,5);


        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glVertexPointer(3, GL_FLOAT, 0, 0L);


            glEnableClientState(GL_VERTEX_ARRAY);
            glEnableClientState(GL_COLOR_ARRAY);

            glPushMatrix();
            moveCamera();
            glColorPointer(3, GL_FLOAT, 0, colorBuff);
            glDrawElements(GL_TRIANGLES, 36, GL_UNSIGNED_INT, 0L);
            glPopMatrix();

//            glRotatef(0.5f, 1, 1, 1);

            glDisableClientState(GL_COLOR_ARRAY);
            glDisableClientState(GL_VERTEX_ARRAY);



            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Main2().run();
    }

    float xAlpha = 0;
    float zAlpha = -3f;

    private void moveCamera() {
        glRotatef(-xAlpha,1,0,0);
        glRotatef(-zAlpha,0,0,1);
    }
}