package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.rmi.*;
import java.rmi.server.*;

public class RemoteMethodsImplementation extends UnicastRemoteObject implements RemoteMethods {

    private final GameController controller;


    public RemoteMethodsImplementation(GameController controller) throws RemoteException {
        this.controller = controller;
    }


    //quando i giocatori accedono per la prima volta li metto nel registro cosi posso chiamare il callback e valuta se
    //è il primo utente connesso in modo da fargli scegliere il numero di giocatori della partita
    public boolean registerClient(String nickname, CallBack clientCallback) throws RemoteException {

        //nel try mancava il return true, la versione precedente ritornava a prescindere sempre false
        try {
            VirtualView view = new RMIVirtualView(nickname, clientCallback);
            controller.addPlayer(nickname, view);

            return true;

        } catch (Exception e) {
            System.out.println("Problema di registrazione del client: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean placeTotem(String nickname, char position) throws RemoteException {

        /*
        try {
            controller.onPlaceTotem(nickname, position);
            //GameDTO game = controller.lastGameUpdate();
            //broadcast(game);
        } catch (Exception e) {
            return false;
        }

        return true;
         */

        return controller.onPlaceTotem(nickname, position);

    }

    @Override
    public boolean takeCard(String nickname, int position, boolean isUpper) throws RemoteException {
        try {
            controller.onTakeCard(nickname, position, isUpper);
            //GameDTO game = controller.lastGameUpdate();
            //broadcast(game);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    @Override
    public boolean choosePlayers(int numPlayers) throws RemoteException {
        try {
            controller.setNumPlayers(numPlayers);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
