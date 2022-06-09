package com.mitchellg.gameengine.model.render.object;

import com.mitchellg.gameengine.Game;
import com.mitchellg.gameengine.Renderer;
import com.mitchellg.gameengine.model.GameObject;
import com.mitchellg.gameengine.model.render.ModelLoader;
import com.mitchellg.gameengine.model.render.Shader;

import java.awt.*;

public class Skybox extends GameObject {
    public Skybox(){
        super(ModelLoader.importModel("skybox").applyCubeMap().cullFaces(false));
        shader = "skybox";
        getTransform().setScale(100f);
    }

    @Override
    public void onRender(Renderer renderer) {

    }

    @Override
    public void onPreRender(Renderer renderer) {
        getTransform().setPosition(renderer.getCamera().getPosition());
    }

    @Override
    public void onPostRender(Renderer renderer) {

    }

    @Override
    public void onUpdate() {

    }

    @Override
    public Color getEffectiveAmbient(Game game){
        return Color.WHITE;
    }
}
