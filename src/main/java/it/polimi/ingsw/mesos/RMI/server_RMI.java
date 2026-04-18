package it.polimi.ingsw.mesos.RMI;

import java.rmi.*;
import java.rmi.registry.*;

public class server_RMI {

    public static void main(String[] args) throws RemoteException, AlreadyBoundException{

        System.out.println("Instantiating remote object...");
        RemoteMethods remoteMethods = new RemoteMethodsImplementation();
        System.out.println("Launching new registry...");
        Registry registry = LocateRegistry.createRegistry(1099);
        System.out.println("Binding remote object to registry…");
        registry.bind("remoteMethods", remoteMethods);
                System.out.println("Waiting for invocations from clients...");
    }

    //gestire la disconnessione e la terminazione della partita
    //gestire la connessione su porta a command line e nome del server non fisso tipo DNS
}
