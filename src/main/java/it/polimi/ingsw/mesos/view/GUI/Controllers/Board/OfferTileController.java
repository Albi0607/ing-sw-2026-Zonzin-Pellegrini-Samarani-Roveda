package it.polimi.ingsw.mesos.view.GUI.Controllers.Board;

import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.network.ClientController;
import it.polimi.ingsw.mesos.common.ClientModel.OfferTileDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.GameControllerGUI;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the offer tile area on the game board.
 * Initializes all offer tile views once at the start of the game and
 * updates only the totem positions on subsequent state changes.
 */
public class OfferTileController {
    /** The client controller used to send the place totem action to the server. */
    private ClientController clientController;
    /** The game controller used to read the current game state and turn. */
    private GameControllerGUI gameController;
    /** The list of offer tile views currently displayed in the offer tile area. */
    private final List<OfferTileView> offerViews = new ArrayList<>();


    // FXML components
    @FXML HBox offerTileArea;


    /**
     * Initializes all offer tile views from the given list of DTOs.
     * Each tile is loaded from FXML, assigned its data and added to the offer tile area.
     * This method should be called once at the start of the game.
     *
     * @param offerTiles the initial list of offer tile DTOs
     */
    public void init(ObservableList<OfferTileDTO> offerTiles){
        for(OfferTileDTO offer : offerTiles){
            try {

                FXMLLoader offerLoader = new FXMLLoader(getClass().getResource("/fxml/offerTileView.fxml"));
                Parent offerTile = offerLoader.load();
                OfferTileView offerView = offerLoader.getController();
                offerView.setController(this);
                offerView.setOffer(offer);
                offerView.setInteractable(false);
                offerTileArea.getChildren().add(offerTile);
                offerViews.add(offerView);

            }catch(Exception e){
                System.out.println("ERRORE DI CARICAMENTO DELLE OFFERTILE NEL OFFERTILECONTROLLER: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Injects the client controller and game controller into this controller.
     *
     * @param clientController the client controller used to send game actions
     * @param gameController the game controller used to read the current game state
     */
    public void setController(ClientController clientController, GameControllerGUI gameController){
        this.clientController = clientController;
        this.gameController = gameController;

    }

    /**
     * Updates the totem positions on all offer tiles from the given list of DTOs.
     * Only the totem images are updated, the tile images remain unchanged.
     * Also refreshes the interactivity state of all tiles.
     *
     * @param offerTiles the updated list of offer tile DTOs
     */
    public void update(ObservableList<OfferTileDTO> offerTiles) {

        for (int i = 0; i < offerTiles.size(); i++) {

            OfferTileDTO dto = offerTiles.get(i);
            OfferTileView view = offerViews.get(i);

            view.updateTotem(dto);
        }

        refreshOfferTileInteraction();
    }

    /**
     * Refreshes the interactivity state of all offer tile views.
     * A tile is enabled only if it is the current player's turn in the
     * PLACING_TOTEMS state and the tile has no occupant.
     */
    public void refreshOfferTileInteraction() {

        for (OfferTileView view : offerViews) {

            boolean enabled = gameController.isMyTurn(GameState.PLACING_TOTEMS)&&view.getDTO().occupantColor==null;
            view.setInteractable(enabled);

        }
    }

    /**
     * Called by an OfferTileView when the player clicks on a tile.
     * Sends the place totem action to the server using the first character of the tile id.
     * Ignored if it is not the current player's turn in the PLACING_TOTEMS state.
     *
     * @param dto the DTO of the selected offer tile
     */
    public void onOfferSelected(OfferTileDTO dto) {

        if (!gameController.isMyTurn(GameState.PLACING_TOTEMS)) return;

        clientController.placeTotem(dto.id.charAt(0));
    }


}
