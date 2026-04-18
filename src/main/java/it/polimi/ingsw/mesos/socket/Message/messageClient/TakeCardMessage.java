package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.socket.Message.Message_prova;

public class TakeCardMessage extends Message_prova {
    private final String nickname;
    private final int position;
    private final boolean isUpper;

    public TakeCardMessage(String nickname, int position, boolean isUpper) {
        this.nickname = nickname;
        this.position = position;
        this.isUpper = isUpper;
    }

    public String getNickname() { return nickname; }
    public int getPosition() { return position; }
    public boolean isUpper() { return isUpper; }
}
