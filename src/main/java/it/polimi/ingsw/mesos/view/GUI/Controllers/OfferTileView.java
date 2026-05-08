package it.polimi.ingsw.mesos.view.GUI.Controllers;

import it.polimi.ingsw.mesos.common.enums.Color;
import javafx.beans.property.BooleanProperty;
import javafx.scene.layout.StackPane;

public class OfferTileView extends StackPane {
    private char tileId;
    private BooleanProperty hasTotem;
    private Color totemColor;

    public OfferTileView(char tileId) { }

    public void setTotem(Color color) { }
    public void setHighlighted(boolean highlighted) { }
}