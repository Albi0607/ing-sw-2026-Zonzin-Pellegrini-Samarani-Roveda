package it.polimi.ingsw.mesos.view.GUI.Controllers.Card_Rendering_System;

import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.CardDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.GameControllerGUI;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class CardController {

    private ClientController clientController;
    private GameControllerGUI gameController;
    private boolean interactable = false;
    private int position;

    @FXML private StackPane cardSlot;
    @FXML private ImageView cardImage;

    private CardDTO dto;
    private CardAction action;

    public void setController(ClientController clientController, GameControllerGUI gameController){
        this.clientController = clientController;
        this.gameController = gameController;
    }

    public void setPosition(int position){
        this.position=position;
    }

    public void setCard(CardDTO dto) {
        this.dto = dto;

        //capisce che effetti e azioni attribuire alla carta in base al tipo
        this.action = CardActionFactory.create(dto);

        //disegna la carta avvalendosi della classe cardView
        CardView.render(cardImage,dto);

        //per resettare o settare lo stile iniziale di tutte le carte
        resetEffect();
    }

    public void setInteractable(boolean enabled) {
        this.interactable = enabled;

        if (!enabled) {
            resetEffect();
        }
    }

    public CardDTO getDTO() {
        return dto;
    }


    @FXML private void handleCardClick() {
        if (!interactable) return;
        if(action!=null){
            action.onClickCard(this);
            clientController.takeCard(position,gameController.getIsUpper());
        }
    }

    @FXML private void handleCardEnter() {
        if (!interactable) return;
        if(action!=null){
            action.onCardEnter(this);
        }
    }

    @FXML private void handleCardExit() {
        if (!interactable) return;
        if(action!=null){
            action.onCardExit(this);
        }
    }


    public void setHoverEffect(String style) {
        cardSlot.setStyle(style);
    }

    public void setClickEffect(String style) {
        cardSlot.setStyle(style);
    }

    public void resetEffect() {
        cardSlot.setStyle("");
    }
}