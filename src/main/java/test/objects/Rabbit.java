package test.objects;

import com.mitchellg.gameengine.Renderer;
import com.mitchellg.gameengine.model.GameObject;
import com.mitchellg.gameengine.model.render.ModelLoader;
import com.mitchellg.gameengine.model.render.object.Texture;

import static org.lwjgl.opengl.GL11.*;

public class Rabbit extends GameObject {
    public Rabbit() {
        super(ModelLoader.importModel("rabbit"));
        transform.getRotation().rotateAxis((float)Math.toRadians(-90), 1 ,0 ,0);
    }

    @Override
    public void onRender(Renderer renderer) {
    }

    @Override
    public void onPreRender(Renderer renderer) {
    }

    @Override
    public void onPostRender(Renderer renderer) {

    }

    @Override
    public void onUpdate() {

    }
}
