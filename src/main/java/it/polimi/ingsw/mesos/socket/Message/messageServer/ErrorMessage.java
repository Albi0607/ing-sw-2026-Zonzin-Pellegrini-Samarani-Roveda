package it.polimi.ingsw.mesos.socket.Message.messageServer;

import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.socket.Message.Message;

/**
 * Inviato dal server al singolo client che ha compiuto un'azione non valida.
 * Non viene fatto broadcast.
 */
public class ErrorMessage extends Message {

    private final String errorText;

    public ErrorMessage(String errorText) {
        this.errorText = errorText;
    }

    @Override
    public void executeClientSide(ClientController controller) {
        controller.showError(errorText);
    }

    public String getErrorText() { return errorText; }
}