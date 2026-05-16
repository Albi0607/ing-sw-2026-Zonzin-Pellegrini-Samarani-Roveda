package it.polimi.ingsw.mesos.persistence;

import it.polimi.ingsw.mesos.controller.GameController;
import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.VirtualView;

import java.util.List;
import java.util.Map;

/**
 * Ricostruisce lo stato della partita rigiocando le mosse salvate su disco.
 *
 * Funzionamento:
 *   1. Legge la lista di GameMove dal MoveLogger.
 *   2. Le rigioca una per una sul GameController passato.
 *   3. Le VirtualView vengono sostituite da dummies se il giocatore non si è ancora riconnesso con stesso nickname.
 */
public class GameRestorer {

    private final MoveLogger logger;

    public GameRestorer(MoveLogger logger) {
        this.logger = logger;
    }

    /**
     * Rigioca tutte le mosse sul controller fornito.
     *
     * Le mosse ADD_PLAYER hanno bisogno di una VirtualView: vengono prese
     * dalla Map views man mano che i giocatori si riconnettono.
     *
     * Le mosse di gioco (PLACE_TOTEM, TAKE_CARD, SKIP_EXTRA_DRAW) vengono
     * eseguite normalmente — il broadcast è disabilitato durante il replay.
     *
     * @param controller  controller su cui le mosse vengono rigiocate
     * @param views       Map nickname → VirtualView dei client riconnessi
     * @return true se il ripristino è andato a buon fine, false in caso di errore
     */
    public boolean restore(GameController controller, Map<String, VirtualView> views) {
        List<GameMove> moves = logger.readAll();

        if (moves.isEmpty()) {
            System.out.println("[GameRestorer] Nessuna mossa salvata, partita nuova.");
            return false;
        }

        System.out.println("[GameRestorer] Ripristino partita con " + moves.size() + " mosse...");

        // Disabilita il broadcast durante il replay: i client riceveranno
        // solo lo stato finale, non tutti gli stati intermedi.
        controller.setReplayMode(true);

        try {
            for (GameMove move : moves) {
                System.out.println("[GameRestorer] Rieseguo: " + move);
                replayMove(move, controller, views);
            }
        } catch (Exception e) {
            System.err.println("[GameRestorer] Errore durante il ripristino: " + e.getMessage());
            controller.setReplayMode(false);
            return false;
        }

        controller.setReplayMode(false);
        controller.sendClientStateToAll(ClientState.IN_GAME);

        // Invia lo stato attuale a tutti i client riconnessi
        controller.broadcastUpdate();

        System.out.println("[GameRestorer] Ripristino completato.");
        return true;
    }

    /**
     * Esegue una singola mossa sul controller.
     *
     * @param move mossa da rigiocare
     * @param controller controller su cui rigiocare la mossa
     * @param views Map da cui ripristinare la view per la mossa ADD_PLAYER
     */
    private void replayMove(GameMove move, GameController controller,
                            Map<String, VirtualView> views) {
        switch (move.type) {

            case SET_NUM_PLAYERS -> controller.setNumPlayers(move.intPayload);

            case ADD_PLAYER -> {
                // Usa la VirtualView del client riconnesso, oppure una
                // DummyVirtualView se il client non si è ancora riconnesso
                // (riceverà l'aggiornamento completo alla fine del replay).
                VirtualView view = views.getOrDefault(
                        move.nickname,
                        new DummyVirtualView(move.nickname)
                );
                controller.addPlayer(move.nickname, move.colorPayload, view);
            }

            case START_GAME -> {
                StateSerializer ss = controller.getStateSerializer();

                if(ss.hasSavedState()) {
                    // 1. Ripristiniamo i mazzi INTERI prima di chiamare startGame
                    ss.restoreDeck(controller.getGame().getBoard().getTribeDeck(), true);
                    ss.restoreDeck(controller.getGame().getBoard().getBuildingDeck(), false);
                }

                // 2. Chiamiamo startGame: pescherà le carte giuste dal mazzo ripristinato!
                controller.startGame();

                // 3. Sistemiamo l'ordine dei giocatori (perché startGame ha rimescolato)
                if(ss.hasSavedState()) {
                    List<String> order = ss.restorePlayerOrder();
                    if (order != null) {
                        controller.getGame().setPlayerOrder(order);
                    }
                }
            }

            case PLACE_TOTEM -> controller.onPlaceTotem(move.nickname, move.charPayload);

            case TAKE_CARD -> controller.onTakeCard(move.nickname, move.intPayload, move.boolPayload);

            case SKIP_EXTRA_DRAW -> controller.onSkipExtraDraw(move.nickname);
        }
    }

    public MoveLogger getMoveLogger() {
        return logger;
    }
}