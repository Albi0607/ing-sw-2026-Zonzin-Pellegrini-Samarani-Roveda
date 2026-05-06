package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.multipleGames.ServerState;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.rmi.*;
import java.rmi.server.*;


/**
 * Class that implements the associated remote interface and provides the methods invoked by the client on the server
 * side, enabling the client to perform actions in the game
 */
public class RemoteMethodsImplementation extends UnicastRemoteObject implements RemoteMethods {

    private final ServerState serverState;
    //da capire se usare o meno
    //private final Queue<Runnable> actions = new LinkedList<>();


    public RemoteMethodsImplementation(ServerState serverState) throws RemoteException {
        this.serverState = serverState;

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
     * Method that allows the client to place the totem on the OfferTile
     * @param nickname name chosen by the client
     * @param position position selected on the OfferTile
     * @return true if the action was performed successfully; otherwise, false
     * @throws RemoteException if there are network errors during the method invocation
     */
    @Override
    public boolean placeTotem(String nickname, char position) throws RemoteException {

        GameController controller = serverState.getController(nickname);
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

        GameController controller = serverState.getController(nickname);
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
        GameController controller = serverState.getController(nickname);
        return controller.onSkipExtraDraw(nickname);
    }

    //metodi remoti da usare nella lobby

    //gestire questo metodo con l'utilizzo di una view parziale senza nome
    public String getLobby(String nickname,CallBack clientCallback) throws RemoteException{
        if(serverState.isNicknameTaken(nickname)||nickname.isEmpty()){
            return null;
        }
        VirtualView view = new RMIVirtualView(nickname, clientCallback);
        serverState.getLobby(view);
        return view.getId();
    }

    public boolean createNewGame(String nickname, int expectedNumPlayers, String virtualViewId) throws  RemoteException{
        try {
            //passo virtualViewId poiché utilizzo la stessa view creata in getLobby
            return serverState.createNewGame(nickname,expectedNumPlayers,virtualViewId);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean joinGame(String nickname, int id, String virtualViewId) throws RemoteException{
        try {
            //passo virtualViewId poiché utilizzo la stessa view creata in getLobby
            return serverState.joinGame(nickname,id,virtualViewId);
        } catch (Exception e) {
            return false;
        }
    }

}
