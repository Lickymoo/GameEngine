package com.mitchellg.gameengine.model.render;

import com.mitchellg.gameengine.model.game.model.Camera;
import com.mitchellg.gameengine.model.location.Transform;
import com.mitchellg.gameengine.model.render.object.Material;
import com.mitchellg.gameengine.model.render.object.lighting.DirectionalLight;
import com.mitchellg.gameengine.model.render.object.lighting.Fog;
import com.mitchellg.gameengine.model.render.object.lighting.PointLight;
import com.mitchellg.gameengine.model.render.object.lighting.SpotLight;
import com.mitchellg.gameengine.util.ColorUtil;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;

@Getter
public class Shader {
    public enum UniformType{REGULAR,
        MATERIAL,
        DIRECTIONAL_LIGHT,
        POINT_LIGHT,
        SPOT_LIGHT,
        FOG
    }

    public static final Map<String, Shader> shaderMap = new HashMap<>();

    private final Map<String, UniformType> uniformNames = new HashMap<>();

    public void addUniformName(String name, UniformType type){
        uniformNames.put(name, type);
    }

    private int vertexShader;
    private int fragmentShader;
    private int program;

    private final Map<String, Integer> uniforms;

    public Shader(){
        uniforms = new HashMap<>();
    }

    public static Shader getShader(String shader){
        if(shaderMap.containsKey(shader)){
            return shaderMap.get(shader);
        }else{
            Shader newShader = new Shader();
            newShader.create(shader);

            shaderMap.put(shader, newShader);
            return newShader;
        }
    }

    public boolean create(String shader){
        int success;

        //vertex shader
        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, readSource(shader + "/vert.vs"));
        glCompileShader(vertexShader);

