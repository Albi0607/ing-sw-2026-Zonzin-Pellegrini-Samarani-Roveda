package it.polimi.ingsw.mesos.controller;

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

import java.util.ArrayList;
import java.util.List;

public class GameController {

    //istanza del model
    private Game game;
    //private View view;
    private List<String> pendingNicknames = new ArrayList<>();
    private int expectedNumPlayers;
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
    public void setNumPlayers(int expectedNumPlayers) {
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
    public void addPlayer(String nickname) {
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

        if (!tile.isAvailable()) {
            throw new IllegalStateException("Tile '" + tileId + "' is already occupied.");
        }

        game.placeTotemOnOffer(player, tile);
        broadcastUpdate();

        // Se PlacingState ha piazzato tutte le tile e ha effettutato a RESOLVING_ACTIONS,
        // calcoliamo quante carte deve prendere il primo giocatore.
        if (game.getCurrentState().getStateId() == GameState.RESOLVING_ACTIONS) {
            prepareNextResolver();
        }
    }

    /**
     * Player chooses the card from upper row
     *
     * @param nickname player who have chosen
     * @param cardIndex index of the chosen card from upper row
     */
    public void onTakeCardFromUpper(String nickname, int cardIndex) {
        requireState(GameState.RESOLVING_ACTIONS);
        requirePicksRemaining();

        Player player = requirePlayer(nickname);
        Board board   = game.getBoard();

        if (cardIndex < 0 || cardIndex >= board.getUpperRow().size()) {
            throw new IndexOutOfBoundsException("Index not valid for upper row: " + cardIndex);
        }

        Card card = board.takeCardFromUpper(cardIndex);
        onAddCard(card, player);
        pendingPicks--;

        broadcastUpdate();
        replaceIfPicksDone(player);
    }

    /**
     * Player chooses the card from lower row
     *
     * @param nickname player who have chosen
     * @param cardIndex index of the chosen card from lower row
     */
    public void onTakeCardFromLower(String nickname, int cardIndex) {
        requireState(GameState.RESOLVING_ACTIONS);
        requirePicksRemaining();

        Player player = requirePlayer(nickname);
        Board board   = game.getBoard();

        if (cardIndex < 0 || cardIndex >= board.getLowerRow().size()) {
            throw new IndexOutOfBoundsException("Index not valid for lower row: " + cardIndex);
        }

        Card card = board.takeCardFromLower(cardIndex);
        onAddCard(card, player);
        pendingPicks--;

        broadcastUpdate();
        replaceIfPicksDone(player);
    }

    // Logica privata di avanzamento round

    /**
     * adds selected card to the tribe of the player and notifies building effects
     *
     * @param card the card chosen by the player
     * @param player the player who has chosen
     */
    private void onAddCard(Card card, Player player) {
        if (card instanceof BuildingCard building) {
            int cost = Math.max(0, building.getCost() - player.getTribe().getBuildingDiscount()); // per non andare in negativo
            if (!player.payFood(cost)) {
                throw new IllegalStateException(
                        "Not enough food to buy this building: " + cost + "food required."
                );
            }
            player.getTribe().addBuilding(building);
            game.notifyBuildingEffects(TriggerType.ON_PURCHASE);

        } else if (card instanceof CharacterCard character) {
            player.getTribe().addCharacter(character);
            if (character instanceof Hunter hunter) { // gestire il bonus di cibo
                hunter.onAddedToTribe(player);
            }
            game.notifyBuildingEffects(TriggerType.ON_CHARACTER_ADDED);
        }
    }

    /**
     * Called when playerr has no pending picks remaining.
     *
     * Replace player's tile on the TurnOrderTrack, empty the tile,
     * then check if there are other player to do card count,
     * otherwise goes to the EventState.
     *
     * @param player to replace its totem
     */
    private void replaceIfPicksDone(Player player) {
        if (pendingPicks > 0) { return;} // gestire la chiamata superflua subito dopo la pescata

        Board board = game.getBoard();

        OfferTile tile = board.getTiles().stream()
                .filter(t -> player.equals(t.getHost()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Player's tile not found"));

        player.addFood(tile.getFoodBonus());

        // Rimetti il totem nella TurnOrderTrack (applica bonus/malus di slot)
        int slot = board.getTurnOrderTrack().getFirstFreeSlot();
        board.getTurnOrderTrack().setPlayerAt(slot, player);

        // Libera la tessera per il round successivo
        tile.reset();

        broadcastUpdate();

        // passaggio ad EventState se tutti hanno risolto, altrimenti prossimo conto carte
        boolean allTilesEmpty = board.getTiles().stream().allMatch(OfferTile::isAvailable);
        if (allTilesEmpty) {
            game.changeState(new EventState());
        } else {
            prepareNextResolver();
        }
    }

    /**
     * search on the offer track the next player to count its card and then
     * replace its totem into the TurnOrderTrack calling replaceIfPicksDone
     */
    private void prepareNextResolver() {
        Board board = game.getBoard();

        OfferTile nextTile = board.getTiles().stream()
                .filter(t -> !t.isAvailable())
                .findFirst() // prende la prima delle tile occupate da sinistra
                .orElse(null);

        // se la prossima tessera è vuota esci (SISTEMARE, da chiarire se per x giocatori le tessere sono sempre occupate))
        if (nextTile == null) return;

        pendingPicks = nextTile.getUpperCount() + nextTile.getLowerCount();

        // Tessera senza pick (es. tessera A solo-cibo): completa subito
        if (pendingPicks == 0) {
            replaceIfPicksDone(nextTile.getHost());
        }
        //penso qui la View chiederà al giocatore di scegliere le carte.
    }

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

    private void requirePicksRemaining() {
        if (pendingPicks <= 0) {
            throw new IllegalStateException("No pick available for this turn.");
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



