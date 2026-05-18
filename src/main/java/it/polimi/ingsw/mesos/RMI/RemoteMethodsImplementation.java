package it.polimi.ingsw.mesos.RMI;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.multipleGames.ServerState;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.rmi.*;
import java.rmi.server.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Class that implements the associated remote interface and provides the methods invoked by the client on the server
 * side, enabling the client to perform actions in the game
 */
public class RemoteMethodsImplementation extends UnicastRemoteObject implements RemoteMethods {

    private final ServerState serverState;

    //da capire se è meglio avere una gestione centralizzata in serverState con tutto
    private final ExecutorService executor;

    // gestione keepAlive Message
    private final Map<String, Long> lastHeartbeat = new ConcurrentHashMap<>();


    public RemoteMethodsImplementation(ServerState serverState) throws RemoteException {
        this.serverState = serverState;
        this.executor = Executors.newCachedThreadPool();
        startWatchdog();
    }


    /**
     * Method that allows the client to place the totem on the OfferTile
     * @param nickname name chosen by the client
     * @param position position selected on the OfferTile
     * @throws RemoteException if there are network errors during the method invocation
     */
    @Override
    public void placeTotem(String nickname, char position) throws RemoteException {
        executor.submit(()-> {
            //forse c'è bisogno di controllare che il controller non sia null e/o fare alcuni controlli per notificare la view
            GameController controller = serverState.getController(nickname);
            if(controller==null){
                return;
            }
            controller.onPlaceTotem(nickname, position);
        });
    }

    /**
     * Method that allows the player to draw a card from the upper or lower row
     * @param nickname name chosen by the client
     * @param position position indicating the selected card
     * @param isUpper if true, the card must be taken from the upper row; otherwise, from the lower row
     * @throws RemoteException if there are network errors during the method invocation
     */
    @Override
    public void takeCard(String nickname, int position, boolean isUpper) throws RemoteException {
        executor.submit(()-> {
            GameController controller = serverState.getController(nickname);
            if(controller==null){
                return;
            }
            controller.onTakeCard(nickname, position, isUpper);
        });
    }

    /**
     * Method that allows the client not to draw the extra card at the end of the turn if they possess the triggering
     * building
     * @param nickname name of the player performing the action
     * otherwise, false
     * @throws RemoteException if there are network errors during the method invocation
     */
    @Override
    public void skipExtraDraw(String nickname) throws RemoteException{
        executor.submit(()-> {
            GameController controller = serverState.getController(nickname);
            if(controller==null){
                return;
            }
            controller.onSkipExtraDraw(nickname);
        });
    }

    //metodi remoti da usare nella lobby

    //gestire questo metodo con l'utilizzo di una view con nome gia scelto univoco
    public String getLobby(String nickname,CallBack clientCallback) throws RemoteException{
        try {
            String clientIP = RemoteServer.getClientHost();
            System.out.println("New RMI connection/request from: " + clientIP + " (Nickname: " + nickname + ")");
        } catch (ServerNotActiveException e) {
            System.out.println("RMI request da IP sconosciuto (Nickname: " + nickname + ")");
        }
        VirtualView view = new RMIVirtualView(nickname, clientCallback);
        serverState.getLobby(nickname,view);
        return view.getId();
    }

    public void createNewGame(String nickname, int expectedNumPlayers, Color color, String virtualViewId) throws  RemoteException{
        executor.submit(()-> {
            //passo virtualViewId poiché utilizzo la stessa view creata in getLobby
            serverState.createNewGame(nickname, expectedNumPlayers, color, virtualViewId);
        });
    }

    public void joinGame(String nickname, int id, Color color, String virtualViewId) throws RemoteException{
        executor.submit(()-> {
            //passo virtualViewId poiché utilizzo la stessa view creata in getLobby
            serverState.joinGame(nickname, id, color, virtualViewId);
        });
    }




    @Override
    public void heartbeat(String nickname) throws RemoteException {
        lastHeartbeat.put(nickname, System.currentTimeMillis());
    }

    private void startWatchdog() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            lastHeartbeat.forEach((nickname, lastTime) -> {
                if (now - lastTime > 30_000) {
                    lastHeartbeat.remove(nickname);
                    System.out.println("[RMI Watchdog] Timeout per: " + nickname);
                    var controller = serverState.getController(nickname);
                    if (controller != null) {
                        controller.onPlayerDisconnected(nickname);
                    }
                    serverState.removePlayer(nickname);
                }
            });
        }, 5, 5, TimeUnit.SECONDS);
    }

}
