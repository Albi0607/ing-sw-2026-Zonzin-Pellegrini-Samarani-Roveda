package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.controller.GameController;

import java.rmi.*;
import java.rmi.registry.*;

//fare documentazione di questa classe
public class server_RMI {

    public void start(GameController controller) {

        try {
            System.out.println("Instantiating remote object...");
            RemoteMethods remoteMethods = new RemoteMethodsImplementation(controller);
            System.out.println("Launching new registry...");
            Registry registry = LocateRegistry.createRegistry(1099);
            System.out.println("Binding remote object to registry…");
            registry.bind("remoteMethods", remoteMethods);
            System.out.println("Waiting for invocations from clients...");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //gestire la disconnessione e la terminazione della partita
        //gestire la connessione su porta a command line e nome del server non fisso tipo DNS
    }
}
