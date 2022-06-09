package com.mitchellg.gameengine.model;

import com.mitchellg.gameengine.Game;
import com.mitchellg.gameengine.Renderer;
import com.mitchellg.gameengine.model.target.UpdatableTarget;
import lombok.Getter;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

public class GameRunnable implements Runnable{

    public static final long ONE_NANO_SECOND = 1000000000;

    public boolean running = false;
    private final Renderer renderer;

    private final int MAX_UPDATES = 20;
    private int updates = 0;
    private long lastUpdateTime = 0;
    private long timePerFrame;

    @Getter
    private int lastFPS = 0;

    private final Game game;

    public GameRunnable(Game game){
        this.game = game;
        this.renderer = game.getRenderer();
        this.timePerFrame = ONE_NANO_SECOND / game.getGamePreferences().getFramesPerSecond();
    }

    @Override
    public void run() {
        running = true;

        game.onLoad();
        tick();

        renderer.cleanup();
        game.onExit();
        glfwFreeCallbacks(renderer.getWindow());
        glfwDestroyWindow(renderer.getWindow());
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void tick(){
        GL.createCapabilities();
        glfwMakeContextCurrent(renderer.getWindow());

        while ( !glfwWindowShouldClose(renderer.getWindow()) ) {
            updateBuffer();
        }
    }

    int cycleFrames = 0;
    int effectiveFrames = 0;
    long lastFrame = 0;
    private void updateBuffer(){
        //fps and update buffer
        long currentTime = System.nanoTime();
        int drawCalls = 0;
        cycleFrames++;


        //Ensure frames are evenly spaced between the second
        //If not all frames will fire first then no frames for remaining cycle
        boolean ignoreCycle = game.getGamePreferences().getFramesPerSecond() == -1;
        if(currentTime - lastFrame >= timePerFrame || lastFrame == 0 || ignoreCycle){
            //Ensure frame cap isnt gone over
            if(effectiveFrames < game.getGamePreferences().getFramesPerSecond() || ignoreCycle){
                drawCalls = renderer.render();
                lastFrame = System.nanoTime();

            }
            effectiveFrames++;
        }

        if(updates < MAX_UPDATES){
            updates++;
            update();
        }

        if(currentTime - lastUpdateTime >= ONE_NANO_SECOND){
            if(game.getGamePreferences().isDisplayFPS())
                System.out.println("tick (Cycle Frames: " + cycleFrames + ", Effective Frames: " + effectiveFrames + ", Updates: " + updates + ", Draw Calls: " + drawCalls);

            lastFPS = effectiveFrames;

            cycleFrames = 0;
            updates = 0;
            effectiveFrames = 0;

            lastUpdateTime = System.nanoTime();

        }

    }

    private void update(){
        if(game.getCurrentScene() == null) return;
        game.onUpdate();
        game.getCurrentScene().onUpdate();

        //send update to all GameObjects
        game.getCurrentScene().getSceneObjectList().forEach(UpdatableTarget::onUpdate);
    }
}




























