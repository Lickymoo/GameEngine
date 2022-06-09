package com.mitchellg.gameengine.model;

import com.mitchellg.gameengine.Game;
import com.mitchellg.gameengine.model.render.object.Skybox;
import com.mitchellg.gameengine.model.render.object.lighting.DirectionalLight;
import com.mitchellg.gameengine.model.render.object.lighting.Fog;
import com.mitchellg.gameengine.model.target.UpdatableTarget;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

@Getter
public abstract class GameScene implements UpdatableTarget {
    protected final Game game;
    protected Skybox skybox;
    protected Fog fog;
    protected DirectionalLight directionalLight;
    protected Color ambientLight;
    protected float specularPower = 10f;

    private ArrayList<GameObject> sceneObjectList = new ArrayList<>();

    public GameScene(Game game){
        this.game = game;
    }

    public void registerSceneObject(GameObject... objects){
        sceneObjectList.addAll(Arrays.asList(objects));
    }

    public abstract void onLoad();

}
