package com.mitchellg.gameengine.model.render;

import com.mitchellg.gameengine.model.render.object.Model;
import com.mitchellg.gameengine.model.render.object.ModelCollection;
import com.mitchellg.gameengine.model.render.object.Texture;
import com.mitchellg.gameengine.util.IOUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.io.File;

public class ModelLoader {

    public static ModelCollection importModel(String modelName){
        return importModel(modelName, "/models/" + modelName, true);
    }

    public static ModelCollection importModel(String modelName, String filepath, boolean asResource){
        //load models
        if(asResource){
            Path resourceDir = Paths.get("resources");
            filepath = resourceDir.toFile().getAbsolutePath().replace("\\", "/") + filepath;
            filepath = filepath.replace("resources", "target/classes");
        }

        //find model matching name ignore extension
        String modelFile = IOUtils.getFileIgnoreExtension(filepath, modelName);

        if(modelFile == null){
            throw new RuntimeException("Could not import model, folder must contain model with name " + modelName);
        }

        Model[] models = loadModelFile(filepath + "/" + modelFile, false);
        //load textures

        String textureFile = IOUtils.getFileIgnoreExtension(filepath + "/", "texture");

        for(int i = 0; i < models.length; i++){
            if(textureFile == null) continue;
            if(models[i].getTexCoords() == null) continue;
            models[i].setTexture(new Texture(filepath + "/" + textureFile, false));
        }

        //load normal
        String normalFile = IOUtils.getFileIgnoreExtension(filepath + "/", "normal");

        for(int i = 0; i < models.length; i++){
            if(normalFile == null) continue;
            models[i].setNormal(new Texture(filepath + "/" + normalFile, false));
        }

        return new ModelCollection(models);
    }

    public static Model[] loadModelFile(String filepath){
        return loadModelFile(filepath, true);
    }

    public static Model[] loadModelFile(String filepath, boolean asResource){
        if(asResource){
            Path resourceDir = Paths.get("resources");
            filepath = resourceDir.toFile().getAbsolutePath().replace("\\", "/") + filepath;
            filepath = filepath.replace("resources", "target/classes");
        }

        AIScene scene = Assimp.aiImportFile(filepath, Assimp.aiProcess_Triangulate);
        ArrayList<Model> modelList = new ArrayList<>();
        if(scene == null){
            throw new RuntimeException("Model unable to load");
        }
        PointerBuffer buffer = scene.mMeshes();
        for(int i = 0; i < buffer.limit(); i++){
            AIMesh mesh = AIMesh.create(buffer.get(i));
            Model model = processMesh(mesh);
            modelList.add(model);
        }

        Assimp.aiReleaseImport(scene);

        return modelList.toArray(new Model[0]);
    }

    private static Model processMesh(AIMesh mesh){
        Model model = new Model();

        //Vectors
        try {
            AIVector3D.Buffer vertBuffer = mesh.mVertices();
            float[] vertices = new float[vertBuffer.limit() * 3];
            for (int i = 0; i < vertBuffer.limit(); i++) {
                AIVector3D vector = vertBuffer.get(i);

                vertices[i * 3] = vector.x();
                vertices[i * 3 + 1] = vector.y();
                vertices[i * 3 + 2] = vector.z();
            }

            model.setVertices(vertices);
        }catch (NullPointerException ignored){
        }


        //Textures
        try {
            AIVector3D.Buffer texBuffer = mesh.mTextureCoords(0);
            float[] texCoords = new float[texBuffer.limit() * 2];
            for (int i = 0; i < texBuffer.limit(); i++) {
                AIVector3D texCoord = texBuffer.get(i);

                texCoords[i * 2] = (texCoord.x());
                //Idk why but y coord is flipped
                texCoords[i * 2 + 1] = (-texCoord.y());
            }
            model.setTexCoords(texCoords);
        }catch (NullPointerException ignored){
        }

        //Normals
        try {
            AIVector3D.Buffer normBuffer = mesh.mNormals();
            float[] normals = new float[normBuffer.limit() * 3];
            for (int i = 0; i < normBuffer.limit(); i++) {
                AIVector3D norm = normBuffer.get(i);

                normals[i * 3 ] = (norm.x());
                normals[i * 3 + 1] = (norm.y());
                normals[i * 3 + 2] = (norm.z());
            }

            model.setNormals(normals);
        }catch (NullPointerException ignored){
        }

        //Colours
        try {
            AIColor4D.Buffer colorBuffer = mesh.mColors(0);
            float[] colors = new float[colorBuffer.limit() * 4];
            for (int i = 0; i < colorBuffer.limit(); i++) {
                AIColor4D color = colorBuffer.get(i);

                colors[i * 4] = (color.r());
                colors[i * 4 + 1] = (color.g());
                colors[i * 4 + 2] = (color.b());
                colors[i * 4 + 3] = (color.a());
            }

            model.setColors(colors);
        }catch (NullPointerException ignored){
        }

        //Indices
        try{

            AIFace.Buffer indicesBuffer = mesh.mFaces();
            int[] indices = new int[mesh.mNumFaces() * 3];
            for(int i = 0; i < indicesBuffer.limit(); i++){
                AIFace face = indicesBuffer.get(i);
                indices[i * 3] = face.mIndices().get(0);
                indices[i * 3 + 1] = face.mIndices().get(1);
                indices[i * 3 + 2] = face.mIndices().get(2);
            }



            model.setIndices(indices);
        }catch (NullPointerException e){
        }

        return model;
    }
}
