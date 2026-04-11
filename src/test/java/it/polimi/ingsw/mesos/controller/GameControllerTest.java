package it.polimi.ingsw.mesos.controller;

import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.enums.GameState;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    @Test
    void simulazioneRoundCompleto() {
        // 1. Setup lobby
        GameController controller = new GameController();

        controller.setNumPlayers(3);

        controller.addPlayer("Alice");
        controller.addPlayer("Bob");
        controller.addPlayer("Carlo");

        // 2. Avvia — esegue startGame() → SetupState → PlacingState
        controller.startGame();

        assertEquals(GameState.PLACING_TOTEMS,
                controller.getGame().getCurrentState().getStateId());

        // 3. Piazzamento totem — l'ordine lo detta la TurnOrderTrack
        // leggiamo chi deve andare per primo
        List<Player> turnOrder = controller.getGame()
                .getBoard().getTurnOrderTrack().getPositions();

        for (Player p : turnOrder) {
            // ogni giocatore sceglie una tessera libera
            OfferTile tile = controller.getGame()
                    .getBoard().getAvailableTiles().get(0);
            controller.onPlaceTotem(p.getNickname(), tile.getId());
        }

        // tutti hanno piazzato → RESOLVING_ACTIONS
        assertEquals(GameState.RESOLVING_ACTIONS,
                controller.getGame().getCurrentState().getStateId());

        // 4. Risoluzione — ogni giocatore pesca le sue carte
        // il primo da risolvere è chi è sulla tessera più a sinistra
        while (controller.getGame().getCurrentState().getStateId()
                == GameState.RESOLVING_ACTIONS) {

            // troviamo il giocatore corrente (prima tessera occupata)
            Player current = controller.getGame().getBoard().getTiles()
                    .stream()
                    .filter(t -> !t.isAvailable())
                    .findFirst()
                    .get()
                    .getHost();

            // pesca dalla fila superiore finché ha pick disponibili
            while (controller.getPendingPicks() > 0) {
                if (!controller.getGame().getBoard().getUpperRow().isEmpty()) {
                    controller.onTakeCardFromLower(current.getNickname(), 0);
                } else {
                    controller.onTakeCardFromLower(current.getNickname(), 0);
                }
            }
        }

        // fine round → EventState ha risolto → torna a SetupState o FinishedState
        assertNotEquals(GameState.RESOLVING_ACTIONS,
                controller.getGame().getCurrentState().getStateId());
    }
}