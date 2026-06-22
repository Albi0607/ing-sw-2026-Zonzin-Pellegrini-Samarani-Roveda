package it.polimi.ingsw.mesos.view.GUI.Controllers.PreGame;

import it.polimi.ingsw.mesos.common.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.view.GUI.Controllers.UIEffects;
import it.polimi.ingsw.mesos.view.GUI.Core.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller for a single game card displayed in the lobby list.
 * Shows the game id, current status and player count for one available game,
 * and allows the player to navigate to the totem choice screen to join it.
 */
public class GameCardController {
    /** The lobby data currently displayed by this card. */
    private LobbyInfoDTO data;
    /** The scene manager used to navigate to the totem choice screen on join. */
    private SceneManager sceneManager;

    //FXML components
    @FXML private Label roomIdLabel;
    @FXML private Label statusLabel;
    @FXML private Label playersLabel;
    @FXML private Button joinButton;

    /**
     * Applies the click effect to the join button.
     */
    @FXML public void initialize() {
        // effetto click
        UIEffects.applyClickEffect(joinButton);
    }

    /**
     * Populates this card with the provided lobby data and updates all labels.
     * Can be called multiple times to refresh the card when the lobby state changes.
     * Disables the join button if the game is already full or has already started,
     * since in both cases only the original players can rejoin.
     *
     * @param dto the lobby data to display
     * @param sceneManager the scene manager used to navigate on join
     */
    public void setData(LobbyInfoDTO dto, SceneManager sceneManager) {
        this.data = dto;
        this.sceneManager = sceneManager;

        roomIdLabel.setText(String.valueOf(dto.id));
        if(dto.started){
            statusLabel.setText("PARTITA INIZIATA");
        }
        else{
            statusLabel.setText("IN ATTESA DI GIOCATORI");
        }
        playersLabel.setText(dto.numPlayers + "/" + dto.maxNumPlayers);

        //disabilito bottone di join
        boolean isFull = dto.numPlayers >= dto.maxNumPlayers;
        boolean isSuspended = dto.started;
        joinButton.setDisable(isFull || isSuspended);
    }

    /**
     * Handles the join button action.
     * Navigates to the totem choice screen in join mode for this specific game.
     */
    @FXML
    public void handleJoinGame() {
        sceneManager.loadTotemScene(data.id,0,data);
    }
}