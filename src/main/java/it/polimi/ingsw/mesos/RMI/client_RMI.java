package it.polimi.ingsw.mesos.RMI;
import it.polimi.ingsw.mesos.rete.ClientController;
import it.polimi.ingsw.mesos.rete.Network;
import it.polimi.ingsw.mesos.common.enums.Color;

import java.rmi.*;
import java.rmi.registry.*;

/**
 * Class responsible for establishing an RMI connection with the RMI server and handling all client-side remote method
 * invocations
 * */

//per attivare la lobby devo avere come attribuo il game controller al quale faccio registry
public class client_RMI implements Network {

    private final RemoteMethods remote;

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
     * Method that allows the client to place the totem on the OfferTile
     * @param nickname name of the player (this player) performing the action
     * @param position position selected on the OfferTile
     * @return true if the action was performed successfully; otherwise, false
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
     * Method that allows the player to draw a card from the upper or lower row
     * @param nickname name of the player (this player) performing the action
     * @param position position indicating the selected card
     * @param isUpper if true, the card must be taken from the upper row; otherwise, from the lower row
     * @return true if the action was performed successfully; otherwise, false
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


    //aggiunta metodi per la lobby

    public String getLobby(String nickname,ClientController controller){
        try {
            CallBack cb = new CallBackImplementation(controller);
            return remote.getLobby(nickname,cb);
        } catch (RemoteException e){
            return null;
        }
    }

    @Override
    public boolean createNewGame(String nickname, int expectedNumPlayers, Color color, String viewId){
        try {
            remote.createNewGame(nickname,expectedNumPlayers,color, viewId);
            return true;
        } catch (RemoteException e){
            return false;
        }
    }

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

