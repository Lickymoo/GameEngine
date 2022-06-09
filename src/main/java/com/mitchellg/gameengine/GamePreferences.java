package com.mitchellg.gameengine;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GamePreferences {
    private int height = 320;
    private int width = 640;
    private boolean fullscreen = false;
    private boolean displayFPS = false;
    private boolean polygonMode = false;
    private String windowName = "Game Window";
    private long monitor = 0;
    private int maxFov = 80;

    private int maxPointLights = 5;
    private int maxSpotLights = 5;

    private int framesPerSecond = -1;

    public double getAspectRatio(){
        return ((double) width)/((double)height);
    }

}
