package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.rmi.*;
import java.util.List;

/**
 * Remote interface that defines the actions that can be invoked remotely by the client and subsequently handled by the
 * server
 */
public interface RemoteMethods extends Remote {

    /**
     * Method that allows the client to place the totem on the OfferTile
     * @param nickname name chosen by the client
     * @param position position selected on the OfferTile
     * @return true if the action was performed successfully; otherwise, false
     * @throws RemoteException if there are network errors during the method invocation
     */
    void placeTotem(String nickname,char position) throws RemoteException;

    /**
     * Method that allows the player to draw a card from the upper or lower row
     * @param nickname name chosen by the client
     * @param position position indicating the selected card
     * @param isUpper if true, the card must be taken from the upper row; otherwise, from the lower row
     * @return true if the action was performed successfully; otherwise, false
     * @throws RemoteException if there are network errors during the method invocation
     */
    void takeCard(String nickname,int position,boolean isUpper) throws RemoteException;

    /**
     * Method that allows the client not to draw the extra card at the end of the turn if they possess the triggering
     * building
     * @param nickname name of the player performing the action
     * @return true if the client has chosen not to draw the extra card due to the effect of the triggering building;
     * otherwise, false
     * @throws RemoteException if there are network errors during the method invocation
     */
    void skipExtraDraw(String nickname) throws RemoteException;


    //metodi da usare nella lobby
    String getLobby(String nickname,CallBack clientCallback) throws RemoteException;

    void createNewGame(String nickname, int expectedNumPlayers, Color color, String viewId) throws  RemoteException;

    void joinGame(String nickname, int id, Color color, String viewId) throws  RemoteException;
    void heartbeat(String nickname) throws RemoteException;

}
