package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.socket.Message.Message;

public class TakeCardMessage extends Message {
    private final String nickname;
    private final int position;
    private final boolean isUpper;

    public TakeCardMessage(String nickname, int position, boolean isUpper) {
        this.nickname = nickname;
        this.position = position;
        this.isUpper = isUpper;
    }

    @Override
    public void executeServerSide(GameController controller) {
        controller.onTakeCard(nickname, position, isUpper);
    }

    public String getNickname() { return nickname; }
    public int getPosition() { return position; }
    public boolean isUpper() { return isUpper; }
}
