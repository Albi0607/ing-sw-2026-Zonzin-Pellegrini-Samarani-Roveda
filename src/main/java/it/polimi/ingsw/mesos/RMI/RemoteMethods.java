package it.polimi.ingsw.mesos.RMI;

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
    boolean placeTotem(String nickname,char position) throws RemoteException;

    /**
     * Method that allows the player to draw a card from the upper or lower row
     * @param nickname name chosen by the client
     * @param position position indicating the selected card
     * @param isUpper if true, the card must be taken from the upper row; otherwise, from the lower row
     * @return true if the action was performed successfully; otherwise, false
     * @throws RemoteException if there are network errors during the method invocation
     */
    boolean takeCard(String nickname,int position,boolean isUpper) throws RemoteException;

    /**
     * Method that allows the client not to draw the extra card at the end of the turn if they possess the triggering
     * building
     * @param nickname name of the player performing the action
     * @return true if the client has chosen not to draw the extra card due to the effect of the triggering building;
     * otherwise, false
     * @throws RemoteException if there are network errors during the method invocation
     */
    boolean skipExtraDraw(String nickname) throws RemoteException;


    //metodi da usare nella lobby
    String getLobby(CallBack clientCallback) throws RemoteException;

    boolean createNewGame(String nickname, int expectedNumPlayers, String viewId) throws  RemoteException;

    boolean joinGame(String nickname, int id, String viewId) throws  RemoteException;

}
