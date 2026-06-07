
package it.polimi.ingsw.mesos.controller;

import it.polimi.ingsw.mesos.DB.DBManager;
import it.polimi.ingsw.mesos.DB.GameResult;
import it.polimi.ingsw.mesos.DB.GameResultDAO;
import it.polimi.ingsw.mesos.DB.LeaderboardService;
import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.Tribe;
import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.character.CharacterCard;
import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.model.state.FinishedState;
import it.polimi.ingsw.mesos.model.state.ResolvingState;
import it.polimi.ingsw.mesos.persistence.*;
import it.polimi.ingsw.mesos.rete.ClientModel.*;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * The GameController class manages the logic for a single game instance.
 * It acts as the mediator between the model (Game) and the network layer (VirtualViews).
 * It handles player connections, game state transitions, move logging for persistence,
 * and turn timeouts.
 */
public class GameController {

    private Game game;
    
    /** Map associating each player's nickname with their corresponding VirtualView. */
    private final Map<String, VirtualView> players = new ConcurrentHashMap<>();

    private final Map<String, Color> chosenColors = new ConcurrentHashMap<>();

    /** List of nicknames that have joined but are waiting for the game to start. */
    private final List<String> pendingNicknames;

    private int expectedNumPlayers = 0;

    private LeaderboardService leaderboardService;

    private MoveLogger moveLogger;

    private StateSerializer stateSerializer;

    private boolean replayMode = false;

    private GameRestorer restorer = null;

    /** Callback executed when the game has finished. */
    private Runnable onGameFinished;

    /** Nicknames of players who have disconnected and are eligible for reconnection. */
    private final java.util.Set<String> disconnectedPlayers = ConcurrentHashMap.newKeySet();

    private ScheduledExecutorService turnTimer = Executors.newSingleThreadScheduledExecutor();

    private ScheduledFuture<?> currentTurnTimeout = null;

    private static final long TURN_TIMEOUT_SEC = 60;

    private final int gameId;

    /**
     * Constructs a new GameController for a specific game ID.
     * Initializes the persistence components and the leaderboard service.
     *
     * @param gameId the unique ID of the game
     */
    public GameController(int gameId) {
        this.gameId = gameId;
        this.pendingNicknames = new ArrayList<>();
        this.moveLogger = new MoveLogger("mesos_game_" + gameId + ".log");
        this.stateSerializer = new StateSerializer(gameId);
        this.leaderboardService = new LeaderboardService(new GameResultDAO());
    }

