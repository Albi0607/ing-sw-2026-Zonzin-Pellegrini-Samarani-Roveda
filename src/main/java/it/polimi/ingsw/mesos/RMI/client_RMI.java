package it.polimi.ingsw.mesos.RMI;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.Network;

import java.rmi.*;
import java.rmi.registry.*;

public class client_RMI implements Network {

    RemoteMethods remote;

    public client_RMI() throws RemoteException, NotBoundException{
        Registry registry = LocateRegistry.getRegistry();
        System.out.print("RMI registry bindings: ");
        String[] e = registry.list();
        for (int i = 0; i < e.length; i++) {
            System.out.println(e[i]);
        }

        String remoteObjectName = "remoteMethods";
        remote = (RemoteMethods) registry.lookup(remoteObjectName);
    }


    @Override
    public boolean register(String nickname, ClientController controller){
        try {
            CallBack cb = new CallBackImplementation(controller);
            return (remote.registerClient(nickname, cb));

        } catch(RemoteException e){
            return false;
        }
    }

    public boolean placeTotem(String nickname, char position){
        try {
            return (remote.placeTotem(nickname, position));

        } catch (RemoteException e) {
            return false;
        }
    }

    @Override
    public boolean takeCard(String nickname, int position, boolean isUpper){
        try {
            return (remote.takeCard(nickname, position, isUpper));
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override
    public boolean choosePlayers(int numPlayers){
        try {
            return (remote.choosePlayers(numPlayers));
        } catch (RemoteException e) {
            return false;
        }
    }

}

