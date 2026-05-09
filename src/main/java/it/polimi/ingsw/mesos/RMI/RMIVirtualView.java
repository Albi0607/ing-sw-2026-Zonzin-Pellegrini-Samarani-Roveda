package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

/**
 * Class that implements the associated interface and manages, according to the RMI protocol, server-side calls that
 * modify, update, and notify the client
 */
public class RMIVirtualView implements VirtualView {

    private final String nickname;
    private final CallBack clientCallBack;
    private final String id;

    /**
     * Constructor of the class that defines the associated client nickname and the object that allows the server,
     * in RMI, to invoke methods on the client side
     * @param nickname client nickname
     * @param clientCallBack object that allows the server to invoke methods on the client side
     */
    public RMIVirtualView(String nickname,CallBack clientCallBack){
        this.nickname=nickname;
        this.clientCallBack=clientCallBack;
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Method that sends the game updated to the latest modification to the ClientController so that it can be displayed
     * in the client view
     * @param game latest game update
     */
    @Override
    public void sendGame(GameDTO game) {
        try {
            clientCallBack.updateGame(game);
        } catch (RemoteException e) {
            System.out.println("Errore nel RMIVirtualView in sendGame");
        }
    }

    /**
     * Method that sends the current state of the player (essential before the game starts) so that the client can
     * determine what actions it must perform
     * @param state latest updated client state
     */
    @Override
    public void sendClientState(ClientState state){
        try {
            clientCallBack.updateClientState(state);
        } catch (RemoteException e) {
            System.out.println("Errore nel RMIVirtualView in sendClientState");
        }

    }

    //metodo per mandare la lobby in caso di modifiche
    public void sendLobby(List<LobbyInfoDTO> lobby){
        try {
            clientCallBack.showLobby(lobby);
        } catch (RemoteException e) {
            System.out.println("Errore nel RMIVirtualView in showMessage");
        }
    }

    /**
     * Method that allows the server to send error messages or general notifications to the client
     * @param message message to be displayed in the client view
     */
    @Override
    public void showMessage(String message) {
        try {
            clientCallBack.showMessage(message);
        } catch (RemoteException e) {
            System.out.println("Errore nel RMIVirtualView in showMessage");
        }

    }

    @Override
    public void showActionRejected(String reason) {
        try {
            clientCallBack.showActionRejected(reason);
        } catch (RemoteException e) {
            System.out.println("Errore nel RMIVirtualView in showActionRejected: " + e.getMessage());
        }
    }

    @Override
    public void showActionAccepted(String message) {
        try {
            clientCallBack.showActionAccepted(message);
        } catch (RemoteException e) {
            System.out.println("Errore nel RMIVirtualView in showActionAccepted: " + e.getMessage());
        }
    }

    /**
     * Method used to retrieve the client's name
     * @return client nickname
     */
    @Override
    public String getNickname() {
        return this.nickname;
    }

    @Override
    public String getId(){
        return id;
    }
}
