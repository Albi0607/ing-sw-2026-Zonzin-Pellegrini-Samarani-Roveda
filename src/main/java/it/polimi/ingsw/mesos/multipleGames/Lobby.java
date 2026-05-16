package it.polimi.ingsw.mesos.multipleGames;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

//mettere anche la possibilità di creare game privati non visibili a tutti (non obbligatorio)
public class Lobby {

    private final ServerState serverState;
    private final Map<Integer, GameController> games = new ConcurrentHashMap<>();
    private int nextId;
    //aggiungere e togliere in maniera corretta i giocatori
    private final Set<String> viewers = ConcurrentHashMap.newKeySet();

    public Lobby(ServerState serverState) {
        this.serverState = serverState;
        nextId = 1;
    }

    public synchronized void addViewer(String virtualViewId) {
        VirtualView view = serverState.getConnection(virtualViewId);
        if (view == null) {
            removeViewer(virtualViewId);
            return;
        }
        viewers.add(virtualViewId);
        view.sendLobby(buildLobby());
    }

    //rimuovere view dalla lobby poiché game iniziato e non ha più bisogno di aggiornamenti della lobby
    public synchronized void removeViewer(String virtualViewId) {
        viewers.remove(virtualViewId);
    }

    //fare un check sul massimo numero di game online e creabili per non sovraccaricare il server?
    //fare in modo lato client che quando fa createNewGame nella lobby deve scegliere il numero di giocatori ed entrare
    //con l'id scelto
    public synchronized GameController createNewGame(String nickname, int expectedNumPlayers, Color color, String virtualViewId) throws Exception{
        //controlli per gestione di eventuali errori e cambiare client.State al client

        //try {
            VirtualView view = serverState.getConnection(virtualViewId);
            if (view == null) {
                return null;
            }
            int id = nextId++;
            GameController controller = new GameController(id);
            controller.setOnGameFinished(() -> removeFinishedGame(id));
            controller.setNumPlayers(expectedNumPlayers);
            games.put(id, controller);
            System.out.println("Nuovo game creato con questo id: " + id);
            games.get(id).addPlayer(nickname, color, view);
            removeViewer(virtualViewId);
            broadcast();
            return controller;
        /*} catch (Exception e) {
            System.out.println("Creazione del gioco non riuscita");
            return null;
        }
         */
    }

    //se da errore perché magari nel frattempo la partita è iniziata con altri giocatori magari gestire direttamente
    //l'errore da qua e restituire la lista di game per poter scegliere ulteriormente
    //Gestire i system out con un messaggio di ritorno al client
    public synchronized GameController joinGame(int id, String nickname, Color color, String virtualViewId) throws Exception{
        VirtualView view = serverState.getConnection(virtualViewId);
        if (view == null) {
            return null;
        }
        GameController controller = games.get(id);
        if (controller == null) {
            System.out.println("Nessun game trovato con questo id: " + id);
            return null;
        }
        if (controller.getGame() != null && !controller.hasRestorer()) {
            System.out.println("Partita già iniziata non è più possibile partecipare");
            return null;
        }
        // Partita non ancora iniziata OPPURE in attesa di ripristino
        //try {
            controller.addPlayer(nickname, color, view);
            removeViewer(virtualViewId);
            broadcast();
            return controller;
        /*} catch (Exception e) {
            System.out.println("Errore generico che non permette di entrare nella partita con id: " + id + ": " + e.getMessage());
            return null;
        }

         */
    }

    /**
     * Rimuove le partite "zombie": create ma senza più giocatori connessi
     * (es. tutti i client si sono disconnessi prima di iniziare).
     *
     * Una partita è considerata vuota se:
     *   - il game non è ancora stato creato (non è iniziata)
     *   - non ha giocatori connessi (nessuna VirtualView attiva)
     *
     * NON rimuove partite con il restorer attivo: quelle sono in attesa
     * di ripristino e devono restare in lobby.
     */
    public synchronized void removeEmptyGames() {
        games.entrySet().removeIf(entry -> {
            GameController controller = entry.getValue();

            // Non tocca partite in attesa di ripristino
            if (controller.hasRestorer()) return false;

            // Non tocca partite già avviate
            if (controller.getGame() != null) return false;

            // Rimuovi se non ci sono giocatori connessi
            boolean isEmpty = controller.getNumPlayersConnected() == 0;
            if (isEmpty) {
                System.out.println("[Lobby] Partita " + entry.getKey()
                        + " rimossa: nessun giocatore connesso.");
            }
            return isEmpty;
        });
    }

    /**
     * Rimuove una partita terminata normalmente dalla lobby.
     * Chiamato da GameController.endGame().
     * Pulisce anche i nickname dei giocatori da ServerState.
     */
    public synchronized void removeFinishedGame(int id) {
        GameController controller = games.get(id);
        if (controller == null) return;

        // Rimuovi i nickname dei giocatori da ServerState
        // così possono riconnettersi con lo stesso nome in una nuova partita
        if (controller.getGame() != null) {
            controller.getGame().getPlayers()
                    .forEach(p -> serverState.removePlayer(p.getNickname()));
        }

        games.remove(id);
        broadcast();
        System.out.println("[Lobby] Partita " + id + " terminata e rimossa dalla lobby.");
    }

    public synchronized void broadcast(){
        removeEmptyGames();
        List<LobbyInfoDTO> lobby = buildLobby();

        Iterator<String> iterator = viewers.iterator();
        while(iterator.hasNext()){
            String virtualViewId = iterator.next();
            VirtualView view = serverState.getConnection(virtualViewId);
            if(view==null){
                removeViewer(virtualViewId);
                continue;
            }
            try{
                view.sendLobby(lobby);
            } catch (Exception e) {
                removeViewer(virtualViewId);
            }
        }
    }

    private List<LobbyInfoDTO> buildLobby(){

        List<LobbyInfoDTO> lobbyGames = new ArrayList<>();
        for(Map.Entry<Integer, GameController> game: games.entrySet()){
            GameController controller = game.getValue();
            LobbyInfoDTO dto = new LobbyInfoDTO();

            dto.id = game.getKey();
            dto.numPlayers=controller.getNumPlayersConnected();
            dto.maxNumPlayers=controller.getExpectedNumPlayers();
            dto.started=(controller.getGame()!=null);
            dto.takenColors = controller.getTakenColors();
            lobbyGames.add(dto);
        }
        return lobbyGames;
    }

    /**
     * Reinserisce in lobby una partita ripristinata da disco.
     * Chiamato da ServerState.initializeFromDisk().
     *
     * Aggiorna anche nextId per evitare collisioni con ID già usati.
     */
    public synchronized void restoreGame(int gameId, GameController controller) {
        games.put(gameId, controller);
        controller.setOnGameFinished(() -> removeFinishedGame(gameId));

        // Garantisce che i prossimi ID siano sempre maggiori di quelli già esistenti
        if (gameId >= nextId) {
            nextId = gameId + 1;
        }

        System.out.println("[Lobby] Partita " + gameId + " reinserita in lobby per il ripristino.");
        broadcast();
    }

    public boolean containView(String virtualViewId){
        return viewers.contains(virtualViewId);
    }


}
