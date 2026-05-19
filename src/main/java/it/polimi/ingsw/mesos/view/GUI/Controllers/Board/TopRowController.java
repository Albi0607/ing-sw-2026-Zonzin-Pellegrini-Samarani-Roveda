package it.polimi.ingsw.mesos.view.GUI.Controllers.Board;

import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.CardDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.Card_Rendering_System.CardController;
import it.polimi.ingsw.mesos.view.GUI.Controllers.GameControllerGUI;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TopRowController {

    private ClientController clientController;
    private GameControllerGUI gameController;
    private final List<CardController> cards = new ArrayList<>();
    private int currentRound = 0;

    @FXML private HBox topRowCards;

    public void setController(ClientController clientController, GameControllerGUI gameController){
        this.clientController = clientController;
        this.gameController = gameController;
    }


    public void updateUpper(ObservableList<CardDTO> upperRow) {

        if(this.currentRound==gameController.getCurrentRound()){
            //non è cambiato nulla le carte sono rimaste le stesse: ritorno
            if(cards.size()==upperRow.size()){
                refreshInteraction();
                return;
            }
            //altrimenti è stata scelta una carta che va eliminata
            for(int i = 0;i<cards.size();i++){
                if(!cards.get(i).getDTO().id.equals(upperRow.get(i).id)){
                    topRowCards.getChildren().remove(i);
                    cards.remove(i);
                    for(int j=i;j<cards.size();j++){
                        cards.get(j).setPosition(j);
                    }
                    refreshInteraction();
                    return;
                }
            }
        }
        this.currentRound=gameController.getCurrentRound();

        // svuota la riga
        topRowCards.getChildren().clear();
        cards.clear();

        int position = 0;
        // crea una carta per ogni CardDTO
        for (CardDTO dto : upperRow) {

            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cardView.fxml"));

                Parent cardNode = loader.load();

                CardController controller = loader.getController();
                controller.setController(clientController,gameController);

                controller.setCard(dto);
                controller.setPosition(position);

                controller.setInteractable(false);

                cards.add(controller);

                topRowCards.getChildren().add(cardNode);

                position++;

            } catch (IOException e) {
                System.out.println("ERRORE DI CARICAMENTO DELLE CARTE NEL TOPROWCONTROLLER: " + e.getMessage());
                e.printStackTrace();
            }
        }
        refreshInteraction();
    }

    //faccio il refresh delle carte per capire se possono essere selezionate o se devono essere disabilitate
    public void refreshInteraction() {

        boolean enabled = gameController.isMyTurn(GameState.RESOLVING_ACTIONS) && gameController.getIsUpper();

        for (CardController card : cards) {
            card.setInteractable(enabled);
        }
    }

}
