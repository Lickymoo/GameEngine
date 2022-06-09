package com.mitchellg.gameengine.model;

import com.mitchellg.gameengine.Game;
import com.mitchellg.gameengine.model.location.Transform;
import com.mitchellg.gameengine.model.render.object.Model;
import com.mitchellg.gameengine.model.render.object.ModelCollection;
import com.mitchellg.gameengine.model.target.RenderableTarget;
import com.mitchellg.gameengine.model.target.UpdatableTarget;
import lombok.Getter;

import java.awt.*;

@Getter
public abstract class GameObject implements RenderableTarget, UpdatableTarget {
    protected ModelCollection model;
    protected Transform transform;
    protected String shader = "complex";
    protected boolean created = false;

    public GameObject(){
        this.model = new ModelCollection(new Model());
        this.transform = new Transform();
    }

    public GameObject(ModelCollection model){
        this.model = model;
        this.transform = new Transform();
    }

    public GameObject(float[] vertices, int[] indices){
        Model m = new Model();
        m.setIndices(indices);
        m.setVertices(vertices);
        this.model = new ModelCollection(m);

        this.transform = new Transform();
    }

    public void create(){
        if(created) return;
        created = true;

        for(Model m : model.getModels()){
            m.create();
        }
    }

    //make it so sky box can override this
    public Color getEffectiveAmbient(Game game){
        return game.getCurrentScene().ambientLight;
    }
}
