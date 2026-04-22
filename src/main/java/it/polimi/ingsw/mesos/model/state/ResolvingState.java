package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.board.TurnOrderTrack;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.card.building.BuildingCard;
import it.polimi.ingsw.mesos.model.card.character.CharacterCard;
import it.polimi.ingsw.mesos.model.card.event.EventCard;
import it.polimi.ingsw.mesos.model.enums.GameState;
import it.polimi.ingsw.mesos.model.enums.TriggerType;

import java.util.List;

/**
 * Represents the phase where actions on the board are resolved.
 * <p>
 * In this state, the game evaluates the offer track from top to bottom,
 * executing the actions associated with the tiles where totems were placed.
 * Players gather resources, buy cards, and apply effects.
 * After all actions are resolved, it transitions to the {@link EventState}.
 * </p>
 */

public class ResolvingState implements GameStateLogic {


    private int currentTileIndex = 0;
    private int remainingUpper = 0;
    private int remainingLower = 0;

    private boolean isExtraDrawPhase = false;
    private int extraQueueIndex = 0;
    private List<Player> extraDrawQueue;
    /**
     * Determines resolution order (left to right on OfferTrack) and
     * waits for each player to complete their picks.
     * When all players are done, transitions to EventState.
     */


    /**
     * Executes the logic for resolving board actions.
     *
     * @param g The main game context. Must not be null.
     */

    @Override
    public void execute(Game g) {
        System.out.println("\n--- [RESOLVING PHASE] Risoluzione delle tessere ---");
        currentTileIndex = 0;
        isExtraDrawPhase = false;
        moveToNextOccupiedTile(g);
    }

    /**
     * Advances the resolution flow to the next available action.
     * <p>
     * It first checks standard Offer Tiles. If all tiles are processed, it populates
     * the extra draw queue and switches to the extra draw phase if eligible players
     * and valid cards are present. Otherwise, it transitions to the {@code EventState}.
     * </p>
     *
     * @param g The main game context.
     */

    private void moveToNextOccupiedTile(Game g) {
        if (isExtraDrawPhase) {
            if (extraQueueIndex < extraDrawQueue.size()) {
                Player p = extraDrawQueue.get(extraQueueIndex);
                System.out.println("[EXTRA] Tocca a " + p.getNickname() + ". Puoi prendere 1 carta sopra o passare.");
                return;
            } else {
                System.out.println("Fase Extra terminata. Passaggio agli eventi.");
                g.changeState(new EventState());
                return;
            }
        }

        Board board = g.getBoard();
        List<OfferTile> tiles = board.getTiles();

        while (currentTileIndex < tiles.size()) {
            OfferTile tile = tiles.get(currentTileIndex);
            Player p = tile.getHost();

            if (p != null) {
                int lowerTarget = tile.getLowerCount();
                int upperTarget = tile.getUpperCount();
                int discount = p.getTribe().getBuildingDiscount();

                // Diamo il bonus (se presente) PRIMA dei calcoli
                tile.giveFoodBonus(p);
                int finalAvailableFood = p.getFood();

                // Calcoliamo le carte EFFETTIVAMENTE disponibili (non eventi e con costo <= cibo)
                int availableUpper = (int) board.getUpperRow().stream()
                        .filter(c -> c.getAsEventCard() == null && Math.max(0, c.getCost() - discount) <= finalAvailableFood)
                        .count();

                int availableLower = (int) board.getLowerRow().stream()
                        .filter(c -> c.getAsEventCard() == null && Math.max(0, c.getCost() - discount) <= finalAvailableFood)
                        .count();

                // Math.min impedisce di assegnare pescate se non ci sono carte sufficienti
                this.remainingUpper = Math.min(upperTarget, availableUpper);
                this.remainingLower = Math.min(lowerTarget, availableLower);

                // Se dopo tutto questo non ha mosse legali, lo saltiamo
                if (this.remainingUpper == 0 && this.remainingLower == 0) {
                    currentTileIndex++;
                    continue;
                }

                System.out.println("Tocca a " + p.getNickname() + " sulla tessera " + tile.getId());
                return;
            }
            currentTileIndex++;
        }

        boolean cardsAvailable = g.getBoard().getUpperRow().stream().anyMatch(card -> card.getAsEventCard() == null);
        this.extraDrawQueue = g.getPlayers().stream().filter(Player::getExtraDraw).toList();

        if (!extraDrawQueue.isEmpty() && cardsAvailable) {
            this.isExtraDrawPhase = true;
            this.extraQueueIndex = 0;
            moveToNextOccupiedTile(g);
        } else {
            System.out.println("Nessuna azione extra possibile. Fine round.");
            g.changeState(new EventState());
        }
    }


