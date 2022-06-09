package com.mitchellg.gameengine.model.render.object;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class CubeMapModel extends Model{
    public CubeMapModel(Model model){
        super(model);
    }

    @Override
    public void create(){

        super.create();
    }

    @Override
    public void render(){
        //Incase model doesnt have specific buffer, dont enable vertex attrib
        if(vbo != 0)
            glEnableVertexAttribArray(0);

        if(tbo != 0)
            glEnableVertexAttribArray(1);

        if(nbo != 0)
            glEnableVertexAttribArray(2);

        glBindVertexArray(vao);

        if(isTextured()) {
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, material.getTexture().getId());
        }
        if(isNormalized()){
            //redo
            // glActiveTexture(GL_TEXTURE1);
            // glBindTexture(GL_TEXTURE_2D, normal.getId());
        }

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);

        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);

    }
}
