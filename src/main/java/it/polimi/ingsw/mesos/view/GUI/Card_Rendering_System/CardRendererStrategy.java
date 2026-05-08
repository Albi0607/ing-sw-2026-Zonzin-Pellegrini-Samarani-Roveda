package it.polimi.ingsw.mesos.view.GUI.Card_Rendering_System;
import it.polimi.ingsw.mesos.common.CardJson;
import javafx.scene.Node;

public interface CardRendererStrategy {
    Node render(CardJson card);
}
