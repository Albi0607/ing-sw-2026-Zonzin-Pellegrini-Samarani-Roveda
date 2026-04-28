package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.rmi.*;
import java.rmi.server.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Class that implements the associated remote interface and provides the methods invoked by the client on the server
 * side, enabling the client to perform actions in the game
 */
public class RemoteMethodsImplementation extends UnicastRemoteObject implements RemoteMethods {

    //al posto di GameController passare tutta la lobby
    private final GameController controller;
    //da capire se usare o meno
    //private final Queue<Runnable> actions = new LinkedList<>();

    /**
     * Constructor of this class that enables remote method invocation through the use of the GameController
     * @param controller GameController on which the methods are invoked and which updates the associated model
     * @throws RemoteException if there are network errors during the method invocation
     */
    public RemoteMethodsImplementation(GameController controller) throws RemoteException {
        this.controller = controller;

        //da capire se usare o meno, forse molto utile per multipartite
        //la gestione del valore di ritorno ed eventuali errori va cambiata però
    /*
        new Thread(()-> {
            while (true) {
                Runnable action;

                synchronized (actions) {
                    while (actions.isEmpty()) {
                        try {
                            actions.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    action = actions.poll();
                }

                try {
                    action.run;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        */
    }

    /**
     *
     * Method that allows the client to register for a game session on the server side
     * @param nickname name chosen by the client
     * @param clientCallback object that allows the server to update and exchange messages with the client
     * @return true if the registration was successful; otherwise, false
     * @throws RemoteException if there are network errors during the method invocation
     */
    public boolean registerClient(String nickname, CallBack clientCallback) throws RemoteException {

        try {
            VirtualView view = new RMIVirtualView(nickname, clientCallback);
            controller.addPlayer(nickname, view);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Method that allows the client to place the totem on the OfferTile
     * @param nickname name chosen by the client
     * @param position position selected on the OfferTile
     * @return true if the action was performed successfully; otherwise, false
     * @throws RemoteException if there are network errors during the method invocation
     */
    @Override
    public boolean placeTotem(String nickname, char position) throws RemoteException {

        return controller.onPlaceTotem(nickname, position);
    }

    /**
     * Method that allows the player to draw a card from the upper or lower row
     * @param nickname name chosen by the client
     * @param position position indicating the selected card
     * @param isUpper if true, the card must be taken from the upper row; otherwise, from the lower row
     * @return true if the action was performed successfully; otherwise, false
     * @throws RemoteException if there are network errors during the method invocation
     */
    @Override
    public boolean takeCard(String nickname, int position, boolean isUpper) throws RemoteException {

        return controller.onTakeCard(nickname, position, isUpper);
    }

    /**
     * Method that allows the client not to draw the extra card at the end of the turn if they possess the triggering
     * building
     * @param nickname name of the player performing the action
     * @return true if the client has chosen not to draw the extra card due to the effect of the triggering building;
     * otherwise, false
     * @throws RemoteException if there are network errors during the method invocation
     */
    @Override
    public boolean skipExtraDraw(String nickname) throws RemoteException{
        return controller.onSkipExtraDraw(nickname);
    }

    /**
     * Method that allows the player (to be used only if they are the first connected player) to choose the number of
     * players participating in the game
     * @param numPlayers number of players selected, ranging from 2 to 5
     * @return true if the players were chosen successfully; otherwise, false
     * @throws RemoteException if there are network errors during the method invocation
     */
    @Override
    public boolean choosePlayers(int numPlayers) throws RemoteException {
        try {
            controller.setNumPlayers(numPlayers);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
