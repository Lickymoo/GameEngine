package com.mitchellg.gameengine.model.target;

import com.mitchellg.gameengine.Renderer;

public interface RenderableTarget {

    public void onRender(Renderer renderer);

    public void onPreRender(Renderer renderer);

    public void onPostRender(Renderer renderer);
}
