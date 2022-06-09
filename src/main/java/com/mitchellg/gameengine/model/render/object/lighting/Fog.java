package com.mitchellg.gameengine.model.render.object.lighting;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import java.awt.*;

@Getter
@Setter
public class Fog {
    private boolean active;

    private Color colour;

    private float density;

    public static Fog NOFOG = new Fog();

    public Fog() {
        active = false;
        this.colour = new Color(0, 0, 0);
        this.density = 0;
    }

    public Fog(boolean active, Color colour, float density) {
        this.colour = colour;
        this.density = density;
        this.active = active;
    }
}