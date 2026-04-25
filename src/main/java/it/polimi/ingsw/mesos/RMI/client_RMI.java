package it.polimi.ingsw.mesos.RMI;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.Network;

import java.rmi.*;
import java.rmi.registry.*;

/**
 * Class responsible for establishing an RMI connection with the RMI server and handling all client-side remote method
 * invocations
 * */

public class client_RMI implements Network {

    RemoteMethods remote;

    /**
     * Constructor that initializes this class and connects to the server registry to obtain a reference to the remote
     * object, which allows invoking remote methods that implement client actions
     * @throws RemoteException if there are any network or connection errors
     * @throws NotBoundException if no entry is found in the registry or if there is an error in the registration of the
     * remote methods.
     * */
    public client_RMI() throws RemoteException, NotBoundException{
        Registry registry = LocateRegistry.getRegistry();
        System.out.print("RMI registry bindings: ");
        String[] e = registry.list();
        for (int i = 0; i < e.length; i++) {
            System.out.println(e[i]);
        }

        String remoteObjectName = "remoteMethods";
        remote = (RemoteMethods) registry.lookup(remoteObjectName);
    }

    /**
     * Method that allows the client to register for a game session on the server side
     * @param nickname name chosen by the client
     * @param controller ClientController used to create a server-side reference, allowing the server to send game
     * updates and other types of messages to the client.
     * @return true if the registration was successful; otherwise, false
     * */
    @Override
    public boolean register(String nickname, ClientController controller){
        try {
            CallBack cb = new CallBackImplementation(controller);
            return (remote.registerClient(nickname, cb));

        } catch(RemoteException e){
            return false;
        }
    }

    /**
     * Method that allows the client to place the totem on the OfferTile
     * @param nickname name of the player (this player) performing the action
     * @param position position selected on the OfferTile
     * @return true if the action was performed successfully; otherwise, false
     */
    public boolean placeTotem(String nickname, char position){
        try {
            return (remote.placeTotem(nickname, position));

        } catch (RemoteException e) {
            return false;
        }
    }

    /**
     * Method that allows the player to draw a card from the upper or lower row
     * @param nickname name of the player (this player) performing the action
     * @param position position indicating the selected card
     * @param isUpper if true, the card must be taken from the upper row; otherwise, from the lower row
     * @return true if the action was performed successfully; otherwise, false
     */
    @Override
    public boolean takeCard(String nickname, int position, boolean isUpper){
        try {
            return (remote.takeCard(nickname, position, isUpper));
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override
    public boolean skipExtraDraw(String nickname){
        try{
            return remote.skipExtraDraw(nickname);
        }
        catch(RemoteException e){
            return false;
        }
    }

    /**
     * Method that allows the player (to be used only if they are the first connected player) to choose the number of
     * players participating in the game
     * @param numPlayers number of players selected, ranging from 2 to 5
     * @return true if the players were chosen successfully; otherwise, false
     */
    @Override
    public boolean choosePlayers(int numPlayers){
        try {
            return (remote.choosePlayers(numPlayers));
        } catch (RemoteException e) {
            return false;
        }
    }

}

