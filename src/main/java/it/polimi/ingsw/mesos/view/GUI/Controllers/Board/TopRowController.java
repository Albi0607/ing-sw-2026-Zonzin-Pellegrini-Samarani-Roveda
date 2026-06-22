package it.polimi.ingsw.mesos.view.GUI.Controllers.Board;


import it.polimi.ingsw.mesos.common.ClientModel.CardDTO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

/**
 * Controller for the upper row of cards on the game board.
 * Extends AbstractRowController and identifies itself as the upper row.
 */
public class TopRowController extends AbstractRowController {

    @FXML private HBox topRowCards;

    /**
     * Returns the HBox containing the card nodes for this topRow.
     *
     * @return the HBox of this row
     */
    @Override
    public HBox getRowBox() { return topRowCards; }

    /**
     * Returns whether this row is the upper row of the board.
     *
     * @return true if this is the upper row, false if it is the lower row
     */
    @Override
    public boolean isUpperRow() { return true; }

    /**
     * Updates the upper row with the given list of card DTOs.
     * Delegates entirely to the shared updateRow logic in AbstractRowController.
     *
     * @param upperRow the updated list of card DTOs for the upper row
     */
    public void updateUpper(ObservableList<CardDTO> upperRow) {
        updateRow(upperRow);
    }
}