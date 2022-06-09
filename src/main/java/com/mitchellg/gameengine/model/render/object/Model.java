package com.mitchellg.gameengine.model.render.object;

import com.mitchellg.gameengine.Renderer;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

@Getter
@Setter
public class Model {
    protected float[] vertices;
    protected float[] texCoords;
    protected float[] normals;
    protected float[] colors;
    protected int[] indices;

    protected int vao;
    protected int vbo, tbo, ibo, nbo;

    protected Material material;

    protected Color color = Color.WHITE;
    protected boolean cullFaces = true;

    public Model(){
        material = new Material();
    }

    public Model(Model model){
        this.vertices = model.vertices;
        this.texCoords = model.texCoords;
        this.normals = model.normals;
        this.colors = model.colors;
        this.indices = model.indices;

        this.vao = model.vao;
        this.vbo = model.vbo;
        this.tbo = model.tbo;
        this.ibo = model.ibo;
        this.nbo = model.nbo;

        this.material = model.material;

        this.color = model.color;
        this.cullFaces = model.cullFaces;
    }

    public void create(){

        FloatBuffer textureBuffer = null;
        FloatBuffer vertexBuffer = null;
        FloatBuffer normalsBuffer = null;
        IntBuffer indicesBuffer = null;

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        try {
            if (vertices != null) {
                vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
                vertexBuffer.put(vertices).flip();

                vbo = glGenBuffers();
                glBindBuffer(GL_ARRAY_BUFFER, vbo);
                glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

                glEnableVertexAttribArray(0);
                glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
                glBindBuffer(GL_ARRAY_BUFFER, 0);
            }
            if (texCoords != null) {
                textureBuffer = MemoryUtil.memAllocFloat(texCoords.length);
                textureBuffer.put(texCoords).flip();

                tbo = glGenBuffers();
                glBindBuffer(GL_ARRAY_BUFFER, tbo);
                glBufferData(GL_ARRAY_BUFFER, textureBuffer, GL_STATIC_DRAW);

                glEnableVertexAttribArray(1);
                glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
                glBindBuffer(GL_ARRAY_BUFFER, 0);
            }
            if(normals != null){
                normalsBuffer = MemoryUtil.memAllocFloat(normals.length);
                normalsBuffer.put(normals).flip();

                nbo = glGenBuffers();
                glBindBuffer(GL_ARRAY_BUFFER, nbo);
                glBufferData(GL_ARRAY_BUFFER, normalsBuffer, GL_STATIC_DRAW);

                glEnableVertexAttribArray(2);
                glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);
                glBindBuffer(GL_ARRAY_BUFFER, 0);
            }
            if (indices != null) {
                indicesBuffer = MemoryUtil.memAllocInt(indices.length);
                indicesBuffer.put(indices).flip();

                ibo = glGenBuffers();
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
                glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
                glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
            }
        }finally {
            if(textureBuffer != null)
                MemoryUtil.memFree(textureBuffer);

            if(vertexBuffer != null)
                MemoryUtil.memFree(vertexBuffer);

            if(indicesBuffer != null)
                MemoryUtil.memFree(indicesBuffer);

            if(normalsBuffer != null)
                MemoryUtil.memFree(normalsBuffer);
        }
    }

    public void render(){
        //Incase model doesnt have specific buffer, dont enable vertex attrib
        if(vbo != 0)
            glEnableVertexAttribArray(0);

        if(tbo != 0)
            glEnableVertexAttribArray(1);

        if(nbo != 0)
            glEnableVertexAttribArray(2);


        if(isTextured()) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, material.getTexture().getId());
        }
        if(isNormalized()){
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, material.getNormal().getId());
        }

        glBindVertexArray(vao);

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);

        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
    }

    public void cleanup(){
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        glBindTexture(GL_TEXTURE_2D, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vbo);
        glDeleteBuffers(tbo);
        glDeleteBuffers(ibo);
        glDeleteBuffers(nbo);
        glDeleteVertexArrays(vao);

        if(isTextured())
            material.getTexture().cleanup();
        if(isNormalized())
            material.getNormal().cleanup();

        glBindVertexArray(0);
    }

    public void setTexture(Texture texture){
        if(material == null)
            this.material = new Material();

        material.setTexture(texture);
    }

    public void setNormal(Texture normal){
        if(material == null)
            this.material = new Material();

        this.material.setNormal(normal);
    }

    public boolean isTextured(){
        if(material == null) return false;
        return material.isTextured();
    }

    public boolean isNormalized(){
        if(material == null) return false;
        return material.isNormalized();
    }
}



























