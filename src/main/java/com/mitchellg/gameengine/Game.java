package com.mitchellg.gameengine;

import com.mitchellg.gameengine.model.GameRunnable;
import com.mitchellg.gameengine.model.GameScene;
import com.mitchellg.gameengine.model.game.model.ActionHandler;
import com.mitchellg.gameengine.model.render.object.lighting.PointLight;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;

@Getter
@Setter
public abstract class Game {
    protected Renderer renderer;
    protected GameScene currentScene;
    protected GamePreferences gamePreferences;
    protected GameRunnable gameRunnable;

    @Setter(AccessLevel.NONE)
    protected ArrayList<ActionHandler> actionHandlers = new ArrayList<>();

    public Game(){
        gamePreferences = new GamePreferences();
        onPreLoad();
        renderer = new Renderer(this, gamePreferences);
        gameRunnable = new GameRunnable(this);

        init();
    }

    public void init(){
        renderer.init();

        gameRunnable.run();
    }

    public void setCurrentScene(GameScene scene){
        this.currentScene = scene;
        this.currentScene.onLoad();
    }

    public void registerKeySubscriber(ActionHandler subscriber){
        this.actionHandlers.add(subscriber);
    }

    public void removeKeySubscriber(ActionHandler subscriber){
        this.actionHandlers.remove(subscriber);
    }

    public abstract void onUpdate();
    public abstract void onExit();
    public abstract void onLoad();
    public abstract void onPreLoad();




}
