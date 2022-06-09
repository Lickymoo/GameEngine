package com.mitchellg.gameengine.model.render.object;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModelCollection {
    private Model[] models;

    public ModelCollection(Model... models){
        this.models = models;
    }

    public ModelCollection mapTexture(int index, Texture texture){
        models[index].setTexture(texture);
        return this;
    }

    public ModelCollection applyCubeMap(){
        Model[] newModels = new Model[models.length];

        for(int i = 0; i < models.length; i++){
            newModels[i] = new CubeMapModel(models[i]);
        }
        models = newModels;
        return this;
    }

    public ModelCollection cullFaces(boolean value){
        for(Model model : models){
            model.setCullFaces(value);
        }
        return this;
    }
}
