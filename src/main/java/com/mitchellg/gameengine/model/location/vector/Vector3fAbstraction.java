package com.mitchellg.gameengine.model.location.vector;

import com.mitchellg.gameengine.Renderer;
import org.joml.Vector3f;

public abstract class Vector3fAbstraction extends Vector3f{

    public Vector3fAbstraction(float x, float y, float z){
        super(x, y, z);
    }

    public abstract Vector3f calculatePosition(Renderer renderer);
    public abstract Vector3f calculateScale(Renderer renderer);
}
