package test;

import com.mitchellg.gameengine.Game;
import com.mitchellg.gameengine.model.GameScene;
import com.mitchellg.gameengine.model.render.object.Skybox;
import com.mitchellg.gameengine.model.render.object.lighting.DirectionalLight;
import com.mitchellg.gameengine.model.render.object.lighting.Fog;
import org.joml.Vector3f;
import test.objects.*;

import java.awt.*;

public class TestScene extends GameScene {
    public TestScene(Game game) {
        super(game);
    }

    @Override
    public void onLoad() {
        ambientLight = new Color(.3f, .3f ,.3f);

        float angRad = (float)Math.toRadians(90);
        Vector3f angle = new Vector3f(angRad, angRad, 0);
        directionalLight = new DirectionalLight(Color.WHITE, angle, 1);

        skybox = new Skybox();

        fog = new Fog();

        Table table = new Table();
        table.getTransform().setPosition(0, 0, -5);
        table.getTransform().setScale(0.05f);
        this.registerSceneObject(table);

        Plane plane = new Plane();
        plane.getTransform().setPosition(0, 0, 0);
        plane.getTransform().setScale(100f);
        this.registerSceneObject(plane);

        Stick stick = new Stick();
        stick.getTransform().setPosition(0, 0, 0);
        stick.getTransform().setScale(0.01f);
        this.registerSceneObject(stick);

        ((TestGame)game).pos = new Vector3f(0, 1, 0);
    }


    @Override
    public void onUpdate() {

    }
}
