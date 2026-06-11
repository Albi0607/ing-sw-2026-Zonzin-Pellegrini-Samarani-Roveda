package it.polimi.ingsw.mesos.view.GUI.Controllers.Board;

import it.polimi.ingsw.mesos.rete.ClientModel.CardDTO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;


public class BottomRowController extends AbstractRowController {

    @FXML private HBox bottomRowCards;

    /**
     * Returns the HBox containing the card nodes for this bottomRow.
     *
     * @return the HBox of this row
     */
    @Override
    public HBox getRowBox() { return bottomRowCards; }

    /**
     * Returns whether this row is the upper row of the board.
     *
     * @return true if this is the upper row, false if it is the lower row
     */
    @Override
    public boolean isUpperRow() { return false; }

    /**
     * Updates the lower row with the given list of card DTOs.
     * Delegates entirely to the shared updateRow logic in AbstractRowController.
     *
     * @param lowerRow the updated list of card DTOs for the lower row
     */
    public void updateLower(ObservableList<CardDTO> lowerRow) {
        updateRow(lowerRow);
    }
}

