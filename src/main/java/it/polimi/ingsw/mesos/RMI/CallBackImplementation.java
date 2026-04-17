package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.RMI.ClientModel.ClientState;
import it.polimi.ingsw.mesos.RMI.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientController;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CallBackImplementation extends UnicastRemoteObject implements CallBack {

    private final ClientController cController;

    public CallBackImplementation(ClientController controller) throws RemoteException{
        this.cController=controller;
    }

    @Override
    public void updateClientState(ClientState state) throws RemoteException {
        cController.updateClientState(state);
    }

    @Override
    public void updateGame(GameDTO game) throws RemoteException{
        cController.updateGame(game);
        System.out.println("Lato client: update game");
    };

    //da rivedere forse meglio chiamare un metodo all'interno del controller
    @Override
    public void showMessage(String message){
        System.out.println("Messaggio inoltrato dal server: "+ message);
    }
}
