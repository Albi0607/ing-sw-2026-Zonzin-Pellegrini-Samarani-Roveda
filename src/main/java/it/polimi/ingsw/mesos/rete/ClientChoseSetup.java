
package it.polimi.ingsw.mesos.rete;

import it.polimi.ingsw.mesos.RMI.client_RMI;
import it.polimi.ingsw.mesos.socket.clientSocket;

public class ClientChoseSetup {
    public static Network createNetwork(String choice) {
        try {
            return switch (choice) {
                case "RMI" -> new client_RMI();
                //case "SOCKET" -> new clientSocket();
                default -> throw new IllegalArgumentException();
            };
        } catch (Exception e) {
            throw new RuntimeException("Errore nella creazione della rete in ClietnChoseSetup: " + choice, e);
        }
    }
/*
    public static View createView(String choice,ClientControllerBoth controller){
        return switch(choice){
            case "CLI" -> new CLIView(controller);
            case "GUI" -> new JavaFXView(controller);
            default -> throw new IllegalArgumentException();
        };

    }
    */
}

