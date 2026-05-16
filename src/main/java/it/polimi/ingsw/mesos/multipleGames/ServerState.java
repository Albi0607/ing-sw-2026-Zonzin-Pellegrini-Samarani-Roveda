package it.polimi.ingsw.mesos.multipleGames;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.persistence.GameMove;
import it.polimi.ingsw.mesos.persistence.GameRestorer;
import it.polimi.ingsw.mesos.persistence.MoveLogger;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//classe da mettere nelle classi di rete per capire in base al nickname ricevuto in che controller fare l'azione
public class ServerState {

    private final Lobby lobby;
    private final Map<String, VirtualView> connections;
    //permette di capire il giocatore a che controller è associato
    private final Map<String, GameController> playerToGame;
    //permette di vedere il nome di tutti i giocatori connessi per non avere ripetizioni
    private final Set<String> nicknames;
    //permette di separare i nickname connessi e quelli in attesa di riconnessione
    private final Map<String, Integer> pendingReconnect;

    public ServerState(){
        this.lobby = new Lobby(this);
        this.connections = new ConcurrentHashMap<>();
        this.playerToGame = new ConcurrentHashMap<>();
        this.nicknames  = ConcurrentHashMap.newKeySet();
        this.pendingReconnect = new ConcurrentHashMap<>();
    }

    public synchronized void getLobby(String nickname,VirtualView view){
        if (nickname == null || nickname.isEmpty()) {
            view.showMessage("Nickname non valido");
            return;
        }

        //gestione riconnessione
        if (pendingReconnect.containsKey(nickname)) {
            int gameId = pendingReconnect.remove(nickname);
            String virtualViewId = view.getId();
            connections.put(virtualViewId, view);
            nicknames.add(nickname);

            try{

                GameController controller = lobby.joinGame(gameId, nickname, null, virtualViewId);
                if (controller != null) {
                    playerToGame.put(nickname, controller);
                    System.out.println("[ServerState] Riconnessione di '" + nickname
                            + "' alla partita " + gameId);
                } else {
                    view.showMessage("Errore durante la riconnessione alla partita " + gameId);
                }
            }catch (Exception e){
                view.showMessage("Errore durante la riconnessione alla partita " + gameId + ": " + e.getMessage());
            }
            return;
        }

        if(isNicknameTaken(nickname)){
            view.showLoginError("Nickname già in uso");
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

    public synchronized void createNewGame(String nickname, int expectedNumPlayers, Color color, String virtualViewId){

        VirtualView view = connections.get(virtualViewId);
        if(view==null || !lobby.containView(virtualViewId)){
            return;
        }

        try{
            if(expectedNumPlayers<2 || expectedNumPlayers>5){
                // Usiamo showActionRejected invece di showMessage
                view.showActionRejected("Numero di giocatori non corretto");
                return;
            }

            GameController controller = lobby.createNewGame(nickname, expectedNumPlayers, color, virtualViewId);
            if(controller == null){
                view.showActionRejected("GameController non creato correttamente");
                return;
            }

            playerToGame.put(nickname, controller);

            view.showMessage("Partita creata correttamente");

        } catch (Exception e) {
            view.showActionRejected(e.getMessage());
        }
    }

    public synchronized void joinGame(String nickname, int id, Color color, String virtualViewId){
        VirtualView view = connections.get(virtualViewId);
        if(view==null || !lobby.containView(virtualViewId)){
            return;
        }

        try {
            GameController controller = lobby.joinGame(id, nickname, color, virtualViewId);
            if(controller == null){
                // Usiamo showActionRejected invece di showMessage
                view.showActionRejected("Partita non trovata");
                return;
            }

            playerToGame.put(nickname, controller);

        } catch (Exception e) {
            view.showActionRejected(e.getMessage());
        }
    }

    public synchronized void removePlayer(String nickname) {
        nicknames.remove(nickname);
        playerToGame.remove(nickname);
    }

    //per rimuovere connessione a tutto
    public void removeConnection(String virtualViewId){
        connections.remove(virtualViewId);
        lobby.removeViewer(virtualViewId);
    }

    /**
     * Scansiona la directory corrente alla ricerca di file di log di partite
     * interrotte (pattern: mesos_game_{id}.log) e le registra come partite
     * ripristinabili.
     *
     * Viene chiamato una volta prima di avviare i server RMI e Socket
     *
     * Le partite ripristinabili appaiono in lobby come "started=true" —
     * i giocatori originali possono riconnettersi con lo stesso nickname
     * e trovare la partita al punto in cui era.
     */
    public synchronized void initializeFromDisk() {
        File dir = new File(".");
        File[] logFiles = dir.listFiles(
                (d, name) -> name.matches("mesos_game_\\d+\\.log")
        );

        if (logFiles == null || logFiles.length == 0) {
            System.out.println("[ServerState] Nessuna partita interrotta trovata su disco.");
            return;
        }

        Pattern pattern = Pattern.compile("mesos_game_(\\d+)\\.log");

        for (File logFile : logFiles) {
            Matcher matcher = pattern.matcher(logFile.getName());
            if (!matcher.matches()) continue;

            int gameId = Integer.parseInt(matcher.group(1));
            MoveLogger logger = new MoveLogger(logFile.getName());

            if (!logger.hasSavedGame()) continue;

            // Ricava i nickname che partecipavano alla partita dal log
            // (sono le mosse ADD_PLAYER)
            List<String> originalNicknames = logger.readAll().stream()
                    .filter(m -> m.type == GameMove.MoveType.ADD_PLAYER)
                    .map(m -> m.nickname)
                    .toList();

            if (originalNicknames.isEmpty()) continue;

            // Crea un GameController con lo stesso ID (usa lo stesso log file)
            GameController controller = new GameController(gameId);

            // Registra il restorer nel controller —
            // partirà non appena tutti i nickname originali si riconnetteranno
            GameRestorer restorer = new GameRestorer(logger);
            controller.setRestorer(restorer);

            // Registra i nickname come "attesi" così non possono essere usati
            // da altri giocatori di altre partite
            for (String nick : originalNicknames) {
                pendingReconnect.put(nick, gameId);
            }

            // Aggiunge la partita alla lobby con l'ID originale
            // (così il nextId della Lobby non sovrascrive un ID già usato)
            lobby.restoreGame(gameId, controller);

            System.out.println("[ServerState] Partita " + gameId +
                    " ripristinabile. Giocatori attesi: " + originalNicknames);
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
