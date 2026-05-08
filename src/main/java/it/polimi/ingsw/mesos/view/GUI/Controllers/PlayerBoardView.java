package it.polimi.ingsw.mesos.view.GUI.Controllers;

import it.polimi.ingsw.mesos.rete.ClientModel.PlayerDTO;
import javafx.scene.layout.VBox;

public class PlayerBoardView extends VBox {
    private String playerNickname;
    private boolean isLocalPlayer;

    public PlayerBoardView(String playerNickname, boolean isLocalPlayer) { }

    public void bindPlayerData(PlayerDTO player) { }
    private void renderTribeCards() { }
}
