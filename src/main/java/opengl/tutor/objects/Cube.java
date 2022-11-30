package opengl.tutor.objects;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;

public class Cube {

    float vertices[] = {
            //front edge
            0,0,0,
            1,0,0,
            1,1,0,

            1,1,0,
            0,1,0,
            0,0,0,

            //right edge
            1,0,0,
            1,0,1,
            1,1,1,

            1,1,1,
            1,1,0,
            1,0,0,

            //left edge
            0,0,0,
            0,0,1,
            0,1,1,

            0,1,1,
            0,1,0,
            0,0,0,

            //bottom edge
            1,0,0,
            1,0,1,
            0,0,0,

            1,0,1,
            0,0,1,
            0,0,0,

            //upper edge
            1,1,0,
            1,1,1,
            0,1,0,

            1,1,1,
            0,1,1,
            0,1,0,

            //bottom edge
            0,0,1,
            0,1,1,
            1,0,1,

            0,1,1,
            1,0,1,
            1,1,1
    };

    float colors [] = {
            1,0,0,
            1,0,0,
            1,0,0,
            1,0,0,
            1,0,0,
            1,0,0,

            0,1,0,
            0,1,0,
            0,1,0,
            0,1,0,
            0,1,0,
            0,1,0,

            0,0,1,
            0,0,1,
            0,0,1,
            0,0,1,
            0,0,1,
            0,0,1,

            1,1,0,
            1,1,0,
            1,1,0,
            1,1,0,
            1,1,0,
            1,1,0,

            1,0,1,
            1,0,1,
            1,0,1,
            1,0,1,
            1,0,1,
            1,0,1,

            1,1,1,
            1,1,1,
            1,1,1,
            1,1,1,
            1,1,1,
            1,1,1
    };

    public float[] getVertices() {
        return vertices;
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices;
    }

    public float[] getColors() {
        return colors;
    }

    public void setColors(float[] colors) {
        this.colors = colors;
    }

    public int vboId() {
        FloatBuffer buff = createFloatBuffer(vertices);
        int vbo = glGenBuffers();
        bufferData(vbo, buff);
        return vbo;
    }

    public int colorVboId() {
        FloatBuffer colorBuff = createFloatBuffer(colors);
        int colorB = glGenBuffers();
        bufferData(colorB, colorBuff);
        return colorB;
    }

    private FloatBuffer createFloatBuffer(float [] data) {
        FloatBuffer buff = BufferUtils.createFloatBuffer(data.length);
        buff.put(data);
        buff.flip();
        return buff;
    }

    private void bufferData(int buffId, FloatBuffer buff) {
        glBindBuffer(GL_ARRAY_BUFFER, buffId);
        glBufferData(GL_ARRAY_BUFFER, buff, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
}
