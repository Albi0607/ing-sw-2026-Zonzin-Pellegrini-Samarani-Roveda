package it.polimi.ingsw.mesos.view.GUI.Controllers.Board;

import it.polimi.ingsw.mesos.common.ClientModel.OfferTileDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.UIEffects;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.Objects;

/**
 * View controller for a single offer tile on the game board.
 * Displays the tile image and the totem of the player currently occupying it.
 * Handles mouse interaction and delegates selection to OfferTileController.
 */
public class OfferTileView {

    /** The parent offer tile controller used to notify tile selection events. */
    private OfferTileController offerController;
    /** The DTO representing the offer tile currently displayed by this view. */
    private OfferTileDTO dto;

    // FXML components

    @FXML private StackPane offerSlot;
    @FXML private ImageView offerImage;
    @FXML private ImageView offerTotemImage;

    /**
     * Injects the parent offer tile controller into this view.
     *
     * @param offerController the controller to notify when this tile is selected
     */
    public void setController(OfferTileController offerController){
        this.offerController = offerController;
    }

    /**
     * Sets the offer tile data and loads the tile image from resources.
     * Called once during initialization for each tile.
     *
     * @param offer the DTO of the offer tile to display
     */
    public void setOffer(OfferTileDTO offer){
        this.dto=offer;
        String pathOffer="/images/Tiles/tile_"+ offer.id + ".png";

        try{
            Image offer_image = new Image(Objects.requireNonNull(OfferTileView.class.getResourceAsStream(pathOffer)));
            offerImage.setImage(offer_image);

        } catch (Exception e) {
            System.out.println("ERRORE DI CARICAMENTO DELLE IMMAGINI DI OFFERTILE O DI TOTEM: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Updates the totem image displayed on this tile.
     * If the tile has no occupant, clears the totem image.
     * Otherwise, loads and displays the totem image for the occupant color,
     * aligning it to the top center of the tile.
     *
     * @param offer the updated DTO containing the current occupant color
     */
    public void updateTotem(OfferTileDTO offer) {
        this.dto = offer;
        if (offer.occupantColor == null) {
            offerTotemImage.setImage(null);
            return;
        }

        String pathTotem = "/images/totem/" + offer.occupantColor + ".png";
        try {
            Image totemImage = new Image(Objects.requireNonNull(OfferTileView.class.getResourceAsStream(pathTotem)));
            offerTotemImage.setImage(totemImage);

            //allineo il totem perfettamente nel suo posto
            StackPane.setAlignment(offerTotemImage, Pos.TOP_CENTER);
            StackPane.setMargin(offerTotemImage, new Insets(1, 0, 0, 0));
        } catch (Exception e) {
            System.out.println("ERRORE NEL DISEGNARE IL TOTEM NELL'OFFERTILE: " + e.getMessage());
        }
    }

    /**
     * Returns the DTO of the offer tile currently displayed by this view.
     *
     * @return the current OfferTileDTO
     */
    public OfferTileDTO getDTO(){
        return dto;
    }

    /**
     * Enables or disables interaction with this offer tile.
     * When disabled, resets any active visual effect.
     *
     * @param enabled true to make this tile interactable, false to disable it
     */
    public void setInteractable(boolean enabled) {
        offerSlot.setDisable(!enabled);

        if (!enabled) {
            resetStyle();
        }
    }

    /**
     * Applies a click effect and notifies the parent controller when the tile is clicked.
     * Ignored if the tile is disabled.
     */
    @FXML public void handleOfferClick() {
        if (offerSlot.isDisabled()) return;
        UIEffects.applyOfferClickEffect(offerSlot);
        offerController.onOfferSelected(dto);
    }

    /**
     * Applies a hover effect when the cursor enters the tile and
     * calls the totem placement action in the client controller.
     * Ignored if the tile is disabled.
     */
    @FXML public void handleOfferEnter() {
        if (offerSlot.isDisabled()) return;
        UIEffects.applyOfferHoverEffect(offerSlot);
    }

    /**
     * Resets the visual effect when the mouse exits the tile.
     * Ignored if the tile is disabled.
     */
    @FXML public void handleOfferExit() {
        if (offerSlot.isDisabled()) return;
        UIEffects.resetOfferEffect(offerSlot);
    }

    /**
     * Resets the visual effect.
     */
    private void resetStyle() {
        offerSlot.setStyle("");
    }

}
