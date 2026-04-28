package it.polimi.ingsw.mesos.rete;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.rete.ClientModel.LobbyInfoDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//mettere anche la possibilità di creare game privati non visibili a tutti (non obbligatorio)
public class Lobby {

    private final Map<Integer, GameController> games;
    private int nextId;
    private final List<VirtualView> viewers = new ArrayList<>();

    public Lobby(){
        games=new ConcurrentHashMap<>();
        nextId=0;
    }

    //gestire nella view un aggiornamento dell'interfaccia per potere vedere se nel frattempo si sono creati altri game
    public synchronized List<LobbyInfoDTO> getGames(){
        List<LobbyInfoDTO> lobbyGames = new ArrayList<>();

        for(Map.Entry<Integer, GameController> game: games.entrySet()){
            GameController controller = game.getValue();

            LobbyInfoDTO dto = new LobbyInfoDTO();

            //modificare e/o mettere metodi get nel gameController per ottenere questi parametri
            dto.id = game.getKey();
            //dto.numPlayers=controller;
            dto.maxNumPlayers=controller.getExpectedNumPlayers();
            dto.started=(controller.getGame()!=null);
            lobbyGames.add(dto);
        }
        return lobbyGames;
    }

    //fare un check sul massimo numero di game online e creabili per non sovraccaricare il server?
    //fare in modo lato client che quando fa createNewGame nella lobby deve scegliere il numero di giocatori ed entrare
    //con l'id scelto
    public synchronized int createNewGame(int expectedNumPlayers){
        GameController controller = new GameController();
        controller.setNumPlayers(expectedNumPlayers);
        games.put(nextId,controller);
        System.out.println("Nuovo game creato con questo id: " + nextId);
        nextId++;
        return nextId-1;
    }

    //se da errore perché magari nel frattempo la partita è iniziata con altri giocatori magari gestire direttamente
    //l'errore da qua e restituire la lista di game per poter scegliere ulteriormente
    //Gestire i system out con un messaggio di ritorno al client
    public synchronized boolean joinGame(int id,String nickname,VirtualView view){
        if(games.get(id)==null){
            System.out.println("Nessun game trovato con questo id: " + id);
            return false;
        }
        else if(games.get(id).getGame()!=null){
            System.out.println("Partita già iniziata non è più possibile partecipare");
            return false;
        }
        else{
            try {
                games.get(id).addPlayer(nickname, view);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        //fare add da qua poiché in nessun caso voglio restituire il gameController al client

    }

    //metodo per rimuovere i games quando vengono creati e tutti i partecipanti escono quindi non ci sono più persone
    //connesse
    public synchronized void removeGame(int id){
        if(games.get(id)!=null&&games.get(id).getGame()==null) {
            games.remove(id);
        }
    }

    //metodo per rimuovere i games quando sono terminati
    public synchronized void removeFinishedGame(int id){
        if(games.get(id)!=null&&games.get(id).getGame()!=null) {
            games.remove(id);
        }
    }


    //mettere un metodo broadcast nel caso un utente entra dentro un game in modo che si veda che cambiano i partecipanti?
    //oppure mostrare quando una partita parte mentre sto scegliendo a che partita accedere così da limitare gli errori

}
