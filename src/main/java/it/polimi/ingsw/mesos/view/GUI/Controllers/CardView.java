package it.polimi.ingsw.mesos.view.GUI.Controllers;

import it.polimi.ingsw.mesos.common.CardJson;
import it.polimi.ingsw.mesos.view.GUI.Card_Rendering_System.CardRendererStrategy;
import javafx.scene.layout.StackPane;
import javafx.beans.property.BooleanProperty;


public class CardView extends StackPane {
    private CardJson card; //
    private CardRendererStrategy renderer;
    private BooleanProperty isSelected;

    public CardView(CardJson card) {
        // Chiama il RendererRegistry per farsi disegnare e aggiunge il Node al suo interno
    }

    public void setSelectable(boolean selectable) { }
    public void setHighlighted(boolean highlighted) { }
    public void playAnimation() { } // Es. flip o hover
}
