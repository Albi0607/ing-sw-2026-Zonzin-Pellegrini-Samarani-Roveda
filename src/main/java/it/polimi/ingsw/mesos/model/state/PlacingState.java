package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.board.TurnOrderTrack;
import it.polimi.ingsw.mesos.model.enums.GameState;

import java.util.List;

/**
 * Represents the phase where players place their totems on the offer track.
 * <p>
 * During this state, the game waits for players to take turns placing
 * their totems on available {@link OfferTile}s. The state remains active
 * until all totems for the current round have been placed.
 * Once placement is complete, it transitions to the {@link ResolvingState}.
 * </p>
 */

public class PlacingState implements GameStateLogic {

    private int activePlayerIndex = 0;
    private List<Player> actingOrder;

    public PlacingState() { }

    @Override
    public void execute(Game g) {
        System.out.println("\n--- [PLACING PHASE] I giocatori scelgono le tessere ---");

        TurnOrderTrack track = g.getBoard().getTurnOrderTrack();

        // 1. Salviamo l'ordine di azione per questo round (chi è attualmente sulla track)
        this.actingOrder = track.getPositions();

        // Creiamo una lista di null per svuotare le posizioni fisiche.

        track.resetOrder();

        this.activePlayerIndex = 0;
        System.out.println("Tocca a: " + actingOrder.get(0).getNickname());
    }



    /**
     * Executes the logic for placing a player's totem on an offer tile.
     * <p>
     * This overrides the default interface behavior because totem placement
     * is fully legal and expected during this specific phase.
     * </p>
     *
     * @param g The main game context.
     * @param p The player placing the totem.
     * @param t The target offer tile.
     */

    @Override
    public void placeTotemOnOffer(Game g, Player p, OfferTile t) {
        if (t == null) {
            throw new IllegalArgumentException("Errore: La tessera selezionata non esiste!");
        }

        // 1. Controllo del turno
        Player expectedPlayer = actingOrder.get(activePlayerIndex);
        if (!p.equals(expectedPlayer)) {
            throw new IllegalStateException("Non è il tuo turno! Tocca a " + expectedPlayer.getNickname());
        }

        // 2. Controllo disponibilità tessera
        if (!t.isAvailable()) {
            throw new IllegalStateException("Tessera già occupata!");
        }

        // 3. PIAZZAMENTO: Il totem si sposta sulla tessera.
        // NON chiamiamo track.setPlayerAt qui, perché il ritorno sulla track avviene a fine round.
        t.placeTotem(p);
        System.out.println(p.getNickname() + " ha piazzato il totem sulla tessera " + t.getId());

        activePlayerIndex++;

        // 4. Passaggio alla fase di Risoluzione
        if (activePlayerIndex >= actingOrder.size()) {
            System.out.println("Tutti i totem sono sulle tessere. Inizia la risoluzione delle azioni.");
            g.changeState(new ResolvingState());
        } else {
            System.out.println("Tocca a: " + actingOrder.get(activePlayerIndex).getNickname());
        }
    }


    @Override
    public GameState getStateId() { return GameState.PLACING_TOTEMS; }


    public Player getActivePlayer() {
        if (actingOrder == null || activePlayerIndex >= actingOrder.size()) {
            return null;
        }
        return actingOrder.get(activePlayerIndex);
    }


}
