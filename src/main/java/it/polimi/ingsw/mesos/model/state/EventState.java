package it.polimi.ingsw.mesos.model.state;

import it.polimi.ingsw.mesos.model.Game;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.card.event.EventCard;
import it.polimi.ingsw.mesos.model.enums.GameState;
import it.polimi.ingsw.mesos.model.enums.TriggerType;

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

        System.out.println("--- EVENT PHASE ---");

        Board board = g.getBoard();

        // 1. Risoluzione degli Eventi
        for (EventCard event : board.getEvents()) {
            System.out.println("-> Risoluzione Evento in corso...");
            event.resolve(g);
            // 2. Notifica agli edifici
            g.notifyBuildingEffects(TriggerType.ON_EVENT, event);
        }


        // 2. PULIZIA FINE ROUND
        board.clearLowerRow();
        System.out.println("-> Fila inferiore pulita.");


        g.setCurrentRound(g.getCurrentRound() + 1);

        if (g.isGameFinished()) {
            System.out.println("-> Condizioni di fine partita soddisfatte!");

            g.changeState(new FinishedState());
        } else {
            System.out.println("-> Preparazione per il Round " + g.getCurrentRound());
            g.changeState(new SetupState());
        }
    }

    @Override
    public void placeTotemOnOffer(Game g, Player p, OfferTile t) {
        throw new IllegalStateException("Errore: Non puoi piazzare totem durante la Fase degli Eventi!");
    }

    @Override
    public GameState getStateId() { return GameState.END_ROUND; } // probabile da cambiare
}
