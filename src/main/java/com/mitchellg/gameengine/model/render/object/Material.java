package com.mitchellg.gameengine.model.render.object;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector4f;

import java.awt.*;

@Getter
@Setter
public class Material {

    private static final Color DEFAULT_COLOR = Color.WHITE;

    private Color ambientColor;
    private Color diffuseColor;
    private Color specularColor;

    private float reflectance;
    private Texture texture;
    private Texture normal;

    public Material(){
        this.ambientColor = DEFAULT_COLOR;
        this.diffuseColor = DEFAULT_COLOR;
        this.specularColor = DEFAULT_COLOR;

        this.reflectance = 1f;
        this.texture = null;
    }

    public Material(Texture texture) {
        this(DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR, texture, 0);
    }

    public Material(Texture texture, float reflectance) {
        this(DEFAULT_COLOR, DEFAULT_COLOR, DEFAULT_COLOR, texture, reflectance);
    }

    public Material(Color ambientColor, Color diffuseColor, Color specularColor, Texture texture, float reflectance) {
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        this.texture = texture;
        this.reflectance = reflectance;
    }

    public boolean isTextured() {
        return this.texture != null;
    }
    public boolean isNormalized() {
        return this.normal != null;
    }
}