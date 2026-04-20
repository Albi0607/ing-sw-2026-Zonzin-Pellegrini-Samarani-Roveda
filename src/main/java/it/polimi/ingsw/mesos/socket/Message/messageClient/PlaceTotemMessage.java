package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.socket.Message.Message;

public class PlaceTotemMessage extends Message {
    private final String nickname;
    private final char position;

    public PlaceTotemMessage(String nickname, char position) {
        this.nickname = nickname;
        this.position = position;
    }

    @Override
    public void executeServerSide(GameController controller) {
        controller.onPlaceTotem(nickname, position);
    }

    public String getNickname() { return nickname; }
    public char getPosition() { return position; }
}