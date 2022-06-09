package com.mitchellg.gameengine.model.game.model;

public abstract class ActionHandler {
    public abstract void onKey(int key, int scancode, int action, int mods);
    public abstract void onMouse(int button, int action, int mods);
    public abstract void onMouseMove(double xpos, double ypos);
}
