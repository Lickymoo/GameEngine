package com.mitchellg.gameengine.model.location;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Getter
@Setter
public class Transform {
    private Vector3f position;
    private Quaternionf rotation;
    private Vector3f scale;
    private Matrix4f lightViewMatrix;

    public Transform(){
        position = new Vector3f();
        rotation = new Quaternionf();
        scale = new Vector3f(1);
        lightViewMatrix = new Matrix4f();
    }

    public Matrix4f getTransformation(){
        Matrix4f matrix = new Matrix4f();

        matrix.translate(position);
        matrix.rotate(rotation);
        matrix.scale(scale);

        return matrix;
    }

    public void setPosition(float x, float y, float z){
        setPosition(new Vector3f(x, y, z));
    }

    public void setScale(float s){
        setScaleVector(new Vector3f(s));
    }

    public void setScaleVector(Vector3f vector){
        scale = vector;
    }

    private Matrix4f updateGenericViewMatrix(Vector3f position, Vector3f rotation, Matrix4f matrix) {
        matrix.identity();
        // First do the rotation so camera rotates over its position
        matrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        matrix.translate(-position.x, -position.y, -position.z);
        return matrix;
    }

    public Matrix4f updateLightViewMatrix(Vector3f position, Vector3f rotation) {
        return updateGenericViewMatrix(position, rotation, lightViewMatrix);
    }
}
