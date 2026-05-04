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

    //capire come gestire la porta diversa anche lato client, forse in RMI non c'é bisogno di sapere la porta
    public static void main(String[] args) {
        //aggiungo serverState e lobby per la gestione delle partite
        ServerState serverState = new ServerState();

        //porta di default;
        int port = 1099;

        if(args.length>0){
            port=Integer.parseInt(args[0]);
        }
        final int finalPort = port;

        //RMI THREAD
        new Thread(() -> {
            server_RMI rmi = new server_RMI();
            rmi.start(serverState,finalPort);
        }).start();

        //SOCKET THREAD
        new Thread(() -> {
            serverSocket socket = new serverSocket();
            // 1234 per il socket per non andare in conflitto con RMI (1099)
            socket.start(serverState, 1234 );
        }).start();

        System.out.println("Server avviati e pronti a connessioni");
    }
}
