package it.polimi.ingsw.mesos.controller;

import it.polimi.ingsw.mesos.DB.DBManager;
import it.polimi.ingsw.mesos.DB.GameResultDAO;
import it.polimi.ingsw.mesos.DB.LeaderboardService;
import it.polimi.ingsw.mesos.model.enums.Color;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.awt.Color.blue;
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



        // Il gioco è appena stato creato ed è in PLACING_TOTEMS.
        // Proviamo a fare un'azione che richiede RESOLVING_ACTIONS (pescare una carta).
        // Il controller DEVE bloccarci per lo stato scorretto
        controller.onTakeCard("Marco", 0, true);

        // Verifichiamo che l'azione sia stata effettivamente ignorata e che
        // il gioco sia RIMASTO saldo nella fase di piazzamento!
        assertEquals(GameState.PLACING_TOTEMS, controller.getGame().getCurrentState().getStateId(),
                "L'azione di pescata doveva essere ignorata dal controller");

        // Avvio del gioco (se fa setup aggiuntivi della board)
        controller.startGame();
        assertEquals(GameState.PLACING_TOTEMS, controller.getGame().getCurrentState().getStateId());


        // --- 3. TEST FASE DI PIAZZAMENTO (PLACING_TOTEMS) ---

        // Azione sbagliata nello stato corrente
        controller.onTakeCard("Marco", 0, true);

        // Verifichiamo che il gioco non sia andato avanti, e che Marco non abbia pescato nulla!
        assertEquals(GameState.PLACING_TOTEMS, controller.getGame().getCurrentState().getStateId(),
                "Il controller deve ignorare la pescata durante il piazzamento");

        // Il controller deve respingere giocatori non registrati restituendo FALSE
        boolean esito1 = controller.onPlaceTotem("GiocatoreFantasma", 'A');
        assertFalse(esito1, "Il controller deve respingere giocatori non registrati ritornando false");

        // Il controller deve respingere tessere inesistenti restituendo FALSE
        boolean esito2 = controller.onPlaceTotem("Marco", 'Z');
        assertFalse(esito2, "Il controller deve respingere tessere non valide ritornando false");

        boolean resultFakePlayer = controller.onPlaceTotem("GiocatoreFantasma", 'A');
        assertFalse(resultFakePlayer, "Il controller deve respingere giocatori non registrati (restituendo false)");

        // Tessera inesistente
        boolean resultFakeTile = controller.onPlaceTotem("Marco", 'Z');
        assertFalse(resultFakeTile, "Il controller deve respingere tessere non valide (restituendo false)");

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
        boolean esitoPiazzamentoRitardato = controller.onPlaceTotem("Marco", 'A');
        assertFalse(esitoPiazzamentoRitardato, "Non si possono piazzare totem durante la risoluzione (deve ritornare false)");



        System.out.println("Stress Test del Controller completato con successo: Barriere di sicurezza intatte.");
    }

    @Test
    void testOnSkipExtraDraw_Success() {
        avanzaFinoARisoluzioneAzioni();

        // Ci assicuriamo di essere nello stato corretto
        assertEquals(GameState.RESOLVING_ACTIONS, controller.getGame().getCurrentState().getStateId());

        // Dobbiamo capire a quale giocatore tocca eseguire la risoluzione ora
        ResolvingState rs = (ResolvingState) controller.getGame().getCurrentState();
        Player activePlayer = rs.getActivePlayer(controller.getGame());

        assertNotNull(activePlayer, "Ci dovrebbe essere un giocatore attivo in risoluzione");

        // Il giocatore attivo decide di saltare
        boolean result = controller.onSkipExtraDraw(activePlayer.getNickname());

        assertTrue(result, "onSkipExtraDraw deve ritornare true quando l'azione è legale");
    }

    @Test
    void testOnSkipExtraDraw_WrongState_ReturnsFalse() {
        // Prepariamo la partita ma NON avanziamo fino a RESOLVING_ACTIONS
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", mockView);
        controller.addPlayer("Bob", mockView);
        controller.startGame();

        // Siamo ancora in PLACING_TOTEMS
        assertEquals(GameState.PLACING_TOTEMS, controller.getGame().getCurrentState().getStateId());

        // Alice prova a saltare un'azione fuori fase
        boolean result = controller.onSkipExtraDraw("Alice");

        assertFalse(result, "onSkipExtraDraw deve ritornare false se chiamato nello stato sbagliato");
    }

    @Test
    void testOnSkipExtraDraw_PlayerNotFound_ReturnsFalse() {
        avanzaFinoARisoluzioneAzioni();

        // Proviamo a chiamare l'azione con un nome che non fa parte del gioco
        boolean result = controller.onSkipExtraDraw("CarloFantasma");

        assertFalse(result, "onSkipExtraDraw deve ritornare false se il giocatore non esiste");
    }

    private void avanzaFinoARisoluzioneAzioni() {
        controller.setNumPlayers(2);
        controller.addPlayer("Alice", mockView);
        controller.addPlayer("Bob", mockView);

        // NOTA: Ho tolto controller.startGame() perché il tuo addPlayer lo chiama
        // in automatico non appena entra l'ultimo giocatore (la stanza si riempie).

        // Ci sono 2 giocatori, quindi dobbiamo fare 2 piazzamenti validi
        for (int i = 0; i < 2; i++) {
            // 1. Chiediamo al gioco a chi tocca esattamente in questo momento
            String currentNickname = controller.getGame().getCurrentPlayerNickname();
            assertNotNull(currentNickname, "Il giocatore corrente non dovrebbe essere null");

            // 2. Chiediamo alla board quale è la prima tessera offerta ancora libera
            char availableTile = controller.getGame().getBoard().getTiles().stream()
                    .filter(t -> t.getHost() == null) // Prendiamo solo quelle libere
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Nessuna tessera libera trovata!"))
                    .getId();

            // 3. Facciamo la mossa legalmente
            boolean success = controller.onPlaceTotem(currentNickname, availableTile);
            assertTrue(success, "Il piazzamento del totem dovrebbe andare a buon fine");
        }
    }

}

