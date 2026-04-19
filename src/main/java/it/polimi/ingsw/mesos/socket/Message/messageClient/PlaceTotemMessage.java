package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.socket.Message.Message_prova;

public class PlaceTotemMessage extends Message_prova {
    private final String nickname;
    private final char position;

    public PlaceTotemMessage(String nickname, char position) {
        this.nickname = nickname;
        this.position = position;
    }

    public String getNickname() { return nickname; }
    public char getPosition() { return position; }
}