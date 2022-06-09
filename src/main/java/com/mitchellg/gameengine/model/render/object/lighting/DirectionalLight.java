package com.mitchellg.gameengine.model.render.object.lighting;


import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import java.awt.*;

@Getter
@Setter
public class DirectionalLight {

    private Color color;

    private Vector3f direction;

    private float intensity;

    private float shadowPosMult = 1f;

    public DirectionalLight(Color color, Vector3f direction, float intensity) {
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    public DirectionalLight(DirectionalLight light) {
        this(light.getColor(), new Vector3f(light.getDirection()), light.getIntensity());
    }
}