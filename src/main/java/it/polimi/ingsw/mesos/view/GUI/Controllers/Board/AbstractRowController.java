package it.polimi.ingsw.mesos.view.GUI.Controllers.Board;

import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.CardDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.GameControllerGUI;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base controller for a row of cards on the game board.
 * Contains all the shared logic for rendering, updating and managing
 * interactivity of a card row, whether upper or lower.
 * Subclasses provide the specific HBox and whether the row is upper or lower.
 */
public abstract class AbstractRowController {
    /** The client controller used to send game actions to the server. */
    private ClientController clientController;
    /** The game controller used to read the current game state. */
    private GameControllerGUI gameController;
    /** The list of CardControllers currently displayed in this row. */
    private final List<CardController> cards = new ArrayList<>();
    /** The round number at the last full row rebuild, used to detect round changes. */
    private int currentRound = 0;

    /**
     * Injects the client controller and game controller into this row controller.
     *
     * @param clientController the client controller used to send game actions
     * @param gameController the game controller used to read the current game state
     */
    public void setController(ClientController clientController, GameControllerGUI gameController) {
        this.clientController = clientController;
        this.gameController = gameController;
    }


    /**
     * Returns the HBox containing the card nodes for this row.
     * Implemented by each subclass to return its own HBox.
     *
     * @return the HBox of this row
     */
    public abstract HBox getRowBox();

    /**
     * Returns whether this row is the upper row of the board.
     * Used by refreshInteraction to determine if this row should be interactable.
     *
     * @return true if this is the upper row, false if it is the lower row
     */
    public abstract boolean isUpperRow();

    /**
     * Updates this row from the given observable list of card DTOs.
     * If the round has not changed and the card count is the same, only refreshes interactivity.
     * If the round has not changed but a card is missing, removes only that card and updates positions.
     * If the round has changed, rebuilds the entire row from scratch.
     *
     * @param row the updated list of card DTOs for this row
     */
    public void updateRow(ObservableList<CardDTO> row) {

        if (this.currentRound == gameController.getCurrentRound()) {
            if (cards.size() == row.size()) {
                refreshInteraction();
                return;
            }
            for (int i = 0; i < cards.size(); i++) {
                if (!cards.get(i).getDTO().id.equals(row.get(i).id)) {
                    getRowBox().getChildren().remove(i);
                    cards.remove(i);
                    for (int j = i; j < cards.size(); j++) {
                        cards.get(j).setPosition(j);
                    }
                    refreshInteraction();
                    return;
                }
            }
        }
        this.currentRound = gameController.getCurrentRound();

        getRowBox().getChildren().clear();
        cards.clear();

        int position = 0;
        for (CardDTO dto : row) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cardView.fxml"));
                Parent cardNode = loader.load();

                CardController controller = loader.getController();
                controller.setController(clientController, gameController);
                controller.setCard(dto);
                controller.setPosition(position);
                controller.setInteractable(false);

                cards.add(controller);
                getRowBox().getChildren().add(cardNode);
                position++;

            } catch (IOException e) {
                System.out.println("ERRORE DI CARICAMENTO DELLE CARTE NELLA RIGA: " + e.getMessage());
                e.printStackTrace();
            }
        }
        refreshInteraction();
    }

    /**
     * Refreshes the interactivity state of all cards in this row.
     * Cards are enabled only if it is the current player's turn in the
     * RESOLVING_ACTIONS state and this row matches the active board row.
     */
    public void refreshInteraction() {
        boolean enabled = gameController.isMyTurn(GameState.RESOLVING_ACTIONS) && (gameController.getIsUpper() == isUpperRow());
        for (CardController card : cards) {
            card.setInteractable(enabled);
        }
    }
}