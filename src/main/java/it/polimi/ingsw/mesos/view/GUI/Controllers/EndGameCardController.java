package it.polimi.ingsw.mesos.view.GUI.Controllers;

import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservablePlayerModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EndGameCardController {

    @FXML private Label nicknameLabel;
    @FXML private Label ppLabel;
    @FXML private Label foodLabel;
    @FXML private Label numPlayersLabel;
    @FXML private Label positionLabel;

    private ObservablePlayerModel player;
    private int numPlayers;

    public void setPlayer(int pos,ObservablePlayerModel observablePlayerModel, int numPlayers) {
        positionLabel.setText(String.valueOf(pos));
        this.player = observablePlayerModel;
        this.numPlayers = numPlayers;
        updateView();
    }

    private void updateView() {

        //gestire il caso di pareggio
        if (player == null) return;

        nicknameLabel.setText(player.getNickname());
        ppLabel.setText("PP: " + player.getPrestigePoints());
        foodLabel.setText("FOOD: " + player.getFood());

        // se non hai questo campo nel model, puoi rimuoverlo
        numPlayersLabel.setText("PLAYERS: " + numPlayers);
    }
}

