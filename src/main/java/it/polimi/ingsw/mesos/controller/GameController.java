
package it.polimi.ingsw.mesos.controller;

import it.polimi.ingsw.mesos.RMI.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.character.CharacterCard;
import it.polimi.ingsw.mesos.model.card.character.Hunter;
import it.polimi.ingsw.mesos.model.enums.Color;
import it.polimi.ingsw.mesos.model.enums.EventType;
import it.polimi.ingsw.mesos.model.enums.GameState;
import it.polimi.ingsw.mesos.model.enums.TriggerType;
import it.polimi.ingsw.mesos.model.state.EventState;
import it.polimi.ingsw.mesos.model.state.GameStateLogic;
import it.polimi.ingsw.mesos.model.state.ResolvingState;
import it.polimi.ingsw.mesos.rete.VirtualView;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameController {

    //istanza del model
    private Game game;
    //capire come gestire le virtual view per poi usare il protocollo di rete adeguato
    private Map<String, VirtualView> players;
    //private View view;
    private List<String> pendingNicknames = new ArrayList<>();
    private int expectedNumPlayers=0;
    private int pendingPicks = 0;

    //il controller è associato ad un game unico o gestisce più game simultaneamente
    // in caso di funzionalità aggiuntiva multiplayer? --> in quel caso private Game dentro costruttore
    public GameController() {
        this.pendingNicknames = new ArrayList<>();
    }

    /**
     * Number of players required to start the game, setted by the first player.
     *
     * @param expectedNumPlayers the number of players
     * @throws IllegalArgumentException if maxPlayers is less than or equal to 1 or grater than 5
     */

    // il controller deve passare il numero di giocatori al modello alla creazione del game
    // questo check è già presente in game nel costruttore, forse più corretto lasciarlo qui e
    // toglierlo di là.
    // per quanto riguarda il num di giocatori io prenderei direttamente il numero dalla size della
    // lista di pendingNicknames dato che il check viene effettuato e sappiamo essere corretti

    //controllo che se 2 giocatori settano il numero di player solo il primo lo scelga veramente ed il secodno invece no

    public synchronized void setNumPlayers(int expectedNumPlayers) {
        if(this.expectedNumPlayers!=0){
            System.out.println("Numero di player gia settato da un altro giocatore");
            return;
        }
        if (expectedNumPlayers <= 1 || expectedNumPlayers > 5) {
            throw new IllegalArgumentException("The number of players is not valid!");
        }
        this.expectedNumPlayers = expectedNumPlayers;
    }

    /**
     * Adds a player nickname.
     * When the required number of players is reached, the game is automatically created.
     *
     * @param nickname the player's nickname
     * @throws IllegalStateException if the game has already been created
     * @throws IllegalStateException if the maximum number of players is already reached
     * @throws IllegalArgumentException if the nickname is already used or invalid
     */


    public synchronized void addPlayer(String nickname, VirtualView view) {
        if (game != null) {
            throw new IllegalStateException("Game has already been created");
        }

        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("Nickname cannot be null or empty");
        }

        if (pendingNicknames.size() >= expectedNumPlayers) {
            throw new IllegalStateException("Maximum number of players reached");
        }

        // check pendingNicknames
        if (pendingNicknames.contains(nickname)) {
            throw new IllegalArgumentException("Nickname already in use");
        }
        // if the current nickname pass all the previous check --> it can be inserts in the list
        pendingNicknames.add(nickname);
        players.put(nickname,view);

        // Automatically create the game when all players are added
        if (pendingNicknames.size() == expectedNumPlayers) {
            createGame();
        }
    }

    /**
     * Creates the game using the collected nicknames.
     * Converts nicknamee into Player objects.
     */

    private void createGame() {
        List<Player> players = new ArrayList<>();
        Color[] availableColors = Color.values();

        for (int i = 0; i < pendingNicknames.size(); i++) {
            Color color = availableColors[i];
            players.add(new Player(pendingNicknames.get(i), color));
        }

        this.game = new Game(players);

        // Clear temporary data after game creation
        pendingNicknames.clear();
        //alternativa a clear se il controller è inteso come unico
        //e ci dovessero essere più di 5 player in attesa magari di
        //un'altra lobby
        //for (int i = 0; i < expectedNumPLayer; i++) {
        //  pendingNicknames.remove(i);
        // }
    }

    /**
     * Starts the game.
     */

    public void startGame() {
        if (game == null) {
            throw new IllegalStateException("Game not created");
        }
        game.startGame();
    }

    /**
     * Ends the latest game.
     */

    public void endGame() {
        // capire cosa mettere qui
        //  view.notifyGameEnded(game.getWinner());
    }

    /**
     * Updates the game board.
     */
    // per come ho scritto la logica sottostante da capire se è necessario questo metodo

    public void updateBoard() {
    }

    // AZIONI DEL GIOCATORE
    // Queste verranno chiamate dal layer di rete una volta implementato.
    // Per ora possono essere chiamate direttamente nei test.

    /**
     * Player place totem on the offer tile.
     * Validates the state of the game and the fact that the tile exists and it is free.
     *
     * @param nickname player placing the totem
     * @param tileId tile the totem will be positioned
     **/

    public void onPlaceTotem(String nickname, char tileId) {
        requireState(GameState.PLACING_TOTEMS);

        Player player = requirePlayer(nickname);
        OfferTile tile = requireTile(tileId);

        game.placeTotemOnOffer(player, tile);

        broadcastUpdate();

    }


    public void onTakeCard(String nickname, int cardIndex, boolean isUpper) {

        requireState(GameState.RESOLVING_ACTIONS);
        Player player = requirePlayer(nickname);

        game.takeCard(player, cardIndex, isUpper);

        broadcastUpdate();

    }

    public void onSkipExtraDraw(String nickname) {
        requireState(GameState.RESOLVING_ACTIONS);
        Player player = requirePlayer(nickname);
        game.skipExtraDraw(player);
        broadcastUpdate();
    }

    //metodo che restituisce la partita all'ultimo aggiornamento e modifica fatta va completata in maniera intelligente
    public GameDTO lastGameUpdate(){
        GameDTO game = new GameDTO();
        return game;
    }

    // Logica privata di avanzamento round

    //protected perchè deve essere visibile dagli altri ma non utilizzabile dal player nell'app
    //metodo che dicevamo aggiornare tutte le altre view
    protected void broadcastUpdate() {
        // TODO: inviare lo stato aggiornato a tutti i client (rete/client)
    }

    // Metodi helper — rendono i metodi pubblici leggibili spostando il check
    // da effettuare su ogni azione esternamente ad ogni metodo

    private void requireGame() {
        if (game == null) {
            throw new IllegalStateException("Game has not been initialized yet.");
        }
    }

    private void requireState(GameState expected) {
        requireGame();
        GameState current = game.getCurrentState().getStateId();
        if (current != expected) {
            throw new IllegalStateException(
                    "Action not valid in state: " + current + " (expected: " + expected + ")."
            );
        }
    }


    private Player requirePlayer(String nickname) {
        return game.getPlayers().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player " + nickname + " not found!"));
    }

    private OfferTile requireTile(char tileId) {
        OfferTile tile = game.getBoard().getTile(tileId);
        if (tile == null) {
            throw new IllegalArgumentException("Tile " + tileId + " not found!");
        }
        return tile;
    }

    // manca un metodo che rispedisce la notifica dal controller al client

    // GETTERS

    public Game getGame() {
        return game;
    }

    public int getExpectedNumPlayers() {
        return expectedNumPlayers;
    }

    public int getPendingPicks() {
        return pendingPicks;
    }
}





