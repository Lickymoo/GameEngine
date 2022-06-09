package com.mitchellg.gameengine.model.render.object.lighting;


import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import java.awt.*;

@Getter
@Setter
public class PointLight {

    private Color color;

    private Vector3f position;

    protected float intensity;

    private Attenuation attenuation;

    public PointLight(Color color, Vector3f position, float intensity) {
        attenuation = new Attenuation(1, 0, 0);
        this.color = color;
        this.position = position;
        this.intensity = intensity;
    }

    public PointLight(Color color, Vector3f position, float intensity, Attenuation attenuation) {
        this(color, position, intensity);
        this.attenuation = attenuation;
    }

    public PointLight(PointLight pointLight) {
        this(pointLight.getColor(), new Vector3f(pointLight.getPosition()),
                pointLight.getIntensity(), pointLight.getAttenuation());
    }

    @Getter
    @Setter
    public static class Attenuation {

        private float constant;

        private float linear;

        private float exponent;

        public Attenuation(float constant, float linear, float exponent) {
            this.constant = constant;
            this.linear = linear;
            this.exponent = exponent;
        }
    }
}