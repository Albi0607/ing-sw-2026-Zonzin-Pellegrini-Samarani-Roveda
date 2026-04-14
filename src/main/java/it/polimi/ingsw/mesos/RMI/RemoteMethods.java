package it.polimi.ingsw.mesos.RMI;

import java.rmi.*;

public interface RemoteMethods extends Remote {

    boolean placeTotem(String nickname,char position) throws RemoteException;

    boolean takeCardFromUpper(String nickname,int position) throws RemoteException;

    boolean takeCardFromLower(String nickname,int position) throws RemoteException;

    boolean chosePlayers(int numPlayers) throws RemoteException;

    boolean choseNickname(String name) throws RemoteException;

    //forse non servono
    boolean choseView() throws RemoteException;

    boolean choseNetwork() throws RemoteException;
}
