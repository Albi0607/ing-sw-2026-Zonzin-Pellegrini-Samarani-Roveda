package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.card.event.EventCard;
import it.polimi.ingsw.mesos.model.enums.EventType;
import it.polimi.ingsw.mesos.model.enums.GameState;
import it.polimi.ingsw.mesos.model.enums.TriggerType;

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
        resolveLowerRowEvents(g);
        System.out.println("\n--- [EVENT PHASE] ---");

        if (g.isGameFinished()) {

            System.out.println(" ULTIMO ROUND: Risoluzione finale di TUTTI gli eventi visibili!");
            resolveAllVisibleEvents(g); // Fila sopra + Fila sotto
            g.changeState(new FinishedState());

        } else {

            resolveLowerRowEvents(g); // Solo fila sotto
            g.changeState(new SetupState());

        }

    }


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

        // 3. Risoluzione e Notifica
        for (EventCard e : allEvents) {
            System.out.println("Risoluzione Finale: " + e.getType());
            e.resolve(g);
            g.notifyBuildingEffects(TriggerType.ON_EVENT);
        }
    }


    public void resolveLowerRowEvents(Game g){
        // 1. Recuperiamo SOLO gli eventi dalla fila inferiore
        List<EventCard> eventsToResolve = g.getBoard().getLowerRow().stream()
                .filter(card -> card.getAsEventCard()!=null)
                .map(card -> (EventCard) card)
                .collect(java.util.stream.Collectors.toList());

        if (eventsToResolve.isEmpty()) return;


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

            event.resolve(g);
            g.notifyBuildingEffects(TriggerType.ON_EVENT);
        }
    }

    @Override
    public void placeTotemOnOffer(Game g, Player p, OfferTile t) {
        throw new IllegalStateException("Errore: Non puoi piazzare totem durante la Fase degli Eventi!");
    }

    @Override
    public GameState getStateId() { return GameState.END_ROUND; }
}
