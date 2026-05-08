package it.polimi.ingsw.mesos.view.GUI.Card_Rendering_System;

import java.util.HashMap;
import java.util.Map;

public class RendererRegistry {
    private final Map<Class<?>, CardRendererStrategy> renderers = new HashMap<>();

    public void registerRenderer(Class<?> cardClass, CardRendererStrategy strategy) { }
    public CardRendererStrategy getRenderer(Class<?> cardClass) {
        return null; // DA IMPLEMENTARE
    }
}
