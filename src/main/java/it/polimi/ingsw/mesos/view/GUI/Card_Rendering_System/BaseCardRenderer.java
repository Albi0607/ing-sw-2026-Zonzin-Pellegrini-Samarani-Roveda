package it.polimi.ingsw.mesos.view.GUI.Card_Rendering_System;

import javafx.scene.layout.StackPane;
import javafx.scene.Node;

public abstract class BaseCardRenderer implements CardRendererStrategy {

    protected StackPane createCardBase() {
        return new StackPane(); // Ritorna il frame di base
    }

    protected void loadCardImage(String id) { }
    protected void applyHoverEffect(Node node) { }
    protected void setupTooltip(Node node, String tooltipText) { }
}
