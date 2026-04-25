package it.polimi.ingsw.mesos.rete;

import it.polimi.ingsw.mesos.RMI.server_RMI;
import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.socket.serverSocket;

/**
 * Class used to start the game server, which accepts clients and manages game creation.
 * The class launches two threads: one to handle all requests from clients implementing the RMI network protocol, and
 * another to spawn a thread for handling all requests from clients using a socket-based network implementation
 */
public class ServerMain {

    public static void main(String[] args) {
        GameController controller = new GameController();

        //RMI THREAD
        new Thread(() -> {
            server_RMI rmi = new server_RMI();
            rmi.start(controller);
        }).start();

        /*
        //SOCKET THREAD
        new Thread(() -> {
            serverSocket socket = new serverSocket();
            socket.start(controller);
        }).start();
        */

        System.out.println("Server avviati e pronti a connessioni");
    }
}
