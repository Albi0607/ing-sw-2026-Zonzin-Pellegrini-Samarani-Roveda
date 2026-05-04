package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

/**
 * Implementation of the associated remote interface that allows the updated game state or messages to be sent from the
 * server to the client
 */
public class CallBackImplementation extends UnicastRemoteObject implements CallBack {

    private final ClientController cController;

    /**
     * Constructor of the class that keeps a reference to the client controller in order to display updates or messages
     * @param controller client controller used to update the game state and display messages
     * @throws RemoteException if there are network errors during the method invocation
     */
    public CallBackImplementation(ClientController controller) throws RemoteException{
        this.cController=controller;
    }

    /**
     * Method that updates the client state to determine the next required action
     * @param state current state of the client
     * @throws RemoteException if there are network errors during the method invocation
     */
    @Override
    public void updateClientState(ClientState state) throws RemoteException {
        cController.updateClientState(state);
    }

    /**
     * Method that updates the client-side game state to reflect the latest updates and changes from the server
     * @param game updated to the latest change
     * @throws RemoteException if there are network errors during the method invocation
     */
    @Override
    public void updateGame(GameDTO game) throws RemoteException{
        cController.updateGame(game);
        System.out.println("Lato client: update game");
    };

    /**
     * Method that allows messages sent by the server to be displayed on the client side
     * @param message message to be displayed
     * @throws RemoteException if there are network errors during the method invocation
     */
    @Override
    public void showMessage(String message) throws RemoteException{
        cController.showMessage(message);
    }

    //metodo per mandare la lobby in caso di modifiche
    public void showLobby(List<LobbyInfoDTO> lobby) throws RemoteException{
        cController.showLobby(lobby);
    }
}
