package it.polimi.ingsw.mesos.RMI;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.Network;
import it.polimi.ingsw.mesos.common.enums.Color;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.rmi.*;
import java.rmi.registry.*;
import java.util.Collections;

/**
 * Class responsible for establishing an RMI connection with the RMI server and handling all client-side remote method
 * invocations
 */

public class client_RMI implements Network {

    /**
     * Remote reference to the server-side methods exposed through RMI.
     * This object is used to invoke remote operations on the server.
     */
    private final RemoteMethods remote;

    /**
     * Creates an RMI client and connects to the server registry to obtain a reference
     * to the remote object used for invoking server-side methods.
     * The client configures its RMI hostname and retrieves the remote reference from the
     * server registry.
     *
     * @param serverIp the IP address of the server
     * @param port the RMI registry port
     * @param clientIp the IP address of this client used for RMI callbacks
     * @throws RemoteException if a network communication error occurs
     * @throws NotBoundException if the remote object is not found in the registry
     */
    public client_RMI(String serverIp, int port,String clientIp) throws RemoteException, NotBoundException{

        try {
            System.setProperty("java.rmi.server.hostname", clientIp);
            System.out.println("✔ RMI Hostname del Client configurato su: " + clientIp);
        } catch (Exception e) {
            System.err.println("⚠ Impossibile settare l'hostname RMI sul client.");
        }

        Registry registry = LocateRegistry.getRegistry(serverIp, port);
        System.out.print("RMI registry bindings: ");
        String[] e = registry.list();
        for (int i = 0; i < e.length; i++) {
            System.out.println(e[i]);
        }

        String remoteObjectName = "remoteMethods";
        remote = (RemoteMethods) registry.lookup(remoteObjectName);
    }

    /**
     * Sends a request to the server to place the player's totem on the OfferTile.
     *
     * @param nickname the name of the player performing the action
     * @param position the selected position on the OfferTile
     * @return true if the request was successfully sent to the server; false otherwise
     */
    public boolean placeTotem(String nickname, char position){
        try {
            remote.placeTotem(nickname, position);
            return true;

        } catch (RemoteException e) {
            return false;
        }
    }

    /**
     * Sends a request to the server to draw a card from the upper or lower row.
     *
     * @param nickname the name of the player performing the action
     * @param position the selected card position
     * @param isUpper true if the card is drawn from the upper row, false for the lower row
     * @return true if the request was successfully sent to the server; false otherwise
     */
    @Override
    public boolean takeCard(String nickname, int position, boolean isUpper){
        try {
            remote.takeCard(nickname, position, isUpper);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    /**
     * Sends a request to the server to skip the extra card draw at the end of the turn.
     * This action is only valid if the player owns the required building card
     * that enables the extra draw.
     *
     * @param nickname the name of the player performing the action
     * @return true if the request was successfully sent to the server; false otherwise
     */
    @Override
    public boolean skipExtraDraw(String nickname){
        try{
            remote.skipExtraDraw(nickname);
            return true;
        }
        catch(RemoteException e){
            return false;
        }
    }

    /**
     * Connects the client to the lobby and retrieves the current lobby state from the server.
     * A remote callback is created to allow the server to send updates to the client.
     *
     * @param nickname the player's nickname
     * @param controller the client controller handling server updates
     * @return the VirtualView identifier associated with the client session
     */
    @Override
    public String getLobby(String nickname, ClientController controller) {
        try {
            CallBack cb = new CallBackImplementation(controller);

            KeepAliveRMI keepAlive = new KeepAliveRMI(remote, nickname);
            Thread t = new Thread(keepAlive);
            t.setDaemon(true);
            t.start();

            return remote.getLobby(nickname, cb);
        } catch (RemoteException e) {
            throw new RuntimeException("Errore di comunicazione RMI durante l'accesso alla lobby.", e);
        }
    }

    /**
     * Sends a request to the server to create a new game session in the lobby.
     * The player becomes the host and selects their own color for in-game representation.
     *
     * @param nickname the name of the player creating the game
     * @param expectedNumPlayers the number of players required for the game
     * @param color the player's chosen color
     * @param viewId the identifier of the client view connection
     * @return true if the request was successfully sent to the server; false otherwise
     */
    @Override
    public boolean createNewGame(String nickname, int expectedNumPlayers, Color color, String viewId){
        try {
            remote.createNewGame(nickname,expectedNumPlayers,color, viewId);
            return true;
        } catch (RemoteException e){
            return false;
        }
    }

    /**
     * Sends a request to join an existing game session in the lobby.
     * The player is added to the specified game if it exists and has available slots.
     *
     * @param nickname the name of the player joining the game
     * @param id the identifier of the game to join
     * @param color the player's chosen color
     * @param viewId the identifier of the client view connection
     * @return true if the request was successfully sent to the server; false otherwise
     */
    @Override
    public boolean joinGame(String nickname, int id, Color color, String viewId){
        try {
            remote.joinGame(nickname,id,color,viewId);
            return true;
        } catch (RemoteException e){
            return false;
        }
    }
}

