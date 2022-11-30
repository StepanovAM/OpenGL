package opengl.tutor;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
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

        createFloorBuffer();
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(1.0f, 1.0f, 0.0f, 0.0f);

        glFrustum(-1,1, -1,1, 2,80);

        glfwSetKeyCallback(window, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {

                if (key == GLFW_KEY_LEFT)
                    zAlpha++;
                if (key == GLFW_KEY_RIGHT)
                    zAlpha--;
                if (key == GLFW_KEY_UP)
                    xAlpha = xAlpha++ > 180 ? 180 : xAlpha;
                if (key == GLFW_KEY_DOWN)
                    xAlpha = xAlpha-- < 0 ? 0 : xAlpha;

                float ugol = (float) (-zAlpha / 180 * Math.PI);
                float speed = 0;

                if (key == GLFW_KEY_W)
                    speed = 0.5f;
                if (key == GLFW_KEY_S)
                    speed = -0.5f;
                if (key == GLFW_KEY_A) {
                    speed = 0.5f;
                    ugol -= Math.PI*0.5;
                }
                if (key == GLFW_KEY_D) {
                    speed = 0.5f;
                    ugol += Math.PI*0.5;
                }

                if (speed != 0) {
                    moveX += Math.sin(ugol) * speed;
                    moveY += Math.cos(ugol) * speed;
                }


            }
        });
        

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glPushMatrix();
                moveCamera();
                showFloor();
            glPopMatrix();


            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    float xAlpha = 80;
    float zAlpha = 0;
    float moveX = 0;
    float moveY = 0;
    float jumpZ = -3;

    private void moveCamera() {

        glRotatef(-xAlpha,1,0,0);
        glRotatef(-zAlpha,0,0,1);
        glTranslatef(-moveX, -moveY, jumpZ);
    }


    public void showFloor() {
        glEnableClientState(GL_VERTEX_ARRAY);
        glVertexPointer(3, GL_FLOAT, 0, floorBuffer);
        for (int i = -5; i < 5; i++) {
            for (int j = -5; j < 5; j++) {
                glPushMatrix();
                if ((i+j) % 2 == 0) glColor3f(0, 0.5f, 0);
                else glColor3f(1,1,1);
                glTranslatef(i*2,j*2,0);
                glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
                glPopMatrix();
            }
        }
        glDisableClientState(GL_VERTEX_ARRAY);
    }


    FloatBuffer floorBuffer;

    void createFloorBuffer() {
        floorBuffer = BufferUtils.createFloatBuffer(50);
        floorBuffer.put(1.0f).put(1.0f).put(0f);
        floorBuffer.put( 1.0f).put(-1.0f).put(0f);
        floorBuffer.put( -1.0f).put( -1.0f).put(0f);
        floorBuffer.put(-1.0f).put( 1.0f).put(0f);

        floorBuffer.flip();
    }


    float vertices1 [] = {
            0, 0, 0,
            1, 0, 0,
            1, 1, 0,
            0, 1, 0
    };

    float vertices2 [] = {
            0, 0, 0,
            -1, 0, 0,
            1, -1, 0,
    };
    int vbo1;
    int vbo2;

    FloatBuffer buffer1;
    FloatBuffer buffer2;

    public void initVbo1() {
        vbo1 = glGenBuffers();

        glBindBuffer(GL_ARRAY_BUFFER, vbo1);

        buffer1 = BufferUtils.createFloatBuffer(vertices1.length);
        buffer1.put(vertices1);
        buffer1.flip();
        glBufferData(GL_ARRAY_BUFFER, buffer1, GL_STATIC_DRAW);
    }

    public void initVbo2() {
        vbo2 = glGenBuffers();

        buffer2 = BufferUtils.createFloatBuffer(vertices2.length);
        buffer2.put(vertices2);
        buffer2.flip();

        glBindBuffer(GL_ARRAY_BUFFER, vbo2);
        glBufferData(GL_ARRAY_BUFFER, buffer2, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public void drawTriangle() {
        glBegin(GL_TRIANGLES);
        glColor3f(1.0f, 0f, 0f);
        glVertex2f(0, 0);
        glVertex2f(0.3f, 0);
        glVertex2f(0.3f, 0.5f);
        glEnd();
    }

    public void drawQuad() {
        glLineWidth(5);
        glBegin(GL_LINE_LOOP);
        glColor3f(1.0f, 0f, 0f);
        glVertex2f(0.1f, 0.1f);
        glVertex2f(-0.1f, 0.1f);
        glVertex2f(-0.1f, -0.1f);
        glVertex2f(0.1f, -0.1f);
        glEnd();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}