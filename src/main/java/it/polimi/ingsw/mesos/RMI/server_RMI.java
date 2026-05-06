package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.multipleGames.ServerState;

import java.rmi.registry.*;

//fare documentazione di questa classe
public class server_RMI {

    //aggiunta della porta scelta a runtime
    public void start(ServerState serverState, int port) {

        try {
            System.setProperty("java.rmi.server.hostname", "10.173.124.90");
            RemoteMethods remoteMethods = new RemoteMethodsImplementation(serverState);
            Registry registry = LocateRegistry.createRegistry(port);
            registry.bind("remoteMethods", remoteMethods);
            System.out.println("Server RMI pronto e in attesa di connessioni");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //gestire la disconnessione e la terminazione della partita
    //gestire la connessione su porta a command line e nome del server non fisso tipo DNS
}
