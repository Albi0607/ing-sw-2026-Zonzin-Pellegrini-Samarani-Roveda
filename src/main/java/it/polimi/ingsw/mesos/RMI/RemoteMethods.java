package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.rmi.*;
import java.util.List;

/**
 * Remote interface that defines the actions that can be invoked remotely by the client
 * and subsequently handled by the server
 */
public interface RemoteMethods extends Remote {

    /**
     * Requests the server to place the player's totem on the OfferTile.
     *
     * @param nickname name of the player performing the action
     * @param position selected position on the OfferTile
     * @throws RemoteException if a network error occurs during the remote call
     */
    void placeTotem(String nickname,char position) throws RemoteException;

    /**
     * Requests the server to draw a card from the upper or lower row.
     *
     * @param nickname name of the player performing the action
     * @param position index of the selected card
     * @param isUpper true if the card is drawn from the upper row, false otherwise
     * @throws RemoteException if a network error occurs during the remote call
     */
    void takeCard(String nickname,int position,boolean isUpper) throws RemoteException;

    /**
     * Requests to skip the extra card draw at the end of the turn.
     * This action is only valid if the player owns the corresponding building
     * that allows skipping the extra draw.
     *
     * @param nickname name of the player performing the action
     * @throws RemoteException if a network error occurs during the remote call
     */
    void skipExtraDraw(String nickname) throws RemoteException;


    /**
     * Requests the current lobby state from the server.
     * This method also registers the client's callback so the server can push
     * updates asynchronously to the client.
     *
     * @param nickname name of the player requesting the lobby
     * @param clientCallback remote callback used by the server to send updates
     * @return the identifier of the associated VirtualView
     * @throws RemoteException if a network error occurs during the remote call
     */
    String getLobby(String nickname,CallBack clientCallback) throws RemoteException;

    /**
     * Creates a new game session in the lobby.
     * The player becomes the host of the new game.
     *
     * @param nickname name of the player creating the game
     * @param expectedNumPlayers number of players required for the game
     * @param color selected color for the player's totem
     * @param viewId identifier of the client's VirtualView
     * @throws RemoteException if a network error occurs during the remote call
     */
    void createNewGame(String nickname, int expectedNumPlayers, Color color, String viewId) throws  RemoteException;

    /**
     * Joins an existing game session in the lobby.
     *
     * @param nickname name of the player joining the game
     * @param id identifier of the game to join
     * @param color selected color for the player's totem
     * @param viewId identifier of the client's VirtualView
     * @throws RemoteException if a network error occurs during the remote call
     */
    void joinGame(String nickname, int id, Color color, String viewId) throws  RemoteException;

    /**
     * Sends a heartbeat signal to the server to indicate that the client is
     * still active and connected.
     *
     * <p>This method is called periodically by {@link KeepAliveRMI} at a fixed
     * interval. The server uses incoming heartbeats to detect disconnected or
     * unresponsive clients: if no heartbeat is received within the expected
     * window, the server may treat the client as disconnected and take
     * appropriate action (e.g. removing the player from the session).</p>
     *
     * @param nickname name of the player sending the heartbeat,
     *                 used to identify the client on the server side
     * @throws RemoteException if a network error occurs during the remote call
     */
    void heartbeat(String nickname) throws RemoteException;

}
