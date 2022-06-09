package test.objects;

import com.mitchellg.gameengine.Renderer;
import com.mitchellg.gameengine.model.GameObject;
import com.mitchellg.gameengine.model.render.ModelLoader;

public class Table extends GameObject {
    public Table() {
        super(ModelLoader.importModel("table"));
        transform.getRotation().rotateAxis((float)Math.toRadians(-90), 1 ,0 ,0);
    }

    @Override
    public void onRender(Renderer renderer) {


    }

    float x = 0;
    @Override
    public void onPreRender(Renderer renderer) {
        x++;
     //   transform.getRotation().rotateAxis((float)Math.toRadians(1), 1 ,1 ,0);
    }

    @Override
    public void onPostRender(Renderer renderer) {

    }

    @Override
    public void onUpdate() {

    }
}
