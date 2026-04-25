package it.polimi.ingsw.mesos.RMI;

import java.rmi.*;

/**
 * Remote interface that defines the actions that can be invoked remotely by the client and subsequently handled by the
 * server
 */
public interface RemoteMethods extends Remote {

    /**
     *
     * Method that allows the client to register for a game session on the server side
     * @param nickname name chosen by the client
     * @param clientCallBack object that allows the server to update and exchange messages with the client
     * @return true if the registration was successful; otherwise, false
     * @throws RemoteException if there are network errors during the method invocation
     */
    boolean registerClient(String nickname, CallBack clientCallBack) throws RemoteException;

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
     * Method that allows the player (to be used only if they are the first connected player) to choose the number of
     * players participating in the game
     * @param numPlayers number of players selected, ranging from 2 to 5
     * @return true if the players were chosen successfully; otherwise, false
     * @throws RemoteException if there are network errors during the method invocation
     */
    boolean choosePlayers(int numPlayers) throws RemoteException;

}
