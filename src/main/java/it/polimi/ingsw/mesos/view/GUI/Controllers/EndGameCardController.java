package it.polimi.ingsw.mesos.view.GUI.Controllers;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservablePlayerModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;

public class EndGameCardController {
    @FXML private HBox HBoxContainer;
    @FXML private Label nicknameLabel;
    @FXML private Label ppLabel;
    @FXML private Label foodLabel_time;
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
        if (player == null) return;

        nicknameLabel.setText(player.getNickname());
        ppLabel.setText("PP: " + player.getPrestigePoints());
        foodLabel_time.setText("FOOD: " + player.getFood());

        // se non hai questo campo nel model, puoi rimuoverlo
        numPlayersLabel.setText("PLAYERS: " + numPlayers);
    }



    public void updateLeaderboard(GameResult result,Boolean myResult,int position){

        positionLabel.setText(String.valueOf(position));
        nicknameLabel.setText(result.getNickname());
        ppLabel.setText("PP: " + result.getPoints());
        foodLabel_time.setText("DATE: " + result.getDate());

        // se non hai questo campo nel model, puoi rimuoverlo
        numPlayersLabel.setText("PLAYERS: " + result.getNumPlayers());
        if(myResult){
            HBoxContainer.setStyle("-fx-background-color: yellow; -fx-background-radius: 12; -fx-border-radius: 12;  -fx-border-width: 1; -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0.2, 0, 2);");
        }


    }

}

