package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.controller.GameController;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class RemoteMethodsImplementation extends UnicastRemoteObject implements RemoteMethods {

    GameController controller;

    public RemoteMethodsImplementation() throws RemoteException{
        controller = new GameController();
    };

    public boolean placeTotem(String nickname,char position) throws RemoteException{
        try{
            controller.onPlaceTotem(nickname,position);
        }
        catch(Exception e){
            return false;
        }

        return true;
    };

    public boolean takeCardFromUpper(String nickname, int position) throws RemoteException{
        try {
            controller.onTakeCardFromUpper(nickname,position);
        }
        catch(Exception e) {
            return false;
        }
        return true;
    };

    public boolean takeCardFromLower(String nickname, int position) throws RemoteException{
        try {
            controller.onTakeCardFromLower(nickname,position);
        }
        catch(Exception e) {
            return false;
        }
        return true;
    };

    public boolean chosePlayers(int numPlayers) throws RemoteException{
        try{
            controller.setNumPlayers(numPlayers);
            }
        catch(Exception e) {
            return false;
        }
        return true;
    };

    //sceglie nickname e aggiunge player
    public boolean choseNickname(String name) throws RemoteException{
        try {
            controller.addPlayer(name);
        }
        catch(Exception e){
            return false;
        }

        return true;
    };

    //forse non serve qua è solo lato client
    public boolean choseView() throws RemoteException{

        return false;
    };

    //forse non serve qua perché se utilizzo questa classe allora ho già deciso che tipologia di rete utilizzare
    public boolean choseNetwork() throws RemoteException{

        return false;
    };
}
