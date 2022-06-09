package test;

import com.mitchellg.gameengine.Game;
import com.mitchellg.gameengine.model.location.Transform;
import com.mitchellg.gameengine.model.render.Shader;
import com.mitchellg.gameengine.model.render.object.lighting.PointLight;
import lombok.Getter;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;

public class TestGame extends Game {

    @Getter private static TestGame instance;

    public Vector3f pos = new Vector3f();
    public Quaternionf rot = new Quaternionf();

    public static void main(String[] args) {
        instance = new TestGame();
    }

    @Override
    public void onUpdate() {
        getRenderer().getCamera().setPosition(pos);
        getRenderer().getCamera().setRotation(rot);
    }

    @Override
    public void onExit() {
        System.out.println("EXIT");
    }

    @Override
    public void onLoad() {
        System.out.println("LOAD");
        pos = getRenderer().getCamera().getPosition();
        rot = getRenderer().getCamera().getRotation();

        //init shaders
        Shader complex = Shader.getShader("complex");
        complex.addUniformName("cameraProjection", Shader.UniformType.REGULAR);
        complex.addUniformName("transformObject", Shader.UniformType.REGULAR);
        complex.addUniformName("transformWorld", Shader.UniformType.REGULAR);
        complex.addUniformName("specularPower", Shader.UniformType.REGULAR);
        complex.addUniformName("ambientLight", Shader.UniformType.REGULAR);
        complex.addUniformName("texture_sampler", Shader.UniformType.REGULAR);
        complex.addUniformName("normalMap", Shader.UniformType.REGULAR);

        complex.addUniformName("material", Shader.UniformType.MATERIAL);
        complex.addUniformName("directionalLight", Shader.UniformType.DIRECTIONAL_LIGHT);
        complex.addUniformName("pointLights", Shader.UniformType.POINT_LIGHT);
        complex.addUniformName("spotLights", Shader.UniformType.SPOT_LIGHT);
        complex.addUniformName("fog", Shader.UniformType.FOG);


        Shader skybox = Shader.getShader("skybox");
        skybox.addUniformName("cameraProjection", Shader.UniformType.REGULAR);
        skybox.addUniformName("transformObject", Shader.UniformType.REGULAR);
        skybox.addUniformName("transformWorld", Shader.UniformType.REGULAR);
        skybox.addUniformName("ambientLight", Shader.UniformType.REGULAR);

        Shader depth = Shader.getShader("depth");
        depth.addUniformName("orthoProjectionMatrix", Shader.UniformType.REGULAR);
        depth.addUniformName("modelLightViewMatrix", Shader.UniformType.REGULAR);

        setCurrentScene(new TestScene(this));
        registerKeySubscriber(new KeyHandler(this));
    }

    @Override
    public void onPreLoad() {
        gamePreferences.setWindowName("Test Game");
    }
}
