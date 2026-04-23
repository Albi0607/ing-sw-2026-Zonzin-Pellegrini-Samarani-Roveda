
package it.polimi.ingsw.mesos.controller;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.Tribe;
import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.character.CharacterCard;
import it.polimi.ingsw.mesos.model.card.event.EventCard;
import it.polimi.ingsw.mesos.model.enums.Color;
import it.polimi.ingsw.mesos.model.enums.GameState;
import it.polimi.ingsw.mesos.rete.ClientModel.*;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class GameController {

    //istanza del model
    private Game game;
    //capire come gestire le virtual view per poi usare il protocollo di rete adeguato
    private final Map<String, VirtualView> players = new ConcurrentHashMap<>();
    //private View view;
    private List<String> pendingNicknames = new ArrayList<>();
    private int expectedNumPlayers=0;
    public GameController() {
        this.pendingNicknames = new ArrayList<>();
    }

    /**
     * Number of players required to start the game, setted by the first player.
     *
     * @param expectedNumPlayers the number of players
     * @throws IllegalArgumentException if maxPlayers is less than or equal to 1 or grater than 5
     */
    //controllo che se 2 giocatori settano il numero di player solo il primo lo scelga veramente ed il secodno invece no

    public synchronized void setNumPlayers(int expectedNumPlayers) {
        if(this.expectedNumPlayers != 0){
            System.out.println("Numero di player gia settato da un altro giocatore");
            return;
        }
        if (expectedNumPlayers <= 1 || expectedNumPlayers > 5) {
            throw new IllegalArgumentException("The number of players is not valid!");
        }

        this.expectedNumPlayers = expectedNumPlayers;

        if (pendingNicknames.size() == this.expectedNumPlayers) {

            createGame();
            game.startGame();

            new Thread(() -> {
                for (VirtualView v : players.values()) {
                    v.sendClientState(ClientState.IN_GAME);
                }
                broadcastUpdate();
            }).start();

        } else {

            for (VirtualView view : players.values()) {
                view.sendClientState(ClientState.WAITING_CONNECTION);
                view.showMessage("Partita impostata a " + expectedNumPlayers + " giocatori. In attesa degli sfidanti...");
            }
        }
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

        if (expectedNumPlayers != 0 && pendingNicknames.size() >= expectedNumPlayers) {
            throw new IllegalStateException("Maximum number of players reached");
        }

        // check pendingNicknames
        if (pendingNicknames.contains(nickname)) {
            throw new IllegalArgumentException("Nickname already in use");
        }

        pendingNicknames.add(nickname);
        players.put(nickname,view);


        if (expectedNumPlayers == 0 && pendingNicknames.size() == 1) {
            // È il primissimo giocatore a entrare! Gli chiediamo di scegliere i posti.
            view.sendClientState(ClientState.CHOOSE_PLAYERS);
        }
        else if (expectedNumPlayers != 0 && pendingNicknames.size() == expectedNumPlayers) {
            // La stanza è piena. Creiamo la partita...
            createGame();

            game.startGame();

            // Usiamo un Thread per sbloccare i giocatori e mandare la prima plancia
            new Thread(() -> {
                for (VirtualView v : players.values()) {
                    v.sendClientState(ClientState.IN_GAME);
                }

                broadcastUpdate();
            }).start();
        }
        else {
            // È entrato un giocatore, ma la stanza non è ancora piena.
            view.sendClientState(ClientState.WAITING_CONNECTION);
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
        //broadcastUpdate();
    }

    /**
     * Starts the game.
     */

    public void startGame() {
        if (game == null) {
            throw new IllegalStateException("Game not created");
        }
        game.startGame();
        broadcastUpdate();
    }

    /**
     * Ends the latest game.
     */

    public void endGame() {
        // capire cosa mettere qui
        //  view.notifyGameEnded(game.getWinner());
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

    public boolean onPlaceTotem(String nickname, char tileId) {
        try {
            requireState(GameState.PLACING_TOTEMS);

            Player player = requirePlayer(nickname);
            OfferTile tile = requireTile(tileId);

            game.placeTotemOnOffer(player, tile);

            broadcastUpdate();

            return true;

        } catch (Exception e) {

            System.err.println("⚠️ Mossa rifiutata per " + nickname + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public void onTakeCard(String nickname, int cardIndex, boolean isUpper) {

        try {
            requireState(GameState.RESOLVING_ACTIONS);
            Player player = requirePlayer(nickname);

            game.takeCard(player, cardIndex, isUpper);

            broadcastUpdate();

        } catch (IllegalArgumentException | IllegalStateException e) {

            VirtualView view = players.get(nickname);
            if (view != null) {
                view.showMessage("Mossa non valida: " + e.getMessage());
            }
        }

    }

    public void onSkipExtraDraw(String nickname) {
        requireState(GameState.RESOLVING_ACTIONS);
        Player player = requirePlayer(nickname);
        game.skipExtraDraw(player);
        broadcastUpdate();
    }

    //protected perchè deve essere visibile dagli altri ma non utilizzabile dal player nell'app
    //metodo che dicevamo aggiornare tutte le altre view
    //capire cosa fare quando cade la rete
    protected void broadcastUpdate() {
        if (game == null) return;
        GameDTO dto = buildLastGameDTO();
        for (VirtualView view : players.values()) {
            view.sendGame(dto);
        }

        game.clearLastResolvedEvents();
    }

    // metodo che ricostruisce lo stato attuale del gioco valido, per ogni aggiornamento
    private GameDTO buildLastGameDTO() {
        GameDTO dto = new GameDTO();
        dto.currentState         = game.getCurrentState().getStateId();
        dto.currentRound         = game.getCurrentRound();
        dto.isUpper =  game.isNextUpper();

        dto.lastResolvedEvents = new ArrayList<>(game.getLastResolvedEvents());

        if (game.getCurrentEra() != null) {
            dto.era = switch (game.getCurrentEra()) {
                case ERA_I   -> "I";
                case ERA_II  -> "II";
                case ERA_III -> "III";
            };
        } else {
            dto.era = "I";
        }

        dto.board = buildBoardDTO(game.getBoard());

        // Giocatore corrente: per ora il primo con un totem sulla board,
        // da sistemare per quando PlacingState/ResolvingState essendo che dovrebbe dipendere dallo stato della partita
        dto.currentPlayerNickname = game.getCurrentPlayerNickname();

        // Stato dei giocatori
        dto.players = game.getPlayers().stream()
                .map(p -> {
                    PlayerDTO pdto = new PlayerDTO();
                    pdto.nickname       = p.getNickname();
                    pdto.food           = p.getFood();
                    pdto.prestigePoints = p.getPrestigePoints();
                    pdto.color          = p.getColor();
                    pdto.tribe = buildTribeDTO(p.getTribe());
                    return pdto;
                })
                .collect(java.util.stream.Collectors.toList());

        return dto;
    }

    private BoardDTO buildBoardDTO(Board board) {
        BoardDTO dto = new BoardDTO();

        dto.upperRow = board.getUpperRow().stream()
                .map(this::buildCardDTO)
                .collect(java.util.stream.Collectors.toList());

        dto.lowerRow = board.getLowerRow().stream()
                .map(this::buildCardDTO)
                .collect(java.util.stream.Collectors.toList());

        dto.offerTiles = new ArrayList<>();
        for (OfferTile tile : board.getTiles()) { // Assumo tu abbia un getOfferTiles()
            OfferTileDTO tileDto = new OfferTileDTO();
            tileDto.id = String.valueOf(tile.getId()); // "A", "B", ecc...

            if (tile.getHost() != null) {
                tileDto.occupantNickname = tile.getHost().getNickname();
                tileDto.occupantColor = tile.getHost().getColor();
            }
            dto.offerTiles.add(tileDto);
        }

        dto.turnOrderSlots = new ArrayList<>();


        int[] modifiers = board.getTurnOrderTrack().getSlots();
        List<Player> positions = board.getTurnOrderTrack().getPositions();

        System.out.println("DEBUG DTO: Numero giocatori nella track: " +
                positions.stream().filter(Objects::nonNull).count());

        for (int i = 0; i < positions.size(); i++) {
            TurnOrderSlotDTO slotDto = new TurnOrderSlotDTO();
            Player p = positions.get(i);

            if (p != null) {
                slotDto.occupantNickname = p.getNickname();
                slotDto.occupantColor = p.getColor();
            }

            // Passiamo il numero intero così com'è!
            slotDto.modifier = (i < modifiers.length) ? modifiers[i] : 0;

            dto.turnOrderSlots.add(slotDto);
        }

        return dto;
    }

    private CardDTO buildCardDTO(Card c) {
        CardDTO dto = new CardDTO();
        dto.id = c.getId();
        return dto;
    }

    private TribeDTO buildTribeDTO(Tribe tribe) {
        TribeDTO dto = new TribeDTO();

        dto.characters = new ArrayList<>();

        for (CharacterCard c : tribe.getCharacters()) {
            CardDTO card = new CardDTO();
            card.id = c.getId();
            dto.characters.add(card);
        }

        dto.buildings = new ArrayList<>();
        for (BuildingCard b : tribe.getBuildings()) {
            CardDTO card = new CardDTO();
            card.id = b.getId();
            dto.buildings.add(card);
        }

        return dto;
    }

    //notifica il cambio state agli altri player
    protected void sendClientStateToAll(ClientState state) {
        for  (VirtualView view : players.values()) {
            view.sendClientState(state);
        }
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

}





