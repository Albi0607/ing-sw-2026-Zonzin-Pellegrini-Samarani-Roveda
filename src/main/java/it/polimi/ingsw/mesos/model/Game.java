package it.polimi.ingsw.mesos.model;

import it.polimi.ingsw.mesos.model.board.*;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.building.BuildingEffect;
import it.polimi.ingsw.mesos.model.card.character.TribeCard;
import it.polimi.ingsw.mesos.model.deck.Deck;
import it.polimi.ingsw.mesos.model.enums.Era;
import it.polimi.ingsw.mesos.model.enums.EventType;
import it.polimi.ingsw.mesos.model.enums.TriggerType;
import it.polimi.ingsw.mesos.model.state.GameStateLogic;
import it.polimi.ingsw.mesos.model.enums.GameState;
import it.polimi.ingsw.mesos.model.state.PlacingState;
import it.polimi.ingsw.mesos.model.state.SetupState;
import jdk.jfr.Event;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Game {

    private static final int MAX_ROUNDS = 10;

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

        this.currentState = new PlacingState();
    }


    public void startGame() {
        System.out.println("--- SYSTEM: Starting the game and initializing... ---");

        int numPlayers = this.players.size();



        // 1. INIZIALIZZAZIONE COMPONENTI
        CreateOfferTile tileCreator = new CreateOfferTile();
        CreateTurnOrderTrack trackCreator = new CreateTurnOrderTrack();

        // Generiamo le tessere e la track
        List<OfferTile> generatedTiles = tileCreator.initializeOfferTiles(numPlayers);
        TurnOrderTrack generatedTrack = trackCreator.initializeTurnOrderTrack(numPlayers);

        // Passiamo gli oggetti creati alla Board
        this.board.setTiles(generatedTiles);
        this.board.setTurnOrderTrack(generatedTrack);

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

        // Fila Inferiore (N+1 carte, gli Eventi saltano sopra)
        int lowerTarget = numPlayers + 1;
        int cardsPlacedInLower = 0;

        while (cardsPlacedInLower < lowerTarget && !board.getTribeDeck().isEmpty()) {
            TribeCard drawn = board.getTribeDeck().draw();
            if (drawn.getAsEventCard()!=null) {
                board.getUpperRow().add(drawn); // Salta sopra
                System.out.println("-> Evento saltato nella fila superiore.");
            } else {
                board.getLowerRow().add(drawn); // Personaggio resta sotto
                cardsPlacedInLower++;
            }
        }

        // Punto 5: Fila Superiore (N+4 carte totali)
        int upperTarget = numPlayers + 4;
        while (board.getUpperRow().size() < upperTarget && !board.getTribeDeck().isEmpty()) {
            board.getUpperRow().add(board.getTribeDeck().draw());
        }


        Deck<BuildingCard> buildingDeck = board.getBuildingDeck();

        while (!buildingDeck.isEmpty()) {
            BuildingCard card = buildingDeck.draw();


            // Controllo Era per debugging e logica
            if (card.getEra() == Era.ERA_I) {
                board.getUpperRow().add(card);
                System.out.println(" Edificio Era I aggiunto: " + card.getClass().getSimpleName());
            }else{
                // Se abbiamo pescato un edificio dell'Era II, lo rimettiamo sopra
                buildingDeck.put(card);
                System.out.println("ℹ️ Trovato edificio " + card.getEra() + ", rimesso nel mazzo. Setup Era I terminato.");
                break; // Usciamo dal ciclo: non ci sono più edifici Era I in cima
            }

        }

        System.out.println("--- SYSTEM: Round 1 Setup Complete. Ready to Play! ---");


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
     * @param nextEra The card's era that triggered the era transition.
     */
    public void handleEraTransition(Era nextEra) {
        System.out.println("\n--- Transizione all'Era " + nextEra + " ---");

        // 1. Gli edifici dell'era precedente "scendono" accanto alla fila inferiore
        this.board.shiftBuildingsToLower();
        System.out.println("- Edifici correnti spostati nella fila inferiore.");

        // 2. Se passiamo all'Era III, gli edifici dell'Era I (che erano già sotto) spariscono
        if (nextEra == Era.ERA_III) {
            this.board.clearBuildingsFromLower();
            System.out.println("- Edifici rimossi definitivamente.");
        }

        // 3. Aggiorniamo l'era del gioco
        this.currentEra = nextEra;
        //da testare
        Deck<BuildingCard> buildingDeck = board.getBuildingDeck();

        while (!buildingDeck.isEmpty()) {
            BuildingCard card = buildingDeck.draw();


            // Controllo Era per debugging e logica
            if (card.getEra() == nextEra) {
                board.getUpperRow().add(card);
                System.out.println(" Edificio Era I aggiunto: " + card.getClass().getSimpleName());
            }else{
                // Se abbiamo pescato un edificio di un' altra era, lo rimettiamo sopra
                buildingDeck.put(card);
                System.out.println("ℹ Trovato edificio " + card.getEra() + ", rimesso nel mazzo. Setup Era I terminato.");
                break; // Usciamo dal ciclo: non ci sono più edifici Era I in cima
            }

        }

        System.out.println("- Nuovi edifici Era " + nextEra + " aggiunti alla fila superiore.");
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
    public void notifyBuildingEffects(TriggerType trigger) {

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
                .max(Comparator.comparingInt(Player::getPrestigePoints)
                        .thenComparingInt(Player::getFood))
                .orElse(null);
    }

    public boolean isGameFinished() {
        return currentRound >= MAX_ROUNDS || board.getTribeDeck().isEmpty();
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

    public EventType getCurrentEventType(){return null;}

    public void setCurrentRound(int round) {
        this.currentRound = round;
    }
}
