package test.objects;

import com.mitchellg.gameengine.Renderer;
import com.mitchellg.gameengine.model.GameObject;
import com.mitchellg.gameengine.model.render.ModelLoader;

public class Plane extends GameObject {
    public Plane() {
        super(ModelLoader.importModel("plane"));
    }

    @Override
    public void onRender(Renderer renderer) {


    }

    float x = 0;
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
