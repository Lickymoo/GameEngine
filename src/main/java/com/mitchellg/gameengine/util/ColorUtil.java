package com.mitchellg.gameengine.util;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.awt.*;

public class ColorUtil {
    public static float[] colorComponents(Color color){
        float r = color.getRed()/255f;
        float g = color.getGreen()/255f;
        float b = color.getBlue()/255f;
        float a = color.getAlpha()/255f;

        return new float[]{r,g,b,a};
    }

    public static Vector3f colorComponents3f(Color color){
        float r = color.getRed()/255f;
        float g = color.getGreen()/255f;
        float b = color.getBlue()/255f;

        return new Vector3f(r,g,b);
    }
    public static Vector4f colorComponents4f(Color color){
        float r = color.getRed()/255f;
        float g = color.getGreen()/255f;
        float b = color.getBlue()/255f;
        float a = color.getAlpha()/255f;

        return new Vector4f(r,g,b,a);
    }
}
