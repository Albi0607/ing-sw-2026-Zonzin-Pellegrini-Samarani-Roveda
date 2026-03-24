package it.polimi.ingsw.mesos.model;

import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.building.BuildingEffect;
import it.polimi.ingsw.mesos.model.card.character.TribeCard;
import it.polimi.ingsw.mesos.model.deck.Deck;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;
import it.polimi.ingsw.mesos.model.enums.TriggerType;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.state.GameStateLogic;
// in teoria l'attributo e i metodi dovrebbero essere enum (GameState) e non l'interfaccia, no? per ora li ho sostituiti
import it.polimi.ingsw.mesos.model.enums.GameState;
import it.polimi.ingsw.mesos.model.state.SetupState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Game {

    private List<Player> players;
    private Board board;
    private int currentRound;
    private Era currentEra;
    private GameStateLogic currentState;

    /**
     * Initializes a new Game instance with the provided list of players.
     * <p>
     * It sets up the game board based on the number of players, initializes
     * the time trackers (round and era), and sets the initial state to the Setup phase.
     * </p>
     *
     * @param players The list of players joining the game. Must contain between 2 and 5 players.
     * @throws IllegalArgumentException if the player list is null or has an invalid size.
     */
    public Game(List<Player> players) {
        if (players == null || players.size() < 2 || players.size() > 5) {
            throw new IllegalArgumentException("A game requires between 2 and 5 players.");
        }

        this.players = new java.util.ArrayList<>(players);


        this.board = new Board(this.players.size());

        this.currentRound = 1;
        this.currentEra = Era.ERA_I;

        this.currentState = new SetupState();
    }


    public void startGame() {
        System.out.println("--- SYSTEM: Starting the game and initializing... ---");

        int numPlayers = this.players.size();

        // 1. INIZIALIZZAZIONE DELLA PLANCIA
        this.board.initializeOfferTiles(numPlayers);
        this.board.initializeTurnOrderTrack(numPlayers);
        System.out.println("Board initialized with Offer Tiles and Turn Order Track for " + numPlayers + " players.");

        java.util.Collections.shuffle(this.players);
        System.out.println("Ordine di turno iniziale stabilito casualmente.");


        this.board.getTurnOrderTrack().updateOrder(this.players);


        // 2. INIZIALIZZAZIONE DEI GIOCATORI
        for (int i = 0; i < numPlayers; i++) {
            Player p = this.players.get(i);
            // Distribuzione del cibo in base alla posizione
            if (i == 0) {
                p.addFood(2); // 1° giocatore
            } else if (i == 1 || i == 2) {
                p.addFood(3); // 2° e 3° giocatore
            } else {
                p.addFood(4); // 4° e 5° giocatore
            }
            System.out.println("Preparato giocatore " + (i+1) + ": " + p.getNickname());
        }

        System.out.println("--- SYSTEM: Handing over control to the initial state ---");

        // Passiamo  al SetupState
        if (this.currentState != null) {
            this.currentState.execute(this);
        } else {
            System.err.println("CRITICAL ERROR: currentState is null at startup!");
        }
    }

    /**
     * Handles the transition of the game from one phase to another.
     * <p>
     * This method is typically called by the states themselves when
     * they have completed their tasks and need to pass control to the next state.
     * </p>
     *
     * @param newState The new state the game should transition to.
     * @throws IllegalArgumentException if the provided state is null.
     */
    public void changeState(GameStateLogic newState) {
        // 1. Controllo di sicurezza
        if (newState == null) {
            throw new IllegalArgumentException("Errore: il nuovo stato non può essere null.");
        }

        // 2. Aggiorniamo il puntatore allo stato corrente
        this.currentState = newState;

        System.out.println("\n--> [TRANSITION] Il gioco passa allo stato: " + this.getCurrentState());

        this.currentState.execute(this);
    }

    /**
     * Checks if a chosen nickname is unique among the current players.
     * <p>
     * This method is useful during the lobby or login phase to prevent
     * players from choosing duplicate names. The comparison is case-insensitive.
     * </p>
     *
     * @param name The nickname to check.
     * @return true if the nickname is available, false if it is already taken or invalid.
     */
    public boolean checkNicknameUnique(String name) {
        return players.stream()
                .map(Player::getNickname)
                .noneMatch(n -> name.equalsIgnoreCase(n));
    }


    /**
     * Attempts to place a player's totem on a specific offer tile.
     * <p>
     * Following the State Pattern, the Game class does not handle the logic directly.
     * Instead, it delegates the action to the current state. The active state will
     * decide whether to execute the placement, ignore it, or throw an exception
     * depending on the current phase of the round.
     * </p>
     *
     * @param p The player attempting to place the totem.
     * @param t The offer tile chosen by the player.
     */
    public void placeTotemOnOffer(Player p, OfferTile t) {
        if (this.currentState != null) {
            // Deleghiamo l'azione allo stato attuale passando il riferimento al Game (this)
            this.currentState.placeTotemOnOffer(this, p, t);
        } else {
            System.err.println("CRITICAL ERROR: No active state to handle totem placement.");
        }
    }

    /**
     * Handles the transition to the next Era following the official rulebook.
     * <p>
     * Step 1: At the start of Era III, discard Buildings from the lower row.
     * Step 2: Move Buildings from the upper row to the lower row.
     * Step 3: Place the new Era's Building cards in the upper row.
     * </p>
     *
     * @param newCard The card that triggered the era transition.
     */
    public void handleEraTransition(Card newCard) {
        System.out.println("\n--- [EVENTO] Transizione di Era in corso... ---");

        if (this.currentEra == Era.ERA_I) {
            this.currentEra = Era.ERA_II;
            System.out.println("Il gioco avanza all'ERA II!");

        } else if (this.currentEra == Era.ERA_II) {
            this.currentEra = Era.ERA_III;
            System.out.println("Il gioco avanza all'ERA III!");


            if (this.board != null) {
                this.board.clearBuildingsFromLower();
                System.out.println("- Passaggio 1 eseguito: Edifici rimossi dalla fila inferiore.");
            }
        } else {
            System.out.println("Siamo già nell'Era III. Nessun avanzamento.");
            return; // Se siamo già alla fine, interrompiamo il metodo qui
        }

        // Se siamo arrivati qui, significa che c'è stato un salto (all'Era II o all'Era III)
        if (this.board != null) {
            // Spostare gli edifici dalla superiore all'inferiore
            this.board.shiftBuildingsToLower();
            System.out.println("- Passaggio 2 eseguito: Edifici spostati dalla fila superiore alla fila inferiore.");





            System.out.println("- Passaggio 3 eseguito: Nuovi edifici dell'Era " + this.currentEra + " piazzati nella fila superiore.");
        }
    }

    /**
     * Notifies all building effects in the game that a specific trigger has occurred.
     *
     * This method iterates over every player and over every building owned in their tribe.
     * For each building that has an associated {@link BuildingEffect}, the effect is
     * executed through the Strategy Pattern by invoking
     * {@code applyEffect(Player, Game, TriggerType)}.
     *
     */
    public void notifyBuildingEffects(TriggerType trigger, Object context) {

        if (trigger == null) {
            throw new IllegalArgumentException("Trigger type cannot be null");
        }

        for (Player player : players) {
            Tribe tribe = player.getTribe();

            for (BuildingCard building : tribe.getBuildings()) {
                BuildingEffect effect = building.getEffect();

                if (effect != null) {
                    effect.applyEffect(player, this, trigger);
                }
            }
        }
    }

    /**
     * Determines the winner of the game based on prestige points.
     *
     * @return the player with the highest prestige points,
     * or null if the player list is empty
     */
    public Player getWinner() {
        return players.stream()
                .max((p1, p2) -> {
                    int prestigeCompare = Integer.compare(p1.getPrestigePoints(), p2.getPrestigePoints());
                    if (prestigeCompare != 0) return prestigeCompare;
                    return Integer.compare(p1.getFood(), p2.getFood());
                })
                .orElse(null);
    }

    public boolean isGameFinished() {
        // da sistemare....
        return true;
    }

    // --- Getters ---

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public Board getBoard() {
        return board;
    }

    public int getCurrentRound() {
        return this.currentRound;
    }

    public Era getCurrentEra() {
        return currentEra;
    }

    public GameStateLogic getCurrentState() {
        return currentState;
    }


    public void setCurrentRound(int round) {
        this.currentRound = round;
    }
}
