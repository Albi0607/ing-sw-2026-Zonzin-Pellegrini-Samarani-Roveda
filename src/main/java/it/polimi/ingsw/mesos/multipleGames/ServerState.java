package it.polimi.ingsw.mesos.multipleGames;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


//classe da mettere nelle classi di rete per capire in base al nickname ricevuto in che controller fare l'azione
public class ServerState {

    private final Lobby lobby;
    private final Map<String, VirtualView> connections;
    //permette di capire il giocatore a che controller è associato
    private final Map<String, GameController> playerToGame;
    //permette di vedere il nome di tutti i giocatori connessi per non avere ripetizioni
    private final Set<String> nicknames;

    public ServerState(){
        this.lobby = new Lobby(this);
        this.connections = new ConcurrentHashMap<>();
        this.playerToGame = new ConcurrentHashMap<>();
        this.nicknames  = ConcurrentHashMap.newKeySet();

    }

    public synchronized void getLobby(String nickname,VirtualView view){
        if(isNicknameTaken(nickname)||nickname.isEmpty()){
            return;
        }
        String virtualViewId= view.getId();
        connections.put(virtualViewId,view);
        lobby.addViewer(virtualViewId);
        nicknames.add(nickname);
    }

    public VirtualView getConnection(String virtualViewId){
        return connections.get(virtualViewId);
    }

    public synchronized void createNewGame(String nickname, int expectedNumPlayers, String virtualViewId){
        try{
            VirtualView view = connections.get(virtualViewId);
            if(view==null||!lobby.containView(virtualViewId)){
                return;
            }
            if(expectedNumPlayers<2||expectedNumPlayers>5){
                view.showMessage("Partita non creata: Numero di giocatori non corretto");
                return;
            }

            GameController controller = lobby.createNewGame(nickname,expectedNumPlayers,virtualViewId);
            if(controller == null){
                view.showMessage("Partita non creata: GameController non creato correttamente");
                return;
            }
            playerToGame.put(nickname,controller);
            view.showMessage("Partita creata correttamente");

        } catch (Exception e) {
            VirtualView view = connections.get(virtualViewId);
            if(view==null||!lobby.containView(virtualViewId)){
                return;
            }
            view.showMessage("Partita non creata: Errore generico");

        }
    }

    public synchronized void joinGame(String nickname, int id, String virtualViewId){
        try {
            VirtualView view = connections.get(virtualViewId);
            if(view==null||!lobby.containView(virtualViewId)){
                return;
            }
            GameController controller = lobby.joinGame(id, nickname, virtualViewId);
            if(controller == null){
                view.showMessage("Non sei entrato nella partita: Partita non trovata");
                return;
            }
            playerToGame.put(nickname, controller);
        } catch (Exception e) {
            VirtualView view = connections.get(virtualViewId);
            if(view==null||!lobby.containView(virtualViewId)){
                return;
            }
            view.showMessage("Non sei entrato nella partita: Errore generico");
        }
    }

    //per rimuovere connessione a tutto
    public void removeConnection(String virtualViewId){
        connections.remove(virtualViewId);
        lobby.removeViewer(virtualViewId);
    }

    public GameController getController(String nickname){
        return playerToGame.get(nickname);
    }

    public synchronized boolean isNicknameTaken(String nickname){
        return nicknames.contains(nickname);
    }

    //gestire l'aggiornamento di questa classe da parte della rete dopo che l'interazione con la lobby è positiva

}
