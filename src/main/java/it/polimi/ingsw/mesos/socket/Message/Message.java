package it.polimi.ingsw.mesos.socket.Message;


import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.rete.ClientController;

import java.io.Serializable;

/**
 * Classe base per tutti i messaggi scambiati tra client e server.
 */
public abstract class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Metodo eseguito lato client quando il messaggio arriva dal server.
     */
    public void executeClientSide(ClientController controller) {
        // default: non fa nulla
    }

    /**
     * Metodo eseguito lato server quando il messaggio arriva dal client.
     */
    public void executeServerSide(GameController serverController) {
        // default: non fa nulla
    }
}
