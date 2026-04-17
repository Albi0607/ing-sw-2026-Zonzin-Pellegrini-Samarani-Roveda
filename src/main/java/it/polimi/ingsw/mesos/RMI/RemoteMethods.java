package it.polimi.ingsw.mesos.RMI;

import java.rmi.*;

public interface RemoteMethods extends Remote {

    boolean registerClient(String nickname, CallBack ClientCallBack) throws RemoteException;

    boolean placeTotem(String nickname,char position) throws RemoteException;

    boolean takeCard(String nickname,int position,boolean isUpper) throws RemoteException;

    boolean choosePlayers(int numPlayers) throws RemoteException;

}
