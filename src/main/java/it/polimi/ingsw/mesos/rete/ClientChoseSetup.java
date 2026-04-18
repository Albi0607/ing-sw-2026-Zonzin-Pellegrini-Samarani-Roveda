/*
package it.polimi.ingsw.mesos.rete;

import it.polimi.ingsw.mesos.RMI.Client_RMI;
import it.polimi.ingsw.mesos.socket.clientSocket;

public class ClientChoseSetup {
    public static Network createNetwork(String choice){
        return switch(choice){
            case "RMI" -> new Client_RMI();
            case "SOCKET" -> new clientSocket();
            default -> throw new IllegalArgumentException();
        };
    }

    public static View createView(String choice,ClientControllerBoth controller){
        return switch(choice){
            case "CLI" -> new CLIView(controller);
            case "GUI" -> new JavaFXView(controller);
            default -> throw new IllegalArgumentException();
        };

    }
}
*/

