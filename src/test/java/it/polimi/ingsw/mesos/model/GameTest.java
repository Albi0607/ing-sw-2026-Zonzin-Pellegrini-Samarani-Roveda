package it.polimi.ingsw.mesos.model;

import it.polimi.ingsw.mesos.common.enums.Color;
import it.polimi.ingsw.mesos.common.enums.GameState;
import it.polimi.ingsw.mesos.model.state.GameStateLogic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private Game game;
    private List<Player> players;


    @BeforeEach
    void setUp() {
        players = new ArrayList<>();
        players.add(new Player("Alice", Color.RED));
        players.add(new Player("Bob", Color.BLUE));
        players.add(new Player("francesco", Color.YELLOW));
        players.add(new Player("Matteo", Color.WHITE));
        game = new Game(players);
    }

    @Test
    void testCheckNicknameUnique() {
        // Alice esiste già
        assertFalse(game.checkNicknameUnique("Alice"), "Dovrebbe essere falso per 'Alice'");
        // Case insensitivity: ALICE
        assertFalse(game.checkNicknameUnique("ALICE"), "Dovrebbe essere falso per 'ALICE'");
        // David non esiste
        assertTrue(game.checkNicknameUnique("David"), "Dovrebbe essere vero per 'David'");
    }


    @Test
    void testConstructorWithTooManyPlayers() {
        // 1. Prepariamo una lista con 6 giocatori (il limite è 5)
        List<Player> tooManyPlayers = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            tooManyPlayers.add(new Player("Player" + i, Color.values()[i % Color.values().length]));
        }

        // 2. Verifichiamo che il costruttore lanci IllegalArgumentException
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Game(tooManyPlayers);
        });

        // 3. (Opzionale) Verifichiamo che il messaggio dell'errore sia quello giusto
        assertEquals("A game requires between 2 and 5 players.", exception.getMessage());
    }

    @Test
    void testConstructorWithTooFewPlayers() {
        // Caso limite inferiore: 1 solo giocatore
        List<Player> onePlayer = List.of(new Player("Solitario", Color.RED));

        assertThrows(IllegalArgumentException.class, () -> {
            new Game(onePlayer);
        }, "Dovrebbe lanciare eccezione con un solo giocatore");
    }



    @Test
    void testConstructorInitialization() {
        assertEquals(4, game.getPlayers().size());
        assertEquals(1, game.getCurrentRound());
        // Verifica che lo stato iniziale sia Setup
        assertEquals(GameState.SETUP, game.getCurrentState().getStateId());
    }


    @Test
    void testChangeState() {
        // Creiamo uno stato "Fake" per testare la transizione senza logica complessa
        GameStateLogic fakeState = new GameStateLogic() {
            @Override public void execute(Game g) { }
            @Override public void placeTotemOnOffer(Game g, Player p, it.polimi.ingsw.mesos.model.board.OfferTile t) {}
            @Override public GameState getStateId() { return GameState.PLACING_TOTEMS; }
        };

        game.changeState(fakeState);
        assertEquals(GameState.PLACING_TOTEMS, game.getCurrentState().getStateId());
    }

    @Test
    void testStartGame() {

        game.startGame();

        for (int i = 0; i < players.size(); i++) {
            assertNotNull(game.getBoard().getTurnOrderTrack().getPositions().get(i));
        }

        // 2. Verifica distribuzione cibo (1° player=2, 2°=3, 3°=3 4°=4)
        // 2 + 3 + 3 + 4 = 12
        int totalFood = game.getPlayers().stream().mapToInt(Player::getFood).sum();
        assertEquals(12, totalFood, "Il cibo totale distribuito deve essere 8 per 3 giocatori");

        // 3. Verifica che le OfferTile siano state create
        assertFalse(game.getBoard().getTiles().isEmpty());
    }

}