    /**
     * Sets the number of players required to start the game.
     * This should be called by the first player joining the lobby.
     * If the number of players is already set, subsequent calls are ignored.
     * If the current number of connected players matches the newly set expected number,
     * the game starts immediately.
     *
     * @param expectedNumPlayers the number of players (must be between 2 and 5)
     * @throws IllegalArgumentException if expectedNumPlayers is not between 2 and 5
     */
    public synchronized void setNumPlayers(int expectedNumPlayers) {
        if(this.expectedNumPlayers != 0){
            System.out.println("Player count already set by another player.");
            return;
        }
        if (expectedNumPlayers <= 1 || expectedNumPlayers > 5) {
            throw new IllegalArgumentException("The number of players must be between 2 and 5!");
        }

        this.expectedNumPlayers = expectedNumPlayers;

        if (moveLogger != null && !replayMode) {
            moveLogger.append(GameMove.setNumPlayers(expectedNumPlayers));
        }

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
                view.sendClientState(ClientState.WAITING_PLAYERS);
                view.showMessage("Game set to " + expectedNumPlayers + " players. Waiting for opponents...");
            }
        }
    }

    /**
     * Adds a player to the game or handles their reconnection.
     *
     * This method handles three main scenarios:
     * 1. Replay mode: The controller is replaying moves from a log.
     * 2. Restoration: A player is reconnecting to a game that crashed or was closed,
     *    triggering a replay once all players have reconnected.
     * 3. Normal connection: A player is joining a new game instance.
     *
     * @param nickname    the player's nickname
     * @param chosenColor the color chosen by the player
     * @param view        the player's VirtualView for communication
     * @throws IllegalStateException    if the game has already started or the room is full
     * @throws IllegalArgumentException if the nickname is invalid, already in use, or the color is taken
     */
    public synchronized void addPlayer(String nickname, Color chosenColor, VirtualView view) {

        if (replayMode) {
            chosenColors.put(nickname, chosenColor);
            pendingNicknames.add(nickname);
            if (expectedNumPlayers != 0 && pendingNicknames.size() == expectedNumPlayers) {
                createGame();
            }
            return;
        }

        // Scenario: Reconnecting to a game that needs restoration (game not yet created)
        if (restorer != null && game == null) {
            players.put(nickname, view);

            List<GameMove> moves = restorer.getMoveLogger().readAll();
            long expectedFromLog = moves.stream()
                    .filter(m -> m.type == GameMove.MoveType.ADD_PLAYER)
                    .count();

            if (players.size() == expectedFromLog) {
                restorer.restore(this, players);
                restorer = null;
            } else {
                view.sendClientState(ClientState.WAITING_PLAYERS);
                view.showMessage("Reconnecting... waiting for other players.");
            }
            return;
        }

        // Scenario: Reconnecting while restoration is already in progress
        if (restorer != null && game != null) {
            reconnectPlayer(nickname, view);

            boolean allReconnected = players.values().stream()
                    .noneMatch(v -> v instanceof DummyVirtualView);
            if (allReconnected) {
                broadcastUpdate();
                sendClientStateToAll(ClientState.IN_GAME);
            }
            return;
        }

        if (game != null) {
            throw new IllegalStateException("Game has already been created");
        }

        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("Nickname cannot be null or empty");
        }

        if (expectedNumPlayers != 0 && pendingNicknames.size() >= expectedNumPlayers) {
            throw new IllegalStateException("Maximum number of players reached");
        }

        if (pendingNicknames.contains(nickname)) {
            throw new IllegalArgumentException("Nickname already in use");
        }

        if (chosenColors.containsValue(chosenColor)) {
            throw new IllegalArgumentException("Color already chosen by another player");
        }

        pendingNicknames.add(nickname);
        players.put(nickname,view);
        chosenColors.put(nickname, chosenColor);
        if (moveLogger != null) moveLogger.append(GameMove.addPlayer(nickname, chosenColor));

        if (expectedNumPlayers != 0 && pendingNicknames.size() == expectedNumPlayers) {
            createGame();
            new Thread(() -> startGame()).start();
        }
        else {
            view.sendClientState(ClientState.WAITING_PLAYERS);
        }
    }

    /**
     * Creates the Game model instance using the collected nicknames and colors.
     * Initializes the player objects and sets up the game end callback.
     * Clears temporary storage used during the registration phase.
     */
    private void createGame() {
        List<Player> playersList = new ArrayList<>();
        /*
        Color[] availableColors = Color.values();

        for (int i = 0; i < pendingNicknames.size(); i++) {
            Color color = availableColors[i];
            players.add(new Player(pendingNicknames.get(i), color));
        }
         */
        for (String nick : pendingNicknames) {
            Color color = chosenColors.get(nick);
            playersList.add(new Player(nick, color));
        }

        this.game = new Game(playersList);

        this.game.setOnGameEnd(() -> {
            try {
                endGame();
            } catch (SQLException e) {
                System.err.println("Error while ending game: " + e.getMessage());
            }
        });

        pendingNicknames.clear();
        chosenColors.clear();
    }

    /**
     * Starts the game.
     * This involves saving the initial state of the decks (to allow deterministic replay),
     * initializing the model, and saving the resulting player order.
     * Finally, it notifies all clients that the game has started.
     *
     * @throws IllegalStateException if the game model has not been created yet
     */
    public void startGame() {
        if (game == null) throw new IllegalStateException("Game not created");

        // Save the FULL decks before the model consumes them for the Round 1 setup
        if (moveLogger != null && !replayMode) {
            stateSerializer.saveDeck(game.getBoard().getTribeDeck(), true);
            stateSerializer.saveDeck(game.getBoard().getBuildingDeck(), false);
        }

        game.startGame();

        if (moveLogger != null && !replayMode) {
            stateSerializer.savePlayerOrder(game.getPlayers());
            moveLogger.append(GameMove.startGame());
        }

        if(!replayMode){
            sendClientStateToAll(ClientState.IN_GAME);
        }

        broadcastUpdate();
    }

    /**
     * Ends the game and handles cleanup and result persistence.
     *
     * This method:
     * 1. Saves player results (prestige points) to the database.
     * 2. Retrieves the final leaderboard and notifies each client of their position.
     * 3. Sends the END_GAME state to all clients.
     * 4. Deletes persistence logs as they are no longer needed.
     * 5. Executes the onGameFinished callback and shuts down the turn timer.
     *
     * @throws SQLException if a database error occurs while saving results
     */
    public void endGame() throws SQLException {
        List<Player> gamePlayers = game.getPlayers();
        int numPlayers = gamePlayers.size();

        for (Player p : gamePlayers) {
            if (DBManager.isActive()) {
                leaderboardService.addResult(p.getNickname(), p.getPrestigePoints(), numPlayers);
            }
        }

        if (DBManager.isActive()) {
            List<GameResult> leaderboard = leaderboardService.getLeaderboard(numPlayers);

            for (Player p : gamePlayers) {
                String nickname = p.getNickname();
                int position = leaderboardService.getPosition(nickname, numPlayers);

                VirtualView view = players.get(nickname);
                if (view != null) {
                    view.showLeaderboard(leaderboard, position);
                }
            }
        }

        sendClientStateToAll(ClientState.END_GAME);

        if (moveLogger != null) moveLogger.delete();
        stateSerializer.delete();

        if (onGameFinished != null) {
            onGameFinished.run();
        }
        turnTimer.shutdownNow();
    }
    
    /**
     * Handles the player's action of placing a totem on an offer tile.
     * Validates the game state, ensures the player is connected, and checks if the tile is available.
     * If successful, the move is logged for persistence and all clients are updated.
     *
     * @param nickname the nickname of the player placing the totem
     * @param tileId   the identifier of the tile where the totem is placed
     * @return true if the action was successful, false otherwise
     */
    public boolean onPlaceTotem(String nickname, char tileId) {
        VirtualView view = players.get(nickname);
        if(view == null){
            return false;
        }
        try {
            requireState(GameState.PLACING_TOTEMS);
            cancelTurnTimer();

            Player player = requirePlayer(nickname);

            if (disconnectedPlayers.contains(nickname)) {
                return false;
            }
            OfferTile tile = requireTile(tileId);

            game.placeTotemOnOffer(player, tile);

            if (moveLogger != null) moveLogger.append(GameMove.placeTotem(nickname, tileId));

            if (!replayMode) view.showActionAccepted("Totem placed successfully!");

            broadcastUpdate();
            return true;

        } catch (Exception e) {
            if (!replayMode) view.showActionRejected("Failed to place totem: " + e.getMessage());
            System.err.println("⚠️ Action rejected for " + nickname + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Handles the player's action of taking a card from the board.
     * Validates the game state and ensures the player is connected.
     * If successful, the move is logged for persistence and all clients are updated.
     *
     * @param nickname  the nickname of the player taking the card
     * @param cardIndex the index of the card on the board
     * @param isUpper   true if the card is from the upper row, false otherwise
     * @return true if the action was successful, false otherwise
     */
    public boolean onTakeCard(String nickname, int cardIndex, boolean isUpper) {
        VirtualView view = players.get(nickname);
        if (view == null) {
            return false;
        }

        try {
            requireState(GameState.RESOLVING_ACTIONS);
            cancelTurnTimer();
            Player player = requirePlayer(nickname);

            if (disconnectedPlayers.contains(nickname)) {
                return false;
            }

            game.takeCard(player, cardIndex, isUpper);
            if (!replayMode) view.showActionAccepted("Card chosen successfully!");

            if (moveLogger != null) moveLogger.append(GameMove.takeCard(nickname, cardIndex, isUpper));

            broadcastUpdate();

            if (game.getCurrentState() instanceof FinishedState) {
                if (game.onGameEnd != null) {
                    game.onGameEnd.run();
                }
            }
            return true;

        } catch (IllegalArgumentException | IllegalStateException e) {
            if (!replayMode) view.showActionRejected("Failed to choose card: " + e.getMessage());
            return false;
        }
    }

    /**
     * Handles the player's decision to skip an extra draw phase.
     * Validates the game state and ensures the player is connected.
     * If successful, the move is logged for persistence and all clients are updated.
     *
     * @param nickname the nickname of the player skipping the action
     * @return true if the action was successful, false otherwise
     */
    public boolean onSkipExtraDraw(String nickname) {
        VirtualView view = players.get(nickname);
        if(view == null){
            return false;
        }
        try {
            requireState(GameState.RESOLVING_ACTIONS);
            cancelTurnTimer();
            Player player = requirePlayer(nickname);

            if (disconnectedPlayers.contains(nickname)) {
                return false;
            }

            game.skipExtraDraw(player);
            if (!replayMode) view.showActionAccepted("Action skipped successfully!");
            if (moveLogger != null) moveLogger.append(GameMove.skipExtraDraw(nickname));
            broadcastUpdate();

            if (game.getCurrentState() instanceof FinishedState) {
                if (game.onGameEnd != null) {
                    game.onGameEnd.run();
                }
            }
            return true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            if (!replayMode) view.showActionRejected("Failed to skip action: " + e.getMessage());
            return false;
        }
    }

    /**
     * Broadcasts the current game state to all connected players.
     * This method builds a GameDTO representing the visible state of the game
     * and sends it to each VirtualView. It also checks if the current player
     * is disconnected and should be skipped, and manages the turn timer.
     */
    public synchronized void broadcastUpdate() {
        if (game == null || replayMode) return;
        GameState state = game.getCurrentState().getStateId();
        if (state == GameState.SETUP) return;
        GameDTO dto = buildLastGameDTO();
        for (VirtualView view : players.values()) {
            try {
                view.sendGame(dto);
            } catch (Exception e) {
                System.err.println("Error: sending update to client");
            }
        }

        game.clearLastResolvedEvents();

        skipIfCurrentPlayerDisconnected();

        String current = game.getCurrentPlayerNickname();
        if (current != null && !disconnectedPlayers.contains(current)) {
            startTurnTimer(current);
        }
    }

    /**
     * Constructs a Data Transfer Object (DTO) representing the current game state.
     * This DTO is sent to clients to update their local views.
     *
     * @return a populated GameDTO instance
     */
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
        dto.currentPlayerNickname = game.getCurrentPlayerNickname();

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

        if (dto.currentState == GameState.RESOLVING_ACTIONS) {
            ResolvingState rs = (ResolvingState) game.getCurrentState();
            dto.isExtraDrawPhase = rs.isExtraPhase();
        } else {
            dto.isExtraDrawPhase = false;
        }

        if (dto.currentState == GameState.FINISHED) {
            dto.players.sort((p1, p2) -> {
                int prestigeCompare = Integer.compare(p2.prestigePoints, p1.prestigePoints);
                if (prestigeCompare == 0) {
                    return Integer.compare(p2.food, p1.food);
                }
                return prestigeCompare;
            });

            List<String> winnerNicknames = game.getWinner().stream()
                    .map(Player::getNickname)
                    .toList();

            dto.winners = dto.players.stream()
                    .filter(p -> winnerNicknames.contains(p.nickname))
                    .collect(java.util.stream.Collectors.toList());
        }

        return dto;
    }

    /**
     * Populates a BoardDTO based on the current board state.
     *
     * @param board the Board model instance
     * @return a populated BoardDTO
     */
    private BoardDTO buildBoardDTO(Board board) {
        BoardDTO dto = new BoardDTO();

        dto.upperRow = board.getUpperRow().stream()
                .map(this::buildCardDTO)
                .collect(java.util.stream.Collectors.toList());

        dto.lowerRow = board.getLowerRow().stream()
                .map(this::buildCardDTO)
                .collect(java.util.stream.Collectors.toList());

        dto.offerTiles = new ArrayList<>();
        for (OfferTile tile : board.getTiles()) {
            OfferTileDTO tileDto = new OfferTileDTO();
            tileDto.id = String.valueOf(tile.getId());

            if (tile.getHost() != null) {
                tileDto.occupantNickname = tile.getHost().getNickname();
                tileDto.occupantColor = tile.getHost().getColor();
            }
            dto.offerTiles.add(tileDto);
        }

        dto.turnOrderSlots = new ArrayList<>();

        int[] modifiers = board.getTurnOrderTrack().getSlots();
        List<Player> positions = board.getTurnOrderTrack().getPositions();

        for (int i = 0; i < positions.size(); i++) {
            TurnOrderSlotDTO slotDto = new TurnOrderSlotDTO();
            Player p = positions.get(i);

            if (p != null) {
                slotDto.occupantNickname = p.getNickname();
                slotDto.occupantColor = p.getColor();
            }

            slotDto.modifier = (i < modifiers.length) ? modifiers[i] : 0;
            dto.turnOrderSlots.add(slotDto);
        }

        return dto;
    }

    /**
     * Converts a Card model to its DTO representation.
     *
     * @param c the Card model instance
     * @return a populated CardDTO
     */
    private CardDTO buildCardDTO(Card c) {
        CardDTO dto = new CardDTO();
        dto.id = c.getId();
        if(c instanceof CharacterCard){
            dto.characterType = ((CharacterCard) c).getCharacterType();
        }
        return dto;
    }

    /**
     * Converts a Tribe model to its DTO representation.
     *
     * @param tribe the Tribe model instance
     * @return a populated TribeDTO
     */
    private TribeDTO buildTribeDTO(Tribe tribe) {
        TribeDTO dto = new TribeDTO();
        dto.characters = new ArrayList<>();

        for (CharacterCard c : tribe.getCharacters()) {
            CardDTO card = new CardDTO();
            card.id = c.getId();
            card.characterType = c.getCharacterType();
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

    /**
     * Sends a new ClientState to all connected players.
     *
     * @param state the new state to notify
     */
    public void sendClientStateToAll(ClientState state) {
        for  (VirtualView view : players.values()) {
            try {
                view.sendClientState(state);
            } catch (Exception e) {
                System.err.println("Error: sending clientState to clients");
            }
        }
    }

    /**
     * Ensures that the game has been initialized.
     *
     * @throws IllegalStateException if the game is null
     */
    private void requireGame() {
        if (game == null) {
            throw new IllegalStateException("Game has not been initialized yet.");
        }
    }

    /**
     * Validates that the game is in the expected state for an action.
     *
     * @param expected the GameState required for the action
     * @throws IllegalStateException if the game is in a different state
     */
    private void requireState(GameState expected) {
        requireGame();
        GameState current = game.getCurrentState().getStateId();
        if (current != expected) {
            throw new IllegalStateException(
                    "Action not valid in state: " + current + " (expected: " + expected + ")."
            );
        }
    }

    /**
     * Retrieves a Player instance by their nickname.
     *
     * @param nickname the player's nickname
     * @return the Player instance
     * @throws IllegalArgumentException if no player with the given nickname is found
     */
    private Player requirePlayer(String nickname) {
        return game.getPlayers().stream()
                .filter(p -> p.getNickname().equals(nickname))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player " + nickname + " not found!"));
    }

    /**
     * Retrieves an OfferTile instance by its ID.
     *
     * @param tileId the identifier of the tile
     * @return the OfferTile instance
     * @throws IllegalArgumentException if no tile with the given ID is found
     */
    private OfferTile requireTile(char tileId) {
        OfferTile tile = game.getBoard().getTile(tileId);
        if (tile == null) {
            throw new IllegalArgumentException("Tile " + tileId + " not found!");
        }
        return tile;
    }

    /**
     * Sets the replay mode for the controller.
     *
     * @param replayMode true to enable replay mode, false to disable
     */
    public void setReplayMode(boolean replayMode) {
        this.replayMode = replayMode;
    }

    /**
     * Sets the GameRestorer instance for state reconstruction.
     *
     * @param restorer the restorer to use
     */
    public void setRestorer(GameRestorer restorer) {
        this.restorer = restorer;
    }

    /**
     * Handles the reconnection of a player.
     * Updates their VirtualView, removes them from the disconnected set,
     * and notifies other players of their return.
     *
     * @param nickname the nickname of the reconnecting player
     * @param newView  the new VirtualView for communication
     * @throws IllegalArgumentException if the player is not found in the initial player list
     */
    public synchronized void reconnectPlayer(String nickname, VirtualView newView) {
        if (!players.containsKey(nickname)) {
            throw new IllegalArgumentException("Player not found: " + nickname);
        }
        players.put(nickname, newView);
        disconnectedPlayers.remove(nickname);

        System.out.println("[GameController] Player reconnected: " + nickname);

        for (Map.Entry<String, VirtualView> entry : players.entrySet()) {
            if (!entry.getKey().equals(nickname)) {
                try {
                    entry.getValue().showMessage(nickname + " has reconnected!");
                } catch (Exception ignored) {}
            }
        }

        newView.sendClientState(ClientState.IN_GAME);
        broadcastUpdate();
    }

    /**
     * Returns the move logger associated with this controller.
     *
     * @return the MoveLogger instance
     */
    public MoveLogger getMoveLogger() {
        return moveLogger;
    }

    /**
     * Returns the state serializer associated with this controller.
     *
     * @return the StateSerializer instance
     */
    public StateSerializer getStateSerializer() {
        return stateSerializer;
    }

    /**
     * Checks if the controller has an active restorer.
     *
     * @return true if a restorer is present, false otherwise
     */
    public boolean hasRestorer() {
        return restorer != null;
    }

    /**
     * Sets the callback to be executed when the game is finished.
     *
     * @param callback the callback runnable
     */
    public void setOnGameFinished(Runnable callback) {
        this.onGameFinished = callback;
    }

    /**
     * Returns the game model instance.
     *
     * @return the Game instance
     */
    public Game getGame() {
        return game;
    }

    /**
     * Returns the expected number of players for this game.
     *
     * @return the expected player count
     */
    public int getExpectedNumPlayers() {
        return expectedNumPlayers;
    }

    /**
     * Returns the number of players currently connected or registered.
     *
     * @return the number of players
     */
    public int getNumPlayersConnected(){
        if (game != null) {
            return game.getPlayers().size();
        }
        return pendingNicknames.size();
    }

    /**
     * Returns the leaderboard service.
     *
     * @return the service instance
     */
    public LeaderboardService getLeaderboardService() {
        return this.leaderboardService;
    }

    /**
     * Returns the list of colors already chosen by players.
     *
     * @return a list of Color enums
     */
    public List<Color> getTakenColors() {
        return new ArrayList<>(chosenColors.values());
    }

    /**
     * Handles the event of a player disconnecting.
     * Marks the player as disconnected, notifies others, and skips their turn
     * if they were the active player.
     *
     * @param nickname the nickname of the disconnected player
     */
    public synchronized void onPlayerDisconnected(String nickname) {
        if (disconnectedPlayers.contains(nickname)) return;
        disconnectedPlayers.add(nickname);

        System.out.println("[GameController] Player disconnected: " + nickname);

        for (Map.Entry<String, VirtualView> entry : players.entrySet()) {
            if (!entry.getKey().equals(nickname)) {
                try {
                    entry.getValue().showMessage(nickname + " has disconnected. Their turn will be skipped.");
                } catch (Exception ignored) {}
            }
        }

        if (game != null && nickname.equals(game.getCurrentPlayerNickname())) {
            skipDisconnectedTurn(nickname);
        }
    }

    /**
     * Advances the turn by skipping the current player if they are disconnected.
     * If all players are disconnected, the game ends.
     *
     * @param nickname the nickname of the disconnected player
     */
    private void skipDisconnectedTurn(String nickname) {
        try {
            long connectedCount = players.keySet().stream()
                    .filter(n -> !disconnectedPlayers.contains(n))
                    .count();

            if (connectedCount == 0) {
                System.out.println("[GameController] All players disconnected, ending game.");
                try { endGame(); } catch (Exception ignored) {}
                return;
            }

            game.skipCurrentPlayerTurn();
            broadcastUpdate();

        } catch (Exception e) {
            System.err.println("[GameController] Error skipping turn: " + e.getMessage());
        }
    }

    /**
     * Automatically skips the current player's turn if they are marked as disconnected.
     */
    private void skipIfCurrentPlayerDisconnected() {
        if (game == null) return;
        String current = game.getCurrentPlayerNickname();
        if (current != null && disconnectedPlayers.contains(current)) {
            System.out.println("[GameController] It is " + current + "'s turn but they are disconnected. Skipping automatically.");
            skipDisconnectedTurn(current);
        }
    }

    /**
     * Starts the turn timeout timer for a specific player.
     * If the player does not perform an action within the limit, they are treated as disconnected.
     *
     * @param nickname the nickname of the player whose turn is timed
     */
    public synchronized void startTurnTimer(String nickname) {
        cancelTurnTimer();

        currentTurnTimeout = turnTimer.schedule(() -> {
            System.out.println("[TurnTimer] Timeout for: " + nickname);
            onPlayerDisconnected(nickname);
        }, TURN_TIMEOUT_SEC, TimeUnit.SECONDS);
    }

    /**
     * Cancels the current turn timeout timer.
     * Should be called when a player performs a valid action.
     */
    public synchronized void cancelTurnTimer() {
        if (currentTurnTimeout != null && !currentTurnTimeout.isDone()) {
            currentTurnTimeout.cancel(false);
            currentTurnTimeout = null;
        }
    }

    /**
     * Checks if a player is currently marked as disconnected.
     *
     * @param nickname the player's nickname
     * @return true if disconnected, false otherwise
     */
    public boolean isPlayerDisconnected(String nickname) {
        return disconnectedPlayers.contains(nickname);
    }

    /**
     * Returns the unique ID for this game.
     *
     * @return the game ID
     */
    public int getGameId() {
        return gameId;
    }
}





