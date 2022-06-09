package com.mitchellg.gameengine.model.game.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Getter
@Setter
public class Camera {
    private Vector3f position;
    private Quaternionf rotation;
    private float fov;

    @Setter(AccessLevel.NONE)
    private Matrix4f projection;

    public Camera(){
        position = new Vector3f();
        rotation = new Quaternionf();
        projection = new Matrix4f();
    }

    public Matrix4f getTransformation(){
        Matrix4f matrix = new Matrix4f();
        matrix.rotate(rotation.conjugate(new Quaternionf()));
        matrix.translate(position.mul(-1, new Vector3f()));

        return matrix;
    }

    public void setOrthographic(float left, float right, float top, float bottom){
        projection.setOrtho2D(left, right, top, bottom);
    }

    public void setPerspective(float fov, float aspectRatio, float zNear, float zFar){
        this.fov = fov;
        projection.setPerspective(fov, aspectRatio, zNear, zFar);
    }

}
