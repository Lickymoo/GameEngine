package test.objects;

import com.mitchellg.gameengine.Renderer;
import com.mitchellg.gameengine.model.GameObject;
import com.mitchellg.gameengine.model.render.ModelLoader;

public class Stick extends GameObject {
    public Stick() {
        super(ModelLoader.importModel("stick"));
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
