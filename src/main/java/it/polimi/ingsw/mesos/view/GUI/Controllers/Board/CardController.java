package it.polimi.ingsw.mesos.view.GUI.Controllers.Board;

import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.CardDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.CardView;
import it.polimi.ingsw.mesos.view.GUI.Controllers.GameControllerGUI;
import it.polimi.ingsw.mesos.view.GUI.Controllers.UIEffects;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * Controller for a single card slot on the game board.
 * Handles rendering, interactivity and visual effects for one card,
 * delegating image rendering to CardView and effects to UIEffects.
 */
public class CardController {
    /** The client controller used to send the take card action to the server. */
    private ClientController clientController;
    /** The game controller used to retrieve the current board state, such as isUpper. */
    private GameControllerGUI gameController;
    /** Whether this card slot is currently interactable by the player. */
    private boolean interactable = false;
    /** The position of this card in the board row, used when sending the take card action. */
    private int position;
    /** The DTO representing the card currently displayed in this slot. */
    private CardDTO dto;

    // FXML components
    @FXML private StackPane cardSlot;
    @FXML private ImageView cardImage;

    /**
     * Injects the client controller and game controller into this card controller.
     *
     * @param clientController the client controller used to send game actions
     * @param gameController the game controller used to read the current board state
     */
    public void setController(ClientController clientController, GameControllerGUI gameController){
        this.clientController = clientController;
        this.gameController = gameController;
    }

    /**
     * Assigns a card DTO to this slot, renders its image and resets any visual effect.
     *
     * @param dto the card data to display in this slot
     */
    public void setCard(CardDTO dto) {
        this.dto = dto;

        //disegna la carta avvalendosi della classe cardView
        CardView.render(cardImage,dto);

        //per settare lo stile iniziale di tutte le carte
        resetEffect();
    }

    /**
     * Sets the position of this card slot in the board row.
     *
     * @param position the index of this card in its row
     */
    public void setPosition(int position){
        this.position=position;
    }

    /**
     * Enables or disables interactivity for this card slot.
     * When disabled, all mouse events are ignored and any active effect is reset.
     *
     * @param enabled true to make this card interactable, false to disable it
     */
    public void setInteractable(boolean enabled) {
        this.interactable = enabled;

        if (!enabled) {
            resetEffect();
        }
    }

    /**
     * Returns the card DTO currently displayed in this slot.
     *
     * @return the current CardDTO
     */
    public CardDTO getDTO() {
        return dto;
    }

    /**
     * Handles a click on this card slot.
     * Applies the click visual effect and sends the take card action to the server.
     * Ignored if the card is not interactable.
     */
    @FXML private void handleCardClick() {
        if (!interactable) return;
        UIEffects.applyCardClickEffect(this, dto.id);
        clientController.takeCard(position, gameController.getIsUpper());
    }

    /**
     * Handles the mouse entering this card slot.
     * Applies the hover visual effect.
     * Ignored if the card is not interactable.
     */
    @FXML private void handleCardEnter() {
        if (!interactable) return;
        UIEffects.applyCardHoverEffect(this, dto.id);
    }

    /**
     * Handles the mouse exiting this card slot.
     * Resets any active visual effect.
     * Ignored if the card is not interactable.
     */
    @FXML private void handleCardExit() {
        if (!interactable) return;
        UIEffects.applyCardExitEffect(this);
    }

    /**
     * Applies the given CSS style string to the card slot.
     * Used by UIEffects to set hover and click effects.
     *
     * @param style the CSS style string to apply
     */
    public void setEffect(String style) {
        cardSlot.setStyle(style);
    }

    /**
     * Removes any active CSS style from the card slot, restoring its default appearance.
     */
    public void resetEffect() {
        cardSlot.setStyle("");
    }
}