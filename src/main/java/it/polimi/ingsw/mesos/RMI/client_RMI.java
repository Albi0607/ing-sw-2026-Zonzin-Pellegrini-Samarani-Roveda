package it.polimi.ingsw.mesos.RMI;
import java.rmi.*;
import java.rmi.registry.*;
import java.util.*;
import javax.naming.*;

public class client_RMI {


    public static void main(String[] args)
            throws NamingException, RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry();
        System.out.print("RMI registry bindings: ");
        String[] e = registry.list();
        for (int i = 0; i < e.length; i++) {
            System.out.println(e[i]);
        }

        //va messo nel controller
        String remoteObjectName = "remoteMethods";
        RemoteMethods remoteMethods = (RemoteMethods) registry.lookup(remoteObjectName);

        //generare un thread per gestire la view e uno per gestire il controller probabilmente

    }


    // azioni client
        // - scegliere su quale offerTile posizionare il totem
        // - pescare una carta
        // - comprare un edificio
        // - scegliere il numero di giocatore iniziale
        // - inserire il nickname
        // - scegliere tipo di view
        // - scegliere tipo di protocollo di rete
    // update generale

}
