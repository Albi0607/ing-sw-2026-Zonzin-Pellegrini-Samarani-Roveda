package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.RMI.ClientModel.ClientState;
import it.polimi.ingsw.mesos.RMI.ClientModel.GameDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CallBack extends Remote {

    void updateGame(GameDTO game) throws RemoteException;

    void updateClientState(ClientState state) throws RemoteException;

    void showMessage(String message)throws RemoteException;

}
