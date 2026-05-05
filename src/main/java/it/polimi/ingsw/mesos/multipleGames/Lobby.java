package it.polimi.ingsw.mesos.multipleGames;

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

    public Lobby(ServerState serverState){
        this.serverState=serverState;
        nextId=1;
    }

    public synchronized void addViewer(String virtualViewId){
        VirtualView view = serverState.getConnection(virtualViewId);
        if(view==null){
            removeViewer(virtualViewId);
            return;
        }
        viewers.add(virtualViewId);
        view.sendLobby(buildLobby());
    }

    //rimuovere view dalla lobby poiché game iniziato e non ha più bisogno di aggiornamenti della lobby
    public synchronized void removeViewer(String virtualViewId){
        viewers.remove(virtualViewId);
    }

    //fare un check sul massimo numero di game online e creabili per non sovraccaricare il server?
    //fare in modo lato client che quando fa createNewGame nella lobby deve scegliere il numero di giocatori ed entrare
    //con l'id scelto
    public synchronized GameController createNewGame(String nickname,int expectedNumPlayers,String virtualViewId){
        //controlli per gestione di eventuali errori e cambiare client.State al client
        try {
            VirtualView view = serverState.getConnection(virtualViewId);
            if(view==null){
                return null;
            }
            GameController controller = new GameController();
            controller.setNumPlayers(expectedNumPlayers);
            int id = nextId++;
            games.put(id, controller);
            System.out.println("Nuovo game creato con questo id: " + id);
            games.get(id).addPlayer(nickname, view);
            removeViewer(virtualViewId);
            broadcast();
            return controller;
        } catch (Exception e) {
            System.out.println("Creazione del gioco non riuscita");
            return null;
        }
    }

    //se da errore perché magari nel frattempo la partita è iniziata con altri giocatori magari gestire direttamente
    //l'errore da qua e restituire la lista di game per poter scegliere ulteriormente
    //Gestire i system out con un messaggio di ritorno al client
    public synchronized GameController joinGame(int id,String nickname,String virtualViewId){
        VirtualView view = serverState.getConnection(virtualViewId);
        if(view==null){
            return null;
        }
        GameController controller = games.get(id);
        if(controller==null){
            System.out.println("Nessun game trovato con questo id: " + id);
            return null;
        }
        else if(controller.getGame()!=null){
            System.out.println("Partita già iniziata non è più possibile partecipare");
            return null;
        }
        else{
            try {
                controller.addPlayer(nickname, view);
                removeViewer(virtualViewId);
                broadcast();
                return controller;

            } catch (Exception e) {
                System.out.println("Errore generico che non permette di entrare nella partita con id: "+id);
                return null;
            }
        }

    }

    //metodo per rimuovere i games quando vengono creati e tutti i partecipanti escono quindi non ci sono più persone
    //connesse
    public synchronized void removeEmptyGames(){
        games.entrySet().removeIf(e ->
                e.getValue() != null && e.getValue().getGame() == null
        );
    }

    //metodo per rimuovere i games quando sono terminati
    public synchronized void removeFinishedGame(int id){
        if(games.get(id)!=null&&games.get(id).getGame()!=null) {
            games.remove(id);
            broadcast();
        }
    }

    public synchronized void broadcast(){
        removeEmptyGames();
        List<LobbyInfoDTO> lobby = buildLobby();

        Iterator<String> iterator = viewers.iterator();
        while(iterator.hasNext()){
            String virtualViewId = iterator.next();
            VirtualView view = serverState.getConnection(virtualViewId);
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
            lobbyGames.add(dto);
        }
        return lobbyGames;
    }

    public boolean containView(String virtualViewId){
        return viewers.contains(virtualViewId);
    }


}
