package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.card.event.EventCard;
import it.polimi.ingsw.mesos.common.enums.EventType;
import it.polimi.ingsw.mesos.common.enums.GameState;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the end-of-round event phase.
 * <p>
 * During this state, end-of-round events like Sustenance are triggered.
 * The game checks if the current era is over or if the game has finished.
 * If the game continues, it transitions back to the {@link SetupState}
 * for the next round. If Era III is over, it transitions to FINISHED.
 * </p>
 */

public class EventState implements GameStateLogic {

    /**
     * Executes the end-of-round events and checks for game end.
     *
     * @param g The main game context. Must not be null.
     */
    @Override
    public void execute(Game g) {

        System.out.println("\n--- [EVENT PHASE] ---");

        if (g.isGameFinished()) {

            System.out.println(" ULTIMO ROUND: Risoluzione finale di TUTTI gli eventi visibili!");
            resolveAllVisibleEvents(g); // Fila sopra + Fila sotto
            g.changeState(new FinishedState());
            /*
            if (g.onGameEnd != null) {
                g.onGameEnd.run();
            }
             */

        } else {

            resolveLowerRowEvents(g); // Solo fila sotto
            g.changeState(new SetupState());

        }

    }

    /**
     * Resolves all visible event cards remaining on the board at the very end of the game.
     * <p>
     * This method collects events from both the Upper and Lower rows,ensuring that {@code SUSTENANCE} effects are applied
     *      * only after all other events have been resolved.
     * It is designed specifically for the final endgame resolution phase.
     * </p>
     * * @param g The current game instance.
     */
    public void resolveAllVisibleEvents(Game g){
        Board board = g.getBoard();
        List<EventCard> allEvents = new ArrayList<>();

        List<EventCard> lr_events = board.getLowerRow().stream()
                .filter(card -> card.getAsEventCard() != null)
                .map(card -> (EventCard) card)
                .collect(java.util.stream.Collectors.toList());

        List<EventCard> ur_events = board.getUpperRow().stream()
                .filter(card -> card.getAsEventCard() != null)
                .map(card -> (EventCard) card)
                .collect(java.util.stream.Collectors.toList());

        // 1. Prendiamo gli eventi da ENTRAMBE le file
        allEvents.addAll(lr_events);
        allEvents.addAll(ur_events);

        // 2. Ordinamento (Sostentamento sempre per ultimo, poi per Era)
        allEvents.sort((e1, e2) -> {
            if (e1.getType() == EventType.SUSTENANCE && e2.getType() != EventType.SUSTENANCE) return 1;
            if (e1.getType() != EventType.SUSTENANCE && e2.getType() == EventType.SUSTENANCE) return -1;
            return e1.getEra().compareTo(e2.getEra());
        });

        List<String> resolvedNames = new ArrayList<>();

        if (allEvents.isEmpty()) {
            resolvedNames.add("Nessun evento risolto.");
        } else {
            for (EventCard e : allEvents) {
                System.out.println("Risoluzione Finale: " + e.getType());
                resolvedNames.add(e.getType().toString()); // <-- Aggiungiamo alla lista!
                e.resolve(g);
            }
        }

        g.setLastResolvedEvents(resolvedNames);

    }

    /**
     * Resolves only the event cards present in the Lower Row during standard game rounds.
     * <p>
     * Unlike the endgame resolution, this method focuses exclusively on the Lower Row.
     * It sorts events by Era, ensuring that {@code SUSTENANCE} effects are applied
     * only after all other events have been resolved.
     * </p>
     * * @param g The current game instance.
     */
    public void resolveLowerRowEvents(Game g){
        // 1. Recuperiamo SOLO gli eventi dalla fila inferiore
        List<EventCard> eventsToResolve = g.getBoard().getLowerRow().stream()
                .filter(card -> card.getAsEventCard()!=null)
                .map(card -> (EventCard) card)
                .collect(java.util.stream.Collectors.toList());

        List<String> resolvedNames = new ArrayList<>();

        if (eventsToResolve.isEmpty()){
            resolvedNames.add("Nessun evento risolto.");
        }else{
            eventsToResolve.sort((e1, e2) -> {
                // Il Sostentamento (SUSTENANCE) va sempre per ultimo
                boolean isSust1 = e1.getType() == EventType.SUSTENANCE;
                boolean isSust2 = e2.getType() == EventType.SUSTENANCE;

                if (isSust1 != isSust2) {
                    return isSust1 ? 1 : -1;
                }

                // stesso tipo ma Ordine di Era (I < II < III)
                if (e1.getType() == e2.getType()) {
                    return e1.getEra().compareTo(e2.getEra());
                }

                return 0;
            });

            for (EventCard event : eventsToResolve) {
                System.out.println("Attivazione Evento: " + event.getType() + " [Era " + event.getEra() + "]");
                resolvedNames.add(event.getType().toString());
                event.resolve(g);
            }
        }

        g.setLastResolvedEvents(resolvedNames);

    }

    @Override
    public void placeTotemOnOffer(Game g, Player p, OfferTile t) {
        throw new IllegalStateException("Errore: Non puoi piazzare totem durante la Fase degli Eventi!");
    }

    @Override
    public void takeCard(Game g, Player p, int cardIndex, boolean isUpper) {
        throw new IllegalStateException("Errore: Non puoi pescare carte durante la Fase di risoluzione eventi!!!");
    }

    @Override
    public void skipExtraDraw(Game g) {
        throw new IllegalStateException("Errore: Non puoi saltare la pesca durante la Fase di risoluzione eventi!!!");
    }

    @Override
    public GameState getStateId() { return GameState.END_ROUND; }
}
