package it.polimi.ingsw.mesos.controller;

import it.polimi.ingsw.mesos.rete.ClientModel.ClientState;
import it.polimi.ingsw.mesos.rete.ClientModel.GameDTO;
import it.polimi.ingsw.mesos.model.Player;
import it.polimi.ingsw.mesos.model.board.Board;
import it.polimi.ingsw.mesos.model.board.OfferTile;
import it.polimi.ingsw.mesos.model.card.Card;
import it.polimi.ingsw.mesos.model.enums.GameState;
import it.polimi.ingsw.mesos.model.state.PlacingState;
import it.polimi.ingsw.mesos.model.state.ResolvingState;
import it.polimi.ingsw.mesos.rete.VirtualView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    private GameController controller;
    private VirtualView mockView;

    @BeforeEach
    void setUp() {

        controller = new GameController();
        mockView = new VirtualView() {
            @Override public void sendGame(GameDTO gameDTO) {}
            @Override public void sendClientState(ClientState clientState) {}
            @Override public void showMessage(String message) {}
            @Override public String getNickname() { return "Test"; }
        };

    }

    @Test
    void testSimulazionePrimoRoundCompleto() {

        controller.setNumPlayers(3);
        controller.addPlayer("Alice", mockView);
        controller.addPlayer("Bob", mockView);
        controller.addPlayer("Carlo", mockView);

        controller.startGame();

        assertEquals(GameState.PLACING_TOTEMS, controller.getGame().getCurrentState().getStateId());

        // --- 2. FASE DI PIAZZAMENTO (PLACING) ---
        // Recuperiamo l'ordine di turno deciso dal modello
        List<Player> actingOrder = controller.getGame().getPlayers(); // O l'ordine della track

        char[] tilesToPick = {'D', 'E', 'F'};
        for (int i = 0; i < actingOrder.size(); i++) {
            String currentNickname = actingOrder.get(i).getNickname();
            controller.onPlaceTotem(currentNickname, tilesToPick[i]);
        }

        // Dopo l'ultimo piazzamento, il gioco deve essere passato automaticamente alla risoluzione
        assertEquals(GameState.RESOLVING_ACTIONS, controller.getGame().getCurrentState().getStateId());

        // --- 3. FASE DI RISOLUZIONE (RESOLVING) ---
        // Risolviamo i giocatori uno alla volta finché ci sono pick pendenti
        while (controller.getGame().getCurrentState().getStateId() == GameState.RESOLVING_ACTIONS) {

            // Chiediamo al modello chi è il giocatore che deve agire ora
            ResolvingState rs = (ResolvingState) controller.getGame().getCurrentState();
            Player activePlayer = rs.getActivePlayer(controller.getGame());

            // Se il giocatore ha ancora carte da prendere, ne prende una dalla fila sotto (indice 0)
            // Usiamo un while interno per svuotare i pick di quel giocatore
            if (activePlayer != null) {
                Board board = controller.getGame().getBoard();

                // Determiniamo se dobbiamo pescare sopra o sotto
                boolean pickUpper = rs.getRemainingUpper() > 0;
                List<Card> row = pickUpper ? board.getUpperRow() : board.getLowerRow();

                // --- FIX: Cerchiamo l'indice della prima carta che NON sia un evento ---
                int validIndex = -1;
                for (int i = 0; i < row.size(); i++) {
                    if (row.get(i).getAsEventCard() == null) {
                        validIndex = i;
                        break;
                    }
                }

                if (validIndex != -1) {
                    controller.onTakeCard(activePlayer.getNickname(), validIndex, pickUpper);
                }
            }
        }

        // --- 4. FASE EVENTI E AVANZAMENTO (EVENT -> SETUP) ---
        // Se la risoluzione è finita, il gioco deve essere passato per gli eventi
        // e poi essere arrivato al SETUP o direttamente al PLACING del Round 2.

        GameState finalState = controller.getGame().getCurrentState().getStateId();

        // Se il tuo SetupState passa automaticamente al PlacingState del Round 2:
        assertEquals(GameState.PLACING_TOTEMS, finalState,
                "Il gioco dovrebbe essere tornato in fase di piazzamento per il Round 2");

        // Verifichiamo che i giocatori siano tornati sulla TurnOrderTrack
        assertFalse(controller.getGame().getBoard().getTurnOrderTrack().getPositions().isEmpty(),
                "La track non dovrebbe essere vuota all'inizio del Round 2");
    }


    @Test
    void testControllerLifecycleAndSecurityFailsafes() {
        // --- 1. TEST DEI LIMITI DI SETUP ---

        // Numero giocatori non valido
        assertThrows(IllegalArgumentException.class, () -> controller.setNumPlayers(1),
                "Il controller deve rifiutare 1 giocatore");
        assertThrows(IllegalArgumentException.class, () -> controller.setNumPlayers(6),
                "Il controller deve rifiutare 6 giocatori");

        controller.setNumPlayers(3);

        // Aggiunta giocatori con nomi non validi
        assertThrows(IllegalArgumentException.class, () -> controller.addPlayer("", mockView),
                "Il controller deve rifiutare stringhe vuote");
        assertThrows(IllegalArgumentException.class, () -> controller.addPlayer(null, mockView),
                "Il controller deve rifiutare nomi null");

        // Aggiunta corretta
        controller.addPlayer("Marco", mockView);
        controller.addPlayer("Sofia", mockView);

        // Duplicati
        assertThrows(IllegalArgumentException.class, () -> controller.addPlayer("Marco", mockView),
                "Il controller deve rifiutare nomi duplicati");

        // Ultimo giocatore (innesca la creazione automatica)
        controller.addPlayer("Alice", mockView);
        assertNotNull(controller.getGame(), "Il gioco deve essere stato istanziato automaticamente");

        // Limite massimo superato
        assertThrows(IllegalStateException.class, () -> controller.addPlayer("Bob", mockView),
                "Il controller deve bloccare aggiunte a gioco già creato");


        // --- 2. TEST DELLE BARRIERE DI STATO ---

        // Il gioco è appena stato creato ed è in PLACING_TOTEMS.
        // Proviamo a fare un'azione che richiede RESOLVING_ACTIONS (pescare una carta).
        // Il controller DEVE bloccarci per lo stato scorretto!
        assertThrows(IllegalStateException.class, () -> controller.onTakeCard("Marco", 0, true),
                "Azione bloccata: il gioco è in fase di piazzamento, non si può pescare!");

        // Avvio del gioco (se fa setup aggiuntivi della board)
        controller.startGame();
        assertEquals(GameState.PLACING_TOTEMS, controller.getGame().getCurrentState().getStateId());


        // --- 3. TEST FASE DI PIAZZAMENTO (PLACING_TOTEMS) ---

        // Azione sbagliata nello stato corrente
        assertThrows(IllegalStateException.class, () -> controller.onTakeCard("Marco", 0, true),
                "Non si possono pescare carte durante il piazzamento");

        // Giocatore inesistente
        assertThrows(IllegalArgumentException.class, () -> controller.onPlaceTotem("GiocatoreFantasma", 'A'),
                "Il controller deve respingere giocatori non registrati");

        // Tessera inesistente
        assertThrows(IllegalArgumentException.class, () -> controller.onPlaceTotem("Marco", 'Z'),
                "Il controller deve respingere tessere non valide");

        // Esecuzione di piazzamenti legali in modo dinamico (ignora l'ordine casuale)
        List<Player> turnOrder = controller.getGame().getPlayers(); // O la TurnOrderTrack se accessibile
        char[] availableTiles = {'C', 'D', 'E'};

        for (int i = 0; i < 3; i++) {
            String activeNickname = turnOrder.get(i).getNickname(); // Sostituisci con il metodo corretto per sapere a chi tocca piazzare

            // Verifichiamo che il piazzamento funzioni senza lanciare eccezioni
            final char tileToPlace = availableTiles[i];
            assertDoesNotThrow(() -> controller.onPlaceTotem(activeNickname, tileToPlace));
        }

        // --- 4. TRANSIZIONE E FASE DI RISOLUZIONE (RESOLVING_ACTIONS) ---

        // Dopo l'ultimo piazzamento, lo stato deve essere cambiato
        assertEquals(GameState.RESOLVING_ACTIONS, controller.getGame().getCurrentState().getStateId());

        // Azione sbagliata nel nuovo stato
        assertThrows(IllegalStateException.class, () -> controller.onPlaceTotem("Marco", 'A'),
                "Non si possono piazzare totem durante la risoluzione");



        System.out.println("Stress Test del Controller completato con successo: Barriere di sicurezza intatte.");
    }

}

