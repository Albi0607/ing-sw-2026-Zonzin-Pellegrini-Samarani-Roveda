package it.polimi.ingsw.mesos.RMI;

import com.sun.jdi.VirtualMachine;
import it.polimi.ingsw.mesos.RMI.ClientModel.ClientState;
import it.polimi.ingsw.mesos.RMI.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.rmi.RemoteException;

public class RMIVirtualView implements VirtualView {

    private String nickname;
    private CallBack clientCallBack;

    public RMIVirtualView(String nickname,CallBack clientCallBack){
        this.nickname=nickname;
        this.clientCallBack=clientCallBack;
    }

    @Override
    public void sendGame(GameDTO game) {
        try {
            clientCallBack.updateGame(game);
        } catch (RemoteException e) {
            System.out.println("Errore nel RMIVirtualView in sendGame");
        }
    }

    @Override
    public void sendClientState(ClientState state){
        try {
            clientCallBack.updateClientState(state);
        } catch (RemoteException e) {
            System.out.println("Errore nel RMIVirtualView in sendClientState");
        }

    }

    //capire come gestire questo message lato RMI
    @Override
    public void showMessage(String message) {
        try {
            clientCallBack.showMessage(message);
        } catch (RemoteException e) {
            System.out.println("Errore nel RMIVirtualView in showMessage");
        }

    }

    @Override
    public String getNickname() {
        return this.nickname;
    }
}
