
package it.polimi.ingsw.mesos.rete;

import it.polimi.ingsw.mesos.RMI.client_RMI;
import it.polimi.ingsw.mesos.socket.clientSocket;

/**
 * Factory class responsible for creating the appropriate network implementation at runtime.
 *
 * The network type is selected according to the player's choice made through the
 * user interface (CLI or GUI), allowing the client to connect using different
 * communication technologies such as RMI or Socket.
 */

public class ClientChoseSetup {
    /**
     * Creates the network implementation selected by the player.
     *
     * Depending on the specified choice, the method instantiates and configures
     * the corresponding network communication layer.
     *
     * @param choice the selected network type
     * @param serverIp the server IP address
     * @param port the communication port
     * @param clientIp the client IP address
     * @return the configured Network instance
     * @throws RuntimeException if the selected network cannot be created
     */
    public static Network createNetwork(String choice, String serverIp, int port,String clientIp) {
        try {
            return switch (choice) {
                case "RMI" -> new client_RMI(serverIp, port,clientIp);
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