        success = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            System.err.println(glGetShaderInfoLog(vertexShader));
            return false;
        }

        //frag shader
        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, readSource(shader + "/frag.fs"));
        glCompileShader(fragmentShader);

        success = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
        if(success == GL_FALSE){
            System.err.println(glGetShaderInfoLog(fragmentShader));
            return false;
        }

        //program
        program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);

        glLinkProgram(program);
        success = glGetProgrami(program, GL_LINK_STATUS);
        if(success == GL_FALSE){
            System.err.println(glGetProgramInfoLog(program));
            return false;
        }
        glValidateProgram(program);
        success = glGetProgrami(program, GL_VALIDATE_STATUS);
        if(success == GL_FALSE){
            System.err.println(glGetProgramInfoLog(program));
            return false;
        }

        return true;

    }

    public void destroy(){
        glDetachShader(program, vertexShader);
        glDetachShader(program, fragmentShader);
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        glDeleteProgram(program);
    }

    public void setCamera(Camera camera){
        {
            float[] matrix = new float[16];
            matrix = camera.getProjection().get(matrix);
            setUniform("cameraProjection", matrix);
        }
        {
            float[] matrix = new float[16];
            matrix = camera.getTransformation().get(matrix);
            setUniform("transformWorld", matrix);
        }
    }

    public void setTransform(Transform transform){
        float[] matrix = new float[16];
        matrix = transform.getTransformation().get(matrix);
        setUniform("transformObject", matrix);
    }

    public void bind() {
        for(Map.Entry<String, UniformType> entry : uniformNames.entrySet()){
            String uniform = entry.getKey();
            UniformType type = entry.getValue();

            switch (type){
                case REGULAR -> {
                    createUniform(uniform);
                }
                case MATERIAL -> {
                    createMaterialUniform(uniform);
                }
                case DIRECTIONAL_LIGHT -> {
                    createDirectionalLightUniform(uniform);
                }
                case POINT_LIGHT -> {
                    createPointLightListUniform(uniform, 5);
                }
                case SPOT_LIGHT -> {
                    createSpotLightListUniform(uniform, 5);
                }
                case FOG -> {
                    createFogUniform(uniform);
                }
            }
        }
        glUseProgram(program);
    }

    public void unbind(){
        glUseProgram(0);
    }

    private String readSource(String file){
        BufferedReader reader = null;
        StringBuilder sourceBuilder = new StringBuilder();

        try{
            reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/shaders/" + file)));
            String line;

            while((line = reader.readLine()) != null){
                sourceBuilder.append(line + "\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                reader.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return sourceBuilder.toString();
    }

    public void createUniform(String uniformName) {
        try {
            int uniformLocation = glGetUniformLocation(program, uniformName);
            if (uniformLocation < 0) {
                throw new RuntimeException("Could not find uniform:" + uniformName);
            }
            uniforms.put(uniformName, uniformLocation);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void createMaterialUniform(String uniformName) {
        try {
            createUniform(uniformName + ".ambient");
            createUniform(uniformName + ".diffuse");
            createUniform(uniformName + ".specular");
            createUniform(uniformName + ".hasTexture");
            createUniform(uniformName + ".hasNormalMap");
            createUniform(uniformName + ".reflectance");
        }catch (Exception ignore){
        }
    }

    public void createDirectionalLightUniform(String uniformName) {
        try {
            createUniform(uniformName + ".colour");
            createUniform(uniformName + ".direction");
            createUniform(uniformName + ".intensity");
        }catch (Exception ignore){

        }
    }

    public void setUniform(String uniformName, Matrix4f value) {
        if(!uniforms.containsKey(uniformName)) return;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(uniforms.get(uniformName), false,
                    value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniformf4(String uniformName, float[] value){
        if(!uniforms.containsKey(uniformName)) return;
        glUniform4f(uniforms.get(uniformName), value[0], value[1], value[2], value[3]);
    }

    public void setUniform(String uniformName, float[] value) {
        if(!uniforms.containsKey(uniformName)) return;
        glUniformMatrix4fv(uniforms.get(uniformName), false, value);
    }

    public void setUniform(String uniformName, int value) {
        if(!uniforms.containsKey(uniformName)) return;
        glUniform1i(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, float value) {

        if(!uniforms.containsKey(uniformName)) return;
        glUniform1f(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, Vector3f value) {
        if(!uniforms.containsKey(uniformName)) return;
        glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, Color value) {
        if(!uniforms.containsKey(uniformName)) return;
        Vector3f colors = ColorUtil.colorComponents3f(value);
        glUniform3f(uniforms.get(uniformName), colors.x, colors.y, colors.z);
    }

    public void setUniformColor4f(String uniformName, Color value) {
        if(!uniforms.containsKey(uniformName)) return;
        float[] colors = ColorUtil.colorComponents(value);
        glUniform4f(uniforms.get(uniformName), colors[0], colors[1], colors[2], colors[3]);
    }

    public void setUniform(String uniformName, Material material) {
        setUniformColor4f(uniformName + ".ambient", material.getAmbientColor());
        setUniformColor4f(uniformName + ".diffuse", material.getDiffuseColor());
        setUniform(uniformName + ".specular", material.getSpecularColor());
        setUniform(uniformName + ".hasTexture", material.isTextured() ? 1 : 0);
        setUniform(uniformName + ".hasNormalMap", material.isNormalized() ? 1 : 0);
        setUniform(uniformName + ".reflectance", material.getReflectance());
    }

    //Directional Light
    public void setUniform(String uniformName, DirectionalLight dirLight) {
        setUniform(uniformName + ".colour", dirLight.getColor() );
        setUniform(uniformName + ".direction", dirLight.getDirection());
        setUniform(uniformName + ".intensity", dirLight.getIntensity());
    }

    //Point Light
    public void createPointLightUniform(String uniformName) {
        try {
            createUniform(uniformName + ".colour");
            createUniform(uniformName + ".position");
            createUniform(uniformName + ".intensity");
            createUniform(uniformName + ".att.constant");
            createUniform(uniformName + ".att.linear");
            createUniform(uniformName + ".att.exponent");
        }catch (Exception ignore){
        }
    }

    public void createPointLightListUniform(String uniformName, int size) {
        for (int i = 0; i < size; i++) {
            createPointLightUniform(uniformName + "[" + i + "]");
        }
    }

    private void setUniform(String uniformName, PointLight pointLight) {
        setUniform(uniformName + ".colour", pointLight.getColor());
        setUniform(uniformName + ".position", pointLight.getPosition());
        setUniform(uniformName + ".intensity", pointLight.getIntensity());
        PointLight.Attenuation att = pointLight.getAttenuation();
        setUniform(uniformName + ".att.constant", att.getConstant());
        setUniform(uniformName + ".att.linear", att.getLinear());
        setUniform(uniformName + ".att.exponent", att.getExponent());
    }

    public void setUniform(String uniformName, PointLight[] pointLights) {
        if(!uniforms.containsKey(uniformName)) return;
        int numLights = pointLights != null ? pointLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            setUniform(uniformName, pointLights[i], i);
        }
    }

    public void setUniform(String uniformName, PointLight pointLight, int pos) {
        setUniform(uniformName + "[" + pos + "]", pointLight);
    }

    //Spot Light
    public void createSpotLightListUniform(String uniformName, int size) {
        for (int i = 0; i < size; i++) {
            createSpotLightUniform(uniformName + "[" + i + "]");
        }
    }

    public void createSpotLightUniform(String uniformName) {
        createPointLightUniform(uniformName + ".pl");
        createUniform(uniformName + ".conedir");
        createUniform(uniformName + ".cutoff");
    }


    public void setUniform(String uniformName, SpotLight spotLight) {
        setUniform(uniformName + ".pl", spotLight.getPointLight());
        setUniform(uniformName + ".conedir", spotLight.getConeDirection());
        setUniform(uniformName + ".cutoff", spotLight.getCutOff());
    }

    public void setUniform(String uniformName, SpotLight[] spotLights) {
        int numLights = spotLights != null ? spotLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            setUniform(uniformName, spotLights[i], i);
        }
    }

    public void setUniform(String uniformName, SpotLight spotLight, int pos) {
        setUniform(uniformName + "[" + pos + "]", spotLight);
    }

    //Fog
    public void createFogUniform(String uniformName) {
        createUniform(uniformName + ".activeFog");
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".density");
    }

    public void setUniform(String uniformName, Fog fog) {
        setUniform(uniformName + ".activeFog", fog.isActive() ? 1 : 0);
        setUniform(uniformName + ".colour", fog.getColour() );
        setUniform(uniformName + ".density", fog.getDensity());
    }
}

