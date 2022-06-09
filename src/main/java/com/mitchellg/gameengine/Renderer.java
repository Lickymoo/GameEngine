package com.mitchellg.gameengine;

import com.mitchellg.gameengine.model.GameObject;
import com.mitchellg.gameengine.model.game.model.Camera;
import com.mitchellg.gameengine.model.location.Transform;
import com.mitchellg.gameengine.model.location.vector.Vector3fAbstraction;
import com.mitchellg.gameengine.model.render.Shader;
import com.mitchellg.gameengine.model.render.object.Model;
import com.mitchellg.gameengine.model.render.object.ShadowMap;
import com.mitchellg.gameengine.model.render.object.Skybox;
import com.mitchellg.gameengine.model.render.object.lighting.DirectionalLight;
import com.mitchellg.gameengine.model.render.object.lighting.PointLight;
import com.mitchellg.gameengine.util.ColorUtil;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;

import java.awt.*;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.SortedMap;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBFramebufferObject.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.ARBFramebufferObject.glBindFramebuffer;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;

@Getter
public class Renderer {

    private final GamePreferences gamePreferences;
    private final Game game;
    private long window;

    private Camera camera;
    private Transform shaderTransform;
    private ShadowMap shadowMap;

    public Renderer(Game game, GamePreferences gamePreferences){
        this.game = game;
        this.gamePreferences = gamePreferences;
    }