    /**
     * Executes the action of a player taking a card from the offer board.
     * <p>
     * This method validates the legality of the move, including checking if it is the player's turn,
     * if the player has remaining picks for the chosen row, and if the player has enough food
     * to pay for the card (factoring in any tribe discounts). It strictly prohibits picking Event cards.
     * </p>
     * <p>
     * Upon successful validation, the card is removed from the board and added to the player's domain,
     * the food cost is deducted, and any relevant card effects are triggered.
     * Finally, it dynamically recalculates the player's remaining valid picks; if no accessible cards
     * remain (e.g., due to lack of food or only Event cards left), it automatically advances the turn
     * to the next occupied tile.
     * </p>
     *
     * @param g         The main game context.
     * @param p         The player attempting to take the card.
     * @param cardIndex The zero-based index of the card within the chosen row.
     * @param isUpper   {@code true} if the card is being picked from the upper row; {@code false} if from the lower row.
     * @throws IllegalArgumentException if the action violates game rules (e.g., attempting to pick an event card,
     * picking from a row with no remaining picks, or picking from the lower row during an extra draw phase).
     * @throws IllegalStateException    if it is not the specified player's active turn.
     */
    @Override
    public void takeCard(Game g, Player p, int cardIndex, boolean isUpper) {
        if (isExtraDrawPhase && !isUpper) {
            throw new IllegalArgumentException("Il potere extra permette di pescare solo dalla fila superiore!");
        }

        if (!p.equals(getActivePlayer(g))) {
            throw new IllegalStateException("Non è il tuo turno, " + p.getNickname() + "!");
        }

        // Controllo di sicurezza: impedisce chiamate forzate senza pick
        if (!isExtraDrawPhase) {
            if (isUpper && remainingUpper <= 0) throw new IllegalArgumentException("Non hai pescate sopra!");
            if (!isUpper && remainingLower <= 0) throw new IllegalArgumentException("Non hai pescate sotto!");
        }

        Board board = g.getBoard();
        if (p == null) return;

        Card card = isUpper ? board.getUpperRow().get(cardIndex) : board.getLowerRow().get(cardIndex);

        if (card.getAsEventCard() != null) {
            throw new IllegalArgumentException("Non puoi prendere una carta EVENTO!");
        }

        int finalCost = Math.max(0, card.getCost() - p.getTribe().getBuildingDiscount());
        if (p.getFood() < finalCost) {
            System.out.println("Cibo insufficiente!");
            return; // Se il cibo è poco, esce senza scalare il pick (aspetta un'altra mossa valida)
        }

        if (isUpper) {
            board.takeCardFromUpper(cardIndex);
            if (!isExtraDrawPhase && remainingUpper > 0) remainingUpper--;
        } else {
            board.takeCardFromLower(cardIndex);
            if (!isExtraDrawPhase && remainingLower > 0) remainingLower--;
        }

        p.payFood(finalCost);
        card.addTo(p);

        TriggerType trigger = (card instanceof BuildingCard) ? TriggerType.ON_PURCHASE : TriggerType.ON_CHARACTER_ADDED;
        g.notifyPlayersBuildingEffects(trigger, p);

        // --- RICALCOLO DINAMICO (Cruciale per non far bloccare il giocatore) ---
        int discount = p.getTribe().getBuildingDiscount();
        int currentFood = p.getFood();

        int currentAvailableUpper = (int) board.getUpperRow().stream()
                .filter(c -> c.getAsEventCard() == null && Math.max(0, c.getCost() - discount) <= currentFood).count();
        int currentAvailableLower = (int) board.getLowerRow().stream()
                .filter(c -> c.getAsEventCard() == null && Math.max(0, c.getCost() - discount) <= currentFood).count();

        // Se ha finito il cibo o svuotato le carte, azzeriamo i pick residui!
        if (currentAvailableUpper == 0) {
            this.remainingUpper = 0;
        }
        if (currentAvailableLower == 0) {
            this.remainingLower = 0;
        }

        if (isExtraDrawPhase) {
            extraQueueIndex++;
            moveToNextOccupiedTile(g);
        } else if (remainingUpper == 0 && remainingLower == 0) {
            currentTileIndex++;
            moveToNextOccupiedTile(g);
        }
    }


    @Override
    public void placeTotemOnOffer(Game g, Player p, OfferTile t) {
        throw new IllegalStateException("Errore: Non puoi piazzare totem durante la Fase di risoluzione!!!");
    }

    @Override
    public GameState getStateId() { return GameState.RESOLVING_ACTIONS; }

    public int getRemainingUpper() {
        return remainingUpper;
    }

    public int getRemainingLower() {
        return remainingLower;
    }

    /**
     * Identifies the player currently allowed to perform an action.
     * * @param g The main game context.
     * @return The active {@link Player}, or null if the phase is complete.
     */
    @Override
    public Player getActivePlayer(Game g) {
        if (isExtraDrawPhase) {
            if (extraQueueIndex < extraDrawQueue.size()) {
                return extraDrawQueue.get(extraQueueIndex);
            }
            return null;
        }

        List<OfferTile> tiles = g.getBoard().getTiles();
        if (currentTileIndex >= 0 && currentTileIndex < tiles.size()) {
            System.out.println(" primo indice della prima tessera su cui è piazzato il primo totem "+ currentTileIndex);
            return tiles.get(currentTileIndex).getHost();
        }
        return null;
    }

    /**
     * Allows a player to skip their optional extra draw action.
     *
     * @param g The main game context.
     */
    @Override
    public void skipExtraDraw(Game g) {
        if (isExtraDrawPhase) {
            System.out.println(extraDrawQueue.get(extraQueueIndex).getNickname() + " ha saltato la pescata extra.");
            extraQueueIndex++;
            moveToNextOccupiedTile(g);
        }
    }

    @Override
    public boolean isNextUpper(Game g) {
        // Se siamo nella fase speciale "Extra Draw", la regola dice che si pesca solo da sopra!
        if (isExtraDrawPhase) {
            return true;
        }

        // Regola base: diamo priorità alle pescate superiori.
        // Se al giocatore ne resta almeno una, deve pescare sopra.
        if (remainingUpper > 0) {
            return true;
        }
        // Altrimenti, se quelle superiori sono finite (o erano 0 fin dall'inizio), pesca sotto.
        else {
            return false;
        }
    }

}
