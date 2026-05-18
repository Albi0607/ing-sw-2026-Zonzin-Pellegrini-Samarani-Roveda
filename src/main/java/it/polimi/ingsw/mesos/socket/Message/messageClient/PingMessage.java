package it.polimi.ingsw.mesos.socket.Message.messageClient;

import it.polimi.ingsw.mesos.socket.Message.Message;
import it.polimi.ingsw.mesos.controller.GameController;

// tipologia di Message che gestisce i keepAlive
public class PingMessage extends Message {
    private static final long serialVersionUID = 1L;

    @Override
    public void executeServerSide(GameController controller) {

    }
}