    public void init(){
        glfwInit();

        //Create window
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit() ) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_STENCIL_BITS, 4);
        glfwWindowHint(GLFW_SAMPLES, 4);

        window = glfwCreateWindow(
                gamePreferences.getWidth(),
                gamePreferences.getHeight(),
                gamePreferences.getWindowName(),
                gamePreferences.getMonitor(),
                0);

        if (window == 0)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback(){
            @Override
            public void invoke(long window, int width, int height){
                onReisze(window, width, height);
            }
        });

        glfwSetKeyCallback(window, new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                game.getActionHandlers().forEach(subscriber ->{
                    subscriber.onKey(key, scancode, action, mods);
                });
            }
        });

        glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallback() {

            @Override
            public void invoke(long window, int button, int action, int mods) {
                game.getActionHandlers().forEach(subscriber ->{
                    subscriber.onMouse(button, action, mods);
                });
            }
        });

        glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {

            @Override
            public void invoke(long window, double xpos, double ypos) {
                game.getActionHandlers().forEach(subscriber ->{
                    subscriber.onMouseMove(xpos, ypos);
                });
            }
        });

        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);
        GL.createCapabilities();


        glEnable(GL_AUTO_NORMAL);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        //Init Camera
        camera = new Camera();
        camera.getRotation().rotateAxis((float)Math.toRadians(180), 0, 0, 1);

        shadowMap = new ShadowMap();

        shaderTransform = new Transform();

    }

    public void onReisze(long window, int width, int height){
        gamePreferences.setWidth(width);
        gamePreferences.setHeight(height);

        glfwInit();

        glViewport(0, 0, width, height);
    }

    public int render(){
        int drawCalls = 0;

        GLFW.glfwSetWindowTitle(window, gamePreferences.getWindowName() + " (FPS " + game.gameRunnable.getLastFPS() + ")");

        //Clear Buffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_DEPTH_TEST);

        //Define aspect ratio for unit size
        //This ensures 1:1 size
        float aspectRatio = ((float)gamePreferences.getWidth()) / ((float)gamePreferences.getHeight());

        camera.setPerspective(gamePreferences.getMaxFov(), aspectRatio, 0.01f, 1000f);

        if(gamePreferences.isPolygonMode())
            glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );

        //Render skybox
        Skybox skybox = game.currentScene.getSkybox();
        if(skybox != null){
            skybox.create();
            skybox.onPreRender(this);
            drawObject(skybox);
            skybox.onPostRender(this);
        }


        //Iterate through scene objects
        ArrayList<GameObject> gameObjects = game.currentScene.getSceneObjectList();
        for(GameObject object : gameObjects){
            object.create();
            object.onPreRender(this);
            drawObject(object);
            object.onRender(this);

            drawCalls += object.getModel().getModels().length;
        }

        //Render
        glfwPollEvents();
        glfwSwapBuffers(getWindow());

        return drawCalls;
    }

    public void renderDepthMap(){
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
        glClear(GL_DEPTH_BUFFER_BIT);

        Shader depth = Shader.getShader("depth");
        depth.bind();

        DirectionalLight light = game.currentScene.getDirectionalLight();
        Vector3f lightDirection = light.getDirection();

        float lightAngleX = (float)Math.toDegrees(Math.acos(lightDirection.z));
        float lightAngleY = (float)Math.toDegrees(Math.asin(lightDirection.x));
        float lightAngleZ = 0;
        Matrix4f lightViewMatrix = shaderTransform.updateLightViewMatrix(new Vector3f(lightDirection).mul(light.getShadowPosMult()), new Vector3f(lightAngleX, lightAngleY, lightAngleZ));
        DirectionalLight.OrthoCoords orthCoords = light.getOrthoCoords();
        Matrix4f orthoProjMatrix = transformation.updateOrthoProjectionMatrix(orthCoords.left, orthCoords.right, orthCoords.bottom, orthCoords.top, orthCoords.near, orthCoords.far);

        depth.setUniform("orthoProjectionMatrix", orthoProjMatrix);
        Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
            mesh.renderList(mapMeshes.get(mesh), (GameItem gameItem) -> {
                        Matrix4f modelLightViewMatrix = transformation.buildModelViewMatrix(gameItem, lightViewMatrix);
                        depthShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);
                    }
            );
        }

        // Unbind
        depth.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void cleanup(){
        ArrayList<GameObject> gameObjects = game.currentScene.getSceneObjectList();
        gameObjects.forEach(obj -> {
            for(Model model : obj.getModel().getModels()){
                model.cleanup();
            }
        });
    }

    public Vector3f calculateScalar(Vector3f scale){
        if(scale instanceof Vector3fAbstraction scaleV) {
            return scaleV.calculateScale(this);
        }else{
            //Regular Vector3f scaling
            return scale;
        }
    }

    public Vector3f calculatePositionScalar(Vector3f position){
        if(position instanceof Vector3fAbstraction positionV){
            return positionV.calculatePosition(this);
        }else{
            return position;
        }
    }

    public void drawObject(GameObject object){
        Model[] models = object.getModel().getModels();
        Shader shader = Shader.getShader(object.getShader());
        shader.bind();


        glViewport(0, 0, gamePreferences.getWidth(), gamePreferences.getHeight());


        shader.setUniform("texture_sampler", 0);
        shader.setUniform("normalMap", 1);
        shader.setUniform("fog", game.currentScene.getFog());
        //Setup lighting

        DirectionalLight currDirLight = new DirectionalLight(game.getCurrentScene().getDirectionalLight());
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(camera.getTransformation());
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));

        for(Model model : models) {
            if(model.isCullFaces()){
                glEnable(GL_CULL_FACE);
                glCullFace(GL_BACK);
            }else{
                glDisable(GL_CULL_FACE);
            }

            shaderTransform = new Transform();
            shaderTransform.setRotation(object.getTransform().getRotation());

            //position calculation
            Vector3f position = object.getTransform().getPosition();
            shaderTransform.setPosition(calculatePositionScalar(position));

            //transform including scalar multiplication
            Vector3f scale = object.getTransform().getScale();
            shaderTransform.setScaleVector(calculateScalar(scale));

            shader.setTransform(shaderTransform);
            shader.setCamera(camera);

            shader.setUniform("specularPower", game.getCurrentScene().getSpecularPower());
            shader.setUniform("ambientLight", object.getEffectiveAmbient(game));
            shader.setUniform("material", model.getMaterial());

            shader.setUniform("directionalLight", currDirLight);


            //render
            model.render();
            shader.unbind();
        }
    }
}
