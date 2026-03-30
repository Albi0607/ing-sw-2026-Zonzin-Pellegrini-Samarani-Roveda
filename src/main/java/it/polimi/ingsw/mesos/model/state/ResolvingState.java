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
        moveToNextOccupiedTile(g);
    }

    private void moveToNextOccupiedTile(Game g) {
        Board board = g.getBoard();
        List<OfferTile> tiles = board.getTiles();

        while (currentTileIndex < tiles.size()) {
            OfferTile tile = tiles.get(currentTileIndex);
            Player p = tile.getHost();

            if (p != null) {
                // 1. Bonus Cibo immediato
                tile.giveFoodBonus(p);

                // 2. Carichiamo i contatori per le carte
                this.remainingUpper = tile.getUpperCount();
                this.remainingLower = tile.getLowerCount();

                System.out.println("Tocca a " + p.getNickname() + " sulla tessera " + tile.getId());


                if (remainingUpper == 0 && remainingLower == 0) {
                    currentTileIndex++;
                    continue;
                }
                return;
            }
            currentTileIndex++;
        }

        // Se arriviamo qui, tutte le tessere sono state analizzate
        System.out.println("Tutte le tessere risolte. Il round sta per finire.");
        g.changeState(new EventState());
    }


    public void takeCard(Game g, int cardIndex, boolean isUpper) {
        Board board = g.getBoard();
        OfferTile tile = board.getTiles().get(currentTileIndex);
        Player p = tile.getHost();

        Card card = isUpper ? board.getUpperRow().get(cardIndex) : board.getLowerRow().get(cardIndex);

        // 2. CONTROLLO DEL COSTO
        int cost = card.getCost();
        int discount = p.getTribe().getBuildingDiscount();

        // Il costo finale è il costo base meno lo sconto (minimo 0)
        int finalCost = Math.max(0, cost - discount);

        if (card.getAsEventCard()!=null) {
            throw new IllegalArgumentException(p.getNickname() + " non puoi prendere una carta EVENTO!");
        }

        if (p.getFood() < finalCost) {
            // Il giocatore non ha abbastanza cibo: l'azione è VIETATA
            System.out.println("Azione non permessa! " + p.getNickname() +
                    " ha solo " + p.getFood() + " cibo, ma la carta costa " + cost);
            return;
        }

        if (isUpper) {
            board.takeCardFromUpper(cardIndex);
            remainingUpper--;
        } else {
            board.takeCardFromLower(cardIndex);
            remainingLower--;
        }

        // 4. Pagamento e aggiunta alla tribù
        p.payFood(finalCost);
        card.addTo(p);
        if (card instanceof BuildingCard) {
            g.notifyBuildingEffects(TriggerType.ON_PURCHASE);
        }else if (card instanceof CharacterCard){
            g.notifyBuildingEffects(TriggerType.ON_CHARACTER_ADDED);
        }
        System.out.println(p.getNickname() + " ha pagato " + finalCost + " cibo e preso la carta.");

        // 5. Se il giocatore ha finito tutte le sue pescate sulla tessera
        if (remainingUpper == 0 && remainingLower == 0) {
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


    /**
     * Restituisce il numero di carte che il giocatore deve ancora
     * prendere dalla fila superiore per la tessera attuale.
     */
    public int getRemainingUpper() {
        return remainingUpper;
    }

    /**
     * Restituisce il numero di carte che il giocatore deve ancora
     * prendere dalla fila inferiore per la tessera attuale.
     */
    public int getRemainingLower() {
        return remainingLower;
    }


    /**
     * Restituisce il giocatore che "possiede" la tessera attualmente in risoluzione.
     * Fondamentale per il Main e per il Controller per sapere a chi mostrare i tasti.
     * * @param g Il contesto del gioco necessario per accedere alla Board.
     * @return Il Player attivo, o null se tutte le tessere sono state risolte.
     */
    public Player getActivePlayer(Game g) {
        List<OfferTile> tiles = g.getBoard().getTiles();
        if (currentTileIndex >= 0 && currentTileIndex < tiles.size()) {
            return tiles.get(currentTileIndex).getHost();
        }
        return null;
    }
}
