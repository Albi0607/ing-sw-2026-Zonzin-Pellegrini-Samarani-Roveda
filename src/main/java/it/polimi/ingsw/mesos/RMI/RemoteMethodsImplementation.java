package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.RMI.ClientModel.ClientState;
import it.polimi.ingsw.mesos.RMI.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RemoteMethodsImplementation extends UnicastRemoteObject implements RemoteMethods {

    private final GameController controller;


    public RemoteMethodsImplementation() throws RemoteException {
        this.controller = new GameController();
    }


    //quando i giocatori accedono per la prima volta li metto nel registro cosi posso chiamare il callback e valuta se
    //è il primo utente connesso in modo da fargli scegliere il numero di giocatori della partita
    public boolean registerClient(String nickname, CallBack clientCallback) throws RemoteException {
        try {
            VirtualView view = new RMIVirtualView(nickname,clientCallback);
            controller.addPlayer(nickname,view);
        } catch (Exception e) {
            System.out.println("Problema di registrazione del client");
        }
        return false;
    }

    @Override
    public boolean placeTotem(String nickname, char position) throws RemoteException {

        try {
            controller.onPlaceTotem(nickname, position);
            //GameDTO game = controller.lastGameUpdate();
            //broadcast(game);
        } catch (Exception e) {
            return false;
        }

        return true;
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

    //deve essere fatto dal gameController a tutte le virtual view ed andare direttamente in ClientCallBack
    /*
    //aggiornamento della view di tutti i client
    private void broadcast(GameDTO game) {
        for (VirtualView view : clients.values()) {
            try {
                view.sendGame(game);
            } catch (Exception e) {
                System.out.println("Errore nell'aggiornamento del broadcast update al client");
            }
        }
    }
    */

}
