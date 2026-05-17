package it.polimi.ingsw.mesos.view.GUI.Controllers.Board;

import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.OfferTileDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.GameControllerGUI;
import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservableGameModel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class OfferTileController {
    private ClientController clientController;
    private GameControllerGUI gameController;
    private ObservableGameModel gameModel;

    private final List<OfferTileView> offerViews = new ArrayList<>();



    @FXML HBox offerTileArea;


    public void set(ClientController clientController, GameControllerGUI gameController){
        this.clientController = clientController;
        this.gameController = gameController;

    }

    //forse non serve
    public void setModel(ObservableGameModel model) {
        this.gameModel = model;

    }

    //disegnare una sola volta tutti gli offerTile e successivamente spostare solo i totem
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

    //controllo se ci sono totem o sono andati via totem da offerTile
    public void update(ObservableList<OfferTileDTO> offerTiles) {

        for (int i = 0; i < offerTiles.size(); i++) {

            OfferTileDTO dto = offerTiles.get(i);
            OfferTileView view = offerViews.get(i);

            view.updateTotem(dto);
        }

        refreshOfferTileInteraction();
    }


    public void refreshOfferTileInteraction() {

        for (OfferTileView view : offerViews) {

            boolean enabled = gameController.isMyTurn(GameState.PLACING_TOTEMS)&&view.getDTO().occupantColor==null;
            view.setInteractable(enabled);

        }
    }

    public void onOfferSelected(OfferTileDTO dto) {

        if (!gameController.isMyTurn(GameState.PLACING_TOTEMS)) return;

        clientController.placeTotem(dto.id.charAt(0));
    }


}
