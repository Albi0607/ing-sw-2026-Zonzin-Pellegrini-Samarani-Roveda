package it.polimi.ingsw.mesos.socket.Message;


import it.polimi.ingsw.mesos.rete.ClientController;

import java.io.Serializable;

/**
 * Classe base per tutti i messaggi scambiati tra client e server.
 */
public abstract class Message_prova implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Metodo eseguito lato client.
     * Ogni messaggio che arriva dal server al client
     * deve implementare questo metodo.
     */
    public void executeClientSide(ClientController controller) {
        // default: non fa nulla
    }

    /**
     * Metodo eseguito lato server.
     */
    public void executeServerSide(Object serverController) {
        // default: non fa nulla
    }
}
