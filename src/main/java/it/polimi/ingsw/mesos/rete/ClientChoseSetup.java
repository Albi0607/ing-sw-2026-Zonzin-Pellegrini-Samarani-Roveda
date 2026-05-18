
package it.polimi.ingsw.mesos.rete;

import it.polimi.ingsw.mesos.RMI.client_RMI;
import it.polimi.ingsw.mesos.socket.clientSocket;

public class ClientChoseSetup {
    public static Network createNetwork(String choice, String serverIp, int port) {
        try {
            return switch (choice) {
                case "RMI" -> new client_RMI(serverIp, port);
                case "SOCKET" -> new clientSocket(serverIp, port);
                default -> throw new IllegalArgumentException();
            };
        } catch (Exception e) {
            throw new RuntimeException("Errore nella creazione della rete in ClientChoseSetup: " + choice, e);
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

