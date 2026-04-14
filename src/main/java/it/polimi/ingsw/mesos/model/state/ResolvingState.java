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
        // 1. GESTIONE FASE EXTRA
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

        // 2. GESTIONE TESSERE OFFERTA (Ciclo Standard)
        Board board = g.getBoard();
        List<OfferTile> tiles = board.getTiles();

        while (currentTileIndex < tiles.size()) {
            OfferTile tile = tiles.get(currentTileIndex);
            Player p = tile.getHost();

            if (p != null) {

                // Prepariamo i conteggi della tessera
                int lowerTarget = tile.getLowerCount();
                int upperTarget = tile.getUpperCount();

                int discount = p.getTribe().getBuildingDiscount();
                int availableFood = p.getFood();

                // Verifichiamo se esiste ALMENO una carta prendibile nelle file richieste
                boolean canPickLower = (lowerTarget > 0) && board.getLowerRow().stream()
                        .anyMatch(c -> c.getAsEventCard() == null && Math.max(0, c.getCost() - discount) <= availableFood);

                boolean canPickUpper = (upperTarget > 0) && board.getUpperRow().stream()
                        .anyMatch(c -> c.getAsEventCard() == null && Math.max(0, c.getCost() - discount) <= availableFood);

                // Se il giocatore ha ancora pescate da fare ma non ci sono carte valide per lui
                // lo saltiamo e passiamo alla tessera successiva.
                if ((lowerTarget > 0 && !canPickLower) && (upperTarget > 0 && !canPickUpper) ||
                        (lowerTarget > 0 && upperTarget == 0 && !canPickLower) ||
                        (upperTarget > 0 && lowerTarget == 0 && !canPickUpper)) {

                    System.out.println(p.getNickname() + " non ha carte accessibili. Salto alla prossima tessera.");
                    currentTileIndex++;
                    continue;
                }

                tile.giveFoodBonus(p);
                this.remainingUpper = upperTarget;
                this.remainingLower = lowerTarget;

                System.out.println("Tocca a " + p.getNickname() + " sulla tessera " + tile.getId());
                return;
            }
            currentTileIndex++;
        }

        // 3. ATTIVAZIONE FASE EXTRA
        System.out.println("Tutte le tessere risolte. Controllo poteri extra...");

        boolean cardsAvailable = g.getBoard().getUpperRow().stream()
                .anyMatch(card -> card.getAsEventCard() == null);

        this.extraDrawQueue = g.getPlayers().stream()
                .filter(Player::getExtraDraw)
                .toList();

        if (!extraDrawQueue.isEmpty() && cardsAvailable) {
            this.isExtraDrawPhase = true;
            this.extraQueueIndex = 0;
            moveToNextOccupiedTile(g); // Ricorsione per iniziare il giro extra
        } else {
            System.out.println("Nessuna azione extra possibile. Fine round.");
            g.changeState(new EventState());
        }
    }

    /**
     * Handles the logic for a player taking a card from the board.
     * <p>
     * Validates if the action is allowed (e.g., extra draw restricted to upper row),
     * calculates building discounts, deducts food cost, and triggers relevant
     * building or character effects.
     * </p>
     *
     * @param g         The main game context.
     * @param cardIndex The index of the card within the chosen row.
     * @param isUpper   True if picking from the upper row, false for the lower row.
     * @throws IllegalArgumentException if the action violates game rules (e.g., picking an event).
     */
    public void takeCard(Game g, int cardIndex, boolean isUpper) {
        if (isExtraDrawPhase && !isUpper) {
            throw new IllegalArgumentException("Il potere extra permette di pescare solo dalla fila superiore!");
        }

        Board board = g.getBoard();
        // IMPORTANTE: Se siamo in fase extra, il player non è sulla tessera!
        Player p = getActivePlayer(g);

        if (p == null) return;

        Card card = isUpper ? board.getUpperRow().get(cardIndex) : board.getLowerRow().get(cardIndex);

        // Controllo Evento
        if (card.getAsEventCard() != null) {
            throw new IllegalArgumentException("Non puoi prendere una carta EVENTO!");
        }

        // Controllo Costo
        int finalCost = Math.max(0, card.getCost() - p.getTribe().getBuildingDiscount());
        if (p.getFood() < finalCost) {
            System.out.println("Cibo insufficiente!");
            return;
        }

        // Esecuzione
        if (isUpper) {
            board.takeCardFromUpper(cardIndex);
            if (!isExtraDrawPhase && remainingUpper > 0) remainingUpper--;
        } else {
            board.takeCardFromLower(cardIndex);
            if (!isExtraDrawPhase && remainingLower > 0)remainingLower--;
        }

        p.payFood(finalCost);
        card.addTo(p);

        // Trigger
        TriggerType trigger = (card instanceof BuildingCard) ? TriggerType.ON_PURCHASE : TriggerType.ON_CHARACTER_ADDED;
        g.notifyPlayersBuildingEffects(trigger, p);

        // Avanzamento
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
    public Player getActivePlayer(Game g) {
        if (isExtraDrawPhase) {
            if (extraQueueIndex < extraDrawQueue.size()) {
                return extraDrawQueue.get(extraQueueIndex);
            }
            return null;
        }

        List<OfferTile> tiles = g.getBoard().getTiles();
        if (currentTileIndex >= 0 && currentTileIndex < tiles.size()) {
            return tiles.get(currentTileIndex).getHost();
        }
        return null;
    }

    /**
     * Allows a player to skip their optional extra draw action.
     *
     * @param g The main game context.
     */
    public void skipExtraDraw(Game g) {
        if (isExtraDrawPhase) {
            System.out.println(extraDrawQueue.get(extraQueueIndex).getNickname() + " ha saltato la pescata extra.");
            extraQueueIndex++;
            moveToNextOccupiedTile(g);
        }
    }
}
