package test;

import com.mitchellg.gameengine.Game;
import com.mitchellg.gameengine.model.game.model.ActionHandler;
import com.mitchellg.gameengine.model.game.model.Camera;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Vector;

import static org.lwjgl.glfw.GLFW.*;

public class KeyHandler extends ActionHandler {
    private final Game game;
    public KeyHandler(Game game){
        this.game = game;
    }

    @Override
    public void onKey(int key, int scancode, int action, int mods) {
        float x = 0;
        float y = 0;
        float z = 0;
        Vector3f pos = ((TestGame)game).pos;
        Quaternionf rot = ((TestGame)game).rot;

        if(key == GLFW_KEY_W){
            z = -.1f;
        }
        if(key == GLFW_KEY_S){
            z = .1f;
        }

        if(key == GLFW_KEY_A){
            x = -.1f;
        }
        if(key == GLFW_KEY_D){
            x = .1f;
        }

        if(key == GLFW_KEY_SPACE){
            y = .1f;
        }
        if(key == GLFW_KEY_LEFT_SHIFT){
            y = -.1f;
        }

        if(key == GLFW_KEY_Q){
            rot.rotateAxis((float)Math.toRadians(-3), 0, 1, 0);
        }
        if(key == GLFW_KEY_E){
            rot.rotateAxis((float)Math.toRadians(3), 0, 1, 0);
        }

        Camera camera = game.getRenderer().getCamera();

        Vector3f eulerAng = camera.getRotation().getEulerAnglesXYZ(new Vector3f(1f));
        Vector3f ang = new Vector3f(-eulerAng.x * 57.295f, -eulerAng.y * 57.295f, -eulerAng.z * 57.295f);

        if(z != 0) {
            pos.x += (float) Math.sin(Math.toRadians(ang.y)) * -1.0f * z;
            pos.z += (float) Math.cos(Math.toRadians(ang.y)) * z;
        }
        if(x != 0){
            pos.x += (float)Math.sin(Math.toRadians(ang.y - 90)) * -1.0f * x;
            pos.z += (float)Math.cos(Math.toRadians(ang.y - 90)) * x;

        }
        pos.y += y;
    }

    @Override
    public void onMouse(int button, int action, int mods) {
        //game.getGamePreferences().setPolygonMode(!game.getGamePreferences().isPolygonMode());
    }

    @Override
    public void onMouseMove(double xpos, double ypos) {
        double x = 2 * xpos/game.getGamePreferences().getWidth() - 1;
        double y = 1 - 2*ypos/game.getGamePreferences().getHeight();

    }
}
