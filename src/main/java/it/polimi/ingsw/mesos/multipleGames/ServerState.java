package it.polimi.ingsw.mesos.multipleGames;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//classe da mettere nelle classi di rete per capire in base al nickname ricevuto in che controller fare l'azione
public class ServerState {

    private final Lobby lobby;
    private final Map<String, VirtualView> connections;
    //permette di capire il giocatore a che controller è associato
    private final Map<String, GameController> playerToGame;
    //permette di vedere il nome di tutti i giocatori connessi per non avere ripetizioni
    private final Set<String> nicknames;

    //per eseguire i thread delle azioni di gioco da capire se usare e come e con che modifiche
    private final ExecutorService executor = Executors.newFixedThreadPool(100);

    public void execute(Runnable action){
        executor.submit(action);
    }

    public ServerState(){
        this.lobby = new Lobby(this);
        this.connections = new ConcurrentHashMap<>();
        this.playerToGame = new ConcurrentHashMap<>();
        this.nicknames  = ConcurrentHashMap.newKeySet();

    }

    public synchronized void getLobby(VirtualView view){
        String virtualViewId= view.getId();
        connections.put(virtualViewId,view);
        lobby.addViewer(virtualViewId);
    }

    public VirtualView getConnection(String virtualViewId){
        return connections.get(virtualViewId);
    }

    public synchronized boolean createNewGame(String nickname, int expectedNumPlayers, String virtualViewId){
        try{
            if(isNicknameTaken(nickname)||nickname.isEmpty()){
                return false;
            }
            if(expectedNumPlayers<2||expectedNumPlayers>5){
                return false;
            }
            VirtualView view = connections.get(virtualViewId);
            if(view==null||!lobby.containView(virtualViewId)){
                return false;
            }
            view.setNickname(nickname);
            GameController controller = lobby.createNewGame(nickname,expectedNumPlayers,virtualViewId);
            if(controller == null){
                return false;
            }
            nicknames.add(nickname);
            playerToGame.put(nickname,controller);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public synchronized boolean joinGame(String nickname, int id, String virtualViewId){
        try {
            if(isNicknameTaken(nickname)||nickname.isEmpty()){
                return false;
            }
            VirtualView view = connections.get(virtualViewId);
            if(view==null||!lobby.containView(virtualViewId)){
                return false;
            }
            view.setNickname(nickname);
            GameController controller = lobby.joinGame(id, nickname, virtualViewId);
            if(controller == null){
                return false;
            }
            nicknames.add(nickname);
            playerToGame.put(nickname, controller);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public GameController getController(String nickname){
        return playerToGame.get(nickname);
    }

    public synchronized boolean isNicknameTaken(String nickname){
        return nicknames.contains(nickname);
    }

    //gestire l'aggiornamento di questa classe da parte della rete dopo che l'interazione con la lobby è positiva

}
