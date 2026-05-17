package it.polimi.ingsw.mesos.view.GUI.Controllers.Board;

import it.polimi.ingsw.mesos.rete.ClientModel.OfferTileDTO;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public class OfferTileView {

    @FXML private StackPane offerSlot;
    @FXML private ImageView offerImage;
    @FXML private ImageView offerTotemImage;
    private OfferTileController offerController;
    private OfferTileDTO dto;

    public void setController(OfferTileController offerController){
        this.offerController = offerController;
    }


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
            StackPane.setMargin(offerTotemImage, new Insets(2, 0, 0, 0));
        } catch (Exception e) {
            System.out.println("ERRORE NEL DISEGNARE IL TOTEM NELL'OFFERTILE: " + e.getMessage());
        }
    }

    public OfferTileDTO getDTO(){
        return dto;
    }

    public void setInteractable(boolean enabled) {
        offerSlot.setDisable(!enabled);

        if (!enabled) {
            resetStyle();
        }
    }

    //effetti visivi
    public void handleOfferEnter() {
        if (offerSlot.isDisabled()) return;

        offerSlot.setStyle("-fx-effect: dropshadow(gaussian, yellow, 14, 0.7, 0, 0); -fx-cursor: hand;");
    }

    public void handleOfferExit() {
        if (offerSlot.isDisabled()) return;

        resetStyle();
    }

    public void handleOfferClick() {
        if (offerSlot.isDisabled()) return;

        offerSlot.setStyle("-fx-effect: dropshadow(gaussian, lime, 18, 0.8, 0, 0);" + "-fx-scale-x: 0.95;" + "-fx-scale-y: 0.95;");
        offerController.onOfferSelected(dto);
    }

    private void resetStyle() {
        offerSlot.setStyle("");
    }

}
