package it.polimi.ingsw.mesos.view.GUI.Controllers.EndGame;

import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.view.GUI.Controllers.UIEffects;
import it.polimi.ingsw.mesos.view.GUI.ObservableGame.ObservablePlayerModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Controller for a single result card displayed on the end game screen.
 * Used both for the current game results and for the all-time leaderboard entries.
 * The foodLabel_time field serves a dual purpose: it shows food in game results
 * and the date in leaderboard entries.
 */
public class EndGameCardController {

    // FXML components
    @FXML private HBox HBoxContainer;
    @FXML private Label nicknameLabel;
    @FXML private Label ppLabel;
    @FXML private Label foodLabel_time;
    @FXML private Label numPlayersLabel;
    @FXML private Label positionLabel;

    /**
     * Populates this card with the end game result of a single player.
     * Does nothing if the player model is null.
     *
     * @param pos the final position of this player in the current game
     * @param player the observable player model containing the final state
     * @param numPlayers the total number of players in the game
     */
    public void setPlayer(int pos,ObservablePlayerModel player, int numPlayers) {
        if (player == null) return;
        positionLabel.setText(String.valueOf(pos));
        nicknameLabel.setText(player.getNickname());
        ppLabel.setText("PP: " + player.getPrestigePoints());
        foodLabel_time.setText("FOOD: " + player.getFood());
        numPlayersLabel.setText("PLAYERS: " + numPlayers);
    }

    /**
     * Populates this card with an all-time leaderboard entry.
     * If this entry belongs to the local player, highlights the card background.
     *
     * @param result the leaderboard entry data to display
     * @param myResult true if this entry belongs to the local player
     * @param position the position of this entry in the leaderboard
     */
    public void updateLeaderboard(GameResult result,Boolean myResult,int position){

        positionLabel.setText(String.valueOf(position));
        nicknameLabel.setText(result.getNickname());
        ppLabel.setText("PP: " + result.getPoints());
        foodLabel_time.setText("DATE: " + result.getDate());
        numPlayersLabel.setText("PLAYERS: " + result.getNumPlayers());

    }

}

