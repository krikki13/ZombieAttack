package zombieattack;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Mesh {
    int vaoId;
    int posVboId;
    int iboId;
    int texVboId;
    int normalVboId;

    int verticeCount;

    static final int verticeSize = 3;

    Material material;

    Mesh(float[] vertices, int[] indices, float[] normals, float[] texCoords) {
        verticeCount = indices.length;
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        FloatBuffer posBuffer;
        FloatBuffer normalBuffer;
        FloatBuffer texCoordsBuffer;
        IntBuffer indiceBuffer;

        posVboId = glGenBuffers();
        posBuffer = MemoryUtil.memAllocFloat(vertices.length);
        posBuffer.put(vertices).flip();
        glBindBuffer(GL_ARRAY_BUFFER, posVboId);
        glBufferData(GL_ARRAY_BUFFER, posBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(0, verticeSize, GL_FLOAT, false, 0, 0);
        MemoryUtil.memFree(posBuffer);

        normalVboId = glGenBuffers();
        normalBuffer = MemoryUtil.memAllocFloat(normals.length);
        normalBuffer.put(normals).flip();
        glBindBuffer(GL_ARRAY_BUFFER, normalVboId);
        glBufferData(GL_ARRAY_BUFFER, normalBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);
        MemoryUtil.memFree(normalBuffer);

        texVboId = glGenBuffers();
        texCoordsBuffer = MemoryUtil.memAllocFloat(texCoords.length);
        texCoordsBuffer.put(texCoords).flip();
        glBindBuffer(GL_ARRAY_BUFFER, texVboId);
        glBufferData(GL_ARRAY_BUFFER, texCoordsBuffer, GL_STATIC_DRAW);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 0, 0);
        MemoryUtil.memFree(texCoordsBuffer);

        iboId = glGenBuffers();
        indiceBuffer = MemoryUtil.memAllocInt(indices.length);
        indiceBuffer.put(indices).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, iboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL_STATIC_DRAW);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
        MemoryUtil.memFree(indiceBuffer);
    }

    void setMaterial(Material material) {
        this.material = material;
    }

    void render() {
        if (material != null && material.texture != null) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, material.texture.texId);
        }

        if (material != null && material.normalMap != null) {
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, material.normalMap.texId);
        }

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glDrawElements(GL_TRIANGLES, verticeCount, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }
}
