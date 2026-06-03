package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.multipleGames.ServerState;

import java.rmi.registry.*;

/**
 * RMI server initializer responsible for setting up the RMI registry
 * and binding the RemoteMethodsImplementation.
 *
 * This class exposes the server functionality to RMI clients by registering
 * the RemoteMethods interface implementation.
 */
public class server_RMI {

    /**
     * Starts the RMI server on the specified port and binds the remote methods implementation to the RMI registry.
     *
     * The method configures the server IP for RMI communication, creates a new
     * registry, and publishes the remote object so that clients can invoke server-side operations.
     *
     * @param serverState the global server state managing games and players
     * @param serverIp the IP address for the server
     * @param port the port on which the RMI registry will be created
     */
    public void start(ServerState serverState, String serverIp, int port) {

        try {
            System.setProperty("java.rmi.server.hostname", serverIp);
            RemoteMethods remoteMethods = new RemoteMethodsImplementation(serverState);
            Registry registry = LocateRegistry.createRegistry(port);
            registry.bind("remoteMethods", remoteMethods);
            System.out.println("Server RMI pronto e in attesa di connessioni");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
